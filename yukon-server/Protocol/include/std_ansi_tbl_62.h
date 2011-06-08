/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_62
*
* Class:
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_62.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2008/10/21 16:30:32 $
*    History:
      $Log: std_ansi_tbl_six_two.h,v $
      Revision 1.6  2008/10/21 16:30:32  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.5  2008/10/07 18:16:46  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.4  2005/12/20 17:20:01  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.3  2005/12/12 20:34:47  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.2  2005/01/03 23:07:15  jrichter
      checking into 3.1, for use at columbia to test sentinel

      Revision 1.1  2004/10/01 17:54:27  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.  New files!

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_62_H__
#define __STD_ANSI_TBL_62_H__
#pragma warning( disable : 4786)


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
#endif // #ifndef __STD_ANSI_TBL_62_H__
