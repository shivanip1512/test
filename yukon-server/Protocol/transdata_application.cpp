
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   transdata_application
*
* Date:   7/22/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2003/10/06 15:18:59 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "transdata_application.h"

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataApplication::CtiTransdataApplication()
{
   _lastState     = 0;
   _numBytes      = 0;
   _finished      = true;
   _storage       = NULL;
}

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataApplication::~CtiTransdataApplication()
{
   destroyMe();
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataApplication::generate( CtiXfer &xfer )
{
   _finished = false;

   switch( _lastState )
   {
   case doLogOn:
      _tracker.logOn( xfer );
      break;

   case doTalk:
      _tracker.general( xfer );    //this should have other *things* it can do...  ie load profile
      break;

   case doLogOff:
      _tracker.logOff( xfer );
      break;
   }

   return( true );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataApplication::isTransactionComplete( void )
{
   return( _finished );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataApplication::decode( CtiXfer &xfer, int status )
{
   _tracker.decode( xfer, status );

   if( _tracker.isTransactionComplete() )
   {
      processData( xfer.getInBuffer() + HEADER_WIDTH, xfer.getInCountActual() );

//      setNextState();

      if( _lastState == doLogOff )
         _finished = true;

      setNextState();
   }

   return( _finished );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataApplication::injectData( RWCString str )
{
   _tracker.injectData( str );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataApplication::setNextState( void )
{
   if( _lastState == doLogOff )
   {
      _lastState = doLogOn;
   }
   else
   {
      _lastState++;
   }
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataApplication::processData( BYTE *data, int numBytes )
{
//   BYTE     *ptr;
//   BYTE     *chunk;

   if( _tracker.goodCRC() )
   {
      while( *data != 0 )
      {
         _storage = new BYTE[numBytes + 1];
         _numBytes = numBytes;

         if( _storage )
            memcpy( _storage, data, _numBytes );

         /*
         ptr = ( unsigned char*)strchr(( const char *)data, '\n' );

         _converted = new CtiTransdataData( data );

         _transVector.push_back( *_converted );

         if( ptr != NULL )
            data = ++ptr;
*/
      }
   }

   return( true );
}

//=====================================================================================================================
//=====================================================================================================================
/*
vector<CtiTransdataData> CtiTransdataApplication::getConverted( void )
{
   return( _transVector );
}
*/
//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataApplication::destroyMe( void )
{
   if( _storage )
      delete [] _storage;


/*
   while( !_transVector.empty() )
   {
      delete _transVector.back();
      _transVector.pop_back();
   }
*/
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataApplication::reinitalize( void )
{
   _tracker.reinitalize();
   destroyMe();
}

//=====================================================================================================================
//=====================================================================================================================

int CtiTransdataApplication::retreiveData( BYTE *data )
{
   memcpy( data, _storage, _numBytes );

   return( _numBytes );
}





























/*
//=====================================================================================================================
//
// here is where we'll pull apart all the number data and stick it into the main struct to pass back to scanner
//
//NOTE: need to add all 8 channels
//=====================================================================================================================

void CtiTransdataApplication::decipherConverted( transdata data )
{
   switch( data.dataID )
   {
   case ch1_TotalUsage:
      {
         _sortedData.channel[0].totalUsage_All = data.data;
      }
      break;

   case ch1_TotalUsage_A:
      {
         _sortedData.channel[0].totalUsage_A = data.data;
      }
      break;

   case ch1_TotalUsage_B:
      {
         _sortedData.channel[0].totalUsage_B = data.data;
      }
      break;

   case ch1_TotalUsage_C:
      {
         _sortedData.channel[0].totalUsage_C = data.data;
      }
      break;

   case ch1_TotalUsage_D:
      {
         _sortedData.channel[0].totalUsage_D = data.data;
      }
      break;

   case ch1_CurrentDemand:
      {
         _sortedData.channel[0].currentDemand = data.data;
      }
      break;

   case ch1_PeakDemand:
      {
         _sortedData.channel[0].peakDemand = data.data;
      }
      break;

   case ch1_TimePeak:
      {
         _sortedData.channel[0].timeOfPeak = data.data;
      }
      break;

   case ch1_DateOfPeak:
      {
         _sortedData.channel[0].dateOfPeak = data.data;
      }
      break;

   case ch1_PreviousDemand:
      {
         _sortedData.channel[0].previousDemand = data.data;
      }
      break;

   case ch2_TotalUsage:
      {
         _sortedData.channel[1].totalUsage_All = data.data;
      }
      break;

   case ch2_TotalUsage_A:
      {
         _sortedData.channel[1].totalUsage_A = data.data;
      }
      break;

   case ch2_TotalUsage_B:
      {
         _sortedData.channel[1].totalUsage_B = data.data;
      }
      break;

   case ch2_TotalUsage_C:
      {
         _sortedData.channel[1].totalUsage_C = data.data;
      }
      break;

   case ch2_TotalUsage_D:
      {
         _sortedData.channel[1].totalUsage_D = data.data;
      }
      break;

   case ch2_CurrentDemand:
      {
         _sortedData.channel[1].currentDemand = data.data;
      }
      break;

   case ch2_PeakDemand:
      {
         _sortedData.channel[1].peakDemand = data.data;
      }
      break;

   case ch2_TimePeak:
      {
         _sortedData.channel[1].timeOfPeak = data.data;
      }
      break;

   case ch2_DateOfPeak:
      {
         _sortedData.channel[1].dateOfPeak = data.data;
      }
      break;

   case ch2_PreviousDemand:
      {
         _sortedData.channel[1].previousDemand = data.data;
      }
      break;

   case ch3_TotalUsage:
      {
         _sortedData.channel[2].totalUsage_All = data.data;
      }
      break;

   case ch3_TotalUsage_A:
      {
         _sortedData.channel[2].totalUsage_A = data.data;
      }
      break;

   case ch3_TotalUsage_B:
      {
         _sortedData.channel[2].totalUsage_B = data.data;
      }
      break;

   case ch3_TotalUsage_C:
      {
         _sortedData.channel[2].totalUsage_C = data.data;
      }
      break;

   case ch3_TotalUsage_D:
      {
         _sortedData.channel[2].totalUsage_D = data.data;
      }
      break;


   case ch3_CurrentDemand:
      {
         _sortedData.channel[2].currentDemand = data.data;
      }
      break;

   case ch3_PeakDemand:
      {
         _sortedData.channel[2].peakDemand = data.data;
      }
      break;

   case ch3_TimePeak:
      {
         _sortedData.channel[2].timeOfPeak = data.data;
      }
      break;

   case ch3_DateOfPeak:
      {
         _sortedData.channel[2].dateOfPeak = data.data;
      }
      break;

   case ch3_PreviousDemand:
      {
         _sortedData.channel[2].previousDemand = data.data;
      }
      break;

   case ch4_TotalUsage:
      {
         _sortedData.channel[3].totalUsage_All = data.data;
      }
      break;

   case ch4_TotalUsage_A:
      {
         _sortedData.channel[3].totalUsage_A = data.data;
      }
      break;

   case ch4_TotalUsage_B:
      {
         _sortedData.channel[3].totalUsage_B = data.data;
      }
      break;

   case ch4_TotalUsage_C:
      {
         _sortedData.channel[3].totalUsage_C = data.data;
      }
      break;

   case ch4_TotalUsage_D:
      {
         _sortedData.channel[3].totalUsage_D = data.data;
      }
      break;


   case ch4_CurrentDemand:
      {
         _sortedData.channel[3].currentDemand = data.data;
      }
      break;

   case ch4_PeakDemand:
      {
         _sortedData.channel[3].peakDemand = data.data;
      }
      break;

   case ch4_TimePeak:
      {
         _sortedData.channel[3].timeOfPeak = data.data;
      }
      break;

   case ch4_DateOfPeak:
      {
         _sortedData.channel[3].dateOfPeak = data.data;
      }
      break;

   case ch4_PreviousDemand:
      {
         _sortedData.channel[3].previousDemand = data.data;
      }
      break;

   }
}

*/

/*
         isTime = false;

         ptr = ( unsigned char*)strchr(( const char *)data, '\n' );

         converted.dataID = stringToInt( data, IDD_WIDTH );
         data += IDD_WIDTH;

         isTime = dataIsTime( converted.dataID );

         if( isTime )
         {
            temp = stringToInt( data, DATA_WIDTH );
         }
         else
         {
            converted.data = stringToInt( data, DATA_WIDTH );
            formatData( converted );
         }
         data += DATA_WIDTH;

         converted.isNegative = isDataNegative( data, SIGN_WIDTH );
         data += SIGN_WIDTH;

         converted.formatCode = stringToInt( data, FORMAT_WIDTH );

         data = ++ptr;

         if( isTime )
         {
            formatTime( converted, temp );
         }
         else
         {
            formatData( converted );
         }
*/

