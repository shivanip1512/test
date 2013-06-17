#pragma once

#include "MsgCapControlMessage.h"
#include "ccarea.h"

class CtiCCGeoAreasMsg : public CapControlMessage
{
    RWDECLARE_COLLECTABLE( CtiCCGeoAreasMsg )

    private:
        typedef CapControlMessage Inherited;

    public:
        CtiCCGeoAreasMsg(CtiCCArea_vec& areaList, unsigned long bitMask = 1);
        CtiCCGeoAreasMsg(CtiCCArea_set& areaList, unsigned long bitMask = 1);
        CtiCCGeoAreasMsg(CtiCCArea* ccArea);
        CtiCCGeoAreasMsg(const CtiCCGeoAreasMsg& ccGeoAreas);

        virtual ~CtiCCGeoAreasMsg();

        CtiCCArea_vec* getCCGeoAreas() const     { return _ccGeoAreas; }

        virtual CtiMessage* replicateMessage() const;

        void restoreGuts( RWvistream& );
        void saveGuts( RWvostream&) const;

        CtiCCGeoAreasMsg& operator=(const CtiCCGeoAreasMsg& right);

        static unsigned long AllAreasSent;
        static unsigned long AreaDeleted;
        static unsigned long AreaAdded;
        static unsigned long AreaModified;

    private:
        CtiCCGeoAreasMsg() : Inherited(), _ccGeoAreas(NULL), _msgInfoBitMask(1) {};

        CtiCCArea_vec* _ccGeoAreas;
        unsigned long _msgInfoBitMask;
};
