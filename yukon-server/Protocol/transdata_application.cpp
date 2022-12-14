#include "precompiled.h"
#include "logger.h"
#include "transdata_application.h"

using std::string;

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataApplication::CtiTransdataApplication():
   _storage( NULL )
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
   case DoLogOn:
      {
         _tracker.logOn( xfer );
         _loggedOff = false;
      }
      break;

   case DoTalk:
      {
         if( _checkRecs )
         {
            checkRecs();
         }

         switch( _command )
         {
         case General:
            {
               _tracker.billing( xfer );
            }
            break;

         case LoadProfile:
            {
               _tracker.loadProfile( xfer );
            }
            break;
         }
      }
      break;

   case DoLogOff:
      {
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
   _tracker.decode( xfer, status );

   if( _tracker.isTransactionComplete() )
   {
      if( _tracker.getError() == Failed )
      {
         setError( Failed );
         _finished = true;
      }
      else
      {
         if( _tracker.haveData() )
         {
            if( _storage )
            {
               _numBytes = _tracker.retrieveData( _storage );
               _finished = true;
            }
         }

         if( _lastState == DoLogOn )
         {
            _connected = true;
         }

         if( _lastState == DoLogOff )
         {
            _finished  = true;
            _loggedOff = true;
         }

         setNextState();
      }
   }

   return( _finished );
}

//=====================================================================================================================
//receives the password to the meter, shoves is down to the next level
//=====================================================================================================================

void CtiTransdataApplication::injectData( string str )
{
   _tracker.injectData( str );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataApplication::setNextState( void )
{
   if( _lastState == DoLogOff )
   {
      _lastState = DoLogOn;
   }
   else if( _lastState == DoTalk )
   {
      if( _getLoadProfile )
      {
         _command = LoadProfile;
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
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataApplication::destroy( void )
{
   _tracker.destroy();

   if( _storage != NULL )
   {
      delete [] _storage;
      _storage = NULL;
   }
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataApplication::reinitalize( void )
{
   _tracker.reinitalize();

   _lastState     = DoLogOn;

   _numBytes      = 0;
   _error         = 0;

   _connected     = false;
   _finished      = true;
   _checkRecs     = true;
   _loggedOff     = false;

   if( _storage != NULL )
   {
      delete [] _storage;
   }

   _storage       = CTIDBG_new BYTE[Storage_size];
}

//=====================================================================================================================
//passes the data received up to the next level
//=====================================================================================================================

int CtiTransdataApplication::retrieveData( BYTE *data )
{
   int temp = _numBytes;

   if( data != NULL )
   {
      memcpy( data, _storage, _numBytes );
   }
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

int CtiTransdataApplication::getCommand( void )
{
   return( _command );
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
//if we want to get loadprofile, but there aren't any records needed (based on lastLPTime), we want to over-ride
//what setcommand() said to do
//=====================================================================================================================

void CtiTransdataApplication::checkRecs( void )
{
   if( _tracker.calcLPRecs() == 0 )
      _getLoadProfile = false;

   _checkRecs = false;
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataApplication::doLoadProfile( void )
{
   return( _getLoadProfile );
}

//=====================================================================================================================
//=====================================================================================================================

int CtiTransdataApplication::getError( void )
{
   return( _error );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataApplication::setError( int err )
{
   _error = err;
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataApplication::loggedOff( void )
{
   return( _loggedOff );
}
