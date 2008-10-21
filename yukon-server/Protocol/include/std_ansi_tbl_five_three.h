/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_53
*
* Class:
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_53.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/10/21 16:30:32 $
*    History:
      $Log: std_ansi_tbl_five_three.h,v $
      Revision 1.5  2008/10/21 16:30:32  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.4  2008/10/07 18:16:45  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.3  2005/12/20 17:20:00  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.2  2005/12/12 20:34:47  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.1  2004/10/01 17:54:27  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.  New files!

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_53_H__
#define __STD_ANSI_TBL_53_H__
#pragma warning( disable : 4786)

#include "dlldefs.h"
#include "ctitypes.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#define BCD                unsigned char

#pragma pack( push, 1)

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable53 : public CtiAnsiTableBase
{
protected:

   TIME_OFFSET_RCD      time_offset_table;

private:


public:

   CtiAnsiTable53(int timefmat  );
   CtiAnsiTable53( BYTE *dataBlob, int timefmat );

   virtual ~CtiAnsiTable53();

   CtiAnsiTable53& operator=(const CtiAnsiTable53& aRef);
   void generateResultPiece( BYTE **dataBlob );
   void printResult( const string& deviceName);
   void decodeResultPiece( BYTE **dataBlob );

};
#endif // #ifndef __STD_ANSI_TBL_53_H__
