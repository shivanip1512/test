#pragma once

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
      void injectData( std::string str );
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
