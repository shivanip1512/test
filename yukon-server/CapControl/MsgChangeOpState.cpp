#include "precompiled.h"

#include "MsgChangeOpState.h"
#include "ccid.h"

using std::string;

DEFINE_COLLECTABLE( ChangeOpState, CHANGE_OP_STATE_MSG_ID )

ChangeOpState::ChangeOpState() :
    Inherited()
{

}

ChangeOpState::ChangeOpState(int bankId, const string& opStateName) :
    Inherited(CapControlCommand::CHANGE_OPERATIONALSTATE,bankId),
    _opStateName(opStateName)
{

}

ChangeOpState::ChangeOpState(const ChangeOpState& msg)
{
    operator=(msg);
}

ChangeOpState::~ChangeOpState()
{

}

const string& ChangeOpState::getOpStateName() const
{
    return _opStateName;
}

void ChangeOpState::setOpStateName(const string& opStateName)
{
    _opStateName = opStateName;
}

ChangeOpState& ChangeOpState::operator=(const ChangeOpState& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);
        _opStateName = right._opStateName;
    }

    return *this;
}

CtiMessage* ChangeOpState::replicateMessage() const
{
    return new ChangeOpState(*this);
}
