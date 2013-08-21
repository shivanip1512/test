#include "precompiled.h"

#include "MsgCapControlCommand.h"
#include "ccid.h"

DEFINE_COLLECTABLE( CapControlCommand, CAP_CONTROL_COMMAND_ID )

CapControlCommand::CapControlCommand() :
    Inherited()
{

}

CapControlCommand::CapControlCommand(int commandId) :
    Inherited(),
    _commandId(commandId)
{

}

CapControlCommand::CapControlCommand(const CapControlCommand& commandMsg)
{
    operator=(commandMsg);
}

CapControlCommand::~CapControlCommand()
{

}

int CapControlCommand::getCommandId() const
{
    return _commandId;
}

long CapControlCommand::getMessageId() const
{
    return _messageId;
}

CapControlCommand& CapControlCommand::operator=(const CapControlCommand& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);
        _messageId = right._messageId;
        _commandId = right._commandId;
    }

    return *this;
}

CtiMessage* CapControlCommand::replicateMessage() const
{
    return new CapControlCommand(*this);
}
