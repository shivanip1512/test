
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   prot_ymodem
*
* Date:   8/4/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/08/28 14:22:57 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*
*  NOTE: this is just a shell of the ymodem protocol and only incorporates what
         the transdata mk-V meter needs.  if you need more, you'll have to fill in
         some more
*-----------------------------------------------------------------------------*/

#include <rw/cstring.h>

#include "guard.h"
#include "logger.h"
#include "prot_ymodem.h"

//=====================================================================================================================
//=====================================================================================================================

CtiProtocolYmodem::CtiProtocolYmodem()
{
}

//=====================================================================================================================
//=====================================================================================================================

CtiProtocolYmodem::~CtiProtocolYmodem()
{
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolYmodem::generate( CtiXfer &xfer )
{
   return( false );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolYmodem::decode( CtiXfer &xfer, int status )
{
   return( false );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolYmodem::isTransactionComplete( void )
{
   return( false );
}

//=====================================================================================================================
//=====================================================================================================================

INT CtiProtocolYmodem::checkCRC( BYTE *InBuffer, ULONG InCount )
{
   BYTEUSHORT  crc;
   INT         retval = NORMAL;

   if(InCount > 3)
   {
      crc.sh = addCRC( InBuffer, InCount - 2, FALSE );

      if( crc.ch[0] == InBuffer[InCount - 1] && crc.ch[1] == InBuffer[InCount - 2] )
      {
         retval = NORMAL;
      }
      else
      {
         retval = !NORMAL;
      }
   }

   return retval;
}

//=====================================================================================================================
//=====================================================================================================================

USHORT CtiProtocolYmodem::addCRC( UCHAR* buffer, LONG length, BOOL bAdd )
{
   ULONG       i,j;
   BYTEUSHORT   CRC;

   BYTE CRCMSB = 0xff;
   BYTE CRCLSB = 0xff;
   BYTE Temp;
   BYTE Acc;

   CRC.sh = 0;

   if( length > 0 )
   {
      for( i = 0; i < (ULONG)length; i++ )
      {
         CRC.ch[1] ^= buffer[i];

         for( j = 0; j < 8; j++ )
         {
            if(CRC.sh & 0x8000)
            {
               CRC.sh = CRC.sh << 1;
               CRC.sh ^= 0x1021;
            }
            else
            {
               CRC.sh = CRC.sh << 1;
            }
         }
      }

      if( bAdd )
      {
         buffer[length]     = CRC.ch[1];
         buffer[length + 1] = CRC.ch[0];
      }

   }

   return CRC.sh;
}











/*
//=========================================================================================================================================
//stolen from the ansi stuff, not sure yet if the calculation is correct
//=========================================================================================================================================

unsigned short CtiProtocolYmodem::crc16( unsigned char octet, unsigned short crc )
{
   int i;

   for( i = 8; i; i-- )
   {
      if( crc & 0x0001 )
      {
         crc >>= 1;

         if( octet & 0x01 )
            crc |= 0x8000;

         crc = crc^0x8408;
         octet >>= 1;
      }
      else
      {
         crc >>= 1;

         if( octet & 0x01 )
            crc |= 0x8000;

         octet >>= 1;
      }
   }
   return crc;
}

//=========================================================================================================================================
//=========================================================================================================================================

unsigned short CtiProtocolYmodem::crc( int size, unsigned char *packet )
{
   int i;
   unsigned short crc;

   crc = ( ~packet[1] << 8 ) | ( ~packet[0] & 0xff );

   for( i = 2; i < size; i++ )
      crc = crc16( packet[i], crc );

   crc = crc16( 0x00, crc );
   crc = crc16( 0x00, crc );

   crc = ~crc;
   crc = crc >> 8 | crc << 8;

   return crc;
}

//=====================================================================================================================
//=====================================================================================================================

int calcrc(ptr, count)
{
   char  *ptr;
   int   count;
   int crc, i;

   crc = 0;

   while( --count >= 0 )
   {
      crc = crc ^ ( int)*ptr++ << 8;
      for( i = 0; i < 8; ++i )
         if( crc & 0x8000 )
            crc = crc << 1 ^ 0x1021;
         else
            crc = crc << 1;
   }
   return(crc & 0xFFFF);
}
*/

