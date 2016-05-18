#include "precompiled.h"

#include "tbl_paoproperty.h"
#include "dbaccess.h"
#include "utility.h"

namespace Cti {
namespace Database {
namespace Tables {

using namespace std;

const string PaoPropertyTable::_tableName = "PaoProperty";

PaoPropertyTable::PaoPropertyTable(Cti::RowReader &rdr)
{
    rdr["paobjectid"]    >> _paoId;
    rdr["propertyname"]  >> _propertyName;
    rdr["propertyvalue"] >> _propertyValue;
}

string PaoPropertyTable::getSQLCoreStatement()
{
    static const string sql = "SELECT PPR.paobjectid, PPR.propertyname, PPR.propertyvalue "
                              "FROM PaoProperty PPR";

    return sql;
}

string PaoPropertyTable::addIDSQLClause(const set<long> &deviceids)
{
    string sqlIDs;

    if( !deviceids.empty() )
    {
        ostringstream in_list;

        if( deviceids.size() == 1 )
        {
            //  special single id case

            in_list << *(deviceids.begin());

            sqlIDs += "AND PPR.paobjectid = " + in_list.str();

            return sqlIDs;
        }
        else
        {
            in_list << "(";
            in_list << Cti::join(deviceids, ",");
            in_list << ")";

            sqlIDs += "AND PPR.paobjectid IN " + in_list.str();

            return sqlIDs;
        }
    }

    return string();
}


long          PaoPropertyTable::getPaoId()         const  {  return _paoId;  }
const string &PaoPropertyTable::getPropertyName()  const  {  return _propertyName;  }
const string &PaoPropertyTable::getPropertyValue() const  {  return _propertyValue;  }


}
}
}


