#include "precompiled.h"

#include "CapControlPointDataHandler.h"
#include "DispatchConnection.h"
#include "capcontroller.h"

CapControlPointDataHandler::CapControlPointDataHandler() : PointDataHandler()
{

}

void CapControlPointDataHandler::registerForPoint(const long pointId)
{
    CtiCapController::getInstance()->getDispatchConnection()->registerForPoint(this, pointId);
}

void CapControlPointDataHandler::unRegisterForPoint(const long pointId)
{
    CtiCapController::getInstance()->getDispatchConnection()->unRegisterForPoint(this, pointId);
}
