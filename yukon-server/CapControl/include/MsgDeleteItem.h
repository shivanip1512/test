#pragma once

#include "MsgCapControlMessage.h"

class DeleteItem : public CapControlMessage
{
    public:
        DECLARE_COLLECTABLE( DeleteItem );

    private:
        typedef CapControlMessage Inherited;

    public:
        DeleteItem(int itemId);
        virtual ~DeleteItem();

        DeleteItem& operator=(const DeleteItem& right);
        virtual CtiMessage* replicateMessage() const;

        int getItemId() const { return _itemId; }

    private:
        int _itemId;
        DeleteItem(){};
};
