
#pragma warning( disable : 4786)
#ifndef __PROT_TRANSDATA_H__
#define __PROT_TRANSDATA_H__

/*---------------------------------------------------------------------------------*
*
* File:   prot_transdata
*
* Class:
* Date:   7/16/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2004/02/09 16:50:17 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include <windows.h>
#include <rw\cstring.h>

#include "transdata_application.h"
#include "transdata_data.h"
#include "xfer.h"

class IM_EX_PROT CtiProtocolTransdata
{
   public:
      
      struct mkv
      {
         int   command;
         bool  getLP;
      };

      struct llp
      {
         ULONG    lastLP;
      };

      CtiProtocolTransdata();
      ~CtiProtocolTransdata();
      
      bool generate( CtiXfer &xfer );
      bool decode( CtiXfer &xfer, int status );

      int sendCommResult( INMESS *InMessage );
      int recvOutbound( OUTMESS *OutMessage );

      bool isTransactionComplete( void );
      void injectData( RWCString str );
      void reinitalize( void );
      void destroy( void );
      void setCommand( int cmd, bool lp );
      int getCommand( void );
      bool getAction( void );
      void processLPData( BYTE *data );
      void processBillingData( BYTE *data );
      int retreiveData( BYTE *data );
      void setError( int err );
      int getError( void );
      bool getDidProcess( void );

      vector<CtiTransdataData *> resultDecode( INMESS *InMessage );

   protected:

   private:

      enum Sizes
      {
         Billing_size      = 1200,
         Storage_size      = 50000,
         Loadprofile_size  = 50000
      };

      enum Errors
      {
         Working     = 0,
         Failed
      };

      bool                       _finished;
      bool                       _collectLP;
      bool                       _billingDone;
      bool                       _lpDone;
      bool                       _reallyDidProcessLP;

      BYTE                       *_storage;
      BYTE                       *_billingBytes;
      BYTE                       *_lpBytes;
      
      int                        _numBytes;
      int                        _command;
      int                        _error;
      int                        _numBilling;
      int                        _numLoadProfile;

      CtiTransdataApplication    _application;
};
#endif // #ifndef __PROT_TRANSDATA_H__
