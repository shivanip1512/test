#pragma once

#include "MsgCapControlMessage.h"

#include "ccsubstationbus.h"

class CtiCCSubstationBusMsg : public CapControlMessage
{
    public:
        DECLARE_COLLECTABLE( CtiCCSubstationBusMsg );

    private:
        typedef CapControlMessage Inherited;

    public:
        CtiCCSubstationBusMsg(CtiCCSubstationBus_vec& buses, unsigned long bitMask = 0);
        CtiCCSubstationBusMsg(CtiCCSubstationBus_set& buses, unsigned long bitMask = 0);
        CtiCCSubstationBusMsg(const CtiCCSubstationBusMsg& substationBusesMsg);
        CtiCCSubstationBusMsg(CtiCCSubstationBus* substationBus);

        virtual ~CtiCCSubstationBusMsg();

        unsigned long getMsgInfoBitMask() const { return _msgInfoBitMask; };
        CtiCCSubstationBus_vec* getCCSubstationBuses() const { return _ccSubstationBuses; }
        virtual CtiMessage* replicateMessage() const;

        CtiCCSubstationBusMsg& operator=(const CtiCCSubstationBusMsg& right) = delete;

        // Possible bit mask settings
        static unsigned long AllSubBusesSent;
        static unsigned long SubBusDeleted;
        static unsigned long SubBusAdded;
        static unsigned long SubBusModified;

    private:
        CtiCCSubstationBusMsg() : CapControlMessage(),
                                  _ccSubstationBuses(NULL),
                                  _msgInfoBitMask(0){};

        unsigned long _msgInfoBitMask;
        CtiCCSubstationBus_vec* _ccSubstationBuses;
};

