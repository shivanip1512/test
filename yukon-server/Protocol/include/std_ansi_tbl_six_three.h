
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_SIX_THREE_H__
#define __STD_ANSI_TBL_SIX_THREE_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_six_three
*
* Class:
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_six_three.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/10/01 17:54:27 $
*    History: 
      $Log: std_ansi_tbl_six_three.h,v $
      Revision 1.1  2004/10/01 17:54:27  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.  New files!

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/


#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
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

class IM_EX_PROT CtiAnsiTableSixThree : public CtiAnsiTableBase
{
protected:

   LP_STATUS_RCD      _lp_status_tbl;

private:  

    bool _lpCtrlDataSetUsed[4];

public:

   CtiAnsiTableSixThree( bool *dataSetUsedFlag  );
   CtiAnsiTableSixThree( BYTE *dataBlob, bool *dataSetUsedFlag );
   
   virtual ~CtiAnsiTableSixThree();

   CtiAnsiTableSixThree& operator=(const CtiAnsiTableSixThree& aRef);
   void generateResultPiece( BYTE **dataBlob );
   void printResult();
   void decodeResultPiece( BYTE **dataBlob );

   UINT16 getNbrValidBlocks(int setNbr);
   UINT16 getLastBlkElmt(int setNbr);
   UINT16 getLastBlkSeqNbr(int setNbr);
   UINT16 getNbrUnreadBlks(int setNbr);
   UINT16 getNbrValidIntvls(int setNbr);

};
#endif // #ifndef __STD_ANSI_TBL_SIX_THREE_H__
