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

void ScannableDeviceManager::refreshDeviceProperties(LONG paoID)
{
    Inherited::refreshDeviceProperties(paoID);

    bool print_bounds = DebugLevel & 0x00020000;

    {  DebugTimer timer("loading scan rates",   print_bounds);  refreshScanRates    (paoID);  }
    {  DebugTimer timer("loading scan windows", print_bounds);  refreshDeviceWindows(paoID);  }
}


void ScannableDeviceManager::refresh(LONG paoID, string category, string devicetype)
{
    if( !paoID )
    {
        list<pair<long, int> > paoids;

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

                rdr[0] >> id;

                RWCString temp;

                rdr[1] >> temp;

                paoids.push_back(make_pair(id, resolveDeviceType(temp.data())));

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

        list<pair<long, int> >::iterator itr, itr_end = paoids.end();

        for( itr = paoids.begin(); itr != itr_end; ++itr )
        {
            if( itr->first > 0 )
            {
                Inherited::refresh(itr->first);
            }
        }
    }
    else
    {
        Inherited::refresh(paoID, category, devicetype);
    }
}

void ScannableDeviceManager::refreshScanRates(LONG id)
{
    coll_type::writer_lock_guard_t guard(getLock());

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn     = getConnection();
    RWDBDatabase   db       = getDatabase();
    RWDBSelector   selector = db.selector();
    RWDBTable      keyTable;

    CtiTableDeviceScanRate::getSQL(db, keyTable, selector);

    if( id > 0 )
    {
        selector.where(keyTable["deviceid"] == id && selector.where());
    }

    RWDBReader rdr = selector.reader(conn);

    if( DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
    }

    if( setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok)
    {
        // Mark all Scan Rate elements as needing refresh..

        if( id > 0 )
        {
            CtiDeviceSPtr devsptr = getDeviceByID(id);
            if(devsptr) devsptr->invalidateScanRates();
        }
        else
        {
            spiterator itr, itr_end = end();

            for( itr = begin(); itr != itr_end; ++itr )
            {
                if(itr->second) itr->second->invalidateScanRates();
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

void ScannableDeviceManager::refreshDeviceWindows(LONG id)
{
    coll_type::writer_lock_guard_t guard(getLock());

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn     = getConnection();
    RWDBDatabase   db       = getDatabase();
    RWDBSelector   selector = db.selector();
    RWDBTable      keyTable;

    CtiTableDeviceWindow::getSQL(db, keyTable, selector);

    if( id > 0 )
    {
        selector.where(keyTable["deviceid"] == id && selector.where());

        CtiDeviceSPtr devsptr = getDeviceByID(id);

        if(devsptr && devsptr->isSingle())
        {
            //  Remove ALL windows in case any have been deleted from the device.
            boost::static_pointer_cast<CtiDeviceSingle>(devsptr)->removeWindowType();
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

