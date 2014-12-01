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
                boost::ptr_map<unsigned, AnalogInput> analogs;
                boost::ptr_map<unsigned, BinaryInput> digitals;
                boost::ptr_map<unsigned, Counter>     counters;

                for each( const input_point &ip in _input_point_list )
                {
                    switch( ip.type )
                    {
                        case AnalogInputType:
                        {
                            std::auto_ptr<AnalogInput> ain(new AnalogInput(AnalogInput::AI_32Bit));
                            ain->setValue(ip.ain.value);
                            ain->setOnlineFlag(ip.online);

                            analogs.insert(ip.control_offset, ain);

                            break;
                        }
                        case DigitalInput:
                        {
                            std::auto_ptr<BinaryInput> bin(new BinaryInput(BinaryInput::BI_WithStatus));
                            bin->setStateValue(ip.din.trip_close);
                            bin->setOnlineFlag(ip.online);

                            digitals.insert(ip.control_offset, bin);

                            break;
                        }
                        case Counters:
                        {
                            std::auto_ptr<Counter> counterin(new Counter(Counter::C_Binary32Bit));
                            counterin->setValue(ip.counterin.value);
                            counterin->setOnlineFlag(ip.online);

                            counters.insert(ip.control_offset, counterin);

                            break;
                        }
                    }
                }

                boost::ptr_deque<ObjectBlock> dobs;

                if( ! analogs.empty() )     dobs.push_back(ObjectBlock::makeLongIndexedBlock(analogs));
                if( ! digitals.empty() )    dobs.push_back(ObjectBlock::makeLongIndexedBlock(digitals));
                if( ! counters.empty() )    dobs.push_back(ObjectBlock::makeLongIndexedBlock(counters));

                getApplicationLayer().setCommand(ApplicationLayer::ResponseResponse, dobs);

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
        getApplicationLayer().completeSlave();
    }
}

void DnpSlaveProtocol::setOptions( int options, int seqNumber )
{
    Inherited::setOptions(options);
    getApplicationLayer().setSequenceNumber(seqNumber);
}

}
}

