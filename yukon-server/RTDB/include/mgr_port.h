#ifndef __PORT_MGR_H__
#define __PORT_MGR_H__
/*************************************************************************
 *
 * mgr_port.h      7/7/99
 *
 *****
 *
 * The class which owns and manages port real time database
 *
 * Originated by:
 *     Corey G. Plender    7/7/99
 *
 *
 * PVCS KEYWORDS:
 * ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/mgr_port.h-arc  $
 * REVISION     :  $Revision: 1.3 $
 * DATE         :  $Date: 2002/04/16 16:00:29 $
 *
 * (c) 1999 Cannon Technologies Inc. Wayzata Minnesota
 * All Rights Reserved
 *
 ************************************************************************/

#include <rw/db/connect.h>

#include "dlldefs.h"
#include "rtdb.h"
#include "port_base.h"
#include "slctprt.h"

typedef void (*CTI_THREAD_FUNC_PTR)(void *);

IM_EX_PRTDB BOOL isAPort(CtiPort*,void*);

class IM_EX_PRTDB CtiPortManager : public CtiRTDB< CtiPort >
{
private:

   CTI_THREAD_FUNC_PTR  _portThreadFunc;

public:
   CtiPortManager(CTI_THREAD_FUNC_PTR fn);

   virtual ~CtiPortManager();

   void RefreshList(CtiPort* (*Factory)(RWDBReader &) = PortFactory,
                    BOOL (*fn)(CtiPort*,void*) = isAPort,
                    void *d = NULL);

   void RefreshEntries(RWDBReader& rdr,
                       CtiPort* (*Factory)(RWDBReader &),
                       BOOL (*testFunc)(CtiPort*,void*),
                       void *arg);

   void DumpList(void);
   void DeleteList(void);

   CtiPort* PortGetEqual(LONG pid);

   INT      writeQueue(INT pid,
                       ULONG Request,
                       ULONG DataSize,
                       PVOID Data,
                       ULONG Priority);

   CTI_THREAD_FUNC_PTR  setPortThreadFunc(CTI_THREAD_FUNC_PTR aFn);

   void haltLogs();
};

#endif                  // #ifndef __PORT_MGR_H__
