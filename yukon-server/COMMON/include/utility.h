
#ifndef __UTILITY_H__
#define __UTILITY_H__

#pragma warning( disable : 4786)
#include <rw\cstring.h>

#include "dlldefs.h"
#include "dsm2.h"

/*-----------------------------------------------------------------------------*
*
* File:   utility
*
* Date:   4/14/2000
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/common/INCLUDE/utility.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2002/07/18 16:10:23 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

IM_EX_CTIBASE LONG GetMaxLMControl(long pao);
IM_EX_CTIBASE LONG LMControlHistoryIdGen(bool force = false);
IM_EX_CTIBASE LONG CommErrorHistoryIdGen(bool force = false);
IM_EX_CTIBASE INT ChangeIdGen(bool force = false);
IM_EX_CTIBASE INT SystemLogIdGen();
IM_EX_CTIBASE INT PAOIdGen();
IM_EX_CTIBASE INT VCUTime (OUTMESS *, PULONG);
IM_EX_CTIBASE BOOL isFileTooBig(RWCString fileName, DWORD thisBig = 0x00500000);
IM_EX_CTIBASE BOOL InEchoToOut(const INMESS *In, OUTMESS *Out);
IM_EX_CTIBASE BOOL OutEchoToIN(const OUTMESS *Out, INMESS *In);
IM_EX_CTIBASE RWCString convertVersacomAddressToHumanForm(INT address);
IM_EX_CTIBASE INT convertHumanFormAddressToVersacom(INT address);

IM_EX_CTIBASE bool pokeDigiPortserver(CHAR *server, INT port = 23);

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
IM_EX_CTIBASE INT EstablishOutMessagePriority(OUTMESS *Out, INT priority);
IM_EX_CTIBASE INT OverrideOutMessagePriority(OUTMESS *Out, INT priority);
IM_EX_CTIBASE bool CheckSocketSubsystem();

#endif // #ifndef __UTILITY_H__
