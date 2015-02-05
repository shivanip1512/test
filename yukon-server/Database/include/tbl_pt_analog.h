#pragma once

#include "row_reader.h"

#include "dbmemobject.h"


class IM_EX_CTIYUKONDB CtiTablePointAnalog : public CtiMemDBObject, private boost::noncopyable
{
   double _multiplier;
   double _dataOffset;
   double _deadband;

public:
   CtiTablePointAnalog();

   void DecodeDatabaseReader(Cti::RowReader &rdr);

   double getMultiplier() const  {  return _multiplier;  };
   double getDataOffset() const  {  return _dataOffset;  };
   double getDeadband()   const  {  return _deadband;  };
};
