#pragma once

#include "dlldefs.h"
#include "smartmap.h"
#include "fdrpoint.h"
#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"

#include <boost/shared_ptr.hpp>

using boost::shared_ptr;

class IM_EX_FDRBASE CtiFDRManager
{

    public:
        DEBUG_INSTRUMENTATION;

        // constructors, destructor
        CtiFDRManager(const std::string InterfaceName);
        CtiFDRManager(const std::string InterfaceName, std::string aWhereSelectStr);

        virtual ~CtiFDRManager();

        typedef CtiSmartMap<CtiFDRPoint> FdrPointMap;
        typedef CtiSmartMap<CtiFDRPoint>::coll_type coll_type;
        typedef CtiSmartMap<CtiFDRPoint>::ptr_type ptr_type;
        typedef CtiSmartMap<CtiFDRPoint>::spiterator spiterator;
        typedef CtiSmartMap<CtiFDRPoint>::reader_lock_guard_t readerLock;
        typedef CtiSmartMap<CtiFDRPoint>::writer_lock_guard_t writerLock;
        typedef CtiSmartMap<CtiFDRPoint>::lock_t lock;

        coll_type & getMap();
        lock & getLock();
        ptr_type find(bool (*testFun)(ptr_type&, void*),void* d);

        std::string       getInterfaceName();
        CtiFDRManager & setInterfaceName(std::string &);

        std::string       getWhereSelectStr();
        CtiFDRManager & setWhereSelectStr(std::string &);

        bool loadPointList(void);
        bool loadPoint(long pointId, CtiFDRPointSPtr & point);
        bool refreshPointList(void);

        CtiFDRPointSPtr findFDRPointID(LONG myPointId);
        CtiFDRPointSPtr removeFDRPointID(long myPointId);
        bool addFDRPointId(long myPointId, CtiFDRPointSPtr & point);

        std::string printIds() const;

        //Encapsulated functions:
        size_t entries();
    protected:

    private:
        bool getPointsFromDB(const std::stringstream &ss, std::map<long,CtiFDRPointSPtr >& fdrPtrMap);

        // private data
        std::string   iInterfaceName;
        std::string   iWhereSelectStr;
        CtiSmartMap<CtiFDRPoint> pointMap;

};
