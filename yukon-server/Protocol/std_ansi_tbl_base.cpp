#include "yukon.h"


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
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/02/10 23:23:57 $
*    History: 
      $Log: std_ansi_tbl_base.cpp,v $
      Revision 1.7  2005/02/10 23:23:57  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.6  2005/01/25 18:33:51  jrichter
      added present value tables for kv2 and sentinel for voltage, current, freq, pf, etc..meter info

      Revision 1.5  2005/01/03 23:07:14  jrichter
      checking into 3.1, for use at columbia to test sentinel

      Revision 1.4  2004/12/10 21:58:41  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.3  2004/09/30 21:37:17  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.2  2004/04/22 21:12:53  dsutton
      Last known revision DLS

      Revision 1.1  2003/04/25 14:52:43  dsutton
      Standard and manufacturer table base class. Contains utility functions
      needed for all tables


* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
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
//
// NOTE:  Data order from standard table 0 will need to be used in final definition  !!!!
//
//=========================================================================================================================================

int CtiAnsiTableBase::toDoubleParser( BYTE *source, double &result, int format )
{
   BYTEFLOAT32  float32;
   BYTEFLOAT64 flipFloat;
   long tempLong;
   double   tempDbl;
   double   multer = 1;
   int      offset = 0;

   switch( format )
   {
   case ANSI_NI_FORMAT_FLOAT64:
      //float64
       {
           if (1) //data order LSB
           {    
               flipFloat.ch[7] = source[7];
               flipFloat.ch[6] = source[6];
               flipFloat.ch[5] = source[5];
               flipFloat.ch[4] = source[4];
               flipFloat.ch[3] = source[3];
               flipFloat.ch[2] = source[2];
               flipFloat.ch[1] = source[1];
               flipFloat.ch[0] = source[0];

               tempDbl = flipFloat.u64;


           }
           else
           {

               flipFloat.ch[7] = source[0];
               flipFloat.ch[6] = source[1];
               flipFloat.ch[5] = source[2];
               flipFloat.ch[4] = source[3];
               flipFloat.ch[3] = source[4];
               flipFloat.ch[2] = source[5];
               flipFloat.ch[1] = source[6];
               flipFloat.ch[0] = source[7];

               tempDbl = flipFloat.u64;
           }
           result = tempDbl;
           offset = sizeof( unsigned char )*8;
       }
      break;

   case ANSI_NI_FORMAT_FLOAT32:
       //float32
       {

            if (1) //data order LSB
            {    
               float32.ch[3] = source[3];
               float32.ch[2] = source[2];
               float32.ch[1] = source[1];
               float32.ch[0] = source[0];
            }
            else
            {
               float32.ch[3] = source[0];
               float32.ch[2] = source[1];
               float32.ch[1] = source[2];
               float32.ch[0] = source[3];
            }

           //memcpy( &tempLong, source, sizeof( long ));
           result = (double)float32.u32;
           offset = sizeof( unsigned char ) * 4;
           

       }
       break;

   case ANSI_NI_FORMAT_ARRAY12_CHAR:
      //array-12 char
      break;

   case ANSI_NI_FORMAT_ARRAY6_CHAR:
      //array-6 char
      break;

   case ANSI_NI_FORMAT_INT32_IMPLIED:
      //int32 w/implied dec. pt. 4<->5th digits
      break;

   case ANSI_NI_FORMAT_ARRAY6_BCD:
      //array-6-bcd
      break;

   case ANSI_NI_FORMAT_ARRAY4_BCD:
      //array-4-bcd
      break;

   case ANSI_NI_FORMAT_INT24:
      //int24
      break;

   case ANSI_NI_FORMAT_INT32:
      //int32
      memcpy( &tempLong, source, sizeof( long ));
      result = tempLong;
      offset = sizeof( long );
      break;

   case ANSI_NI_FORMAT_INT40:
      //int40  
       {
           if (1) 
           {
               tempDbl = ((double) source[5] * (0x10000000000)) +
                         ((double) source[4] * (0x100000000)) +
                         ((double) source[3] * (0x1000000)) +
                         ((double) source[2] * (0x10000)) +
                         ((double) source[1] * (0x100)) +
                         (double) source[0];
           }
           else
           {
               tempDbl = ((double)source[0] * (0x10000000000)) +
                         ((double)source[1] * (0x100000000)) +
                         ((double)source[2] * (0x1000000)) +
                         ((double)source[3] * (0x10000)) +
                         ((double)source[4] * (0x100)) +
                         (double)source[5];
           }
           result = tempDbl;
           offset = sizeof( unsigned char )*5;
       }

       break;

   case ANSI_NI_FORMAT_INT48:
      {
          //NOTE: need to figure out if this order is correct
          //int48
          //tempDbl = source[0] * multer;
               
           if (1) 
           {
               tempDbl = ((double) source[5] * (0x10000000000)) +
                         ((double) source[4] * (0x100000000)) +
                         ((double) source[3] * (0x1000000)) +
                         ((double) source[2] * (0x10000)) +
                         ((double) source[1] * (0x100)) +
                         (double) source[0];
           }
           else
           {
               tempDbl = ((double)source[0] * (0x10000000000)) +
                         ((double)source[1] * (0x100000000)) +
                         ((double)source[2] * (0x1000000)) +
                         ((double)source[3] * (0x10000)) +
                         ((double)source[4] * (0x100)) +
                         (double)source[5];
           }
           result = tempDbl;
           offset = sizeof( unsigned char )*6;
      }
      break;

   case ANSI_NI_FORMAT_INT64:
      //int64
      break;

   case ANSI_NI_FORMAT_ARRAY8_BCD:
      //array-8-bcd
      break;

   case ANSI_NI_FORMAT_ARRAY21_CHAR:
      //array-21-char
      break;

   }
   return( offset );
}


//=========================================================================================================================================
//this guy converts raw bytes from the meter to doubles so we have a set size to work with
//all the tables have access to him
//
// NOTE:  Data order from standard table 0 will need to be used in final definition  !!!!
//
//=========================================================================================================================================

int CtiAnsiTableBase::fromDoubleParser( double &source, BYTE *result, int format )
{
   BYTEFLOAT32  float32;
   long tempLong;
   double   tempDbl;
   BYTEFLOAT64 flipFloat;
   //double   resultDbl;
   double   multer = 1;
   int      offset = 0;

   switch( format )
   {
   case ANSI_NI_FORMAT_FLOAT64:
       {   //float64
       
           tempDbl = source;
           if (1) 
           {    
               flipFloat.u64 = tempDbl;
               result[0] = flipFloat.ch[0];
               result[1] = flipFloat.ch[1];
               result[2] = flipFloat.ch[2];
               result[3] = flipFloat.ch[3];
               result[4] = flipFloat.ch[4];
               result[5] = flipFloat.ch[5];
               result[6] = flipFloat.ch[6];
               result[7] = flipFloat.ch[7];

              
           }
           else
           {

               flipFloat.u64 = tempDbl;
               result[0] = flipFloat.ch[7];
               result[1] = flipFloat.ch[6];
               result[2] = flipFloat.ch[5];
               result[3] = flipFloat.ch[4];
               result[4] = flipFloat.ch[3];
               result[5] = flipFloat.ch[2];
               result[6] = flipFloat.ch[1];
               result[7] = flipFloat.ch[0];
           }
           offset = sizeof( unsigned char )*8;
      }
      break;

   case ANSI_NI_FORMAT_FLOAT32:
      //float32
       float32.u32 = source;
       if (1) 
       {
           result[0] = float32.ch[0];
           result[1] = float32.ch[1];
           result[2] = float32.ch[2];
           result[3] = float32.ch[3];
       }
       else
       {
           result[0] = float32.ch[3];
           result[1] = float32.ch[2];
           result[2] = float32.ch[1];
           result[3] = float32.ch[0];
       }
       offset = sizeof( unsigned char )*4;

      break;

   case ANSI_NI_FORMAT_ARRAY12_CHAR:
      //array-12 char
      break;

   case ANSI_NI_FORMAT_ARRAY6_CHAR:
      //array-6 char
      break;

   case ANSI_NI_FORMAT_INT32_IMPLIED:
      //int32 w/implied dec. pt. 4<->5th digits
      break;

   case ANSI_NI_FORMAT_ARRAY6_BCD:
      //array-6-bcd
      break;

   case ANSI_NI_FORMAT_ARRAY4_BCD:
      //array-4-bcd
      break;

   case ANSI_NI_FORMAT_INT24:
      //int24
      break;

   case ANSI_NI_FORMAT_INT32:
      //int32
       tempLong = (long)source;
       if (1)
       {
           result[3] = tempLong / 0x1000000;
           tempLong =  tempLong - (result[3] * 0x1000000);
           result[2] = tempLong / 0x10000;
           tempLong =  tempLong - (result[2] * 0x10000);
           result[1] = tempLong / 0x100;
           tempLong =  tempLong - (result[1] * 0x100);
           result[0] = tempLong;
       }
       else
       {
           result[0] = tempLong / 0x1000000;
           tempLong =  tempLong - (result[0] * 0x1000000);
           result[1] = tempLong / 0x10000;
           tempLong =  tempLong - (result[1] * 0x10000);
           result[2] = tempLong / 0x100;
           tempLong =  tempLong - (result[2] * 0x100);
           result[3] = tempLong;
       }
       offset = sizeof( long );
       break;

   case ANSI_NI_FORMAT_INT40:
      //int40
      break;

   case ANSI_NI_FORMAT_INT48:
       tempDbl = source;
       if (1) 
       {
           result[5] = tempDbl / 0x10000000000;
           tempDbl =  tempDbl - (result[5] * 0x10000000000);
           result[4] = tempDbl / 0x100000000;
           tempDbl =  tempDbl - (result[4] * 0x100000000);
           result[3] = tempDbl / 0x1000000;
           tempDbl =  tempDbl - (result[3] * 0x1000000);
           result[2] = tempDbl / 0x10000;
           tempDbl =  tempDbl - (result[2] * 0x10000);
           result[1] = tempDbl / 0x100;
           tempDbl =  tempDbl - (result[1] * 0x100);
           result[0] = tempDbl;
       }
       else
       {
           result[0] = tempDbl / 0x10000000000;
           tempDbl =  tempDbl - (result[0] * 0x10000000000);
           result[1] = tempDbl / 0x100000000;
           tempDbl =  tempDbl - (result[1] * 0x100000000);
           result[2] = tempDbl / 0x1000000;
           tempDbl =  tempDbl - (result[2] * 0x1000000);
           result[3] = tempDbl / 0x10000;
           tempDbl =  tempDbl - (result[3] * 0x10000);
           result[4] = tempDbl / 0x100;
           tempDbl =  tempDbl - (result[4] * 0x100);
           result[5] = tempDbl;
       }
       offset = sizeof( unsigned char )*6;
      break;

   case ANSI_NI_FORMAT_INT64:
      //int64
      break;

   case ANSI_NI_FORMAT_ARRAY8_BCD:
      //array-8-bcd
      break;

   case ANSI_NI_FORMAT_ARRAY21_CHAR:
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
   
   /*ULONG    year = 0;
   ULONG    month = 0;
   ULONG    day = 0;
   ULONG    hour = 0;
   ULONG    minute = 0;
   ULONG    second = 0;
   */
   unsigned       year = 0;  
       unsigned month = 0;   
       unsigned day = 0;     
       unsigned hour = 0;    
       unsigned minute = 0;  
       unsigned second = 0;  

   int      offset = 0;

   switch( format )
   {
   case 0:
       {
       }
      break;

   case 1:
      {
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
      }
      break;

   case 2:
       {
       memcpy ((void *)&year, source, sizeof (BYTE) );
       source += sizeof (BYTE);
       memcpy ((void *)&month, source, sizeof (BYTE) );
       source += sizeof (BYTE);
       memcpy ((void *)&day, source, sizeof (BYTE) );
       source += sizeof (BYTE);
       memcpy ((void *)&hour, source, sizeof (BYTE) );
       source += sizeof (BYTE);
       memcpy ((void *)&minute, source, sizeof (BYTE) );
       source += sizeof (BYTE);
       
       offset = 5;
       //result = 11;
       RWTime timeResult( RWDate( day, month, year + 2000 ), hour, minute);
       //result = RWTime( RWDate( day, month, year ), hour, minute).seconds();
       result = timeResult.seconds();
       }
      break;

   case 3:
       {
           temp = (int)*source  
               + ((int)*(source + 1)* 0x100) 
               + ((int)*(source + 2)* 0x10000) 
               + ((int)*(source + 3)* 0x1000000);

           temp = temp * 60;

           result = RWTime(temp + RWTime(RWDate(1,1,1970)).seconds() /*- 3600*/).seconds();
           offset = 4;

       }
      break;
   default:
       break;
   }

   return( offset );
}

int CtiAnsiTableBase::toTime( BYTE *source, ULONG &result, int format )
{
   ULONG    temp;
   
   unsigned year = 0;  
   unsigned month = 0;   
   unsigned day = 0;     
   unsigned hour = 0;    
   unsigned minute = 0;  
   unsigned second = 0;  

   int      offset = 0;

   switch( format )
   {
   case 0:
       {
       }
      break;

   case 1:
      {
      hour = BCDtoBase10( source + offset, 1 );
      offset += 1;

      minute = BCDtoBase10( source + offset, 1 );
      offset += 1;

      second = BCDtoBase10( source + offset, 1 );
      offset += 1;
      result = RWTime( hour, minute, second).seconds();
      }
      break;

   case 2:
       {
       memcpy ((void *)&hour, source, sizeof (BYTE) );
       source += sizeof (BYTE);
       memcpy ((void *)&minute, source, sizeof (BYTE) );
       source += sizeof (BYTE);
       memcpy ((void *)&second, source, sizeof (BYTE) );
       source += sizeof (BYTE);

       offset = 3;
       //result = 11;
       RWTime timeResult( hour, minute, second);
       //result = RWTime( RWDate( day, month, year ), hour, minute).seconds();
       result = timeResult.seconds();
       }
      break;

   case 3:
       {
           /*temp = (int)*source  
               + ((int)*(source + 1)* 0x100) 
               + ((int)*(source + 2)* 0x10000) 
               + ((int)*(source + 3)* 0x1000000);

           temp = temp * 60;
           */
           memcpy ((void *)&temp, source, sizeof (BYTE)*4 );
           source += sizeof (BYTE)*4;


           result = RWTime(temp /*- 3600*/).seconds();
           offset = 4;

       }
      break;
   default:
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

int CtiAnsiTableBase::toUint32LTime( BYTE *source, ULONG &result, int format )
{
   ULONG    temp;
   RWTime   time;
   ULONG    year = 0;
   ULONG    month = 0;
   ULONG    day = 0;
   ULONG    hour = 0;
   ULONG    minute = 0;
   ULONG    second = 0;
   int      offset = 0;

   switch( format )
   {
   case 0:
      break;

   case 1:
      /*
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
       */
      break;

   case 2:
       {
          
          memcpy ((void *)year, source, sizeof (BYTE) );
          source += sizeof (BYTE);
          memcpy ((void *)month, source, sizeof (BYTE) );
          source += sizeof (BYTE);
          memcpy ((void *)day, source, sizeof (BYTE) );
          source += sizeof (BYTE);
          memcpy ((void *)hour, source, sizeof (BYTE) );
          source += sizeof (BYTE);
          memcpy ((void *)minute, source, sizeof (BYTE) );
          source += sizeof (BYTE);
          memcpy ((void *)second, source, sizeof (BYTE) );
          source += sizeof (BYTE);
          offset = 6;
          result = RWTime( RWDate( day, month, year ), hour, minute, second).seconds();
       }
      break;

   case 3:
       {
           temp = (int)*source  
               + ((int)*(source + 1)* 0x100) 
               + ((int)*(source + 2)* 0x10000) 
               + ((int)*(source + 3)* 0x1000000);
           temp = (temp * 60) + ((int)*(source + 4));
           
           result = RWTime(temp + RWTime(RWDate(1,1,1970)).seconds() /*- 3600*/).seconds();
           
        }
      break;
   }

   return( offset );
}


