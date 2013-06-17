#pragma once

#include "MsgCapControlCommand.h"

class ItemCommand : public CapControlCommand
{
    RWDECLARE_COLLECTABLE( ItemCommand )

    private:
        typedef CapControlCommand Inherited;

    public:
        ItemCommand();
        ItemCommand(int commandId, int itemId);
        ItemCommand(const ItemCommand& commandMsg);
        virtual ~ItemCommand();

        int getItemId();
        void setItemId(int itemId);

        void restoreGuts(RWvistream& iStream);
        void saveGuts(RWvostream& oStream) const;

        ItemCommand& operator=(const ItemCommand& right);

        virtual CtiMessage* replicateMessage() const;

    private:
        int _itemId;
};