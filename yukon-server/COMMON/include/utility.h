/*-----------------------------------------------------------------------------*
*
* File:   utility
*
* Date:   4/14/2000
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/common/INCLUDE/utility.h-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2003/03/13 19:35:28 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __UTILITY_H__
#define __UTILITY_H__
#pragma warning( disable : 4786)


#include <rw\cstring.h>
#include <rw\rwtime.h>

#include "dsm2.h"
#include "dlldefs.h"


IM_EX_CTIBASE LONG GetMaxLMControl(long pao);
IM_EX_CTIBASE LONG LMControlHistoryIdGen(bool force = false);
IM_EX_CTIBASE LONG CommErrorHistoryIdGen(bool force = false);
IM_EX_CTIBASE INT ChangeIdGen(bool force = false);
IM_EX_CTIBASE INT SystemLogIdGen();
IM_EX_CTIBASE INT PAOIdGen();
IM_EX_CTIBASE INT VCUTime (CtiOutMessage *, PULONG);
IM_EX_CTIBASE BOOL isFileTooBig(RWCString fileName, DWORD thisBig = 0x00500000);
IM_EX_CTIBASE BOOL InEchoToOut(const INMESS *In, CtiOutMessage *Out);
IM_EX_CTIBASE BOOL OutEchoToIN(const CtiOutMessage *Out, INMESS *In);
IM_EX_CTIBASE RWCString convertVersacomAddressToHumanForm(INT address);
IM_EX_CTIBASE INT convertHumanFormAddressToVersacom(INT address);

IM_EX_CTIBASE bool pokeDigiPortserver(CHAR *server, INT port = 23);
IM_EX_CTIBASE RWCString& traceBuffer(RWCString &str, BYTE *Message, ULONG Length);
IM_EX_CTIBASE RWTime nextScheduledTimeAlignedOnRate( const RWTime &origin, LONG rate );
IM_EX_CTIBASE void autopsy(char *calleefile, int calleeline);       // Usage is: autopsy( __FILE__, __LINE__);



// SendMail defines /////////////////////////////////////////////

#define SMTP_PORT       25
#define MAX_LINE_SIZE   1024
#define MAX_NAME_SIZE   64


// SendMail data structures /////////////////////////////////////
typedef struct SENDMAIL
{
   LPCTSTR lpszHost;          // host name or dotted IP address
   LPCTSTR lpszSender;        // sender userID (optional)
   LPCTSTR lpszSenderName;    // sender display name (optional)
   LPCTSTR lpszRecipient;     // recipient userID
   LPCTSTR lpszRecipientName; // recipient display name (optional)
   LPCTSTR lpszReplyTo;       // reply to userID (optional)
   LPCTSTR lpszReplyToName;   // reply to display name (optional)
   LPCTSTR lpszMessageID;     // message ID (optional)
   LPCTSTR lpszSubject;       // subject of message
   LPCTSTR lpszMessage;       // message text
   BOOL    bLog;              // if TRUE, log messages to file
} SENDMAIL;




// SendMail exported functions //////////////////////////////////
IM_EX_CTIBASE BOOL SendMail( struct SENDMAIL *pMail, int *pResult );

typedef struct
{
   char *proj;
   int major;
   int minor;
   int build;
   char *date;
} CTICOMPILEINFO;

typedef struct {
   char *fname;
   double rev;
   char *date;
} CTICOMPONENTINFO;


IM_EX_CTIBASE void identifyProject(const CTICOMPILEINFO &Info);
IM_EX_CTIBASE void identifyProjectComponents(const CTICOMPONENTINFO *pInfo);


extern CTICOMPILEINFO CompileInfo;

extern CtiMutex gOutMessageMux;
extern ULONG gOutMessageCounter;

IM_EX_CTIBASE void incrementCount();
IM_EX_CTIBASE void decrementCount();
IM_EX_CTIBASE ULONG OutMessageCount();
IM_EX_CTIBASE bool isLCU(INT type);
IM_EX_CTIBASE int generateTransmissionID();
IM_EX_CTIBASE LONG GetPAOIdOfPoint(long pid);
IM_EX_CTIBASE INT EstablishOutMessagePriority(CtiOutMessage *Out, INT priority);
IM_EX_CTIBASE INT OverrideOutMessagePriority(CtiOutMessage *Out, INT priority);
IM_EX_CTIBASE bool CheckSocketSubsystem();


IM_EX_CTIBASE ULONG    BCDtoBase10(UCHAR* buffer, ULONG len);
IM_EX_CTIBASE ULONG    StrToUlong(UCHAR* buffer, ULONG len);
IM_EX_CTIBASE INT      CheckCCITT16CRC(INT Id,BYTE *InBuffer,ULONG InCount);
IM_EX_CTIBASE USHORT   CCITT16CRC(INT Id, BYTE* buffer, LONG length, BOOL bAdd);
IM_EX_CTIBASE USHORT   ShortLittleEndian(USHORT *ShortEndianFloat);
IM_EX_CTIBASE FLOAT    FltLittleEndian(FLOAT  *BigEndianFloat);
IM_EX_CTIBASE DOUBLE   DblLittleEndian(DOUBLE *BigEndianDouble);
IM_EX_CTIBASE VOID     BDblLittleEndian(CHAR *BigEndianBDouble);
IM_EX_CTIBASE ULONG    MilliTime (PULONG);


#endif // #ifndef __UTILITY_H__
