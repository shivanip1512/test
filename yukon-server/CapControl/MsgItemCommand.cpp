#include "precompiled.h"

#include <string>

#include "MsgItemCommand.h"
#include "ccid.h"
#include "logger.h"

using std::endl;

RWDEFINE_COLLECTABLE( ItemCommand, ITEM_COMMAND_MSG_ID )

ItemCommand::ItemCommand() :
    Inherited()
{

}

ItemCommand::ItemCommand(int commandId, int itemId) :
    Inherited(commandId),
    _itemId(itemId)
{

}

ItemCommand::ItemCommand(const ItemCommand& commandMsg)
{
    operator=(commandMsg);
}

ItemCommand::~ItemCommand()
{

}

int ItemCommand::getItemId()
{
    return _itemId;
}

void ItemCommand::setItemId(int itemId)
{
    _itemId = itemId;
}

void ItemCommand::restoreGuts(RWvistream& iStream)
{
    Inherited::restoreGuts(iStream);

    iStream >> _itemId;

    return;
}

void ItemCommand::saveGuts(RWvostream& oStream) const
{
    Inherited::saveGuts(oStream);

    oStream << _itemId;

    return;
}

ItemCommand& ItemCommand::operator=(const ItemCommand& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);
        _itemId = right._itemId;
    }

    return *this;
}

CtiMessage* ItemCommand::replicateMessage() const
{
    return new ItemCommand(*this);
}
