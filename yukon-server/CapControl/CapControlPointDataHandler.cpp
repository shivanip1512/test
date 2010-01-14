#include "yukon.h"

#include "CapControlPointDataHandler.h"
#include "DispatchConnection.h"
#include "capcontroller.h"

CapControlPointDataHandler::CapControlPointDataHandler() : Inherited()
{

}

void CapControlPointDataHandler::registerForPoint(int pointId)
{
    CtiCapController::getInstance()->getDispatchConnection()->registerForPoint(pointId);
}

void CapControlPointDataHandler::unRegisterForPoint(int pointId)
{
    CtiCapController::getInstance()->getDispatchConnection()->unRegisterForPoint(pointId);
}
