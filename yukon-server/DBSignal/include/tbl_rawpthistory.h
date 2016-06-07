#pragma once

#include "ctitime.h"
#include "row_writer.h"
#include "database_connection.h"

class IM_EX_SIGNAL CtiTableRawPointHistory : public Cti::Loggable
{
   long    _pointId;
   CtiTime _time;
   int     _millis;
   int     _quality;
   double  _value;

   static int validateMillis(int millis);

public:

   CtiTableRawPointHistory(long pid, int qual, double val, const CtiTime tme, int millis);

   using DbClientType = Cti::Database::DatabaseConnection::ClientType;

   static std::string getInsertSql             (const DbClientType clientType, size_t rows);
   static std::string getFinalizeSql           (const DbClientType clientType);
   static std::string getTempTableTruncationSql(const DbClientType clientType);
   static std::string getTempTableCreationSql  (const DbClientType clientType);

   void fillInserter(Cti::RowWriter &inserter);

   std::string toString() const override;
};
