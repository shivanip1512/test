#include "precompiled.h"

#include "dev_mct420.h"
#include "config_device.h"

#include "dev_mct420_commands.h"

using namespace Cti::Devices::Commands;
using Cti::Protocols::EmetconProtocol;
using std::string;
using std::endl;
using std::list;
using std::vector;

namespace Cti {
namespace Devices {

const Mct420Device::CommandSet       Mct420Device::_commandStore = Mct420Device::initCommandStore();
const Mct420Device::ConfigPartsList  Mct420Device::_config_parts = Mct420Device::initConfigParts();

const Mct420Device::ValueMapping              Mct420Device::_memoryMap             = Mct420Device::initMemoryMap();
const Mct420Device::FunctionReadValueMappings Mct420Device::_functionReadValueMaps = Mct420Device::initFunctionReadValueMaps();


Mct420Device::CommandSet Mct420Device::initCommandStore()
{
    CommandSet cs;

    cs.insert(CommandStore(EmetconProtocol::GetConfig_Multiplier,       EmetconProtocol::IO_Function_Read, 0xf3, 2));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_MeterParameters,  EmetconProtocol::IO_Function_Read, 0xf3, 2));

    return cs;
}

Mct420Device::ConfigPartsList Mct420Device::initConfigParts()
{
    return ConfigPartsList(1, PutConfigPart_display);
}

Mct420Device::ValueMapping Mct420Device::initMemoryMap()
{
    //  inherit the MCT-410's key store...  this is a little awkward, but perhaps the best way to do it
    ValueMapping memoryMap = Mct410Device::initMemoryMap();

    //  Clear out the SSPEC and SSPEC revision - they've changed in the MCT-420
    for( ValueMapping::iterator itr = memoryMap.begin(); itr != memoryMap.end(); )
    {
        switch( itr->second.name )
        {
            case CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision:
            case CtiTableDynamicPaoInfo::Key_MCT_SSpec:
            {
                memoryMap.erase(itr++);
                break;
            }
            default:
            {
                ++itr;
            }
        }
    }

    struct memory_read_value
    {
        unsigned offset;
        value_descriptor value;
    }
    const values[] =
    {
        { 0, { 1, CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision } },
        { 1, { 2, CtiTableDynamicPaoInfo::Key_MCT_SSpec         } },
    };

    for each( memory_read_value mrv in values )
    {
        memoryMap.insert(std::make_pair(mrv.offset, mrv.value));
    }

    return memoryMap;
}


Mct420Device::FunctionReadValueMappings Mct420Device::initFunctionReadValueMaps()
{
    //  Masking off the 0x100 bit
    const int read1 = Mct420LcdConfigurationCommand::Read_LcdConfiguration1 & 0xff;
    const int read2 = Mct420LcdConfigurationCommand::Read_LcdConfiguration2 & 0xff;

    struct function_read_value
    {
        unsigned function;
        unsigned offset;
        value_descriptor value;
    }
    const values[] =
    {
        { read1,  0, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric01 } },
        { read1,  1, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric02 } },
        { read1,  2, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric03 } },
        { read1,  3, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric04 } },
        { read1,  4, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric05 } },
        { read1,  5, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric06 } },
        { read1,  6, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric07 } },
        { read1,  7, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric08 } },
        { read1,  8, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric09 } },
        { read1,  9, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric10 } },
        { read1, 10, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric11 } },
        { read1, 11, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric12 } },
        { read1, 12, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric13 } },

        { read2,  0, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric14 } },
        { read2,  1, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric15 } },
        { read2,  2, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric16 } },
        { read2,  3, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric17 } },
        { read2,  4, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric18 } },
        { read2,  5, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric19 } },
        { read2,  6, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric20 } },
        { read2,  7, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric21 } },
        { read2,  8, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric22 } },
        { read2,  9, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric23 } },
        { read2, 10, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric24 } },
        { read2, 11, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric25 } },
        { read2, 12, { 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric26 } },
    };

    FunctionReadValueMappings fr = Mct410Device::initFunctionReadValueMaps();

    for each( function_read_value frv in values )
    {
        fr[frv.function].insert(std::make_pair(frv.offset, frv.value));
    }

    return fr;
}


bool Mct420Device::getOperation( const UINT &cmd, BSTRUCT &bst ) const
{
    if( getOperationFromStore(_commandStore, cmd, bst) )
    {
        return true;
    }

    return Inherited::getOperation(cmd, bst);
}


const Mct420Device::ValueMapping *Mct420Device::getMemoryMap(void) const
{
    return &_memoryMap;
}


const Mct420Device::FunctionReadValueMappings *Mct420Device::getFunctionReadValueMaps(void) const
{
    return &_functionReadValueMaps;
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
    //  Explicitly disallow this command for the MCT-420
    if( parse.isKeyValid("centron_parameters") )
    {
        return NoMethod;
    }

    return Inherited::executeGetConfig(pReq, parse, OutMessage, vgList, retList, outList);
}


DlcBaseDevice::DlcCommandSPtr Mct420Device::makeHourlyReadCommand(const CtiDate date_begin, const CtiDate date_end, const unsigned channel) const
{
    return DlcCommandSPtr(new Mct420HourlyReadCommand(date_begin, date_end, channel));
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
    if( int status = decodeCheckErrorReturn(InMessage, retList, outList) )
    {
        return status;
    }

    // No error occured, we must do a real decode!

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
    if( int status = decodeCheckErrorReturn(InMessage, retList, outList) )
    {
        return status;
    }

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

