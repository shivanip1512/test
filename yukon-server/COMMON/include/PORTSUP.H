
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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/09/19 15:54:23 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/* Prototypes from portsup.c */
IM_EX_CTIBASE INT WaitForResponse (HANDLE PortHandle, PULONG ResponseSize,  PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse = NULL);
IM_EX_CTIBASE INT SetLineMode (HANDLE, INT bits = 8, INT parity = NOPARITY, INT stopbits = ONESTOPBIT);
IM_EX_CTIBASE INT SetLineModeE71 (HANDLE);
IM_EX_CTIBASE INT SetLineModeTAPTerm (HANDLE);
IM_EX_CTIBASE INT SetBaudRate (HANDLE, USHORT);
IM_EX_CTIBASE INT SetDefaultDCB (HANDLE);
IM_EX_CTIBASE INT SetXONXOFFDCB (HANDLE);
IM_EX_CTIBASE INT SetReadTimeOut (HANDLE, USHORT);
IM_EX_CTIBASE INT SetWriteTimeOut (HANDLE, USHORT);
IM_EX_CTIBASE INT ModemDCD (HANDLE);
IM_EX_CTIBASE INT ModemDSR (HANDLE);
IM_EX_CTIBASE INT ModemCTS (HANDLE);
IM_EX_CTIBASE INT RaiseModemRTS (HANDLE);
IM_EX_CTIBASE INT LowerModemRTS (HANDLE);
IM_EX_CTIBASE INT RaiseModemDTR (HANDLE);
IM_EX_CTIBASE INT LowerModemDTR (HANDLE);
IM_EX_CTIBASE INT PortInputFlush (HANDLE);
IM_EX_CTIBASE INT GetPortInputQueueCount (HANDLE);
IM_EX_CTIBASE INT PortOutputFlush (HANDLE);
IM_EX_CTIBASE ULONG GetPortOutputQueueCount (HANDLE);
IM_EX_CTIBASE USHORT GetPortBaudRate (HANDLE);
IM_EX_CTIBASE USHORT GetPortCommEvent (HANDLE);
IM_EX_CTIBASE INT GetPortCommError (HANDLE);
IM_EX_CTIBASE ULONG MilliTime (PULONG);
IM_EX_CTIBASE INT ModemReset (HANDLE, USHORT, USHORT, BOOL dcdTest = TRUE);
IM_EX_CTIBASE INT ModemHangup (HANDLE, PCHAR, USHORT, USHORT, BOOL dcdTest = TRUE);
IM_EX_CTIBASE INT ModemSetup (HANDLE, PCHAR, USHORT, USHORT, BOOL dcdTest = TRUE);
IM_EX_CTIBASE INT ModemConnect (HANDLE, PCHAR, USHORT, USHORT, BOOL dcdTest = TRUE);

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
