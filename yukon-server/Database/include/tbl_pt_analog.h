#pragma once

#include "row_reader.h"

#include "dbmemobject.h"


class IM_EX_CTIYUKONDB CtiTablePointAnalog : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTablePointAnalog(const CtiTablePointAnalog&);
    CtiTablePointAnalog& operator=(const CtiTablePointAnalog&);

protected:

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
