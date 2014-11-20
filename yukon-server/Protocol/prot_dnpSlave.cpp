#include "precompiled.h"

#include "prot_dnpSlave.h"

#include "dnp_object_analoginput.h"
#include "dnp_object_binaryinput.h"
#include "dnp_object_counter.h"

namespace Cti {
namespace Protocols {

using namespace DNP;

DnpSlaveProtocol::DnpSlaveProtocol()
{
   getApplicationLayer().completeSlave();
   setOptions(DnpSlaveProtocol::Options_SlaveResponse);
}

YukonError_t DnpSlaveProtocol::slaveDecode( CtiXfer &xfer )
{
    if( xfer.getOutBuffer()[10] & 0x80 )
    {
        slaveTransactionComplete();
        return ClientErrors::None;
    }

    return getApplicationLayer().decode(xfer, ClientErrors::None);
}

int DnpSlaveProtocol::slaveGenerate( CtiXfer &xfer )
{
    if( getApplicationLayer().isTransactionComplete() )
    {
        switch( getCommand() )
        {
            case Command_Class1230Read:
            case Command_Class123Read:
            {
                getApplicationLayer().setCommand(ApplicationLayer::ResponseResponse);
                ObjectBlock         *dob1;
                ObjectBlock         *dob2;
                ObjectBlock         *dob3;

                AnalogInputChange *ainc;
                BinaryInputChange *binc;
                CounterEvent *counterevent;
                AnalogInput *ain;
                BinaryInput *bin;
                Counter *counterin;

                dob1 = CTIDBG_new ObjectBlock(ObjectBlock::ShortIndex_ShortQty);
                dob2 = CTIDBG_new ObjectBlock(ObjectBlock::ShortIndex_ShortQty);
                dob3 = CTIDBG_new ObjectBlock(ObjectBlock::ShortIndex_ShortQty);

                for ( int i = 0; i < _input_point_list.size(); i++ )
                {
                    input_point &ip =  _input_point_list[i];

                    if( ip.type == AnalogInputType )
                    {
                        if( ip.includeTime)
                        {
                            ainc = CTIDBG_new AnalogInputChange( AnalogInputChange::AIC_32BitWithTime);
                            ainc->setTime(ip.timestamp);
                            ainc->setValue(ip.ain.value);
                            ainc->setOnlineFlag(ip.online);
                            dob1->addObjectIndex(ainc, ip.control_offset);
                        }
                        else
                        {
                            ain = CTIDBG_new AnalogInput( AnalogInput::AI_32Bit );
                            ain->setValue(ip.ain.value);
                            ain->setOnlineFlag(ip.online);
                            dob1->addObjectIndex(ain, ip.control_offset);
                        }
                    }
                    else if( ip.type == DigitalInput )
                    {
                        if( ip.includeTime)
                        {
                            binc = CTIDBG_new BinaryInputChange( BinaryInputChange::BIC_WithTime);
                            binc->setTime(ip.timestamp);
                            binc->setStateValue(ip.din.trip_close);
                            binc->setOnlineFlag(ip.online);
                            dob2->addObjectIndex(binc, ip.control_offset);
                        }
                        else
                        {
                            bin = CTIDBG_new BinaryInput( BinaryInput::BI_WithStatus);
                            bin->setStateValue(ip.din.trip_close);
                            bin->setOnlineFlag(ip.online);
                            dob2->addObjectIndex(bin, ip.control_offset);
                        }
                    }
                    else if( ip.type == Counters )
                    {
                        if( ip.includeTime)
                        {
                            counterevent = CTIDBG_new CounterEvent( CounterEvent::CE_Delta32BitWithTime);
                            counterevent->setTime(ip.timestamp);
                            counterevent->setValue(ip.counterin.value);
                            counterevent->setOnlineFlag(ip.online);
                            dob3->addObjectIndex(counterevent, ip.control_offset);
                        }
                        else
                        {
                            counterin = CTIDBG_new Counter( Counter::C_Binary32Bit );
                            counterin->setValue(ip.counterin.value);
                            counterin->setOnlineFlag(ip.online);
                            dob3->addObjectIndex(counterin, ip.control_offset);
                        }
                    }
                }
                addObjectBlock(dob1);
                addObjectBlock(dob2);
                addObjectBlock(dob3);

                break;
            }
            case Command_UnsolicitedInbound:
            case Command_WriteTime:
            case Command_ReadTime:
            case Command_Loopback:
            case Command_UnsolicitedEnable:
            case Command_UnsolicitedDisable:
            case Command_SetAnalogOut:
            case Command_SetDigitalOut_Direct:
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
        getApplicationLayer().initForSlaveOutput();
    }

    int retVal = getApplicationLayer().generate(xfer);
    if( retVal )
    {
        slaveTransactionComplete();
    }
    return retVal;
}

void DnpSlaveProtocol::slaveTransactionComplete()
{
    if( getApplicationLayer().errorCondition() )
    {
        addStringResults(new std::string("Operation failed"));
    }
    setSlaveCommand(Command_Complete);

    return;
}

void DnpSlaveProtocol::addInputPoint(const input_point &ip)
{
    _input_point_list.push_back(ip);
    return;
}

bool DnpSlaveProtocol::setSlaveCommand( Command command )
{
    setCommand(command);

    if( getCommand() == Command_Complete)
    {
        _input_point_list.clear();
        getApplicationLayer().completeSlave();
    }
    return getCommand() != Command_Invalid;
}

void DnpSlaveProtocol::addObjectBlock(ObjectBlock *objBlock)
{
    if( !objBlock->empty() )
    {
        getApplicationLayer().addObjectBlock(objBlock);
    }
    else
    {
        delete objBlock;
    }
}

void DnpSlaveProtocol::setOptions( int options, int seqNumber )
{
    Inherited::setOptions(options);
    getApplicationLayer().setSequenceNumber(seqNumber);
}

}
}

