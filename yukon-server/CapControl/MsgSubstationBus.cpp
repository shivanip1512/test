#include "precompiled.h"

#include "MsgSubstationBus.h"
#include "ccid.h"

extern unsigned long _CC_DEBUG;

using std::endl;

RWDEFINE_COLLECTABLE( CtiCCSubstationBusMsg, CTICCSUBSTATIONBUS_MSG_ID )

CtiCCSubstationBusMsg::CtiCCSubstationBusMsg(CtiCCSubstationBus_vec& buses, unsigned long bitMask) : Inherited(), _ccSubstationBuses(NULL), _msgInfoBitMask(bitMask)
{
    _ccSubstationBuses = new CtiCCSubstationBus_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CtiCCSubstationBusMsg has "<< buses.size()<<" entries." << endl;
    }
    if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
    {
        for (int h=0;h < buses.size(); h++)
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Sub: "<<((CtiCCSubstationBus*)buses[h])->getPaoName()<<" "<<((CtiCCSubstationBus*)buses[h])->getCurrentVarLoadPointValue()<<" "<<((CtiCCSubstationBus*)buses[h])->getEstimatedVarLoadPointValue() <<" "<<
                    ((CtiCCSubstationBus*)buses[h])->getStrategy()->getPeakLead()<<" "<<((CtiCCSubstationBus*)buses[h])->getStrategy()->getPeakLag()<< endl;
            }
            CtiFeeder_vec& feeds =   ((CtiCCSubstationBus*)buses[h])->getCCFeeders();
            for (int hh = 0; hh < feeds.size(); hh++)
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " -    Feed: "<<((CtiCCFeeder*)feeds[hh])->getPaoName()<<" "<<((CtiCCFeeder*)feeds[hh])->getCurrentVarLoadPointValue()<<" "<<((CtiCCFeeder*)feeds[hh])->getEstimatedVarLoadPointValue() <<" " <<
                        ((CtiCCFeeder*)feeds[hh])->getStrategy()->getPeakLead()<<" "<<((CtiCCFeeder*)feeds[hh])->getStrategy()->getPeakLag()<< endl;
                }

                CtiCCCapBank_SVector& caps =   ((CtiCCFeeder*)feeds[hh])->getCCCapBanks();
                for (int hhh = 0; hhh < caps.size(); hhh++)
                {
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " -        Cap: "<<((CtiCCCapBank*)caps[hhh])->getPaoName() <<" "<<
                            ((CtiCCCapBank*)caps[hhh])->getControlStatusText()<< endl;
                    }
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CtiCCSubstationBusMsg has "<< buses.size()<<" entries." << endl;
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

void CtiCCSubstationBusMsg::restoreGuts(RWvistream& strm)
{
    Inherited::restoreGuts(strm);
    strm >> _msgInfoBitMask
         >> _ccSubstationBuses;
}

/*---------------------------------------------------------------------------
    saveGuts

    Saves the state of self into the given RWvostream
---------------------------------------------------------------------------*/
void CtiCCSubstationBusMsg::saveGuts(RWvostream& strm) const
{
    Inherited::saveGuts(strm);
    strm << _msgInfoBitMask
         << _ccSubstationBuses;
}

// Static Members
unsigned long CtiCCSubstationBusMsg::AllSubBusesSent = 0x00000001;
unsigned long CtiCCSubstationBusMsg::SubBusDeleted   = 0x00000002;
unsigned long CtiCCSubstationBusMsg::SubBusAdded     = 0x00000004;
unsigned long CtiCCSubstationBusMsg::SubBusModified  = 0x00000008;
