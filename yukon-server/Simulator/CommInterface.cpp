#include "precompiled.h"

#include "CommInterface.h"

#include <boost/scoped_array.hpp>

using Cti::Timing::Chrono;
using namespace std;

extern HANDLE gQuitEvent;

namespace Cti {
namespace Simulator {

bool Comms::write(const bytes &buf, Logger &logger)
{
    bytes temp_buf = buf;
    ProcessMessage(temp_buf, logger);
    writeMessage(temp_buf);

    return true;
}

void Comms::ProcessMessage(bytes &buf, Logger &logger)
{
    _behaviorCollection.processMessage(buf, logger);
}

SocketComms::SocketComms(StreamSocketConnection &nexus, unsigned baud) :
    _nexus(nexus)
{
    _baud = std::max(baud / 1200, 1U) * 1200;
}

void SocketComms::commDelay(unsigned myBytes)
{
    //  assume 10 comm bits per byte (1 start, 8 data, 1 stop)
    unsigned bits = myBytes * 10;

    //  calc millis to sleep (bits / bits/sec)
    Sleep(bits * 1000 / _baud);
}

bool SocketComms::read(byte_appender &destination, unsigned expected)
{
    boost::scoped_array<unsigned char> temp(new unsigned char[expected]);

    const unsigned bytesRead = _nexus.read(temp.get(), expected, Chrono::seconds(SocketTimeout), &gQuitEvent);

    copy(temp.get(), temp.get() + bytesRead, destination);

    commDelay(expected);

    return bytesRead == expected;
}

bool SocketComms::peek(byte_appender &destination, unsigned expected)
{
    boost::scoped_array<unsigned char> temp(new unsigned char[expected]);

    const unsigned bytesRead = _nexus.peek(temp.get(), expected);

    if( bytesRead != expected )
    {
        return false;
    }

    copy(temp.get(), temp.get() + bytesRead, destination);

    return true;
}

bool SocketComms::available(unsigned aCount)
{
    boost::scoped_array<unsigned char> temp(new unsigned char[aCount]);

    const unsigned bytesRead = _nexus.peek(temp.get(), aCount);

    return bytesRead == aCount;
}

bool SocketComms::writeMessage(const bytes &buf)
{
    const unsigned bytesWritten = _nexus.write(buf.data(), buf.size(), Chrono::seconds(SocketTimeout));

    commDelay(bytesWritten);

    return bytesWritten == buf.size();
}

void SocketComms::setBehavior(std::unique_ptr<CommsBehavior> &&behavior)
{
    _behaviorCollection.push_back(std::move(behavior));
}

void SocketComms::clear()
{
    _nexus.flushInput();
}


BufferCommsIn::BufferCommsIn(const bytes &input) :
    _itr(input.begin()),
    _end(input.end())
{
}

bool BufferCommsIn::read(byte_appender &destination, unsigned expected)
{
    if( distance(_itr, _end) < expected )
    {
        return false;
    }

    while( expected-- && _itr != _end )
    {
        *destination++ = *_itr++;
    }

    return true;
}

bool BufferCommsIn::peek(byte_appender &destination, unsigned expected)
{
    if( distance(_itr, _end) < expected )
    {
        return false;
    }

    bytes::const_iterator peek_itr = _itr;

    while( expected-- && peek_itr != _end )
    {
        *destination++ = *peek_itr++;
    }

    return true;
}

bool BufferCommsIn::available(unsigned aCount)
{
    return distance(_itr, _end) >= aCount;
}

}
}

