#pragma once

#include "PointResponse.h"
#include "database_connection.h"
#include <boost/shared_ptr.hpp>

namespace Cti {
namespace CapControl {

class PointResponseDao
{
    public:
        virtual std::vector<PointResponse> getPointResponsesByDeviceId(int deviceId)=0;
        virtual std::vector<PointResponse> getPointResponsesBySubBusId(int subBusId)=0;
        virtual std::vector<PointResponse> getPointResponsesByPointId(int pointId)=0;

        virtual std::vector<PointResponse> getAllPointResponses()=0;

        virtual bool update(PointResponse pointResponse)=0;
        virtual bool insert(PointResponse pointResponse)=0;
        virtual bool save(PointResponse pointResponse)=0;

        virtual bool update(Cti::Database::DatabaseConnection& databaseConnection, PointResponse pointResponse)=0;
        virtual bool insert(Cti::Database::DatabaseConnection& databaseConnection, PointResponse pointResponse)=0;
        virtual bool save(Cti::Database::DatabaseConnection& databaseConnection, PointResponse pointResponse)=0;
};

typedef boost::shared_ptr<PointResponseDao> PointResponseDaoPtr;

}
}

