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
struct LP_DATA_SELECTION_RCD
{
    LP_SOURCE_SEL_RCD *lp_sel_set1;
    UINT8 int_fmt_cde1;
    UINT16 *scalars_set1;
    UINT16 *divisor_set1;

    LP_SOURCE_SEL_RCD *lp_sel_set2;
    UINT8 int_fmt_cde2;
    UINT16 *scalars_set2;
    UINT16 *divisor_set2;

    LP_SOURCE_SEL_RCD *lp_sel_set3;
    UINT8 int_fmt_cde3;
    UINT16 *scalars_set3;
    UINT16 *divisor_set3;

    LP_SOURCE_SEL_RCD *lp_sel_set4;
    UINT8 int_fmt_cde4;
    UINT16 *scalars_set4;
    UINT16 *divisor_set4;
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

   CtiAnsiTable62(bool *dataSetUsedFlag, LP_DATA_SET *lp_data_set_info, bool scalarDivisorFlag1,
                    bool scalarDivisorFlag2, bool scalarDivisorFlag3, bool scalarDivisorFlag4,
                    int stdVersionNumber   );
   CtiAnsiTable62( BYTE *dataBlob, bool *dataSetUsedFlag, LP_DATA_SET *lp_data_set_info, bool scalarDivisorFlag1,
                    bool scalarDivisorFlag2, bool scalarDivisorFlag3, bool scalarDivisorFlag4,
                    int stdVersionNumber  );

   virtual ~CtiAnsiTable62();

   CtiAnsiTable62& operator=(const CtiAnsiTable62& aRef);
   void generateResultPiece( BYTE **dataBlob );
   void printResult( const std::string& deviceName);
   void decodeResultPiece( BYTE **dataBlob );


    void printLPSelSet(int set, int numChans);
    void printScalarsDivisorSet(int set, int numChans);
    UINT8 getIntervalFmtCde(int setNbr);
    bool  getNoMultiplierFlag(int setNbr);

    UINT8* getLPDemandSelect(int setNbr);

};
