/*-----------------------------------------------------------------------------*
*
* File:   repeaterrole
*
* Date:   6/27/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/02/10 23:23:45 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include "repeaterrole.h"

CtiDeviceRepeaterRole::CtiDeviceRepeaterRole() :
    _routeID(0),
    _roleNumber(0),
    _inBits(31),
    _outBits(7),
    _stagesToFollow(0)
{

}

LONG CtiDeviceRepeaterRole::getRouteID() const
{
    return _routeID;
}
CtiDeviceRepeaterRole& CtiDeviceRepeaterRole::setRouteID(LONG rid)
{
    _routeID = rid;
    return *this;
}

int CtiDeviceRepeaterRole::getRoleNumber() const
{
    return _roleNumber;
}
CtiDeviceRepeaterRole& CtiDeviceRepeaterRole::setRoleNumber(int rolenum)
{
    _roleNumber = rolenum;
    return *this;
}

BYTE CtiDeviceRepeaterRole::getOutBits() const
{
    return _outBits;
}
CtiDeviceRepeaterRole& CtiDeviceRepeaterRole::setOutBits(BYTE bits)
{
    _outBits = bits;
    return *this;
}

BYTE CtiDeviceRepeaterRole::getFixBits() const
{
    return _fixBits;
}
CtiDeviceRepeaterRole& CtiDeviceRepeaterRole::setFixBits(BYTE bits)
{
    _fixBits = bits;
    return *this;
}

BYTE CtiDeviceRepeaterRole::getInBits() const
{
    return _inBits;
}
CtiDeviceRepeaterRole& CtiDeviceRepeaterRole::setInBits(BYTE bits)
{
    _inBits = bits;
    return *this;
}

BYTE CtiDeviceRepeaterRole::getStages() const
{
    return _stagesToFollow;
}

CtiDeviceRepeaterRole& CtiDeviceRepeaterRole::setStages(BYTE stages)
{
    _stagesToFollow = stages;
    return *this;
}


