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

#include "porter.h"
#include "logger.h"
#include "dev_sentinel.h"
#include "utility.h"
#include "pt_analog.h"


//=========================================================================================================================================
//=========================================================================================================================================

CtiDeviceSentinel::CtiDeviceSentinel()
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

INT CtiDeviceSentinel::GeneralScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList,
                               RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority )
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
          buildScannerTableRequest (ptr);
      }
      else
      {
          buildCommanderTableRequest (ptr);
      }
      outList.insert( OutMessage );


      OutMessage = 0;
   }
   else
   {
      return MEMORY;
   }
   return NoError;
}

INT CtiDeviceSentinel::DemandReset( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList,
                               RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority )
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

    RWCString pswdTemp;
    pswdTemp = getIED().getPassword();
    pswdTemp.toUpper();

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << "pswdTemp "<<pswdTemp<< endl;
    }
    BYTE *temp;
    temp = (BYTE *)pswdTemp.data();
    struct CHexMap
    {
        char c;
        int val;
    };

    CHexMap HexMap[16] = {
        {'0', 0}, {'1', 1},
        {'2', 2}, {'3', 3},
        {'4', 4}, {'5', 5},
        {'6', 6}, {'7', 7},
        {'8', 8}, {'9', 9},
        {'A', 10}, {'B', 11},
        {'C', 12}, {'D', 13},
        {'E', 14}, {'F', 15}
    };


    int pwdTemp;
    int nibble;
    for (int aa = 0; aa < 20; aa++)
        password[aa] = 0;
    for (int a = 0; a < 10; a++)
    {
        nibble = 0;
        for (int nib = 0; nib < 2; nib++)
        {
            pwdTemp = 0;
            for (int i = 0; i < 16; i++)
            {
                if (*temp == HexMap[i].c)
                {
                    pwdTemp = HexMap[i].val;
                }
            }
            if (nib == 0)
            {
                pwdTemp = pwdTemp << 4;
            }

            password[a] |= pwdTemp;
            temp++;
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

    BYTE scanOperation = 1; //1 = demand reset

    // put the stuff in the buffer
    memcpy( aMsg, &header, sizeof (header));
    memcpy( (aMsg+sizeof(header)), &password, sizeof (password));
    memcpy ((aMsg+sizeof(header)+sizeof(password)),
            &table,
            (header.numTablesRequested*sizeof (ANSI_TABLE_WANTS)));
    memcpy ((aMsg+sizeof(header)+sizeof(password)+(header.numTablesRequested*sizeof (ANSI_TABLE_WANTS))),
            &scanOperation, sizeof(BYTE));

    outList.insert( OutMessage );
    OutMessage = 0;
   }
   else
   {
      return MEMORY;
   }

   {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << "outList.entries() = " <<outList.entries()<< endl;
   }
   return NoError;
}



INT CtiDeviceSentinel::ExecuteRequest( CtiRequestMsg         *pReq,
                                  CtiCommandParser           &parse,
                                  OUTMESS                   *&OutMessage,
                                  RWTPtrSlist< CtiMessage >  &vgList,
                                  RWTPtrSlist< CtiMessage >  &retList,
                                  RWTPtrSlist< OUTMESS >     &outList )
{
    int nRet = NoError;
    RWTPtrSlist< OUTMESS > tmpOutList;

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
                dout << RWTime( ) << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unsupported command on EMETCON route. Command = " << parse.getCommand( ) << endl;
            }
            nRet = NoMethod;

            break;
        }
    }

    if( nRet != NORMAL )
    {
        RWCString resultString;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime( ) << " Couldn't come up with an operation for device " << getName( ) << endl;
            dout << RWTime( ) << "   Command: " << pReq->CommandString( ) << endl;
        }

        resultString = "NoMethod or invalid command.";
        retList.insert( CTIDBG_new CtiReturnMsg(getID( ),
                                                RWCString(OutMessage->Request.CommandStr),
                                                resultString,
                                                nRet,
                                                OutMessage->Request.RouteID,
                                                OutMessage->Request.MacroOffset,
                                                OutMessage->Request.Attempt,
                                                OutMessage->Request.TrxID,
                                                OutMessage->Request.UserID,
                                                OutMessage->Request.SOE,
                                                RWOrdered( )) );
    }
    else
    {
        /*if(OutMessage != NULL)
        {
            tmpOutList.append( OutMessage );
            OutMessage = NULL;
        } */

        //executeOnDLCRoute(pReq, parse, OutMessage, tmpOutList, vgList, retList, outList, true);
    } 

    return nRet;
}


//=========================================================================================================================================
//=========================================================================================================================================

INT CtiDeviceSentinel::ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist < CtiMessage >&retList,
                                RWTPtrSlist< OUTMESS >    &outList)
{
    CtiReturnMsg *retMsg = NULL;
    if (getSentinelProtocol().getScanOperation() == 1) //demand Reset
    {
        if (InMessage->EventCode == NORMAL)
        {
            retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                            RWCString(InMessage->Return.CommandStr),
                                            //RWCString(),
                                            RWCString(getName() + " / demand reset successful"),
                                            InMessage->EventCode & 0x7fff,
                                            InMessage->Return.RouteID,
                                            InMessage->Return.MacroOffset,
                                            InMessage->Return.Attempt,
                                            InMessage->Return.TrxID,
                                            InMessage->Return.UserID);
        }
        else
        {
            retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                            RWCString(InMessage->Return.CommandStr),
                                            //RWCString(),
                                            RWCString(getName() + " / demand reset failed"),
                                            InMessage->EventCode & 0x7fff,
                                            InMessage->Return.RouteID,
                                            InMessage->Return.MacroOffset,
                                            InMessage->Return.Attempt,
                                            InMessage->Return.TrxID,
                                            InMessage->Return.UserID);
        }   
    }
    else
    {

        if (useScanFlags())
        {
            unsigned long *lastLpTime;
            lastLpTime =  (unsigned long *)InMessage->Buffer.InMessage;

            setUseScanFlags(TRUE);
            setLastLPTime(RWTime(*lastLpTime));
            if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << "ResultDecode    getLastLPTime "<<getLastLPTime()<< endl;
            }
            resetScanPending();

            setUseScanFlags(FALSE);

        }
        else
        {
            if (InMessage->EventCode == NORMAL)
            {
                retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                RWCString(InMessage->Return.CommandStr),
                                                //RWCString(),
                                                RWCString(getName() + " / general scan successful"),
                                                InMessage->EventCode & 0x7fff,
                                                InMessage->Return.RouteID,
                                                InMessage->Return.MacroOffset,
                                                InMessage->Return.Attempt,
                                                InMessage->Return.TrxID,
                                                InMessage->Return.UserID);
            }
            else
            {
                 retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                RWCString(InMessage->Return.CommandStr),
                                                //RWCString(),
                                                RWCString(getName() + " / general scan failed"),
                                                InMessage->EventCode & 0x7fff,
                                                InMessage->Return.RouteID,
                                                InMessage->Return.MacroOffset,
                                                InMessage->Return.Attempt,
                                                InMessage->Return.TrxID,
                                                InMessage->Return.UserID);
            }

        } 
    }
    if( retMsg != NULL )
    {
        retList.insert(retMsg);
        retMsg = NULL;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << RWTime::now() << " ==============================================" << endl;
        dout << RWTime::now() << " ==========The Sentinel responded with data=========" << endl;
        dout << RWTime::now() << " ==============================================" << endl;
    }
    return( 0 ); //just a val
}
INT CtiDeviceSentinel::sendCommResult( INMESS *InMessage)
{
    if (getSentinelProtocol().getScanOperation() == 1) //demand Reset
    {
        if (InMessage->EventCode == NORMAL)
        {                                
            RWCString returnString("demand reset successful");
            int sizeOfReturnString = returnString.length();
            memcpy( InMessage->Buffer.InMessage, returnString, sizeOfReturnString );
            InMessage->InLength = sizeOfReturnString;

            InMessage->EventCode = NORMAL;
        }
        else
        {
            RWCString returnString("demand reset failed");
            int sizeOfReturnString = returnString.length();
            memcpy( InMessage->Buffer.InMessage, returnString, sizeOfReturnString );
            InMessage->InLength = sizeOfReturnString;
        }  
    }
    else //general Scan
    {
    
        if (useScanFlags())
        {
            //setUseScanFlags(TRUE);
            unsigned long lastLpTime = getLastLPTime().seconds();

            memcpy( InMessage->Buffer.InMessage, (void *)&lastLpTime, sizeof (unsigned long) );
            InMessage->InLength = sizeof (unsigned long);
            InMessage->EventCode = NORMAL;


            if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << "sendCommResult    lastLPTime "<<getLastLPTime()<< endl;
            }
        }
      //  else
        {

        }
    }
   return( 0 ); //just a val
}


//=========================================================================================================================================
//=========================================================================================================================================

INT CtiDeviceSentinel::ErrorDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist < CtiMessage >&vgList, RWTPtrSlist< CtiMessage > &retList,
                               RWTPtrSlist< OUTMESS > &outList)
{

   return( 1 ); //just a val
}

/*************************************************************************************
* build the list of tables and header requested in the device as each ansi device may need a few
* different tables
*************************************************************************************
*/
int CtiDeviceSentinel::buildScannerTableRequest (BYTE *aMsg)
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
        { 31,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 32,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 33,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},

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

    RWCString pswdTemp;
    pswdTemp = getIED().getPassword();
    pswdTemp.toUpper();
    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << "pswdTemp "<<pswdTemp<< endl;
    }
    BYTE *temp;
    temp = (BYTE *)pswdTemp.data();
    struct CHexMap
    {
        char c;
        int val;
    };

    CHexMap HexMap[16] = {
        {'0', 0}, {'1', 1},
        {'2', 2}, {'3', 3},
        {'4', 4}, {'5', 5},
        {'6', 6}, {'7', 7},
        {'8', 8}, {'9', 9},
        {'A', 10}, {'B', 11},
        {'C', 12}, {'D', 13},
        {'E', 14}, {'F', 15}
    };


    int pwdTemp;
    int nibble;
    for (int aa = 0; aa < 20; aa++)
        password[aa] = 0;
    for (int a = 0; a < 10; a++)
    {
        nibble = 0;
        for (int nib = 0; nib < 2; nib++)
        {
            pwdTemp = 0;
            for (int i = 0; i < 16; i++)
            {
                if (*temp == HexMap[i].c)
                {
                    pwdTemp = HexMap[i].val;
                }
            }
            if (nib == 0)
            {
                pwdTemp = pwdTemp << 4;
            }

            password[a] |= pwdTemp;
            temp++;
        }
    }

    // currently defaulted at billing data only
    header.lastLoadProfileTime = getLastLPTime().seconds();
    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << "lastLPTime "<<getLastLPTime()<< endl;
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
    

    // keep the list on the scanner side for decode
    getSentinelProtocol().buildWantedTableList (aMsg);
    return NORMAL;
}

/*************************************************************************************
* build the list of tables and header requested in the device as each ansi device may need a few
* different tables
*************************************************************************************
*/
int CtiDeviceSentinel::buildCommanderTableRequest (BYTE *aMsg)
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
        { 27,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 28,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 31,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 32,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 33,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 52,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  -1,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ}
        
    };

    RWCString pswdTemp;
    pswdTemp = getIED().getPassword();
    pswdTemp.toUpper();
    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << "pswdTemp "<<pswdTemp<< endl;
    }
    BYTE *temp;
    temp = (BYTE *)pswdTemp.data();
    struct CHexMap
    {
        char c;
        int val;
    };

    CHexMap HexMap[16] = {
        {'0', 0}, {'1', 1},
        {'2', 2}, {'3', 3},
        {'4', 4}, {'5', 5},
        {'6', 6}, {'7', 7},
        {'8', 8}, {'9', 9},
        {'A', 10}, {'B', 11},
        {'C', 12}, {'D', 13},
        {'E', 14}, {'F', 15}
    };


    int pwdTemp;
    int nibble;
    for (int aa = 0; aa < 20; aa++)
        password[aa] = 0;
    for (int a = 0; a < 10; a++)
    {       
        nibble = 0;
        for (int nib = 0; nib < 2; nib++)
        {   
            pwdTemp = 0;
            for (int i = 0; i < 16; i++)
            {
                if (*temp == HexMap[i].c)
                {
                    pwdTemp = HexMap[i].val;
                }
            }
            if (nib == 0)
            {
                pwdTemp = pwdTemp << 4;
            }

            password[a] |= pwdTemp;
            temp++;
        }
    }

    // currently defaulted at billing data only
    header.lastLoadProfileTime = getLastLPTime().seconds();
    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << "lastLPTime "<<getLastLPTime()<< endl;
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
    BYTE tempBuffer[1000];


    // put the stuff in the buffer
    memcpy( aMsg, &header, sizeof (header));

    memcpy( (aMsg+sizeof(header)), &password, sizeof (password));
    memcpy ((aMsg+sizeof(header)+sizeof(password)),
            &table,
            (header.numTablesRequested*sizeof (ANSI_TABLE_WANTS)));

    memcpy ((aMsg+sizeof(header)+sizeof(password)+(header.numTablesRequested*sizeof (ANSI_TABLE_WANTS))),
            &scanOperation, sizeof(BYTE));
    


    // keep the list on the scanner side for decode
    getSentinelProtocol().buildWantedTableList (aMsg);
    return NORMAL;
}



//=========================================================================================================================================
//
//=========================================================================================================================================

CtiProtocolANSI & CtiDeviceSentinel::getSentinelProtocol( void )
{
   return  _ansiProtocol;
}

//=====================================================================================================================
//=====================================================================================================================

void CtiDeviceSentinel::processDispatchReturnMessage( CtiReturnMsg *msgPtr )
{

    CtiMultiMsg                      *msgMulti = CTIDBG_new CtiMultiMsg;
    CtiPointDataMsg                  *pData = NULL;
    //CtiPointBase                     *pPoint = NULL;
    CtiPointAnalog *pPoint = NULL;
    double value = 0;
    double lpValue = 0;
    int    qual = 0;
    bool                             foundSomething = false;
    bool gotValue = false;
    bool gotLPValues = false;
    int x, y, z;
    RWTime lastLoadProfileTime;

    
    {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ----Process Dispatch Message In Progress For " << getName() << "----" << endl;
    }
    if (getSentinelProtocol().getScanOperation() == 1)
    {

        return;
    }
    else //general Scan
    {

        setUseScanFlags(TRUE);
        lastLoadProfileTime = getLastLPTime();
        setUseScanFlags(FALSE);

        x =  OFFSET_TOTAL_KWH;
        while (x <= OFFSET_DAYS_ON_BATTERY)  
        {
            pPoint = (CtiPointAnalog*)getDevicePointOffsetTypeEqual(x, AnalogPointType);
            if (pPoint != NULL) 
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Point Offset ==> " <<x<< endl;
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
                    gotValue = getSentinelProtocol().retreiveSummation( x, &value );
                    break;
                }
                case OFFSET_PEAK_KW_OR_RATE_A_KW:
                case OFFSET_RATE_B_KW:
                case OFFSET_RATE_C_KW:
                case OFFSET_RATE_D_KW:
                case OFFSET_RATE_E_KW:
                case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW:

                case OFFSET_PEAK_KVAR_OR_RATE_A_KVAR:
                case OFFSET_RATE_B_KVAR:
                case OFFSET_RATE_C_KVAR:
                case OFFSET_RATE_D_KVAR:
                case OFFSET_RATE_E_KVAR:
                case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR:

                case OFFSET_PEAK_KVA_OR_RATE_A_KVA:
                case OFFSET_RATE_B_KVA:
                case OFFSET_RATE_C_KVA:
                case OFFSET_RATE_D_KVA:
                case OFFSET_RATE_E_KVA:
                case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA:
                {
                    gotValue = getSentinelProtocol().retreiveDemand( x, &value );
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
                {
                    gotValue = getSentinelProtocol().retreivePresentValue(x, &value);
                    break;
                }
                case OFFSET_BATTERY_LIFE:
                case OFFSET_DAYS_ON_BATTERY:
                {
                    gotValue = getSentinelProtocol().retreiveBatteryLife(x, &value);
                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " battery life value =  "<< value<< endl;
                    }
                    break;
                }
                default:
                {  
                    gotValue = false;
                    gotLPValues = false;
                }
                if (gotValue) 
                {
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


                    pData->setValue( value );
                    qual = NormalQuality;
                    pData->setQuality( qual );
                    //pData->setTags( TAG_POINT_LOAD_PROFILE_DATA );
                    pData->setTime( RWTime() );
                    pData->setType( pPoint->getType() );

                    msgPtr->insert(pData); 
                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " gotValue! "<< endl;
                    }

                    pData = NULL;
                }
                else if (gotLPValues) 
                {
                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " gotLPValues! "<< endl;
                    }

                    int ptMultiplier = pPoint->getMultiplier();
                    int ptOffset = pPoint->getDataOffset();

                    qual = NormalQuality;
                    // first time send to scanner...  ////////////////////
                    lpValue = getSentinelProtocol().getLPValue(getSentinelProtocol().getTotalWantedLPBlockInts()-1);
                    if (ptMultiplier != NULL)
                    {
                        lpValue = lpValue * ptMultiplier;
                        if (ptOffset != NULL)
                        {
                            lpValue += ptOffset;
                        }
                    }
                    pData = new CtiPointDataMsg(pPoint->getID(), lpValue, qual, pPoint->getType());
                    pData->setTime( RWTime(getSentinelProtocol().getLPTime(getSentinelProtocol().getTotalWantedLPBlockInts()-1)) );
                    msgPtr->insert(pData); 
                    pData = NULL;
                    //////////////////////////////////////////////////////

                    for (y = getSentinelProtocol().getTotalWantedLPBlockInts()-1; y >= 0; y--) 
                    {
                        if (getSentinelProtocol().getLPTime(y) > lastLoadProfileTime.seconds())
                        {
                            qual = NormalQuality;

                            lpValue = getSentinelProtocol().getLPValue(y);
                            if (ptMultiplier != NULL)
                            {
                                lpValue = lpValue * ptMultiplier;
                                if (ptOffset != NULL)
                                {
                                    lpValue += ptOffset;
                                }
                            }
                            pData = new CtiPointDataMsg(pPoint->getID(), lpValue, qual, pPoint->getType());
                            pData->setTags( TAG_POINT_LOAD_PROFILE_DATA );
                            pData->setTime( RWTime(getSentinelProtocol().getLPTime(y)) );
                            msgPtr->insert(pData); 
                            pData = NULL;
                        }
                        else
                        {
                            y = -1;
                        }
                        

                    }
                    //setUseScanFlags(FALSE);
                    setUseScanFlags(TRUE);
                    setLastLPTime(RWTime(getSentinelProtocol().getLPTime(getSentinelProtocol().getTotalWantedLPBlockInts()-1)));
                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << "lastLPTime "<<RWTime(getSentinelProtocol().getlastLoadProfileTime())<< endl;
                    }
                } 
                if (pData != NULL) 
                {
                    delete []pData;
                    pData = NULL;
                } 
            }
            pPoint = NULL;
            gotValue = false;
            gotLPValues = false;
            x++;
        }
    }
    if( msgMulti != NULL )
    {
       delete msgMulti;
       msgMulti = NULL;
    }
}



