
/*-----------------------------------------------------------------------------*
*
* File:   mgr_device
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_device.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2002/04/23 14:50:20 $
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
#include "dev_remote.h"
#include "dev_meter.h"
#include "dev_idlc.h"
#include "dev_carrier.h"
#include "dev_repeater.h"
#include "dev_tap.h"
#include "dev_grp_emetcon.h"
#include "dev_grp_ripple.h"
#include "dev_grp_versacom.h"
#include "yukon.h"

#include "devicetypes.h"
#include "resolvers.h"
#include "slctdev.h"
#include "rtdb.h"


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


void CtiDeviceManager::RefreshList(LONG paoID)
{
    CtiHashKey key(paoID);
    CtiDeviceBase *pDev = Map.findValue(&key);

    if(pDev)
    {
        RWDBConnection conn = getConnection();
        RWLockGuard<RWDBConnection> conn_guard(conn);
        RWDBDatabase db = getDatabase();

        RWDBTable   keyTable;
        RWDBSelector selector = db.selector();

        pDev->getSQL( db, keyTable, selector );
        selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );
        RWDBReader rdr = selector.reader(conn);

        RefreshDevices(rdr, DeviceFactory, isADevice, NULL);

        // if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << RWTime() << " Done reloading " << pDev->getName() << endl;
        }
    }
    else
    {
        RefreshList();
    }
}

void CtiDeviceManager::RefreshList(CtiDeviceBase* (*Factory)(RWDBReader &), BOOL (*testFunc)(CtiDeviceBase*,void*), void *arg)
{
    CtiDeviceBase *pTempCtiDevice = NULL;

    try
    {
        CtiDeviceBase *pSp;

        {   // Make sure all objects that that store results
            {
                // Reset everyone's Updated flag.
                Map.apply(ApplyDeviceResetUpdated, NULL);
                resetErrorCode();

                {
                    RWDBConnection conn = getConnection();
                    RWLockGuard<RWDBConnection> conn_guard(conn);
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

                    RWDBReader rdr = selector.reader(conn);
                    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                    }

                    RefreshDevices(rdr, Factory, testFunc, arg);

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Sixnet IEDs" << endl;
                    }
                }

                {
                    RWDBConnection conn = getConnection();
                    RWLockGuard<RWDBConnection> conn_guard(conn);
                    RWDBDatabase db = getDatabase();

                    RWDBTable   keyTable;
                    RWDBSelector selector = db.selector();

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Meters & IEDs" << endl;
                    }
                    CtiDeviceMeter().getSQL( db, keyTable, selector );

                    RWDBReader rdr = selector.reader(conn);
                    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                    }
                    RefreshDevices(rdr, Factory, testFunc, arg);

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Meters & IEDs" << endl;
                    }
                }

                {
                    RWDBConnection conn = getConnection();
                    RWLockGuard<RWDBConnection> conn_guard(conn);
                    RWDBDatabase db = getDatabase();

                    RWDBTable   keyTable;
                    RWDBSelector selector = db.selector();

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Tap Devices" << endl;
                    }
                    CtiDeviceTapPagingTerminal().getSQL( db, keyTable, selector );

                    RWDBReader rdr = selector.reader(conn);
                    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                    }
                    RefreshDevices(rdr, Factory, testFunc, arg);

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for TAP Devices" << endl;
                    }
                }

                {
                    RWDBConnection conn = getConnection();
                    RWLockGuard<RWDBConnection> conn_guard(conn);
                    RWDBDatabase db = getDatabase();

                    RWDBTable   keyTable;
                    RWDBSelector selector = db.selector();

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for IDLC Target Devices" << endl;
                    }
                    CtiDeviceIDLC().getSQL( db, keyTable, selector );

                    RWDBReader rdr = selector.reader(conn);
                    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                    }
                    RefreshDevices(rdr, Factory, testFunc, arg);

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for IDLC Target Devices" << endl;
                    }
                }

                {
                    RWDBConnection conn = getConnection();
                    RWLockGuard<RWDBConnection> conn_guard(conn);
                    RWDBDatabase db = getDatabase();

                    RWDBTable   keyTable;
                    RWDBSelector selector = db.selector();

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for DLC Devices" << endl;
                    }
                    CtiDeviceCarrier().getSQL( db, keyTable, selector );

                    RWDBReader rdr = selector.reader(conn);
                    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                    }
                    RefreshDevices(rdr, Factory, testFunc, arg);

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for DLC Devices" << endl;
                    }
                }

                {
                    RWDBConnection conn = getConnection();
                    RWLockGuard<RWDBConnection> conn_guard(conn);
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


                    RWDBReader rdr = selector.reader(conn);
                    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                    }
                    RefreshDevices(rdr, Factory, testFunc, arg);

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for REPEATER Devices" << endl;
                    }
                }

                {
                    RWDBConnection conn = getConnection();
                    RWLockGuard<RWDBConnection> conn_guard(conn);
                    RWDBDatabase db = getDatabase();

                    RWDBTable   keyTable;
                    RWDBSelector selector = db.selector();

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for CBC Devices" << endl;
                    }
                    CtiDeviceCBC().getSQL( db, keyTable, selector );
                    RWDBReader rdr = selector.reader(conn);
                    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                    }
                    RefreshDevices(rdr, Factory, testFunc, arg);

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for CBC Devices" << endl;
                    }
                }

                {
                    RWDBConnection conn = getConnection();
                    RWLockGuard<RWDBConnection> conn_guard(conn);
                    RWDBDatabase db = getDatabase();

                    RWDBTable   keyTable;
                    RWDBSelector selector = db.selector();

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Emetcon Group Devices" << endl;
                    }
                    CtiDeviceGroupEmetcon().getSQL( db, keyTable, selector );

                    RWDBReader rdr = selector.reader(conn);
                    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                    }
                    RefreshDevices(rdr, Factory, testFunc, arg);

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Emetcon Group Devices" << endl;
                    }
                }

                {
                    RWDBConnection conn = getConnection();
                    RWLockGuard<RWDBConnection> conn_guard(conn);
                    RWDBDatabase db = getDatabase();

                    RWDBTable   keyTable;
                    RWDBSelector selector = db.selector();

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Versacom Group Devices" << endl;
                    }
                    CtiDeviceGroupVersacom().getSQL( db, keyTable, selector );

                    RWDBReader rdr = selector.reader(conn);
                    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                    }
                    RefreshDevices(rdr, Factory, testFunc, arg);

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Versacom Group Devices" << endl;
                    }
                }

                {
                    RWDBConnection conn = getConnection();
                    RWLockGuard<RWDBConnection> conn_guard(conn);
                    RWDBDatabase db = getDatabase();

                    RWDBTable   keyTable;

                    RWDBSelector selector = db.selector();

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Ripple Group Devices" << endl;
                    }
                    CtiDeviceGroupRipple().getSQL( db, keyTable, selector );

                    RWDBReader rdr = selector.reader(conn);
                    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                    }
                    RefreshDevices(rdr, Factory, testFunc, arg);

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Ripple Group Devices" << endl;
                    }
                }

                {
                    RWDBConnection conn = getConnection();
                    RWLockGuard<RWDBConnection> conn_guard(conn);
                    RWDBDatabase db = getDatabase();
                    RWDBTable   keyTable;

                    RWDBSelector selector = db.selector();

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Macro Devices" << endl;
                    }
                    CtiDeviceMacro().getSQL( db, keyTable, selector );

                    RWDBReader rdr = selector.reader(conn);
                    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                    }
                    RefreshDevices(rdr, Factory, testFunc, arg);

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Macro Devices" << endl;
                    }
                }

                {
                    LockGuard dev_guard(monitor());
                    RWDBConnection conn = getConnection();
                    RWLockGuard<RWDBConnection> conn_guard(conn);
                    RWDBDatabase db = getDatabase();
                    RWDBSelector selector = db.selector();
                    RWDBTable tblGenericMacro = db.table("GenericMacro");
                    RWDBReader rdr;
                    long tmpOwnerID, tmpChildID;
                    CtiHashKey tmpOwnerKey(0), tmpChildKey(0);

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Macro Subdevices" << endl;
                    }

                    selector << tblGenericMacro["ChildID"]
                             << tblGenericMacro["OwnerID"];
                    selector.from(tblGenericMacro);
                    selector.where(tblGenericMacro["MacroType"] == RWDBExpr("GROUP"));
                    selector.orderBy(tblGenericMacro["ChildOrder"]);

                    rdr = selector.reader(conn);
                    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                    }

                    if(rdr.status().errorCode() == RWDBStatus::ok)
                    {
                        Map.apply(ApplyClearMacroDeviceList, NULL);

                        while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
                        {
                            rdr["OwnerID"] >> tmpOwnerID;
                            rdr["ChildID"] >> tmpChildID;
                            tmpOwnerKey.setID(tmpOwnerID);
                            tmpChildKey.setID(tmpChildID);
                            ((CtiDeviceMacro *)Map.findValue(&tmpOwnerKey))->addDevice((CtiDeviceBase *)Map.findValue(&tmpChildKey));
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Error reading macro subdevices from database: " << rdr.status().errorCode() << endl;
                    }

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Macro Subdevices" << endl;
                    }
                }

                {
                    RWDBConnection conn = getConnection();
                    RWLockGuard<RWDBConnection> conn_guard(conn);
                    RWDBDatabase db = getDatabase();
                    RWDBTable   keyTable;

                    RWDBSelector selector = db.selector();

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for System Devices" << endl;
                    }
                    CtiDevice().getSQL( db, keyTable, selector );
                    selector.where( keyTable["type"]==RWDBExpr("System") && selector.where() );   // Need to attach a few conditions!

                    RWDBReader rdr = selector.reader(conn);
                    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                    }
                    RefreshDevices(rdr, Factory, testFunc, arg);

                    if(DebugLevel & 0x00020000)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for System Devices" << endl;
                    }
                }

                if(getErrorCode() != RWDBStatus::ok)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << " database had a return code of " << getErrorCode() << endl;
                    }
                }
                else
                {
                    // Now I need to check for any Device removals based upon the
                    // Updated Flag being NOT set

                    Map.apply(ApplyInvalidateNotUpdated, NULL);

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


void CtiDeviceManager::RefreshDevices(RWDBReader& rdr, CtiDeviceBase* (*Factory)(RWDBReader &), BOOL (*testFunc)(CtiDeviceBase*,void*), void *arg)
{
    LONG              lTemp = 0;
    CtiDeviceBase*    pTempCtiDevice = NULL;

    while( (setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        rdr["deviceid"] >> lTemp;            // get the DeviceID
        CtiHashKey key(lTemp);

        if( Map.entries() > 0 && ((pTempCtiDevice = Map.findValue(&key)) != NULL) )
        {
            /*
             *  The point just returned from the rdr already was in my list.  We need to
             *  update my list entry to the new settings!
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

                if(((*testFunc)(pSp, arg)))            // If I care about this point in the db in question....
                {
                    pSp->setUpdatedFlag();               // Mark it updated
                    Map.insert( new CtiHashKey(pSp->getID()), pSp ); // Stuff it in the list
                }
                else
                {
                    delete pSp;                         // I don't want it!
                }
            }
        }
    }
}

void CtiDeviceManager::RefreshScanRates()
{
    LONG        lTemp = 0;
    CtiDeviceBase*   pTempCtiDevice = NULL;

    LockGuard  dev_guard(monitor());       // Protect our iteration!

    CtiRTDBIterator   itr(Map);

    RWDBConnection conn = getConnection();
    RWLockGuard<RWDBConnection> conn_guard(conn);
    RWDBDatabase db = getDatabase();

    RWDBTable   keyTable;

    RWDBSelector selector = db.selector();

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Looking for ScanRates" << endl;
    }
    CtiTableDeviceScanRate::getSQL( db, keyTable, selector );

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
        for(;itr();)
        {
            pTempCtiDevice = itr.value();
            pTempCtiDevice->invalidateScanRates();     // Mark all Scan Rate elements as needing refresh..
        }
    }

    while( (setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        CtiDeviceBase* pSp = NULL;

        rdr["deviceid"] >> lTemp;            // get the DeviceID
        CtiHashKey key(lTemp);

        if( Map.entries() > 0 && ((pTempCtiDevice = Map.findValue(&key)) != NULL) )
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

void CtiDeviceManager::RefreshDeviceWindows()
{
   LONG        lTemp = 0;
   CtiDeviceBase*   pTempCtiDevice = NULL;

   LockGuard  dev_guard(monitor());       // Protect our iteration!

   CtiRTDBIterator   itr(Map);

   RWDBConnection conn = getConnection();
   RWLockGuard<RWDBConnection> conn_guard(conn);
   RWDBDatabase db = getDatabase();

   RWDBTable   keyTable;

   RWDBSelector selector = db.selector();

   if(DebugLevel & 0x00020000)
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << RWTime() << " Looking for Device Windows" << endl;
   }
   CtiTableDeviceWindow::getSQL( db, keyTable, selector );
   if(DebugLevel & 0x00020000)
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << selector.asString() << endl;
   }

   RWDBReader rdr = selector.reader(conn);

   if(DebugLevel & 0x00020000)
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << RWTime() << " Done looking for ScanWindows" << endl;
   }

   while( (setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
   {
      CtiDeviceBase* pSp = NULL;

      rdr["deviceid"] >> lTemp;            // get the DeviceID
      CtiHashKey key(lTemp);

      if( Map.entries() > 0 && ((pTempCtiDevice = Map.findValue(&key)) != NULL) )
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
}

void CtiDeviceManager::RefreshStatistics()
{
    LONG        lTemp = 0;
    CtiDeviceBase*   pTempCtiDevice = NULL;

    LockGuard  dev_guard(monitor());       // Protect our iteration!

    RWDBConnection conn = getConnection();
    RWLockGuard<RWDBConnection> conn_guard(conn);
    RWDBDatabase db = getDatabase();
    RWDBTable   keyTable;

    RWDBSelector selector = db.selector();

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Looking for Statistics" << endl;
    }
    CtiTableDeviceStatistics::getSQL( db, keyTable, selector );

    RWDBReader rdr = selector.reader(conn);
    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
    }

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Done looking for Statistics" << endl;
    }

    while( rdr() )
    {
        CtiDeviceBase* pSp = NULL;

        rdr["deviceid"] >> lTemp;            // get the DeviceID
        CtiHashKey key(lTemp);

        if( Map.entries() > 0 && ((pTempCtiDevice = Map.findValue(&key)) != NULL) )
        {
            if( pTempCtiDevice->isSingle() )
            {
                /*
                 *  The point just returned from the rdr already was in my list, and is a
                 *  scannable device....  We need to
                 *  update the list entry with the scan rates!
                 */

                ((CtiDeviceSingle*)pTempCtiDevice)->DecodeStatisticsDatabaseReader(rdr);        // Fills himself in from the reader
            }
        }
    }
}

void CtiDeviceManager::RefreshRoutes()
{
    LONG        lTemp = 0;
    CtiDeviceBase*   pTempCtiDevice = NULL;

    LockGuard  dev_guard(monitor());       // Protect our iteration!

    RWDBConnection conn = getConnection();
    RWLockGuard<RWDBConnection> conn_guard(conn);
    RWDBDatabase db = getDatabase();
    RWDBTable   keyTable;

    RWDBSelector selector = db.selector();

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Looking for Routes" << endl;
    }
    CtiTableDeviceRoute::getSQL( db, keyTable, selector );

    RWDBReader rdr = selector.reader(conn);
    if(DebugLevel & 0x00020000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
    }

    while( (setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        rdr["deviceid"] >> lTemp;            // get the DeviceID
        CtiHashKey key(lTemp);

        if( Map.entries() > 0 && ((pTempCtiDevice = Map.findValue(&key)) != NULL) )
        {
            /*
             *  The point just returned from the rdr already was in my list.  We need to
             *  update my list entry to the new limit settings!
             */

            ((CtiDeviceDLCBase*)pTempCtiDevice)->DecodeRoutesDatabaseReader(rdr);        // Fills himself in from the reader
        }
    }

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Done looking for Routes" << endl;
    }
}


CtiDeviceBase* CtiDeviceManager::RemoteGetPortRemoteEqual (LONG Port, LONG Remote)
{
    CtiDeviceBase     *p = NULL;

    RWRecursiveLock<RWMutexLock>::LockGuard  dev_guard(getMux());

    CtiRTDBIterator   itr(Map);


    if(Map.entries() == 0)
    {
        cerr << "There are no entries in the remote device list" << endl;
    }

    for(;itr();)
    {
        p = itr.value();

        if( p->getAddress() > 0 &&  p->getPortID() == Port && p->getAddress() == Remote )
        {
            // cout << "Found Port " << Port << " Remote " << Remote << endl;
            break;
        }
        else
        {
            p = NULL;
        }
    }

    return p;
}

CtiDeviceBase* CtiDeviceManager::RemoteGetEqual (LONG Dev)
{
    CtiHashKey key(Dev);
    return Map.findValue(&key);

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

CtiDeviceManager::CtiDeviceManager() {}
CtiDeviceManager::~CtiDeviceManager() {}

void CtiDeviceManager::DeleteList(void)   { Map.clearAndDestroy();}
