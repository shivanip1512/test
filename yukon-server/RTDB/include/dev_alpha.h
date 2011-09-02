#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_meter.h"

#define ALPHA_DEFAULT_OTHERCLASS   9999
#define ALPHA_RETRIES                10

#define  STX                  0x02
#define  PAD                  0x00

/* (CB) Command Byte Masks ans Defines */
#define  ALPHA_FORMAT_MASK       0x80        /* 1 indicates a short command format (0 long) */
#define  ALPHA_SHORT_CMD         0x80        /* 1 indicates a short command format (0 long) */

#define  ALPHA_SHORT_CMD_MASK    0x1F
#define  ALPHA_FUNCTION_MASK     0x08        /* 1 indicates a function command */
#define  ALPHA_READ_MASK         0x04        /* 1 indicates a read command */
#define  ALPHA_CLASS_MASK        0x01        /* 1 indicates a class request command */

#define  ALPHA_CMD_TERMINATE     0x80        /* Terminate Session */
#define  ALPHA_CMD_CONTINUE_RD   0x81        /* Continue Read Operation */
#define  ALPHA_CMD_RESEND        0x82        /* Resend Last Packet */
#define  ALPHA_CMD_OKAY          0x83        /* Are you there/Okay? (from Alpha) */
#define  ALPHA_CMD_TAKE_CONTROL  0x84        /* Take Control (from Alpha) */
#define  ALPHA_CMD_BAUD_1200     0x90        /* set baud to 1200 (from Alpha) */
#define  ALPHA_CMD_BAUD_9600     0x93        /* set baud to 9600 (from Alpha) */

#define  ALPHA_CMD_CLASS_READ    0x05
#define  ALPHA_CMD_NO_DATA       0x08        /* Function without Data (see FUNC below)   */
#define  ALPHA_CMD_WITH_DATA     0x18        /* Functions with short data buffers */
#define  ALPHA_CMD_PARTIAL_READ  0x85        /* Class Read CMD (portion) */
#define  ALPHA_CMD_READ_ALL      0x9F        /* Class Read ALL CMD (portion) */

/*
 * Command Format  : STX 0x18 FUNC PAD LEN DATA CRCH CRCL
 * Command Response: STX 0x18 ACK/NAK STAT CRCH CRCL
 */
#define  ALPHA_FUNC_PASSWORD     0x01
#define  ALPHA_FUNC_TIME_SET     0x02
#define  ALPHA_FUNC_WHO_ARE_YOU  0x06
#define  ALPHA_FUNC_BR_CALL_DATE 0x07
#define  ALPHA_FUNC_CALL_BACK    0x08
#define  ALPHA_FUNC_PACKET_SIZE  0x09
#define  ALPHA_FUNC_CRIT_PEAK    0x0B
#define  ALPHA_FUNC_TIME_SYNC    0x0C
#define  ALPHA_FUNC_COMM_TO      0xF2

/*
 * (STAT) Status Codes
 */

#define  ALPHA_AUTOREAD_OCCURED  0x80
#define  ALPHA_SEASON_CHANGE     0x40
#define  ALPHA_POWER_FAIL        0x20
#define  ALPHA_GENERAL_ALARM     0x10
#define  ALPHA_WRITE_PROTECT     0x08
#define  ALPHA_FUTURE_CONFIG     0x04
#define  ALPHA_DEMAND_RESET      0x02
#define  ALPHA_TIME_CHANGE       0x01

#define KEY_ALPHA_CLASS 1
#define KEY_ALPHA_DESC  2
#define KEY_ALPHA_FUNC  3

#define ALPHA_UNDEFINED       0
#define ALPHA_RATE_A       100
#define ALPHA_RATE_B       101
#define ALPHA_RATE_C       102
#define ALPHA_RATE_D       103
#define ALPHA_RATE_TOTAL      105
#define ALPHA_QUADRANT_1      106
#define ALPHA_QUADRANT_2      107
#define ALPHA_QUADRANT_3      108
#define ALPHA_QUADRANT_4      109


#define ALPHA_DEMAND       1
#define ALPHA_ENERGY       2

#pragma pack(push, alpha_packing, 1)

/* Some of these here 'uns might be needed too */

typedef struct
{
    UINT  Class;
    INT   Length;
    char  *Description;
} CTI_alpha_class;

typedef struct
{
    UINT  Function;
    INT   DataBytes;
    char  *Description;
} CTI_alpha_func;

typedef struct
{
    UCHAR Year;
    UCHAR Month;
    UCHAR Day;
    UCHAR Hour;
    UCHAR Minute;
} AlphaDateTime_t;


typedef struct
{
    ULONG   pointId;
    DOUBLE  multiplier;
    SHORT   mapping;
} AlphaLPPointInfo_t;

#pragma pack(pop, alpha_packing)     // Restore the prior packing alignment..


class IM_EX_DEVDB CtiDeviceAlpha : public CtiDeviceMeter
{
private:

    typedef CtiDeviceMeter Inherited;

protected:

    USHORT         _readClass;
    USHORT         _readLength;
    USHORT         _readFunction;

    USHORT         _bytesToRetrieve;

    BOOL           _classReadComplete;

    ULONG          _totalByteCount;

    BYTE           *_dataBuffer;
    BYTE           *_loadProfileBuffer;
    BYTE           *_lpWorkBuffer;


    BYTE           *_singleMsgBuffer;
    USHORT         _singleMsgBufferBytes;


    CtiDeviceAlpha & operator=(const CtiDeviceAlpha & aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - operator=() is invalid for device \"" << getName() << "\" **** " << FO(__FILE__) << " (" << __LINE__ << ")" << std::endl;
        }

        return *this;
    }

    CtiDeviceAlpha (const CtiDeviceAlpha & aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - copy constructor is invalid for device \"" << getName() << "\" **** " << FO(__FILE__) << " (" << __LINE__ << ")" << std::endl;
        }
    }


public:

    CtiDeviceAlpha(BYTE         *dataPtr  = NULL,
                   BYTE          *lpPtr = NULL,
                   BYTE          *wPtr = NULL,
                   ULONG         cnt=0);

    virtual ~CtiDeviceAlpha();

    // setters and getters
    USHORT            getReadLength () const;
    CtiDeviceAlpha&   setReadLength (USHORT aLength);

    USHORT            getReadClass () const;
    CtiDeviceAlpha&   setReadClass (USHORT aClass);

    USHORT            getBytesToRetrieve () const;
    CtiDeviceAlpha&   setBytesToRetrieve (USHORT aBytes);

    USHORT            getReadFunction () const;
    CtiDeviceAlpha&   setReadFunction (USHORT aFunction);

    BOOL              isClassReadComplete () const;
    CtiDeviceAlpha&   setClassReadComplete (BOOL aFlag);

    ULONG             getTotalByteCount() const;
    CtiDeviceAlpha&   setTotalByteCount(ULONG c);

    USHORT            getSingleMsgByteCount() const;
    CtiDeviceAlpha&   setSingleMsgByteCount( USHORT c);

    bool isReturnedBufferValid (CtiXfer  &Transfer);


    // general generate functions
    INT   generateCommandTerminate( CtiXfer  &Transfer, std::list< CtiMessage* > &traceList );

    // general decode routines

    UCHAR decodeAckNak(UCHAR AckNak);
    ULONG alphaCrypt(ULONG Key, ULONG PWord);
    USHORT  addCRC(UCHAR* buffer, LONG length, BOOL bAdd);
    INT checkCRC(BYTE *InBuffer,ULONG InCount);


    virtual INT GeneralScan(CtiRequestMsg              *pReq,
                            CtiCommandParser           &parse,
                            OUTMESS                   *&OutMessage,
                            std::list< CtiMessage* >  &vgList,
                            std::list< CtiMessage* >  &retList,
                            std::list< OUTMESS* >     &outList,
                            INT                         ScanPriority=MAXPRIORITY-4);

    virtual INT ResultDecode(INMESS                    *InMessage,
                             CtiTime                    &TimeNow,
                             std::list< CtiMessage* > &vgList,
                             std::list< CtiMessage* > &retList,
                             std::list< OUTMESS* >    &outList);
    virtual INT ErrorDecode(const INMESS        &InMessage,
                            const CtiTime        TimeNow,
                            std::list< CtiMessage* > &retList);

    // all defined in dev_alpha.cpp
    virtual INT generateCommand          (CtiXfer &Transfer, std::list< CtiMessage* > &traceList);
    virtual INT generateCommandHandshake (CtiXfer &Transfer, std::list< CtiMessage* > &traceList);

    virtual INT decodeResponse          (CtiXfer &Transfer, INT commReturnValue, std::list< CtiMessage* > &traceList);
    virtual INT decodeResponseHandshake (CtiXfer &Transfer, INT commReturnValue, std::list< CtiMessage* > &traceList);
    virtual INT freeDataBins();


    virtual BOOL verifyAndAddPointToReturnMsg (LONG          aPointId,
                                               DOUBLE        aValue,
                                               USHORT        aQuality,
                                               CtiTime        aTime,
                                               CtiReturnMsg *aReturnMsg,
                                               USHORT        aIntervalType=0,
                                               std::string     aValReport=std::string());


    virtual BOOL insertPointIntoReturnMsg (CtiMessage   *aDataPoint,
                                           CtiReturnMsg *aReturnMsg);



    // all the following functions need to be defined in the lower classes
    virtual UCHAR touBlockMapping( UCHAR config, USHORT type ) { return YukonBaseError; }
};



