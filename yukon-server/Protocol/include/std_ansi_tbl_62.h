#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#include "std_ansi_tbl_61.h"

#pragma pack( push, 1)

struct LP_CTRL_FLAGS_BFLD
{
    unsigned char  end_rdg_flag :1;

    unsigned char  no_multiplier_flag :1;
    unsigned char  lp_algorithm :3;
    unsigned char  filler :3;
};
struct LP_SOURCE_SEL_RCD
{
    LP_CTRL_FLAGS_BFLD chnl_flag;
    UINT8 lp_source_sel;
    UINT8 end_blk_rdg_source_select;
};
struct LP_SELECTION_SET
{
    LP_SOURCE_SEL_RCD *lp_sel_set;
    UINT8 int_fmt_cde;
    UINT16 *scalars_set;
    UINT16 *divisor_set;
};
struct LP_DATA_SELECTION_RCD
{
    LP_SELECTION_SET lp_sel[4];
};


#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable62 : public CtiAnsiTableBase
{
protected:

   LP_DATA_SELECTION_RCD      _lp_ctrl_tbl;
   bool _lpCtrlDataSetUsed[4];
   bool _scalarDivisorFlagSet[4];
   UINT8 _numChansSet[4];
   int _stdVerNumber;

private:

public:

    CtiAnsiTable62( BYTE *dataBlob, bool *dataSetUsedFlag, LP_DATA_SET *lp_data_set_info, bool scalarDivisorFlag1,
                     bool scalarDivisorFlag2, bool scalarDivisorFlag3, bool scalarDivisorFlag4,
                     int stdVersionNumber, DataOrder dataOrder = LSB );
    
    virtual ~CtiAnsiTable62();

    CtiAnsiTable62& operator=(const CtiAnsiTable62& aRef);
    void printResult( const std::string& deviceName);

    void appendLPSelSet(int set, int numChans, Cti::FormattedList& itemList);
    void appendScalarsDivisorSet(int set, int numChans, Cti::FormattedList& itemList);
    UINT8 getIntervalFmtCde(int setNbr);
    bool  getNoMultiplierFlag(int setNbr);

    UINT8* getLPDemandSelect(int setNbr);

};
