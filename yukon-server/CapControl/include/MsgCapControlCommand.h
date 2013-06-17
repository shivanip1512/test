#pragma once

#include "message.h"

class CapControlCommand : public CtiMessage
{
    RWDECLARE_COLLECTABLE( CapControlCommand );

    private:
        typedef CtiMessage Inherited;

    public:
        enum
        {
            UNDEFINED = -1,
            ENABLE_SUBSTATION_BUS = 0,
            DISABLE_SUBSTATION_BUS,//1
            ENABLE_FEEDER,//2
            DISABLE_FEEDER,//3
            ENABLE_CAPBANK,//4
            DISABLE_CAPBANK,//5
            //OPEN_CAPBANK,//6
            //CLOSE_CAPBANK,//7
            CONFIRM_OPEN=8,//8
            CONFIRM_CLOSE,//9
            REQUEST_ALL_DATA,//10
            RETURN_CAP_TO_ORIGINAL_FEEDER,//11
            RESET_DAILY_OPERATIONS,//12
            ENABLE_SUBSTATION,
            DISABLE_SUBSTATION,
            CONFIRM_FEEDER = 15,
            RESET_SYSTEM_OP_COUNTS=16,

            //ENABLE_OVUV,//17
            //DISABLE_OVUV,//18

            DELETE_ITEM=19, //19
            CONFIRM_SUBSTATIONBUS, //20
            CONFIRM_AREA, //21
            ENABLE_AREA,  //22
            DISABLE_AREA,  //23
            //SCAN_2WAY_DEVICE,  //24
            ENABLE_SYSTEM=25,  //25
            DISABLE_SYSTEM,  //26
            FLIP_7010_CAPBANK,  //27
            SYSTEM_STATUS, //28

            SEND_OPEN_CAPBANK, //29
            SEND_CLOSE_CAPBANK, //30

            SEND_ENABLE_OVUV, //31
            SEND_DISABLE_OVUV, //32
            SEND_SCAN_2WAY_DEVICE, //33
            SEND_TIME_SYNC, //34
            CHANGE_OPERATIONALSTATE,  //35
            AUTO_ENABLE_OVUV, //36
            AUTO_DISABLE_OVUV, //37
            RETURN_FEEDER_TO_ORIGINAL_SUBBUS,//38
            CONFIRM_SUBSTATION,//39
            SEND_ENABLE_TEMPCONTROL = 40,
            SEND_DISABLE_TEMPCONTROL,
            SEND_ENABLE_VARCONTROL,
            SEND_DISABLE_VARCONTROL,
            SEND_ENABLE_TIMECONTROL,
            SEND_DISABLE_TIMECONTROL,//45
            //BANK_ENABLE_TEMPCONTROL,
            //BANK_DISABLE_TEMPCONTROL,
            //BANK_ENABLE_VARCONTROL,
            //BANK_DISABLE_VARCONTROL,
            //BANK_ENABLE_TIMECONTROL,//50
            //BANK_DISABLE_TIMECONTROL,
            //SYNC_CBC_CAPBANK_STATE=52, //52
            SEND_SYNC_CBC_CAPBANK_STATE=53, //53
            MOVE_BANK,//54
            MANUAL_ENTRY,//55

            VERIFY_ALL_BANK,//56
            VERIFY_FAILED_QUESTIONABLE_BANK,//57
            VERIFY_FAILED_BANK,//58
            VERIFY_QUESTIONABLE_BANK,//59
            VERIFY_INACTIVE_BANKS,//60
            VERIFY_STAND_ALONE_BANK,//61
            STOP_VERIFICATION,//62
            EMERGENCY_STOP_VERIFICATION,//63
            VERIFY_SELECTED_BANK,//64

            VOLTAGE_REGULATOR_INTEGRITY_SCAN = 70,
            VOLTAGE_REGULATOR_REMOTE_CONTROL_ENABLE,
            VOLTAGE_REGULATOR_REMOTE_CONTROL_DISABLE,
            VOLTAGE_REGULATOR_TAP_POSITION_RAISE,
            VOLTAGE_REGULATOR_TAP_POSITION_LOWER,
            VOLTAGE_REGULATOR_KEEP_ALIVE_ENABLE,
            VOLTAGE_REGULATOR_KEEP_ALIVE_DISABLE    // 76
        };

        CapControlCommand();
        CapControlCommand(int commandId);
        CapControlCommand(const CapControlCommand& commandMsg);

        virtual ~CapControlCommand();

        void setCommandId(int commandId);
        int getCommandId();

        long getMessageId();

        void restoreGuts(RWvistream& iStream);
        void saveGuts(RWvostream& oStream) const;

        CapControlCommand& operator=(const CapControlCommand& right);

        virtual CtiMessage* replicateMessage() const;

    private:
        int _commandId;
        long _messageId;
};
