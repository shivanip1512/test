
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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2003/10/30 15:02:50 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include <windows.h>
#include <rw\cstring.h>
#include "xfer.h"

class IM_EX_PROT CtiProtocolYmodem
{
   public:

      enum
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

      enum
      {
         doStart,
         doAck
      };

      CtiProtocolYmodem();
      ~CtiProtocolYmodem();

      bool generate( CtiXfer &xfer );
      bool decode( CtiXfer &xfer, int status );
      void setXfer( CtiXfer &xfer, BYTE dataOut, int bytesIn, bool block, ULONG time );

      bool isTransactionComplete( void );

      unsigned short calcCRC( BYTE *ptr, int count );
      unsigned short updateCRC( BYTE c, unsigned short crc );

      void retreiveData( BYTE *data, int *bytes );
      bool isCrcValid( void );
      void destroy( void );
      void reinitalize( void );

   protected:

   private:

      bool        _finished;
      bool        _gotData;
      int         _lastState;
      int         _bytesReceived;
      int         _bytesExpected;
      BYTE        *_storage;

};

#endif // #ifndef __PROT_YMODEM_H__
