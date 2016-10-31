#include "precompiled.h"

#include "MsgSubstationBus.h"
#include "ccid.h"

extern unsigned long _CC_DEBUG;

using std::endl;

DEFINE_COLLECTABLE( CtiCCSubstationBusMsg, CTICCSUBSTATIONBUS_MSG_ID )

CtiCCSubstationBusMsg::CtiCCSubstationBusMsg(CtiCCSubstationBus_vec& buses, unsigned long bitMask) : Inherited(), _substationBuses(NULL), _msgInfoBitMask(bitMask)
{
    _substationBuses = new CtiCCSubstationBus_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CTILOG_DEBUG(dout, "CtiCCSubstationBusMsg has "<< buses.size()<<" entries.");
    }
    if( _CC_DEBUG & CC_DEBUG_RIDICULOUS )
    {
        for( const auto bus : buses )
        {
            CTILOG_DEBUG(dout, "Sub: "<<bus->getPaoName()<<" "<<bus->getCurrentVarLoadPointValue()<<" "<<bus->getEstimatedVarLoadPointValue() <<" "<<
                    bus->getStrategy()->getPeakLead()<<" "<<bus->getStrategy()->getPeakLag());

            for( const auto feeder : bus->getCCFeeders() )
            {
                CTILOG_DEBUG(dout, "  Feed: "<<feeder->getPaoName()<<" "<<feeder->getCurrentVarLoadPointValue()<<" "<<feeder->getEstimatedVarLoadPointValue() <<" " <<
                        feeder->getStrategy()->getPeakLead()<<" "<<feeder->getStrategy()->getPeakLag());

                for ( const auto cap : feeder->getCCCapBanks() )
                {
                    CTILOG_DEBUG(dout, "      Cap: "<<cap->getPaoName() <<" "<<cap->getControlStatusText());
                }

            }
        }
    }
    for( const auto bus : buses )
    {
        _substationBuses->push_back(bus->replicate());
    }
}

CtiCCSubstationBusMsg::CtiCCSubstationBusMsg(CtiCCSubstationBus_set& buses, unsigned long bitMask) : Inherited(), _substationBuses(NULL), _msgInfoBitMask(bitMask)
{
    _substationBuses = new CtiCCSubstationBus_vec;
    if( _CC_DEBUG & CC_DEBUG_PERFORMANCE )
    {
        CTILOG_DEBUG(dout, "CtiCCSubstationBusMsg has "<< buses.size()<<" entries.");
    }
    for( const auto bus : buses )
    {
        _substationBuses->push_back(bus->replicate());
    }
}

CtiCCSubstationBusMsg::CtiCCSubstationBusMsg(CtiCCSubstationBus* substationBus) : Inherited(), _substationBuses(NULL), _msgInfoBitMask(0)
{
    _substationBuses = new CtiCCSubstationBus_vec;
    _substationBuses->push_back(substationBus->replicate());
}


CtiCCSubstationBusMsg::CtiCCSubstationBusMsg(const CtiCCSubstationBusMsg& right)
{
    Inherited::operator=(right);

    _msgInfoBitMask = right.getMsgInfoBitMask();
    _substationBuses = new CtiCCSubstationBus_vec;
    for( const auto bus : *right.getCCSubstationBuses() )
    {
        _substationBuses->push_back(bus->replicate());
    }
}

CtiCCSubstationBusMsg::~CtiCCSubstationBusMsg()
{
    if( _substationBuses != NULL )
    {
        delete_container(*_substationBuses);

        delete _substationBuses;
    }
}

CtiMessage* CtiCCSubstationBusMsg::replicateMessage() const
{
    return new CtiCCSubstationBusMsg(*this);
}

// Static Members
unsigned long CtiCCSubstationBusMsg::AllSubBusesSent = 0x00000001;
unsigned long CtiCCSubstationBusMsg::SubBusDeleted   = 0x00000002;
unsigned long CtiCCSubstationBusMsg::SubBusAdded     = 0x00000004;
unsigned long CtiCCSubstationBusMsg::SubBusModified  = 0x00000008;
