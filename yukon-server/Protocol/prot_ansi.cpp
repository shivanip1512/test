
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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/11/15 20:37:23 $
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
   _inBuff = new BYTE[512];
   _outBuff = new BYTE[4000];
   _weDone = false;
   _index = 0;
   _numberOfTablesToGet = 0;
   _numberOfTablesReceived = 0;

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiProtocolANSI::~CtiProtocolANSI()
{

   delete []_inBuff;
   delete []_outBuff;

   delete _tableZeroZero;
   delete _tableZeroOne;
   delete _tableOneOne;
   delete _tableOneTwo;
   delete _tableOneThree;
   delete _tableOneFour;
   delete _tableOneFive;
   delete _tableOneSix;
   delete _tableTwoOne;
   delete _tableTwoTwo;
   delete _tableTwoThree;
   delete _tableFiveTwo;

   delete _ansiBillingTable;
}

//=========================================================================================================================================
//scanner side:
//we want to grab the tables that are needed for a general scan of the ansi meter (kv2) and stick them in the outmessage, then we'll send
//the list to porter
//=========================================================================================================================================

void CtiProtocolANSI::getGeneralScanTables( BYTE *ptrToOutmessageBuffer )
{
   _tableList[0] = 0;
   _tableList[1] = 1;

   getTables( ptrToOutmessageBuffer, 2 );

   memset( _tableList, NULL, sizeof( _tableList ) );
}

//=========================================================================================================================================
//this is just a test method at this point, but it could be a getBillingTables() or getGeneralScanTables() just by adjusting the ID's
//requested...
//=========================================================================================================================================

void CtiProtocolANSI::getBillingTables( BYTE *ptrToOutmessageBuffer )
{
   //these are the ansi-table ID's that we want
   _tableList[0] = 0;
   _tableList[1] = 1;
   _tableList[2] = 11;
   _tableList[3] = 12;
   _tableList[4] = 13;
   _tableList[5] = 14;
   _tableList[6] = 15;
   _tableList[7] = 16;
   _tableList[8] = 21;
   _tableList[9] = 22;
   _tableList[10] = 23;
   _tableList[11] = 52;

   getTables( ptrToOutmessageBuffer, 12 );

   memset( _tableList, NULL, sizeof( _tableList ) );
}

//=========================================================================================================================================
//a general request builder that we can pass any number of table ids that we want
//
// NOTE: not sure if we'll be able to get tables with ID larger than 255 if we use a byte array (_tableList) to hold the ids on the way in
//............hmmmmmmmmmmmmmmm...
//=========================================================================================================================================

void CtiProtocolANSI::getTables( BYTE *ptrToOutmessageBuffer, int numTables )
{
   int   index;
   int   temp = 0;

   _tables = new ANSI_TABLE_WANTS[numTables];

   for( index = 0; index < numTables; index++ )
   {
      _tables[index].tableID = _tableList[index];
      _tables[index].tableOffset = 0;
      _tables[index].bytesExpected = getTableSize( _tableList[index] );
   }

   temp = sizeof( ANSI_TABLE_WANTS ) * index;

   if( ptrToOutmessageBuffer != NULL )
      memcpy( ptrToOutmessageBuffer, _tables, temp );

//   _numberOfTablesToGet = numTables;
}

//=========================================================================================================================================
//porter side:
//we take the outmessage that we assembled above on the scanner side and start it on it's journey through the layers of the ansi protocol
//=========================================================================================================================================

int CtiProtocolANSI::recvOutbound( OUTMESS *OutMessage )
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

      _numberOfTablesToGet = _header->numTablesRequested;

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
   int   numBytes = 0;

   if( getApplicationLayer().decode( xfer ) != false )
   {
      memset( data, NULL, sizeof( data ));

      numBytes = getApplicationLayer().pullData( data );

      //now we whack off the <ok> & <count> in front
      convertToTable( &data[3], numBytes-3 );
   }

   setTransactionComplete( getApplicationLayer().getDone() );

   return( 1 ); //just a val
}

//=========================================================================================================================================
//we've gotten table data back from the device, so we'll paste it into the appropriate table structure
//we may decode it here, we may send it somewhere else....
//=========================================================================================================================================

void CtiProtocolANSI::convertToTable( BYTE *data, int numBytes )
{
   switch( _tables[_index].tableID )
   {
   case 0:
      {
         _tableZeroZero = new CtiAnsiTableZeroZero( data );

         //let's keep track of actual tables we've gotten back
         _numberOfTablesReceived++;
      }
      break;

   case 1:
      {
         _tableZeroOne = new CtiAnsiTableZeroOne( data );
         _numberOfTablesReceived++;
      }
      break;

   case 10:
      {
         _tableOneZero = new CtiAnsiTableOneZero( data );
         _numberOfTablesReceived++;
      }
      break;

   case 11:
      {
         _tableOneOne = new CtiAnsiTableOneOne( data );
         _numberOfTablesReceived++;
      }
   break;

   case 12:
      {
         _tableOneTwo = new CtiAnsiTableOneTwo( data, _tableOneOne->getNumberUOMEntries() );
         _numberOfTablesReceived++;
      }
      break;

   case 13:
      {
         _tableOneThree = new CtiAnsiTableOneThree( data, _tableOneOne->getNumberDemandControlEntries(), _tableOneOne->getPFExcludeFlag(), _tableOneOne->getSlidingDemandFlag(),
                                                          _tableOneOne->getResetExcludeFlag() );
         _numberOfTablesReceived++;
      }
      break;

   case 14:
      {
         _tableOneFour = new CtiAnsiTableOneFour( data, _tableOneOne->getDataControlLength(), _tableOneOne->getNumberDataControlEntries());
         _numberOfTablesReceived++;
      }
      break;

   case 15:
      {
         _tableOneFive = new CtiAnsiTableOneFive( data, _tableOneOne->getConstantsSelector(), _tableOneOne->getNumberConstantsEntries(), _tableOneOne->getOffsetFlag(),
                                                  _tableOneOne->getSetOnePresentFlag(), _tableOneOne->getSetTwoPresentFlag() );
         _numberOfTablesReceived++;
      }
      break;

   case 16:
      {
         _tableOneSix = new CtiAnsiTableOneSix( data, _tableOneOne->getNumberConstants() );
         _numberOfTablesReceived++;
      }
      break;

   case 21:
      {
         _tableTwoOne = new CtiAnsiTableTwoOne( data );
         _numberOfTablesReceived++;
      }
      break;

   case 22:
      {
         _tableTwoTwo = new CtiAnsiTableTwoTwo( data, _tableTwoOne->getNumberSummations(), _tableTwoOne->getNumberDemands(), _tableTwoOne->getCoinValues() );
         _numberOfTablesReceived++;
      }
      break;

   case 23:
      {
         _tableTwoThree = new CtiAnsiTableTwoThree( data, _tableTwoOne->getOccur(), _tableTwoOne->getNumberSummations(),
                                                          _tableTwoOne->getNumberDemands(), _tableTwoOne->getCoinValues(), _tableTwoOne->getTier(),
                                                          _tableTwoOne->getDemandResetCtrFlag(), _tableTwoOne->getTimeDateFieldFlag(),
                                                          _tableTwoOne->getCumDemandFlag(), _tableTwoOne->getContCumDemandFlag() );
         _numberOfTablesReceived++;
      }
      break;

   case 52:
      {
         _tableFiveTwo = new CtiAnsiTableFiveTwo( data );

         _numberOfTablesReceived++;
      }
      break;
   }

   if( _index < _header->numTablesRequested )
      _index++;

   //once we've got all the tables we asked for, we can assemble a message
   //for scanner
//   if( _numberOfTablesReceived >= _numberOfTablesToGet )
   {


//      setTransactionComplete( true );
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiProtocolANSI::getTableSize( int tableID )
{
   switch( tableID )
   {
   case 27:
      return sizeof( TABLE_27_PRESENT_REGISTER_SELECTION );
      break;

   case 28:
      return sizeof( TABLE_28_PRESENT_REGISTER_DATA );
      break;

   case 72:
      return sizeof( TABLE_GE72_POWER_QUALITY );
      break;

   default:
      return( 0 );
      break;
   }
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

//=========================================================================================================================================
//=========================================================================================================================================

int CtiProtocolANSI::sendInbound( INMESS *InMessage )
{
   if( 1 )
   {
      processBillingTables();

      if( getTotalBillingSize() < sizeof( InMessage->Buffer.InMessage ))
         memcpy( InMessage->Buffer.InMessage, _outBuff, getTotalBillingSize());
      else
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
         dout << "!!!  data > 4k!  !!!" << endl;
      }
   }
   else
      processLoadProfileTables();

   return( 1 );    //don't know what we should use...
}

//=========================================================================================================================================
//deciphering the tables we got with billing data
//these are the full tables 00,01,11,12,13,14,15,15,16,21,22,23,52
//we will yank out what stuff we need from most of the tables, stick them in a new struct and pass it and the full table 23 (perhaps
//others) to the scanner side..
//=========================================================================================================================================

void CtiProtocolANSI::processBillingTables( void )
{
   _ansiBillingTable = new CtiAnsiBillingTable( _tableTwoTwo->getDemandSelectSize(), _tableTwoThree->getTotSize(), _tableTwoOne->getNumberSummations(),
                                                _tableTwoOne->getNumberDemands(), _tableTwoOne->getCoinValues(), _tableTwoOne->getOccur(),
                                                _tableOneOne->getNumberUOMEntries(), _tableOneOne->getNumberDemandControlEntries(),
                                                _tableOneOne->getDataControlLength(), _tableOneOne->getNumberDataControlEntries(),
                                                _tableOneOne->getNumberConstantsEntries(),_tableZeroZero->getTmFormat(), _tableZeroZero->getIntFormat(),
                                                _tableZeroZero->getNiFormatOne(), _tableZeroZero->getNiFormatTwo() );

   if( _outBuff != NULL )
   {
      memcpy( _outBuff, _ansiBillingTable, _ansiBillingTable->getTableSize());
      _outBuff += _ansiBillingTable->getTableSize();

      memcpy( _outBuff, (void *)&(_tableTwoThree->getTotDataBlock()), _tableTwoThree->getTotSize());
      _outBuff += _tableTwoThree->getTotSize();

      memcpy( _outBuff, _tableTwoTwo->getDemandSelect(), _tableTwoTwo->getDemandSelectSize());
   }
}

//=========================================================================================================================================
//deciphering the tables we got with load-profile data
//=========================================================================================================================================

void CtiProtocolANSI::processLoadProfileTables( void )
{

}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiProtocolANSI::getTotalBillingSize( void )
{
   int temp = _ansiBillingTable->getTableSize();
   int hemp = _tableTwoTwo->getDemandSelectSize();
   int demp = _tableTwoThree->getTotSize();

   return _ansiBillingTable->getTableSize()+_tableTwoTwo->getDemandSelectSize()+_tableTwoThree->getTotSize();
}
