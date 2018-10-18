#include "precompiled.h"

#include "boost/lexical_cast.hpp"
#include "string_util.h"
#include "ctidate.h"
#include "ctitime.h"
#include "numstr.h"
#include "streamBuffer.h"
#include "boostutil.h"

using namespace std::string_literals;

namespace Cti {

template<class StreamBufferT>
inline void StreamBufferBase<StreamBufferT>::initSStream()
{
    if( !_osstream )
    {
        _osstream.reset(new std::ostringstream());
    }
}

template<class StreamBufferT>
inline void StreamBufferBase<StreamBufferT>::appendCString(const char* s)
{
    if( _osstream )
    {
        *_osstream << s;
    }
    else
    {
        _text += s;
    }
}

template<class StreamBufferT>
inline void StreamBufferBase<StreamBufferT>::appendChar(char c)
{
    if( _osstream )
    {
        *_osstream << c;
    }
    else
    {
        _text += c;
    }
}

template<class StreamBufferT>
inline void StreamBufferBase<StreamBufferT>::appendString(const std::string& s)
{
    if( _osstream )
    {
        *_osstream << s;
    }
    else
    {
        _text += s;
    }
}

template<class StreamBufferT>
template<class ValueT>
inline void StreamBufferBase<StreamBufferT>::appendDigit(const ValueT val)
{
    if( _osstream )
    {
        *_osstream << val;
    }
    else
    {
        _text += boost::lexical_cast<std::string>(val);
    }
}

template<class StreamBufferT>
template<class SStreamT>
inline void StreamBufferBase<StreamBufferT>::appendSStream(const SStreamT& ss)
{
    if( _osstream )
    {
        *_osstream << ss.str();
    }
    else if( _text.empty() )
    {
        _text.swap(ss.str());
    }
    else
    {
        _text += ss.str();
    }
}

template<class StreamBufferT>
inline void StreamBufferBase<StreamBufferT>::swapOrAppend(std::string& s)
{
    if( _osstream )
    {
        *_osstream << s;
    }
    else if( _text.empty() )
    {
        _text.swap(s);
    }
    else
    {
        _text += s;
    }
}


template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (const std::string& s) { appendString(s);  return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (const char* s)        { appendCString(s); return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (char c)               { appendChar(c);    return static_cast<StreamBufferT&>(*this); }

template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (unsigned char val)    { appendDigit((unsigned)val); return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (bool b)               { appendCString(b?"true":"false"); return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (short val)            { appendDigit(val); return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (unsigned short val)   { appendDigit(val); return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (int val)              { appendDigit(val); return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (unsigned int val)     { appendDigit(val); return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (long val)             { appendDigit(val); return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (unsigned long long val){ appendDigit(val); return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (unsigned long val)    { appendDigit(val); return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (float val)            { appendDigit(val); return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (double val)           { appendDigit(val); return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (long double val)      { appendDigit(val); return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (const void* ptr)      { appendDigit(ptr); return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (time_t val)           { appendDigit(val); return static_cast<StreamBufferT&>(*this); }

template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (const std::stringstream& ss)   { appendSStream(ss);  return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (const std::ostringstream& oss) { appendSStream(oss); return static_cast<StreamBufferT&>(*this); }

template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (StreamBuffer& sb)              { appendString(sb.extractToString()); return static_cast<StreamBufferT&>(*this); }

template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (std::ios&      (*pf) (std::ios&))      { initSStream(); *_osstream << pf; return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (std::ios_base& (*pf) (std::ios_base&)) { initSStream(); *_osstream << pf; return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (std::ostream&  (*pf) (std::ostream&))
{
    if( !_osstream )
    {
        if( pf == std::endl )
        {
            _text += '\n';
            return static_cast<StreamBufferT&>(*this);
        }

        initSStream();
    }

    *_osstream << pf;
    return static_cast<StreamBufferT&>(*this);
}

template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (const CtiNumStr& numstr) { swapOrAppend(numstr.toString()); return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (const CtiDate& date)     { swapOrAppend(date.asString());   return static_cast<StreamBufferT&>(*this); }
template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (const CtiTime& time)     { swapOrAppend(time.asString());   return static_cast<StreamBufferT&>(*this); }

template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (const std::chrono::milliseconds milliseconds)
{
    swapOrAppend(commaFormatted(milliseconds.count()) + " ms");
    return static_cast<StreamBufferT&>(*this);
}

template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (const std::chrono::seconds seconds)
{ 
    const auto suffix = seconds.count() == 1 ? " second" : " seconds";

    swapOrAppend(commaFormatted(seconds.count()) + suffix);
    return static_cast<StreamBufferT&>(*this); 
}

template<class StreamBufferT> StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (const std::chrono::minutes minutes)
{
    const auto suffix = minutes.count() == 1 ? " minute" : " minutes";

    swapOrAppend(commaFormatted(minutes.count()) + suffix);
    return static_cast<StreamBufferT&>(*this);
}

template<class StreamBufferT>
StreamBufferT& StreamBufferBase<StreamBufferT>::operator<<(const Cti::CallSite cs)
{
    appendCString(cs.getFunction());
    appendCString("@");
    appendCString(cs.getFilename());
    appendCString(":");
    appendDigit(cs.getLine());
    return static_cast<StreamBufferT&>(*this);
}

template<class StreamBufferT>
StreamBufferT& StreamBufferBase<StreamBufferT>::operator<< (const Cti::Loggable& loggable)
{
    swapOrAppend(loggable.toString());
    return static_cast<StreamBufferT&>(*this);
}

template<class StreamBufferT>
std::string StreamBufferBase<StreamBufferT>::extractToString()
{
    if( _osstream )
    {
        if( _text.empty() )
        {
            _text.swap(_osstream->str());
        }
        else
        {
            _text += _osstream->str();
        }

        _osstream->str(""); // clear the content
    }

    std::string tempStr;
    tempStr.swap(_text);

    return tempStr;
}

//// StreamBuffer ////

StreamBuffer::operator std::string()
{
    return extractToString();
}

//// explicit instantiation ////

template class StreamBufferBase<StreamBuffer>;
template class StreamBufferBase<StreamBufferSink>;

} // namespace Cti
