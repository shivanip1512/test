#pragma once

#include "ctitime.h"
#include "row_writer.h"
#include "database_util.h"


struct CtiTableDynamicPaoStatistics : Cti::RowSource
{
    const int
        _pAObjectId,
        _requests,
        _attempts,
        _completions,
        _commErrors,
        _protocolErrors,
        _systemErrors;

    const std::string
        _statisticType;

    const CtiTime
        _startDateTime;

   CtiTableDynamicPaoStatistics( int paoID,
                                 const std::string & statType,
                                 const CtiTime timestamp,
                                 int requests, 
                                 int attempts,
                                 int completions,
                                 int commErrors,
                                 int protocolErrors,
                                 int systemErrors );

   using TableSchema = std::array< Cti::Database::ColumnDefinition, 9 >;

   static TableSchema getTempTableSchema();

   void fillRowWriter( Cti::RowWriter & inserter ) const override;

   std::string toString() const override;
};

