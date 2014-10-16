#include "precompiled.h"

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

CtiTablePointUnit::~CtiTablePointUnit()
{}

void CtiTablePointUnit::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
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

std::string CtiTablePointUnit::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTablePointUnit";
    itemList.add("UOM ID")         << _unitID;
    itemList.add("Decimal Places") << _decimalPlaces;
    itemList.add("Decimal Digits") << _decimalDigits;

    return itemList.toString();
}

string CtiTablePointUnit::getTableName()
{
    return "PointUnit";
}

CtiTableUnitMeasure &CtiTablePointUnit::getUnitMeasure()
{
    return _unitMeasure;
}

const CtiTableUnitMeasure& CtiTablePointUnit::getUnitMeasure() const
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

