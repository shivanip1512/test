
#pragma warning( disable : 4786)
#ifndef __MSG_TRACE_H__
#define __MSG_TRACE_H__

/*-----------------------------------------------------------------------------*
*
* File:   msg_trace
*
* Class:  CtiTraceMsg
* Date:   10/29/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_trace.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:42 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <wincon.h>         // console colors.

#include <rw/collect.h>
#include <rw/rwtime.h>

#include "dlldefs.h"
#include "message.h"       // get the base class

class IM_EX_MSG CtiTraceMsg : public CtiMessage
{
protected:

    INT _attributes;        // FOREGROUND_BLUE | FOREGROUND_GREEN | FOREGROUND_RED default
    RWCString _trace;

private:

public:

    RWDECLARE_COLLECTABLE( CtiTraceMsg );

    typedef  CtiMessage Inherited;

    CtiTraceMsg();
    CtiTraceMsg(const CtiTraceMsg& aRef);
    virtual ~CtiTraceMsg();
    CtiTraceMsg& operator=(const CtiTraceMsg& aRef);

    INT getAttributes() const;
    RWCString getTrace() const;
    RWCString& getTrace();
    
    CtiTraceMsg& setAttributes(const INT& attr);
    CtiTraceMsg& setTrace(const RWCString& str);

    void saveGuts(RWvostream &aStream) const;
    void restoreGuts(RWvistream& aStream);
    CtiMessage* replicateMessage() const;

    void What() const;
    virtual void dump() const;


    CtiTraceMsg& resetAttributes();
    CtiTraceMsg& setBright();   // Always apply this attribute last.
    CtiTraceMsg& setRed();
    CtiTraceMsg& setGreen();
    CtiTraceMsg& setBlue();

    CtiTraceMsg& setYellow();
    CtiTraceMsg& setCyan();
    CtiTraceMsg& setMagenta();
    CtiTraceMsg& setWhite();
    CtiTraceMsg& setNormal();

    CtiTraceMsg& setBrightRed();
    CtiTraceMsg& setBrightGreen();
    CtiTraceMsg& setBrightBlue();
    CtiTraceMsg& setBrightYellow();
    CtiTraceMsg& setBrightCyan();
    CtiTraceMsg& setBrightMagenta();
    CtiTraceMsg& setBrightWhite();

};

inline CtiTraceMsg& CtiTraceMsg::resetAttributes()     { _attributes = 0; return *this; }

inline CtiTraceMsg& CtiTraceMsg::setBright()           { _attributes |= FOREGROUND_INTENSITY; return *this; }
inline CtiTraceMsg& CtiTraceMsg::setRed()              { _attributes |= FOREGROUND_RED; return *this; }
inline CtiTraceMsg& CtiTraceMsg::setGreen()            { _attributes |= FOREGROUND_GREEN; return *this; }
inline CtiTraceMsg& CtiTraceMsg::setBlue()             { _attributes |= FOREGROUND_BLUE; return *this; }

inline CtiTraceMsg& CtiTraceMsg::setYellow()           { return resetAttributes().setRed().setGreen(); }
inline CtiTraceMsg& CtiTraceMsg::setCyan()             { return resetAttributes().setBlue().setGreen(); }
inline CtiTraceMsg& CtiTraceMsg::setMagenta()          { return resetAttributes().setRed().setBlue(); }
inline CtiTraceMsg& CtiTraceMsg::setWhite()            { return resetAttributes().setRed().setBlue().setGreen();  }
inline CtiTraceMsg& CtiTraceMsg::setBrightRed()        { return resetAttributes().setRed().setBright(); }
inline CtiTraceMsg& CtiTraceMsg::setBrightGreen()      { return resetAttributes().setGreen().setBright(); }
inline CtiTraceMsg& CtiTraceMsg::setBrightBlue()       { return resetAttributes().setBlue().setBright(); }

inline CtiTraceMsg& CtiTraceMsg::setNormal()           { return setWhite();  }
inline CtiTraceMsg& CtiTraceMsg::setBrightYellow()     { return setYellow().setBright(); }
inline CtiTraceMsg& CtiTraceMsg::setBrightCyan()       { return setCyan().setBright(); }
inline CtiTraceMsg& CtiTraceMsg::setBrightMagenta()    { return setMagenta().setBright(); }
inline CtiTraceMsg& CtiTraceMsg::setBrightWhite()      { return setWhite().setBright();  }



#endif // #ifndef __MSG_TRACE_H__
