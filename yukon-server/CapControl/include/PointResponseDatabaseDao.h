#pragma once

#include "PointResponseDao.h"
#include "PointResponse.h"

#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"

namespace Cti {
namespace CapControl {
namespace Database {

class PointResponseDatabaseDao : public PointResponseDao
{
    public:
        PointResponseDatabaseDao();

        virtual std::vector<PointResponse> getPointResponsesByBankId(int bankId);
        virtual std::vector<PointResponse> getPointResponsesByPointId(int pointId);

        virtual std::vector<PointResponse> getAllPointResponses();

        virtual bool update(PointResponse pointResponse);
        virtual bool insert(PointResponse pointResponse);
        virtual bool save(PointResponse pointResponse);

        virtual bool save(Cti::Database::DatabaseConnection& databaseConnection, PointResponse pointResponse);
        virtual bool update(Cti::Database::DatabaseConnection& databaseConnection, PointResponse pointResponse);
        virtual bool insert(Cti::Database::DatabaseConnection& databaseConnection, PointResponse pointResponse);
    private:
        bool performDatabaseOperation(Cti::Database::DatabaseReader& reader, std::vector<PointResponse>& pointResponses);
        void buildPointResponseFromReader(Cti::Database::DatabaseReader& reader, std::vector<PointResponse>& pointResponses);

        static const std::string _selectSql;
};

}
}
}
