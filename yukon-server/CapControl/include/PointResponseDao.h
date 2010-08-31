#pragma once

#include "PointResponse.h"
#include <boost/shared_ptr.hpp>

namespace Cti {
namespace CapControl {

class PointResponseDao
{
    public:
        virtual std::vector<PointResponse> getPointResponsesByBankId(int bankId)=0;
        virtual std::vector<PointResponse> getPointResponsesByPointId(int pointId)=0;

        virtual std::vector<PointResponse> getAllPointResponses()=0;

        virtual bool update(PointResponse pointResponse)=0;
        virtual bool insert(PointResponse pointResponse)=0;
        virtual bool save(PointResponse pointResponse)=0;
};

typedef boost::shared_ptr<PointResponseDao> PointResponseDaoPtr;

}
}

