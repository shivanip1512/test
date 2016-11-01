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

CtiCCSubstationsMsg::CtiCCSubstationsMsg(const CtiCCSubstation_vec& ccSubstations, unsigned long bitMask) : Inherited(), _substations(NULL), _msgInfoBitMask(bitMask)
{
    _substations = new CtiCCSubstation_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CTILOG_DEBUG(dout, "CtiCCSubstationsMsg has "<< ccSubstations.size()<<" entries.");
    }
    if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
    {
        for( const auto station : ccSubstations )
        {
            Cti::FormattedList list;

            list.add("Substation") << station->getPaoName();
            list.add("Parent")     << station->getParentId();
            list.add("SAEnabled")  << station->getSaEnabledFlag();
            list.add("SAEnId")     << station->getSaEnabledId();

            for( auto busId : station->getCCSubIds() )
            {
                list.add("SubBus") << busId;
            }

            CTILOG_DEBUG(dout, list);
        }
    }

    for( const auto station : ccSubstations )
    {
        _substations->push_back(station->replicate());
    }
}

CtiCCSubstationsMsg::CtiCCSubstationsMsg(CtiCCSubstation_set& ccSubstations, unsigned long bitMask) : Inherited(), _substations(NULL), _msgInfoBitMask(bitMask)
{
    _substations = new CtiCCSubstation_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CTILOG_DEBUG(dout, "CtiCCSubstationsMsg has "<< ccSubstations.size()<<" entries.");
    }
    for( const auto station : ccSubstations )
    {
        _substations->push_back(station->replicate());
    }
}


CtiCCSubstationsMsg::CtiCCSubstationsMsg(const CtiCCSubstationsMsg& right)
{
    Inherited::operator=(right);
    _msgInfoBitMask = right.getMsgInfoBitMask();
    _substations = new CtiCCSubstation_vec;
    for( const auto sub : *right.getCCSubstations() )
    {
        _substations->push_back(sub->replicate());
    }
}

CtiCCSubstationsMsg::~CtiCCSubstationsMsg()
{
    if( _substations != NULL )
    {
        delete_container(*_substations);

        delete _substations;
    }
}

CtiMessage* CtiCCSubstationsMsg::replicateMessage() const
{
    return new CtiCCSubstationsMsg(*this);
}
