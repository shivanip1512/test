

#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_unit
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_unit.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:36 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <limits.h>
#include "tbl_pt_unit.h"
#include "resolvers.h"
#include "yukon.h"
#include "logger.h"

CtiTablePointUnit::CtiTablePointUnit() :
_pointID(-1),
_calcType(-1),
_defaultValue(0.0),
_logFrequency(INT_MAX),
_unitID(0),
_decimalPlaces(2),
_highReasonablityLimit(DBL_MAX),
_lowReasonablityLimit(-DBL_MAX)
{}

CtiTablePointUnit::CtiTablePointUnit(const CtiTablePointUnit& aRef) :
_pointID(-1),
_calcType(-1),
_defaultValue(0.0),
_logFrequency(INT_MAX),
_unitID(0),
_decimalPlaces(2),
_highReasonablityLimit(DBL_MAX),
_lowReasonablityLimit(-DBL_MAX)
{
    *this = aRef;
}

CtiTablePointUnit& CtiTablePointUnit::operator=(const CtiTablePointUnit& aRef)
{
    if(this != &aRef)
    {
        _pointID                = aRef.getPointID();
        _unitID                 = aRef.getUnitID();
        _decimalPlaces          = aRef.getDecimalPlaces();
        _highReasonablityLimit  = aRef.getHighReasonabilityLimit();
        _lowReasonablityLimit   = aRef.getLowReasonabilityLimit();
        _calcType               = aRef.getCalcType();
        _logFrequency           = aRef.getLogFrequency();
        _defaultValue           = aRef.getDefaultValue();

    }
    return *this;
}

CtiTablePointUnit::~CtiTablePointUnit()
{}

CtiTablePointUnit& CtiTablePointUnit::operator=(const CtiTablePointUnit& aRef);

void CtiTablePointUnit::DecodeDatabaseReader(RWDBReader &rdr)
{
    if(getDebugLevel() & 0x0800)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["pointid"]                  >> _pointID;
    rdr["uomid"]                    >> _unitID;
    rdr["decimalplaces"]            >> _decimalPlaces;
    rdr["highreasonabilitylimit"]   >> _highReasonablityLimit;
    rdr["lowreasonabilitylimit"]    >> _lowReasonablityLimit;
}

INT CtiTablePointUnit::getUnitID() const
{


    return _unitID;
}

INT CtiTablePointUnit::getLogFrequency() const
{


    return _logFrequency;
}

INT CtiTablePointUnit::getCalcType() const
{


    return _calcType;
}

DOUBLE CtiTablePointUnit::getDefaultValue() const
{


    return _defaultValue;
}

LONG CtiTablePointUnit::getPointID() const
{


    return _pointID;
}

CtiTablePointUnit& CtiTablePointUnit::setPointID( const LONG pointID )
{


    _pointID = pointID;
    return *this;
}

CtiTablePointUnit& CtiTablePointUnit::setUnitID(const INT &id)
{


    _unitID = id;
    return *this;
}

CtiTablePointUnit& CtiTablePointUnit::setLogFrequency(INT i)
{


    _logFrequency = i;
    return *this;
}

CtiTablePointUnit& CtiTablePointUnit::setCalcType(INT i)
{


    _calcType = i;
    return *this;
}

CtiTablePointUnit& CtiTablePointUnit::setDefaultValue(DOUBLE d)
{


    _defaultValue = d;
    return *this;
}

void CtiTablePointUnit::dump() const
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << " PointID                                  : " << _pointID << endl;
    dout << " UOM ID                                   : " << _unitID << endl;
    dout << " Decimal Places                           : " << _decimalPlaces << endl;
    dout << " High Reasonabiliy Limit                  : " << _highReasonablityLimit << endl;
    dout << " Low  Reasonabiliy Limit                  : " << _lowReasonablityLimit << endl;
}

void CtiTablePointUnit::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable tbl = db.table(getTableName() );

    selector <<
    tbl["pointid"] <<
    tbl["uomid"] <<
    tbl["decimalplaces"] <<
    tbl["highreasonabilitylimit"] <<
    tbl["lowreasonabilitylimit"];

    selector.from(tbl);

    selector.where( selector.where() && keyTable["pointid"].leftOuterJoin(tbl["pointid"]));
}

RWCString CtiTablePointUnit::getTableName()
{
    return "PointUnit";
}

INT CtiTablePointUnit::getDecimalPlaces() const
{

    return _decimalPlaces;
}
CtiTablePointUnit& CtiTablePointUnit::setDecimalPlaces(const INT &dp)
{

    _decimalPlaces = dp;
    return *this;
}
DOUBLE CtiTablePointUnit::getHighReasonabilityLimit() const
{

    return _highReasonablityLimit;
}

CtiTablePointUnit& CtiTablePointUnit::setHighReasonabilityLimit(DOUBLE rl)
{

    _highReasonablityLimit = rl;
    return *this;
}
DOUBLE CtiTablePointUnit::getLowReasonabilityLimit() const
{

    return _lowReasonablityLimit;
}
CtiTablePointUnit& CtiTablePointUnit::setLowReasonabilityLimit(DOUBLE rl)
{

    _lowReasonablityLimit = rl;
    return *this;
}

