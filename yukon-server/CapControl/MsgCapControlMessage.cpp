#include "precompiled.h"

#include "MsgCapControlMessage.h"

CapControlMessage::CapControlMessage()
{

}

CapControlMessage::~CapControlMessage()
{

}

CapControlMessage& CapControlMessage::operator=(const CapControlMessage& right)
{

    if( this != &right )
    {
        Inherited::operator=(right);
    }

    return *this;
}
