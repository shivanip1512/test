/*
 *  This file contains defines for dialup devices and should match a basic set as well
 */

#ifndef _DIALUP_H_
#define _DIALUP_H_

// Forward Declarations

class CtiPort;

#pragma pack(1)

/*
 *  The following defines are an attempt to eliminate a protocol from the requesting object
 *  the porter side does all it can to get the requested data any way necesary....
 */

/* PING is effectively a loopback doing any status checks deemd important on the meter */
#define DIALUP_REQUEST_PING                           0x00000001
#define DIALUP_REQUEST_DEMAND_RESET                   0x00000002

#define DIALUP_REQUEST_INSTRUMENTATION_DATA           0x00000000

#define DIALUP_REQUEST_INSTANTANEOUS_VOLTS            0x00000000
#define DIALUP_REQUEST_INSTANTANEOUS_AMPS             0x00000000

typedef struct
{
   OUTMESS        *OMessage;
   INMESS         *IMessage;
   CtiPort        *PRecord;
   CtiDeviceBase  *RRecord;
} REQUESTRECORD;

typedef struct {
   BYTE     *OutBuffer;
   ULONG     OutCount;
   BYTE     *InBuffer;
   ULONG    InCountExpected;
   ULONG    *InCountReceived; // Returned back in this guy
   INT      InTimeout;
   UINT     Flags;
} CTIXMIT;

/*
 *  This guy is where info the meter is so kind to get for us will be put...
 */
typedef struct {
   INT      OutageCount;
   INT      DemandResetCount;
   INT      PhaseOutageCount;
} DialupInstrumentation_t;

// Flags defnied for Flags member of the above structure
#define  XFER_ADD_CRC      0x0001
#define  XFER_VERIFY_CRC   0x0002


/*
 * CTI defined error returns - try to keep them bit fields just for utility sake.
 */
#define DIALUP_ERR_NODATA         0x00000001
#define DIALUP_ERR_BADCRC         0x00000002
#define DIALUP_ERR_INVALIDCLASS   0x00000003
#define DIALUP_ERR_RDTIMEOUT      0x00000008

/*
 * Completion states the request goes through.. Allows porter side to do protocol
 */
#define DIALUP_COMP_START            0x0001
#define DIALUP_COMP_ID               0x0002
#define DIALUP_COMP_PWD              0x0004

/***************************
 * Alpha States
 ***************************/
#define DIALUP_COMP_CLASS0           0x0008

#define DIALUP_COMP_SENDCLASS        0x0010
#define DIALUP_COMP_RECKNOWN         0x0020
#define DIALUP_COMP_RECUNKNOWN       0x0040
#define DIALUP_COMP_CONTREAD         0x0080

#define DIALUP_COMP_RCREAD1          0x0100      // Read of 72/1 or 2 occurred
#define DIALUP_COMP_RCREAD2          0x0200      // Read of 72/3 or 4 occurred
/***************************
 * END Alpha States
 ***************************/
/***************************
 * Fulcrum States
 ***************************/
#define DIALUP_COMP_SCAN             0x0008
/***************************
 * END Fulcrum States
 ***************************/

#define DIALUP_COMP_DECODED          0x1000
#define DIALUP_COMP_SUCCESS          0x8000

extern char *CTI_DIALUP_ERR[];

void     ClearBuffer(UCHAR* buffer, ULONG len);

INT      DialupSendAndReceive( CTIXMIT *Xfer,REQUESTRECORD *ReqRecord);
INT      DialupSend(CTIXMIT *Xfer, REQUESTRECORD *ReqRecord);
INT      DialupReceive(CTIXMIT *Xfer, REQUESTRECORD *ReqRecord);
INT      DialupHandShake  (OUTMESS *OutMessage, INMESS  *InMessage, CtiPort *PortRecord, CtiDeviceBase  *RemoteRecord);
INT      DialupPostScan  (OUTMESS *OutMessage, INMESS  *InMessage, CtiDeviceBase  *DeviceRecord);

#pragma pack()

#endif // #ifndef _DIALUP_H_
