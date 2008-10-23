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
 * REVISION     :  $Revision: 1.34 $
 * DATE         :  $Date: 2008/10/23 20:38:04 $
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


private:

    int _dberrorcode;

    CtiApplication_t _app_id;

    coll_type        _smartMap;
    coll_type        _exclusionMap;         // This is a map of the devices which HAVE exclusions.
    coll_type        _portExclusions;       // This is a map of the devices the port has added - when a DB reload occurs, it clears
                                            //   _exclusionMap, so these need to be retained and reinserted from a seperate list
private:

    void refreshList(LONG paoID = 0, long deviceType = 0 );
    bool loadDeviceType(long paoid, const string &device_name, CtiDeviceBase &device, string type=string(), bool include_type=true);
    bool refreshDevices(RWDBReader& rdr);

    bool refreshDeviceByPao(CtiDeviceSPtr pDev, LONG paoID);
    void refreshExclusions(LONG id = 0);
    void refreshIONMeterGroups(LONG paoID = 0);
    void refreshMacroSubdevices(LONG paoID = 0);
    void refreshMCTConfigs(LONG paoID = 0);
    void refreshMCT400Configs(LONG paoID = 0);
    void refreshDynamicPaoInfo(LONG paoID = 0);

protected:

    virtual void refreshDeviceProperties(LONG paoID = 0);

    spiterator begin();
    spiterator end();

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
    bool refreshPointGroups(LONG paoID = 0);
    void writeDynamicPaoInfo(void);

    void test_dumpList(void);
    void deleteList(void);

    ptr_type getDeviceByID(LONG Remote);
    ptr_type RemoteGetPortRemoteEqual (LONG Port, LONG Remote);
    ptr_type RemoteGetPortRemoteTypeEqual (LONG Port, LONG Remote, INT Type);
    ptr_type RemoteGetPortMasterSlaveTypeEqual (LONG Port, LONG Master, LONG Slave, INT Type);
    ptr_type RemoteGetEqualbyName (const string &RemoteName);

    void apply(void (*applyFun)(const long, ptr_type, void*), void* d);
    ptr_type  find(bool (*findFun)(const long, const ptr_type &, void*), void* d);
    bool contains (bool (*findFun)(const long, const ptr_type &, void*), void* d);

    int select(bool (*selectFun)(const long, ptr_type, void*), void* d, vector< ptr_type > &coll);

    bool mayDeviceExecuteExclusionFree(CtiDeviceSPtr anxiousDevice, CtiTablePaoExclusion &deviceexclusion);
    bool removeInfiniteExclusion(CtiDeviceSPtr anxiousDevice);
    ptr_type chooseExclusionDevice(LONG portid);
    CtiDeviceManager &addPortExclusion(LONG paoID);

};

#endif                  // #ifndef __MGR_DEVICE_H__
