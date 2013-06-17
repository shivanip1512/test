#pragma once

#include "MsgCapControlMessage.h"
#include "ccsparea.h"

class CtiCCSpecialAreasMsg : public CapControlMessage
{
    RWDECLARE_COLLECTABLE( CtiCCSpecialAreasMsg )

    private:
        typedef CapControlMessage Inherited;

    public:

        CtiCCSpecialAreasMsg(CtiCCSpArea_vec& areaList);
        CtiCCSpecialAreasMsg(CtiCCSpArea_set& areaList);
        CtiCCSpecialAreasMsg(CtiCCSpecial* ccArea);
        CtiCCSpecialAreasMsg(const CtiCCSpecialAreasMsg& ccSpecialAreas);

        virtual ~CtiCCSpecialAreasMsg();

        CtiCCSpArea_vec* getCCSpecialAreas() const     { return _ccSpecialAreas; }

        virtual CtiMessage* replicateMessage() const;

        void restoreGuts( RWvistream& );
        void saveGuts( RWvostream&) const;

        CtiCCSpecialAreasMsg& operator=(const CtiCCSpecialAreasMsg& right);
    private:
        CtiCCSpecialAreasMsg() : Inherited(), _ccSpecialAreas(NULL){};

        CtiCCSpArea_vec* _ccSpecialAreas;
};
