/*-----------------------------------------------------------------------------*
*
* File:   dev_dr87
*
* Class:  CtiDeviceDR87
* Date:   01/02/2000
*
* Author: David Sutton
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_dr87.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2004/07/23 12:54:28 $
*
* Copyright (c) 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_DR87_H__
#define __DEV_DR87_H__
#pragma warning( disable : 4786 )


#include <windows.h>
#include <iostream>
#include <vector>

#include "ctitypes.h"
#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_meter.h"
#include "mgr_point.h"
#include "logger.h"
#include "guard.h"


#define DR87_LOGIN                  0x09
#define DR87_DUMP_MEMORY            0x01
#define DR87_EXECUTE_FUNCTION       0x06
#define DR87_DUMP_TIME              0x02

#define DR87_ACK            0x06
#define DR87_NAK            0x15
#define DR87_PAD            0xff
#define DR87_SYNC           0x16

#define DR87_MSG_COMPLETE   0
#define DR87_MSG_INCOMPLETE 1
#define DR87_BAD_CRC        2
#define DR87_COMM_ERROR     3

#pragma pack(push, dr87_packing, 1)

typedef struct _DR87ScanData_t
{
    USHORT          numberOfChannels;
    DOUBLE          scaleFactor[4];
    DOUBLE          kvalue[4];
    DOUBLE          totalPulses[4];

} DR87ScanData_t;


typedef struct _DR87LPConfig
{
    USHORT          numberOfChannels;
    USHORT          intervalLength;
    DOUBLE          scaleFactor[4];
    DOUBLE          kvalue[4];
    ULONG           lastIntervalTime;
    USHORT          blockSize;
} DR87LPConfig;

typedef struct _DR87LoadProfile_t
{
    ULONG                porterLPTime;
    BOOL                 lastLPMessage;
    RWTime               recorderTime;
    DR87LPConfig         config;
    DOUBLE               channelUsage[4];
    BYTE                 loadProfileData[300];
} DR87LoadProfile_t;

typedef struct
{
    ULONG   pointId;
    DOUBLE  multiplier;
    SHORT   mapping;
} DR87LPPointInfo_t;

#pragma pack(pop, dr87_packing)     // Restore the prior packing alignment..



class CtiDR87PorterSide;  // contained in this file at bottome

class IM_EX_DEVDB CtiDeviceDR87 : public CtiDeviceMeter
{
protected:


    private:

    INT            iRetryAttempts;
    ULONG          iTotalByteCount;
    CtiDR87PorterSide     *iPorterSide;

    BYTE           *iDataBuffer;
    BYTE           *iLoadProfileBuffer;

    vector<int>     iLPPulseVector;
    CtiMeterMachineStates_t    iRequestedState;

    CtiDeviceDR87 & operator=(const CtiDeviceDR87 & aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << __FILE__ << " = operator is invalid for this device" << endl;
        }
        return *this;
    }

    CtiDeviceDR87 (const CtiDeviceDR87 & aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << __FILE__ << " copy constructor is invalid for this device" << endl;
        }
    }

public:

    typedef CtiDeviceMeter Inherited;

    CtiDeviceDR87(BYTE         *aPtr  = NULL,
                  BYTE         *lpPtr  = NULL,
                  ULONG         cnt=0);
    virtual ~CtiDeviceDR87 ();

    CtiMeterMachineStates_t  getRequestedState () const;
    CtiDeviceDR87& setRequestedState (CtiMeterMachineStates_t aState);

    ULONG            getTotalByteCount() const;
    CtiDeviceDR87&   setTotalByteCount( ULONG c );

    INT              getRetryAttempts () const;
    CtiDeviceDR87&   setRetryAttempts (INT aRetry);

    CtiDR87PorterSide *getPorterSide () const;
    CtiDeviceDR87&    setPorterSide ( CtiDR87PorterSide *aTemp);


    BOOL    syncByteHasBeenReceived() const;
    CtiDeviceDR87& setSyncByteReceived(BOOL c);

    BOOL    ackNakByteHasBeenReceived() const;
    CtiDeviceDR87& setAckNakByteReceived(BOOL c);

    BOOL    isDataMsgComplete() const;
    CtiDeviceDR87& setDataMsgComplete(BOOL c);

    INT             getTotalBytesExpected () const;
    CtiDeviceDR87&  setTotalBytesExpected (INT aByteCnt);

    const BYTE*    getWorkBuffer() const;
    BYTE*          getWorkBuffer();

    INT getByteNumber () const;
    CtiDeviceDR87& setByteNumber (INT aByteCnt);

    INT getNumberOfIncompleteMsgs () const;
    CtiDeviceDR87& setNumberOfIncompleteMsgs (INT aNumberOfIncompleteMsgs);

    USHORT getOldestIntervalByteOffset () const;
    CtiDeviceDR87& setOldestIntervalByteOffset (INT aByteOffset);

    USHORT getCurrentByteOffset () const;
    CtiDeviceDR87& setCurrentByteOffset (INT aByteOffset);

    USHORT getMassMemoryStart () const;
    CtiDeviceDR87& setMassMemoryStart (INT aBlockSize);

    USHORT getMassMemoryStop () const;
    CtiDeviceDR87& setMassMemoryStop (INT aBlockSize);

    INT getLogoffFunction () const;
    CtiDeviceDR87& setLogoffFunction (INT aFunc);


    INT ResultDisplay (INMESS *InMessage);

    /*
     *  These guys initiate a scan based upon the type requested.
     */

    virtual INT GeneralScan(CtiRequestMsg *pReq,
                            CtiCommandParser &parse,
                            OUTMESS *&OutMessage,
                            RWTPtrSlist< CtiMessage > &vgList,
                            RWTPtrSlist< CtiMessage > &retList,
                            RWTPtrSlist< OUTMESS > &outList,
                            INT ScanPriority);

    virtual INT ResultDecode(INMESS *InMessage,
                             RWTime &TimeNow,
                             RWTPtrSlist< CtiMessage >   &vgList,
                             RWTPtrSlist< CtiMessage > &retList,
                             RWTPtrSlist< OUTMESS > &outList);
    virtual INT ErrorDecode(INMESS*,
                            RWTime&,
                            RWTPtrSlist< CtiMessage >   &vgList,
                            RWTPtrSlist< CtiMessage > &retList,
                            RWTPtrSlist<OUTMESS> &outList);

    // interrogation routines
    virtual INT decodeResponseHandshake   (CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList);
    virtual INT decodeResponse            (CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList);
    virtual INT decodeResponseScan        (CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList);
    virtual INT decodeResponseLoadProfile (CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList);

    virtual INT generateCommandHandshake   (CtiXfer &Transfer, RWTPtrSlist< CtiMessage > &traceList);
    virtual INT generateCommand            (CtiXfer &Transfer, RWTPtrSlist< CtiMessage > &traceList);
    virtual INT generateCommandScan        (CtiXfer &Transfer, RWTPtrSlist< CtiMessage > &traceList);
    virtual INT generateCommandLoadProfile (CtiXfer &Transfer, RWTPtrSlist< CtiMessage > &traceList);
    virtual INT generateCommandTerminate   (CtiXfer &Transfer, RWTPtrSlist< CtiMessage > &traceList);

    virtual INT reformatDataBuffer (BYTE *aInMessBuffer, ULONG &aBytesReceived);
    virtual INT copyLoadProfileData(BYTE *aInMessBuffer, ULONG &aTotalBytes);

    virtual INT allocateDataBins (OUTMESS *outMess);
    virtual INT freeDataBins ();

    virtual INT   decodeResultScan (INMESS                    *InMessage,
                                    RWTime                    &TimeNow,
                                    RWTPtrSlist< CtiMessage > &vgList,
                                    RWTPtrSlist< CtiMessage > &retList,
                                    RWTPtrSlist< OUTMESS >    &outList);

    virtual INT decodeResultLoadProfile (INMESS                    *InMessage,
                                         RWTime                    &TimeNow,
                                         RWTPtrSlist< CtiMessage > &vgList,
                                         RWTPtrSlist< CtiMessage > &retList,
                                         RWTPtrSlist< OUTMESS >    &outList);
    virtual BOOL verifyAndAddPointToReturnMsg (LONG          aPointID,
                                               DOUBLE        aValue,
                                               USHORT        aQuality,
                                               RWTime        aTime,
                                               CtiReturnMsg *aReturnMsg,
                                               USHORT        aIntervalType=0,
                                               RWCString     aValReport=RWCString());

    virtual BOOL insertPointIntoReturnMsg (CtiMessage   *aDataPoint,
                                           CtiReturnMsg *aReturnMsg);


    INT     checkCRC(BYTE *InBuffer,ULONG InCount);
    USHORT  calculateCRC(UCHAR* buffer, LONG length, BOOL bAdd);
    INT     getMessageInDataStream (CtiXfer  &Transfer,int aCommValue);
    INT     decodeReceivedMessage ();
    INT     fillUploadTransferObject (CtiXfer  &aTransfer, USHORT aCmd, USHORT aStartAddress, USHORT aOffset, USHORT aBytesToRead);
    BOOL    getMeterDataFromScanStruct (int aOffset, DOUBLE &aValue, RWTime &peak, DR87ScanData_t *aScanData);
    LONG    findLPDataPoint (DR87LPPointInfo_t &point, USHORT aMetric);
    INT     calculateBlockSize(void);
};


class CtiDR87PorterSide
{
private:
    BOOL           iSyncByteReceivedFlag;
    BOOL           iAckNakByteReceivedFlag;
    BOOL           iDataMsgCompleteFlag;
    INT            iTotalBytesExpected;
    INT            iByteNumber;             // from the DR87 doc

    USHORT         iOldestIntervalByteOffset;
    USHORT         iCurrentByteOffset;
    USHORT         iMassMemoryStart;
    USHORT         iMassMemoryStop;

    INT            iLogoffFunction;
    INT            iNumberOfIncompleteMsgs;


    BYTE           iWorkBuffer[300];


public:


    CtiDR87PorterSide()
    : iLogoffFunction(-1)
    {
    }

    virtual ~CtiDR87PorterSide ()
    {
    }

    const BYTE*    getWorkBuffer() const
    {
        return iWorkBuffer;
    }

    BYTE*    getWorkBuffer()
    {
        return iWorkBuffer;
    }

    BOOL    syncByteHasBeenReceived() const
    {
        return iSyncByteReceivedFlag;
    }

    CtiDR87PorterSide& setSyncByteReceived(BOOL c)
    {
        iSyncByteReceivedFlag = c;
        return *this;
    }
    BOOL    ackNakByteHasBeenReceived() const
    {
        return iAckNakByteReceivedFlag;
    }

    CtiDR87PorterSide& setAckNakByteReceived(BOOL c)
    {
        iAckNakByteReceivedFlag = c;
        return *this;
    }

    BOOL    isDataMsgComplete() const
    {
        return iDataMsgCompleteFlag;
    }

    CtiDR87PorterSide& setDataMsgComplete(BOOL c)
    {
        iDataMsgCompleteFlag = c;
        return *this;
    }

    INT CtiDR87PorterSide::getTotalBytesExpected () const
    {
        return iTotalBytesExpected;
    }

    CtiDR87PorterSide& setTotalBytesExpected (INT aByteCnt)
    {
        iTotalBytesExpected = aByteCnt;
        return *this;
    }

    INT CtiDR87PorterSide::getNumberOfIncompleteMsgs () const
    {
        return iNumberOfIncompleteMsgs;
    }

    CtiDR87PorterSide& setNumberOfIncompleteMsgs (INT aNumberOfIncompleteMsgs)
    {
        iNumberOfIncompleteMsgs = aNumberOfIncompleteMsgs;
        return *this;
    }

    INT CtiDR87PorterSide::getByteNumber () const
    {
        return iByteNumber;
    }

    CtiDR87PorterSide& setByteNumber (INT aByteCnt)
    {
        iByteNumber = aByteCnt;
        return *this;
    }

    USHORT CtiDR87PorterSide::getOldestIntervalByteOffset () const
    {
        return iOldestIntervalByteOffset;
    }

    CtiDR87PorterSide& setOldestIntervalByteOffset (INT aByteOffset)
    {
        iOldestIntervalByteOffset = aByteOffset;
        return *this;
    }

    USHORT CtiDR87PorterSide::getCurrentByteOffset () const
    {
        return iCurrentByteOffset;
    }

    CtiDR87PorterSide& setCurrentByteOffset (INT aByteOffset)
    {
        iCurrentByteOffset = aByteOffset;
        return *this;
    }

    USHORT CtiDR87PorterSide::getMassMemoryStart () const
    {
        return iMassMemoryStart;
    }

    CtiDR87PorterSide& setMassMemoryStart  (INT aMemory)
    {
        iMassMemoryStart = aMemory;
        return *this;
    }

    USHORT CtiDR87PorterSide::getMassMemoryStop () const
    {
        return iMassMemoryStop;
    }

    CtiDR87PorterSide& setMassMemoryStop  (INT aMemory)
    {
        iMassMemoryStop = aMemory;
        return *this;
    }

    INT CtiDR87PorterSide::getLogoffFunction () const
    {
        return iLogoffFunction;
    }

    CtiDR87PorterSide& setLogoffFunction  (INT aFunc)
    {
        iLogoffFunction = aFunc;
        return *this;
    }
};



#endif // #ifndef __DEV_DR87_H__