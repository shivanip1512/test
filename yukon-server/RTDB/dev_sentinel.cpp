#include "yukon.h"
/*-----------------------------------------------------------------------------*
*
* File:   dev_sentinel
*
* Date:   8/19/2004
*
* Author: Julie Richter
*
** Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
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

bool isUnintializedTimeAndValue(double value, double time);
//=========================================================================================================================================
//=========================================================================================================================================

CtiDeviceSentinel::CtiDeviceSentinel()
{
    //_lastLPTime = 0;
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
          retMsg->setExpectMore(1);
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
    for (aa = 0; aa < pswdTemp.length(); aa++)
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

    //inMsgResultString = string("successfully");
    inMsgResultString = string((const char*)InMessage->Buffer.InMessage, InMessage->InLength);

    if (getSentinelProtocol().getScanOperation() == 2) //demand Reset
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
                    if( getSentinelProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
                    {
                         CtiLockGuard<CtiLogger> doubt_guard(dout);
                         dout << CtiTime() << " ResultDecode for " << getName() <<" lastLPTime: "<<getLastLPTime()<< endl;
                     }
                }
                    }
                else
                {
                    if( getSentinelProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
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
        retMsg->setExpectMore(0);
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
    if (getSentinelProtocol().getScanOperation() == 2) //demand Reset
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
            if (getSentinelProtocol().getlastLoadProfileTime() != 0 || getSentinelProtocol().getScanOperation() == 0) //scanner
            {
                ULONG lptime = getSentinelProtocol().getlastLoadProfileTime();
                memcpy( InMessage->Buffer.InMessage, (void *)&lptime, sizeof (unsigned long) );
                InMessage->InLength = sizeof (unsigned long);

                if( getSentinelProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " sendCommResult for " << getName() <<" lastLPTime: "<<CtiTime(_lastLPTime)<< endl;
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
   //return( 0 ); //just a val
}


//=========================================================================================================================================
//=========================================================================================================================================

INT CtiDeviceSentinel::ErrorDecode( INMESS *InMessage, CtiTime &TimeNow, list < CtiMessage* >&vgList, list< CtiMessage* > &retList,
                               list< OUTMESS* > &outList, bool &overrideExpectMore)
{

   return( 1 ); //just a val
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
    for (aa = 0; aa < pswdTemp.length(); aa++)
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
                dout << CtiTime() << " ** INVALID LAST LP TIME ** Adjusting" << getName() <<"'s lastLPTime from: "<<getLastLPTime()<< endl;
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
        if( getSentinelProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getName() <<" lastLPTime "<<getLastLPTime()<< endl;
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
    //getSentinelProtocol().buildWantedTableList (aMsg);


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
    //ANSI_SCAN_OPERATION scanOperation = generalScan;

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
        //{ 31,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        //{ 32,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        //{ 33,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 52,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  -1,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ}

    };

    string pswdTemp;
    pswdTemp = getIED().getPassword();

    for (int aa = 0; aa < 20; aa++)
        password[aa] = 0;

    BYTE *temp;
    temp = (BYTE *)pswdTemp.c_str();
    for (aa = 0; aa < pswdTemp.length(); aa++)
        password[aa] = *(temp + aa);

    // currently defaulted at billing data only
    header.lastLoadProfileTime = 0;
    //_lastLPTime = header.lastLoadProfileTime;
   /* if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << "lastLPTime "<<getLastLPTime()<< endl;
    } */

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



    // keep the list on the scanner side for decode
    //getSentinelProtocol().buildWantedTableList (aMsg);
    return NORMAL;
}



//=========================================================================================================================================
//
//=========================================================================================================================================

CtiProtocolANSI_sentinel& CtiDeviceSentinel::getSentinelProtocol( void )
{
   return  _ansiProtocol;
}

//=====================================================================================================================
//=====================================================================================================================

void CtiDeviceSentinel::processDispatchReturnMessage( list< CtiReturnMsg* > &retList, UINT archiveFlag )
{

    CtiReturnMsg *msgPtr;
    CtiPointDataMsg                  *pData = NULL;
    CtiPointAnalogSPtr pPoint;
    CtiPointStatusSPtr pStatusPoint;
    double value = 0;
    double time = 0;
    double lpValue = 0;
    int    qual = 0;
    bool                             foundSomething = false;
    bool gotValue = false;
    bool gotLPValues = false;
    int x, y, z;
    CtiTime lastLoadProfileTime;
    string resultString = "";

    _result_string = "";

    {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << CtiTime() << " ----Process Dispatch Message In Progress For " << getName() << "----" << endl;
    }


    try
    {

        if (getSentinelProtocol().getScanOperation() == 2)
        {

            return;
        }
        else //general Scan
        {
            //if (useScanFlags())
            {
                //_lastLPTime = getSentinelProtocol().getlastLoadProfileTime();
                lastLoadProfileTime = CtiTime(getSentinelProtocol().getlastLoadProfileTime());
            }

            x =  OFFSET_TOTAL_KWH;
            while (x <= OFFSET_METER_TIME_STATUS)
            {
                pPoint = boost::static_pointer_cast<CtiPointAnalog>(getDevicePointOffsetTypeEqual(x, AnalogPointType));
                if (pPoint)
                {

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Point Offset ==> " <<x<< endl;
                    }
                    foundSomething = true;
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
                            if (archiveFlag & CMD_FLAG_FROZEN)
                            {
                                gotValue = getSentinelProtocol().retreiveFrozenSummation( x, &value, &time );
                            }
                            else
                            {
                                gotValue = getSentinelProtocol().retreiveSummation( x, &value );
                            }
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
                            if (archiveFlag & CMD_FLAG_FROZEN)
                            {
                                gotValue = getSentinelProtocol().retreiveFrozenDemand( x, &value, &time );
                            }
                            else
                            {
                                gotValue = getSentinelProtocol().retreiveDemand( x, &value, &time );
                            }
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
                        {

                            gotLPValues = getSentinelProtocol().retreiveLPDemand( x, 1);  // 1=table64 - kv2 only uses that lp table.
                            break;
                        }
                        case OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE:
                        case OFFSET_LOADPROFILE_PHASE_A_VOLTAGE:
                        case OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE:
                        case OFFSET_LOADPROFILE_PHASE_B_VOLTAGE:
                        case OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE:
                        case OFFSET_LOADPROFILE_PHASE_C_VOLTAGE:
                        case OFFSET_INSTANTANEOUS_PHASE_A_CURRENT:
                        case OFFSET_LOADPROFILE_PHASE_A_CURRENT:
                        case OFFSET_INSTANTANEOUS_PHASE_B_CURRENT:
                        case OFFSET_LOADPROFILE_PHASE_B_CURRENT:
                        case OFFSET_INSTANTANEOUS_PHASE_C_CURRENT:
                        case OFFSET_LOADPROFILE_PHASE_C_CURRENT:
                        case OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT:
                        case OFFSET_LOADPROFILE_NEUTRAL_CURRENT:
                        case OFFSET_POWER_FACTOR:
                        {
                            gotValue = getSentinelProtocol().retreivePresentValue(x, &value);
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
                            gotValue = getSentinelProtocol().retreivePresentDemand(x, &value);
                            break;
                        }
                        case OFFSET_BATTERY_LIFE:
                        case OFFSET_DAYS_ON_BATTERY:
                        {
                            gotValue = getSentinelProtocol().retreiveBatteryLife(x, &value);
                            if( getSentinelProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " battery life value =  "<< value<< endl;
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
                        if (isUnintializedTimeAndValue(value, time))
                        {
                            time = 0;
                        }

                        pData = CTIDBG_new CtiPointDataMsg();
                        pData->setId( pPoint->getID() );

                        if (pPoint->getMultiplier() != NULL)
                        {
                            value = value * pPoint->getMultiplier();
                            if (pPoint->getDataOffset() != NULL)
                            {
                                value += pPoint->getDataOffset();
                            }
                        }

                        resultString = getName() + " / " + pPoint->getName() + ": " + CtiNumStr(value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

                        pData->setValue( value );
                        qual = NormalQuality;
                        pData->setQuality( qual );
                        if (archiveFlag & CMD_FLAG_UPDATE)
                        {
                            pData->setTags(TAG_POINT_MUST_ARCHIVE);
                        }
                        if (time != 0)
                        {
                            pData->setTime(CtiTime(time));
                        }
                        else
                        {
                            pData->setTime( CtiTime() );
                        }
                        pData->setType( pPoint->getType() );

                        msgPtr = CTIDBG_new CtiReturnMsg();

                        msgPtr->insert(pData);
                        retList.push_back(msgPtr);
                        if( getSentinelProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " gotValue! "<< endl;
                        }

                        pData = NULL;
                        msgPtr = NULL;
                    }
                    else if (gotLPValues)
                    {
                        if( getSentinelProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " gotLPValues! "<< endl;
                        }

                        int ptMultiplier = pPoint->getMultiplier();
                        int ptOffset = pPoint->getDataOffset();

                        qual = NormalQuality;

                        int msgCntr = 0;
                        msgPtr = CTIDBG_new CtiReturnMsg();

                        for (y = getSentinelProtocol().getTotalWantedLPBlockInts()-1; y >= 0; y--)
                        {
                            if (getSentinelProtocol().getLPTime(y) > lastLoadProfileTime.seconds())
                            {
                                qual = getSentinelProtocol().getLPQuality(y);

                                lpValue = getSentinelProtocol().getLPValue(y);
                                if (ptMultiplier != NULL)
                                {
                                    lpValue = lpValue * ptMultiplier;
                                    if (ptOffset != NULL)
                                    {
                                        lpValue += ptOffset;
                                    }
                                }
                                pData = CTIDBG_new CtiPointDataMsg(pPoint->getID(), lpValue, qual, pPoint->getType());
                                pData->setTags( TAG_POINT_LOAD_PROFILE_DATA );
                                pData->setTime( CtiTime(getSentinelProtocol().getLPTime(y)) );

                                msgPtr->insert(pData);

                                if (msgCntr >= 400 || y <= 0 )
                                {
                                    msgCntr = 0;
                                    retList.push_back(msgPtr);
                                    msgPtr = NULL;
                                    if (y > 0)
                                        msgPtr = CTIDBG_new CtiReturnMsg();
                                }
                                else
                                    msgCntr++;

                                pData = NULL;

                            }
                            else
                            {
                                y = -1;
                                if (msgPtr->getCount() > 0)
                                {
                                    retList.push_back(msgPtr);
                                    msgPtr = NULL;
                                }
                            }


                        }
                        //setLastLPTime(CtiTime(getSentinelProtocol().getLPTime(getSentinelProtocol().getTotalWantedLPBlockInts()-1)));
                        _lastLPTime = getSentinelProtocol().getLPTime(getSentinelProtocol().getTotalWantedLPBlockInts()-1);
                        getSentinelProtocol().setLastLoadProfileTime(_lastLPTime);
                        if( getSentinelProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << "lastLPTime "<<CtiTime(getSentinelProtocol().getLPTime(getSentinelProtocol().getTotalWantedLPBlockInts()-1))<< endl;
                        }
                        if (pData != NULL)
                        {
                            delete []pData;
                            pData = NULL;
                        }  
                         
                        if (msgPtr != NULL)
                        {
                            //delete msgPtr;
                            msgPtr = NULL;
                        }
                    }
                    pPoint;
                }
                else //try pPoint as a StatusPoint
                {
                    pStatusPoint = boost::static_pointer_cast<CtiPointStatus>(getDevicePointOffsetTypeEqual(x, StatusPointType));
                    if (pStatusPoint)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Point Offset ==> " <<x<< endl;
                        }
                        foundSomething = true;
                        if (x == OFFSET_METER_TIME_STATUS)
                        {
                            gotValue = getSentinelProtocol().retreiveMeterTimeDiffStatus(x, &value);
                            if (gotValue)
                            {
                                pData = CTIDBG_new CtiPointDataMsg();
                                pData->setId( pStatusPoint->getID() );

                                pData->setValue( value );
                                qual = NormalQuality;
                                pData->setQuality( qual );
                                if (archiveFlag & CMD_FLAG_UPDATE)
                                {
                                    pData->setTags(TAG_POINT_MUST_ARCHIVE);
                                }
                                pData->setTime( CtiTime() );
                                pData->setType( pStatusPoint->getType() );

                                msgPtr = CTIDBG_new CtiReturnMsg();
                                msgPtr->insert(pData);

                                retList.push_back(msgPtr);

                                if( getSentinelProtocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " gotValue! "<< endl;
                                }
                                resultString  = getName() + " / " + pStatusPoint->getName() + ": " + ResolveStateName(pStatusPoint->getStateGroupID(), value);
                                pData = NULL;
                                msgPtr = NULL;
                            }
                        }
                        pStatusPoint.reset();
                    }
                }
                if (resultString != "")
                {
                    _result_string += resultString;
                    _result_string += "\n";
                }
                resultString = "";
                value = 0;
                time = 0;
                gotValue = false;
                gotLPValues = false;
                x++;
            }
        }

    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

bool isUnintializedTimeAndValue(double value, double time)
{
    CtiTime t1 =  CtiTime(time);
    CtiTime t2 =  CtiTime(CtiDate(1,1,2000));

    if ( t1.seconds() == t2.seconds() &&
        value == 0)
        return true;
    else
        return false;
}




