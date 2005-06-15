/*-----------------------------------------------------------------------------*
*
* File:   mgr_route
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_route.cpp-arc  $
* REVISION     :  $Revision: 1.20 $
* DATE         :  $Date: 2005/06/15 19:19:38 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <rw/db/db.h>

#include "mgr_route.h"
#include "rte_xcu.h"
#include "rte_ccu.h"
#include "rte_versacom.h"
#include "rte_macro.h"

#include "tbl_rtversacom.h"
#include "tbl_rtmacro.h"

#include "dbaccess.h"

CtiRouteManager::CtiRouteManager() {}

extern void cleanupDB();

CtiRouteManager::~CtiRouteManager()
{
    // cleanupDB();  // Deallocate all the DB stuff.
}


void CtiRouteManager::DeleteList(void)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}



inline RWBoolean isRouteIdStaticId(CtiRouteBase *pRoute, void* d)
{
    CtiRouteBase *pSp = (CtiRouteBase *)d;

    return(pRoute->getRouteID() == pSp->getRouteID());
}

inline RWBoolean  isRouteNotUpdated(CtiRouteBase *pRoute, void* d)
{
    // Return TRUE if it is NOT SET
    return(RWBoolean(!pRoute->getUpdatedFlag()));
}


void ApplyRouteResetUpdated(const long key, CtiRouteSPtr pRoute, void* d)
{
    pRoute->resetUpdatedFlag();

    if(pRoute->getType() == RouteTypeMacro)
    {
        CtiRouteMacro* pMacro = (CtiRouteMacro*)pRoute.get();
        pMacro->getRouteList().clear();   // We will refill this one as we go!
    }
    if(pRoute->getType() == RouteTypeCCU)
    {
        CtiRouteCCU* pCCU = (CtiRouteCCU*)pRoute.get();
        pCCU->getRepeaterList().clear();  //  ditto
    }

    return;
}

void ApplyRepeaterSort(const long key, CtiRouteSPtr pRoute, void* d)
{
    if(pRoute->getType() == RouteTypeCCU)  //  used to be RouteTypeRepeater
    {
        CtiRouteCCU* pCCU = (CtiRouteCCU*)pRoute.get();
        pCCU->getRepeaterList().sort();
    }
    return;
}

void ApplyMacroRouteSort(const long key, CtiRouteSPtr pRoute, void* d)
{
    if(pRoute->getType() == RouteTypeMacro)
    {
        CtiRouteMacro* pMacro = (CtiRouteMacro*)pRoute.get();
        pMacro->getRouteList().sort();
    }
    return;
}

void ApplyInvalidateNotUpdated(const long key, CtiRouteSPtr pPt, void* d)
{
    if(!pPt->getUpdatedFlag())
    {
        pPt->setValid(FALSE);   //   NOT NOT NOT Valid
    }
    return;
}

void CtiRouteManager::RefreshList(CtiRouteBase* (*Factory)(RWDBReader &), BOOL (*testFunc)(CtiRouteBase*,void*), void *arg)
{
    ptr_type pTempCtiRoute;
    bool rowFound = false;

    try
    {
        {
            // Reset everyone's Updated flag.
            if(!_smartMap.empty())
            {
                apply(ApplyRouteResetUpdated, NULL);
            }

            _smartMap.resetErrorCode();

            /*
             *  093099 CGP: Look for items starting at the bottom of the inheritance hierarchy.
             *  The way the selects are, we should get an entry fro EACH route in the MAIN route
             *  table based upon the setting in the type field.
             *
             *  The items not collected in the first call are collected in subsequent calls
             *  at the table level.. i.e. CtiTableVersacomRoute::getSQLColumns(column) ....
             *
             *  This allows the data to be collected in just four calls to the SQL DB.
             */
            {
                // Make sure all objects that that store results
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();
                // are out of scope when the release is called
                RWDBDatabase db = conn.database();
                RWDBSelector selector = conn.database().selector();

                RWDBTable   keyTable;

                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for CCU, LCU, TCU, & CCURPT Routes" << endl;
                }
                CtiRouteCCU().getSQL( db, keyTable, selector );
                selector.where( rwdbUpper(keyTable["category"]) == RWDBExpr("ROUTE") && selector.where());
                RWDBReader  rdr = selector.reader(conn);
                if(DebugLevel & 0x00040000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }
                RefreshRoutes(rowFound, rdr, Factory, testFunc, arg);
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for CCU, LCU, TCU, & CCURPT Routes" << endl;
                }
            }

            {
                // Make sure all objects that that store results
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();
                // are out of scope when the release is called
                RWDBDatabase db = conn.database();
                RWDBSelector selector = conn.database().selector();

                RWDBTable   keyTable;

                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Versacom Routes" << endl;
                }
                CtiTableVersacomRoute::getSQL( db, keyTable, selector );
                RWDBReader  rdr = selector.reader(conn);
                if(DebugLevel & 0x00040000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }
                RefreshVersacomRoutes(rowFound, rdr, testFunc, arg);
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Versacom Routes" << endl;
                }
            }

            {
                // Make sure all objects that that store results
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();
                // are out of scope when the release is called
                RWDBDatabase db = conn.database();
                RWDBSelector selector = conn.database().selector();

                RWDBTable   keyTable;

                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Repeater Information" << endl;
                }
                CtiTableRepeaterRoute::getSQL( db, keyTable, selector );
                RWDBReader  rdr = selector.reader(conn);
                if(DebugLevel & 0x00040000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }
                RefreshRepeaterRoutes(rowFound, rdr, testFunc, arg);
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Repeater Information" << endl;
                }
            }

            {
                // Make sure all objects that that store results
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();
                // are out of scope when the release is called
                RWDBDatabase db = conn.database();
                RWDBSelector selector = conn.database().selector();

                RWDBTable   keyTable;
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Macro Routes" << endl;
                }
                CtiTableMacroRoute::getSQL( db, keyTable, selector );
                RWDBReader  rdr = selector.reader(conn);
                if(DebugLevel & 0x00040000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }
                RefreshMacroRoutes(rowFound, rdr, testFunc, arg);
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Macro Routes" << endl;
                }
            }

            if(_smartMap.getErrorCode() != RWDBStatus::ok)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " database had a return code of " << _smartMap.getErrorCode() << endl;
                }
            }
            else
            {
                if(rowFound)
                {
                    // Now I need to check for any Route removals based upon the
                    // Updated Flag being NOT set

                    apply(ApplyInvalidateNotUpdated, NULL);
                }
            }

        }   // Temporary results are destroyed to free the connection
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}


void CtiRouteManager::RefreshRoutes(bool &rowFound, RWDBReader& rdr, CtiRouteBase* (*Factory)(RWDBReader &), BOOL (*testFunc)(CtiRouteBase*,void*), void *arg)
{
    LONG lTemp = 0;
    ptr_type pTempCtiRoute;

    while( (_smartMap.setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        rowFound = true;
        rdr["paobjectid"] >> lTemp;            // get the RouteID

        if( !_smartMap.empty() && ((pTempCtiRoute = _smartMap.find(lTemp))) )
        {
            /*
             *  The point just returned from the rdr already was in my list.  We need to
             *  update my list entry to the CTIDBG_new settings!
             */

            pTempCtiRoute->DecodeDatabaseReader(rdr);  // Fills himself in from the reader
            pTempCtiRoute->setUpdatedFlag();           // Mark it updated
        }
        else
        {
            CtiRouteBase* pSp = (*Factory)(rdr);  // Use the reader to get me an object of the proper type

            if(pSp)
            {
                pSp->DecodeDatabaseReader(rdr);        // Fills himself in from the reader

                if(((*testFunc)(pSp, arg)))            // If I care about this point in the db in question....
                {
                    pSp->setUpdatedFlag();             // Mark it updated
                    _smartMap.insert( pSp->getRouteID(), pSp );
                }
                else
                {
                    delete pSp;                         // I don't want it!
                }
            }
        }
    }
}

void CtiRouteManager::RefreshRepeaterRoutes(bool &rowFound, RWDBReader& rdr, BOOL (*testFunc)(CtiRouteBase*,void*), void *arg)
{
    LONG lTemp = 0;
    ptr_type pTempCtiRoute;

    while( (_smartMap.setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        rowFound = true;

        rdr["routeid"] >> lTemp;            // get the RouteID

        if( !_smartMap.empty() && ((pTempCtiRoute = _smartMap.find(lTemp))) )
        {
            if(pTempCtiRoute->getType() == RouteTypeCCU)  //  used to be RouteTypeRepeater
            {
                /*
                 *  The route just returned from the rdr already was in my list.  We need to
                 *  add this repeater to the route entry... It had better be a repeater route.
                 */

                CtiRouteCCU *pCCU = (CtiRouteCCU*)pTempCtiRoute.get();
                pCCU->DecodeRepeaterDatabaseReader(rdr);        // Fills himself in from the reader
            }
        }
    }

    // Sort all the repeater listings based on repeater order
    apply(ApplyRepeaterSort, NULL);
}

void CtiRouteManager::RefreshMacroRoutes(bool &rowFound, RWDBReader& rdr, BOOL (*testFunc)(CtiRouteBase*,void*), void *arg)
{
    LONG lTemp = 0;
    ptr_type pTempCtiRoute;

    while( (_smartMap.setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        rowFound = true;

        rdr["routeid"] >> lTemp;            // get the RouteID

        if( !_smartMap.empty() && ((pTempCtiRoute = _smartMap.find(lTemp))) )
        {
            if(pTempCtiRoute->getType() == RouteTypeMacro)
            {
                /*
                 *  The route just returned from the rdr already was in my list.  We need to
                 *  add this route to the macro route entry... It had better be a macro route.
                 */

                CtiRouteMacro *pMacro = (CtiRouteMacro*)pTempCtiRoute.get();
                pMacro->DecodeMacroReader(rdr);        // Fills himself in from the reader
            }
        }
    }

    apply(ApplyMacroRouteSort, NULL);
}

void CtiRouteManager::RefreshVersacomRoutes(bool &rowFound, RWDBReader& rdr, BOOL (*testFunc)(CtiRouteBase*,void*), void *arg)
{
    LONG        lTemp = 0;
    ptr_type    pTempCtiRoute;

    while( (_smartMap.setErrorCode(rdr.status().errorCode()) == RWDBStatus::ok) && rdr() )
    {
        rowFound = true;
        CtiRouteBase* pSp = NULL;

        rdr["routeid"] >> lTemp; // get the RouteID

        if( !_smartMap.empty() && (pTempCtiRoute = _smartMap.find(lTemp)) )
        {
            if(pTempCtiRoute->getType() == RouteTypeVersacom)
            {
                /*
                 *  The route just returned from the rdr already was in my list.  We need to
                 *  add this versacom data to the route entry... It had better be a versacom route.
                 */

                CtiRouteVersacom *pVersacom = (CtiRouteVersacom*)pTempCtiRoute.get();
                pVersacom->DecodeVersacomDatabaseReader(rdr);        // Fills himself in from the reader
            }
        }
    }
}

CtiRouteManager::ptr_type CtiRouteManager::getEqual( LONG Rte )
{
    ptr_type p;
    try
    {
        p = _smartMap.find(Rte);
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



CtiRouteManager::ptr_type CtiRouteManager::getEqualByName( RWCString &rname )
{
    ptr_type p;
    RWCString tmpName;
    rname.toLower();

    spiterator itr;

    //  go through the list looking for a matching name
    for( itr = begin(); itr != end(); itr++)
    {
        tmpName = itr->second->getName();
        tmpName.toLower();

        if( tmpName == rname )
        {
            p = itr->second;
            break;  //  look, ma, this makes us O(n/2) instead of O(n) - ha ha.
        }
    }

    return p;
}


void CtiRouteManager::DumpList(void)
{
    try
    {
        spiterator itr;

        for(itr = begin(); itr != end(); itr++)
        {
            itr->second->DumpData();
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

void CtiRouteManager::apply(void (*applyFun)(const long, ptr_type, void*), void* d)
{
    try
    {
        _smartMap.apply(applyFun,d);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

bool CtiRouteManager::empty() const
{
    return _smartMap.empty();
}

CtiRouteManager::spiterator CtiRouteManager::begin()
{
    return _smartMap.getMap().begin();
}
CtiRouteManager::spiterator CtiRouteManager::end()
{
    return _smartMap.getMap().end();
}

CtiRouteManager::spiterator CtiRouteManager::nextPos(CtiRouteManager::spiterator &my_itr)
{
    return my_itr++;
}

bool CtiRouteManager::buildRoleVector( long id, CtiRequestMsg& Req, RWTPtrSlist< CtiMessage > &retList, vector< CtiDeviceRepeaterRole > & roleVector )
{
    CtiLockGuard< CtiMutex > guard(_smartMap.getMux());
    spiterator itr_rte;

    int rolenum = 1;
    int stagestofollow;
    bool foundit;

    for(itr_rte = begin(); itr_rte != end(); nextPos(itr_rte))
    {
        foundit = false;
        stagestofollow = 0;

        CtiRouteSPtr route = itr_rte->second;

        if(route && route->getType() == RouteTypeCCU)
        {
            CtiRouteCCU *ccuroute = (CtiRouteCCU*)route.get();

            if( ccuroute->getRepeaterList().entries() > 0 )
            {
                // This CCURoute has repeater entries.
                if(ccuroute->getCarrier().getCCUVarBits() == 7)
                {
                    RWCString resStr = "*** WARNING *** " + ccuroute->getName() + " Has CCU variable bits set to 7 AND has repeaters. ";
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << resStr << endl;
                        dout << "  It will be skipped. for role generation." << endl;
                    }

                    retList.insert( CTIDBG_new CtiReturnMsg( Req.DeviceId(),
                                                      Req.CommandString(),
                                                      resStr,
                                                      BADPARAM,
                                                      Req.RouteId(),
                                                      Req.MacroOffset(),
                                                      Req.AttemptNum(),
                                                      Req.TransmissionId(),
                                                      Req.UserMessageId()) );

                    continue;
                }
                else
                {
                    int i;
                    CtiDeviceRepeaterRole role;

                    for(i = 0; i < ccuroute->getRepeaterList().length(); i++)
                    {

                        role.setRouteID( ccuroute->getRouteID() );
                        role.setRoleNumber( rolenum++ );

                        if(!foundit)
                        {
                            if( ccuroute->getRepeaterList()[i].getDeviceID() == id )   // Is our repeater in there?
                            {
                                foundit = true;

                                if(i == 0)
                                {
                                    // Use the route to build up the Role object
                                    role.setOutBits( ccuroute->getCarrier().getCCUVarBits() );
                                }
                                else
                                {
                                    // Use the previous repeater to build up the role object.
                                    role.setOutBits( ccuroute->getRepeaterList()[i-1].getVarBit() );
                                }
                                role.setFixBits(ccuroute->getCarrier().getCCUFixBits());
                                role.setInBits(ccuroute->getRepeaterList()[i].getVarBit());

                                if(ccuroute->getRepeaterList()[i].getVarBit() == 7)
                                {
                                    break;      // The for... It is kaput!
                                }
                            }
                        }
                        else
                        {
                            // We have found it in this route.  We need to count the additional stages in the route.
                            stagestofollow++;
                            if(ccuroute->getRepeaterList()[i].getVarBit() == 7)
                            {
                                break;      // The for... no more routes!
                            }
                        }
                    }

                    role.setStages(stagestofollow);

                    if(foundit)
                    {
                        roleVector.push_back( role );
                    }
                }
            }
        }
    }

    return !roleVector.empty();
}
