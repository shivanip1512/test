
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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/04/25 14:52:43 $
*    History: 
      $Log: std_ansi_tbl_base.h,v $
      Revision 1.1  2003/04/25 14:52:43  dsutton
      Standard and manufacturer table base class. Contains utility functions
      needed for all tables

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#include <rw/rwtime.h>
#include <rw/rwdate.h>

#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
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

class IM_EX_PROT CtiAnsiTableBase
{
protected:

private:

public:

   int toDoubleParser( BYTE *source, double &result, int format );
   int toUint32STime( BYTE *source, ULONG &result, int format );
   ULONG BCDtoBase10( UCHAR* buffer, ULONG len );


   CtiAnsiTableBase();
   virtual ~CtiAnsiTableBase();
   CtiAnsiTableBase& operator=(const CtiAnsiTableBase& aRef);

};

#endif // #ifndef __STD_ANSI_TBL_BASE_H__
