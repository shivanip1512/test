
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_accum
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_accum.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:34 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "tbl_pt_accum.h"
#include "logger.h"

CtiTablePointAccumulator& CtiTablePointAccumulator::operator=(const CtiTablePointAccumulator& aRef)
{
   if(this != &aRef)
   {
      _pointID          = aRef.getPointID();
      _multiplier       = aRef.getMultiplier();
      _dataOffset       = aRef.getDataOffset();
   }
   return *this;
}

void CtiTablePointAccumulator::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   RWDBTable tbl = db.table(getTableName() );

   selector <<
      tbl["multiplier"] <<
      tbl["dataoffset"];

   selector.from(tbl);

   selector.where( selector.where() && keyTable["pointid"] == tbl["pointid"] );

}

void CtiTablePointAccumulator::DecodeDatabaseReader(RWDBReader &rdr)
{
   rdr["multiplier"]       >> _multiplier;
   rdr["dataoffset"]       >> _dataOffset;
}

void CtiTablePointAccumulator::dump() const
{
   CtiLockGuard<CtiLogger> doubt_guard(dout);
   dout << " Multiplier                               : " << _multiplier << endl;
   dout << " Data Offset                              : " << _dataOffset << endl;
}

DOUBLE      CtiTablePointAccumulator::getMultiplier() const
{
   return _multiplier;
}

DOUBLE      CtiTablePointAccumulator::getDataOffset() const
{
   return _dataOffset;
}

CtiTablePointAccumulator& CtiTablePointAccumulator::setMultiplier(DOUBLE d)
{
   _multiplier = d;
   return *this;
}

CtiTablePointAccumulator& CtiTablePointAccumulator::setDataOffset(DOUBLE d)
{
   _dataOffset = d;
   return *this;
}

CtiTablePointAccumulator& CtiTablePointAccumulator::setPointID( const LONG pointID)
{
   _pointID = pointID;
   return *this;
}

CtiTablePointAccumulator::CtiTablePointAccumulator() :
   _multiplier(1.0),
   _dataOffset(0.0)
{}

CtiTablePointAccumulator::CtiTablePointAccumulator(const CtiTablePointAccumulator& aRef)
{
   *this = aRef;
}

CtiTablePointAccumulator::~CtiTablePointAccumulator() {}

LONG CtiTablePointAccumulator::getPointID() const
{
   return _pointID;
}

RWCString CtiTablePointAccumulator::getTableName()
{
   return "PointAccumulator";
}


