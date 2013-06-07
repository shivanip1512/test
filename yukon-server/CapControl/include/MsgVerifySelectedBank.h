
#pragma once

#include "MsgVerifyBanks.h"

class VerifySelectedBank : public VerifyBanks
{
    public:
        DECLARE_COLLECTABLE( VerifySelectedBank );

    private:
        typedef VerifyBanks Inherited;

    public:
        VerifySelectedBank();
        VerifySelectedBank(int itemId, int bankId, bool disableOvUv);
        VerifySelectedBank(const VerifySelectedBank& msg);
        ~VerifySelectedBank();

        long getBankId() const;
        void setBankId(long bankIId);

        VerifySelectedBank& operator=(const VerifySelectedBank& right);

        virtual CtiMessage* replicateMessage() const;

    private:
        long _bankId;
};
