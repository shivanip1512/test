/*-----------------------------------------------------------------------------*
*
* File:   mgr_device
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_device.cpp-arc  $
* REVISION     :  $Revision: 1.50 $
* DATE         :  $Date: 2004/10/12 16:23:42 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )

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
#include "dev_idlc.h"
#include "dev_carrier.h"
#include "dev_mct.h"
#include "dev_repeater.h"
#include "dev_rtc.h"
#include "dev_rtm.h"
#include "dev_tap.h"
#include "dev_grp_emetcon.h"
#include "dev_grp_energypro.h"
#include "dev_grp_expresscom.h"
#include "dev_grp_golay.h"
#include "dev_grp_ripple.h"
#include "dev_grp_sa105.h"
#include "dev_grp_sa305.h"
#include "dev_grp_sa205.h"
#include "dev_grp_sadigital.h"
#include "dev_grp_versacom.h"
#include "dev_grp_mct.h"
#include "dev_mct_broadcast.h"
#include "yukon.h"

#include "devicetypes.h"
#include "resolvers.h"
#include "slctdev.h"
#include "rtdb.h"


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

// prevent any devB (proximity excluded device) which may be seleced to execute from interfereing with devA's next evaluation time.
static void applyExecutionGrantExpiresIsEvaluateNext(const long key, CtiDeviceSPtr devB, void* devSelect)
{
    CtiDevice *devA = (CtiDevice *)devSelect;

    if(devA != devB.get() && devA->getExclusion().proximityExcludes(devB->getID()))
    {
        RWTime now;

        // Use the most restrictive of it's current or devA's..
        if( devA->getExclusion().getEvaluateNextAt() > now &&
            ( devB->getExclusion().getExecutionGrantExpires() > devA->getExclusion().getEvaluateNextAt()) )
        {
            devB->getExclusion().setExecutionGrantExpires( devA->getExclusion().getEvaluateNextAt() );                      // prevent any devB  which may be seleced below from taking our entire slot.

            {
                CtiLockGuard<CtiLogger> doubt_guard(slog);
                slog << RWTime() << " " << devA->getName() << " requires " << devB->getName() << " to complete by " << devB->getExclusion().getExecutionGrantExpires() << " if grant occurs" << endl;
            }
        }
    }
}

static void applyEvaluateNextByExecutingUntil(const long key, CtiDeviceSPtr devB, void* devSelect)
{
    CtiDevice *devA = (CtiDevice *)devSelect;

    if(devA != devB.get() && devA->getExclusion().proximityExcludes(devB->getID()))
    {
        RWTime now;

        // Use the most restrictive of it's current or devA's..
        if( devB->getExclusion().getEvaluateNextAt() < devA->getExclusion().getExecutingUntil() ||
            devB->getExclusion().getEvaluateNextAt() < now )
        {
            devB->getExclusion().setEvaluateNextAt(devA->getExclusion().getExecutingUntil());    // mark out all proximity conflicts to when devA will be done.
            devB->setExecutionProhibited(devA->getID(), devA->getExclusion().getExecutingUntil());

            {
                CtiLockGuard<CtiLogger> doubt_guard(slog);
                slog << RWTime() << " " << devB->getName() << "'s execution blocked by " << devA->getName() << " until " << devB->getExclusion().getEvaluateNextAt() << endl;
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

        if(found && getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
        {
            CtiLockGuard<CtiLogger> doubt_guard(slog);
            slog << RWTime() << " Device " << Device->getName() << " no longer prohibited because of " << pAnxiousDevice->getName() << "." << endl;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    if( Device->getType() == TYPE_MACRO )
        ((CtiDeviceMacro *)(Device.get()))->clearDeviceList();
}

void CtiDeviceManager::dumpList(void)
{
    CtiDeviceBase *p = NULL;
    try
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() <<" There are " << _smartMap.entries() << " entries" << endl;
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
            dout << RWTime() << " **** Exclusions **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << RWTime() << " **** Exclusions **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Attempting to clear device list..." << endl;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " DumpDevices:  " << e.why() << endl;
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
            dout << RWTime() << " There are no entries in the remote device list" << endl;
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
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return p;
}

CtiDeviceManager::ptr_type CtiDeviceManager::RemoteGetEqualbyName (const RWCString &RemoteName)
{
    ptr_type p;

    RWCString cmpname = RemoteName;
    RWCString devname;

    cmpname.toLower();

    if(_smartMap.entries() == 0)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " There are no entries in the device manager list" << endl;
        }
    }

    spiterator itr;

    for(itr = begin(); itr != end(); itr++)
    {
        p = (itr->second);

        devname = p->getName();
        devname.toLower();

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

CtiDeviceManager::CtiDeviceManager() :
_includeScanInfo(false),
_removeFunc(NULL)
{
}

CtiDeviceManager::~CtiDeviceManager()
{
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
        dout << RWTime() << " Looking for ScanRates" << endl;
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
        dout << RWTime() <<" Done looking for ScanRates" << endl;
    }

    if(setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok)
    {
        if(id > 0)
        {
            CtiDeviceSPtr devsptr = getEqual(id);
            pTempCtiDevice = devsptr.get();
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
        pTempCtiDevice = devsptr.get();

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
                dout << RWTime() << " There are scanrates in the scanrate table for a nonscannable device: " << pTempCtiDevice->getName() << endl;
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
        dout << RWTime() << " Looking for Device Windows" << endl;
    }
    CtiTableDeviceWindow::getSQL( db, keyTable, selector );

    if(id > 0)
    {
        selector.where(keyTable["deviceid"] == id && selector.where());
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

        CtiDeviceSPtr devsptr = getEqual(id);
        pTempCtiDevice = devsptr.get();

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
                    dout << RWTime() << " There are scan windows in the device window table for a nonscannable device." << endl;
                }
            }
        }
    }

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Done looking for ScanWindows" << endl;
    }
}

#if 0
void CtiDeviceManager::RefreshDeviceRoute(LONG id)
{
    LONG        lTemp = 0;
    CtiDeviceBase*   pTempCtiDevice = NULL;

    LockGuard  dev_guard(monitor());       // Protect our iteration!

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    RWDBDatabase db = getDatabase();
    RWDBTable   keyTable;

    RWDBSelector selector = db.selector();

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Looking for Routes" << endl;
    }
    CtiTableDeviceRoute::getSQL( db, keyTable, selector );

    if(id > 0)
    {
        selector.where(keyTable["deviceid"] == id && selector.where());
    }

    RWDBReader rdr = selector.reader(conn);
    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
    }

    while( (setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        rdr["deviceid"] >> lTemp;            // get the DeviceID

        if( _smartMap.entries() > 0 && ((pTempCtiDevice = getEqual(lTemp)) != NULL) )
        {
            if(pTempCtiDevice->getType())       // FIX FIX FIX FIX FIX
            {
                ((CtiDeviceDLCBase*)pTempCtiDevice)->DecodeRoutesDatabaseReader(rdr);        // Fills himself in from the reader
            }
        }
    }

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Done looking for Routes" << endl;
    }
}
#endif

void CtiDeviceManager::refresh(CtiDeviceBase* (*Factory)(RWDBReader &), bool (*removeFunc)(CtiDeviceSPtr&,void*), void *d, LONG paoID, RWCString category, RWCString devicetype)
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
                    dout << RWTime() << " " << pDev->getName() << " has changed type to " << devicetype << " from " << desolveDeviceType(pDev->getType()) << endl;
                }

                if( _smartMap.remove(paoID) )
                {
                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Old device object has been orphaned " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

    RWTime start, stop, querytime;

    if(removeFunc != NULL)
    {
        _removeFunc = removeFunc;
    }

    try
    {
        CtiDeviceBase *pSp;

        {
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
                        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  SIXNET " << endl;
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
                        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  Meters & IEDs" << endl;
                    }


                    start = start.now();
                    {
                        RWDBConnection conn = getConnection();
                        RWDBDatabase db = getDatabase();

                        RWDBTable   keyTable;
                        RWDBSelector selector = db.selector();

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for DNP/ION/LMI Devices" << endl;
                        }
                        CtiDeviceDNP().getSQL( db, keyTable, selector );
                        if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                        RWDBReader rdr = selector.reader(conn);
                        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                        }
                        refreshDevices(rowFound, rdr, Factory);

                        if(DebugLevel & 0x00020000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for DNP/ION/LMI Devices" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  DNP/ION/LMI Devices" << endl;
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
                        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load RTM Devices" << endl;
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
                        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  TAP Devices" << endl;
                    }


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
                        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  IDLC Target Devices" << endl;
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
                        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  MCT Broadcast Devices" << endl;
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
                        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  DLC Devices" << endl;
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

                        selector.where( (rwdbUpper(keyTable["type"]) == RWDBExpr("REPEATER 800") ||
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
                        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  REPEATER Devices" << endl;
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
                        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  CBC Devices" << endl;
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
                        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  RTC Devices" << endl;
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
                            dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  Emetcon Group Devices" << endl;
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
                            dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  Versacom Group Devices" << endl;
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
                            dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  ExpressCom Group Devices" << endl;
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
                            dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  EnergyPro Group Devices" << endl;
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
                            dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  Ripple Group Devices" << endl;
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
                            dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  MCT Load Group Devices" << endl;
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
                            dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  Macro Devices" << endl;
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
                            dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  105 Group Devices" << endl;
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
                            dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  205 Group Devices" << endl;
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
                            dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load 305 Devices" << endl;
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
                            dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  SA Digital Group Devices" << endl;
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
                            dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  Golay Group Devices" << endl;
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
                        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  System Devices" << endl;
                    }

                    // Now load the device properties onto the devices
                    refreshDeviceProperties(paoID);
                }

                if(getErrorCode() != RWDBStatus::ok && getErrorCode() != RWDBStatus::endOfFetch)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to apply removeFunc." << endl;
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
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "  Evicting " << pTempCtiDevice->getName() << " from list" << endl;
                            }

                            // Effectively deletes the memory if there are no other "owners"
                        }

                    } while(pTempCtiDevice);
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to apply applyInvalidateNotUpdated." << endl;
                    }
                }
            }
        }   // Temporary results are destroyed to free the connection
    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Attempting to clear device list..." << endl;
        }
        deleteList();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " getDevices:  " << e.why() << endl;
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
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << RWTime() << " " << pDev->getName() << " removed from DB." << endl;
            _smartMap.remove(paoID);
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << RWTime() << " Done reloading " << pDev->getName() << endl;
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
            RWTime now;

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
                        RWTime open = anxiousDevice->getExclusion().getTimeSlotOpen();
                        RWTime close = anxiousDevice->getExclusion().getTimeSlotClose();

                        if( anxiousDevice->getExclusion().isTimeExclusionOpen() )
                        {
                            // The window is open.  All proximity devices should eventually clear out of our way.
                            if(0 && getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Device " << anxiousDevice->getName() << " is in its execution window, checking proximity conflicts." << endl;
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
                                dout << RWTime() << " Device " << anxiousDevice->getName() << " is outside its execution window and will execute at " << open << " - " << close << endl;
                            }

                            deviceexclusion = anxiousDevice->getExclusion().getCycleTimeExclusion();                             // Pass this out to the callee as the device which blocked us first!
                            busted = true;
                            bstatus = false;
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
                                device = getEqual(paox.getExcludedPaoId());

                                if(device)
                                {
                                    if(device->isExecuting())
                                    {
                                        if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " Device " << anxiousDevice->getName() << " cannot execute because " << device->getName() << " is executing" << endl;
                                        }
                                        deviceexclusion = paox;     // Pass this out to the callee as the device which blocked us first!
                                        anxiousDeviceBlocksThisVector.clear();             // Cannot use the list to block other devices.
                                        bstatus = false;
                                        break;                      // we cannot go
                                    }
                                    else if( !device->isExecutionProhibited(now, anxiousDevice->getID()) )
                                    {
                                        bstatus = true;             // Do not remark a device excluded by this id a second time.
                                    }
                                    else
                                    {
                                        anxiousDeviceBlocksThisVector.push_back(device);
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
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                                break;
                            }
                        }
                    }

                    //
                    // If none of anxiousDevice's exclusions devices caused it to blink, we will iterate across the vector and mark them prohibited.
                    //
                    if(!anxiousDeviceBlocksThisVector.empty())     // This tells me that I have no conflicting devices!
                    {
                        vector< CtiDeviceSPtr >::iterator xitr;
                        for(xitr = anxiousDeviceBlocksThisVector.begin(); xitr != anxiousDeviceBlocksThisVector.end(); xitr++)
                        {
                            if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Device " << device->getName() << " prohibited because " << anxiousDevice->getName() << " is executing" << endl;
                            }
                            device = *xitr;
                            device->setExecutionProhibited(anxiousDevice->getID());
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
                    dout << RWTime() << " Device " << anxiousDevice->getName() << " is clear to execute" << endl;
                }
                anxiousDevice->getExclusion().setExecutingUntil(anxiousDevice->selectCompletionTime());                    // Mark ourselves as executing!
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        dout << RWTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        dout << RWTime() << " Looking for Device Exclusions" << endl;
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

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Done looking for Device Exclusions" << endl;
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
        RWCString tmpCollectionGroup,
        tmpTestCollectionGroup,
        tmpMeterNumber,
        tmpBillingGroup;

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for ION Meter Groups" << endl;
        }

        selector << tblMeterGroup["DeviceID"]
        << tblMeterGroup["CollectionGroup"]
        << tblMeterGroup["TestCollectionGroup"]
        << tblMeterGroup["MeterNumber"]
        << tblMeterGroup["BillingGroup"];
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
                rdr["CollectionGroup"]     >> tmpCollectionGroup;
                rdr["TestCollectionGroup"] >> tmpTestCollectionGroup;
                rdr["MeterNumber"]         >> tmpMeterNumber;
                rdr["BillingGroup"]        >> tmpBillingGroup;

                pTempCtiDevice = getEqual(tmpDeviceID);

                if(pTempCtiDevice)
                {
                    CtiDeviceION *tmpION = (CtiDeviceION *)(pTempCtiDevice.get());

                    tmpION->setMeterGroupData(tmpCollectionGroup,
                                              tmpTestCollectionGroup,
                                              tmpMeterNumber,
                                              tmpBillingGroup);
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        apply(applyClearMacroDeviceList, NULL);

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
#if 0
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " There is a DB error or zero entries in GenericMacro Table. DB return code: " << macroResult.status().errorCode() << endl;
    }
#endif

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
    RWCString tmpconfigname,
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
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Error reading MCT Configs from database: " << rdr.status().errorCode() << endl;
        }

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for MCT Configs" << endl;
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
    RWTime start, stop;

    start = start.now();
    refreshMacroSubdevices(paoID);
    stop = stop.now();
    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  Macro Subdevices" << endl;
    }

    start = start.now();
    refreshIONMeterGroups(paoID);
    stop = stop.now();
    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load ION Meter Groups" << endl;
    }

    start = start.now();
    refreshExclusions(paoID);
    stop = stop.now();
    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load Exclusions" << endl;
    }

    start = start.now();
    refreshMCTConfigs(paoID);
    stop = stop.now();
    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load MCT Configs" << endl;
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
            dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  scan rates" << endl;
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
            dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load scan windows" << endl;
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

        LockGuard gaurd(getMux(), 30000);

        while(!gaurd.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint: Unable to lock port mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            gaurd.tryAcquire(30000);

            if(trycount++ > 6)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint: Unable to lock port mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  CtiPortManager::apply " << endl;
                }
                return;
            }
        }

        _smartMap.apply(applyFun,d);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

CtiDeviceManager::ptr_type CtiDeviceManager::find(bool (*findFun)(const long, ptr_type, void*), void* d)
{
    ptr_type p;

    try
    {
        LockGuard gaurd(getMux(), 30000);

        while(!gaurd.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint: Unable to lock device manager mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            gaurd.tryAcquire(30000);
        }

        spiterator itr;

        for(itr = begin(); itr != end(); itr++)
        {
            if( findFun( itr->first, itr->second, d ) )
            {
                p = itr->second;
                break;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return p;
}

bool CtiDeviceManager::contains(bool (*findFun)(const long, ptr_type, void*), void* d)
{
    bool found = false;

    try
    {
        LockGuard gaurd(getMux(), 30000);

        while(!gaurd.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint: Unable to lock device manager mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return found;
}

CtiDeviceManager::ptr_type CtiDeviceManager::chooseExclusionDevice( LONG portid )
{
    RWTime now;
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
        if( portid == devA->getPortID() &&
            devA->getExclusion().hasTimeExclusion() &&
            devA->getExclusion().getEvaluateNextAt() <= now )
        {
            // Is this transmitter permitted to transmit right now?
            if(devA->getExclusion().isTimeExclusionOpen())
            {
                // Does this transmitter have any queued work to perform?
                if(devA->hasQueuedWork())
                {
                    // We will preempt transmitter devS if and only if we have been waiting longer than it
                    if( !devS || (devS && devS->getExclusion().getExecutionGrant() < devA->getExclusion().getExecutionGrant()) )
                    {
                        // Select the transmitter with the oldest LastExclusionGrant.
                        devS = devA;
                        devS->getExclusion().setExecutionGrantExpires(devS->selectCompletionTime());
                    }
                    else
                    {
                        // Not sure if this case should ever occur.
                        // I would presume this problem becomes vastly more complex if two devS may be time excluded against one another.
                        devA->getExclusion().setEvaluateNextAt( devS->getExclusion().getExecutingUntil() );
                    }
                }
                else
                {   // Allocate out some of devA's time window.
                    RWTime close = devA->getExclusion().getTimeSlotClose();
                    unsigned alloc_increment = gConfigParms.getValueAsInt("YUKON_TIME_SLOT_ALLOCATION_INCREMENT", 20);
                    RWTime alloc_out = close < now + alloc_increment ? close : now + alloc_increment;
                    devA->getExclusion().setEvaluateNextAt( alloc_out );
                    _exclusionMap.apply(applyExecutionGrantExpiresIsEvaluateNext, (void*)(devA.get()));

                    #if 0
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(slog);
                        slog << RWTime() << " " << devA->getName() << " has no queued work.  Proximity excluded devices released until " << devA->getExclusion().getEvaluateNextAt() << endl;
                    }
                    #endif
                }
            }
            else
            {
                RWTime opens = devA->getExclusion().getTimeSlotOpen();
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
        devS->getExclusion().setExecutingUntil( devS->getExclusion().getExecutionGrantExpires() );           // Make sure we know who is executing.
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
            if(  portid == devA->getPortID() && devA->getExclusion().getEvaluateNextAt() <= now )
            {
                if(devA->hasQueuedWork())
                {
                    if(!devS || devS->getExclusion().getExecutionGrant() > devA->getExclusion().getExecutionGrant())
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
            devS->getExclusion().setExecutingUntil( devS->getExclusion().getExecutionGrantExpires() );           // Make sure we know who is executing.
            _exclusionMap.apply(applyEvaluateNextByExecutingUntil, (void*)(devS.get()));
        }
    }

    if(devS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(slog);
        slog << RWTime() << " " << devS->getName() << " Execution Granted          " << devS->getExclusion().getExecutionGrant() << endl;
        slog << RWTime() << " " << devS->getName() << " Execution Grant Expires at " << devS->getExclusion().getExecutionGrantExpires() << endl;
    }

    return devS;
}

