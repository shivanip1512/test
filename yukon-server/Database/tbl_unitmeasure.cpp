/*-----------------------------------------------------------------------------*
*
* File:   tbl_unitmeasure
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_analog.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/17 19:02:57 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_unitmeasure.h"
#include "logger.h"

CtiTableUnitMeasure::CtiTableUnitMeasure() {}

CtiTableUnitMeasure::CtiTableUnitMeasure(const CtiTableUnitMeasure& aRef)
{
   *this = aRef;
}

CtiTableUnitMeasure::~CtiTableUnitMeasure() {}

CtiTableUnitMeasure& CtiTableUnitMeasure::operator=(const CtiTableUnitMeasure &aRef)
{
    if(this != &aRef)
    {
        _uomName   = aRef.getUOMName();
        _calcType  = aRef.getCalcType();
        _longName  = aRef.getLongName();
        _formula   = aRef.getFormula();
    }
    return *this;
}

void CtiTableUnitMeasure::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   RWDBTable tbl = db.table("UnitMeasure");

   selector <<
   tbl["uomname"] <<
   tbl["calctype"] <<
   tbl["longname"] <<
   tbl["formula"];

   selector.from(tbl);

   selector.where( selector.where() &&
                   (keyTable["uomid"] == tbl["uomid"]) );

}

void CtiTableUnitMeasure::DecodeDatabaseReader(RWDBReader &rdr)
{
   rdr["uomname"]   >> _uomName;
   rdr["calctype"]  >> _calcType;
   rdr["longname"]  >> _longName;
   rdr["formula"]   >> _formula;
}

void CtiTableUnitMeasure::dump() const
{
   CtiLockGuard<CtiLogger> doubt_guard(dout);
   dout << " UOMName                               : " << _uomName << endl;
   dout << " Calc Type                             : " << _calcType << endl;
   dout << " Long Name                             : " << _longName << endl;
   dout << " Formula                               : " << _formula << endl;
}

RWCString CtiTableUnitMeasure::getUOMName() const
{
   return _uomName;
}

int CtiTableUnitMeasure::getCalcType() const
{
   return _calcType;
}

RWCString CtiTableUnitMeasure::getLongName() const
{
   return _longName;
}

RWCString CtiTableUnitMeasure::getFormula() const
{
   return _formula;
}


