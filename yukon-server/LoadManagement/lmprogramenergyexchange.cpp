/*---------------------------------------------------------------------------
        Filename:  lmprogramenergyexchange.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMProgramEnergyExchange.
                        CtiLMProgramEnergyExchange maintains the state and handles
                        the persistence of programs for Load Management.

        Initial Date:  5/7/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

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
#include "msg_notif_email.h"
#include "ctidate.h"
#include "utility.h"

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
    delete_container(_lmenergyexchangeoffers);
    _lmenergyexchangeoffers.clear();
    delete_container(_lmenergyexchangecustomers);
    _lmenergyexchangecustomers.clear();
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
const string& CtiLMProgramEnergyExchange::getHeading() const
{

    return _heading;
}

/*---------------------------------------------------------------------------
    getMessageHeader

    Returns the message header of the energy exchange program
---------------------------------------------------------------------------*/
const string& CtiLMProgramEnergyExchange::getMessageHeader() const
{

    return _messageheader;
}

/*---------------------------------------------------------------------------
    getMessageFooter

    Returns the message footer of the energy exchange program
---------------------------------------------------------------------------*/
const string& CtiLMProgramEnergyExchange::getMessageFooter() const
{

    return _messagefooter;
}

/*---------------------------------------------------------------------------
    getCanceledMsg

    Returns the canceled msg of the energy exchange program
---------------------------------------------------------------------------*/
const string& CtiLMProgramEnergyExchange::getCanceledMsg() const
{

    return _canceledmsg;
}

/*---------------------------------------------------------------------------
    getStoppedEarlyMsg

    Returns the stopped early msg of the energy exchange program
---------------------------------------------------------------------------*/
const string& CtiLMProgramEnergyExchange::getStoppedEarlyMsg() const
{

    return _stoppedearlymsg;
}

/*---------------------------------------------------------------------------
    getLMEnergyExchangeOffers

    Returns a list of offer revisions for this energy exchange program
---------------------------------------------------------------------------*/
std::vector<CtiLMEnergyExchangeOffer*>& CtiLMProgramEnergyExchange::getLMEnergyExchangeOffers()
{

    return _lmenergyexchangeoffers;
}

/*---------------------------------------------------------------------------
    getLMEnergyExchangeCustomers

    Returns a list of customers for this energy exchange program
---------------------------------------------------------------------------*/
std::vector<CtiLMEnergyExchangeCustomer*>& CtiLMProgramEnergyExchange::getLMEnergyExchangeCustomers()
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
CtiLMProgramEnergyExchange& CtiLMProgramEnergyExchange::setHeading(const string& head)
{

    _heading = head;

    return *this;
}

/*---------------------------------------------------------------------------
    setMessageHeader

    Sets the message header of the energy exchange program
---------------------------------------------------------------------------*/
CtiLMProgramEnergyExchange& CtiLMProgramEnergyExchange::setMessageHeader(const string& msgheader)
{

    _messageheader = msgheader;

    return *this;
}

/*---------------------------------------------------------------------------
    setMessageFooter

    Sets the message footer of the energy exchange program
---------------------------------------------------------------------------*/
CtiLMProgramEnergyExchange& CtiLMProgramEnergyExchange::setMessageFooter(const string& msgfooter)
{

    _messagefooter = msgfooter;

    return *this;
}

/*---------------------------------------------------------------------------
    setCanceledMsg

    Sets the canceled msg of the energy exchange program
---------------------------------------------------------------------------*/
CtiLMProgramEnergyExchange& CtiLMProgramEnergyExchange::setCanceledMsg(const string& canceled)
{

    _canceledmsg = canceled;

    return *this;
}

/*---------------------------------------------------------------------------
    setStoppedEarlyMsg

    Sets the stopped early msg of the energy exchange program
---------------------------------------------------------------------------*/
CtiLMProgramEnergyExchange& CtiLMProgramEnergyExchange::setStoppedEarlyMsg(const string& stoppedearly)
{

    _stoppedearlymsg = stoppedearly;

    return *this;
}


/*---------------------------------------------------------------------------
    reduceProgramLoad

    Sets the group selection method of the energy exchange program
---------------------------------------------------------------------------*/
DOUBLE CtiLMProgramEnergyExchange::reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, vector<CtiLMControlAreaTrigger*> controlAreaTriggers, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL isTriggerCheckNeeded)
{


    DOUBLE expectedLoadReduced = 0.0;

    return expectedLoadReduced;
}

/*---------------------------------------------------------------------------
    stopProgramControl

    Stops control on the program by sending all groups that are active.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramEnergyExchange::stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, ULONG secondsFrom1901)
{
    BOOL returnBool = TRUE;

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - stopProgramControl isn't implemented yet, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return returnBool;
}

/*---------------------------------------------------------------------------
    handleManualControl

    Handles manual control messages for the energy exchange program.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramEnergyExchange::handleManualControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{


    BOOL returnBoolean = FALSE;

    LONG numberOfCompletedOrCanceledOffers = 0;
    const CtiTime currentDateTime;
    for(LONG i=0;i<_lmenergyexchangeoffers.size();i++)
    {
        CtiLMEnergyExchangeOffer* currentOffer = (CtiLMEnergyExchangeOffer*)_lmenergyexchangeoffers[i];
        CtiLMEnergyExchangeOfferRevision* currentOfferRevision = currentOffer->getCurrentOfferRevision();

        if( currentOfferRevision != NULL )
        {
            if( !stringCompareIgnoreCase(currentOffer->getRunStatus(),CtiLMEnergyExchangeOffer::ScheduledRunStatus ) )
            {
                if( currentDateTime >= currentOfferRevision->getNotificationDateTime() )
                {
                    returnBoolean = TRUE;
                    notifyCustomers(currentOffer, multiNotifMsg);
                    setProgramState(CtiLMProgramBase::ManualActiveState);
                    currentOffer->setRunStatus(CtiLMEnergyExchangeOffer::OpenRunStatus);
                    currentOffer->dumpDynamicData();

                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Energy Exchange notification sent to all customers in program: " << getPAOName() << " offer date: " << currentOffer->getOfferDate() << endl;
                    }
                }
            }
            else if( !stringCompareIgnoreCase(currentOffer->getRunStatus(),CtiLMEnergyExchangeOffer::OpenRunStatus ) )
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
                        dout << CtiTime() << " - Energy Exchange offer expired, curtailment pending in program: " << getPAOName() << " offer date: " << currentOffer->getOfferDate() << endl;
                    }
                }
            }
            else if( !stringCompareIgnoreCase(currentOffer->getRunStatus(),CtiLMEnergyExchangeOffer::ClosingRunStatus ) )
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
                        dout << CtiTime() << " - Energy Exchange offer closed, curtailment pending in program: " << getPAOName() << " offer date: " << currentOffer->getOfferDate() << endl;
                    }
                }
            }
            else if( !stringCompareIgnoreCase(currentOffer->getRunStatus(),CtiLMEnergyExchangeOffer::CurtailmentPendingRunStatus ) )
            {
                if( currentDateTime >= CtiTime(currentOffer->getOfferDate().date(),currentOfferRevision->getFirstCurtailHour(),0,0)  )
                {
                    returnBoolean = TRUE;
                    //setProgramState(CtiLMProgramBase::ManualActiveState);
                    currentOffer->setRunStatus(CtiLMEnergyExchangeOffer::CurtailmentActiveRunStatus);
                    currentOffer->dumpDynamicData();

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Energy Exchange curtailment period started in program: " << getPAOName() << " offer date: " << currentOffer->getOfferDate() << endl;
                    }
                }
            }
            else if( !stringCompareIgnoreCase(currentOffer->getRunStatus(),CtiLMEnergyExchangeOffer::CurtailmentActiveRunStatus ) )
            {
                if( currentDateTime >= CtiTime(currentOffer->getOfferDate().date(),currentOfferRevision->getLastCurtailHour(),0,0)  )
                {
                    returnBoolean = TRUE;
                    //setProgramState(CtiLMProgramBase::InactiveState);
                    currentOffer->setRunStatus(CtiLMEnergyExchangeOffer::CompletedRunStatus);
                    currentOffer->dumpDynamicData();

                    if( _LM_DEBUG & LM_DEBUG_STANDARD )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Energy Exchange curtailment period completed in program: " << getPAOName() << " offer date: " << currentOffer->getOfferDate() << endl;
                    }
                }
            }
            else if( !stringCompareIgnoreCase(currentOffer->getRunStatus(),CtiLMEnergyExchangeOffer::CompletedRunStatus ) ||
                     !stringCompareIgnoreCase(currentOffer->getRunStatus(),CtiLMEnergyExchangeOffer::CanceledRunStatus ) )
            {
                numberOfCompletedOrCanceledOffers++;
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Invalid manual run status: " << currentOffer->getRunStatus() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Current offer revision = NULL in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }

    if( numberOfCompletedOrCanceledOffers == _lmenergyexchangeoffers.size() )
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
void CtiLMProgramEnergyExchange::notifyCustomers(CtiLMEnergyExchangeOffer* offer, CtiMultiMsg* multiNotifMsg)
{
    CtiLMEnergyExchangeOfferRevision* currentOfferRevision = offer->getCurrentOfferRevision();

    for(LONG i=0;i<_lmenergyexchangecustomers.size();i++)
    {
        CtiLMEnergyExchangeCustomer* currentCustomer = (CtiLMEnergyExchangeCustomer*)_lmenergyexchangecustomers[i];
        if( !currentCustomer->hasAcceptedOffer(offer->getOfferId()) )
        {
            vector<CtiLMEnergyExchangeCustomerReply*>& customerReplies = currentCustomer->getLMEnergyExchangeCustomerReplies();

            CtiLMEnergyExchangeCustomerReply* newCustomerReply = CTIDBG_new CtiLMEnergyExchangeCustomerReply();

            newCustomerReply->setCustomerId(currentCustomer->getCustomerId());
            newCustomerReply->setOfferId(offer->getOfferId());
            newCustomerReply->setAcceptStatus(CtiLMEnergyExchangeCustomerReply::NoResponseAcceptStatus);
            newCustomerReply->setAcceptDateTime(gInvalidCtiTime);
            newCustomerReply->setRevisionNumber(currentOfferRevision->getRevisionNumber());
            newCustomerReply->setIPAddressOfAcceptUser("(none)");
            newCustomerReply->setUserIdName("(none)");
            newCustomerReply->setNameOfAcceptPerson("(none)");
            newCustomerReply->setEnergyExchangeNotes("(none)");
            newCustomerReply->addLMEnergyExchangeCustomerReplyTable();
            customerReplies.push_back(newCustomerReply);

            CtiCustomerNotifEmailMsg* emailMsg = CTIDBG_new CtiCustomerNotifEmailMsg();
    emailMsg->setCustomerId(currentCustomer->getCustomerId());
            emailMsg->setSubject(getHeading());

            string emailBody = getMessageHeader();
            emailBody += "\r\n\r\n";// 2 return lines
            emailBody += "Facility:  ";
            emailBody += currentCustomer->getCompanyName();
            emailBody += "\r\n\r\n";// 2 return lines
            emailBody += "Offer Date:  ";
            emailBody += offer->getOfferDate().date().asString();
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
            emailBody += (currentOfferRevision->getOfferExpirationDateTime().isDST() ? RWZone::local().altZoneName() : RWZone::local().timeZoneName() );
            emailBody += "\r\n\r\n";// 2 return lines

            emailBody += getMessageFooter();

            emailMsg->setBody(emailBody);
            multiNotifMsg->insert(emailMsg);
        }
    }

}

/*---------------------------------------------------------------------------
    notifyCustomersOfCancel

    .
---------------------------------------------------------------------------*/
void CtiLMProgramEnergyExchange::notifyCustomersOfCancel(CtiLMEnergyExchangeOffer* offer, CtiMultiMsg* multiNotifMsg)
{
    CtiLMEnergyExchangeOfferRevision* currentOfferRevision = offer->getCurrentOfferRevision();

    for(LONG i=0;i<_lmenergyexchangecustomers.size();i++)
    {
        CtiLMEnergyExchangeCustomer* currentCustomer = (CtiLMEnergyExchangeCustomer*)_lmenergyexchangecustomers[i];
        if( currentCustomer->hasAcceptedOffer(offer->getOfferId()) )
        {
            CtiCustomerNotifEmailMsg* emailMsg = CTIDBG_new CtiCustomerNotifEmailMsg();
    emailMsg->setCustomerId(currentCustomer->getCustomerId());
            emailMsg->setSubject(getHeading());

            string emailBody = getCanceledMsg();
            emailBody += "\r\n\r\n";// 2 return lines
            emailBody += getMessageHeader();
            emailBody += "\r\n\r\n";// 2 return lines
            emailBody += "Facility:  ";
            emailBody += currentCustomer->getCompanyName();
            emailBody += "\r\n\r\n";// 2 return lines
            emailBody += "Offer Date:  ";
            emailBody += offer->getOfferDate().date().asString();
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

            emailMsg->setBody(emailBody);
            multiNotifMsg->insert(emailMsg);
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

        delete_container(_lmenergyexchangeoffers);
        _lmenergyexchangeoffers.clear();
        for(LONG i=0;i<right._lmenergyexchangeoffers.size();i++)
        {
            _lmenergyexchangeoffers.push_back(((CtiLMEnergyExchangeOffer*)right._lmenergyexchangeoffers[i])->replicate());
        }

        delete_container(_lmenergyexchangecustomers);
        _lmenergyexchangecustomers.clear();
        for(LONG j=0;j<right._lmenergyexchangecustomers.size();j++)
        {
            _lmenergyexchangecustomers.push_back(((CtiLMEnergyExchangeCustomer*)right._lmenergyexchangecustomers[j])->replicate());
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

    if( _lmenergyexchangeoffers.size() > 0 )
    {
        for(LONG i=0;i<_lmenergyexchangeoffers.size();i++)
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

    CtiTime currentDateTime;

    for(LONG i=0;i<_lmenergyexchangeoffers.size();i++)
    {
        CtiLMEnergyExchangeOffer* currentOffer = (CtiLMEnergyExchangeOffer*)_lmenergyexchangeoffers[i];
        if( currentOffer->getOfferId() == offerID  )
        {
            if( currentOffer->getLMEnergyExchangeOfferRevisions().size() > 0 )
            {
                vector<CtiLMEnergyExchangeOfferRevision*>& revisions = currentOffer->getLMEnergyExchangeOfferRevisions();
                for(LONG j=0;j<revisions.size();j++)
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


    return returnBoolean;
}

/*---------------------------------------------------------------------------
    getOfferWithId

    Returns a pointer to a offer for a given offer id
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOffer* CtiLMProgramEnergyExchange::getOfferWithId(LONG offerid)
{
    CtiLMEnergyExchangeOffer* returnOffer = NULL;

    if( _lmenergyexchangeoffers.size() > 0 )
    {
        for(LONG i=0;i<_lmenergyexchangeoffers.size();i++)
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
CtiLMProgramBaseSPtr CtiLMProgramEnergyExchange::replicate() const
{
    return (CTIDBG_new CtiLMProgramEnergyExchange(*this));
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

    dumpDynamicData(conn,CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this energy exchange program.
---------------------------------------------------------------------------*/
void CtiLMProgramEnergyExchange::dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime)
{
    if( getManualControlReceivedFlag() )
    {
        for(LONG i=0;i<_lmenergyexchangeoffers.size();i++)
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

