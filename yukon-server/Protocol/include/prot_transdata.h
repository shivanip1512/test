#pragma once

#include <windows.h>

#include "transdata_application.h"
#include "transdata_data.h"
#include "xfer.h"
#include "dllbase.h"

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
      virtual ~CtiProtocolTransdata();

      bool generate( CtiXfer &xfer );
      bool decode( CtiXfer &xfer, int status );

      YukonError_t sendCommResult( INMESS &InMessage );
      int recvOutbound( OUTMESS *OutMessage );

      bool isTransactionComplete( void ) const;
      void injectData( std::string str );
      void reinitalize( void );
      void destroy( void );
      void setCommand( int cmd, bool lp );
      int getCommand( void );
      bool getAction( void );
      void processLPData( BYTE *data );
      void processBillingData( BYTE *data );
      int retrieveData( BYTE *data );
      void setError( int err );
      int getError( void );
      bool getDidProcess( void );
      CtiTime getLastLoadProfileTime( void );

      std::vector<CtiTransdataData *> resultDecode( const INMESS &InMessage );

private:

      enum Sizes
      {
         Billing_size      = 1200,
         Storage_size      = 50000,
         Loadprofile_size  = 50000
      };

      enum TransdataErrors
      {
         Working     = 0,
         Failed
      };

      CtiTime                     _lastLPTime;

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
