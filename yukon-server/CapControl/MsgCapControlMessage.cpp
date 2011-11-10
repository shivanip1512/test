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

void CapControlMessage::restoreGuts(RWvistream& strm)
{
    Inherited::restoreGuts(strm);

    return;
}

void CapControlMessage::saveGuts(RWvostream& strm) const
{
    Inherited::saveGuts(strm);

    return;
}

