/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_11
*
* Class:
* Date:   9/16/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_11.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: std_ansi_tbl_one_one.h,v $
      Revision 1.7  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.6  2008/10/07 18:16:45  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.5  2005/12/20 17:20:00  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.4  2005/12/12 20:34:47  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_11_H__
#define __STD_ANSI_TBL_11_H__
#pragma warning( disable : 4786)

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

   CtiAnsiTable11( );
   CtiAnsiTable11( BYTE *dataBlob );
   virtual ~CtiAnsiTable11();
   CtiAnsiTable11& operator=(const CtiAnsiTable11& aRef);

   void generateResultPiece( BYTE **dataBlob );
   void decodeResultPiece( BYTE **dataBlob );
   void printResult(const std::string& deviceName);


private:
    SOURCE_RCD           *_source_record;
};

#endif // #ifndef __STD_ANSI_TBL_11_H__
