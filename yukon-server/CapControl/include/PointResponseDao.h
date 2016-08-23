#pragma once

#include "PointResponse.h"

#include <boost/shared_ptr.hpp>

namespace Cti {

namespace Database  {

class DatabaseConnection;

}

namespace CapControl {

class PointResponseDao
{
public:

    virtual std::vector<PointResponse> getPointResponsesBySubBusId(int subBusId)=0;
    virtual std::vector<PointResponse> getAllPointResponses()=0;

    virtual bool save(Cti::Database::DatabaseConnection& databaseConnection, PointResponse & pointResponse)=0;

protected:

    virtual bool update(Cti::Database::DatabaseConnection& databaseConnection, PointResponse & pointResponse)=0;
    virtual bool insert(Cti::Database::DatabaseConnection& databaseConnection, PointResponse & pointResponse)=0;
};

typedef boost::shared_ptr<PointResponseDao> PointResponseDaoPtr;

}
}

