#pragma once

#include "ctitime.h"
#include "row_writer.h"
#include "database_util.h"

struct CtiTableRawPointHistory : Cti::RowSource
{
   const long    pointId;
   const CtiTime time;
   const int     millis;
   const int     quality;
   const double  value;
   const std::string trackingId;  //  Just for tracking, not written to the database

   static int validateMillis(int millis);

   CtiTableRawPointHistory(long pid, int qual, double val, const CtiTime tme, int millis, std::string trackingId);

   static std::array<Cti::Database::ColumnDefinition, 5> getTempTableSchema();

   void fillRowWriter(Cti::RowWriter &inserter) const override;

   std::string toString() const override;
};
