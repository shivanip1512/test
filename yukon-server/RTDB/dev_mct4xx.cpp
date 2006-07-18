/*-----------------------------------------------------------------------------*
*
* File:   dev_mct4xx
*
* Date:   10/5/2005
*
* Author: Jess M. Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_mct4xx-arc  $
* REVISION     :  $Revision: 1.17 $
* DATE         :  $Date: 2006/07/18 15:21:53 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <rw\re.h>
#undef mask_                // Stupid RogueWave re.h

#include "dev_mct4xx.h"
#include <boost/regex.hpp>
#include "ctistring.h"
#include "numstr.h"

using namespace::std;

using Cti::Protocol::Emetcon;

const char *CtiDeviceMCT4xx::PutConfigPart_all           = "all";
const char *CtiDeviceMCT4xx::PutConfigPart_tou           = "tou";
const char *CtiDeviceMCT4xx::PutConfigPart_dst           = "dst";
const char *CtiDeviceMCT4xx::PutConfigPart_vthreshold    = "vthreshold";
const char *CtiDeviceMCT4xx::PutConfigPart_demand_lp     = "demandlp";
const char *CtiDeviceMCT4xx::PutConfigPart_options       = "options";
const char *CtiDeviceMCT4xx::PutConfigPart_addressing    = "addressing";
const char *CtiDeviceMCT4xx::PutConfigPart_disconnect    = "disconnect";
const char *CtiDeviceMCT4xx::PutConfigPart_holiday       = "holiday";
const char *CtiDeviceMCT4xx::PutConfigPart_usage         = "usage";
const char *CtiDeviceMCT4xx::PutConfigPart_llp           = "llp";
const char *CtiDeviceMCT4xx::PutConfigPart_lpchannel     = "lpchannel";
const char *CtiDeviceMCT4xx::PutConfigPart_relays        = "relays";
const char *CtiDeviceMCT4xx::PutConfigPart_precanned_table = "precannedtable";
const char *CtiDeviceMCT4xx::PutConfigPart_centron       = "centron";

const CtiDeviceMCT4xx::ConfigPartsList CtiDeviceMCT4xx::_config_parts = initConfigParts();

CtiDeviceMCT4xx::CtiDeviceMCT4xx()
{
}

CtiDeviceMCT4xx::CtiDeviceMCT4xx(const CtiDeviceMCT4xx& aRef)
{
    *this = aRef;
}

CtiDeviceMCT4xx::~CtiDeviceMCT4xx()
{
}

CtiDeviceMCT4xx &CtiDeviceMCT4xx::operator=(const CtiDeviceMCT4xx &aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        LockGuard guard(monitor());            // Protect this device!
    }
    return *this;
}

CtiDeviceMCT4xx::ConfigPartsList CtiDeviceMCT4xx::initConfigParts()
{
    ConfigPartsList tempList;
    tempList.push_back(PutConfigPart_dst);
    tempList.push_back(PutConfigPart_vthreshold);
    tempList.push_back(PutConfigPart_demand_lp);
    tempList.push_back(PutConfigPart_options);
    tempList.push_back(PutConfigPart_addressing);
    tempList.push_back(PutConfigPart_disconnect);
    tempList.push_back(PutConfigPart_holiday);
    tempList.push_back(PutConfigPart_usage);
    tempList.push_back(PutConfigPart_llp);


    return tempList;
}

CtiDeviceMCT4xx::ConfigPartsList CtiDeviceMCT4xx::getPartsList()
{
    return _config_parts;
}

INT CtiDeviceMCT4xx::executePutConfig(CtiRequestMsg                  *pReq,
                                   CtiCommandParser               &parse,
                                   OUTMESS                        *&OutMessage,
                                   list< CtiMessage* >      &vgList,
                                   list< CtiMessage* >      &retList,
                                   list< OUTMESS* >         &outList)
{
    bool  found = false;
    INT   nRet = NoError, sRet;

    if( parse.isKeyValid("install") )
    {
        if( parse.getsValue("installvalue") == PutConfigPart_all )
        {
            ConfigPartsList tempList = getPartsList();
            if(!tempList.empty())
            {
                CtiRequestMsg *tempReq = CTIDBG_new CtiRequestMsg(*pReq);

                // Load all the other stuff that is needed
                OutMessage->DeviceID  = getID();
                OutMessage->TargetID  = getID();
                OutMessage->Port      = getPortID();
                OutMessage->Remote    = getAddress();
                OutMessage->Priority  = MAXPRIORITY-4;//standard seen in rest of devices.
                OutMessage->TimeOut   = 2;
                OutMessage->Retry     = 2;
                OutMessage->Sequence = Cti::Protocol::Emetcon::PutConfig_Install;  //  this will be handled by the putconfig decode - basically, a no-op
                OutMessage->Request.RouteID   = getRouteID();

                for(CtiDeviceMCT4xx::ConfigPartsList::const_iterator tempItr = tempList.begin();tempItr != tempList.end();tempItr++)
                {
                    if( tempReq != NULL && *tempItr != PutConfigPart_all)//_all == infinite loop == unhappy program == very unhappy jess
                    {
                        string tempString = pReq->CommandString();
                        string replaceString = " ";
                        replaceString += *tempItr; //FIX_ME Consider not keeping the old string but just creating a new, internal string.
                        replaceString += " ";

                        CtiToLower(tempString);

                        CtiString ts_tempString = tempString;
                        boost::regex re (" all($| )");
                        ts_tempString.replace( re,replaceString );
                        tempString = ts_tempString;

                        tempReq->setCommandString(tempString);

                        tempReq->setConnectionHandle(pReq->getConnectionHandle());

                        CtiCommandParser parseSingle(tempReq->CommandString());

                        sRet = executePutConfigSingle(tempReq, parseSingle, OutMessage, vgList, retList, outList);
                    }
                }

                if(tempReq!=NULL)
                {
                    delete tempReq;
                    tempReq = NULL;
                }

            }

        }
        else
        {
            strncpy(OutMessage->Request.CommandStr, (pReq->CommandString()).c_str(), COMMAND_STR_SIZE);
            nRet = executePutConfigSingle(pReq, parse, OutMessage, vgList, retList, outList);
        }
        recordMultiMessageRead(outList);
        incrementGroupMessageCount(pReq->UserMessageId(), (long)pReq->getConnectionHandle(), outList.size());

        if( !outList.empty() && !retList.empty() )
        {
            //hackish way to fix problem of retlist automatically telling commander to not expect more anymore.
            //pil will not set expectmore on the last entry, so I do it by hand...
            //This may be useless at the moment, but is on my way to controlling expect more properly.
            ((CtiReturnMsg*)retList.back())->setExpectMore(1);
        }

        if(OutMessage!=NULL)
        {
            delete OutMessage;
            OutMessage = NULL;
        }
    }
    else if( parse.isKeyValid("timezone_offset") ||
             parse.isKeyValid("timezone_name") )
    {
        unsigned short function, length, io;

        if( getOperation(Emetcon::PutConfig_TimeZoneOffset, function, length, io) )
        {
            int timezone_blocks = 0;

            if( parse.isKeyValid("timezone_offset") )
            {
                double timezone_offset = parse.getdValue("timezone_offset", -999.0);

                timezone_blocks = (int)(timezone_offset * 4.0);
            }
            if( parse.isKeyValid("timezone_name") )
            {
                string timezone_name = parse.getsValue("timezone_name");

                if( !timezone_name.empty() )
                {
                    switch( timezone_name.at(0) )
                    {
                        case 'h':   timezone_blocks = -10 * 4;   break;  //  hawaiian time
                        case 'a':   timezone_blocks =  -9 * 4;   break;  //  alaskan time
                        case 'p':   timezone_blocks =  -8 * 4;   break;  //  pacific time
                        case 'm':   timezone_blocks =  -7 * 4;   break;  //  mountain time
                        case 'c':   timezone_blocks =  -6 * 4;   break;  //  central time
                        case 'e':   timezone_blocks =  -5 * 4;   break;  //  eastern time
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }

            OutMessage->Sequence = Emetcon::PutConfig_TimeZoneOffset;
            OutMessage->Buffer.BSt.Function   = function;
            OutMessage->Buffer.BSt.Length     = length;
            OutMessage->Buffer.BSt.IO         = io;
            OutMessage->Buffer.BSt.Message[0] = timezone_blocks;
        }
        else
        {
            nRet = NoMethod;
        }
    }
    else
    {
        nRet = Inherited::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;

}

int CtiDeviceMCT4xx::executePutConfigSingle(CtiRequestMsg         *pReq,
                                   CtiCommandParser               &parse,
                                   OUTMESS                        *&OutMessage,
                                   list< CtiMessage* >      &vgList,
                                   list< CtiMessage* >      &retList,
                                   list< OUTMESS* >         &outList)
{
    // Load all the other stuff that is needed
    OutMessage->DeviceID  = getID();
    OutMessage->TargetID  = getID();
    OutMessage->Port      = getPortID();
    OutMessage->Remote    = getAddress();
    OutMessage->Priority  = MAXPRIORITY-4;//standard seen in rest of devices.
    OutMessage->TimeOut   = 2;
    OutMessage->Retry     = 2;
    OutMessage->Sequence = Cti::Protocol::Emetcon::PutConfig_Install;  //  this will be handled by the putconfig decode - basically, a no-op
    OutMessage->Request.RouteID   = getRouteID();

    string installValue = parse.getsValue("installvalue");

    int nRet = NORMAL;
    if( installValue == PutConfigPart_tou )
    {
        nRet = executePutConfigTOU(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_dst )
    {
        nRet = executePutConfigDst(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_vthreshold )
    {
        nRet = executePutConfigVThreshold(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_demand_lp )
    {
        nRet = executePutConfigDemandLP(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_options )
    {
        nRet = executePutConfigOptions(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_addressing )
    {
        nRet = executePutConfigAddressing(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_disconnect )
    {
        nRet = executePutConfigDisconnect(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_holiday )
    {
        nRet = executePutConfigHoliday(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_usage )
    {
        nRet = executePutConfigUsage(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_llp )
    {
        nRet = executePutConfigLongLoadProfile(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_lpchannel )
    {
        nRet = executePutConfigLoadProfileChannel(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_relays )
    {
        nRet = executePutConfigRelays(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_precanned_table )
    {
        nRet = executePutConfigPrecannedTable(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_centron )
    {
        nRet = executePutConfigCentron(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else
    {   //Not sure if this is correct, this could just return NoMethod. This is here
        //just in case anyone wants to use a putconfig install  for anything but configs.
        //nRet = Inherited::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
        nRet = NoMethod;
    }

    if( nRet != NORMAL )
    {
        CtiString resultString;

        if( nRet == NoConfigData )
        {
            resultString = "ERROR: Invalid config data. Config name:" + installValue;
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime( ) << " Device " << getName( ) << " had no configuration for config: " << installValue << endl;
        }
        else
        {
            resultString = "ERROR: NoMethod or invalid config. Config name:" + installValue;
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime( ) << " Device " << getName( ) << " had a configuration error using config " << installValue << endl;
        }

        retList.push_back( CTIDBG_new CtiReturnMsg(getID( ),
                                                string(OutMessage->Request.CommandStr),
                                                resultString,
                                                nRet,
                                                OutMessage->Request.RouteID,
                                                OutMessage->Request.MacroOffset,
                                                OutMessage->Request.Attempt,
                                                OutMessage->Request.TrxID,
                                                OutMessage->Request.UserID,
                                                OutMessage->Request.SOE,
                                                CtiMultiMsg_vec( )) );
    }

    return nRet;
}


INT CtiDeviceMCT4xx::decodePutConfig(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT   status = NORMAL,
          j;
    ULONG pfCount = 0;
    string resultString;

    CtiReturnMsg  *ReturnMsg = NULL;

    bool expectMore = false;

    INT ErrReturn = InMessage->EventCode & 0x3fff;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        switch( InMessage->Sequence )
        {
            case Emetcon::PutConfig_Install:
            {
                if(InMessage->Buffer.DSt.Length>0)
                {
                    resultString = "Config data received: ";
                    for(int i = 0; i<InMessage->Buffer.DSt.Length;i++)
                    {
                        resultString.append( (CtiNumStr(InMessage->Buffer.DSt.Message[i]).hex().zpad(2)).toString(),0,2);
                    }
                }
                ReturnMsg->setUserMessageId(InMessage->Return.UserID);
                ReturnMsg->setResultString( resultString );

                if( InMessage->MessageFlags & MessageFlag_ExpectMore || getGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection)!=0 )
                {
                    ReturnMsg->setExpectMore(true);
                }

                retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

                //note that at the moment only putconfig install will ever have a group message count.
                decrementGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection);

                break;
            }

            default:
            {
                status = Inherited::decodePutConfig(InMessage,TimeNow,vgList,retList,outList);
                break;
            }
        }

    }
    return status;
}


using namespace Cti;
using namespace Config;
using namespace MCT;
int CtiDeviceMCT4xx::executePutConfigVThreshold(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if(deviceConfig)
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTVThreshold);

        if(tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTVThreshold)
        {
            MCTVThresholdSPtr config = boost::static_pointer_cast< ConfigurationPart<MCTVThreshold> >(tempBasePtr);

            long underVThreshold, overVThreshold;
            USHORT function, length, io;

            underVThreshold = config->getLongValueFromKey(UnderVoltageThreshold);
            overVThreshold = config->getLongValueFromKey(OverVoltageThreshold);

            if(!getOperation(Emetcon::PutConfig_VThreshold, function, length, io))
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Operation PutConfig_VTreshold not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            if(underVThreshold == std::numeric_limits<long>::min())
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored for UnderVoltageThreshold **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if(parse.isKeyValid("force") || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_UnderVoltageThreshold) != underVThreshold
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_OverVoltageThreshold) != overVThreshold)
                {
                    OutMessage->Buffer.BSt.Function   = function;
                    OutMessage->Buffer.BSt.Length     = length;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Write;
                    OutMessage->Buffer.BSt.Message[0] = (overVThreshold>>8);
                    OutMessage->Buffer.BSt.Message[1] = (overVThreshold);
                    OutMessage->Buffer.BSt.Message[2] = (underVThreshold>>8);
                    OutMessage->Buffer.BSt.Message[3] = (underVThreshold);

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                    OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                    OutMessage->Priority             += 1;//return to normal
                }
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;

    return nRet;
}

int CtiDeviceMCT4xx::executePutConfigDemandLP(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    return NoMethod;
}

int CtiDeviceMCT4xx::executePutConfigLoadProfileChannel(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    return NoMethod;
}

int CtiDeviceMCT4xx::executePutConfigRelays(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    return NoMethod;
}

int CtiDeviceMCT4xx::executePutConfigPrecannedTable(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    return NoMethod;
}

int CtiDeviceMCT4xx::executePutConfigOptions(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    return NoMethod;
}

int CtiDeviceMCT4xx::executePutConfigCentron(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    return NoMethod;
}

int CtiDeviceMCT4xx::executePutConfigAddressing(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if(deviceConfig)
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTAddressing);

        if(tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTAddressing)
        {
            long lead, bronze, collection, spid;
            USHORT function, length, io;

            MCTAddressingSPtr config = boost::static_pointer_cast< ConfigurationPart<MCTAddressing> >(tempBasePtr);

            lead = config->getLongValueFromKey(Lead);
            bronze = config->getLongValueFromKey(Bronze);
            collection = config->getLongValueFromKey(Collection);
            spid = config->getLongValueFromKey(ServiceProviderID);

            if(!getOperation(Emetcon::PutConfig_Addressing, function, length, io))
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Operation PutConfig_Addressing not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            if(lead == std::numeric_limits<long>::min() || bronze == std::numeric_limits<long>::min() || collection == std::numeric_limits<long>::min() || spid == std::numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if(parse.isKeyValid("force") || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressBronze) != bronze
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressLead) != lead
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressCollection) != collection
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID) != spid )
                {
                    OutMessage->Buffer.BSt.Function   = function;
                    OutMessage->Buffer.BSt.Length     = length;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Write;
                    OutMessage->Buffer.BSt.Message[0] = (bronze);
                    OutMessage->Buffer.BSt.Message[1] = (lead>>8);
                    OutMessage->Buffer.BSt.Message[2] = (lead);
                    OutMessage->Buffer.BSt.Message[3] = (collection>>8);
                    OutMessage->Buffer.BSt.Message[4] = (collection);
                    OutMessage->Buffer.BSt.Message[5] = spid;

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                    OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                    OutMessage->Priority             += 1;//return to normal
                }
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;

    return nRet;
}

int CtiDeviceMCT4xx::executePutConfigDst(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if(deviceConfig)
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTDST);

        if(tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTDST)
        {
            long dstBegin, dstEnd, timezoneOffset;
            USHORT function, length, io;
            MCT_DST_SPtr dstConfig = boost::static_pointer_cast< ConfigurationPart<MCT_DST> >(tempBasePtr);
            dstBegin = dstConfig->getLongValueFromKey(DstBegin);
            dstEnd = dstConfig->getLongValueFromKey(DstEnd);
            timezoneOffset = dstConfig->getLongValueFromKey(TimeZoneOffset);

            if(!getOperation(Emetcon::PutConfig_DST, function, length, io))
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Operation PutConfig_DST not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            if(dstBegin == std::numeric_limits<long>::min() || dstEnd == std::numeric_limits<long>::min() || timezoneOffset == std::numeric_limits<long>::min())
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if(parse.isKeyValid("force") || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DSTStartTime) != dstBegin
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DSTEndTime) != dstEnd
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset) != timezoneOffset)
                {
                    OutMessage->Buffer.BSt.Function   = function;
                    OutMessage->Buffer.BSt.Length     = length;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Write;
                    OutMessage->Buffer.BSt.Message[0] = (dstBegin>>24);
                    OutMessage->Buffer.BSt.Message[1] = (dstBegin>>16);
                    OutMessage->Buffer.BSt.Message[2] = (dstBegin>>8);
                    OutMessage->Buffer.BSt.Message[3] = (dstBegin);
                    OutMessage->Buffer.BSt.Message[4] = (dstEnd>>24);
                    OutMessage->Buffer.BSt.Message[5] = (dstEnd>>16);
                    OutMessage->Buffer.BSt.Message[6] = (dstEnd>>8);
                    OutMessage->Buffer.BSt.Message[7] = (dstEnd);
                    OutMessage->Buffer.BSt.Message[8] = (timezoneOffset);


                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                    OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                    OutMessage->Priority             += 1;//return to normal

                    nRet = NORMAL;
                }
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;

    return nRet;
}

int CtiDeviceMCT4xx::executePutConfigTOU(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    return NoMethod;
}

int CtiDeviceMCT4xx::executePutConfigDisconnect(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    return NoMethod;
}

int CtiDeviceMCT4xx::executePutConfigHoliday(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if(deviceConfig)
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTHoliday);

        if(tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTHoliday)
        {
            long holiday1, holiday2, holiday3;
            USHORT function, length, io;

            MCTHolidaySPtr config = boost::static_pointer_cast< ConfigurationPart<MCTHoliday> >(tempBasePtr);
            holiday1 = config->getLongValueFromKey(HolidayDate1);
            holiday2 = config->getLongValueFromKey(HolidayDate2);
            holiday3 = config->getLongValueFromKey(HolidayDate3);

            if(!getOperation(Emetcon::PutConfig_Holiday, function, length, io))
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Operation PutConfig_Holiday not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            if(holiday1 == std::numeric_limits<long>::min() || holiday2 == std::numeric_limits<long>::min() || holiday3 == std::numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if(parse.isKeyValid("force") || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Holiday1) != holiday1
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Holiday2) != holiday2
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Holiday3) != holiday3 )
                {
                    OutMessage->Buffer.BSt.Function   = function;
                    OutMessage->Buffer.BSt.Length     = length;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Write;
                    OutMessage->Buffer.BSt.Message[0] = (holiday1>>24);
                    OutMessage->Buffer.BSt.Message[1] = (holiday1>>16);
                    OutMessage->Buffer.BSt.Message[2] = (holiday1>>8);
                    OutMessage->Buffer.BSt.Message[3] = (holiday1);
                    OutMessage->Buffer.BSt.Message[4] = (holiday2>>24);
                    OutMessage->Buffer.BSt.Message[5] = (holiday2>>16);
                    OutMessage->Buffer.BSt.Message[6] = (holiday2>>8);
                    OutMessage->Buffer.BSt.Message[7] = (holiday2);
                    OutMessage->Buffer.BSt.Message[8] = (holiday3>>24);
                    OutMessage->Buffer.BSt.Message[9] = (holiday3>>16);
                    OutMessage->Buffer.BSt.Message[10] = (holiday3>>8);
                    OutMessage->Buffer.BSt.Message[11] = (holiday3);
                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                    OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                    OutMessage->Priority             += 1;//return to normal
                }
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;

    return nRet;
}

int CtiDeviceMCT4xx::executePutConfigUsage(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    return NoMethod;
}


int CtiDeviceMCT4xx::executePutConfigLongLoadProfile(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if(deviceConfig)
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTLongLoadProfile);

        if(tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTLongLoadProfile)
        {
            long channel1, channel2, channel3, channel4, spid;
            USHORT function, length, io;


            MCTLongLoadProfileSPtr config = boost::static_pointer_cast< ConfigurationPart<MCTLongLoadProfile> >(tempBasePtr);
            channel1 = config->getLongValueFromKey(Channel1Length);
            channel2 = config->getLongValueFromKey(Channel2Length);
            channel3 = config->getLongValueFromKey(Channel3Length);
            channel4 = config->getLongValueFromKey(Channel4Length);
            spid = CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID);

            if( spid == numeric_limits<long>::min() )
            {
                //We dont have it in dynamic pao info yet, we will get it from the config tables
                BaseSPtr addressTempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTAddressing);

                if(addressTempBasePtr && addressTempBasePtr->getType() == ConfigTypeMCTAddressing)
                {
                    MCTAddressingSPtr addressConfig = boost::static_pointer_cast< ConfigurationPart<MCTAddressing> >(addressTempBasePtr);
                    spid = addressConfig->getLongValueFromKey(ServiceProviderID);
                }
            }

            if(!getOperation(Emetcon::PutConfig_LongloadProfile, function, length, io))
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Operation PutConfig_LongloadProfile not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            if(channel1 == numeric_limits<long>::min() || channel3 == numeric_limits<long>::min() || channel2 == numeric_limits<long>::min()
               || channel4 == numeric_limits<long>::min() || spid == numeric_limits<long>::min())
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if(parse.isKeyValid("force") || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPChannel1Len) != channel1
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPChannel2Len) != channel2
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPChannel3Len) != channel3
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len) != channel4 )
                {
                    OutMessage->Buffer.BSt.Function   = function;
                    OutMessage->Buffer.BSt.Length     = length;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0] = (spid);
                    OutMessage->Buffer.BSt.Message[1] = (channel1);
                    OutMessage->Buffer.BSt.Message[2] = (channel2);
                    OutMessage->Buffer.BSt.Message[3] = (channel3);
                    OutMessage->Buffer.BSt.Message[4] = (channel4);

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    getOperation(Emetcon::GetConfig_LongloadProfile, function, length, io);
                    OutMessage->Buffer.BSt.Function   = function;
                    OutMessage->Buffer.BSt.Length     = length;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Read;
                    OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                    OutMessage->Priority             += 1;//return to normal
                }
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;


    return nRet;
}

