#define TAPTIME_T1      2
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   TAPTERM
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/INCLUDE/TAPTERM.H-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#define TAPTIME_T2      1
#define TAPTIME_T3      10
#define TAPTIME_T4      4
#define TAPTIME_T5      8

#define TAPCOUNT_N1     3
#define TAPCOUNT_N2     3
#define TAPCOUNT_N3     3

#define CHAR_CR         0x0D
#define CHAR_LF         0x0A
#define CHAR_ESC        0x1B
#define CHAR_STX        0x02
#define CHAR_ETX        0x03
#define CHAR_US         0x1F
#define CHAR_ETB        0x17
#define CHAR_EOT        0x04
#define CHAR_SUB        0x1A
#define CHAR_ACK        0x06
#define CHAR_NAK        0x15
#define CHAR_RS         0x1e

int TAPTermHandShake (OUTMESS *OutMessage,
                      INMESS *InMessage,
                      CtiPort *PortRecord,
                      CtiDeviceBase *RemoteRecord);

