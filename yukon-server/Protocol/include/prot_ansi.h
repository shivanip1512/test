
#pragma warning( disable : 4786)
#ifndef __PROT_ANSI_H__
#define __PROT_ANSI_H__

/*-----------------------------------------------------------------------------*
*
* File:   prot_ansi
*
* Class:
* Date:   6/13/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/09/03 17:27:50 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <rw\cstring.h>

#include "ansi_application.h"

#define UINT64             __int64 //FIXME - figure out how to get a uint64
#define BCD                unsigned char

enum States
{
   identified = 0,
   negotiated,
   timingSet,
   loggedOn,
   authenticated,
   readyToGo

};

//duplication of what's on the scanner side FIXME
struct WANTS_HEADER
{
   unsigned long  lastLoadProfileTime;
   int            numTablesRequested;
   int            command;
};

//this one's usable on both sides
struct ANSI_TABLE_WANTS
{
   int   tableID;
   int   tableOffset;
   int   bytesExpected;
};


//=========================================================================================================================================
//tables defined by the ansi standard
//=========================================================================================================================================

struct FORMAT_CONTROL_1
{
   unsigned char data_order         :1;
   unsigned char char_format        :3;
   unsigned char model_select       :3;
   unsigned char mfg_sn_flag        :1;
};

struct FORMAT_CONTROL_2
{
   unsigned char tm_format          :3;
   unsigned char data_access_meth   :2;
   unsigned char id_format          :1;
   unsigned char int_format         :2;
};

struct FORMAT_CONTROL_3
{
   unsigned char ni_format1         :4;
   unsigned char ni_format2         :4;
};

struct TABLE_00_GEN_CONFIG
{
   FORMAT_CONTROL_1  control_1;
   FORMAT_CONTROL_2  control_2;
   FORMAT_CONTROL_3  control_3;
   unsigned char     device_class[4];
   unsigned char     default_set_used;
   unsigned char     max_proc_parm_len;
   unsigned char     max_resp_data_len;
   unsigned char     std_version_no;
   unsigned char     std_revision_no;
   unsigned char     dim_std_tbls_used;
   unsigned char     dim_mfg_tbls_used;
   unsigned char     dim_std_proc_used;
   unsigned char     dim_mfg_proc_used;
   unsigned char     dim_mfg_status_used;
   unsigned char     nbr_pending;
   unsigned char     std_tbls_used[256];
   unsigned char     mfg_tbls_used[256];
   unsigned char     std_proc_used[256];
   unsigned char     mfg_proc_used[256];
   unsigned char     std_tbls_write[256];
   unsigned char     mfg_tbls_write[256];
};

struct TABLE_01_GEN_MFG_ID
{
  unsigned char      manufacturer[4];
  unsigned char      ed_model[8];
  unsigned char      hw_version_number;
  unsigned char      hw_revision_number;
  unsigned char      fw_version_number;
  unsigned char      fw_revision_number;

  union
  {
     BCD       bcd_sn[8];
     char      char_sn[16];
     UINT64    ll_sn;

  }mfg_serial_number;
};

struct SOURCE_FLAGS_BFLD
{
   unsigned char  pf_exclude_flag         :1;
   unsigned char  reset_exclude_flag      :1;
   unsigned char  block_demand_flag       :1;
   unsigned char  sliding_demand_flag     :1;
   unsigned char  thermal_demand_flag     :1;
   unsigned char  set1_present_flag       :1;
   unsigned char  set2_present_flag       :1;
   unsigned char  no_offset_flag          :1;
};

/*
struct TABLE_10_DIM_SOURCES_LIM
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

struct TABLE_11_ACT_SOURCES_LIM
{

};
*/
struct UOM_ENTRY_BFLD
{
   unsigned char     id_code                    :8;
   unsigned char     time_base                  :3;
   unsigned char     multiplier                 :3;
   unsigned char     q1_accountablility         :1;
   unsigned char     q2_accountablility         :1;
   unsigned char     q3_accountablility         :1;
   unsigned char     q4_accountablility         :1;
   unsigned char     net_flow_accountablility   :1;
   unsigned char     segmentation               :3;
   unsigned char     harmonic                   :1;
   unsigned char     reserved                   :8;
   unsigned char     nfs                        :1;
};

//can have 255 of these
struct TABLE_12_UNIT_OF_MEASURE
{
   UOM_ENTRY_BFLD    uom_entries[255];
};

union FLAGS
{
   unsigned char  reset_exclusion;

   struct PF_EXCLUDE
   {
      unsigned char  p_fail_recogntn_tm;
      unsigned char  p_fail_exclusion;
      unsigned char  cold_load_pickup;
   };
};

union INT_CONTROL_RCD
{
   struct CONTROL_RECORD
   {
      unsigned char  sub_int;
      unsigned char  int_mulitplier;
   };

   UINT  int_length;
};

struct TABLE_13_DEMAND_CONTROL
{
   FLAGS                flags;
   INT_CONTROL_RCD      int_control_rec[256];
};

struct DATA_RCD
{
   unsigned char     source_id[256];
};

struct DATA_CONTROL_RCD
{
   DATA_RCD          data_rcd[256];
};

struct TABLE_14_DATA_CONTROL
{
   DATA_CONTROL_RCD  data_control_record;
};

struct GAS_PRESSURE
{
   double      gas_press_zero;
   double      gas_press_fullscale;
   double      base_pressure;
};

struct GAS_TEMP
{
   double      gas_temp_zero;
   double      gas_temp_fullscale;
   double      base_temp;
};

struct GAS_DP
{
   double      gas_dp_zero;
   double      gas_dp_fullscale;
};

struct PIP_ORIF_DIA
{
   double      pipe_dia;
   double      orif_dia;
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
   double            set1_constants;
   double            set2_constants;
};

struct TABLE_15_CONSTANTS
{
   union
   {
      GAS_CONSTANTS_AGA3      gas_constants_aga3;
      GAS_CONSTANTS_AGA7      gas_constants_aga7;
      ELECTRIC_CONSTANTS      electric_constants;
   };
};

struct SOURCE_LINK_BFLD
{
   unsigned char     uom_entry_flag          :1;
   unsigned char     demand_ctrl_flag        :1;
   unsigned char     data_ctrl_flag          :1;
   unsigned char     constants_flag          :1;
   unsigned char     pulse_engr_flag         :1;
   unsigned char     constant_to_be_applied  :1;
   unsigned char     filler                  :2;
};

struct TABLE_16_SOURCE_DEFINITION
{
   SOURCE_LINK_BFLD  source_link[256];
};

struct REG_FUNC1_BFLD
{
   unsigned char        season_info_field_flag  :1;
   unsigned char        date_time_field_flag    :1;
   unsigned char        demand_reset_ctr_flag   :1;
   unsigned char        demand_reset_lock_flag  :1;
   unsigned char        cum_demand_flag         :1;
   unsigned char        cont_cum_demand_flag    :1;
   unsigned char        time_remaining_flag     :1;
   unsigned char        filler                  :1;
};

struct REG_FUNC2_BFLD
{
   unsigned char        self_read_inhibit_overflow_flag  :1;
   unsigned char        self_read_seq_nbr_flag           :1;
   unsigned char        daily_self_read_flag             :1;
   unsigned char        weekly_self_read_flag            :1;
   unsigned char        self_read_demand_reset           :2;
   unsigned char        filler                           :2;
};

//this one might be wrong
struct TABLE_20_DIMENSION_REGISTER
{
   REG_FUNC1_BFLD       reg_func1_flags;
   REG_FUNC2_BFLD       reg_func2_flags;
   unsigned char        nbr_self_reads;
   unsigned char        nbr_summations;
   unsigned char        nbr_demands;
   unsigned char        nbr_coin_values;
   unsigned char        occur;
   unsigned char        tiers;
   unsigned char        nbr_present_demands;
   unsigned char        nbr_present_values;
};

//or this one might be wrong
struct TABLE_21_ACTUAL_REGISTER
{
   REG_FUNC1_BFLD       reg_func1_flags;
   REG_FUNC2_BFLD       reg_func2_flags;
   unsigned char        nbr_self_reads;
   unsigned char        nbr_summations;
   unsigned char        nbr_demands;
   unsigned char        nbr_coin_values;
   unsigned char        occur;
   unsigned char        tiers;
   unsigned char        nbr_present_demands;
   unsigned char        nbr_present_values;
};

struct TABLE_22_DATA_SELECTION
{
   unsigned char        summation_select[256];
   unsigned char        demand_select[256];
   unsigned char        set[256];
   unsigned char        coincident_select[256];
   unsigned char        coin_demand_assoc[256];
};

struct STIME_DATE
{
   union CASES
   {
      struct CASE1
      {
         BCD   year;
         BCD   month;
         BCD   day;
         BCD   hour;
         BCD   minute;
      };

      struct CASE2
      {
         unsigned char  year;
         unsigned char  month;
         unsigned char  day;
         unsigned char  hour;
         unsigned char  minute;
      };

      struct CASE3
      {
         long  d_time;
      };

      struct CASE4
      {
         long  d_time;
      };
   };
};

struct COINCIDENTS_RCD
{
   double               coincident_values[255];
};

struct DEMANDS_RCD
{
   STIME_DATE           event_time[255];
   double               cum_demand;
   double               cont_cum_demand;
   double               demand[255];
};

struct DATA_BLK_RCD
{
   double               summations[255];
   DEMANDS_RCD          demands[255];
   COINCIDENTS_RCD      coincidents[255];
};

struct TABLE_23_CURRENT_REGISTER_DATA
{
   unsigned char        nbr_demand_resets;
   DATA_BLK_RCD         tot_data_block;
   DATA_BLK_RCD         tier_data_block[255];
};

struct TABLE_27_PRESETN_REGISTER_SELECTION
{
   unsigned char        present_demand_select[255];
   unsigned char        present_value_select[255];
};

struct TIME
{
   union CASES
   {
      struct CASE1
      {
         BCD   hour;
         BCD   minute;
         BCD   second;
      };

      struct CASE2
      {
         unsigned char  hour;
         unsigned char  minute;
         unsigned char  second;
      };

      struct CASE3
      {
         long d_time;
      };

      struct CASE4
      {
         long d_time;
      };
   };
};

struct PRESENT_DEMAND_RCD
{
   TIME     time_remaining;
   double   demand_value;
};

struct TABLE_28_PRESENT_REGISTER_DATA
{
   PRESENT_DEMAND_RCD   present_demand[255];
   double               present_value[255];
};

struct STATUS_BFLD
{
   union seperate_sum
   {
      struct first
      {
         unsigned charcurr_summ_tier      :3;
         unsigned charcurr_demand_tier    :3;
      };

      struct sec
      {
         unsigned charcurr_tier           :3;
         unsigned charfiller              :3;
      };
   };

   unsigned char  tier_drive              :2;
   unsigned char  special_schd_active     :4;
   unsigned char  season                  :4;
};

struct LTIME_DATE
{
   union CASES
   {
      struct CASE1
      {
         BCD      year;
         BCD      month;
         BCD      day;
         BCD      hour;
         BCD      minute;
         BCD      second;
      };

      struct CASE2
      {
         unsigned char  year;
         unsigned char  month;
         unsigned char  day;
         unsigned char  hour;
         unsigned char  minute;
         unsigned char  second;
      };

      struct CASE3
      {
         long           u_time;
         unsigned char  second;
      };

      struct CASE4
      {
         long           u_time_sec;
      };
   };
};

struct TIME_DATE_QUAL_BFLD
{
   unsigned char        day_of_week          :3;
   unsigned char        dst_flag             :1;
   unsigned char        gmt_flag             :1;
   unsigned char        tm_zn_applied_flag   :1;
   unsigned char        dst_applied_flag     :1;
   unsigned char        filler               :1;
};

struct TABLE_55_CLOCK_STATE
{
   LTIME_DATE           clock_calendar;
   TIME_DATE_QUAL_BFLD  time_date;
   STATUS_BFLD          status;
};

struct TABLE_GE72_POWER_QUALITY
{
   unsigned char     current_angle_pha[2];
   unsigned char     voltage_angle_pha[2];
   unsigned char     current_angle_phb[2];
   unsigned char     voltage_angle_phb[2];
   unsigned char     current_angle_phc[2];
   unsigned char     voltage_angle_phc[2];
   unsigned char     current_mag_pha[2];
   unsigned char     voltage_mag_pha[2];
   unsigned char     current_mag_phb[2];
   unsigned char     voltage_mag_phb[2];
   unsigned char     current_mag_phc[2];
   unsigned char     voltage_mag_phc[2];
   unsigned char     du_pf;
   unsigned char     diag_1_cnt;
   unsigned char     diag_2_cnt;
   unsigned char     diag_3_cnt;
   unsigned char     diag_4_cnt;
   unsigned char     diag_5_cnt_pha;
   unsigned char     diag_5_cnt_phb;
   unsigned char     diag_5_cnt_phc;
   unsigned char     diag_5_cnt_total;
   unsigned char     diag_6_cnt;
   unsigned char     diag_7_cnt;
   unsigned char     diag_8_cnt;
   unsigned char     diag_1_caution :1;
   unsigned char     diag_2_caution :1;
   unsigned char     diag_3_caution :1;
   unsigned char     diag_4_caution :1;
   unsigned char     diag_5_caution :1;
   unsigned char     diag_6_caution :1;
   unsigned char     diag_7_caution :1;
   unsigned char     diag_8_caution :1;
};

class IM_EX_PROT CtiProtocolANSI
{
   public:

      CtiProtocolANSI();
      ~CtiProtocolANSI();

      void getGeneralScanTables( BYTE *ptr );

//...................... testing ......................
      void getTables( BYTE *ptr );
//...................... testing ......................

      int generate( CtiXfer &xfer );
      int decode  ( CtiXfer &xfer, int status );

      bool isTransactionComplete( void );
      void setTransactionComplete( bool done );
      int recvOutbound( OUTMESS  *OutMessage );

      void convertToTable( BYTE *data );
      /*int sendOutbound( OUTMESS *&OutMessage );


      int sendInbound( INMESS *InMessage );
      int recvInbound( INMESS *InMessage );
        */
      CtiANSIApplication &getApplicationLayer( void );

      ULONG getBytesGot( void );
      void setBytesGot( ULONG bytes );

   protected:

   private:

      bool                 _weDone;
      int                  _index;

      States               _currentState;
      States               _previousState;

      CtiANSIApplication   _appLayer;

      BYTE                 *_inBuff;
      BYTE                 *_outBUff;

      BYTE                 *_porterishMessage;
      BYTE                 *_deviceishMessage;

      ULONG                _bytesInGot;

      ANSI_TABLE_WANTS     *_tables;
      WANTS_HEADER         *_header;

      TABLE_00_GEN_CONFIG        *_tableZeroZero;
      TABLE_01_GEN_MFG_ID        *_tableZeroOne;
      TABLE_21_ACTUAL_REGISTER   *_tableTwoOne;
};


#endif // #ifndef __PROT_ANSI_H__
