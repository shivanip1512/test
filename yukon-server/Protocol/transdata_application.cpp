
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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2003/12/18 15:57:18 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "transdata_application.h"

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataApplication::CtiTransdataApplication()
{
   reinitalize();
}

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataApplication::~CtiTransdataApplication()
{
   destroy();
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataApplication::generate( CtiXfer &xfer )
{
   _finished = false;
   
   switch( _lastState )
   {
   case doLogOn:
      {
         if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " case doLogOn" << endl;
         }
         _tracker.logOn( xfer );
      }
      break;

   case doTalk:
      {
         if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " case doTalk" << endl;
         }
         switch( _command )   
         {
         case GENERAL:
            {
               if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
               {
                  CtiLockGuard<CtiLogger> doubt_guard(dout);
                  dout << RWTime() << " case _command=GENERAL" << endl;
               }
               _tracker.billing( xfer );    
            }
            break;

         case LOADPROFILE:
            {
               if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
               {
                  CtiLockGuard<CtiLogger> doubt_guard(dout);
                  dout << RWTime() << " case _command=LOADPROFILE" << endl;
               }
               _tracker.loadProfile( xfer );    
            }
            break;
         }
      }
      break;

   case doLogOff:
      {
         if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " case doLogOff" << endl;
         }
         _tracker.logOff( xfer );
      }
      break;
   }

   return( true );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataApplication::decode( CtiXfer &xfer, int status )
{
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " app decode" << endl;
   }

   _tracker.decode( xfer, status );

   if( _tracker.isTransactionComplete() )
   {
      if( _tracker.goodCRC() )
      {
         if( _storage )
         {
            _numBytes = _tracker.retreiveData( _storage );
            _finished = true;
         }
      }
      
      if( _lastState == doLogOn )
         _connected = true;
      
      if( _lastState == doLogOff )
         _finished = true;
      
      setNextState();
   }

   return( _finished );
}

//=====================================================================================================================
//receives the password to the meter, shoves is down to the next level
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
   else if( _lastState == doTalk )
   {
      if( _getLoadProfile )
      {
         _command = LOADPROFILE;
         _getLoadProfile = false;
      }
      else
      {
         _lastState++;
      }
   }
   else
   {
      _lastState++;
   }

   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " app state " << _lastState << endl;
   }

}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataApplication::destroy( void )
{
   _tracker.destroy();

   if( _storage )
   {
      delete [] _storage;
      _storage = NULL;
   }
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataApplication::reinitalize( void )
{
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " app reinit" << endl;
   }
   
   _tracker.reinitalize();

   _lastState     = doLogOn;
   _numBytes      = 0;
   _connected     = false;
   _finished      = true;
   _storage       = new BYTE[Storage_size];
}

//=====================================================================================================================
//passes the data recieved up to the next level
//=====================================================================================================================

int CtiTransdataApplication::retreiveData( BYTE *data )
{
   int temp = _numBytes;

   memcpy( data, _storage, _numBytes );

   return( temp );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataApplication::setCommand( int cmd, bool getAll )
{
   _command = cmd;
   _getLoadProfile = getAll;
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataApplication::isTransactionComplete( void )
{
   return( _finished );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataApplication::setLastLPTime( ULONG lpTime )
{
   _tracker.setLastLPTime( lpTime );
}
























//=====================================================================================================================
//=====================================================================================================================
/*
bool CtiTransdataApplication::processData( BYTE *data, int numBytes )
{
   if( _tracker.goodCRC() )
   {
      if( *data != 0 )
      {
         _storage = new BYTE[numBytes + 1];
         _numBytes = numBytes;

         if( _storage )
            _tracker.retreiveData( _storage );
      }
   }
   
   return( true );
}
*/
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
//=====================================================================================================================
//=====================================================================================================================
/*
vector<CtiTransdataData> CtiTransdataApplication::getConverted( void )
{
   return( _transVector );
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

/*
bool CtiTransdataApplication::generate( CtiXfer &xfer )
{
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " app gen" << endl;
   }

   _finished = false;

   switch( _lastState )
   {
   case doLogOn:
      _tracker.logOn( xfer );
      break;

   case doTalk:
      {
         switch( _talkState )
         {
         case doBilling:
            _tracker.billing( xfer );    
            break;

         case doLoadProfile:
            _tracker.loadProfile( xfer );    
            break;
         }
      }
      break;

   case doLogOff:
      _tracker.logOff( xfer );
      break;
   }

   return( true );
} */
