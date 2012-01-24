#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#pragma pack( push, 1)

union INT_CONTROL_RCD
{
   struct CONTROL_RECORD
   {
      unsigned char  sub_int;
      unsigned char  int_mulitplier;
   }cntl_rec;
   USHORT  int_length;
};

struct DEMAND_CONTROL_RCD
{
   unsigned char  reset_exclusion;

   struct PF_EXCLUDE
   {
      unsigned char  p_fail_recogntn_tm;
      unsigned char  p_fail_exclusion;
      unsigned char  cold_load_pickup;
   }excludes;

   INT_CONTROL_RCD   *_int_control_rec;
};


#pragma pack( pop )


class IM_EX_PROT CtiAnsiTable13 : public CtiAnsiTableBase
{

public:

   CtiAnsiTable13( BYTE *dataBlob, int num_entries, bool pf_exclude, bool sliding_demand, bool reset_exclude, bool lsbDataOrder = true );
   virtual ~CtiAnsiTable13();
   CtiAnsiTable13& operator=(const CtiAnsiTable13& aRef);
   void printResult( const std::string& deviceName );

   bool getPFExcludeFlag();
   bool getSlidingDemandFlag();
   bool getResetExcludeFlag();

private:

    int                  _numberDemandCtrlEntries;
    bool                 _pfExcludeFlag;
    bool                 _slidingDemandFlag;
    bool                 _resetExcludeFlag;

    DEMAND_CONTROL_RCD   _demand_control_record;


};
