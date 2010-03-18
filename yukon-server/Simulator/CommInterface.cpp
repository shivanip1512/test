#include "yukon.h"

#include "CommInterface.h"

#include <boost/scoped_array.hpp>

using namespace std;

namespace Cti {
namespace Simulator {

bool Comms::write(const bytes &buf)
{
    bytes temp_buf = buf;
    ProcessMessage(temp_buf);
    writeMessage(temp_buf);
    
    return true;
}

bool Comms::ProcessMessage(bytes &buf)
{
    if( _behaviorCollection.processMessage(buf) )
    {
        return true;
    }
    return false;
}

SocketComms::SocketComms(CTINEXUS &nexus, unsigned baud) :
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

    unsigned long bytes_read = 0;

    _nexus.CTINexusRead(temp.get(), expected, &bytes_read, SocketTimeout);

    copy(temp.get(), temp.get() + bytes_read, destination);

    commDelay(expected);

    return bytes_read == expected;
}

bool SocketComms::peek(byte_appender &destination, unsigned expected)
{
    boost::scoped_array<unsigned char> temp(new unsigned char[expected]);

    unsigned long bytes_read = 0;

    _nexus.CTINexusPeek(temp.get(), expected, &bytes_read);

    if( bytes_read != expected )
    {
        return false;
    }

    copy(temp.get(), temp.get() + bytes_read, destination);

    return true;
}

bool SocketComms::available(unsigned aCount)
{
    boost::scoped_array<unsigned char> temp(new unsigned char[aCount]);

    unsigned long bytes_read = 0;

    _nexus.CTINexusPeek(temp.get(), aCount, &bytes_read);

    return bytes_read == aCount;
}

bool SocketComms::writeMessage(const bytes &buf)
{
    boost::scoped_array<unsigned char> temp(new unsigned char[buf.size()]);

    copy(buf.begin(), buf.end(), temp.get());
    
    unsigned long bytes_written = 0;

    _nexus.CTINexusWrite(temp.get(), buf.size(), &bytes_written, SocketTimeout);

    commDelay(bytes_written);

    return bytes_written == buf.size();
}

void SocketComms::setBehavior(std::auto_ptr<CommsBehavior> behavior)
{
    _behaviorCollection.push_back(behavior);
}

void SocketComms::clear()
{
    _nexus.CTINexusFlushInput();
}


BufferCommsIn::BufferCommsIn(const bytes &input) :
    _itr(input.begin()),
    _end(input.end())
{
}

BufferCommsOut::BufferCommsOut(byte_appender &output) :
    _output(output)
{
}

BufferComms::BufferComms(const bytes &input, byte_appender &output) :
    BufferCommsIn(input),
    BufferCommsOut(output)
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

bool BufferCommsOut::write(const bytes &buf)
{
    copy(buf.begin(), buf.end(), _output);

    return true;
}


}
}

