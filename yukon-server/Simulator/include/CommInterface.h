#pragma once

#include "types.h"
#include "ctinexus.h"

#include <boost/utility.hpp>

namespace Cti {
namespace Simulator {

class CommsIn : boost::noncopyable
{
public:
    virtual bool read(byte_appender &destination, unsigned expected) = 0;
    virtual bool peek(byte_appender &destination, unsigned expected) = 0;

    virtual bool available(unsigned count) = 0;
};

class CommsOut : boost::noncopyable
{
public:
    virtual bool write(const bytes &buf) = 0;
};

class Comms : public CommsIn, virtual public CommsOut
{
public:
    CommsIn  &asInput()  { return *(static_cast<CommsIn *> (this)); };
    CommsOut &asOutput() { return *(static_cast<CommsOut *>(this)); };
};


class SocketComms : public Comms
{
private:
    CTINEXUS &_nexus;
    unsigned  _baud;

    void commDelay(unsigned chars);

    enum { SocketTimeout = 5 };

public:
    SocketComms(CTINEXUS &nexus, unsigned baud);

    virtual bool read(byte_appender &destination, unsigned expected);
    virtual bool peek(byte_appender &destination, unsigned expected);

    virtual bool available(unsigned count);

    virtual bool write(const bytes &buf);

    void clear();
};


class BufferCommsIn : public CommsIn
{
private:
    bytes::const_iterator _itr, _end;

public:
    BufferCommsIn(const bytes &input);

    virtual bool read(byte_appender &destination, unsigned expected);
    virtual bool peek(byte_appender &destination, unsigned expected);

    virtual bool available(unsigned count);
};

class BufferCommsOut : public CommsOut
{
private:
    byte_appender _output;

public:
    BufferCommsOut(byte_appender &output);

    virtual bool write(const bytes &buf);
};

class BufferComms : public Comms, public BufferCommsIn, public BufferCommsOut
{
public:
    BufferComms(const bytes &input, byte_appender &output);
};

}
}

