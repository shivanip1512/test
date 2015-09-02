#pragma once

#include "MsgCapControlMessage.h"
#include "ccarea.h"

class CtiCCGeoAreasMsg : public CapControlMessage
{
    public:
        DECLARE_COLLECTABLE( CtiCCGeoAreasMsg )

    private:
        typedef CapControlMessage Inherited;

    public:
        CtiCCGeoAreasMsg(CtiCCArea_vec& areaList, unsigned long bitMask = 1);
        CtiCCGeoAreasMsg(CtiCCArea_set& areaList, unsigned long bitMask = 1);
        CtiCCGeoAreasMsg(CtiCCArea* ccArea);
        CtiCCGeoAreasMsg(const CtiCCGeoAreasMsg& ccGeoAreas);

        virtual ~CtiCCGeoAreasMsg();

        CtiCCArea_vec* getCCGeoAreas() const     { return _ccGeoAreas; }
        unsigned long  getBitMask()    const     { return _msgInfoBitMask; }

        virtual CtiMessage* replicateMessage() const;

        CtiCCGeoAreasMsg& operator=(const CtiCCGeoAreasMsg& right) = delete;

        static unsigned long AllAreasSent;
        static unsigned long AreaDeleted;
        static unsigned long AreaAdded;
        static unsigned long AreaModified;

    private:
        CtiCCGeoAreasMsg() : Inherited(), _ccGeoAreas(NULL), _msgInfoBitMask(1) {};

        CtiCCArea_vec* _ccGeoAreas;
        unsigned long _msgInfoBitMask;
};
