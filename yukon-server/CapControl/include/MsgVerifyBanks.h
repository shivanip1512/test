#pragma once

#include "MsgItemCommand.h"

class VerifyBanks : public ItemCommand
{
    public:
        DECLARE_COLLECTABLE( VerifyBanks );

    private:
        typedef ItemCommand Inherited;

    public:
        VerifyBanks();
        VerifyBanks(int itemId, bool disableOvUv, int strategy);
        VerifyBanks(const VerifyBanks& msg);
        ~VerifyBanks();

        bool getDisableOvUv() const;
        void setDisableOvUv(bool disableOvUv);

        VerifyBanks& operator=(const VerifyBanks& right);

        virtual CtiMessage* replicateMessage() const;

    private:
        bool _disableOvUv;
};
