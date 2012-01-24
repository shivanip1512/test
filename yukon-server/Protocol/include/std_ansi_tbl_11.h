#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#pragma pack( push, 1)

struct SOURCE_FLAGS_BFLD
{
   unsigned char        pf_exclude_flag       :1;
   unsigned char        reset_exclude_flag    :1;
   unsigned char        block_demand_flag     :1;
   unsigned char        sliding_demand_flag   :1;
   unsigned char        thermal_demand_flag   :1;
   unsigned char        set1_preset_flag      :1;
   unsigned char        set2_preset_flag      :1;
   unsigned char        no_offset_flag        :1;
};

struct SOURCE_RCD
{
   SOURCE_FLAGS_BFLD    source_flags;
   unsigned char        nbr_uom_entries;
   unsigned char        nbr_demand_ctrl_entries;
   unsigned char        data_ctrl_length;
   unsigned char        nbr_data_ctrl_entries;
   unsigned char        nbr_constants_entries;
   unsigned char        constants_selector;
   unsigned char        nbr_sources;
};

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable11 : public CtiAnsiTableBase
{

public:

    typedef enum
    {
        gasConstantsAGA3 = 0,
        gasConstantsAGA7,
        electricConstants,
        constantReserved
    } CONSTANTS_SELECTOR_e;

    static const CHAR * SELECTOR_CONSTANTS_GAS_AGA3;
    static const CHAR * SELECTOR_CONSTANTS_GAS_AGA7;
    static const CHAR * SELECTOR_CONSTANTS_ELECTRIC;
    static const CHAR * SELECTOR_CONSTANTS_RESERVED;

   int getNumberUOMEntries( void );
   int getNumberDemandControlEntries( void );
   int getDataControlLength( void );
   int getNumberDataControlEntries( void );
   int getNumberConstantsEntries( void );
   int getNumberSources( void );

   int getRawConstantsSelector( void );
   bool getRawNoOffsetFlag( void );
   bool getRawSetOnePresentFlag( void );
   bool getRawSetTwoPresentFlag( void );
   bool getRawPFExcludeFlag( void );
   bool getRawResetExcludeFlag( void );
   bool getRawBlockDemandFlag( void );
   bool getRawSlidingDemandFlag( void );
   bool getRawThermalDemandFlag( void );

   std::string getResolvedConstantsSelector( void );
   std::string getResolvedNoOffsetFlag( void );
   std::string getResolvedSetOnePresentFlag( void );
   std::string getResolvedSetTwoPresentFlag( void );
   std::string getResolvedPFExcludeFlag( void );
   std::string getResolvedResetExcludeFlag( void );
   std::string getResolvedBlockDemandFlag( void );
   std::string getResolvedSlidingDemandFlag( void );
   std::string getResolvedThermalDemandFlag( void );

   CtiAnsiTable11( BYTE *dataBlob );
   virtual ~CtiAnsiTable11();
   CtiAnsiTable11& operator=(const CtiAnsiTable11& aRef);

   void printResult(const std::string& deviceName);


private:
    SOURCE_RCD           *_source_record;
};
