#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"


#define UINT16             __int16
#define UINT8               __int8

#pragma pack( push, 1)

struct LP_DATA_SET
{
   UINT16   nbr_blks_set;
   UINT16   nbr_blk_ints_set;
   UINT8    nbr_chns_set;
   UINT8    max_int_time_set;
};
struct LP_FLAGS_BFLD
{
    unsigned char lp_set1_inhibit_ovf_flag :1;
    unsigned char lp_set2_inhibit_ovf_flag :1;
    unsigned char lp_set3_inhibit_ovf_flag :1;
    unsigned char lp_set4_inhibit_ovf_flag :1;
    unsigned char blk_end_read_flag        :1;
    unsigned char blk_end_pulse_flag       :1;
    unsigned char scalar_divisor_flag_set1 :1;
    unsigned char scalar_divisor_flag_set2 :1;
    unsigned char scalar_divisor_flag_set3 :1;
    unsigned char scalar_divisor_flag_set4 :1;
    unsigned char extended_int_status_flag :1;
    unsigned char simple_int_status_flag   :1;
    unsigned char closure_status_flag      :1;
    unsigned char filler                   :3;
};

struct LP_FMATS_BFLD
{
    unsigned char inv_uint8_flag    :1;
    unsigned char inv_uint16_flag   :1;
    unsigned char inv_uint32_flag   :1;
    unsigned char inv_int8_flag     :1;
    unsigned char inv_int16_flag    :1;
    unsigned char inv_int32_flag    :1;
    unsigned char inv_ni_fmat1_flag :1;
    unsigned char inv_ni_fmat2_flag :1;
};


struct LP_SET_RCD
{
   UINT32           lp_memory_len;
   LP_FLAGS_BFLD    lp_flags;
   LP_FMATS_BFLD    lp_fmats;

   LP_DATA_SET   *lp_data_set_info;

};

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable61 : public CtiAnsiTableBase
{
   LP_SET_RCD      _lp_tbl;
   bool _lpDataSetUsed[4];

public:

   CtiAnsiTable61(  BYTE *dataBlob,  unsigned char *stdTblsUsed, int dimStdTblsUsed, DataOrder dataOrder = LSB);

   virtual ~CtiAnsiTable61();

   CtiAnsiTable61& operator=(const CtiAnsiTable61& aRef);
   void printResult( const std::string& deviceName);
   void appendLPDataSetInfo(int set, int offset, Cti::FormattedList &itemList);
   LP_DATA_SET * getLPDataSetInfo();
   bool * getLPDataSetUsedFlags();
   bool getLPScalarDivisorFlag( int setNo );

   int getNbrBlksSet (int setNbr );
   int getNbrChansSet(int setNbr);
   int getNbrBlkIntsSet( int setNbr);
   int getMaxIntTimeSet( int setNbr);
   bool getClosureStatusFlag();
   bool getSimpleIntStatusFlag();
   bool getBlkEndReadFlag();
   bool getBlkEndPulseFlag();
   bool getExtendedIntStatusFlag();


   UINT32 getLPMemoryLength();
};
