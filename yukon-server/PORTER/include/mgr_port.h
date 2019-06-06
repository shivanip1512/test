#pragma once

#include "dlldefs.h"
#include "smartmap.h"
#include "port_base.h"

namespace Cti {
    class RowReader;
}

class CtiPortManager
{
private:

    CtiSmartMap< CtiPort >      _smartMap;

    void RefreshDialableEntries(bool &rowFound, Cti::RowReader& rdr);
    void RefreshEntries(bool &rowFound, Cti::RowReader& rdr);
    bool RefreshType(const std::string name, const std::string sql, void (CtiPortManager::*refreshMethod)(bool &, Cti::RowReader &));

    static CtiPort* PortFactory(Cti::RowReader &rdr);

protected:

    void RefreshPooledPortEntries(bool &rowFound, Cti::RowReader& rdr);

public:

    typedef CtiLockGuard<CtiMutex>              LockGuard;
    typedef CtiSmartMap< CtiPort >              coll_type;              // This is the collection type!
    typedef CtiSmartMap< CtiPort >::ptr_type    ptr_type;
    typedef CtiSmartMap< CtiPort >::spiterator  spiterator;
    typedef CtiSmartMap< CtiPort >::insert_pair insert_pair;

    void RefreshList();
    void reloadPortLoggers();

    void apply(void (*applyFun)(const long, ptr_type, void*), void* d);

    ptr_type getPortById(LONG pid);

    INT writeQueue(std::unique_ptr<OUTMESS> OutMessage);
    INT writeQueue(OUTMESS *OutMessage);
    INT writeQueueWithPriority(OUTMESS *OutMessage, INT Priority);

    std::vector<CtiPortSPtr> getPorts() const;

    coll_type::lock_t &getLock()
    {
        return _smartMap.getLock();
    }

    bool mayPortExecuteExclusionFree(ptr_type anxiousPort, CtiTablePaoExclusion &portexclusion);
    bool refreshExclusions(LONG id = 0);
};
