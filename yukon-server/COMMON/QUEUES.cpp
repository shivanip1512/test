#include "yukon.h"

#include <windows.h>
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
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " ******  RESTART THE BLOCK SEMAPHORE ******" << endl;
    }

    /* Close the semaphore */
    CTICloseMutexSem (&QueueHandle->BlockSem);
    // ReCreate it?
    if(CTICreateMutexSem (NULL, &(QueueHandle->BlockSem), 0, FALSE))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " ******  Unable to restart the block semaphore." << endl;
        }
    }

    return;
}

/* Routine to create a queue */
IM_EX_CTIBASE INT CreateQueue (PHCTIQUEUE QueueHandle, ULONG Type, HANDLE QuitHandle)
{
    ULONG i;

    /* Check that we know the type */
    if((Type & ~(QUE_LIFO | QUE_PRIORITY | QUE_CONVERT_ADDRESS)))
    {
        return(ERROR_QUE_UNABLE_TO_INIT);
    }

    /* Now make sure we do not conflict */
    if((Type & QUE_LIFO) && (Type & QUE_PRIORITY))
    {
        return(ERROR_QUE_UNABLE_TO_INIT);
    }

    /* Parameters are ok so get create the poor bugger */
    if((*QueueHandle = (QUEUESTRUCT*)malloc (sizeof (QUEUESTRUCT))) == NULL)
    {
        return(ERROR_QUE_MEMORY_ERROR);
    }

    /* We got the memory so fill it */
    if(CTICreateMutexSem (NULL, &((*QueueHandle)->BlockSem), 0, FALSE))
    {
        free (*QueueHandle);
        return(ERROR_QUE_UNABLE_TO_INIT);
    }


    (*QueueHandle)->WaitArray[1] = QuitHandle;

    if(CTICreateEventSem (NULL, &((*QueueHandle)->WaitArray[0]), 0, FALSE))
    {
        free (*QueueHandle);
        return(ERROR_QUE_UNABLE_TO_INIT);
    }

    (*QueueHandle)->Type = Type;

    (*QueueHandle)->First = NULL;

    for(i = 0; i <= MAXPRIORITY; i++)
    {
        (*QueueHandle)->Last[i] = NULL;
    }

    (*QueueHandle)->Elements = 0;
    (*QueueHandle)->Element = 1;

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

    free (QueueHandle);
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
    if((QueueHandle->Type & QUE_PRIORITY) && Priority > MAXPRIORITY)
    {
        return(ERROR_QUE_INVALID_PRIORITY);
    }

    /* get the block semaphore */
#if 1
    int dlcnt = 0;
    while(CTIRequestMutexSem (QueueHandle->BlockSem, 30000))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Possible deadlock " << __FILE__ << " (" << __LINE__ << ")  " << CurrentTID() << " for " << QueueHandle << endl;
        }

        //autopsy(__FILE__, __LINE__);

        if(++dlcnt > 10)
        {
            DefibBlockSem(QueueHandle);
        }
    }
#else
    if(CTIRequestMutexSem (QueueHandle->BlockSem, SEM_INDEFINITE_WAIT))
    {
        return(ERROR_QUE_UNABLE_TO_ACCESS);
    }
#endif

    /* get the Memory */
    if((Entry = (QUEUEENT*)malloc (sizeof (QUEUEENT))) == NULL)
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

    /* Now figure out where it goes... */
    if(QueueHandle->Type & QUE_PRIORITY)
    {
        /* Priority Queue */
        if(QueueHandle->Last[Priority] != NULL)
        {
            Entry->Next = QueueHandle->Last[Priority]->Next;
            QueueHandle->Last[Priority]->Next = Entry;
            QueueHandle->Last[Priority] = Entry;
        }
        else
        {
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
    }
    else if(QueueHandle->Type & QUE_LIFO)
    {
        /* LIFO Queue */
        if(QueueHandle->First == NULL)
        {
            QueueHandle->First = Entry;
            QueueHandle->Last[0] = Entry;
        }
        else
        {
            Entry->Next = QueueHandle->First;
            QueueHandle->First = Entry;
        }
    }
    else
    {
        /* FIFO queue */
        Entry->Next = NULL;
        if(QueueHandle->First == NULL)
        {
            QueueHandle->First = Entry;
            QueueHandle->Last[0] = Entry;
        }
        else
        {
            QueueHandle->Last[0]->Next = Entry;
            QueueHandle->Last[0] = Entry;
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
                             PREQUESTDATA RequestData,
                             PULONG DataSize,
                             PPVOID Data,
                             PULONG Element,
                             BOOL32 WaitFlag,
                             PBYTE  Priority)

{
    PQUEUEENT Entry;

    if(QueueHandle == (HCTIQUEUE) NULL)
    {
        return(ERROR_QUE_INVALID_HANDLE);
    }

    if(!(*Element))
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
#if 1
    int dlcnt = 0;
    while(CTIRequestMutexSem (QueueHandle->BlockSem, 30000))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Possible deadlock " << __FILE__ << " (" << __LINE__ << ")  " << CurrentTID() << " for " << QueueHandle << endl;
        }

        //autopsy(__FILE__, __LINE__);

        if(++dlcnt > 10)
        {
            DefibBlockSem(QueueHandle);
        }
    }
#else
    if(CTIRequestMutexSem (QueueHandle->BlockSem, SEM_INDEFINITE_WAIT))
    {
        return(ERROR_QUE_UNABLE_TO_ACCESS);
    }
#endif

    /* We the man so unless there has been a fubar...*/
    if(QueueHandle->First == NULL)
    {
        CTIReleaseMutexSem (QueueHandle->BlockSem);
        return(ERROR_QUE_EMPTY);
    }

    Entry = QueueHandle->First;

    if(!(*Element))
    {
        while(Entry != NULL)
        {
            if(Entry->Element == *Element)
            {
                break;
            }
            Entry = Entry->Next;
        }
        if(Entry == NULL)
        {
            CTIReleaseMutexSem (QueueHandle->BlockSem);
            return(ERROR_QUE_ELEMENT_NOT_EXIST);
        }
    }

    /* Otherwise put it in there */
    *RequestData = Entry->RequestData;
    *DataSize = Entry->DataSize;
    *Data = Entry->Data;
    *Priority = Entry->Priority;
    *Element=Entry->Element;

    CTIReleaseMutexSem (QueueHandle->BlockSem);

    return(NO_ERROR);
}


/* Routine to read an entry from the queue */
IM_EX_CTIBASE INT ReadQueue (HCTIQUEUE QueueHandle, PREQUESTDATA RequestData, PULONG  DataSize, PPVOID Data, ULONG Element, BOOL32 WaitFlag, PBYTE Priority, ULONG *pElementCount)
{
    PQUEUEENT Entry;
    PQUEUEENT Previous = NULL;
    ULONG i;

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
#if 1
    int dlcnt = 0;
    while(CTIRequestMutexSem (QueueHandle->BlockSem, 30000))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Possible deadlock " << __FILE__ << " (" << __LINE__ << ")  " << CurrentTID() << " for " << QueueHandle << endl;
        }

        //autopsy(__FILE__, __LINE__);

        if(++dlcnt > 10)
        {
            DefibBlockSem(QueueHandle);
        }
    }
#else
    if(CTIRequestMutexSem (QueueHandle->BlockSem, SEM_INDEFINITE_WAIT))
    {
        return(ERROR_QUE_UNABLE_TO_ACCESS);
    }
#endif

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
    *RequestData   = Entry->RequestData;
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
#if 1
    int dlcnt = 0;
    while(CTIRequestMutexSem (QueueHandle->BlockSem, 30000))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Possible deadlock " << __FILE__ << " (" << __LINE__ << ")  " << CurrentTID() << " for " << QueueHandle << endl;
        }

        //autopsy(__FILE__, __LINE__);

        if(++dlcnt > 10)
        {
            DefibBlockSem(QueueHandle);
        }
    }
#else
    if(CTIRequestMutexSem (QueueHandle->BlockSem, SEM_INDEFINITE_WAIT))
    {
        return(ERROR_QUE_UNABLE_TO_ACCESS);
    }
#endif

    try
    {
        while(QueueHandle->First != NULL)
        {
            QueueElement = QueueHandle->First;
            QueueHandle->First = QueueElement->Next;
            free (QueueElement);
        }

        /* Clear out the Priority slots */
        for(i = 0; i <= MAXPRIORITY; i++)
        {
            QueueHandle->Last[i] = NULL;
        }

        QueueHandle->Elements = 0;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    CTIResetEventSem (QueueHandle->WaitArray[0], &i);
    CTIReleaseMutexSem (QueueHandle->BlockSem);

    return(NO_ERROR);
}


IM_EX_CTIBASE INT SearchQueue( HCTIQUEUE QueueHandle, void *ptr, BOOL (*myFunc)(void*, void*))
{
    INT element = 0;  // ie. not found.
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
                            element = Entry->Element;
                            break;         // We found a match!
                        }
                    }
                    catch(...)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " EXCEPTION " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << " SearchQueue function exception " << endl;
                        }

                        autopsy( __FILE__, __LINE__ );
                    }

                    Entry = Entry->Next;
                }
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

                CTIReleaseMutexSem (QueueHandle->BlockSem);
            }
        }

    return element;
}

IM_EX_CTIBASE INT SearchQueue (HCTIQUEUE     QueueHandle,
                               PVOID         CompareData,
                               ULONG         CompareDataSize,
                               ULONG         CompareDataOffset,
                               PREQUESTDATA  RequestData,
                               PULONG        DataSize,
                               PPVOID        Data,
                               PULONG        Element,
                               PBYTE         Priority)

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
#if 1
    int dlcnt = 0;
    while(CTIRequestMutexSem (QueueHandle->BlockSem, 30000))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Possible deadlock " << __FILE__ << " (" << __LINE__ << ")  " << CurrentTID() << " for " << QueueHandle << endl;
        }

        // autopsy(__FILE__, __LINE__);

        if(++dlcnt > 10)
        {
            DefibBlockSem(QueueHandle);
        }
    }
#else
    if(CTIRequestMutexSem (QueueHandle->BlockSem, SEM_INDEFINITE_WAIT))
    {
        return(ERROR_QUE_UNABLE_TO_ACCESS);
    }
#endif

    try
    {
        /* We the man so unless there has been a fubar...*/
        if(QueueHandle->First == NULL)
        {
            CTIReleaseMutexSem (QueueHandle->BlockSem);
            return(ERROR_QUE_EMPTY);
        }

        Entry = QueueHandle->First;

        if(!(*Element))
        {
            while(Entry != NULL)
            {
                if(!(memcmp ((PCHAR)Entry->Data + CompareDataOffset, (PCHAR)CompareData, CompareDataSize)))
                {
                    break;
                }

                Entry = Entry->Next;

                if(Entry == NULL)
                {
                    CTIReleaseMutexSem (QueueHandle->BlockSem);
                    return(ERROR_QUE_ELEMENT_NOT_EXIST);
                }
            }
        }

        /* Otherwise put it in there */
        *RequestData = Entry->RequestData;
        *DataSize    = Entry->DataSize;
        *Data        = Entry->Data;
        *Priority    = Entry->Priority;
        *Element     = Entry->Element;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    CTIReleaseMutexSem (QueueHandle->BlockSem);

    return(NO_ERROR);
}


/*
 *  This function sweeps through the queue removing any queue entries which return bool true from myFindFunc.
 *  Returns the number of purged queue entries.
 */
IM_EX_CTIBASE INT CleanQueue( HCTIQUEUE QueueHandle,
                              void *ptr,
                              bool (*myFindFunc)(void*, void*),
                              void (*myCleanFunc)(void*, void*))
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
                        if( (*myFindFunc)(ptr, DeleteEntry->Data) )
                        {
                            purgecnt++;
                            (*myCleanFunc)( ptr, DeleteEntry->Data);         // Call the cleanup function.  It better delete the data
                            RemoveQueueEntry(QueueHandle, DeleteEntry, Previous);     // No longer linked.
                        }
                        else
                        {
                            Previous = DeleteEntry;     // This holds the last entry NOT deleted!
                        }
                    }
                    catch(...)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " EXCEPTION " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << " CleanQueue function exception " << endl;
                        }

                        autopsy( __FILE__, __LINE__ );
                    }
                }

                CTIReleaseMutexSem (QueueHandle->BlockSem);
            }
        }
    }

    return purgecnt;
}

void RemoveQueueEntry(HCTIQUEUE QueueHandle, PQUEUEENT Entry, PQUEUEENT Previous )
{
    /* Now we gotta take it off the queue */
    if(QueueHandle->First == Entry)
    {
        QueueHandle->First = Entry->Next;
    }
    else
    {
        Previous->Next = Entry->Next;
    }

    if(QueueHandle->Type & QUE_PRIORITY)
    {
        if(QueueHandle->Last[Entry->Priority] == Entry)
        {
            QueueHandle->Last[Entry->Priority] = NULL;
        }
    }
    else
    {
        if(QueueHandle->Last[0] == Entry)
        {
            QueueHandle->Last[0] = NULL;
        }
    }

    free (Entry);

    QueueHandle->Elements--;

    return;
}

IM_EX_CTIBASE INT ApplyQueue( HCTIQUEUE QueueHandle, void *ptr, void (*myFunc)(void*, void*))
{
    INT count = 0;
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
                    count++;
                    try
                    {
                        (*myFunc)(ptr, Entry->Data);
                    }
                    catch(...)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                    Entry = Entry->Next;
                }

                CTIReleaseMutexSem (QueueHandle->BlockSem);
            }
        }
    }

    return count;
}
