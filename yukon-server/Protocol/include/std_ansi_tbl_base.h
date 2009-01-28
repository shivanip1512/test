
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_BASE_H__
#define __STD_ANSI_TBL_BASE_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_base
*
* Class:
* Date:   12/4/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_base.h-arc  $
* REVISION     :  $Revision: 1.7.2.1 $
* DATE         :  $Date: 2008/11/17 23:06:33 $
*    History:
      $Log: std_ansi_tbl_base.h,v $
      Revision 1.7.2.1  2008/11/17 23:06:33  jmarks
      YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008
      **************************************************************************************************************
      Removed "CTITYPES.H" from every file in the project, so far there were no
      known side-effects or even compile errors, however, they could still happen.

      Also, made many other changes for compiling.

      The project now apparently compiles until reching the database
      subdirectory, however, I have seen cases where there is apparent
      regressing and need to re-work things.

      However, enough changes have happened, that I felt it was good to
      committ.
      **************************************************************************************************************
      Possibly other misc. changes since last commit.
      *******************************************************
      
      Revision 1.7  2008/10/07 18:16:45  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.6  2005/12/20 17:19:59  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.5  2005/03/14 21:44:16  jrichter
      updated with present value regs, batterylife info, corrected quals, multipliers/offsets, corrected single precision float define, modifed for commander commands, added demand reset

      Revision 1.4  2005/01/25 18:33:51  jrichter
      added present value tables for kv2 and sentinel for voltage, current, freq, pf, etc..meter info

      Revision 1.3  2004/12/10 21:58:42  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.2  2004/09/30 21:37:19  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.1  2003/04/25 14:52:43  dsutton
      Standard and manufacturer table base class. Contains utility functions
      needed for all tables

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

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
   UCHAR    ch[4];
   float      u32;
} BYTEFLOAT32;



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

   int toDoubleParser( BYTE *source, double &result, int format );
   int fromDoubleParser ( double &result, BYTE *source, int format );
   int toUint32STime( BYTE *source, ULONG &result, int format );
   ULONG BCDtoBase10( UCHAR* buffer, ULONG len );
   int toUint32LTime( BYTE *source, ULONG &result, int format );
   int toTime( BYTE *source, ULONG &result, int format );

   CtiAnsiTableBase();
   virtual ~CtiAnsiTableBase();
   CtiAnsiTableBase& operator=(const CtiAnsiTableBase& aRef);

};

#endif // #ifndef __STD_ANSI_TBL_BASE_H__
