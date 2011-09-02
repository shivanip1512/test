#pragma once

#include "yukon.h"
#include "PointDataHandler.h"

/**
 * Extended PointDataHandler to keep cap control specific
 * connection code out of the base class.
 *
 */
class CapControlPointDataHandler : public PointDataHandler
{
    public:
        CapControlPointDataHandler();

    private:

        virtual void registerForPoint(int pointId);
        virtual void unRegisterForPoint(int pointId);

        typedef PointDataHandler Inherited;
};
