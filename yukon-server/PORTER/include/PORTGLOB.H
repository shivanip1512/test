#include "dlldefs.h"
#include "types.h"
#include "streamSocketListener.h"
#include "porter.h"
#include "logger.h"

#define PORTER_DEBUG_VERBOSE           0x00000001
#define PORTER_DEBUG_NEXUSREAD         0x00000004
#define PORTER_DEBUG_PORTQUEREAD       0x00000008
#define PORTER_DEBUG_SENDERROR         0x00000010
#define PORTER_DEBUG_COMMFAIL          0x00000020
#define PORTER_DEBUG_VERSACOM          0x00000100
#define PORTER_DEBUG_RIPPLE            0x00000800
#define PORTER_DEBUG_PRIORITIES        0x00001000
#define PORTER_DEBUG_CCUMESSAGES       0x00010000
#define PORTER_DEBUG_CCUCONFIG         0x00020000

#define PORTER_DEBUG_DIALBACK_PILDIRECT     0x10000000


#define P_TIMESYNC_EVENT               0
#define P_QUEUE_EVENT                  1
#define P_QUIT_EVENT                   2

#define NUMPORTEREVENTS                3

extern IM_EX_PORTGLOB INT           PorterRefreshRate;

extern IM_EX_PORTGLOB USHORT        TraceFlag;
extern IM_EX_PORTGLOB USHORT        TraceErrorsOnly;
extern IM_EX_PORTGLOB USHORT        ResetAll711s;
extern IM_EX_PORTGLOB USHORT        LoadRoutes;
extern IM_EX_PORTGLOB USHORT        VCUWait;
extern IM_EX_PORTGLOB USHORT        LAndGLCUs;
extern IM_EX_PORTGLOB USHORT        DLCFreq1;
extern IM_EX_PORTGLOB USHORT        DLCFreq2;
extern IM_EX_PORTGLOB USHORT        MaxOcts;
extern IM_EX_PORTGLOB LONG          TracePort;
extern IM_EX_PORTGLOB LONG          TraceRemote;

extern IM_EX_PORTGLOB UINT          PorterDebugLevel;


/* Not exported */
extern IM_EX_PORTGLOB BOOL          PorterQuit;
extern IM_EX_PORTGLOB HANDLE        hPorterEvents[];
extern IM_EX_PORTGLOB bool          gIgnoreTCU5X00QueFull;



extern CtiSyncDefStruct             PorterSyncs[];

extern IM_EX_PORTGLOB std::string        gDelayDatFile;

