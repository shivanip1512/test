
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_kv2
*
* Date:   6/13/2002
*
* Author: Eric Schmit
*
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_kv2.cpp-arc  $
*    REVISION     :  $Revision: 1.5 $
*    DATE         :  $Date: 2003/04/25 15:14:07 $
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
      Revision 1.5  2003/04/25 15:14:07  dsutton
      Changed general scan and decode result


* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "porter.h"
#include "logger.h"
#include "dev_kv2.h"
#include "utility.h"

//=========================================================================================================================================
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

      buildScannerTableRequest (ptr);
      outList.insert( OutMessage );
   }
   else
   {
      return MEMORY;
   }
   return NoError;
}

//=========================================================================================================================================
//=========================================================================================================================================

INT CtiDeviceKV2::ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist < CtiMessage >&retList,
                                RWTPtrSlist< OUTMESS >    &outList)
{
    getProtocol().receiveCommResult( InMessage );
//   getProtocol().recvInbound( InMessage );

//   CtiLockGuard< CtiLogger > doubt_guard( dout );
//   dout << RWTime::now() << " ==============================================" << endl;
//   dout << RWTime::now() << " ==========The KV2 responded with data=========" << endl;
//   dout << RWTime::now() << " ==============================================" << endl;

   return( 1 ); //just a val
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
    // here are the tables requested for the kv2
    ANSI_TABLE_WANTS    table[100] = {
        {  0,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  1,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
//        {  7,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_WRITE},
//        {  8,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 11,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 12,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 13,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 14,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 15,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 16,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 21,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 22,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 23,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        { 52,     0,      0,      ANSI_TABLE_TYPE_STANDARD,          ANSI_OPERATION_READ},
        {  0,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ},
        { 70,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ},
        {  -1,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ}
//        {110,     0,      0,      ANSI_TABLE_TYPE_MANUFACTURER,      ANSI_OPERATION_READ},
    };


    // currently defaulted at billing data only
    header.lastLoadProfileTime = getLastLPTime().seconds();

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

    // put the stuff in the buffer
    memcpy( aMsg, &header, sizeof (header));
    memcpy ((aMsg+sizeof(header)),
            &table,
            (header.numTablesRequested*sizeof (ANSI_TABLE_WANTS)));

    // keep the list on the scanner side for decode
    getProtocol().buildWantedTableList (aMsg);
    return NORMAL;
}

//=========================================================================================================================================
//
//=========================================================================================================================================

CtiProtocolANSI & CtiDeviceKV2::getProtocol( void )
{
   return _ansiProtocol;
}




