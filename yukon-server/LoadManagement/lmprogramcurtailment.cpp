#include "precompiled.h"

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
#include "utility.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"
#include "database_util.h"

using std::string;
using std::endl;
using std::vector;

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( CtiLMProgramCurtailment, CTILMPROGRAMCURTAILMENT_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment::CtiLMProgramCurtailment() :
_minnotifytime(0),
_acktimelimit(0),
_curtailreferenceid(0)
{
}

CtiLMProgramCurtailment::CtiLMProgramCurtailment(Cti::RowReader &rdr)
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
    delete_container(_lmprogramcurtailmentcustomers);
    _lmprogramcurtailmentcustomers.clear();
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
const string& CtiLMProgramCurtailment::getHeading() const
{

    return _heading;
}

/*---------------------------------------------------------------------------
    getMessageHeader

    Returns the message header of the curtailment program
---------------------------------------------------------------------------*/
const string& CtiLMProgramCurtailment::getMessageHeader() const
{

    return _messageheader;
}

/*---------------------------------------------------------------------------
    getMessageFooter

    Returns the message footer of the curtailment program
---------------------------------------------------------------------------*/
const string& CtiLMProgramCurtailment::getMessageFooter() const
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
const string& CtiLMProgramCurtailment::getCanceledMsg() const
{

    return _canceledmsg;
}

/*---------------------------------------------------------------------------
    getStoppedEarlyMsg

    Returns the stopped early msg of the curtailment program
---------------------------------------------------------------------------*/
const string& CtiLMProgramCurtailment::getStoppedEarlyMsg() const
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
const CtiTime& CtiLMProgramCurtailment::getActionDateTime() const
{

    return _actiondatetime;
}

/*---------------------------------------------------------------------------
    getNotificationDateTime

    Returns the notification datetime of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
const CtiTime& CtiLMProgramCurtailment::getNotificationDateTime() const
{

    return _notificationdatetime;
}

/*---------------------------------------------------------------------------
    getCurtailmentStartTime

    Returns the start datetime of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
const CtiTime& CtiLMProgramCurtailment::getCurtailmentStartTime() const
{

    return _curtailmentstarttime;
}

/*---------------------------------------------------------------------------
    getCurtailmentStopTime

    Returns the stop time of the current control for the curtailment
    program.
---------------------------------------------------------------------------*/
const CtiTime& CtiLMProgramCurtailment::getCurtailmentStopTime() const
{

    return _curtailmentstoptime;
}

/*---------------------------------------------------------------------------
    getRunStatus

    Returns the run status of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
const string& CtiLMProgramCurtailment::getRunStatus() const
{

    return _runstatus;
}

/*---------------------------------------------------------------------------
    getAdditionalInfo

    Returns the additional info of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
const string& CtiLMProgramCurtailment::getAdditionalInfo() const
{

    return _additionalinfo;
}

/*---------------------------------------------------------------------------
    getLMProgramCurtailmentCustomers

    Returns a list of customers for this curtailment program
---------------------------------------------------------------------------*/
vector<CtiLMCurtailCustomer*>& CtiLMProgramCurtailment::getLMProgramCurtailmentCustomers()
{

    return _lmprogramcurtailmentcustomers;
}

const vector<CtiLMCurtailCustomer*>& CtiLMProgramCurtailment::getLMProgramCurtailmentCustomers() const
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
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setHeading(const string& head)
{

    _heading = head;

    return *this;
}

/*---------------------------------------------------------------------------
    setMessageHeader

    Sets the message header of the curtailment program
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setMessageHeader(const string& msgheader)
{

    _messageheader = msgheader;

    return *this;
}

/*---------------------------------------------------------------------------
    setMessageFooter

    Sets the message footer of the curtailment program
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setMessageFooter(const string& msgfooter)
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
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setCanceledMsg(const string& canceled)
{

    _canceledmsg = canceled;

    return *this;
}

/*---------------------------------------------------------------------------
    setStoppedEarlyMsg

    Sets the stopped early msg of the curtailment program
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setStoppedEarlyMsg(const string& stoppedearly)
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
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setActionDateTime(const CtiTime& actiontime)
{

    _actiondatetime = actiontime;

    return *this;
}

/*---------------------------------------------------------------------------
    setNotificationDateTime

    Sets the notification datetime of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setNotificationDateTime(const CtiTime& notifytime)
{

    _notificationdatetime = notifytime;

    return *this;
}

/*---------------------------------------------------------------------------
    setCurtailmentStartTime

    Sets the start datetime of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setCurtailmentStartTime(const CtiTime& starttime)
{

    _curtailmentstarttime = starttime;

    return *this;
}

/*---------------------------------------------------------------------------
    setCurtailmentStopTime

    Sets the stop time of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setCurtailmentStopTime(const CtiTime& stoptime)
{

    _curtailmentstoptime = stoptime;

    return *this;
}

/*---------------------------------------------------------------------------
    setRunStatus

    Sets the run status of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setRunStatus(const string& runstat)
{

    _runstatus = runstat;

    return *this;
}

/*---------------------------------------------------------------------------
    setAdditionalInfo

    Sets the additional info of the current control for the
    curtailment program.
---------------------------------------------------------------------------*/
CtiLMProgramCurtailment& CtiLMProgramCurtailment::setAdditionalInfo(const string& additional)
{

    _additionalinfo = additional;

    return *this;
}


/*---------------------------------------------------------------------------
    reduceProgramLoad

    Sets the group selection method of the curtailment program
---------------------------------------------------------------------------*/
DOUBLE CtiLMProgramCurtailment::reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, vector<CtiLMControlAreaTrigger*> controlAreaTriggers, LONG secondsFromBeginningOfDay, CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL isTriggerCheckNeeded)
{


    DOUBLE expectedLoadReduced = 0.0;

    return expectedLoadReduced;
}

/*---------------------------------------------------------------------------
    stopProgramControl

    Stops control on the program by sending all groups that are active.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramCurtailment::stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, CtiTime currentTime)
{
    BOOL returnBool = TRUE;

    const CtiTime currentDateTime;
    if( getProgramState() == CtiLMProgramBase::StoppingState )
    {
        if( currentDateTime >= getCurtailmentStopTime() )
        {
            setRunStatus(CtiLMProgramCurtailment::CompletedRunStatus);
            dumpDynamicData();
            setCurtailReferenceId(0);
            setActionDateTime(gInvalidCtiTime);
            setNotificationDateTime(gInvalidCtiTime);
            setCurtailmentStartTime(gInvalidCtiTime);
            //setStartedControlling(gInvalidCtiTime);
            setCurtailmentStopTime(gInvalidCtiTime);
        }
        else if( currentDateTime >= getCurtailmentStartTime() && currentDateTime <= getCurtailmentStopTime() )
        {
            setRunStatus(CtiLMProgramCurtailment::StoppedEarlyRunStatus);
            setCurtailmentStopTime(CtiTime());
            notifyCustomersOfStop(multiNotifMsg);
            dumpDynamicData();
            setCurtailReferenceId(0);
            setActionDateTime(gInvalidCtiTime);
            setNotificationDateTime(gInvalidCtiTime);
            setCurtailmentStartTime(gInvalidCtiTime);
            //setStartedControlling(gInvalidCtiTime);
            setCurtailmentStopTime(gInvalidCtiTime);
            for(LONG i=0;i<_lmprogramcurtailmentcustomers.size();i++)
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
            setActionDateTime(gInvalidCtiTime);
            setNotificationDateTime(gInvalidCtiTime);
            setCurtailmentStartTime(gInvalidCtiTime);
            //setStartedControlling(gInvalidCtiTime);
            setCurtailmentStopTime(gInvalidCtiTime);
            for(LONG i=0;i<_lmprogramcurtailmentcustomers.size();i++)
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
        CTILOG_INFO(dout, "Trying to stop curtailment on a program that isn't in the StoppingState state is: " << getProgramState());
    }

    return returnBool;
}

/*---------------------------------------------------------------------------
    handleManualControl

    Handles manual control messages for the curtailment program.
---------------------------------------------------------------------------*/
BOOL CtiLMProgramCurtailment::handleManualControl(CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg)
{
    BOOL returnBoolean = FALSE;

    const CtiTime currentDateTime;
    if( getProgramState() == CtiLMProgramBase::ScheduledState )
    {
        if( currentDateTime >= getNotificationDateTime() )
        {
            returnBoolean = TRUE;
            notifyCustomers(multiNotifMsg);
            setProgramState(CtiLMProgramBase::NotifiedState);
            setRunStatus(CtiLMProgramCurtailment::NotifiedRunStatus);
            dumpDynamicData();

            CTILOG_INFO(dout, "Curtailment notification sent to all customers in program: " << getPAOName());
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

            CTILOG_INFO(dout, "Curtailment went active in program: " << getPAOName());
        }
    }
    else if( getProgramState() == CtiLMProgramBase::ManualActiveState )
    {
        if( currentDateTime >= getCurtailmentStopTime() )
        {
            returnBoolean = TRUE;
            setProgramState(CtiLMProgramBase::StoppingState);
            stopProgramControl(multiPilMsg,multiDispatchMsg, multiNotifMsg, currentTime);
            setManualControlReceivedFlag(FALSE);

            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, "Curtailment ended in program: " << getPAOName());
            }
        }
    }
    else if( getProgramState() == CtiLMProgramBase::StoppingState )
    {
        returnBoolean = TRUE;
        stopProgramControl(multiPilMsg,multiDispatchMsg, multiNotifMsg, currentTime);
        setManualControlReceivedFlag(FALSE);
    }
    else
    {
        CTILOG_INFO(dout, "Invalid manual control state: " << getProgramState());
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    notifyCustomers

    .
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::notifyCustomers(CtiMultiMsg* multiNotificationMsg)
{
    for(LONG i=0;i<_lmprogramcurtailmentcustomers.size();i++)
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

        CtiCustomerNotifEmailMsg* emailMsg = CTIDBG_new CtiCustomerNotifEmailMsg();
    emailMsg->setCustomerId(currentCustomer->getCustomerId());
        emailMsg->setSubject(getHeading());

        string emailBody = getMessageHeader();
        emailBody += "\r\n\r\n";// 2 return lines
        emailBody += "Facility:  ";
        emailBody += currentCustomer->getCompanyName();
        emailBody += "\r\n\r\n";// 2 return lines
        emailBody += "Scheduled Start:  ";
        emailBody += getCurtailmentStartTime().asString(CtiTime::Local, CtiTime::IncludeTimezone);
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

/*---------------------------------------------------------------------------
    notifyCustomersOfStop

    .
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::notifyCustomersOfStop(CtiMultiMsg* multiNotificationMsg)
{
    for(LONG i=0;i<_lmprogramcurtailmentcustomers.size();i++)
    {
        CtiLMCurtailCustomer* currentCustomer = (CtiLMCurtailCustomer*)_lmprogramcurtailmentcustomers[i];
        currentCustomer->dumpDynamicData();

        CtiCustomerNotifEmailMsg* emailMsg = CTIDBG_new CtiCustomerNotifEmailMsg();
    emailMsg->setCustomerId(currentCustomer->getCustomerId());
        emailMsg->setSubject(getHeading());

        string emailBody;
        if( ciStringEqual(getRunStatus(), CtiLMProgramCurtailment::CanceledRunStatus) )
        {
            emailBody = getCanceledMsg();
        }
        else if( ciStringEqual(getRunStatus(), CtiLMProgramCurtailment::StoppedEarlyRunStatus) )
        {
            emailBody = getStoppedEarlyMsg();
        }
        else
        {
            emailBody = "THIS CURTAILMENT HAS BEEN CANCELED";
            CTILOG_INFO(dout, "Could not determine type of stop notification to send, sent default.  Run status: " << getRunStatus());
        }
        emailBody += "\r\n\r\n";// 2 return lines
        emailBody += getMessageHeader();
        emailBody += "\r\n\r\n";// 2 return lines
        emailBody += "Facility:  ";
        emailBody += currentCustomer->getCompanyName();
        emailBody += "\r\n\r\n";// 2 return lines
        emailBody += "Scheduled Start:  ";
        emailBody += getCurtailmentStartTime().asString(CtiTime::Local, CtiTime::IncludeTimezone);
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

/*---------------------------------------------------------------------------
    addLMCurtailProgramActivity

    .
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::addLMCurtailProgramActivityTable()
{
    //need to get a new curtail reference id from the database
    static const string sql =  "SELECT CPA.curtailreferenceid "
                               "FROM lmcurtailprogramactivity CPA "
                               "ORDER BY CPA.curtailreferenceid DESC";

    Cti::Database::DatabaseConnection conn;
    Cti::Database::DatabaseReader rdr(conn);

    rdr.setCommandText(sql);
    rdr.execute();

    if( _LM_DEBUG & LM_DEBUG_DATABASE )
    {
        CTILOG_DEBUG(dout, rdr.asString());
    }

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

    CTILOG_INFO(dout, "Inserting a program curtailment entry into LMCurtailProgramActivity: " << getPAOName() << " with curtail reference id: " << getCurtailReferenceId());

    {
        static const std::string sql_insert = "insert into lmcurtailprogramactivity values (?, ?, ?, ?, ?, ?, ?, ?)";

        Cti::Database::DatabaseConnection   conn;
        Cti::Database::DatabaseWriter       inserter(conn, sql_insert);

        inserter
            << getPAOId()
            << getCurtailReferenceId()
            << getActionDateTime()
            << getNotificationDateTime()
            << getCurtailmentStartTime()
            << getCurtailmentStopTime()
            << getRunStatus()
            << getAdditionalInfo();

        if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
        {
            CTILOG_DEBUG(dout, inserter.asString());
        }

        inserter.execute();
    }
}

/*---------------------------------------------------------------------------
    updateLMCurtailProgramActivity

    .
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::updateLMCurtailProgramActivityTable(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    static const std::string sql_update = "update lmcurtailprogramactivity"
                                           " set "
                                                "curtailmentstoptime = ?, "
                                                "runstatus = ?, "
                                                "additionalinfo = ?"
                                           " where "
                                                "deviceid = ? and "
                                                "curtailreferenceid = ?";

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseWriter       updater(connection, sql_update);

    updater
        << getCurtailmentStopTime()
        << getRunStatus()[0]
        << getAdditionalInfo()[0]
        << getPAOId()
        << getCurtailReferenceId();

    if( ! Cti::Database::executeUpdater( updater, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) ))
    {
        // If update failed, we should try to insert the record because it means that there probably wasn't a entry for this object yet

        static const string sql =  "SELECT CPA.curtailreferenceid "
                                   "FROM lmcurtailprogramactivity CPA "
                                   "ORDER BY CPA.curtailreferenceid DESC";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection);

        rdr.setCommandText(sql);
        rdr.execute();

        if( _LM_DEBUG & LM_DEBUG_DATABASE )
        {
            CTILOG_DEBUG(dout, rdr.asString());
        }

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

        CTILOG_INFO(dout, "Inserting a program curtailment entry into LMCurtailProgramActivity: " << getPAOName() << " with curtail reference id: " << getCurtailReferenceId());

        {
            static const std::string sql_insert = "insert into lmcurtailprogramactivity values (?, ?, ?, ?, ?, ?, ?, ?)";

            Cti::Database::DatabaseConnection   conn;
            Cti::Database::DatabaseWriter       inserter(conn, sql_insert);

            inserter
                << getPAOId()
                << getCurtailReferenceId()
                << getActionDateTime()
                << getNotificationDateTime()
                << getCurtailmentStartTime()
                << getCurtailmentStopTime()
                << getRunStatus()
                << getAdditionalInfo();

            Cti::Database::executeCommand( inserter, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) );
        }
    }
}

/*---------------------------------------------------------------------------
    deleteLMCurtailProgramActivity

    .
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::deleteLMCurtailProgramActivityTable()
{
    static const std::string sql = "delete from lmcurtailprogramactivity where deviceid = ? and curtailreferenceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter
        << getPAOId()
        << getCurtailReferenceId();

    deleter.execute();
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

        delete_container(_lmprogramcurtailmentcustomers);
        _lmprogramcurtailmentcustomers.clear();
        for(LONG i=0;i<right._lmprogramcurtailmentcustomers.size();i++)
        {
            _lmprogramcurtailmentcustomers.push_back(((CtiLMCurtailCustomer*)right._lmprogramcurtailmentcustomers[i])->replicate());
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
CtiLMProgramBaseSPtr CtiLMProgramCurtailment::replicate() const
{
    return CtiLMProgramBaseSPtr(CTIDBG_new CtiLMProgramCurtailment(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::restore(Cti::RowReader &rdr)
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
    setActionDateTime(gInvalidCtiTime);
    setNotificationDateTime(gInvalidCtiTime);
    setCurtailmentStartTime(gInvalidCtiTime);
    setCurtailmentStopTime(gInvalidCtiTime);
    setRunStatus(CtiLMProgramCurtailment::NullRunStatus);
    setAdditionalInfo("null");
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information.
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::dumpDynamicData()
{
    Cti::Database::DatabaseConnection   conn;

    dumpDynamicData(conn,CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this curtailment program.
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if( getManualControlReceivedFlag() )
    {
        updateLMCurtailProgramActivityTable(conn, currentDateTime);
    }
}

/*---------------------------------------------------------------------------
    restoreDynamicData

    Restores self's dynamic data
---------------------------------------------------------------------------*/
void CtiLMProgramCurtailment::restoreDynamicData()
{
    if( getManualControlReceivedFlag() )
    {
        static const string sql =  "SELECT CPA.curtailreferenceid, CPA.actiondatetime, CPA.notificationdatetime, "
                                       "CPA.curtailmentstarttime, CPA.curtailmentstoptime, CPA.runstatus, "
                                       "CPA.additionalinfo "
                                   "FROM lmcurtailprogramactivity CPA "
                                   "WHERE CPA.deviceid = ? "
                                   "ORDER BY CPA.actiondatetime DESC";

        Cti::Database::DatabaseConnection conn;
        Cti::Database::DatabaseReader rdr(conn, sql);

        rdr << getPAOId();

        rdr.execute();

        if( _LM_DEBUG & LM_DEBUG_DATABASE )
        {
            CTILOG_DEBUG(dout, rdr.asString());
        }

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
}

// Static Members

// Possible run statuses
const string CtiLMProgramCurtailment::NullRunStatus = "Null";
const string CtiLMProgramCurtailment::ScheduledRunStatus = "Scheduled";
const string CtiLMProgramCurtailment::NotifiedRunStatus = "Notified";
const string CtiLMProgramCurtailment::CanceledRunStatus = "Canceled";
const string CtiLMProgramCurtailment::ActiveRunStatus = "Active";
const string CtiLMProgramCurtailment::StoppedEarlyRunStatus = "StoppedEarly";
const string CtiLMProgramCurtailment::CompletedRunStatus = "Completed";

