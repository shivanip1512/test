/*-----------------------------------------------------------------------------
    Filename:  ctdpcptrq.h

    Programmer:  Aaron Lauinger

    Description: Header file for CtiCountedPDCQueue.
                 Combines the functionality of RWPCPtrQueue and
                 RWCountingBody to make a queue that can be reference counted
                 and is thread-safe.

    Initial Date:  5/12/99

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/

#ifndef CTICOUNTEDPCPTRQUEUE_H
#define CTICOUNTEDPCPTRQUEUE_H

#include <rw/thr/countptr.h>
#include <rw/thr/mutex.h>
#include <rw/thr/prodcons.h>

#include "dlldefs.h"

template<class Type>
class IM_EX_CTIBASE CtiCountedPCPtrQueue : public RWPCPtrQueue<Type>, public RWCountingBody<RWMutexLock>
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

