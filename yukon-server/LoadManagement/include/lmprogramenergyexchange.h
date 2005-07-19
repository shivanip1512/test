/*---------------------------------------------------------------------------
        Filename:  lmprogramenergyexchange.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMProgramEnergyExchange

        Initial Date:  5/4/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMPROGRAMENERGYEXCHANGEIMPL_H
#define CTILMPROGRAMENERGYEXCHANGEIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "lmprogrambase.h"
#include "observe.h"
#include "lmenergyexchangeoffer.h"

class CtiLMProgramEnergyExchange : public CtiLMProgramBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMProgramEnergyExchange )

    CtiLMProgramEnergyExchange();
    CtiLMProgramEnergyExchange(RWDBReader& rdr);
    CtiLMProgramEnergyExchange(const CtiLMProgramEnergyExchange& energyexchangeprog);

    virtual ~CtiLMProgramEnergyExchange();

    LONG getMinNotifyTime() const;
    const RWCString& getHeading() const;
    const RWCString& getMessageHeader() const;
    const RWCString& getMessageFooter() const;
    const RWCString& getCanceledMsg() const;
    const RWCString& getStoppedEarlyMsg() const;
    RWOrdered& getLMEnergyExchangeOffers();
    RWOrdered& getLMEnergyExchangeCustomers();

    CtiLMProgramEnergyExchange& setMinNotifyTime(LONG notifytime);
    CtiLMProgramEnergyExchange& setHeading(const RWCString& head);
    CtiLMProgramEnergyExchange& setMessageHeader(const RWCString& msgheader);
    CtiLMProgramEnergyExchange& setMessageFooter(const RWCString& msgfooter);
    CtiLMProgramEnergyExchange& setCanceledMsg(const RWCString& canceled);
    CtiLMProgramEnergyExchange& setStoppedEarlyMsg(const RWCString& stoppedearly);

    BOOL isOfferWithId(LONG offerid);
    BOOL isOfferRevisionOpen(LONG offerID, LONG revisionNumber);
    CtiLMEnergyExchangeOffer* getOfferWithId(LONG offerid);
    //void restoreEnergyExchangeSpecificDatabaseEntries(RWDBReader& rdr);
    void notifyCustomers(CtiLMEnergyExchangeOffer* offer, CtiMultiMsg* multiNotifMsg);
    void notifyCustomersOfCancel(CtiLMEnergyExchangeOffer* offer, CtiMultiMsg* multiNotifMsg);
    void restoreDynamicData(RWDBReader& rdr);
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime);

    virtual CtiLMProgramBase* replicate() const;
    virtual DOUBLE reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, RWOrdered controlAreaTriggers, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL isTriggerCheckNeeded);
    virtual BOOL hasControlHoursAvailable();
    virtual BOOL stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, ULONG secondsFrom1901);
    virtual BOOL handleManualControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMProgramEnergyExchange& operator=(const CtiLMProgramEnergyExchange& right);

    int operator==(const CtiLMProgramEnergyExchange& right) const;
    int operator!=(const CtiLMProgramEnergyExchange& right) const;

    // Static Members

    // Possible 

private:

    LONG _minnotifytime;
    RWCString _heading;
    RWCString _messageheader;
    RWCString _messagefooter;
    RWCString _canceledmsg;
    RWCString _stoppedearlymsg;

    RWOrdered _lmenergyexchangeoffers;
    RWOrdered _lmenergyexchangecustomers;

    void restore(RWDBReader& rdr);
};
#endif

