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
YukonError_t GenReply (PBYTE, USHORT, PUSHORT, PUSHORT, USHORT, USHORT, bool*);
YukonError_t RTUReply (PBYTE, USHORT);
YukonError_t RTUReplyHeader (USHORT, USHORT, PBYTE, PULONG);
YukonError_t IDLCRej (PBYTE, PUSHORT);
INT IDLCSArm (PBYTE, USHORT);
YukonError_t IDLCua (PBYTE, PUSHORT, PUSHORT);
INT IDLCAlgStat (PBYTE, PUSHORT);

/* Prototypes from PORTER.C */
void APIENTRY PorterCleanUp (ULONG);

/* Prototypes from PORTERSU.C */
YukonError_t SendError(OUTMESS *&, YukonError_t, INMESS *InMessage = NULL);

/* Prototypes from PORTFILL.C */
void FillerThread();

/* Prototypes from PORTPIPE.C */
void PorterInterfaceThread();

/*  */
void PortThread(void *);
void PortDialbackThread(void *);
bool RemoteReset (CtiDeviceSPtr &Device, const CtiPortSPtr &pPort);

/* Prototypes from PORTQUE.C */
void QueueThread();
YukonError_t CCUResponseDecode (INMESS &, CtiDeviceSPtr , OUTMESS *OutMessage);
void KickerThread();
YukonError_t DeQueue(const INMESS &);
INT QueueFlush       (CtiDeviceSPtr Dev);
INT BuildLGrpQ       (CtiDeviceSPtr Dev);
INT BuildActinShed   (CtiDeviceSPtr Dev);

/* Prototypes from PORTTIME.C */
void TimeSyncThread();
INT LoadXTimeMessage (PBYTE);

INT RefreshMCTTimeSync (OUTMESS *);

INT ILEXHeader (PBYTE, USHORT, USHORT, USHORT, USHORT);
INT LoadILEXTimeMessage (PBYTE, USHORT);
INT LoadWelcoTimeMessage (PBYTE, USHORT);
INT LoadSES92TimeMessage (PBYTE, USHORT);

/* Prototypes from PORTCONF.C */
void VConfigThread();
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
YukonError_t LCUResultDecode (OUTMESS *, const INMESS &, CtiDeviceSPtr , YukonError_t, bool mayqueuescans);
INT MPCPointSet( int status, CtiDeviceBase *dev, bool setter );

