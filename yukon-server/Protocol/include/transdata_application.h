
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
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2003/12/16 17:23:04 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include <vector>

#include "xfer.h"
#include "utility.h"
#include "transdata_tracker.h"
#include "transdata_data.h"

#define GENERAL      0
#define LOADPROFILE  1

class IM_EX_PROT CtiTransdataApplication
{
   public:

      CtiTransdataApplication();
      ~CtiTransdataApplication();

      bool generate( CtiXfer &xfer );
      bool decode( CtiXfer &xfer, int status );
      bool isTransactionComplete( void );
      void injectData( RWCString str );
      void setNextState( void );
      int getError( void );
      void destroy( void );
      void reinitalize( void );
      int retreiveData( BYTE *data );
      void setCommand( int cmd, bool lp );
      void setLastLPTime( ULONG lpTime );

   protected:

   private:
      enum
      {
         doLogOn = 0,
         doTalk,
         doLogOff
      };

      enum
      {
         doBilling = 0,
         doLoadProfile
      };

      enum
      {
         Storage_size      = 4500
      };

      CtiTransdataTracker  _tracker;

      int                  _lastState;
      int                  _numBytes;
      int                  _command;

      bool                 _finished;
      bool                 _connected;
      bool                 _getLoadProfile;
      BYTE                 *_storage;
};

#endif // #ifndef __TRANSDATA_APPLICATION_H__

