#pragma once

#include "MsgCapControlMessage.h"

class DeleteItem : public CapControlMessage
{
    RWDECLARE_COLLECTABLE( DeleteItem );

    private:
        typedef CapControlMessage Inherited;

    public:
        DeleteItem(int itemId);
        virtual ~DeleteItem();

        void restoreGuts(RWvistream& iStream);
        void saveGuts(RWvostream& oStream) const;

        DeleteItem& operator=(const DeleteItem& right);
        virtual CtiMessage* replicateMessage() const;

    private:
        int _itemId;
        DeleteItem(){};
};
