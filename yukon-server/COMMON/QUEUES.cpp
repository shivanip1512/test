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
extern void autopsy(char *calleefile, int calleeline);       // Usage is: autopsy( __FILE__, __LINE__);

static void DefibBlockSem(HCTIQUEUE QueueHandle)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " ******  RESTART THE BLOCK SEMAPHORE ******" << endl;
    }

    /* Close the semaphore */
    CTICloseMutexSem (&QueueHandle->BlockSem);
    // ReCreate it?
    if(CTICreateMutexSem (NULL, &(QueueHandle->BlockSem), 0, FALSE))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " ******  Unable to restart the block semaphore." << endl;
        }
    }

    return;
}

/* Routine to return the starting position of a RequestID, returns 0 if not found */
IM_EX_CTIBASE INT GetIndividualRequestStartPos(HCTIQUEUE QueueHandle, ULONG RequestID, UINT &StartPos)
{
    INT retVal = NORMAL;

    StartPos = 0;
    UINT tempPos = 0;
    QUEUEENT *Entry = NULL;

    int dlcnt = 0;
    while(CTIRequestMutexSem (QueueHandle->BlockSem, 30000))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Possible deadlock " << __FILE__ << " (" << __LINE__ << ")  " << CurrentTID() << " for " << QueueHandle << endl;
        }

        //autopsy(__FILE__, __LINE__);

        if(++dlcnt > 10)
        {
            DefibBlockSem(QueueHandle);
        }
    }

    std::map<long, REQUESTSTATUS>::iterator iter = QueueHandle->RequestInfo.find(RequestID);
    if(iter != QueueHandle->RequestInfo.end())
    {
        for(int i = iter->second.Priority + 1; i <= MAXPRIORITY; i++)
        {
            tempPos += QueueHandle->NumElements[i];
        }

        /* Walk up the priorities until we find one that has a last */
        for(i = iter->second.Priority + 1; i <= MAXPRIORITY; i++)
        {
            if(QueueHandle->Last[i] != NULL)
            {
                Entry = QueueHandle->Last[i];
                break;
            }
        }
        if(Entry == NULL)
        {
            Entry = QueueHandle->First;
        }

        INT count = 1;
        while(Entry != NULL && Entry->Request != RequestID)
        {
            Entry = Entry->Next;
            count++;
        }

        if(Entry != NULL)
        {
            StartPos = tempPos + count;
        }
        else
        {
            retVal = ERROR_QUE_ELEMENT_NOT_EXIST;
        }
    }

    CTIReleaseMutexSem (QueueHandle->BlockSem);

    return retVal;
}

/* Routine to return the starting position of a RequestID, returns 0 if not found */
IM_EX_CTIBASE INT GetRequestCountAndPriority(HCTIQUEUE QueueHandle, ULONG RequestID, ULONG &Count, ULONG &Priority)
{
    int retVal = NORMAL;
    Count = 0;
    Priority = 0;
    QUEUEENT *Entry = NULL;

    int dlcnt = 0;
    while(CTIRequestMutexSem (QueueHandle->BlockSem, 30000))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Possible deadlock " << __FILE__ << " (" << __LINE__ << ")  " << CurrentTID() << " for " << QueueHandle << endl;
        }

        //autopsy(__FILE__, __LINE__);

        if(++dlcnt > 10)
        {
            DefibBlockSem(QueueHandle);
        }
    }

    std::map<long, REQUESTSTATUS>::iterator iter = QueueHandle->RequestInfo.find(RequestID);
    if(iter != QueueHandle->RequestInfo.end())
    {
        Count = iter->second.Count;
        Priority = iter->second.Priority;
    }
    else
    {
        retVal = ERROR_QUE_ELEMENT_NOT_EXIST;
    }

    CTIReleaseMutexSem (QueueHandle->BlockSem);

    return retVal;
}

/* Routine to change priorities */
IM_EX_CTIBASE INT AdjustPriority(HCTIQUEUE QueueHandle, ULONG RequestID, INT &NumberFound, ULONG TopPriority, INT Count)
{
    INT foundCount = 0;
    PQUEUEENT PrevEntry = NULL;
    PQUEUEENT CurEntry = NULL;

    if(QueueHandle == (HCTIQUEUE) NULL)
    {
        return(ERROR_QUE_INVALID_HANDLE);
    }

    /* Now figure out where it goes... */
    if(QueueHandle->Type & QUE_PRIORITY)
    {
        if(TopPriority > MAXPRIORITY)
        {
            TopPriority = MAXPRIORITY;
        }
        /* get the block semaphore */
        int dlcnt = 0;
        while(CTIRequestMutexSem (QueueHandle->BlockSem, 30000))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Possible deadlock " << __FILE__ << " (" << __LINE__ << ")  " << CurrentTID() << " for " << QueueHandle << endl;
            }
    
            //autopsy(__FILE__, __LINE__);
    
            if(++dlcnt > 10)
            {
                DefibBlockSem(QueueHandle);
            }
        }

        CurEntry = QueueHandle->First;
        std::map<long, REQUESTSTATUS>::iterator iter = QueueHandle->RequestInfo.find(RequestID);
        if(iter != QueueHandle->RequestInfo.end() && Count >= (iter->second.Count/2))
        {
            if(iter->second.Priority < TopPriority)
            {
                iter->second.Priority++;
            }
        }
        else
        {
            // According to RequestInfo, this guy doesnt exist... lets not loop.
            CurEntry = NULL;
        }
        while(CurEntry != NULL)
        {
            if(CurEntry->Request != RequestID)
            {
                PrevEntry = CurEntry;
                CurEntry = CurEntry->Next;
            }
            else if(CurEntry->Priority < TopPriority) //The request ID matches, make sure we can add to its priority
            {
                BYTE newPriority = CurEntry->Priority + 1;
                BYTE oldPriority = CurEntry->Priority; //Prevents lots of -> calls

                foundCount ++;
                CurEntry->Priority = newPriority;
                QueueHandle->NumElements[newPriority]++;
                QueueHandle->NumElements[oldPriority]--;
                if(PrevEntry != NULL)
                {
                    PrevEntry->Next = CurEntry->Next;
                }
                if(CurEntry = QueueHandle->Last[oldPriority])
                {
                    if(PrevEntry != NULL && PrevEntry->Priority == oldPriority)
                    {
                        QueueHandle->Last[oldPriority] = PrevEntry;
                    }
                    else
                    {
                        QueueHandle->Last[oldPriority] = NULL;
                    }
                }

                /* Walk up the priorities until we find one that has a last */
                for(int i = newPriority; i < MAXPRIORITY; i++)
                {
                    if(QueueHandle->Last[i] != NULL)
                    {
                        break;
                    }
                }
                if(QueueHandle->Last[i] == NULL)
                {
                    /* Must be the top and highest */
                    CurEntry->Next = QueueHandle->First;
                    QueueHandle->First = CurEntry;
                    QueueHandle->Last[newPriority]= CurEntry;
                }
                else
                {
                    CurEntry->Next = QueueHandle->Last[i]->Next;
                    QueueHandle->Last[i]->Next = CurEntry;
                    QueueHandle->Last[newPriority] = CurEntry;
                }

                // The Previous entry stays the same in this case, the Cur entry changes
                if( Count > 0 && foundCount == Count)
                {
                    CurEntry = NULL; //We need to stop now
                }
                else if(PrevEntry != NULL)
                {
                    CurEntry = PrevEntry->Next;
                }
                else //PrevEntry == NULL
                {
                    PrevEntry = CurEntry;
                    CurEntry = PrevEntry->Next;
                }
            }
        }

        CTIReleaseMutexSem (QueueHandle->BlockSem);
    }
    NumberFound = foundCount;

    return(NO_ERROR);
}

/* Routine to Cancel Entries */
/*IM_EX_CTIBASE INT CancelEntriesByRequestID(HCTIQUEUE QueueHandle, ULONG RequestID, INT &NumberFound)
{
    INT foundCount = 0;
    PQUEUEENT PrevEntry = NULL;
    PQUEUEENT CurEntry = NULL;

    if(QueueHandle == (HCTIQUEUE) NULL)
    {
        return(ERROR_QUE_INVALID_HANDLE);
    }

    // get the block semaphore 
    int dlcnt = 0;
    while(CTIRequestMutexSem (QueueHandle->BlockSem, 30000))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Possible deadlock " << __FILE__ << " (" << __LINE__ << ")  " << CurrentTID() << " for " << QueueHandle << endl;
        }

        //autopsy(__FILE__, __LINE__);

        if(++dlcnt > 10)
        {
            DefibBlockSem(QueueHandle);
        }
    }

    CurEntry = QueueHandle->First;
    while(CurEntry != NULL)
    {
        if(CurEntry->Request != RequestID)
        {
            PrevEntry = CurEntry;
            CurEntry = CurEntry->Next;
        }
        else
        {
            BYTE newPriority = CurEntry->Priority + 1;
            BYTE oldPriority = CurEntry->Priority; //Prevents lots of -> calls

            foundCount ++;
            PrevEntry->Next = CurEntry->Next;
            CurEntry->Priority = newPriority;
            if(CurEntry = QueueHandle->Last[oldPriority])
            {
                if(PrevEntry->Priority == oldPriority)
                {
                    QueueHandle->Last[oldPriority] = PrevEntry;
                }
                else
                {
                    QueueHandle->Last[oldPriority] = NULL;
                }
            }

            // Walk up the priorities until we find one that has a last
            for(int i = newPriority; i < MAXPRIORITY; i++)
            {
                if(QueueHandle->Last[i] != NULL)
                {
                    break;
                }
            }
            if(QueueHandle->Last[i] == NULL)
            {
                // Must be the top and highest
                CurEntry->Next = QueueHandle->First;
                QueueHandle->First = CurEntry;
                QueueHandle->Last[newPriority]= CurEntry;
            }
            else
            {
                CurEntry->Next = QueueHandle->Last[i]->Next;
                QueueHandle->Last[i]->Next = CurEntry;
                QueueHandle->Last[newPriority] = CurEntry;
            }
        }
    }

    CTIReleaseMutexSem (QueueHandle->BlockSem);
    NumberFound = foundCount;

    return(NO_ERROR);
}
*/

IM_EX_CTIBASE bool VerifyRequestCount (HCTIQUEUE QueueHandle)
{
    bool isCorrect = true;
    
    if(!CTIRequestMutexSem (QueueHandle->BlockSem, 5000))
    {
        QUEUEENT *Entry = QueueHandle->First;
        std::map<long, long> trueCount;

        while(Entry != NULL)
        {
            std::map<long, long>::iterator iter = trueCount.find(Entry->Request);
            if(iter != trueCount.end())
            {
                iter->second++;
            }
            else
            {
                trueCount.insert(std::map<long, long>::value_type(Entry->Request, 1));
            }
        }

        std::map<long, long>::iterator trueIter = trueCount.begin();
        std::map<long, REQUESTSTATUS>::iterator requestIter = QueueHandle->RequestInfo.begin();
        // This is the dirty method. It also works well and makes no assumptions about the map structure.
        while(trueIter != trueCount.end())
        {
            requestIter = QueueHandle->RequestInfo.find(trueIter->first);
            if(requestIter != QueueHandle->RequestInfo.end())
            {
                if(requestIter->second.Count != trueIter->second)
                {
                    isCorrect = false;
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** CHECKPOINT **** Entry Incorrect" << trueIter->second << " != " << requestIter->second.Count << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
            else
            {
                isCorrect = false;
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** CHECKPOINT **** Missing Entry " << trueIter->first << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            trueIter++;
        }

        requestIter = QueueHandle->RequestInfo.begin();
        // This is the dirty method. It also works well and makes no assumptions about the map structure.
        while(requestIter != QueueHandle->RequestInfo.end())
        {
            trueIter = trueCount.find(requestIter->first);
            if(trueIter == trueCount.end())
            {
                isCorrect = false;
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** CHECKPOINT **** Extra Entry " << requestIter->first << " " << requestIter->second.Count << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            requestIter++;
        }
    }

    return isCorrect;
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

    (*QueueHandle)->Type = Type;

    (*QueueHandle)->First = NULL;

    for(i = 0; i <= MAXPRIORITY; i++)
    {
        (*QueueHandle)->Last[i] = NULL;
    }

    (*QueueHandle)->Elements = 0;
    (*QueueHandle)->Element = 1;

    (*QueueHandle)->RequestInfo.clear();

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
            dout << CtiTime() << " Possible deadlock " << __FILE__ << " (" << __LINE__ << ")  " << CurrentTID() << " for " << QueueHandle << endl;
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

    std::map<long, REQUESTSTATUS>::iterator iter = QueueHandle->RequestInfo.find(Request);
    if( iter != QueueHandle->RequestInfo.end() )
    {
        iter->second.Count++;
    }
    else
    {
        REQUESTSTATUS tempData = {1, Priority};
        QueueHandle->RequestInfo.insert(std::map<long, REQUESTSTATUS>::value_type(Request, tempData));
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
            dout << CtiTime() << " Possible deadlock " << __FILE__ << " (" << __LINE__ << ")  " << CurrentTID() << " for " << QueueHandle << endl;
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
            dout << CtiTime() << " Possible deadlock " << __FILE__ << " (" << __LINE__ << ")  " << CurrentTID() << " for " << QueueHandle << endl;
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
            dout << CtiTime() << " Possible deadlock " << __FILE__ << " (" << __LINE__ << ")  " << CurrentTID() << " for " << QueueHandle << endl;
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
        QueueHandle->RequestInfo.clear();
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " EXCEPTION " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                    dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

                CTIReleaseMutexSem (QueueHandle->BlockSem);
            }
        }

    return element;
}

/*
IM_EX_CTIBASE INT SearchQueue (HCTIQUEUE     QueueHandle,
                               PVOID         CompareData,
                               ULONG         CompareDataSize,
                               ULONG         CompareDataOffset,
                               PREQUESTDATA  RequestData,
                               PULONG        DataSize,
                               PPVOID        Data,
                               PULONG        Element,
                               PBYTE         Priority,
                               PPVOID        Stupid)

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

    // get the exclusion semaphore 
#if 1
    int dlcnt = 0;
    while(CTIRequestMutexSem (QueueHandle->BlockSem, 30000))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Possible deadlock " << __FILE__ << " (" << __LINE__ << ")  " << CurrentTID() << " for " << QueueHandle << endl;
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
        // We the man so unless there has been a fubar...
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

        // Otherwise put it in there
        *RequestData = Entry->RequestData;
        *DataSize    = Entry->DataSize;
        *Data        = Entry->Data;
        *Priority    = Entry->Priority;
        *Element     = Entry->Element;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    CTIReleaseMutexSem (QueueHandle->BlockSem);

    return(NO_ERROR);
}
/*


/*
 *  This function sweeps through the queue removing any queue entries which return bool true from myFindFunc.
 *  Returns the number of purged queue entries.
 */
IM_EX_CTIBASE INT CleanQueue( HCTIQUEUE QueueHandle,
                              void *findFuncPtr,
                              bool (*myFindFunc)(void*, PQUEUEENT),
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
                        if( (*myFindFunc)(findFuncPtr, DeleteEntry) )
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
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " EXCEPTION " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << " CleanQueue function exception " << endl;
                        }

                        autopsy( __FILE__, __LINE__ );
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

    if(QueueHandle->Type & QUE_PRIORITY)
    {
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

    std::map<long, REQUESTSTATUS>::iterator iter = QueueHandle->RequestInfo.find(request);
    if( iter != QueueHandle->RequestInfo.end() )
    {
        iter->second.Count--;

        if( iter->second.Count <= 0 )
        {
            QueueHandle->RequestInfo.erase(iter);
            iter = QueueHandle->RequestInfo.end();
        }
    }

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
                            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
