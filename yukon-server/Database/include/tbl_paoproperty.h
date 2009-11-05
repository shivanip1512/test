#pragma once

#include "dlldefs.h"

#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>

#include <string>

namespace Cti {
namespace Database {
namespace Tables {

class IM_EX_CTIYUKONDB PaoPropertyTable
{
private:

    long        _paoId;
    std::string _propertyName;
    std::string _propertyValue;

    static const std::string _tableName;

public:

    PaoPropertyTable(RWDBReader &rdr);
    virtual ~PaoPropertyTable() {};

    long getPaoId() const;
    const std::string &getPropertyName() const;
    const std::string &getPropertyValue() const;

    static std::string getTableName();
    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

    void dump() const;

};

}
}
}

