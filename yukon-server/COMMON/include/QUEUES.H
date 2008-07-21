
#ifndef QUEUE_H
#define QUEUE_H

#include "cticalls.h"
#include "types.h"
#include <map>

#ifndef MAXPRIORITY
   #define MAXPRIORITY 15
#endif

#ifndef __OS2__
typedef struct _REQUESTDATA     /* reqqdata */
{
   PID         pid;
   ULONG       ulData;
} REQUESTDATA;

typedef REQUESTDATA *PREQUESTDATA;

   #define QUE_FIFO              0L
   #define QUE_LIFO              1L
   #define QUE_PRIORITY          2L
   #define QUE_NOCONVERT_ADDRESS 0L
   #define QUE_CONVERT_ADDRESS   4L

   #define ERROR_QUE_CURRENT_NAME          329     /* MSG%none */
   #define ERROR_QUE_PROC_NOT_OWNED        330     /* MSG%QUE_PROC_NOT_OWNED */
   #define ERROR_QUE_PROC_OWNED            331     /* MSG%none */
   #define ERROR_QUE_DUPLICATE             332     /* MSG%QUE_DUPLICATE */
   #define ERROR_QUE_ELEMENT_NOT_EXIST     333     /* MSG%QUE_ELEMENT_NOT_EXIST */
   #define ERROR_QUE_NO_MEMORY             334     /* MSG%QUE_NO_MEMORY */
   #define ERROR_QUE_INVALID_NAME          335     /* MSG%none */
   #define ERROR_QUE_INVALID_PRIORITY      336     /* MSG%none */
   #define ERROR_QUE_INVALID_HANDLE        337     /* MSG%none */
   #define ERROR_QUE_LINK_NOT_FOUND        338     /* MSG%none */
   #define ERROR_QUE_MEMORY_ERROR          339     /* MSG%none */
   #define ERROR_QUE_PREV_AT_END           340     /* MSG%none */
   #define ERROR_QUE_PROC_NO_ACCESS        341     /* MSG%none */
   #define ERROR_QUE_EMPTY                 342     /* MSG%none */
   #define ERROR_QUE_NAME_NOT_EXIST        343     /* MSG%none */
   #define ERROR_QUE_NOT_INITIALIZED       344     /* MSG%none */
   #define ERROR_QUE_UNABLE_TO_ACCESS      345     /* MSG%none */
   #define ERROR_QUE_UNABLE_TO_ADD         346     /* MSG%none */
   #define ERROR_QUE_UNABLE_TO_INIT        347     /* MSG%none */
#endif

typedef struct _QUEUEENT
{
   ULONG Element;
   union
   {
      ULONG Request;
      REQUESTDATA RequestData;
   };
   ULONG DataSize;
   PVOID Data;
   BYTE Priority;
   struct _QUEUEENT *Next;
} QUEUEENT;

typedef QUEUEENT *PQUEUEENT;

typedef struct
{
    long Count;
    long Priority;
} REQUESTSTATUS;

/* Define the queue structure */
typedef struct
{
   ULONG Type;
   HMTX BlockSem;
   HANDLE WaitArray[2];
   QUEUEENT *First;
   QUEUEENT *Last[MAXPRIORITY + 1];
   ULONG NumElements[MAXPRIORITY + 1];
   ULONG Element;
   ULONG Elements;
   std::map<long, REQUESTSTATUS> RequestInfo;

} QUEUESTRUCT;

typedef QUEUESTRUCT *PQUEUESTRUCT;
typedef PQUEUESTRUCT HCTIQUEUE;
typedef HCTIQUEUE *PHCTIQUEUE;


IM_EX_CTIBASE INT CreateQueue (PHCTIQUEUE QueueHandle, ULONG Type, HANDLE QuitHandle = NULL);

IM_EX_CTIBASE INT CloseQueue (HCTIQUEUE QueueHandle);

IM_EX_CTIBASE INT WriteQueue (HCTIQUEUE QueueHandle, ULONG Request, ULONG DataSize, PVOID Data, ULONG Priority, ULONG *pElementCount = NULL);

IM_EX_CTIBASE INT QueryQueue (HCTIQUEUE QueueHandle, PULONG Elements);

IM_EX_CTIBASE INT PeekQueue (HCTIQUEUE QueueHandle,
                             PREQUESTDATA Request,
                             PULONG DataSize,
                             PPVOID Data,
                             PULONG Element,
                             BOOL32 WaitFlag,
                             PBYTE  Priority);

IM_EX_CTIBASE INT ReadQueue (HCTIQUEUE QueueHandle,
                             PREQUESTDATA Request,
                             PULONG DataSize,
                             PPVOID Data,
                             ULONG Element,
                             BOOL32 WaitFlag,
                             PBYTE Priority,
                             ULONG *pElementCount = NULL);

IM_EX_CTIBASE INT PurgeQueue (HCTIQUEUE QueueHandle);

IM_EX_CTIBASE INT SearchQueue( HCTIQUEUE QueueHandle, void *ptr, BOOL (*myFunc)(void*, void*), bool useFirstElement = true);

/*IM_EX_CTIBASE INT SearchQueue (HCTIQUEUE QueueHandle,
                               PVOID CompareData,
                               ULONG CompareDataSize,
                               ULONG CompareDataOffset,
                               PREQUESTDATA RequestData,
                               PULONG   DataSize,
                               PPVOID Data,
                               PULONG Element,
                               PBYTE  Priority,
                               PPVOID Stupid);*/

IM_EX_CTIBASE INT CleanQueue( HCTIQUEUE QueueHandle,
                              void *findFuncPtr,
                              bool (*myFindFunc)(void*, PQUEUEENT),
                              void (*myCleanFunc)(void*, void*),
                              void *cleanFuncPtr = NULL);

IM_EX_CTIBASE INT ApplyQueue( HCTIQUEUE QueueHandle, void *ptr, void (*myFunc)(void*, void*));

IM_EX_CTIBASE INT AdjustPriority(HCTIQUEUE QueueHandle, ULONG RequestID, INT &NumberFound, ULONG TopPriority = MAXPRIORITY, INT Count = 0);

IM_EX_CTIBASE INT GetIndividualRequestStartPos(HCTIQUEUE QueueHandle, ULONG RequestID, UINT &StartPos);

IM_EX_CTIBASE INT GetRequestCountAndPriority(HCTIQUEUE QueueHandle, ULONG RequestID, ULONG &Count, ULONG &Priority);

#endif /* QUEUE_H */
