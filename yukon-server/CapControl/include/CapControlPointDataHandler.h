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

    void registerForPoint( const long pointId ) override;
    void unRegisterForPoint( const long pointId ) override;
};
