/*-----------------------------------------------------------------------------*
*
* File:   utility
*
* Date:   4/14/2000
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/common/INCLUDE/utility.h-arc  $
* REVISION     :  $Revision: 1.24 $
* DATE         :  $Date: 2005/07/19 22:48:52 $
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
IM_EX_CTIBASE LONG VerificationSequenceGen(bool force = false);
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

IM_EX_CTIBASE BOOL searchFuncForOutMessageDevID(void *pId, void* d);
IM_EX_CTIBASE BOOL searchFuncForOutMessageRteID(void *pId, void* d);
IM_EX_CTIBASE BOOL searchFuncForOutMessageUniqueID(void *pId, void* d);
IM_EX_CTIBASE void applyPortQueueOutMessageReport(void *ptr, void* d);
IM_EX_CTIBASE double limitValue(double input, double min, double max);

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

typedef struct {

// #define NOWAIT          0x0000
// #define NORESULT        0x0000
// #define WAIT            0x0001
// #define RESULT          0x0002
// #define QUEUED          0x0004
// #define ACTIN           0x0008
// #define AWORD           0x0010
// #define BWORD           0x0020
// #define DTRAN           0x0040
// #define RCONT           0x0080
// #define RIPPLE          0x0100
// #define STAGE           0x0200
// #define VERSACOM        0x0400
// #define TSYNC           0x0800
// #define REMS            0x1000   // This can never be used now.... CGP Corey.
// #define FISHERPIERCE    0x1000
// #define ENCODED         0x4000
// #define DECODED         0x4000
// #define COMMANDCODE     0x8000

    int priority_count[16];         // Count of elements at the respective priority 1-15.
    int metrics[256];               // Ok, these are 256 metrics I want to count!

} CtiQueueAnalysis_t;


IM_EX_CTIBASE RWCString identifyProjectVersion(const CTICOMPILEINFO &Info);
IM_EX_CTIBASE void identifyProject(const CTICOMPILEINFO &Info);
IM_EX_CTIBASE void identifyProjectComponents(const CTICOMPONENTINFO *pInfo);


extern CTICOMPILEINFO CompileInfo;

extern CtiMutex gOutMessageMux;
extern ULONG gOutMessageCounter;

IM_EX_CTIBASE void incrementCount();
IM_EX_CTIBASE void decrementCount();
IM_EX_CTIBASE ULONG OutMessageCount();
IM_EX_CTIBASE bool isLCU(INT type);
IM_EX_CTIBASE bool isION(INT type);
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

IM_EX_CTIBASE LONG     ResetBreakAlloc();

IM_EX_CTIBASE bool findLPRequestEntries(void *om, void* d);
IM_EX_CTIBASE void cleanupOutMessages(void *unusedptr, void* d);
IM_EX_CTIBASE RWCString explainTags(const unsigned tags);

IM_EX_CTIBASE int binaryStringToInt(const CHAR *buffer, int length);

IM_EX_CTIBASE unsigned char addBitToSA305CRC(unsigned char crc, unsigned char bit); // bit is 0 or 1
IM_EX_CTIBASE unsigned char addOctalCharToSA305CRC(unsigned char crc, unsigned char ch); // octal char
IM_EX_CTIBASE void testSA305CRC(char* testData);
IM_EX_CTIBASE LONG GetPAOIdOfEnergyPro(long devicesn);


#endif // #ifndef __UTILITY_H__
