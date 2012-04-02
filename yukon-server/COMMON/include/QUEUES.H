#pragma once

#include "cticalls.h"
#include "types.h"
#include <map>

#ifndef MAXPRIORITY
   #define MAXPRIORITY 15
#endif

#ifndef __OS2__
   #define ERROR_QUE_ELEMENT_NOT_EXIST     333     /* MSG%QUE_ELEMENT_NOT_EXIST */
   #define ERROR_QUE_NO_MEMORY             334     /* MSG%QUE_NO_MEMORY */
   #define ERROR_QUE_INVALID_PRIORITY      336     /* MSG%none */
   #define ERROR_QUE_INVALID_HANDLE        337     /* MSG%none */
   #define ERROR_QUE_MEMORY_ERROR          339     /* MSG%none */
   #define ERROR_QUE_EMPTY                 342     /* MSG%none */
   #define ERROR_QUE_UNABLE_TO_ACCESS      345     /* MSG%none */
   #define ERROR_QUE_UNABLE_TO_INIT        347     /* MSG%none */
#endif

typedef struct _QUEUEENT
{
   ULONG Element;
   ULONG Request;  //  typically a copy of CtiOutMessage.Request.GrpMsgID; set by WriteQueue
   ULONG DataSize;
   PVOID Data;
   BYTE Priority;
   struct _QUEUEENT *Next;
} QUEUEENT;

typedef QUEUEENT *PQUEUEENT;

typedef std::map<long, unsigned long> request_count_t;

/* Define the queue structure */
typedef struct
{
   HMTX BlockSem;
   HANDLE WaitArray[2];
   QUEUEENT *First;
   QUEUEENT *Last[MAXPRIORITY + 1];
   ULONG NumElements[MAXPRIORITY + 1];
   ULONG Element;
   ULONG Elements;
   request_count_t RequestCount;

} QUEUESTRUCT;

typedef QUEUESTRUCT *PQUEUESTRUCT;
typedef PQUEUESTRUCT HCTIQUEUE;
typedef HCTIQUEUE *PHCTIQUEUE;


IM_EX_CTIBASE INT CreateQueue (PHCTIQUEUE QueueHandle, HANDLE QuitHandle=NULL);

IM_EX_CTIBASE INT CloseQueue (HCTIQUEUE QueueHandle);

IM_EX_CTIBASE INT WriteQueue (HCTIQUEUE QueueHandle, ULONG Request, ULONG DataSize, PVOID Data, ULONG Priority, ULONG *pElementCount = NULL);

IM_EX_CTIBASE INT QueryQueue (HCTIQUEUE QueueHandle, PULONG Elements);

IM_EX_CTIBASE INT PeekQueue (HCTIQUEUE QueueHandle,
                             PULONG DataSize,
                             PPVOID Data,
                             PULONG Element,
                             BOOL32 WaitFlag,
                             PBYTE Priority);

IM_EX_CTIBASE INT ReadElementById (HCTIQUEUE QueueHandle,
                                      PULONG DataSize,
                                      PPVOID Data,
                                      ULONG Element,
                                      BOOL32 WaitFlag,
                                      PBYTE Priority,
                                      ULONG* pElementCount=NULL);

IM_EX_CTIBASE INT ReadFrontElement (HCTIQUEUE QueueHandle,
                                    PULONG DataSize,
                                    PPVOID Data,
                                    BOOL32 WaitFlag,
                                    PBYTE Priority);

IM_EX_CTIBASE INT PurgeQueue (HCTIQUEUE QueueHandle);

IM_EX_CTIBASE INT SearchQueue( HCTIQUEUE QueueHandle, void *ptr, BOOL (*myFunc)(void*, void*), bool useFirstElement = true);

IM_EX_CTIBASE INT CleanQueue( HCTIQUEUE QueueHandle,
                              void *findFuncPtr,
                              bool (*myFindFunc)(void*, void*),
                              void (*myCleanFunc)(void*, void*),
                              void *cleanFuncPtr = NULL);

IM_EX_CTIBASE INT ApplyQueue( HCTIQUEUE QueueHandle, void *ptr, void (*myFunc)(void*, void*));

IM_EX_CTIBASE INT GetRequestCount(HCTIQUEUE QueueHandle, ULONG RequestID, ULONG &Count);

