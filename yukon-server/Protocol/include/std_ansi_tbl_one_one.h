
#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_ONE_ONE_H__
#define __STD_ANSI_TBL_ONE_ONE_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_one
*
* Class:
* Date:   9/16/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_one_one.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2008/10/07 18:16:45 $
*    History:
      $Log: std_ansi_tbl_one_one.h,v $
      Revision 1.6  2008/10/07 18:16:45  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.5  2005/12/20 17:20:00  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

<<<<<<< std_ansi_tbl_one_one.h
      Revision 1.3.18.1  2005/07/12 21:08:43  jliu
      rpStringWithoutCmpParser

=======
      Revision 1.4  2005/12/12 20:34:47  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.3.20.1  2005/12/12 19:51:02  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

>>>>>>> 1.4
      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/


#include "dlldefs.h"
#include "ctitypes.h"
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

class IM_EX_PROT CtiAnsiTableOneOne : public CtiAnsiTableBase
{

public:

    enum
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

   string getResolvedConstantsSelector( void );
   string getResolvedNoOffsetFlag( void );
   string getResolvedSetOnePresentFlag( void );
   string getResolvedSetTwoPresentFlag( void );
   string getResolvedPFExcludeFlag( void );
   string getResolvedResetExcludeFlag( void );
   string getResolvedBlockDemandFlag( void );
   string getResolvedSlidingDemandFlag( void );
   string getResolvedThermalDemandFlag( void );

   CtiAnsiTableOneOne( );
   CtiAnsiTableOneOne( BYTE *dataBlob );
   virtual ~CtiAnsiTableOneOne();
   CtiAnsiTableOneOne& operator=(const CtiAnsiTableOneOne& aRef);

   void generateResultPiece( BYTE **dataBlob );
   void decodeResultPiece( BYTE **dataBlob );
   void printResult(const string& deviceName);


private:
    SOURCE_RCD           *_source_record;
};

#endif // #ifndef __STD_ANSI_TBL_ONE_ONE_H__
