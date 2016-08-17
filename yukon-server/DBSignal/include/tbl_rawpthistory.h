#pragma once

#include "ctitime.h"
#include "row_writer.h"
#include "database_connection.h"

struct IM_EX_SIGNAL CtiTableRawPointHistory : public Cti::Loggable
{
   const long    pointId;
   const CtiTime time;
   const int     millis;
   const int     quality;
   const double  value;

   static int validateMillis(int millis);

   CtiTableRawPointHistory(long pid, int qual, double val, const CtiTime tme, int millis);

   using DbClientType = Cti::Database::DatabaseConnection::ClientType;

   static std::string getInsertSql             (const DbClientType clientType, size_t rows);
   static std::string getFinalizeSql           (const DbClientType clientType);
   static std::string getTempTableTruncationSql(const DbClientType clientType);
   static std::string getTempTableCreationSql  (const DbClientType clientType);

   void fillInserter(Cti::RowWriter &inserter) const;

   std::string toString() const override;
};
