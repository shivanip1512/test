#pragma once

#include "PointResponseDao.h"
#include "PointResponseDatabaseDao.h"

#include <boost/shared_ptr.hpp>

namespace Cti {
namespace CapControl {
namespace Database {

class DaoFactory
{
    public:
        virtual PointResponseDaoPtr getPointResponseDao()=0;
};

class DatabaseDaoFactory : public DaoFactory
{
    public:
        DatabaseDaoFactory(){}

        virtual PointResponseDaoPtr getPointResponseDao()
        {
            PointResponseDatabaseDao* dao = new PointResponseDatabaseDao();

            return PointResponseDaoPtr(dao);
        }
};

}
}
}
