#ifndef TCPSUP_H
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   TCPSUP
*
* Date:  20 Dec 2000 15:04:32
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/TCPSUP.H-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 16:00:21 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define TCPSUP_H
/******************************************************************************
 * PVCS KEYWORDS:
 * ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/TCPSUP.H-arc  $
 * REVISION     :  $Revision: 1.3 $
 * DATE         :  $Date: 2002/04/16 16:00:21 $
 ******************************************************************************/

#include "ctinexus.h"

#define TSDEFAULTPORT 1000

#define INITFLAG_TAPTERM      0x00000001
#define INITFLAG_PORTDELAYS   0x00000002

// Defines and macros for the incoming queue
#define QUEUE_LEN 8192  // If you know whats good for you make this a power of two

class CtiPort;

typedef struct STRUCTQUEUE {
    BYTE Buffer[QUEUE_LEN]; /* Receive buffer.  */
    USHORT Head; /* Receive buffer tail. */
    USHORT Tail;    /* Receive buffer tail. */
} QUEUE;

USHORT QUERYQUE (QUEUE *);
USHORT QUELEFT (QUEUE *);
USHORT FLUSHQUE (QUEUE *);
USHORT QUENEXT (USHORT);

#define ENQUE(queue) ((queue.Buffer[(queue.Head = QUENEXT (queue.Head))]))
#define DEQUE(queue) ((queue.Buffer[(queue.Tail = QUENEXT (queue.Tail))]))

typedef
    struct {
        struct sockaddr_in Server;
        SOCKET TerminalSocket;
        USHORT Port;
        QUEUE Queue;
        BOOL Open;
        BOOL Connected;
        LONG   LastClose;
        USHORT Baud;
        BYTE MStat;
        BYTE MOut;
        USHORT CFlags;
        USHORT IFlags;
        USHORT OFlags;
        USHORT XFlags;
        USHORT EStat;
        USHORT TXBufferSize;
        USHORT RXBufferSize;
        USHORT TOut;
        USHORT TReq;
        USHORT TIn;
        USHORT RIn;
        USHORT RWin;
        HMTX PortLockSem;
        TID MyThreadID;
        BOOL NetCXAltPin;
} NETCXPORTINFO;

/* Define the structure that will be used for in/out communications */
typedef struct {
    USHORT                 PortNumber;
    struct sockaddr_in     TCPIPReceiveClient;
    SOCKET                 TCPIPServerSocket;
    SOCKET                 TCPIPSocket;
    USHORT                 TCPIPFailed;
    CTINEXUS               ReturnNexus;
    HEV                    TCPIPFailedSem;
    HEV                    CommSem;
    ULONG                  Busy;
    ULONG                  Flags;
    NETCXPORTINFO          MyPortInfo;
} TCPIPSTRUCT;


/* Defines for Digiboards NET C/X protocol */
#define DIST(From, To) (((To) - (From)) & 0xffff)

// CFLAGS
#define CFLAGS_CBAUD       0x000F      // Not Supported
#define CFLAGS_CSIZE       0x0030      // Character Size
#define CFLAGS_CSTOPB      0x0040      // Two Stop Bits
#define CFLAGS_CREAD       0x0080      // Enable Receiver
#define CFLAGS_PARENB      0x0100      // Enable Parity
#define CFLAGS_PARODD      0x0200      // Odd Parity
#define CFLAGS_HUPCL       0x0400      // Drop DTR on Close
#define CFLAGS_CLOCAL      0x0800      // Not Supported

// Values for CSIZE
#define DATA_BITS_5        0x0000
#define DATA_BITS_6        0x0010
#define DATA_BITS_7        0x0020
#define DATA_BITS_8        0x0030

// XFLAGS
#define XFLAGS_XPAR        0x0001      // Enable Mark/Space Parity
#define XFLAGS_XMODEM      0x0002      // Enable In-Band Modem Signalling
#define XFLAGS_XCASE       0x0004      // Convert Special Characters
#define XFLAGS_XEDATA      0x0008      // Error Data Placed in I/O Stream
#define XFLAGS_XTOSS       0x0040      // Toss IXANY Characters
#define XFLAGS_XIXON       0x2000      // Enable Second set of XON/XOFF chars

// IFLAGS
#define IFLAGS_IGNBRK      0x0001      // Ignore all break conditions
#define IFLAGS_BRKINT      0x0002      // Signal Interrupt on Break
#define IFLAGS_IGNPAR      0x0004      // Ignore Parity or Framing Errors
#define IFLAGS_PARMRK      0x0008      // Error Characters Marked in Input Stream
#define IFLAGS_INPCK       0x0010      // Input Parity Errors Detected
#define IFLAGS_ISTRIP      0x0020      // All input chars masked with 0x7f
#define IFLAGS_INLCR       0x0040      // Not Supported
#define IFLAGS_IGNCR       0x0080      // Not Supported
#define IFLAGS_ICRNL       0x0100      // Not Supported
#define IFLAGS_IUCCL       0x0200      // Not Supported
#define IFLAGS_IXON        0x0400      // Output Software Flow Control Enabled
#define IFLAGS_IXANY       0x0800      // When output paused, Any character can restart
#define IFLAGS_IXOFF       0x1000      // Input Software Flow Control Enabled
#define IFLAGS_DOSMODE     0x8000      // 16450 Compatible error indication

// OFLAGS
#define OFLAGS_OPOST       0x0001      // Not Supported
#define OFLAGS_OLCUC       0x0002      // Map lower case to upper case
#define OFLAGS_ONLCR       0x0004      // Map NL to CR-NL
#define OFLAGS_OCRNL       0x0008      // Map CR to NL on Output
#define OFLAGS_ONOCR       0x0010      // Do Not Output CR if already in column 0
#define OFLAGS_ONLRET      0x0020      // Assume NL also performs CR
#define OFLAGS_OFILL       0x0040      // Not Supported
#define OFLAGS_OFDEL       0x0080      // Not Supported
#define OFLAGS_NLDLY       0x0100      // Not Supported
#define OFLAGS_CRDLY       0x0200      // Not Supported
#define OFLAGS_TABDLY      0x1800      // Tab Delay.  Tabs expand to 8 spaced if = 0x1800
#define OFLAGS_BSDLY       0x2000      // Not Supported
#define OFLAGS_VTDLY       0x4000      // Not Supported
#define OFLAGS_FFDLY       0x8000      // Not Supported

// Modem Bit Assignments
#define MODEM_DTR          0x01
#define MODEM_RTS          0x02
#define MODEM_CTS          0x10
#define MODEM_DSR          0x20
#define MODEM_RI           0x40
#define MODEM_DCD          0x80

// EFLAGS
#define EFLAGS_OPU         0x0001      // Output paused unconditionally by client
#define EFLAGS_OPS         0x0002      // Output paused by regular software flow control
#define EFLAGS_OPX         0x0004      // Output paused by extra software flow control
#define EFLAGS_OPH         0x0008      // Output paused by hardware flow control
#define EFLAGS_IPU         0x0010      // Input paused unconditionally by client
#define EFLAGS_IPS         0x0020      // Input paused by high/low water marks
#define EFLAGS_TXB         0x0040      // Transmit break pending
#define EFLAGS_TXI         0x0080      // Transmit immediate pending
#define EFLAGS_TXF         0x0100      // Transmit flow control character pending
#define EFLAGS_RXB         0x0200      // Break Received


// Message Types - get masked with LS nibble of port
#define MESSAGE(__Message, __Port) ((__Message) | ((__Port) & 0x0f))
#define MESSAGE_DATA_1                      0x00
#define MESSAGE_DATA_2                      0x10
#define MESSAGE_DATA_3                      0x20
#define MESSAGE_DATA_4                      0x30
#define MESSAGE_DATA_5                      0x40
#define MESSAGE_DATA_6                      0x50
#define MESSAGE_DATA_7                      0x60
#define MESSAGE_DATA_8                      0x70
#define MESSAGE_DATA_255                    0x80
#define MESSAGE_DATA_65535                  0x90
#define MESSAGE_WINDOW_SEQUENCE             0xa0
#define MESSAGE_COMMAND                     0xb0
#define MESSAGE_EVENT                       0xc0
#define MESSAGE_MISC                        0xf0

// MISC Types
// 0 - 7 are small module selects
#define MISC_MODULE_SELECT                  8
#define MISC_ID_REQUEST                     11
#define MISC_ID_RESPONSE                    12
#define MISC_KME                            13
#define MISC_DEBUG                          14
#define MISC_RESET                          15

// ID Request Types
#define ID_REQUEST_ECHO                     0
#define ID_REQUEST_PRODUCT                  1
#define ID_REQUEST_CAPABILITY               2


// Commands Types
#define COMMAND_OPEN_REQUEST                10
#define COMMAND_OPEN_RESPONSE               11
#define COMMAND_SYNCHRONIZE_REQUEST         12
#define COMMAND_SYNCHRONIZE_RESPONSE        13
#define COMMAND_SEQUENCE_REQUEST            14
#define COMMAND_SEQUENCE_RESPONSE           15
#define COMMAND_STATUS_REQUEST              16
#define COMMAND_STATUS_RESPONSE             17
#define COMMAND_LINE_ERROR_REQUEST          18
#define COMMAND_LINE_ERROR_RESPONSE         19
#define COMMAND_BUFFER_REQUEST              20
#define COMMAND_BUFFER_RESPONSE             21
#define COMMAND_PORT_CAPABILITY_REQUEST     22
#define COMMAND_PORT_CAPABILITY_RESPONSE    23
#define COMMAND_PORT_AVAILIBILITY_REQUEST   24
#define COMMAND_PORT_AVAILABILITY_RESPONSE  25
#define COMMAND_SET_PORT_PARAMETERS         40
#define COMMAND_SELECT_EVENT_CONDITIONS     42
#define COMMAND_SET_WINDOW_TRIGGER          43
#define COMMAND_SET_MODEM_AND_FLOW          44
#define COMMAND_SET_RECEIVE_WATER_MARKS     45
#define COMMAND_SET_FLOW_CONTROL_CHARACTERS 46
#define COMMAND_SET_RMAX_AND_RTIME          47
#define COMMAND_SET_TMAX_AND_TTIME          48
#define COMMAND_SEND_CHARACTER_IMMEDIATE    60
#define COMMAND_SEND_BREAK_IMMEDIATE        61
#define COMMAND_FLUSH_BUFFERS               62
#define COMMAND_PAUSE_IO                    63
#define COMMAND_UNPAUSE_IO                  64

#define BAUD_DIVIDEND                       1843200

// Open Types
#define OPEN_IMMEDIATE                      0
#define OPEN_PERSISTENT                     1
#define OPEN_INCOMING                       2
#define OPEN_CANCEL_IMMEDIATE               3
#define OPEN_CANCEL_PESISTENT               4
#define OPEN_CANCEL_INCOMING                5

#define FLUSH_NONE                          0
#define FLUSH_INPUT                         1
#define FLUSH_OUTPUT                        2
#define FLUSH_BOTH                          3

// This guy is a bit different.
IM_EX_TCPSUP INT  TCPReceiveChars (TCPIPSTRUCT *, PBYTE, ULONG, ULONG, PULONG);

IM_EX_TCPSUP INT  TCPInit (NETCXPORTINFO *, CtiPort *, UINT InitFlags);
IM_EX_TCPSUP INT  TCPInitNext (NETCXPORTINFO *, CtiPort *, UINT InitFlags);
IM_EX_TCPSUP INT  NetCXInit (NETCXPORTINFO *, CtiPort *, UINT Flags);

IM_EX_TCPSUP INT  TCPSend (NETCXPORTINFO *, PBYTE, ULONG);
IM_EX_TCPSUP INT  TCPReceive (NETCXPORTINFO *, PBYTE, LONG, ULONG, PLONG);
IM_EX_TCPSUP INT  TCPQuery (NETCXPORTINFO *);
IM_EX_TCPSUP INT  TCPInputFlush (NETCXPORTINFO *);
IM_EX_TCPSUP INT  TCPInputFlushTS (NETCXPORTINFO *);
IM_EX_TCPSUP INT  TCPOutputFlush (NETCXPORTINFO *);
IM_EX_TCPSUP INT  TCPsetBaudRate (NETCXPORTINFO *, USHORT);
IM_EX_TCPSUP INT  TCPRaiseRTS (NETCXPORTINFO *);
IM_EX_TCPSUP INT  TCPLowerRTS (NETCXPORTINFO *);
IM_EX_TCPSUP INT  TCPRaiseDTR (NETCXPORTINFO *);
IM_EX_TCPSUP INT  TCPLowerDTR (NETCXPORTINFO *);
IM_EX_TCPSUP INT  TCPDCDTest (NETCXPORTINFO *);
IM_EX_TCPSUP INT  TCPCTSTest (NETCXPORTINFO *);
IM_EX_TCPSUP INT  TCPDSRTest (NETCXPORTINFO *);
IM_EX_TCPSUP INT  TCPClose (NETCXPORTINFO *);

IM_EX_TCPSUP INT SocketShutdownClose(PCHAR FileName, ULONG Line, SOCKET &Socket);
IM_EX_TCPSUP INT SocketInputFlush (SOCKET &sock);
USHORT QUERYQUE (QUEUE *);
USHORT QUELEFT (QUEUE *);
USHORT FLUSHQUE (QUEUE *);
USHORT QUENEXT (USHORT);



#endif




