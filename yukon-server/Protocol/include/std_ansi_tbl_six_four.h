

#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_SIX_FOUR_H__
#define __STD_ANSI_TBL_SIX_FOUR_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_six_four
*
* Class:
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_six_four.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/10/01 17:54:27 $
*    History: 
      $Log: std_ansi_tbl_six_four.h,v $
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

#define INT16             __int16
#define INT8               __int8
#define UINT16             __int16
#define UINT8               __int8

#pragma pack( push, 1)

struct INT_FMT1_RCD
{
    union itemtype
    {
       struct case1
       {
          UINT8 item;
       }s1;
       struct case2
       {
          UINT16 item;
       }s2;
       struct case4
       {
          UINT32 item;
       }s4;
       struct case8
       {
          INT8 item;
       }s8;
       struct case16
       {
          INT16 item;
       }s16;
       struct case32
       {
          INT32 item;
       }s32;
       struct case64
       {
          double item;
       }s64;
       struct case128
       {
          double item;
       }s128;
    }u;
};
struct INT_SET1_RCD
{
    UINT8 *extended_int_status;
    INT_FMT1_RCD *int_data;
};
struct CLOSURE_STATUS_BFLD
{
    unsigned char status:4;
    unsigned short nbr_valid_interval:12;
};
struct READINGS_RCD 
{
    double block_end_read;
    UINT32 block_end_pulse;

};
struct LP_BLK1_DAT_RCD
{
    ULONG blk_end_time; //STIME_DATE
    READINGS_RCD  *end_readings;
    CLOSURE_STATUS_BFLD  *closure_status;
    unsigned char  *set_simple_int_status;
    INT_SET1_RCD   *lp_int;
};
struct LP_DATA_SET1_RCD
{
    LP_BLK1_DAT_RCD  *lp_data_sets1;
};


#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableSixFour : public CtiAnsiTableBase
{
protected:

   LP_DATA_SET1_RCD      _lp_data_set1_tbl;

private:
    int   _nbrBlksSet1;
    int   _nbrChnsSet1;
    bool  _closureStatusFlag;
    bool  _simpleIntStatusFlag;
    int   _nbrBlkIntsSet1;
    bool  _blkEndReadFlag;
    bool  _blkEndPulseFlag;
    bool  _extendedIntStatusFlag;
    int   _maxIntvlTime;
    int   _intFmtCde1;
    int   _nbrValidInts;
    int   _niFmt1;
    int   _niFmt2;
    int   _timeFmt;

public:

   CtiAnsiTableSixFour( int numberBlocksSet, int numberChansSet, 
                        bool closureStatusFlag, bool simpleIntervalStatusFlag,
                        int numberBlockIntervalsSet, bool blockEndReadFlag,
                        bool blockEndPulseFlag, bool extendedIntervalStatusFlag, int maxIntvlTime, 
                        int intervalFmtCde, int nbrValidInts, int niFmt1, int niFmt2, 
                        int timeFmt  );
   CtiAnsiTableSixFour( BYTE *dataBlob, int numberBlocksSet, int numberChansSet, 
                        bool closureStatusFlag, bool simpleIntervalStatusFlag,
                        int numberBlockIntervalsSet, bool blockEndReadFlag,
                        bool blockEndPulseFlag, bool extendedIntervalStatusFlag, int maxIntvlTime, 
                        int intervalFmtCde, int nbrValidInts, int niFmt1, int niFmt2, 
                        int timeFmt );
   
   virtual ~CtiAnsiTableSixFour();

   CtiAnsiTableSixFour& operator=(const CtiAnsiTableSixFour& aRef);
   void generateResultPiece( BYTE **dataBlob );
   void printResult();
   void decodeResultPiece( BYTE **dataBlob );
   int populateIntData(INT_FMT1_RCD *intData, BYTE *dataBlob);
   void printIntervalFmtRecord(INT_FMT1_RCD intData);
   
   bool getBlkIntvlTime(int blkSet, int blkIntvl, ULONG &blkIntvlTime);
   bool getBlkEndTime(int blkSet, ULONG &blkEndTime);

   double getLPDemandValue ( int channel, int blkSet, int blkIntvl );
   ULONG getLPDemandTime (int blkSet, int blkIntvl);

};
#endif // #ifndef __STD_ANSI_TBL_SIX_FOUR_H__
