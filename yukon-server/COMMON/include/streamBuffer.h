#pragma once

#include "dlldefs.h"
#include "loggable.h"

#include <string>
#include <sstream>
#include <boost/scoped_ptr.hpp>

class CtiNumStr;
class CtiDate;
class CtiTime;

namespace Cti {

class FormattedTable;
class FormattedList;
class StreamBuffer;

/**
 * StreamBuffer template base class
 */
template<class StreamBufferT>
class IM_EX_CTIBASE StreamBufferBase : private boost::noncopyable
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    StreamBufferBase(const StreamBufferBase&);
    StreamBufferBase& operator=(const StreamBufferBase&);

    std::string _text;
    boost::scoped_ptr<std::ostringstream> _osstream;

    void initSStream();

    void appendString  (const std::string& s);
    void appendCString (const char* s);
    void appendChar    (char c);

    template <class ValueT>
    void appendDigit(const ValueT val);

    template <class SStreamT>
    void appendSStream(const SStreamT& ss);

    void swapOrAppend(std::string& str);

protected:
    StreamBufferBase() {}

public:
    virtual ~StreamBufferBase() {}

    StreamBufferT& operator<< (const std::string& s);
    StreamBufferT& operator<< (const char* s);
    StreamBufferT& operator<< (char c);
    StreamBufferT& operator<< (unsigned char val);
    StreamBufferT& operator<< (bool val);
    StreamBufferT& operator<< (short val);
    StreamBufferT& operator<< (unsigned short val);
    StreamBufferT& operator<< (int val);
    StreamBufferT& operator<< (unsigned int val);
    StreamBufferT& operator<< (long val);
    StreamBufferT& operator<< (unsigned long val);
    StreamBufferT& operator<< (float val);
    StreamBufferT& operator<< (double val);
    StreamBufferT& operator<< (long double val);
    StreamBufferT& operator<< (void* ptr);
    StreamBufferT& operator<< (time_t val);

    StreamBufferT& operator<< (std::ostream&  (*pf)(std::ostream&));
    StreamBufferT& operator<< (std::ios&      (*pf)(std::ios&));
    StreamBufferT& operator<< (std::ios_base& (*pf)(std::ios_base&));

    StreamBufferT& operator<< (const std::stringstream& ss);
    StreamBufferT& operator<< (const std::ostringstream& oss);

    StreamBufferT& operator<< (const CtiNumStr& numstr);
    StreamBufferT& operator<< (const CtiDate& date);
    StreamBufferT& operator<< (const CtiTime& time);

    StreamBufferT& operator<< (const Loggable& loggable);

    /**
     * Note:
     * Inserting a StreamBufferSink is not allowed
     * StreamBufferT& operator<< (const streamBufferSink&) is not implemented
     * use StreamBuffer instead
     */
    StreamBufferT& operator<< (StreamBuffer& sb);

    template<class ManipT>
    StreamBufferT& operator<< (const std::_Smanip<ManipT>& manip)
    {
        initSStream();
        *_osstream << manip;
        return static_cast<StreamBufferT&>(*this);
    }

    template<class FillobjT>
    StreamBufferT& operator<< (const std::_Fillobj<FillobjT>& fillobj)
    {
        initSStream();
        *_osstream << fillobj;
        return static_cast<StreamBufferT&>(*this);
    }

    template<class ValueT>
    StreamBufferT& operator<< (const std::vector<ValueT>& vec)
    {
        initSStream();
        *_osstream << vec;
        return static_cast<StreamBufferT&>(*this);
    }

    template<class ValueT>
    StreamBufferT& operator<< (const boost::iterator_range<ValueT>& vec)
    {
        initSStream();
        *_osstream << vec;
        return static_cast<StreamBufferT&>(*this);
    }

    std::string extractToString();
};


class IM_EX_CTIBASE StreamBuffer : public StreamBufferBase<StreamBuffer>
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    StreamBuffer(const StreamBuffer&);
    StreamBuffer& operator=(const StreamBuffer&);

public:
    StreamBuffer()
    {}

    /**
     * Note:
     * typecast overload to std::string is only implemented only for StreamBuffer
     */
    operator std::string();
};


class IM_EX_CTIBASE StreamBufferSink : public StreamBufferBase<StreamBufferSink>
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    StreamBufferSink(const StreamBufferSink&);
    StreamBufferSink& operator=(const StreamBufferSink&);

public:
    StreamBufferSink()
    {}
};

} //namespace Cti;
