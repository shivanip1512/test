/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_five
*
* Class:
* Date:   9/17/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/03/13 19:35:49 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#ifndef __STD_ANSI_TBL_ONE_FIVE_H__
#define __STD_ANSI_TBL_ONE_FIVE_H__
#pragma warning( disable : 4786)


#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"

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

class IM_EX_PROT CtiAnsiTableOneFive
{
protected:

   CONSTANTS_SELECT        *_constants_table;

private:

public:

   CtiAnsiTableOneFive( BYTE *dataBlob, int selector, int constants_entries, bool noOffset, bool useSet1, bool useSet2 );
   virtual ~CtiAnsiTableOneFive();
   CtiAnsiTableOneFive& operator=(const CtiAnsiTableOneFive& aRef);

   //these retrieve the electrical constants
   double getElecMultiplier( void );
   double getElecOffset( void );
   SET_APPLIED getElecSetOneConstants( void );
   SET_APPLIED getElecSetTwoConstants( void );
   bool getUseControl( void );
   double getRatioF1( void );
   double getRatioP1( void );
   ELECTRIC_CONSTANTS getElecConstants( void );
};

#endif // #ifndef __STD_ANSI_TBL_ONE_FIVE_H__
