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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/03/13 19:35:48 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_ANSI_H__
#define __PROT_ANSI_H__
#pragma warning( disable : 4786)


#include <windows.h>
#include <rw\cstring.h>

#include "ansi_application.h"
#include "ansi_billing_table.h"
#include "std_ansi_tbl_zero_zero.h"
#include "std_ansi_tbl_zero_one.h"
#include "std_ansi_tbl_one_zero.h"
#include "std_ansi_tbl_one_one.h"
#include "std_ansi_tbl_one_two.h"
#include "std_ansi_tbl_one_three.h"
#include "std_ansi_tbl_one_four.h"
#include "std_ansi_tbl_one_five.h"
#include "std_ansi_tbl_one_six.h"
#include "std_ansi_tbl_two_one.h"
#include "std_ansi_tbl_two_two.h"
#include "std_ansi_tbl_two_three.h"

//325
//#include "std_ansi_tbl_five_five.h"
#include "std_ansi_tbl_five_two.h"

#define UINT64             __int64 //FIXME - figure out how to get a uint64
#define BCD                unsigned char


//class CtiAnsiTableZeroZero;

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
#pragma pack( push, 1)

struct TABLE_27_PRESENT_REGISTER_SELECTION
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

/*
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
*/

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

#pragma pack( pop )

class IM_EX_PROT CtiProtocolANSI
{
   public:

      CtiProtocolANSI();
      ~CtiProtocolANSI();

      void getGeneralScanTables( BYTE *ptr );

      void getBillingTables( BYTE *ptr );

      void getTables( BYTE *ptr, int numTables );
      int getTableSize( int tableID );
      int getTotalBillingSize( void );

      int generate( CtiXfer &xfer );
      int decode  ( CtiXfer &xfer, int status );

      bool isTransactionComplete( void );
      void setTransactionComplete( bool done );
      int recvOutbound( OUTMESS  *OutMessage );

      void convertToTable( BYTE *data, int numBytes );
//      int sendOutbound( OUTMESS *&OutMessage );


      int sendInbound( INMESS *InMessage );
//      int recvInbound( INMESS *InMessage );
      CtiANSIApplication &getApplicationLayer( void );

      ULONG getBytesGot( void );
      void setBytesGot( ULONG bytes );

      void processBillingTables( void );
      void processLoadProfileTables( void );



   protected:

   private:

      bool                             _weDone;
      int                              _index;
      int                              _numberOfTablesToGet;
      int                              _numberOfTablesReceived;

      States                           _currentState;
      States                           _previousState;

      CtiANSIApplication               _appLayer;

      BYTE                             *_inBuff;
      BYTE                             *_outBuff;

//      BYTE                             *_porterishMessage;
//      BYTE                             *_deviceishMessage;
      BYTE                             _tableList[100];        //I suppose this could be up to 2048 (max ansi tables), but I doubt that's ness...

      ULONG                            _bytesInGot;

      ANSI_TABLE_WANTS                 *_tables;
      WANTS_HEADER                     *_header;

      CtiAnsiBillingTable              *_ansiBillingTable;

      CtiAnsiTableZeroZero             *_tableZeroZero;
      CtiAnsiTableZeroOne              *_tableZeroOne;
      CtiAnsiTableOneZero              *_tableOneZero;
      CtiAnsiTableOneOne               *_tableOneOne;
      CtiAnsiTableOneTwo               *_tableOneTwo;
      CtiAnsiTableOneThree             *_tableOneThree;
      CtiAnsiTableOneFour              *_tableOneFour;
      CtiAnsiTableOneFive              *_tableOneFive;
      CtiAnsiTableOneSix               *_tableOneSix;
      CtiAnsiTableTwoOne               *_tableTwoOne;
      CtiAnsiTableTwoTwo               *_tableTwoTwo;
      CtiAnsiTableTwoThree             *_tableTwoThree;
      CtiAnsiTableFiveTwo              *_tableFiveTwo;


};


#endif // #ifndef __PROT_ANSI_H__
