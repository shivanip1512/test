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
#include "lmenergyexchangecustomer.h"

class CtiLMProgramEnergyExchange : public CtiLMProgramBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMProgramEnergyExchange )

    CtiLMProgramEnergyExchange();
    CtiLMProgramEnergyExchange(RWDBReader& rdr);
    CtiLMProgramEnergyExchange(const CtiLMProgramEnergyExchange& energyexchangeprog);

    virtual ~CtiLMProgramEnergyExchange();

    LONG getMinNotifyTime() const;
    const string& getHeading() const;
    const string& getMessageHeader() const;
    const string& getMessageFooter() const;
    const string& getCanceledMsg() const;
    const string& getStoppedEarlyMsg() const;
    std::vector<CtiLMEnergyExchangeOffer*>& getLMEnergyExchangeOffers();
    std::vector<CtiLMEnergyExchangeCustomer*>& getLMEnergyExchangeCustomers();

    CtiLMProgramEnergyExchange& setMinNotifyTime(LONG notifytime);
    CtiLMProgramEnergyExchange& setHeading(const string& head);
    CtiLMProgramEnergyExchange& setMessageHeader(const string& msgheader);
    CtiLMProgramEnergyExchange& setMessageFooter(const string& msgfooter);
    CtiLMProgramEnergyExchange& setCanceledMsg(const string& canceled);
    CtiLMProgramEnergyExchange& setStoppedEarlyMsg(const string& stoppedearly);

    BOOL isOfferWithId(LONG offerid);
    BOOL isOfferRevisionOpen(LONG offerID, LONG revisionNumber);
    CtiLMEnergyExchangeOffer* getOfferWithId(LONG offerid);
    //void restoreEnergyExchangeSpecificDatabaseEntries(RWDBReader& rdr);
    void notifyCustomers(CtiLMEnergyExchangeOffer* offer, CtiMultiMsg* multiNotifMsg);
    void notifyCustomersOfCancel(CtiLMEnergyExchangeOffer* offer, CtiMultiMsg* multiNotifMsg);
    void restoreDynamicData(RWDBReader& rdr);
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);

    virtual CtiLMProgramBaseSPtr replicate() const;
    virtual DOUBLE reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, vector<CtiLMControlAreaTrigger*> controlAreaTriggers, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL isTriggerCheckNeeded);
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
    string _heading;
    string _messageheader;
    string _messagefooter;
    string _canceledmsg;
    string _stoppedearlymsg;

    std::vector<CtiLMEnergyExchangeOffer*>     _lmenergyexchangeoffers;
    std::vector<CtiLMEnergyExchangeCustomer*>  _lmenergyexchangecustomers;
                                               
    void restore(RWDBReader& rdr);
};

#if VSLICK_TAG_WORKAROUND
typedef CtiLMProgramEnergyExchange * CtiLMProgramEnergyExchangeSPtr;
#else
typedef shared_ptr< CtiLMProgramEnergyExchange > CtiLMProgramEnergyExchangeSPtr;
#endif

#endif

