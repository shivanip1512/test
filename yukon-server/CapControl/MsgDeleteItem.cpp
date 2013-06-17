#include "precompiled.h"

#include "MsgDeleteItem.h"
#include "ccid.h"

RWDEFINE_COLLECTABLE( DeleteItem, DELETE_ITEM_MSG_ID )

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

void DeleteItem::restoreGuts(RWvistream& strm)
{
    Inherited::restoreGuts(strm);

    strm >> _itemId;

    return;
}

void DeleteItem::saveGuts(RWvostream& strm) const
{
    Inherited::saveGuts(strm);

    strm << _itemId;

    return;
}

CtiMessage* DeleteItem::replicateMessage() const
{
    return new DeleteItem(*this);
}
