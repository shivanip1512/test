
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   prot_transdata
*
* Date:   7/16/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2003/12/09 17:55:26 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/cstring.h>

#include "guard.h"
#include "logger.h"
#include "prot_transdata.h"

//=====================================================================================================================
//=====================================================================================================================

CtiProtocolTransdata::CtiProtocolTransdata()
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
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " prot gen" << endl;
   }

   _application.generate( xfer );

   return( false );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolTransdata::decode( CtiXfer &xfer, int status )
{
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " prot decode" << endl;
   }

   _application.decode( xfer, status );

   if( _application.isTransactionComplete() )
   {
      _numBytes = _application.retreiveData( _storage );
      
      if(( _lpDone ) && ( _billingDone ))
      {
         if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " prot **all done** " << endl;
         }
         
         _finished = true;
      }
      else
      {
         if( _command == LOADPROFILE )
         {
            processLPData( _storage );
   //         _finished = true;
         }
         else
         {
            processBillingData( _storage );
            _command = LOADPROFILE;             ////just temp until I get smart
         }
      }
   }

   return( _finished );
}

//=====================================================================================================================
//all we're going to do with this guy is copy data from one box to another, we'll do the real decode work on scanner
//=====================================================================================================================

void CtiProtocolTransdata::processBillingData( BYTE *data )
{
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ~~~~~~~~~prot processBilling" << endl;
   }
   
   memcpy( _billingBytes, _storage, _numBytes );
   _billingDone = true;
}

//=====================================================================================================================
//we want to sort out and squish together the raw data into a form suitable for storing until we're done-done with
//our communication loop.  The data will get stuck into a Dispatch message after that
//=====================================================================================================================

void CtiProtocolTransdata::processLPData( BYTE *data )
{
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ~~~~~~~~~prot processLP" << endl;
   }

   memcpy( _lpBytes, _storage, _numBytes );

   _lpDone = true;
}

//=====================================================================================================================
//this is the guy that builds Dispatch messages out of LoadProfile pre-processed data after the comm loop
//=====================================================================================================================
/*
void CtiProtocolTransdata::processDispatchReturnMessage( CtiConnection &conn )
{
   CtiTransdataTracker::mark_v_lp   *lp = NULL;
   CtiMultiMsg                      *msgMulti = new CtiMultiMsg;
   CtiPointDataMsg                  *pData = NULL;
   CtiPointBase                     *pPoint = NULL;
   int                              index;
   int                              numEnabledChannels = 0;

   lp = ( CtiTransdataTracker::mark_v_lp *)_storage;

   RWTime mTime( lp->meterTime );

   for( index = 0; index < 8; index++ )
   {
      if( lp->enabledChannels[index] )
         numEnabledChannels++;
   }

   //
   //the meter hands us the lp data in order of youngest to oldest
   //
   for( index = 0; index < lp->numLpRecs; index += numEnabledChannels )
   {
      for( int x = 7; x >= 0; x-- )
      {
         if( lp->enabledChannels[x] )
         {
            pPoint = getDevicePointOffsetTypeEqual( CH1_OFFSET + LOAD_PROFILE, AnalogPointType );

            if( pPoint != NULL )
            {
               pData->setID( pPoint->getID() );
               pData->setValue( ( DOUBLE)lp->lpData[index] );
               pData->setQuality( NormalQuality );             //just for now
               pData->setTags( TAG_POINT_LOAD_PROFILE_DATA );
               pData->setMessageTime( mTime );

               index += 2; //lp data is 2 bytes per
            }

            msgMulti->getData().insert( pData );
         }
      }

      //decrement the time to the interval previous to the current one...
      mTime -= lp->lpFormat[0] * 60; 
   }
}
*/
//=====================================================================================================================
//=====================================================================================================================

int CtiProtocolTransdata::recvOutbound( OUTMESS *OutMessage )
{
   mkv *ptr = NULL;

   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " STAND BACK! DECODING MESSAGE!" << endl;
   }

   _application.setLastLPTime( OutMessage->Buffer.DUPReq.LP_Time );

   ptr = ( mkv *)OutMessage->Buffer.OutMessage;
   
   setCommand( ptr->command, ptr->getLP );

   return( 1 );
}

//=====================================================================================================================
//=====================================================================================================================

int CtiProtocolTransdata::sendCommResult( INMESS *InMessage )
{
   memcpy( InMessage->Buffer.InMessage, _billingBytes, _numBytes );
   InMessage->InLength = _numBytes;
   
   _numBytes = 0;

   return( NORMAL );
}

//=====================================================================================================================
//we're going to take the raw data that we got back from porter and bust it up into messages and stick them into
//a vector, pass them back up to the device, and we're done from here down...
//=====================================================================================================================

vector<CtiTransdataData *> CtiProtocolTransdata::resultDecode( INMESS *InMessage )
{
   CtiTransdataData           *converted;
   BYTE                       *ptr;
   vector<CtiTransdataData *> transVector;

   ptr = ( unsigned char*)( InMessage->Buffer.InMessage );

   while( *ptr != NULL )
   {
      converted = new CtiTransdataData( ptr );

      // Do we need to NULL the converted ptr??? 

      transVector.push_back( converted );

      if( ptr != NULL )
      {
         ptr = ( unsigned char*)strchr(( const char*)ptr, '\n' );
         ptr++;
      }
   }

   return( transVector );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolTransdata::isTransactionComplete( void )
{
   return( _finished );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolTransdata::injectData( RWCString str )
{
   _application.injectData( str );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolTransdata::reinitalize( void )
{
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " prot reinit" << endl;
   }

   _command = 0;
//   _lastLPTime = 0;
   
   _application.reinitalize();
   
   _collectLP = false;
   _finished = false;
   _billingDone = false;
   _lpDone = false;

   _storage = new BYTE[4000];
   _lpBytes = new BYTE[4000];
   _billingBytes = new BYTE[1200];
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolTransdata::destroy( void )
{
   _application.destroy();

   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " prot DESTROY! DESTROY! DESTROY! DESTROY! DESTROY!" << endl;
   }

   if( _storage )
   {
      delete [] _storage;
      _storage = NULL;
   }
   
   if( _billingBytes )
   {
      delete [] _billingBytes;
      _billingBytes = NULL;
   }
   
   if( _lpBytes )
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

int CtiProtocolTransdata::retreiveData( BYTE *data )
{
   int temp = _numBytes;

   memcpy( data, _lpBytes, _numBytes );

   return( temp );
}


































/* before alteration to try to get 2 seperate commands to run 11/29/03
bool CtiProtocolTransdata::decode( CtiXfer &xfer, int status )
{
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " prot decode" << endl;
   }

   _application.decode( xfer, status );

   if( _application.isTransactionComplete() )
   {
      _numBytes = _application.retreiveData( _storage );

      if( _command == LOADPROFILE )
         processLPData( _storage );
      else
         processBillingData( _storage );

      _finished = true;
   }

   return( _finished );
}
*/
