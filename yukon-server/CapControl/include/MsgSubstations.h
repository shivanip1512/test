#pragma once

#include "MsgCapControlMessage.h"
#include "ccsubstation.h"

class CtiCCSubstationsMsg : public CapControlMessage
{
    RWDECLARE_COLLECTABLE( CtiCCSubstationsMsg )

    private:
        typedef CapControlMessage Inherited;
    public:

        CtiCCSubstationsMsg(CtiCCSubstation_vec& substationList, ULONG bitMask = 0);
        CtiCCSubstationsMsg(CtiCCSubstation_set& substationList, ULONG bitMask = 0);
        CtiCCSubstationsMsg(CtiCCSubstation* ccSubstations);
        CtiCCSubstationsMsg(const CtiCCSubstationsMsg& ccSubstations);

        virtual ~CtiCCSubstationsMsg();

        ULONG getMsgInfoBitMask() const { return _msgInfoBitMask; };

        CtiCCSubstation_vec* getCCSubstations() const     { return _ccSubstations; }

        virtual CtiMessage* replicateMessage() const;

        void restoreGuts( RWvistream& );
        void saveGuts( RWvostream&) const;

        CtiCCSubstationsMsg& operator=(const CtiCCSubstationsMsg& right);

        // Possible bit mask settings
        static ULONG AllSubsSent;
        static ULONG SubDeleted;
        static ULONG SubAdded;
        static ULONG SubModified;


    private:
        CtiCCSubstationsMsg() : Inherited(), _ccSubstations(NULL), _msgInfoBitMask(0){};

        ULONG _msgInfoBitMask;
        CtiCCSubstation_vec* _ccSubstations;
};
