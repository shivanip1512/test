#pragma once


#include "repeaterrole.h"
#include "rte_base.h"
#include "dlldefs.h"
#include "slctdev.h"
#include "smartmap.h"

class IM_EX_DEVDB CtiRouteManager
{
private:

    typedef std::pair<long, long> route_repeater_relation;

    typedef std::set<route_repeater_relation> route_repeater_associations;

    struct
    {
        route_repeater_associations current;
        route_repeater_associations previous;

    } _routeRepeaters;

    CtiSmartMap< CtiRouteBase > _smartMap;

    void RefreshRoutes(bool &rowFound, Cti::RowReader& rdr, CtiRouteBase* (*Factory)(Cti::RowReader &));
    void RefreshVersacomRoutes(bool &rowFound, Cti::RowReader& rdr);
    void RefreshRepeaterRoutes(bool &rowFound, Cti::RowReader& rdr);
    void RefreshMacroRoutes(bool &rowFound, Cti::RowReader& rdr);

    void        refreshStaticPaoInfo(const Cti::Database::id_set &paoids);
    void        refreshRouteEncryptionKeys(const Cti::Database::id_set &paoids);
    std::string createIdSqlClause(const Cti::Database::id_set &paoids, const std::string table, const std::string attrib);

public:

    typedef CtiLockGuard<CtiMutex>                      LockGuard;
    typedef CtiSmartMap< CtiRouteBase >                 coll_type;              // This is the collection type!
    typedef CtiSmartMap< CtiRouteBase >::ptr_type       ptr_type;
    typedef CtiSmartMap< CtiRouteBase >::spiterator     spiterator;
    typedef CtiSmartMap< CtiRouteBase >::insert_pair    insert_pair;


    CtiRouteManager();
    virtual ~CtiRouteManager();

    void apply(void (*applyFun)(const long, ptr_type, void*), void* d);

    void DumpList(void);

    void RefreshList(CtiRouteBase* (*Factory)(Cti::RowReader &) = RouteFactory);
    ptr_type getRouteById( LONG rid );
    virtual ptr_type getRouteByName( std::string rname );

    spiterator begin();
    spiterator end();

    static spiterator nextPos(spiterator &my_itr); // This is to overcome MS's flaw in the STL.  We MUST use this method in binaries other than this DLL.

    coll_type::lock_t &getLock()
    {
        return _smartMap.getLock();
    }

    bool empty() const;
    bool buildRoleVector( long id, CtiRequestMsg& Req, std::list< CtiMessage* > &retList, std::vector< CtiDeviceRepeaterRole > & roleVector );

    bool isRepeaterRelevantToRoute( long repeater_id, long route_id ) const;
};

