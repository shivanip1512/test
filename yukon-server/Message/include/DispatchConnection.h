#pragma warning( disable : 4786)
#pragma once

#include "connection.h"
#include "message.h"

#include <list>
#include <string>

/**
 * This class is an extension of the default connection class to
 * allow for grouping of point registration messages without the
 * client having to group them up or even know what a point
 * registration message is.
 *
 * @author Thain Spar (12/10/2009)
 */
class IM_EX_MSG DispatchConnection : public CtiConnection
{
    private:
        bool _registered;

        std::list<int> _addList;
        std::list<int> _removeList;

        mutable CtiMutex _regListMux;

        //Hidden
        DispatchConnection();
    public:

        typedef CtiConnection Inherited;

        DispatchConnection( const string &connectionName, const int &port, const string &host, Que_t *inQ = NULL, int tt = 3);

        void registerForPoints(const std::list<int>& pointIds);
        void registerForPoint(int pointId);
        void unRegisterForPoints(const std::list<int>& pointIds);
        void unRegisterForPoint(int pointId);

        virtual void preWork();
};
