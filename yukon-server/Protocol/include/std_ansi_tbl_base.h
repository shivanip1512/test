#pragma once

#include "dlldefs.h"
#include "types.h"

typedef union
{
   ULONG    num;
   char  alpha[4];
} LFLIP;

typedef union
{
   UCHAR    ch[8];
   UINT64   u64;
} BYTEUINT64;

typedef union
{
   UCHAR    ch[8];
   double   u64;
} BYTEFLOAT64;

typedef union
{
   UCHAR    ch[8];
   long long   int48;
} BYTEINT48;

typedef union
{
   UCHAR    ch[4];
   float      u32;
} BYTEFLOAT32;

typedef union
{
   UCHAR    ch[2];
   short    u16;
} BYTEUINT16;

typedef union
{
   UCHAR    ch[4];
   short    u32;
} BYTEUINT32;


typedef enum
{
    LSB,
    MSB
} DataOrder;

#define BCD                unsigned char

// non integer formats
#define ANSI_NI_FORMAT_FLOAT64          0
#define ANSI_NI_FORMAT_FLOAT32          1
#define ANSI_NI_FORMAT_ARRAY12_CHAR     2
#define ANSI_NI_FORMAT_ARRAY6_CHAR      3
#define ANSI_NI_FORMAT_INT32_IMPLIED    4
#define ANSI_NI_FORMAT_ARRAY6_BCD       5
#define ANSI_NI_FORMAT_ARRAY4_BCD       6
#define ANSI_NI_FORMAT_INT24            7
#define ANSI_NI_FORMAT_INT32            8
#define ANSI_NI_FORMAT_INT40            9
#define ANSI_NI_FORMAT_INT48            10
#define ANSI_NI_FORMAT_INT64            11
#define ANSI_NI_FORMAT_ARRAY8_BCD       12
#define ANSI_NI_FORMAT_ARRAY21_CHAR     13

#define ANSI_FALSE "False"
#define ANSI_TRUE "True"

struct STIME_DATE
{
   union CASES
   {
      struct CASE1
      {
         unsigned char   year;
         unsigned char   month;
         unsigned char   day;
         unsigned char   hour;
         unsigned char   minute;
      };

      struct CASE2
      {
         unsigned char  year;
         unsigned char  month;
         unsigned char  day;
         unsigned char  hour;
         unsigned char  minute;
      };

      struct CASE3
      {
         ULONG  d_time;
      };

      struct CASE4
      {
         ULONG  d_time;
      };
   };
};

struct LTIME_DATE
{
   union// CASES
   {
      struct CASE1
      {
         BCD      year;
         BCD      month;
         BCD      day;
         BCD      hour;
         BCD      minute;
         BCD      second;
      }c1;

      struct CASE2
      {
         unsigned char  year;
         unsigned char  month;
         unsigned char  day;
         unsigned char  hour;
         unsigned char  minute;
         unsigned char  second;
      }c2;

      struct CASE3
      {
         long           u_time;
         unsigned char  second;
      }c3;

      struct CASE4
      {
         long           u_time_sec;
      }c4;

   }cases;
};

struct TIME_DATE_QUAL_BFLD
{
   unsigned char        day_of_week          :3;
   unsigned char        dst_flag             :1;
   unsigned char        gmt_flag             :1;
   unsigned char        tm_zn_applied_flag   :1;
   unsigned char        dst_applied_flag     :1;
   unsigned char        filler               :1;
};

class IM_EX_PROT CtiAnsiTableBase
{
protected:

private:

public:

   void reverseOrder(  BYTE *source, int length );
   int toDoubleParser( BYTE *source, double &result, int format, DataOrder dataOrderLSB = LSB );
   int fromDoubleParser ( double &result, BYTE *source, int format,  DataOrder dataOrder = LSB );
   int toUint32STime( BYTE *source, ULONG &result, int format );
   ULONG BCDtoBase10( UCHAR* buffer, ULONG len );
   int toUint32LTime( BYTE *source, ULONG &result, int format );
   int toTime( BYTE *source, ULONG &result, int format );

   CtiAnsiTableBase();
   virtual ~CtiAnsiTableBase();
   CtiAnsiTableBase& operator=(const CtiAnsiTableBase& aRef);

};
