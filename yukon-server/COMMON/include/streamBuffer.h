#pragma once

#include "dlldefs.h"
#include "loggable.h"
#include "CallSite.h"

#include <boost/scoped_ptr.hpp>
#include <boost/range/iterator_range.hpp>

#include <string>
#include <sstream>
#include <iomanip>
#include <chrono>
#include <map>
#include <set>
#include <vector>

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
    StreamBufferT& operator<< (unsigned long long val);
    StreamBufferT& operator<< (unsigned long val);
    StreamBufferT& operator<< (float val);
    StreamBufferT& operator<< (double val);
    StreamBufferT& operator<< (long double val);
    StreamBufferT& operator<< (const void* ptr);
    StreamBufferT& operator<< (time_t val); // Same as long long

    StreamBufferT& operator<< (std::ostream&  (*pf)(std::ostream&));
    StreamBufferT& operator<< (std::ios&      (*pf)(std::ios&));
    StreamBufferT& operator<< (std::ios_base& (*pf)(std::ios_base&));

    StreamBufferT& operator<< (const std::stringstream& ss);
    StreamBufferT& operator<< (const std::ostringstream& oss);

    StreamBufferT& operator<< (const CtiNumStr& numstr);
    StreamBufferT& operator<< (const CtiDate& date);
    StreamBufferT& operator<< (const CtiTime& time);
    StreamBufferT& operator<< (const std::chrono::milliseconds val);
    StreamBufferT& operator<< (const std::chrono::seconds val);
    StreamBufferT& operator<< (const std::chrono::minutes val);

    StreamBufferT& operator<< (const Cti::CallSite cs);

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
    StreamBufferT& operator<< (const std::set<ValueT>& s)
    {
        initSStream();
        *_osstream << s;
        return static_cast<StreamBufferT&>(*this);
    }

    template<class KeyT, class ValueT>
    StreamBufferT& operator<< (const std::map<KeyT, ValueT>& m)
    {
        initSStream();
        *_osstream << m;
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
public:
    StreamBufferSink()
    {}
};

} //namespace Cti;
