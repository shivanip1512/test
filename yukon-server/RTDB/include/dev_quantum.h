#pragma once

#include <windows.h>

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_meter.h"
#include "dev_schlum.h"

#define QUANTUM_MAX_MMRECORD_SIZE  1547

#pragma pack(push, schlumberger_packing, 1)

typedef struct
{
    BYTE    softwareRevision;
    BYTE    currentRecord[3];
    BYTE    currentInterval;
    BYTE    channelProgram[48];
    BYTE    numChannels;
    BYTE    mmBitSize;
    BYTE    numIntervalsInRecord;
    BYTE    intervalLength;
    BYTE    mmStart[3];
    BYTE    mmEnd[3];
    BYTE    realTime[7];
    BYTE    mmPhaseTable[16];
//    BYTE    toDST[3];
//    BYTE    fromDST[3];
    BYTE    regMultInstW[4];
    BYTE    regMultInstV[4];
    BYTE    regMultInstA[4];
    BYTE    kt[2];
    BYTE    regMultAmpDemand[5];
    BYTE    programTable[128];
    BYTE    demandIntervalSetup[2];
    BYTE    regMultipliers[20];
} QuantumRawConfigData_t;

typedef struct
{
    QuantumRawConfigData_t configData;
    BYTE    programmedRegisters[192];
} QuantumRawScanData_t;

typedef struct
{
    INT     softwareRevision;
    LONG    currentRecord;
    INT     currentInterval;
    struct
    {
        INT regNum;
        INT channelWeight;
    }       channelProgram[16];
    INT     numChannels;
    INT     mmBitSize;
    INT     numIntervalsInRecord;
    INT     intervalLength;
    LONG    mmStart;
    LONG    mmEnd;
    struct
    {
        INT dayOfWeek;
        INT year;
        INT month;
        INT dayOfMonth;
        INT hour;
        INT minute;
        INT second;
    }       realTime;
    INT     mmPhaseTable[16];
/*    struct
    {
        INT month;
        INT day;
        INT hour;
    }       toDST;
    struct
    {
        INT month;
        INT day;
        INT hour;
    }       fromDST;  */
    FLOAT   regMultInstW;
    FLOAT   regMultInstV;
    FLOAT   regMultInstA;
    FLOAT   kt;
    FLOAT   regMultAmpDemand;
    struct
    {
        INT regNum;
        INT nonRegNum;
        INT phase;
        INT displayMultiplier;
        INT decimals;
        INT dataFieldWidth;
        INT isPulse;
    }       programTable[32];
    INT     demandIntervalLength;
    INT     demandIntervalSubintervals;
    FLOAT   regMultDemand;
    FLOAT   regMultVSq;
    FLOAT   regMultASq;
    FLOAT   regMultEnergy;
} QuantumConfigData_t;

typedef struct
{
    QuantumConfigData_t configData;
    FLOAT               programmedRegisters[32];
} QuantumScanData_t;

typedef struct
{
    INT     year;
    INT     month;
    INT     day;
    INT     hour;
} QuantumRecordTime_t;

typedef struct
{
    QuantumRawConfigData_t configData;
    ULONG                  timestamp;      //  this is here because because the time/date in the record isn't updated until the record is completed
                                           //    we'll likely be getting many incomplete records during our LP-snagging days.
    LONG                   recordAddress;  //  and to that end, here's the address of the current record.  that way, we don't blindly spit out
                                           //    the empty parts of an incomplete record - we can check to see if we're on the last record.
    BYTE                   MMBuffer[QUANTUM_MAX_MMRECORD_SIZE];
}   QuantumLoadProfileMessage_t;                    // To fit in a DIALREPLY we cannot be > 2kB

#pragma pack(pop, schlumberger_packing)     // Restore the prior packing alignment..

typedef struct
{
    INT     regNum;
    INT     phase;
    INT     loadProfile;
    INT     CTIOffset;
}   QuantumRegisterMappings_t;


class IM_EX_DEVDB CtiDeviceQuantum : public CtiDeviceSchlumberger
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceQuantum(const CtiDeviceQuantum&);
    CtiDeviceQuantum& operator=(const CtiDeviceQuantum&);

    typedef CtiDeviceSchlumberger Inherited;

    INT   _commandPacket;
    ULONG _basePageStart;
    ULONG _currentRecordTime;
    LONG  _currentRecordLocation;

    LONG getPreviousRecordLocation( LONG currentRecordAddress );
    LONG getNextRecordLocation( LONG currentRecordAddress );

    void translateQuantumConfigData( const QuantumRawConfigData_t *rawScan, QuantumConfigData_t *translated );
    void translateQuantumProgrammedRegisters( const QuantumRawScanData_t *rawScan, QuantumConfigData_t *translated, FLOAT *programmedRegisters );

    LONG  bytesToLong( const BYTE *toConvert, INT numBytes );
    FLOAT bytesToFloat( const BYTE *toConvert, INT numBytes );
    FLOAT bcdToFloat( const BYTE *toConvert, INT numBytes );
    FLOAT registerToFloat( const BYTE *rawReg, QuantumConfigData_t *translated, INT programmedRegNum );

protected:

public:

    // default constructor that takes 2 optional parameters
    CtiDeviceQuantum( BYTE         *dataPtr  = NULL,
                      BYTE         *mmPtr  = NULL,
                      BYTE         *timeDatePtr = NULL,
                      BYTE         *mmlProfilePtr = NULL,
                      BYTE         *mmlProfileInputPtr = NULL,
                      BYTE         *lProfilePtr = NULL,
                      ULONG        totalByteCount = 0 ) :
        CtiDeviceSchlumberger( dataPtr,
                               mmPtr,
                               timeDatePtr,
                               mmlProfilePtr,
                               mmlProfileInputPtr,
                               lProfilePtr,
                               totalByteCount )
    {
       setRetryAttempts( SCHLUMBERGER_RETRIES );
    }

    INT               getCommandPacket( ) const;
    CtiDeviceQuantum &setCommandPacket( INT aCmd );

    ULONG             getBasePageStart( ) const;
    CtiDeviceQuantum &setBasePageStart( ULONG pageStart );


   /*
    *  These guys initiate a scan based upon the type requested.
    */

    YukonError_t GeneralScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,
                             INT ScanPriority = MAXPRIORITY - 4 ) override;

   // interrogation routines
   YukonError_t generateCommandHandshake  ( CtiXfer &Transfer, CtiMessageList &traceList ) override;
   YukonError_t generateCommand           ( CtiXfer &Transfer, CtiMessageList &traceList ) override;
   YukonError_t generateCommandScan       ( CtiXfer &Transfer, CtiMessageList &traceList ) override;
   YukonError_t generateCommandLoadProfile( CtiXfer &Transfer, CtiMessageList &traceList ) override;
   YukonError_t generateCommandSelectMeter( CtiXfer &Transfer, CtiMessageList &traceList ) override;

   YukonError_t decodeResponse           ( CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList ) override;
   YukonError_t decodeResponseHandshake  ( CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList ) override;
   YukonError_t decodeResponseScan       ( CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList ) override;
   YukonError_t decodeResponseSelectMeter( CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList ) override;
   YukonError_t decodeResponseLoadProfile( CtiXfer &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList ) override;

   INT reformatDataBuffer ( BYTE *aInMessBuffer, ULONG &aBytesReceived ) override;
   INT copyLoadProfileData( BYTE *aInMessBuffer, ULONG &aTotalBytes ) override;

   INT allocateDataBins( OUTMESS *outMess ) override;

   INT decodeResultScan       ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;
   INT decodeResultLoadProfile( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;

   BOOL getMeterDataFromScanStruct( int aOffset, DOUBLE &aValue, CtiTime &peak, QuantumScanData_t *aScanData );
};
