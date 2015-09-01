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
        for (int h=0;h < ccSpecialAreas.size(); h++)
        {
            CTILOG_DEBUG(dout, "Area: "<<((CtiCCSpecial*)ccSpecialAreas[h])->getPaoName());
        }
    }

    for(int i=0;i<ccSpecialAreas.size();i++)
    {
        _ccSpecialAreas->push_back((CtiCCSpecial*)(ccSpecialAreas.at(i))->replicate());
    }
}

CtiCCSpecialAreasMsg::CtiCCSpecialAreasMsg(CtiCCSpArea_set& ccSpecialAreas) : Inherited(), _ccSpecialAreas(NULL)
{
    _ccSpecialAreas = new CtiCCSpArea_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CTILOG_DEBUG(dout, "CtiCCSpecialAreasMsg has "<< ccSpecialAreas.size()<<" entries.");
    }
    CtiCCSpArea_set::iterator it;
    for(it = ccSpecialAreas.begin(); it != ccSpecialAreas.end(); it++)
    {
        _ccSpecialAreas->push_back((CtiCCSpecial*)(*it)->replicate());
    }
}

CtiCCSpecialAreasMsg::CtiCCSpecialAreasMsg(const CtiCCSpecialAreasMsg& ccSpecialAreasMsg) : Inherited(), _ccSpecialAreas(NULL)
{
    operator=(ccSpecialAreasMsg);
}

CtiCCSpecialAreasMsg::~CtiCCSpecialAreasMsg()
{
    if( _ccSpecialAreas != NULL )
    {
        if( _ccSpecialAreas->size() > 0 )
        {
            delete_container(*_ccSpecialAreas);
        }
        delete _ccSpecialAreas;
    }
}

CtiMessage* CtiCCSpecialAreasMsg::replicateMessage() const
{
    return new CtiCCSpecialAreasMsg(*this);
}

CtiCCSpecialAreasMsg& CtiCCSpecialAreasMsg::operator=(const CtiCCSpecialAreasMsg& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);

        if( _ccSpecialAreas != NULL &&
            _ccSpecialAreas->size() > 0 )
        {
            delete_container(*_ccSpecialAreas);
            _ccSpecialAreas->clear();
            delete _ccSpecialAreas;
        }
        if ( _ccSpecialAreas == NULL )
            _ccSpecialAreas = new CtiCCSpArea_vec;
        for(int i=0;i<(right.getCCSpecialAreas())->size();i++)
        {

            _ccSpecialAreas->push_back(((CtiCCSpecial*)(*right.getCCSpecialAreas()).at(i))->replicate());
        }
    }

    return *this;
}
