/*---------------------------------------------------------------------------
        Filename:  lmprogramenergyexchange.cpp
        
        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiLMProgramEnergyExchange.
                        CtiLMProgramEnergyExchange maintains the state and handles
                        the persistence of programs for Load Management.

        Initial Date:  5/7/2001
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "loadmanager.h"
#include "lmprogramenergyexchange.h"
#include "lmenergyexchangecustomer.h"
#include "lmenergyexchangecustomerreply.h"
#include "msg_email.h"

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMProgramEnergyExchange, CTILMPROGRAMENERGYEXCHANGE_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMProgramEnergyExchange::CtiLMProgramEnergyExchange()
{   
}

CtiLMProgramEnergyExchange::CtiLMProgramEnergyExchange(RWDBReader& rdr)
{
    restore(rdr);   
}

CtiLMProgramEnergyExchange::CtiLMProgramEnergyExchange(const CtiLMProgramEnergyExchange& energyexchangeprog)
{
    operator=(energyexchangeprog);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMProgramEnergyExchange::~CtiLMProgramEnergyExchange()
{

    _lmenergyexchangeoffers.clearAndDestroy();
    _lmenergyexchangecustomers.clearAndDestroy();
}

/*---------------------------------------------------------------------------
    getMinNotifyTime
    
    Returns the minimum notify time in seconds of the energy exchange program
---------------------------------------------------------------------------*/
LONG CtiLMProgramEnergyExchange::getMinNotifyTime() const
{

    return _minnotifytime;
}

/*---------------------------------------------------------------------------
    getHeading
    
    Returns the heading of the energy exchange program
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramEnergyExchange::getHeading() const
{

    return _heading;
}

/*---------------------------------------------------------------------------
    getMessageHeader
    
    Returns the message header of the energy exchange program
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramEnergyExchange::getMessageHeader() const
{

    return _messageheader;
}

/*---------------------------------------------------------------------------
    getMessageFooter
    
    Returns the message footer of the energy exchange program
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramEnergyExchange::getMessageFooter() const
{

    return _messagefooter;
}

/*---------------------------------------------------------------------------
    getCanceledMsg

    Returns the canceled msg of the energy exchange program
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramEnergyExchange::getCanceledMsg() const
{

    return _canceledmsg;
}

/*---------------------------------------------------------------------------
    getStoppedEarlyMsg

    Returns the stopped early msg of the energy exchange program
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramEnergyExchange::getStoppedEarlyMsg() const
{

    return _stoppedearlymsg;
}

/*---------------------------------------------------------------------------
    getLMEnergyExchangeOffers
    
    Returns a list of offer revisions for this energy exchange program
---------------------------------------------------------------------------*/
RWOrdered& CtiLMProgramEnergyExchange::getLMEnergyExchangeOffers()
{

    return _lmenergyexchangeoffers;
}

/*---------------------------------------------------------------------------
    getLMEnergyExchangeCustomers
    
    Returns a list of customers for this energy exchange program
---------------------------------------------------------------------------*/
RWOrdered& CtiLMProgramEnergyExchange::getLMEnergyExchangeCustomers()
{

    return _lmenergyexchangecustomers;
}

/*---------------------------------------------------------------------------
    setMinNotifyTime
    
    Sets the minimum notify time of the energy exchange program
---------------------------------------------------------------------------*/    
CtiLMProgramEnergyExchange& CtiLMProgramEnergyExchange::setMinNotifyTime(LONG notifytime)
{

    _minnotifytime = notifytime;

    return *this;
}

/*---------------------------------------------------------------------------
    setHeading
    
    Sets the heading of the energy exchange program
---------------------------------------------------------------------------*/    
CtiLMProgramEnergyExchange& CtiLMProgramEnergyExchange::setHeading(const RWCString& head)
{

    _heading = head;

    return *this;
}

/*---------------------------------------------------------------------------
    setMessageHeader
    
    Sets the message header of the energy exchange program
---------------------------------------------------------------------------*/    
CtiLMProgramEnergyExchange& CtiLMProgramEnergyExchange::setMessageHeader(const RWCString& msgheader)
{

    _messageheader = msgheader;

    return *this;
}

/*---------------------------------------------------------------------------
    setMessageFooter
    
    Sets the message footer of the energy exchange program
---------------------------------------------------------------------------*/    
CtiLMProgramEnergyExchange& CtiLMProgramEnergyExchange::setMessageFooter(const RWCString& msgfooter)
{

    _messagefooter = msgfooter;

    return *this;
}

/*---------------------------------------------------------------------------
    setCanceledMsg
    
    Sets the canceled msg of the energy exchange program
---------------------------------------------------------------------------*/    
CtiLMProgramEnergyExchange& CtiLMProgramEnergyExchange::setCanceledMsg(const RWCString& canceled)
{

    _canceledmsg = canceled;

    return *this;
}

/*---------------------------------------------------------------------------
    setStoppedEarlyMsg
    
    Sets the stopped early msg of the energy exchange program
---------------------------------------------------------------------------*/    
CtiLMProgramEnergyExchange& CtiLMProgramEnergyExchange::setStoppedEarlyMsg(const RWCString& stoppedearly)
{

    _stoppedearlymsg = stoppedearly;

    return *this;
}


/*---------------------------------------------------------------------------
    reduceProgramLoad
    
    Sets the group selection method of the energy exchange program
---------------------------------------------------------------------------*/    
DOUBLE CtiLMProgramEnergyExchange::reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, RWOrdered controlAreaTriggers, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, BOOL isTriggerCheckNeeded)
{


    DOUBLE expectedLoadReduced = 0.0;

    return expectedLoadReduced;
}

/*---------------------------------------------------------------------------
    stopProgramControl

    Stops control on the program by sending all groups that are active.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramEnergyExchange::stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, ULONG secondsFrom1901)
{
    BOOL returnBool = TRUE;

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - stopProgramControl isn't implemented yet, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    
    return returnBool;
}

/*---------------------------------------------------------------------------
    handleManualControl

    Handles manual control messages for the energy exchange program.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramEnergyExchange::handleManualControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg)
{


    BOOL returnBoolean = FALSE;

    LONG numberOfCompletedOrCanceledOffers = 0;
    const RWDBDateTime currentDateTime;
    for(LONG i=0;i<_lmenergyexchangeoffers.entries();i++)
    {
        CtiLMEnergyExchangeOffer* currentOffer = (CtiLMEnergyExchangeOffer*)_lmenergyexchangeoffers[i];
        CtiLMEnergyExchangeOfferRevision* currentOfferRevision = currentOffer->getCurrentOfferRevision();

        if( currentOfferRevision != NULL )
        {
            if( !currentOffer->getRunStatus().compareTo(CtiLMEnergyExchangeOffer::ScheduledRunStatus,RWCString::ignoreCase) )
            {
                if( currentDateTime >= currentOfferRevision->getNotificationDateTime() )
                {
                    returnBoolean = TRUE;
                    notifyCustomers(currentOffer, multiDispatchMsg);
                    setProgramState(CtiLMProgramBase::ManualActiveState);
                    currentOffer->setRunStatus(CtiLMEnergyExchangeOffer::OpenRunStatus);
                    currentOffer->dumpDynamicData();

                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Energy Exchange notification sent to all customers in program: " << getPAOName() << " offer date: " << currentOffer->getOfferDate().rwtime() << endl;
                    }
                }
            }
            else if( !currentOffer->getRunStatus().compareTo(CtiLMEnergyExchangeOffer::OpenRunStatus,RWCString::ignoreCase) )
            {
                if( currentDateTime >= currentOfferRevision->getOfferExpirationDateTime() )
                {
                    returnBoolean = TRUE;
                    //setProgramState(CtiLMProgramBase::ManualActiveState);
                    currentOffer->setRunStatus(CtiLMEnergyExchangeOffer::CurtailmentPendingRunStatus);
                    currentOffer->dumpDynamicData();

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Energy Exchange offer expired, curtailment pending in program: " << getPAOName() << " offer date: " << currentOffer->getOfferDate().rwtime() << endl;
                    }
                }
            }
            else if( !currentOffer->getRunStatus().compareTo(CtiLMEnergyExchangeOffer::ClosingRunStatus,RWCString::ignoreCase) )
            {
                if( currentDateTime >= currentOfferRevision->getOfferExpirationDateTime() )
                {
                    returnBoolean = TRUE;
                    //setProgramState(CtiLMProgramBase::ManualActiveState);
                    currentOffer->setRunStatus(CtiLMEnergyExchangeOffer::CurtailmentPendingRunStatus);
                    currentOffer->dumpDynamicData();

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Energy Exchange offer closed, curtailment pending in program: " << getPAOName() << " offer date: " << currentOffer->getOfferDate().rwtime() << endl;
                    }
                }
            }
            else if( !currentOffer->getRunStatus().compareTo(CtiLMEnergyExchangeOffer::CurtailmentPendingRunStatus,RWCString::ignoreCase) )
            {
                if( currentDateTime >= RWDBDateTime(currentOffer->getOfferDate().year(),currentOffer->getOfferDate().month(),currentOffer->getOfferDate().dayOfMonth(),currentOfferRevision->getFirstCurtailHour(),0,0,0)  )
                {
                    returnBoolean = TRUE;
                    //setProgramState(CtiLMProgramBase::ManualActiveState);
                    currentOffer->setRunStatus(CtiLMEnergyExchangeOffer::CurtailmentActiveRunStatus);
                    currentOffer->dumpDynamicData();

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Energy Exchange curtailment period started in program: " << getPAOName() << " offer date: " << currentOffer->getOfferDate().rwtime() << endl;
                    }
                }
            }
            else if( !currentOffer->getRunStatus().compareTo(CtiLMEnergyExchangeOffer::CurtailmentActiveRunStatus,RWCString::ignoreCase) )
            {
                if( currentDateTime >= RWDBDateTime(currentOffer->getOfferDate().year(),currentOffer->getOfferDate().month(),currentOffer->getOfferDate().dayOfMonth(),currentOfferRevision->getLastCurtailHour(),0,0,0)  )
                {
                    returnBoolean = TRUE;
                    //setProgramState(CtiLMProgramBase::InactiveState);
                    currentOffer->setRunStatus(CtiLMEnergyExchangeOffer::CompletedRunStatus);
                    currentOffer->dumpDynamicData();

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Energy Exchange curtailment period completed in program: " << getPAOName() << " offer date: " << currentOffer->getOfferDate().rwtime() << endl;
                    }
                }
            }
            else if( !currentOffer->getRunStatus().compareTo(CtiLMEnergyExchangeOffer::CompletedRunStatus,RWCString::ignoreCase) ||
                     !currentOffer->getRunStatus().compareTo(CtiLMEnergyExchangeOffer::CanceledRunStatus,RWCString::ignoreCase) )
            {
                numberOfCompletedOrCanceledOffers++;
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Invalid manual run status: " << currentOffer->getRunStatus() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Current offer revision = NULL in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }

    if( numberOfCompletedOrCanceledOffers == _lmenergyexchangeoffers.entries() )
    {
        setManualControlReceivedFlag(FALSE);
        setProgramState(CtiLMProgramBase::InactiveState);
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    notifyCustomers

    .
---------------------------------------------------------------------------*/
void CtiLMProgramEnergyExchange::notifyCustomers(CtiLMEnergyExchangeOffer* offer, CtiMultiMsg* multiDispatchMsg)
{


    CtiLMEnergyExchangeOfferRevision* currentOfferRevision = offer->getCurrentOfferRevision();

    if( _lmenergyexchangecustomers.entries() > 0 )
    {
        CtiEmailMsg* emailMsg = NULL;
        for(LONG i=0;i<_lmenergyexchangecustomers.entries();i++)
        {
            CtiLMEnergyExchangeCustomer* currentCustomer = (CtiLMEnergyExchangeCustomer*)_lmenergyexchangecustomers[i];
            if( !currentCustomer->hasAcceptedOffer(offer->getOfferId()) )
            {
                RWOrdered& customerReplies = currentCustomer->getLMEnergyExchangeCustomerReplies();

                CtiLMEnergyExchangeCustomerReply* newCustomerReply = new CtiLMEnergyExchangeCustomerReply();

                newCustomerReply->setCustomerId(currentCustomer->getCustomerId());
                newCustomerReply->setOfferId(offer->getOfferId());
                newCustomerReply->setAcceptStatus(CtiLMEnergyExchangeCustomerReply::NoResponseAcceptStatus);
                newCustomerReply->setAcceptDateTime(gInvalidRWDBDateTime);
                newCustomerReply->setRevisionNumber(currentOfferRevision->getRevisionNumber());
                newCustomerReply->setIPAddressOfAcceptUser("(none)");
                newCustomerReply->setUserIdName("(none)");
                newCustomerReply->setNameOfAcceptPerson("(none)");
                newCustomerReply->setEnergyExchangeNotes("(none)");
                newCustomerReply->addLMEnergyExchangeCustomerReplyTable();
                customerReplies.insert(newCustomerReply);

                CtiEmailMsg* emailMsg = new CtiEmailMsg(currentCustomer->getCustomerId(),CtiEmailMsg::CICustomerEmailType);
                emailMsg->setSubject(getHeading());

                RWCString emailBody = getMessageHeader();
                emailBody += "\r\n\r\n";// 2 return lines
                emailBody += "Facility:  ";
                emailBody += currentCustomer->getCompanyName();
                emailBody += "\r\n\r\n";// 2 return lines
                emailBody += "Offer Date:  ";
                emailBody += offer->getOfferDate().rwdate().asString();
                emailBody += "\r\n\r\n";// 2 return lines
                char tempchar[64];
                emailBody += "Offer ID:  ";
                _ltoa(currentOfferRevision->getOfferId(),tempchar,10);
                emailBody += tempchar;
                emailBody += "-";
                _ltoa(currentOfferRevision->getRevisionNumber(),tempchar,10);
                emailBody += tempchar;
                emailBody += "\r\n\r\n";// 2 return lines
                emailBody += "Offer Expires:  ";
                emailBody += currentOfferRevision->getOfferExpirationDateTime().asString();
                emailBody += " ";
                emailBody += (currentOfferRevision->getOfferExpirationDateTime().rwtime().isDST() ? RWZone::local().altZoneName() : RWZone::local().timeZoneName() );
                emailBody += "\r\n\r\n";// 2 return lines

                emailBody += getMessageFooter();

                emailMsg->setText(emailBody);
                multiDispatchMsg->insert(emailMsg);
            }
        }
    }
}

/*---------------------------------------------------------------------------
    notifyCustomersOfCancel

    .
---------------------------------------------------------------------------*/
void CtiLMProgramEnergyExchange::notifyCustomersOfCancel(CtiLMEnergyExchangeOffer* offer, CtiMultiMsg* multiDispatchMsg)
{


    CtiLMEnergyExchangeOfferRevision* currentOfferRevision = offer->getCurrentOfferRevision();

    if( _lmenergyexchangecustomers.entries() > 0 )
    {
        CtiEmailMsg* emailMsg = NULL;
        for(LONG i=0;i<_lmenergyexchangecustomers.entries();i++)
        {
            CtiLMEnergyExchangeCustomer* currentCustomer = (CtiLMEnergyExchangeCustomer*)_lmenergyexchangecustomers[i];
            if( currentCustomer->hasAcceptedOffer(offer->getOfferId()) )
            {
                CtiEmailMsg* emailMsg = new CtiEmailMsg(currentCustomer->getCustomerId(),CtiEmailMsg::CICustomerEmailType);
                emailMsg->setSubject(getHeading());

                RWCString emailBody = getCanceledMsg();
                emailBody += "\r\n\r\n";// 2 return lines
                emailBody += getMessageHeader();
                emailBody += "\r\n\r\n";// 2 return lines
                emailBody += "Facility:  ";
                emailBody += currentCustomer->getCompanyName();
                emailBody += "\r\n\r\n";// 2 return lines
                emailBody += "Offer Date:  ";
                emailBody += offer->getOfferDate().rwdate().asString();
                emailBody += "\r\n\r\n";// 2 return lines
                char tempchar[64];
                emailBody += "Offer ID:  ";
                _ltoa(currentOfferRevision->getOfferId(),tempchar,10);
                emailBody += tempchar;
                emailBody += "-";
                _ltoa(currentOfferRevision->getRevisionNumber(),tempchar,10);
                emailBody += tempchar;
                emailBody += "\r\n\r\n";// 2 return lines
                emailBody += getMessageFooter();

                emailMsg->setText(emailBody);
                multiDispatchMsg->insert(emailMsg);
            }
        }
    }
}

/*---------------------------------------------------------------------------
    hasControlHoursAvailable

    Returns boolean if groups in this program are below the max hours
    daily/monthly/seasonal/annually.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramEnergyExchange::hasControlHoursAvailable() 
{
    BOOL returnBoolean = TRUE;
    return returnBoolean;
}

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMProgramEnergyExchange::restoreGuts(RWvistream& istrm)
{



    CtiLMProgramBase::restoreGuts( istrm );

    istrm >> _minnotifytime
          >> _heading
          >> _messageheader
          >> _messagefooter
          >> _canceledmsg
          >> _stoppedearlymsg
          >> _lmenergyexchangeoffers
          >> _lmenergyexchangecustomers;

}  
   
/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMProgramEnergyExchange::saveGuts(RWvostream& ostrm ) const  
{


        
    CtiLMProgramBase::saveGuts( ostrm );

    ostrm << _minnotifytime
          << _heading
          << _messageheader
          << _messagefooter
          << _canceledmsg
          << _stoppedearlymsg
          << _lmenergyexchangeoffers
          << _lmenergyexchangecustomers;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMProgramEnergyExchange& CtiLMProgramEnergyExchange::operator=(const CtiLMProgramEnergyExchange& right)
{


    if( this != &right )
    {
        CtiLMProgramBase::operator=(right);
        _minnotifytime = right._minnotifytime;
        _heading = right._heading;
        _messageheader = right._messageheader;
        _messagefooter = right._messagefooter;
        _canceledmsg = right._canceledmsg;
        _stoppedearlymsg = right._stoppedearlymsg;

        _lmenergyexchangeoffers.clearAndDestroy();
        for(LONG i=0;i<right._lmenergyexchangeoffers.entries();i++)
        {
            _lmenergyexchangeoffers.insert(((CtiLMEnergyExchangeOffer*)right._lmenergyexchangeoffers[i])->replicate());
        }

        _lmenergyexchangecustomers.clearAndDestroy();
        for(LONG j=0;j<right._lmenergyexchangecustomers.entries();j++)
        {
            _lmenergyexchangecustomers.insert(((CtiLMEnergyExchangeCustomer*)right._lmenergyexchangecustomers[j])->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMProgramEnergyExchange::operator==(const CtiLMProgramEnergyExchange& right) const
{

    return CtiLMProgramBase::operator==(right);
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMProgramEnergyExchange::operator!=(const CtiLMProgramEnergyExchange& right) const
{

    return CtiLMProgramBase::operator!=(right);
}

/*---------------------------------------------------------------------------
    isOfferForDate

    Returns a boolean if there is a offer for a given date
---------------------------------------------------------------------------*/
BOOL CtiLMProgramEnergyExchange::isOfferWithId(LONG offerid)
{
    BOOL returnBoolean = FALSE;

    if( _lmenergyexchangeoffers.entries() > 0 )
    {
        for(LONG i=0;i<_lmenergyexchangeoffers.entries();i++)
        {
            if( offerid == ((CtiLMEnergyExchangeOffer*)_lmenergyexchangeoffers[i])->getOfferId() )
            {
                returnBoolean = TRUE;
                break;
            }
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isOfferRevisionOpen

    Returns a boolean if there is a offer 
---------------------------------------------------------------------------*/
BOOL CtiLMProgramEnergyExchange::isOfferRevisionOpen(LONG offerID, LONG revisionNumber)
{
    BOOL returnBoolean = FALSE;

    RWDBDateTime currentDateTime;
    if( _lmenergyexchangeoffers.entries() > 0 )
    {
        for(LONG i=0;i<_lmenergyexchangeoffers.entries();i++)
        {
            CtiLMEnergyExchangeOffer* currentOffer = (CtiLMEnergyExchangeOffer*)_lmenergyexchangeoffers[i];
            if( currentOffer->getOfferId() == offerID  )
            {
                if( currentOffer->getLMEnergyExchangeOfferRevisions().entries() > 0 )
                {
                    RWOrdered& revisions = currentOffer->getLMEnergyExchangeOfferRevisions();
                    for(LONG j=0;j<revisions.entries();j++)
                    {
                        CtiLMEnergyExchangeOfferRevision* currentRevision = (CtiLMEnergyExchangeOfferRevision*)revisions[j];
                        if( currentRevision->getRevisionNumber() == revisionNumber )
                        {
                            if( currentDateTime >= currentRevision->getNotificationDateTime() &&
                                currentDateTime < currentRevision->getOfferExpirationDateTime() )
                            {
                                returnBoolean = TRUE;
                            }
                            break;
                        }
                    }
                }
                break;
            }
        }
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    getOfferWithId

    Returns a pointer to a offer for a given offer id
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOffer* CtiLMProgramEnergyExchange::getOfferWithId(LONG offerid)
{
    CtiLMEnergyExchangeOffer* returnOffer = NULL;

    if( _lmenergyexchangeoffers.entries() > 0 )
    {
        for(LONG i=0;i<_lmenergyexchangeoffers.entries();i++)
        {
            if( offerid == ((CtiLMEnergyExchangeOffer*)_lmenergyexchangeoffers[i])->getOfferId() )
            {
                returnOffer = (CtiLMEnergyExchangeOffer*)_lmenergyexchangeoffers[i];
                break;
            }
        }
    }

    return returnOffer;
}

/*---------------------------------------------------------------------------
    replicate
    
    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMProgramBase* CtiLMProgramEnergyExchange::replicate() const
{
    return (new CtiLMProgramEnergyExchange(*this));
}

/*---------------------------------------------------------------------------
    restore
    
    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMProgramEnergyExchange::restore(RWDBReader& rdr)
{


    CtiLMProgramBase::restore(rdr);

    rdr["minnotifytime"] >> _minnotifytime;
    rdr["heading"] >> _heading;
    rdr["messageheader"] >> _messageheader;
    rdr["messagefooter"] >> _messagefooter;
    rdr["canceledmsg"] >> _canceledmsg;
    rdr["stoppedearlymsg"] >> _stoppedearlymsg;
    /*setMinNotifyTime(0);
    setHeading("Null");
    setMessageHeader("Null");
    setMessageFooter("Null");
    setCanceledMsg("Null");
    setStoppedEarlyMsg("Null");*/
}

/*---------------------------------------------------------------------------
    restoreEnergyExchangeSpecificDatabaseEntries
    
    Restores the database entries for a energy exchange program that are not
    contained in the base table.
---------------------------------------------------------------------------*/
/*void CtiLMProgramEnergyExchange::restoreEnergyExchangeSpecificDatabaseEntries(RWDBReader& rdr)
{
    rdr["minnotifytime"] >> _minnotifytime;
    rdr["heading"] >> _heading;
    rdr["messageheader"] >> _messageheader;
    rdr["messagefooter"] >> _messagefooter;
    rdr["canceledmsg"] >> _canceledmsg;
    rdr["stoppedearlymsg"] >> _stoppedearlymsg;
}*/

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information.
---------------------------------------------------------------------------*/
void CtiLMProgramEnergyExchange::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,RWDBDateTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData
    
    Writes out the dynamic information for this energy exchange program.
---------------------------------------------------------------------------*/
void CtiLMProgramEnergyExchange::dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime)
{
    if( getManualControlReceivedFlag() )
    {
        for(LONG i=0;i<_lmenergyexchangeoffers.entries();i++)
        {
            ((CtiLMEnergyExchangeOffer*)_lmenergyexchangeoffers[i])->dumpDynamicData(conn, currentDateTime);
        }
    }
}

/*---------------------------------------------------------------------------
    restoreDynamicData
    
    Restores self's dynamic data given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMProgramEnergyExchange::restoreDynamicData(RWDBReader& rdr)
{


    if( getManualControlReceivedFlag() )
    {
    }
}

// Static Members

// Possible 

