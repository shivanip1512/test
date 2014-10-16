#include "precompiled.h"

#include "guard.h"
#include "logger.h"
#include "prot_transdata.h"

using std::endl;
using std::vector;
using std::string;

//=====================================================================================================================
//=====================================================================================================================

CtiProtocolTransdata::CtiProtocolTransdata():
   _storage( NULL ),
   _billingBytes( NULL ),
   _lpBytes( NULL ),
   _lastLPTime( CtiTime::now() - 31 * 86400 )  //  31 days ago;  for startup conditions
{
   reinitalize();
}

//=====================================================================================================================
//=====================================================================================================================

CtiProtocolTransdata::~CtiProtocolTransdata()
{
   destroy();
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolTransdata::generate( CtiXfer &xfer )
{
   _application.generate( xfer );

   return( false );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolTransdata::decode( CtiXfer &xfer, int status )
{
   _application.decode( xfer, status );

   if( _application.isTransactionComplete() )
   {
      if( _application.getError() == Failed )
      {
         setError( Failed );
         _finished = true;
      }
      else
      {
         _numBytes = _application.retrieveData( _storage );

         if(( _lpDone ) && ( _billingDone ))
         {
            if( _application.loggedOff() )
            {
               _finished = true;
            }
         }
         else
         {
            if( _command == CtiTransdataApplication::LoadProfile )
            {
               processLPData( _storage );
            }
            else //_command == GENERAL
            {
               processBillingData( _storage );

               if( _application.getCommand() == CtiTransdataApplication::LoadProfile )
               {
                  _command = CtiTransdataApplication::LoadProfile;             ////just temp until I get smart
               }
               else
               {
                  _lpDone = true;   //so we skip trying
               }
            }
         }
      }
   }

   return( getError() );
}

//=====================================================================================================================
//all we're going to do with this guy is copy data from one box to another, we'll do the real decode work on scanner
//=====================================================================================================================

void CtiProtocolTransdata::processBillingData( BYTE *data )
{
   _numBilling = _numBytes;

   if( _numBilling > Billing_size )
   {
      if( getDebugLevel() & DEBUGLEVEL_ACTIVITY_INFO )
      {
          CTILOG_DEBUG(dout, "Billing bytes too large - bytes = "<< _numBilling);
      }
   }
   else
   {
      memcpy( _billingBytes, _storage, _numBilling );
      _billingDone = true;
   }
}

//=====================================================================================================================
//we want to sort out and squish together the raw data into a form suitable for storing until we're done-done with
//our communication loop.  The data will get stuck into a Dispatch message after that
//=====================================================================================================================

void CtiProtocolTransdata::processLPData( BYTE *data )
{
   _numLoadProfile = _numBytes;

   if( _numLoadProfile > Loadprofile_size )
   {
      if( getDebugLevel() & DEBUGLEVEL_ACTIVITY_INFO )
      {
          CTILOG_DEBUG(dout, "LP bytes too large - bytes = "<< _numLoadProfile);
      }
   }
   else
   {
      memcpy( _lpBytes, data, _numLoadProfile );

      _reallyDidProcessLP = true;
      _lpDone = true;
   }
}

//=====================================================================================================================
//=====================================================================================================================

int CtiProtocolTransdata::recvOutbound( OUTMESS *OutMessage )
{
   mkv *ptr = NULL;

   _application.setLastLPTime( OutMessage->Buffer.DUPReq.LP_Time );

   ptr = ( mkv *)OutMessage->Buffer.OutMessage;

   if( ptr != NULL )
   {
      setCommand( ptr->command, ptr->getLP );
      ptr = NULL;
   }

   return( 1 );
}

//=====================================================================================================================
//=====================================================================================================================

CtiTime CtiProtocolTransdata::getLastLoadProfileTime( void )
{
   return( _lastLPTime );
}

//=====================================================================================================================
//=====================================================================================================================

YukonError_t CtiProtocolTransdata::sendCommResult( INMESS &InMessage )
{
   if( _billingDone )
   {
//      memcpy( InMessage.Buffer.InMessage + sizeof( ULONG ), _billingBytes, _numBilling );
      memcpy( InMessage.Buffer.InMessage + sizeof( llp ), _billingBytes, _numBilling );
      InMessage.InLength = _numBilling + sizeof( llp );
      InMessage.ErrorCode = ClientErrors::None;
      _numBytes = 0;
   }
   else
   {
      InMessage.ErrorCode = ClientErrors::Abnormal;
      InMessage.InLength = 0;   //matt says this is the way to know failure
   }

   return ClientErrors::None;
}

//=====================================================================================================================
//we're going to take the raw data that we got back from porter and bust it up into messages and stick them into
//a vector, pass them back up to the device, and we're done from here down...
//=====================================================================================================================

vector<CtiTransdataData *> CtiProtocolTransdata::resultDecode( const INMESS &InMessage )
{
   CtiTransdataData           *converted = NULL;
   const BYTE                 *ptr = NULL;
   const llp                  *lp = NULL;
   vector<CtiTransdataData *> transVector;

   ptr = ( const unsigned char*)( InMessage.Buffer.InMessage );
   const BYTE *pEND = (const BYTE*)( InMessage.Buffer.InMessage ) + sizeof(InMessage.Buffer.InMessage);   // 20060908 Don't decode beyond your DATA!

   lp = ( const llp *)ptr;

   if(_lastLPTime <= lp->lastLP && lp->lastLP <= _lastLPTime.now() + 86400)
   {
       _lastLPTime = lp->lastLP;
       ptr += sizeof( lp );

       if( getDebugLevel() & DEBUGLEVEL_ACTIVITY_INFO )
       {
           CTILOG_DEBUG(dout, "Scanner thinks last lp time is = "<< _lastLPTime);
       }

       while( *ptr != NULL && ptr < pEND )
       {
          converted = CTIDBG_new CtiTransdataData( ptr );

          // Do we need to NULL the converted ptr???

          transVector.push_back( converted );

          if( ptr != NULL )
          {
             ptr = (const unsigned char*)strchr((const char*)ptr, '\n' );
             ptr++;
          }
       }
   }
   else
   {
       CTILOG_WARN(dout, "Decode attempted to assign a last LP timestamp of "<< lp->lastLP <<" to the device");
   }

   //we're done with this guy
   if( ptr != NULL )
      ptr = NULL;

   return( transVector );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolTransdata::isTransactionComplete( void ) const
{
   return( _finished );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolTransdata::injectData( string str )
{
   _application.injectData( str );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolTransdata::reinitalize( void )
{
   _command = 0;
   _error = Working;
   _numBilling = 0;
   _numLoadProfile = 0;

   _application.reinitalize();

   _collectLP = true;
   _finished = false;
   _billingDone = false;
   _lpDone = false;
   _reallyDidProcessLP = false;

   //could probably get away with using destroy()
   if( _storage != NULL )
   {
      delete [] _storage;
   }

   if( _lpBytes != NULL )
   {
      delete [] _lpBytes;
   }

   if( _billingBytes != NULL )
   {
      delete [] _billingBytes;
   }

   _storage = CTIDBG_new BYTE[Storage_size];
   _lpBytes = CTIDBG_new BYTE[Loadprofile_size];
   _billingBytes = CTIDBG_new BYTE[Billing_size];
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolTransdata::destroy( void )
{
   _application.destroy();

   if( _storage != NULL )
   {
      delete [] _storage;
      _storage = NULL;
   }

   if( _billingBytes != NULL )
   {
      delete [] _billingBytes;
      _billingBytes = NULL;
   }

   if( _lpBytes != NULL )
   {
      delete [] _lpBytes;
      _lpBytes = NULL;
   }
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolTransdata::setCommand( int cmd, bool lp )
{
   _command = cmd;
   _collectLP = lp;

   _application.setCommand( cmd, lp );
}

//=====================================================================================================================
//=====================================================================================================================

int CtiProtocolTransdata::getCommand( void )
{
   return( _command );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolTransdata::getAction( void )
{
   return( _collectLP );
}

//=====================================================================================================================
//=====================================================================================================================

int CtiProtocolTransdata::retrieveData( BYTE *data )
{
   int temp = _numBytes;

   if(( data != NULL ) && ( _lpBytes != NULL ))
   {
      memcpy( data, _lpBytes, _numBytes );
   }
   return( temp );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolTransdata::setError( int err )
{
   _error = err;
}

//=====================================================================================================================
//=====================================================================================================================

int CtiProtocolTransdata::getError( void )
{
   return( _error );
}

//=====================================================================================================================
//we want to let the device know that we did some actual work on LP so he doesn't bother to try to stick a multi
//into dispatch if there isn't anything there
//=====================================================================================================================

bool CtiProtocolTransdata::getDidProcess( void )
{
   return( _reallyDidProcessLP );
}
