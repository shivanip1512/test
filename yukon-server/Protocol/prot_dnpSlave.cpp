#include "precompiled.h"

#include "prot_dnpSlave.h"

#include "dnp_object_analoginput.h"
#include "dnp_object_binaryinput.h"
#include "dnp_object_counter.h"

namespace Cti {
namespace Protocols {

using namespace Cti::Protocols::DNP;

DnpSlaveProtocol::DnpSlaveProtocol()
{
   _app_layer.completeSlave();
   _transport.setIoStateComplete();
   _datalink .setIoStateComplete();

   _datalink .setSlaveResponse();
   _app_layer.setSequenceNumber(0);
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

YukonError_t DnpSlaveProtocol::slaveGenerate( CtiXfer &xfer )
{
    if( _app_layer.isTransactionComplete() )
    {
        switch( getCommand() )
        {
            case Command_Class1230Read:
            case Command_Class123Read:
            {
                std::map<unsigned, std::unique_ptr<const AnalogInput>> analogs;
                std::map<unsigned, std::unique_ptr<const BinaryInput>> digitals;
                std::map<unsigned, std::unique_ptr<const Counter>>     counters;

                for( const auto &ip : _input_point_list )
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
            case Command_SetDigitalOut_Direct:
            {
                if( ! _input_point_list.empty()
                    && _input_point_list[0].type == DigitalInput )
                {
                    input_point &p = _input_point_list[0];

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
                    setSlaveCommand(Command_Invalid);
                }
            }
            case Command_UnsolicitedInbound:
            case Command_WriteTime:
            case Command_ReadTime:
            case Command_Loopback:
            case Command_UnsolicitedEnable:
            case Command_UnsolicitedDisable:
            case Command_SetAnalogOut:
            case Command_SetDigitalOut_SBO_SelectOnly:
            case Command_SetDigitalOut_SBO_Select:
            case Command_SetDigitalOut_SBO_Operate:
            default:
            {
                CTILOG_ERROR(dout, "invalid command "<< getCommand());

                setSlaveCommand(Command_Invalid);
            }
        }

        //  finalize the request
        _app_layer.initForSlaveOutput();
    }

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
    if( _app_layer.errorCondition() )
    {
        addStringResults(new std::string("Operation failed"));
    }
    setSlaveCommand(Command_Complete);
}

void DnpSlaveProtocol::addInputPoint(const input_point &ip)
{
    _input_point_list.push_back(ip);
}

void DnpSlaveProtocol::setSlaveCommand( Command command )
{
    setCommand(command);

    if( getCommand() == Command_Complete)
    {
        _input_point_list.clear();
        _app_layer.completeSlave();
        _transport.setIoStateComplete();
        _datalink .setIoStateComplete();
    }
}

void DnpSlaveProtocol::setSequence( int seqNumber )
{
    _app_layer.setSequenceNumber(seqNumber);
}

}
}

