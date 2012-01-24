#pragma once

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

   CtiAnsiTable63( BYTE *dataBlob, bool *dataSetUsedFlag, bool lsbDataOrder = true );

   virtual ~CtiAnsiTable63();

   CtiAnsiTable63& operator=(const CtiAnsiTable63& aRef);
   void printResult( const std::string& deviceName);

   UINT16 getNbrValidBlocks(int setNbr);
   UINT16 getLastBlkElmt(int setNbr);
   UINT16 getLastBlkSeqNbr(int setNbr);
   UINT16 getNbrUnreadBlks(int setNbr);
   UINT16 getNbrValidIntvls(int setNbr);
   bool isDataBlockOrderDecreasing(int setNbr);

};
