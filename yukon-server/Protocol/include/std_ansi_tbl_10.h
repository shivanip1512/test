#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#pragma pack( push, 1)
/*
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
   char                 nbr_uom_entries;
   unsigned char        nbr_demand_ctrl_entries;
   unsigned char        data_ctrl_length;
   unsigned char        nbr_data_ctrl_entries;
   unsigned char        nbr_constants_entries;
   unsigned char        constants_selector;
   unsigned char        nbr_sources;
};
*/
#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable10 : public CtiAnsiTableBase
{
protected:

//   SOURCE_RCD           *source_record;

private:

public:

   int getNumberUOMEntries( void );

   CtiAnsiTable10( BYTE *dataBlob );
   virtual ~CtiAnsiTable10();
   CtiAnsiTable10& operator=(const CtiAnsiTable10& aRef);

};
