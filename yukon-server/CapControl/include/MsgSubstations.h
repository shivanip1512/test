#pragma once

#include "MsgCapControlMessage.h"
#include "ccsubstation.h"

class CtiCCSubstationsMsg : public CapControlMessage
{
    public:
        DECLARE_COLLECTABLE( CtiCCSubstationsMsg );

    private:
        typedef CapControlMessage Inherited;
    public:

        CtiCCSubstationsMsg(CtiCCSubstation_vec& substationList, unsigned long bitMask = 0);
        CtiCCSubstationsMsg(CtiCCSubstation_set& substationList, unsigned long bitMask = 0);
        CtiCCSubstationsMsg(const CtiCCSubstationsMsg& ccSubstations);

        virtual ~CtiCCSubstationsMsg();

        unsigned long getMsgInfoBitMask() const { return _msgInfoBitMask; };

        CtiCCSubstation_vec* getCCSubstations() const     { return _ccSubstations; }

        virtual CtiMessage* replicateMessage() const;

        CtiCCSubstationsMsg& operator=(const CtiCCSubstationsMsg& right) = delete;

        // Possible bit mask settings
        static const unsigned long AllSubsSent;
        static const unsigned long SubDeleted;
        static const unsigned long SubAdded;
        static const unsigned long SubModified;


    private:
        CtiCCSubstationsMsg() : Inherited(), _ccSubstations(NULL), _msgInfoBitMask(0){};

        unsigned long _msgInfoBitMask;
        CtiCCSubstation_vec* _ccSubstations;
};
