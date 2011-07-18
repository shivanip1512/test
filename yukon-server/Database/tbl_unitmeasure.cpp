/*-----------------------------------------------------------------------------*
*
* File:   tbl_unitmeasure
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_analog.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2007/09/28 15:43:05 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "tbl_unitmeasure.h"
#include "logger.h"

using std::endl;

CtiTableUnitMeasure::CtiTableUnitMeasure() :
   _calcType(0)
{

}

CtiTableUnitMeasure::CtiTableUnitMeasure(const CtiTableUnitMeasure& aRef)
{
   *this = aRef;
}

CtiTableUnitMeasure::~CtiTableUnitMeasure() {}

CtiTableUnitMeasure& CtiTableUnitMeasure::operator=(const CtiTableUnitMeasure &aRef)
{
    if(this != &aRef)
    {
        //_uomName   = aRef.getUOMName();
        _calcType  = aRef.getCalcType();
        //_longName  = aRef.getLongName();
        //_formula   = aRef.getFormula();
    }
    return *this;
}

void CtiTableUnitMeasure::DecodeDatabaseReader(Cti::RowReader &rdr)
{
   //rdr  >> _uomName;
   rdr  >> _calcType;
   //rdr  >> _longName;
   //rdr  >> _formula;
}

void CtiTableUnitMeasure::dump() const
{
   CtiLockGuard<CtiLogger> doubt_guard(dout);
   //dout << " UOMName                               : " << _uomName << endl;
   dout << " Calc Type                             : " << _calcType << endl;
   //dout << " Long Name                             : " << _longName << endl;
   //dout << " Formula                               : " << _formula << endl;
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


