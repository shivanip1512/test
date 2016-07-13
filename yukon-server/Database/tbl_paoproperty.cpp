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

long          PaoPropertyTable::getPaoId()         const  {  return _paoId;  }
const string &PaoPropertyTable::getPropertyName()  const  {  return _propertyName;  }
const string &PaoPropertyTable::getPropertyValue() const  {  return _propertyValue;  }


}
}
}


