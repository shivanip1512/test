#pragma once

#include "MsgVerifyBanks.h"

class VerifyInactiveBanks : public VerifyBanks
{
    public:
        DECLARE_COLLECTABLE( VerifyInactiveBanks );

    private:
        typedef VerifyBanks Inherited;

    public:
        VerifyInactiveBanks();
        VerifyInactiveBanks(int itemId, long bankInactiveTime, bool disableOvUv);
        VerifyInactiveBanks(const VerifyInactiveBanks& msg);
        ~VerifyInactiveBanks();

        long getBankInactiveTime() const;
        void setBankInactiveTime(long bankInactiveTime);

        VerifyInactiveBanks& operator=(const VerifyInactiveBanks& right);

        virtual CtiMessage* replicateMessage() const;

    private:
        long _bankInactiveTime;
};
