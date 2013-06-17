
#pragma once

#include "MsgVerifyBanks.h"

class VerifySelectedBank : public VerifyBanks
{
    RWDECLARE_COLLECTABLE( VerifySelectedBank )

    private:
        typedef VerifyBanks Inherited;

    public:
        VerifySelectedBank();
        VerifySelectedBank(int itemId, int bankId, bool disableOvUv);
        VerifySelectedBank(const VerifySelectedBank& msg);
        ~VerifySelectedBank();

        long getBankId();
        void setBankId(long bankIId);

        void restoreGuts(RWvistream& iStream);
        void saveGuts(RWvostream& oStream) const;

        VerifySelectedBank& operator=(const VerifySelectedBank& right);

        virtual CtiMessage* replicateMessage() const;

    private:
        long _bankId;
};
