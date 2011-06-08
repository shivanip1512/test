/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_unit
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_unit.cpp-arc  $
* REVISION     :  $Revision: 1.11.2.1 $
* DATE         :  $Date: 2008/11/18 20:11:29 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <limits.h>
#include "tbl_pt_unit.h"
#include "resolvers.h"
#include "logger.h"

using std::string;
using std::endl;

CtiTablePointUnit::CtiTablePointUnit() :
_logFrequency(INT_MAX),
_unitID(0),
_decimalPlaces(2),
_decimalDigits(0)
//_highReasonablityLimit(DBL_MAX),
//_lowReasonablityLimit(-DBL_MAX)
{}

CtiTablePointUnit::CtiTablePointUnit(const CtiTablePointUnit& aRef) :
_logFrequency(INT_MAX),
_unitID(0),
_decimalPlaces(2),
_decimalDigits(0)
//_highReasonablityLimit(DBL_MAX),
//_lowReasonablityLimit(-DBL_MAX)
{
    *this = aRef;
}

CtiTablePointUnit& CtiTablePointUnit::operator=(const CtiTablePointUnit& aRef)
{
    if(this != &aRef)
    {
        _unitID                 = aRef.getUnitID();
        _decimalPlaces          = aRef.getDecimalPlaces();
        _decimalDigits          = aRef.getDecimalDigits();
        //_highReasonablityLimit  = aRef.getHighReasonabilityLimit();
        //_lowReasonablityLimit   = aRef.getLowReasonabilityLimit();
        _logFrequency           = aRef.getLogFrequency();

        _unitMeasure            = aRef.getUnitMeasure();
    }
    return *this;
}

CtiTablePointUnit::~CtiTablePointUnit()
{}


void CtiTablePointUnit::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr   >> _unitID;
    rdr   >> _decimalPlaces;
    rdr   >> _decimalDigits;

    _unitMeasure.DecodeDatabaseReader(rdr);
}

INT CtiTablePointUnit::getUnitID() const
{
    return _unitID;
}

INT CtiTablePointUnit::getLogFrequency() const
{
    return _logFrequency;
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

void CtiTablePointUnit::dump() const
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << " UOM ID                                   : " << _unitID << endl;
    dout << " Decimal Places                           : " << _decimalPlaces << endl;
    dout << " Decimal Digits                           : " << _decimalDigits << endl;
    //dout << " High Reasonabiliy Limit                  : " << _highReasonablityLimit << endl;
    //dout << " Low  Reasonabiliy Limit                  : " << _lowReasonablityLimit << endl;
}

string CtiTablePointUnit::getTableName()
{
    return "PointUnit";
}

CtiTableUnitMeasure &CtiTablePointUnit::getUnitMeasure()
{
    return _unitMeasure;
}

CtiTableUnitMeasure CtiTablePointUnit::getUnitMeasure() const
{
    return _unitMeasure;
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
INT CtiTablePointUnit::getDecimalDigits() const
{

    return _decimalDigits;
}
CtiTablePointUnit& CtiTablePointUnit::setDecimalDigits(const INT &digits)
{

    _decimalDigits = digits;
    return *this;
}

