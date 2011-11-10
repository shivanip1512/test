#include "precompiled.h"

#include "MsgCapControlCommand.h"
#include "ccid.h"

RWDEFINE_COLLECTABLE( CapControlCommand, CAP_CONTROL_COMMAND_ID )

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

int CapControlCommand::getCommandId()
{
    return _commandId;
}

long CapControlCommand::getMessageId()
{
    return _messageId;
}

void CapControlCommand::restoreGuts(RWvistream& iStream)
{
    Inherited::restoreGuts(iStream);

    iStream >> _messageId
            >> _commandId;

    return;
}

void CapControlCommand::saveGuts(RWvostream& oStream) const
{
    Inherited::saveGuts(oStream);


    oStream << _messageId
            << _commandId;

    return;
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
