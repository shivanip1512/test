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
 * REVISION     :  $Revision: 1.18 $
 * DATE         :  $Date: 2004/05/05 15:31:41 $
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

class CtiCommandMsg;
/*
 *  The following functions may be used to create sublists for the points in our database.
 */

#if 1

class IM_EX_DEVDB CtiDeviceManager
{
private:

    int _dberrorcode;

    CtiSmartMap< CtiDevice >    _smartMap;
    CtiMutex                    _mux;

private:

    bool (*_removeFunc)(CtiDeviceSPtr&,void*);
    bool _includeScanInfo;

    // Inherit "List" from Parent

    void refreshDevices(bool &rowFound, RWDBReader& rdr, CtiDeviceBase* (*Factory)(RWDBReader &));
    // void RefreshDeviceRoute(LONG id = 0);
    void refreshScanRates(LONG id = 0);
    void refreshDeviceWindows(LONG id = 0);

    void refreshList(CtiDeviceBase* (*Factory)(RWDBReader &) = DeviceFactory, bool (*removeFunc)(CtiDeviceSPtr&,void*) = isNotADevice, void *d = NULL, LONG paoID = 0);
    bool refreshDeviceByPao(CtiDeviceSPtr pDev, LONG paoID);
    void refreshExclusions(LONG id = 0);
    void refreshIONMeterGroups(LONG paoID = 0);
    void refreshMacroSubdevices(LONG paoID = 0);
    void refreshDeviceProperties(LONG paoID = 0);
    void refreshMCTConfigs(LONG paoID = 0);


public:

    typedef CtiLockGuard<CtiMutex>                LockGuard;
    typedef CtiSmartMap< CtiDevice >              coll_type;              // This is the collection type!
    typedef CtiSmartMap< CtiDevice >::ptr_type    ptr_type;
    typedef CtiSmartMap< CtiDevice >::spiterator  spiterator;
    typedef CtiSmartMap< CtiDevice >::insert_pair insert_pair;

    CtiDeviceManager();
    virtual ~CtiDeviceManager();

    spiterator begin();
    spiterator end();

    CtiMutex & getMux()
    {
        return _mux;
    }

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

    void refresh(CtiDeviceBase* (*Factory)(RWDBReader &) = DeviceFactory, bool (*removeFunc)(CtiDeviceSPtr&,void*) = isNotADevice, void *d = NULL, LONG paoID = 0, RWCString category = RWCString(""), RWCString devicetype = RWCString(""));

    void dumpList(void);
    void deleteList(void);

    ptr_type getEqual(LONG Remote);
    ptr_type RemoteGetPortRemoteEqual (LONG Port, LONG Remote);
    ptr_type RemoteGetEqual(LONG Remote);
    ptr_type RemoteGetEqualbyName (const RWCString &RemoteName);

    void apply(void (*applyFun)(const long, ptr_type, void*), void* d);
    ptr_type  find(bool (*findFun)(const long, ptr_type, void*), void* d);
    bool contains(bool (*findFun)(const long, ptr_type, void*), void* d);

    void setIncludeScanInfo();
    void resetIncludeScanInfo();

    bool mayDeviceExecuteExclusionFree(CtiDeviceSPtr anxiousDevice, CtiTablePaoExclusion &deviceexclusion);
    bool removeDeviceExclusionBlocks(CtiDeviceSPtr anxiousDevice);

};

#else
class IM_EX_DEVDB CtiDeviceManager : public CtiRTDB<CtiDeviceBase>
{
private:

    bool (*_removeFunc)(CtiDeviceBase*,void*);
    bool _includeScanInfo;

    // Inherit "List" from Parent

    void refreshDevices(bool &rowFound, RWDBReader& rdr, CtiDeviceBase* (*Factory)(RWDBReader &));
    // void RefreshDeviceRoute(LONG id = 0);
    void refreshScanRates(LONG id = 0);
    void refreshDeviceWindows(LONG id = 0);

    void refreshList(CtiDeviceBase* (*Factory)(RWDBReader &) = DeviceFactory, bool (*removeFunc)(CtiDeviceBase*,void*) = isNotADevice, void *d = NULL, LONG paoID = 0);
    bool refreshDeviceByPao(CtiDeviceBase *&pDev, LONG paoID);
    void refreshExclusions(LONG id = 0);
    void refreshIONMeterGroups(LONG paoID = 0);
    void refreshMacroSubdevices(LONG paoID = 0);
    void refreshDeviceProperties(LONG paoID = 0);
    void refreshMCTConfigs(LONG paoID = 0);


public:
    CtiDeviceManager();
    virtual ~CtiDeviceManager();

    void refresh(CtiDeviceBase* (*Factory)(RWDBReader &) = DeviceFactory, bool (*removeFunc)(CtiDeviceBase*,void*) = isNotADevice, void *d = NULL, LONG paoID = 0, RWCString category = RWCString(""), RWCString devicetype = RWCString(""));

    void DumpList(void);
    void DeleteList(void);

    CtiDeviceBase* getEqual(LONG Remote);
    CtiDeviceBase* RemoteGetPortRemoteEqual (LONG Port, LONG Remote);
    CtiDeviceBase* RemoteGetEqual(LONG Remote);
    CtiDeviceBase* RemoteGetEqualbyName (const RWCString &RemoteName);

    void setIncludeScanInfo();
    void resetIncludeScanInfo();

    bool mayDeviceExecuteExclusionFree(CtiDeviceBase *anxiousDevice, CtiTablePaoExclusion &deviceexclusion);
    bool removeDeviceExclusionBlocks(CtiDeviceBase *anxiousDevice);

};
#endif

#endif                  // #ifndef __MGR_DEVICE_H__
