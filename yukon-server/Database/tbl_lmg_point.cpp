
/*-----------------------------------------------------------------------------*
*
* File:   tbl_lmg_point
*
* Date:   4/3/2006
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2006/08/04 20:08:44 $
*
* HISTORY      :
* $Log: tbl_lmg_point.cpp,v $
* Revision 1.4  2006/08/04 20:08:44  mfisher
* Fixed SQL for multiple pointgroups in DB
*
* Revision 1.3  2006/04/27 19:38:04  cplender
* Removed some debug printouts.
*
* Revision 1.2  2006/04/05 16:22:18  cplender
* Initial Revision
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include "precompiled.h"
#include "logger.h"
#include "tbl_lmg_point.h"

using std::string;

//=====================================================================================================================
//=====================================================================================================================

CtiTablePointGroup::CtiTablePointGroup() :
    _lmGroupId(0),
    _controlDevice(0),
    _controlPoint(0),
    _controlStartRawState(0)
{
}

//=====================================================================================================================
//=====================================================================================================================

CtiTablePointGroup::CtiTablePointGroup(const CtiTablePointGroup &aRef)
{
    *this = aRef;
}

//=====================================================================================================================
//=====================================================================================================================

CtiTablePointGroup::~CtiTablePointGroup()
{
}

//=====================================================================================================================
// This is the lmgrouppoint deviceid column.
//=====================================================================================================================

LONG CtiTablePointGroup::getLmGroupId() const
{
    return _lmGroupId;
}

//=====================================================================================================================
// This is the lmgroupoint deviceidusage column. Who named that?
//=====================================================================================================================

LONG CtiTablePointGroup::getControlDevice() const
{
    return _controlDevice;
}

//=====================================================================================================================
// This is the lmgroupoint pointidusage column. Who named that?
//=====================================================================================================================

LONG CtiTablePointGroup::getControlPoint() const
{
    return _controlPoint;
}

//=====================================================================================================================
// This is the lmgroupoint startcontrolrawstate column. Who named that?
//=====================================================================================================================

LONG CtiTablePointGroup::getControlStartRawState() const
{
    return _controlStartRawState;
}

//=====================================================================================================================
//=====================================================================================================================
string CtiTablePointGroup::getControlStartString() const
{
    return _rawstate[getControlStartRawState()];
}

//=====================================================================================================================
//=====================================================================================================================
string CtiTablePointGroup::getControlStopString() const
{
    return _rawstate[ ((getControlStartRawState() + 1) % 2) ];
}

//=====================================================================================================================
//=====================================================================================================================

string CtiTablePointGroup::getTableName( void )
{
    return string( "LMGroupPoint" );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTablePointGroup::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string tStr;

    rdr["deviceid"]             >> _lmGroupId;
    rdr["deviceidusage"]        >> _controlDevice;
    rdr["pointidusage"]         >> _controlPoint;
    rdr["startcontrolrawstate"] >> _controlStartRawState;
    rdr["statezerocontrol"]     >> tStr;  _rawstate[0] = tStr;
    rdr["stateonecontrol"]      >> tStr;  _rawstate[1] = tStr;

    #if 0
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << getControlStartString() << " / " << getControlStopString() << endl;
    }
    #endif
}

