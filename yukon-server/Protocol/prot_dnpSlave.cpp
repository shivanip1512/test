#include "precompiled.h"

#include "prot_dnpSlave.h"

#include "dnp_object_analoginput.h"
#include "dnp_object_analogoutput.h"
#include "dnp_object_binaryinput.h"
#include "dnp_object_binaryoutput.h"
#include "dnp_object_counter.h"
#include "dnp_object_class.h"

#include "logger.h"

#include "std_helper.h"

namespace Cti {
namespace Protocols {

using namespace Cti::Protocols::DNP;
using namespace std::chrono_literals;

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


std::string describeBlocks(const std::vector<std::unique_ptr<ObjectBlock>>& blocks)
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

    return tbl.toString();
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

    if( application_payload.size() < ApplicationLayer::ReqHeaderSize )
    {
        return Invalid;
    }

    _application.setSequenceNumber(application_payload[0] & 0x0f);

    const unsigned char applicationCommand = application_payload[1];

    //  ideally, this would be done with the actual application layer ingesting the payload and beginning the request
    switch( applicationCommand )
    {
        default:
        {
            return { Commands::Unsupported, nullptr };
        }
        case ApplicationLayer::RequestDisableUnsolicited:
        {
            return { Commands::UnsolicitedDisable, nullptr };
        }
        case ApplicationLayer::RequestDelayMeasurement:
        {
            return { Commands::DelayMeasurement, nullptr };
        }
        case ApplicationLayer::RequestWrite:
        {
            auto blocks =
                ApplicationLayer::restoreObjectBlocks(
                    application_payload.data() + 2,
                    application_payload.size() - 2);

            if( blocks.size() == 1
                && blocks[0]->getGroup() == Time::Group
                && blocks[0]->getVariation() == Time::T_TimeAndDate )
            {
                return { Commands::WriteTime, nullptr };
            }

            CTILOG_WARN(dout, "Unknown write, returning Unsupported" << describeBlocks(blocks));

            return { Commands::Unsupported, nullptr };
        }
        case ApplicationLayer::RequestEnableUnsolicited:
        {
            return { Commands::UnsolicitedEnable, nullptr };
        }
        case ApplicationLayer::RequestRead:
        {
            auto blocks =
                    ApplicationLayer::restoreObjectBlocks(
                            application_payload.data() + 2,
                            application_payload.size() - 2);

            if( blocks.size() == 4
                && blocks[0]->getGroup() == Class::Group
                && blocks[0]->getVariation() == Class::Class1
                && blocks[1]->getGroup() == Class::Group
                && blocks[1]->getVariation() == Class::Class2
                && blocks[2]->getGroup() == Class::Group
                && blocks[2]->getVariation() == Class::Class3
                && blocks[3]->getGroup() == Class::Group
                && blocks[3]->getVariation() == Class::Class0 )
            {
                //  nothing, this is an actual class 1230 poll
            }
            else if( blocks.size() == 3
                && blocks[0]->getGroup() == Class::Group
                && blocks[0]->getVariation() == Class::Class1
                && blocks[1]->getGroup() == Class::Group
                && blocks[1]->getVariation() == Class::Class2
                && blocks[2]->getGroup() == Class::Group
                && blocks[2]->getVariation() == Class::Class3 )
            {
                CTILOG_WARN(dout, "Class 123 poll received, returning class 1230 poll anyway");
            }
            else
            {
                CTILOG_WARN(dout, "Unknown read, returning class 1230 poll anyway" << describeBlocks(blocks));
            }

            return { Commands::Class1230Read, nullptr };
        }
        case ApplicationLayer::RequestSelect:
        case ApplicationLayer::RequestOperate:
        case ApplicationLayer::RequestDirectOp:
        {
            auto blocks =
                    ApplicationLayer::restoreObjectBlocks(
                            application_payload.data() + 2,
                            application_payload.size() - 2);

            if( ! blocks.empty() )
            {
                boost::optional<Commands> command;

                if( blocks[0]->getGroup()     == BinaryOutputControl::Group &&
                    blocks[0]->getVariation() == BinaryOutputControl::BOC_ControlRelayOutputBlock )
                {
                    static const std::map<unsigned char, Commands> digitalCommandTypes {
                        { ApplicationLayer::RequestSelect,   Commands::SetDigitalOut_Select },
                        { ApplicationLayer::RequestOperate,  Commands::SetDigitalOut_Operate },
                        { ApplicationLayer::RequestDirectOp, Commands::SetDigitalOut_Direct }};

                    command = mapFind(digitalCommandTypes, applicationCommand);
                }
                else if( blocks[0]->getGroup() == AnalogOutput::Group )
                {
                    static const std::map<unsigned char, Commands> analogCommandTypes {
                        { ApplicationLayer::RequestSelect,   Commands::SetAnalogOut_Select },
                        { ApplicationLayer::RequestOperate,  Commands::SetAnalogOut_Operate },
                        { ApplicationLayer::RequestDirectOp, Commands::SetAnalogOut_Direct }};

                    command = mapFind(analogCommandTypes, applicationCommand);
                }

                if( command )
                {
                    return { *command, ObjectBlockPtr{ std::move(blocks[0]) } };
                }
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

template <typename Type>
using PointMap = std::map<unsigned, std::unique_ptr<const Type>>;

template <typename Object, typename Variation, typename Source>
void emplaceNumeric(Source s, PointMap<Object>& map, Variation v)
{
    auto n = std::make_unique<Object>(v);
    n->setValue(s.value);
    n->setOnlineFlag(s.online);
    map.emplace(s.offset, std::move(n));
}

template <typename Object, typename Source>
void emplaceBoolean(Source s, PointMap<Object>& map, typename Object::Variation v)
{
    auto b = std::make_unique<Object>(v);
    b->setStateValue(s.value);
    b->setOnlineFlag(s.online);
    map.emplace(s.offset, std::move(b));
}

void DnpSlaveProtocol::setScanCommand( std::vector<DnpSlave::output_point> outputPoints )
{
    _command = Commands::Class1230Read;

    PointMap<DNP::AnalogInput>        analog_inputs;
    PointMap<DNP::BinaryInput>        binary_inputs;
    PointMap<DNP::AnalogOutputStatus> analog_outputs;
    PointMap<DNP::BinaryOutput>       binary_outputs;
    PointMap<DNP::Counter>            accumulators;
    PointMap<DNP::Counter>            demand_accumulators;

    using DnpSlave::PointType;

    std::map<PointType, std::function<void(DnpSlave::output_point)>> pointSorters {
        { PointType::AnalogInput,       [&](auto p) { emplaceNumeric(p, analog_inputs,        AnalogInput::AI_32Bit);       } },
        { PointType::AnalogOutput,      [&](auto p) { emplaceNumeric(p, analog_outputs,       AnalogOutput::AO_32Bit);      } },
        { PointType::BinaryInput,       [&](auto p) { emplaceBoolean(p, binary_inputs,        BinaryInput::BI_WithStatus);  } },
        { PointType::BinaryOutput,      [&](auto p) { emplaceBoolean(p, binary_outputs,       BinaryOutput::BO_WithStatus); } },
        { PointType::Accumulator,       [&](auto p) { emplaceNumeric(p, accumulators,         Counter::C_Binary32Bit);      } },
        { PointType::DemandAccumulator, [&](auto p) { emplaceNumeric(p, demand_accumulators,  Counter::C_Binary32Bit);      } },
    };

    for( const auto p : outputPoints )
    {
        if( auto sorter = mapFindRef(pointSorters, p.type) )
        {
            (*sorter)(p);
        }
        else
        {
            CTILOG_DEBUG(dout, "Unhandled point type " << static_cast<int>(p.type));
        }
    }

    std::vector<ObjectBlockPtr> dobs;

    if( ! analog_inputs.empty() )
    {
        for( auto& e : ObjectBlock::makeRangedBlocks(std::move(analog_inputs)) )
        {
            dobs.emplace_back(std::move(e));
        }
    }

    if( ! binary_inputs.empty() )
    {
        for( auto& e : ObjectBlock::makeRangedBlocks(std::move(binary_inputs)) )
        {
            dobs.emplace_back(std::move(e));
        }
    }

    if( ! accumulators.empty() )
    {
        for( auto& e : ObjectBlock::makeRangedBlocks(std::move(accumulators)) )
        {
            dobs.emplace_back(std::move(e));
        }
    }

    if( ! demand_accumulators.empty() )
    {
        for( auto& e : ObjectBlock::makeRangedBlocks(std::move(demand_accumulators)) )
        {
            dobs.emplace_back(std::move(e));
        }
    }

    if( ! analog_outputs.empty() )
    {
        for( auto& e : ObjectBlock::makeRangedBlocks(std::move(analog_outputs)) )
        {
            dobs.emplace_back(std::move(e));
        }
    }

    if( ! binary_outputs.empty() )
    {
        for( auto& e : ObjectBlock::makeRangedBlocks(std::move(binary_outputs)) )
        {
            dobs.emplace_back(std::move(e));
        }
    }

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


void DnpSlaveProtocol::setAnalogOutputCommand( const DnpSlave::analog_output_request &analog )
{
    _command = Commands::SetAnalogOut_Direct;

    auto aoc = std::make_unique<AnalogOutput>(analog.type);

    aoc->setControl(analog.value);
    aoc->setStatus(analog.status);

    const auto BlockFactoryFunc =
            analog.isLongIndexed
                ? ObjectBlock::makeLongIndexedBlock
                : ObjectBlock::makeIndexedBlock;

    _application.setCommand(
            ApplicationLayer::ResponseResponse,
            BlockFactoryFunc(
                    std::move(aoc),
                    analog.offset));

    //  finalize the request
    _application.initForSlaveOutput();
}


void DnpSlaveProtocol::setDelayMeasurementCommand( const std::chrono::milliseconds delay )
{
    using std::chrono::seconds;
    using std::chrono::duration_cast;

    _command = Commands::DelayMeasurement;

    //  If delay is less than 65 seconds, use Fine with millis, otherwise use Coarse with seconds
    auto td = (delay < 65s)
        ? std::make_unique<TimeDelay>(delay)
        : std::make_unique<TimeDelay>(duration_cast<seconds>(delay));
    
    _application.setCommand(
            ApplicationLayer::ResponseResponse,
            ObjectBlock::makeRangedBlock(std::move(td), 0));

    _application.initForSlaveOutput();
}


void DnpSlaveProtocol::setWriteTimeCommand()
{
    _command = Commands::WriteTime;

    _application.setCommand(
            ApplicationLayer::ResponseResponse);

    _application.initForSlaveOutput();
}


void DnpSlaveProtocol::setUnsupportedCommand()
{
    _command = Commands::Unsupported;

    _application.setCommand(ApplicationLayer::ResponseResponse);

    _application.initForSlaveOutput();

    _application.setInternalIndications_FunctionCodeUnsupported();
}


void DnpSlaveProtocol::setUnsolicitedDisableCommand()
{
    _command = Commands::UnsolicitedDisable;

    _application.setCommand(ApplicationLayer::ResponseResponse);

    _application.initForSlaveOutput();

    _application.setInternalIndications_FunctionCodeUnsupported();
}


void DnpSlaveProtocol::setUnsolicitedEnableCommand()
{
    _command = Commands::UnsolicitedEnable;

    _application.setCommand(ApplicationLayer::ResponseResponse);

    _application.initForSlaveOutput();

    _application.setInternalIndications_FunctionCodeUnsupported();
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

