/*************************************************************************
 *
 * mgr_route.h      7/7/99
 *
 *****
 *
 * The class which owns and manages route real time database
 *
 * Originated by:
 *     Corey G. Plender    7/7/99
 *
 *
 * PVCS KEYWORDS:
 * REVISION     :  $Revision: 1.35.2.2 $
 * DATE         :  $Date: 2008/11/21 17:55:24 $
 *
 *
 * (c) 1999 Cannon Technologies Inc. Wayzata Minnesota
 * All Rights Reserved
 *
 ************************************************************************/
#ifndef __MGR_DEVICE_H__
#define __MGR_DEVICE_H__

#include <rw/db/connect.h>

#include "dlldefs.h"
#include "rtdb.h"
#include "dev_base.h"
#include "slctdev.h"
#include "smartmap.h"
#include <map>

class CtiCommandMsg;
/*
 *  The following functions may be used to create sublists for the points in our database.
 */

class IM_EX_DEVDB CtiDeviceManager
{
public:

    typedef CtiLockGuard<CtiMutex>      LockGuard;
    typedef CtiSmartMap<CtiDeviceBase>  coll_type;              // This is the collection type!
    typedef coll_type::ptr_type         ptr_type;
    typedef coll_type::spiterator       spiterator;
    typedef coll_type::insert_pair      insert_pair;

    typedef map<long, int> device_priorities_t;

protected:

    class id_range_t;

private:

    int _dberrorcode;

    CtiApplication_t _app_id;

    coll_type        _smartMap;
    coll_type        _exclusionMap;         // This is a map of the devices which HAVE exclusions.
    coll_type        _portExclusions;       // This is a map of the devices the port has added - when a DB reload occurs, it clears
                                            //   _exclusionMap, so these need to be retained and reinserted from a seperate list

    typedef map<long, set<long> > port_devices_t;
    typedef map<int,  set<long> > type_devices_t;
    typedef map<long, device_priorities_t> port_device_priorities_t;

    port_devices_t _portDevices;
    type_devices_t _typeDevices;

    port_device_priorities_t _portDevicePriorities;

    mutable Cti::readers_writer_lock_t _portDevicePrioritiesLock;

    bool refreshDevices(RWDBReader& rdr);

    bool loadDeviceType(id_range_t &paoids, const string &device_name, const CtiDeviceBase &device, string type=string(), const bool include_type=true);

    void refreshDeviceParameters    (id_range_t &paoids, int type);
    void refreshExclusions          (id_range_t &paoids);
    void refreshIONMeterGroups      (id_range_t &paoids);
    void refreshMacroSubdevices     (id_range_t &paoids);
    void refreshMCTConfigs          (id_range_t &paoids);
    void refreshMCT400Configs       (id_range_t &paoids);
    void refreshDynamicPaoInfo      (id_range_t &paoids);
    bool refreshPointGroups         (id_range_t &paoids);

protected:

    //  these should be made private - anything that child classes should need to do should be handled through a function
    spiterator begin();
    spiterator end();

    //typedef set<long> id_range_t;

    //  This class is used as a lightweight replacement for set<long> - it allows us to pass around single parameters with much less overhead than a set.
    //    When we move beyond VC6, we can use template member functions to allow for the long and set<long> specializations
    class id_range_t
    {
    public:

        typedef long value_type;
        typedef std::vector<value_type>::const_iterator const_iterator;

/*        id_range_t()
            : _itr_begin(0),
              _itr_end  (0)
            {};
        id_range_t(value_type val)
            : _val(val),
              _itr_begin(&_val),
              _itr_end  (&_val + 1)
            {};*/
        id_range_t(id_range_t::const_iterator begin, id_range_t::const_iterator end)
            : _itr_begin(begin),
              _itr_end  (end)
            {};

        const_iterator begin() const  {  return _itr_begin;  };
        const_iterator end()   const  {  return _itr_end;    };
        bool           empty() const  {  return _itr_begin == _itr_end;  };
        unsigned       size()  const  {  return _itr_end - _itr_begin;  };  //  optimization for long * - not true in the general iterator case, only true for random access iterator types

    private:

        const_iterator _itr_begin, _itr_end;
        value_type _val;
    };

    typedef id_range_t::const_iterator id_itr_t;

    void refreshList(id_range_t &paoids, const long deviceType = 0);

    virtual void refreshDeviceProperties(id_range_t &paoids, int type);

    int getPortDevicePriority(long portid, long deviceid) const;

public:

    CtiDeviceManager(CtiApplication_t app_id);
    virtual ~CtiDeviceManager();

    coll_type::lock_t &getLock();

    int getErrorCode() const { return _dberrorcode; };
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

    virtual void refresh(LONG paoID = 0, string category = string(""), string devicetype = string(""));
    void refreshGroupHierarchy(LONG paoID = 0);
    bool refreshPointGroups(void);
    void writeDynamicPaoInfo(void);

    void test_dumpList(void);
    void deleteList(void);

    ptr_type getDeviceByID(LONG Remote);
    void     getDevicesByPortID(long portid, vector<ptr_type> &devices);
    void     getDevicesByType  (int  type,   vector<ptr_type> &devices);
    ptr_type RemoteGetPortRemoteEqual (LONG Port, LONG Remote);
    ptr_type RemoteGetPortRemoteTypeEqual (LONG Port, LONG Remote, INT Type);
    ptr_type RemoteGetPortMasterSlaveTypeEqual (LONG Port, LONG Master, LONG Slave, INT Type);
    ptr_type RemoteGetEqualbyName (const string &RemoteName);

    bool containsType(int type);

    void apply(void (*applyFun)(const long, ptr_type, void*), void* d = NULL);
    ptr_type  find(bool (*findFun)(const long, const ptr_type &, void*), void* d = NULL);

    int select(bool (*selectFun)(const long, ptr_type, void*), void* d, vector< ptr_type > &coll);

    CtiDeviceManager &setDevicePrioritiesForPort(long portid, const device_priorities_t &device_priorities);

    bool mayDeviceExecuteExclusionFree(CtiDeviceSPtr anxiousDevice, const int requestPriority, CtiTablePaoExclusion &deviceexclusion);
    bool removeInfiniteExclusion(CtiDeviceSPtr anxiousDevice);
    ptr_type chooseExclusionDevice(LONG portid);
    CtiDeviceManager &addPortExclusion(LONG paoID);

};


#endif                  // #ifndef __MGR_DEVICE_H__
