#pragma once

#include <list>

#include "dllbase.h"
#include "ansi_application.h"
#include "ansi_datalink.h"
#include "ansi_billing_table.h"
#include "std_ansi_tbl_base.h"
#include "std_ansi_tbl_00.h"
#include "std_ansi_tbl_01.h"
#include "std_ansi_tbl_08.h"
#include "std_ansi_tbl_10.h"
#include "std_ansi_tbl_11.h"
#include "std_ansi_tbl_12.h"
#include "std_ansi_tbl_13.h"
#include "std_ansi_tbl_14.h"
#include "std_ansi_tbl_15.h"
#include "std_ansi_tbl_16.h"
#include "std_ansi_tbl_21.h"
#include "std_ansi_tbl_22.h"
#include "std_ansi_tbl_23.h"
#include "std_ansi_tbl_25.h"
#include "std_ansi_tbl_27.h"
#include "std_ansi_tbl_28.h"
#include "std_ansi_tbl_31.h"
#include "std_ansi_tbl_32.h"
#include "std_ansi_tbl_33.h"
#include "std_ansi_tbl_51.h"
#include "std_ansi_tbl_52.h"
#include "std_ansi_tbl_61.h"
#include "std_ansi_tbl_62.h"
#include "std_ansi_tbl_63.h"
#include "std_ansi_tbl_64.h"


#define UINT64             __int64 //FIXME - figure out how to get a uint64
#define BCD                unsigned char

#define FLOAT64

//converters
//these aren't needed anymore

namespace Cti {
namespace Protocols {
namespace Ansi {

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

//this one's usable on both sides
struct ANSI_TABLE_WANTS
{
   short   tableID;
   unsigned int   tableOffset;
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
typedef enum
{
      Configuration                       = 0,
      GeneralManufacturerIdentification   = 1,
      ProcedureInitiate                   = 7,
      ProcedureResponse                   = 8,
      DataSource                          = 10,
      ActualSourcesLimiting               = 11,
      UnitOfMeasureEntry                  = 12,
      DemandControl                       = 13,
      DataControl                         = 14,
      Constants                           = 15,
      SourceDefinition                    = 16,
      Register                            = 20,
      ActualRegister                      = 21,
      DataSelection                       = 22,
      CurrentRegisterData                 = 23,
      PreviousSeasonData                  = 24,
      FrozenRegisterData                  = 25,
      PresentRegisterSelection            = 27,
      PresentRegisterData                 = 28,
      ActualDisplay                       = 31,
      DisplaySource                       = 32,
      PrimaryDisplayList                  = 33,
      SecondaryDisplayList                = 34,
      ActualTimeAndTOU                    = 51,
      Clock                               = 52,
      ActualLoadProfile                   = 61,
      LoadProfileControl                  = 62,
      LoadProfileStatus                   = 63,
      LoadProfileDataSet1                 = 64,
      LoadProfileDataSet2                 = 65,
      LoadProfileDataSet3                 = 66,
      LoadProfileDataSet4                 = 67,
      Undefined                           = -1
} Tables;

typedef enum
{
    Sentinel_BatteryLifeRequest           = 2049,
    Sentinel_BatteryLifeResponse          = 2050,
    Focus_SetLpReadControl                = 2082,
} MfgTables;



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
          demandReset,
          loopBack
      } ANSI_SCAN_OPERATION;

      void reinitialize( void );
      void destroyMe( void );

      bool generate( CtiXfer &xfer );
      bool decode  ( CtiXfer &xfer, int status );

      void printResults();
      bool handleWriteOperations();
      bool createWriteOperations();

      bool isTransactionComplete( void ) const;
      bool isTransactionFailed( void );
      int recvOutbound( OUTMESS  *OutMessage );

      void convertToTable();


      int sendCommResult( INMESS *InMessage );
      void receiveCommResult( INMESS *InMessage );
      void buildWantedTableList( BYTE *aPtr);


    CtiANSIApplication &getApplicationLayer( void );
    void updateBytesExpected( );
    void updateMfgBytesExpected( );
    int sizeOfNonIntegerFormat( int aFormat );

    int sizeOfSTimeDate( void );
    int sizeOfLTimeDate( void );


    // pure virtual functions
    virtual void destroyManufacturerTables( void );
    virtual void convertToManufacturerTable( BYTE *data, BYTE numBytes, short aTableID );
    virtual int calculateLPDataBlockStartIndex(ULONG lastLPTime);
    virtual void setAnsiDeviceType() = 0;
    virtual bool snapshotData();
    virtual bool batteryLifeData();
    virtual int getGoodBatteryReading();
    virtual int getCurrentBatteryReading();
    virtual int getDaysOnBatteryReading();
    virtual bool retreiveMfgPresentValue( int offset, double *value );

    bool retreiveSummation( int offset, double *value, double *time, bool frozen = false );
    bool retreiveDemand( int offset, double *value, double *time, bool frozen = false );
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

    const std::string& getAnsiDeviceName() const;
    void setAnsiDeviceName(const std::string& devName);

    int proc09RemoteReset(UINT8 actionFlag);
    int proc22LoadProfileStartBlock( void );

    void setCurrentAnsiWantsTableValues(short tableID,int tableOffset, unsigned int bytesExpected,BYTE  type, BYTE operation);
    void setLastLoadProfileTime(LONG lastLPTime);
    int getWriteSequenceNbr( void );

    unsigned long getlastLoadProfileTime(void);
    int getScanOperation(void);
    UINT getParseFlags(void);

    bool forceProcessDispatchMsg();
    bool isTimeUninitialized(double time);
    bool isDataBlockOrderDecreasing();
    int getLastBlockIndex();
    static const CHAR * METER_TIME_TOLERANCE;

   protected:
      bool setLoadProfileVariables();
      void setLoadProfileFullBlocks(long fullBlocks);
      int getNbrValidIntvls();
      int getNbrValidBlks();
      int getMaxIntervalTime();
      int getNbrIntervalsPerBlock();
      int getNbrBlksSet();



   private:
       void prepareApplicationLayer();
       void setTablesAvailable(unsigned char * stdTblsUsed, int dimStdTblsUsed,
       unsigned char * mfgTblsUsed, int dimMfgTblsUsed);
       std::list < short > getStdTblsAvailable(void);
       std::list < short > getMfgTblsAvailable(void);
       bool isStdTableAvailableInMeter(short tableNbr);
       bool isMfgTableAvailableInMeter(short tableNbr);

      int                              _index;

      CtiANSIApplication               _appLayer;


      ULONG                            _bytesInGot;

      ANSI_TABLE_WANTS                 *_tables;
      WANTS_HEADER                     *_header;

      CtiAnsiBillingTable              *_billingTable;

      CtiAnsiTable00  *_table00;
      CtiAnsiTable01  *_table01;
      CtiAnsiTable08  *_table08;
      CtiAnsiTable10  *_table10;
      CtiAnsiTable11  *_table11;
      CtiAnsiTable12  *_table12;
      CtiAnsiTable13  *_table13;
      CtiAnsiTable14  *_table14;
      CtiAnsiTable15  *_table15;
      CtiAnsiTable16  *_table16;
      CtiAnsiTable21  *_table21;
      CtiAnsiTable22  *_table22;
      CtiAnsiTable23  *_table23;
      CtiAnsiTable27  *_table27;
      CtiAnsiTable28  *_table28;
      CtiAnsiTable31  *_table31;
      CtiAnsiTable32  *_table32;
      CtiAnsiTable33  *_table33;
      CtiAnsiTable51  *_table51;
      CtiAnsiTable52  *_table52;
      CtiAnsiTable61  *_table61;
      CtiAnsiTable62  *_table62;
      CtiAnsiTable63  *_table63;
      CtiAnsiTable64  *_table64;

      CtiAnsiTable25  *_frozenRegTable;

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

       std::string _ansiDevName;

     int _lpNbrLoadProfileChannels;
     int _lpNbrIntvlsLastBlock;
     int _lpNbrValidBlks;
     int _lpNbrIntvlsPerBlock;
     int _lpNbrBlksSet;
     int _lpLastBlockIndex;
     int _lpStartBlockIndex;
     int _lpMaxIntervalTime;
     int _lpBlockSize;
     unsigned int _lpOffset;
     int _lpNbrFullBlocks;
     int _lpLastBlockSize;

     BOOL _ansiAbortOperation;
     std::list < short > _stdTblsAvailable;
     std::list < short > _mfgTblsAvailable;

     bool _currentTableNotAvailableFlag;
     bool _requestingBatteryLifeFlag;
     bool _invalidLastLoadProfileTime;
     bool _forceProcessDispatchMsg;

     ANSI_SCAN_OPERATION _scanOperation;  //General Scan, Demand Reset,
     UINT _parseFlags;
};
}}}


