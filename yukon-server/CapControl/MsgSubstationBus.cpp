#include "precompiled.h"

#include "MsgSubstationBus.h"
#include "ccid.h"

extern unsigned long _CC_DEBUG;

using std::endl;

DEFINE_COLLECTABLE( CtiCCSubstationBusMsg, CTICCSUBSTATIONBUS_MSG_ID )

CtiCCSubstationBusMsg::CtiCCSubstationBusMsg(CtiCCSubstationBus_vec& buses, unsigned long bitMask) : Inherited(), _ccSubstationBuses(NULL), _msgInfoBitMask(bitMask)
{
    _ccSubstationBuses = new CtiCCSubstationBus_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CTILOG_DEBUG(dout, "CtiCCSubstationBusMsg has "<< buses.size()<<" entries.");
    }
    if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
    {
        for (int h=0;h < buses.size(); h++)
        {
            CTILOG_DEBUG(dout, "Sub: "<<((CtiCCSubstationBus*)buses[h])->getPaoName()<<" "<<((CtiCCSubstationBus*)buses[h])->getCurrentVarLoadPointValue()<<" "<<((CtiCCSubstationBus*)buses[h])->getEstimatedVarLoadPointValue() <<" "<<
                    ((CtiCCSubstationBus*)buses[h])->getStrategy()->getPeakLead()<<" "<<((CtiCCSubstationBus*)buses[h])->getStrategy()->getPeakLag());

            CtiFeeder_vec& feeds =   ((CtiCCSubstationBus*)buses[h])->getCCFeeders();
            for (int hh = 0; hh < feeds.size(); hh++)
            {
                CTILOG_DEBUG(dout, "  Feed: "<<((CtiCCFeeder*)feeds[hh])->getPaoName()<<" "<<((CtiCCFeeder*)feeds[hh])->getCurrentVarLoadPointValue()<<" "<<((CtiCCFeeder*)feeds[hh])->getEstimatedVarLoadPointValue() <<" " <<
                        ((CtiCCFeeder*)feeds[hh])->getStrategy()->getPeakLead()<<" "<<((CtiCCFeeder*)feeds[hh])->getStrategy()->getPeakLag());

                CtiCCCapBank_SVector& caps =   ((CtiCCFeeder*)feeds[hh])->getCCCapBanks();
                for (int hhh = 0; hhh < caps.size(); hhh++)
                {
                    CTILOG_DEBUG(dout, "      Cap: "<<((CtiCCCapBank*)caps[hhh])->getPaoName() <<" "<<
                            ((CtiCCCapBank*)caps[hhh])->getControlStatusText());
                }

            }
        }
    }
    for(int i=0;i<buses.size();i++)
    {
        _ccSubstationBuses->push_back(((CtiCCSubstationBus*)buses.at(i))->replicate());
    }
}

CtiCCSubstationBusMsg::CtiCCSubstationBusMsg(CtiCCSubstationBus_set& buses, unsigned long bitMask) : Inherited(), _ccSubstationBuses(NULL), _msgInfoBitMask(bitMask)
{
    _ccSubstationBuses = new CtiCCSubstationBus_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CTILOG_DEBUG(dout, "CtiCCSubstationBusMsg has "<< buses.size()<<" entries.");
    }
    CtiCCSubstationBus_set::iterator it;
    for(it = buses.begin(); it != buses.end();it++)
    {
        _ccSubstationBuses->push_back(((CtiCCSubstationBus*)*it)->replicate());
    }
}

CtiCCSubstationBusMsg::CtiCCSubstationBusMsg(CtiCCSubstationBus* substationBus) : Inherited(), _ccSubstationBuses(NULL), _msgInfoBitMask(0)
{
    _ccSubstationBuses = new CtiCCSubstationBus_vec;
    _ccSubstationBuses->push_back(substationBus->replicate());
}


CtiCCSubstationBusMsg::CtiCCSubstationBusMsg(const CtiCCSubstationBusMsg& substationBusMsg) : Inherited(), _ccSubstationBuses(NULL), _msgInfoBitMask(0)
{
    operator=(substationBusMsg);
}

CtiCCSubstationBusMsg::~CtiCCSubstationBusMsg()
{
    if( _ccSubstationBuses != NULL &&
        _ccSubstationBuses->size() > 0 )
    {
        delete_container(*_ccSubstationBuses);
        _ccSubstationBuses->clear();
        delete _ccSubstationBuses;
    }
}

CtiMessage* CtiCCSubstationBusMsg::replicateMessage() const
{
    return new CtiCCSubstationBusMsg(*this);
}

CtiCCSubstationBusMsg& CtiCCSubstationBusMsg::operator=(const CtiCCSubstationBusMsg& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);

        _msgInfoBitMask = right.getMsgInfoBitMask();
        if( _ccSubstationBuses != NULL &&
            _ccSubstationBuses->size() > 0 )
        {
            delete_container(*_ccSubstationBuses);
            _ccSubstationBuses->clear();
            delete _ccSubstationBuses;
        }
        if ( _ccSubstationBuses == NULL )
            _ccSubstationBuses = new CtiCCSubstationBus_vec;
        for(int i=0;i<(right.getCCSubstationBuses())->size();i++)
        {
            _ccSubstationBuses->push_back(((CtiCCSubstationBus*)(*right.getCCSubstationBuses()).at(i))->replicate());
        }
    }

    return *this;
}

// Static Members
unsigned long CtiCCSubstationBusMsg::AllSubBusesSent = 0x00000001;
unsigned long CtiCCSubstationBusMsg::SubBusDeleted   = 0x00000002;
unsigned long CtiCCSubstationBusMsg::SubBusAdded     = 0x00000004;
unsigned long CtiCCSubstationBusMsg::SubBusModified  = 0x00000008;
