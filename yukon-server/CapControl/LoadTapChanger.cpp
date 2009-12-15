#include "yukon.h"
#include "LoadTapChanger.h"

LoadTapChanger::LoadTapChanger() : Inherited()
{

}

LoadTapChanger::LoadTapChanger(RWDBReader& rdr) : Inherited(rdr)
{

}

LtcPointHolder& LoadTapChanger::getPointValueHolder()
{
    return _pointValues;
}

void LoadTapChanger::handlePointData(CtiPointDataMsg* message)
{
    _pointValues.updatePointValue(message);
}
