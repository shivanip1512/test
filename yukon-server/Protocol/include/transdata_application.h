
#pragma warning( disable : 4786)
#ifndef __TRANSDATA_APPLICATION_H__
#define __TRANSDATA_APPLICATION_H__

/*---------------------------------------------------------------------------------*
*
* File:   transdata_application
*
* Class:
* Date:   7/22/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2004/02/09 16:51:37 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include <vector>

#include "xfer.h"
#include "utility.h"
#include "transdata_tracker.h"
#include "transdata_data.h"

class IM_EX_PROT CtiTransdataApplication
{
   public:
      
      enum Commands
      {
         General = 0,
         LoadProfile
      };

      CtiTransdataApplication();
      ~CtiTransdataApplication();

      bool generate( CtiXfer &xfer );
      bool decode( CtiXfer &xfer, int status );
      bool isTransactionComplete( void );
      void injectData( RWCString str );
      void setNextState( void );
      void destroy( void );
      void reinitalize( void );
      int retreiveData( BYTE *data );
      void setCommand( int cmd, bool lp );
      int getCommand( void );
      void setLastLPTime( ULONG lpTime );
      void checkRecs( void );
      bool doLoadProfile( void );
      void setError( int err );
      int getError( void );
      bool loggedOff( void );

   protected:

   private:
      
      enum Errors
      {
         Working = 0,
         Failed
      };
      
      enum States
      {
         DoLogOn = 0,
         DoTalk,
         DoLogOff
      };
      
      enum Sizes
      {
         Storage_size      = 50000
      };

      CtiTransdataTracker  _tracker;

      int                  _lastState;
      int                  _numBytes;
      int                  _command;
      int                  _error;

      bool                 _finished;
      bool                 _connected;
      bool                 _getLoadProfile;
      bool                 _checkRecs;
      bool                 _loggedOff;

      BYTE                 *_storage;
};

#endif // #ifndef __TRANSDATA_APPLICATION_H__

