#pragma once

#include "MsgCapControlMessage.h"
#include "ccsparea.h"

class CtiCCSpecialAreasMsg : public CapControlMessage
{
    public:
        DECLARE_COLLECTABLE( CtiCCSpecialAreasMsg );

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

        CtiCCSpecialAreasMsg& operator=(const CtiCCSpecialAreasMsg& right) = delete;

    private:
        CtiCCSpecialAreasMsg() : Inherited(), _ccSpecialAreas(NULL){};

        CtiCCSpArea_vec* _ccSpecialAreas;
};
