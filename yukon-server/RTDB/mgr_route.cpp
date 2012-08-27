/*-----------------------------------------------------------------------------*
*
* File:   mgr_route
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_route.cpp-arc  $
* REVISION     :  $Revision: 1.26 $
* DATE         :  $Date: 2008/08/14 15:57:40 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"



#include "mgr_route.h"
#include "rte_xcu.h"
#include "rte_ccu.h"
#include "rte_versacom.h"
#include "rte_expresscom.h"
#include "rte_macro.h"

#include "tbl_rtversacom.h"
#include "tbl_rtmacro.h"

#include "dbaccess.h"
#include "database_connection.h"
#include "database_reader.h"

#include <sstream>

using std::string;
using std::endl;
using std::list;
using std::vector;
using std::ostringstream;


CtiRouteManager::CtiRouteManager() {}

CtiRouteManager::~CtiRouteManager()
{
    // cleanupDB();  // Deallocate all the DB stuff.
}


void CtiRouteManager::DeleteList(void)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    if(pRoute->getType() == RouteTypeCCU)
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

void CtiRouteManager::RefreshList(CtiRouteBase* (*Factory)(Cti::RowReader &), BOOL (*testFunc)(CtiRouteBase*,void*), void *arg)
{
    ptr_type pTempCtiRoute;
    bool rowFound = false;

    try
    {
        {
            // Reset everyone's Updated flag and clear out all Macro and Repeater route entries.
            if(!_smartMap.empty())
            {
                apply(ApplyRouteResetUpdated, NULL);

                //  keep a copy of what our routes' repeaters used to be so we can
                //    clear the relevant roles from any repeaters that have been removed from routes
                _routeRepeaters.previous = _routeRepeaters.current;
                _routeRepeaters.current.clear();
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
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for CCU, LCU, TCU, & CCURPT Routes" << endl;
                }

                string sql = CtiRouteCCU::getSQLCoreStatement();

                sql += " WHERE upper (YP.category) = 'ROUTE'";

                Cti::Database::DatabaseConnection connection;
                Cti::Database::DatabaseReader rdr(connection,  sql);

                rdr.execute();

                if(DebugLevel & 0x00040000 || !rdr.isValid())
                {
                    string loggedSQLstring = rdr.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << loggedSQLstring << endl;
                    }
                }
                RefreshRoutes(rowFound, rdr, Factory, testFunc, arg);
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for CCU, LCU, TCU, & CCURPT Routes" << endl;
                }
            }

            {
                // Make sure all objects that that store results
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Versacom Routes" << endl;
                }

                static const string sql = CtiTableVersacomRoute::getSQLCoreStatement();

                Cti::Database::DatabaseConnection connection;
                Cti::Database::DatabaseReader rdr(connection, sql);
                rdr.execute();
                if(DebugLevel & 0x00040000 || !rdr.isValid())
                {
                    string loggedSQLstring = rdr.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << loggedSQLstring << endl;
                    }
                }
                RefreshVersacomRoutes(rowFound, rdr, testFunc, arg);
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Versacom Routes" << endl;
                }
            }

            {
                // Make sure all objects that that store results
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Repeater Information" << endl;
                }

                static const string sql = CtiTableRepeaterRoute::getSQLCoreStatement();
                Cti::Database::DatabaseConnection connection;
                Cti::Database::DatabaseReader rdr(connection, sql);
                rdr.execute();
                if(DebugLevel & 0x00040000 || !rdr.isValid())
                {
                    string loggedSQLstring = rdr.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << loggedSQLstring << endl;
                    }
                }
                RefreshRepeaterRoutes(rowFound, rdr, testFunc, arg);
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Repeater Information" << endl;
                }
            }

            {
                // Make sure all objects that that store results
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Macro Routes" << endl;
                }

                static const string sql = CtiTableMacroRoute::getSQLCoreStatement();

                Cti::Database::DatabaseConnection connection;
                Cti::Database::DatabaseReader rdr(connection, sql);
                rdr.execute();
                if(DebugLevel & 0x00040000 || !rdr.isValid())
                {
                    string loggedSQLstring = rdr.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << loggedSQLstring << endl;
                    }
                }
                RefreshMacroRoutes(rowFound, rdr, testFunc, arg);
                if(DebugLevel & 0x00040000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Macro Routes" << endl;
                }
            }

            if(rowFound)
            {
                // Now I need to check for any Route removals based upon the
                // Updated Flag being NOT set

                apply(ApplyInvalidateNotUpdated, NULL);
            }

            {   // Load the static PAO info for any route in the smart map

                Cti::Database::id_set   paoids;
                {
                    CtiSmartMap<CtiRouteBase>::reader_lock_guard_t  guard( _smartMap.getLock() );

                    for ( spiterator sp = begin(); sp != end() ; ++sp )
                    {
                        paoids.insert(sp->first);
                    }
                }
                refreshStaticPaoInfo( paoids );

                // Load encryption keys for any routes that may have one

                refreshRouteEncryptionKeys( paoids );
            }

        }   // Temporary results are destroyed to free the connection
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}


void CtiRouteManager::RefreshRoutes(bool &rowFound, Cti::RowReader& rdr, CtiRouteBase* (*Factory)(Cti::RowReader &), BOOL (*testFunc)(CtiRouteBase*,void*), void *arg)
{
    LONG lTemp = 0;
    ptr_type pTempCtiRoute;

    while( (_smartMap.setErrorCode(rdr.isValid() ? 0 : 1) == 0) && rdr() )
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

void CtiRouteManager::RefreshRepeaterRoutes(bool &rowFound, Cti::RowReader& rdr, BOOL (*testFunc)(CtiRouteBase*,void*), void *arg)
{
    while( (_smartMap.setErrorCode(rdr.isValid() ? 0 : 1) == 0) && rdr() )
    {
        rowFound = true;

        const CtiTableRepeaterRoute Rpt(rdr);

        if( ptr_type pTempCtiRoute = _smartMap.find(Rpt.getRouteID()) )
        {
            if(pTempCtiRoute->getType() == RouteTypeCCU)
            {
                CtiRouteCCU *pCCU = (CtiRouteCCU*)pTempCtiRoute.get();
                pCCU->addRepeater(Rpt);

                const route_repeater_relation relation(Rpt.getRouteID(), Rpt.getDeviceID());

                _routeRepeaters.current.insert(relation);
            }
        }
    }

    // Sort all the repeater listings based on repeater order
    apply(ApplyRepeaterSort, NULL);
}


bool CtiRouteManager::isRepeaterRelevantToRoute(long repeater_id, long route_id) const
{
    const route_repeater_relation relation(route_id, repeater_id);

    //  was this repeater ever associated with this route?
    return _routeRepeaters.current.count(relation) ||
           _routeRepeaters.previous.count(relation);
}


void CtiRouteManager::RefreshMacroRoutes(bool &rowFound, Cti::RowReader& rdr, BOOL (*testFunc)(CtiRouteBase*,void*), void *arg)
{
    LONG lTemp = 0;
    ptr_type pTempCtiRoute;

    while( (_smartMap.setErrorCode(rdr.isValid() ? 0 : 1) == 0) && rdr() )
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

void CtiRouteManager::RefreshVersacomRoutes(bool &rowFound, Cti::RowReader& rdr, BOOL (*testFunc)(CtiRouteBase*,void*), void *arg)
{
    LONG        lTemp = 0;
    ptr_type    pTempCtiRoute;

    while( (_smartMap.setErrorCode(rdr.isValid() ? 0 : 1) == 0) && rdr() )
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
            else if(pTempCtiRoute->getType() == RouteTypeExpresscom)
            {
                CtiRouteExpresscom *pExpresscom = (CtiRouteExpresscom*)pTempCtiRoute.get();
                pExpresscom->DecodeVersacomDatabaseReader(rdr);        // Fills himself in from the reader
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
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return p;
}



CtiRouteManager::ptr_type CtiRouteManager::getEqualByName( string &rname )
{
    ptr_type p;
    string tmpName;
    std::transform(rname.begin(), rname.end(), rname.begin(), tolower);


    spiterator itr;

    //  go through the list looking for a matching name
    for( itr = begin(); itr != end(); itr++)
    {
        tmpName = itr->second->getName();
        std::transform(tmpName.begin(), tmpName.end(), tmpName.begin(), tolower);

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
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

bool CtiRouteManager::buildRoleVector( long id, CtiRequestMsg& Req, list< CtiMessage* > &retList, vector< CtiDeviceRepeaterRole > & roleVector )
{
    coll_type::reader_lock_guard_t guard(getLock());
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
                if(ccuroute->getCCUVarBits() == 7)
                {
                    string resStr = "*** WARNING *** " + ccuroute->getName() + " Has CCU variable bits set to 7 AND has repeaters. ";
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << resStr << endl;
                        dout << "  It will be skipped. for role generation." << endl;
                    }

                    retList.push_back( CTIDBG_new CtiReturnMsg( Req.DeviceId(),
                                                      Req.CommandString(),
                                                      resStr,
                                                      BADPARAM,
                                                      Req.RouteId(),
                                                      Req.MacroOffset(),
                                                      Req.AttemptNum(),
                                                      Req.GroupMessageId(),
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
                                    role.setOutBits( ccuroute->getCCUVarBits() );
                                }
                                else
                                {
                                    // Use the previous repeater to build up the role object.
                                    role.setOutBits( ccuroute->getRepeaterList()[i-1].getVarBit() );
                                }
                                role.setFixBits(ccuroute->getCCUFixBits());
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


void CtiRouteManager::refreshStaticPaoInfo(const Cti::Database::id_set &paoids)
{
    CtiRouteManager::ptr_type route;

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

                route = getEqual(tmp_paobjectid);

                if( route )
                {
                    route->setStaticInfo(static_paoinfo);
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

string CtiRouteManager::createIdSqlClause(const Cti::Database::id_set &paoids, const string table, const string attrib)
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


void CtiRouteManager::refreshRouteEncryptionKeys( const Cti::Database::id_set & paoids )
{
    if ( paoids.empty() )   // no routes to load
    {
        return;
    }

    if ( DebugLevel & 0x00020000 )
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << CtiTime() << "Loading Route Encryption Keys." << std::endl;
    }

    std::string sql =   "SELECT Y.PAObjectID, E.Name, E.Value "
                        "FROM   EncryptionKey E JOIN YukonPAObjectEncryptionKey Y ON E.EncryptionKeyId = Y.EncryptionKeyId "
                        "WHERE " + createIdSqlClause( paoids, "Y", "PAObjectID" );

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseReader       rdr( connection, sql );

    rdr.execute();

    if ( DebugLevel & 0x00020000 || !rdr.isValid() )
    {
        std::string loggedSQLstring = rdr.asString();
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << loggedSQLstring << std::endl;
        }
    }

    if ( rdr.isValid() )
    {
        while ( rdr() )
        {
            long        paoId;
            std::string name,
                        encryptedKey;

            rdr[ "PAObjectID" ] >> paoId;
            rdr[ "Name" ]       >> name;
            rdr[ "Value" ]      >> encryptedKey;

            CtiRouteManager::ptr_type route = getEqual( paoId );

            if ( route )
            {
                Cti::Encryption::Buffer encrypted,
                                        decrypted;

                convertHexStringToBytes( encryptedKey, encrypted );

                try
                {
                    decrypted = Cti::Encryption::decrypt( Cti::Encryption::OneWayMsgEncryptionKey, encrypted );
                    route->setEncryptionKey( decrypted );
                }
                catch ( Cti::Encryption::Error e )
                {
                    CtiLockGuard< CtiLogger > doubt_guard( dout );
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")\n"
                         << "Error decrypting Encryption Key named: " << name
                         << "\nException caught: " << e.what() << std::endl;
                }
            }
            else
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")\n"
                     << "No Route found for Encryption Key named: " << name <<  std::endl;
            }
        }
    }
    else
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")\n"
             << "Error reading Route Encryption Keys from database." << std::endl;
    }

    if ( DebugLevel & 0x00020000 )
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << CtiTime() << "Done loading Route Encryption Keys." << std::endl;
    }
}
