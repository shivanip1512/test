
#pragma warning( disable : 4786)
#ifndef __PORTDECL_H__
#define __PORTDECL_H__

/*-----------------------------------------------------------------------------*
*
* File:   portdecl
*
* Class:
* Date:   12/11/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/INCLUDE/portdecl.h-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2002/09/19 15:54:22 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "rte_base.h"      // For CtiRoute
#include "port_base.h"

class CtiDeviceBase;

/* Prototypes from PLIDLC.C */
INT PreIDLC (PBYTE, USHORT, USHORT, USHORT, USHORT, USHORT, USHORT, USHORT, USHORT);
INT PreUnSequenced (PBYTE, USHORT, USHORT, USHORT, CtiDeviceBase*);
INT PreVTU (PBYTE, USHORT, USHORT, USHORT, USHORT, USHORT);
INT PostIDLC (PBYTE, USHORT);
INT GenReply (PBYTE, USHORT, PUSHORT, PUSHORT, USHORT);
INT RTUReply (PBYTE, USHORT);
INT RTUReplyHeader (USHORT, USHORT, PBYTE, PULONG);
INT IDLCRej (PBYTE, PUSHORT);
INT IDLCSRej (PBYTE, USHORT, USHORT);
INT IDLCSArm (PBYTE, USHORT);
INT IDLCua (PBYTE, PUSHORT, PUSHORT);
INT IDLCAlgStat (PBYTE, PUSHORT);

/* Prototypes from PORTER.C */
VOID APIENTRY PorterCleanUp (ULONG);

/* Prototypes from PORTERSU.C */
INT SendError (OUTMESS *&, USHORT, INMESS *InMessage = NULL);
INT ReportRemoteError (CtiDeviceBase *, ERRSTRUCT *);
INT ReportDeviceError (DEVICE *, CtiPortSPtr , ERRSTRUCT *);

/* Prototypes from PORTFILL.C */
VOID FillerThread (PVOID);

/* Prototypes from PORTPERF.C */
VOID PerfThread (PVOID);
VOID PerfUpdateThread (PVOID);
void statisticsNewRequest(long paoportid, long trxpaoid, long targpaoid);
void statisticsNewAttempt(long paoportid, long trxpaoid, long targpaoid, int result);
void statisticsNewCompletion(long paoportid, long trxpaoid, long targpaoid, int result);
void statisticsReport( CtiDeviceBase *pDevice );
void statisticsRecord();


/* Prototypes from PORTPIPE.C */
VOID PorterConnectionThread (PVOID);
VOID RouterThread (PVOID);

/* Prototypes from PORTPIL.C */
VOID PorterInterfaceThread (PVOID);

/*  */
VOID PortThread(void *);
void RemoteInitialize (CtiDeviceBase *&Device, CtiPortSPtr pPort);
void RemoteReset (CtiDeviceBase *&Device, CtiPortSPtr pPort);
INT TDMarkVHandShake (OUTMESS *, INMESS *, CtiPortSPtr , CtiDeviceBase *);
INT APlusHandShake (OUTMESS *, INMESS *, CtiPortSPtr , CtiDeviceBase *);
INT SchlHandShake (OUTMESS *, INMESS *, CtiPortSPtr , CtiDeviceBase *);
INT SchlPostScan  ( OUTMESS *OutMessage, INMESS  *InMessage, CtiDeviceBase  *DeviceRecord);

/* Prototypes from PORTQUE.C */
VOID QueueThread (PVOID);
INT CCUResponseDecode (INMESS *, CtiDeviceBase*, OUTMESS *OutMessage);
VOID KickerThread (PVOID);
INT DeQueue(INMESS *);
INT CCUQueueFlush    (CtiDeviceBase *Dev);
INT QueueFlush       (CtiDeviceBase *Dev);
INT BuildLGrpQ       (CtiDeviceBase *Dev);
INT BuildActinShed   (CtiDeviceBase *Dev);

/* Prototypes from PORTTIME.C */
VOID TimeSyncThread (PVOID);
INT SendTime(VOID);
INT LoadXTimeMessage (PBYTE);
INT LoadBTimeMessage (OUTMESS *);
INT LoadILEXTimeMessage (PBYTE, USHORT);
INT LoadWelcoTimeMessage (PBYTE, USHORT);
INT LoadSES92TimeMessage (PBYTE, USHORT);
INT WWVReceiversetup (HANDLE *);
INT WWVClockSync (HANDLE);
INT WWVBufferRead (HANDLE, char *);
INT WWVBufferWrite (HANDLE, char *);

/* Prototypes from PORTCONF.C */
VOID VConfigThread (PVOID);
INT VSend (VSTRUCT *, PCHAR, USHORT);
INT VSend2 (VSTRUCT *, CtiRouteSPtr );

/* Prototypes from PORTCONT.C */
INT RemoteControl (OUTMESS *);

/* Prototypes from PLIDLC.C */
INT PreIDLC (PBYTE, USHORT, USHORT, USHORT, USHORT, USHORT, USHORT, USHORT, USHORT);
INT PreUnSequenced (PBYTE, USHORT, USHORT, USHORT, CtiDeviceBase*);
INT PreVTU (PBYTE, USHORT, USHORT, USHORT, USHORT, USHORT);
INT PostIDLC (PBYTE, USHORT);
INT GenReply (PBYTE, USHORT, PUSHORT, PUSHORT, USHORT);
INT RTUReply (PBYTE, USHORT);
INT RTUReplyHeader (USHORT, USHORT, PBYTE, PULONG);
INT IDLCAlgStat (PBYTE, PUSHORT);

/* id of source process */
/* id of destination process */
/* function to execute */
IDLCFunction (CtiDeviceBase *pDev, USHORT Source, USHORT Dest, USHORT Function);
/* Port record */
/* ccu record */
/* various ccu specific data */
IDLCInit (CtiPortSPtr PortRecord, CtiDeviceBase *RemoteRecord, REMOTESEQUENCE *RemoteSequence);
IDLCSetDelaySets(CtiDeviceBase *pDev);
IDLCRColQ(CtiDeviceBase *pDev, INT priority = 11);
IDLCSetBaseSList(CtiDeviceBase *pDev);
IDLCSetTSStores(CtiDeviceBase *pDev, USHORT Priority, USHORT Trigger, USHORT Period);
IDLCRCont(CtiDeviceBase *pDev);


/* Prototypes from RIPPLE.C */
//INT LCUStage (OUTMESS *&);
//INT LCUShed (OUTMESS *&);
//INT LCUTime (OUTMESS *&, PULONG);
INT LCUPreSend (OUTMESS *&, CtiDeviceBase *);
INT LCUResultDecode (OUTMESS *, INMESS *, CtiDeviceBase *, ULONG, bool mayqueuescans);
INT MPCPointClear (PCHAR);
INT MPCPointSet (PCHAR);

INT TraceIn (PBYTE, USHORT, USHORT, USHORT, USHORT);
INT TraceOut (PBYTE, USHORT, USHORT, USHORT);
INT TPrint (PBYTE, USHORT);
INT BinPrint (BYTE);


#endif // #ifndef __PORTDECL_H__
