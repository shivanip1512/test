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

