#include "precompiled.h"

#include "PointDataRequestFactory.h"
#include "DispatchPointDataRequest.h"

PointDataRequestFactory::PointDataRequestFactory()
{

}

PointDataRequestPtr PointDataRequestFactory::createDispatchPointDataRequest(DispatchConnectionPtr conn)
{
    DispatchPointDataRequest* ptr = new DispatchPointDataRequest();
    ptr->setDispatchConnection(conn);

    PointDataRequestPtr requestPtr(ptr);

    return requestPtr;
}
