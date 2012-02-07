#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#pragma pack( push, 1)

struct UOM_ENTRY_BFLD
{
   unsigned int      id_code                    :8;
   unsigned int      time_base                  :3;
   unsigned int      multiplier                 :3;
   unsigned int      q1_accountablility         :1;
   unsigned int      q2_accountablility         :1;
   unsigned int      q3_accountablility         :1;
   unsigned int      q4_accountablility         :1;
   unsigned int      net_flow_accountablility   :1;
   unsigned int      segmentation               :3;
   unsigned int      harmonic                   :1;
   unsigned int      reserved                   :8;
   unsigned int      nfs                        :1;
};

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable12 : public CtiAnsiTableBase
{
public:

   CtiAnsiTable12( BYTE *dataBlob, int num_entries, DataOrder dataOrder = LSB );
   virtual ~CtiAnsiTable12();
   CtiAnsiTable12& operator=(const CtiAnsiTable12& aRef);

   typedef enum
   {
       uom_watts = 0,
       uom_vars,
       uom_va,
       uom_rms_volts=8,
       uom_rms_volts_squared=10,
       uom_instantaneous_volts,
       uom_rms_amps,
       uom_rms_amps_squared=14,
       uom_instantaneous_amps,
       uom_not_supported
   } UNITOFMEASURES_e;

    static const CHAR * ANSI_UOM_WATTS;
    static const CHAR * ANSI_UOM_VARS;
    static const CHAR * ANSI_UOM_VA;
    static const CHAR * ANSI_UOM_RMS_VOLTAGE;
    static const CHAR * ANSI_UOM_RMS_VOLTAGE_SQUARED;
    static const CHAR * ANSI_UOM_INSTANTANEOUS_VOLTAGE;
    static const CHAR * ANSI_UOM_RMS_CURRENT;
    static const CHAR * ANSI_UOM_RMS_CURRENT_SQUARED;
    static const CHAR * ANSI_UOM_INSTANTANEOUS_CURRENT;
    static const CHAR * ANSI_UOM_NOT_SUPPORTED;

    typedef enum
    {
        timebase_dial_reading = 0,
        timebase_instantaneous,
        timebase_period_based,
        timebase_sub_block_average,
        timebase_block_average,
        timebase_relative_dial_reading,
        timebase_thermal,
        timebase_event,
        timebase_unknown
    } TIMEBASE_e;

    static const CHAR * ANSI_TIMEBASE_DIAL_READING;
    static const CHAR * ANSI_TIMEBASE_INSTANTANEOUS;
    static const CHAR * ANSI_TIMEBASE_PERIOD_BASED;
    static const CHAR * ANSI_TIMEBASE_SUB_BLOCK_AVERAGE;
    static const CHAR * ANSI_TIMEBASE_BLOCK_AVERAGE;
    static const CHAR * ANSI_TIMEBASE_RELATIVE_DIAL_READING;
    static const CHAR * ANSI_TIMEBASE_THERMAL;
    static const CHAR * ANSI_TIMEBASE_EVENT;
    static const CHAR * ANSI_TIMEBASE_UNKNOWN;

    typedef enum
    {
        multiplier_10_to_0=0,
        multiplier_10_to_2,
        multiplier_10_to_3,
        multiplier_10_to_6,
        multiplier_10_to_9,
        multiplier_10_to_minus_2,
        multiplier_10_to_minus_3,
        multiplier_10_to_minus_6,
        multiplier_unknown
    } MULTIPLIER_e;

    bool isCorrectData( int aOffset,int aUOM);
    int getRawIDCode( int aOffset );
    std::string getResolvedIDCode( int aOffset );
    int getRawTimeBase( int aOffset );
    std::string getResolvedTimeBase( int aOffset );
    int getRawMultiplier( int aOffset );
    DOUBLE getResolvedMultiplier( int aOffset );

    bool getQuadrantAccountabilityFlag(int quadrant, int index);
    bool getNetFlowAccountabilityFlag(int index);
    int getSegmentation(int index);
    bool getHarmonicFlag(int index);

    void printResult(const std::string& deviceName);


private:

    // from table 11
   int _numUomEntries;

   //can have 255 of these
   UOM_ENTRY_BFLD    *_uom_entries;

};
