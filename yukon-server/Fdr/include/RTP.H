//  rtp.h
//
//  LiveData Real-Time Protocol (RTP)
//
//  Copyright (c) 1998-2000 LiveData, Inc.;
//  all rights reserved.
//
//  Author:
//
//  References:
//
//
//  Description
//
//      Header file that defines RTP protocol opcodes and status codes. This
//      file also serves as the protocol specification document.
//
//  Revision History
//
//	05/10/99   DCS	Added support for the LONG_COUNT modifier.
//	02/22/99   CMD	Added comments regarding the 4-byte length prefix
//                      when running over sockets.
//
//

/* Opcodes */
#define RTP_READ_REQ       0x00    /* Read Request */
#define RTP_WRITE_REQ      0x01    /* Write Request */
#define RTP_NOTIFY         0x02    /* Notify (unacknowledged write) */
#define RTP_RESOLVE_REQ    0x03    /* Resolve Variable Names Request */
#define RTP_KA_PACKET      0x04    /* Keep alive packet */

#define RTP_READ_RSP       0x80    /* Read Response */
#define RTP_WRITE_RSP      0x81    /* Write Response */
#define RTP_RESOLVE_RSP    0x83    /* Resolve Variable Names Response */
#define RTP_UNKNOWN_RSP    0xFF    /* error condition with unknown opcode */

/* Status Codes */
#define RTP_SUCCESS        0x00    /* no error */
#define RTP_OTHER_ERROR    0x01    /* error not covered by list below */
#define RTP_BAD_ADDR       0x02    /* no memory at given address */
#define RTP_TOO_BIG        0x03    /* read or write beyond memory */
#define RTP_BAD_DATA       0x04    /* unacceptable data given in write */
#define RTP_ACCESS_TEMP    0x05    /* data cannot be accessed temporarily */
#define RTP_ACCESS_PERM    0x06    /* requested data can never be accessed */
#define RTP_TOO_MUCH       0x07    /* more data requested than fits in PDU */
#define RTP_NEED_RESOLVE   0x08    /* given address is no longer valid */
#define RTP_PROTOCOL_ERROR 0x09    /* invalid RTP protocol detected */
#define RTP_NO_RESOURCES   0x0A    /* insufficient resources to complete req */
#define RTP_UNSUPPORTED    0x0B    /* RTP request not supported */

#define RTP_MAX_STATUS_CODE   RTP_UNSUPPORTED

/* modifier code */
#define NO_MODIFIER	   0x00    /* default operation */
#define LONG_COUNT	   0x01    /* message contains 32-bit count field(s) */

/*
Protocol Data Units:

Read Variable Specifier with NO_MODIFIER:
    byte 1 to 4 of specifier: 32-bit Binary address (low byte first)
    byte 5 to 6 of specifier: 16-bit count (low byte first) of bytes to read

Read Variable Specifier with LONG_COUNT:
    byte 1 to 4 of specifier: 32-bit Binary address (low byte first)
    byte 5 to 8 of specifier: 32-bit count (low byte first) of bytes to read

Read Request:
    byte 1 of message: Opcode (0x00)
    byte 2 of message: Modifier
    bytes 3 to 4 of message: Sequence number
                            (arbitrarily specified by requestor)
    bytes 5 to 10 or 5 to 12 of message: Read Variable Specifier for variable 1
    remaining bytes of message: Read Variable Specifiers for any
                            remaining variables

Read Response:
    byte 1: Opcode (0x80)
    byte 2: Status code
    byte 3 to 4: Sequence number (copied from request)
    beginning with byte 5 of message: (present only for RTP_SUCCESS)
        data read for variable 1 (the number of bytes provided shall equal
			    that specified in the count field of the first
                            Read Variable Specifier in the Read Request)
        data read for remaining variables: (the number of bytes provided for
			    each variable shall equal that specified in the
			    count field of the associated Read Variable
			    Specifier in the Read Request)

Write Variable Specifier with NO_MODIFIER:
    byte 1 to 4 of specifier: 32-bit Binary address (low byte first)
    byte 5 to 6 of specifier: 16-bit count (low byte first) of bytes to write
    remaining bytes of specifier: data to write (the number of bytes provided
			    shall equal that specified in the count field of
                            this Write Variable Specifier)

Write Variable Specifier with LONG_COUNT:
    byte 1 to 4 of specifier: 32-bit Binary address (low byte first)
    byte 5 to 8 of specifier: 32-bit count (low byte first) of bytes to write
    remaining bytes of specifier: data to write (the number of bytes provided
			    shall equal that specified in the count field of
                            this Write Variable Specifier)

Write or Notify Request:
    byte 1 of message: Opcode (0x01 or 0x02)
    byte 2 of message: Modifier
    byte 3 to 4 of message: Sequence number
                            (arbitrarily specified by requestor)
    beginning with byte 5 of message: Write Variable Specifier for variable 1
    remaining bytes of message: Write Variable Specifiers for any remaining
                            variables

Write Response:
    byte 1: Opcode (0x81)
    byte 2: Status code
    byte 3 to 4: Sequence number (copied from request)

Resolve Variable Names Request:
    byte 1 of message: Opcode (0x03)
    byte 2 of message: Modifier
    bytes 3 to 4 of message: Sequence number
                            (arbitrarily specified by requestor)
    beginning with byte 5 of message:
        NULL-terminated ASCII string being the first name needing resolution.
    remaining bytes of message: Sequence of NULL-terminated ASCII strings for
                                the remaining names needing resolution.

Name Resolution Result Specifier:
    byte 1 to 4 of specifier: 32-bit Binary address or tag (low byte first)
    byte 5 to 6 of specifier: 16-bit count (low byte first) of bytes in the
                              variable. In case the name cannot be resolved,
                              this count is zero (the address is ignored).

Resolve Variable Names Response:
    byte 1: Opcode (0x83)
    byte 2: Status code
    byte 3 to 4: Sequence number (copied from request)
    beginning with byte 5 of message: (present only for RTP_SUCCESS)
        Name Resolution Result Specifier for the first requested name.
    remaining bytes of message:
        Name Resolution Result Specifiers for the remaining requested names.

Keep Alive Packet:
    byte 1: Opcode 0x04
    bytes 2-4: 0

Notes:
  1) The limit on the amount of data which can be sent or requested in one
     PDU is based on the maximum size of a network message.
  2) If it is not possible to resolve all of the requested names, information
     on those names which can be resolved will be present in the Resolve
     Variable Names Response message, but for those names which cannot be
     resolved, the response message will indicate a zero byte count. The
     Status Code in the response message will be RTP_SUCCESS (unless all
     names fail, in which case the responder may choose to return an error
     code and no Name Resolution Result Specifiers).
  3) When using the RTP protocol over TCP/IP (sockets), and 4-byte length
     must precede the RTP protocol packet. This length should be equal to
     the entire length of the RTP protocol packet plus four bytes (itself).
*/
