#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_analog
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_analog.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "tbl_pt_analog.h"
#include "logger.h"

CtiTablePointAnalog& CtiTablePointAnalog::operator=(const CtiTablePointAnalog& aRef)
{
   if(this != &aRef)
   {
      _multiplier       = aRef.getMultiplier();
      _dataOffset       = aRef.getDataOffset();
      _deadband         = aRef.getDeadband();
      _transducerType   = aRef.getTransducerType();
   }
   return *this;
}

void CtiTablePointAnalog::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   RWDBTable tbl = db.table("PointAnalog");

   selector <<
   tbl["multiplier"] <<
   tbl["dataoffset"] <<
   tbl["deadband"] <<
   tbl["transducertype"];

   selector.from(tbl);

   selector.where( selector.where() &&
                   keyTable["pointid"] == tbl["pointid"] );

}

void CtiTablePointAnalog::DecodeDatabaseReader(RWDBReader &rdr)
{
   rdr["multiplier"]       >> _multiplier;
   rdr["dataoffset"]       >> _dataOffset;
   rdr["deadband"]         >> _deadband;
   rdr["transducertype"]   >> _transducerType;
}

void CtiTablePointAnalog::dump() const
{
   CtiLockGuard<CtiLogger> doubt_guard(dout);
   dout << " Multiplier                               : " << _multiplier << endl;
   dout << " Data Offset                              : " << _dataOffset << endl;
   dout << " Deadband  +/-                            : " << _deadband << endl;
   dout << " Transducer Type                          : " << _transducerType << endl;
}

DOUBLE CtiTablePointAnalog::getMultiplier() const
{
   return _multiplier;
}
DOUBLE CtiTablePointAnalog::getDataOffset() const
{
   return _dataOffset;
}

DOUBLE CtiTablePointAnalog::getDeadband() const
{
   return _deadband;
}
RWCString CtiTablePointAnalog::getTransducerType() const
{
   return _transducerType;
}


CtiTablePointAnalog& CtiTablePointAnalog::setMultiplier(DOUBLE d)
{
   _multiplier = d;
   return *this;
}
CtiTablePointAnalog& CtiTablePointAnalog::setDataOffset(DOUBLE d)
{
   _dataOffset = d;
   return *this;
}

CtiTablePointAnalog& CtiTablePointAnalog::setDeadband(DOUBLE d)
{
   _deadband = d;
   return *this;
}
CtiTablePointAnalog& CtiTablePointAnalog::setTransducerType(RWCString &str)
{
   _transducerType = str;
   return *this;
}

CtiTablePointAnalog::CtiTablePointAnalog() {}

CtiTablePointAnalog::CtiTablePointAnalog(const CtiTablePointAnalog& aRef)
{
   *this = aRef;
}

CtiTablePointAnalog::~CtiTablePointAnalog() {}



