#pragma once

#include "dlldefs.h"
#include "smartmap.h"
#include "port_base.h"
#include "slctprt.h"

namespace Cti {
    class RowReader;
}

IM_EX_PRTDB BOOL isAPort(CtiPort*,void*);

class IM_EX_PRTDB CtiPortManager
{
private:

    CTI_PORTTHREAD_FUNC_FACTORY_PTR     _portThreadFuncFactory;
    CtiSmartMap< CtiPort >      _smartMap;

    void RefreshDialableEntries(bool &rowFound, Cti::RowReader& rdr, CtiPort* (*Factory)(Cti::RowReader &), BOOL (*testFunc)(CtiPort*,void*), void *arg);
    void RefreshEntries(bool &rowFound, Cti::RowReader& rdr, CtiPort* (*Factory)(Cti::RowReader &), BOOL (*testFunc)(CtiPort*,void*),void *arg);

protected:

    void RefreshPooledPortEntries(bool &rowFound, Cti::RowReader& rdr, CtiPort* (*Factory)(Cti::RowReader &), BOOL (*testFunc)(CtiPort*,void*), void *arg);

public:

    typedef CtiLockGuard<CtiMutex>              LockGuard;
    typedef CtiSmartMap< CtiPort >              coll_type;              // This is the collection type!
    typedef CtiSmartMap< CtiPort >::ptr_type    ptr_type;
    typedef CtiSmartMap< CtiPort >::spiterator  spiterator;
    typedef CtiSmartMap< CtiPort >::insert_pair insert_pair;



    CtiPortManager(CTI_PORTTHREAD_FUNC_FACTORY_PTR fn);

    virtual ~CtiPortManager();

    void RefreshList(CtiPort* (*Factory)(Cti::RowReader &) = PortFactory, BOOL (*fn)(CtiPort*,void*) = isAPort, void *d = NULL);

    void apply(void (*applyFun)(const long, ptr_type, void*), void* d);
    ptr_type  find(bool (*findFun)(const long, ptr_type, void*), void* d);

    ptr_type getPortById(LONG pid);

    INT writeQueue(INT pid, ULONG Request, ULONG DataSize, PVOID Data, ULONG Priority);

    void haltLogs();

    spiterator begin();
    spiterator end();

    coll_type::lock_t &getLock()
    {
        return _smartMap.getLock();
    }

    bool mayPortExecuteExclusionFree(ptr_type anxiousPort, CtiTablePaoExclusion &portexclusion);
    bool removePortExclusionBlocks(ptr_type anxiousPort);
    bool refreshExclusions(LONG id = 0);
};
