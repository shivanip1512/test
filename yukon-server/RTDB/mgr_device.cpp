/*-----------------------------------------------------------------------------*
*
* File:   mgr_device
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_device.cpp-arc  $
* REVISION     :  $Revision: 1.101 $
* DATE         :  $Date: 2008/10/28 19:21:43 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw/db/db.h>

#include "boost/multi_array/algorithm.hpp"

#include "mgr_device.h"
#include "debug_timer.h"
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


using namespace Cti;  //  in preparation for moving devices to their own namespace
using namespace std;


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

static void applyRemoveInfiniteProhibit(const long unusedkey, CtiDeviceSPtr Device, void* d)
{
    try
    {
        CtiDeviceBase *pAnxiousDevice = (CtiDeviceBase *)d;       // This is the port that wishes to execute!
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

inline bool isDeviceIdStaticId(CtiDeviceSPtr &pDevice, void* d)
{
    CtiDeviceBase *pSp = (CtiDeviceBase *)d;

    return pDevice->getID() == pSp->getID();
}

inline bool isDeviceNotUpdated(CtiDeviceSPtr &pDevice, void* d)
{
    // Return TRUE if it is NOT SET
    return !pDevice->getUpdatedFlag();
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

void CtiDeviceManager::test_dumpList(void)
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


bool CtiDeviceManager::refreshDevices(RWDBReader& rdr)
{
    LONG              lTemp = 0;
    CtiDeviceSPtr     pTempCtiDevice;
    bool rowSelected = false;

    while( (setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        rowSelected = true;
        rdr["deviceid"] >> lTemp;            // get the DeviceID

        if( pTempCtiDevice = getDeviceByID(lTemp) )
        {
            //  The device in this row is already in the list.  We need to
            //    update the list entry to the new settings

            // Fills himself in from the reader
            pTempCtiDevice->DecodeDatabaseReader(rdr);

            // Mark it updated...  should DecodeDatabaseReader() do this?
            pTempCtiDevice->setUpdatedFlag();
        }
        else
        {
            // Use the reader to get me an object of the proper type
            CtiDeviceBase* pSp = DeviceFactory(rdr);

            if(pSp)
            {
                // Fills himself in from the reader
                pSp->DecodeDatabaseReader(rdr);

                // Mark it updated...  should DecodeDatabaseReader() do this?
                pSp->setUpdatedFlag();

                // Stuff it in the list
                _smartMap.insert( pSp->getID(), pSp );
            }
        }
    }

    return rowSelected;
}



CtiDeviceManager::ptr_type CtiDeviceManager::RemoteGetPortRemoteEqual (LONG Port, LONG Remote)
{
    ptr_type p;

    coll_type::reader_lock_guard_t guard(getLock());

    if(_smartMap.empty())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " There are no entries in the remote device list" << endl;
        }
    }

    spiterator itr, itr_end = end();

    for(itr = begin(); itr != itr_end; itr++)
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

    coll_type::reader_lock_guard_t guard(getLock());

    if(_smartMap.empty())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " There are no entries in the remote device list" << endl;
        }
    }

    spiterator itr, itr_end = end();

    for(itr = begin(); itr != itr_end; itr++)
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

    coll_type::reader_lock_guard_t guard(getLock());

    if(_smartMap.empty())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " There are no entries in the remote device list" << endl;
        }
    }

    spiterator itr, itr_end = end();

    for(itr = begin(); itr != itr_end; itr++)
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

CtiDeviceManager::ptr_type CtiDeviceManager::getDeviceByID (LONG Dev)
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

    if(_smartMap.empty())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " There are no entries in the device manager list" << endl;
        }
    }

    spiterator itr, itr_end = end();

    for(itr = begin(); itr != itr_end; itr++)
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
_app_id(app_id)
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

void CtiDeviceManager::refresh(LONG paoID, string category, string devicetype)
{
    if(paoID != 0)
    {
        bool rowFound = false;
        CtiDeviceSPtr pDev = getDeviceByID(paoID);

        int type = resolvePAOType(category, devicetype);

        if(pDev && pDev->getType() != type)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << pDev->getName() << " has changed type to " << devicetype << " from " << desolveDeviceType(pDev->getType()) << endl;
            }

            if( _smartMap.remove(paoID) && DebugLevel & 0x00020000)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Old device object has been orphaned " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        // We were given an id.  We can use that to reduce our workload.
        refreshList(id_range_t(paoID), type);
    }
    else
    {
        // we were given no id.  There must be no dbchange info.
        refreshList();
    }
}


bool CtiDeviceManager::loadDeviceType(id_range_t &paoids, const string &device_name, const CtiDeviceBase &device, string type, const bool include_type)
{
    bool print_bounds = DebugLevel & 0x00020000;

    DebugTimer timer("looking for " + device_name, print_bounds);

    RWDBConnection conn     = getConnection();
    RWDBDatabase   db       = getDatabase();
    RWDBSelector   selector = db.selector();
    RWDBTable      keyTable;

    device.getSQL(db, keyTable, selector);

    if( !type.empty() )
    {
        transform(type.begin(), type.end(), type.begin(), toupper);

        if( include_type )
        {
            selector.where(rwdbUpper(keyTable["type"]) == type.c_str() && selector.where());
        }
        else
        {
            selector.where(rwdbUpper(keyTable["type"]) != type.c_str() && selector.where());
        }
    }

    addIDClause(selector, keyTable["paobjectid"], paoids);

    RWDBReader rdr = selector.reader(conn);

    if( DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << selector.asString() << endl;
    }

    return refreshDevices(rdr);
}


void CtiDeviceManager::addIDClause(RWDBSelector &selector, RWDBColumn &id_column, id_range_t &id_range)
{
    if( id_range.empty() )
    {
        return;
    }

    if( id_range.size() == 1 )
    {
        //  special single id case

        selector.where(selector.where() && id_column == *(id_range.begin()));

        return;
    }

    ostringstream in_list;

    in_list << "(";

    copy(id_range.begin(), id_range.end(), csv_output_iterator<long, ostringstream>(in_list));

    in_list << ")";

    selector.where(selector.where() && id_column.in(RWDBExpr(in_list.str().c_str(), false)));
}


void CtiDeviceManager::refreshList(id_range_t &paoids, const LONG deviceType)
{
    CtiDeviceSPtr pTempCtiDevice;
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
            id_itr_t paoid_itr = paoids.begin();

            while( paoid_itr != paoids.end() )
            {
                pTempCtiDevice = getDeviceByID(*paoid_itr++);
                if(pTempCtiDevice) pTempCtiDevice->resetUpdatedFlag();
            }
        }

        resetErrorCode();

        CtiDeviceBase *dev = 0;

        if( deviceType )
        {
            dev = createDeviceType(deviceType);
        }

        int max_ids_per_select = gConfigParms.getValueAsInt("MAX_IDS_PER_DEVICE_SELECT", 256);

        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);

            id_itr_t paoid_itr = paoids.begin();

            //  I don't really like the do-while construct, but this loop must happen at least once even if no paoids were passed in
            do
            {
                int subset_size = min(distance(paoid_itr, paoids.end()), max_ids_per_select);

                //  note the iterator difference/addition - requires a random-access iterator...
                //    trait tags could be useful here except that VC6 doesn't support template member functions
                id_range_t paoid_subset(paoid_itr, paoid_itr + subset_size);

                if( dev )
                {
                    //  type-specific directed load
                    rowFound |= loadDeviceType(paoid_subset, "directed load", *dev);
                }
                else
                {
                    rowFound |= loadDeviceType(paoid_subset, "DLC devices", CtiDeviceCarrier());

                    if( deviceType != TYPEMCT410 )
                    {
                        rowFound |= loadDeviceType(paoid_subset, "Grid Advisor devices",   CtiDeviceGridAdvisor());

                        rowFound |= loadDeviceType(paoid_subset, "Sixnet IEDs",            CtiDeviceIED(),         "SIXNET");
                        rowFound |= loadDeviceType(paoid_subset, "Meters and IEDs",        CtiDeviceMeter());

                        //  prevent the LMI from being loaded twice
                        rowFound |= loadDeviceType(paoid_subset, "DNP/ION devices",        Device::DNP(),          "RTU-LMI", false);
                        rowFound |= loadDeviceType(paoid_subset, "LMI RTUs",               CtiDeviceLMI());
                        rowFound |= loadDeviceType(paoid_subset, "RTM devices",            CtiDeviceRTM(),         "RTM");

                        rowFound |= loadDeviceType(paoid_subset, "TAP devices",            CtiDeviceTapPagingTerminal());
                        rowFound |= loadDeviceType(paoid_subset, "TNPP devices",           CtiDeviceTnppPagingTerminal());

                        //  exclude the CCU 721
                        rowFound |= loadDeviceType(paoid_subset, "IDLC target devices",    CtiDeviceIDLC(),        "CCU-721", false);
                        rowFound |= loadDeviceType(paoid_subset, "CCU-721 devices",        Device::CCU721());

                        rowFound |= loadDeviceType(paoid_subset, "MCT broadcast devices",  CtiDeviceMCTBroadcast());

                        rowFound |= loadDeviceType(paoid_subset, "Repeater 800 devices",   CtiDeviceDLCBase(),     "REPEATER 800");
                        rowFound |= loadDeviceType(paoid_subset, "Repeater 801 devices",   CtiDeviceDLCBase(),     "REPEATER 801");
                        rowFound |= loadDeviceType(paoid_subset, "Repeater 900 devices",   CtiDeviceDLCBase(),     "REPEATER");
                        rowFound |= loadDeviceType(paoid_subset, "Repeater 902 devices",   CtiDeviceDLCBase(),     "REPEATER 902");
                        rowFound |= loadDeviceType(paoid_subset, "Repeater 921 devices",   CtiDeviceDLCBase(),     "REPEATER 921");

                        rowFound |= loadDeviceType(paoid_subset, "CBC devices",            CtiDeviceCBC());
                        rowFound |= loadDeviceType(paoid_subset, "FMU devices",            CtiDeviceFMU(),         "FMU");
                        rowFound |= loadDeviceType(paoid_subset, "RTC devices",            CtiDeviceRTC());

                        rowFound |= loadDeviceType(paoid_subset, "Emetcon groups",         CtiDeviceGroupEmetcon());
                        rowFound |= loadDeviceType(paoid_subset, "Versacom groups",        CtiDeviceGroupVersacom());
                        rowFound |= loadDeviceType(paoid_subset, "Expresscom groups",      CtiDeviceGroupExpresscom());
                        rowFound |= loadDeviceType(paoid_subset, "EnergyPro groups",       CtiDeviceGroupEnergyPro());
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

                paoid_itr += subset_size;

            } while( paoid_itr != paoids.end() );
        }

        if( dev )
        {
            delete dev;
        }

        // Now I need to check for any Device removals based upon the
        // Updated Flag being NOT set.  I only do this for non-directed loads.  a paoid is directed.!
        if( paoids.empty() && rowFound )
        {
            DebugTimer timer("removing non-updated devices ");

            // Effectively deletes the memory if there are no other "owners"
            while( pTempCtiDevice = _smartMap.remove(isDeviceNotUpdated, NULL) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  Evicting " << pTempCtiDevice->getName() << " from list" << endl;
            }
        }

        if( getErrorCode() != RWDBStatus::ok && getErrorCode() != RWDBStatus::endOfFetch )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " database had a return code of " << getErrorCode() << endl;
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
            coll_type::reader_lock_guard_t guard(getLock());

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

                    CtiDeviceBase::exclusions exvector = anxiousDevice->getExclusions();
                    CtiDeviceBase::exclusions::iterator itr;

                    for(itr = exvector.begin(); !busted && itr != exvector.end(); itr++)
                    {
                        CtiTablePaoExclusion &paox = *itr;

                        switch(paox.getFunctionId())
                        {
                        case (CtiTablePaoExclusion::ExFunctionIdExclusion):
                            {
                                device = getDeviceByID(paox.getExcludedPaoId());  // grab the excludable device

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

    //  I don't think we need this - apply() locks us instead
    //coll_type::writer_lock_guard_t guard(getLock());

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


void CtiDeviceManager::refreshExclusions(id_range_t &paoids)
{
    coll_type::writer_lock_guard_t guard(getLock());

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn     = getConnection();
    RWDBDatabase   db       = getDatabase();
    RWDBSelector   selector = db.selector();
    RWDBTable      keyTable;

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Looking for Device Exclusions" << endl;
    }

    CtiTablePaoExclusion::getSQL( db, keyTable, selector );

    // The servers do not care about the LM subordination.
    selector.where(keyTable["functionid"] != CtiTablePaoExclusion::ExFunctionLMSubordination && selector.where());

    addIDClause(selector, keyTable["paoid"], paoids);

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << selector.asString() << endl;
    }

    RWDBReader rdr = selector.reader(conn);

    if(rdr.status().errorCode() == RWDBStatus::ok)
    {
        // prep the exclusion lists
        if( paoids.empty() )
        {
            apply(applyClearExclusions);
            _exclusionMap.removeAll(removeExclusionDevice, (void*)0);
        }
        else
        {
            for( id_itr_t paoid_itr = paoids.begin(); paoid_itr != paoids.end(); ++paoid_itr )
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

    while( (setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
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


void CtiDeviceManager::refreshIONMeterGroups(id_range_t &paoids)
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

    addIDClause(selector, tblPAObject["PAObjectID"], paoids);

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

            CtiDeviceSPtr pTempCtiDevice = getDeviceByID(tmpDeviceID);

            if(pTempCtiDevice && isION(pTempCtiDevice->getType()))
            {
                boost::static_pointer_cast<CtiDeviceION>(pTempCtiDevice)->setMeterGroupData(tmpMeterNumber);
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


void CtiDeviceManager::refreshMacroSubdevices(id_range_t &paoids)
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

        addIDClause(selector, tblGenericMacro["OwnerID"], paoids);

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

    addIDClause(selector, tblGenericMacro["OwnerID"], paoids);

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

        // prep the exclusion lists
        if( paoids.empty() )
        {
            apply(applyClearMacroDeviceList);
        }
        else
        {
            for( id_itr_t paoid_itr = paoids.begin(); paoid_itr != paoids.end(); ++paoid_itr )
            {
                CtiDeviceSPtr dev = getDeviceByID(*paoid_itr);

                if( dev )  applyClearMacroDeviceList(0, dev, (void *)dev->getID());
            }
        }


        while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
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


void CtiDeviceManager::refreshMCTConfigs(id_range_t &paoids)
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

        addIDClause(selector, tblPAObject["PAObjectID"], paoids);

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

                pTempCtiDevice = getDeviceByID(tmpmctid);

                if(pTempCtiDevice)
                {
                    boost::static_pointer_cast<CtiDeviceMCT>(pTempCtiDevice)->setConfigData(tmpconfigname, tmpconfigtype, tmpconfigmode, tmpwire, tmpmpkh);
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


void CtiDeviceManager::refreshMCT400Configs(id_range_t &paoids)
{
    LONG tmpmctid, tmpdisconnectaddress;
    bool row_found = false;

    {
        RWDBConnection conn   = getConnection();
        RWDBDatabase db       = getDatabase();
        RWDBSelector selector = db.selector();
        RWDBTable configTbl   = db.table("devicemct400series");
        RWDBReader rdr;

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for MCT Configs" << endl;
        }

        selector << configTbl["deviceid"];
        selector << configTbl["disconnectaddress"];

        //  no need to bring yukonpaobject into this yet, we'll just link straight to the config table
        addIDClause(selector, configTbl["deviceid"], paoids);

        rdr = selector.reader(conn);
        if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
        }

        if(rdr.status().errorCode() == RWDBStatus::ok)
        {
            CtiDeviceSPtr       tmpDevice;
            CtiDeviceMCT410SPtr tmpMCT410;

            while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
            {
                row_found = true;

                rdr["deviceid"]          >> tmpmctid;
                rdr["disconnectaddress"] >> tmpdisconnectaddress;

                tmpMCT410 = boost::static_pointer_cast<CtiDeviceMCT410>(getDeviceByID(tmpmctid));

                if( tmpMCT410 )
                {
                    if( tmpdisconnectaddress > 0 && tmpdisconnectaddress < 0x400000 )
                    {
                        tmpMCT410->setDisconnectAddress(tmpdisconnectaddress);
                    }
                    else
                    {
                        tmpMCT410->setDisconnectAddress(0);

                        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
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
                for( id_itr_t paoid_itr = paoids.begin(); paoid_itr != paoids.end(); ++paoid_itr )
                {
                    tmpDevice = getDeviceByID(*paoid_itr);

                    if( tmpDevice && tmpDevice->getType() == TYPEMCT410 )
                    {
                        boost::static_pointer_cast<CtiDeviceMCT410>(tmpDevice)->setDisconnectAddress(0);
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


void CtiDeviceManager::refreshDynamicPaoInfo(id_range_t &paoids)
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

        addIDClause(selector, keyTable["paobjectid"], paoids);

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
            dout << "Error reading Dynamic PAO Info from database: " << rdr.status().errorCode() << endl;
        }

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Dynamic PAO Info" << endl;
        }
    }
}


//  This method loads all the device properties/characteristics which must be appended to an already
//  loaded device.
void CtiDeviceManager::refreshDeviceProperties(id_range_t &paoids, int type)
{
    if( !type || type == TYPE_MACRO )
    {
        DebugTimer timer("loading macro subdevices");       refreshMacroSubdevices(paoids);
    }

    if( !type || isION(type) )
    {
        DebugTimer timer("loading ION meter groups");       refreshIONMeterGroups (paoids);
    }

    if( !type || isMCT(type) )
    {
        DebugTimer timer("loading MCT configs");            refreshMCTConfigs     (paoids);
                                                            refreshMCT400Configs  (paoids);
    }

    {  DebugTimer timer("loading device exclusions");       refreshExclusions     (paoids);  }
    {  DebugTimer timer("loading dynamic device data");     refreshDynamicPaoInfo (paoids);  }
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
CtiDeviceManager::coll_type::lock_t &CtiDeviceManager::getLock()
{
    return _smartMap.getLock();
}

void CtiDeviceManager::apply(void (*applyFun)(const long, ptr_type, void*), void* d)
{
    try
    {
        int trycount = 0;

        #if 1
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

bool CtiDeviceManager::contains(bool (*findFun)(const long, const ptr_type &, void*), void* d)
{
    bool found = false;

    try
    {
        coll_type::reader_lock_guard_t guard(getLock(), 30000);

        while(!guard.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint: Unable to lock device manager mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            guard.tryAcquire(30000);
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


bool CtiDeviceManager::refreshPointGroups()
{
    return refreshPointGroups(id_range_t());
}

bool CtiDeviceManager::refreshPointGroups(id_range_t &paoids)
{
    return loadDeviceType(paoids, "point groups", CtiDeviceGroupPoint());
}

//Currently sets up addressing for expresscom only.
void CtiDeviceManager::refreshGroupHierarchy(LONG deviceID)
{
    CtiDeviceManager::ptr_type device = getDeviceByID(deviceID);

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

