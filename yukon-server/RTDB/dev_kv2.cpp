
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_kv2
*
* Date:   6/13/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/11/15 20:42:58 $
*
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

      //why don't we use the getProtocol() to get the header???
      adjustment = makeMessageHeader( ptr, 5 );

//      getProtocol().getGeneralScanTables( ptr + adjustment );

      getProtocol().getBillingTables( ptr + adjustment );

      outList.insert( OutMessage );
//      OutMessage = NULL;
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


   CtiLockGuard< CtiLogger > doubt_guard( dout );
   dout << RWTime::now() << " The KV2 responded with data" << endl;
   return( 1 ); //just a val
}

//=========================================================================================================================================
//=========================================================================================================================================

INT CtiDeviceKV2::ErrorDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist < CtiMessage >&vgList, RWTPtrSlist< CtiMessage > &retList,
                               RWTPtrSlist< OUTMESS > &outList)
{

   return( 1 ); //just a val
}

//=========================================================================================================================================
//here we're making up a little extra data that we will need on the porter side of the tracks, so we take a pointer to our outmessag[300]
//buffer, build a header and stick it in
//=========================================================================================================================================

int CtiDeviceKV2::makeMessageHeader( BYTE *ptr, int cmd )
{
   WANTS_HEADER   *header = CTIDBG_new WANTS_HEADER;
   int            length;

   switch( cmd )
   {
   case( 0 ): //general scan
      {
         header->lastLoadProfileTime = getLastLPTime().seconds();     //don't know if this is avail to us ....
         header->numTablesRequested = 2;
         header->command = cmd;
      }
      break;

   case( 1 ):
      {
         header->lastLoadProfileTime = getLastLPTime().seconds();     //not sure about this
         header->numTablesRequested = 2;
         header->command = cmd;
      }
      break;

   case( 5 ):
      {
         header->lastLoadProfileTime = getLastLPTime().seconds();
         header->numTablesRequested = 12;
         header->command = cmd;
      }
      break;

   default:
      {
         header->lastLoadProfileTime = 0;
         header->numTablesRequested = 0;
         header->command = 0;
      }
      break;
   }

   length = sizeof( *header );
   memcpy( ptr, ( void *)header, length );

   return( length );
}

//=========================================================================================================================================
//
//=========================================================================================================================================

CtiProtocolANSI & CtiDeviceKV2::getProtocol( void )
{
   return _ansiProtocol;
}




