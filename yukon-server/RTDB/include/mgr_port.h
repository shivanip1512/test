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
 * REVISION     :  $Revision: 1.12 $
 * DATE         :  $Date: 2003/05/09 16:09:55 $
 *
 * (c) 1999 Cannon Technologies Inc. Wayzata Minnesota
 * All Rights Reserved
 *
 ************************************************************************/
#ifndef __PORT_MGR_H__
#define __PORT_MGR_H__

#include <rw/db/connect.h>

#include "dlldefs.h"
#include "smartmap.h"
#include "port_base.h"
#include "slctprt.h"

IM_EX_PRTDB BOOL isAPort(CtiPort*,void*);

class IM_EX_PRTDB CtiPortManager
{
private:

    CTI_PORTTHREAD_FUNC_FACTORY_PTR     _portThreadFuncFactory;
    CtiSmartMap< CtiPort >      _smartMap;

    CtiMutex                    _mux;

    void RefreshDialableEntries(bool &rowFound, RWDBReader& rdr, CtiPort* (*Factory)(RWDBReader &), BOOL (*testFunc)(CtiPort*,void*), void *arg);
    void RefreshEntries(bool &rowFound, RWDBReader& rdr, CtiPort* (*Factory)(RWDBReader &), BOOL (*testFunc)(CtiPort*,void*),void *arg);

public:

    typedef CtiLockGuard<CtiMutex>              LockGuard;
    typedef CtiSmartMap< CtiPort >              coll_type;              // This is the collection type!
    typedef CtiSmartMap< CtiPort >::ptr_type    ptr_type;
    typedef CtiSmartMap< CtiPort >::spiterator  spiterator;
    typedef CtiSmartMap< CtiPort >::insert_pair insert_pair;



    CtiPortManager(CTI_PORTTHREAD_FUNC_FACTORY_PTR fn);

    virtual ~CtiPortManager();

    void RefreshPooledPortEntries(bool &rowFound, RWDBReader& rdr, CtiPort* (*Factory)(RWDBReader &), BOOL (*testFunc)(CtiPort*,void*), void *arg);
    void RefreshList(CtiPort* (*Factory)(RWDBReader &) = PortFactory, BOOL (*fn)(CtiPort*,void*) = isAPort, void *d = NULL);
    void DumpList(void);
    void DeleteList(void);

    void apply(void (*applyFun)(const long, ptr_type, void*), void* d);
    ptr_type  find(bool (*findFun)(const long, ptr_type, void*), void* d);

    ptr_type PortGetEqual(LONG pid);

    INT writeQueue(INT pid, ULONG Request, ULONG DataSize, PVOID Data, ULONG Priority);

    CTI_PORTTHREAD_FUNC_FACTORY_PTR setPortThreadFunc(CTI_PORTTHREAD_FUNC_FACTORY_PTR aFn);  // Assign this entry to every (new) entry.  Return the old entry.

    void haltLogs();

    spiterator begin();
    spiterator end();

    CtiMutex & getMux()
    {
        return _mux;
    }

    bool mayPortExecuteExclusionFree(ptr_type anxiousPort);
    bool removePortExclusionBlocks(ptr_type anxiousPort);
};

#endif                  // #ifndef __PORT_MGR_H__
