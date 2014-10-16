#include "precompiled.h"

#include "tbl_pt_accum.h"
#include "logger.h"

using std::string;
using std::endl;

void CtiTablePointAccumulator::DecodeDatabaseReader(Cti::RowReader &rdr)
{
   rdr       >> _multiplier;
   rdr       >> _dataOffset;
}

std::string CtiTablePointAccumulator::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTablePointAccumulator";
    itemList.add("Multiplier")  << _multiplier;
    itemList.add("Data Offset") << _dataOffset;

    return itemList.toString();
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
   _dataOffset(0.0),
   _pointID(0)
{}

CtiTablePointAccumulator::~CtiTablePointAccumulator() {}

LONG CtiTablePointAccumulator::getPointID() const
{
   return _pointID;
}

string CtiTablePointAccumulator::getTableName()
{
   return "PointAccumulator";
}


