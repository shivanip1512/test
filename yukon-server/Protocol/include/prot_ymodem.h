
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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/08/28 14:22:56 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include <windows.h>
#include <rw\cstring.h>
#include "xfer.h"

#define  SOH      0x01
#define  STX      0x02
//#define  START    0x43

class IM_EX_PROT CtiProtocolYmodem
{
   public:

      CtiProtocolYmodem();
      virtual ~CtiProtocolYmodem();

      bool generate( CtiXfer &xfer );
      bool decode( CtiXfer &xfer, int status );

      bool isTransactionComplete( void );

//      unsigned short crc16( unsigned char octet, unsigned short crc );
//      unsigned short crc( int size, unsigned char *packet );

      INT checkCRC( BYTE *InBuffer, ULONG InCount );
      USHORT addCRC(UCHAR* buffer, LONG length, BOOL bAdd);



   protected:

   private:


};

#endif // #ifndef __PROT_YMODEM_H__
