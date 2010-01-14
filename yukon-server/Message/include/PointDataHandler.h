#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"
#include "PointDataListener.h"
#include "msg_pdata.h"

#include <map>
#include <list>

class IM_EX_MSG PointDataHandler
{
    public:
        PointDataHandler();

        void clear();

        bool addPoint(int pointId, int paoId);
        bool removePointOnPao(int pointId, int paoId);

        bool removeAllPointsForPao(int paoId);
        bool removePointId(int pointId);

        bool processIncomingPointData(CtiPointDataMsg* message);

        void getAllPointIds(std::list<int>& pointIds);

        void setPointDataListener(PointDataListener* pointDataListener);

    private:
        virtual void registerForPoint(int pointId) = 0;
        virtual void unRegisterForPoint(int pointId) = 0;

        //To find all paos that are connected to a point. Main map.
        typedef std::map<int,std::set<int> > PointIdMap;
        typedef std::map<int,std::set<int> >::iterator PointIdMapItr;

        //Used for finding all points related to a pao. Used for maintenance
        typedef std::map<int,std::set<int> > PaoIdMap;
        typedef std::map<int,std::set<int> >::iterator PaoIdMapItr;

        PointIdMap _pointIdMap;
        PaoIdMap _paoIdMap;

        PointDataListener * _pointDataListener;
};
