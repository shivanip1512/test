#pragma once

enum CtiCCEventType_t
{
    capControlNoEvent = -1,
    capBankStateUpdate = 0,  //operation confirmed
    capControlCommandSent = 1, //operation sent
    capControlManualCommand = 2,
    capControlPointOutsideOperatingLimits = 3,
    capControlSetOperationCount = 4,
    capControlEnable = 5,
    capControlDisable = 6,
    capControlEnableVerification = 7,
    capControlDisableVerification = 8,
    capControlVerificationCommandSent = 9,
    capControlSwitchOverUpdate = 10,
    capControlEnableOvUv = 11,
    capControlDisableOvUv = 12,
    capBankStateUpdateFail = 13,
    capControlCommandRetrySent = 14,
    capControlUnexpectedCBCStateReported = 15,
    capControlIvvcCommStatus = 16,
    capControlIvvcMissingPoint = 17,
    capControlIvvcRejectedPoint = 18,
    capControlIvvcTapOperation = 19,
    capControlIvvcRemoteControlEvent = 20,
    capControlIvvcScanOperation = 21
};

