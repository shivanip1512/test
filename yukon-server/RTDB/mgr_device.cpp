/*-----------------------------------------------------------------------------*
*
* File:   mgr_device
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_device.cpp-arc  $
* REVISION     :  $Revision: 1.34 $
* DATE         :  $Date: 2003/12/19 16:23:47 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )

#include <rw/db/db.h>

#include "rtdb.h"
#include "mgr_device.h"
#include "dbaccess.h"
#include "dev_macro.h"
#include "dev_cbc.h"
#include "dev_dnp.h"
#include "dev_ion.h"
#include "dev_remote.h"
#include "dev_meter.h"
#include "dev_idlc.h"
#include "dev_carrier.h"
#include "dev_repeater.h"
#include "dev_tap.h"
#include "dev_grp_emetcon.h"
#include "dev_grp_energypro.h"
#include "dev_grp_expresscom.h"
#include "dev_grp_ripple.h"
#include "dev_grp_versacom.h"
#include "dev_grp_mct.h"
#include "dev_mct_broadcast.h"
#include "yukon.h"

#include "devicetypes.h"
#include "resolvers.h"
#include "slctdev.h"
#include "rtdb.h"


bool findExecutingAndExcludedDevice(CtiDeviceManager::val_pair vpd, void* d)
{
    bool bstatus = false;

    CtiDeviceBase* Device = vpd.second;

    CtiDeviceBase *pAnxiousDevice = (CtiDeviceBase *)d;       // This is the port that wishes to execute!

    if(pAnxiousDevice->getID() != Device->getID())      // And it is not me...
    {
        bool excluded = pAnxiousDevice->isDeviceExcluded(Device->getID());

        if(excluded)
        {
            // Ok, now decide if that excluded port is executing....
            bstatus = Device->isExecuting();
        }
    }

    return bstatus;
}

inline void applyClearExclusions(const CtiHashKey *unusedkey, CtiDeviceBase *&Device, void* d)
{
    Device->clearExclusions();
    return;
}

inline void applyRemoveProhibit(const CtiHashKey *unusedkey, CtiDeviceBase *&Device, void* d)
{
    try
    {
        CtiDevice *pAnxiousDevice = (CtiDevice *)d;       // This is the port that wishes to execute!
        LONG did = (LONG)pAnxiousDevice->getID();         // This is the id which is to be pulled from the prohibition list.

        if(Device->isExecutionProhibited())     // There is at least one entry in the list...
        {
            bool found = Device->removeExecutionProhibited( did );

            if(found && getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Device " << Device->getName() << " no longer prohibited because of " << pAnxiousDevice->getName() << "." << endl;
            }
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

inline RWBoolean
isDeviceIdStaticId(CtiDeviceBase *pDevice, void* d)
{
    CtiDeviceBase *pSp = (CtiDeviceBase *)d;

    return(pDevice->getID() == pSp->getID());
}

inline RWBoolean
isDeviceNotUpdated(CtiDeviceBase *pDevice, void* d)
{
    // Return TRUE if it is NOT SET
    return(RWBoolean(!pDevice->getUpdatedFlag()));
}


void
ApplyDeviceResetUpdated(const CtiHashKey *key, CtiDeviceBase *&pDevice, void* d)
{
    pDevice->resetUpdatedFlag();
    return;
}

void
ApplyInvalidateNotUpdated(const CtiHashKey *key, CtiDeviceBase *&pPt, void* d)
{
    if(!pPt->getUpdatedFlag())
    {
        pPt->setValid(FALSE);   //   NOT NOT NOT Valid
    }
    return;
}

void
ApplyClearMacroDeviceList(const CtiHashKey *key, CtiDeviceBase *&pDevice, void *d)
{
    if( pDevice->getType() == TYPE_MACRO )
        ((CtiDeviceMacro *)pDevice)->clearDeviceList();
}


void CtiDeviceManager::DumpList(void)
{
    CtiDeviceBase *p = NULL;
    try
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() <<" There are " << Map.entries() << " entries" << endl;
        }
        CtiRTDBIterator itr(Map);

        for(;itr();)
        {
            p = itr.value();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                p->DumpData();
            }
        }
    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Attempting to clear device list..." << endl;
        }

        Map.clearAndDestroy();

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
    CtiDeviceBase*    pTempCtiDevice = NULL;

    while( (setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        rowFound = true;
        rdr["deviceid"] >> lTemp;            // get the DeviceID

        if( Map.entries() > 0 && ((pTempCtiDevice = getEqual(lTemp)) != NULL) )
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
                Map.insert( CTIDBG_new CtiHashKey(pSp->getID()), pSp );    // Stuff it in the list
            }
        }
    }
}



CtiDeviceBase* CtiDeviceManager::RemoteGetPortRemoteEqual (LONG Port, LONG Remote)
{
    CtiDeviceBase     *p = NULL;

    RWRecursiveLock<RWMutexLock>::LockGuard  dev_guard(getMux());

    CtiRTDBIterator   itr(Map);


    if(Map.entries() == 0)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " There are no entries in the remote device list" << endl;
        }
    }

    for(;itr();)
    {
        p = itr.value();

        if( p->getAddress() > 0 &&  p->getPortID() == Port && p->getAddress() == Remote )
        {
            break;
        }

        p = NULL;
    }

    return p;
}

CtiDeviceBase* CtiDeviceManager::RemoteGetEqual (LONG Dev)
{
    return getEqual(Dev);
}

CtiDeviceBase* CtiDeviceManager::getEqual (LONG Dev)
{
    CtiHashKey key(Dev);
    return Map.findValue(&key);
}

CtiDeviceBase* CtiDeviceManager::RemoteGetEqualbyName (const RWCString &RemoteName)
{
    // RWMutexLock::LockGuard guard(getMux());

    CtiDeviceBase     *p = NULL;
    CtiRTDBIterator   itr(Map);

    RWCString cmpname = RemoteName;
    RWCString devname;

    cmpname.toLower();

    if(Map.entries() == 0)
    {
        cerr << "There are no entries in the remote device list" << endl;
    }

    for(;itr();)
    {
        p = itr.value();

        devname = p->getName();
        devname.toLower();

        // cout << p->getName() << " == " << RemoteName << endl;
        if( devname == cmpname )
        {
            break;
        }
        else
        {
            p = NULL;
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

void CtiDeviceManager::DeleteList(void)
{
    Map.clearAndDestroy();
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
    LONG        lTemp = 0;
    CtiDeviceBase*   pTempCtiDevice = NULL;

    LockGuard  dev_guard(monitor());       // Protect our iteration!

    CtiRTDBIterator   itr(Map);

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
            pTempCtiDevice = getEqual(id);
            if(pTempCtiDevice) pTempCtiDevice->invalidateScanRates();     // Mark all Scan Rate elements as needing refresh..
        }
        else
        {
            for(;itr();)
            {
                pTempCtiDevice = itr.value();
                if(pTempCtiDevice) pTempCtiDevice->invalidateScanRates();     // Mark all Scan Rate elements as needing refresh..
            }
        }
    }

    while( (setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        CtiDeviceBase* pSp = NULL;

        rdr["deviceid"] >> lTemp;            // get the DeviceID

        if( Map.entries() > 0 && ((pTempCtiDevice = getEqual(lTemp)) != NULL) )
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
                dout << RWTime() << " There are scanrates in the scanrate table for a nonscannable device." << endl;
            }
        }
    }

    if(setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok || setErrorCode(rdr.status().errorCode()) == RWDBStatus::endOfFetch)
    {
        itr.reset(Map);

        // Remove any scan rates which were NOT refreshed, but only if we read a few correctly!
        for(;itr();)
        {
            pTempCtiDevice = itr.value();
            pTempCtiDevice->deleteNonUpdatedScanRates();
        }
    }
}

void CtiDeviceManager::refreshDeviceWindows(LONG id)
{
    LONG        lTemp = 0;
    CtiDeviceBase*   pTempCtiDevice = NULL;

    LockGuard  dev_guard(monitor());       // Protect our iteration!

    CtiRTDBIterator   itr(Map);

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
        CtiDeviceBase* pSp = NULL;

        rdr["deviceid"] >> lTemp;            // get the DeviceID

        if( Map.entries() > 0 && ((pTempCtiDevice = getEqual(lTemp)) != NULL) )
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

        if( Map.entries() > 0 && ((pTempCtiDevice = getEqual(lTemp)) != NULL) )
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

void CtiDeviceManager::refresh(CtiDeviceBase* (*Factory)(RWDBReader &), bool (*removeFunc)(CtiDeviceBase*,void*), void *d, LONG paoID, RWCString category, RWCString devicetype)
{
    if(paoID != 0)
    {
        bool rowFound = false;
        CtiDeviceBase *pDev = getEqual(paoID);

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

                if( orphan(paoID) )
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


void CtiDeviceManager::refreshList(CtiDeviceBase* (*Factory)(RWDBReader &), bool (*removeFunc)(CtiDeviceBase*,void*), void *arg, LONG paoID)
{
    CtiDeviceBase *pTempCtiDevice = NULL;
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
                    Map.apply(ApplyDeviceResetUpdated, NULL); // Reset everyone's Updated flag iff not a directed load.
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

                        selector.where( keyTable["type"] == "SIXNET" && selector.where() );
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
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for DNP/ION Devices" << endl;
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
                            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for DNP/ION Devices" << endl;
                        }
                    }
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load  DNP/ION Devices" << endl;
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

                        selector.where( (keyTable["type"]==RWDBExpr("REPEATER 800") ||
                                         keyTable["type"]==RWDBExpr("REPEATER")) && selector.where() );   // Need to attach a few conditions!
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
                        selector.where( keyTable["type"]==RWDBExpr("System") && selector.where() );   // Need to attach a few conditions!
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
                        removeAndDestroy(_removeFunc, arg);
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

#if 0               // Not sure that anyone(me) knows what valid means for this stuff.
                    if(paoID == 0)
                    {
                        Map.apply(ApplyInvalidateNotUpdated, NULL);
                    }
                    else
                    {
                        pTempCtiDevice = getEqual(paoID);
                        if(pTempCtiDevice)
                        {
                            if(!pTempCtiDevice->getUpdatedFlag())
                            {
                                pTempCtiDevice->setValid(FALSE);   //   NOT NOT NOT Valid
                            }
                        }
                    }
#endif

                    do
                    {
                        pTempCtiDevice = remove(isDeviceNotUpdated, NULL);
                        if(pTempCtiDevice != NULL)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "  Evicting " << pTempCtiDevice->getName() << " from list" << endl;
                            }

                            delete pTempCtiDevice;
                        }

                    } while(pTempCtiDevice != NULL);
                    stop = stop.now();
                    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to apply ApplyInvalidateNotUpdated." << endl;
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
            dout << RWTime() << " Attempting to clear point list..." << endl;
        }
        Map.clearAndDestroy();


        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " getDevices:  " << e.why() << endl;
        }
        RWTHROW(e);

    }
}


bool CtiDeviceManager::refreshDeviceByPao(CtiDeviceBase *&pDev, LONG paoID)
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
            orphan(paoID);
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
bool CtiDeviceManager::mayDeviceExecuteExclusionFree(CtiDeviceBase* anxiousDevice, CtiTablePaoExclusion &deviceexclusion)
{
    bool bstatus = false;

    try
    {
        if(anxiousDevice)
        {
            RWRecursiveLock<RWMutexLock>::LockGuard  dev_guard(getMux());

            // Make sure no other device out there has begun executing and doesn't want us to until they are done.
            // The device may also have logic which prevents it's executing.
            if( !anxiousDevice->isExecutionProhibited() && !anxiousDevice->isExecutionProhibitedByInternalLogic() )
            {
                if(anxiousDevice->hasExclusions())
                {
                    CtiDeviceBase *device = 0;
                    vector< CtiDeviceBase* > exlist;

                    CtiDevice::exclusions exvector = anxiousDevice->getExclusions();
                    CtiDevice::exclusions::iterator itr;

                    for(itr = exvector.begin(); itr != exvector.end(); itr++)
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
                                        deviceexclusion = paox;   // Pass this out to the callee!
                                        exlist.clear();         // Cannot use it!
                                        break;                  // we cannot go
                                    }
                                    else
                                    {
                                        exlist.push_back(device);
                                    }
                                }

                                break;
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

                    if(!exlist.empty())     // This tells me that I have no conflicting devices!
                    {
                        vector< CtiDeviceBase* >::iterator xitr;
                        for(xitr = exlist.begin(); xitr != exlist.end(); xitr++)
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
                if(anxiousDevice->hasExclusions() && getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Device " << anxiousDevice->getName() << " is clear to execute" << endl;
                }
                anxiousDevice->setExecuting(true);                    // Mark ourselves as executing!
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
bool CtiDeviceManager::removeDeviceExclusionBlocks(CtiDeviceBase* anxiousDevice)
{
    bool bstatus = false;

    RWRecursiveLock<RWMutexLock>::LockGuard  dev_guard(getMux());

    try
    {
        if(anxiousDevice)
        {
            apply( applyRemoveProhibit, (void*)anxiousDevice);   // Remove prohibit mark from any device.
            anxiousDevice->setExecuting(false);                               // Mark ourselves as executing!
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
    CtiDeviceBase*   pTempCtiDevice = NULL;

    LockGuard  dev_guard(monitor());       // Protect our iteration!

    // clear the exclusion lists.
    apply( applyClearExclusions, NULL);

    CtiRTDBIterator   itr(Map);

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

    while( (setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        CtiDeviceBase* pSp = NULL;

        rdr["paoid"] >> lTemp;            // get the DeviceID

        if( Map.entries() > 0 && ((pTempCtiDevice = getEqual(lTemp)) != NULL) )
        {
            CtiTablePaoExclusion paox;
            paox.DecodeDatabaseReader(rdr);
            // Add this exclusion into the list.
            pTempCtiDevice->addExclusion(paox);
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
    CtiDeviceBase *pTempCtiDevice = 0;

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
                    CtiDeviceION *tmpION = (CtiDeviceION *)pTempCtiDevice;

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
    CtiDeviceBase *pTempCtiDevice;

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
        Map.apply(ApplyClearMacroDeviceList, NULL);

        while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
        {
            rdr["OwnerID"] >> tmpOwnerID;
            rdr["ChildID"] >> tmpChildID;

            pTempCtiDevice = getEqual(tmpOwnerID);
            if(pTempCtiDevice)
            {
                CtiDeviceMacro * pOwner = (CtiDeviceMacro *)pTempCtiDevice;
                if( NULL != (pTempCtiDevice = getEqual(tmpChildID)) )
                {
                    pOwner->addDevice(pTempCtiDevice);
                }
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " There is a DB error or zero entries in GenericMacro Table. DB return code: " << macroResult.status().errorCode() << endl;
    }

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Macro Subdevices" << endl;
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
