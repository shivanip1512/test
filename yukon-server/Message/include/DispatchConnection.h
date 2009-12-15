#pragma warning( disable : 4786)
#pragma once

#include "connection.h"

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

    public:
        DispatchConnection();
        DispatchConnection( const int &port, const string &host, Que_t *inQ = NULL, int tt = 3);

        void addRegistrationForPoints(const std::list<int>& pointIds);
        void addRegistrationForPoint(int pointId);

        void removeRegistrationForPoints(const std::list<int>& pointIds);
        void removeRegistrationForPoint(int pointId);

        virtual void preWork();
};
