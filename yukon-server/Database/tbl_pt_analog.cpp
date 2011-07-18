#include "precompiled.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_analog
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_analog.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2008/06/30 15:24:29 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "tbl_pt_analog.h"
#include "logger.h"

using std::endl;

CtiTablePointAnalog& CtiTablePointAnalog::operator=(const CtiTablePointAnalog& aRef)
{
   if(this != &aRef)
   {
      _multiplier       = aRef.getMultiplier();
      _dataOffset       = aRef.getDataOffset();
      _deadband         = aRef.getDeadband();
      //_transducerType   = aRef.getTransducerType();
   }
   return *this;
}

void CtiTablePointAnalog::DecodeDatabaseReader(Cti::RowReader &rdr)
{

   rdr >> _multiplier;
   rdr >> _dataOffset;
   rdr >> _deadband;
   //rdr >> _transducerType;
}

void CtiTablePointAnalog::dump() const
{
   CtiLockGuard<CtiLogger> doubt_guard(dout);
   dout << " Multiplier                               : " << _multiplier << endl;
   dout << " Data Offset                              : " << _dataOffset << endl;
   dout << " Deadband  +/-                            : " << _deadband << endl;
   //dout << " Transducer Type                          : " << _transducerType << endl;
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
/*string CtiTablePointAnalog::getTransducerType() const
{
   return _transducerType;
}*/


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
/*CtiTablePointAnalog& CtiTablePointAnalog::setTransducerType(string &str)
{
   _transducerType = str;
   return *this;
}*/

CtiTablePointAnalog::CtiTablePointAnalog() :
    _multiplier(0.0),
    _dataOffset(0.0),
    _deadband(0.0)
{}

CtiTablePointAnalog::CtiTablePointAnalog(const CtiTablePointAnalog& aRef)
{
   *this = aRef;
}

CtiTablePointAnalog::~CtiTablePointAnalog() {}



