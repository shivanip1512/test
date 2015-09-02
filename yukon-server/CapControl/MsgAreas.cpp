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
        for( const auto geoArea : ccGeoAreas )
        {
            CTILOG_DEBUG(dout, "Area: "<<geoArea->getPaoName());
        }
    }

    for( const auto geoArea : ccGeoAreas )
    {
        _ccGeoAreas->push_back(geoArea->replicate());
    }
}


CtiCCGeoAreasMsg::CtiCCGeoAreasMsg(CtiCCArea_set& ccGeoAreas, unsigned long bitMask) : Inherited(), _ccGeoAreas(NULL), _msgInfoBitMask(bitMask)
{
    _ccGeoAreas = new CtiCCArea_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CTILOG_DEBUG(dout, "CtiCCGeoAreasMsg has "<< ccGeoAreas.size()<<" entries.");
    }
    for( const auto geoArea : ccGeoAreas )
    {
        _ccGeoAreas->push_back(geoArea->replicate());
    }
}




CtiCCGeoAreasMsg::CtiCCGeoAreasMsg(const CtiCCGeoAreasMsg& right)
{
    _msgInfoBitMask = right._msgInfoBitMask;

    Inherited::operator=(right);

    _ccGeoAreas = new CtiCCArea_vec;

    for( const auto geoArea : *right.getCCGeoAreas() )
    {
        _ccGeoAreas->push_back(geoArea->replicate());
    }
}

CtiCCGeoAreasMsg::~CtiCCGeoAreasMsg()
{
    if( _ccGeoAreas != NULL )
    {
        delete_container(*_ccGeoAreas);

        delete _ccGeoAreas;
    }
}

CtiMessage* CtiCCGeoAreasMsg::replicateMessage() const
{
    return new CtiCCGeoAreasMsg(*this);
}
