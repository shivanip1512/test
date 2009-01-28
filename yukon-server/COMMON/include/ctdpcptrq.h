/*-----------------------------------------------------------------------------
    Filename:  ctdpcptrq.h

    Programmer:  Aaron Lauinger

    Description: Header file for CtiCountedPDCQueue.
                 Combines the functionality of CtiPCPtrQueue and
                 RWCountingBody to make a queue that can be reference counted
                 and is thread-safe.

    Initial Date:  5/12/99

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/

#ifndef CTICOUNTEDPCPTRQUEUE_H
#define CTICOUNTEDPCPTRQUEUE_H

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

#endif

