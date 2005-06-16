#include "yukon.h"

/*-----------------------------------------------------------------------------*
*
* File:   dev_kv2
*
* Date:   6/13/2002
*
* Author: Eric Schmit
*
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_kv2.cpp-arc  $
*    REVISION     :  $Revision: 1.14 $
*    DATE         :  $Date: 2005/06/16 19:17:59 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Generic Interface used to download a file using ftp
*
*    DESCRIPTION:
*
*    History:
      $Log: dev_kv2.cpp,v $
      Revision 1.14  2005/06/16 19:17:59  jrichter
      Sync ANSI code with 3.1 branch!

      Revision 1.13  2005/05/12 18:33:12  jrichter
      changed so it doesn't send additional last lp value to scanner.  Matt advised to do this and says now all lp values are sent to clients.

      Revision 1.12  2005/03/14 21:44:16  jrichter
      updated with present value regs, batterylife info, corrected quals, multipliers/offsets, corrected single precision float define, modifed for commander commands, added demand reset

      Revision 1.11  2005/03/10 20:49:47  mfisher
      changed getProtocol to getKV2Protocol so it wouldn't interfere with the new dev_single getProtocol

      Revision 1.10  2005/02/10 23:24:00  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.9  2005/01/25 18:33:51  jrichter
      added present value tables for kv2 and sentinel for voltage, current, freq, pf, etc..meter info

      Revision 1.8  2005/01/03 23:07:15  jrichter
      checking into 3.1, for use at columbia to test sentinel

      Revision 1.7  2004/12/10 21:58:43  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.6  2004/09/30 21:37:21  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.5  2003/04/25 15:14:07  dsutton
      Changed general scan and decode result


* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "porter.h"
#include "logger.h"
#include "dev_kv2.h"
#include "utility.h"
#include "pt_analog.h"
#include "pt_status.h"
#include "cmdparse.h"
#include "numstr.h"

//=========================================================================================================================================

CtiDeviceKV2::CtiDeviceKV2()
{
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiDeviceKV2::~CtiDeviceKV2()
{
}

//=========================================================================================================================================
//scanner has decided that it's time to talk to an ansi-talking device and has called up on Us to carry out this mission - let us be brave
//
//we get handed a bunch of junk we don't care about, build a header about the command (GeneralScan) and , then pop down
//to the ansi protocol object to get info about the tables we know we need for a GeneralScan
//=========================================================================================================================================

INT CtiDeviceKV2::GeneralScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList,
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

INT CtiDeviceKV2::DemandReset( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList,
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

    _parseFlags = parse.getFlags();

    // put the stuff in the buffer
    memcpy( aMsg, &header, sizeof (header));
    memcpy( (aMsg+sizeof(header)), &password, sizeof (password));
    memcpy ((aMsg+sizeof(header)+sizeof(password)),
            &table,
            (header.numTablesRequested*sizeof (ANSI_TABLE_WANTS)));
    memcpy ((aMsg+sizeof(header)+sizeof(password)+(header.numTablesRequested*sizeof (ANSI_TABLE_WANTS))),
            &scanOperation, sizeof(BYTE));
    memcpy ((aMsg+sizeof(header)+sizeof(password)+(header.numTablesRequested*sizeof (ANSI_TABLE_WANTS)) +sizeof(BYTE)),
            &_parseFlags, sizeof(UINT));


      outList.insert( OutMessage );

      OutMessage = 0;
   }
   else
   {
      return MEMORY;
   }

   return NoError;
}



INT CtiDeviceKV2::ExecuteRequest( CtiRequestMsg         *pReq,
                                  CtiCommandParser           &parse,
                                  OUTMESS                   *&OutMessage,
                                  RWTPtrSlist< CtiMessage >  &vgList,
                                  RWTPtrSlist< CtiMessage >  &retList,
                                  RWTPtrSlist< OUTMESS >     &outList )
{
    int nRet = NoError;
    RWTPtrSlist< OUTMESS > tmpOutList;

    _parseFlags = parse.getFlags();
    
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

INT CtiDeviceKV2::ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist < CtiMessage >&retList,
                                RWTPtrSlist< OUTMESS >    &outList)
{
    CtiReturnMsg *retMsg = NULL;
    if (getKV2Protocol().getScanOperation() == 1) //demand Reset
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
            if (InMessage->EventCode == NORMAL)
            {
                unsigned long *lastLpTime;
                lastLpTime =  (unsigned long *)InMessage->Buffer.InMessage;

                if (lastLpTime != NULL && *lastLpTime != 0)
                {  
                    setLastLPTime(RWTime(*lastLpTime));
                }
                if( getKV2Protocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " ResultDecode for " << getName() <<" lastLPTime: "<<getLastLPTime()<< endl;
                }
            }
            else
            {
                if( getKV2Protocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " ResultDecode for " << getName() <<" **SCAN FAILED**  lastLPTime: "<<getLastLPTime()<< endl;
                }
            }
            resetScanPending();
        }
        else
        {
            if (InMessage->EventCode == NORMAL)
            {
                retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                RWCString(InMessage->Return.CommandStr),
                                                //RWCString(),
                                                 RWCString(getName() + " / general scan successful : \n" + _result_string.data()),
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
        retMsg->setExpectMore(0);
        retList.insert(retMsg);
        retMsg = NULL;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << RWTime::now() << " ==============================================" << endl;
        dout << RWTime::now() << " ==========The " << getName() << " responded with data=========" << endl;
        dout << RWTime::now() << " ==============================================" << endl;
    }   
    return( 0 ); //just a val
}

//=========================================================================================================================================
//=========================================================================================================================================

INT CtiDeviceKV2::sendCommResult( INMESS *InMessage)
{
    if (getKV2Protocol().getScanOperation() == 1) //demand Reset
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
            unsigned long lastLpTime = getLastLPTime().seconds();

            memcpy( InMessage->Buffer.InMessage, (void *)&lastLpTime, sizeof (unsigned long) );
            InMessage->InLength = sizeof (unsigned long);
            //InMessage->EventCode = NORMAL;


            if( getKV2Protocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )//DEBUGLEVEL_LUDICROUS )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " sendCommResult for" << getName() <<" lastLPTime: "<<getLastLPTime()<< endl;
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

INT CtiDeviceKV2::ErrorDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist < CtiMessage >&vgList, RWTPtrSlist< CtiMessage > &retList,
                               RWTPtrSlist< OUTMESS > &outList)
{

   return( 1 ); //just a val
}

/*************************************************************************************
* build the list of tables and header requested in the device as each ansi device may need a few
* different tables
*************************************************************************************
*/
int CtiDeviceKV2::buildScannerTableRequest (BYTE *aMsg)
{
    WANTS_HEADER   header;

    BYTE        password[] = { 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0xaa, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };

    // here are the tables requested for the kv2
    ANSI_TABLE_WANTS    table[100] = {
        {  0,     0,      30,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  1,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  3,     0,      0,      ANSI_TABLE_TYPE_STANDARD,     ANSI_OPERATION_READ},
        { 11,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 12,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 13,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 14,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 15,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 16,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 21,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 22,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 23,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 61,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 62,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 63,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 64,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  0,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ},
        {  1,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
       // { 70,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ},
        {  -1,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ}

//        {110,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ},
    };

    // currently defaulted at billing data only
    header.lastLoadProfileTime = getLastLPTime().seconds();
    if( getKV2Protocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )//DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << getName() <<" lastLPTime: "<<getLastLPTime()<< endl;
    }

    RWCString pswdTemp;
    pswdTemp = getIED().getPassword();
    pswdTemp.toUpper();
    /*if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << "pswdTemp "<<pswdTemp<< endl;
    } */
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

    BYTE scanOperation = 0;  //0 = general Scan

    // put the stuff in the buffer
    memcpy( aMsg, &header, sizeof (header));
    memcpy( (aMsg+sizeof(header)), &password, sizeof (password));
    memcpy ((aMsg+sizeof(header)+sizeof(password)),
            &table,
            (header.numTablesRequested*sizeof (ANSI_TABLE_WANTS)));
    memcpy ((aMsg+sizeof(header)+sizeof(password)+(header.numTablesRequested*sizeof(ANSI_TABLE_WANTS))),
            &scanOperation, sizeof(BYTE));
    memcpy ((aMsg+sizeof(header)+sizeof(password)+(header.numTablesRequested*sizeof (ANSI_TABLE_WANTS)) +sizeof(BYTE)),
            &_parseFlags, sizeof(UINT));


    // keep the list on the scanner side for decode
    //getKV2Protocol().buildWantedTableList (aMsg);
    return NORMAL;
}

/*************************************************************************************
* build the list of tables and header requested in the device as each ansi device may need a few
* different tables
*************************************************************************************
*/

int CtiDeviceKV2::buildCommanderTableRequest (BYTE *aMsg)
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
    header.lastLoadProfileTime = 0;
    
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
    memcpy ((aMsg+sizeof(header)+sizeof(password)+(header.numTablesRequested*sizeof (ANSI_TABLE_WANTS)) +sizeof(BYTE)),
            &_parseFlags, sizeof(UINT));


    // keep the list on the scanner side for decode
    //getKV2Protocol().buildWantedTableList (aMsg);
    return NORMAL;
}

//=========================================================================================================================================
//
//=========================================================================================================================================

CtiProtocolANSI & CtiDeviceKV2::getKV2Protocol( void )
{
   return _ansiProtocol;
}

//=====================================================================================================================
//=====================================================================================================================

void CtiDeviceKV2::processDispatchReturnMessage( CtiReturnMsg *msgPtr, UINT archiveFlag )
{

    CtiMultiMsg                      *msgMulti = CTIDBG_new CtiMultiMsg;
    CtiPointDataMsg                  *pData = NULL;
    //CtiPointBase                     *pPoint = NULL;
    CtiPointAnalog *pPoint = NULL;
    CtiPointStatus *pStatusPoint = NULL;
    double value = 0;
    double time = 0;
    double lpValue = 0;
    int    qual = 0;
    bool                             foundSomething = false;
    bool gotValue = false;
    bool gotLPValues = false;
    int x, y, z;
    RWTime lastLoadProfileTime;
    RWCString resultString = "";

    //RWCString 
    _result_string = "";  

    {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ----Process Dispatch Message In Progress For " << getName() << "----" << endl;
    }
    if (getKV2Protocol().getScanOperation() == 1)
    {
        return;
    }
    else //general Scan
    {
        if (useScanFlags())
        {                 
            lastLoadProfileTime = getLastLPTime();
        }

        x =  OFFSET_TOTAL_KWH;
        while (x <= OFFSET_METER_TIME_STATUS)  
        {
            pPoint = (CtiPointAnalog*)getDevicePointOffsetTypeEqual(x, AnalogPointType);
            if (pPoint != NULL) 
            {
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
                        gotValue = getKV2Protocol().retreiveSummation( x, &value );
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
                        gotValue = getKV2Protocol().retreiveDemand( x, &value, &time );
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

                        gotLPValues = getKV2Protocol().retreiveLPDemand( x, 1);  // 1=table64 - kv2 only uses that lp table.
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
                        gotValue = getKV2Protocol().retreivePresentValue(x, &value);
                        break;
                    }
                    case OFFSET_BATTERY_LIFE:
                    case OFFSET_DAYS_ON_BATTERY:
                    {
                        gotValue = getKV2Protocol().retreiveBatteryLife(x, &value);
                        if( getKV2Protocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )//DEBUGLEVEL_LUDICROUS )
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
                    resultString = getName() + " / " + pPoint->getName() + ": " + CtiNumStr(value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
                    
                    pData->setValue( value );
                    qual = NormalQuality;
                    pData->setQuality( qual );
                    //if (_parseFlags & CMD_FLAG_UPDATE)
                    if (archiveFlag & CMD_FLAG_UPDATE)
                    {
                        pData->setTags(TAG_POINT_MUST_ARCHIVE);
                    }
                    if (time != 0)
                    {
                        pData->setTime(RWTime(time));
                    }   
                    else
                    {
                        pData->setTime( RWTime() );
                    }
                    pData->setType( pPoint->getType() );

                    msgPtr->insert(pData); 
                    if( getKV2Protocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )//DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " gotValue! "<< endl;
                    }
                    pData = NULL;
                }
                else if (gotLPValues) 
                {
                    if( getKV2Protocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )//DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " gotLPValues! "<< endl;
                    }

                    int ptMultiplier = pPoint->getMultiplier();
                    int ptOffset = pPoint->getDataOffset();


                    qual = NormalQuality;

                    for (y = getKV2Protocol().getTotalWantedLPBlockInts()-1; y >= 0; y--) 
                    {
                        if (getKV2Protocol().getLPTime(y) > lastLoadProfileTime.seconds())
                        {
                            qual = NormalQuality;

                            lpValue = getKV2Protocol().getLPValue(y);
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
                            pData->setTime( RWTime(getKV2Protocol().getLPTime(y)) );
                            msgPtr->insert(pData); 
                            pData = NULL;
                        }
                        else
                        {
                            y = -1;
                        }
                    }
                    setLastLPTime(RWTime(getKV2Protocol().getLPTime(getKV2Protocol().getTotalWantedLPBlockInts()-1)));
                    if( getKV2Protocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )//DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << "lastLPTime "<<RWTime(getKV2Protocol().getlastLoadProfileTime())<< endl;
                    }
                    if (pData != NULL) 
                    {
                        delete []pData;
                        pData = NULL;
                    }
                } 
                pPoint = NULL;
            }

            else //try pPoint as a StatusPoint
            {
                pStatusPoint = (CtiPointStatus*)getDevicePointOffsetTypeEqual(x, StatusPointType);
                if (pStatusPoint != NULL)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Point Offset ==> " <<x<< endl;
                    }
                    foundSomething = true;
                    if (x == OFFSET_METER_TIME_STATUS)
                    {
                        gotValue = getKV2Protocol().retreiveMeterTimeDiffStatus(x, &value);
                        if (gotValue) 
                        {
                            pData = CTIDBG_new CtiPointDataMsg();
                            pData->setId( pStatusPoint->getID() );

                            pData->setValue( value );
                            qual = NormalQuality;
                            pData->setQuality( qual );
                            //if (_parseFlags & CMD_FLAG_UPDATE)
                            if (archiveFlag & CMD_FLAG_UPDATE)
                            {
                                pData->setTags(TAG_POINT_MUST_ARCHIVE);
                            }
                            pData->setTime( RWTime() );
                            pData->setType( pStatusPoint->getType() );

                            msgPtr->insert(pData); 
                            if( getKV2Protocol().getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )//DEBUGLEVEL_LUDICROUS )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " gotValue! "<< endl;
                            }

                            resultString  = getName() + " / " + pStatusPoint->getName() + ": " + ResolveStateName(((CtiPointStatus *)pStatusPoint)->getStateGroupID(), value);
                            pData = NULL;
                        }
                    }
                    pStatusPoint = NULL;
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
    if( msgMulti != NULL )
    {
       delete msgMulti;
       msgMulti = NULL;
    }    
}



