/*-----------------------------------------------------------------------------*
*
* File:   mgr_device_scannable
*
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "mgr_device_scannable.h"
#include "dev_single.h"
#include "tbl_dv_wnd.h"

#include "debug_timer.h"

using namespace std;

namespace Cti {

void ScannableDeviceManager::refreshDeviceProperties(id_range_t &paoids, int type)
{
    Inherited::refreshDeviceProperties(paoids, type);

    bool print_bounds = DebugLevel & 0x00020000;

    {  DebugTimer timer("loading scan rates",   print_bounds);  refreshScanRates    (paoids);  }
    {  DebugTimer timer("loading scan windows", print_bounds);  refreshDeviceWindows(paoids);  }
}


void ScannableDeviceManager::refresh(LONG paoID, string category, string devicetype)
{
    if( !paoID )
    {
        map<int, vector<long> > type_paoids;

        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);

            RWDBConnection conn     = getConnection();
            RWDBDatabase   db       = getDatabase();
            RWDBSelector   selector             = db.selector(),
                           scanrate_selector    = db.selector(),
                           loadprofile_selector = db.selector();

            RWDBTable      tbl_paobject         (db.table("yukonpaobject")),
                           tbl_devicescanrate   (db.table("devicescanrate")),
                           tbl_deviceloadprofile(db.table("deviceloadprofile"));

            scanrate_selector << tbl_devicescanrate["deviceid"];

            loadprofile_selector << tbl_deviceloadprofile["deviceid"];

            loadprofile_selector.where(tbl_deviceloadprofile["loadprofilecollection"] != "NNNN");

            selector << tbl_paobject["paobjectid"];
            selector << tbl_paobject["type"];

            selector.where(tbl_paobject["paobjectid"].in(scanrate_selector.union_(loadprofile_selector)));

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);

                dout << selector.asString() << endl;
            }

            RWDBReader  rdr = selector.reader(conn);

            int i = 0;

            while( rdr() )
            {
                long id;
                RWCString temp;

                rdr[0] >> id;
                rdr[1] >> temp;

                type_paoids[resolveDeviceType(temp.data())].push_back(id);

                if( !(++i % 1000) )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);

                    dout << "loaded " << i << " scannables " << endl;
                }
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);

                dout << "loaded " << i << " scannables " << endl;
            }
        }

        map<int, vector<long> >::iterator itr, itr_end = type_paoids.end();

        for( itr = type_paoids.begin(); itr != itr_end; ++itr )
        {
            Inherited::refreshList(id_range_t(itr->second.begin(), itr->second.end()), itr->first);
        }
    }
    else
    {
        Inherited::refresh(paoID, category, devicetype);
    }
}


void ScannableDeviceManager::refreshScanRates(id_range_t &paoids)
{
    coll_type::writer_lock_guard_t guard(getLock());

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn     = getConnection();
    RWDBDatabase   db       = getDatabase();
    RWDBSelector   selector = db.selector();
    RWDBTable      keyTable;

    CtiTableDeviceScanRate::getSQL(db, keyTable, selector);

    addIDClause(selector, keyTable["deviceid"], paoids);

    RWDBReader rdr = selector.reader(conn);

    if( DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
    }

    if( setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok)
    {
        // Mark all Scan Rate elements as needing refresh..
        if( paoids.empty() )
        {
            spiterator itr, itr_end = end();

            for( itr = begin(); itr != itr_end; ++itr )
            {
                //  VC6 - again, this would be perfect for for_each(begin(), end(), mem_fun(invalidateScanRates));
                if(itr->second) itr->second->invalidateScanRates();
            }
        }
        else
        {
            for( id_itr_t paoid_itr = paoids.begin(); paoid_itr != paoids.end(); ++paoid_itr )
            {
                //  is this check necessary?
                if( *paoid_itr > 0 )
                {
                    CtiDeviceSPtr devsptr = getDeviceByID(*paoid_itr);
                    if(devsptr) devsptr->invalidateScanRates();
                }
            }
        }
    }

    while( (setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        LONG device_id = 0;

        rdr["deviceid"] >> device_id;

        CtiDeviceSPtr devsptr = getDeviceByID(device_id);

        if( devsptr && devsptr->isSingle() )
        {
            //  The device just returned from the rdr is already in my list, and is a
            //    scannable device....  We need to update our entry with the new scan rates!
            boost::static_pointer_cast<CtiDeviceSingle>(devsptr)->DecodeScanRateDatabaseReader(rdr);
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " There are scanrates in the scanrate table for a nonscannable device, deviceid: " << device_id << endl;
        }
    }

    if(setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok || setErrorCode(rdr.status().errorCode()) == RWDBStatus::endOfFetch)
    {
        // Remove any scan rates which were NOT refreshed, but only if we read a few correctly!
        spiterator itr, itr_end = end();

        for( itr = begin(); itr != itr_end; ++itr )
        {
            if(itr->second) itr->second->deleteNonUpdatedScanRates();
        }
    }
}

void ScannableDeviceManager::refreshDeviceWindows(id_range_t &paoids)
{
    coll_type::writer_lock_guard_t guard(getLock());

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn     = getConnection();
    RWDBDatabase   db       = getDatabase();
    RWDBSelector   selector = db.selector();
    RWDBTable      keyTable;

    CtiTableDeviceWindow::getSQL(db, keyTable, selector);

    addIDClause(selector, keyTable["deviceid"], paoids);

    if( !paoids.empty() )
    {
        for( id_itr_t paoid_itr = paoids.begin(); paoid_itr != paoids.end(); ++paoid_itr )
        {
            CtiDeviceSPtr devsptr = getDeviceByID(*paoid_itr);

            if(devsptr && devsptr->isSingle())
            {
                //  Remove ALL windows in case any have been deleted from the device.
                boost::static_pointer_cast<CtiDeviceSingle>(devsptr)->removeWindowType();
            }
        }
    }

    if( DebugLevel & 0x00020000 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << selector.asString() << endl;
    }

    RWDBReader rdr = selector.reader(conn);

    while( (setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        long device_id = 0;

        rdr["deviceid"] >> device_id;

        CtiDeviceSPtr devsptr = getDeviceByID(device_id);

        if( devsptr && devsptr->isSingle() )
        {
            //  The device ID just returned from the rdr is already in my list, and is a
            //    scannable device....  We need to update the list entry with the new scan windows!
            boost::static_pointer_cast<CtiDeviceSingle>(devsptr)->DecodeDeviceWindowDatabaseReader(rdr);
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " There are scan windows in the device window table for a nonscannable device. " << devsptr->getName() << endl;
        }
    }
}



}

