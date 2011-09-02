#pragma once

#include "dlldefs.h"
#include "CtiPCPtrQueue.h"

template<class Type>
class CtiCountedPCPtrQueue : public CtiPCPtrQueue<Type>
{
public:
    virtual ~CtiCountedPCPtrQueue()
    {
        //clean up any items still in the queue
        Type* item;

        while( this->tryRead(item) )
            delete item;
    }
};

