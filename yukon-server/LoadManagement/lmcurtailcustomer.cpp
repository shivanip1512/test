#include "precompiled.h"

#include "dbaccess.h"
#include "lmcurtailcustomer.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "resolvers.h"
#include "utility.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"
#include "database_util.h"

using std::string;
using std::endl;

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( CtiLMCurtailCustomer, CTILMCURTAILCUSTOMER_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer::CtiLMCurtailCustomer() :
_requireack(false),
_curtailreferenceid(0),
_acklateflag(false)
{
}

CtiLMCurtailCustomer::CtiLMCurtailCustomer(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMCurtailCustomer::CtiLMCurtailCustomer(const CtiLMCurtailCustomer& customer)
{
    operator=(customer);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer::~CtiLMCurtailCustomer()
{
}

BOOL CtiLMCurtailCustomer::getRequireAck() const
{
    return _requireack;
}

LONG CtiLMCurtailCustomer::getCurtailReferenceId() const
{
    return _curtailreferenceid;
}

const string& CtiLMCurtailCustomer::getAcknowledgeStatus() const
{
    return _acknowledgestatus;
}

const CtiTime& CtiLMCurtailCustomer::getAckDateTime() const
{
    return _ackdatetime;
}

const string& CtiLMCurtailCustomer::getIPAddressOfAckUser() const
{
    return _ipaddressofackuser;
}

const string& CtiLMCurtailCustomer::getUserIdName() const
{
    return _useridname;
}

const string& CtiLMCurtailCustomer::getNameOfAckPerson() const
{
    return _nameofackperson;
}

const string& CtiLMCurtailCustomer::getCurtailmentNotes() const
{
    return _curtailmentnotes;
}

BOOL CtiLMCurtailCustomer::getAckLateFlag() const
{
    return _acklateflag;
}



CtiLMCurtailCustomer& CtiLMCurtailCustomer::setRequireAck(BOOL reqack)
{
    _requireack = reqack;
    return *this;
}

CtiLMCurtailCustomer& CtiLMCurtailCustomer::setCurtailReferenceId(LONG refid)
{
    _curtailreferenceid = refid;
    return *this;
}

CtiLMCurtailCustomer& CtiLMCurtailCustomer::setAcknowledgeStatus(const string& ackstatus)
{
    _acknowledgestatus = ackstatus;
    return *this;
}

CtiLMCurtailCustomer& CtiLMCurtailCustomer::setAckDateTime(const CtiTime& acktime)
{
    _ackdatetime = acktime;
    return *this;
}

CtiLMCurtailCustomer& CtiLMCurtailCustomer::setIPAddressOfAckUser(const string& ipaddress)
{
    _ipaddressofackuser = ipaddress;
    return *this;
}

CtiLMCurtailCustomer& CtiLMCurtailCustomer::setUserIdName(const string& username)
{
    _useridname = username;
    return *this;
}

CtiLMCurtailCustomer& CtiLMCurtailCustomer::setNameOfAckPerson(const string& nameackperson)
{
    _nameofackperson = nameackperson;
    return *this;
}

CtiLMCurtailCustomer& CtiLMCurtailCustomer::setCurtailmentNotes(const string& curtailnotes)
{
    _curtailmentnotes = curtailnotes;
    return *this;
}

CtiLMCurtailCustomer& CtiLMCurtailCustomer::setAckLateFlag(BOOL acklate)
{
    _acklateflag = acklate;
    return *this;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer* CtiLMCurtailCustomer::replicate() const
{
    return(CTIDBG_new CtiLMCurtailCustomer(*this));
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::operator=(const CtiLMCurtailCustomer& right)
{
    if( this != &right )
    {
        CtiLMCICustomerBase::operator=(right);
        _requireack             = right._requireack;
        _curtailreferenceid     = right._curtailreferenceid;
        _acknowledgestatus      = right._acknowledgestatus;
        _ackdatetime            = right._ackdatetime;
        _ipaddressofackuser     = right._ipaddressofackuser;
        _useridname             = right._useridname;
        _nameofackperson        = right._nameofackperson;
        _curtailmentnotes       = right._curtailmentnotes;
        _acklateflag            = right._acklateflag;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::restore(Cti::RowReader &rdr)
{
    CtiLMCICustomerBase::restore(rdr);
    string tempBoolString;

    rdr["requireack"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    tempBoolString = trim(tempBoolString);

    setRequireAck(tempBoolString=="y"?TRUE:FALSE);
    setCurtailReferenceId(0);
    setAcknowledgeStatus(CtiLMCurtailCustomer::NotRequiredAckStatus);
    setAckDateTime(gInvalidCtiTime);
    setIPAddressOfAckUser("1.1.1.1");
    setUserIdName("Null");
    setNameOfAckPerson("Null");
    setCurtailmentNotes("Null");
    setAckLateFlag(FALSE);
}

/*---------------------------------------------------------------------------
    addLMCurtailCustomerActivity

    .
---------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::addLMCurtailCustomerActivityTable()
{
    static const std::string sql = "insert into lmcurtailcustomeractivity values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter
        << getCustomerId()
        << getCurtailReferenceId()
        << getAcknowledgeStatus()
        << getAckDateTime()
        << getIPAddressOfAckUser()
        << getUserIdName()
        << getNameOfAckPerson()
        << getCurtailmentNotes()
        << getCustomerDemandLevel()
        << ( ( getAckLateFlag() ? std::string("Y") : std::string("N") ) );

    Cti::Database::executeCommand( inserter, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) );
}

/*---------------------------------------------------------------------------
    updateLMCurtailCustomerActivity

    .
---------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::updateLMCurtailCustomerActivityTable(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    static const std::string sql = "update lmcurtailcustomeractivity"
                                   " set "
                                        "acknowledgestatus = ?, "
                                        "ackdatetime = ?, "
                                        "ipaddressofackuser = ?, "
                                        "useridname = ?, "
                                        "nameofackperson = ?, "
                                        "curtailmentnotes = ?, "
                                        "currentpdl = ?, "
                                        "acklateflag = ?"
                                   " where "
                                        "customerid = ? and "
                                        "curtailreferenceid = ?";

    Cti::Database::DatabaseWriter   updater(conn, sql);

    updater
        << getAcknowledgeStatus()[0]
        << getAckDateTime()
        << getIPAddressOfAckUser()[0]
        << getUserIdName()[0]
        << getNameOfAckPerson()[0]
        << getCurtailmentNotes()[0]
        << getCustomerDemandLevel()
        << ( getAckLateFlag() ? std::string("Y") : std::string("N") )
        << getCustomerId()
        << getCurtailReferenceId();

    Cti::Database::executeCommand( updater, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) );
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information.
---------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::dumpDynamicData()
{
    Cti::Database::DatabaseConnection   conn;

    dumpDynamicData(conn,CtiTime());
}
/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this customer.
---------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    updateLMCurtailCustomerActivityTable(conn, currentDateTime);
}

/*---------------------------------------------------------------------------
    restoreDynamicData

    Restores self's dynamic data
---------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::restoreDynamicData()
{
    static const string sql =  "SELECT CCA.curtailreferenceid, CCA.acknowledgestatus, CCA.ackdatetime, "
                                   "CCA.ipaddressofackuser, CCA.useridname, CCA.nameofackperson, CCA.curtailmentnotes, "
                                   "CCA.acklateflag "
                               "FROM lmcurtailcustomeractivity CCA "
                               "WHERE CCA.customerid = ? "
                               "ORDER BY CCA.curtailreferenceid DESC";

    Cti::Database::DatabaseConnection conn;
    Cti::Database::DatabaseReader rdr(conn, sql);

    rdr << getCustomerId();

    rdr.execute();

    if( _LM_DEBUG & LM_DEBUG_DATABASE )
    {
        CTILOG_DEBUG(dout, rdr.asString());
    }

    if(rdr())
    {
        string tempBoolString;
        rdr["curtailreferenceid"] >> _curtailreferenceid;
        rdr["acknowledgestatus"] >> _acknowledgestatus;
        rdr["ackdatetime"] >> _ackdatetime;
        rdr["ipaddressofackuser"] >> _ipaddressofackuser;
        rdr["useridname"] >> _useridname;
        rdr["nameofackperson"] >> _nameofackperson;
        rdr["curtailmentnotes"] >> _curtailmentnotes;
        rdr["acklateflag"] >> tempBoolString;
        std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
        setAckLateFlag(tempBoolString=="y"?TRUE:FALSE);
    }
}

// Static Members

// Possible acknowledge statuses
const string CtiLMCurtailCustomer::UnAcknowledgedAckStatus = "UnAcknowledged";
const string CtiLMCurtailCustomer::AcknowledgedAckStatus = "Acknowledged";
const string CtiLMCurtailCustomer::NotRequiredAckStatus = "Not Required";
const string CtiLMCurtailCustomer::VerbalAckStatus = "Verbal";

