#pragma once

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
    DataOrder  _dataOrder;
    bool  _descBlockOrder;
    bool  _descIntervalOrder;


public:

   CtiAnsiTable64( BYTE *dataBlob, int numberBlocksSet, int numberChansSet,
                        bool closureStatusFlag, bool simpleIntervalStatusFlag,
                        int numberBlockIntervalsSet, bool blockEndReadFlag,
                        bool blockEndPulseFlag, bool extendedIntervalStatusFlag, int maxIntvlTime,
                        int intervalFmtCde, int nbrValidInts, int niFmt1, int niFmt2,
                        int timeFmt, int meterHour, bool timeZoneApplied, DataOrder dataOrder = LSB,
                        bool descBlockOrder = false, bool descIntervalOrder = false);

   virtual ~CtiAnsiTable64();

   CtiAnsiTable64& operator=(const CtiAnsiTable64& aRef);
   void printResult( const std::string& deviceName);
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
