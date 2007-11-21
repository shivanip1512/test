/*-----------------------------------------------------------------------------*
*
* File:   mgr_device
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_device.cpp-arc  $
* REVISION     :  $Revision: 1.93 $
* DATE         :  $Date: 2007/11/21 19:55:47 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <rw/db/db.h>

#include "rtdb.h"
#include "mgr_device.h"
#include "cparms.h"
#include "dbaccess.h"
#include "dev_macro.h"
#include "dev_cbc.h"
#include "dev_dnp.h"
#include "dev_ion.h"
#include "dev_remote.h"
#include "dev_meter.h"
#include "dev_gridadvisor.h"
#include "dev_idlc.h"
#include "dev_ccu721.h"
#include "dev_carrier.h"
#include "dev_lmi.h"
#include "dev_mct.h"
#include "dev_mct410.h"
#include "dev_modbus.h"
#include "dev_repeater.h"
#include "dev_rtc.h"
#include "dev_rtm.h"
#include "dev_fmu.h"
#include "dev_tap.h"
#include "dev_snpp.h"
#include "dev_tnpp.h"
#include "dev_pagingreceiver.h"
#include "dev_grp_emetcon.h"
#include "dev_grp_energypro.h"
#include "dev_grp_expresscom.h"
#include "dev_grp_golay.h"
#include "dev_grp_point.h"
#include "dev_grp_ripple.h"
#include "dev_grp_sa105.h"
#include "dev_grp_sa305.h"
#include "dev_grp_sa205.h"
#include "dev_grp_sadigital.h"
#include "dev_grp_versacom.h"
#include "dev_grp_mct.h"
#include "dev_mct_broadcast.h"

#include "devicetypes.h"
#include "resolvers.h"
#include "slctdev.h"
#include "rtdb.h"

using namespace Cti;  //  in preparation for moving devices to their own namespace
using namespace std;


bool findExecutingAndExcludedDevice(const long key, CtiDeviceSPtr devsptr, void* d)
{
    bool bstatus = false;
    CtiDeviceBase* Device = devsptr.get();
    CtiDeviceBase *pAnxiousDevice = (CtiDeviceBase *)d;         // This is the port that wishes to execute!

    if(pAnxiousDevice->getID() != Device->getID())              // And it is not me...
    {
        bool excluded = pAnxiousDevice->isDeviceExcluded(Device->getID());

        if(excluded)
        {
            // Ok, now decide if that excluded device is currently executing....
            bstatus = Device->isExecuting();
        }
    }

    return bstatus;
}

bool findAllExpresscomGroups(const long key, CtiDeviceSPtr devsptr, void* d)
{
    bool bstatus = false;

    if( devsptr && devsptr->isGroup() && devsptr->getType() == TYPE_LMGROUP_EXPRESSCOM )
    {
        bstatus = true;
    }

    return bstatus;
}

// prevent any devB (proximity excluded device) which may be seleced to execute from interfereing with devA's next evaluation time.
static void applyExecutionGrantExpiresIsEvaluateNext(const long key, CtiDeviceSPtr devB, void* devSelect)
{
    CtiDevice *devA = (CtiDevice *)devSelect;

    if(devA != devB.get() && devA->getExclusion().proximityExcludes(devB->getID()))
    {
        CtiTime now;

        // Use the most restrictive of it's current or devA's..
        if( devA->getExclusion().getEvaluateNextAt() > now &&
            ( devB->getExclusion().getExecutionGrantExpires() > devA->getExclusion().getEvaluateNextAt()) )
        {
            devB->getExclusion().setExecutionGrantExpires( devA->getExclusion().getEvaluateNextAt() );                      // prevent any devB  which may be seleced below from taking our entire slot.

            {
                CtiLockGuard<CtiLogger> doubt_guard(slog);
                slog << CtiTime() << " " << devA->getName() << " requires " << devB->getName() << " to complete by " << devB->getExclusion().getExecutionGrantExpires() << " if grant occurs" << endl;
            }
        }
    }
}

static void applyEvaluateNextByExecutingUntil(const long key, CtiDeviceSPtr devB, void* devSelect)
{
    CtiDevice *devA = (CtiDevice *)devSelect;

    if(devA != devB.get() && devA->getExclusion().proximityExcludes(devB->getID()))
    {
        CtiTime now;

        // Use the most restrictive of it's current or devA's..
        if( devB->getExclusion().getEvaluateNextAt() < devA->getExclusion().getExecutingUntil() ||
            devB->getExclusion().getEvaluateNextAt() < now )
        {
            devB->getExclusion().setEvaluateNextAt(devA->getExclusion().getExecutingUntil());    // mark out all proximity conflicts to when devA will be done.
            devB->setExecutionProhibited(devA->getID(), devA->getExclusion().getExecutingUntil());

            {
                CtiLockGuard<CtiLogger> doubt_guard(slog);
                slog << CtiTime() << " " << devB->getName() << "'s execution blocked by " << devA->getName() << " until " << devB->getExclusion().getEvaluateNextAt() << endl;
            }
        }
    }
}

static void applyClearExclusions(const long unusedkey, CtiDeviceSPtr Device, void* lptrid)
{
    LONG id = (LONG)lptrid;

    if( !id || (id && id == Device->getID()))
    {
        Device->clearExclusions();
    }
    return;
}

static void applyRemoveInfiniteProhibit(const long unusedkey, CtiDeviceSPtr Device, void* d)
{
    try
    {
        CtiDevice *pAnxiousDevice = (CtiDevice *)d;       // This is the port that wishes to execute!
        LONG did = (LONG)pAnxiousDevice->getID();         // This is the id which is to be pulled from the prohibition list.

        bool found = Device->removeInfiniteProhibit( did );

        if(found && (getDebugLevel() & DEBUGLEVEL_EXCLUSIONS))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Device " << Device->getName() << " no longer prohibited because of " << pAnxiousDevice->getName() << "." << endl;
            }
            {
                CtiLockGuard<CtiLogger> doubt_guard(slog);
                slog << CtiTime() << " Device " << Device->getName() << " no longer prohibited because of " << pAnxiousDevice->getName() << "." << endl;
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return;
}



bool removeExclusionDevice(CtiDeviceSPtr &Device, void *lptrid)
{
    bool bstatus = false;
    LONG id = (LONG)lptrid;

    if( !id || (id == Device->getID()) )
    {
        bstatus = true;
    }

    return bstatus;
}

inline RWBoolean isDeviceIdStaticId(CtiDeviceSPtr &pDevice, void* d)
{
    CtiDeviceBase *pSp = (CtiDeviceBase *)d;

    return(pDevice->getID() == pSp->getID());
}

inline RWBoolean isDeviceNotUpdated(CtiDeviceSPtr &pDevice, void* d)
{
    // Return TRUE if it is NOT SET
    return(RWBoolean(!pDevice->getUpdatedFlag()));
}


static void applyDeviceResetUpdated(const long unusedkey, CtiDeviceSPtr Device, void* d)
{
    Device->resetUpdatedFlag();
    return;
}

static void applyInvalidateNotUpdated(const long unusedkey, CtiDeviceSPtr Device, void* d)
{
    if(!Device->getUpdatedFlag())
    {
        Device->setValid(FALSE);   //   NOT NOT NOT Valid
    }
    return;
}

static void applyClearMacroDeviceList(const long unusedkey, CtiDeviceSPtr Device, void* d)
{
    LONG paoid = (LONG)d;

    if( Device->getType() == TYPE_MACRO && (!paoid || Device->getID() == paoid) )
        ((CtiDeviceMacro *)(Device.get()))->clearDeviceList();
}

void CtiDeviceManager::dumpList(void)
{
    CtiDeviceBase *p = NULL;
    try
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() <<" There are " << _smartMap.entries() << " entries" << endl;
        }

        spiterator itr;

        for(itr = begin(); itr != end(); itr++)
        {
            p = (itr->second).get();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                p->DumpData();
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Exclusions **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        for(itr = _exclusionMap.getMap().begin(); itr != _exclusionMap.getMap().end(); itr++)
        {
            p = (itr->second).get();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                p->getExclusion().Dump();
            }
        }
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Exclusions **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Attempting to clear device list..." << endl;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " DumpDevices:  " << e.why() << endl;
        }
        RWTHROW(e);

    }
}


void CtiDeviceManager::refreshDevices(bool &rowFound, RWDBReader& rdr, CtiDeviceBase* (*Factory)(RWDBReader &))
{
    LONG              lTemp = 0;
    CtiDeviceSPtr     pTempCtiDevice;

    while( (setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        rowFound = true;
        rdr["deviceid"] >> lTemp;            // get the DeviceID

        if( _smartMap.entries() > 0 && (pTempCtiDevice = getEqual(lTemp)) )
        {
            /*
             *  The point just returned from the rdr already was in my list.  We need to
             *  update my list entry to the CTIDBG_new settings!
             */

            pTempCtiDevice->DecodeDatabaseReader(rdr);         // Fills himself in from the reader
            pTempCtiDevice->setUpdatedFlag();       // Mark it updated
        }
        else
        {
            CtiDeviceBase* pSp = (*Factory)(rdr);  // Use the reader to get me an object of the proper type

            if(pSp)
            {
                pSp->DecodeDatabaseReader(rdr);        // Fills himself in from the reader

                pSp->setUpdatedFlag();                              // Mark it updated
                _smartMap.insert( pSp->getID(), pSp );    // Stuff it in the list
            }
        }
    }
}



CtiDeviceManager::ptr_type CtiDeviceManager::RemoteGetPortRemoteEqual (LONG Port, LONG Remote)
{
    ptr_type p;

    LockGuard  dev_guard(getMux());

    if(_smartMap.entries() == 0)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " There are no entries in the remote device list" << endl;
        }
    }

    spiterator itr;

    for(itr = begin(); itr != end(); itr++)
    {
        p = itr->second;

        if( p->getAddress() > 0 &&  p->getPortID() == Port && p->getAddress() == Remote )
        {
            break;
        }

        p.reset();
    }

    return p;
}

CtiDeviceManager::ptr_type CtiDeviceManager::RemoteGetPortRemoteTypeEqual (LONG Port, LONG Remote, INT Type)
{
    ptr_type p;

    LockGuard  dev_guard(getMux());

    if(_smartMap.entries() == 0)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " There are no entries in the remote device list" << endl;
        }
    }

    spiterator itr;

    for(itr = begin(); itr != end(); itr++)
    {
        p = itr->second;

        if( p->getType() == Type && p->getAddress() > 0 &&  p->getPortID() == Port && p->getAddress() == Remote )
        {
            break;
        }

        p.reset();
    }

    return p;
}

CtiDeviceManager::ptr_type CtiDeviceManager::RemoteGetPortMasterSlaveTypeEqual (LONG Port, LONG Master, LONG Slave, INT Type)
{
    ptr_type p;

    LockGuard  dev_guard(getMux());

    if(_smartMap.entries() == 0)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " There are no entries in the remote device list" << endl;
        }
    }

    spiterator itr;

    for(itr = begin(); itr != end(); itr++)
    {
        p = itr->second;

        if( p->getType()          == Type  &&
            p->getPortID()        == Port  &&
            p->getAddress()       > 0      &&
            p->getMasterAddress() > 0      &&
            p->getAddress()       == Slave &&
            p->getMasterAddress() == Master )
        {
            break;
        }

        p.reset();
    }

    return p;
}

CtiDeviceManager::ptr_type CtiDeviceManager::RemoteGetEqual (LONG Dev)
{
    return getEqual(Dev);
}

CtiDeviceManager::ptr_type CtiDeviceManager::getEqual (LONG Dev)
{
    ptr_type p;
    try
    {
        p = _smartMap.find(Dev);
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return p;
}

CtiDeviceManager::ptr_type CtiDeviceManager::RemoteGetEqualbyName (const string &RemoteName)
{
    ptr_type p;

    string cmpname = RemoteName;
    string devname;

    std::transform(cmpname.begin(), cmpname.end(), cmpname.begin(), ::tolower);

    if(_smartMap.entries() == 0)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " There are no entries in the device manager list" << endl;
        }
    }

    spiterator itr;

    for(itr = begin(); itr != end(); itr++)
    {
        p = (itr->second);

        devname = p->getName();
        std::transform(devname.begin(), devname.end(), devname.begin(), ::tolower);


        if( devname == cmpname )
        {
            break;
        }
        else
        {
            p.reset();
        }
    }

    return p;
}

CtiDeviceManager::CtiDeviceManager(CtiApplication_t app_id) :
_app_id(app_id),
_includeScanInfo(false),
_removeFunc(NULL)
{
}

extern void cleanupDB();

CtiDeviceManager::~CtiDeviceManager()
{
    // cleanupDB();  // Deallocate all the DB stuff.
}

void CtiDeviceManager::deleteList(void)
{
    _smartMap.removeAll(NULL, 0);
}

void CtiDeviceManager::setIncludeScanInfo()
{
    _includeScanInfo = true;
}

void CtiDeviceManager::resetIncludeScanInfo()
{
    _includeScanInfo = false;
}



void CtiDeviceManager::refreshScanRates(LONG id)
{
    LONG lTemp = 0;
    CtiDeviceBase* pTempCtiDevice = NULL;

    LockGuard  dev_guard(getMux());       // Protect our iteration!

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    RWDBDatabase db = getDatabase();

    RWDBTable   keyTable;

    RWDBSelector selector = db.selector();

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Looking for ScanRates" << endl;
    }
    CtiTableDeviceScanRate::getSQL( db, keyTable, selector );

    if(id > 0)
    {
        selector.where(keyTable["deviceid"] == id && selector.where());
    }

    RWDBReader rdr = selector.reader(conn);
    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
    }
    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() <<" Done looking for ScanRates" << endl;
    }

    if(setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok)
    {
        if(id > 0)
        {
            CtiDeviceSPtr devsptr = getEqual(id);
            if(devsptr) pTempCtiDevice = devsptr.get();
            if(pTempCtiDevice) pTempCtiDevice->invalidateScanRates();     // Mark all Scan Rate elements as needing refresh..
        }
        else
        {
            spiterator itr;

            for(itr = begin(); itr != end(); itr++)
            {
                pTempCtiDevice = (itr->second).get();
                if(pTempCtiDevice) pTempCtiDevice->invalidateScanRates();     // Mark all Scan Rate elements as needing refresh..
            }
        }
    }

    while( (setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        rdr["deviceid"] >> lTemp;            // get the DeviceID

        CtiDeviceSPtr devsptr = getEqual(lTemp);
        if(devsptr) pTempCtiDevice = devsptr.get();

        if( pTempCtiDevice )
        {
            if( pTempCtiDevice->isSingle() )
            {
                /*
                 *  The point just returned from the rdr already was in my list, and is a
                 *  scannable device....  We need to
                 *  update the list entry with the scan rates!
                 */
                ((CtiDeviceSingle*)pTempCtiDevice)->DecodeScanRateDatabaseReader(rdr);        // Fills himself in from the reader
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " There are scanrates in the scanrate table for a nonscannable device: " << pTempCtiDevice->getName() << endl;
            }
        }
    }

    if(setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok || setErrorCode(rdr.status().errorCode()) == RWDBStatus::endOfFetch)
    {
        // Remove any scan rates which were NOT refreshed, but only if we read a few correctly!
        spiterator itr;

        for(itr = begin(); itr != end(); itr++)
        {
            pTempCtiDevice = (itr->second).get();
            pTempCtiDevice->deleteNonUpdatedScanRates();
        }
    }
}

void CtiDeviceManager::refreshDeviceWindows(LONG id)
{
    LONG        lTemp = 0;
    CtiDeviceBase*   pTempCtiDevice = NULL;

    LockGuard  dev_guard(getMux());       // Protect our iteration!

    spiterator itr;

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    RWDBDatabase db = getDatabase();

    RWDBTable   keyTable;

    RWDBSelector selector = db.selector();

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Looking for Device Windows" << endl;
    }
    CtiTableDeviceWindow::getSQL( db, keyTable, selector );

    if(id > 0)
    {
        selector.where(keyTable["deviceid"] == id && selector.where());

        CtiDeviceSPtr devsptr = getEqual(id);
        if(devsptr && devsptr->isSingle())
        {
            ((CtiDeviceSingle*)devsptr.get())->removeWindowType();  // This should remove ALL windows.  It is needed in case they have deleted the window on the device.
        }
    }

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << selector.asString() << endl;
    }

    RWDBReader rdr = selector.reader(conn);

    while( (setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        rdr["deviceid"] >> lTemp;            // get the DeviceID

        CtiDeviceSPtr devsptr = getEqual(lTemp);
        if(devsptr) pTempCtiDevice = devsptr.get();

        if( pTempCtiDevice )
        {
            if( pTempCtiDevice->isSingle() )
            {
                /*
                 *  The point just returned from the rdr already was in my list, and is a
                 *  scannable device....  We need to
                 *  update the list entry with the scan rates!
                 */
                ((CtiDeviceSingle*)pTempCtiDevice)->DecodeDeviceWindowDatabaseReader(rdr);        // Fills himself in from the reader
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " There are scan windows in the device window table for a nonscannable device. " << pTempCtiDevice->getName() << endl;
                }
            }
        }
    }

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Done looking for ScanWindows" << endl;
    }
}

void CtiDeviceManager::refresh(CtiDeviceBase* (*Factory)(RWDBReader &), bool (*removeFunc)(CtiDeviceSPtr&,void*), void *d, LONG paoID, string category, string devicetype)
{
    if(paoID != 0)
    {
        bool rowFound = false;
        CtiDeviceSPtr pDev = getEqual(paoID);

        if(pDev)        // If we have it, we can take a shortcut and do specific selects on the device type in question.
        {
            /*
             *  This is the code to handle change type.
             */
            if(resolvePAOType(category, devicetype) != pDev->getType())
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << pDev->getName() << " has changed type to " << devicetype << " from " << desolveDeviceType(pDev->getType()) << endl;
                }

                if( _smartMap.remove(paoID) )
                {
                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Old device object has been orphaned " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    // I don't know who the old guy is anymore!
                    refreshList(Factory, removeFunc, d, paoID);     // This should accomplish a minimal reload, getting the "new" guy.
                }
            }
            else
            {
                refreshDeviceByPao(pDev, paoID);
            }
        }
        else
        {
            // We were given an id.  We can use that to reduce our workload.
            refreshList(Factory, removeFunc, d, paoID);
        }
    }
    else
    {
        // we were given no id.  There must be no dbchange info.
        refreshList(Factory, removeFunc, d, paoID);
    }
}


void CtiDeviceManager::refreshList(CtiDeviceBase* (*Factory)(RWDBReader &), bool (*removeFunc)(CtiDeviceSPtr&,void*), void *arg, LONG paoID)
{
    CtiDeviceSPtr pTempCtiDevice;
    bool rowFound = false;

    CtiTime start, stop, querytime;

    if(removeFunc != NULL)
    {
        _removeFunc = removeFunc;
    }

    try
    {
        CtiDeviceBase *pSp;

        {
            if(paoID == 0)
            {
                apply(applyDeviceResetUpdated, NULL); // Reset everyone's Updated flag iff not a directed load.
            }
            else
            {
                pTempCtiDevice = getEqual(paoID);
                if(pTempCtiDevice) pTempCtiDevice->resetUpdatedFlag();
            }

            resetErrorCode();

            if(pTempCtiDevice)
            {
                refreshDeviceByPao(pTempCtiDevice, paoID);
            }
            else
            {
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);

                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();

                        RWDBTable   keyTable;
                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "Looking for Sixnet IEDs" << endl;
                        }
                        CtiDeviceIED().getSQL( db, keyTable, selector );

                        selector.where( rwdbUpper(keyTable["type"]) == "SIXNET" && selector.where() );
                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }

                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Sixnet IEDs" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  SIXNET " << endl;
                    }

                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();

                        RWDBTable   keyTable;
                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "Looking for Grid Advisor Devices" << endl;
                        }
                        CtiDeviceGridAdvisor().getSQL( db, keyTable, selector );

                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }

                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Sixnet IEDs" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  SIXNET " << endl;
                    }

                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();

                        RWDBTable   keyTable;
                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Meters & IEDs" << endl;
                        }
                        CtiDeviceMeter().getSQL( db, keyTable, selector );
                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }
                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Meters & IEDs" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  Meters & IEDs" << endl;
                    }


                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();

                        RWDBTable   keyTable;
                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for LMI Devices" << endl;
                        }
                        CtiDeviceLMI().getSQL( db, keyTable, selector );
                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }
                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for LMI Devices" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  LMI Devices" << endl;
                    }

                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();

                        RWDBTable   keyTable;
                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for DNP/ION Devices" << endl;
                        }
                        Device::DNP().getSQL( db, keyTable, selector );
                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                        //  added to prevent the LMI from being loaded twice
                        selector.where( (keyTable["type"] != "RTU-LMI") && selector.where() );

                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }
                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for DNP/ION Devices" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  DNP/ION/LMI Devices" << endl;
                    }


                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();

                        RWDBTable   keyTable;
                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for RTM Devices" << endl;
                        }
                        CtiDeviceRTM().getSQL( db, keyTable, selector );
                        selector.where( rwdbUpper(keyTable["type"]) == RWDBExpr( "RTM" ) && selector.where() );
                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }
                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for RTM Devices" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load RTM Devices" << endl;
                    }

                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();

                        RWDBTable   keyTable;
                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Tap Devices" << endl;
                        }
                        CtiDeviceTapPagingTerminal().getSQL( db, keyTable, selector );
                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }
                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for TAP Devices" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  TAP Devices" << endl;
                    }

                    // JESS
                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();

                        RWDBTable   keyTable;
                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Tnpp Devices" << endl;
                        }
                        CtiDeviceTnppPagingTerminal().getSQL( db, keyTable, selector );
                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }
                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Tnpp Devices" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load TNPP Devices" << endl;
                    }

                    //END JESS


                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();

                        RWDBTable   keyTable;
                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for IDLC Target Devices" << endl;
                        }
                        CtiDeviceIDLC().getSQL( db, keyTable, selector );

                        //  exclude the CCU 721
                        selector.where( (keyTable["type"] != RWDBExpr("CCU-721")) && selector.where() );

                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }
                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for IDLC Target Devices" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  IDLC Target Devices" << endl;
                    }


                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();

                        RWDBTable   keyTable;
                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for CCU-721 Devices" << endl;
                        }
                        //  all of these getSQL() calls should be static functions at some point - it's pointless
                        //    and costly to nstantiate devices for each block when a static function would do as well
                        Cti::Device::CCU721().getSQL( db, keyTable, selector );
                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }
                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for CCU-721 Devices" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  IDLC Target Devices" << endl;
                    }


                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();

                        RWDBTable   keyTable;
                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for MCT Broadcast Devices" << endl;
                        }
                        CtiDeviceMCTBroadcast().getSQL( db, keyTable, selector );
                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }

                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for MCT Broadcast Devices" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  MCT Broadcast Devices" << endl;
                    }


                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();

                        RWDBTable   keyTable;
                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for DLC Devices" << endl;
                        }
                        CtiDeviceCarrier().getSQL( db, keyTable, selector );
                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }

                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for DLC Devices" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  DLC Devices" << endl;
                    }


                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();

                        RWDBTable   keyTable;
                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for REPEATER Devices" << endl;
                        }
                        CtiDeviceDLCBase().getSQL( db, keyTable, selector );

                        selector.where( (rwdbUpper(keyTable["type"]) == RWDBExpr("REPEATER 921") ||
                                         rwdbUpper(keyTable["type"]) == RWDBExpr("REPEATER 801") ||
                                         rwdbUpper(keyTable["type"]) == RWDBExpr("REPEATER 800") ||
                                         rwdbUpper(keyTable["type"]) == RWDBExpr("REPEATER")) && selector.where() );   // Need to attach a few conditions!
                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }
                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for REPEATER Devices" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  REPEATER Devices" << endl;
                    }


                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();

                        RWDBTable   keyTable;
                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for CBC Devices" << endl;
                        }
                        CtiDeviceCBC().getSQL( db, keyTable, selector );
                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }
                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for CBC Devices" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  CBC Devices" << endl;
                    }

                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();

                        RWDBTable   keyTable;
                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for FMU Devices" << endl;
                        }
                        CtiDeviceFMU().getSQL( db, keyTable, selector );
                        selector.where( rwdbUpper(keyTable["type"]) == RWDBExpr("FMU") && selector.where() );   // Need to attach a few conditions!
                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );


                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }
                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for FMU Devices" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  RTC Devices" << endl;
                    }

                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();

                        RWDBTable   keyTable;
                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for RTC Devices" << endl;
                        }
                        CtiDeviceRTC().getSQL( db, keyTable, selector );
                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }
                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for RTC Devices" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  RTC Devices" << endl;
                    }


                    if(!_includeScanInfo)       // These are not scannable items..
                    {
                        start = start.now();
                        {
                            RWDBConnection conn = getConnection();
                            RWDBDatabase db = getDatabase();

                            RWDBTable   keyTable;
                            RWDBSelector selector = db.selector();

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Emetcon Group Devices" << endl;
                            }
                            CtiDeviceGroupEmetcon().getSQL( db, keyTable, selector );
                            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                            RWDBReader rdr = selector.reader(conn);
                            if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                            }
                            refreshDevices(rowFound, rdr, Factory);

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Emetcon Group Devices" << endl;
                            }
                        }
                        stop = stop.now();
                        if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  Emetcon Group Devices" << endl;
                        }


                        start = start.now();
                        {
                            RWDBConnection conn = getConnection();
                            RWDBDatabase db = getDatabase();

                            RWDBTable   keyTable;
                            RWDBSelector selector = db.selector();

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Versacom Group Devices" << endl;
                            }
                            CtiDeviceGroupVersacom().getSQL( db, keyTable, selector );
                            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                            RWDBReader rdr = selector.reader(conn);
                            if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                            }
                            refreshDevices(rowFound, rdr, Factory);

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Versacom Group Devices" << endl;
                            }
                        }
                        stop = stop.now();
                        if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  Versacom Group Devices" << endl;
                        }


                        start = start.now();
                        {
                            RWDBConnection conn = getConnection();
                            RWDBDatabase db = getDatabase();

                            RWDBTable   keyTable;
                            RWDBSelector selector = db.selector();

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for ExpressCom Group Devices" << endl;
                            }
                            CtiDeviceGroupExpresscom().getSQL( db, keyTable, selector );
                            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                            RWDBReader rdr = selector.reader(conn);
                            if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                            }
                            refreshDevices(rowFound, rdr, Factory);

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for ExpressCom Group Devices" << endl;
                            }
                        }
                        stop = stop.now();
                        if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  ExpressCom Group Devices" << endl;
                        }


                        start = start.now();
                        {
                            RWDBConnection conn = getConnection();
                            RWDBDatabase db = getDatabase();

                            RWDBTable   keyTable;
                            RWDBSelector selector = db.selector();

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for EnergyPro Group Devices" << endl;
                            }
                            CtiDeviceGroupEnergyPro().getSQL( db, keyTable, selector );
                            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                            RWDBReader rdr = selector.reader(conn);
                            if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                            }
                            refreshDevices(rowFound, rdr, Factory);

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for EnergyPro Group Devices" << endl;
                            }
                        }
                        stop = stop.now();
                        if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  EnergyPro Group Devices" << endl;
                        }


                        start = start.now();
                        {
                            RWDBConnection conn = getConnection();
                            RWDBDatabase db = getDatabase();

                            RWDBTable   keyTable;

                            RWDBSelector selector = db.selector();

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Ripple Group Devices" << endl;
                            }
                            CtiDeviceGroupRipple().getSQL( db, keyTable, selector );
                            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                            RWDBReader rdr = selector.reader(conn);
                            if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                            }
                            refreshDevices(rowFound, rdr, Factory);

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Ripple Group Devices" << endl;
                            }
                        }
                        stop = stop.now();
                        if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  Ripple Group Devices" << endl;
                        }


                        start = start.now();
                        {
                            RWDBConnection conn = getConnection();
                            RWDBDatabase db = getDatabase();

                            RWDBTable   keyTable;

                            RWDBSelector selector = db.selector();

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for MCT Load Group Devices" << endl;
                            }
                            CtiDeviceGroupMCT().getSQL( db, keyTable, selector );
                            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                            RWDBReader rdr = selector.reader(conn);
                            if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                            }
                            refreshDevices(rowFound, rdr, Factory);

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for MCT Load Group Devices" << endl;
                            }
                        }
                        stop = stop.now();
                        if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  MCT Load Group Devices" << endl;
                        }


                        start = start.now();
                        {
                            RWDBConnection conn = getConnection();
                            RWDBDatabase db = getDatabase();
                            RWDBTable   keyTable;

                            RWDBSelector selector = db.selector();

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Macro Devices" << endl;
                            }
                            CtiDeviceMacro().getSQL( db, keyTable, selector );
                            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                            RWDBReader rdr = selector.reader(conn);
                            if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                            }
                            refreshDevices(rowFound, rdr, Factory);

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Macro Devices" << endl;
                            }
                        }
                        stop = stop.now();
                        if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  Macro Devices" << endl;
                        }


                        start = start.now();
                        {
                            RWDBConnection conn = getConnection();
                            RWDBDatabase db = getDatabase();

                            RWDBTable   keyTable;
                            RWDBSelector selector = db.selector();

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for  105 Group Devices" << endl;
                            }
                            CtiDeviceGroupSA105().getSQL( db, keyTable, selector );
                            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                            RWDBReader rdr = selector.reader(conn);
                            if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                            }
                            refreshDevices(rowFound, rdr, Factory);

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for  105 Group Devices" << endl;
                            }
                        }
                        stop = stop.now();
                        if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  105 Group Devices" << endl;
                        }


                        start = start.now();
                        {
                            RWDBConnection conn = getConnection();
                            RWDBDatabase db = getDatabase();

                            RWDBTable   keyTable;
                            RWDBSelector selector = db.selector();

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for  205 Group Devices" << endl;
                            }
                            CtiDeviceGroupSA205().getSQL( db, keyTable, selector );
                            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                            RWDBReader rdr = selector.reader(conn);
                            if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                            }
                            refreshDevices(rowFound, rdr, Factory);

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for  205 Group Devices" << endl;
                            }
                        }
                        stop = stop.now();
                        if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  205 Group Devices" << endl;
                        }

                        start = start.now();
                        {
                            RWDBConnection conn = getConnection();
                            RWDBDatabase db = getDatabase();

                            RWDBTable   keyTable;
                            RWDBSelector selector = db.selector();

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for 305 Group Devices" << endl;
                            }
                            CtiDeviceGroupSA305().getSQL( db, keyTable, selector );
                            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                            RWDBReader rdr = selector.reader(conn);
                            if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                            }
                            refreshDevices(rowFound, rdr, Factory);

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for 305 Group Devices" << endl;
                            }
                        }
                        stop = stop.now();
                        if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load 305 Devices" << endl;
                        }


                        start = start.now();
                        {
                            RWDBConnection conn = getConnection();
                            RWDBDatabase db = getDatabase();

                            RWDBTable   keyTable;
                            RWDBSelector selector = db.selector();

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for  SA Digital Group Devices" << endl;
                            }
                            CtiDeviceGroupSADigital().getSQL( db, keyTable, selector );
                            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                            RWDBReader rdr = selector.reader(conn);
                            if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                            }
                            refreshDevices(rowFound, rdr, Factory);

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for  SA Digital Group Devices" << endl;
                            }
                        }
                        stop = stop.now();
                        if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  SA Digital Group Devices" << endl;
                        }


                        start = start.now();
                        {
                            RWDBConnection conn = getConnection();
                            RWDBDatabase db = getDatabase();

                            RWDBTable   keyTable;
                            RWDBSelector selector = db.selector();

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for  Golay Group Devices" << endl;
                            }
                            CtiDeviceGroupGolay().getSQL( db, keyTable, selector );
                            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                            RWDBReader rdr = selector.reader(conn);
                            if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                            }
                            refreshDevices(rowFound, rdr, Factory);

                            if(DebugLevel & 0x00020000)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for  Golay Group Devices" << endl;
                            }
                        }
                        stop = stop.now();
                        if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  Golay Group Devices" << endl;
                        }

                        start = start.now();
                        refreshPointGroups(paoID,Factory);
                        stop = stop.now();
                        if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load Point Group Devices" << endl;
                        }
                    }

                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();
                        RWDBTable   keyTable;

                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for System Devices" << endl;
                        }
                        CtiDevice().getSQL( db, keyTable, selector );
                        selector.where( rwdbUpper(keyTable["type"]) == RWDBExpr("SYSTEM") && selector.where() );   // Need to attach a few conditions!
                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }
                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for System Devices" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  System Devices" << endl;
                    }

                    // Now load the device properties onto the devices
                    refreshDeviceProperties(paoID);
                }

                if(getErrorCode() != RWDBStatus::ok && getErrorCode() != RWDBStatus::endOfFetch)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << " database had a return code of " << getErrorCode() << endl;
                    }
                }
                else if(rowFound)
                {
                    start = start.now();

                    if(_removeFunc)
                    {
                        _smartMap.removeAll(_removeFunc, arg);
                    }

                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to apply removeFunc." << endl;
                    }

                    // Now I need to check for any Device removals based upon the
                    // Updated Flag being NOT set.  I only do this for non-directed loads.  a paoid is directed.!

                    start = start.now();

                    do
                    {
                        pTempCtiDevice = _smartMap.remove(isDeviceNotUpdated, NULL);
                        if(pTempCtiDevice)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "  Evicting " << pTempCtiDevice->getName() << " from list" << endl;
                            }

                            // Effectively deletes the memory if there are no other "owners"
                        }

                    } while(pTempCtiDevice);
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to apply applyInvalidateNotUpdated." << endl;
                    }
                }
        }   // Temporary results are destroyed to free the connection
    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Attempting to clear device list..." << endl;
        }
        deleteList();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " getDevices:  " << e.why() << endl;
        }
        RWTHROW(e);

    }
}


bool CtiDeviceManager::refreshDeviceByPao(CtiDeviceSPtr pDev, LONG paoID)
{
    bool status = false;

    // Found it and it has not changed type at all.  Let's reload it based upon its type.
    if(pDev)
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();
        RWDBDatabase db = getDatabase();

        RWDBTable   keyTable;
        RWDBSelector selector = db.selector();

        pDev->getSQL( db, keyTable, selector );

        selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );
        RWDBReader rdr = selector.reader(conn);

        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
        }

        refreshDevices(status, rdr, DeviceFactory);

        if(!status)     // It was NOT found in the DB!
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " " << pDev->getName() << " removed from DB." << endl;
            _smartMap.remove(paoID);
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " Done reloading " << pDev->getName() << endl;
        }
    }

    // Now load the device properties onto the device.
    refreshDeviceProperties(paoID);

    return status;
}

/*
 * ptr_type anxiousDevice has asked to execute.  We make certain that no other device which is in his exclusion list is executing.
 */
bool CtiDeviceManager::mayDeviceExecuteExclusionFree(CtiDeviceSPtr anxiousDevice, CtiTablePaoExclusion &deviceexclusion)
{
    bool bstatus = false;

    try
    {
        if(anxiousDevice)
        {
            LockGuard  dev_guard(getMux());
            CtiTime now;

            // Make sure no other device out there has begun executing and doesn't want us to until they are done.
            // The device may also have logic which prevents it's executing.
            if( !anxiousDevice->isExecutionProhibited() && !anxiousDevice->isExecutionProhibitedByInternalLogic() )
            {
                if(anxiousDevice->hasExclusions())
                {
                    /*
                     *  Walk the anxiousDevice's exclusion list checking if any of the devices on it indicate that they are
                     *  currently executing.  If any of them are executing, the anxious device cannot start.
                     */
                    bool busted = false;
                    CtiDeviceSPtr device;
                    vector< CtiDeviceSPtr > anxiousDeviceBlocksThisVector;

                    if(anxiousDevice->getExclusion().hasTimeExclusion())
                    {
                        /* This exclusion identifies a cycle time type exclusion window/start/duration */
                        CtiTime open = anxiousDevice->getExclusion().getTimeSlotOpen();
                        CtiTime close = anxiousDevice->getExclusion().getTimeSlotClose();

                        if( anxiousDevice->getExclusion().isTimeExclusionOpen() )
                        {
                            // The window is open.  All proximity devices should eventually clear out of our way.
                            if(0 && getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Device " << anxiousDevice->getName() << " is in its execution window, checking proximity conflicts." << endl;
                                dout << "   CycleTime " << anxiousDevice->getExclusion().getCycleTimeExclusion().getCycleTime() <<
                                    ", Offset " << anxiousDevice->getExclusion().getCycleTimeExclusion().getCycleOffset() <<
                                    ", Duration " << anxiousDevice->getExclusion().getCycleTimeExclusion().getTransmitTime() << endl;
                            }

                            bstatus = true;             // Provided no proximity exclusion is executing, we can go!
                        }
                        else
                        {
                            if(0 && getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Device " << anxiousDevice->getName() << " is outside its execution window and will execute at " << open << " - " << close << endl;
                            }

                            deviceexclusion = anxiousDevice->getExclusion().getCycleTimeExclusion();                             // Pass this out to the callee as the device which blocked us first!
                            busted = true;          // Window is closed!
                            bstatus = false;        // Cannot execute.
                        }
                    }

                    CtiDevice::exclusions exvector = anxiousDevice->getExclusions();
                    CtiDevice::exclusions::iterator itr;

                    for(itr = exvector.begin(); !busted && itr != exvector.end(); itr++)
                    {
                        CtiTablePaoExclusion &paox = *itr;

                        switch(paox.getFunctionId())
                        {
                        case (CtiTablePaoExclusion::ExFunctionIdExclusion):
                            {
                                device = getEqual(paox.getExcludedPaoId());  // grab the excludable device

                                if(device)
                                {
                                    if(device->isExecuting())               // is the excludable executing?  This would block anxiousDevice.
                                    {
                                        if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << " Device: " << anxiousDevice->getName() << " Port: " << anxiousDevice->getPortID() << " cannot execute because device " << device->getName() << " port: " << device->getPortID() << " is executing.  TID: " << GetCurrentThreadId() << endl;
                                        }
                                        deviceexclusion = paox;     // Pass this out to the callee as the device which blocked us first!
                                        anxiousDeviceBlocksThisVector.clear();             // Cannot use the list to block other devices.
                                        busted = true;              // 20060228 CGP.  // Prevent additional loops.
                                        break;                      // we cannot go
                                    }/*
                                    else if( device->isExecutionProhibited(now, anxiousDevice->getID()) )  // This asks if anxiousDevice is already blocking "device".  We don't add it a second time.
                                    {
                                        if(0 && getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << " Device " << device->getName() << " is already blocked by " << anxiousDevice->getName() << endl;
                                        }
                                    }*/
                                    else
                                    {
                                        anxiousDeviceBlocksThisVector.push_back(device);    // Throw a copy of the shptr onto this vector to be marked out if we succeed.
                                    }
                                }

                                break;
                            }
                        case (CtiTablePaoExclusion::ExFunctionCycleTime):
                            {
                                // Processed above!
                                break;
                            }
                        case (CtiTablePaoExclusion::ExFunctionLMSubordination):
                            {
                                break; // Do not care about this stuff.
                            }
                        default:
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                                break;
                            }
                        }
                    }

                    /*
                     *  If we get through all proximity exclusion devices and no one is running, we MAY execute.
                     */
                    if(!busted && itr == exvector.end())
                    {
                        bstatus = true;                     // we may execute in this case.
                    }

                    //
                    // If none of anxiousDevice's exclusions devices caused it to blink, we will iterate across the vector and mark them prohibited.
                    //
                    if(!anxiousDeviceBlocksThisVector.empty())     // This tells me that I have no conflicting devices!
                    {
                        int procnt = 0;
                        vector< CtiDeviceSPtr >::iterator xitr;
                        for(xitr = anxiousDeviceBlocksThisVector.begin(); xitr != anxiousDeviceBlocksThisVector.end(); xitr++)
                        {
                            if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Device " << device->getName() << " prohibited because " << anxiousDevice->getName() << " is executing" << endl;
                            }
                            procnt++;
                            device = *xitr;
                            device->setExecutionProhibited(anxiousDevice->getID());
                        }

                        if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << anxiousDevice->getName() << " caused  " << procnt << " devices to be blocked because it is executing" << endl;
                        }
                        bstatus = true;
                    }
                }
                else
                {
                    bstatus = true;
                }
            }

            if(bstatus)
            {
                if( 0 && getDebugLevel() & DEBUGLEVEL_EXCLUSIONS && anxiousDevice->hasExclusions() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Device " << anxiousDevice->getName() << " is clear to execute" << endl;
                }
                anxiousDevice->setExecuting(true, anxiousDevice->selectCompletionTime());                    // Mark ourselves as executing!
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return bstatus;
}

/*
 * ptr_type anxiousDevice has completed an execution.  We must cleanup his mess.
 */
bool CtiDeviceManager::removeInfiniteExclusion(CtiDeviceSPtr anxiousDevice)
{
    bool bstatus = false;

    LockGuard  dev_guard(getMux());

    try
    {
        if(anxiousDevice)
        {
            apply( applyRemoveInfiniteProhibit, (void*)(anxiousDevice.get()));  // Remove prohibit mark from any device.
            anxiousDevice->setExecuting(false);                                 // Mark ourselves as _not_ executing!
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return bstatus;
}


void CtiDeviceManager::refreshExclusions(LONG id)
{
    LONG        lTemp = 0;
    CtiDeviceSPtr   pTempCtiDevice;

    LockGuard  dev_guard(getMux());       // Protect our iteration!

    spiterator itr;

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    RWDBDatabase db = getDatabase();

    RWDBTable   keyTable;

    RWDBSelector selector = db.selector();

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Looking for Device Exclusions" << endl;
    }
    CtiTablePaoExclusion::getSQL( db, keyTable, selector );

    // The servers do not care about the LM subordination.
    selector.where(keyTable["functionid"] != CtiTablePaoExclusion::ExFunctionLMSubordination && selector.where());

    if(id > 0)
    {
        selector.where(keyTable["paoid"] == id && selector.where());
    }

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << selector.asString() << endl;
    }

    RWDBReader rdr = selector.reader(conn);

    if(rdr.status().errorCode() == RWDBStatus::ok)
    {
        // clear the exclusion lists.
        apply( applyClearExclusions, (void*)id);
        _exclusionMap.removeAll(removeExclusionDevice, (void*)id);    // no known exclusion devices.
    }

    while( (setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        CtiDeviceBase* pSp = NULL;

        rdr["paoid"] >> lTemp;            // get the DeviceID

        if( _smartMap.entries() > 0 && (pTempCtiDevice = getEqual(lTemp)) )
        {
            CtiTablePaoExclusion paox;
            paox.DecodeDatabaseReader(rdr);
            // Add this exclusion into the list.
            pTempCtiDevice->addExclusion(paox);
            _exclusionMap.insert(pTempCtiDevice->getID(), pTempCtiDevice);      // May try to do multiple inserts, but is should not mater since the pointer gets in there at least one time.
        }
    }

    for( itr = _portExclusions.getMap().begin(); itr != _portExclusions.getMap().end(); itr++ )
    {
        if( _smartMap.find(itr->second->getID()) )
        {
            _exclusionMap.insert(itr->second->getID(), itr->second);
        }
        else
        {
            //  That device doesn't exist any more - delete it
            _portExclusions.getMap().erase(itr);
        }
    }

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Done looking for Device Exclusions" << endl;
    }
}


void CtiDeviceManager::refreshIONMeterGroups(LONG paoID)
{
    CtiDeviceSPtr pTempCtiDevice;

    {
        RWDBConnection conn = getConnection();
        RWDBDatabase db = getDatabase();
        RWDBSelector selector = db.selector();
        RWDBSelector tmpSelector;
        RWDBTable tblMeterGroup = db.table("devicemetergroup");
        RWDBTable tblPAObject = db.table("yukonpaobject");
        RWDBReader rdr;
        long tmpDeviceID;
        string tmpCollectionGroup,
        tmpTestCollectionGroup,
        tmpMeterNumber,
        tmpBillingGroup;

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for ION Meter Groups" << endl;
        }

        selector << tblMeterGroup["DeviceID"]
        << tblMeterGroup["MeterNumber"];
        selector.where((tblMeterGroup["DeviceID"] == tblPAObject["PAObjectID"]) && (tblPAObject["Type"].like("ION%")));

        if(paoID)
        {
            selector.where( selector.where() && tblPAObject["PAObjectID"] == paoID );
        }

        rdr = selector.reader(conn);
        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
        }

        if(rdr.status().errorCode() == RWDBStatus::ok)
        {
            while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
            {
                rdr["DeviceID"]            >> tmpDeviceID;
                rdr["MeterNumber"]         >> tmpMeterNumber;

                pTempCtiDevice = getEqual(tmpDeviceID);

                if(pTempCtiDevice)
                {
                    CtiDeviceION *tmpION = (CtiDeviceION *)(pTempCtiDevice.get());

                    tmpION->setMeterGroupData(tmpMeterNumber);
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Error reading ION Meter Groups from database: " << rdr.status().errorCode() << endl;
        }

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for ION Meter Groups" << endl;
        }
    }
}


void CtiDeviceManager::refreshMacroSubdevices(LONG paoID)
{
    int childcount = 0;
    CtiDeviceSPtr pTempCtiDevice;

    RWDBConnection conn = getConnection();
    RWDBDatabase db = getDatabase();
    RWDBSelector selector = db.selector();
    RWDBTable tblGenericMacro = db.table("GenericMacro");
    RWDBReader rdr;
    long tmpOwnerID, tmpChildID;

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Macro Subdevices" << endl;
    }

    {
        selector << rwdbName("childcount",rwdbCount("ChildID"));
        selector.from(tblGenericMacro);
        selector.where(tblGenericMacro["MacroType"] == RWDBExpr("GROUP"));
        if(paoID != 0) selector.where( tblGenericMacro["OwnerID"] == RWDBExpr( paoID ) && selector.where() );
        rdr = selector.reader(conn);

        if(setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok)
        {
            if( rdr() )
            {
                rdr["childcount"] >> childcount;
            }
        }

        selector.clear();
    }

    selector << tblGenericMacro["ChildID"] << tblGenericMacro["OwnerID"];
    selector.from(tblGenericMacro);
    selector.where(tblGenericMacro["MacroType"] == RWDBExpr("GROUP"));
    if(paoID != 0) selector.where( tblGenericMacro["OwnerID"] == RWDBExpr( paoID ) && selector.where() );
    selector.orderBy(tblGenericMacro["ChildOrder"]);

    RWDBResult macroResult = selector.execute(conn);
    RWDBTable myMacroTable = macroResult.table();

    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
    }

    if(childcount != 0 && macroResult.status().errorCode() == RWDBStatus::ok)
    {
        rdr = myMacroTable.reader();
        apply(applyClearMacroDeviceList, (void*)paoID);

        while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
        {
            rdr["OwnerID"] >> tmpOwnerID;
            rdr["ChildID"] >> tmpChildID;

            pTempCtiDevice = getEqual(tmpOwnerID);
            if(pTempCtiDevice)
            {
                CtiDeviceMacro * pOwner = (CtiDeviceMacro *)(pTempCtiDevice.get());
                if( (pTempCtiDevice = getEqual(tmpChildID)) )
                {
                    pOwner->addDevice(pTempCtiDevice);
                }
            }
        }
    }

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Macro Subdevices" << endl;
    }
}


void CtiDeviceManager::refreshMCTConfigs(LONG paoID)
{
    CtiDeviceSPtr pTempCtiDevice;

    LONG      tmpmctid;
    int       tmpwire[3],
              tmpconfigtype;
    double    tmpmpkh[3];
    string tmpconfigname,
              tmpconfigmode;

    {
        RWDBConnection conn   = getConnection();
        RWDBDatabase db       = getDatabase();
        RWDBSelector selector = db.selector();
        RWDBTable tblPAObject = db.table("yukonpaobject");
        RWDBTable mappingTbl  = db.table("mctconfigmapping");
        RWDBTable configTbl   = db.table("mctconfig");
        RWDBReader rdr;

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for MCT Configs" << endl;
        }

        selector << mappingTbl["mctid"]
                 << configTbl["configname"]
                 << configTbl["configtype"]
                 << configTbl["configmode"]
                 << configTbl["mctwire1"]
                 << configTbl["mctwire2"]
                 << configTbl["mctwire3"]
                 << configTbl["ke1"]
                 << configTbl["ke2"]
                 << configTbl["ke3"];

        selector.where( mappingTbl["mctid"]    == tblPAObject["paobjectid"] &&
                        mappingTbl["configid"] == configTbl["configid"] );

        if(paoID)
        {
            selector.where( selector.where() && tblPAObject["PAObjectID"] == paoID );
        }

        rdr = selector.reader(conn);
        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
        }

        if(rdr.status().errorCode() == RWDBStatus::ok)
        {
            while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
            {
                rdr["mctid"]      >> tmpmctid;
                rdr["configname"] >> tmpconfigname;
                rdr["configtype"] >> tmpconfigtype;
                rdr["configmode"] >> tmpconfigmode;

                rdr["mctwire1"]   >> tmpwire[0];
                rdr["mctwire2"]   >> tmpwire[1];
                rdr["mctwire3"]   >> tmpwire[2];

                rdr["ke1"]        >> tmpmpkh[0];
                rdr["ke2"]        >> tmpmpkh[1];
                rdr["ke3"]        >> tmpmpkh[2];

                pTempCtiDevice = getEqual(tmpmctid);

                if(pTempCtiDevice)
                {
                    CtiDeviceMCT *tmpMCT = (CtiDeviceMCT *)(pTempCtiDevice.get());

                    tmpMCT->setConfigData(tmpconfigname, tmpconfigtype, tmpconfigmode, tmpwire, tmpmpkh);
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Error reading MCT Configs from database: " << rdr.status().errorCode() << endl;
        }

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for MCT Configs" << endl;
        }
    }
}


void CtiDeviceManager::refreshMCT400Configs(LONG paoID)
{
    CtiDeviceSPtr pTempCtiDevice;

    LONG      tmpmctid, tmpdisconnectaddress;

    {
        RWDBConnection conn   = getConnection();
        RWDBDatabase db       = getDatabase();
        RWDBSelector selector = db.selector();
        RWDBTable configTbl   = db.table("devicemct400series");
        /*
        RWDBTable touDayMapping      = db.table("toudaymapping"),
                  touDayRateSwitches = db.table("toudayrateswitches"),
                  touSchedule        = db.table("touschedule");
        */

        //RWDBTable tblPAObject = db.table("yukonpaobject");
        RWDBReader rdr;

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for MCT Configs" << endl;
        }

        selector << configTbl["deviceid"];
        selector << configTbl["disconnectaddress"];

        if(paoID)
        {
            //  no need to bring yukonpaobject into this yet, we'll just link straight to the config table
            selector.where( selector.where() && configTbl["deviceid"] == paoID );
        }

        rdr = selector.reader(conn);
        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
        }

        if(rdr.status().errorCode() == RWDBStatus::ok)
        {
            while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
            {
                rdr["deviceid"]          >> tmpmctid;
                rdr["disconnectaddress"] >> tmpdisconnectaddress;

                pTempCtiDevice = getEqual(tmpmctid);

                if( pTempCtiDevice )
                {
                    CtiDeviceMCT410 *tmpMCT410 = (CtiDeviceMCT410 *)pTempCtiDevice.get();

                    if( tmpdisconnectaddress > 0 && tmpdisconnectaddress < 0x400000 )
                    {
                        tmpMCT410->setDisconnectAddress(tmpdisconnectaddress);
                    }
                    else if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - invalid disconnect address " << tmpdisconnectaddress << " for device \"" << pTempCtiDevice->getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Error reading MCT 400 Configs from database: " << rdr.status().errorCode() << endl;
        }

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for MCT Configs" << endl;
        }
    }
}


void CtiDeviceManager::refreshDynamicPaoInfo(LONG paoID)
{
    CtiDeviceSPtr device;
    long tmp_paobjectid, tmp_entryid;

    CtiTableDynamicPaoInfo dynamic_paoinfo;

    {
        RWDBConnection conn   = getConnection();
        RWDBDatabase db       = getDatabase();
        RWDBSelector selector = db.selector();
        RWDBTable keyTable;
        RWDBReader rdr;

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Dynamic PAO Info" << endl;
        }

        CtiTableDynamicPaoInfo::getSQL(db, keyTable, selector, _app_id);

        if(paoID)
        {
            selector.where( selector.where() && keyTable["paobjectid"] == paoID );
        }

        rdr = selector.reader(conn);
        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
        }

        if(rdr.status().errorCode() == RWDBStatus::ok)
        {
            while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
            {
                dynamic_paoinfo.DecodeDatabaseReader(rdr);

                rdr["paobjectid"] >> tmp_paobjectid;
                rdr["paobjectid"] >> tmp_entryid;

                device = getEqual(tmp_paobjectid);

                if( device )
                {
                    device->setDynamicInfo(dynamic_paoinfo);
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - no parent found for dynamic PAO info record (pao " << tmp_paobjectid << ", entryid " << tmp_entryid << ")  **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Error reading Dynamic PAO Info from database: " << rdr.status().errorCode() << endl;
        }

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Dynamic PAO Info" << endl;
        }
    }
}


// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// This method loads all the device properties/characteristics which must be appended to an already
// loaded device.
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
void CtiDeviceManager::refreshDeviceProperties(LONG paoID)
{
    CtiTime start, stop;

    start = start.now();
    refreshMacroSubdevices(paoID);
    stop = stop.now();
    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  Macro Subdevices" << endl;
    }

    start = start.now();
    refreshIONMeterGroups(paoID);
    stop = stop.now();
    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load ION Meter Groups" << endl;
    }

    start = start.now();
    refreshExclusions(paoID);
    stop = stop.now();
    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load Exclusions" << endl;
    }

    start = start.now();
    refreshMCTConfigs(paoID);
    stop = stop.now();
    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load MCT Configs" << endl;
    }

    start = start.now();
    refreshMCT400Configs(paoID);
    stop = stop.now();
    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load MCT 400 Configs" << endl;
    }

    start = start.now();
    refreshDynamicPaoInfo(paoID);
    stop = stop.now();
    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load Dynamic PAO Info" << endl;
    }


    if(_includeScanInfo)
    {
        start = start.now();
        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for scan rates." << endl;
        }
        refreshScanRates(paoID);

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for scan rates" << endl;
        }
        stop = stop.now();
        if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load  scan rates" << endl;
        }

        start = start.now();
        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for scan windows." << endl;
        }
        refreshDeviceWindows(paoID);

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for scan windows" << endl;
        }
        stop = stop.now();
        if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load scan windows" << endl;
        }
    }
}


void CtiDeviceManager::writeDynamicPaoInfo( void )
{
    static const char *sql          = "select max(entryid) from dynamicpaoinfo";
    static const char *dynamic_info = "dynamicinfo";
    static long max_entryid;

    vector<CtiTableDynamicPaoInfo *> dirty_info;

    for( spiterator dev_itr = begin(); dev_itr != end(); dev_itr++ )
    {
        //  passed by reference
        dev_itr->second->getDirtyInfo(dirty_info);
    }

    if( dirty_info.size() > 0 )
    {
        try
        {
            RWDBConnection conn = getConnection();
            RWDBReader rdr;
            RWDBStatus status;

            rdr = ExecuteQuery(conn, sql);

            if(rdr() && rdr.isValid())
            {
                rdr >> max_entryid;
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint: invalid reader, unable to select max_entryid, attempting to use " << max_entryid << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            //  just in case two applications are writing at once - if we make this an atomic
            //    operation, the select-maxid-before-write will be safe
            conn.beginTransaction(dynamic_info);

            vector<CtiTableDynamicPaoInfo *>::iterator itr;
            for( itr = dirty_info.begin(); conn.isValid() && itr != dirty_info.end(); itr++ )
            {
                long rowsAffected = 0;

                (*itr)->setOwner(_app_id);

                status = (*itr)->Update(conn, rowsAffected);

                //  update didn't work, so we have to assign a new entry ID
                //    this is clunky - entry ID is useless, since we key on Owner, PAO, and Key anyway
                if( status.errorCode() != RWDBStatus::ok || !rowsAffected )
                {
                    (*itr)->setEntryID(max_entryid + 1);

                    status = (*itr)->Insert(conn).errorCode();

                    if( status.errorCode() == RWDBStatus::ok )
                    {
                        max_entryid++;  //  increments the reference
                    }
                }

                if( status.errorCode() != RWDBStatus::ok )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - error (" << status.errorCode() << ") inserting/updating DynamicPaoInfo **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    (*itr)->dump();
                }

                //  TODO:  if the insert fails, this needs to keep the records around so it can write them in the future
                delete *itr;
            }

            conn.commitTransaction(dynamic_info);

            /*
            if( conn.commitTransaction(dynamic_info).errorCode() == RWDBStatus::ok )
            {
                //  clear it out if it was successfully inserted
                for( itr = dirty_info.begin(); conn.isValid() && itr != dirty_info.end(); itr++ )
                {
                    delete *itr;
                }
            }
            */
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
}


CtiDeviceManager::spiterator CtiDeviceManager::begin()
{
    return _smartMap.getMap().begin();
}
CtiDeviceManager::spiterator CtiDeviceManager::end()
{
    return _smartMap.getMap().end();
}

void CtiDeviceManager::apply(void (*applyFun)(const long, ptr_type, void*), void* d)
{
    try
    {
        int trycount = 0;

        #if 1
        LockGuard gaurd(getMux(), 30000);

        while(!gaurd.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint: Unable to lock device mutex.  Will retry. **** " << __FILE__ << " (" << __LINE__ << ") Last Acquired By TID: " << getMux().lastAcquiredByTID() << " Faddr: 0x" << applyFun << endl;
            }
            gaurd.tryAcquire(30000);

            if(trycount++ > 6)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint: Unable to lock device mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  CtiDeviceManager::apply " << endl;
                }
                break;
            }
        }
        #endif

        _smartMap.apply(applyFun,d);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

int CtiDeviceManager::select(bool (*selectFun)(const long, ptr_type, void*), void* d, vector< CtiDeviceManager::ptr_type > &coll)
{
    ptr_type p;
    spiterator itr;

    LockGuard gaurd(getMux(), 30000);

    while(!gaurd.isAcquired())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint: Unable to lock device manager mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        gaurd.tryAcquire(30000);
    }

    for(itr = begin(); itr != end(); itr++)
    {
        if( selectFun(itr->first, itr->second, d) )
        {
            coll.push_back(itr->second);
        }
    }

    return coll.size();
}

CtiDeviceManager::ptr_type CtiDeviceManager::find(bool (*findFun)(const long, const ptr_type &, void*), void* d)
{
    ptr_type p;

    try
    {
        int trycount = 0;
        LockGuard gaurd(getMux(), 30000);

        while(!gaurd.isAcquired())
        {
            if(trycount++ > 6)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint: Unable to lock device mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  CtiDeviceManager::find " << endl;
                }
                break;
            }
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint: Unable to lock device mutex.  Will retry. **** " << __FILE__ << " (" << __LINE__ << ") Last Acquired By TID: " << getMux().lastAcquiredByTID() << " Faddr: 0x" << findFun << endl;
            }
            gaurd.tryAcquire(30000);
        }

        p = _smartMap.find(findFun, d);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return p;
}

bool CtiDeviceManager::contains(bool (*findFun)(const long, const ptr_type &, void*), void* d)
{
    bool found = false;

    try
    {
        LockGuard gaurd(getMux(), 30000);

        while(!gaurd.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint: Unable to lock device manager mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            gaurd.tryAcquire(30000);
        }

        spiterator itr;

        for(itr = begin(); itr != end(); itr++)
        {
            if( findFun( itr->first, itr->second, d ) )
            {
                found = true;
                break;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return found;
}

CtiDeviceManager::ptr_type CtiDeviceManager::chooseExclusionDevice( LONG portid )
{
    CtiTime now;
    CtiDeviceSPtr devA;           //
    CtiDeviceSPtr devB;           //
    CtiDeviceSPtr devS;

    /*
     *  This function's sole purpose in life is to find devices on this port which are queued with work and to select the very
     *  very best one from their number to allow execution.  The parameters assigned into the device define when it needs to complete
     *  and subject itself to a re-evaluation.
     */

    /*
     *  The function below walks _all_ devices _on this port_ which have queued work and are time excluded.  During the walk any non-time excluded
     *  device in their lists are marked with the time at which the TX device needs to be evaluated again.  This time should be in the future
     *  and should is called
     */
    // Find the Best Time Excluded Device


    spiterator itr;

    /*
     * This loop should sweep the devices and examine each for time exclusions.  If a device has time exclusions it will
     * evaluate whether we are in or out of the time window.  If we are in the window and have work, this device will be selected (if it is the oldest unselected device)
     * If we are in the window and do not have work, we will allocate a portion of our window to any proximity excluded transmitter.
     * If we are not in the window, we mark all proximity excluded devices to the time we need to be examined again.
     */
    for(itr = _exclusionMap.getMap().begin(); itr != _exclusionMap.getMap().end(); itr++)
    {
        devA = itr->second;
        /*
         *  Only look at a given device if it has told us that it needs attention.
         *  This apply function only examines TIME excluded transmitters
         */
        if( devA->getPortID() == portid &&
            devA->isInhibited() == false &&
            devA->getExclusion().hasTimeExclusion() &&
            devA->getExclusion().getEvaluateNextAt() <= now )
        {
            // Is this transmitter permitted to transmit right now?
            if(devA->getExclusion().isTimeExclusionOpen())
            {
                // Does this transmitter have any queued work to perform?
                if(devA->hasQueuedWork())
                {
                    // We will preempt transmitter devS if and only if we have been waiting longer than it or we have more work to do
                    if( !devS || (devS && (devS->getExclusion().getExecutionGrant() > devA->getExclusion().getExecutionGrant())) )
                    {
                        // Select the transmitter with the oldest LastExclusionGrant.
                        devS = devA;
                        devS->getExclusion().setExecutionGrantExpires(devS->selectCompletionTime());
                    }
                    else
                    {
                        // Not sure if this case should ever occur.
                        // I would presume this problem becomes vastly more complex if two devS may be time excluded against one another.
                        //
                        devA->getExclusion().setEvaluateNextAt( devS->selectCompletionTime() );
                    }
                }
                else
                {   // Allocate out some of devA's time window.
                    CtiTime close = devA->getExclusion().getTimeSlotClose();
                    unsigned alloc_increment = gConfigParms.getValueAsInt("YUKON_TIME_SLOT_ALLOCATION_INCREMENT", 20);
                    CtiTime alloc_out = close < now + alloc_increment ? close : now + alloc_increment;
                    devA->getExclusion().setEvaluateNextAt( alloc_out );
                    _exclusionMap.apply(applyExecutionGrantExpiresIsEvaluateNext, (void*)(devA.get()));

                    #if 0
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(slog);
                        slog << CtiTime() << " " << devA->getName() << " has no queued work.  Proximity excluded devices released until " << devA->getExclusion().getEvaluateNextAt() << endl;
                    }
                    #endif
                }
            }
            else
            {
                CtiTime opens = devA->getExclusion().getTimeSlotOpen();
                if(now > opens) opens = devA->getExclusion().getNextTimeSlotOpen();

                devA->getExclusion().setEvaluateNextAt( opens );  // offset may be the next window open, or the partial allocation given up in the case of no codes.
                _exclusionMap.apply(applyExecutionGrantExpiresIsEvaluateNext, (void*)(devA.get()));
            }
        }
    }


    if(devS)
    {
        devS->getExclusion().setExecutionGrant(now);
        devS->getExclusion().setExecutionGrantExpires(devS->selectCompletionTime());
        devS->getExclusion().setExecuting( true, devS->getExclusion().getExecutionGrantExpires() );           // Make sure we know who is executing.
        _exclusionMap.apply(applyEvaluateNextByExecutingUntil, (void*)(devS.get()));
    }
    else       // We did not find any time excluded transmitters willing to take the ball.
    {
        // Find Best Proximity Excluded Device with queue entries.

        for(itr = _exclusionMap.getMap().begin(); itr != _exclusionMap.getMap().end(); itr++)
        {
            devA = itr->second;
            /*
             *  Only look at a given device if it has told us that it needs attention.
             *  This apply function only examines TIME excluded transmitters
             */
            /*
             *  In this case, we should be filtering off all time excluded device which setEvaluateNextAt() into the future up above.
             *  We will also not evaluate any proximity excluded devices which may have been bumped into the future above.
             */
            if( !devA->isInhibited() && devA->getPortID() == portid && devA->getExclusion().getEvaluateNextAt() <= now )
            {
                if(devA->hasQueuedWork())
                {
                    if( !devS || (devS && (devS->getExclusion().getExecutionGrant() > devA->getExclusion().getExecutionGrant())) )
                    {
                        devS = devA;    // Select the transmitter with the oldest lastTx.
                        devS->getExclusion().setExecutionGrantExpires(devS->selectCompletionTime());
                    }
                }
            }
        }

        if(devS)
        {
            devS->getExclusion().setExecutionGrant(now);
            devS->getExclusion().setExecutionGrantExpires(devS->selectCompletionTime());
            devS->getExclusion().setExecuting( true, devS->getExclusion().getExecutionGrantExpires() );           // Make sure we know who is executing.
            _exclusionMap.apply(applyEvaluateNextByExecutingUntil, (void*)(devS.get()));
        }
    }

    if(devS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(slog);
        slog << CtiTime() << " " << devS->getName() << " Execution Granted          " << devS->getExclusion().getExecutionGrant() << endl;
        slog << CtiTime() << " " << devS->getName() << " Execution Grant Expires at " << devS->getExclusion().getExecutionGrantExpires() << endl;
    }

    return devS;
}


//  This is a little distasteful, but necessary - a port must add preload devices to the exclusion list so they
//    can be time-excluded against one another
CtiDeviceManager &CtiDeviceManager::addPortExclusion( LONG paoID )
{
    ptr_type new_device;

    if( new_device = _smartMap.find(paoID) )
    {
        _exclusionMap.insert(paoID, new_device);

        //  this is so they're retained over DB reloads
        _portExclusions.insert(paoID, new_device);
    }

    return *this;
}


void CtiDeviceManager::refreshPointGroups(LONG paoID, CtiDeviceBase* (*Factory)(RWDBReader &))
{
    bool rowFound = false;
    RWTime start, stop, querytime;

    RWDBConnection conn = getConnection();
    RWDBDatabase db = getDatabase();

    RWDBTable   keyTable;
    RWDBSelector selector = db.selector();

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Point Group Devices" << endl;
    }
    CtiDeviceGroupPoint().getSQL( db, keyTable, selector );
    if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

    RWDBReader rdr = selector.reader(conn);
    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
    }
    refreshDevices(rowFound, rdr, Factory);

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Point Group Devices" << endl;
    }
}

//Currently sets up addressing for expresscom only.
void CtiDeviceManager::refreshGroupHierarchy(LONG deviceID)
{
    CtiDeviceManager::ptr_type device = RemoteGetEqual(deviceID);

    if( gConfigParms.isTrue("LOG_WITH_EXPRESSCOM_HIERARCHY") && (deviceID == 0 || (device && device->isGroup() && device->getType() == TYPE_LMGROUP_EXPRESSCOM)) )
    {
        CtiDeviceGroupBaseSPtr groupDevice = boost::static_pointer_cast<CtiDeviceGroupBase>(device);
        vector< CtiDeviceSPtr > match_coll;
        vector< CtiDeviceGroupBaseSPtr > groupVec;
        select(findAllExpresscomGroups, NULL, match_coll);

        //This makes me so very unhappy.
        for( vector< CtiDeviceSPtr >::iterator iter = match_coll.begin(); iter != match_coll.end(); iter++ )
        {
            groupVec.push_back(boost::static_pointer_cast<CtiDeviceGroupBase>(*iter));
        }

        if( deviceID != 0 )
        {
            groupDevice->clearChildren();//Remove all!

            for( vector< CtiDeviceGroupBaseSPtr >::iterator iter = groupVec.begin(); iter != groupVec.end(); iter++ )
            {
                if( (*iter)->getID() != deviceID )
                {
                    (*iter)->removeChild(deviceID); //We clear them, then check if they need to be added again
                    CtiDeviceGroupBase::ADDRESSING_COMPARE_RESULT result = groupDevice->compareAddressing(*iter);
                    if( result == CtiDeviceGroupBase::ADDRESSING_EQUIVALENT )
                    {
                        (*iter)->addChild(groupDevice);
                        groupDevice->addChild((*iter));
                    }
                    else if( result == CtiDeviceGroupBase::THIS_IS_PARENT )
                    {
                        groupDevice->addChild((*iter));
                    }
                    else if( result == CtiDeviceGroupBase::OPERAND_IS_PARENT )
                    {
                        (*iter)->addChild(groupDevice);
                    }
                }
            }

        }
        else
        {
            for( vector< CtiDeviceGroupBaseSPtr >::iterator allIter = groupVec.begin(); allIter != groupVec.end(); allIter++ )
            {
                (*allIter)->clearChildren();
            }

            for( vector< CtiDeviceGroupBaseSPtr >::iterator iter = groupVec.begin(); iter != groupVec.end(); iter++ )
            {
                vector< CtiDeviceGroupBaseSPtr >::iterator tempIter = iter;
                tempIter++;
                for( ; tempIter != groupVec.end(); tempIter++ )
                {
                    CtiDeviceGroupBase::ADDRESSING_COMPARE_RESULT result = (*iter)->compareAddressing(*tempIter);
                    if( result == CtiDeviceGroupBase::ADDRESSING_EQUIVALENT )
                    {
                        (*iter)->addChild((*tempIter));
                        (*tempIter)->addChild((*iter));
                    }
                    else if( result == CtiDeviceGroupBase::THIS_IS_PARENT )
                    {
                        (*iter)->addChild((*tempIter));
                    }
                    else if( result == CtiDeviceGroupBase::OPERAND_IS_PARENT )
                    {
                        (*tempIter)->addChild((*iter));
                    }
                }
            }
        }
    }
    //if not 0 and not a group, we do nothing!
}

