#include "precompiled.h"

#include "dev_mct420.h"
#include "config_device.h"

#include "dev_mct420_commands.h"

#include <boost/assign/list_of.hpp>

using namespace Cti::Devices::Commands;
using namespace Cti::Protocols;
using std::string;
using std::endl;
using std::list;
using std::vector;
using std::make_pair;

namespace Cti {
namespace Devices {


const Mct420Device::CommandSet       Mct420Device::_commandStore = boost::assign::list_of
    (CommandStore(EmetconProtocol::GetConfig_Multiplier,          EmetconProtocol::IO_Function_Read, 0xf3, 2))
    (CommandStore(EmetconProtocol::GetConfig_MeterParameters,     EmetconProtocol::IO_Function_Read, 0xf3, 2))
    (CommandStore(EmetconProtocol::GetConfig_DailyReadInterest,   EmetconProtocol::IO_Function_Read, 0x1e, 11))
    (CommandStore(EmetconProtocol::GetConfig_Options,             EmetconProtocol::IO_Function_Read, 0x01, 6))
    (CommandStore(EmetconProtocol::PutConfig_Channel2NetMetering, EmetconProtocol::IO_Write,         0x85, 0));


const Mct420Device::ConfigPartsList  Mct420Device::_config_parts = boost::assign::list_of
    (PutConfigPart_display);


const Mct420Device::FunctionReadValueMappings Mct420Device::_readValueMaps = boost::assign::map_list_of
    (0x000, boost::assign::map_list_of
        ( 0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision))
        ( 1, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_SSpec        )))
    (0x04f, boost::assign::map_list_of
        ( 0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_ScheduledFreezeDay)))
    (0x005, boost::assign::map_list_of
        ( 5, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1))
        ( 6, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2))
        ( 7, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask )))
    (0x00f, boost::assign::map_list_of
        ( 0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_DisplayParameters)))
    (0x013, boost::assign::map_list_of
        ( 0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_AddressBronze           ))
        ( 1, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_AddressLead             ))
        ( 3, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_AddressCollection       ))
        ( 5, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID)))
    (0x019, boost::assign::map_list_of
        ( 0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_TransformerRatio)))
    (0x01a, boost::assign::map_list_of
        ( 0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_DemandInterval       ))
        ( 1, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval  ))
        ( 2, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_VoltageDemandInterval))
        ( 3, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_VoltageLPInterval    )))
    (0x01e, boost::assign::map_list_of
        ( 0, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_OverVoltageThreshold ))
        ( 2, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_UnderVoltageThreshold))
        ( 4, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_OutageCycles         )))
    (0x022, boost::assign::map_list_of
        ( 0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_OutageCycles)))
    (0x036, boost::assign::map_list_of
        ( 0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_TimeAdjustTolerance)))
    (0x03f, boost::assign::map_list_of
        ( 0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset)))
    (0x0d0, boost::assign::map_list_of
        ( 0, make_value_descriptor(4, CtiTableDynamicPaoInfo::Key_MCT_Holiday1))
        ( 4, make_value_descriptor(4, CtiTableDynamicPaoInfo::Key_MCT_Holiday2))
        ( 8, make_value_descriptor(4, CtiTableDynamicPaoInfo::Key_MCT_Holiday3)))
    (0x0d4, boost::assign::map_list_of
        ( 0, make_value_descriptor(4, CtiTableDynamicPaoInfo::Key_MCT_Holiday2))
        ( 4, make_value_descriptor(4, CtiTableDynamicPaoInfo::Key_MCT_Holiday3)))
    (0x0d8, boost::assign::map_list_of
        ( 0, make_value_descriptor(4, CtiTableDynamicPaoInfo::Key_MCT_Holiday3)))
    (0x101, boost::assign::map_list_of
        ( 0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_Configuration))
        ( 1, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1))
        ( 2, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2))
        ( 3, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask))
        ( 5, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_Options)))
    (0x19d, boost::assign::map_list_of
        ( 4, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel1Len))
        ( 5, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel2Len))
        ( 6, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel3Len))
        ( 7, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len)))
    (0x1ad, boost::assign::map_list_of
        ( 0, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_DayTable))
        ( 2, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate))
        (10, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset)))
    (0x1f6, boost::assign::map_list_of
        ( 0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric01))
        ( 1, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric02))
        ( 2, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric03))
        ( 3, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric04))
        ( 4, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric05))
        ( 5, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric06))
        ( 6, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric07))
        ( 7, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric08))
        ( 8, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric09))
        ( 9, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric10))
        (10, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric11))
        (11, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric12))
        (12, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric13)))
    (0x1f7, boost::assign::map_list_of
        ( 0, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric14))
        ( 1, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric15))
        ( 2, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric16))
        ( 3, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric17))
        ( 4, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric18))
        ( 5, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric19))
        ( 6, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric20))
        ( 7, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric21))
        ( 8, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric22))
        ( 9, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric23))
        (10, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric24))
        (11, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric25))
        (12, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric26)))
    (0x1fe, boost::assign::map_list_of
        ( 5, make_value_descriptor(2, CtiTableDynamicPaoInfo::Key_MCT_DemandThreshold  ))
        ( 7, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_ConnectDelay     ))
        ( 9, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_DisconnectMinutes))
        (10, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_ConnectMinutes   ))
        (11, make_value_descriptor(1, CtiTableDynamicPaoInfo::Key_MCT_Configuration    )));


const Mct420Device::FlagSet Mct420Device::_eventFlags = boost::assign::list_of
    (make_pair(0x0001, "Zero usage"))
    (make_pair(0x0002, "Disconnect error"))
    (make_pair(0x0004, "Meter reading corrupted"))
    (make_pair(0x0008, "Reverse power"))
    // 0x0010-0x0080 unused
    (make_pair(0x0100, "Power fail event"))
    (make_pair(0x0200, "Under voltage event"))
    (make_pair(0x0400, "Over voltage event"))
    (make_pair(0x0800, "RTC lost"))
    (make_pair(0x1000, "RTC adjusted"))
    (make_pair(0x2000, "Holiday flag"))
    (make_pair(0x4000, "DST change"))
    (make_pair(0x8000, "Tamper flag"));


const Mct420Device::FlagSet Mct420Device::_meterAlarmFlags = boost::assign::list_of
    (make_pair(0x0001, "Unprogrammed"))
    (make_pair(0x0002, "Configuration error"))
    (make_pair(0x0004, "Self check error"))
    (make_pair(0x0008, "RAM failure"))
    // 0x0010 unsupported
    (make_pair(0x0020, "Non-volatile memory failure"))
    // 0x0040 unsupported
    (make_pair(0x0080, "Measurement error"))
    // 0x0100-0x0400 and 0x100-0x800 unsupported
    (make_pair(0x0800, "Power failure"));


const Mct420Device::ReadDescriptor Mct420Device::getDescriptorForRead(const unsigned char io, const unsigned function, const unsigned readLength) const
{
    unsigned read = function;

    switch( io )
    {
        case EmetconProtocol::IO_Function_Read:
        {
            read += 0x100;
            //  fall through
        }
        case EmetconProtocol::IO_Read:
        {
            if( const FunctionReadValueMappings *rm = getReadValueMaps() )
            {
                FunctionReadValueMappings::const_iterator itr = rm->find(read);

                if( itr != rm->end() )
                {
                    return getDescriptorFromMapping(itr->second, 0, readLength);
                }
            }
        }
    }

    return ReadDescriptor();
}


bool Mct420Device::getOperation( const UINT &cmd, BSTRUCT &bst ) const
{
    if( getOperationFromStore(_commandStore, cmd, bst) )
    {
        return true;
    }

    return Inherited::getOperation(cmd, bst);
}


const Mct420Device::FunctionReadValueMappings *Mct420Device::getReadValueMaps(void) const
{
    return &_readValueMaps;
}


Mct420Device::ConfigPartsList Mct420Device::getPartsList()
{
    return _config_parts;
}


unsigned Mct420Device::getUsageReportDelay(const unsigned interval_length, const unsigned days) const
{
    const int fixed_delay    = gConfigParms.getValueAsInt("PORTER_MCT_PEAK_REPORT_DELAY", 10);

    //  Calculates at least 36 days of usage no matter how many days are requested
    const unsigned intervals = std::max(days, 36U) * intervalsPerDay(interval_length);

    const unsigned variable_delay = (intervals * 10 + 999) / 1000;  //  10 ms per interval, rounded up to the next second

    return fixed_delay + variable_delay;
}


bool Mct420Device::isProfileTablePointerCurrent(const unsigned char table_pointer, const CtiTime TimeNow, const unsigned interval_len) const
{
    const unsigned long intervals_since_epoch = TimeNow.seconds() / interval_len;

    //  truncating on purpose
    const unsigned char expected_table_pointer = intervals_since_epoch / 6 - 1;

    return table_pointer == expected_table_pointer;
}


int Mct420Device::executePutConfig( CtiRequestMsg        *pReq,
                                    CtiCommandParser     &parse,
                                    OUTMESS             *&OutMessage,
                                    list< CtiMessage* >  &vgList,
                                    list< CtiMessage* >  &retList,
                                    list< OUTMESS* >     &outList )
{
    //  Load all the other stuff that is needed
    OutMessage->TargetID  = getID();
    OutMessage->Retry     = 2;

    OutMessage->Request.RouteID   = getRouteID();
    strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

    if( parse.isKeyValid("lcd_cycle_time") )
    {
        unsigned char meter_parameters = 0x00;  //  default, see sspec for details

        OutMessage->Buffer.BSt.Function = FuncWrite_MeterParametersPos;
        OutMessage->Buffer.BSt.Length   = FuncWrite_MeterParametersLen;
        OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Write;

        OutMessage->Sequence            = EmetconProtocol::PutConfig_Parameters;

        OutMessage->Buffer.BSt.Message[0] = gMCT400SeriesSPID;

        const unsigned cycle_time = parse.getiValue("lcd_cycle_time");

        if( cycle_time > 15 )
        {
            returnErrorMessage(BADPARAM, OutMessage, retList,
                               "Invalid LCD cycle time (" + CtiNumStr(cycle_time) + "), must be 0-15");
        }

        OutMessage->Buffer.BSt.Message[1] = cycle_time;

        if( parse.isKeyValid("transformer_ratio") )
        {
            int transformer_ratio = parse.getiValue("transformer_ratio");

            if( transformer_ratio > 0 && transformer_ratio <= 255 )
            {
                OutMessage->Buffer.BSt.Message[2] = parse.getiValue("transformer_ratio");
            }
            else
            {
                returnErrorMessage(BADPARAM, OutMessage, retList,
                                   "Invalid transformer ratio (" + CtiNumStr(transformer_ratio) + "), must be 1-255");
            }
        }
        else
        {
            //  omit the multiplier ratio
            OutMessage->Buffer.BSt.Length--;
        }

        return ExecutionComplete;
    }
    else if( parse.isKeyValid("channel_2_configuration") )
    {
        OutMessage->Sequence = EmetconProtocol::PutConfig_Channel2NetMetering;
        bool found = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);

        string optionsString = parse.getsValue("channel_2_configuration_setting");

        if( optionsString == "none" )
        {
            // We need to increment the function to disable net metering.
            OutMessage->Buffer.BSt.Function += 1;
        }

        if( !found )
        {
            return NoMethod;
        }

        return NoError;
    }
    else
    {
        return Inherited::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }
}


int Mct420Device::executePutConfigDisplay(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* > &outList, bool readsOnly)
{
    Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( ! deviceConfig )
    {
        return NoConfigData;
    }

    vector<unsigned char> display_metrics;

    if( ! readsOnly )
    {
        static const PaoInfoKeys lcd_metric_keys[26] = {

            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric01,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric02,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric03,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric04,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric05,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric06,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric07,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric08,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric09,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric10,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric11,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric12,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric13,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric14,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric15,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric16,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric17,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric18,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric19,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric20,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric21,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric22,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric23,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric24,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric25,
            CtiTableDynamicPaoInfo::Key_MCT_LcdMetric26
        };

        int i = 0;

        vector<unsigned char> paoinfo_metrics;

        for each( const PaoInfoKeys pao_key in lcd_metric_keys )
        {
            string config_key = "Display Item " + CtiNumStr(++i);

            long config_value;

            if( !deviceConfig->getLongValue(config_key, config_value) )
            {
                if( getMCTDebugLevel(DebugLevel_Configs) )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Device \"" << getName() << "\" - invalid value (" << deviceConfig->getValueFromKey(config_key) << ") for config key \"" << config_key << "\" " << __FUNCTION__ << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                return NoConfigData;
            }

            display_metrics.push_back(config_value);

            long pao_value = 0xff;

            //  will not touch pao_value if it's not found
            getDynamicInfo(pao_key, pao_value);

            paoinfo_metrics.push_back(pao_value);
        }

        if( display_metrics.size() == paoinfo_metrics.size() && std::equal(display_metrics.begin(), display_metrics.end(), paoinfo_metrics.begin()) )
        {
            if( ! parse.isKeyValid("force") )
            {
                return ConfigCurrent;
            }
        }
        else
        {
            if( parse.isKeyValid("verify") )
            {
                return ConfigNotCurrent;
            }
        }
    }

    DlcCommandSPtr lcdConfiguration(new Mct420LcdConfigurationCommand(display_metrics, readsOnly));

    if( ! tryExecuteCommand(*OutMessage, lcdConfiguration) )
    {
        return NoMethod;
    }

    outList.push_back(OutMessage);

    OutMessage = NULL;

    return NoError;
}


int Mct420Device::executeGetConfig( CtiRequestMsg        *pReq,
                                    CtiCommandParser     &parse,
                                    OUTMESS             *&OutMessage,
                                    list< CtiMessage* >  &vgList,
                                    list< CtiMessage* >  &retList,
                                    list< OUTMESS* >     &outList )
{
    INT nRet = NoMethod;
    bool found = false;

    //  Explicitly disallow this command for the MCT-420
    if( parse.isKeyValid("centron_parameters") )
    {
        return NoMethod;
    }
    else if( parse.isKeyValid("configuration") )
    {
        OutMessage->Sequence = EmetconProtocol::GetConfig_Options;
        found = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);
    }
    else
    {
    return Inherited::executeGetConfig(pReq, parse, OutMessage, vgList, retList, outList);
}

    if( found )
    {
        // Load all the other stuff that is needed
        //  FIXME:  most of this is taken care of in propagateRequest - we could probably trim a lot of this out
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        nRet = NoError;
    }

    return nRet;
}


DlcBaseDevice::DlcCommandSPtr Mct420Device::makeHourlyReadCommand(const CtiDate date_begin, const CtiDate date_end, const unsigned channel) const
{
    return DlcCommandSPtr(new Mct420HourlyReadCommand(date_begin, date_end, channel));
}


/**
 * Calls the corresponding decode method for the function stored
 * in InMessage->Sequence. All EmetconProtocol commands
 * supported by the MCT-420 that aren't supported by a parent of
 * the MCT-420 must be represented in this function. Virtual
 * decode calls will be made from the parent ModelDecode calls,
 * so any decode that is supported by a parent class should be
 * omitted from this function.
 */
INT Mct420Device::ModelDecode( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    if( !InMessage )
    {
        return MEMORY;
    }

    switch(InMessage->Sequence)
    {
        case EmetconProtocol::GetConfig_Options:
        {
            return decodeGetConfigOptions(InMessage, TimeNow, vgList, retList, outList);
        }

        case EmetconProtocol::PutConfig_Channel2NetMetering:
        {
            return decodePutConfig(InMessage, TimeNow, vgList, retList, outList);
        }
    }

    const int status = Inherited::ModelDecode(InMessage, TimeNow, vgList, retList, outList);

    if( status )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << " IM->Sequence = " << InMessage->Sequence << " " << getName() << endl;
    }

    return status;
}

int Mct420Device::decodePutConfig( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    int status = NoError;
    string resultString;

    switch ( InMessage->Sequence )
    {
        case EmetconProtocol::PutConfig_Channel2NetMetering:
        {
            status = decodePutConfigChannel2NetMetering(InMessage, TimeNow, vgList, retList, outList);
            resultString = getName( ) + " / channel 2 configuration sent";
            break;
        }

        default:
        {
            return Inherited::decodePutConfig(InMessage, TimeNow, vgList, retList, outList);
        }
    }

    // Handle the return message.
    {
        std::auto_ptr<CtiReturnMsg> ReturnMsg(new CtiReturnMsg(getID(), InMessage->Return.CommandStr));

        ReturnMsg->setUserMessageId( InMessage->Return.UserID );
        ReturnMsg->setResultString ( resultString );

        decrementGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection);

        if( InMessage->MessageFlags & MessageFlag_ExpectMore || getGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection)!=0 )
        {
            ReturnMsg->setExpectMore(true);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg.release(), vgList, retList );
    }

    return status;
}


int Mct420Device::decodePutConfigChannel2NetMetering( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    // Execute a read to get the configuration data to be stored in dynamic pao info.
    {
        std::auto_ptr<CtiRequestMsg> pReq(
            new CtiRequestMsg(
                InMessage->TargetID,
                "getconfig configuration",
                InMessage->Return.UserID,
                InMessage->Return.GrpMsgID,
                InMessage->Return.RouteID,
                selectInitialMacroRouteOffset(InMessage->Return.RouteID),
                InMessage->Return.Attempt));

        if( strstr(InMessage->Return.CommandStr, "noqueue") )
        {
            pReq->setCommandString(pReq->CommandString() + " noqueue");
        }

        CtiCommandParser parse(pReq->CommandString());

        beginExecuteRequest(pReq.release(), parse, vgList, retList, outList);
    }

    return NoError;
}


string Mct420Device::decodeDisconnectStatus(const DSTRUCT &DSt)
{
    string resultStr;

    //  and adds load side voltage detection as well
    if( DSt.Message[0] & 0x40 )
    {
        resultStr += "Load side voltage detected\n";
    }

    //  The MCT-420 supports all of the MCT-410's statuses
    resultStr += Mct410Device::decodeDisconnectStatus(DSt);

    return resultStr;
}


int Mct420Device::decodeGetConfigMeterParameters(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{

    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    string resultString = getName() + " / Meter Parameters:\n";

    resultString += "Disconnect display ";
    resultString += (DSt->Message[0] & 0x80) ? "disabled\n" : "enabled\n";

    resultString += "LCD cycle time: ";

    if( const unsigned lcd_cycle_time = DSt->Message[0] & 0x0f )
    {
        resultString += CtiNumStr(lcd_cycle_time) + " seconds\n";
    }
    else
    {
        resultString += "8 seconds (meter default)\n";
    }

    resultString += "Transformer ratio: " + CtiNumStr(DSt->Message[1]) + "\n";

    CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage->Return.CommandStr, NoError, ReturnMsg, vgList, retList );

    return NoError;
}


int Mct420Device::decodeGetConfigModel(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    DSTRUCT &DSt = InMessage->Buffer.DSt;

    const unsigned revision = DSt.Message[0];
    const unsigned sspec    = DSt.Message[1] << 8 |
                              DSt.Message[2];
    string descriptor;

    descriptor += getName() + " / Model information:\n";
    descriptor += "Software Specification " + CtiNumStr(sspec) + " rev ";

    //  convert 10 to 1.0, 24 to 2.4
    descriptor += CtiNumStr(((double)revision) / 10.0, 1);

    descriptor += "\n";

    descriptor += getName() + " / Physical meter configuration:\n";
    descriptor += "Base meter: ";

    switch( sspec % 10 )
    {
        case 0x00:  descriptor += "Itron Centron C2SX";  break;
        default:    descriptor += "(unknown)";
    }

    descriptor += "\n";

    CtiReturnMsg *ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);
    ReturnMsg->setResultString(descriptor);

    retMsgHandler( InMessage->Return.CommandStr, NoError, ReturnMsg, vgList, retList, InMessage->MessageFlags & MessageFlag_ExpectMore );

    return NoError;
}

int Mct420Device::decodeGetConfigOptions( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    DSTRUCT &DSt = InMessage->Buffer.DSt;

    const unsigned configuration  = DSt.Message[0];
    const unsigned eventMask      = DSt.Message[1] << 8 | DSt.Message[2];
    const unsigned meterAlarmMask = DSt.Message[3] << 8 | DSt.Message[4];
    const unsigned options        = DSt.Message[5];

    string descriptor;

    descriptor += getName() + " / Configuration information:\n";

    // Configuration description
    descriptor += configuration & 0x01 ? "DST enabled\n"
                                       : "DST disabled\n";
    descriptor += configuration & 0x02 ? "LED test enabled\n"
                                       : "LED test disabled\n";
    descriptor += configuration & 0x04 ? "Reconnect button required\n"
                                       : "Reconnect button not required\n";
    descriptor += configuration & 0x08 ? "Demand limit mode enabled\n"
                                       : "Demand limit mode disabled\n";
    descriptor += configuration & 0x10 ? "Disconnect cycling mode enabled\n"
                                       : "Disconnect cycling mode disabled\n";
    descriptor += configuration & 0x20 ? "Repeater role enabled\n"
                                       : "Repeater role disabled\n";
    descriptor += configuration & 0x40 ? "Disconnect collar is MCT-410d Rev E (or later)\n"
                                       : "Disconnect collar is not MCT-410d Rev E (or later)\n";
    descriptor += configuration & 0x80 ? "Daily reporting enabled\n"
                                       : "Daily reporting disabled\n";

    // Event mask descriptions
    descriptor += getName() + " / Event mask information:\n";

    for each (const Mct420Device::FlagMask &flag in _eventFlags)
    {
        descriptor += flag.second + " event mask " + string(eventMask & flag.first ? "en" : "dis") + "abled\n";
    }

    // Meter alarm mask descriptions
    descriptor += getName() + " / Meter alarm mask information:\n";

    for each (const Mct420Device::FlagMask &flag in _meterAlarmFlags)
    {
        descriptor += flag.second + " meter alarm mask " + string(eventMask & flag.first ? "en" : "dis") + "abled\n";
    }

    // Options description
    descriptor += getName() + " / Channel configuration:\n";

    if( (options & 0x1c) == 0x1c )
    {
        descriptor += "Channel 2: Net metering mode enabled\n";
    }
    else
    {
        descriptor += "Channel 2: No meter attached\n";
    }

    if( !(options & 0x60) )
    {
        descriptor += "Channel 3: No meter attached\n";
    }

    CtiReturnMsg *ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);
    ReturnMsg->setResultString(descriptor);

    retMsgHandler( InMessage->Return.CommandStr, NoError, ReturnMsg, vgList, retList, InMessage->MessageFlags & MessageFlag_ExpectMore );

    return NoError;
}


int Mct420Device::decodeGetConfigDailyReadInterest(const INMESS &InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    const DSTRUCT &DSt = InMessage.Buffer.DSt;

    std::auto_ptr<CtiReturnMsg> ReturnMsg(
        new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    const unsigned interest_day     =   DSt.Message[9];
    const unsigned interest_month   =  (DSt.Message[10] & 0x0f) + 1;
    const unsigned interest_channel = ((DSt.Message[10] & 0x30) >> 4) + 1;

    string resultString;

    resultString  = getName() + " / Daily read interest channel: " + CtiNumStr(interest_channel) + "\n";
    resultString += getName() + " / Daily read interest month: "   + CtiNumStr(interest_month) + "\n";
    resultString += getName() + " / Daily read interest day: "     + CtiNumStr(interest_day) + "\n";

    tryVerifyDailyReadInterestDate(interest_day, interest_month, TimeNow);

    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, NORMAL, ReturnMsg.release(), vgList, retList );

    return NORMAL;
}


Mct420Device::point_info Mct420Device::decodePulseAccumulator(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter)
{
    return Mct4xxDevice::decodePulseAccumulator(buf, len, freeze_counter);
}


Mct420Device::point_info Mct420Device::getAccumulatorData(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const
{
    point_info pi = Mct420Device::decodePulseAccumulator(buf, len, freeze_counter);

    if( freeze_counter )
    {
        //  The MCT-410's logic assumes that the reading was stored by the previous freeze counter.
        pi.freeze_bit = ! (*freeze_counter & 0x01);
    }

    return pi;
}


Mct420Device::point_info Mct420Device::getDemandData(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const
{
    point_info pi = Mct410Device::getData(buf, len, ValueType_DynamicDemand);

    if( freeze_counter )
    {
        //  The MCT-410's logic assumes that the reading was stored by the previous freeze counter.
        pi.freeze_bit = ! (*freeze_counter & 0x01);
    }

    return pi;
}


//  I wanted to keep devicetypes.h away from everything else...
//    In The Year 2000, we shouldn't have to compare against types, but that's where we're at right now
#include "devicetypes.h"

bool Mct420Device::isSupported(const Mct410Device::Features feature) const
{
    switch( feature )
    {
        case Feature_DisconnectCollar:
        {
            //  this is the only MCT-420 that supports the collar right now
            return getType() == TYPEMCT420FL;
        }
        default:
        {
            return Mct410Device::isSupported(feature);
        }
    }
}


}
}

