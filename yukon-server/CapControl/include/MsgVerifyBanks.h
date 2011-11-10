#pragma once

#include "MsgItemCommand.h"

class VerifyBanks : public ItemCommand
{
    RWDECLARE_COLLECTABLE( VerifyBanks )

    private:
        typedef ItemCommand Inherited;

    public:
        VerifyBanks();
        VerifyBanks(int itemId, bool disableOvUv, int strategy);
        VerifyBanks(const VerifyBanks& msg);
        ~VerifyBanks();

        bool getDisableOvUv();
        void setDisableOvUv(bool disableOvUv);

        void restoreGuts(RWvistream& iStream);
        void saveGuts(RWvostream& oStream) const;

        VerifyBanks& operator=(const VerifyBanks& right);

        virtual CtiMessage* replicateMessage() const;

    private:
        bool _disableOvUv;
};
