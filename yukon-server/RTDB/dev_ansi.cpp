#include "precompiled.h"
#include "porter.h"
#include "logger.h"
#include "dev_ansi.h"
#include "utility.h"
#include "cmdparse.h"
#include "numstr.h"
#include "ctidate.h"
#include "ctitime.h"

using namespace Cti::Protocols::Ansi;
using std::string;
using std::list;
using std::endl;

namespace Cti {
namespace Devices {

bool isUnintializedTimeAndValue(double value, double time);
//=========================================================================================================================================
//=========================================================================================================================================

CtiDeviceAnsi::CtiDeviceAnsi() :
    _lastLPTime(0)
{
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiDeviceAnsi::buildSingleTableRequest(BYTE *ptr, UINT tableId)
{
}

YukonError_t CtiDeviceAnsi::executeLoopback( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList,
                                             CtiMessageList &retList, OutMessageList &outList)
{
    if( OutMessage != NULL )
    {
         setCurrentCommand( CmdScanData );

         // Load all the other stuff that is needed
         populateRemoteOutMessage(*OutMessage);
         OutMessage->Retry = 3;  //  override
         EstablishOutMessagePriority( OutMessage, MAXPRIORITY );

         //let's populate this list with the tables we want for a general scan...
         BYTE *ptr = OutMessage->Buffer.OutMessage;


         buildSingleTableRequest (ptr, 0);
         CtiReturnMsg *retMsg = NULL;
         retMsg = CTIDBG_new CtiReturnMsg(getID(),
                             pReq->CommandString(),
                             string(getName() + " / loopback in progress"),
                             ClientErrors::None,
                             pReq->RouteId(),
                             pReq->MacroOffset(),
                             1, //pReq->Attempt(),
                             pReq->GroupMessageId(),
                             pReq->UserMessageId());
          retMsg->setExpectMore(true);
          retList.push_back(retMsg);

          retMsg = 0;

          outList.push_back( OutMessage );
          OutMessage = 0;
   }
   else
   {
      return ClientErrors::MemoryAccess;
   }
   return ClientErrors::None;
}

//=========================================================================================================================================
//scanner has decided that it's time to talk to an ansi-talking device and has called up on Us to carry out this mission - let us be brave
//
//we get handed a bunch of junk we don't care about, build a header about the command (GeneralScan) and , then pop down
//to the ansi protocol object to get info about the tables we know we need for a GeneralScan
//=========================================================================================================================================

YukonError_t CtiDeviceAnsi::GeneralScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList,
                                         CtiMessageList &retList, OutMessageList &outList, INT ScanPriority )
{

   ULONG BytesWritten;
   int   adjustment = 0;

   if( OutMessage != NULL )
   {
      setCurrentCommand( CmdScanData );

      // Load all the other stuff that is needed
      populateRemoteOutMessage(*OutMessage);
      OutMessage->Retry = 3;  //  override
      EstablishOutMessagePriority( OutMessage, ScanPriority );

      //let's populate this list with the tables we want for a general scan...
      BYTE *ptr = OutMessage->Buffer.OutMessage;

      if (useScanFlags())
      {
          buildScannerTableRequest (ptr, parse.getFlags());
      }
      else
      {
          buildCommanderTableRequest (ptr, parse.getFlags());
          CtiReturnMsg *retMsg = NULL;
          retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                          pReq->CommandString(),
                                          string(getName() + " / scan general in progress"),
                                          ClientErrors::None,
                                          pReq->RouteId(),
                                          pReq->MacroOffset(),
                                          1, //pReq->Attempt(),
                                          pReq->GroupMessageId(),
                                          pReq->UserMessageId());
          retMsg->setExpectMore(true);
          retList.push_back(retMsg);

          retMsg = 0;

      }
      outList.push_back( OutMessage );


      OutMessage = 0;
   }
   else
   {
      return ClientErrors::MemoryAccess;
   }
   return ClientErrors::None;
}

YukonError_t CtiDeviceAnsi::DemandReset( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList,
                                         CtiMessageList &retList, OutMessageList &outList, INT ScanPriority )
{

   if( OutMessage != NULL )
   {
      setCurrentCommand( CmdScanData );

      // Load all the other stuff that is needed
      populateRemoteOutMessage(*OutMessage);
      OutMessage->Retry = 3;  //  override
      EstablishOutMessagePriority( OutMessage, ScanPriority );

      //let's populate this list with the tables we want for a general scan...
      BYTE *aMsg = OutMessage->Buffer.OutMessage;



      WANTS_HEADER   header;

      //here is the password for the sentinel (should be changed to a cparm, I think)
      BYTE        password[] = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    // here are the tables requested for the sentinel
      ANSI_TABLE_WANTS    table[100] = {
        {  0,     0,      30,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  1,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  8,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  -1,     0,      0,     ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ}};

    string pswdTemp;
    pswdTemp = getIED().getPassword();

    for (int aa = 0; aa < 20; aa++)
        password[aa] = 0;

    const BYTE *temp;
    temp = (const BYTE *)pswdTemp.c_str();
    for (int aa = 0; aa < pswdTemp.length(); aa++)
        password[aa] = *(temp + aa);

    // lazyness so I don't have to continually remember to update this
    header.numTablesRequested = 0;
    header.lastLoadProfileTime = 0;
    //_lastLPTime = header.lastLoadProfileTime;

    for (int x=0; x < 100; x++)
    {
        if (table[x].tableID < 0)
        {
            break;
        }
        else
        {
            header.numTablesRequested++;
        }
    }
    header.command = 5; // ?

    BYTE scanOperation = 2; //2 = demand reset
    UINT flags = parse.getFlags();

    // put the stuff in the buffer
    memcpy( aMsg, &header, sizeof (header));
    memcpy( (aMsg+sizeof(header)), &password, sizeof (password));
    memcpy ((aMsg+sizeof(header)+sizeof(password)),
            &table,
            (header.numTablesRequested*sizeof (ANSI_TABLE_WANTS)));
    memcpy ((aMsg+sizeof(header)+sizeof(password)+(header.numTablesRequested*sizeof (ANSI_TABLE_WANTS))),
            &scanOperation, sizeof(BYTE));
    memcpy ((aMsg+sizeof(header)+sizeof(password)+(header.numTablesRequested*sizeof (ANSI_TABLE_WANTS)) +sizeof(BYTE)),
            &flags, sizeof(UINT));



    outList.push_back( OutMessage );
    OutMessage = 0;
   }
   else
   {
      return ClientErrors::MemoryAccess;
   }

    CTILOG_INFO(dout, "outList.size() = "<< outList.size());

   return ClientErrors::None;
}



YukonError_t CtiDeviceAnsi::ExecuteRequest( CtiRequestMsg    *pReq,
                                            CtiCommandParser  &parse,
                                            OUTMESS          *&OutMessage,
                                            CtiMessageList    &vgList,
                                            CtiMessageList    &retList,
                                            OutMessageList    &outList )
{
    YukonError_t nRet = ClientErrors::None;

    //_parseFlags = parse.getFlags();

    switch( parse.getCommand( ) )
    {
        case LoopbackRequest:
        {
            nRet = executeLoopback( pReq, parse, OutMessage, vgList, retList, outList );
            break;
        }
        case ScanRequest:
        {
            nRet = GeneralScan( pReq, parse, OutMessage, vgList, retList, outList );
            break;
        }
        case PutValueRequest:
        {
            nRet = DemandReset( pReq, parse, OutMessage, vgList, retList, outList );
            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "Unsupported command on Ansi Device route. Command = " << parse.getCommand());

            nRet = ClientErrors::NoMethod;
        }
    }

    if( nRet )
    {
        string resultString;

        CTILOG_ERROR(dout, "Couldn't come up with an operation for device "<< getName() <<". Command = "<< parse.getCommand());

        resultString = "NoMethod or invalid command.";
        retList.push_back( CTIDBG_new CtiReturnMsg(getID( ),
                                                string(OutMessage->Request.CommandStr),
                                                resultString,
                                                nRet,
                                                OutMessage->Request.RouteID,
                                                OutMessage->Request.RetryMacroOffset,
                                                OutMessage->Request.Attempt,
                                                OutMessage->Request.GrpMsgID,
                                                OutMessage->Request.UserID,
                                                OutMessage->Request.SOE,
                                                CtiMultiMsg_vec( )) );
    }


    return nRet;
}


//=========================================================================================================================================
//=========================================================================================================================================

YukonError_t CtiDeviceAnsi::ResultDecode( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList&retList,
                                OutMessageList    &outList)
{
    CtiReturnMsg *retMsg = NULL;
    string inMsgResultString = "";

    inMsgResultString = string((const char*)InMessage.Buffer.InMessage, InMessage.InLength);

    if (getANSIProtocol().getScanOperation() == CtiProtocolANSI::demandReset || getANSIProtocol().getScanOperation() == CtiProtocolANSI::loopBack)
    {
        string returnString = getName() + " / " + (getANSIProtocol().getScanOperation() == CtiProtocolANSI::demandReset ? "demand reset " : "loopback ");
        if (findStringIgnoreCase(inMsgResultString, "successful"))
        {
            returnString += " successful";
        }
        else
        {
            returnString += " failed";
        }
        retMsg = CTIDBG_new CtiReturnMsg(getID(),
                InMessage.Return.CommandStr,
                returnString,
                InMessage.ErrorCode,
                InMessage.Return.RouteID,
                InMessage.Return.RetryMacroOffset,
                InMessage.Return.Attempt,
                InMessage.Return.GrpMsgID,
                InMessage.Return.UserID);
    }
    else
    {
        if (findStringIgnoreCase(inMsgResultString, "general") )
        {
            if (findStringIgnoreCase(inMsgResultString, "successful") )
            {
                retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                InMessage.Return.CommandStr,
                                                //string(),
                                                getName() + " / general scan successful : \n" + _result_string.c_str(),
                                                InMessage.ErrorCode,
                                                InMessage.Return.RouteID,
                                                InMessage.Return.RetryMacroOffset,
                                                InMessage.Return.Attempt,
                                                InMessage.Return.GrpMsgID,
                                                InMessage.Return.UserID);
            }
            else
            {
                 retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                InMessage.Return.CommandStr,
                                                //string(),
                                                getName() + " / general scan failed",
                                                InMessage.ErrorCode,
                                                InMessage.Return.RouteID,
                                                InMessage.Return.RetryMacroOffset,
                                                InMessage.Return.Attempt,
                                                InMessage.Return.GrpMsgID,
                                                InMessage.Return.UserID);
            }



        }
        else if (useScanFlags())
        {
           const unsigned long *lastLpTime = (const unsigned long *)InMessage.Buffer.InMessage;

           try
           {
               if (lastLpTime != NULL && *lastLpTime != 0)
               {
                   if (CtiTime(*lastLpTime).isValid())
                   {
                       setLastLPTime(CtiTime(*lastLpTime));
                       if( getANSIProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
                       {
                           CTILOG_DEBUG(dout, "ResultDecode for "<< getName() <<" lastLPTime: "<<getLastLPTime());
                       }
                   }
               }
               else
               {
                   if( getANSIProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
                   {
                       CTILOG_DEBUG(dout, "ResultDecode for "<< getName() <<" lastLPTime: 0 ERROR");
                   }
               }
           }
           catch(...)
           {
               CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
           }

           resetScanFlag(ScanRateGeneral);
        }

    }
    if( retMsg != NULL )
    {
        retMsg->setExpectMore(false);
        retList.push_back(retMsg);
        retMsg = NULL;
    }

    CTILOG_INFO(dout, getName() <<" responded with data");

    return ClientErrors::None;}
YukonError_t CtiDeviceAnsi::sendCommResult( INMESS &InMessage)
{
    if (getANSIProtocol().getScanOperation() >= CtiProtocolANSI::demandReset ) //2= demand Reset, 3=loopback
    {
        string returnString = (getANSIProtocol().getScanOperation() == CtiProtocolANSI::demandReset ? "demand reset " : "loopback ");
        if( ! InMessage.ErrorCode )
        {
            returnString += "successful";
            int sizeOfReturnString = returnString.length();
            memcpy( InMessage.Buffer.InMessage, returnString.c_str(), sizeOfReturnString );
            InMessage.InLength = sizeOfReturnString;
        }
        else
        {
            returnString += "failed";
            int sizeOfReturnString = returnString.length();
            memcpy( InMessage.Buffer.InMessage, returnString.c_str(), sizeOfReturnString );
            InMessage.InLength = sizeOfReturnString;
        }
    }
    else //general Scan
    {

        if( ! InMessage.ErrorCode )
        {
            if (getANSIProtocol().getlastLoadProfileTime() != 0 || getANSIProtocol().getScanOperation() == CtiProtocolANSI::generalScan) //scanner
            {
                ULONG lptime = getANSIProtocol().getlastLoadProfileTime();
                memcpy( InMessage.Buffer.InMessage, (void *)&lptime, sizeof (unsigned long) );
                InMessage.InLength = sizeof (unsigned long);

                if( getANSIProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )
                {
                    CTILOG_DEBUG(dout, "Send Comm Result for "<< getName() <<" Last LP Time: "<< CtiTime(_lastLPTime));
                }
            }
            else
            {

                string returnString("general scan successful");
                int sizeOfReturnString = returnString.length();
                memcpy( InMessage.Buffer.InMessage, returnString.c_str(), sizeOfReturnString );
                InMessage.InLength = sizeOfReturnString;
            }
        }
        else
        {
            string returnString("general scan failed");
            int sizeOfReturnString = returnString.length();
            memcpy( InMessage.Buffer.InMessage, returnString.c_str(), sizeOfReturnString );
            InMessage.InLength = sizeOfReturnString;
        }

    }

    return InMessage.ErrorCode;
}
//=====================================================================================================================
//=====================================================================================================================

void CtiDeviceAnsi::processDispatchReturnMessage( list< CtiReturnMsg* > &retList, UINT archiveFlag )
{
    double value = 0;
    double timestamp = 0;
    bool gotValue = false;
    bool gotLPValues = false;
    int x =  OFFSET_TOTAL_KWH;
    string resultString = "";

    _result_string = "";

    if (getANSIProtocol().getScanOperation() >= CtiProtocolANSI::demandReset)
    {
        return;
    }

    try
    {
        CTILOG_INFO(dout, "Process Dispatch Message In Progress For "<< getName());

        while (x <= OFFSET_METER_TIME_STATUS)
        {
            if( const CtiPointAnalogSPtr pAnalogPoint = boost::dynamic_pointer_cast<CtiPointAnalog>(getDevicePointOffsetTypeEqual(x, AnalogPointType)) )
            {
                CTILOG_INFO(dout, getName() <<" Point Offset ==> "<< x);

                switch (x)
                {
                    case OFFSET_TOTAL_KWH:
                    case OFFSET_RATE_A_KWH:
                    case OFFSET_RATE_B_KWH:
                    case OFFSET_RATE_C_KWH:
                    case OFFSET_RATE_D_KWH:
                    case OFFSET_RATE_E_KWH:

                    case OFFSET_TOTAL_KVARH:
                    case OFFSET_RATE_A_KVARH:
                    case OFFSET_RATE_B_KVARH:
                    case OFFSET_RATE_C_KVARH:
                    case OFFSET_RATE_D_KVARH:
                    case OFFSET_RATE_E_KVARH:

                    case OFFSET_TOTAL_KVAH:
                    case OFFSET_RATE_A_KVAH:
                    case OFFSET_RATE_B_KVAH:
                    case OFFSET_RATE_C_KVAH:
                    case OFFSET_RATE_D_KVAH:
                    case OFFSET_RATE_E_KVAH:
                    {
                        gotValue = getANSIProtocol().retrieveSummation( x, &value, &timestamp, archiveFlag & CMD_FLAG_FROZEN );
                        break;
                    }
                    case OFFSET_PEAK_KW_OR_RATE_A_KW:
                    case OFFSET_RATE_B_KW:
                    case OFFSET_RATE_C_KW:
                    case OFFSET_RATE_D_KW:
                    case OFFSET_RATE_E_KW:

                    case OFFSET_PEAK_KVAR_OR_RATE_A_KVAR:
                    case OFFSET_RATE_B_KVAR:
                    case OFFSET_RATE_C_KVAR:
                    case OFFSET_RATE_D_KVAR:
                    case OFFSET_RATE_E_KVAR:

                    case OFFSET_PEAK_KVA_OR_RATE_A_KVA:
                    case OFFSET_RATE_B_KVA:
                    case OFFSET_RATE_C_KVA:
                    case OFFSET_RATE_D_KVA:
                    case OFFSET_RATE_E_KVA:
                    {
                        gotValue = getANSIProtocol().retrieveDemand( x, &value, &timestamp, archiveFlag & CMD_FLAG_FROZEN );

                        break;
                    }
                    case OFFSET_LOADPROFILE_KW:
                    case OFFSET_LOADPROFILE_KVAR:
                    case OFFSET_LOADPROFILE_QUADRANT1_KVAR:
                    case OFFSET_LOADPROFILE_QUADRANT2_KVAR:
                    case OFFSET_LOADPROFILE_QUADRANT3_KVAR:
                    case OFFSET_LOADPROFILE_QUADRANT4_KVAR:
                    case OFFSET_LOADPROFILE_KVA:
                    case OFFSET_LOADPROFILE_QUADRANT1_KVA:
                    case OFFSET_LOADPROFILE_QUADRANT2_KVA:
                    case OFFSET_LOADPROFILE_QUADRANT3_KVA:
                    case OFFSET_LOADPROFILE_QUADRANT4_KVA:
                    case OFFSET_LOADPROFILE_PHASE_A_VOLTAGE:
                    case OFFSET_LOADPROFILE_PHASE_B_VOLTAGE:
                    case OFFSET_LOADPROFILE_PHASE_C_VOLTAGE:
                    case OFFSET_LOADPROFILE_PHASE_A_CURRENT:
                    case OFFSET_LOADPROFILE_PHASE_B_CURRENT:
                    case OFFSET_LOADPROFILE_PHASE_C_CURRENT:
                    case OFFSET_LOADPROFILE_NEUTRAL_CURRENT:
                    {

                        gotLPValues = getANSIProtocol().retrieveLPDemand( x, 1);  // 1=table64 - kv2 only uses that lp table.
                        break;
                    }
                    case OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE:
                    case OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE:
                    case OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE:
                    case OFFSET_INSTANTANEOUS_PHASE_A_CURRENT:
                    case OFFSET_INSTANTANEOUS_PHASE_B_CURRENT:
                    case OFFSET_INSTANTANEOUS_PHASE_C_CURRENT:
                    case OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT:
                    case OFFSET_POWER_FACTOR:
                    {
                        gotValue = getANSIProtocol().retrievePresentValue(x, &value);
                        break;
                    }
                    case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW:
                    case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR:
                    case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA:
                    case OFFSET_QUADRANT1_LAST_INTERVAL_KVAR:
                    case OFFSET_QUADRANT2_LAST_INTERVAL_KVAR:
                    case OFFSET_QUADRANT3_LAST_INTERVAL_KVAR:
                    case OFFSET_QUADRANT4_LAST_INTERVAL_KVAR:
                    {
                        gotValue = getANSIProtocol().retrievePresentDemand(x, &value);
                        break;
                    }
                    case OFFSET_BATTERY_LIFE:
                    case OFFSET_DAYS_ON_BATTERY:
                    {
                        gotValue = getANSIProtocol().retrieveBatteryLife(x, &value);
                        if( getANSIProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
                        {
                            CTILOG_DEBUG(dout, getName() <<" Battery Life Value =  "<< value);
                        }
                        break;
                    }
                    default:
                    {
                        gotValue = false;
                        gotLPValues = false;
                    }
                }
                if (gotValue)
                {
                    if (isUnintializedTimeAndValue(value, timestamp))
                    {
                        timestamp = 0;
                    }
                    createPointData(*pAnalogPoint, value, timestamp, archiveFlag, retList);
                }
                else if (gotLPValues)
                {
                    createLoadProfilePointData(*pAnalogPoint, retList);
                }
            }
            //try pPoint as a StatusPoint
            else if( const CtiPointStatusSPtr pStatusPoint = boost::static_pointer_cast<CtiPointStatus>(getDevicePointOffsetTypeEqual(x, StatusPointType)) )
            {
                if( getANSIProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                {
                    CTILOG_DEBUG(dout, getName() <<" Point Offset ==> "<< x);
                }

                if (x == OFFSET_METER_TIME_STATUS)
                {
                    gotValue = getANSIProtocol().retrieveMeterTimeDiffStatus(x, &value);
                    if (gotValue)
                    {
                        std::auto_ptr<CtiPointDataMsg> pData(new CtiPointDataMsg);
                        pData->setId( pStatusPoint->getID() );

                        pData->setValue( value );
                        pData->setQuality( NormalQuality );
                        if (archiveFlag & CMD_FLAG_UPDATE)
                        {
                            pData->setTags(TAG_POINT_MUST_ARCHIVE);
                        }
                        pData->setTime( CtiTime() );
                        pData->setType( pStatusPoint->getType() );

                        std::auto_ptr<CtiReturnMsg> msgPtr(new CtiReturnMsg);
                        msgPtr->insert(pData.release());

                        retList.push_back(msgPtr.release());

                        resultString  = getName() + " / " + pStatusPoint->getName() + ": " + ResolveStateName(pStatusPoint->getStateGroupID(), value);
                    }
                }
            }
            if (resultString != "")
            {
                _result_string += resultString;
                _result_string += "\n";
            }
            resultString = "";
            value = 0;
            timestamp = 0;
            gotValue = false;
            gotLPValues = false;
            x++;
        }
        getANSIProtocol().setLastLoadProfileTime(_lastLPTime);
        if( getANSIProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
        {
            CTILOG_DEBUG(dout, getName() <<" Last LP Time "<< CtiTime(_lastLPTime));
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}
void CtiDeviceAnsi::createPointData(const CtiPointAnalog &analogPoint, double value, double timestamp, unsigned int archiveFlag, list< CtiReturnMsg* > &retList)
{
    value *= analogPoint.getMultiplier();
    value += analogPoint.getDataOffset();

    _result_string += getName() + " / " + analogPoint.getName() + ": " + CtiNumStr(value, analogPoint.getPointUnits().getDecimalPlaces()) + "\n";

    if( getANSIProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
    {
       CTILOG_DEBUG(dout, _result_string);
    }

    std::auto_ptr<CtiPointDataMsg> pData(
            new CtiPointDataMsg(
                    analogPoint.getID(),
                    value,
                    NormalQuality,
                    analogPoint.getType()));

    if (archiveFlag & CMD_FLAG_UPDATE)
    {
        pData->setTags(TAG_POINT_MUST_ARCHIVE);
    }
    if (timestamp != 0)
    {
        pData->setTime(CtiTime(timestamp));
    }
    else
    {
        pData->setTime( CtiTime() );
    }

    std::auto_ptr<CtiReturnMsg> msgPtr(new CtiReturnMsg);

    msgPtr->insert(pData.release());

    retList.push_back(msgPtr.release());

}
void CtiDeviceAnsi::createLoadProfilePointData(const CtiPointAnalog &analogPoint, list< CtiReturnMsg* > &retList)
{
    const double ptMultiplier = analogPoint.getMultiplier();
    const double ptOffset     = analogPoint.getDataOffset();
    CtiTime lastLoadProfileTime = CtiTime(getANSIProtocol().getlastLoadProfileTime());

    std::auto_ptr<CtiReturnMsg> msgPtr(new CtiReturnMsg);

    for (int y = getANSIProtocol().getTotalWantedLPBlockInts()-1; y >= 0; y--)
    {
        if (getANSIProtocol().getLPTime(y) > lastLoadProfileTime.seconds())
        {
            double lpValue = getANSIProtocol().getLPValue(y);
            lpValue *= ptMultiplier;
            lpValue += ptOffset;

            std::auto_ptr<CtiPointDataMsg> pData(
                    new CtiPointDataMsg(
                            analogPoint.getID(),
                            lpValue,
                            getANSIProtocol().getLPQuality(y),
                            analogPoint.getType()));

            pData->setTags( TAG_POINT_LOAD_PROFILE_DATA );
            pData->setTime( CtiTime(getANSIProtocol().getLPTime(y)) );

            msgPtr->insert( pData.release() );

            if( msgPtr->getCount() >= 400 || y <= 0 )
            {
                retList.push_back(msgPtr.release());

                if (y > 0)
                {
                    msgPtr.reset(new CtiReturnMsg);
                }
            }
        }
    }

    if( msgPtr.get() && msgPtr->getCount() > 0 )
    {
        retList.push_back(msgPtr.release());
    }

    _lastLPTime =
            getANSIProtocol().getLPTime(
                    getANSIProtocol().isDataBlockOrderDecreasing()
                        ? 0
                        : getANSIProtocol().getTotalWantedLPBlockInts() - 1);
}

unsigned long CtiDeviceAnsi::updateLastLpTime()
{
    return getANSIProtocol().getLPTime(getANSIProtocol().getTotalWantedLPBlockInts()-1);
}

bool isUnintializedTimeAndValue(double value, double timestamp)
{
    CtiTime t1 =  CtiTime(timestamp);
    CtiTime t2 =  CtiTime(CtiDate(1,1,2000));

    if ( t1.seconds() == t2.seconds() &&
        value == 0)
        return true;
    else
        return false;
}

}
}
