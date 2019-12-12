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
#include "database_util.h"
#include "coroutine_util.h"

#include <sstream>

using std::string;
using std::endl;
using std::list;
using std::vector;
using std::ostringstream;


void ApplyRouteResetUpdated(const long key, CtiRouteSPtr pRoute, void* d)
{
    pRoute->resetUpdatedFlag();

    if(pRoute->getType() == RouteTypeMacro)
    {
        Cti::Routes::MacroRoute* pMacro = (Cti::Routes::MacroRoute*)pRoute.get();
        pMacro->clearSubroutes();   // We will refill this one as we go!
    }
    if(pRoute->getType() == RouteTypeCCU)
    {
        CtiRouteCCU* pCCU = (CtiRouteCCU*)pRoute.get();
        pCCU->clearRepeaters();  //  ditto
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

void CtiRouteManager::RefreshList(CtiRouteBase* (*Factory)(Cti::RowReader &))
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
                    CTILOG_DEBUG(dout, "Looking for CCU, LCU, TCU, & CCURPT Routes");
                }

                string sql = CtiRouteCCU::getSQLCoreStatement();

                sql += " WHERE upper (YP.category) = 'ROUTE'";

                Cti::Database::DatabaseConnection connection;
                Cti::Database::DatabaseReader rdr(connection,  sql);

                rdr.execute();

                if( ! rdr.isValid() )
                {
                    CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
                }
                else if( DebugLevel & 0x00040000 )
                {
                    CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
                }

                RefreshRoutes(rowFound, rdr, Factory);
                if(DebugLevel & 0x00040000)
                {
                    CTILOG_DEBUG(dout, "Done looking for CCU, LCU, TCU, & CCURPT Routes");
                }
            }

            {
                // Make sure all objects that that store results
                if(DebugLevel & 0x00040000)
                {
                    CTILOG_DEBUG(dout, "Looking for Versacom Routes");
                }

                static const string sql = CtiTableVersacomRoute::getSQLCoreStatement();

                Cti::Database::DatabaseConnection connection;
                Cti::Database::DatabaseReader rdr(connection, sql);
                rdr.execute();

                if( ! rdr.isValid() )
                {
                    CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
                }
                else if( DebugLevel & 0x00040000 )
                {
                    CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
                }

                RefreshVersacomRoutes(rowFound, rdr);
                if(DebugLevel & 0x00040000)
                {
                    CTILOG_DEBUG(dout, "Done looking for Versacom Routes");
                }
            }

            {
                // Make sure all objects that that store results
                if(DebugLevel & 0x00040000)
                {
                    CTILOG_DEBUG(dout, "Looking for Repeater Information");
                }

                static const string sql = CtiTableRepeaterRoute::getSQLCoreStatement();
                Cti::Database::DatabaseConnection connection;
                Cti::Database::DatabaseReader rdr(connection, sql);
                rdr.execute();

                if( ! rdr.isValid() )
                {
                    CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
                }
                else if( DebugLevel & 0x00040000 )
                {
                    CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
                }

                RefreshRepeaterRoutes(rowFound, rdr);
                if(DebugLevel & 0x00040000)
                {
                    CTILOG_DEBUG(dout, "Done looking for Repeater Information");
                }
            }

            {
                // Make sure all objects that that store results
                if(DebugLevel & 0x00040000)
                {
                    CTILOG_DEBUG(dout, "Looking for Macro Routes");
                }

                static const string sql = Cti::Tables::MacroRouteTable::getSQLCoreStatement();

                Cti::Database::DatabaseConnection connection;
                Cti::Database::DatabaseReader rdr(connection, sql);
                rdr.execute();

                if( ! rdr.isValid() )
                {
                    CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
                }
                else if( DebugLevel & 0x00040000 )
                {
                    CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
                }

                RefreshMacroRoutes(rowFound, rdr);
                if(DebugLevel & 0x00040000)
                {
                    CTILOG_DEBUG(dout, "Done looking for Macro Routes");
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
                {   // chunk collection so as not to break the reader
                    unsigned long max_size = gConfigParms.getValueAsULong("MAX_IDS_PER_SELECT", 950);

                    for( const auto& id_chunk : Cti::Coroutines::chunked(paoids, max_size) )
                    {
                        std::set<long> idSubset{ id_chunk.begin(), id_chunk.end() };

                        refreshStaticPaoInfo( idSubset );

                        // Load encryption keys for any routes that may have one

                        refreshRouteEncryptionKeys( idSubset );
                    }
                }
            }

        }   // Temporary results are destroyed to free the connection
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}


void CtiRouteManager::RefreshRoutes(bool &rowFound, Cti::RowReader& rdr, CtiRouteBase* (*Factory)(Cti::RowReader &))
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

                pSp->setUpdatedFlag();             // Mark it updated
                _smartMap.insert( pSp->getRouteID(), pSp );
            }
        }
    }
}

void CtiRouteManager::RefreshRepeaterRoutes(bool &rowFound, Cti::RowReader& rdr)
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
}


bool CtiRouteManager::isRepeaterRelevantToRoute(long repeater_id, long route_id) const
{
    const route_repeater_relation relation(route_id, repeater_id);

    //  was this repeater ever associated with this route?
    return _routeRepeaters.current.count(relation) ||
           _routeRepeaters.previous.count(relation);
}


void CtiRouteManager::RefreshMacroRoutes(bool &rowFound, Cti::RowReader& rdr)
{
    while( (_smartMap.setErrorCode(rdr.isValid() ? 0 : 1) == 0) && rdr() )
    {
        rowFound = true;

        long rteid;
        rdr["routeid"] >> rteid;

        if( ptr_type rte = _smartMap.find(rteid) )
        {
            //  make sure it's a macro route
            if( rte->getType() == RouteTypeMacro )
            {
                static_cast<Cti::Routes::MacroRoute *>(rte.get())->DecodeSubrouteReader(rdr);
            }
        }
    }
}

void CtiRouteManager::RefreshVersacomRoutes(bool &rowFound, Cti::RowReader& rdr)
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

CtiRouteManager::ptr_type CtiRouteManager::getRouteById( LONG Rte )
{
    ptr_type p;
    try
    {
        p = _smartMap.find(Rte);
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return p;
}



CtiRouteManager::ptr_type CtiRouteManager::getRouteByName( string rname )
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


std::string CtiRouteManager::toString() const
{
    Cti::FormattedList itemList;
    itemList << "CtiRouteManager";
    try
    {
        unsigned index=0;
        const_spiterator itr;
        for(itr = begin(); itr != end(); itr++)
        {
            itemList <<"Route"<< ++index;
            itemList << *(itr->second);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return itemList.toString();
}

void CtiRouteManager::apply(void (*applyFun)(const long, ptr_type, void*), void* d)
{
    try
    {
        _smartMap.apply(applyFun,d);
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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

CtiRouteManager::const_spiterator CtiRouteManager::begin() const
{
    return _smartMap.getMap().begin();
}
CtiRouteManager::const_spiterator CtiRouteManager::end() const
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

    int rolenum = 1;

    for(spiterator itr_rte = begin(); itr_rte != end(); nextPos(itr_rte))
    {
        CtiRouteSPtr route = itr_rte->second;

        if( ! route || route->getType() != RouteTypeCCU )
        {
            continue;
        }

        CtiRouteCCU *ccuroute = (CtiRouteCCU*)route.get();

        if( ccuroute->getRepeaters().empty() )
        {
            continue;
        }

        // This CCURoute has repeater entries.
        if(ccuroute->getCCUVarBits() == 7)
        {
            string resStr = "*** WARNING *** " + ccuroute->getName() + " Has CCU variable bits set to 7 AND has repeaters. It will be skipped for role generation.";

            CTILOG_WARN( dout, resStr );

            auto retMsg = std::make_unique<CtiReturnMsg>(
                Req.DeviceId(),
                Req.CommandString(),
                resStr,
                ClientErrors::BadParameter,
                Req.RouteId(),
                Req.MacroOffset(),
                Req.AttemptNum(),
                Req.GroupMessageId(),
                Req.UserMessageId());

            retMsg->setExpectMore( true );

            retList.push_back( retMsg.release() );

            continue;
        }

        CtiDeviceRepeaterRole role;
        const CtiTableRepeaterRoute *prev_rte = 0;

        bool foundit = false;
        int stagestofollow = 0;
        for each( const CtiTableRepeaterRoute &rte in ccuroute->getRepeaters() )
        {
            role.setRouteID( ccuroute->getRouteID() );
            role.setRoleNumber( rolenum++ );

            if(!foundit)
            {
                if( rte.getDeviceID() == id )   // Is our repeater in there?
                {
                    foundit = true;

                    if( ! prev_rte )
                    {
                        // Use the route to build up the Role object
                        role.setOutBits( ccuroute->getCCUVarBits() );
                    }
                    else
                    {
                        // Use the previous repeater to build up the role object.
                        role.setOutBits( prev_rte->getVarBit() );
                    }
                    role.setFixBits(ccuroute->getCCUFixBits());
                    role.setInBits(rte.getVarBit());

                    if(rte.getVarBit() == 7)
                    {
                        break;      // The for... It is kaput!
                    }
                }
            }
            else
            {
                // We have found it in this route.  We need to count the additional stages in the route.
                stagestofollow++;
                if(rte.getVarBit() == 7)
                {
                    break;      // The for... no more routes!
                }
            }
            prev_rte = &rte;
        }

        if( foundit )
        {
            role.setStages(stagestofollow);

            roleVector.push_back( role );
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
            CTILOG_DEBUG(dout, "Looking for Static PAO Info");
        }

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection);

        string sql = CtiTableStaticPaoInfo::getSQLCoreStatement();

        if( !sql.empty() )
        {
            if(!paoids.empty())
            {
                sql += " AND " + Cti::Database::createIdInClause("SPI", "paobjectid", paoids.size());

                rdr.setCommandText(sql);

                rdr << paoids;
            }
            else
            {
                rdr.setCommandText(sql);
            }
            rdr.execute();
        }
        else
        {
            return;
        }

        if( ! rdr.isValid() )
        {
            CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
        }
        else if( DebugLevel & 0x00020000 )
        {
            CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
        }

        if(rdr.isValid())
        {
            while( rdr() )
            {
                static_paoinfo.DecodeDatabaseReader(rdr);

                rdr["paobjectid"]       >> tmp_paobjectid;
                rdr["staticpaoinfoid"]  >> tmp_entryid;

                route = getRouteById(tmp_paobjectid);

                if( route )
                {
                    route->setStaticInfo(static_paoinfo);
                }
                else
                {
                    CTILOG_ERROR(dout, "no parent found for static PAO info record (pao " << tmp_paobjectid << ", entryid " << tmp_entryid << ")");
                }
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Could not read Static PAO Info from database");
        }

        if(DebugLevel & 0x00020000)
        {
            CTILOG_DEBUG(dout, "Done looking for Static PAO Info");
        }
    }
}

void CtiRouteManager::refreshRouteEncryptionKeys( const Cti::Database::id_set & paoids )
{
    if ( paoids.empty() )   // no routes to load
    {
        return;
    }

    if ( DebugLevel & 0x00020000 )
    {
        CTILOG_DEBUG(dout, "Loading Route Encryption Keys");
    }

    // clear out the existing keys...

    for each ( long ID in paoids )
    {
        CtiRouteManager::ptr_type route = getRouteById( ID );

        if ( route )
        {
            route->setEncryptionKey( Cti::Encryption::Buffer() );
        }
    }

    // reload keys from db

    std::string sql =   "SELECT Y.PAObjectID, E.Name, E.PrivateKey "
                        "FROM   EncryptionKey E JOIN YukonPAObjectEncryptionKey Y ON E.EncryptionKeyId = Y.EncryptionKeyId "
                        "WHERE " + Cti::Database::createIdInClause( "Y", "PAObjectID", paoids.size() );

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseReader       rdr( connection, sql );

    rdr << paoids;

    rdr.execute();

    if( ! rdr.isValid() )
    {
        CTILOG_ERROR(dout, "DB read failed: "<< rdr.asString());
    }
    else if( DebugLevel & 0x00020000 )
    {
        CTILOG_DEBUG(dout, "DB read: "<< rdr.asString());
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
            rdr[ "PrivateKey" ] >> encryptedKey;

            CtiRouteManager::ptr_type route = getRouteById( paoId );

            if ( route )
            {
                const auto encrypted = convertHexStringToBytes( encryptedKey );

                try
                {
                    const auto decrypted = Cti::Encryption::decrypt( Cti::Encryption::SharedKeyfile, encrypted );
                    route->setEncryptionKey( decrypted );
                }
                catch ( Cti::Encryption::Error e )
                {
                    CTILOG_EXCEPTION_ERROR(dout, e, "Error decrypting Encryption Key named: "<< name);
                }
            }
            else
            {
                CTILOG_ERROR(dout, "No Route found for Encryption Key named: "<< name);
            }
        }
    }
    else
    {
        CTILOG_ERROR(dout, "Error reading Route Encryption Keys from database");
    }

    if ( DebugLevel & 0x00020000 )
    {
        CTILOG_DEBUG(dout, "Done loading Route Encryption Keys");
    }
}
