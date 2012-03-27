#include "precompiled.h"

#include "MsgSpecialAreas.h"
#include "ccid.h"

extern unsigned long _CC_DEBUG;

using std::endl;

RWDEFINE_COLLECTABLE( CtiCCSpecialAreasMsg, CTICCSPECIALAREAS_MSG_ID )

CtiCCSpecialAreasMsg::CtiCCSpecialAreasMsg(CtiCCSpArea_vec& ccSpecialAreas) : Inherited(), _ccSpecialAreas(NULL)
{
    _ccSpecialAreas = new CtiCCSpArea_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CtiCCSpecialAreasMsg has "<< ccSpecialAreas.size()<<" entries." << endl;
    }
    if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
    {
        for (int h=0;h < ccSpecialAreas.size(); h++)
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Area: "<<((CtiCCSpecial*)ccSpecialAreas[h])->getPaoName()<< endl;
            }
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CtiCCSpecialAreasMsg has "<< ccSpecialAreas.size()<<" entries." << endl;
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
    if( _ccSpecialAreas != NULL &&
            _ccSpecialAreas->size() > 0 )
        {
            delete_container(*_ccSpecialAreas);
            _ccSpecialAreas->clear();
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

void CtiCCSpecialAreasMsg::restoreGuts(RWvistream& strm)
{
    Inherited::restoreGuts(strm);
    strm >> _ccSpecialAreas;
}

void CtiCCSpecialAreasMsg::saveGuts(RWvostream& strm) const
{
    Inherited::saveGuts(strm);
    if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - CtiCCSpecialAreasMsg has "<< _ccSpecialAreas->size()<<" entries." << endl;
        }
        for (int h=0;h < _ccSpecialAreas->size(); h++)
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Area: "<<((CtiCCSpecial*)_ccSpecialAreas->at(h))->getPaoName()<<
                    " : "<<((CtiCCSpecial*)_ccSpecialAreas->at(h))->getPaoId()<< endl;
            }
        }
    }

    strm << _ccSpecialAreas;
}
