#pragma once

#include "types.h"
#include "ctinexus.h"
#include "BehaviorCollection.h"
#include "CommsBehavior.h"
#include "SimulatorLogger.h"

#include <boost/utility.hpp>
#include <boost/shared_ptr.hpp>

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
    virtual bool write(const bytes &buf, Logger &logger) = 0;
};

class Comms : public CommsIn, virtual public CommsOut
{
protected:
    BehaviorCollection<CommsBehavior> _behaviorCollection;
public:
    CommsIn  &asInput()  { return *(static_cast<CommsIn *> (this)); };
    CommsOut &asOutput() { return *(static_cast<CommsOut *>(this)); };
    virtual bool write(const bytes &buf, Logger &logger);
private:
    virtual bool writeMessage(const bytes &buf)=0;
    bool ProcessMessage(bytes &buf, Logger &logger);
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

    virtual bool available(unsigned aCount);

    virtual bool writeMessage(const bytes &buf);

    void setBehavior(std::auto_ptr<CommsBehavior> behavior);
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

    virtual bool available(unsigned aCount);
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

