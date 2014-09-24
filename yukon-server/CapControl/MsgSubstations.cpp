#include "precompiled.h"

#include "MsgSubstations.h"
#include "ccid.h"
#include "logger.h"

extern unsigned long _CC_DEBUG;

using std::endl;

const unsigned long CtiCCSubstationsMsg::AllSubsSent = 0x00000001;
const unsigned long CtiCCSubstationsMsg::SubDeleted  = 0x00000002;
const unsigned long CtiCCSubstationsMsg::SubAdded    = 0x00000004;
const unsigned long CtiCCSubstationsMsg::SubModified = 0x00000008;

DEFINE_COLLECTABLE( CtiCCSubstationsMsg, CTICCSUBSTATION_MSG_ID )

CtiCCSubstationsMsg::CtiCCSubstationsMsg(CtiCCSubstation_vec& ccSubstations, unsigned long bitMask) : Inherited(), _ccSubstations(NULL), _msgInfoBitMask(bitMask)
{
    _ccSubstations = new CtiCCSubstation_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CtiCCSubstationsMsg has "<< ccSubstations.size()<<" entries." << endl;
    }
    if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
    {
        for (int h=0;h < ccSubstations.size(); h++)
        {
            CtiCCSubstation* station = (CtiCCSubstation*)ccSubstations[h];
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Substation: "<<station->getPaoName()<< " Parent: "<< station->getParentId() <<" SAEnabled ?"<<station->getSaEnabledFlag() << " SAEnId : " << station->getSaEnabledId()<< endl;
            }
            for each (long busId in station->getCCSubIds())
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " -    SubBus: "<< busId << endl;
                }
            }
        }
    }

    for(int i=0;i<ccSubstations.size();i++)
    {
        _ccSubstations->push_back((CtiCCSubstation*)(ccSubstations.at(i))->replicate());
    }

}

CtiCCSubstationsMsg::CtiCCSubstationsMsg(CtiCCSubstation_set& ccSubstations, unsigned long bitMask) : Inherited(), _ccSubstations(NULL), _msgInfoBitMask(bitMask)
{
    _ccSubstations = new CtiCCSubstation_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CtiCCSubstationsMsg has "<< ccSubstations.size()<<" entries." << endl;
    }
    CtiCCSubstation_set::iterator it;
    for(it = ccSubstations.begin(); it != ccSubstations.end(); it++)
    {
        _ccSubstations->push_back((CtiCCSubstation*)(*it)->replicate());
    }

}


CtiCCSubstationsMsg::CtiCCSubstationsMsg(const CtiCCSubstationsMsg& ccSubstationsMsg) : Inherited(), _ccSubstations(NULL)
{
    operator=(ccSubstationsMsg);
}

CtiCCSubstationsMsg::~CtiCCSubstationsMsg()
{
    if( _ccSubstations != NULL &&
            _ccSubstations->size() > 0 )
        {
            delete_container(*_ccSubstations);
            _ccSubstations->clear();
            delete _ccSubstations;
        }
}

CtiMessage* CtiCCSubstationsMsg::replicateMessage() const
{
    return new CtiCCSubstationsMsg(*this);
}

CtiCCSubstationsMsg& CtiCCSubstationsMsg::operator=(const CtiCCSubstationsMsg& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);
        _msgInfoBitMask = right.getMsgInfoBitMask();
        if( _ccSubstations != NULL &&
            _ccSubstations->size() > 0 )
        {
            delete_container(*_ccSubstations);
            _ccSubstations->clear();
            delete _ccSubstations;
        }
        if ( _ccSubstations == NULL )
            _ccSubstations = new CtiCCSubstation_vec;
        for(int i=0;i<(right.getCCSubstations())->size();i++)
        {
            _ccSubstations->push_back(((CtiCCSubstation*)(*right.getCCSubstations()).at(i))->replicate());
        }
    }

    return *this;
}
