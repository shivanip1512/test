
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_SIX_ONE_H__
#define __STD_ANSI_TBL_SIX_ONE_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_six_one
*
* Class:
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_six_two.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2004/12/10 21:58:43 $
*    History: 
      $Log: std_ansi_tbl_six_one.h,v $
      Revision 1.2  2004/12/10 21:58:43  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

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

class IM_EX_PROT CtiAnsiTableSixOne : public CtiAnsiTableBase
{
protected:

   LP_SET_RCD      _lp_tbl;
   bool _lpDataSetUsed[4];
   


private:

public:

   CtiAnsiTableSixOne( unsigned char *stdTblsUsed, int dimStdTblsUsed );
   CtiAnsiTableSixOne(  BYTE *dataBlob,  unsigned char *stdTblsUsed, int dimStdTblsUsed );
   
   virtual ~CtiAnsiTableSixOne();

   CtiAnsiTableSixOne& operator=(const CtiAnsiTableSixOne& aRef);
   void generateResultPiece( BYTE **dataBlob );
   void printResult();
   void decodeResultPiece( BYTE **dataBlob );
   void printLPDataSetInfo( int set, int offset );
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
#endif // #ifndef __STD_ANSI_TBL_SIX_ONE_H__
