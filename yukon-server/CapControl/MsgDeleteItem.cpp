#include "precompiled.h"

#include "MsgDeleteItem.h"
#include "ccid.h"

DEFINE_COLLECTABLE( DeleteItem, DELETE_ITEM_MSG_ID )

DeleteItem::DeleteItem(int itemId)
{
    _itemId = itemId;
}

DeleteItem::~DeleteItem()
{

}

DeleteItem& DeleteItem::operator=(const DeleteItem& right)
{

    if( this != &right )
    {
        Inherited::operator=(right);
    }

    return *this;
}

CtiMessage* DeleteItem::replicateMessage() const
{
    return new DeleteItem(*this);
}
