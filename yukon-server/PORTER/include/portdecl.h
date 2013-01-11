#pragma once

#include "dsm2err.h"
#include "rte_base.h"      // For CtiRoute
#include "port_base.h"
#include "porter.h"

class CtiDeviceBase;

/* Prototypes from PLIDLC.C */
INT PreIDLC (PBYTE, USHORT, USHORT, USHORT, USHORT, USHORT, USHORT, USHORT, USHORT);
INT PreUnSequenced (PBYTE, USHORT, USHORT, USHORT, CtiDeviceSPtr );
INT PostIDLC (PBYTE, USHORT);
INT GenReply (PBYTE, USHORT, PUSHORT, PUSHORT, USHORT, USHORT, bool*);
INT RTUReply (PBYTE, USHORT);
INT RTUReplyHeader (USHORT, USHORT, PBYTE, PULONG);
INT IDLCRej (PBYTE, PUSHORT);
INT IDLCSArm (PBYTE, USHORT);
INT IDLCua (PBYTE, PUSHORT, PUSHORT);
INT IDLCAlgStat (PBYTE, PUSHORT);

/* Prototypes from PORTER.C */
void APIENTRY PorterCleanUp (ULONG);

/* Prototypes from PORTERSU.C */
INT SendError(OUTMESS *&, USHORT, INMESS *InMessage = NULL);

/* Prototypes from PORTFILL.C */
void FillerThread (PVOID);

/* Prototypes from PORTPIPE.C */
void PorterConnectionThread (PVOID);

void PorterInterfaceThread (PVOID);

/*  */
void PortThread(void *);
void PortDialbackThread(void *);
bool RemoteReset (CtiDeviceSPtr &Device, const CtiPortSPtr &pPort);

/* Prototypes from PORTQUE.C */
void QueueThread (PVOID);
INT CCUResponseDecode (INMESS *, CtiDeviceSPtr , OUTMESS *OutMessage);
void KickerThread (PVOID);
INT DeQueue(INMESS *);
INT QueueFlush       (CtiDeviceSPtr Dev);
INT BuildLGrpQ       (CtiDeviceSPtr Dev);
INT BuildActinShed   (CtiDeviceSPtr Dev);

/* Prototypes from PORTTIME.C */
void TimeSyncThread (PVOID);
INT LoadXTimeMessage (PBYTE);

INT RefreshMCTTimeSync (OUTMESS *);

INT ILEXHeader (PBYTE, USHORT, USHORT, USHORT, USHORT);
INT LoadILEXTimeMessage (PBYTE, USHORT);
INT LoadWelcoTimeMessage (PBYTE, USHORT);
INT LoadSES92TimeMessage (PBYTE, USHORT);

/* Prototypes from PORTCONF.C */
void VConfigThread (PVOID);
INT VSend (VSTRUCT *, PCHAR, USHORT);
INT VSend2 (VSTRUCT *, CtiRouteSPtr );

INT IDLCFunction (CtiDeviceSPtr &pDev, USHORT Source, USHORT Dest, USHORT Function);
INT IDLCInit (CtiPortSPtr PortRecord, CtiDeviceSPtr &RemoteRecord, REMOTESEQUENCE *RemoteSequence);
INT IDLCSetDelaySets(CtiDeviceSPtr &pDev);
INT IDLCRColQ(CtiDeviceSPtr &pDev, INT priority = 11);
INT IDLCSetBaseSList(CtiDeviceSPtr &pDev);
INT IDLCSetTSStores(CtiDeviceSPtr &pDev, USHORT Priority, USHORT Trigger, USHORT Period);
INT IDLCRCont(CtiDeviceSPtr &pDev);


/* Prototypes from RIPPLE.C */
INT LCUPreSend (OUTMESS *&, CtiDeviceSPtr );
INT LCUResultDecode (OUTMESS *, INMESS *, CtiDeviceSPtr , ULONG, bool mayqueuescans);
INT MPCPointSet( int status, CtiDeviceBase *dev, bool setter );

