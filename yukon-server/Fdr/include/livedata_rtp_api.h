#pragma once

#include <string>
#include <exception>

#include "logger.h"
#include "guard.h"


extern "C" {
  #include "RTP_APIW.H"
  #include "RTP.H"
}

/**
 * Read/Write request callback. This is an abstract class that 
 * provides an interface for the callback of a RTP read/write
 * request from the client to the server. 
 */
class CompletionCallback {
public:
  DEBUG_INSTRUMENTATION;

  CompletionCallback() {};
  virtual ~CompletionCallback() {};
  virtual void complete() = 0;
  virtual void* getResultBuffer() = 0;
  void setStatus(RTP_STATUS status) {rtpStatus = status;};
  RTP_STATUS getStatus() {return rtpStatus;};

protected:
  RTP_STATUS rtpStatus;
};

/**
 * Write callback. This is an abstract class that 
 * provides an interface for the callback of a RTP write from
 * the server to this client. 
 */
class WriteCallback {
public:
    DEBUG_INSTRUMENTATION;

  WriteCallback() {};
  virtual ~WriteCallback() {};
  virtual bool write(unsigned long address, unsigned long length, void * buffer) = 0;

};

/**
 * LiveData API wrapper. This class wraps
 * the procedural RTP API that was provided by LiveData.
 */
class LiveDataApi {

  typedef RTP_STATUS (*WriteHandler) (HANDLE          hRtpApi,
                                      unsigned long   lAddress,
                                      unsigned long   lLength,
                                      unsigned char   *pBuffer,
                                      void            *userArgument);
  typedef RTP_STATUS (*ReadHandler) (HANDLE          hRtpApi,
                                     unsigned long   lAddress,
                                     unsigned long   lLength,
                                     unsigned char   *pBuffer,
                                     void            *userArgument);
  typedef void (*MessageHandler) (const char *pzText,
                                  unsigned long lCode);
  typedef void (*CompletionFunction) (RTP_STATUS RtpStatus,
                                      void        *pCompletionArgument);


public: 
  DEBUG_INSTRUMENTATION;

  LiveDataApi();

  ~LiveDataApi();

  void initialize(std::string ipAaddressString,
                        unsigned short port);

  void setMessageHandler(MessageHandler messageHandler);
  static void defaultMessageHandler(const char *pzText,
                                    unsigned long lCode);

  void setWriteHandler(WriteCallback* callback) {_writeCallback = callback;};
  
  
  REQUEST_STATUS get(unsigned long        *lpAddresses,
                     unsigned long        *lpLengths,
                     int                  nCount,
                     CompletionCallback   *completionCallback);
  
  
  REQUEST_STATUS put(unsigned long        *lpAddresses,
                     unsigned long        *lpLengths,
                     int                  nCount,
                     unsigned char        *pBuffer,
                     CompletionCallback   *completionCallback,
                     bool                 bNotifyFlag);
  
  void dispatch();

  void setMaxBacklog(unsigned short nMaxBacklog);
  
  void setSendTimeout(int nSendTimeout);

  bool isConnected();

  void exit();

  class LiveDataException : std::exception {
  public:
    LiveDataException(std::string errMsg) : std::exception("LiveDataException") {}
  };

protected:
  HANDLE   _phRtpApi;
  MessageHandler _messageHandler;
  WriteCallback   *_writeCallback;
  unsigned long _initReturnValue;
  bool   _connectionOpen;
};
