#pragma once

#include "dlldefs.h"
#include "row_reader.h"

#include <string>


namespace Cti {
namespace Database {
namespace Tables {

class IM_EX_CTIYUKONDB PaoPropertyTable : private boost::noncopyable
{
    long        _paoId;
    std::string _propertyName;
    std::string _propertyValue;

    static const std::string _tableName;

public:

    PaoPropertyTable(Cti::RowReader &rdr);
    virtual ~PaoPropertyTable() {};

    long getPaoId() const;
    const std::string &getPropertyName() const;
    const std::string &getPropertyValue() const;

    static std::string getTableName();
    static std::string getSQLCoreStatement();
};

}
}
}

