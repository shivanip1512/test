
#pragma warning( disable : 4786)
#ifndef __PORTSUP_H__
#define __PORTSUP_H__

/*-----------------------------------------------------------------------------*
*
* File:   PORTSUP
*
* Class:
* Date:   1/23/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/common/INCLUDE/PORTSUP.H-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:37 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/* Prototypes from portsup.c */
IM_EX_CTIBASE INT WaitForResponse (HFILE PortHandle, PULONG ResponseSize,  PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse = NULL);
IM_EX_CTIBASE INT SetLineMode (HFILE, INT bits = 8, INT parity = NOPARITY, INT stopbits = ONESTOPBIT);
IM_EX_CTIBASE INT SetLineModeE71 (HFILE);
IM_EX_CTIBASE INT SetLineModeTAPTerm (HFILE);
IM_EX_CTIBASE INT SetBaudRate (HFILE, USHORT);
IM_EX_CTIBASE INT SetDefaultDCB (HFILE);
IM_EX_CTIBASE INT SetXONXOFFDCB (HFILE);
IM_EX_CTIBASE INT SetReadTimeOut (HFILE, USHORT);
IM_EX_CTIBASE INT SetWriteTimeOut (HFILE, USHORT);
IM_EX_CTIBASE INT ModemDCD (HFILE);
IM_EX_CTIBASE INT ModemDSR (HFILE);
IM_EX_CTIBASE INT ModemCTS (HFILE);
IM_EX_CTIBASE INT RaiseModemRTS (HFILE);
IM_EX_CTIBASE INT LowerModemRTS (HFILE);
IM_EX_CTIBASE INT RaiseModemDTR (HFILE);
IM_EX_CTIBASE INT LowerModemDTR (HFILE);
IM_EX_CTIBASE INT PortInputFlush (HFILE);
IM_EX_CTIBASE INT GetPortInputQueueCount (HFILE);
IM_EX_CTIBASE INT PortOutputFlush (HFILE);
IM_EX_CTIBASE ULONG GetPortOutputQueueCount (HFILE);
IM_EX_CTIBASE USHORT GetPortBaudRate (HFILE);
IM_EX_CTIBASE USHORT GetPortCommEvent (HFILE);
IM_EX_CTIBASE INT GetPortCommError (HFILE);
IM_EX_CTIBASE ULONG MilliTime (PULONG);
IM_EX_CTIBASE INT ModemReset (HFILE, USHORT, USHORT, BOOL dcdTest = TRUE);
IM_EX_CTIBASE INT ModemHangup (HFILE, PCHAR, USHORT, USHORT, BOOL dcdTest = TRUE);
IM_EX_CTIBASE INT ModemSetup (HFILE, PCHAR, USHORT, USHORT, BOOL dcdTest = TRUE);
IM_EX_CTIBASE INT ModemConnect (HFILE, PCHAR, USHORT, USHORT, BOOL dcdTest = TRUE);

IM_EX_CTIBASE ULONG    BCDtoBase10(UCHAR* buffer, ULONG len);
IM_EX_CTIBASE ULONG    StrToUlong(UCHAR* buffer, ULONG len);
IM_EX_CTIBASE INT      CheckCCITT16CRC(INT Id,BYTE *InBuffer,ULONG InCount);
IM_EX_CTIBASE USHORT   CCITT16CRC(INT Id, BYTE* buffer, LONG length, BOOL bAdd);
IM_EX_CTIBASE USHORT   ShortLittleEndian(USHORT *ShortEndianFloat);
IM_EX_CTIBASE FLOAT    FltLittleEndian(FLOAT  *BigEndianFloat);
IM_EX_CTIBASE DOUBLE   DblLittleEndian(DOUBLE *BigEndianDouble);
IM_EX_CTIBASE VOID     BDblLittleEndian(CHAR *BigEndianBDouble);
IM_EX_CTIBASE void     DumpHexString(UCHAR* buffer, ULONG len);
IM_EX_CTIBASE void     DumpASCIIString(UCHAR* buffer, ULONG len);

#endif // #ifndef __PORTSUP_H__
