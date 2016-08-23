#pragma once

#include "PointResponseDao.h"


namespace Cti {

namespace Database  {

class DatabaseReader;

}

namespace CapControl {
namespace Database {

class PointResponseDatabaseDao : public PointResponseDao
{
public:

    PointResponseDatabaseDao();

    virtual std::vector<PointResponse> getPointResponsesBySubBusId(int subBusId);
    virtual std::vector<PointResponse> getAllPointResponses();

    virtual bool save(Cti::Database::DatabaseConnection& databaseConnection, PointResponse & pointResponse);

protected:

    virtual bool update(Cti::Database::DatabaseConnection& databaseConnection, PointResponse & pointResponse);
    virtual bool insert(Cti::Database::DatabaseConnection& databaseConnection, PointResponse & pointResponse);

private:

    bool performDatabaseOperation(Cti::Database::DatabaseReader& reader, std::vector<PointResponse>& pointResponses);
    void buildPointResponseFromReader(Cti::Database::DatabaseReader& reader, std::vector<PointResponse>& pointResponses);

    static const std::string _selectSql;
    static const std::string _selectSqlForBanksBySubBus;
    static const std::string _selectSqlForRegulatorsBySubBus;
    static const std::string _selectSqlForAdditionalBySubBus;
};

}
}
}
