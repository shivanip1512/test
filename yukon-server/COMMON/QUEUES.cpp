#include "precompiled.h"

#include <iostream>
using namespace std;
#include <memory.h>

#include "os2_2w32.h"
#include "cticalls.h"

#include "logger.h"
#include "queues.h"
#include "dllbase.h"

static void RemoveQueueEntry(HCTIQUEUE QueueHandle, PQUEUEENT Entry, PQUEUEENT Previous);

static void DefibBlockSem(HCTIQUEUE QueueHandle)
{
    CTILOG_INFO(dout, "RESTART THE BLOCK SEMAPHORE");

    /* Close the semaphore */
    CTICloseMutexSem (&QueueHandle->BlockSem);
    // ReCreate it?
    if(CTICreateMutexSem (NULL, &(QueueHandle->BlockSem), 0, FALSE))
    {
        CTILOG_ERROR(dout, "Unable to restart the block semaphore.");
    }

    return;
}

/* Routine to return the number of requests for a RequestID, returns 0 if not found */
IM_EX_CTIBASE INT GetRequestCount(HCTIQUEUE QueueHandle, ULONG RequestID, ULONG &Count)
{
    int retVal = ClientErrors::None;
    Count = 0;

    int dlcnt = 0;
    while(CTIRequestMutexSem (QueueHandle->BlockSem, 30000))
    {
        CTILOG_WARN(dout, "Possible deadlock for " << QueueHandle);

        //autopsy(CALLSITE);

        if(++dlcnt > 10)
        {
            DefibBlockSem(QueueHandle);
        }
    }

    request_count_t::iterator iter = QueueHandle->RequestCount.find(RequestID);
    if(iter != QueueHandle->RequestCount.end())
    {
        Count = iter->second;
    }
    else
    {
        retVal = ERROR_QUE_ELEMENT_NOT_EXIST;
    }

    CTIReleaseMutexSem (QueueHandle->BlockSem);

    return retVal;
}

/* Routine to create a queue */
IM_EX_CTIBASE INT CreateQueue (PHCTIQUEUE QueueHandle, HANDLE QuitHandle /*=NULL*/)
{
    ULONG i;

    /* Parameters are ok so get create the poor bugger */
    if((*QueueHandle = (QUEUESTRUCT*)CTIDBG_new QUEUESTRUCT) == NULL)
    {
        return(ERROR_QUE_MEMORY_ERROR);
    }

    /* We got the memory so fill it */
    if(CTICreateMutexSem (NULL, &((*QueueHandle)->BlockSem), 0, FALSE))
    {
        delete (*QueueHandle);
        return(ERROR_QUE_UNABLE_TO_INIT);
    }


    (*QueueHandle)->WaitArray[1] = QuitHandle;

    if(CTICreateEventSem (NULL, &((*QueueHandle)->WaitArray[0]), 0, FALSE))
    {
        delete (*QueueHandle);
        return(ERROR_QUE_UNABLE_TO_INIT);
    }

    (*QueueHandle)->First = NULL;

    for(i = 0; i <= MAXPRIORITY; i++)
    {
        (*QueueHandle)->Last[i] = NULL;
    }

    (*QueueHandle)->Elements = 0;
    (*QueueHandle)->Element = 1;

    (*QueueHandle)->RequestCount.clear();

    return(NO_ERROR);
}


/* Routine to close queue */
IM_EX_CTIBASE INT CloseQueue (HCTIQUEUE QueueHandle)
{
    if(QueueHandle == (HCTIQUEUE) NULL)
    {
        return(ERROR_QUE_INVALID_HANDLE);
    }

    /* Start with a good purge */
    PurgeQueue (QueueHandle);

    /* Close the semaphore */
    CTICloseMutexSem (&QueueHandle->BlockSem);

    CTICloseEventSem (&QueueHandle->WaitArray[0]);

    delete (QueueHandle);
    QueueHandle = (HCTIQUEUE) NULL;

    return(NO_ERROR);
}


/* Routine to write an entry to the queue */
IM_EX_CTIBASE INT WriteQueue (HCTIQUEUE QueueHandle,
                              ULONG Request,
                              ULONG DataSize,
                              PVOID Data,
                              ULONG Priority,
                              ULONG *pElementCount)
{
    PQUEUEENT Entry;
    ULONG i;

    if(QueueHandle == (HCTIQUEUE) NULL)
    {
        return(ERROR_QUE_INVALID_HANDLE);
    }

    if(pElementCount != NULL)
    {
        *pElementCount = 0;
    }

    /* Check the priority */
    if(Priority > MAXPRIORITY)
    {
        return(ERROR_QUE_INVALID_PRIORITY);
    }

    /* get the block semaphore */
    int dlcnt = 0;
    while(CTIRequestMutexSem (QueueHandle->BlockSem, 30000))
    {
        CTILOG_WARN(dout, "Possible deadlock for " << QueueHandle);

        //autopsy(CALLSITE);

        if(++dlcnt > 10)
        {
            DefibBlockSem(QueueHandle);
        }
    }

    /* get the Memory */
    if((Entry = new QUEUEENT) == nullptr)
    {
        CTIReleaseMutexSem (QueueHandle->BlockSem);
        return(ERROR_QUE_NO_MEMORY);
    }

    /* Load it up */
    Entry->Request = Request;
    Entry->DataSize = DataSize;
    Entry->Data = Data;
    Entry->Priority = (BYTE)Priority;
    Entry->Element = QueueHandle->Element;

    /* Kick the Element Number */
    QueueHandle->Element++;
    if(!(QueueHandle->Element))
    {
        QueueHandle->Element = 1;
    }

    if(QueueHandle->Last[Priority] != NULL)
    {
        Entry->Next = QueueHandle->Last[Priority]->Next;
        QueueHandle->Last[Priority]->Next = Entry;
        QueueHandle->Last[Priority] = Entry;
        QueueHandle->NumElements[Priority]++;
    }
    else
    {
        QueueHandle->NumElements[Priority] = 1;
        /* Walk up the priorities until we find one that has a last */
        for(i = Priority + 1; i <= MAXPRIORITY; i++)
        {
            if(QueueHandle->Last[i] != NULL)
            {
                break;
            }
        }

        /* At highest with elements or top */
        if(i > MAXPRIORITY)
        {
            i = MAXPRIORITY;
        }
        if(QueueHandle->Last[i] == NULL)
        {
            /* Must be the top and highest */
            Entry->Next = QueueHandle->First;
            QueueHandle->First = Entry;
            QueueHandle->Last[Priority]= Entry;
        }
        else
        {
            Entry->Next = QueueHandle->Last[i]->Next;
            QueueHandle->Last[i]->Next = Entry;
            QueueHandle->Last[Priority] = Entry;
        }
    }

    if(QueueHandle->Elements)
    {
        QueueHandle->Elements++;
    }
    else
    {
        QueueHandle->Elements++;
        CTIPostEventSem (QueueHandle->WaitArray[0]);
    }

    request_count_t::iterator iter = QueueHandle->RequestCount.find(Request);
    if( iter != QueueHandle->RequestCount.end() )
    {
        iter->second++;
    }
    else
    {
        QueueHandle->RequestCount[Request] = 1;
    }

    if(pElementCount != NULL)
    {
        *pElementCount = QueueHandle->Elements;
    }

    CTIReleaseMutexSem (QueueHandle->BlockSem);

    return(NO_ERROR);
}


/* Routine to query number of elements on queue */
IM_EX_CTIBASE INT QueryQueue (HCTIQUEUE QueueHandle, PULONG Elements)
{
    if(QueueHandle == (HCTIQUEUE) NULL)
    {
        return(ERROR_QUE_INVALID_HANDLE);
    }

    *Elements = QueueHandle->Elements;

    return(NO_ERROR);
}


/* Routine to peek at first element on queue */
IM_EX_CTIBASE INT PeekQueue (HCTIQUEUE QueueHandle,
                             PBYTE Priority)

{
    PQUEUEENT Entry;

    if(QueueHandle == (HCTIQUEUE) NULL)
    {
        return(ERROR_QUE_INVALID_HANDLE);
    }

    if(!(QueueHandle->Elements))
    {
        return(ERROR_QUE_EMPTY);
    }

    /* get the exclusion semaphore */
    int dlcnt = 0;
    while(CTIRequestMutexSem (QueueHandle->BlockSem, 30000))
    {
        CTILOG_WARN(dout, "Possible deadlock for " << QueueHandle);

        //autopsy(CALLSITE);

        if(++dlcnt > 10)
        {
            DefibBlockSem(QueueHandle);
        }
    }

    /* We the man so unless there has been a fubar...*/
    if(QueueHandle->First == NULL)
    {
        CTIReleaseMutexSem (QueueHandle->BlockSem);
        return(ERROR_QUE_EMPTY);
    }

    Entry = QueueHandle->First;

    /* Otherwise put it in there */
    *Priority = Entry->Priority;

    CTIReleaseMutexSem (QueueHandle->BlockSem);

    return(NO_ERROR);
}


/* Routine to read an entry from the queue */
IM_EX_CTIBASE INT ReadElementById(HCTIQUEUE QueueHandle, PULONG DataSize, PPVOID Data, ULONG Element, BOOL32 WaitFlag, PBYTE Priority, ULONG* pElementCount /*=NULL*/)
{
    PQUEUEENT Entry;
    PQUEUEENT Previous = NULL;
    ULONG i = 0;

    if(QueueHandle == (HCTIQUEUE) NULL)
    {
        return(ERROR_QUE_INVALID_HANDLE);
    }

    if(pElementCount != NULL)
    {
        *pElementCount = 0;
    }

    if(!(Element))
    {
        /* Check if we are to wait */
        if(WaitFlag == DCWW_WAIT)
        {
            /* Wait for an element */
            INT      WaitHandles = 2;
            DWORD    dwWait;

            if(QueueHandle->WaitArray[1] == NULL)
            {
                WaitHandles = 1;
            }

            dwWait = WaitForMultipleObjects(WaitHandles, QueueHandle->WaitArray, FALSE, INFINITE);

            switch( dwWait - WAIT_OBJECT_0)
            {
            case WAIT_OBJECT_0:              // This is the post event!
            case WAIT_ABANDONED:             // Call this the post event to let things shutdown nice
                {
                    break;
                }
            case WAIT_OBJECT_0 + 1:
                {
                    // This is a quit event!
                    return(ERROR_QUE_UNABLE_TO_ACCESS);
                    break;
                }
            }
        }
    }

    if(!(QueueHandle->Elements))
    {
        return(ERROR_QUE_EMPTY);
    }

    /* get the exclusion semaphore */
    int dlcnt = 0;
    while(CTIRequestMutexSem (QueueHandle->BlockSem, 30000))
    {
        CTILOG_WARN(dout, "Possible deadlock for " << QueueHandle);

        //autopsy(CALLSITE);

        if(++dlcnt > 10)
        {
            DefibBlockSem(QueueHandle);
        }
    }

    /* We the man so unless there has been a fubar...*/
    if(QueueHandle->First == NULL)
    {
        CTIReleaseMutexSem (QueueHandle->BlockSem);
        return(ERROR_QUE_EMPTY);
    }

    Entry = QueueHandle->First;

    if(Element)
    {
        while(Entry != NULL)
        {
            if(Entry->Element == Element)
            {
                break;
            }
            Previous = Entry;
            Entry = Entry->Next;
        }
        if(Entry == NULL)
        {
            CTIReleaseMutexSem (QueueHandle->BlockSem);
            return(ERROR_QUE_ELEMENT_NOT_EXIST);
        }
    }

    /* Otherwise put it in there */
    *DataSize      = Entry->DataSize;
    *Data          = Entry->Data;
    *Priority      = Entry->Priority;

    RemoveQueueEntry(QueueHandle, Entry, Previous);

    if(!(QueueHandle->Elements))
    {
        CTIResetEventSem (QueueHandle->WaitArray[0], &i);
    }

    if(pElementCount != NULL)
    {
        *pElementCount = QueueHandle->Elements;
    }

    CTIReleaseMutexSem (QueueHandle->BlockSem);

    return(NO_ERROR);
}

IM_EX_CTIBASE INT ReadFrontElement(HCTIQUEUE QueueHandle, PULONG DataSize, PPVOID Data, BOOL32 WaitFlag, PBYTE Priority)
{
    return ReadElementById(QueueHandle, DataSize, Data, 0, WaitFlag, Priority);
}

/* Routine to perform the binary equivelant of an enima */
IM_EX_CTIBASE INT PurgeQueue (HCTIQUEUE QueueHandle)
{
    QUEUEENT *QueueElement;
    ULONG i;

    if(QueueHandle == (HCTIQUEUE) NULL)
    {
        return(ERROR_QUE_INVALID_HANDLE);
    }

    /* get the exclusion semaphore */
    int dlcnt = 0;
    while(CTIRequestMutexSem (QueueHandle->BlockSem, 30000))
    {
        CTILOG_WARN(dout, "Possible deadlock for " << QueueHandle);

        //autopsy(CALLSITE);

        if(++dlcnt > 10)
        {
            DefibBlockSem(QueueHandle);
        }
    }

    try
    {
        while(QueueHandle->First != NULL)
        {
            QueueElement = QueueHandle->First;
            QueueHandle->First = QueueElement->Next;
            delete QueueElement;
        }

        /* Clear out the Priority slots */
        for(i = 0; i <= MAXPRIORITY; i++)
        {
            QueueHandle->Last[i] = NULL;
        }

        QueueHandle->Elements = 0;
        QueueHandle->RequestCount.clear();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    CTIResetEventSem (QueueHandle->WaitArray[0], &i);
    CTIReleaseMutexSem (QueueHandle->BlockSem);

    return(NO_ERROR);
}


IM_EX_CTIBASE INT SearchQueue( HCTIQUEUE QueueHandle, void *ptr, BOOL (*myFunc)(void*, void*), bool useFirstElement)
{
    INT element = 0;  // ie. not found, or first entry was a find.
    PQUEUEENT Entry;

    if(QueueHandle != (HCTIQUEUE) NULL && (QueueHandle->Elements) > 0)
    {
        /* get the exclusion semaphore */
        if( !CTIRequestMutexSem (QueueHandle->BlockSem, 2500) )
        {
            try
            {
                /* We the man so unless there has been a fubar...*/
                if(QueueHandle->First != NULL)
                {
                    Entry = QueueHandle->First;

                    while(Entry != NULL)
                    {
                        try
                        {
                            if( (*myFunc)(ptr, Entry->Data) )
                            {
                                if(useFirstElement || Entry != QueueHandle->First)         // If the Top of Queue is a "find", return zero.
                                    element = Entry->Element;

                                break;         // We found a match!
                            }
                        }
                        catch(...)
                        {
                            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                            autopsy(CALLSITE );
                        }

                        Entry = Entry->Next;
                    }
                }
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }

            CTIReleaseMutexSem (QueueHandle->BlockSem);
        }
    }

    return element;
}

/*
 *  This function sweeps through the queue removing any queue entries which return bool true from myFindFunc.
 *  Returns the number of purged queue entries.
 */
IM_EX_CTIBASE INT CleanQueue( HCTIQUEUE QueueHandle,
                              void *findFuncPtr,
                              bool (*myFindFunc)(void*, void*),
                              void (*myCleanFunc)(void*, void*),
                              void *cleanFuncPtr)
{
    INT purgecnt = 0;
    PQUEUEENT Entry;
    PQUEUEENT DeleteEntry;
    PQUEUEENT Previous = NULL;


    if(QueueHandle != (HCTIQUEUE) NULL && (QueueHandle->Elements) > 0)
    {
        /* get the exclusion semaphore */
        if( !CTIRequestMutexSem (QueueHandle->BlockSem, 2500) )
        {
            /* We the man so unless there has been a fubar...*/
            if(QueueHandle->First != NULL)
            {
                Entry = QueueHandle->First;

                while(Entry != NULL)
                {
                    DeleteEntry = Entry;        // This is our deletion candidate.
                    Entry = Entry->Next;        // Hang on to the next guy, so we can continue;
                    try
                    {
                        if( (*myFindFunc)(findFuncPtr, DeleteEntry->Data) )
                        {
                            purgecnt++;
                            (*myCleanFunc)( cleanFuncPtr, DeleteEntry->Data);         // Call the cleanup function.  It better delete the data
                            RemoveQueueEntry(QueueHandle, DeleteEntry, Previous);     // No longer linked.
                        }
                        else
                        {
                            Previous = DeleteEntry;     // This holds the last entry NOT deleted!
                        }
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                        autopsy(CALLSITE );
                    }
                }
            }

            CTIReleaseMutexSem (QueueHandle->BlockSem);
        }
    }

    return purgecnt;
}

void RemoveQueueEntry(HCTIQUEUE QueueHandle, PQUEUEENT Entry, PQUEUEENT Previous )
{
    long request = Entry->Request;
    /* Now we gotta take it off the queue */
    if(QueueHandle->First == Entry)
    {
        QueueHandle->First = Entry->Next;
    }
    else
    {
        Previous->Next = Entry->Next;
    }

    if(QueueHandle->Last[Entry->Priority] == Entry)
    {
        if(Previous != NULL && Previous->Priority == Entry->Priority)
        {
            QueueHandle->Last[Entry->Priority] = Previous;
        }
        else
        {
            QueueHandle->Last[Entry->Priority] = NULL;
        }
    }
    QueueHandle->NumElements[Entry->Priority]--;

    delete Entry;

    QueueHandle->Elements--;

    request_count_t::iterator iter = QueueHandle->RequestCount.find(request);
    if( iter != QueueHandle->RequestCount.end() )
    {
        iter->second--;

        if( iter->second <= 0 )
        {
            QueueHandle->RequestCount.erase(iter);
        }
    }

    return;
}

IM_EX_CTIBASE INT ApplyQueue( HCTIQUEUE QueueHandle, void *ptr, void (*myFunc)(void*, void*))
{
    INT apply_count = 0;
    PQUEUEENT Entry;

    if(QueueHandle != (HCTIQUEUE) NULL && (QueueHandle->Elements) > 0)
    {
        /* get the exclusion semaphore */
        if( !CTIRequestMutexSem (QueueHandle->BlockSem, 2500) )
        {
            /* We the man so unless there has been a fubar...*/
            if(QueueHandle->First != NULL)
            {
                Entry = QueueHandle->First;

                while(Entry != NULL)
                {
                    apply_count++;
                    try
                    {
                        (*myFunc)(ptr, Entry->Data);
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }
                    Entry = Entry->Next;
                }

                CTIReleaseMutexSem (QueueHandle->BlockSem);
            }
        }
    }

    return apply_count;
}
