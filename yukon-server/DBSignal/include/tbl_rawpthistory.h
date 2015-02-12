#pragma once

#include "ctitime.h"
#include "row_writer.h"

class IM_EX_SIGNAL CtiTableRawPointHistory
{
   long    _pointId;
   CtiTime _time;
   int     _millis;
   int     _quality;
   double  _value;

   static int validateMillis(int millis);

public:

   CtiTableRawPointHistory(long pid, int qual, double val, const CtiTime tme, int millis);

   static std::string getInsertSql();

   void fillInserter(Cti::RowWriter &inserter, const long long changeid);
};
