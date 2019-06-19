#include "precompiled.h"
#include "logger.h"
#include "tbl_dyn_paostatistics.h"


CtiTableDynamicPaoStatistics::CtiTableDynamicPaoStatistics( int paoID,
                                                            const std::string & statType,
                                                            const CtiTime timestamp,
                                                            int requests,
                                                            int attempts,
                                                            int completions,
                                                            int commErrors,
                                                            int protocolErrors,
                                                            int systemErrors )
    :   _pAObjectId( paoID ),
        _requests( requests ),
        _attempts( attempts ),
        _completions( completions ),
        _commErrors( commErrors ),
        _protocolErrors( protocolErrors ),
        _systemErrors( systemErrors ),
        _statisticType( statType ),
        _startDateTime( timestamp )
{
    // empty
}

CtiTableDynamicPaoStatistics::TableSchema CtiTableDynamicPaoStatistics::getTempTableSchema()
{
    return
    {
        Cti::Database::ColumnDefinition
            { "PAObjectId",             "numeric",      "NUMBER"        },
            { "StatisticType",          "varchar(16)",  "VARCHAR2(16)"  },
            { "StartDateTime",          "datetime",     "DATE"          },
            { "Requests",               "numeric",      "NUMBER"        },
            { "Attempts",               "numeric",      "NUMBER"        },
            { "Completions",            "numeric",      "NUMBER"        },
            { "CommErrors",             "numeric",      "NUMBER"        },
            { "ProtocolErrors",         "numeric",      "NUMBER"        },
            { "SystemErrors",           "numeric",      "NUMBER"        }
    };
}

void CtiTableDynamicPaoStatistics::fillRowWriter( Cti::RowWriter & inserter ) const
{
    inserter
        << _pAObjectId
        << _statisticType
        << _startDateTime
        << _requests
        << _attempts
        << _completions
        << _commErrors
        << _protocolErrors
        << _systemErrors;
}

std::string CtiTableDynamicPaoStatistics::toString() const
{
    Cti::FormattedList itemList;

    itemList << "CtiTableDynamicPaoStatistics";

    itemList.add("PAObjectId")      << _pAObjectId;
    itemList.add("StatisticType")   << _statisticType;
    itemList.add("StartDateTime")   << _startDateTime;
    itemList.add("Requests")        << _requests;
    itemList.add("Attempts")        << _attempts;
    itemList.add("Completions")     << _completions;
    itemList.add("CommErrors")      << _commErrors;
    itemList.add("ProtocolErrors")  << _protocolErrors;
    itemList.add("SystemErrors")    << _systemErrors;

    return itemList.toString();
}

