#pragma once

#include "PointDataRequest.h"
#include "DispatchConnection.h"

#include <boost/shared_ptr.hpp>

class IM_EX_MSG PointDataRequestFactory
{
    public:
        PointDataRequestFactory();
        virtual PointDataRequestPtr createDispatchPointDataRequest(DispatchConnectionPtr conn);
};

typedef boost::shared_ptr<PointDataRequestFactory> PointDataRequestFactoryPtr;
