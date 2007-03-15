
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
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/prot_ansi.h-arc  $
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2007/03/15 17:46:36 $
*    History: 
      $Log: prot_ansi.h,v $
      Revision 1.16  2007/03/15 17:46:36  jrichter
      Last Interval Quadrant KVar readings reporting back correctly from present value table 28.

      Revision 1.15  2006/03/31 16:18:32  jrichter
      BUG FIX & ENHANCEMENT:  fixed a memory leak (multiple allocations of lpBlocks, but only one deallocation), added quality retrieval.

      Revision 1.14  2005/12/20 17:19:58  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.13  2005/12/12 20:34:30  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.12.2.1  2005/12/12 19:51:02  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.12  2005/09/29 21:19:24  jrichter
      Merged latest 3.1 changes to head.
      Revision 1.10.2.2  2005/07/27 19:28:01  alauinger
      merged from the head 20050720


      Revision 1.10.2.1  2005/07/14 22:27:02  jliu
      RWCStringRemoved

      Revision 1.11  2005/06/16 19:18:00  jrichter
      Sync ANSI code with 3.1 branch!

      Revision 1.10  2005/03/14 21:44:16  jrichter
      updated with present value regs, batterylife info, corrected quals, multipliers/offsets, corrected single precision float define, modifed for commander commands, added demand reset

      Revision 1.9  2005/02/10 23:23:58  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.8  2005/01/25 18:33:51  jrichter
      added present value tables for kv2 and sentinel for voltage, current, freq, pf, etc..meter info

      Revision 1.7  2004/12/10 21:58:42  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.6  2004/09/30 21:37:19  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.5  2004/04/22 21:12:54  dsutton
      Last known revision DLS

      Revision 1.4  2003/04/25 15:12:29  dsutton
      This is now base protocol class for every ansi type meter

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <rw/ordcltn.h>
#include <list>

#include "dllbase.h"
#include "ansi_application.h"
#include "ansi_datalink.h"
#include "ansi_billing_table.h"
#include "std_ansi_tbl_base.h"
#include "std_ansi_tbl_zero_zero.h"
#include "std_ansi_tbl_zero_one.h"
#include "std_ansi_tbl_zero_eight.h"
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
#include "std_ansi_tbl_two_five.h"
#include "std_ansi_tbl_two_seven.h"
#include "std_ansi_tbl_two_eight.h"
#include "std_ansi_tbl_three_one.h"
#include "std_ansi_tbl_three_two.h"
#include "std_ansi_tbl_three_three.h"
#include "std_ansi_tbl_five_one.h"
#include "std_ansi_tbl_five_two.h"
#include "std_ansi_tbl_six_one.h"
#include "std_ansi_tbl_six_two.h"
#include "std_ansi_tbl_six_three.h"
#include "std_ansi_tbl_six_four.h"
//#include "std_ansi_tbl_five_five.h"

#define UINT64             __int64 //FIXME - figure out how to get a uint64
#define BCD                unsigned char

#define FLOAT64

using std::list;
using std::endl;

//converters
//these aren't needed anymore


typedef union
{
   UCHAR    ch[12];
} BYTEARRAY12;

typedef union
{
   UCHAR    ch[6];
} BYTEARRAY;

typedef union
{
   UCHAR    ch[6];
   double   int48;
} BYTEINT48;

typedef union
{
   UCHAR    ch[4];
   long     int32;
} BYTEINT32;


struct WANTS_HEADER
{
   unsigned long  lastLoadProfileTime;
   int            numTablesRequested;
   int            command;
};

#define ANSI_TABLE_TYPE_STANDARD         0
#define ANSI_TABLE_TYPE_MANUFACTURER     1
#define ANSI_OPERATION_READ              10
#define ANSI_OPERATION_WRITE             11



/*struct TBL_IDB_BFLD
{
   unsigned char   tbl_proc_nbr:11;
   unsigned char   std_vs_mfg_flag:1;
   unsigned char   selector:4;
};*/

struct REQ_DATA_RCD
{
    TBL_IDB_BFLD proc;
    UINT8          seq_nbr;
    union PARMS
    {
      struct PARM0
      {  
          //no parms
      }p0;
      struct PARM1
      {
          //no parms
      }p1;
      struct PARM2
      {
          //no parms
      }p2;
      struct PARM3
      {
          //no parms
      }p3;
      struct PARM4
      {
          UINT8 tbl_list;
      }p4;
      struct PARM5
      {
          UINT8 tbl_list;
          UINT16 entries_read;
      }p5;
      struct PARM6
      {
         // ED_MODE_BFLD ed_mode;
      }p6;
      struct PARM7
      {
        //  ED_STD_STATUS1_BFLD ed_std_status_1;
         // ED_STD_STATUS2_BFLD ed_std_status_2;
      }p7;
      struct PARM8
      {
          //ED_MFG_STATUS_RCD ed_mfg_status;
      }p8;
      struct PARM9
      {
         // ACTION_FLAG_BFLD action_flag;
          UINT8 action_flag;
      }p9;
      struct PARM10
      {
         // ACTION_FLAG_BFLD action_flag;
      }p10;
      struct PARM11
      {
         // ACTION_FLAG_BFLD action_flag;
      }p11;
      struct PARM12
      {
         // ACTION_FLAG_BFLD action_flag;
      }p12;
      struct PARM13
      {
         // ACTION_FLAG_BFLD action_flag;
      }p13;
      struct PARM14
      {
         // ACTION_FLAG_BFLD action_flag;
      }p14;
      struct PARM15
      {
         // ACTION_FLAG_BFLD action_flag;
      }p15;
      struct PARM16
      {
         // ACTION_FLAG_BFLD action_flag;
      }p16;
      struct PARM17
      {
         // ACTION_FLAG_BFLD action_flag;
      }p17;
      struct PARM_M22
      {
         UINT32 time;
      }pm22;
    }u;

};
//=========================================================================================================================================
//tables defined by the ansi standard
//=========================================================================================================================================
#pragma pack( push, 1)
/*
struct TABLE_27_PRESENT_REGISTER_SELECTION
{
   unsigned char        present_demand_select[255];
   unsigned char        present_value_select[255];
};
*/

//this one's usable on both sides
struct ANSI_TABLE_WANTS
{
   short   tableID;
   int   tableOffset;
   unsigned int   bytesExpected;
   BYTE  type;
   BYTE  operation;
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

#pragma pack( pop )

class IM_EX_PROT CtiProtocolANSI
{
   public:
      CtiProtocolANSI();
      virtual ~CtiProtocolANSI();

      typedef enum
      {
          generalScan = 0,
          generalPilScan,
          demandReset
      } ANSI_SCAN_OPERATION;

      void reinitialize( void );
      void destroyMe( void );

      bool generate( CtiXfer &xfer );
      bool decode  ( CtiXfer &xfer, int status );

      bool isTransactionComplete( void );
      bool isTransactionFailed( void );
      int recvOutbound( OUTMESS  *OutMessage );

      void convertToTable( );

      int sendCommResult( INMESS *InMessage );
        void receiveCommResult( INMESS *InMessage );
    void buildWantedTableList( BYTE *aPtr);


    CtiANSIApplication &getApplicationLayer( void );
    void updateBytesExpected( );
    int sizeOfNonIntegerFormat( int aFormat );

    int sizeOfSTimeDate( void );
    int sizeOfLTimeDate( void );

    // pure virtual functions
    virtual void destroyManufacturerTables( void )=0;
    virtual void convertToManufacturerTable( BYTE *data, BYTE numBytes, short aTableID )=0;
    virtual int calculateLPDataBlockStartIndex(ULONG lastLPTime) = 0;
    virtual int calculateLPDataBlockSize(int numChans) = 0;
    virtual int calculateLPLastDataBlockSize(int numChans, int numIntvlsLastDataBlock) = 0;
    virtual void setAnsiDeviceType() = 0;
    virtual int snapshotData() = 0;
    virtual int batteryLifeData() = 0;
    virtual int getGoodBatteryReading()=0;
    virtual int getCurrentBatteryReading()=0;
    virtual int getDaysOnBatteryReading()=0;
    virtual bool retreiveKV2PresentValue( int offset, double *value )=0;

    /**** JULIE *****/
    bool retreiveSummation( int offset, double *value );
    bool retreiveDemand( int offset, double *value, double *time );
    bool retreiveFrozenSummation( int offset, double *value, double *time );
    bool retreiveFrozenDemand( int offset, double *value, double *time );
    bool retreivePresentValue( int offset, double *value );
    bool retreivePresentDemand( int offset, double *value );
    bool retreiveLPDemand( int offset, int dataSet );
    bool retreiveBatteryLife(int x, double *value);
    bool retreiveMeterTimeDiffStatus( int offset, double *status );
    double getLPValue( int index );
    ULONG getLPTime( int index );
    UINT8 getLPQuality( int index );

    int getUnitsOffsetMapping(int offset);
    int getQuadrantOffsetMapping(int offset);
    int getRateOffsetMapping(int offset);
    int getSegmentationOffsetMapping(int offset);
    int translateAnsiQualityToYukon(int ansiQuality );


    int getSizeOfLPDataBlock(int dataSetNbr);
    int getSizeOfLastLPDataBlock(int dataSetNbr);
    int getSizeOfLPReadingsRcd();
    int getSizeOfLPIntSetRcd(int dataSetNbr);
    int getSizeOfLPIntFmtRcd(int dataSetNbr);

    unsigned short getTotalWantedLPBlockInts();

    int getNbrValidIntvls();
    int getNbrValidBlks();
    int getMaxIntervalTime();
    int getNbrIntervalsPerBlock();
    int getLastBlockIndex();
    int getNbrBlksSet();

    const string& getAnsiDeviceName() const;
    void setAnsiDeviceName(const string& devName);

    int proc09RemoteReset(UINT8 actionFlag);
    int proc22LoadProfileStartBlock( void );

    void setCurrentAnsiWantsTableValues(short tableID,int tableOffset, unsigned int bytesExpected,BYTE  type, BYTE operation);
    void setLastLoadProfileTime(LONG lastLPTime);
    int getWriteSequenceNbr( void );

    unsigned long getlastLoadProfileTime(void);


    void setTablesAvailable(unsigned char * stdTblsUsed, int dimStdTblsUsed, 
                            unsigned char * mfgTblsUsed, int dimMfgTblsUsed);


    list< short > getStdTblsAvailable(void);
    list < short > getMfgTblsAvailable(void);
    bool isStdTableAvailableInMeter(short tableNbr);
    bool isMfgTableAvailableInMeter(short tableNbr);

    int getScanOperation(void);
    UINT getParseFlags(void);

    bool forceProcessDispatchMsg();
    bool isTimeUninitialized(double time);
    
    static const CHAR * METER_TIME_TOLERANCE;

   protected:

   private:

      int                              _index;

      CtiANSIApplication               _appLayer;


      ULONG                            _bytesInGot;

      ANSI_TABLE_WANTS                 *_tables;
      WANTS_HEADER                     *_header;

      CtiAnsiBillingTable              *_billingTable;

      CtiAnsiTableZeroZero             *_tableZeroZero;
      CtiAnsiTableZeroOne              *_tableZeroOne;
      CtiAnsiTableZeroEight            *_tableZeroEight;
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
      CtiAnsiTableTwoSeven             *_tableTwoSeven;
      CtiAnsiTableTwoEight             *_tableTwoEight;
      CtiAnsiTableThreeOne             *_tableThreeOne;
      CtiAnsiTableThreeTwo             *_tableThreeTwo;
      CtiAnsiTableThreeThree           *_tableThreeThree;
      CtiAnsiTableFiveOne              *_tableFiveOne;
      CtiAnsiTableFiveTwo              *_tableFiveTwo;
      CtiAnsiTableSixOne               *_tableSixOne;
      CtiAnsiTableSixTwo               *_tableSixTwo;
      CtiAnsiTableSixThree             *_tableSixThree;
      CtiAnsiTableSixFour              *_tableSixFour;
    //  CtiAnsiTableFiveFive             *_tableFiveFive;

      CtiAnsiTableTwoFive             *_frozenRegTable;
      
      bool _validFlag;
      bool _previewTable64;
      bool _entireTableFlag;
      bool _clearMfgTables;
      int _nbrLPDataBlksWanted;
      unsigned short _nbrLPDataBlkIntvlsWanted;
      int _nbrFirstLPDataBlkIntvlsWanted;
      int _nbrLastLPDataBlkIntvlsWanted;
      ULONG _timeSinceLastLPTime;

      int _seqNbr;

       double *_lpValues;
       ULONG *_lpTimes;
       UINT8 *_lpQuality;

       string _ansiDevName;

     int _lpNbrLoadProfileChannels;
     int _lpNbrIntvlsLastBlock;
     int _lpNbrValidBlks;
     int _lpNbrIntvlsPerBlock;
     int _lpNbrBlksSet;
     int _lpLastBlockIndex;
     int _lpStartBlockIndex;
     int _lpMaxIntervalTime;
     int _lpBlockSize;
     int _lpOffset;
     int _lpNbrFullBlocks;
     int _lpLastBlockSize;

     list < short > _stdTblsAvailable;
     list < short > _mfgTblsAvailable;

     bool _currentTableNotAvailableFlag;
     bool _requestingBatteryLifeFlag;
     bool _invalidLastLoadProfileTime;
     bool _forceProcessDispatchMsg;

     ANSI_SCAN_OPERATION _scanOperation;  //General Scan, Demand Reset, 
     UINT _parseFlags;
};


#endif // #ifndef __PROT_ANSI_H__
