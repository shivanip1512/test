
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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/09/03 18:11:55 $
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

      unsigned short calcCRC( BYTE *ptr, int count );
      unsigned short updateCRC( BYTE c, unsigned short crc );

   protected:

   private:


};

#endif // #ifndef __PROT_YMODEM_H__
