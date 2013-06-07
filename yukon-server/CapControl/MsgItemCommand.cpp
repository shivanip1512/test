#include "precompiled.h"

#include <string>

#include "MsgItemCommand.h"
#include "ccid.h"
#include "logger.h"

using std::endl;

DEFINE_COLLECTABLE( ItemCommand, ITEM_COMMAND_MSG_ID )

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

int ItemCommand::getItemId() const
{
    return _itemId;
}

void ItemCommand::setItemId(int itemId)
{
    _itemId = itemId;
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
