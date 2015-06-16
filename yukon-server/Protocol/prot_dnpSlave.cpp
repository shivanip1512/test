#include "precompiled.h"

#include "prot_dnpSlave.h"

#include "dnp_object_analoginput.h"
#include "dnp_object_binaryinput.h"
#include "dnp_object_binaryoutput.h"
#include "dnp_object_counter.h"
#include "dnp_object_class.h"

#include "logger.h"

namespace Cti {
namespace Protocols {

using namespace Cti::Protocols::DNP;

YukonError_t DnpSlaveProtocol::decode( CtiXfer &xfer )
{
    if( xfer.getOutBuffer()[10] & 0x80 )
    {
        setTransactionComplete();
        return ClientErrors::None;
    }

    YukonError_t retVal = _datalink.decode(xfer, ClientErrors::None);

    if( ! _datalink.isTransactionComplete() )
    {
        return retVal;
    }

    retVal = _transport.decode(_datalink);

    if( ! _transport.isTransactionComplete() )
    {
        return retVal;
    }

    return _application.decode(_transport);
}


auto DnpSlaveProtocol::identifyRequest( const char* data, unsigned int size ) -> std::pair<Commands, ObjectBlockPtr>
{
    auto Invalid = std::make_pair(Commands::Invalid, nullptr);

    if( ! data )
    {
        return Invalid;
    }

    if( ! DatalinkLayer::isPacketValid(reinterpret_cast<const unsigned char *>(data), size) )
    {
        return Invalid;
    }

    const DatalinkPacket::dl_packet &packet = *reinterpret_cast<const DatalinkPacket::dl_packet *>(data);

    if (packet.header.fmt.control.p.direction == 0)
    {
        return Invalid;
    }

    _datalink.setAddresses(packet.header.fmt.source, packet.header.fmt.destination);

    if( packet.header.fmt.control.p.functionCode == DatalinkLayer::Control_PrimaryLinkStatus )
    {
        return { Commands::LinkStatus, nullptr };
    }
    if( packet.header.fmt.control.p.functionCode == DatalinkLayer::Control_PrimaryResetLink )
    {
        return { Commands::ResetLink, nullptr };
    }

    if( packet.header.fmt.control.p.functionCode != DatalinkLayer::Control_PrimaryUserDataUnconfirmed )
    {
        return Invalid;
    }

    unsigned char buf[DatalinkPacket::PayloadLengthMax];
    int len = 0;

    DatalinkLayer::putPacketPayload(packet, buf, &len);

    if( len < Transport::TransportPacket::HeaderLen )
    {
        return Invalid;
    }

    Transport::TransportPacket transport_packet(buf[0], buf + 1, buf + len);

    if( ! transport_packet.isFirst() ||
        ! transport_packet.isFinal() )
    {
        return Invalid;
    }

    auto application_payload = transport_packet.payload();

    if( application_payload.size() <= ApplicationLayer::ReqHeaderSize )
    {
        return Invalid;
    }

    _application.setSequenceNumber(application_payload[0] & 0x0f);

    //  ideally, this would be done with the actual application layer ingesting the payload and beginning the request
    switch( application_payload[1] )
    {
        default:
        {
            return Invalid;
        }
        case ApplicationLayer::RequestRead:
        {
            auto blocks =
                    ApplicationLayer::restoreObjectBlocks(
                            application_payload.data() + 2,
                            application_payload.size() - 2);

            if( blocks.size() == 4
                && blocks[0]->getGroup()     == Class::Group
                && blocks[0]->getVariation() == Class::Class1
                && blocks[1]->getGroup()     == Class::Group
                && blocks[1]->getVariation() == Class::Class2
                && blocks[2]->getGroup()     == Class::Group
                && blocks[2]->getVariation() == Class::Class3
                && blocks[3]->getGroup()     == Class::Group
                && blocks[3]->getVariation() == Class::Class0 )
            {
                //  nothing, this is an actual class 1230 poll
            }
            else
            {
                Cti::FormattedTable tbl;

                tbl.setCell(0, 0) << "Index";
                tbl.setCell(0, 1) << "Group";
                tbl.setCell(0, 2) << "Variation";

                unsigned idx = 0;

                for( const auto &block : blocks )
                {
                    tbl.setCell(idx + 1, 0) << idx;
                    tbl.setCell(idx + 1, 1) << block->getGroup();
                    tbl.setCell(idx + 1, 2) << block->getVariation();
                    ++idx;
                }

                CTILOG_WARN(dout, "Unknown read, returning class 1230 poll anyway" << tbl);
            }

            return { Commands::Class1230Read, nullptr };
        }
        case ApplicationLayer::RequestDirectOp:
        {
            auto blocks =
                    ApplicationLayer::restoreObjectBlocks(
                            application_payload.data() + 2,
                            application_payload.size() - 2);

            if( blocks.size() == 1
                && blocks[0]->getGroup()     == BinaryOutputControl::Group
                && blocks[0]->getVariation() == BinaryOutputControl::BOC_ControlRelayOutputBlock )
            {
                return { Commands::SetDigitalOut_Direct, ObjectBlockPtr{ std::move(blocks[0]) } };
            }
        }
    }

    return Invalid;
}


std::vector<unsigned char> DnpSlaveProtocol::createDatalinkConfirmation()
{
    auto packet =
            _datalink.constructSecondaryControlPacket(
                    Cti::Protocols::DNP::DatalinkLayer::Control_SecondaryLinkStatus,
                    true);

    auto buf = reinterpret_cast<unsigned char *>(&packet);

    //  guaranteed to be 10, but this is the proper method
    return std::vector<unsigned char> { buf, buf + DatalinkPacket::calcPacketLength(packet.header.fmt.len) };
}

std::vector<unsigned char> DnpSlaveProtocol::createDatalinkAck()
{
    auto packet =
            _datalink.constructSecondaryControlPacket(
                    Cti::Protocols::DNP::DatalinkLayer::Control_SecondaryACK,
                    true);

    auto buf = reinterpret_cast<unsigned char *>(&packet);

    //  guaranteed to be 10, but this is the proper method
    return std::vector<unsigned char> { buf, buf + DatalinkPacket::calcPacketLength(packet.header.fmt.len) };
}

void DnpSlaveProtocol::setScanCommand( std::vector<std::unique_ptr<DnpSlave::output_point>> outputPoints )
{
    _command = Commands::Class1230Read;

    struct PointSorter : DnpSlave::OutputPointHandler
    {
        std::map<unsigned, std::unique_ptr<const DNP::AnalogInput>> analogs;
        std::map<unsigned, std::unique_ptr<const DNP::BinaryInput>> digitals;
        std::map<unsigned, std::unique_ptr<const DNP::Counter>>     counters;

        void handle(const DnpSlave::output_analog &a) override
        {
            auto ain = std::make_unique<AnalogInput>(AnalogInput::AI_32Bit);
            ain->setValue(a.value);
            ain->setOnlineFlag(a.online);

            analogs.emplace(a.offset, std::move(ain));
        }
        void handle(const DnpSlave::output_digital &d) override
        {
            auto bin = std::make_unique<BinaryInput>(BinaryInput::BI_WithStatus);
            bin->setStateValue(d.status);
            bin->setOnlineFlag(d.online);

            digitals.emplace(d.offset, std::move(bin));
        }
        void handle(const DnpSlave::output_counter &c) override
        {
            auto counterin = std::make_unique<Counter>(Counter::C_Binary32Bit);
            counterin->setValue(c.value);
            counterin->setOnlineFlag(c.online);

            counters.emplace(c.offset, std::move(counterin));
        }
    }
    ps;

    for( const auto &point : outputPoints )
    {
        point->identify(ps);
    }

    std::vector<ObjectBlockPtr> dobs;

    if( ! ps.analogs.empty() )     dobs.emplace_back(ObjectBlock::makeLongIndexedBlock(std::move(ps.analogs)));
    if( ! ps.digitals.empty() )    dobs.emplace_back(ObjectBlock::makeLongIndexedBlock(std::move(ps.digitals)));
    if( ! ps.counters.empty() )    dobs.emplace_back(ObjectBlock::makeLongIndexedBlock(std::move(ps.counters)));

    _application.setCommand(
            ApplicationLayer::ResponseResponse,
            std::move(dobs));

    _application.initForSlaveOutput();
}


void DnpSlaveProtocol::setControlCommand( const DnpSlave::control_request &control )
{
    _command = Commands::SetDigitalOut_Direct;

    auto boc = std::make_unique<BinaryOutputControl>(BinaryOutputControl::BOC_ControlRelayOutputBlock);

    boc->setControlBlock(
            control.on_time,
            control.off_time,
            control.count,
            control.control,
            control.queue,
            control.clear,
            control.trip_close);
    boc->setStatus(
            control.status);

    const auto BlockFactoryFunc =
            control.isLongIndexed
                ? ObjectBlock::makeLongIndexedBlock
                : ObjectBlock::makeIndexedBlock;

    _application.setCommand(
            ApplicationLayer::ResponseResponse,
                    BlockFactoryFunc(
                          std::move(boc),
                          control.offset));

    //  finalize the request
    _application.initForSlaveOutput();
}


YukonError_t DnpSlaveProtocol::generate( CtiXfer &xfer )
{
    YukonError_t retVal = ClientErrors::None;

    if( _transport.isTransactionComplete() )
    {
        retVal = _application.generate(_transport);

        if( retVal )
        {
            setTransactionComplete();
        }
    }

    if( ! retVal )
    {
        if( _datalink.isTransactionComplete() )
        {
            retVal = _transport.generate(_datalink);
        }
    }

    if( !retVal )
    {
        retVal = _datalink.generate(xfer);
    }

    return retVal;
}

void DnpSlaveProtocol::setTransactionComplete()
{
    _command = Commands::Complete;

    _application.completeSlave();
    _transport.setIoStateComplete();
    _datalink .setIoStateComplete();
}

bool DnpSlaveProtocol::isTransactionComplete( void ) const
{
    return _command == Commands::Complete || _command == Commands::Invalid;
}

unsigned short DnpSlaveProtocol::getSrcAddr() const
{
    return _datalink.getSrcAddress();
}

unsigned short DnpSlaveProtocol::getDstAddr() const
{
    return _datalink.getDstAddress();
}

}
}

