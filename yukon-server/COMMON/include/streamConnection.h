#pragma once

#include "yukon.h"
#include "dlldefs.h"
#include "timing_util.h"
#include "immutable.h"

namespace Cti {

//-----------------------------------------------------------------------------
//  Exception thrown by stream connections
//-----------------------------------------------------------------------------
class StreamConnectionException : public std::exception
{
    const std::string _message;

public:
    StreamConnectionException(const std::string &message) :
        _message(message)
    {}

    virtual const char* what() const
    {
        return _message.c_str();
    }
};

//-----------------------------------------------------------------------------
//  Stream connection abstract base class
//-----------------------------------------------------------------------------
class StreamConnection : private boost::noncopyable
{
protected:
    typedef Timing::Chrono Chrono;

public:
    StreamConnection() : Name("") {};
    virtual ~StreamConnection() {};

    // The pure virtuals below make this an abstract class
    virtual bool   isValid () const = 0;
    virtual size_t write   (const void *buf, int len, const Chrono& timeout) = 0;
    virtual size_t read    (void *buf, int len, const Chrono& timeout, const HANDLE *hAbort) = 0;
    virtual size_t peek    (void *buf, int len) = 0;

    // Text Description of connection
    Immutable<std::string> Name;
};

} // namespace Cti
