#pragma once

#include <wincon.h>         // console colors.

#include "dlldefs.h"
#include "message.h"       // get the base class

class IM_EX_MSG CtiTraceMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiTraceMsg )

protected:

    bool _end;
    INT _attributes;        // FOREGROUND_BLUE | FOREGROUND_GREEN | FOREGROUND_RED default
    std::string _trace;

public:

    typedef  CtiMessage  Inherited;

    CtiTraceMsg();
    CtiTraceMsg(const CtiTraceMsg& aRef);
    virtual ~CtiTraceMsg();
    CtiTraceMsg& operator=(const CtiTraceMsg& aRef);

    INT getAttributes() const;
    const std::string& getTrace() const;

    CtiTraceMsg& setAttributes(const INT& attr);
    CtiTraceMsg& setTrace(const std::string& str);

    CtiMessage* replicateMessage() const;

    std::size_t getFixedSize() const override    { return sizeof( *this ); }
    std::size_t getVariableSize() const override;

    std::string toString() const override;

    CtiTraceMsg& resetAttributes();
    CtiTraceMsg& setEnd(bool nd = true);
    CtiTraceMsg& clearEnd();
    bool isEnd() const;

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

inline CtiTraceMsg& CtiTraceMsg::clearEnd()            { _end = false; return *this; }
inline bool CtiTraceMsg::isEnd() const                 { return _end; }
