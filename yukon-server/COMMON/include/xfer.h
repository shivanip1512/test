/*-----------------------------------------------------------------------------*
*
* File:   xfer
*
* Class:  CtiXfer
* Date:   3/28/2000
*
* Author: Corey Plender
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __XFER_H__
#define __XFER_H__
#pragma warning( disable : 4786)


// IM_EX_YUKONDB

#include <windows.h>
#include <rw\cstring.h>

class CtiDeviceBase;

#include "dsm2.h"
#include "dialup.h"
#include "yukon.h"

class IM_EX_CTIBASE CtiXfer
{
protected:

   BYTE     *_outBuffer;
   ULONG    _outCount;

   BYTE     *_inBuffer;
   BYTE     *_traceInPtr;
   ULONG    *_inCountActual; // Returned back in this guy
   ULONG    _inCountExpected;
   ULONG    _inTimeout;
   ULONG    _bufferSize; //Size of buffer in Bytes. NOTE this is only used in direct port, non-blocking reads.

   union {
      UINT    _flag;
      struct {
         UINT     _addCRC              : 1;
         UINT     _verifyCRC           : 1;
         UINT     _messageStart        : 1;  // If set, this is the first request for this byte stream, check DCD and Flush parameters!  Also look for first protocol header byte!
         UINT     _messageComplete     : 1;  // If set, this is the last request for this byte stream, SLEEP the postDelay!
         UINT     _traceDefault        : 1;
         UINT     _traceError          : 1;
         UINT     _traceDebug          : 1;
         UINT     _traceRemote         : 1;
         UINT     _nonBlockReads       : 1;  // Only read the data byte available, no incountexpected is needed.
      };
   };

   ULONG    _remoteToTrace;

private:

public:

   CtiXfer(BYTE         *outPtr  = NULL,
           ULONG        outCount = 0,
           BYTE         *inPtr   = NULL,
           ULONG        inCount  = 0,
           ULONG        *inRecv  = NULL,
           ULONG        timeOut  = 0,
           BOOL         outCRC   = FALSE,
           BOOL         inCRC    = FALSE,
           ULONG        trceMask = 0)     :
      _flag(0),
      _outBuffer(outPtr),
      _inBuffer(inPtr),
      _traceInPtr(outPtr),
      _outCount(outCount),
      _inCountExpected(inCount),
      _inCountActual(inRecv),
      _inTimeout(timeOut),
      _remoteToTrace(0),
      _bufferSize(0)
   {
      setTraceMask(trceMask);
   }

   CtiXfer(const CtiXfer& aRef)
   {
      *this = aRef;
   }

   virtual ~CtiXfer() {}

   BYTE*    getOutBuffer();
   const BYTE*    getOutBuffer() const;
   BYTE*    getInBuffer();
   ULONG    getOutCount() const;
   ULONG&   getOutCount();
   ULONG    getInCountExpected() const;
   ULONG&   getInCountExpected();
   ULONG    getInCountActual() const;
   ULONG    getInTimeout() const;
   ULONG&   getInTimeout();
   UINT     getFlag() const;
   ULONG    getBufferSize() const;
   ULONG&   getBufferSize();

   CtiXfer& setOutBuffer(BYTE *p);
   CtiXfer& setInBuffer(BYTE *p);
   CtiXfer& setOutCount(ULONG c);
   CtiXfer& setInCountExpected(ULONG c);
   CtiXfer& setInCountActual(ULONG* c);
   CtiXfer& setInCountActual(ULONG c);
   CtiXfer& setInTimeout(ULONG t);
   CtiXfer& setFlag(UINT flg);
   CtiXfer& setBufferSize(ULONG c);


   CtiXfer& setRemoteToTrace(ULONG remote);
   CtiXfer& setTraceMask(ULONG mask, ULONG rem = 0);

   CtiXfer& setTraceMask(INT all, INT errs, INT port = 0, LONG rem = 0);

   BOOL     doTrace(INT status, ULONG rem = 0) const;
   BOOL     traceDefault() const;
   BOOL     traceError() const;

   BOOL     isMessageStart() const;
   BOOL     isMessageComplete() const;
   CtiXfer& setMessageStart(BOOL b = TRUE);
   CtiXfer& setMessageComplete(BOOL b = TRUE);

   BOOL     verifyCRC() const;
   BOOL     addCRC() const;
   CtiXfer& setCRCFlag(UINT flg);

   bool     getNonBlockingReads() const;
   CtiXfer& setNonBlockingReads(bool tothis = true);        // Only reads available data...



   static ULONG TRACE_DEFAULT;
   static ULONG TRACE_ERROR  ;
   static ULONG TRACE_DEBUG  ;
   static ULONG TRACE_REMOTE ;
};
#endif // #ifndef __XFER_H__
