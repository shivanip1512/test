#pragma once

#include "streamSocketConnection.h"
#include "types.h"
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
    bool write(const bytes &buf, Logger &logger) override;
private:
    virtual bool writeMessage(const bytes &buf) = 0;
    void ProcessMessage(bytes &buf, Logger &logger);
};


class SocketComms : public Comms
{
private:
    StreamSocketConnection &_nexus;
    unsigned  _baud;

    void commDelay(unsigned chars);

    enum { SocketTimeout = 5 };

public:
    SocketComms(StreamSocketConnection &nexus, unsigned baud);

    bool read(byte_appender &destination, unsigned expected) override;
    bool peek(byte_appender &destination, unsigned expected) override;

    bool available(unsigned aCount) override;

    bool writeMessage(const bytes &buf) override;

    void setBehavior(std::unique_ptr<CommsBehavior> &&behavior);
    void clear();
};


class BufferCommsIn : public CommsIn
{
private:
    bytes::const_iterator _itr, _end;

public:
    BufferCommsIn(const bytes &input);

    bool read(byte_appender &destination, unsigned expected) override;
    bool peek(byte_appender &destination, unsigned expected) override;

    bool available(unsigned aCount) override;
};

}
}

