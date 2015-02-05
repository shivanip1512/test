#include "precompiled.h"

#include "tbl_unitmeasure.h"
#include "logger.h"

using std::endl;

CtiTableUnitMeasure::CtiTableUnitMeasure() :
   _calcType(0)
{

}

CtiTableUnitMeasure::~CtiTableUnitMeasure() {}

void CtiTableUnitMeasure::DecodeDatabaseReader(Cti::RowReader &rdr)
{
   rdr  >> _calcType;
}

std::string CtiTableUnitMeasure::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableUnitMeasure";
    itemList.add("Calc Type") << _calcType;

    return itemList.toString();
}

int CtiTableUnitMeasure::getCalcType() const
{
   return _calcType;
}

