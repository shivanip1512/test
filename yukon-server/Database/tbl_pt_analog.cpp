#include "precompiled.h"

#include "tbl_pt_analog.h"

void CtiTablePointAnalog::DecodeDatabaseReader(Cti::RowReader &rdr)
{
   rdr >> _multiplier;
   rdr >> _dataOffset;
   rdr >> _deadband;
}

CtiTablePointAnalog::CtiTablePointAnalog() :
    _multiplier(0.0),
    _dataOffset(0.0),
    _deadband(0.0)
{}


