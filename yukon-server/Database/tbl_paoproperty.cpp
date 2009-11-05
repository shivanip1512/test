#include "yukon.h"

#include "tbl_paoproperty.h"
#include "dbaccess.h"
#include "rwutil.h"

namespace Cti {
namespace Database {
namespace Tables {

using namespace std;

const string PaoPropertyTable::_tableName = "PaoProperty";

PaoPropertyTable::PaoPropertyTable(RWDBReader &rdr)
{
    rdr["paobjectid"]    >> _paoId;
    rdr["propertyname"]  >> _propertyName;
    rdr["propertyvalue"] >> _propertyValue;
}

void PaoPropertyTable::getSQL(RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(_tableName.c_str());

    selector
        << keyTable["paobjectid"]
        << keyTable["propertyname"]
        << keyTable["propertyvalue"];

    selector.from(keyTable);
}


long          PaoPropertyTable::getPaoId()         const  {  return _paoId;  }
const string &PaoPropertyTable::getPropertyName()  const  {  return _propertyName;  }
const string &PaoPropertyTable::getPropertyValue() const  {  return _propertyValue;  }


}
}
}


