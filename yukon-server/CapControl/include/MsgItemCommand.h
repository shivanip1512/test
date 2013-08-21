#pragma once

#include "MsgCapControlCommand.h"

class ItemCommand : public CapControlCommand
{
    public:
        DECLARE_COLLECTABLE( ItemCommand );

    private:
        typedef CapControlCommand Inherited;

    public:
        ItemCommand();
        ItemCommand(int commandId, int itemId);
        ItemCommand(const ItemCommand& commandMsg);
        virtual ~ItemCommand();

        int getItemId() const;
        void setItemId(int itemId);

        ItemCommand& operator=(const ItemCommand& right);

        virtual CtiMessage* replicateMessage() const;

    private:
        int _itemId;
};
