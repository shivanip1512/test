#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   msg_trace
*
* Date:   10/29/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/msg_trace.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/02/10 23:23:54 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\thr\mutex.h>
#include <rw/collect.h>
#include <rw/rwtime.h>

#include "dllbase.h"
#include "collectable.h"
#include "dlldefs.h"
#include "logger.h"
#include "msg_trace.h"

RWDEFINE_COLLECTABLE( CtiTraceMsg, MSG_TRACE );


CtiTraceMsg::CtiTraceMsg() :
    _end(true),
    _attributes( FOREGROUND_BLUE | FOREGROUND_GREEN | FOREGROUND_RED )
{}

CtiTraceMsg::CtiTraceMsg(const CtiTraceMsg& aRef)
{
    *this = aRef;
}

CtiTraceMsg::~CtiTraceMsg() {}

CtiTraceMsg& CtiTraceMsg::operator=(const CtiTraceMsg& aRef)
{
    if(this != &aRef)
    {
        setEnd(aRef.isEnd());
        setAttributes(aRef.getAttributes());
        setTrace(aRef.getTrace());
    }
    return *this;
}


// Return a new'ed copy of this message!
CtiMessage* CtiTraceMsg::replicateMessage() const
{
   CtiTraceMsg *ret = CTIDBG_new CtiTraceMsg(*this);

   return( (CtiMessage*)ret );
}

void
CtiTraceMsg::restoreGuts(RWvistream& aStream)
{
    int end = FALSE;                    // stupid RWVistream cant do bool...

    Inherited::restoreGuts( aStream );

   aStream >> end >> _attributes >> _trace;

   _end = end ? true : false;
}

void
CtiTraceMsg::saveGuts(RWvostream &aStream) const
{
    int end = _end ? TRUE : FALSE;      // stupid RWVistream cant do bool...

    Inherited::saveGuts( aStream );
    aStream << end << _attributes << _trace;
}

void
CtiTraceMsg::What() const
{
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << "CtiTraceMsg.... " << endl;
   }
}

void CtiTraceMsg::dump() const
{
   Inherited::dump();

   CtiLockGuard<CtiLogger> doubt_guard(dout);
   dout << " Message Attributes            " << _attributes << endl;
   dout << " Message End                   " << (_end ? "TRUE" : "FALSE") << endl;
   dout << " Message Trace                 " << _trace << endl;
}


INT CtiTraceMsg::getAttributes() const
{
    return _attributes;
}
RWCString CtiTraceMsg::getTrace() const
{
    return _trace;
}
RWCString& CtiTraceMsg::getTrace()
{
    return _trace;
}

/*
 * #define FOREGROUND_BLUE      0x0001 // text color contains blue.
 * #define FOREGROUND_GREEN     0x0002 // text color contains green.
 * #define FOREGROUND_RED       0x0004 // text color contains red.
 * #define FOREGROUND_INTENSITY 0x0008 // text color is intensified.
 * #define BACKGROUND_BLUE      0x0010 // background color contains blue.
 * #define BACKGROUND_GREEN     0x0020 // background color contains green.
 * #define BACKGROUND_RED       0x0040 // background color contains red.
 * #define BACKGROUND_INTENSITY 0x0080 // background color is intensified.
 */
CtiTraceMsg& CtiTraceMsg::setAttributes(const INT& attr)
{
    _attributes = attr;
    return *this;
}
CtiTraceMsg& CtiTraceMsg::setTrace(const RWCString& str)
{
    _trace = str;
    return *this;
}

CtiTraceMsg& CtiTraceMsg::setEnd(bool nd)
{
    _end = nd;
    return *this;
}

