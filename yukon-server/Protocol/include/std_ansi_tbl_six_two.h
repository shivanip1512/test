#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_SIX_TWO_H__
#define __STD_ANSI_TBL_SIX_TWO_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_six_two
*
* Class:
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_six_two.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/10/01 17:54:27 $
*    History: 
      $Log: std_ansi_tbl_six_two.h,v $
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

#include "std_ansi_tbl_six_one.h"

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

class IM_EX_PROT CtiAnsiTableSixTwo : public CtiAnsiTableBase
{
protected:

   LP_DATA_SELECTION_RCD      _lp_ctrl_tbl;
   bool _lpCtrlDataSetUsed[4];
   bool _scalarDivisorFlagSet[4];
   UINT8 _numChansSet[4];
   int _stdVerNumber;

private:

public:

   CtiAnsiTableSixTwo(bool *dataSetUsedFlag, LP_DATA_SET *lp_data_set_info, bool scalarDivisorFlag1, 
                    bool scalarDivisorFlag2, bool scalarDivisorFlag3, bool scalarDivisorFlag4, 
                    int stdVersionNumber   );
   CtiAnsiTableSixTwo( BYTE *dataBlob, bool *dataSetUsedFlag, LP_DATA_SET *lp_data_set_info, bool scalarDivisorFlag1, 
                    bool scalarDivisorFlag2, bool scalarDivisorFlag3, bool scalarDivisorFlag4, 
                    int stdVersionNumber  );
   
   virtual ~CtiAnsiTableSixTwo();

   CtiAnsiTableSixTwo& operator=(const CtiAnsiTableSixTwo& aRef);
   void generateResultPiece( BYTE **dataBlob );
   void printResult();
   void decodeResultPiece( BYTE **dataBlob );


    void printLPSelSet(int set, int numChans);
    void printScalarsDivisorSet(int set, int numChans);
    UINT8 getIntervalFmtCde(int setNbr);

    UINT8* getLPDemandSelect(int setNbr);

};
#endif // #ifndef __STD_ANSI_TBL_SIX_TWO_H__
