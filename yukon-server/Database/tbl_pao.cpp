#include "precompiled.h"

#include "dbaccess.h"
#include "logger.h"
#include "resolvers.h"
#include "tbl_pao.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"
#include "database_util.h"

using std::transform;
using std::map;
using std::string;
using std::endl;

CtiTblPAO::CtiTblPAO() :
    _paObjectID(-1),
    _class(0),
    _type(0),
    _disableFlag(false)
{}

CtiTblPAO::CtiTblPAO(const CtiTblPAO& aRef)
{
    *this = aRef;
}

CtiTblPAO::~CtiTblPAO() {}

CtiTblPAO& CtiTblPAO::operator=(const CtiTblPAO& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _paObjectID = aRef._paObjectID;
        _category = aRef._category;
        _class = aRef._class;
        _classStr = aRef._classStr;
        _name = aRef._name;
        _type = aRef._type;
        _typeStr = aRef._typeStr;
        _description = aRef._description;
        _disableFlag = aRef._disableFlag;
    }
    return *this;
}

LONG CtiTblPAO::getID() const
{

    return _paObjectID;
}

CtiTblPAO& CtiTblPAO::setID( LONG paObjectID )
{


    _paObjectID = paObjectID;
    return *this;
}

string CtiTblPAO::getCategory() const
{

    return _category;
}

string& CtiTblPAO::getCategory()
{

    return _category;
}

CtiTblPAO& CtiTblPAO::setCategory(const string &catStr)
{


    _category = catStr;
    return *this;
}

INT CtiTblPAO::getClass() const
{

    return _class;
}

INT& CtiTblPAO::getClass()
{

    return _class;
}

CtiTblPAO& CtiTblPAO::setClass(const INT &aInt)
{


    _class = aInt;
    // _classStr = desolvePAOClass(_class) ???
    return *this;
}

const string& CtiTblPAO::getClassStr() const
{

    return _classStr;
}

CtiTblPAO& CtiTblPAO::setClassStr(const string& classStr)
{

    _class = resolvePAOClass(classStr);
    _classStr = classStr;
    return *this;
}

string CtiTblPAO::getName() const
{

    return _name;
}

string& CtiTblPAO::getName()
{

    return _name;
}

CtiTblPAO& CtiTblPAO::setName(const string &nmStr)
{


    _name = nmStr;
    return *this;
}

INT CtiTblPAO::getType() const
{

    return _type;
}

CtiTblPAO& CtiTblPAO::setType( const INT &aint )
{


    _type = aint;
    // _type_str = desolvePAOType(getCategory(), _type) ???
    return *this;
}

const string& CtiTblPAO::getTypeStr() const
{

    return _typeStr;
}

CtiTblPAO& CtiTblPAO::setTypeStr(const string& typeStr)
{

    _type = resolvePAOType(getCategory(), typeStr);
    _typeStr = typeStr;
    return *this;
}

string CtiTblPAO::getDisableFlagStr() const
{


    return( _disableFlag ? "Y" : "N" );
}

CtiTblPAO& CtiTblPAO::setDisableFlagStr(const string& flag)
{
    assert( flag == "Y" || flag == "y" ||
            flag == "N" || flag == "n" );



    _disableFlag = ( flag == "y" || flag == "Y" );
    return *this;

}

CtiTblPAO& CtiTblPAO::setDisableFlag( const bool disableFlag )
{

    _disableFlag = disableFlag;
    return *this;
}

void CtiTblPAO::resetDisableFlag(bool b)
{
    _disableFlag = b;
}

string CtiTblPAO::getDescription() const
{

    return _category;
}

string& CtiTblPAO::getDescription()
{

    return _category;
}

CtiTblPAO& CtiTblPAO::setDescription(const string &desStr)
{


    _category = desStr;
    return *this;
}

string CtiTblPAO::getTableName()
{
    return "YukonPAObject";
}

bool CtiTblPAO::Insert()
{
    static const std::string sql = "insert into " + getTableName() +
                                   " values (?, ?, ?, ?, ?, ?, ?, ?)";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter
        << getID()
        << getCategory()
        << getClassStr()
        << getName()
        << getTypeStr()
        << getDescription()
        << getDisableFlagStr()
        << getStatisticsStr();

    if( ! Cti::Database::executeCommand( inserter, __FILE__, __LINE__ ))
    {
        return false;
    }

    setDirty(false);

    return true; // No error occured!
}

bool CtiTblPAO::Update()
{
    static const std::string sql = "update " + getTableName() +
                                   " set "
                                        "category = ?, "
                                        "paoclass = ?, "
                                        "paoname = ?, "
                                        "type = ?, "
                                        "description = ?, "
                                        "disableflag = ?, "
                                        "paostatistics = ?"
                                   " where "
                                        "paobjectid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << getCategory()
        << getClassStr()
        << getName()
        << getTypeStr()
        << getDescription()
        << getDisableFlagStr()
        << getStatisticsStr()
        << getID();

    if( ! Cti::Database::executeUpdater( updater, __FILE__, __LINE__ ))
    {
        return false;
    }

    setDirty(false);

    return true; // No error occured!
}

bool CtiTblPAO::Delete()
{
    static const std::string sql = "delete from " + getTableName() + " where paobjectid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << getID();

    return Cti::Database::executeCommand( deleter, __FILE__, __LINE__ );
}


void CtiTblPAO::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    INT iTemp;
    string disableStr;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["paobjectid"] >> _paObjectID;
    rdr["category"]   >> _category;
    rdr["paoclass"]   >> _classStr;

    _class = resolvePAOClass(_classStr);

    rdr["paoname"] >> _name;
    rdr["type"] >> _typeStr;

    _type = resolvePAOType(_category, _typeStr);

    rdr["description"] >> _description;

    rdr["disableflag"] >> disableStr;
    transform(disableStr.begin(), disableStr.end(), disableStr.begin(), tolower);
    _disableFlag = ((disableStr == "y") ? true : false);

    rdr["paostatistics"] >> _paostatistics;
}

std::string CtiTblPAO::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTblPAO";
    itemList.add("PAO Id")      << _paObjectID;
    itemList.add("Name")        << _name;
    itemList.add("Description") << _description;
    itemList.add("Category")    << _category;
    itemList.add("Class")       << _class;
    itemList.add("Type")        << _type;
    itemList.add("Disabled")    << _disableFlag;
    itemList.add("Statistics")  << getStatisticsStr();

    return itemList.toString();
}


string CtiTblPAO::getStatisticsStr() const
{
    return _paostatistics;
}

CtiTblPAO& CtiTblPAO::setStatisticsStr(const string &Str)
{
    _paostatistics = Str;
    return *this;
}


