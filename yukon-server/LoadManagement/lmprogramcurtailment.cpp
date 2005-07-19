/*---------------------------------------------------------------------------
        Filename:  lmprogramcurtailment.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMProgramCurtailment.

        Initial Date:  3/24/2001

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
#include "lmprogramcurtailment.h"
#include "lmcurtailcustomer.h"
#include "msg_pcrequest.h"
#include "msg_notif_email.h"
#include "lmcontrolareatrigger.h"

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMProgramCurtailment, CTILMPROGRAMCURTAILMENT_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment::CtiLMProgramCurtailment()
{
}

CtiLMProgramCurtailment::CtiLMProgramCurtailment(RWDBReader& rdr)
{
    restore(rdr);
}

CtiLMProgramCurtailment::CtiLMProgramCurtailment(const CtiLMProgramCurtailment& curtailprog)
{
    operator=(curtailprog);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment::~CtiLMProgramCurtailment()
{

    _lmprogramcurtailmentcustomers.clearAndDestroy();
}

/*---------------------------------------------------------------------------
    getMinNotifyTime

    Returns the minimum notify time in seconds of the curtailment program
---------------------------------------------------------------------------*/
LONG CtiLMProgramCurtailment::getMinNotifyTime() const
{

    return _minnotifytime;
}

/*---------------------------------------------------------------------------
    getHeading

    Returns the heading of the curtailment program
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramCurtailment::getHeading() const
{

    return _heading;
}

/*---------------------------------------------------------------------------
    getMessageHeader

    Returns the message header of the curtailment program
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramCurtailment::getMessageHeader() const
{

    return _messageheader;
}

/*---------------------------------------------------------------------------
    getMessageFooter

    Returns the message footer of the curtailment program
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramCurtailment::getMessageFooter() const
{

    return _messagefooter;
}

/*---------------------------------------------------------------------------
    getAckTimeLimit

    Returns the acknowledgement time limit of the curtailment program
---------------------------------------------------------------------------*/
LONG CtiLMProgramCurtailment::getAckTimeLimit() const
{

    return _acktimelimit;
}

/*---------------------------------------------------------------------------
    getCanceledMsg

    Returns the canceled msg of the curtailment program
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramCurtailment::getCanceledMsg() const
{

    return _canceledmsg;
}

/*---------------------------------------------------------------------------
    getStoppedEarlyMsg

    Returns the stopped early msg of the curtailment program
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramCurtailment::getStoppedEarlyMsg() const
{

    return _stoppedearlymsg;
}

/*---------------------------------------------------------------------------
    getCurtailReferenceId

    Returns the reference of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
LONG CtiLMProgramCurtailment::getCurtailReferenceId() const
{

    return _curtailreferenceid;
}

/*---------------------------------------------------------------------------
    getActionDateTime

    Returns the action datetime of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMProgramCurtailment::getActionDateTime() const
{

    return _actiondatetime;
}

/*---------------------------------------------------------------------------
    getNotificationDateTime

    Returns the notification datetime of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMProgramCurtailment::getNotificationDateTime() const
{

    return _notificationdatetime;
}

/*---------------------------------------------------------------------------
    getCurtailmentStartTime

    Returns the start datetime of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMProgramCurtailment::getCurtailmentStartTime() const
{

    return _curtailmentstarttime;
}

/*---------------------------------------------------------------------------
    getCurtailmentStopTime

    Returns the stop time of the current control for the curtailment
    program.
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMProgramCurtailment::getCurtailmentStopTime() const
{

    return _curtailmentstoptime;
}

/*---------------------------------------------------------------------------
    getRunStatus

    Returns the run status of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramCurtailment::getRunStatus() const
{

    return _runstatus;
}

/*---------------------------------------------------------------------------
    getAdditionalInfo

    Returns the additional info of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
const RWCString& CtiLMProgramCurtailment::getAdditionalInfo() const
{

    return _additionalinfo;
}

/*---------------------------------------------------------------------------
    getLMProgramCurtailmentCustomers

    Returns a list of customers for this curtailment program
---------------------------------------------------------------------------*/
RWOrdered& CtiLMProgramCurtailment::getLMProgramCurtailmentCustomers()
{

    return _lmprogramcurtailmentcustomers;
}

/*---------------------------------------------------------------------------
    setMinNotifyTime

    Sets the minimum notify time of the curtailment program
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setMinNotifyTime(LONG notifytime)
{

    _minnotifytime = notifytime;

    return *this;
}

/*---------------------------------------------------------------------------
    setHeading

    Sets the heading of the curtailment program
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setHeading(const RWCString& head)
{

    _heading = head;

    return *this;
}

/*---------------------------------------------------------------------------
    setMessageHeader

    Sets the message header of the curtailment program
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setMessageHeader(const RWCString& msgheader)
{

    _messageheader = msgheader;

    return *this;
}

/*---------------------------------------------------------------------------
    setMessageFooter

    Sets the message footer of the curtailment program
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setMessageFooter(const RWCString& msgfooter)
{

    _messagefooter = msgfooter;

    return *this;
}

/*---------------------------------------------------------------------------
    setAckTimeLimit

    Sets the acknowledge time limit of the curtailment program
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setAckTimeLimit(LONG timelimit)
{

    _acktimelimit = timelimit;

    return *this;
}

/*---------------------------------------------------------------------------
    setCanceledMsg

    Sets the canceled msg of the curtailment program
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setCanceledMsg(const RWCString& canceled)
{

    _canceledmsg = canceled;

    return *this;
}

/*---------------------------------------------------------------------------
    setStoppedEarlyMsg

    Sets the stopped early msg of the curtailment program
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setStoppedEarlyMsg(const RWCString& stoppedearly)
{

    _stoppedearlymsg = stoppedearly;

    return *this;
}

/*---------------------------------------------------------------------------
    setCurtailReferenceId

    Sets the reference id of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setCurtailReferenceId(LONG refid)
{

    _curtailreferenceid = refid;

    return *this;
}

/*---------------------------------------------------------------------------
    setActionDateTime

    Sets the action datetime of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setActionDateTime(const RWDBDateTime& actiontime)
{

    _actiondatetime = actiontime;

    return *this;
}

/*---------------------------------------------------------------------------
    setNotificationDateTime

    Sets the notification datetime of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setNotificationDateTime(const RWDBDateTime& notifytime)
{

    _notificationdatetime = notifytime;

    return *this;
}

/*---------------------------------------------------------------------------
    setCurtailmentStartTime

    Sets the start datetime of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setCurtailmentStartTime(const RWDBDateTime& starttime)
{

    _curtailmentstarttime = starttime;

    return *this;
}

/*---------------------------------------------------------------------------
    setCurtailmentStopTime

    Sets the stop time of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setCurtailmentStopTime(const RWDBDateTime& stoptime)
{

    _curtailmentstoptime = stoptime;

    return *this;
}

/*---------------------------------------------------------------------------
    setRunStatus

    Sets the run status of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setRunStatus(const RWCString& runstat)
{

    _runstatus = runstat;

    return *this;
}

/*---------------------------------------------------------------------------
    setAdditionalInfo

    Sets the additional info of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setAdditionalInfo(const RWCString& additional)
{

    _additionalinfo = additional;

    return *this;
}


/*---------------------------------------------------------------------------
    reduceProgramLoad

    Sets the group selection method of the curtailment program
---------------------------------------------------------------------------*/
DOUBLE CtiLMProgramCurtailment::reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, RWOrdered controlAreaTriggers, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL isTriggerCheckNeeded)
{


    DOUBLE expectedLoadReduced = 0.0;

    return expectedLoadReduced;
}

/*---------------------------------------------------------------------------
    stopProgramControl

    Stops control on the program by sending all groups that are active.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramCurtailment::stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, ULONG secondsFrom1901)
{
    BOOL returnBool = TRUE;

    const RWDBDateTime currentDateTime;
    if( getProgramState() == CtiLMProgramBase::StoppingState )
    {
        if( currentDateTime >= getCurtailmentStopTime() )
        {
            setRunStatus(CtiLMProgramCurtailment::CompletedRunStatus);
            dumpDynamicData();
            setCurtailReferenceId(0);
            setActionDateTime(gInvalidRWDBDateTime);
            setNotificationDateTime(gInvalidRWDBDateTime);
            setCurtailmentStartTime(gInvalidRWDBDateTime);
            //setStartedControlling(gInvalidRWDBDateTime);
            setCurtailmentStopTime(gInvalidRWDBDateTime);
        }
        else if( currentDateTime >= getCurtailmentStartTime() && currentDateTime <= getCurtailmentStopTime() )
        {
            setRunStatus(CtiLMProgramCurtailment::StoppedEarlyRunStatus);
            setCurtailmentStopTime(RWDBDateTime());
            notifyCustomersOfStop(multiNotifMsg);
            dumpDynamicData();
            setCurtailReferenceId(0);
            setActionDateTime(gInvalidRWDBDateTime);
            setNotificationDateTime(gInvalidRWDBDateTime);
            setCurtailmentStartTime(gInvalidRWDBDateTime);
            //setStartedControlling(gInvalidRWDBDateTime);
            setCurtailmentStopTime(gInvalidRWDBDateTime);
            for(LONG i=0;i<_lmprogramcurtailmentcustomers.entries();i++)
            {
                ((CtiLMCurtailCustomer*)_lmprogramcurtailmentcustomers[i])->setAcknowledgeStatus(CtiLMCurtailCustomer::NotRequiredAckStatus);
            }
        }
        else if( currentDateTime >= getNotificationDateTime() && currentDateTime <= getCurtailmentStartTime() )
        {
            setRunStatus(CtiLMProgramCurtailment::CanceledRunStatus);
            notifyCustomersOfStop(multiNotifMsg);
            dumpDynamicData();
            setCurtailReferenceId(0);
            setActionDateTime(gInvalidRWDBDateTime);
            setNotificationDateTime(gInvalidRWDBDateTime);
            setCurtailmentStartTime(gInvalidRWDBDateTime);
            //setStartedControlling(gInvalidRWDBDateTime);
            setCurtailmentStopTime(gInvalidRWDBDateTime);
            for(LONG i=0;i<_lmprogramcurtailmentcustomers.entries();i++)
            {
                ((CtiLMCurtailCustomer*)_lmprogramcurtailmentcustomers[i])->setAcknowledgeStatus(CtiLMCurtailCustomer::NotRequiredAckStatus);
            }
        }
        else if( currentDateTime <= getNotificationDateTime() )
        {   //Not notified yet so we don't need to send out cancelation notifications
            //just delete the entry from LMCurtailProgramActivity table
            deleteLMCurtailProgramActivityTable();
        }
        setProgramState(CtiLMProgramBase::InactiveState);
    }
    else
    {
        returnBool = FALSE;
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Trying to stop curtailment on a program that isn't in the StoppingState state is: " << getProgramState() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return returnBool;
}

/*---------------------------------------------------------------------------
    handleManualControl

    Handles manual control messages for the curtailment program.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramCurtailment::handleManualControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    BOOL returnBoolean = FALSE;

    const RWDBDateTime currentDateTime;
    if( getProgramState() == CtiLMProgramBase::ScheduledState )
    {
        if( currentDateTime >= getNotificationDateTime() )
        {
            returnBoolean = TRUE;
            notifyCustomers(multiNotifMsg);
            setProgramState(CtiLMProgramBase::NotifiedState);
            setRunStatus(CtiLMProgramCurtailment::NotifiedRunStatus);
            dumpDynamicData();

            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Curtailment notification sent to all customers in program: " << getPAOName() << endl;
            }
        }
    }
    else if( getProgramState() == CtiLMProgramBase::NotifiedState )
    {
        if( currentDateTime >= getCurtailmentStartTime() )
        {
            returnBoolean = TRUE;
            setProgramState(CtiLMProgramBase::ManualActiveState);
            setRunStatus(CtiLMProgramCurtailment::ActiveRunStatus);
            dumpDynamicData();

            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Curtailment went active in program: " << getPAOName() << endl;
        }
    }
    else if( getProgramState() == CtiLMProgramBase::ManualActiveState )
    {
        if( currentDateTime >= getCurtailmentStopTime() )
        {
            returnBoolean = TRUE;
            setProgramState(CtiLMProgramBase::StoppingState);
            stopProgramControl(multiPilMsg,multiDispatchMsg, multiNotifMsg, secondsFrom1901);
            setManualControlReceivedFlag(FALSE);

            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Curtailment ended in program: " << getPAOName() << endl;
            }
        }
    }
    else if( getProgramState() == CtiLMProgramBase::StoppingState )
    {
        returnBoolean = TRUE;
        stopProgramControl(multiPilMsg,multiDispatchMsg, multiNotifMsg, secondsFrom1901);
        setManualControlReceivedFlag(FALSE);
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Invalid manual control state: " << getProgramState() << " in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    notifyCustomers

    .
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::notifyCustomers(CtiMultiMsg* multiNotificationMsg)
{
    if( _lmprogramcurtailmentcustomers.entries() > 0 )
    {
        for(LONG i=0;i<_lmprogramcurtailmentcustomers.entries();i++)
        {
            CtiLMCurtailCustomer* currentCustomer = (CtiLMCurtailCustomer*)_lmprogramcurtailmentcustomers[i];
            currentCustomer->setCurtailReferenceId(getCurtailReferenceId());
            if( currentCustomer->getRequireAck() )
            {
                currentCustomer->setAcknowledgeStatus(CtiLMCurtailCustomer::UnAcknowledgedAckStatus);
            }
            else
            {
                currentCustomer->setAcknowledgeStatus(CtiLMCurtailCustomer::NotRequiredAckStatus);
            }
            currentCustomer->addLMCurtailCustomerActivityTable();

            CtiCustomerNotifEmailMsg* emailMsg = new CtiCustomerNotifEmailMsg();
	    emailMsg->setCustomerId(currentCustomer->getCustomerId());
            emailMsg->setSubject(getHeading());

            RWCString emailBody = getMessageHeader();
            emailBody += "\r\n\r\n";// 2 return lines
            emailBody += "Facility:  ";
            emailBody += currentCustomer->getCompanyName();
            emailBody += "\r\n\r\n";// 2 return lines
            emailBody += "Scheduled Start:  ";
            emailBody += getCurtailmentStartTime().rwtime().asString();
            emailBody += " ";
            emailBody += (getCurtailmentStartTime().rwtime().isDST() ? RWZone::local().altZoneName() : RWZone::local().timeZoneName() );
            emailBody += "\r\n\r\n";// 2 return lines
            emailBody += "Scheduled Duration:  ";
            ULONG durationInSeconds = getCurtailmentStopTime().seconds() - getCurtailmentStartTime().seconds();
            char tempchar[64];
            if( (durationInSeconds/3600) > 0 )
            {
                _ultoa(durationInSeconds/3600,tempchar,10);
                emailBody += tempchar;
                emailBody += " hours ";
            }
            if( ((durationInSeconds%3600)/60) > 0 )
            {
                _ultoa((durationInSeconds%3600)/60,tempchar,10);
                emailBody += tempchar;
                emailBody += " minutes ";
            }
            if( ((durationInSeconds%3600)%60) > 0 )
            {
                _ultoa((durationInSeconds%3600)%60,tempchar,10);
                emailBody += tempchar;
                emailBody += " seconds ";
            }
            emailBody += "\r\n\r\n";// 2 return lines
            emailBody += getMessageFooter();

            emailMsg->setBody(emailBody);
            multiNotificationMsg->insert(emailMsg);
        }
    }
}

/*---------------------------------------------------------------------------
    notifyCustomersOfStop

    .
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::notifyCustomersOfStop(CtiMultiMsg* multiNotificationMsg)
{
    if( _lmprogramcurtailmentcustomers.entries() > 0 )
    {
        for(LONG i=0;i<_lmprogramcurtailmentcustomers.entries();i++)
        {
            CtiLMCurtailCustomer* currentCustomer = (CtiLMCurtailCustomer*)_lmprogramcurtailmentcustomers[i];
            currentCustomer->dumpDynamicData();

            CtiCustomerNotifEmailMsg* emailMsg = new CtiCustomerNotifEmailMsg();
	    emailMsg->setCustomerId(currentCustomer->getCustomerId());
            emailMsg->setSubject(getHeading());

            RWCString emailBody;
            if( !getRunStatus().compareTo(CtiLMProgramCurtailment::CanceledRunStatus,RWCString::ignoreCase) )
            {
                emailBody = getCanceledMsg();
            }
            else if( !getRunStatus().compareTo(CtiLMProgramCurtailment::StoppedEarlyRunStatus,RWCString::ignoreCase) )
            {
                emailBody = getStoppedEarlyMsg();
            }
            else
            {
                emailBody = "THIS CURTAILMENT HAS BEEN CANCELED";
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Could not determine type of stop notification to send, sent default.  Run status: " << getRunStatus() << " in: " << __FILE__ << " at: " << __LINE__ << endl;
            }
            emailBody += "\r\n\r\n";// 2 return lines
            emailBody += getMessageHeader();
            emailBody += "\r\n\r\n";// 2 return lines
            emailBody += "Facility:  ";
            emailBody += currentCustomer->getCompanyName();
            emailBody += "\r\n\r\n";// 2 return lines
            emailBody += "Scheduled Start:  ";
            emailBody += getCurtailmentStartTime().rwtime().asString();
            emailBody += " ";
            emailBody += (getCurtailmentStartTime().rwtime().isDST() ? RWZone::local().altZoneName() : RWZone::local().timeZoneName() );
            emailBody += "\r\n\r\n";// 2 return lines
            emailBody += "Scheduled Duration:  ";
            ULONG durationInSeconds = getCurtailmentStopTime().seconds() - getCurtailmentStartTime().seconds();
            char tempchar[64];
            if( (durationInSeconds/3600) > 0 )
            {
                _ultoa(durationInSeconds/3600,tempchar,10);
                emailBody += tempchar;
                emailBody += " hours ";
            }
            if( ((durationInSeconds%3600)/60) > 0 )
            {
                _ultoa((durationInSeconds%3600)/60,tempchar,10);
                emailBody += tempchar;
                emailBody += " minutes ";
            }
            if( ((durationInSeconds%3600)%60) > 0 )
            {
                _ultoa((durationInSeconds%3600)%60,tempchar,10);
                emailBody += tempchar;
                emailBody += " seconds ";
            }
            emailBody += "\r\n\r\n";// 2 return lines
            emailBody += getMessageFooter();

            emailMsg->setBody(emailBody);
            multiNotificationMsg->insert(emailMsg);
        }
    }
}

/*---------------------------------------------------------------------------
    addLMCurtailProgramActivity

    .
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::addLMCurtailProgramActivityTable()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        if( conn.isValid() )
        {
            RWDBDatabase db = getDatabase();
            RWDBTable lmCurtailProgramActivityTable = db.table("lmcurtailprogramactivity");

            {//need to get a new curtail reference id from the database
                RWDBSelector selector = db.selector();
                selector << lmCurtailProgramActivityTable["curtailreferenceid"];

                selector.from(lmCurtailProgramActivityTable);

                selector.orderByDescending(lmCurtailProgramActivityTable["curtailreferenceid"]);

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << selector.asString().data() << endl;
                }

                RWDBReader rdr = selector.reader(conn);

                if( rdr() )
                {
                    LONG tempLONG = 0;
                    rdr["curtailreferenceid"] >> tempLONG;
                    setCurtailReferenceId(tempLONG+1);
                }
                else
                {
                    setCurtailReferenceId(1);
                }

                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Inserting a program curtailment entry into LMCurtailProgramActivity: " << getPAOName() << " with curtail reference id: " << getCurtailReferenceId() << endl;
                }
            }

            {
                RWDBInserter inserter = lmCurtailProgramActivityTable.inserter();

                inserter << getPAOId()
                << getCurtailReferenceId()
                << getActionDateTime()
                << getNotificationDateTime()
                << getCurtailmentStartTime()
                << getCurtailmentStopTime()
                << getRunStatus()
                << getAdditionalInfo();

                if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << inserter.asString().data() << endl;
                }

                inserter.execute( conn );
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid DB Connection in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*---------------------------------------------------------------------------
    updateLMCurtailProgramActivity

    .
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::updateLMCurtailProgramActivityTable(RWDBConnection& conn, RWDBDateTime& currentDateTime)
{

    {
        if( conn.isValid() )
        {
            RWDBDatabase db = getDatabase();
            RWDBTable lmCurtailProgramActivityTable = db.table("lmcurtailprogramactivity");
            RWDBUpdater updater = lmCurtailProgramActivityTable.updater();

            updater << lmCurtailProgramActivityTable["curtailmentstoptime"].assign(getCurtailmentStopTime())
            << lmCurtailProgramActivityTable["runstatus"].assign(getRunStatus())
            << lmCurtailProgramActivityTable["additionalinfo"].assign(getAdditionalInfo());

            updater.where(lmCurtailProgramActivityTable["deviceid"]==getPAOId() &&//will be paobjectid
                          lmCurtailProgramActivityTable["curtailreferenceid"]==getCurtailReferenceId());

            if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << updater.asString().data() << endl;
            }

            updater.execute( conn );

            if( updater.status().errorCode() != RWDBStatus::ok )
            {// If update failed, we should try to insert the record because it means that there probably wasn't a entry for this object yet
                RWDBSelector selector = db.selector();
                selector << lmCurtailProgramActivityTable["curtailreferenceid"];

                selector.from(lmCurtailProgramActivityTable);

                selector.orderByDescending(lmCurtailProgramActivityTable["curtailreferenceid"]);

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << selector.asString().data() << endl;
                }

                RWDBReader rdr = selector.reader(conn);

                if( rdr() )
                {
                    LONG tempLONG = 0;
                    rdr["curtailreferenceid"] >> tempLONG;
                    setCurtailReferenceId(tempLONG+1);
                }
                else
                {
                    setCurtailReferenceId(1);
                }

                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Inserting a program curtailment entry into LMCurtailProgramActivity: " << getPAOName() << " with curtail reference id: " << getCurtailReferenceId() << endl;
                }

                RWDBInserter inserter = lmCurtailProgramActivityTable.inserter();

                inserter << getPAOId()
                << getCurtailReferenceId()
                << getActionDateTime()
                << getNotificationDateTime()
                << getCurtailmentStartTime()
                << getCurtailmentStopTime()
                << getRunStatus()
                << getAdditionalInfo();

                if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << inserter.asString().data() << endl;
                }

                inserter.execute( conn );
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid DB Connection in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*---------------------------------------------------------------------------
    deleteLMCurtailProgramActivity

    .
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::deleteLMCurtailProgramActivityTable()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        if( conn.isValid() )
        {
            RWDBDatabase db = getDatabase();
            RWDBTable lmCurtailProgramActivityTable = db.table("lmcurtailprogramactivity");
            RWDBDeleter deleter = lmCurtailProgramActivityTable.deleter();

            deleter.where(lmCurtailProgramActivityTable["deviceid"]==getPAOId() &&//will be paobjectid
                          lmCurtailProgramActivityTable["curtailreferenceid"]==getCurtailReferenceId());

            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << deleter.asString().data() << endl;
            }*/

            deleter.execute( conn );
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid DB Connection in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*---------------------------------------------------------------------------
    hasControlHoursAvailable

    Returns boolean if groups in this program are below the max hours
    daily/monthly/seasonal/annually.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramCurtailment::hasControlHoursAvailable() 
{
    BOOL returnBoolean = TRUE;
    return returnBoolean;
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::restoreGuts(RWvistream& istrm)
{
    CtiLMProgramBase::restoreGuts( istrm );

    RWTime tempTime1;
    RWTime tempTime2;
    RWTime tempTime3;
    RWTime tempTime4;

    istrm >> _minnotifytime
    >> _heading
    >> _messageheader
    >> _messagefooter
    >> _acktimelimit
    >> _canceledmsg
    >> _stoppedearlymsg
    >> _curtailreferenceid
    >> tempTime1
    >> tempTime2
    >> tempTime3
    >> tempTime4
    >> _runstatus
    >> _additionalinfo
    >> _lmprogramcurtailmentcustomers;

    _actiondatetime = RWDBDateTime(tempTime1);
    _notificationdatetime = RWDBDateTime(tempTime2);
    _curtailmentstarttime = RWDBDateTime(tempTime3);
    _curtailmentstoptime = RWDBDateTime(tempTime4);
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::saveGuts(RWvostream& ostrm ) const
{



    CtiLMProgramBase::saveGuts( ostrm );

    ostrm << _minnotifytime
    << _heading
    << _messageheader
    << _messagefooter
    << _acktimelimit
    << _canceledmsg
    << _stoppedearlymsg
    << _curtailreferenceid
    << _actiondatetime.rwtime()
    << _notificationdatetime.rwtime()
    << _curtailmentstarttime.rwtime()
    << _curtailmentstoptime.rwtime()
    << _runstatus
    << _additionalinfo
    << _lmprogramcurtailmentcustomers;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::operator=(const CtiLMProgramCurtailment& right)
{


    if( this != &right )
    {
        CtiLMProgramBase::operator=(right);
        _minnotifytime = right._minnotifytime;
        _heading = right._heading;
        _messageheader = right._messageheader;
        _messagefooter = right._messagefooter;
        _acktimelimit = right._acktimelimit;
        _canceledmsg = right._canceledmsg;
        _stoppedearlymsg = right._stoppedearlymsg;
        _curtailreferenceid = right._curtailreferenceid;
        _actiondatetime = right._actiondatetime;
        _notificationdatetime = right._notificationdatetime;
        _curtailmentstarttime = right._curtailmentstarttime;
        _curtailmentstoptime = right._curtailmentstoptime;
        _runstatus = right._runstatus;
        _additionalinfo = right._additionalinfo;

        _lmprogramcurtailmentcustomers.clearAndDestroy();
        for(LONG i=0;i<right._lmprogramcurtailmentcustomers.entries();i++)
        {
            _lmprogramcurtailmentcustomers.insert(((CtiLMCurtailCustomer*)right._lmprogramcurtailmentcustomers[i])->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMProgramCurtailment::operator==(const CtiLMProgramCurtailment& right) const
{

    return CtiLMProgramBase::operator==(right);
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMProgramCurtailment::operator!=(const CtiLMProgramCurtailment& right) const
{

    return CtiLMProgramBase::operator!=(right);
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMProgramBase* CtiLMProgramCurtailment::replicate() const
{
    return(new CtiLMProgramCurtailment(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::restore(RWDBReader& rdr)
{
    CtiLMProgramBase::restore(rdr);

    rdr["minnotifytime"] >> _minnotifytime;
    rdr["heading"] >> _heading;
    rdr["messageheader"] >> _messageheader;
    rdr["messagefooter"] >> _messagefooter;
    rdr["acktimelimit"] >> _acktimelimit;
    rdr["canceledmsg"] >> _canceledmsg;
    rdr["stoppedearlymsg"] >> _stoppedearlymsg;
    setCurtailReferenceId(0);
    setActionDateTime(gInvalidRWDBDateTime);
    setNotificationDateTime(gInvalidRWDBDateTime);
    setCurtailmentStartTime(gInvalidRWDBDateTime);
    setCurtailmentStopTime(gInvalidRWDBDateTime);
    setRunStatus(CtiLMProgramCurtailment::NullRunStatus);
    setAdditionalInfo("null");
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information.
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,RWDBDateTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this curtailment program.
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime)
{
    if( getManualControlReceivedFlag() )
    {
        updateLMCurtailProgramActivityTable(conn, currentDateTime);
    }
}

/*---------------------------------------------------------------------------
    restoreDynamicData

    Restores self's dynamic data given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::restoreDynamicData(RWDBReader& rdr)
{


    if( getManualControlReceivedFlag() )
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();
        {

            if( conn.isValid() )
            {
                RWDBDatabase db = getDatabase();
                RWDBTable lmCurtailProgramActivityTable = db.table("lmcurtailprogramactivity");
                RWDBSelector selector = db.selector();
                selector << lmCurtailProgramActivityTable["curtailreferenceid"]
                << lmCurtailProgramActivityTable["actiondatetime"]
                << lmCurtailProgramActivityTable["notificationdatetime"]
                << lmCurtailProgramActivityTable["curtailmentstarttime"]
                << lmCurtailProgramActivityTable["curtailmentstoptime"]
                << lmCurtailProgramActivityTable["runstatus"]
                << lmCurtailProgramActivityTable["additionalinfo"];

                selector.from(lmCurtailProgramActivityTable);

                selector.where(lmCurtailProgramActivityTable["deviceid"]==getPAOId());//will be paobjectid

                selector.orderByDescending(lmCurtailProgramActivityTable["actiondatetime"]);

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << selector.asString().data() << endl;
                }

                RWDBReader rdr = selector.reader(conn);

                if(rdr())
                {
                    rdr["curtailreferenceid"] >> _curtailreferenceid;
                    rdr["actiondatetime"] >> _actiondatetime;
                    rdr["notificationdatetime"] >> _notificationdatetime;
                    rdr["curtailmentstarttime"] >> _curtailmentstarttime;
                    rdr["curtailmentstoptime"] >> _curtailmentstoptime;
                    rdr["runstatus"] >> _runstatus;
                    rdr["additionalinfo"] >> _additionalinfo;
                }

            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Invalid DB Connection in: " << __FILE__ << " at: " << __LINE__ << endl;
            }
        }
    }
}

// Static Members

// Possible run statuses
const RWCString CtiLMProgramCurtailment::NullRunStatus = "Null";
const RWCString CtiLMProgramCurtailment::ScheduledRunStatus = "Scheduled";
const RWCString CtiLMProgramCurtailment::NotifiedRunStatus = "Notified";
const RWCString CtiLMProgramCurtailment::CanceledRunStatus = "Canceled";
const RWCString CtiLMProgramCurtailment::ActiveRunStatus = "Active";
const RWCString CtiLMProgramCurtailment::StoppedEarlyRunStatus = "StoppedEarly";
const RWCString CtiLMProgramCurtailment::CompletedRunStatus = "Completed";

