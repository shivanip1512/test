/*-----------------------------------------------------------------------------*
*
* File:   dev_schlum
*
* Class:  CtiDeviceSchlumberger
* Date:   01/02/2000
*
* Author: David Sutton
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_alm_nloc.h-arc  $
* REVISION     :  $Revision: 1.12.4.2 $
* DATE         :  $Date: 2008/11/17 23:06:32 $
*
* Copyright (c) 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_SCHLUM_H__
#define __DEV_SCHLUM_H__
#pragma warning( disable : 4786 )



#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_meter.h"

#define SCHLUMBERGER_RETRIES 7

/* Schlumberger Protocol Field Definition */
#define  ENQ                  0x05
#define  ACK                  0x06
#define  NAK                  0x15
#define  CAN                  0x18

#define SCHLUM_RESEND_CMD 99


#define SCHLUMBERGER_UNDEFINED   0
#define SCHLUMBERGER_RATE_A   100
#define SCHLUMBERGER_RATE_B   101
#define SCHLUMBERGER_RATE_C   102
#define SCHLUMBERGER_RATE_D   103
#define SCHLUMBERGER_RATE_E   104

#pragma pack(push, schlumberger_packing, 1)


typedef struct
{
   CHAR     UnitType[4];
   CHAR     UnitId[9];
   ULONG    Start;
   ULONG    Stop;
   ULONG    Length;
   BYTE     Direction;
} SchlMeterStruct;

typedef struct
{
   time_t      RecordTime;
   ULONG       RecordAddress;
}  SchlLoadProfile_t;

// array of commands used by both meters
typedef struct
{
   INT      startAddress;
   INT      stopAddress;
   INT      startOffset;
   INT      bytesToRead;
   CHAR     *name;
} SchlumbergerCommandBlk_t;

// structure containing all the mass memory config params we need
typedef struct
{
   INT      MMIndex;
   INT      MMVintage;        // This is the last index we need to retrieve to make scanner happy
   ULONG    MMCount;
   ULONG    MMPos;
   ULONG    MMBlockSize;
   ULONG    MMScanStartAddress;
} SchlumbergerLProfileInput_t;

#pragma pack(pop, schlumberger_packing)     // Restore the prior packing alignment..


class IM_EX_DEVDB CtiDeviceSchlumberger : public CtiDeviceMeter, boost::noncopyable
{
private:

    typedef CtiDeviceMeter Inherited;

protected:

   SchlMeterStruct            _meterParams;

   INT                        _retryAttempts;

   INT                        _CRCErrors;

   ULONG                      _totalByteCount;

   BYTE                       *_dataBuffer;
   // load profile information
   BYTE                       *_massMemoryConfig;
   BYTE                       *_loadProfileTimeDate;
   BYTE                       *_massMemoryLoadProfile;
   BYTE                       *_massMemoryRequestInputs;
   BYTE                       *_loadProfileBuffer;

public:

   // default constructor that takes 2 optional parameters
   CtiDeviceSchlumberger ( BYTE         *dataPtr  = NULL,
                           BYTE         *mmPtr  = NULL,
                           BYTE         *timeDatePtr = NULL,
                           BYTE         *mmlProfilePtr = NULL,
                           BYTE         *mmlProfileInputPtr = NULL,
                           BYTE         *lProfilePtr = NULL,
                           ULONG        totalByteCount = 0 );

   virtual ~CtiDeviceSchlumberger();

   ULONG                   getTotalByteCount() const;
   CtiDeviceSchlumberger&  setTotalByteCount(ULONG c);

   SchlMeterStruct         getMeterParams() const;
   CtiDeviceSchlumberger&  setMeterParams (SchlMeterStruct &aParamSet);

   INT                     getRetryAttempts () const;
   CtiDeviceSchlumberger&  setRetryAttempts (INT aRetry);

   INT                     getCRCErrors () const;
   CtiDeviceSchlumberger&  setCRCErrors (INT aError);


   virtual INT GeneralScan(CtiRequestMsg *pReq,
                           CtiCommandParser &parse,
                           OUTMESS *&OutMessage,
                           std::list< CtiMessage* > &vgList,
                           std::list< CtiMessage* > &retList,
                           std::list< OUTMESS* > &outList,
                           INT ScanPriority = MAXPRIORITY - 4);
   virtual INT ResultDecode(INMESS *InMessage,
                            CtiTime &TimeNow,
                            std::list< CtiMessage* >   &vgList,
                            std::list< CtiMessage* > &retList,
                            std::list< OUTMESS* > &outList);

   virtual INT ErrorDecode(const INMESS      &InMessage,
                           const CtiTime      TimeNow,
                           std::list<CtiMessage*> &retList);

   virtual BOOL verifyAndAddPointToReturnMsg (LONG          aPointId,
                                              DOUBLE        aValue,
                                              USHORT        aQuality,
                                              CtiTime        aTime,
                                              CtiReturnMsg *aReturnMsg,
                                              USHORT        aIntervalType=0,
                                              std::string        aValReport=std::string());

   virtual BOOL insertPointIntoReturnMsg (CtiMessage    *aDataPoint,
                                  CtiReturnMsg   *aReturnMsg);
   virtual INT freeDataBins ();




   ULONG    previousMassMemoryAddress( ULONG SA,     // Starting Address
                                       ULONG CA,     // Current Address
                                       ULONG MA,     // Maximum Address
                                       ULONG RS);    // Record Size
   ULONG    bytesToBase10 (UCHAR* buffer, ULONG len);
   LONG     nibblesAndBits (BYTE* bptr, INT MaxChannel, INT Channel, INT Interval);

   INT fillUploadTransferObject (CtiXfer  &aTransfer, ULONG aStartAddress, ULONG aStopAddress);
   INT checkReturnMsg(CtiXfer  &Transfer,INT       commReturnValue);

};

#endif // #ifndef __DEV_SCHLUM_H__
