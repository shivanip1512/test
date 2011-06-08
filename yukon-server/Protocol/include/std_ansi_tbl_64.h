/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_64
*
* Class:
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_64.h-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2008/10/21 16:30:32 $
*    History:
      $Log: std_ansi_tbl_six_four.h,v $
      Revision 1.8  2008/10/21 16:30:32  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.7  2008/10/07 18:16:46  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.6  2006/05/03 17:19:33  jrichter
      BUG FIX:  correct DST adjustment for columbia flags.  added check for _nbrFullBlocks > 0 so it wouldn't set lastLPTime to 2036

      Revision 1.5  2006/03/31 16:18:32  jrichter
      BUG FIX & ENHANCEMENT:  fixed a memory leak (multiple allocations of lpBlocks, but only one deallocation), added quality retrieval.

      Revision 1.4  2005/12/20 17:20:00  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.3  2005/12/12 20:34:47  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.2  2005/01/25 18:33:51  jrichter
      added present value tables for kv2 and sentinel for voltage, current, freq, pf, etc..meter info

      Revision 1.1  2004/10/01 17:54:27  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.  New files!

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_64_H__
#define __STD_ANSI_TBL_64_H__
#pragma warning( disable : 4786)

#include "dlldefs.h"
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
    bool dayLightSavingsFlag;
    bool powerFailFlag;
    bool clockResetForwardFlag;
    bool clockResetBackwardsFlag;
    int *extendedStatus;
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

class IM_EX_PROT CtiAnsiTable64 : public CtiAnsiTableBase
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
    int   _meterHour;
    bool  _timeZoneApplied;
    bool  _lsbDataOrder;
    bool  _descBlockOrder;
    bool  _descIntervalOrder;


public:

   CtiAnsiTable64( int numberBlocksSet, int numberChansSet,
                        bool closureStatusFlag, bool simpleIntervalStatusFlag,
                        int numberBlockIntervalsSet, bool blockEndReadFlag,
                        bool blockEndPulseFlag, bool extendedIntervalStatusFlag, int maxIntvlTime,
                        int intervalFmtCde, int nbrValidInts, int niFmt1, int niFmt2,
                        int timeFmt, int meterHour, bool timeZoneApplied, bool lsbDataOrder = true,
                        bool descBlockOrder = false, bool descIntervalOrder = false);
   CtiAnsiTable64( BYTE *dataBlob, int numberBlocksSet, int numberChansSet,
                        bool closureStatusFlag, bool simpleIntervalStatusFlag,
                        int numberBlockIntervalsSet, bool blockEndReadFlag,
                        bool blockEndPulseFlag, bool extendedIntervalStatusFlag, int maxIntvlTime,
                        int intervalFmtCde, int nbrValidInts, int niFmt1, int niFmt2,
                        int timeFmt, int meterHour, bool timeZoneApplied, bool lsbDataOrder = true,
                        bool descBlockOrder = false, bool descIntervalOrder = false);

   virtual ~CtiAnsiTable64();

   CtiAnsiTable64& operator=(const CtiAnsiTable64& aRef);
   void generateResultPiece( BYTE **dataBlob );
   void printResult( const std::string& deviceName);
   void decodeResultPiece( BYTE **dataBlob );
   int populateIntData(INT_FMT1_RCD *intData, BYTE *dataBlob);
   void printIntervalFmtRecord(INT_FMT1_RCD intData);

   void getBlkIntvlTime(int blkSet, int blkIntvl, ULONG &blkIntvlTime, bool blockOrderDecreasing);
   bool getBlkEndTime(int blkSet, ULONG &blkEndTime);

   double getLPDemandValue ( int channel, int blkSet, int blkIntvl );
   ULONG getLPDemandTime (int blkSet, int blkIntvl, bool decreasingBlockOrder);

   int getExtendedIntervalStatus(int channel, int blkSet, int blkIntvl);
   bool getDayLightSavingsFlag(int blkSet, int blkIntvl) const;
   bool getPowerFailFlag(int blkSet, int blkIntvl) const;
   bool getClockResetForwardFlag(int blkSet, int blkIntvl) const;
   bool getClockResetBackwardsFlag(int blkSet, int blkIntvl) const;

};
#endif // #ifndef __STD_ANSI_TBL_64_H__
