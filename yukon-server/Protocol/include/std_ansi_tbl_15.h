/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_15
*
* Class:
* Date:   9/17/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_tbl_15.h-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: std_ansi_tbl_one_five.h,v $
      Revision 1.8  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.7  2008/10/07 18:16:45  mfisher
      YUK-6504 Server-side point management is naive
      cleaned up a few dsm2.h dependencies

      Revision 1.6  2005/12/20 17:20:00  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.5  2005/12/12 20:34:47  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.4.6.1  2005/12/12 19:51:02  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.4  2004/09/30 21:37:20  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_15_H__
#define __STD_ANSI_TBL_15_H__
#pragma warning( disable : 4786)

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#pragma pack( push, 1)

   struct GAS_PRESSURE
   {
      double            gas_press_zero;
      double            gas_press_fullscale;
      double            base_pressure;
   };

   struct GAS_TEMP
   {
      double            gas_temp_zero;
      double            gas_temp_fullscale;
      double            base_temp;
   };

   struct GAS_DP
   {
      double            gas_dp_zero;
      double            gas_dp_fullscale;
   };

   struct PIP_ORIF_DIA
   {
      double            pipe_dia;
      double            orif_dia;
   };

   struct GAS_AGA3_CORR
   {
      double            aux_corr_fctr;
      double            gas_aga3_corr_fctr;
      PIP_ORIF_DIA      pipe_orif_dia;
      unsigned char     tap_up_dn;
      GAS_PRESSURE      gas_press_parm;
      GAS_TEMP          gas_temp_parm;
   };

   struct GAS_AGA7_CORR
   {
      GAS_PRESSURE      gas_press_parm;
      GAS_TEMP          gas_temp_parm;
      double            aux_corr_fctr;
      double            gas_aga7_corr;
   };

   struct GAS_ENERGY
   {
      double            gas_energy_zero;
      double            gas_energy_full;
   };

   struct GAS_DP_SHUTOFF
   {
      double            gas_shutoff;
   };

   struct GAS_CONSTANTS_AGA3
   {
      GAS_DP            gas_dp_parm;
      GAS_DP_SHUTOFF    gas_dp_shutoff;
      GAS_PRESSURE      gas_press_parm;
      GAS_AGA3_CORR     gas_aga3_corr;
      GAS_ENERGY        gas_energy;
   };

   struct GAS_CONSTANTS_AGA7
   {
      GAS_AGA7_CORR     gas_aga7_corr;
      GAS_ENERGY        gas_energy;
   };

   struct SET_CNTL_BFLD
   {
      unsigned char     set_applied_flag  :1;
      unsigned char     filler            :7;
   };

   struct SET_APPLIED
   {
      SET_CNTL_BFLD     set_flags;
      double            ratio_f1;
      double            ratio_p1;
   };

   struct ELECTRIC_CONSTANTS
   {
      double            multiplier;
      double            offset;
      SET_APPLIED       set1_constants;
      SET_APPLIED       set2_constants;
   };

   typedef union
   {
      GAS_CONSTANTS_AGA3      gas_constants_aga3;
      GAS_CONSTANTS_AGA7      gas_constants_aga7;
      ELECTRIC_CONSTANTS      electric_constants;

   } CONSTANTS_SELECT;

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable15 : public CtiAnsiTableBase
{
protected:

   CONSTANTS_SELECT        *_constants_table;

private:
    int _RawConstantsSelector;
    int _NumberConstantsEntries;
    bool _NoOffsetFlag;
    bool _SetOnePresentFlag;
    bool _SetTwoPresentFlag;
    int _NIFormat1;
    int _NIFormat2;
    bool _lsbDataOrder;

public:

   CtiAnsiTable15( int selector, int constants_entries, bool noOffset, bool useSet1, bool useSet2, int format1, int format2, bool lsbDataOrder = true );
   CtiAnsiTable15( BYTE *dataBlob, int selector, int constants_entries, bool noOffset, bool useSet1, bool useSet2, int format1, int format2, bool lsbDataOrder = true);
   virtual ~CtiAnsiTable15();
   CtiAnsiTable15& operator=(const CtiAnsiTable15& aRef);

   void generateResultPiece( BYTE **dataBlob );
   void printResult( const std::string& deviceName);
   void decodeResultPiece( BYTE **dataBlob );
   bool getSet1AppliedFlag(int index );
   bool getSet2AppliedFlag(int index );


   //these retrieve the electrical constants
   double getElecMultiplier( int index );
   double getElecOffset( int index );
   SET_APPLIED getElecSetOneConstants( void );
   SET_APPLIED getElecSetTwoConstants( void );
   bool getUseControl( void );
   double getRatioF1( void );
   double getRatioP1( void );
   ELECTRIC_CONSTANTS getElecConstants( void );
};

#endif // #ifndef __STD_ANSI_TBL_15_H__
