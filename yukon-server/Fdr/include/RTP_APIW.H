//---------------------------------------------------------------------------
// File: rtp_api.h
//
// Description: Header file with declarations for RTP API user routines.
//
// Copyright (c) 2000 LiveData, Inc.
// All rights reserved.
//
// Author: cmd
//
// References:
//
// Detailed Description: This header contains declarations for the following
//  user routines for the RTP Initiator API:
//
//      Init() - called by user to spawn a socket connection/RTP session
//      management thread.
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
//      Exit() - called by user to shut down the socket/RTP management thread
//      and free it's resources.
//
//      SetMaxBacklog() - called to set the max number of RTP requests allowed
//      outstanding before a call the Get() or Put() will return "Blocked".
//
//      SetSendTimeout() - called to set the max time allowed for a RTP request
//      to complete.
//
//
//
// Notes: As the NT version of the RTP API is multithreaded, any application
// built on it must be linked to the MT versions of LIBC (LIBCMT.lib).
//
// Revision History: 6/16/99 cmd Initial code.
//      
//
//---------------------------------------------------------------------------
#ifndef __RTP_APIW_H__
#define __RTP_APIW_H__


//#include <winsock2.h>
//#include <windows.h>

// Macro to tell if error code is fatal.
#define ERROR_IS_FATAL(lErrorCode) ((lErrorCode >> 15) & 1)

// Error codes.
#define RTPAPIERROR_SET_EVENT       1  // SetEvent() failed for send event.
#define RTPAPIERROR_MEM_ALLOC       2  // Error allocating memory.
#define RTPAPIERROR_SOCKET          3  // socket() returned error.
#define RTPAPIERROR_IOCTL           4  // ioctl() returned error.
#define RTPAPIERROR_SETSOCKOPT      5  // setsockopt() returned error.
#define RTPAPIERROR_BIND            6  // bind() returned error.
#define RTPAPIERROR_LISTEN          7  // listen() returned error.
#define RTPAPIERROR_ACCEPT          8  // accept() returned error.
#define RTPAPIERROR_CR_WT           9  // CreateWaitableTimer() returned error.
#define RTPAPIERROR_CL_WT           10 // CancelWaitableTimer() returned error.
#define RTPAPIERROR_S_WT            11 // SetWaitableTimer() returned error.
#define RTPAPIERROR_WSA_EVNT_SLCT   12 // WSAEventSelect() returned error.
#define RTPAPIERROR_RESET_EVENT     13 // ResetEvent() returned error.
#define RTPAPIERROR_WSA_STARTUP     14 // WSAStartup() returned error.
#define RTPAPIERROR_WSA_CREATE_EVNT 15 // WSACreateEvent() returned error.
#define RTPAPIERROR_SEND            16 // send() returned error.
#define RTPAPIERROR_RECV            17 // recv() returned error.
#define RTPAPIERROR_SELECT          18 // select() returned error.

// type for RTP status returns.
typedef unsigned char RTP_STATUS;

// enum type to describe the socket connection state.
typedef enum {Disconnected, Connecting, Connected, Listening} SOCKET_STATE;

// enum type to describe the status of a Get of Put.
typedef enum {Success, Blocked, Failure} REQUEST_STATUS;

// enum type to describe if were a connector or connectee.
typedef enum {Connect, Listen} CONNECT_ROLE;

// enum type for thread safe queue operations.
typedef enum
{   PutQueue,
    GetQueue,
    CheckQueue,
    LookupQueue,
    RemoveQueue
} QUE_OPS;



// prototypes for user functions.




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
                   void                 *pWriteArgument);





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

void Exit(HANDLE hRtpApi);




//---------------------------------------------------------------------------
//
// Function: Get()
//
// Description:
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
                   void                 *pCompletionArgument);


//---------------------------------------------------------------------------
//
// Function: Put()
//
// Description:
//
// Arguments: hRtpApi - a handle to identify this session.
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
                   BOOL                 bNotifyFlag);





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

int Dispatch(HANDLE hRtpApi, int nExecuteCount);


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

void SetMaxBacklog(HANDLE hRtpApi, unsigned short nMaxBacklog);


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

void SetSendTimeout(HANDLE hRtpApi, int nSendTimeout);

BOOL IsConnected(HANDLE hRtpApi);


#endif // #ifndef __RTP_APIW_H__



