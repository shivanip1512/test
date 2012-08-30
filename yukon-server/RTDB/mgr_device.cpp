#include "precompiled.h"


#include "mgr_device.h"
#include "debug_timer.h"
#include "cparms.h"
#include "dbaccess.h"
#include "database_reader.h"
#include "database_connection.h"
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
#include "dev_paging.h"
#include "dev_pagingreceiver.h"
#include "dev_grp_emetcon.h"
#include "dev_grp_expresscom.h"
#include "dev_grp_rfn_expresscom.h"
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
#include "dev_rds.h"
#include "tbl_static_paoinfo.h"

#include "devicetypes.h"
#include "resolvers.h"
#include "utility.h"

using namespace Cti;  //  in preparation for moving devices to their own namespace
using namespace std;
using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseReader;

// prevent any devB (proximity excluded device) which may be seleced to execute from interfereing with devA's next evaluation time.
static void applyExecutionGrantExpiresIsEvaluateNext(const long key, CtiDeviceSPtr devB, void* devSelect)
{
    CtiDeviceBase *devA = (CtiDeviceBase *)devSelect;

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
    CtiDeviceBase *devA = (CtiDeviceBase *)devSelect;

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

//  once VC6 is gone, this is a prime candidate for for_each(begin(), end(), mem_fun(clearExclusions));
static void applyClearExclusions(const long unusedkey, CtiDeviceSPtr Device, void* lptrid)
{
    Device->clearExclusions();
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

inline bool isDeviceNotUpdated(CtiDeviceSPtr &pDevice, void* d)
{
    // Return TRUE if it is NOT SET
    return !pDevice->getUpdatedFlag();
}

static void applyDeviceClearParameters(const long unusedkey, CtiDeviceSPtr Device, void* d)
{
    Device->clearParameters();
    return;
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

bool CtiDeviceManager::refreshDevices(Cti::RowReader &rdr)
{
    bool rowSelected = false;

    coll_type::writer_lock_guard_t guard(getLock());

    while( rdr() )
    {
        rowSelected = true;

        long paoid = 0;
        rdr["deviceid"] >> paoid;            // get the DeviceID

        if( const CtiDeviceSPtr existing_device = getDeviceByID(paoid) )
        {
            removeAssociations(*existing_device);

            //  The device in this row is already in the list.  We need to
            //    update the list entry to the new settings

            // Fills himself in from the reader
            existing_device->DecodeDatabaseReader(rdr);

            // Mark it updated...  should DecodeDatabaseReader() do this?
            existing_device->setUpdatedFlag();

            addAssociations(*existing_device);
        }
        else
        {
            // Use the reader to get me an object of the proper type
            CtiDeviceBase *new_device = DeviceFactory(rdr);

            if( new_device )
            {
                // Fills himself in from the reader
                new_device->DecodeDatabaseReader(rdr);

                // Mark it updated...  should DecodeDatabaseReader() do this?
                new_device->setUpdatedFlag();

                // Stuff it in the list
                _smartMap.insert( new_device->getID(), new_device );

                addAssociations(*new_device);
            }
        }
    }

    return rowSelected;
}


void CtiDeviceManager::addAssociations(const CtiDeviceBase &dev)
{
    const long pao_id  = dev.getID();
    const long port_id = dev.getPortID();
    const long type    = dev.getType();

    if( port_id >= 0 )
    {
        _portDevices[port_id].insert(pao_id);
    }

    if( type > 0 )
    {
        _typeDevices[type].insert(pao_id);
    }
}


CtiDeviceManager::ptr_type CtiDeviceManager::RemoteGetPortRemoteEqual (LONG Port, LONG Remote)
{
    coll_type::reader_lock_guard_t guard(getLock());

    if(_smartMap.empty())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " There are no entries in the remote device list" << endl;
        }
    }

    ptr_type p;

    vector<ptr_type> port_devices;

    getDevicesByPortID(Port, port_devices);

    vector<ptr_type>::iterator itr     = port_devices.begin(),
                               itr_end = port_devices.end();

    while( itr != itr_end )
    {
        p = *itr++;

        if( p->getAddress() >= 0 && p->getAddress() == Remote )
        {
            break;
        }

        p.reset();
    }

    return p;
}

CtiDeviceManager::ptr_type CtiDeviceManager::RemoteGetPortRemoteTypeEqual (LONG Port, LONG Remote, INT Type)
{
    coll_type::reader_lock_guard_t guard(getLock());

    if(_smartMap.empty())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " There are no entries in the remote device list" << endl;
        }
    }

    ptr_type p;

    vector<ptr_type> port_devices;

    getDevicesByPortID(Port, port_devices);

    vector<ptr_type>::iterator itr     = port_devices.begin(),
                               itr_end = port_devices.end();

    while( itr != itr_end )
    {
        p = *itr++;

        if( p->getType() == Type && p->getAddress() >= 0 && p->getAddress() == Remote )
        {
            break;
        }

        p.reset();
    }

    return p;
}

CtiDeviceManager::ptr_type CtiDeviceManager::RemoteGetPortMasterSlaveTypeEqual (LONG Port, LONG Master, LONG Slave, INT Type)
{
    coll_type::reader_lock_guard_t guard(getLock());

    if(_smartMap.empty())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " There are no entries in the remote device list" << endl;
        }
    }

    ptr_type p;

    vector<ptr_type> port_devices;

    getDevicesByPortID(Port, port_devices);

    vector<ptr_type>::iterator itr     = port_devices.begin(),
                               itr_end = port_devices.end();

    while( itr != itr_end )
    {
        p = *itr++;

        if( p->getType()          == Type  &&
            p->getAddress()       >= 0     &&
            p->getAddress()       == Slave &&
            p->getMasterAddress() >= 0     &&
            p->getMasterAddress() == Master )
        {
            break;
        }

        p.reset();
    }

    return p;
}

CtiDeviceManager::ptr_type CtiDeviceManager::getDeviceByID (LONG Dev)
{
    ptr_type p;
    try
    {
        p = _smartMap.find(Dev);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return p;
}


void CtiDeviceManager::getDevicesByPortID(long portid, vector<ptr_type> &devices)
{
    coll_type::reader_lock_guard_t guard(getLock());

    port_devices_t::iterator pd_itr = _portDevices.find(portid);

    if( pd_itr != _portDevices.end() )
    {
        Cti::Database::id_set &port_deviceids = pd_itr->second;

        Cti::Database::id_set_itr id_itr = port_deviceids.begin(),
                                  id_end = port_deviceids.end();

        ptr_type dev;

        for( ; id_itr != id_end; ++id_itr )
        {
            if( dev = _smartMap.find(*id_itr) )
            {
                devices.push_back(dev);
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " - CtiDeviceManager::getDevicesByPortID() - deviceid " << *id_itr << " not in map, port id = " << portid << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                port_deviceids.erase(*id_itr);
            }
        }
    }
}



//  Appends all devices of the specified type to the "devices" collection.
void CtiDeviceManager::getDevicesByType(int type, vector<ptr_type> &devices)
{
    coll_type::reader_lock_guard_t guard(getLock());

    type_devices_t::iterator pd_itr = _typeDevices.find(type);

    if( pd_itr != _typeDevices.end() )
    {
        Cti::Database::id_set &type_deviceids = pd_itr->second;

        Cti::Database::id_set_itr id_itr = type_deviceids.begin(),
                            id_end = type_deviceids.end();

        ptr_type dev;

        for( ; id_itr != id_end; ++id_itr )
        {
            if( dev = _smartMap.find(*id_itr) )
            {
                devices.push_back(dev);
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " - CtiDeviceManager::getDevicesByType() - deviceid " << *id_itr << " not in map, type = " << type << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                type_deviceids.erase(*id_itr);
            }
        }
    }
}


bool CtiDeviceManager::containsType(int type)
{
    coll_type::reader_lock_guard_t guard(getLock());

    type_devices_t::iterator pd_itr = _typeDevices.find(type);

    return pd_itr != _typeDevices.end() && !pd_itr->second.empty();
}


struct device_name_equal
{
    string cmpname;

    device_name_equal(const string &s)
    {
        cmpname = s;
        transform(cmpname.begin(), cmpname.end(), cmpname.begin(), ::tolower);
    };

    bool operator()(pair<const long, CtiDeviceManager::ptr_type> &record)
    {
        string devname;

        if( record.second )
        {
            devname = record.second->getName();
            std::transform(devname.begin(), devname.end(), devname.begin(), ::tolower);
        }

        return devname == cmpname;
    }
};


CtiDeviceManager::ptr_type CtiDeviceManager::RemoteGetEqualbyName (const string &RemoteName)
{
    ptr_type p;

    if(_smartMap.empty())
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " There are no entries in the device manager list" << endl;
    }

    spiterator itr = find_if(begin(), end(), device_name_equal(RemoteName));

    if( itr != end() )
    {
        p = itr->second;
    }

    return p;
}

CtiDeviceManager::CtiDeviceManager(CtiApplication_t app_id) :
_app_id(app_id),
_dberrorcode(0)
{
}

CtiDeviceManager::~CtiDeviceManager()
{
}

void CtiDeviceManager::deleteList(void)
{
    _smartMap.removeAll(NULL, 0);
}

void CtiDeviceManager::refreshDeviceByID(LONG paoID, string category, string devicetype)
{
    if( ! paoID )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** - PaoId = 0 in CtiDeviceManager::refreshDeviceByID(), calling refreshAllDevices() " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        return refreshAllDevices();
    }

    int type = resolvePAOType(category, devicetype);

    if( CtiDeviceSPtr pDev = getDeviceByID(paoID) )
    {
        if( pDev->getType() != type)
        {
            coll_type::writer_lock_guard_t guard(getLock());

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << pDev->getName() << " has changed type to " << devicetype << " from " << desolveDeviceType(pDev->getType()) << endl;
            }

            if( CtiDeviceSPtr orphanedDevice = _smartMap.remove(paoID) )
            {
                if( DebugLevel & 0x00020000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Old device object has been orphaned " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                removeAssociations(*orphanedDevice);
            }
        }
    }

    // We were given an id.  We can use that to reduce our workload.
    Cti::Database::id_set paoids;

    paoids.insert(paoID);

    refreshList(paoids, type);
}

//  Overridden by Cti::ScannableDeviceManager
void CtiDeviceManager::refreshAllDevices()
{
    refreshList(Database::id_set(), 0);
}

string CtiDeviceManager::createTypeSqlClause(string type, const bool include_type)
{
    string sqlType;

    if( !type.empty() )
    {
        transform(type.begin(), type.end(), type.begin(), toupper);

        if( include_type )
        {
            sqlType = "AND upper (YP.type) = '" + type + "'";
        }
        else
        {
            sqlType = "AND upper (YP.type) != '" + type + "'";
        }
    }

    return sqlType;
}

string CtiDeviceManager::createIdSqlClause(const Cti::Database::id_set &paoids, const string table, const string attrib)
{
    string sqlIDs;

    if( !paoids.empty() )
    {
        ostringstream in_list;

        if( paoids.size() == 1 )
        {
            //  special single id case

            in_list << *(paoids.begin());

            sqlIDs += table + "." + attrib + " = " + in_list.str();

            return sqlIDs;
        }
        else
        {
            in_list << "(";

            copy(paoids.begin(), paoids.end(), csv_output_iterator<long, ostringstream>(in_list));

            in_list << ")";

            sqlIDs += table + "." + attrib + " IN " + in_list.str();

            return sqlIDs;
        }
    }

    return string();
}

bool CtiDeviceManager::loadDeviceType(Cti::Database::id_set &paoids, const string &device_name, const CtiDeviceBase &device, string type, const bool include_type)
{
    bool print_bounds = DebugLevel & 0x00020000;
    bool retVal = false;

    Timing::DebugTimer timer("looking for " + device_name, print_bounds);

    string       sql        = device.getSQLCoreStatement();
    const string typeClause = createTypeSqlClause(type, include_type);
    const string idClause   = createIdSqlClause(paoids);

    if( !typeClause.empty() )
    {
        sql += " ";
        sql += typeClause;
    }
    if( !idClause.empty() )
    {
        sql += " AND ";
        sql += idClause;
    }

    Cti::Database::DatabaseConnection connection;
    DatabaseReader rdr(connection, sql);

    rdr.execute();

    retVal = refreshDevices(rdr);

    if( DebugLevel & 0x00020000 || !rdr.isValid() )
    {
        string loggedSQLstring = rdr.asString(); //selector.asString();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << loggedSQLstring << endl;
        }
    }

    return retVal;
}


void CtiDeviceManager::refreshList(const Cti::Database::id_set &paoids, const LONG deviceType)
{
    using Devices::MctDevice;

    bool rowFound = false;

    try
    {
        if( paoids.empty() )
        {
            //  Reset everyone's Updated flag if not a directed load - this allows us to purge nonexistent entries later
            apply(applyDeviceResetUpdated, NULL);
        }
        else
        {
            coll_type::writer_lock_guard_t guard(getLock());

            Cti::Database::id_set_itr paoid_itr = paoids.begin();

            while( paoid_itr != paoids.end() )
            {
                CtiDeviceSPtr dev = getDeviceByID(*paoid_itr++);

                if( dev )  dev->resetUpdatedFlag();
            }
        }

        resetErrorCode();

        CtiDeviceBase *deviceTemplate = 0;

        if( deviceType )
        {
            deviceTemplate = createDeviceType(deviceType);
        }

        int max_ids_per_select = gConfigParms.getValueAsInt("MAX_IDS_PER_DEVICE_SELECT", 256);

        {
            Cti::Database::id_set_itr paoid_itr = paoids.begin();

            //  I don't really like the do-while construct, but this loop must happen at least once even if no paoids were passed in
            do
            {
                int subset_size = min(distance(paoid_itr, paoids.end()), max_ids_per_select);

                //  note the iterator difference/addition - requires a random-access iterator...
                //    trait tags could be useful here except that VC6 doesn't support template member functions
                Cti::Database::id_set_itr subset_end = paoid_itr;
                advance(subset_end, subset_size);
                Cti::Database::id_set paoid_subset(paoid_itr, subset_end);

                if( deviceTemplate )
                {
                    //  type-specific directed load
                    rowFound |= loadDeviceType(paoid_subset, "directed load", *deviceTemplate);
                }
                else
                {
                    rowFound |= loadDeviceType(paoid_subset, "DLC devices", Devices::CarrierDevice());

                    if( !MctDevice::isMct410(deviceType) )
                    {
                        rowFound |= loadDeviceType(paoid_subset, "Grid Advisor devices",   CtiDeviceGridAdvisor());

                        rowFound |= loadDeviceType(paoid_subset, "Sixnet IEDs",            CtiDeviceIED(),         "SIXNET");
                        rowFound |= loadDeviceType(paoid_subset, "Meters and IEDs",        CtiDeviceMeter());

                        //  prevent the LMI from being loaded twice
                        rowFound |= loadDeviceType(paoid_subset, "DNP/ION devices",        Devices::DnpDevice(),          "RTU-LMI", false);
                        rowFound |= loadDeviceType(paoid_subset, "LMI RTUs",               CtiDeviceLMI());
                        rowFound |= loadDeviceType(paoid_subset, "RTM devices",            CtiDeviceIED(),         "RTM");

                        rowFound |= loadDeviceType(paoid_subset, "TAP devices",            Devices::DevicePaging());
                        rowFound |= loadDeviceType(paoid_subset, "TNPP devices",           CtiDeviceTnppPagingTerminal());
                        rowFound |= loadDeviceType(paoid_subset, "RDS devices",            Devices::RDSTransmitter(),     "RDS TERMINAL");

                        //  exclude the CCU 721
                        rowFound |= loadDeviceType(paoid_subset, "IDLC target devices",    CtiDeviceIDLC(),        "CCU-721", false);
                        rowFound |= loadDeviceType(paoid_subset, "CCU-721 devices",        Devices::Ccu721Device());

                        rowFound |= loadDeviceType(paoid_subset, "MCT broadcast devices",  Devices::MctBroadcastDevice());

                        rowFound |= loadDeviceType(paoid_subset, "Repeater 800 devices",   Devices::DlcBaseDevice(),     "REPEATER 800");
                        rowFound |= loadDeviceType(paoid_subset, "Repeater 801 devices",   Devices::DlcBaseDevice(),     "REPEATER 801");
                        rowFound |= loadDeviceType(paoid_subset, "Repeater 850 devices",   Devices::DlcBaseDevice(),     "REPEATER 850");
                        rowFound |= loadDeviceType(paoid_subset, "Repeater 900 devices",   Devices::DlcBaseDevice(),     "REPEATER");
                        rowFound |= loadDeviceType(paoid_subset, "Repeater 902 devices",   Devices::DlcBaseDevice(),     "REPEATER 902");
                        rowFound |= loadDeviceType(paoid_subset, "Repeater 921 devices",   Devices::DlcBaseDevice(),     "REPEATER 921");

                        rowFound |= loadDeviceType(paoid_subset, "CBC devices",            CtiDeviceCBC());
                        rowFound |= loadDeviceType(paoid_subset, "FMU devices",            CtiDeviceIED(),         "FMU");
                        rowFound |= loadDeviceType(paoid_subset, "RTC devices",            CtiDeviceRTC());

                        rowFound |= loadDeviceType(paoid_subset, "Emetcon groups",         CtiDeviceGroupEmetcon());
                        rowFound |= loadDeviceType(paoid_subset, "Versacom groups",        CtiDeviceGroupVersacom());
                        rowFound |= loadDeviceType(paoid_subset, "Expresscom groups",      CtiDeviceGroupExpresscom());
                        rowFound |= loadDeviceType(paoid_subset, "RFN Expresscom groups",  CtiDeviceGroupRfnExpresscom());
                        rowFound |= loadDeviceType(paoid_subset, "Ripple groups",          CtiDeviceGroupRipple());

                        rowFound |= loadDeviceType(paoid_subset, "MCT load groups",        CtiDeviceGroupMCT());

                        rowFound |= loadDeviceType(paoid_subset, "105 groups",             CtiDeviceGroupSA105());
                        rowFound |= loadDeviceType(paoid_subset, "205 groups",             CtiDeviceGroupSA205());
                        rowFound |= loadDeviceType(paoid_subset, "305 groups",             CtiDeviceGroupSA305());
                        rowFound |= loadDeviceType(paoid_subset, "SA Digital groups",      CtiDeviceGroupSADigital());
                        rowFound |= loadDeviceType(paoid_subset, "Golay groups",           CtiDeviceGroupGolay());

                        rowFound |= loadDeviceType(paoid_subset, "Macro devices",          CtiDeviceMacro());

                        //  should not be done in Scanner
                        rowFound |= refreshPointGroups(paoid_subset);

                        rowFound |= loadDeviceType(paoid_subset, "System devices",         CtiDeviceBase(),        "SYSTEM");
                    }
                }

                // Now load the device properties onto the devices
                refreshDeviceProperties(paoid_subset, deviceType);

                advance(paoid_itr, subset_size);

            } while( paoid_itr != paoids.end() );
        }

        if( deviceTemplate )
        {
            delete deviceTemplate;
        }

        {
            Timing::DebugTimer timer("removing invalidated devices ");

            std::vector<CtiDeviceSPtr> evictedDevices;

            //  If this was a "reload all"...
            if( paoids.empty() )
            {
                //  ...make sure we loaded something before we evict any records
                if( rowFound )
                {
                    evictedDevices = _smartMap.findAll(boost::bind(&CtiDeviceManager::shouldDiscardDevice, this, _1));
                }
            }
            else
            {
                for each( const long paoid in paoids )
                {
                    if( CtiDeviceSPtr dev = getDeviceByID(paoid) )
                    {
                        if( shouldDiscardDevice(dev) )
                        {
                            evictedDevices.push_back(dev);
                        }
                    }
                }
            }

            if( ! evictedDevices.empty() )
            {
                //  We need to grab the writer lock since we're modifying the associations.
                coll_type::writer_lock_guard_t guard(getLock());

                for each( CtiDeviceSPtr evictedDevice in evictedDevices )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  Evicting \"" << evictedDevice->getName() << "\" from list" << endl;
                    }

                    removeAssociations(*evictedDevice);

                    _smartMap.remove(evictedDevice->getID());
                }
            }
        }
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


bool CtiDeviceManager::shouldDiscardDevice(CtiDeviceSPtr dev) const
{
    //  Discard the device if it has not been updated
    return ! (dev && dev->getUpdatedFlag());
}


void CtiDeviceManager::removeAssociations(const CtiDeviceBase &evictedDevice)
{
    _portDevices[evictedDevice.getPortID()].erase(evictedDevice.getID());
    _typeDevices[evictedDevice.getType()  ].erase(evictedDevice.getID());
}


/*
 * ptr_type anxiousDevice has asked to execute.  We make certain that no other device which is in his exclusion list is executing.
 */
bool CtiDeviceManager::mayDeviceExecuteExclusionFree(CtiDeviceSPtr anxiousDevice, const int requestPriority, CtiTablePaoExclusion &deviceexclusion)
{
    bool mayExecute = false;

    try
    {
        if(anxiousDevice)
        {
            coll_type::reader_lock_guard_t guard(getLock());

            CtiTime now;

            // Make sure no other device out there has begun executing and doesn't want us to until they are done.
            if( !anxiousDevice->isExecutionProhibited() )
            {
                if(anxiousDevice->hasExclusions())
                {
                    /*
                     *  Walk the anxiousDevice's exclusion list checking if any of the devices on it indicate that they are
                     *  currently executing.  If any of them are executing, the anxious device cannot start.
                     */
                    bool blocked = false;
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

                            mayExecute = true;      // Provided no proximity exclusion is executing, we can go!
                        }
                        else
                        {
                            if(0 && getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Device " << anxiousDevice->getName() << " is outside its execution window and will execute at " << open << " - " << close << endl;
                            }

                            deviceexclusion = anxiousDevice->getExclusion().getCycleTimeExclusion();                             // Pass this out to the callee as the device which blocked us first!
                            blocked = true;          // Window is closed!
                            mayExecute = false;     // Cannot execute.
                        }
                    }

                    CtiDeviceBase::exclusions exvector = anxiousDevice->getExclusions();
                    CtiDeviceBase::exclusions::iterator itr;

                    for(itr = exvector.begin(); !blocked && itr != exvector.end(); itr++)
                    {
                        CtiTablePaoExclusion &paox = *itr;

                        switch(paox.getFunctionId())
                        {
                        case (CtiTablePaoExclusion::ExFunctionIdExclusion):
                            {
                                device = getDeviceByID(paox.getExcludedPaoId());  // grab the excludable device

                                int deviceMaxWaitingPriority = 0;  //  initialized in the else-if below

                                if(device)
                                {
                                    //  is the excludable executing?  This would block anxiousDevice.
                                    if(device->isExecuting())
                                    {
                                        if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << " Device: " << anxiousDevice->getName() << " Port: " << anxiousDevice->getPortID() << " cannot execute because device " << device->getName() << " port: " << device->getPortID() << " is executing.  TID: " << GetCurrentThreadId() << endl;
                                        }
                                        deviceexclusion = paox;     // Pass this out to the callee as the device which blocked us first!
                                        anxiousDeviceBlocksThisVector.clear();  // Cannot use the list to block other devices.
                                        blocked = true;             // 20060228 CGP.  // Prevent additional loops.
                                        break;                      // we cannot go
                                    }
                                    //  Does the excludable have a higher priority request waiting that should block anxiousDevice?
                                    else if(requestPriority < (deviceMaxWaitingPriority = getPortDevicePriority(device->getPortID(), device->getID())))
                                    {
                                        if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << " Device: " << anxiousDevice->getName()
                                                              << " Port: " << anxiousDevice->getPortID()
                                                              << " Priority: " << requestPriority
                                                              << " cannot execute because "
                                                              << " device " << device->getName()
                                                              << " port: " << device->getPortID()
                                                              << " priority: " <<  deviceMaxWaitingPriority
                                                              << " is executing.  TID: " << GetCurrentThreadId() << endl;
                                        }
                                        deviceexclusion = paox;     // Pass this out to the callee as the device which blocked us first!
                                        anxiousDeviceBlocksThisVector.clear();  // Cannot use the list to block other devices.
                                        blocked = true;
                                        break;
                                    }
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
                    if(!blocked && itr == exvector.end())
                    {
                        mayExecute = true;                     // we may execute in this case.
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
                        mayExecute = true;
                    }
                }
                else
                {
                    mayExecute = true;
                }
            }

            if(mayExecute)
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

    return mayExecute;
}


CtiDeviceManager &CtiDeviceManager::setDevicePrioritiesForPort(long portid, const device_priorities_t &device_priorities)
{
    Cti::readers_writer_lock_t::writer_lock_guard_t guard(_portDevicePrioritiesLock);

    _portDevicePriorities[portid] = device_priorities;

    return *this;
}


int CtiDeviceManager::getPortDevicePriority(long portid, long deviceid) const
{
    Cti::readers_writer_lock_t::reader_lock_guard_t guard(_portDevicePrioritiesLock);

    port_device_priorities_t::const_iterator pdp_itr = _portDevicePriorities.find(portid);

    if( pdp_itr == _portDevicePriorities.end() )
    {
        return 0;
    }

    const device_priorities_t &dp = pdp_itr->second;

    device_priorities_t::const_iterator dp_itr = dp.find(deviceid);

    if( dp_itr == dp.end() )
    {
        return 0;
    }

    return dp_itr->second;
}



/*
 * ptr_type anxiousDevice has completed an execution.  We must cleanup his mess.
 */
void CtiDeviceManager::removeInfiniteExclusion(CtiDeviceSPtr anxiousDevice)
{
    try
    {
        if(anxiousDevice)
        {
            coll_type::reader_lock_guard_t guard(getLock());

            long anxiousDeviceId = anxiousDevice->getID();         // This is the id which is to be pulled from the prohibition list.

            for each( const coll_type::val_type &record in _exclusionMap.getMap() )
            {
                const CtiDeviceSPtr &device = record.second;

                if( device->removeInfiniteProhibit(anxiousDeviceId) )
                {
                    if( getDebugLevel() & DEBUGLEVEL_EXCLUSIONS )
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Device " << device->getName() << " no longer prohibited because of " << anxiousDevice->getName() << "." << endl;
                        }
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(slog);
                            slog << CtiTime() << " Device " << device->getName() << " no longer prohibited because of " << anxiousDevice->getName() << "." << endl;
                        }
                    }
                }
            }

            anxiousDevice->setExecuting(false);                                 // Mark ourselves as _not_ executing!
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


bool CtiDeviceManager::isMct(int type)
{
    switch(type)
    {
        case TYPELMT2:
        case TYPEDCT501:
        case TYPEMCT210:
        case TYPEMCT212:
        case TYPEMCT213:
        case TYPEMCT224:
        case TYPEMCT226:
        case TYPEMCT240:
        case TYPEMCT242:
        case TYPEMCT248:
        case TYPEMCT250:
        case TYPEMCT260:
        case TYPEMCT310:
        case TYPEMCT310ID:
        case TYPEMCT318:
        case TYPEMCT310IL:
        case TYPEMCT318L:
        case TYPEMCT310IDL:
        case TYPEMCT360:
        case TYPEMCT370:
        case TYPEMCT410CL:
        case TYPEMCT410FL:
        case TYPEMCT410GL:
        case TYPEMCT410IL:
        case TYPEMCT420CL:
        case TYPEMCT420CD:
        case TYPEMCT420FL:
        case TYPEMCT420FD:
        case TYPEMCT430A:
        case TYPEMCT430A3:
        case TYPEMCT430S4:
        case TYPEMCT430SL:
        case TYPEMCT470:
        {
            return true;
        }
    }

    return false;
}

bool CtiDeviceManager::isIon(int type)
{
    switch(type)
    {
        case TYPE_ION7330:
        case TYPE_ION7700:
        case TYPE_ION8300:
        {
            return true;
        }
    }

    return false;
}


void CtiDeviceManager::refreshExclusions(Cti::Database::id_set &paoids)
{
    coll_type::writer_lock_guard_t guard(getLock());

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Looking for Device Exclusions" << endl;
    }

    static const string sqlCore = CtiTablePaoExclusion::getSQLCoreStatement();
    const string idClause = createIdSqlClause(paoids, "PEX", "paoid");

    string sql = sqlCore;

    if(!idClause.empty())
    {
        sql += " AND ";
        sql += idClause;
    }

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection, sql);

    rdr.execute();

    if(DebugLevel & 0x00020000)
    {
        string loggedSQLstring = rdr.asString();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << loggedSQLstring << endl;
        }
    }

    if(rdr.isValid())
    {
        // prep the exclusion lists
        if( paoids.empty() )
        {
            apply(applyClearExclusions);
            _exclusionMap.removeAll(removeExclusionDevice, (void*)0);
        }
        else
        {
            for( Cti::Database::id_set_itr paoid_itr = paoids.begin(); paoid_itr != paoids.end(); ++paoid_itr )
            {
                CtiDeviceSPtr dev = getDeviceByID(*paoid_itr);

                if( dev )
                {
                    dev->clearExclusions();
                }

                _exclusionMap.removeAll(removeExclusionDevice, (void*)*paoid_itr);
            }
        }
    }

    while( rdr() )
    {
        LONG lTemp;
        CtiDeviceSPtr pTempCtiDevice;

        rdr["paoid"] >> lTemp;            // get the DeviceID

        if( _smartMap.entries() > 0 && (pTempCtiDevice = getDeviceByID(lTemp)) )
        {
            CtiTablePaoExclusion paox;
            paox.DecodeDatabaseReader(rdr);
            // Add this exclusion into the list.
            pTempCtiDevice->addExclusion(paox);
            _exclusionMap.insert(pTempCtiDevice->getID(), pTempCtiDevice);      // May try to do multiple inserts, but is should not mater since the pointer gets in there at least one time.
        }
    }

    //  this will need to be reworked if we ever do exclusion on a large system
    for( spiterator portx_itr = _portExclusions.getMap().begin(); portx_itr != _portExclusions.getMap().end(); portx_itr++ )
    {
        if( _smartMap.find(portx_itr->second->getID()) )
        {
            _exclusionMap.insert(portx_itr->second->getID(), portx_itr->second);
        }
        else
        {
            //  That device doesn't exist any more - delete it
            _portExclusions.getMap().erase(portx_itr);
        }
    }

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Done looking for Device Exclusions" << endl;
    }
}

void CtiDeviceManager::refreshIONMeterGroups(Cti::Database::id_set &paoids)
{
    long tmpDeviceID;
    string tmpCollectionGroup,
    tmpTestCollectionGroup,
    tmpMeterNumber,
    tmpBillingGroup;

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for ION Meter Groups" << endl;
    }

    static const string sqlCore = "SELECT DMG.DeviceID, DMG.MeterNumber "
                                  "FROM devicemetergroup DMG, yukonpaobject YP "
                                  "WHERE DMG.DeviceID = YP.PAObjectID AND YP.Type LIKE 'ION%'";
    const string idClause = createIdSqlClause(paoids);

    string sql = sqlCore;

    if(!idClause.empty())
    {
        sql += " AND ";
        sql += idClause;
    }

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection, sql);

    rdr.execute();

    if(DebugLevel & 0x00020000 || !rdr.isValid())
    {
        string loggedSQLstring = rdr.asString();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << loggedSQLstring << endl;
        }
    }

    if(rdr.isValid())
    {
        while( rdr() )
        {
            rdr["DeviceID"]            >> tmpDeviceID;
            rdr["MeterNumber"]         >> tmpMeterNumber;

            CtiDeviceSPtr pTempCtiDevice = getDeviceByID(tmpDeviceID);

            if(pTempCtiDevice && isIon(pTempCtiDevice->getType()))
            {
                boost::static_pointer_cast<CtiDeviceION>(pTempCtiDevice)->setMeterGroupData(tmpMeterNumber);
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Error reading ION Meter Groups from database" << endl;
    }

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for ION Meter Groups" << endl;
    }
}

void CtiDeviceManager::refreshMacroSubdevices(Cti::Database::id_set &paoids)
{
    int childcount = 0;
    CtiDeviceSPtr pTempCtiDevice;

    long tmpOwnerID, tmpChildID;

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Macro Subdevices" << endl;
    }
    {
        const string childCountQuery = "SELECT COUNT ('ChildID') as childcount "
                                       "FROM GenericMacro GM "
                                       "WHERE GM.MacroType = 'GROUP'";
        const string idClause = createIdSqlClause(paoids, "GM", "OwnerID");

        string sql = childCountQuery;

        if(!idClause.empty())
        {
            sql += " AND ";
            sql += idClause;
        }

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);

        rdr.execute();

        if( rdr() )
        {
            rdr["childcount"] >> childcount;
        }
    }

    const string sqlCore =  "SELECT GM.ChildID, GM.OwnerID "
                            "FROM GenericMacro GM "
                            "WHERE GM.MacroType = 'GROUP'";
    const string idClause = createIdSqlClause(paoids, "GM", "OwnerID");
    const string orderBy  = "ORDER BY GM.ChildOrder ASC";

    string sql = sqlCore;

    if(!idClause.empty())
    {
        sql += " AND ";
        sql += idClause;
        sql += " ";
        sql += orderBy;
    }
    else
    {
        sql += " ";
        sql += orderBy;
    }

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection, sql);

    rdr.execute();

    if(DebugLevel & 0x00020000 || !rdr.isValid())
    {
        string loggedSQLstring = rdr.asString();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << loggedSQLstring << endl;
        }
    }

    if(childcount != 0 && rdr.isValid())
    {

        // prep the exclusion lists
        if( paoids.empty() )
        {
            apply(applyClearMacroDeviceList);
        }
        else
        {
            for( Cti::Database::id_set_itr paoid_itr = paoids.begin(); paoid_itr != paoids.end(); ++paoid_itr )
            {
                CtiDeviceSPtr dev = getDeviceByID(*paoid_itr);

                if( dev )  applyClearMacroDeviceList(0, dev, (void *)dev->getID());
            }
        }


        while( rdr() )
        {
            rdr["OwnerID"] >> tmpOwnerID;
            rdr["ChildID"] >> tmpChildID;

            pTempCtiDevice = getDeviceByID(tmpOwnerID);
            if(pTempCtiDevice)
            {
                CtiDeviceMacro * pOwner = (CtiDeviceMacro *)(pTempCtiDevice.get());
                if( (pTempCtiDevice = getDeviceByID(tmpChildID)) )
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


void CtiDeviceManager::refreshMCTConfigs(Cti::Database::id_set &paoids)
{
    CtiDeviceSPtr pTempCtiDevice;

    LONG      tmpmctid;
    int       tmpwire[3],
              tmpconfigtype;
    double    tmpmpkh[3];
    string tmpconfigname,
              tmpconfigmode;

    {
        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for MCT Configs" << endl;
        }

        const string sqlCore = "SELECT MCM.mctid, CFG.configname, CFG.configtype, CFG.configmode, CFG.mctwire1, "
                                 "CFG.mctwire2, CFG.mctwire3, CFG.ke1, CFG.ke2, CFG.ke3 "
                               "FROM mctconfigmapping MCM, mctconfig CFG, yukonpaobject YP "
                               "WHERE MCM.mctid = YP.paobjectid AND MCM.configid = CFG.configid";
        const string idClause = createIdSqlClause(paoids);

        string sql = sqlCore;

        if(!idClause.empty())
        {
            sql += " AND ";
            sql += idClause;
        }

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);

        rdr.execute();

        if(DebugLevel & 0x00020000 || !rdr.isValid())
        {
            string loggedSQLstring = rdr.asString();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << loggedSQLstring << endl;
            }
        }

        if(rdr.isValid())
        {
            while( rdr() )
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

                pTempCtiDevice = getDeviceByID(tmpmctid);

                if(pTempCtiDevice)
                {
                    boost::static_pointer_cast<Devices::MctDevice>(pTempCtiDevice)->setConfigData(tmpconfigname, tmpconfigtype, tmpconfigmode, tmpwire, tmpmpkh);
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Error reading MCT Configs from database" << endl;
        }

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for MCT Configs" << endl;
        }
    }
}


void CtiDeviceManager::refreshMCT400Configs(Cti::Database::id_set &paoids)
{
    LONG tmpmctid, tmpdisconnectaddress;
    bool row_found = false;

    {
        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for MCT Configs" << endl;
        }

        static const string sqlCore = "SELECT DMS.deviceid, DMS.disconnectaddress "
                                      "FROM devicemct400series DMS";
        const string idClause = createIdSqlClause(paoids, "DMS", "deviceid");

        string sql = sqlCore;

        if(!idClause.empty())
        {
            sql += " WHERE ";
            sql += idClause;
        }

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);

        rdr.execute();

        if(DebugLevel & 0x00020000 || !rdr.isValid())
        {
            string loggedSQLstring = rdr.asString();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << loggedSQLstring << endl;
            }
        }

        if(rdr.isValid())
        {
            using Cti::Devices::MctDevice;
            using Cti::Devices::Mct410Device;
            using Cti::Devices::Mct410DeviceSPtr;

            CtiDeviceSPtr       tmpDevice;
            Mct410DeviceSPtr tmpMCT410;

            while( rdr() )
            {
                row_found = true;

                rdr["deviceid"]          >> tmpmctid;
                rdr["disconnectaddress"] >> tmpdisconnectaddress;

                tmpMCT410 = boost::static_pointer_cast<Mct410Device>(getDeviceByID(tmpmctid));

                if( tmpMCT410 )
                {
                    if( tmpdisconnectaddress > 0 && tmpdisconnectaddress < 0x400000 )
                    {
                        tmpMCT410->setDisconnectAddress(tmpdisconnectaddress);
                    }
                    else
                    {
                        tmpMCT410->setDisconnectAddress(0);

                        if( isDebugLudicrous() )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - invalid disconnect address " << tmpdisconnectaddress << " for device \"" << tmpMCT410->getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                }
            }

            //  if this was targeted at a certain MCT and we didn't find a row, clear out the MCT's address
            if( !paoids.empty() && !row_found )
            {
                for( Cti::Database::id_set_itr paoid_itr = paoids.begin(); paoid_itr != paoids.end(); ++paoid_itr )
                {
                    tmpDevice = getDeviceByID(*paoid_itr);

                    if( tmpDevice && (MctDevice::isMct410(tmpDevice->getType()) || tmpDevice->getType() == TYPEMCT420FL) )
                    {
                        boost::static_pointer_cast<Mct410Device>(tmpDevice)->setDisconnectAddress(0);
                    }
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Error reading MCT 400 Configs from database" << endl;
        }

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for MCT Configs" << endl;
        }
    }
}


void CtiDeviceManager::refreshDynamicPaoInfo(Cti::Database::id_set &paoids)
{
    CtiDeviceSPtr device;
    long tmp_paobjectid, tmp_entryid;

    CtiTableDynamicPaoInfo dynamic_paoinfo;

    {
        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Dynamic PAO Info" << endl;
        }

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection);

        string sql = CtiTableDynamicPaoInfo::getSQLCoreStatement(_app_id);

        if( !sql.empty() )
        {
            if(!paoids.empty())
            {
                sql += " AND " + createIdSqlClause(paoids, "DPI", "paobjectid");
            }
            rdr.setCommandText(sql);
            rdr.execute();
        }
        else
        {
            return;
        }

        if(DebugLevel & 0x00020000 || !rdr.isValid())
        {
            string loggedSQLstring = rdr.asString();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << loggedSQLstring << endl;
            }
        }

        if(rdr.isValid())
        {
            while( rdr() )
            {
                dynamic_paoinfo.DecodeDatabaseReader(rdr);

                rdr["paobjectid"] >> tmp_paobjectid;
                rdr["entryid"]    >> tmp_entryid;

                device = getDeviceByID(tmp_paobjectid);

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
            dout << "Error reading Dynamic PAO Info from database. " <<  endl;
        }

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Dynamic PAO Info" << endl;
        }
    }
}

void CtiDeviceManager::refreshStaticPaoInfo(Cti::Database::id_set &paoids)
{
    CtiDeviceSPtr device;
    long tmp_paobjectid, tmp_entryid;

    CtiTableStaticPaoInfo static_paoinfo;

    {
        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Static PAO Info" << endl;
        }

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection);

        string sql = CtiTableStaticPaoInfo::getSQLCoreStatement();

        if( !sql.empty() )
        {
            if(!paoids.empty())
            {
                sql += " AND " + createIdSqlClause(paoids, "SPI", "paobjectid");
            }
            rdr.setCommandText(sql);
            rdr.execute();
        }
        else
        {
            return;
        }

        if(DebugLevel & 0x00020000 || !rdr.isValid())
        {
            string loggedSQLstring = rdr.asString();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << loggedSQLstring << endl;
            }
        }

        if(rdr.isValid())
        {
            while( rdr() )
            {
                static_paoinfo.DecodeDatabaseReader(rdr);

                rdr["paobjectid"]       >> tmp_paobjectid;
                rdr["staticpaoinfoid"]  >> tmp_entryid;

                device = getDeviceByID(tmp_paobjectid);

                if( device )
                {
                    device->setStaticInfo(static_paoinfo);
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - no parent found for static PAO info record (pao " << tmp_paobjectid << ", entryid " << tmp_entryid << ")  **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Error reading Static PAO Info from database. " <<  endl;
        }

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Static PAO Info" << endl;
        }
    }
}

//  This method loads all the device properties/characteristics which must be appended to an already
//  loaded device.
void CtiDeviceManager::refreshDeviceProperties(Cti::Database::id_set &paoids, int type)
{
    if( !type || type == TYPE_MACRO )
    {
        Timing::DebugTimer timer("loading macro subdevices");
        refreshMacroSubdevices(paoids);
    }

    if( !type || isIon(type) )
    {
        Timing::DebugTimer timer("loading ION meter groups");
        refreshIONMeterGroups(paoids);
    }

    if( !type || isMct(type) )
    {
        Timing::DebugTimer timer("loading MCT configs");
        refreshMCTConfigs(paoids);
        refreshMCT400Configs(paoids);
    }

    {
        Timing::DebugTimer timer("loading device exclusions");
        refreshExclusions(paoids);
    }

    {
        Timing::DebugTimer timer("loading dynamic device data");
        refreshDynamicPaoInfo(paoids);
    }

    {
        Timing::DebugTimer timer("loading static device data");
        refreshStaticPaoInfo(paoids);
    }
}


void CtiDeviceManager::writeDynamicPaoInfo( void )
{
    static const char *sql = "select max(entryid) from dynamicpaoinfo";
    static long max_entryid;

    vector<CtiTableDynamicPaoInfo *> dirty_info;

    Cti::Database::DatabaseConnection   conn;

    if ( ! conn.isValid() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** ERROR **** Invalid Connection to Database.  " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

        return;
    }

    spiterator dev_itr = begin(),
               dev_end = end();

    for( ; dev_itr != dev_end; dev_itr++ )
    {
        //  passed by reference
        dev_itr->second->getDirtyInfo(dirty_info, _app_id);
    }

    if( !dirty_info.empty() )
    {
        try
        {
            bool status;

            Cti::Database::DatabaseReader       rdr(conn, sql);
            rdr.execute();

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
            conn.beginTransaction();

            vector<CtiTableDynamicPaoInfo *>::iterator itr;
            for( itr = dirty_info.begin(); itr != dirty_info.end(); itr++ )
            {
                (*itr)->setOwner(_app_id);

                status = (*itr)->Update(conn);

                //  update didn't work, so we have to assign a new entry ID
                //    this is clunky - entry ID is useless, since we key on Owner, PAO, and Key anyway
                if( ! status )
                {
                    (*itr)->setEntryID(max_entryid + 1);

                    status = (*itr)->Insert(conn);

                    if( status )
                    {
                        max_entryid++;  //  increments the reference
                    }
                }

                if( ! status )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - error inserting/updating DynamicPaoInfo **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    (*itr)->dump();
                }

                //  TODO:  if the insert fails, this needs to keep the records around so it can write them in the future
                delete *itr;
            }

            bool commitSuccess = conn.commitTransaction();

            /*
            if( commitSuccess )
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


CtiDeviceManager::spiterator CtiDeviceManager::begin()  {  return _smartMap.getMap().begin();  }
CtiDeviceManager::spiterator CtiDeviceManager::end()    {  return _smartMap.getMap().end();    }


CtiDeviceManager::coll_type::lock_t &CtiDeviceManager::getLock()
{
    return _smartMap.getLock();
}

void CtiDeviceManager::apply(void (*applyFun)(const long, ptr_type, void*), void* d)
{
    try
    {
        int trycount = 0;

        coll_type::reader_lock_guard_t guard(getLock());

        while(!guard.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint: Unable to lock device mutex.  Will retry. **** " << __FILE__ << " (" << __LINE__ << ") Last Acquired By TID: " << static_cast<string>(getLock()) << " Faddr: 0x" << applyFun << endl;
            }
            guard.tryAcquire(30000);

            if(trycount++ > 6)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint: Was unable to lock device mutex.  Applying anyway. **** " << __FILE__ << " (" << __LINE__ << " Last Acquired By TID: " << static_cast<string>(getLock()) << " Faddr: 0x" << applyFun << endl;
                    dout << "  CtiDeviceManager::apply " << endl;
                }
                break;
            }
        }

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

    coll_type::reader_lock_guard_t guard(getLock(), 30000);

    while(!guard.isAcquired())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint: Unable to lock device manager mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        guard.tryAcquire(30000);
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
        coll_type::reader_lock_guard_t guard(getLock(), 30000);

        while(!guard.isAcquired())
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
                dout << CtiTime() << " **** Checkpoint: Unable to lock device mutex.  Will retry. **** " << __FILE__ << " (" << __LINE__ << ") Last Acquired By TID: " << static_cast<string>(getLock()) << " Faddr: 0x" << findFun << endl;
            }
            guard.tryAcquire(30000);
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

CtiDeviceManager::ptr_type CtiDeviceManager::chooseExclusionDevice( LONG portid )
{
    CtiTime now;
    CtiDeviceSPtr devA;           //
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

    coll_type::reader_lock_guard_t lock(getLock());

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


bool CtiDeviceManager::refreshPointGroups()
{
    Cti::Database::id_set stub;
    return refreshPointGroups(stub);
}

bool CtiDeviceManager::refreshPointGroups(Cti::Database::id_set &paoids)
{
    return loadDeviceType(paoids, "point groups", CtiDeviceGroupPoint());
}

//Currently sets up addressing for expresscom only.
void CtiDeviceManager::refreshGroupHierarchy(LONG deviceID)
{
    CtiDeviceManager::ptr_type device = getDeviceByID(deviceID);

    if( gConfigParms.isTrue("LOG_WITH_EXPRESSCOM_HIERARCHY") && (deviceID == 0 || (device && device->isGroup() && isExpresscomGroup(device->getType()))) )
    {
        CtiDeviceGroupBaseSPtr groupDevice = boost::static_pointer_cast<CtiDeviceGroupBase>(device);
        vector< CtiDeviceSPtr > match_coll;
        vector< CtiDeviceGroupBaseSPtr > groupVec;
        getDevicesByType(TYPE_LMGROUP_EXPRESSCOM, match_coll);
        getDevicesByType(TYPE_LMGROUP_RFN_EXPRESSCOM, match_coll);
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

