#include "precompiled.h"

#include "database_util.h"

namespace Cti {
namespace Database {

std::string createIdSqlClause(const id_set &paoids, const std::string &table, const std::string &column)
{
    if( paoids.empty() )
    {
        return std::string();
    }

    std::ostringstream sql;

    sql << table << "." << column;

    //  special single id case
    if( paoids.size() == 1 )
    {
        sql << " = " << *paoids.begin();
    }
    else
    {
        sql << " IN (";

        std::copy(paoids.begin(), paoids.end(), csv_output_iterator<long, std::ostringstream>(sql));

        sql << ")";
    }

    return sql.str();
}

}
}
