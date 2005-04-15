//---------------------------------------------------------------------------
// File: rtp_api.c
//
// Description: Implements the RTP API
//
// Copyright (c) 2000 LiveData, Inc.
// All rights reserved.
//
// Author: cmd
//
// References:
//
// Detailed Description: This module implements the following user routines
//  for the RTP Initiator API, as well as support routines:
//
//      Init() - called by user to spawn a socket connection management
//      thread.
//
//      Get() - called by user to generate RTP read requests to an RTP peer.
//      User also provides a completion function and argument to be placed
//      in a dispatch queue when the RTP read request completes.
//
//      Put() - called by user to generate RTP write requests to an RTP
//      peer. User also provides a completions function and argument to
//      be places in a dispatch queue when the RTP write completes.
//
//      Dispatch() - called by user to execute user provided completion
//      functions.
//
//
// Revision History: 6/16/99 cmd Initial code.
//      
//
//---------------------------------------------------------------------------


#define _WIN32_WINNT 0x0400
#include <windows.h>
#include <process.h>
#include <stdio.h>
#include "include\rtp_apiw.h"
#include "include\rtp.h"


// RTP message sizes and offsets.  The values are for the enhanced RTP
// protocol which includes a message length prefix, as well as 32-bit
// RTP variable address lengths.

#define RTP_LEN_SIZE                4
#define RTP_HDR_SIZE                4
#define RTP_HDRPLUSLENGTH_SIZE      RTP_LEN_SIZE +  RTP_HDR_SIZE
#define RTP_VARSPEC_SIZE            8

#define RTP_MSG_SIZE_OFFSET         0
#define RTP_OPCODE_OFFSET           0
#define RTP_STATUS_OFFSET           1
#define RTP_MSG_RESERVED_OFFSET     1
#define RTP_MODIFIER_OFFSET         1
#define RTP_SEQ_NUM_OFFSET          2
#define RTP_SEQ_NUM_LOW_OFFSET      2
#define RTP_SEQ_NUM_HIGH_OFFSET     3

#define RTP_VARSPEC_ADDR_OFFSET     0
#define RTP_VARSPEC_LENGTH_OFFSET   4
#define RTP_DATA_OFFSET             4

#define SEND_TIMEOUT    5000
#define MAX_BACKLOG     8


//----------------------------------------------------------------
// struct type for socket send queue element.
typedef struct tagSocketQueueElement
{
    struct tagSocketQueueElement    *pNext;
    unsigned char                   *pBuffer;
    unsigned long                   lLength;
    unsigned long                   lBytesSent;
} SOCKET_QUEUE_ELEMENT;

// struct type for the dispatch queue.
typedef struct tagDispatchQueueElement
{
    struct tagDispatchQueueElement  *pNext;
    void                            (*CompletionFunction)
                                        (RTP_STATUS RtpStatus,
                                        void        *pCompletionArgument);
    RTP_STATUS                      RtpStatus;
    void                            *pCompletionArgument;
} DISPATCH_QUEUE_ELEMENT;



// struct type to manage a receive buffer.
typedef struct
{
    unsigned char   *pBuffer;
    unsigned long   lLength;
    unsigned long   lOffset;
    unsigned long   lLengthOffset;
} RECEIVE_BUFFER;

// struct type for user send queue elements and completion queue.
typedef struct tagSendQueueElement
{
    struct tagSendQueueElement  *pNext;
    void                        *pSocketConnection;
    unsigned char               *pOutBuffer;
    unsigned char               *pInBuffer;
    unsigned long               lLength;
    unsigned long               lExpectedPDUSize;
    unsigned short              nSequenceNumber;
    void                        (*CompletionFunction)
                                    (RTP_STATUS RtpStatus,
                                     void       *pCompletionArgument);
    void                        *pCompletionArgument;
    HANDLE                      hTimerHandle;
    BOOL                        bNotifyFlag;

} SEND_QUEUE_ELEMENT, COMPLETION_QUEUE_ELEMENT;

// struct type to manage the RTP session.
typedef struct
{
    SEND_QUEUE_ELEMENT          *pSendQueueHead;
    CRITICAL_SECTION            SendQueueCSObject;
    unsigned short              nSendQueueBacklogCount;
    unsigned short              nMaxSendQueueBacklogCount;
    COMPLETION_QUEUE_ELEMENT    *pCompletionQueueHead;
    CRITICAL_SECTION            CompletionQueueCSObject;
    DISPATCH_QUEUE_ELEMENT      *pDispatchQueueHead;
    CRITICAL_SECTION            DispatchQueueCSObject;
    HANDLE                      hSendEvent;
    HANDLE                      hDispatchEvent;
    unsigned short volatile     nSequenceNumber;
    LARGE_INTEGER               TimeoutInterval;
    RTP_STATUS                  (*ReadHandler)
                                (HANDLE         hRtpApi,
                                unsigned long   lAddress,
                                unsigned long   lLength,
                                unsigned char   *pBuffer,
                                void            *userArgument);
    void                        *pReadArgument;
    RTP_STATUS                  (*WriteHandler)
                                (HANDLE         hRtpApi,
                                unsigned long   lAddress,
                                unsigned long   lLength,
                                unsigned char   *pBuffer,
                                void            *userArgument);
    void                        *pWriteArgument;
} RTP_SESSION;

// struct type used to characterize a socket connection.
typedef struct
{
    SOCKET                  Socket;
    WSAEVENT                SocketEvent;
    long                    lEventMask;
    BOOL volatile           bShutdown;
    BOOL volatile           bShutdownComplete;
    RECEIVE_BUFFER          ReceiveBuffer;
    SOCKET_STATE            eState;
    unsigned char           zIPAddress[16];
    unsigned short          nPort;
    CONNECT_ROLE            eConnectionRole;
    void                    (*MessageHandler)
                            (const char *pzText,
                            unsigned long nCode);
    SOCKET_QUEUE_ELEMENT    *pSocketQueueHead;
    RTP_SESSION             RtpSession;
} SOCKET_CONNECTION;

//-----------------------------------------------------------------------



// Following is a set of macros that manage a FIFO queue.

//  PUT_QUE(head, pentry, link): This macro puts the structure whose address is
//  "pentry" onto the end of the list whose list head is "head". "head" is
//  a of type pointer to that type of structure. "link" is the name of the
//  component of the structure used for linking this particular list and is
//  of type pointer to that type of structure.
#define PUT_QUE(head, pentry, link) \
    ((head) == NULL ? ((pentry)->link = (pentry)) : \
    ((pentry)->link = (head)->link, (head)->link = (pentry)), \
    (head) = (pentry))

#define IS_QUE_EMPTY(head) (head == NULL)

//  GET_QUE(head, pfirst, link): This macro removes the first entry from the
//  linked list with list header "head" and places the entry's address into the
//  variable "pfirst", and also returns that address. "pfirst" must be an
//  lvalue which is of type pointer to the structure supported by the list.
//  "link" is the name of the component of such a structure used for
//  linking this particular list. "link" is of type pointer to the
//  structure. The user must insure that the list is not empty before
//  invoking this macro.
#define GET_QUE(head, pfirst, link) \
((head) == ((pfirst) = (head)->link) ? ((head) = NULL) : \
((head)->link = (pfirst)->link), (pfirst))

//  TRY_GET_QUE(head, pfirst, link): If the linked list with list header
//  "head" is not empty, this macro removes the first entry and places it's
//  address into the variable "pfirst", and also returns that address. If
//  the list is empty, the macro sets "pfirst" to NULL and also returns NULL.
//  "pfirst" must be an lvalue which is of type pointer to the structure
//  supported by the list. "link" is the name of the component of such a
//  structure used for linking this particular list. "link" is of type
//  pointer to the structure.
#define TRY_GET_QUE(head, pfirst, link) (IS_QUE_EMPTY(head) ? \
    ((pfirst) = NULL, (pfirst)) : GET_QUE(head, pfirst, link))

//  GET_FIRST(head, link): If the linked list with list header "head" is not
//  empty, this macro returns the address of the first entry, but does not
//  remove the entry from the queue. If the list is empty, the macro returns
//  NULL. "link" is the name of the component of the list structure used for
//  linking this particular list.
#define GET_FIRST(head, link) (IS_QUE_EMPTY(head) ? NULL : (head)->link)

// Macro to call the user provided message handler it it exists.
#define USER_MESSAGE_HANDLER(text, code)                    \
{                                                           \
                                                            \
    if(pSocketConnection->MessageHandler)                   \
    {                                                       \
        ((*pSocketConnection->MessageHandler)(text, code)); \
    }                                                       \
}

// Global to track net memory allocation.
long glMemCount;




//---------------------------------------------------------------------------
//
// Function: _malloc()
//
// Description: Indirect call to malloc() that counts malloc()s.
//
// Arguments:   int nSize - size of requested block.
//      
//
//  Input Globals:
//      None
//
// Return Value: See doc for malloc().
//      
//
//  Argument Return Values:
//      None
//
//  Output Globals: glMemCount is incremented by one.
//      
//
// Limits:
//
// Notes: Useful for tracking down memory leaks.
//
//---------------------------------------------------------------------------

void *_malloc(int nSize)
{
    glMemCount++;
    return(malloc(nSize));
}



//---------------------------------------------------------------------------
//
// Function: _calloc()
//
// Description: Indirect call to calloc() that counts calloc()s.
//
// Arguments:   int nCount - number of elements in requested block.
//
//              int nSize - size of each element.
//      
//
//  Input Globals:
//      None
//
// Return Value: See doc for calloc().
//      
//
//  Argument Return Values:
//      None
//
//  Output Globals: glMemCount is incremented by one.
//      
//
// Limits:
//
// Notes: Useful for tracking down memory leaks.
//
//---------------------------------------------------------------------------

void *_calloc(int nCount, int nSize)
{
    glMemCount++;
    return(calloc(nCount, nSize));
}



//---------------------------------------------------------------------------
//
// Function: _free()
//
// Description: Indirect call to free() that counts free()s.
//
// Arguments:   void *addr - address of block to be freed.
//      
//
//  Input Globals:
//      None
//
// Return Value: See doc for free().
//      
//
//  Argument Return Values:
//      None
//
//  Output Globals: glMemCount decremented by one.
//      
//
// Limits:
//
// Notes: Useful for tracking down memory leaks.
//
//---------------------------------------------------------------------------

void _free(void *addr)
{
    glMemCount--;
    free(addr);
}




//---------------------------------------------------------------------------
//
// Function: Message()
//
// Description: Formats message text and error code and calls the user
//              provided message handler routine.
//
// Arguments:   SOCKET_CONNECTION   *pSocketConnection - pointer to a
//              SOCKET_CONNECTION struct.
//
//              const char *pzString - pointer to a string containing a
//              message.
//
//              int nLocalErrorCode - RTP API error code, if any, as defined
//              in rtp_api.h
//
//              int nSysErrorCode - system or library error code, if any.
//      
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void Message(SOCKET_CONNECTION   *pSocketConnection,
             const char *pzString,
             int nLocalErrorCode,
             int nSysErrorCode)
{
    unsigned long   lErrorCode;

    // The passed error code is a 32-bit value looking like this:
    //
    // Hi Word             Lo Word
    // xxxxxxxx xxxxxxxx | xxxxxxxx xxxxxxxx
    //                 ^   ^               ^
    // Hi word is a   _|   |               |_ Lo byte is RtpApi error code.
    // system error        |
    // code, if avail.     |_ Hi bit of the lo word is Fatal Error bit.
    //
    lErrorCode = nLocalErrorCode | (nSysErrorCode << 16);
    USER_MESSAGE_HANDLER(pzString, lErrorCode);
}


//---------------------------------------------------------------------------
//
// Function: FatalError()
//
// Description: Call on error deemed unrecoverable. Sends message to the
//              main thread and terminated this thread.
//
// Arguments:   pSocketConnection - pointer to a SOCKET_CONNECTION struct.
//
//              pzErrorString - pointer to NULL terminated string containing
//              error text.
//
//              int nLocalErrorCode - RTP API error code as defined in
//              rtp_api.h.
//
//              int nSysErrorCode - system or library error code, if avail.
//      
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void FatalError(SOCKET_CONNECTION   *pSocketConnection,
                const char *pzErrorString,
                int nLocalErrorCode,
                int nSysErrorCode)
{
    // Set the fatal error bit.
    nLocalErrorCode |= 0x8000;
    Message(pSocketConnection,
            pzErrorString,
            nLocalErrorCode,
            nSysErrorCode);
    _endthread();
}




//---------------------------------------------------------------------------
//
// Function: ResetReceiveBuffer()
//
// Description: Resets the socket receive buffer after a complete pdu has been
//              processed, or when the socket connection must be reset.
//
// Arguments: pSocketConnection - pointer to a SOCKET_CONNECTION struct.
//      
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void ResetReceiveBuffer(SOCKET_CONNECTION *pSocketConnection)
{
    if(pSocketConnection->ReceiveBuffer.pBuffer)
    {
        _free(pSocketConnection->ReceiveBuffer.pBuffer);
        pSocketConnection->ReceiveBuffer.pBuffer = NULL;
    }
    pSocketConnection->ReceiveBuffer.lLength = 0;
    pSocketConnection->ReceiveBuffer.lOffset = 0;
    pSocketConnection->ReceiveBuffer.lLengthOffset = 0;
}




//---------------------------------------------------------------------------
//
// Function: CloseSocket()
//
// Description: Shuts down the socket and cleans up the socket send queue.
//
// Arguments: pSocketConnection - pointer to a SOCKET_CONNECTION struct.
//      
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void CloseSocket(SOCKET_CONNECTION *pSocketConnection)
{
    SOCKET_QUEUE_ELEMENT   *pQueueFirst;

    // Close the socket.
    if(pSocketConnection->Socket)
    {
        closesocket(pSocketConnection->Socket);
        pSocketConnection->Socket = 0;
    }

    // Set state to Disconnected.
    pSocketConnection->eState = Disconnected;

    // Clear the send queue.
    while(TRY_GET_QUE(pSocketConnection->pSocketQueueHead,
                      pQueueFirst,
                      pNext))
    {
        if(pQueueFirst->pBuffer)
            _free(pQueueFirst->pBuffer);
        _free(pQueueFirst);
    }

    // Reset the receive buffer.
    ResetReceiveBuffer(pSocketConnection);

}





//---------------------------------------------------------------------------
//
// Function: CompletionQueueLookup()
//
// Description: Searches the completion queue for the element w/ a given
//              sequence number, and removes it from the queue.
//
// Arguments:   pSocketConnection - pointer to a SOCKET_CONNECTION struct.
//
//              nSequenceNumber - key for search in the queue.
//      
//
//  Input Globals:
//      None
//
// Return Value:    Returns a pointer to the queue element w/ the given
//                  sequence, or NULL if not found.
//      
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes: CompletionQueueLookup() should never be called directly, but instead
//        should be called through the UseCompletionQueue() function to
//        ensure no thread/thread or thread/interupt conflicts.
//
//---------------------------------------------------------------------------

COMPLETION_QUEUE_ELEMENT
*CompletionQueueLookup(SOCKET_CONNECTION *pSocketConnection,
                      unsigned short nSequenceNumber)
{
    COMPLETION_QUEUE_ELEMENT    *pMovingHead;
    COMPLETION_QUEUE_ELEMENT    *pCurrentCompletionQueueElem = NULL;
    COMPLETION_QUEUE_ELEMENT    *pMatchingCompletionQueueElem = NULL;

    // Walk the queue until we find the matching elem.
    for(pMovingHead = pSocketConnection->RtpSession.pCompletionQueueHead;
        pCurrentCompletionQueueElem = GET_FIRST(pMovingHead, pNext);
        pMovingHead = pCurrentCompletionQueueElem)
    {
        if(pCurrentCompletionQueueElem->nSequenceNumber == nSequenceNumber)
        {
            pMatchingCompletionQueueElem = pCurrentCompletionQueueElem;

            // If this was the list head, and the list is now empty,
            // null the list.
            if(pCurrentCompletionQueueElem->pNext ==
               pCurrentCompletionQueueElem)
            {
                pSocketConnection->RtpSession.pCompletionQueueHead = NULL;
            }
            // If this was the head, and the list is not empty...
            else if(pCurrentCompletionQueueElem ==
                    pSocketConnection->RtpSession.pCompletionQueueHead)
            {
                pMovingHead->pNext =  pCurrentCompletionQueueElem->pNext;
                pSocketConnection->RtpSession.pCompletionQueueHead =
                    pMovingHead;
            }
            // If this is the last elem of a now non-empty list, make the
            // prev elem point back to the head.
            else if(pCurrentCompletionQueueElem->pNext ==
                    pSocketConnection->RtpSession.pCompletionQueueHead)
            {
                pMovingHead->pNext =
                    pSocketConnection->RtpSession.pCompletionQueueHead;
            }
            // In all other cases, make the prev elem point to the elem
            // after the current elem.
            else
            {
                pMovingHead->pNext =  pCurrentCompletionQueueElem->pNext;
            }

            break;
        }
        else
        {
            // As this is a circular list, we need to see if we've run the
            // whole loop.
            if(pCurrentCompletionQueueElem ==
                pSocketConnection->RtpSession.pCompletionQueueHead)
            {
                break;
            }
        }
    }

    return(pMatchingCompletionQueueElem);
}





//---------------------------------------------------------------------------
//
// Function: UseSendQueue()
//
// Description: Wraps the necessary send queue manipulation routines in a
//              critical section to ensure a thread-safe queue.
//
// Arguments:   pSocketConnection - pointer to a SOCKET_CONNECTION struct.
//
//              pElement - pointer to a send queue element.
//
//              ppElementOut - pointer to pointer to a send queue element
//              to receive a returned element.
//
//              eOperation - identifies the queue operation to take place.
//      
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values: ppElementOut may contain a returned element.
//      
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void UseSendQueue(SOCKET_CONNECTION     *pSocketConnection,
                  SEND_QUEUE_ELEMENT    *pElement,
                  SEND_QUEUE_ELEMENT    **ppElementOut,
                  QUE_OPS               eOperation)
{
    EnterCriticalSection(&pSocketConnection->RtpSession.SendQueueCSObject);

    switch(eOperation)
    {
    case PutQueue:
        PUT_QUE(pSocketConnection->RtpSession.pSendQueueHead,
                pElement,
                pNext);
        break;
    case GetQueue:
        TRY_GET_QUE(pSocketConnection->RtpSession.pSendQueueHead,
                    *ppElementOut,
                    pNext);
        break;
    case CheckQueue:
        *ppElementOut = GET_FIRST(pSocketConnection->RtpSession.pSendQueueHead,
                  pNext);
        break;
    default:
        break;
    }

    LeaveCriticalSection(&pSocketConnection->RtpSession.SendQueueCSObject);
}



//---------------------------------------------------------------------------
//
// Function: UseCompletionQueue()
//
// Description: Wraps the necessary completion queue manipulation routines in a
//              critical section to ensure a thread-safe queue.
//
// Arguments:   pSocketConnection - pointer to a SOCKET_CONNECTION struct.
//
//              pElement - pointer to a completion queue element.
//
//              ppElementOut - pointer to pointer to a completion queue element
//              to receive a returned element.
//
//              nSequenceNumber - sequence number to use as search key.
//
//              eOperation - identifies the queue operation to take place.
//      
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values: ppElementOut may contain a returned element.
//      
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------
void UseCompletionQueue(SOCKET_CONNECTION           *pSocketConnection,
                        COMPLETION_QUEUE_ELEMENT    *pElement,
                        COMPLETION_QUEUE_ELEMENT    **ppElementOut,
                        unsigned short              nSequenceNumber,
                        QUE_OPS                     eOperation)
{
    EnterCriticalSection(&pSocketConnection->RtpSession.
                         CompletionQueueCSObject);

    switch(eOperation)
    {
    case PutQueue:
        PUT_QUE(pSocketConnection->RtpSession.pCompletionQueueHead,
                pElement,
                pNext);
        break;
    case GetQueue:
        TRY_GET_QUE(pSocketConnection->RtpSession.pCompletionQueueHead,
                    *ppElementOut,
                    pNext);
        break;
    case CheckQueue:
        *ppElementOut = GET_FIRST(pSocketConnection->RtpSession.
                                  pCompletionQueueHead,
                                  pNext);
        break;
    case LookupQueue:
        *ppElementOut = CompletionQueueLookup(pSocketConnection,
                                              nSequenceNumber);
        break;
    case RemoveQueue:
        *ppElementOut = CompletionQueueLookup(pSocketConnection,
                                              nSequenceNumber);
        break;
    default:
        break;
    }

    LeaveCriticalSection(&pSocketConnection->RtpSession.
                         CompletionQueueCSObject);
}



//---------------------------------------------------------------------------
//
// Function: UseDispatchQueue()
//
// Description: Wraps the necessary dispatch queue manipulation routines in a
//              critical section to ensure a thread-safe queue.
//
// Arguments:   pSocketConnection - pointer to a SOCKET_CONNECTION struct.
//
//              pElement - pointer to a dispatch queue element.
//
//              ppElementOut - pointer to pointer to a dispatch queue element
//              to receive a returned element.
//
//              eOperation - identifies the queue operation to take place.
//      
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values: ppElementOut may contain a returned element.
//      
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------
void UseDispatchQueue(SOCKET_CONNECTION         *pSocketConnection,
                      DISPATCH_QUEUE_ELEMENT    *pElement,
                      DISPATCH_QUEUE_ELEMENT    **ppElementOut,
                      QUE_OPS                   eOperation)
{
    EnterCriticalSection(&pSocketConnection->RtpSession.DispatchQueueCSObject);

    switch(eOperation)
    {
    case PutQueue:
        PUT_QUE(pSocketConnection->RtpSession.pDispatchQueueHead,
                pElement,
                pNext);
        break;
    case GetQueue:
        TRY_GET_QUE(pSocketConnection->RtpSession.pDispatchQueueHead,
                    *ppElementOut,
                    pNext);
        break;
    case CheckQueue:
        *ppElementOut =
            GET_FIRST(pSocketConnection->RtpSession.pDispatchQueueHead,
                      pNext);
        break;
    default:
        break;
    }

    LeaveCriticalSection(&pSocketConnection->RtpSession.DispatchQueueCSObject);
}





//---------------------------------------------------------------------------
//
// Function: Dispatch()
//
// Description: Called by user to execute a batch of enqueued completion
//              function.
//
// Arguments:   hRtpApi - a handle to identify this session.
//
//              nExecuteCount - max number of completion functions to execute.
//              Dispatch() will only execute as many completion functions as
//              are in the queue.
//      
//
//  Input Globals:
//      None
//
// Return Value: number of completion functions executed.
//      
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

int Dispatch(HANDLE hRtpApi, int nExecuteCount)
{
    SOCKET_CONNECTION           *pSocketConnection =
        (SOCKET_CONNECTION*)hRtpApi;
    DISPATCH_QUEUE_ELEMENT      *pCurrentDispatchQueueElem = NULL;
    int                         iIndex;

    // Walk the queue and execute up to nExecuteCount completion functions.
    for(iIndex = nExecuteCount; iIndex; iIndex--)
    {
        UseDispatchQueue(pSocketConnection,
                         NULL,
                         &pCurrentDispatchQueueElem,
                         GetQueue);
        if(!pCurrentDispatchQueueElem)
        {
            break;
        }

        // Execute the function.
        if(pCurrentDispatchQueueElem->CompletionFunction)
        {
            (*pCurrentDispatchQueueElem->CompletionFunction)
                (pCurrentDispatchQueueElem->RtpStatus,
                pCurrentDispatchQueueElem->pCompletionArgument);
        }
        // Free the dispatch queue elem.
        _free(pCurrentDispatchQueueElem);
    }

    return(nExecuteCount - iIndex);
}





//---------------------------------------------------------------------------
//
// Function: KillQueues()
//
// Description: Cleans up all queues for shutdown or for an application level
//              reset of the API.
//
// Arguments: pSocketConnection - pointer to a SOCKET_CONNECTION struct.
//      None
//
//  Input Globals:
//      
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void KillQueues(SOCKET_CONNECTION *pSocketConnection)
{
    SEND_QUEUE_ELEMENT          *pSendQueueElement;
    COMPLETION_QUEUE_ELEMENT    *pCompletionQueueElem;
    DISPATCH_QUEUE_ELEMENT      *pDispatchQueueElem;

    for(UseSendQueue(pSocketConnection,
                     NULL,
                     &pSendQueueElement,
                     GetQueue);
        pSendQueueElement != NULL;
        UseSendQueue(pSocketConnection,
                     NULL,
                     &pSendQueueElement,
                     GetQueue))
    {
        if(pSendQueueElement->pOutBuffer)
        {
            _free(pSendQueueElement->pOutBuffer);
        }

        _free(pSendQueueElement);
    }

    for(UseCompletionQueue(pSocketConnection,
                     NULL,
                     &pCompletionQueueElem,
                     0,
                     GetQueue);
        pCompletionQueueElem != NULL;
        UseCompletionQueue(pSocketConnection,
                           NULL,
                           &pCompletionQueueElem,
                           0,
                           GetQueue))
    {
        CancelWaitableTimer(pCompletionQueueElem->hTimerHandle);
        CloseHandle(pCompletionQueueElem->hTimerHandle);
        _free(pCompletionQueueElem);
    }

    for(UseDispatchQueue(pSocketConnection,
                     NULL,
                     &pDispatchQueueElem,
                     GetQueue);
        pDispatchQueueElem != NULL;
        UseDispatchQueue(pSocketConnection,
                         NULL,
                         &pDispatchQueueElem,
                         GetQueue))
    {
        _free(pDispatchQueueElem);
    }

}


//---------------------------------------------------------------------------
//
// Function: Get()
//
// Description: Called by user to create and send a RTP read request.
//
// Arguments: hRtpApi - a handle to identify this session.
//
//              lpAddresses - pointer to an array of 32-bit addresses.
//
//              lpLengths - pointer to an array of 32-bit lengths.
//
//              nCount - count of elements in above two arrays.
//
//              pBuffer - buffer to receive read data. Must be long enough
//              to hold all requested data.
//
//              CompletionFunction - pointer to a user defined completion
//              function.
//
//              pCompletionArgument - arg to be passed to CompletionFunction.
//      
//
//  Input Globals:
//      None
//
// Return Value:    Returns type REQUEST_STATUS. Return values:
//
//                  Success - request successfully enqueued for send.
//                  Blocked - request not enqueued for send due to too many
//                  outstanding requests.
//                  Failure - request failed. Can be due to call to Exit().
//      
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------


REQUEST_STATUS Get(HANDLE               hRtpApi,
                   unsigned long        *lpAddresses,
                   unsigned long        *lpLengths,
                   int                  nCount,
                   unsigned char        *pBuffer,
                   void                 (*CompletionFunction)
                                        (RTP_STATUS RtpStatus,
                                        void        *pCompletionArgument),
                   void                 *pCompletionArgument)
{
    SOCKET_CONNECTION   *pSocketConnection =
        (SOCKET_CONNECTION*)hRtpApi;
    unsigned long       lTotalLength;
    unsigned char       *pRequestBuffer;
    unsigned char       *pPDUIndex;
    unsigned long       lExpectedPDUSize;
    SEND_QUEUE_ELEMENT  *pSendQueueElem;
    unsigned short      nSequenceNumber =
        pSocketConnection->RtpSession.nSequenceNumber++;

    // See if we're in shutdown.
    if(pSocketConnection->bShutdown || pSocketConnection->eState != Connected)
    {
        return(Failure);
    }

    // See if this request puts us over the MAX_SEND_QUEUE_BACKLOG limit.
    if(pSocketConnection->RtpSession.nSendQueueBacklogCount >=
       pSocketConnection->RtpSession.nMaxSendQueueBacklogCount)
    {
        char zTextBuffer[128];
        sprintf(zTextBuffer,
                "RTP API:Send queue at limit: %d",
                pSocketConnection->RtpSession.nMaxSendQueueBacklogCount);
        Message(pSocketConnection,
                zTextBuffer,
                0,
                0);

        // Arm the event to tell the socket thread send PDUs.
        if(!SetEvent(pSocketConnection->RtpSession.hSendEvent))
        {
            FatalError(pSocketConnection,
                       "RTP API:SetEvent() failed for send event",
                       RTPAPIERROR_SET_EVENT,
                       GetLastError());
        }
        return(Blocked);
    }
    else
    {
        pSocketConnection->RtpSession.nSendQueueBacklogCount++;
    }

    // Calculate the total length of the DPU.
    lTotalLength =  RTP_HDRPLUSLENGTH_SIZE + (nCount * RTP_VARSPEC_SIZE);

    // Allocate a buffer for the request.
    pRequestBuffer = _malloc(lTotalLength);
    if(!pRequestBuffer)
    {
        FatalError(pSocketConnection,
                   "RTP API:Error allocating memory",
                   RTPAPIERROR_MEM_ALLOC,
                   0);
    }

    // Build the RTP header.

    pPDUIndex = pRequestBuffer;
    // Put the length in the PDU.
    *((unsigned long*)(pPDUIndex + RTP_MSG_SIZE_OFFSET)) = lTotalLength;

    // Put the RTP protocol header in the PDU.
    *(pPDUIndex + RTP_OPCODE_OFFSET + RTP_LEN_SIZE) = RTP_READ_REQ;

    // Put the 32-bit length indicator bit in the reserved byte and put it
    // in the PDU.
    *(pPDUIndex + RTP_MSG_RESERVED_OFFSET + RTP_LEN_SIZE) = 1;

    // Put the sequence number in the PDU.
    *((unsigned short*)(pPDUIndex + RTP_SEQ_NUM_OFFSET + RTP_LEN_SIZE)) =
        nSequenceNumber;

    // Build each variable specifier and add it to the PDU. Also calculate
    // the expected total size of the response PDU.
    for(pPDUIndex = pRequestBuffer + RTP_HDRPLUSLENGTH_SIZE,
            lExpectedPDUSize = RTP_HDR_SIZE;
        nCount;
        pPDUIndex += RTP_VARSPEC_SIZE, nCount--)
    {
        // Add the current address to the PDU.
        *((unsigned long*)pPDUIndex) = *lpAddresses++;

        // Add the current length to the PDU.
        *((unsigned long*)(pPDUIndex + sizeof(*lpAddresses))) = *lpLengths;

        // Increment the expected PDU size.
        lExpectedPDUSize += *lpLengths++;

    } // end for

    // Allocate a user send queue element.
    pSendQueueElem = _calloc(sizeof(*pSendQueueElem), 1);
    if(!pSendQueueElem)
    {
        FatalError(pSocketConnection,
                   "RTP API:Error allocating memory",
                   RTPAPIERROR_MEM_ALLOC,
                   0);
    }

    // Fill it in and enqueue it.
    pSendQueueElem->pSocketConnection = (void*)pSocketConnection;
    pSendQueueElem->pOutBuffer = pRequestBuffer;
    pSendQueueElem->pInBuffer = pBuffer;
    pSendQueueElem->lLength =  lTotalLength;
    pSendQueueElem->lExpectedPDUSize = lExpectedPDUSize;
    pSendQueueElem->nSequenceNumber = nSequenceNumber;
    pSendQueueElem->CompletionFunction = CompletionFunction;
    pSendQueueElem->pCompletionArgument = pCompletionArgument;
    pSendQueueElem->bNotifyFlag = FALSE;
    UseSendQueue(pSocketConnection,
                 pSendQueueElem,
                 NULL,
                 PutQueue);

    // Arm the event to tell the socket thread send PDUs.
    if(!SetEvent(pSocketConnection->RtpSession.hSendEvent))
    {
        FatalError(pSocketConnection,
                   "RTP API:SetEvent() failed for send event",
                   RTPAPIERROR_SET_EVENT,
                   GetLastError());
    }

    return(Success);
}


//---------------------------------------------------------------------------
//
// Function: Put()
//
// Description: Called by user to create and send a RTP write of notify
//              request.
//
// Arguments: hRtpApi - pointer to a SOCKET_CONNECTION struct.
//
//              lpAddresses - pointer to an array of 32-bit addresses.
//
//              lpLengths - pointer to an array of 32-bit lengths.
//
//              nCount - count of elements in above two arrays.
//
//              pBuffer - buffer to containing the data to write in contiguous,
//              non-padded form.
//
//              CompletionFunction - pointer to a user defined completion
//              function.
//
//              pCompletionArgument - arg to be passed to CompletionFunction.
//
//              bNotifyFlag - if TRUE, specfies an RTP Notify requests instead
//              if RTP Write.
//      
//
//  Input Globals:
//      None
//
// Return Value:    Returns type REQUEST_STATUS. Return values:
//
//                  Success - request successfully enqueued for send.
//                  Blocked - request not enqueued for send due to too many
//                  outstanding requests.
//                  Failure - request failed. Can be due to call to Exit().
//      
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

REQUEST_STATUS Put(HANDLE               hRtpApi,
                   unsigned long        *lpAddresses,
                   unsigned long        *lpLengths,
                   int                  nCount,
                   unsigned char        *pBuffer,
                   void                 (*CompletionFunction)
                                        (RTP_STATUS RtpStatus,
                                        void        *pCompletionArgument),
                   void                 *pCompletionArgument,
                   BOOL                 bNotifyFlag)
{
    SOCKET_CONNECTION   *pSocketConnection =
        (SOCKET_CONNECTION*)hRtpApi;
    unsigned long       lTotalLength;
    unsigned char       *pRequestBuffer;
    unsigned char       *pPDUIndex;
    int                 nLocalCount;
    unsigned long       *lpLocalLengths;
    unsigned char       *pBufferIndex;
    SEND_QUEUE_ELEMENT  *pSendQueueElem;
    unsigned short  nSequenceNumber =
        pSocketConnection->RtpSession.nSequenceNumber++;

    // See if we're in shutdown.
    if(pSocketConnection->bShutdown)
    {
        return(Blocked);
    }

    // See if this request puts us over the MAX_SEND_QUEUE_BACKLOG limit.
    if(pSocketConnection->RtpSession.nSendQueueBacklogCount >=
       pSocketConnection->RtpSession.nMaxSendQueueBacklogCount)
    {
        char zTextBuffer[128];
        sprintf(zTextBuffer,
                "RTP API:Send queue at limit: %d",
                pSocketConnection->RtpSession.nMaxSendQueueBacklogCount);
        Message(pSocketConnection,
                zTextBuffer,
                0,
                0);

        // Arm the event to tell the socket thread to send PDUs.
        if(!SetEvent(pSocketConnection->RtpSession.hSendEvent))
        {
            FatalError(pSocketConnection,
                       "RTP API:SetEvent() failed for send event",
                       RTPAPIERROR_SET_EVENT,
                       GetLastError());
        }
        return(Blocked);
    }
    else
    {
        pSocketConnection->RtpSession.nSendQueueBacklogCount++;
    }

    // Calculate the total length of the PDU.
    for(nLocalCount = nCount,
            lpLocalLengths = lpLengths,
            lTotalLength = RTP_HDRPLUSLENGTH_SIZE +
                (nCount * RTP_VARSPEC_SIZE);
        nLocalCount;
        nLocalCount--, lpLocalLengths++)
    {
        lTotalLength += *lpLocalLengths;
    }

    // Allocate a buffer for the request.
    pRequestBuffer = _malloc(lTotalLength);
    if(!pRequestBuffer)
    {
        FatalError(pSocketConnection,
                   "RTP API:Error allocating memory",
                   RTPAPIERROR_MEM_ALLOC,
                   0);
    }

    // Build the RTP header.

    pPDUIndex = pRequestBuffer;
    // Put the length in the PDU.
    *((unsigned long*)(pPDUIndex + RTP_MSG_SIZE_OFFSET)) = lTotalLength;

    // Put the RTP protocol header in the PDU.
    if(bNotifyFlag)
    {
        *(pPDUIndex + RTP_OPCODE_OFFSET + RTP_LEN_SIZE) = RTP_NOTIFY;
    }
    else
    {
        *(pPDUIndex + RTP_OPCODE_OFFSET + RTP_LEN_SIZE) = RTP_WRITE_REQ;
    }

    // Put the 32-bit length indicator bit in the reserved byte and put it
    // in the PDU.
    *(pPDUIndex + RTP_MSG_RESERVED_OFFSET + RTP_LEN_SIZE) = 1;

    // Put the sequence number in the PDU.
    *((unsigned short*)(pPDUIndex + RTP_SEQ_NUM_OFFSET + RTP_LEN_SIZE)) =
        nSequenceNumber;


    // Build each variable specifier and add it to the PDU.
    for(pPDUIndex = pRequestBuffer + RTP_HDRPLUSLENGTH_SIZE,
            nLocalCount = nCount,
            pBufferIndex = pBuffer;
        nLocalCount;
        pPDUIndex += (RTP_VARSPEC_SIZE + *lpLengths),
        pBufferIndex += *lpLengths++,
        nLocalCount--)
    {
        // Add the current address to the PDU.
        *((unsigned long*)pPDUIndex) = *lpAddresses++;

        // Add the current length to the PDU.
        *((unsigned long*)(pPDUIndex + sizeof(*lpAddresses))) = *lpLengths;

        // Add the data to the PDU.
        memcpy(pPDUIndex + RTP_VARSPEC_SIZE, pBufferIndex, *lpLengths);

    } // end for

    // Allocate a send user send queue element.
    pSendQueueElem = _calloc(sizeof(*pSendQueueElem), 1);
    if(!pSendQueueElem)
    {
        FatalError(pSocketConnection,
                   "RTP API:Error allocating memory",
                   RTPAPIERROR_MEM_ALLOC,
                   0);
    }

    // Fill it in and enqueue it.
    pSendQueueElem->pSocketConnection = (void*)pSocketConnection;
    pSendQueueElem->pOutBuffer = pRequestBuffer;
    pSendQueueElem->pInBuffer = NULL;
    pSendQueueElem->lLength =  lTotalLength;
    pSendQueueElem->lExpectedPDUSize = RTP_HDR_SIZE;
    pSendQueueElem->nSequenceNumber = nSequenceNumber;
    pSendQueueElem->CompletionFunction = CompletionFunction;
    pSendQueueElem->pCompletionArgument = pCompletionArgument;
    pSendQueueElem->bNotifyFlag = bNotifyFlag;
    UseSendQueue(pSocketConnection,
                 pSendQueueElem,
                 NULL,
                 PutQueue);

    // Arm the event to tell the socket thread to send PDUs.
    if(!SetEvent(pSocketConnection->RtpSession.hSendEvent))
    {
         FatalError(pSocketConnection,
                    "RTP API:SetEvent() failed for send event",
                    RTPAPIERROR_SET_EVENT,
                    GetLastError());
    }

    return(Success);
}






//---------------------------------------------------------------------------
//
// Function: CheckConnection()
//
// Description: Checks to see if a socket connection attempt has succeeded.
//
// Arguments:   pSocketConnection - pointer to a SOCKET_CONNECTION struct.
//      None
//
//  Input Globals:
//      None
//
// Return Value: Returns the state of the socket connection.
//      
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

SOCKET_STATE CheckConnection(SOCKET_CONNECTION *pSocketConnection)
{
    fd_set                  ConnectEvent;
    const struct timeval    SelectTimeOut = {2, 0}; // One second.


    // Zero out the fd_set struct.
    FD_ZERO(&ConnectEvent);

    // Set up to get report on write events, which will indicate a
    // successful connection.
    FD_SET(pSocketConnection->Socket, &ConnectEvent);

    // Call select() to see if we have write events. SelectTimeOut is set to
    // 2 seconds. If we don't get a connect notification in 2 seconds,
    // we assume there's nobody listening for us.
    if(select(0, 0, &ConnectEvent, 0, &SelectTimeOut) > 0)
    {
        return(Connected);
    }
    else
    {
        return(Disconnected);
    }
}






//---------------------------------------------------------------------------
//
// Function: MakeConnection()
//
// Description: Attempts to make a socket connection.
//
// Arguments:   pSocketConnection - pointer to a SOCKET_CONNECTION struct.
//      
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void MakeConnection(SOCKET_CONNECTION *pSocketConnection)
{
    const unsigned long klNonBlockingMode = TRUE;
    const unsigned long klReceiveBufferSize = (64 * 1024);
    struct sockaddr_in  SocketAddress;
    char                zTextbuffer[128];

    // Get a socket handle from Winsock.
    pSocketConnection->Socket = socket(AF_INET,
                                       SOCK_STREAM,
                                       0);
    if(pSocketConnection->Socket == INVALID_SOCKET)
    {
        FatalError(pSocketConnection,
                   "RTP API:socket() returned error",
                   RTPAPIERROR_SOCKET,
                   WSAGetLastError());
    }

    // Set socket to non-blocking mode.
    if(ioctlsocket(pSocketConnection->Socket,
                   FIONBIO,
                   (unsigned long*)&klNonBlockingMode))
    {
        FatalError(pSocketConnection,
                   "RTP API:ioctl() returned error",
                   RTPAPIERROR_IOCTL,
                   WSAGetLastError());
    }

    // Set socket max receive buffer size.
    if(setsockopt(pSocketConnection->Socket,
                  SOL_SOCKET,
                  SO_RCVBUF,
                  (const char*)&klReceiveBufferSize,
                   sizeof(klReceiveBufferSize)))
    {
        FatalError(pSocketConnection,
                   "RTP API:setsockopt() returned error",
                   RTPAPIERROR_SETSOCKOPT,
                   WSAGetLastError());
    }

    // Zero out the sockaddr_in struct.
    memset(&SocketAddress, 0, sizeof(SocketAddress));

    // Fill in the sockaddr_in struct.
    SocketAddress.sin_addr.s_addr =
        inet_addr(pSocketConnection->zIPAddress);
    SocketAddress.sin_port =
        htons(pSocketConnection->nPort);
    SocketAddress.sin_family = AF_INET;

    // Print message to screen.
    sprintf(zTextbuffer,
            "RTP API:Attemping to connect to %s:%d",
            pSocketConnection->zIPAddress,
            pSocketConnection->nPort);
    Message(pSocketConnection,
            zTextbuffer,
            0,
            0);

    // Go ahead and connect.
    if(connect(pSocketConnection->Socket,
               (const struct sockaddr*)&SocketAddress,
               sizeof(SocketAddress))
       && WSAGetLastError() != WSAEWOULDBLOCK)
    {
        CloseSocket(pSocketConnection);
        return;
    }

    // Check if connection actually succeeded.
    pSocketConnection->eState = CheckConnection(pSocketConnection);
    if(pSocketConnection->eState == Connected)
    {
        sprintf(zTextbuffer,
                "RTP API:Connected to %s:%d",
                pSocketConnection->zIPAddress,
                pSocketConnection->nPort);
        Message(pSocketConnection,
                zTextbuffer,
                0,
                0);
        pSocketConnection->lEventMask = FD_READ;
    }
    else
    {
        CloseSocket(pSocketConnection);
        Message(pSocketConnection,
                "RTP API:Connection attempt timed out",
                0,
                0);
    }


}



//---------------------------------------------------------------------------
//
// Function: SetupListen()
//
// Description: Sets up a socket to listen for incoming connections.
//
// Arguments:   SOCKET_CONNECTION *pSocketConnection - pointer to a
//              SOCKET_CONNECTION struct.
//      
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void SetupListen(SOCKET_CONNECTION *pSocketConnection)
{
    const unsigned long klNonBlockingMode = TRUE;
    const unsigned long klReceiveBufferSize = (64 * 1024);
    struct sockaddr_in  SocketAddress;
    char                zTextbuffer[128];

    // Get a socket handle from Winsock.
    pSocketConnection->Socket = socket(AF_INET,
                                       SOCK_STREAM,
                                       0);
    if(pSocketConnection->Socket == INVALID_SOCKET)
    {
        FatalError(pSocketConnection,
                   "RTP API:socket() returned error",
                   RTPAPIERROR_SOCKET,
                   WSAGetLastError());
    }

    // Set socket to non-blocking mode.
    if(ioctlsocket(pSocketConnection->Socket,
                   FIONBIO,
                   (unsigned long*)&klNonBlockingMode))
    {
        FatalError(pSocketConnection,
                   "RTP API:ioctl() returned error",
                   RTPAPIERROR_IOCTL,
                   WSAGetLastError());
    }

    // Set socket max receive buffer size.
    if(setsockopt(pSocketConnection->Socket,
                  SOL_SOCKET,
                  SO_RCVBUF,
                  (const char*)&klReceiveBufferSize,
                   sizeof(klReceiveBufferSize)))
    {
         FatalError(pSocketConnection,
                   "RTP API:setsockopt() returned error",
                   RTPAPIERROR_SETSOCKOPT,
                   WSAGetLastError());
    }

    // Zero out the sockaddr_in struct.
    memset(&SocketAddress, 0, sizeof(SocketAddress));

    // Fill in the sockaddr_in struct.
    SocketAddress.sin_port =
        htons(pSocketConnection->nPort);
    SocketAddress.sin_family = AF_INET;

    // Send message.
    sprintf(zTextbuffer,
            "RTP API:Setting up listen on %d",
            pSocketConnection->nPort);
    Message(pSocketConnection,
            zTextbuffer,
            0,
            0);

    // Bind the socket.
    if(bind(pSocketConnection->Socket,
            (const struct sockaddr*)&SocketAddress,
            sizeof(SocketAddress)) == SOCKET_ERROR)
    {
        FatalError(pSocketConnection,
                   "RTP API:bind() returned error",
                   RTPAPIERROR_BIND,
                   WSAGetLastError());
    }

    // Go ahead and listen.
    if(listen(pSocketConnection->Socket, 2) == SOCKET_ERROR)
    {
        FatalError(pSocketConnection,
                   "RTP API:listen() returned error",
                   RTPAPIERROR_LISTEN,
                   WSAGetLastError());
    }

    // Set state.
    pSocketConnection->eState = Listening;
}



//---------------------------------------------------------------------------
//
// Function: AcceptConnection()
//
// Description: Attempts to accept an incoming connection.
//
// Arguments:   SOCKET_CONNECTION *pSocketConnection - pointer to a
//              SOCKET_CONNECTION struct.
//      
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void AcceptConnection(SOCKET_CONNECTION *pSocketConnection)
{
    SOCKET              TempSocket;
    struct sockaddr_in  SocketAddress;
    int                 nAddressLength;

    // Set up a sockaddr_in struct and a length variable.
    memset(&SocketAddress, 0, sizeof(SocketAddress));
    nAddressLength = sizeof(SocketAddress);

    // Try to accept a connecting. If this call fails, noone is trying to
    // connect.
    TempSocket = accept(pSocketConnection->Socket,
                        (struct sockaddr*)&SocketAddress,
                        &nAddressLength);

    if(TempSocket == INVALID_SOCKET)
    {
        if(WSAGetLastError() == WSAEWOULDBLOCK)
        {
            // Sleep for a second before checking again.
            SleepEx(1000, FALSE);
        }
        else
        {
            FatalError(pSocketConnection,
                       "RTP API:accept() returned error",
                       RTPAPIERROR_ACCEPT,
                       WSAGetLastError());
        }
    }
    else
    {
        char zTextbuffer[128];

        // We have a connection request and we've accepted it.
        closesocket(pSocketConnection->Socket);
        pSocketConnection->Socket = TempSocket;
        pSocketConnection->eState = Connected;
        pSocketConnection->lEventMask = FD_READ;
        // Send message.
        sprintf(zTextbuffer,
                "RTP API:Accepting connection from:%d.%d.%d.%d:%d",
                SocketAddress.sin_addr.S_un.S_un_b.s_b1,
                SocketAddress.sin_addr.S_un.S_un_b.s_b2,
                SocketAddress.sin_addr.S_un.S_un_b.s_b3,
                SocketAddress.sin_addr.S_un.S_un_b.s_b4,
                SocketAddress.sin_port);
        Message(pSocketConnection,
                zTextbuffer,
                0,
                0);
    }
}





//---------------------------------------------------------------------------
//
// Function: SendData()
//
// Description: Attempts to send data on the socket. If there is already data
//              enqueued for send, or if some or all of the data couldn't be
//              sent, then the unsent portion of the data is enqueued.
//
// Arguments:   pSocketConnection - pointer to a SOCKET_CONNECTION struct.
//
//              pBuffer - pointer to buffer containing the data to send.
//
//              lLength - length of data in pBuffer.
//      
//
//  Input Globals:
//      None
//
// Return Value:    Returns the number of bytes actually sent.
//      
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

int SendData(SOCKET_CONNECTION  *pSocketConnection,
             unsigned char      *pBuffer,
             unsigned long      lLength)
{
    int                     nBytesSent;
    SOCKET_QUEUE_ELEMENT    *pNewElem;

    // See if there's any enqueued data. If not, try a send, else, enqueue
    // this data.

    if(!pSocketConnection->pSocketQueueHead)
    {
        nBytesSent = send(pSocketConnection->Socket,
                          (char*)pBuffer,
                          lLength,
                          0);
        if(nBytesSent == SOCKET_ERROR)
        {
            if(WSAGetLastError() != WSAEWOULDBLOCK)
            {
                Message(pSocketConnection,
                        "RTP API:send() returned error. Resetting connection",
                        RTPAPIERROR_SEND,
                        WSAGetLastError());

                CloseSocket(pSocketConnection);
                if(pBuffer)
                {
                    _free(pBuffer);
                }
                return(0);
            }
            else
            {
                nBytesSent = 0;
            }
        }
    }
    else
    {
        nBytesSent = 0;
    }

    // See if we're done or if we need to enqueue data.
    if(nBytesSent != (int)lLength)
    {
        // Allocate a queue element.
        pNewElem = _calloc(sizeof(*pNewElem), 1);
        if(!pNewElem)
        {
            FatalError(pSocketConnection,
                       "RTP API:Error allocating memory",
                       RTPAPIERROR_MEM_ALLOC,
                       0);
        }
        pNewElem->pBuffer = pBuffer;
        pNewElem->lLength = lLength;
        pNewElem->lBytesSent = nBytesSent;

        // Toss it in the queue.
        PUT_QUE(pSocketConnection->pSocketQueueHead,
                pNewElem,
                pNext);

        // Set up for FD_WRITE notification.
        pSocketConnection->lEventMask |= FD_WRITE;
    }
    else
    {
        if(pBuffer)
        {
            _free(pBuffer);
        }
    }

    return(nBytesSent);
}




//---------------------------------------------------------------------------
//
// Function: SendErrorResponse()
//
// Description: Sends an error response PDU to a request that we don't
//              like.
//
// Arguments:   SOCKET_CONNECTION    *pSocketConnection - pointer to a
//              SOCKET_CONNECTION stuct.
//
//              unsigned char nStatus - RTP status code bytes, as defined in
//              rtp.h.
//
//              unsigned char *pRequestBuffer - pointer to a buffer
//              containing the offending request.
//      
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void SendErrorResponse(SOCKET_CONNECTION    *pSocketConnection,
                       unsigned char        nStatus,
                       unsigned char        *pRequestBuffer)
{
    unsigned char   *pResponseBuffer;

    // Allocate a response buffer.
    pResponseBuffer = _malloc(RTP_HDRPLUSLENGTH_SIZE);
    if(!pResponseBuffer)
    {
        FatalError(pSocketConnection,
                   "RTP API:Error allocating memory",
                   RTPAPIERROR_MEM_ALLOC,
                   0);
    }

    // Build the response.

    // Add the size to the response.
    *((unsigned long*)(pResponseBuffer + RTP_MSG_SIZE_OFFSET)) =
         RTP_HDRPLUSLENGTH_SIZE;

    // Add the Opcode to the response.
    pResponseBuffer[RTP_OPCODE_OFFSET + RTP_LEN_SIZE] =
        pRequestBuffer[RTP_OPCODE_OFFSET] | 0x80;

    // Add the status to the response.
    pResponseBuffer[RTP_STATUS_OFFSET + RTP_LEN_SIZE] = nStatus;

    // Add the sequence number to the response.
    pResponseBuffer[RTP_SEQ_NUM_LOW_OFFSET + RTP_LEN_SIZE] =
        pRequestBuffer[RTP_SEQ_NUM_LOW_OFFSET];
    pResponseBuffer[RTP_SEQ_NUM_HIGH_OFFSET + RTP_LEN_SIZE] =
        pRequestBuffer[RTP_SEQ_NUM_HIGH_OFFSET];

    SendData(pSocketConnection, pResponseBuffer, RTP_HDRPLUSLENGTH_SIZE);
}




//---------------------------------------------------------------------------
//
// Function: ProcessRequest()
//
// Description: Process an incoming request PDU (RTP read/write/notify
//              request).
//
// Arguments:   SOCKET_CONNECTION *pSocketConnection - pointer to a
//              SOCKET_CONNECTION struct.
//      
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void ProcessRequest(SOCKET_CONNECTION *pSocketConnection)
{
    unsigned char   *pRequestBuffer =
        pSocketConnection->ReceiveBuffer.pBuffer;
    unsigned long   lRequestLength =
        pSocketConnection->ReceiveBuffer.lLength;
    int             nVariableSpecifierCount;
    int             iIndex;
    char            *pVariableSpecifierOffset;
    char            *pResponseOffset;
    unsigned long   lDataLength;
    unsigned char   *pReponseBuffer;
    unsigned long   lVariableSpecifierLength;
    unsigned long   lVariableSpecifierAddress;
    unsigned char   nStatus;

    if(pRequestBuffer[RTP_OPCODE_OFFSET] == RTP_READ_REQ)
    {
        // Validate request length.
        if(lRequestLength < RTP_HDR_SIZE ||
           (lRequestLength - RTP_HDR_SIZE) % RTP_VARSPEC_SIZE)
        {
            SendErrorResponse(pSocketConnection,
                              RTP_OTHER_ERROR,
                              pRequestBuffer);
            return;
        }

        // See how many variable specifiers are in the request.
        nVariableSpecifierCount =
            (lRequestLength - (RTP_HDR_SIZE)) / RTP_VARSPEC_SIZE;

        // Determine the total data length.
        pVariableSpecifierOffset = pRequestBuffer + (RTP_HDR_SIZE);
        lDataLength = 0;
        for(iIndex = 0; iIndex < nVariableSpecifierCount; iIndex ++)
        {
            lDataLength +=
                *((unsigned long*)(pVariableSpecifierOffset +
                RTP_VARSPEC_LENGTH_OFFSET));

           pVariableSpecifierOffset += RTP_VARSPEC_SIZE;
        } // end for

        // Allocate a buffer for the response.
        pReponseBuffer = _malloc(RTP_HDRPLUSLENGTH_SIZE + lDataLength);
        if(!pReponseBuffer)
        {
            FatalError(pSocketConnection,
                       "RTP API:Error allocating memory",
                       RTPAPIERROR_MEM_ALLOC,
                       0);
        }

        // Add the length to the response.
        *((unsigned long*)(pReponseBuffer + RTP_MSG_SIZE_OFFSET)) =
            RTP_HDRPLUSLENGTH_SIZE + lDataLength;

        // Add the OpCode to the response.
        pReponseBuffer[RTP_OPCODE_OFFSET + RTP_LEN_SIZE] = RTP_READ_RSP;

        // Add the sequence number to the response.
        pReponseBuffer[RTP_SEQ_NUM_LOW_OFFSET + RTP_LEN_SIZE] =
            pRequestBuffer[RTP_SEQ_NUM_LOW_OFFSET ];
        pReponseBuffer[RTP_SEQ_NUM_HIGH_OFFSET + RTP_LEN_SIZE] =
            pRequestBuffer[RTP_SEQ_NUM_HIGH_OFFSET];

        // Build each part of the response.

        pVariableSpecifierOffset = pRequestBuffer + RTP_HDR_SIZE;
        pResponseOffset = pReponseBuffer + RTP_HDRPLUSLENGTH_SIZE;

        for(iIndex = 0; iIndex < nVariableSpecifierCount; iIndex ++)
        {
            // Get the length for this variable specifier.
            lVariableSpecifierLength =
                *((unsigned long*)(pVariableSpecifierOffset +
                RTP_VARSPEC_LENGTH_OFFSET));

            // Get the address for this variable specifier.
            lVariableSpecifierAddress =
                *((unsigned long*)(pVariableSpecifierOffset +
                RTP_VARSPEC_ADDR_OFFSET));

            // Call user ReadHandler() function to process read.
            if(pSocketConnection->RtpSession.ReadHandler)
            {
                nStatus = (*(pSocketConnection->RtpSession.ReadHandler))
                            ((HANDLE)pSocketConnection,
                            lVariableSpecifierAddress,
                            lVariableSpecifierLength,
                            pResponseOffset,
                            pSocketConnection->RtpSession.pReadArgument);
            }
            else
            {
                nStatus = RTP_ACCESS_PERM;
            }

            // If the user ReadHandler() function doesn't succeed,
            // send an error response.
            if(nStatus != RTP_SUCCESS)
            {
                *((unsigned long*)(pReponseBuffer + RTP_MSG_SIZE_OFFSET)) =
                    RTP_HDRPLUSLENGTH_SIZE;
                pReponseBuffer[RTP_STATUS_OFFSET + RTP_LEN_SIZE] = nStatus;
                SendData(pSocketConnection,
                         pReponseBuffer,
                         RTP_HDRPLUSLENGTH_SIZE);
                return;
            }

            pResponseOffset += lVariableSpecifierLength;
            pVariableSpecifierOffset += RTP_VARSPEC_SIZE;
        } // end for

        // Mark the PDU as successful and send it.
        pReponseBuffer[RTP_STATUS_OFFSET + RTP_LEN_SIZE] = RTP_SUCCESS;
        SendData(pSocketConnection,
                 pReponseBuffer,
                 RTP_HDRPLUSLENGTH_SIZE + lDataLength);


    }
    else if(pRequestBuffer[RTP_OPCODE_OFFSET] == RTP_WRITE_REQ ||
            pRequestBuffer[RTP_OPCODE_OFFSET] == RTP_NOTIFY)
    {
        unsigned char   *pMessageEnd;

        // Validate the request length.
        if(lRequestLength < RTP_HDR_SIZE)
        {
            if(pRequestBuffer[RTP_OPCODE_OFFSET] != RTP_NOTIFY)
            {
                SendErrorResponse(pSocketConnection,
                              RTP_OTHER_ERROR,
                              pRequestBuffer);
            }
            return;
        }

        // Allocate a buffer for the response, but only if we're processing
        // a write - a notify requires no response.
        if(pRequestBuffer[RTP_OPCODE_OFFSET] != RTP_NOTIFY)
        {
            pReponseBuffer = _malloc(RTP_HDRPLUSLENGTH_SIZE);
            if(!pReponseBuffer)
            {
                FatalError(pSocketConnection,
                           "RTP API:Error allocating memory",
                           RTPAPIERROR_MEM_ALLOC,
                           0);
            }

            // Add the length to the response.
            *((unsigned long*)(pReponseBuffer + RTP_MSG_SIZE_OFFSET)) =
                RTP_HDRPLUSLENGTH_SIZE;

            // Add the OpCode to the response.
            pReponseBuffer[RTP_OPCODE_OFFSET + RTP_LEN_SIZE] = RTP_WRITE_RSP;

            // Add the sequence number to the response.
            pReponseBuffer[RTP_SEQ_NUM_LOW_OFFSET + RTP_LEN_SIZE] =
                pRequestBuffer[RTP_SEQ_NUM_LOW_OFFSET];
            pReponseBuffer[RTP_SEQ_NUM_HIGH_OFFSET + RTP_LEN_SIZE] =
                pRequestBuffer[RTP_SEQ_NUM_HIGH_OFFSET];
        }

        // Write the variable for each specifier.

        pVariableSpecifierOffset = pRequestBuffer + RTP_HDR_SIZE;
        pMessageEnd = pRequestBuffer + lRequestLength;
        nStatus = RTP_SUCCESS;

        while(pVariableSpecifierOffset < pMessageEnd)
        {
            // Make sure the full variable specifion is contained
            // in the message.

            if(pVariableSpecifierOffset + RTP_VARSPEC_SIZE > pMessageEnd)
            {
                nStatus = RTP_PROTOCOL_ERROR;
                break;
            }

            lVariableSpecifierLength =
                *((unsigned long*)(pVariableSpecifierOffset +
                RTP_VARSPEC_LENGTH_OFFSET));

            if(pVariableSpecifierOffset + RTP_VARSPEC_SIZE +
               lVariableSpecifierLength > pMessageEnd)
            {
                nStatus = RTP_OTHER_ERROR;
                break;
            }

            // Pass the data to the user WriteHandler() function.

            lVariableSpecifierAddress =
                *((unsigned long*)(pVariableSpecifierOffset +
                RTP_VARSPEC_ADDR_OFFSET));

            // Call user ReadHandler() function to process read.
            if(pSocketConnection->RtpSession.WriteHandler)
            {
                nStatus = (*(pSocketConnection->RtpSession.WriteHandler))
                            ((HANDLE)pSocketConnection,
                            lVariableSpecifierAddress,
                            lVariableSpecifierLength,
                            pVariableSpecifierOffset + RTP_VARSPEC_SIZE,
                            pSocketConnection->RtpSession.pWriteArgument);
            }

           if(nStatus != RTP_SUCCESS)
                break;

           pVariableSpecifierOffset +=
               (lVariableSpecifierLength + RTP_VARSPEC_SIZE);
        } // end while

        // Mark the status of the response and send it.

        if(pRequestBuffer[RTP_OPCODE_OFFSET] != RTP_NOTIFY)
        {
            pReponseBuffer[RTP_STATUS_OFFSET + RTP_LEN_SIZE] = nStatus;
            SendData(pSocketConnection, pReponseBuffer, RTP_HDRPLUSLENGTH_SIZE);
        }
    }
    else; // Can't get here from there.

}



//---------------------------------------------------------------------------
//
// Function: ProcessResponse()
//
// Description: Process an incoming response PDU.
//
// Arguments: SOCKET_CONNECTION *pSocketConnection - pointer to a
//            SOCKET_CONNECTION struct.
//      
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void ProcessResponse(SOCKET_CONNECTION *pSocketConnection)
{
    unsigned char               *pResponseBuffer =
        pSocketConnection->ReceiveBuffer.pBuffer;
    unsigned long               lResponseLength =
        pSocketConnection->ReceiveBuffer.lLength;
    COMPLETION_QUEUE_ELEMENT    *pCurrentCompletionElem = NULL;
    DISPATCH_QUEUE_ELEMENT      *pNewDispatchQueueElem = NULL;


    // Grab the completion queue element that matches this response.
    UseCompletionQueue(pSocketConnection,
                       NULL,
                       &pCurrentCompletionElem,
                       *((unsigned short*)(pResponseBuffer +
                       RTP_SEQ_NUM_OFFSET)),
                       LookupQueue);

    if(!pCurrentCompletionElem)
    {
        unsigned char zTextBuffer[128];
        sprintf(zTextBuffer,
                "RTP API:Incoming PDU received with invalid "
                "sequence number: %d",
                *((unsigned short*)(pResponseBuffer + RTP_SEQ_NUM_OFFSET)));
        Message(pSocketConnection,
                zTextBuffer,
                0,
                0);
        ResetReceiveBuffer(pSocketConnection);
        return;
    }

    // Decrement the send queue backlog count.
    pSocketConnection->RtpSession.nSendQueueBacklogCount--;

    // Kill the timeout timer.
    if(!CancelWaitableTimer(pCurrentCompletionElem->hTimerHandle))
    {
        FatalError(pSocketConnection,
                   "RTP API:CancelWaitableTimer() returned error",
                   RTPAPIERROR_CL_WT,
                   GetLastError());
    }
    CloseHandle(pCurrentCompletionElem->hTimerHandle);

    // Allocate a dispatch queue element.
    pNewDispatchQueueElem = _calloc(sizeof(*pNewDispatchQueueElem), 1);
    if(!pNewDispatchQueueElem)
    {
        FatalError(pSocketConnection,
                   "RTP API:CreateWaitableTimer() returned error",
                   RTPAPIERROR_CR_WT,
                   GetLastError());
    }

    // Transfer stuff from the completion queue elem to the new dispatch
    // queue elem.
    pNewDispatchQueueElem->CompletionFunction =
        pCurrentCompletionElem->CompletionFunction;
    pNewDispatchQueueElem->pCompletionArgument =
        pCurrentCompletionElem->pCompletionArgument;

    do
    {
        if(pResponseBuffer[RTP_OPCODE_OFFSET] == RTP_READ_RSP)
        {
            // See if we have a bad return status.
            if(pResponseBuffer[RTP_STATUS_OFFSET] != RTP_SUCCESS)
            {
                pNewDispatchQueueElem->RtpStatus =
                    pResponseBuffer[RTP_STATUS_OFFSET];
                break;
            }

            // Validate the size of the PDU.
            if(lResponseLength != pCurrentCompletionElem->lExpectedPDUSize)
            {
                pNewDispatchQueueElem->RtpStatus = RTP_OTHER_ERROR;
                break;
            }

            // Ok, we have a good read, so lets copy the data into the buffer
            if(pCurrentCompletionElem->pInBuffer)
                memcpy(pCurrentCompletionElem->pInBuffer,
                       pResponseBuffer + RTP_DATA_OFFSET,
                       pCurrentCompletionElem->lExpectedPDUSize -
                       (RTP_HDR_SIZE));

            pNewDispatchQueueElem->RtpStatus = RTP_SUCCESS;

        } // end if RTP_READ_REQ

        else if(pResponseBuffer[RTP_OPCODE_OFFSET] == RTP_WRITE_RSP)
        {
            // See if we have a bad return status.
            if(pResponseBuffer[RTP_STATUS_OFFSET] != RTP_SUCCESS)
            {
                pNewDispatchQueueElem->RtpStatus =
                    pResponseBuffer[RTP_STATUS_OFFSET];
                break;
            }

            // Validate the size of the PDU.
            if(lResponseLength != pCurrentCompletionElem->lExpectedPDUSize)
            {
                pNewDispatchQueueElem->RtpStatus = RTP_OTHER_ERROR;
                break;
            }

            pNewDispatchQueueElem->RtpStatus = RTP_SUCCESS;

        } // end else if RTP_WRITE_REQ or RTP_NOTIFY.

        else if(pResponseBuffer[RTP_OPCODE_OFFSET] == RTP_KA_PACKET); // Do
                                                                   // nothing.
        else
        {
            pNewDispatchQueueElem->RtpStatus = RTP_UNSUPPORTED;
        }

    } while(0);


    // Free the completion queue elem.
    _free(pCurrentCompletionElem);

    // Put the new dispatch queue elem in the dispatch queue and signal the
    // event.
    UseDispatchQueue(pSocketConnection,
                     pNewDispatchQueueElem,
                     NULL,
                     PutQueue);

    if(pSocketConnection->RtpSession.hDispatchEvent)
    {
        SetEvent(pSocketConnection->RtpSession.hDispatchEvent);
    }
}




//---------------------------------------------------------------------------
//
// Function: ProcessIncoming()
//
// Description: Processes the incoming PDUs.
//
// Arguments:   pSocketConnection - pointer to a SOCKET_CONNECTION struct.
//      None
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void ProcessIncoming(SOCKET_CONNECTION  *pSocketConnection)
{
    unsigned char               *pIncomingBuffer =
        pSocketConnection->ReceiveBuffer.pBuffer;

    if(pIncomingBuffer[RTP_OPCODE_OFFSET] == RTP_READ_REQ ||
       pIncomingBuffer[RTP_OPCODE_OFFSET] == RTP_WRITE_REQ ||
       pIncomingBuffer[RTP_OPCODE_OFFSET] == RTP_NOTIFY)
    {
        ProcessRequest(pSocketConnection);
    }
    else
    {
        ProcessResponse(pSocketConnection);
    }
}





//---------------------------------------------------------------------------
//
// Function: ProcessReadEvent()
//
// Description: Processes a read event notification on the socket.
//
// Arguments:   pSocketConnection - pointer to a SOCKET_CONNECTION struct.
//      None
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void ProcessReadEvent(SOCKET_CONNECTION *pSocketConnection)
{
    int     nBytesReceived;

    // See if we're expecting a new PDU.
    if(!pSocketConnection->ReceiveBuffer.pBuffer)
    {
        nBytesReceived =
            recv(pSocketConnection->Socket,
                 (char*)(&(pSocketConnection->ReceiveBuffer.lLength) +
                 pSocketConnection->ReceiveBuffer.lLengthOffset),
                 sizeof(pSocketConnection->ReceiveBuffer.lLength) -
                 pSocketConnection->ReceiveBuffer.lLengthOffset,
                 0);
        if(nBytesReceived == SOCKET_ERROR)
        {
            Message(pSocketConnection,
                    "RTP API:recv() returned error. Resetting connection",
                    RTPAPIERROR_RECV,
                    WSAGetLastError());
            CloseSocket(pSocketConnection);
            return;
        }

        pSocketConnection->ReceiveBuffer.lLengthOffset += nBytesReceived;

        // See if we have the whole length.
        if(pSocketConnection->ReceiveBuffer.lLengthOffset !=
           sizeof(pSocketConnection->ReceiveBuffer.lLength))
        {
            return;
        }
        else
        {
            // Decrement the length by 4 bytes.
            pSocketConnection->ReceiveBuffer.lLength -=
                sizeof(pSocketConnection->ReceiveBuffer.lLength);
        }
    }

    // We got here, so we have a length and maybe part of a buffer.

    // Allocate a buffer for the PDU if we don't have one yet.
    if(!pSocketConnection->ReceiveBuffer.pBuffer)
    {
        pSocketConnection->ReceiveBuffer.pBuffer =
            _malloc(pSocketConnection->ReceiveBuffer.lLength);
        if(!pSocketConnection->ReceiveBuffer.pBuffer)
        {
            FatalError(pSocketConnection,
                       "RTP API:Error allocating memory",
                       RTPAPIERROR_MEM_ALLOC,
                       0);
        }
    }

    // Receive data from the socket.
    nBytesReceived =
        recv(pSocketConnection->Socket,
             (char*)(pSocketConnection->ReceiveBuffer.pBuffer +
             pSocketConnection->ReceiveBuffer.lOffset),
             pSocketConnection->ReceiveBuffer.lLength -
             pSocketConnection->ReceiveBuffer.lOffset,
             0);
    if(nBytesReceived == SOCKET_ERROR)
    {
        Message(pSocketConnection,
                "RTP API:recv() returned error. Resetting connection",
                RTPAPIERROR_RECV,
                WSAGetLastError());
        CloseSocket(pSocketConnection);
        return;
    }

    pSocketConnection->ReceiveBuffer.lOffset += nBytesReceived;

    // See if we have a whole PDU.
    if(pSocketConnection->ReceiveBuffer.lOffset !=
       pSocketConnection->ReceiveBuffer.lLength)
    {
        return;
    }
    else
    {
        // We do, so process it and reset the receive buffer after.
        ProcessIncoming(pSocketConnection);
        ResetReceiveBuffer(pSocketConnection);
    }
}





//---------------------------------------------------------------------------
//
// Function: ProcessWriteEvent()
//
// Description: Processes a write event notification on the socket. Walks the
//              list of data enqueues for send and trys to send it.
//
// Arguments:   pSocketConnection - pointer to a SOCKET_CONNECTION struct.
//      
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void ProcessWriteEvent(SOCKET_CONNECTION *pSocketConnection)
{
    SOCKET_QUEUE_ELEMENT   *pFirstElem;
    int                     nBytesSent;

    // See if there's any enqueued data to send, and if so, send it.
    while(pFirstElem = GET_FIRST(pSocketConnection->pSocketQueueHead,
                                 pNext))
    {
        nBytesSent = send(pSocketConnection->Socket,
                          (char*)(pFirstElem->pBuffer +
                          pFirstElem->lBytesSent),
                          pFirstElem->lLength - pFirstElem->lBytesSent,
                          0);
        if(nBytesSent == SOCKET_ERROR)
        {
            if(WSAGetLastError() != WSAEWOULDBLOCK)
            {
                Message(pSocketConnection,
                        "RTP API:send() returned error Resetting connection",
                        RTPAPIERROR_SEND,
                        WSAGetLastError());
                CloseSocket(pSocketConnection);
                return;
            }
            else
            {
                nBytesSent = 0;
            }
        }

        pFirstElem->lBytesSent += nBytesSent;

        // See if we sent all of this PDU's data.
        if(pFirstElem->lBytesSent != pFirstElem->lLength)
        {
            // We haven't, so return.
            return;
        }
        else
        {
            // We have, so dequeue this element and free it and it's buffer.
            GET_QUE(pSocketConnection->pSocketQueueHead,
                    pFirstElem,
                    pNext);
            if(pFirstElem->pBuffer)
            {
                _free(pFirstElem->pBuffer);
            }
            _free(pFirstElem);
        }
    }

    // Remove the FD_WRITE event notification.
    pSocketConnection->lEventMask &= ~FD_WRITE;

}





//---------------------------------------------------------------------------
//
// Function: ProcessSendTimeout()
//
// Description: Callback routine to be called when a send timer times out.
//
// Arguments:   pArg - pointer to a completion queue element.
//      
//              dwThrowAwayOne - unused.
//
//              dwThrowAwayTwo - unused.
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

VOID CALLBACK ProcessSendTimeout(void *pArg,
                                 DWORD dwThrowAwayOne,
                                 DWORD dwThrowAwayTwo)
{
    COMPLETION_QUEUE_ELEMENT    *pCompletionQueueElem =
        (COMPLETION_QUEUE_ELEMENT*) pArg;
    SOCKET_CONNECTION           *pSocketConnection =
        (SOCKET_CONNECTION*)pCompletionQueueElem->pSocketConnection;
    COMPLETION_QUEUE_ELEMENT    *pReturnTest = NULL;
    DISPATCH_QUEUE_ELEMENT      *pNewDispatchQueueElem;

    // Dequeue this elem from the completion queue.
    UseCompletionQueue(pSocketConnection,
                       NULL,
                       &pReturnTest,
                       pCompletionQueueElem->nSequenceNumber,
                       RemoveQueue);
    if(pReturnTest != pCompletionQueueElem)
    {
        goto FREE_AND_EXIT;
    }

    // Allocate a new dispatch queue elem and set it up.
    pNewDispatchQueueElem = _malloc(sizeof(*pNewDispatchQueueElem));
    if(!pNewDispatchQueueElem)
    {
        FatalError(pSocketConnection,
                   "RTP API:Error allocating memory",
                   RTPAPIERROR_MEM_ALLOC,
                   0);
    }

    pNewDispatchQueueElem->CompletionFunction =
        pCompletionQueueElem->CompletionFunction;
    pNewDispatchQueueElem->RtpStatus = RTP_ACCESS_TEMP;
    pNewDispatchQueueElem->pCompletionArgument =
        pCompletionQueueElem->pCompletionArgument;

    UseDispatchQueue(pSocketConnection,
                     pNewDispatchQueueElem,
                     NULL,
                     PutQueue);

    // Decrement the send queue backlog count.
    pSocketConnection->RtpSession.nSendQueueBacklogCount--;

    if(pSocketConnection->RtpSession.hDispatchEvent)
    {
        SetEvent(pSocketConnection->RtpSession.hDispatchEvent);
    }


FREE_AND_EXIT:
    // Free the completion queue elem.
    _free(pCompletionQueueElem);

}





//---------------------------------------------------------------------------
//
// Function: ProcessSends()
//
// Description: Runs the queue of requests to send and sends them, starting
//              their timers.
//
// Arguments:   pSocketConnection - pointer to a SOCKET_CONNECTION struct.
//      None
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void ProcessSends(SOCKET_CONNECTION *pSocketConnection)
{
    SEND_QUEUE_ELEMENT      *pFirstSendElem = NULL;

    // Grab everything from the send queue and send it.
    for(UseSendQueue(pSocketConnection,
                     NULL,
                     &pFirstSendElem,
                     GetQueue);
        pFirstSendElem != NULL;
        UseSendQueue(pSocketConnection,
                     NULL,
                     &pFirstSendElem,
                     GetQueue))
    {
        SendData(pSocketConnection,
                 pFirstSendElem->pOutBuffer,
                 pFirstSendElem->lLength);

        // If this was a Notify, we're done with it.
        if(pFirstSendElem->bNotifyFlag)
        {
            _free(pFirstSendElem);
            pSocketConnection->RtpSession.nSendQueueBacklogCount--;
            continue;
        }

        // After the data's sent, put the send queue elem into the completion
        // queue for retrieval when we receive a response. First, set the
        // timeout timer.

        pFirstSendElem->hTimerHandle = CreateWaitableTimer(NULL,
                                                           FALSE,
                                                           NULL);

        if(!SetWaitableTimer(pFirstSendElem->hTimerHandle,
                             (const LARGE_INTEGER*) &pSocketConnection->
                             RtpSession.TimeoutInterval,
                             0L,
                             ProcessSendTimeout,
                             (void*)pFirstSendElem,
                             FALSE))
        {
            FatalError(pSocketConnection,
                       "RTP API:SetWaitableTimer() returned error",
                       RTPAPIERROR_S_WT,
                       GetLastError());
        }

        UseCompletionQueue(pSocketConnection,
                           (COMPLETION_QUEUE_ELEMENT*)pFirstSendElem,
                           NULL,
                           0,
                           PutQueue);
    }
}





//---------------------------------------------------------------------------
//
// Function: ManageSocketConnection()
//
// Description: Called with _beginthread() to start a thread to manage the
//              socket connection and RTP session.
//
// Arguments:   pArg - pointer to a SOCKET_CONNECTION struct.
//      None
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void ManageSocketConnection(void *pArg)
{
    SOCKET_CONNECTION       *pSocketConnection = (SOCKET_CONNECTION*)pArg;
    HANDLE                  hWaitableObjects[2];
    fd_set                  ReadEvent;
    fd_set                  WriteEvent;
    int                     nSelectReturn;
    const struct timeval    PollRate = {0, 0};




    // Loop while monitoring the connection state, and waiting for socket and
    // user get/put events.
    for(;;)
    {
        // See if we should shut down this thread.
        if(pSocketConnection->bShutdown)
        {
            CloseSocket(pSocketConnection);
            ResetReceiveBuffer(pSocketConnection);
            KillQueues(pSocketConnection);
            pSocketConnection->bShutdownComplete = TRUE;
            return;
        }

        // Try to connect if we aren't connected.
        if(pSocketConnection->eState != Connected)
        {
            if(pSocketConnection->eConnectionRole == Connect)
            {
                MakeConnection(pSocketConnection);
                continue;
            }
            else if(pSocketConnection->eConnectionRole == Listen)
            {
                if(pSocketConnection->eState == Disconnected)
                {
                    SetupListen(pSocketConnection);
                    pSocketConnection->lEventMask = FD_ACCEPT;
                }
                else if(pSocketConnection->eState == Listening)
                {
                    AcceptConnection(pSocketConnection);
                }
                continue;
            }
        }

        // Set up for event notification on read and write events.
        if(WSAEventSelect(pSocketConnection->Socket,
                          pSocketConnection->SocketEvent,
                          pSocketConnection->lEventMask | FD_CLOSE) == SOCKET_ERROR)
        {
            if(WSAGetLastError() == WSAEINPROGRESS)
            {
                continue;
            }
            else
            {
                FatalError(pSocketConnection,
                           "RTP API:WSAEventSelect() returned error",
                           RTPAPIERROR_WSA_EVNT_SLCT,
                           WSAGetLastError());
            }
        }

        hWaitableObjects[0] = (HANDLE) pSocketConnection->SocketEvent;
        hWaitableObjects[1] = pSocketConnection->RtpSession.hSendEvent;

        // Sleep until we need to do something, or for 1 second.
        if(WaitForMultipleObjectsEx(2,
                                    (const HANDLE*)&hWaitableObjects,
                                    FALSE,
                                    1000,
                                    TRUE) != WAIT_TIMEOUT)
        {
            SEND_QUEUE_ELEMENT    *pSendEventTest = NULL;
            // If we get here we've been notified of either data on the
            // socket, or that the user has stuff to send. First we'll
            // set up to do a select on the socket to see what, if any
            // socket events took place. Then we'll check to see if the
            // user has posted PDUs for sending.

            // Zero out the fd_set structs.
            FD_ZERO(&ReadEvent);
            FD_ZERO(&WriteEvent);

            // Set the fd_set structs for our socket and call select().
            FD_SET(pSocketConnection->Socket, &ReadEvent);
            FD_SET(pSocketConnection->Socket, &WriteEvent);

            // Call select.
            if((nSelectReturn = select(0,
                                       &ReadEvent,
                                       &WriteEvent,
                                       0,
                                       &PollRate)))
            {
                if(nSelectReturn == INVALID_SOCKET)
                {
                    FatalError(pSocketConnection,
                               "select() returned error",
                               RTPAPIERROR_SELECT,
                               WSAGetLastError());
                }

                // If there is a read event, process it.
                if(FD_ISSET(pSocketConnection->Socket, &ReadEvent))
                {
                    ProcessReadEvent(pSocketConnection);
                }

                // If there is a write event, process it.
                if(FD_ISSET(pSocketConnection->Socket, &WriteEvent))
                {
                    ProcessWriteEvent(pSocketConnection);
                }
            }

            // See if the user has posted PDUs for send.
            if(pSocketConnection->eState == Connected)
            {
                ProcessSends(pSocketConnection);
            }

            // Reset the socket event.
            if(!ResetEvent(pSocketConnection->SocketEvent))
            {
                FatalError(pSocketConnection,
                           "RTP API:ResetEvent() returned error",
                           RTPAPIERROR_RESET_EVENT,
                           GetLastError());
            }

        } // end if WaitForMultipleObjects()

    } // end for

}



//---------------------------------------------------------------------------
//
// Function: Init()
//
// Description: Init initializes the RTP API and starts a worker thread to
// manage the socket connection and the RTP protocol session.
//
// Arguments:   phRtpApi - pointer to a handle to identify the
//              TCP/IP/RTP session.
//
//              pzIpAddressString - pointer to NULL terminated string
//              containing the IP address of the TCP/IP peer that your
//              app will connect to, in the form "123.123.123.123".
//
//              nPort - the port number at which your app will try to make a
//              the TCP/IP connection.
//
//              CONNECT_ROLE eConnectionRole - enum type variable that defines
//              our role as connecter or connectee.
//
//              HANDLE hDispatchEvent - optional handle to a user defined
//              event that we will arm when there's stuff in the dispatch
//              queue.
//
//              MessageHandler - pointer to a error handling routine. If NULL,
//              no error reporting takes place.
//
//              ReadHandler - optional pointer to a function to handle RTP read
//              requests.
//
//              WriteHandler - optional poiner to a function to handle RTP
//              write requests.
//
//      
//
//  Input Globals:
//      None
//
// Return Value: Returns a handle to the worker thread.
//      
//
//  Argument Return Values: phRtpApi returns a hancdle to identify
//  the session.
//
//      
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes: Init() must be called before any call to Get(), Put(), or Exit().
// Init() can be called multiple times with different IP addresses, ports, and
// phRtpApi pointers. The result is that multiple worker threads are
// started to manage multiple TCP/RTP connections.
//
//---------------------------------------------------------------------------

unsigned long Init(HANDLE               *phRtpApi,
                   const char           *pzIpAddressString,
                   unsigned short       nPort,
                   CONNECT_ROLE         eConnectionRole,
                   HANDLE               hDispatchEvent,
                   void                 (*MessageHandler)
                                        (const char *pzText,
                                        unsigned long lCode),
                   RTP_STATUS           (*ReadHandler)
                                        (HANDLE          hRtpApi,
                                        unsigned long   lAddress,
                                        unsigned long   lLength,
                                        unsigned char   *pBuffer,
                                        void            *userArgument),
                   void                 *pReadArgument,
                   RTP_STATUS           (*WriteHandler)
                                        (HANDLE          hRtpApi,
                                        unsigned long   lAddress,
                                        unsigned long   lLength,
                                        unsigned char   *pBuffer,
                                        void            *userArgument),
                   void                 *pWriteArgument)
{
    SOCKET_CONNECTION       *pSocketConnection;
    const unsigned short    knWinsockVersion = 2;
    WSADATA                 WinsockVersionInfo;
    int                     nWSAStartupError;

    // Initialize to global memory tracking variable.
    glMemCount = 0;

    // Allocate a SOCKET_CONNECTION struct.
    pSocketConnection = _calloc(sizeof(*pSocketConnection), 1);
    if(!pSocketConnection)
    {
        return(0);
    }
    else
    {
        *phRtpApi = (HANDLE)pSocketConnection;
    }

    // Set the thread shutdown indicator to FALSE;
    pSocketConnection->bShutdown = FALSE;
    pSocketConnection->bShutdownComplete = FALSE;

    // Set up the initial socket state.
    pSocketConnection->eState = Disconnected;

    // Set the IP address.
    if(pzIpAddressString)
    {
        strcpy(pSocketConnection->zIPAddress, pzIpAddressString);
    }

    // Set the port.
    pSocketConnection->nPort = nPort;

    // Set the connectin role.
    pSocketConnection->eConnectionRole = eConnectionRole;

    // Set the dispatch event.
    pSocketConnection->RtpSession.hDispatchEvent = hDispatchEvent;

    // Set the send timeout.
    pSocketConnection->RtpSession.TimeoutInterval.QuadPart =
        (SEND_TIMEOUT * 10000) - (2 * (SEND_TIMEOUT * 10000));

    // Set the max send queue backlog.
    pSocketConnection->RtpSession.nMaxSendQueueBacklogCount =
        MAX_BACKLOG;

    // Set the message handler routine.
    pSocketConnection->MessageHandler = MessageHandler;

    // Register the user read and write handlers.
    pSocketConnection->RtpSession.ReadHandler = ReadHandler;
    pSocketConnection->RtpSession.pReadArgument = pReadArgument;
    pSocketConnection->RtpSession.WriteHandler = WriteHandler;
    pSocketConnection->RtpSession.pWriteArgument = pWriteArgument;

    // Initialize Winsock.
    if(nWSAStartupError = WSAStartup(knWinsockVersion, &WinsockVersionInfo))
    {
        FatalError(pSocketConnection,
                   "RTP API:WSAStartup() returned error",
                   RTPAPIERROR_WSA_STARTUP,
                   nWSAStartupError);
    }

    // Create a socket event.
    pSocketConnection->SocketEvent = WSACreateEvent();
    if(pSocketConnection->SocketEvent == WSA_INVALID_EVENT)
    {
        FatalError(pSocketConnection,
                   "RTP API:WSACreateEvent() returned error",
                   RTPAPIERROR_WSA_CREATE_EVNT,
                   WSAGetLastError());
    }

    // Create the user send event.
    pSocketConnection->RtpSession.hSendEvent = CreateEvent(NULL,
                                                           FALSE,
                                                           FALSE,
                                                           NULL);

    // Initialize the Critical Section Objects.
    InitializeCriticalSection(&pSocketConnection->
                              RtpSession.SendQueueCSObject);
    InitializeCriticalSection(&pSocketConnection->
                              RtpSession.DispatchQueueCSObject);
    InitializeCriticalSection(&pSocketConnection->
                              RtpSession.CompletionQueueCSObject);

    // Launch a thread to manage the socket connection.
    return(_beginthread(ManageSocketConnection,
                        0,
                        (void*)pSocketConnection));
}



//---------------------------------------------------------------------------
//
// Function: Exit()
//
// Description: Exit() shuts down the worker thread, cleans up all queues,
// and frees the SOCKET_CONNECTION struct allocated in the call to Init().
//
//
// Arguments: hRtpApi - a handle to identify this session.
//      
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------
void Exit(HANDLE hRtpApi)
{
    SOCKET_CONNECTION   *pSocketConnection =
        (SOCKET_CONNECTION*)hRtpApi;
    pSocketConnection->bShutdown = TRUE;

    // Spinlock 'till shutdown is done.
    while(!pSocketConnection->bShutdownComplete);

    Message(pSocketConnection,
            "RTP API:Shutdown complete",
            0,
            0);

    _free(pSocketConnection);


}




//---------------------------------------------------------------------------
//
// Function: SetMaxBacklog()
//
// Description: Sets the max number of Get and Put requests that can be queued
//              to send before Get and Put return blocked.
//
// Arguments:   HANDLE hRtpApi - handle to identify this session.
//
//              unsigned short nMaxBacklog - new value for max backlog.
//      
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void SetMaxBacklog(HANDLE hRtpApi, unsigned short nMaxBacklog)
{
    SOCKET_CONNECTION   *pSocketConnection = (SOCKET_CONNECTION*)hRtpApi;
    // Set the max send queue backlog.
    pSocketConnection->RtpSession.nMaxSendQueueBacklogCount =
        nMaxBacklog;
}



//---------------------------------------------------------------------------
//
// Function: SetSendTimeout()
//
// Description: Sets the timeout value for messages sent to the peer.
//
// Arguments:   HANDLE hRtpApi - handle to identify this session.
//
//              int nSendTimeout - new value for send timeout.
//      
//
//  Input Globals:
//      None
//
// Return Value:
//      None
//
//  Argument Return Values:
//      None
//
//  Output Globals:
//      None
//
// Limits:
//
// Notes:
//
//---------------------------------------------------------------------------

void SetSendTimeout(HANDLE hRtpApi, int nSendTimeout)
{
    SOCKET_CONNECTION   *pSocketConnection = (SOCKET_CONNECTION*)hRtpApi;

    // Set the send timeout.
    pSocketConnection->RtpSession.TimeoutInterval.QuadPart =
        (nSendTimeout * 10000) - (2 * (nSendTimeout * 10000));
}

BOOL IsConnected(HANDLE hRtpApi) 
{
  SOCKET_CONNECTION   *pSocketConnection = (SOCKET_CONNECTION*)hRtpApi;
  return pSocketConnection->eState == Connected;
}





