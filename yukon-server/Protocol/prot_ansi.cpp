
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   prot_ansi
*
* Date:   6/13/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/11/15 14:08:06 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/cstring.h>

#include "guard.h"
#include "logger.h"
#include "prot_ansi.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiProtocolANSI::CtiProtocolANSI()
{
   _inBuff = CTIDBG_new BYTE[512];
//   _outBuff = CTIDBG_new BYTE[300];
   _weDone = false;
   _index = 0;

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiProtocolANSI::~CtiProtocolANSI()
{
   delete _inBuff;
   delete _tableZeroZero;
   delete _tableZeroOne;
//   delete _outBUff;
}

//=========================================================================================================================================
//scanner side:
//we want to grab the tables that are needed for a general scan of the ansi meter (kv2) and stick them in the outmessage, then we'll send
//the list to porter
//=========================================================================================================================================

void CtiProtocolANSI::getGeneralScanTables( BYTE *ptrToOutmessageBuffer )
{
   int   temp;

   _tables = CTIDBG_new ANSI_TABLE_WANTS[2];

   _tables[0].tableID = 0;
   _tables[0].tableOffset = 0;
   _tables[0].bytesExpected = sizeof( TABLE_00_GEN_CONFIG );

   _tables[1].tableID = 1;
   _tables[1].tableOffset = 0;
   _tables[1].bytesExpected = sizeof( TABLE_01_GEN_MFG_ID );

   temp = sizeof( ANSI_TABLE_WANTS )* 2;

   memcpy( ptrToOutmessageBuffer, _tables, temp );
}


//...................... testing ......................

void CtiProtocolANSI::getTables( BYTE *ptrToOutmessageBuffer )
{
   int   temp;

   _tables = CTIDBG_new ANSI_TABLE_WANTS[3];

   _tables[0].tableID = 0;
   _tables[0].tableOffset = 0;
   _tables[0].bytesExpected = sizeof( TABLE_00_GEN_CONFIG );

   _tables[1].tableID = 1;
   _tables[1].tableOffset = 0;
   _tables[1].bytesExpected = sizeof( TABLE_01_GEN_MFG_ID );

   _tables[2].tableID = 12;
   _tables[2].tableOffset = 0;
   _tables[2].bytesExpected = sizeof( TABLE_12_UNIT_OF_MEASURE );

   temp = sizeof( ANSI_TABLE_WANTS )* 3;

   memcpy( ptrToOutmessageBuffer, _tables, temp );
}

//...................... testing ......................

//=========================================================================================================================================
//porter side:
//we take the outmessage that we assembled above on the scanner side and start it on it's journey through the layers of the ansi protocol
//=========================================================================================================================================

int CtiProtocolANSI::recvOutbound( OUTMESS  *OutMessage )
{
   BYTE  *bufptr = OutMessage->Buffer.OutMessage;
   BYTE  *request = CTIDBG_new BYTE[256];
   BYTE  *ptr = request;
   int   index = 0;

   //refill our structs

   _header = CTIDBG_new WANTS_HEADER;

   if( _header != NULL )
   {
      memcpy( ( void *)_header, bufptr, sizeof( WANTS_HEADER ) );
      bufptr += sizeof( WANTS_HEADER );

      _tables = CTIDBG_new ANSI_TABLE_WANTS[_header->numTablesRequested];

      if( _tables != NULL )
      {
         for( index = 0; index < _header->numTablesRequested; index++ )
         {
            memcpy( ( void *)&_tables[index], bufptr, sizeof( ANSI_TABLE_WANTS ) );
            bufptr += sizeof( ANSI_TABLE_WANTS );
         }
      }
   }

   if( index )
   {
      getApplicationLayer().passRequest( OutMessage->Buffer.OutMessage, sizeof( WANTS_HEADER ) + ( sizeof( ANSI_TABLE_WANTS )*_header->numTablesRequested ) );
      delete request;
   }

   return( index );   //just a val
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiANSIApplication &CtiProtocolANSI::getApplicationLayer( void )
{
   return _appLayer;
}

//=========================================================================================================================================
//porter side:
//pass our xfer message to whomever can make it look ansi-like
//=========================================================================================================================================

int CtiProtocolANSI::generate( CtiXfer &xfer )
{
   bool done = false;

   done = getApplicationLayer().generate( xfer );
   return( 1 );   //just a val
}

//=========================================================================================================================================
//here's where we'll decide what we got back from the port
//=========================================================================================================================================

int CtiProtocolANSI::decode( CtiXfer &xfer, int status )
{
   BYTE  data[1000];
   bool  allDone = false;

   if( getApplicationLayer().decode( xfer ) != false )
   {
      memset( data, NULL, sizeof( data ) );

      getApplicationLayer().pullData( data );
      convertToTable( data );
   }
   return( 1 ); //just a val
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiProtocolANSI::convertToTable( BYTE *data )
{
   switch( _tables[_index].tableID )
   {
   case 0:
      {
         _tableZeroZero = CTIDBG_new TABLE_00_GEN_CONFIG;
         memcpy( _tableZeroZero, data, sizeof( TABLE_00_GEN_CONFIG ) );
      }
      break;

   case 1:
      {
         _tableZeroOne = CTIDBG_new TABLE_01_GEN_MFG_ID;
         memcpy( _tableZeroOne, data, sizeof( TABLE_01_GEN_MFG_ID ) );

         delete _tableZeroOne;
      }
      break;

   case 21:
      {
         _tableTwoOne = CTIDBG_new TABLE_21_ACTUAL_REGISTER;
         memcpy( _tableTwoOne, data, sizeof( TABLE_21_ACTUAL_REGISTER ) );
      }
      break;
   }


   if( _index < _header->numTablesRequested )
      _index++;
}

//=========================================================================================================================================
//=========================================================================================================================================

ULONG CtiProtocolANSI::getBytesGot( void )
{
   return _bytesInGot;
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiProtocolANSI::setBytesGot( ULONG bytes )
{
   _bytesInGot = bytes;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiProtocolANSI::isTransactionComplete( void )
{
   return _weDone;
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiProtocolANSI::setTransactionComplete( bool done )
{
   _weDone = done;
}

