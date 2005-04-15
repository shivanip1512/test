#include "yukon.h"

/**
 * File:   livedata_rtp_api
 *
 * Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
 *
 * Author: Tom Mack
 *
 * ARCHIVE      :  $Archive$
 * REVISION     :  $Revision: 1.1 $
 * DATE         :  $Date: 2005/04/15 15:34:41 $
 */

#include "livedata_rtp_api.h"


/** 
 * This function is passed to the RTP_API.
 */ 
static void completionHelperFunction(RTP_STATUS RtpStatus,
                                      void        *pCompletionArgument);

/** 
 * This function is passed to the RTP_API.
 */ 
static RTP_STATUS writeHelperFunction(HANDLE          hRtpApi,
                                      unsigned long   lAddress,
                                      unsigned long   lLength,
                                      unsigned char   *pBuffer,
                                      void            *userArgument);

/** 
 * Default Constructor.
 */ 
LiveDataApi::LiveDataApi()
{
  _messageHandler = NULL;
  _phRtpApi = NULL;
  _connectionOpen = false;
  setMessageHandler(LiveDataApi::defaultMessageHandler);

}

/** 
 * Setup the connection.
 */ 
void LiveDataApi::initialize(std::string ipAddressString,
                             unsigned short port)
{
  if (_phRtpApi == NULL)
  {
    unsigned long threadHandle;
  
    threadHandle = Init(&_phRtpApi,
                        ipAddressString.c_str(),
                        port,
                        Connect,
                        NULL,
                        _messageHandler,
                        NULL,
                        NULL,
                        writeHelperFunction,
                        _writeCallback);

    _connectionOpen = true;
  
  }
}

/** 
 * Destructor.
 */ 
LiveDataApi::~LiveDataApi()
{
  if (_connectionOpen)
  {
    exit();
  }
}

/** 
 * Sets the error message callback.
 */ 
void LiveDataApi::setMessageHandler(MessageHandler messageHandler)
{
  _messageHandler = messageHandler;
}
  
/** 
 * Closes the connection.
 */ 
void LiveDataApi::exit()
{
  _connectionOpen = false;
  Exit(_phRtpApi);
}
  
  
/** 
 * Makes a request for data from the server.
 * The response is returned via the CompletionCallback object.
 */ 
REQUEST_STATUS LiveDataApi::get(unsigned long        *lpAddresses,
                                unsigned long        *lpLengths,
                                int                  nCount,
                                CompletionCallback   *completionCallback)
{

  REQUEST_STATUS result;

  result = Get(_phRtpApi,
               lpAddresses,
               lpLengths,
               nCount,
               (unsigned char *)completionCallback->getResultBuffer(),
               completionHelperFunction,
               completionCallback);

  if (result != Success)
  {
    delete completionCallback;
  }

  return result;

}
  
  
/** 
 * Write data to the server.
 * The response is returned via the CompletionCallback object.
 */ 
REQUEST_STATUS LiveDataApi::put(unsigned long        *lpAddresses,
                                unsigned long        *lpLengths,
                                int                  nCount,
                                unsigned char        *pBuffer,
                                CompletionCallback   *completionCallback,
                                bool                 bNotifyFlag)
{

  REQUEST_STATUS result;

  result = Put(_phRtpApi,
               lpAddresses,
               lpLengths,
               nCount,
               pBuffer,
               completionHelperFunction,
               completionCallback,
               bNotifyFlag);

  return result;

}
  
/** 
 * Allows the callbacks to be made.
 * MUST be called periodically.
 */ 
void LiveDataApi::dispatch()
{
  Dispatch(_phRtpApi, 10);
}

/** 
 * Sets the number of pending requests the client can hold on to.
 */ 
void LiveDataApi::setMaxBacklog(unsigned short nMaxBacklog)
{
  SetMaxBacklog(_phRtpApi, nMaxBacklog);
}
  
/** 
 * Sets the number of seconds that the client should wait for a response.
 */ 
void LiveDataApi::setSendTimeout(int nSendTimeout)
{
  SetSendTimeout(_phRtpApi, nSendTimeout);
}

/** 
 * Tests the state of the server connection.
 * (Note, this does not "ping" the server.)
 * If the server is not connected, it will retain pending
 * requests up to the MaxBacklog.
 */ 
bool LiveDataApi::isConnected()
{
  return IsConnected(_phRtpApi);
}

/** 
 * A simple message handler.
 */ 
void LiveDataApi::defaultMessageHandler(const char *pzText,
                                        unsigned long lCode)
{
  short upperCode = lCode >> 16;
  short lowerCode = lCode & 0xffff;
  CtiLockGuard<CtiLogger> doubt_guard(dout);
  dout << RWTime() << " LiveData: " << pzText << " (" << upperCode << "," << lowerCode << ")" << endl;

}

static void completionHelperFunction(RTP_STATUS RtpStatus,
                                     void  *pCompletionArgument)
{
  CompletionCallback * callback = (CompletionCallback*)pCompletionArgument;
  callback->setStatus(RtpStatus);
  callback->complete();
  delete callback;
}

static RTP_STATUS writeHelperFunction(HANDLE          hRtpApi,
                                      unsigned long   lAddress,
                                      unsigned long   lLength,
                                      unsigned char   *pBuffer,
                                      void            *userArgument)
{
  WriteCallback *callback = (WriteCallback*)userArgument;
  bool result = callback->write(lAddress,lLength,pBuffer);
  return result ? RTP_SUCCESS : RTP_OTHER_ERROR;
}


