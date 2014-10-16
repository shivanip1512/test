#pragma once

#include "dlldefs.h"
#include "row_reader.h"

#include <string>


namespace Cti {
namespace Database {
namespace Tables {

class IM_EX_CTIYUKONDB PaoPropertyTable : private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    PaoPropertyTable(const PaoPropertyTable&);
    PaoPropertyTable& operator=(const PaoPropertyTable&);

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

    static std::string addIDSQLClause(const std::set<long> &deviceids);
};

}
}
}

