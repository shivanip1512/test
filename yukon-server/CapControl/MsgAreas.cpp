#include "precompiled.h"

#include "MsgAreas.h"
#include "ccid.h"
#include "logger.h"

extern unsigned long _CC_DEBUG;

using std::endl;

unsigned long CtiCCGeoAreasMsg::AllAreasSent = 0x00000001;
unsigned long CtiCCGeoAreasMsg::AreaDeleted   = 0x00000002;
unsigned long CtiCCGeoAreasMsg::AreaAdded     = 0x00000004;
unsigned long CtiCCGeoAreasMsg::AreaModified  = 0x00000008;

DEFINE_COLLECTABLE( CtiCCGeoAreasMsg, CTICCGEOAREAS_MSG_ID )

CtiCCGeoAreasMsg::CtiCCGeoAreasMsg(CtiCCArea_vec& ccGeoAreas, unsigned long bitMask) : Inherited(), _ccGeoAreas(NULL), _msgInfoBitMask(bitMask)
{
    _ccGeoAreas = new CtiCCArea_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CTILOG_DEBUG(dout, "CtiCCGeoAreasMsg has "<< ccGeoAreas.size()<<" entries.");
    }
    if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
    {
        for (int h=0;h < ccGeoAreas.size(); h++)
        {
            CTILOG_DEBUG(dout, "Area: "<<((CtiCCArea*)ccGeoAreas[h])->getPaoName());
        }
    }

    for(int i=0;i<ccGeoAreas.size();i++)
    {
        _ccGeoAreas->push_back(((CtiCCArea*)ccGeoAreas.at(i))->replicate());
    }
}


CtiCCGeoAreasMsg::CtiCCGeoAreasMsg(CtiCCArea_set& ccGeoAreas, unsigned long bitMask) : Inherited(), _ccGeoAreas(NULL), _msgInfoBitMask(bitMask)
{
    _ccGeoAreas = new CtiCCArea_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CTILOG_DEBUG(dout, "CtiCCGeoAreasMsg has "<< ccGeoAreas.size()<<" entries.");
    }
    CtiCCArea_set::iterator it;
    for(it = ccGeoAreas.begin(); it != ccGeoAreas.end(); it++)
    {
        _ccGeoAreas->push_back(((CtiCCArea*)*it)->replicate());
    }
}




CtiCCGeoAreasMsg::CtiCCGeoAreasMsg(const CtiCCGeoAreasMsg& ccGeoAreasMsg) : Inherited(), _ccGeoAreas(NULL), _msgInfoBitMask(ccGeoAreasMsg._msgInfoBitMask)
{
    operator=(ccGeoAreasMsg);
}

CtiCCGeoAreasMsg::~CtiCCGeoAreasMsg()
{
    if( _ccGeoAreas != NULL )
    {
        if( _ccGeoAreas->size() > 0 )
        {
            delete_container(*_ccGeoAreas);
        }
        delete _ccGeoAreas;
    }
}

CtiMessage* CtiCCGeoAreasMsg::replicateMessage() const
{
    return new CtiCCGeoAreasMsg(*this);
}

CtiCCGeoAreasMsg& CtiCCGeoAreasMsg::operator=(const CtiCCGeoAreasMsg& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);

        if( _ccGeoAreas != NULL &&
            _ccGeoAreas->size() > 0 )
        {
            delete_container(*_ccGeoAreas);
            _ccGeoAreas->clear();
            delete _ccGeoAreas;
        }

        if ( _ccGeoAreas == NULL )
            _ccGeoAreas = new CtiCCArea_vec;
        for(int i=0;i<(right.getCCGeoAreas())->size();i++)
        {
            _ccGeoAreas->push_back(((CtiCCArea*)(*right.getCCGeoAreas()).at(i))->replicate());
        }
    }

    return *this;
}
