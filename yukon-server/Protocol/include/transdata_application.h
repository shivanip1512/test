
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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/08/28 14:22:57 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "xfer.h"
#include "transdata_tracker.h"

class IM_EX_PROT CtiTransdataApplication
{
   enum
   {
      doLogOn = 0,
      doTalk,
      doLogOff
   };

   public:

      CtiTransdataApplication();
      ~CtiTransdataApplication();

      bool generate( CtiXfer &xfer );
      bool decode( CtiXfer &xfer, int status );

      bool isTransactionComplete( void );
      void injectData( RWCString str );

      void setNextState( void );
      bool processData( BYTE *data );

      int getError( void );

   protected:

   private:

      CtiTransdataTracker  _tracker;

      int                  _lastState;

      bool                 _dataProcessed;
      bool                 _weHaveData;
      bool                 _finished;

      BYTE                 *_storage;
};

#endif // #ifndef __TRANSDATA_APPLICATION_H__
