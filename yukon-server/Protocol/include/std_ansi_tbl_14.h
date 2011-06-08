/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_14
*
* Class:
* Date:   9/17/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_14.h-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: std_ansi_tbl_one_four.h,v $
      Revision 1.8  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.7  2008/10/07 18:16:45  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.6  2005/12/20 17:20:00  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.5  2005/12/12 20:34:47  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.4.6.1  2005/12/12 19:51:02  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.4  2004/09/30 21:37:20  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_14_H__
#define __STD_ANSI_TBL_14_H__
#pragma warning( disable : 4786)

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#pragma pack( push, 1)

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable14 : public CtiAnsiTableBase
{
protected:

   struct DATA_RCD
   {
      unsigned char     *source_id;
   };

   struct DATA_CONTROL_RCD
   {
      DATA_RCD          *data_rcd;
   };

   DATA_CONTROL_RCD  _data_control_record;


private:

   int         _controlLength;
   int         _controlEntries;

public:

   CtiAnsiTable14( int dataCtrlLen, int numDataCtrlEntries );
   CtiAnsiTable14( BYTE *dataBlob, int dataCtrlLen, int numDataCtrlEntries );
   virtual ~CtiAnsiTable14();
   CtiAnsiTable14& operator=(const CtiAnsiTable14& aRef);
   void printResult( const std::string& deviceName );

   void decodeResultPiece( BYTE **dataBlob );
   void generateResultPiece( BYTE **dataBlob );


};

#endif // #ifndef __STD_ANSI_TBL_14_H__
