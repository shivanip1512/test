#include "yukon.h"

#include "dev_mct420.h"
#include "config_device.h"

#include "dev_mct420_commands.h"

using namespace Cti::Devices::Commands;
using Cti::Protocols::EmetconProtocol;

namespace Cti {
namespace Devices {

const Mct420Device::ConfigPartsList  Mct420Device::_config_parts = Mct420Device::initConfigParts();
const Mct420Device::read_key_store_t Mct420Device::_readKeyStore = Mct420Device::initReadKeyStore();

Mct420Device::ConfigPartsList Mct420Device::initConfigParts()
{
    return ConfigPartsList(1, PutConfigPart_display);
}

Mct420Device::read_key_store_t Mct420Device::initReadKeyStore()
{
    //  inherit the MCT-410's key store...  this is a little awkward, but perhaps the best way to do it
    read_key_store_t readKeyStore = Mct410Device::initReadKeyStore();

    const int read1 = Mct420LcdConfigurationCommand::Read_LcdConfiguration1 & 0xff;

    readKeyStore.insert(read_key_info_t(read1,  0, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric01));
    readKeyStore.insert(read_key_info_t(read1,  1, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric02));
    readKeyStore.insert(read_key_info_t(read1,  2, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric03));
    readKeyStore.insert(read_key_info_t(read1,  3, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric04));
    readKeyStore.insert(read_key_info_t(read1,  4, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric05));
    readKeyStore.insert(read_key_info_t(read1,  5, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric06));
    readKeyStore.insert(read_key_info_t(read1,  6, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric07));
    readKeyStore.insert(read_key_info_t(read1,  7, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric08));
    readKeyStore.insert(read_key_info_t(read1,  8, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric09));
    readKeyStore.insert(read_key_info_t(read1,  9, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric10));
    readKeyStore.insert(read_key_info_t(read1, 10, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric11));
    readKeyStore.insert(read_key_info_t(read1, 11, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric12));
    readKeyStore.insert(read_key_info_t(read1, 12, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric13));

    const int read2 = Mct420LcdConfigurationCommand::Read_LcdConfiguration2 & 0xff;

    readKeyStore.insert(read_key_info_t(read2,  0, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric14));
    readKeyStore.insert(read_key_info_t(read2,  1, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric15));
    readKeyStore.insert(read_key_info_t(read2,  2, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric16));
    readKeyStore.insert(read_key_info_t(read2,  3, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric17));
    readKeyStore.insert(read_key_info_t(read2,  4, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric18));
    readKeyStore.insert(read_key_info_t(read2,  5, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric19));
    readKeyStore.insert(read_key_info_t(read2,  6, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric20));
    readKeyStore.insert(read_key_info_t(read2,  7, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric21));
    readKeyStore.insert(read_key_info_t(read2,  8, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric22));
    readKeyStore.insert(read_key_info_t(read2,  9, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric23));
    readKeyStore.insert(read_key_info_t(read2, 10, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric24));
    readKeyStore.insert(read_key_info_t(read2, 11, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric25));
    readKeyStore.insert(read_key_info_t(read2, 12, 1, CtiTableDynamicPaoInfo::Key_MCT_LcdMetric26));

    return readKeyStore;
}


Mct420Device::ConfigPartsList Mct420Device::getPartsList()
{
    return _config_parts;
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

    string resultString;
    int transformer_ratio = -1;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if( InMessage->Sequence == EmetconProtocol::GetConfig_MeterParameters )
    {
        resultString = getName() + " / Meter Parameters:\n";

        const unsigned lcd_cycle_time = DSt->Message[0] & 0x0f;

        resultString += "LCD cycle time: ";

        if( lcd_cycle_time )
        {
            resultString += CtiNumStr(lcd_cycle_time) + " seconds\n";
        }
        else
        {
            resultString += "8 seconds (meter default)\n";
        }

        //  they did the long read, so assign the multiplier
        if( DSt->Length >= 11 )
        {
            transformer_ratio = DSt->Message[10];
        }
    }
    else if( InMessage->Sequence == EmetconProtocol::GetConfig_Multiplier )
    {
        transformer_ratio = DSt->Message[0];
    }

    if( transformer_ratio >= 0 )
    {
        resultString += getName() + " / Transformer ratio: " + CtiNumStr(transformer_ratio);
    }

    CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage->Return.CommandStr, NoError, ReturnMsg, vgList, retList );

    return NoError;
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

