
#pragma warning( disable : 4786)
#ifndef __PROT_YMODEM_H__
#define __PROT_YMODEM_H__

/*---------------------------------------------------------------------------------*
*
* File:   prot_ymodem
*
* Class:
* Date:   8/4/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2003/12/29 21:00:40 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include <windows.h>
#include <rw\cstring.h>
#include "xfer.h"

class IM_EX_PROT CtiProtocolYmodem
{
   public:

      CtiProtocolYmodem();
      ~CtiProtocolYmodem();

//      bool generate( CtiXfer &xfer, int bytesWanted, int timeToWait );
      bool generate( CtiXfer &xfer, int reqAcks );
      bool decode( CtiXfer &xfer, int status );
      void setXfer( CtiXfer &xfer, BYTE dataOut, int bytesIn, bool block, ULONG time );

      bool isTransactionComplete( void );

      unsigned short calcCRC( BYTE *ptr, int count );
      unsigned short updateCRC( BYTE c, unsigned short crc );

      void retreiveData( BYTE *data, int *bytes );
      bool isCrcValid( void );
      void destroy( void );
      void reinitalize( void );
      void setError( void );
      int getError( void );
      void setStart( bool doSet );
      int getAcks( void );
      void setAcks( int acks );

   protected:

   private:
      
      enum Signals
      {
         //http://www.bsdg.org/swag/COMM/0084.PAS.html
         //Ahh The Rosetta Stone.

         Soh       = 0x01,
         Stx,
         Brk,
         Eot,
         Enq,
         Ack,
         Nak       = 0x15,
         Can       = 0x18,
         Crcnak    = 0x43,
         Ygnak     = 0x47,
         Zdle      = 0x18
      };

      enum Errors
      {
         Working     = 0,
         Failed
      };

      enum States
      {
         doStart,
         doAck
      };

      enum
      {
         Packet_size    = 1029,
         Storage_size   = 4500
      };

      bool        _finished;
      bool        _start;

      int         _failCount;
      int         _error;
      int         _lastState;
      int         _bytesReceived;
      int         _bytesExpected;
      int         _acks;
      int         _reqAcks;

      BYTE        *_storage;
};

#endif // #ifndef __PROT_YMODEM_H__
