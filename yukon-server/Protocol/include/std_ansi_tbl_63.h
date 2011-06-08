/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_63
*
* Class:
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_63.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/10/21 16:30:32 $
*    History:
      $Log: std_ansi_tbl_six_three.h,v $
      Revision 1.5  2008/10/21 16:30:32  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.4  2008/10/07 18:16:46  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.3  2005/12/20 17:20:01  tspar
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
#ifndef __STD_ANSI_TBL_63_H__
#define __STD_ANSI_TBL_63_H__
#pragma warning( disable : 4786)

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#define UINT16             __int16
#define UINT8               __int8
#define INT16             __int16
#define INT8               __int8

#pragma pack( push, 1)

struct LP_SET_STATUS_BFLD
{
    unsigned char  block_order :1;
    unsigned char  overflow_flag :1;
    unsigned char  list_type :1;
    unsigned char  block_inhibit_overflow_flag :1;
    unsigned char  interval_order :1;
    unsigned char  active_mode_flag :1;
    unsigned char  test_mode :1;
    unsigned char  filler :1;


};
struct LP_SET_STATUS_RCD
{
    LP_SET_STATUS_BFLD lp_set_status_flags;
    UINT16 nbr_valid_blocks;
    UINT16 last_block_element;
    UINT32 last_block_seq_nbr;
    UINT16 nbr_unread_blocks;
    UINT16 nbr_valid_int;
};
struct LP_STATUS_RCD
{
    LP_SET_STATUS_RCD  *lp_status_set;
};


#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable63 : public CtiAnsiTableBase
{
protected:

   LP_STATUS_RCD      _lp_status_tbl;

private:

    bool _lpCtrlDataSetUsed[4];

public:

   CtiAnsiTable63( bool *dataSetUsedFlag, bool lsbDataOrder = true  );
   CtiAnsiTable63( BYTE *dataBlob, bool *dataSetUsedFlag, bool lsbDataOrder = true );

   virtual ~CtiAnsiTable63();

   CtiAnsiTable63& operator=(const CtiAnsiTable63& aRef);
   void generateResultPiece( BYTE **dataBlob );
   void printResult( const std::string& deviceName);
   void decodeResultPiece( BYTE **dataBlob );

   UINT16 getNbrValidBlocks(int setNbr);
   UINT16 getLastBlkElmt(int setNbr);
   UINT16 getLastBlkSeqNbr(int setNbr);
   UINT16 getNbrUnreadBlks(int setNbr);
   UINT16 getNbrValidIntvls(int setNbr);
   bool isDataBlockOrderDecreasing(int setNbr);

};
#endif // #ifndef __STD_ANSI_TBL_63_H__
