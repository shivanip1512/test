#include "yukon.h"

#include <windows.h>
#include <iostream>
using namespace std;

#include "xfer.h"

ULONG CtiXfer::TRACE_DEFAULT = 0x00000001;
ULONG CtiXfer::TRACE_ERROR   = 0x00000002;
ULONG CtiXfer::TRACE_DEBUG   = 0x00000004;
ULONG CtiXfer::TRACE_REMOTE  = 0x00000008;

const BYTE*    CtiXfer::getOutBuffer() const
{
   return _outBuffer;
}

BYTE*    CtiXfer::getOutBuffer()
{
   return _outBuffer;
}
BYTE*    CtiXfer::getInBuffer()
{
   return _inBuffer;
}
ULONG    CtiXfer::getOutCount() const
{
   return _outCount;
}
ULONG&    CtiXfer::getOutCount()
{
   return _outCount;
}

ULONG    CtiXfer::getInCountExpected() const
{
   return _inCountExpected;
}
ULONG&    CtiXfer::getInCountExpected()
{
   return _inCountExpected;
}
ULONG   CtiXfer::getInCountActual() const
{
    ULONG cnt = 0;

    if(_inCountActual != NULL)
    {
        cnt = *_inCountActual;
    }
   return cnt;
}
ULONG    CtiXfer::getInTimeout() const
{
   return _inTimeout;
}
ULONG&    CtiXfer::getInTimeout()
{
   return _inTimeout;
}
UINT     CtiXfer::getFlag() const
{
   return _flag;
}

CtiXfer& CtiXfer::setOutBuffer(BYTE * p)
{
   _outBuffer = p;
   return *this;
}

CtiXfer& CtiXfer::setInBuffer(BYTE * p)
{
   _inBuffer = p;
   return *this;
}

CtiXfer& CtiXfer::setOutCount(ULONG c)
{
   _outCount = c;
   return *this;
}
CtiXfer& CtiXfer::setInCountExpected(ULONG c)
{
   _inCountExpected = c;
   return *this;
}

CtiXfer& CtiXfer::setInCountActual(ULONG* c)
{
   _inCountActual = c;
   return *this;
}

CtiXfer& CtiXfer::setInCountActual(ULONG c)
{
   if(_inCountActual != NULL)
   {
      *_inCountActual = c;
   }
   return *this;
}

CtiXfer& CtiXfer::setInTimeout(ULONG t)
{
   _inTimeout = t;
   return *this;
}

CtiXfer& CtiXfer::setFlag(UINT flg)
{
   _flag = flg;
   return *this;
}


CtiXfer& CtiXfer::setRemoteToTrace(ULONG remote)
{
   _traceDefault = FALSE;
   _traceRemote  = TRUE;
   _remoteToTrace = remote;
   return *this;
}


CtiXfer& CtiXfer::setTraceMask(INT all, INT errs, INT port, LONG rem)
{
   if(all || port)      _traceDefault = TRUE;
   if(errs)
   {
      _traceError   = TRUE;
      _traceDefault = FALSE;
   }

   _traceDebug   = FALSE;

   if(rem != 0)
   {
      setRemoteToTrace(rem);
   }
   return *this;
}

BOOL     CtiXfer::traceDefault() const
{
   return _traceDefault;
}

BOOL     CtiXfer::traceError() const
{
   return _traceError;
}

CtiXfer& CtiXfer::setTraceMask(ULONG mask, ULONG rem)
{
   _traceDefault = (mask & TRACE_DEFAULT) ? TRUE : FALSE;
   _traceError   = (mask & TRACE_ERROR)   ? TRUE : FALSE;
   _traceDebug   = (mask & TRACE_DEBUG)   ? TRUE : FALSE;

   if(mask & TRACE_REMOTE && rem != 0)
   {
      setRemoteToTrace(rem);
   }
   //else
   //{
   //   setRemoteToTrace(0);
   //}


   return *this;
}

BOOL     CtiXfer::doTrace(INT status, ULONG rem) const
{
   BOOL  trace = (_traceDefault || _traceDebug) ? TRUE : FALSE;

   if(_traceError && status != NORMAL)
   {
      trace = TRUE;
   }

   if(rem != 0)      // They are asking about this remote
   {
      trace = (rem == _remoteToTrace);    // Is it the remote we were set up for?
   }

   return trace;
}

CtiXfer& CtiXfer::setNonBlockingReads(bool tothis)
{
   _nonBlockReads = tothis;
   return *this;
}

bool CtiXfer::getNonBlockingReads() const
{
   return _nonBlockReads;
}

BOOL     CtiXfer::verifyCRC() const
{
   return _verifyCRC;
}
BOOL     CtiXfer::addCRC() const
{
   return _addCRC;
}
CtiXfer& CtiXfer::setCRCFlag(UINT flg)
{
   if(flg & XFER_ADD_CRC)
      _addCRC     = TRUE;
   else
      _addCRC     = FALSE;

   if(flg & XFER_VERIFY_CRC)
      _verifyCRC  = TRUE;
   else
      _verifyCRC  = FALSE;

   return *this;
}

BOOL     CtiXfer::isMessageStart() const
{
   return _messageStart;
}

BOOL     CtiXfer::isMessageComplete() const
{
   return _messageComplete;
}
CtiXfer& CtiXfer::setMessageStart(BOOL b)
{
   _messageStart = b;
   return *this;
}

CtiXfer& CtiXfer::setMessageComplete(BOOL b)
{
   _messageComplete = b;
   return *this;
}

