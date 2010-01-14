#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"
#include "PointDataHandler.h"

class CapControlPointDataHandler : public PointDataHandler
{
    public:
        CapControlPointDataHandler();

    private:

        virtual void registerForPoint(int pointId);
        virtual void unRegisterForPoint(int pointId);

        typedef PointDataHandler Inherited;
};
