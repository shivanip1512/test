/*-----------------------------------------------------------------------------*
*
* File:   dev_lgs4
*
* Class:  CtiDeviceLandisGyrS4
* Date:   01/02/2000
*
* Author: David Sutton
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_alm_nloc.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2004/07/23 12:54:28 $
*
* Copyright (c) 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_LGS4_H__
#define __DEV_LGS4_H__
#pragma warning( disable : 4786 )


#include <windows.h>
#include "ctitypes.h"
#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_meter.h"
#include "mgr_point.h"


#define LGS4_TRANSMISSION_ACCEPTED        0x33
#define LGS4_READY_FOR_NEXT_PACKET        0x66
#define LGS4_CHECKSUM_ERROR               0x99
#define LGS4_COMMAND_NOT_ACCEPTED         0xCC
#define LGS4_UNLOCK_CONFIRMATION          0xDD

#define LGS4_RESEND_PREVIOUS_CMD          0xEE


#define LGS4_ENQ1                         0x55
#define LGS4_ENQ2                         0xAA

#define LGS4_LOADPROFILE_BUFFER_SIZE        128

/**********************************
*
*  metrics
*
***********************************
*/
#define LGS4_METRIC_KWH                   0x00
#define LGS4_METRIC_NEGATIVEKWH           0x01
#define LGS4_METRIC_RMSKVARH              0x02  // not valid for firmware newer than 2.11
#define LGS4_METRIC_RMSKVAH               0x03
#define LGS4_METRIC_KQH                   0x04  // not valid for firmware 3.00 or greater
#define LGS4_METRIC_KVARH                 0x06
#define LGS4_METRIC_LEAD_KVARH            0x07
#define LGS4_METRIC_VASQRH                0x08
#define LGS4_METRIC_VBSQRH                0x09
#define LGS4_METRIC_VCSQRH                0x0A
#define LGS4_METRIC_INSQRH                0x0B
#define LGS4_METRIC_IASQRH                0x0C
#define LGS4_METRIC_IBSQRH                0x0D
#define LGS4_METRIC_ICSQRH                0x0E
#define LGS4_METRIC_INPUT1                0x0F
#define LGS4_METRIC_INPUT2                0x10  // valid 3.0 and higher
#define LGS4_METRIC_TD_LAG_KVAH           0x11
#define LGS4_METRIC_TD_LEAD_KVAH          0x12
#define LGS4_METRIC_VOLTAGEA_SAG          0x13
#define LGS4_METRIC_VOLTAGEB_SAG          0x14
#define LGS4_METRIC_VOLTAGEC_SAG          0x15
#define LGS4_METRIC_VOLTAGEA_SWELL        0x16
#define LGS4_METRIC_VOLTAGEB_SWELL        0x17
#define LGS4_METRIC_VOLTAGEC_SWELL        0x18
#define LGS4_METRIC_VOLTAGE_SAG           0x19
#define LGS4_METRIC_VOLTAGE_SWELL         0x1A

#define LGS4_HANGUP                       0x79

#pragma pack(push, lgs4_packing, 1)


  /**********************************
* NOTE:  all commands are based on firmware 3.0 or higher
***********************************
*/

typedef struct
{
    INT      command;
    INT      totalBytesReturned;
    INT      startOffset;
    INT      bytesToRead;
    CHAR     *name;
} LGS4CommandArray;

// structures and constants needed for this meter
typedef struct
{
    UCHAR    minutes;    // 0 - 59
    UCHAR    hours;      // 0 - 23
    UCHAR    dayOfWeek;  // 1 - 7
    UCHAR    year;       // stored 00 - 99
    UCHAR    day;        // 1 - 31
    UCHAR    month;      // 1 -12
} LGS4DateTime_t;



typedef struct
{
    ULONG   pointId;
    DOUBLE  multiplier;
    SHORT   metric;

} LGS4LPPointInfo_t;


typedef struct
{
    USHORT voltageConstant;
    USHORT currentConstant;
} LGS4PhaseInfo_t;

typedef struct
{
   FLOAT    kFactor;
   USHORT   intervalLength;      // saved in minutes
   USHORT   numberOfChannels;
   ULONG    totalKBytesRequested;
   ULONG    totalBytesReceived;
   USHORT   channelMetrics[15];

    LGS4PhaseInfo_t   phaseA;
    LGS4PhaseInfo_t   phaseB;
    LGS4PhaseInfo_t   phaseC;

    USHORT  classCode;
    USHORT  voltageCode;

} LGS4LProfileConfig_t;



typedef struct _LGS4ScanData_t
{
    struct
    {
        LGS4DateTime_t                dateTime;

        UCHAR                         deviceID[15];

        UCHAR                         prevIntervalDemand[2];
        UCHAR                         prevIntervalSelectedMetric[2];
        UCHAR                         prevIntervalPowerFactor[2];
        UCHAR                         averagePowerFactor[2];
        UCHAR                         prevIntervalThirdMetric[2];

        UCHAR                         powerOutages;

        UCHAR                         neutralCurrent[3];
        UCHAR                         phaseACurrent[3];
        UCHAR                         phaseBCurrent[3];
        UCHAR                         phaseCCurrent[3];

        UCHAR                         phaseAVoltage[3];
        UCHAR                         phaseBVoltage[3];
        UCHAR                         phaseCVoltage[3];

        // not decoded as of yet
        UCHAR                         touStates[3];

        UCHAR                         serialNumber[4];

        UCHAR                         rateAkWh[6];
        UCHAR                         rateBkWh[6];
        UCHAR                         rateCkWh[6];
        UCHAR                         rateDkWh[6];
        UCHAR                         rateEkWh[6];
        UCHAR                         totalkWh[6];

        UCHAR                         rateAkMh[6];
        UCHAR                         rateBkMh[6];
        UCHAR                         rateCkMh[6];
        UCHAR                         rateDkMh[6];
        UCHAR                         rateEkMh[6];
        UCHAR                         totalkMh[6];

        LGS4DateTime_t                dateTimeRateAMaxkW;
        LGS4DateTime_t                dateTimeRateBMaxkW;
        LGS4DateTime_t                dateTimeRateCMaxkW;
        LGS4DateTime_t                dateTimeRateDMaxkW;
        LGS4DateTime_t                dateTimeRateEMaxkW;

        UCHAR                         rateAMaxkW[2];
        UCHAR                         rateBMaxkW[2];
        UCHAR                         rateCMaxkW[2];
        UCHAR                         rateDMaxkW[2];
        UCHAR                         rateEMaxkW[2];

        LGS4DateTime_t                dateTimeRateAMaxkM;
        LGS4DateTime_t                dateTimeRateBMaxkM;
        LGS4DateTime_t                dateTimeRateCMaxkM;
        LGS4DateTime_t                dateTimeRateDMaxkM;
        LGS4DateTime_t                dateTimeRateEMaxkM;

        UCHAR                         rateAMaxkM[2];
        UCHAR                         rateBMaxkM[2];
        UCHAR                         rateCMaxkM[2];
        UCHAR                         rateDMaxkM[2];
        UCHAR                         rateEMaxkM[2];

        UCHAR                         rateACoincidentDemand[2];
        UCHAR                         rateBCoincidentDemand[2];
        UCHAR                         rateCCoincidentDemand[2];
        UCHAR                         rateDCoincidentDemand[2];
        UCHAR                         rateECoincidentDemand[2];

        UCHAR                         rateAPowerFactorAtMax[2];
        UCHAR                         rateBPowerFactorAtMax[2];
        UCHAR                         rateCPowerFactorAtMax[2];
        UCHAR                         rateDPowerFactorAtMax[2];
        UCHAR                         rateEPowerFactorAtMax[2];

        UCHAR                         kFactor[3];
        UCHAR                         subIntervals;
        UCHAR                         filler3;
        UCHAR                         numberOfSubIntervals;

        UCHAR                         selectableMetric;
        UCHAR                         billingMetric;
        UCHAR                         thirdMetric;

        LGS4DateTime_t                dateTimeMaxkM3;
        UCHAR                         maxkM3[2];
        UCHAR                         powerFactorAtMaxkM3[2];
        UCHAR                         coincidentkM3atMaxDemand[2];
        UCHAR                         totalkMh3[6];


    } Byte;

    struct
    {
        LGS4DateTime_t                dateTime;
        CHAR                          deviceID[15];

        USHORT                        powerOutages;

        DOUBLE                        prevIntervalDemand;
        DOUBLE                        prevIntervalSelectedMetric;
        DOUBLE                        prevIntervalThirdMetric;

        FLOAT                         neutralCurrent;
        FLOAT                         phaseACurrent;
        FLOAT                         phaseBCurrent;
        FLOAT                         phaseCCurrent;

        FLOAT                         phaseAVoltage;
        FLOAT                         phaseBVoltage;
        FLOAT                         phaseCVoltage;

        ULONG                         touStates;

        ULONG                         serialNumber;

        DOUBLE                        rateAkWh;
        DOUBLE                        rateBkWh;
        DOUBLE                        rateCkWh;
        DOUBLE                        rateDkWh;
        DOUBLE                        rateEkWh;
        DOUBLE                        totalkWh;

        DOUBLE                        rateAkMh;
        DOUBLE                        rateBkMh;
        DOUBLE                        rateCkMh;
        DOUBLE                        rateDkMh;
        DOUBLE                        rateEkMh;
        DOUBLE                        totalkMh;

        LGS4DateTime_t                dateTimeRateAMaxkW;
        LGS4DateTime_t                dateTimeRateBMaxkW;
        LGS4DateTime_t                dateTimeRateCMaxkW;
        LGS4DateTime_t                dateTimeRateDMaxkW;
        LGS4DateTime_t                dateTimeRateEMaxkW;

        DOUBLE                        rateAMaxkW;
        DOUBLE                        rateBMaxkW;
        DOUBLE                        rateCMaxkW;
        DOUBLE                        rateDMaxkW;
        DOUBLE                        rateEMaxkW;

        LGS4DateTime_t                dateTimeRateAMaxkM;
        LGS4DateTime_t                dateTimeRateBMaxkM;
        LGS4DateTime_t                dateTimeRateCMaxkM;
        LGS4DateTime_t                dateTimeRateDMaxkM;
        LGS4DateTime_t                dateTimeRateEMaxkM;

        DOUBLE                        rateAMaxkM;
        DOUBLE                        rateBMaxkM;
        DOUBLE                        rateCMaxkM;
        DOUBLE                        rateDMaxkM;
        DOUBLE                        rateEMaxkM;

        DOUBLE                        rateACoincidentDemand;
        DOUBLE                        rateBCoincidentDemand;
        DOUBLE                        rateCCoincidentDemand;
        DOUBLE                        rateDCoincidentDemand;
        DOUBLE                        rateECoincidentDemand;

        FLOAT                         rateAPowerFactorAtMax;
        FLOAT                         rateBPowerFactorAtMax;
        FLOAT                         rateCPowerFactorAtMax;
        FLOAT                         rateDPowerFactorAtMax;
        FLOAT                         rateEPowerFactorAtMax;

        FLOAT                         kFactor;
        USHORT                        demandInterval;

        USHORT                        selectableMetric;
        USHORT                        billingMetric;
        USHORT                        thirdMetric;

        LGS4DateTime_t                dateTimeMaxkM3;
        DOUBLE                        maxkM3;
        FLOAT                         powerFactorAtMaxkM3;
        DOUBLE                        coincidentkM3atMaxDemand;
        DOUBLE                        totalkMh3;


    } Real;


    BOOL     bDataIsReal;

} LGS4ScanData_t;


typedef struct
{
   ULONG                porterLPTime;
   BOOL                 lastLPMessage;
   LGS4DateTime_t       meterDate;
   LGS4LProfileConfig_t configuration;
   BYTE                 loadProfileData[300];
} LGS4LoadProfile_t;


#pragma pack(pop, lgs4_packing)     // Restore the prior packing alignment..

class IM_EX_DEVDB CtiDeviceLandisGyrS4 : public CtiDeviceMeter
{
protected:


private:


    INT            iCommandPacket;

    INT            iRetryAttempts;

    ULONG          iTotalByteCount;

    BYTE           *iDataBuffer;

    BYTE           *iLoadProfileBuffer;
    BYTE           *iLoadProfileConfig;
    ULONG          iCurrentLPDate; // current day we are one for load profile
    ULONG          iPreviousLPDate;
    INT            iCurrentLPChannel;
    INT            iCurrentLPInterval;

    ULONG          iPowerDownTime;
    ULONG          iPowerUpTime;
    ULONG          iPowerUpDate;

    ULONG          iNewDate;
    ULONG          iOldTime;
    BOOL           iUpdateLPFlag;

    CtiDeviceLandisGyrS4 & operator=(const CtiDeviceLandisGyrS4 & aRef)
    {
        cout << __FILE__ << " = operator is invalid for this device" << endl;
        return *this;
    }

    CtiDeviceLandisGyrS4 (const CtiDeviceLandisGyrS4 & aRef)
    {
        cout << __FILE__ << " copy constructor is invalid for this device" << endl;
    }

public:

    typedef CtiDeviceMeter Inherited;

    CtiDeviceLandisGyrS4(BYTE         *dataPtr  = NULL,
                         ULONG         cnt=0):
//                        BYTE          *lpPtr = NULL) :

    iDataBuffer(dataPtr),
    iTotalByteCount (cnt),
    iLoadProfileBuffer(NULL),
    iLoadProfileConfig(NULL),
    iCommandPacket(0)
    {
/*    if (lpPtr != NULL)
       {
           iLoadProfileConfig = (BYTE *)&(((LGS4LoadProfile_t *)iLoadProfileBuffer)->configuration);
       }
       else
           iLoadProfileConfig = NULL;
*/
    }


    virtual ~CtiDeviceLandisGyrS4 ()
    {
        if (iDataBuffer != NULL)
        {
            delete []iDataBuffer;
            iDataBuffer = NULL;
        }

        if (iLoadProfileConfig != NULL)
        {
            iLoadProfileConfig = NULL;
        }

        if (iLoadProfileBuffer != NULL)
        {
            delete []iLoadProfileBuffer;
            iLoadProfileBuffer = NULL;
        }
    }



    ULONG                   getTotalByteCount() const;
    CtiDeviceLandisGyrS4&   setTotalByteCount( ULONG c );

    INT                     getRetryAttempts () const;
    CtiDeviceLandisGyrS4&   setRetryAttempts (INT aRetry);

    INT                     getCommandPacket () const;
    CtiDeviceLandisGyrS4&   setCommandPacket (INT aCommand);

    ULONG                   getPowerDownTime() const;
    CtiDeviceLandisGyrS4&   setPowerDownTime( ULONG c );

    ULONG                   getPowerUpTime() const;
    CtiDeviceLandisGyrS4&   setPowerUpTime( ULONG c );

    ULONG                   getPowerUpDate() const;
    CtiDeviceLandisGyrS4&   setPowerUpDate( ULONG c );

    INT getCurrentLPChannel () const;
    CtiDeviceLandisGyrS4& setCurrentLPChannel (INT aChannel);

    INT getCurrentLPInterval () const;
    CtiDeviceLandisGyrS4& setCurrentLPInterval (INT aInterval);

    ULONG getCurrentLPDate () const;
    CtiDeviceLandisGyrS4& setCurrentLPDate (ULONG aDate);

    ULONG                   getNewDate() const;
    CtiDeviceLandisGyrS4&   setNewDate( ULONG c );

    ULONG                   getPreviousLPDate() const;
    CtiDeviceLandisGyrS4&   setPreviousLPDate( ULONG c );

    ULONG                   getOldTime() const;
    CtiDeviceLandisGyrS4&   setOldTime( ULONG c );

    BOOL                    isLPTimeUpdateFlag() const;
    CtiDeviceLandisGyrS4&   setLPTimeUpdateFlag( BOOL c );


    INT ResultDisplay (INMESS *InMessage);

    /*
     *  These guys initiate a scan based upon the type requested.
     */

    virtual INT GeneralScan( CtiRequestMsg              *pReq,
                             CtiCommandParser           &parse,
                             OUTMESS                   *&OutMessage,
                             RWTPtrSlist< CtiMessage >  &vgList,
                             RWTPtrSlist< CtiMessage >  &retList,
                             RWTPtrSlist< OUTMESS >     &outList,
                             INT                         ScanPriority );

    virtual INT ResultDecode( INMESS                    *InMessage,
                              RWTime                    &TimeNow,
                              RWTPtrSlist< CtiMessage > &vgList,
                              RWTPtrSlist< CtiMessage > &retList,
                              RWTPtrSlist< OUTMESS >    &outList );
    virtual INT ErrorDecode ( INMESS                    *InMessage,
                              RWTime                    &TimeNow,
                              RWTPtrSlist< CtiMessage > &vgList,
                              RWTPtrSlist< CtiMessage > &retList,
                              RWTPtrSlist<OUTMESS>      &outList );

    // interrogation routines
    virtual INT decodeResponseHandshake  ( CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList );
    virtual INT decodeResponse           ( CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList );
    virtual INT decodeResponseScan       ( CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList );
    virtual INT decodeResponseLoadProfile( CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList );

    virtual INT generateCommandHandshake  ( CtiXfer &Transfer, RWTPtrSlist< CtiMessage > &traceList );
    virtual INT generateCommand           ( CtiXfer &Transfer, RWTPtrSlist< CtiMessage > &traceList );
    virtual INT generateCommandScan       ( CtiXfer &Transfer, RWTPtrSlist< CtiMessage > &traceList );
    virtual INT generateCommandLoadProfile( CtiXfer &Transfer, RWTPtrSlist< CtiMessage > &traceList );

    virtual INT reformatDataBuffer ( BYTE *aInMessBuffer, ULONG &aBytesReceived );
    virtual INT copyLoadProfileData( BYTE *aInMessBuffer, ULONG &aTotalBytes );

    virtual INT allocateDataBins( OUTMESS *outMess );
    virtual INT freeDataBins( );

    virtual INT decodeResultScan( INMESS                    *InMessage,
                                  RWTime                    &TimeNow,
                                  RWTPtrSlist< CtiMessage > &vgList,
                                  RWTPtrSlist< CtiMessage > &retList,
                                  RWTPtrSlist< OUTMESS >    &outList );
    virtual INT decodeResultLoadProfile( INMESS                    *InMessage,
                                         RWTime                    &TimeNow,
                                         RWTPtrSlist< CtiMessage > &vgList,
                                         RWTPtrSlist< CtiMessage > &retList,
                                         RWTPtrSlist< OUTMESS >    &outList );

    virtual BOOL verifyAndAddPointToReturnMsg( LONG          aPointID,
                                               DOUBLE        aValue,
                                               USHORT        aQuality,
                                               RWTime        aTime,
                                               CtiReturnMsg *aReturnMsg,
                                               USHORT        aIntervalType=0,
                                               RWCString     aValReport=RWCString() );

    virtual BOOL insertPointIntoReturnMsg( CtiMessage   *aDataPoint,
                                           CtiReturnMsg *aReturnMsg );


    LONG findLPDataPoint( LGS4LPPointInfo_t &point,
                          USHORT             aMetric );

    DOUBLE calculateIntervalData( USHORT             aInterval,
                                  LGS4LoadProfile_t *aLPConfig,
                                  LGS4LPPointInfo_t  aLPPoint);


    BOOL getMeterDataFromScanStruct( int             aOffset,
                                     DOUBLE         &aValue,
                                     RWTime         &peak,
                                     LGS4ScanData_t *aScanData);


    void syncAppropriateTime( ULONG seconds );

};

#endif // #ifndef __DEV_LGS4_H__

