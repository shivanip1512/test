#pragma once

#include "xfer.h"
#include "dllbase.h"

class IM_EX_PROT CtiProtocolYmodem : private boost::noncopyable
{
   public:

      CtiProtocolYmodem();
      virtual ~CtiProtocolYmodem();

      bool generate( CtiXfer &xfer, int packets );
      bool decode( CtiXfer &xfer, int status );

      bool isTransactionComplete( void ) const;

      void retrieveData( BYTE *data, int *bytes );
      bool isCrcValid( void );
      void destroy( void );
      void reinitalize( void );
      void setError( void );
      int  getError( void );
      void setStart( void );
      int  packetsReceived( void );

      enum Sizes
      {
          Packet_data_length = 1024
      };

   protected:

      void outputAck ( CtiXfer &xfer, BYTE ack );
      void readPacket( CtiXfer &xfer );
      void readEot( CtiXfer &xfer );

      unsigned short calcCRC( BYTE *ptr, int count );
      unsigned short updateCRC( BYTE c, unsigned short crc );

   private:

      enum Signals
      {
         Signal_SOH    = 0x01,
         Signal_STX    = 0x02,
         Signal_BRK    = 0x03,
         Signal_EOT    = 0x04,
         Signal_ENQ    = 0x05,
         Signal_ACK    = 0x06,

         Signal_NAK    = 0x15,

         Signal_CAN    = 0x18,

         Signal_CRCNAK = 0x43,

         Signal_YGNak  = 0x47,

         Signal_ZDLE   = 0x18
      };

      enum Errors
      {
         Working     = 0,
         Failed
      };

      enum States
      {
         doStart,
         doReadPacket,
         doSendPacketAck,
         doReadEOT,
         doSendEOTAck,
         doComplete
      };

      enum
      {
         Packet_size    = 1029
      };

      int    _state;
      int    _failCount;
      int    _error;
      int    _bytesReceived;
      int    _packetsReceived;
      int    _packetsExpected;

      BYTE  *_storage;
};
