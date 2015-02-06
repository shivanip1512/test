#include "precompiled.h"

#include "prot_dnpSlave.h"

#include "dnp_object_analoginput.h"
#include "dnp_object_binaryinput.h"
#include "dnp_object_counter.h"

namespace Cti {
namespace Protocols {

using namespace Cti::Protocols::DNP;

void DnpSlaveProtocol::setAddresses( unsigned short dstAddr, unsigned short srcAddr )
{
    _datalink.setAddresses(dstAddr, srcAddr);
}

YukonError_t DnpSlaveProtocol::slaveDecode( CtiXfer &xfer )
{
    if( xfer.getOutBuffer()[10] & 0x80 )
    {
        slaveTransactionComplete();
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

    return _app_layer.decode(_transport);
}

void DnpSlaveProtocol::setSlaveCommand( Commands command, int seqNumber, std::vector<input_point> inputPoints )
{
    _app_layer.setSequenceNumber(seqNumber);

    _command = command;

    switch( _command )
    {
        case Commands::Class1230Read:
        {
            std::map<unsigned, std::unique_ptr<const AnalogInput>> analogs;
            std::map<unsigned, std::unique_ptr<const BinaryInput>> digitals;
            std::map<unsigned, std::unique_ptr<const Counter>>     counters;

            for( const auto &ip : inputPoints )
            {
                switch( ip.type )
                {
                    case AnalogInputType:
                    {
                        auto ain = std::make_unique<AnalogInput>(AnalogInput::AI_32Bit);
                        ain->setValue(ip.ain.value);
                        ain->setOnlineFlag(ip.online);

                        analogs.emplace(ip.control_offset, std::move(ain));

                        break;
                    }
                    case DigitalInput:
                    {
                        auto bin = std::make_unique<BinaryInput>(BinaryInput::BI_WithStatus);
                        bin->setStateValue(ip.din.trip_close);
                        bin->setOnlineFlag(ip.online);

                        digitals.emplace(ip.control_offset, std::move(bin));

                        break;
                    }
                    case Counters:
                    {
                        auto counterin = std::make_unique<Counter>(Counter::C_Binary32Bit);
                        counterin->setValue(ip.counterin.value);
                        counterin->setOnlineFlag(ip.online);

                        counters.emplace(ip.control_offset, std::move(counterin));

                        break;
                    }
                }
            }

            std::vector<ObjectBlockPtr> dobs;

            if( ! analogs.empty() )     dobs.emplace_back(ObjectBlock::makeLongIndexedBlock(std::move(analogs)));
            if( ! digitals.empty() )    dobs.emplace_back(ObjectBlock::makeLongIndexedBlock(std::move(digitals)));
            if( ! counters.empty() )    dobs.emplace_back(ObjectBlock::makeLongIndexedBlock(std::move(counters)));

            _app_layer.setCommand(
                    ApplicationLayer::ResponseResponse,
                    std::move(dobs));

            break;
        }
        case Commands::SetDigitalOut_Direct:
        {
            if( ! inputPoints.empty()
                && inputPoints[0].type == DigitalInput )
            {
                const input_point &p = inputPoints[0];

                auto boc = std::make_unique<BinaryOutputControl>(BinaryOutputControl::BOC_ControlRelayOutputBlock);

                boc->setControlBlock(
                        p.din.on_time,
                        p.din.off_time,
                        p.din.count,
                        p.din.control,
                        p.din.queue,
                        p.din.clear,
                        p.din.trip_close);
                boc->setStatus(
                        p.din.status);

                _app_layer.setCommand(
                        ApplicationLayer::ResponseResponse,
                        ObjectBlock::makeIndexedBlock(
                                std::move(boc),
                                p.control_offset));

                break;
            }
            else
            {
                CTILOG_ERROR(dout, "Input point invalid for control");
                _command = Commands::Invalid;
            }
        }
        default:
        {
            CTILOG_ERROR(dout, "invalid command "<< static_cast<int>(_command));

            _command = Commands::Invalid;
        }
    }

    //  finalize the request
    _app_layer.initForSlaveOutput();
}


YukonError_t DnpSlaveProtocol::slaveGenerate( CtiXfer &xfer )
{
    YukonError_t retVal = ClientErrors::None;

    if( _transport.isTransactionComplete() )
    {
        retVal = _app_layer.generate(_transport);

        if( retVal )
        {
            slaveTransactionComplete();
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

void DnpSlaveProtocol::slaveTransactionComplete()
{
    _command = Commands::Complete;

    _app_layer.completeSlave();
    _transport.setIoStateComplete();
    _datalink .setIoStateComplete();
}

bool DnpSlaveProtocol::isTransactionComplete( void ) const
{
    return _command == Commands::Complete || _command == Commands::Invalid;
}

}
}

