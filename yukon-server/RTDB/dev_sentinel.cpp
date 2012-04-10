#include "precompiled.h"
#include "porter.h"
#include "logger.h"
#include "dev_sentinel.h"
#include "utility.h"
#include "pt_analog.h"
#include "pt_status.h"
#include "cmdparse.h"
#include "numstr.h"
#include "ctidate.h"
#include "ctitime.h"


using namespace Cti::Protocols::Ansi;
using std::string;
using std::endl;
using std::list;

namespace Cti {
namespace Devices {
//=========================================================================================================================================
//=========================================================================================================================================

CtiDeviceSentinel::CtiDeviceSentinel() :
    _lastLPTime(0)
{
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiDeviceSentinel::~CtiDeviceSentinel()
{
}

//=========================================================================================================================================
//scanner has decided that it's time to talk to an ansi-talking device and has called up on Us to carry out this mission - let us be brave
//
//we get handed a bunch of junk we don't care about, build a header about the command (GeneralScan) and , then pop down
//to the ansi protocol object to get info about the tables we know we need for a GeneralScan
//=========================================================================================================================================

INT CtiDeviceSentinel::GeneralScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList,
                               list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority )
{

   ULONG BytesWritten;
   int   adjustment = 0;

   if( OutMessage != NULL )
   {
      setCurrentCommand( CmdScanData );

      // Load all the other stuff that is needed
      OutMessage->DeviceID  = getID();
      OutMessage->TargetID  = getID();
      OutMessage->Port      = getPortID();
      OutMessage->Remote    = getAddress();
      OutMessage->TimeOut   = 2;
      OutMessage->EventCode = RESULT | ENCODED;
      OutMessage->Sequence  = 0;
      OutMessage->Retry     = 3;
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
                                          NORMAL,//EventCode & 0x7fff
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
      return MEMORY;
   }
   return NoError;
}

INT CtiDeviceSentinel::DemandReset( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList,
                               list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority )
{

   if( OutMessage != NULL )
   {
      setCurrentCommand( CmdScanData );

      // Load all the other stuff that is needed
      OutMessage->DeviceID  = getID();
      OutMessage->TargetID  = getID();
      OutMessage->Port      = getPortID();
      OutMessage->Remote    = getAddress();
      OutMessage->TimeOut   = 2;
      OutMessage->EventCode = RESULT | ENCODED;
      OutMessage->Sequence  = 0;
      OutMessage->Retry     = 3;
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

    BYTE *temp;
    temp = (BYTE *)pswdTemp.c_str();
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
      return MEMORY;
   }

   {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << "outList.size() = " <<outList.size()<< endl;
   }
   return NoError;
}



INT CtiDeviceSentinel::ExecuteRequest( CtiRequestMsg         *pReq,
                                  CtiCommandParser           &parse,
                                  OUTMESS                   *&OutMessage,
                                  list< CtiMessage* >  &vgList,
                                  list< CtiMessage* >  &retList,
                                  list< OUTMESS* >     &outList )
{
    int nRet = NoError;
    list< OUTMESS* > tmpOutList;

    //_parseFlags = parse.getFlags();

    switch( parse.getCommand( ) )
    {


        /*case LoopbackRequest:
        {
            nRet = executeLoopback( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        */
        case ScanRequest:
        {
            //nRet = GeneralScan( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            nRet = GeneralScan( pReq, parse, OutMessage, vgList, retList, outList );
            //nRet = executeScan( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        /*case GetValueRequest:
        {
            nRet = executeGetValue( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        } */
        case PutValueRequest:
        {
            //nRet = DemandReset( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            nRet = DemandReset( pReq, parse, OutMessage, vgList, retList, outList );
           // nRet = executePutValue( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
       /* case ControlRequest:
        {
            nRet = executeControl( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        case GetStatusRequest:
        {
            nRet = executeGetStatus( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        case PutStatusRequest:
        {
            nRet = executePutStatus( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        case GetConfigRequest:
        {
            nRet = executeGetConfig( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        case PutConfigRequest:
        {
            nRet = executePutConfig( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        */
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime( ) << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unsupported command on EMETCON route. Command = " << parse.getCommand( ) << endl;
            }
            nRet = NoMethod;

            break;
        }
    }

    if( nRet != NORMAL )
    {
        string resultString;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime( ) << " Couldn't come up with an operation for device " << getName( ) << endl;
            dout << CtiTime( ) << "   Command: " << pReq->CommandString( ) << endl;
        }

        resultString = "NoMethod or invalid command.";
        retList.push_back( CTIDBG_new CtiReturnMsg(getID( ),
                                                string(OutMessage->Request.CommandStr),
                                                resultString,
                                                nRet,
                                                OutMessage->Request.RouteID,
                                                OutMessage->Request.MacroOffset,
                                                OutMessage->Request.Attempt,
                                                OutMessage->Request.GrpMsgID,
                                                OutMessage->Request.UserID,
                                                OutMessage->Request.SOE,
                                                CtiMultiMsg_vec( )) );
    }
    else
    {
        /*if(OutMessage != NULL)
        {
            tmpoutList.push_back( OutMessage );
            OutMessage = NULL;
        } */

        //executeOnDLCRoute(pReq, parse, OutMessage, tmpOutList, vgList, retList, outList, true);
    }

    return nRet;
}


//=========================================================================================================================================
//=========================================================================================================================================

INT CtiDeviceSentinel::ResultDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list < CtiMessage* >&retList,
                                list< OUTMESS* >    &outList)
{
    CtiReturnMsg *retMsg = NULL;
    string inMsgResultString = "";

    inMsgResultString = string((const char*)InMessage->Buffer.InMessage, InMessage->InLength);

    if (getANSIProtocol().getScanOperation() == CtiProtocolANSI::demandReset) //demand Reset
    {
        //if (InMessage->EventCode == NORMAL)
        if (findStringIgnoreCase(inMsgResultString, "successful"))
        {
            retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                            InMessage->Return.CommandStr,
                                            //string(),
                                            getName() + " / demand reset successful",
                                            InMessage->EventCode & 0x7fff,
                                            InMessage->Return.RouteID,
                                            InMessage->Return.MacroOffset,
                                            InMessage->Return.Attempt,
                                            InMessage->Return.GrpMsgID,
                                            InMessage->Return.UserID);
        }
        else
        {
            retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                            InMessage->Return.CommandStr,
                                            //string(),
                                            getName() + " / demand reset failed",
                                            InMessage->EventCode & 0x7fff,
                                            InMessage->Return.RouteID,
                                            InMessage->Return.MacroOffset,
                                            InMessage->Return.Attempt,
                                            InMessage->Return.GrpMsgID,
                                            InMessage->Return.UserID);
        }
    }
    else
    {
        if (findStringIgnoreCase(inMsgResultString, "general") )
        {
            if (findStringIgnoreCase(inMsgResultString, "successful") )
            {
                retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                InMessage->Return.CommandStr,
                                                //string(),
                                                getName() + " / general scan successful : \n" + _result_string.c_str(),
                                                InMessage->EventCode & 0x7fff,
                                                InMessage->Return.RouteID,
                                                InMessage->Return.MacroOffset,
                                                InMessage->Return.Attempt,
                                                InMessage->Return.GrpMsgID,
                                                InMessage->Return.UserID);
            }
            else
            {
                 retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                InMessage->Return.CommandStr,
                                                //string(),
                                                getName() + " / general scan failed",
                                                InMessage->EventCode & 0x7fff,
                                                InMessage->Return.RouteID,
                                                InMessage->Return.MacroOffset,
                                                InMessage->Return.Attempt,
                                                InMessage->Return.GrpMsgID,
                                                InMessage->Return.UserID);
            }



        }
        else if (useScanFlags())
        {
            //if (InMessage->EventCode == NORMAL)
            //if (findStringIgnoreCase(inMsgResultString, "successful"))
            {
                unsigned long *lastLpTime;
                lastLpTime =  (unsigned long *)InMessage->Buffer.InMessage;

                try
                {

                if (lastLpTime != NULL && *lastLpTime != 0)
                {
                        if (CtiTime(*lastLpTime).isValid())
                        {
                    setLastLPTime(CtiTime(*lastLpTime));
                    if( getANSIProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
                    {
                         CtiLockGuard<CtiLogger> doubt_guard(dout);
                         dout << CtiTime() << " ResultDecode for " << getName() <<" lastLPTime: "<<getLastLPTime()<< endl;
                     }
                }
                    }
                else
                {
                    if( getANSIProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " ResultDecode for " << getName() <<" lastLPTime: 0 ERROR"<< endl;
                    }
                }
            }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
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
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << CtiTime::now() << " ==============================================" << endl;
        dout << CtiTime::now() << " ==========The " << getName() << " responded with data=========" << endl;
        dout << CtiTime::now() << " ==============================================" << endl;
    }


    return( 0 ); //just a val
}
INT CtiDeviceSentinel::sendCommResult( INMESS *InMessage)
{
    if (getANSIProtocol().getScanOperation() == CtiProtocolANSI::demandReset) //demand Reset
    {
        if (InMessage->EventCode == NORMAL)
        {
            string returnString("demand reset successful");
            int sizeOfReturnString = returnString.length();
            memcpy( InMessage->Buffer.InMessage, returnString.c_str(), sizeOfReturnString );
            InMessage->InLength = sizeOfReturnString;

            InMessage->EventCode = NORMAL;
        }
        else
        {
            string returnString("demand reset failed");
            int sizeOfReturnString = returnString.length();
            memcpy( InMessage->Buffer.InMessage, returnString.c_str(), sizeOfReturnString );
            InMessage->InLength = sizeOfReturnString;
        }
    }
    else //general Scan
    {

        //if (useScanFlags())

        if (InMessage->EventCode == NORMAL)
        {
            if (getANSIProtocol().getlastLoadProfileTime() != 0 || getANSIProtocol().getScanOperation() == CtiProtocolANSI::generalScan) //scanner
            {
                ULONG lptime = getANSIProtocol().getlastLoadProfileTime();
                memcpy( InMessage->Buffer.InMessage, (void *)&lptime, sizeof (unsigned long) );
                InMessage->InLength = sizeof (unsigned long);

                if( getANSIProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Send Comm Result for " << getName() <<" Last LP Time: "<<CtiTime(_lastLPTime)<< endl;
                }
            }
            else
            {

                string returnString("general scan successful");
                int sizeOfReturnString = returnString.length();
                memcpy( InMessage->Buffer.InMessage, returnString.c_str(), sizeOfReturnString );
                InMessage->InLength = sizeOfReturnString;

                InMessage->EventCode = NORMAL;
            }
        }
        else
        {
            string returnString("general scan failed");
            int sizeOfReturnString = returnString.length();
            memcpy( InMessage->Buffer.InMessage, returnString.c_str(), sizeOfReturnString );
            InMessage->InLength = sizeOfReturnString;
        }

    }

    return( InMessage->EventCode ); //just a val
}


/*************************************************************************************
* build the list of tables and header requested in the device as each ansi device may need a few
* different tables
*************************************************************************************
*/
int CtiDeviceSentinel::buildScannerTableRequest (BYTE *aMsg, UINT flags)
{
    WANTS_HEADER   header;

    //here is the password for the sentinel (should be changed to a cparm, I think)
    BYTE        password[] = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    // here are the tables requested for the sentinel
    ANSI_TABLE_WANTS    table[100] = {
        {  0,     0,      30,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  1,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
//        {  7,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_WRITE},
//        {  8,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 11,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 12,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 13,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 15,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 16,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 21,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 22,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 23,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 27,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 28,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
       // { 31,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
       // { 32,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
       // { 33,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},

    //    { 51,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 52,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 61,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 62,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 63,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 64,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
       // {  0,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ},
       // { 70,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ},
        {  -1,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ}

    };

    string pswdTemp;
    pswdTemp = getIED().getPassword();

    for (int aa = 0; aa < 20; aa++)
        password[aa] = 0;

    BYTE *temp;
    temp = (BYTE *)pswdTemp.c_str();
    for (int aa = 0; aa < pswdTemp.length(); aa++)
        password[aa] = *(temp + aa);

    // currently defaulted at billing data only
    if (useScanFlags())
    {
        header.lastLoadProfileTime = getLastLPTime().seconds();
        if (  getLastLPTime().seconds() > CtiTime().seconds() + (3600 * 12)  || // 12 hours ahead
              getLastLPTime().seconds() < CtiTime().seconds() - (86400 * 90)  ) // 3 months old
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " ** INVALID LAST LP TIME ** Adjusting" << getName() <<"'s Last LP Time from: "<<getLastLPTime()<< endl;
            }
            setLastLPTime( CtiTime(CtiTime().seconds() - (86400 * 30)) );
        }
    }
    else
    {
        header.lastLoadProfileTime = 0;
    }


    if (useScanFlags())
    {
        if( getANSIProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getName() <<" Last LP Time "<<getLastLPTime()<< endl;
        }
    }

    // lazyness so I don't have to continually remember to update this
    header.numTablesRequested = 0;
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

    BYTE scanOperation = 0; //0 = general scan

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


    // keep the list on the scanner side for decode
    //getANSIProtocol().buildWantedTableList (aMsg);


    return NORMAL;
}

/*************************************************************************************
* build the list of tables and header requested in the device as each ansi device may need a few
* different tables
*************************************************************************************
*/
int CtiDeviceSentinel::buildCommanderTableRequest (BYTE *aMsg, UINT flags)
{
    WANTS_HEADER   header;
    //AnsiScanOperation scanOperation = generalScan;

    //here is the password for the sentinel (should be changed to a cparm, I think)
    BYTE        password[] = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    // here are the tables requested for the sentinel
    ANSI_TABLE_WANTS    table[100] = {
        {  0,     0,      30,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  1,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 11,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 12,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 13,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 15,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 16,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 21,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 22,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 23,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 25,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 27,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 28,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 52,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  -1,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ}

    };

    string pswdTemp;
    pswdTemp = getIED().getPassword();

    for (int aa = 0; aa < 20; aa++)
        password[aa] = 0;

    BYTE *temp;
    temp = (BYTE *)pswdTemp.c_str();
    for (int aa = 0; aa < pswdTemp.length(); aa++)
        password[aa] = *(temp + aa);

    // currently defaulted at billing data only
    header.lastLoadProfileTime = 0;
    header.numTablesRequested = 0;
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

    BYTE scanOperation = 1; //1 = general pil scan


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

    return NORMAL;
}



//=========================================================================================================================================
//
//=========================================================================================================================================

CtiProtocolANSI& CtiDeviceSentinel::getANSIProtocol( void )
{
   return  _ansiProtocol;
}

//=====================================================================================================================
//=====================================================================================================================


}
}



