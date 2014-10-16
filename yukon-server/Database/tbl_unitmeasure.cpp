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
   //rdr  >> _uomName;
   rdr  >> _calcType;
   //rdr  >> _longName;
   //rdr  >> _formula;
}

std::string CtiTableUnitMeasure::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableUnitMeasure";
    itemList.add("Calc Type") << _calcType;

    return itemList.toString();
}

/*string CtiTableUnitMeasure::getUOMName() const
{
   return _uomName;
}*/

int CtiTableUnitMeasure::getCalcType() const
{
   return _calcType;
}

/*string CtiTableUnitMeasure::getLongName() const
{
   return _longName;
}*/

/*string CtiTableUnitMeasure::getFormula() const
{
   return _formula;
}*/


