#include "precompiled.h"

#include "mgr_device_scannable.h"
#include "dev_single.h"
#include "dev_carrier.h"
#include "tbl_dv_wnd.h"

#include "debug_timer.h"
#include "database_connection.h"
#include "database_reader.h"

using namespace std;

namespace Cti {

void ScannableDeviceManager::refreshDeviceProperties(Database::id_set &paoids, int type)
{
    Inherited::refreshDeviceProperties(paoids, type);

    bool print_bounds = DebugLevel & 0x00020000;

    {  Timing::DebugTimer timer("loading scan rates",   print_bounds);  refreshScanRates    (paoids);  }
    {  Timing::DebugTimer timer("loading scan windows", print_bounds);  refreshDeviceWindows(paoids);  }
}


bool ScannableDeviceManager::shouldDiscardDevice(CtiDeviceSPtr dev) const
{
    //  first, check to see if the parent wants to discard the device (if non-updated on a DB reload, the device was deleted)
    if( Inherited::shouldDiscardDevice(dev) )
    {
        return true;
    }

    //  then look to see if the device has a scan rate or is collecting load profile
    if( dev && dev->isSingle() )
    {
        CtiDeviceSingleSPtr devSingle = boost::static_pointer_cast<CtiDeviceSingle>(dev);

        // Return TRUE if it is NOT SET
        for(INT i = 0; i  < ScanRateInvalid; i++ )
        {
            if( devSingle->getScanRate(i) != -1 )
            {
                return false;              // I found a scan rate...
            }
        }

        if( isCarrierLPDeviceType(devSingle->getType()) )
        {
            Cti::Devices::CarrierDevice *devCarrier = (Cti::Devices::CarrierDevice *)devSingle.get();

            for(int i = 0; i < CtiTableDeviceLoadProfile::MaxCollectedChannel; i++)
            {
                if( devCarrier->getLoadProfile()->isChannelValid(i) )
                {
                    return false;
                }
            }
        }
    }

    //  otherwise discard it
    return true;
}


void ScannableDeviceManager::refreshAllDevices()
{
    map<int, Database::id_set > type_paoids;

    {
        static const string sql =  "SELECT YP.paobjectid, YP.type "
                                   "FROM yukonpaobject YP "
                                   "WHERE YP.paobjectid IN (SELECT DSR.deviceid "
                                                           "FROM devicescanrate DSR "
                                                           "UNION "
                                                           "SELECT DLP.deviceid "
                                                           "FROM deviceloadprofile DLP "
                                                           "WHERE DLP.loadprofilecollection != 'NNNN')";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);

        rdr.execute();

        if( !rdr.isValid() )
        {
            string loggedSQLstring = rdr.asString();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << loggedSQLstring << endl;
            }
        }

        int load_count = 0;

        while( rdr() )
        {
            long id;
            string temp;

            rdr[0] >> id;
            rdr[1] >> temp;

            type_paoids[resolveDeviceType(temp)].insert(id);

            if( !(++load_count % 1000) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);

                dout << "loaded " << load_count << " scannables " << endl;
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);

            dout << "loaded " << load_count << " scannables " << endl;
        }
    }

    map<int, Cti::Database::id_set >::iterator itr, itr_end = type_paoids.end();

    for( itr = type_paoids.begin(); itr != itr_end; ++itr )
    {
        Inherited::refreshList(itr->second, itr->first);
    }
}


void ScannableDeviceManager::refreshScanRates(Database::id_set &paoids)
{
    coll_type::writer_lock_guard_t guard(getLock());

    const string sqlCore  = CtiTableDeviceScanRate::getSQLCoreStatement();
    const string idClause = CtiTableDeviceScanRate::addIDSQLClause(paoids);

    string sql = sqlCore;

    if( !idClause.empty() )
    {
        sql += " ";
        sql += idClause;
    }

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection, sql);

    rdr.execute();

    if( DebugLevel & 0x00020000 || !rdr.isValid() )
    {
        string loggedSQLstring = rdr.asString();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << loggedSQLstring << endl;
        }
    }

    if( setErrorCode(rdr.isValid() ? 0 : 1) == 0 )
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
            for( Cti::Database::id_set_itr paoid_itr = paoids.begin(); paoid_itr != paoids.end(); ++paoid_itr )
            {
                //  is this check necessary?
                if( *paoid_itr > 0 )
                {
                    if(CtiDeviceSPtr devsptr = getDeviceByID(*paoid_itr))
                    {
                        devsptr->invalidateScanRates();
                    }
                }
            }
        }
    }

    while( setErrorCode(rdr.isValid() ? 0 : 1) == 0 && rdr() )
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

    if(setErrorCode(rdr.isValid() ? 0 : 1) == 0)
    {
        // Remove any scan rates which were NOT refreshed, but only if we read a few correctly!
        spiterator itr, itr_end = end();

        for( itr = begin(); itr != itr_end; ++itr )
        {
            if(itr->second) itr->second->deleteNonUpdatedScanRates();
        }
    }
}

void ScannableDeviceManager::refreshDeviceWindows(Database::id_set &paoids)
{
    coll_type::writer_lock_guard_t guard(getLock());

    if( !paoids.empty() )
    {
        for( Cti::Database::id_set_itr paoid_itr = paoids.begin(); paoid_itr != paoids.end(); ++paoid_itr )
        {
            CtiDeviceSPtr devsptr = getDeviceByID(*paoid_itr);

            if(devsptr && devsptr->isSingle())
            {
                //  Remove ALL windows in case any have been deleted from the device.
                boost::static_pointer_cast<CtiDeviceSingle>(devsptr)->removeWindowType();
            }
        }
    }

    static const string sqlCore = CtiTableDeviceWindow::getSQLCoreStatement();
    const string idClause = CtiTableDeviceWindow::addIDSQLClause(paoids);

    string sql = sqlCore;

    if( !idClause.empty() )
    {
        sql += " ";
        sql += idClause;
    }

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection, sql);

    rdr.execute();

    if( DebugLevel & 0x00020000 )
    {
        string loggedSQLstring = rdr.asString();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << loggedSQLstring << endl;
        }
    }

    while( setErrorCode(rdr.isValid() ? 0 : 1) == 0 && rdr() )
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

