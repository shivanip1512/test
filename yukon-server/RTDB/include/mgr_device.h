#pragma once

#include "dlldefs.h"
#include "rtdb.h"
#include "dev_base.h"
#include "slctdev.h"
#include "smartmap.h"
#include <map>

namespace Cti {
struct RfnIdentifier;

namespace Database {
class DatabaseReaderInterface;
}

namespace Devices {
class RfnDevice;
}
}

class IM_EX_DEVDB CtiDeviceManager
{
public:

    typedef CtiLockGuard<CtiMutex>      LockGuard;
    typedef CtiSmartMap<CtiDeviceBase>  coll_type;              // This is the collection type!
    typedef coll_type::ptr_type         ptr_type;
    typedef coll_type::spiterator       spiterator;
    typedef coll_type::insert_pair      insert_pair;

    typedef std::map<long, int> device_priorities_t;

private:

    int _dberrorcode{ 0 };

    coll_type        _smartMap;
    coll_type        _exclusionMap;         // This is a map of the devices which HAVE exclusions.
    coll_type        _portExclusions;       // This is a map of the devices the port has added - when a DB reload occurs, it clears
                                            //   _exclusionMap, so these need to be retained and reinserted from a seperate list
    std::set<long>   _paoIds;

    typedef std::map<long, std::set<long> > port_devices_t;
    typedef std::map<int,  std::set<long> > type_devices_t;
    typedef std::map<long, device_priorities_t> port_device_priorities_t;

    port_devices_t _portDevices;
    type_devices_t _typeDevices;

    port_device_priorities_t _portDevicePriorities;

    mutable Cti::readers_writer_lock_t _portDevicePrioritiesLock;

    std::string createTypeSqlClause(std::string type=std::string(), const bool include_type=true);

    bool refreshDevices(Cti::RowReader& rdr);

    bool loadDeviceType(Cti::Database::id_set &paoids, const std::string &device_name, const CtiDeviceBase &device, std::string type=std::string(), const bool include_type=true);
    void refreshPaoIds(const Cti::Database::id_set &ids);

    void addAssociations   (const CtiDeviceBase &dev);
    void removeAssociations(const CtiDeviceBase &dev);

    static bool isMct(int type);
    static bool isIon(int type);
    static bool isDnpChild(int type);

    void refreshExclusions     (Cti::Database::id_set &paoids);
    void refreshIONMeterGroups (Cti::Database::id_set &paoids);
    void refreshMacroSubdevices(Cti::Database::id_set &paoids);
    void refreshMCTConfigs     (Cti::Database::id_set &paoids);
    void refreshMCT400Configs  (Cti::Database::id_set &paoids);
    void refreshStaticPaoInfo  (Cti::Database::id_set &paoids);
    bool refreshPointGroups    (Cti::Database::id_set &paoids);

protected:

    //  these should be made private - anything that child classes should need to do should be handled through a function
    spiterator begin();
    spiterator end();

    void refreshList(const Cti::Database::id_set &paoids, const long deviceType = 0);

    virtual void refreshDeviceProperties(Cti::Database::id_set &paoids, int type);
    virtual void refreshDnpChildDevices(Cti::Database::id_set &paoids);

    virtual bool shouldDiscardDevice(CtiDeviceSPtr dev) const;

    int getPortDevicePriority(long portid, long deviceid) const;

    std::vector<CtiDeviceSPtr> getDiscardableDevices() const;
    void evictDevices(std::vector<CtiDeviceSPtr> & devices);

public:

    virtual ~CtiDeviceManager() = default;

    coll_type::lock_t &getLock();

    int setErrorCode(int ec)
    {
        if( ec ) _dberrorcode = ec;      // Only set it if there was an error (don't re-set it)
        return ec;
    }

    void resetErrorCode()
    {
        _dberrorcode = 0;      // Only set it if there was an error (don't re-set it)
    }

    size_t entries() const
    {
        return _smartMap.entries();
    }

    //  Overridden by ScannableDeviceManager
    virtual void refreshAllDevices();
    void refreshDeviceByID(LONG paoID, std::string category, std::string devicetype);
    void refreshGroupHierarchy(LONG paoID = 0);
    bool refreshPointGroups(void);

    bool isPaoId(long id);

    virtual ptr_type getDeviceByID(LONG Remote);
    void     getDevicesByPortID(long portid, std::vector<ptr_type> &devices);
    void     getDevicesByType  (int  type,   std::vector<ptr_type> &devices);
    virtual auto getDeviceByRfnIdentifier(const Cti::RfnIdentifier& rfnId) -> boost::shared_ptr<Cti::Devices::RfnDevice>;
    ptr_type RemoteGetPortRemoteEqual (LONG Port, LONG Remote);
    ptr_type RemoteGetPortRemoteTypeEqual (LONG Port, LONG Remote, INT Type);
    ptr_type RemoteGetPortMasterSlaveTypeEqual (LONG Port, LONG Master, LONG Slave, INT Type);
    virtual ptr_type RemoteGetEqualbyName (const std::string &RemoteName);

    bool containsType(int type);

    void apply(void (*applyFun)(const long, ptr_type, void*), void* d = NULL);
    ptr_type  find(bool (*findFun)(const long, const ptr_type &, void*), void* d = NULL);

    int select(bool (*selectFun)(const long, ptr_type, void*), void* d, std::vector< ptr_type > &coll);

    CtiDeviceManager &setDevicePrioritiesForPort(long portid, const device_priorities_t &device_priorities);

    bool mayDeviceExecuteExclusionFree(CtiDeviceSPtr anxiousDevice, const int requestPriority, CtiTablePaoExclusion &deviceexclusion);
    void removeInfiniteExclusion(CtiDeviceSPtr anxiousDevice);
    ptr_type chooseExclusionDevice(LONG portid);
    CtiDeviceManager &addPortExclusion(LONG paoID);

};

