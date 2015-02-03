#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_base.h"
#include "ctidate.h"
#include "ctitime.h"

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

void CtiAnsiTableBase::reverseOrder(  BYTE *source, int length )
{
    BYTE* tempArray = new BYTE[length];
    memcpy ((void *)tempArray, source, length );

    for (int x = 0; x < length; x++)
    {
        source[x] = tempArray[length - (x + 1)];
    }
    delete []tempArray;
}

//=========================================================================================================================================
//this guy converts raw bytes from the meter to doubles so we have a set size to work with
//all the tables have access to him
//
// NOTE:  Data order from standard table 0 will need to be used in final definition  !!!!
//
//=========================================================================================================================================

int CtiAnsiTableBase::toDoubleParser( BYTE *source, double &result, int format,  DataOrder dataOrder )
{
   BYTEFLOAT32  float32;
   BYTEFLOAT64 flipFloat;
   long tempLong;
   double   tempDbl;
   int      offset = 0;

   switch( format )
   {
       case ANSI_NI_FORMAT_FLOAT64:
           //float64
           {
               offset = sizeof( unsigned char ) * 8;
               if (dataOrder == MSB) 
               {
                   reverseOrder(  source, offset );
               }
               flipFloat.ch[7] = source[7];
               flipFloat.ch[6] = source[6];
               flipFloat.ch[5] = source[5];
               flipFloat.ch[4] = source[4];
               flipFloat.ch[3] = source[3];
               flipFloat.ch[2] = source[2];
               flipFloat.ch[1] = source[1];
               flipFloat.ch[0] = source[0];

               result = flipFloat.u64;
           }
           break;

       case ANSI_NI_FORMAT_FLOAT32:
           //float32
           {
               offset = sizeof( unsigned char ) * 4;
               if (dataOrder == MSB) 
               {
                   reverseOrder(  source, offset );
               }
               
               float32.ch[3] = source[3];
               float32.ch[2] = source[2];
               float32.ch[1] = source[1];
               float32.ch[0] = source[0];

               result = float32.u32;
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
           {
               //int24
               offset = sizeof( unsigned char ) * 3;
               if (dataOrder == MSB) 
               {
                   reverseOrder(  source, offset );
               }
               tempLong = ((long) source[2] * (0x10000)) +
                          ((long) source[1] * (0x100)) +
                           (long) source[0];
               
               result = tempLong;
           }
          break;

       case ANSI_NI_FORMAT_INT32:
          {
              //int32
               offset = sizeof( long );
               if (dataOrder == MSB) 
               {
                   reverseOrder(  source, offset );
               }
               tempLong = ((long) source[3] * (0x1000000)) +
                          ((long) source[2] * (0x10000)) +
                          ((long) source[1] * (0x100)) +
                           (long) source[0];
               
               result = tempLong;
          }
          break;

       case ANSI_NI_FORMAT_INT40:
           {
               //int40
               offset = sizeof( unsigned char ) * 5;
               if (dataOrder == MSB) 
               {
                   reverseOrder(  source, offset );
               }
               tempDbl = ((double) source[5] * (0x10000000000)) +
                         ((double) source[4] * (0x100000000)) +
                         ((double) source[3] * (0x1000000)) +
                         ((double) source[2] * (0x10000)) +
                         ((double) source[1] * (0x100)) +
                         (double) source[0];
               
               result = tempDbl;
           }
           break;

       case ANSI_NI_FORMAT_INT48:
          {
              //int48
               offset = sizeof( unsigned char ) * 6;
               if (dataOrder == MSB) 
               {
                   reverseOrder(  source, offset );
               }
               BYTEINT48 byteInt48; 
               byteInt48.ch[7] = source[5];
               byteInt48.ch[6] = source[4];
               byteInt48.ch[5] = source[3];
               byteInt48.ch[4] = source[2];
               byteInt48.ch[3] = source[1];
               byteInt48.ch[2] = source[0];
               byteInt48.ch[1] = 0;
               byteInt48.ch[0] = 0;

               result = byteInt48.int48 >> 16;
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


int CtiAnsiTableBase::toAnsiIntParser( BYTE *source, void *result, size_t length,  DataOrder dataOrder )
{
     if (dataOrder == MSB)
     {
         reverseOrder(source, length);
     }
     memcpy( result, source, length);
     return length;

}
//=========================================================================================================================================
//an STIME_DATE converter to UINT32ness
//=========================================================================================================================================

int CtiAnsiTableBase::toUint32STime( BYTE *source, ULONG &result, int format )
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
          result = 0;
          offset = 0;
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
      result = CtiTime( CtiDate( day, month, year )).seconds();
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
       CtiTime timeResult( CtiDate( day, month, year + 2000), hour, minute);
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

           result = CtiTime(temp).seconds();
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
           result = CtiTime( second ).seconds();
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
      result = CtiTime( hour, minute, second).seconds();
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
       CtiTime timeResult( hour, minute, second);
       //result = CtiTime( CtiDate( day, month, year ), hour, minute).seconds();
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


           result = CtiTime(temp /*- 3600*/).seconds();
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
   CtiTime   time;
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

      {
          result = 0;
          offset = 0;
      }
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

      result = CtiTime( CtiDate( day, month, year )).seconds();
       */
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
          memcpy ((void *)&second, source, sizeof (BYTE) );
          source += sizeof (BYTE);
          offset = 6;
          result = CtiTime( CtiDate( day, month, year + 2000 ), hour, minute, second).seconds();
       }
      break;

   case 3:
       {
           temp = (int)*source
               + ((int)*(source + 1)* 0x100)
               + ((int)*(source + 2)* 0x10000)
               + ((int)*(source + 3)* 0x1000000);
           temp = (temp * 60) + ((int)*(source + 4));

           offset = 5;

           result = CtiTime(temp).seconds();

        }
      break;
   }

   return( offset );
}

namespace {
Cti::StringFormatter fieldNameFormatter = Cti::StringFormatter().width(40);
}

std::string CtiAnsiTableBase::formatTableName(const std::string &name)
{
    std::string tableName;

    tableName += "=================== " + name + " ===================";

    return tableName;
}
