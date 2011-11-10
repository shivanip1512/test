#pragma once

#include "MsgCapControlMessage.h"

#include "ccsubstationbus.h"

class CtiCCSubstationBusMsg : public CapControlMessage
{
    RWDECLARE_COLLECTABLE( CtiCCSubstationBusMsg )

    private:
        typedef CapControlMessage Inherited;

    public:
        CtiCCSubstationBusMsg(CtiCCSubstationBus_vec& buses, ULONG bitMask = 0);
        CtiCCSubstationBusMsg(CtiCCSubstationBus_set& buses, ULONG bitMask = 0);
        CtiCCSubstationBusMsg(const CtiCCSubstationBusMsg& substationBusesMsg);
        CtiCCSubstationBusMsg(CtiCCSubstationBus* substationBus);

        virtual ~CtiCCSubstationBusMsg();

        unsigned long getMsgInfoBitMask() const { return _msgInfoBitMask; };
        CtiCCSubstationBus_vec* getCCSubstationBuses() const { return _ccSubstationBuses; }
        virtual CtiMessage* replicateMessage() const;

        void restoreGuts( RWvistream& iStream);
        void saveGuts( RWvostream& oStream) const;

        CtiCCSubstationBusMsg& operator=(const CtiCCSubstationBusMsg& right);

        // Possible bit mask settings
        static ULONG AllSubBusesSent;
        static ULONG SubBusDeleted;
        static ULONG SubBusAdded;
        static ULONG SubBusModified;

    private:
        CtiCCSubstationBusMsg() : CapControlMessage(),
                                  _ccSubstationBuses(NULL),
                                  _msgInfoBitMask(0){};

        unsigned long _msgInfoBitMask;
        CtiCCSubstationBus_vec* _ccSubstationBuses;
};

