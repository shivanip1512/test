
#pragma warning( disable : 4786)
#ifndef __REPEATERROLE_H__
#define __REPEATERROLE_H__

/*-----------------------------------------------------------------------------*
*
* File:   repeaterrole
*
* Class:
* Date:   6/27/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/07/01 17:53:54 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "dlldefs.h"

class IM_EX_CTIBASE CtiDeviceRepeaterRole
{
protected:

    LONG _routeID;           // What route did this info come from.. Used only as info.
    int _roleNumber;         // What role number has been assigned to this route id.. Used only as info.

    BYTE _outBits;           // Comprised of the Route's FixBits and either the Routes Varbits, or the preceeding repeaters variable bits.  This is what this repeater is watching for!
    BYTE _fixBits;           // Comprised of this roles fix bit pattern
    BYTE _inBits;            // Comprised of this repeater's variable bits in this route!
    BYTE _stagesToFollow;    // Comprised of a count of the number of repeaters in this route, which follow this repeater.

public:

    CtiDeviceRepeaterRole();

    LONG getRouteID() const;
    CtiDeviceRepeaterRole& setRouteID(LONG rid);

    int getRoleNumber() const;
    CtiDeviceRepeaterRole& setRoleNumber(int rolenum);

    BYTE getOutBits() const;
    CtiDeviceRepeaterRole& setOutBits(BYTE bits);

    BYTE getFixBits() const;
    CtiDeviceRepeaterRole& setFixBits(BYTE bits);

    BYTE getInBits() const;
    CtiDeviceRepeaterRole& setInBits(BYTE bits);

    BYTE getStages() const;
    CtiDeviceRepeaterRole& setStages(BYTE stages);
};

#endif // #ifndef __REPEATERROLE_H__
