#pragma once

#include "MsgVerifyBanks.h"
#include "MsgVerifyInactiveBanks.h"
#include "MsgVerifySelectedBank.h"
#include "ccexecutor.h"

class VerificationExecutor : public CtiCCExecutor
{
    public:
        enum VerificationAction
        {
            VERIFY_START,
            VERIFY_STOP,
            VERIFY_FORCE_STOP
        } ;

        VerificationExecutor(VerifyBanks* command);
        VerificationExecutor(VerifyInactiveBanks* command);
        VerificationExecutor(VerifySelectedBank* command);
        virtual void execute();

        VerificationAction convertVerificationCommand();

    private:

        void startVerification();
        void stopVerification(bool immediate = false);

        long _deviceId;
        long _verifyType;
        bool _disableOvUv;
        long _inactiveTime;
        long _bankId;
        std::string _userName;
};
