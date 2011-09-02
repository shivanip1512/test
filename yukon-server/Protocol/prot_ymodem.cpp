#include "precompiled.h"
#include "guard.h"
#include "logger.h"
#include "prot_ymodem.h"

using std::endl;

//=====================================================================================================================
//=====================================================================================================================

CtiProtocolYmodem::CtiProtocolYmodem():
   _storage( NULL )
{
   reinitalize();
}

//=====================================================================================================================
//=====================================================================================================================

CtiProtocolYmodem::~CtiProtocolYmodem()
{
   destroy();
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::destroy( void )
{
   if( _storage != NULL )
   {
      delete [] _storage;
      _storage = NULL;
   }
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::reinitalize( void )
{
   _error         = Working;
   _failCount     = 0;
   _bytesReceived = 0;
   _packetsReceived = 0;
   _packetsExpected = 0;

   setStart();

   if( _storage != NULL )
   {
      delete [] _storage;
   }

   _storage = CTIDBG_new BYTE[Packet_size];
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolYmodem::generate( CtiXfer &xfer, int packetsExpected )
{
   switch( _state )
   {
      case doStart:
      {
         _packetsExpected = packetsExpected;  //  when we start the protocol, set how many packets we expect
         _packetsReceived = 0;

         //  start the exchange
         outputAck( xfer, Signal_CRCNAK );
         break;
      }
      case doReadPacket:
      {
         readPacket( xfer );
         break;
      }
      case doReadEOT:
      {
         readEot( xfer );
         break;
      }
      case doSendPacketAck:
      case doSendEOTAck:
      {
         outputAck( xfer, Signal_ACK );
         break;
      }
      default:
      {
         setError();
         break;
      }
   }

   return( false );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolYmodem::decode( CtiXfer &xfer, int status )
{
   switch( _state )
   {
      case doStart:
      case doSendPacketAck:
      case doSendEOTAck:
      {
         _state++;
         break;
      }
      case doReadPacket:
      {
         if( xfer.getInCountActual() < Packet_size )
         {
            setError();
         }
         else
         {
            unsigned char *buf = xfer.getInBuffer();

            if( buf[0] == Signal_STX &&
                buf[1] == (_packetsReceived + 1) &&
                buf[2] == (0xff - _packetsReceived - 1) )
            {
               _packetsReceived++;
               memcpy( _storage, xfer.getInBuffer(), xfer.getInCountActual() );
               _bytesReceived = xfer.getInCountActual();

               _state++;
            }
            else if( buf[0] == Signal_STX &&
                     buf[1] == (_packetsReceived) &&
                     buf[2] == (0xff - _packetsReceived) )
            {
               //  don't pull in the data - this will make isCrcValid() return false, and we'll just cruise
               //    over this repeated packet...  also, we're still (re-)sending the ack like we should
               {
                  CtiLockGuard<CtiLogger> doubt_guard(dout);
                  dout << CtiTime() << " **** Checkpoint - duplicate YMODEM packet received **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
               }

               _state++;
            }
            else
            {
               if( getDebugLevel() & DEBUGLEVEL_ACTIVITY_INFO )
               {
                  CtiLockGuard<CtiLogger> doubt_guard(dout);
                  dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
               }

               setError();
            }
         }
         break;
      }
      case doReadEOT:
      {
         if( xfer.getInCountActual() >= 1 &&
             xfer.getInBuffer()[0] == Signal_EOT )
         {
            _state++;
         }
         else
         {
            setError();
         }
         break;
      }
      default:
      {
         setError();
         break;
      }
   }

   return( false );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolYmodem::isTransactionComplete( void ) const
{
   bool complete = false;

   if( _packetsReceived >= _packetsExpected )
   {
      //  if we've received and ACKed the last expected packet, we need to receive and ACK the EOT char at the end
      complete = _state > doSendEOTAck;
   }
   else
   {
      //  otherwise, we just need to send the ACK after receiving the packet
      complete = _state > doSendPacketAck;
   }

   return complete || (_error == Failed);
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::retreiveData( BYTE *data, int *bytes )
{
   if( _storage != NULL )
   {
      //do the 'front & end shaving' here instead of in tracker....
      memcpy( data, _storage + 3, _bytesReceived - 5 );
      *bytes = _bytesReceived - 5;

      memset( _storage, 0, Packet_size );

      _bytesReceived = 0;

      //  the only place besides setStart() that initializes our state
      //  gets us ready to read the next packet
      _state = doReadPacket;
   }
}

//=====================================================================================================================
// from the XMODEM/YMODEM Protocol Reference
// www.techfest.com
//=====================================================================================================================

unsigned short CtiProtocolYmodem::updateCRC( BYTE c, unsigned short crc )
{
   int            count;
   unsigned short data = c;

   for( count = 8; --count >= 0; )
   {
      if( crc & 0x8000 )
      {
         crc <<= 1;
         crc += (( ( data <<= 1 ) & 0400 ) != 0 );
         crc ^= 0x1021;
      }
      else
      {
         crc <<= 1;
         crc += (( ( data <<= 1 ) & 0400 ) != 0 );
      }
   }

   return crc;
}

//=====================================================================================================================
//=====================================================================================================================

unsigned short CtiProtocolYmodem::calcCRC( BYTE *ptr, int count )
{
   int            index = 0;
   unsigned short crc   = 0;

   for( index; index < count; index++, ptr++ )
   {
      crc = updateCRC( *ptr, crc );
   }

   return( crc );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolYmodem::isCrcValid( void )
{
   BYTEUSHORT  crc;
   bool        isOk = false;
   BYTE        temp[3000];

   if( _bytesReceived > 1020 )
   {
      memset( temp, 0, sizeof( temp ) );
      memcpy( temp, ( void *)_storage, _bytesReceived - 2 );

      crc.ch[0] = _storage[_bytesReceived - 1];
      crc.ch[1] = _storage[_bytesReceived - 2];

      if( crc.sh == calcCRC( temp + 3, _bytesReceived - 3 ))    //fixme.. should not use hardcoded stuff if pos.
      {
         isOk = true;
      }
   }

   return( isOk );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::outputAck( CtiXfer &xfer, BYTE ack )
{
   xfer.getOutBuffer()[0] = ack;
   xfer.setMessageStart(true);
   xfer.setOutCount(1);
   xfer.setInCountExpected(0);
   xfer.setInTimeout(0);
   xfer.setNonBlockingReads(false);
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::readPacket( CtiXfer &xfer )
{
   xfer.setMessageStart(true);
   xfer.setOutCount(0);
   xfer.setInCountExpected(Packet_size);
   xfer.setInTimeout(0);
   xfer.setNonBlockingReads(false);
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::readEot( CtiXfer &xfer )
{
   xfer.setMessageStart(true);
   xfer.setOutCount(0);
   xfer.setInCountExpected(1);
   xfer.setInTimeout(0);
   xfer.setNonBlockingReads(false);
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::setError( void )
{
   if( ++_failCount > 1 )
      _error = Failed;
   else
      _error = Working;
}

//=====================================================================================================================
//=====================================================================================================================

int CtiProtocolYmodem::getError( void )
{
   return( _error );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::setStart( void )
{
   _state = doStart;
}

//=====================================================================================================================
//=====================================================================================================================

int CtiProtocolYmodem::packetsReceived( void )
{
   return _packetsReceived;
}

