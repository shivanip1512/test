#include "precompiled.h"

#include "MsgSpecialAreas.h"
#include "ccid.h"
#include "logger.h"

extern unsigned long _CC_DEBUG;

using std::endl;

DEFINE_COLLECTABLE( CtiCCSpecialAreasMsg, CTICCSPECIALAREAS_MSG_ID )

CtiCCSpecialAreasMsg::CtiCCSpecialAreasMsg(CtiCCSpArea_vec& ccSpecialAreas) : Inherited(), _ccSpecialAreas(NULL)
{
    _ccSpecialAreas = new CtiCCSpArea_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CTILOG_DEBUG(dout, "CtiCCSpecialAreasMsg has "<< ccSpecialAreas.size()<<" entries.");
    }
    if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
    {
        for( const auto spArea : ccSpecialAreas )
        {
            CTILOG_DEBUG(dout, "Area: "<<spArea->getPaoName());
        }
    }

    for( const auto spArea : ccSpecialAreas )
    {
        _ccSpecialAreas->push_back(spArea->replicate());
    }
}

CtiCCSpecialAreasMsg::CtiCCSpecialAreasMsg(CtiCCSpArea_set& ccSpecialAreas) : Inherited(), _ccSpecialAreas(NULL)
{
    _ccSpecialAreas = new CtiCCSpArea_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CTILOG_DEBUG(dout, "CtiCCSpecialAreasMsg has "<< ccSpecialAreas.size()<<" entries.");
    }
    for( const auto spArea : ccSpecialAreas )
    {
        _ccSpecialAreas->push_back(spArea->replicate());
    }
}

CtiCCSpecialAreasMsg::CtiCCSpecialAreasMsg(const CtiCCSpecialAreasMsg& right)
{
    Inherited::operator=(right);

    _ccSpecialAreas = new CtiCCSpArea_vec;

    for( const auto spArea : *right.getCCSpecialAreas() )
    {
        _ccSpecialAreas->push_back(spArea->replicate());
    }
}

CtiCCSpecialAreasMsg::~CtiCCSpecialAreasMsg()
{
    if( _ccSpecialAreas != NULL )
    {
         delete_container(*_ccSpecialAreas);

        delete _ccSpecialAreas;
    }
}

CtiMessage* CtiCCSpecialAreasMsg::replicateMessage() const
{
    return new CtiCCSpecialAreasMsg(*this);
}
