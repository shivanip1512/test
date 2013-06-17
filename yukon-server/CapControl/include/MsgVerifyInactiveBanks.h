#pragma once

#include "MsgVerifyBanks.h"

class VerifyInactiveBanks : public VerifyBanks
{
    RWDECLARE_COLLECTABLE( VerifyInactiveBanks )

    private:
        typedef VerifyBanks Inherited;

    public:
        VerifyInactiveBanks();
        VerifyInactiveBanks(int itemId, long bankInactiveTime, bool disableOvUv);
        VerifyInactiveBanks(const VerifyInactiveBanks& msg);
        ~VerifyInactiveBanks();

        long getBankInactiveTime();
        void setBankInactiveTime(long bankInactiveTime);

        void restoreGuts(RWvistream& iStream);
        void saveGuts(RWvostream& oStream) const;

        VerifyInactiveBanks& operator=(const VerifyInactiveBanks& right);

        virtual CtiMessage* replicateMessage() const;

    private:
        long _bankInactiveTime;
};
