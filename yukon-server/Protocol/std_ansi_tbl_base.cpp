
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_base
*
* Date:   12/4/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_base.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/04/25 14:52:43 $
*    History: 
      $Log: std_ansi_tbl_base.cpp,v $
      Revision 1.1  2003/04/25 14:52:43  dsutton
      Standard and manufacturer table base class. Contains utility functions
      needed for all tables


* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "std_ansi_tbl_base.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableBase::CtiAnsiTableBase()
{
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableBase::~CtiAnsiTableBase()
{
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableBase& CtiAnsiTableBase::operator=(const CtiAnsiTableBase& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//this guy converts raw bytes from the meter to doubles so we have a set size to work with
//all the tables have access to him
//=========================================================================================================================================

int CtiAnsiTableBase::toDoubleParser( BYTE *source, double &result, int format )
{
   long     tempLong;
   double   tempDbl;
   double   multer = 1;
   int      offset = 0;

   switch( format )
   {
   case 0:
      //float64
      break;

   case 1:
      //float32
      break;

   case 2:
      //array-12 char
      break;

   case 3:
      //array-6 char
      break;

   case 4:
      //int32 w/implied dec. pt. 4<->5th digits
      break;

   case 5:
      //array-6-bcd
      break;

   case 6:
      //array-4-bcd
      break;

   case 7:
      //int24
      break;

   case 8:
      //int32
      memcpy( &tempLong, source, sizeof( long ));
      result = tempLong;
      offset = sizeof( long );
      break;

   case 9:
      //int40
      break;

   case 10:
      //NOTE: need to figure out if this order is correct
      //int48
      tempDbl = source[0] * multer;
      multer += 256;

      tempDbl += source[1] * multer;
      multer += 256;

      tempDbl += source[2] * multer;
      multer += 256;

      tempDbl += source[3] * multer;
      multer += 256;

      tempDbl += source[4] * multer;
      multer += 256;

      tempDbl += source[5] * multer;

      result = tempDbl;
      offset = sizeof( unsigned char )*6;
      break;

   case 11:
      //int64
      break;

   case 12:
      //array-8-bcd
      break;

   case 13:
      //array-21-char
      break;

   }
   return( offset );
}

//=========================================================================================================================================
//an STIME_DATE converter to UINT32ness
//=========================================================================================================================================

int CtiAnsiTableBase::toUint32STime( BYTE *source, ULONG &result, int format )
{
   ULONG    temp;
   RWTime   time;
   ULONG    year;
   ULONG    month;
   ULONG    day;
   ULONG    hour;
   ULONG    minute;
   ULONG    second;
   int      offset = 0;

   switch( format )
   {
   case 0:
      break;

   case 1:

      year = BCDtoBase10( source + offset, 1 );
      offset += 1;

      month = BCDtoBase10( source + offset, 1 );
      offset += 1;

      day = BCDtoBase10( source + offset, 1 );
      offset += 1;

      hour = BCDtoBase10( source + offset, 1 );
      offset += 1;

      minute = BCDtoBase10( source + offset, 1 );
      offset += 1;

      result = RWTime( RWDate( day, month, year )).seconds();

      break;

   case 2:
      break;

   case 3:
      break;
   }

   return( offset );
}

//=========================================================================================================================================
//stole this from portsup... hehe
//=========================================================================================================================================

ULONG CtiAnsiTableBase::BCDtoBase10(UCHAR* buffer, ULONG len)
{

   int i, j;
   ULONG temp;
   ULONG scratch = 0;

   for(i = (INT)len, j = 0; i > 0; j++, i--)
   {
      temp = 0;

      /* The high nibble */
      temp += (((buffer[j] & 0xf0) >> 4)  * 10);

      /* The Low nibble */
      temp += (buffer[j] & 0x0f);

      scratch = scratch * 100 + temp ;
   }

   return scratch;
}

