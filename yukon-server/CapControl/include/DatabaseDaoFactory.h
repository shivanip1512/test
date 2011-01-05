#pragma once

#include "PointResponseDao.h"
#include "PointResponseDatabaseDao.h"

#include <boost/shared_ptr.hpp>

namespace Cti {
namespace CapControl {

class DaoFactory
{
    public:
        typedef boost::shared_ptr<DaoFactory> SharedPtr;

        virtual PointResponseDaoPtr getPointResponseDao()=0;
};

namespace Database {

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
