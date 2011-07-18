#include "precompiled.h"

#include "dbaccess.h"
#include "logger.h"
#include "resolvers.h"
#include "tbl_pao.h"

#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"

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

        /* WHAT WAS THIS CHECKPOINT FOR?
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        } */
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

    bool success = inserter.execute();

    if ( success )
    {
        setDirty(false);
    }

    return success;
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

    bool success = executeUpdater(updater);

    if( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTblPAO::Delete()
{
    static const std::string sql = "delete from " + getTableName() + " where paobjectid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << getID();

    return deleter.execute();
}


void CtiTblPAO::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    INT iTemp;
    string rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["paobjectid"] >> _paObjectID;
    rdr["category"]   >> _category;
    rdr["paoclass"]   >> _classStr;

    _class = resolvePAOClass(_classStr);

    rdr["paoname"] >> _name;
    rdr["type"] >> _typeStr;

    _type = resolvePAOType(_category, _typeStr);

    rdr["description"] >> _description;

    rdr["disableflag"] >> rwsTemp;
    transform(rwsTemp.begin(), rwsTemp.end(), rwsTemp.begin(), tolower);
    _disableFlag = ((rwsTemp == "y") ? true : false);

    rdr["paostatistics"] >> _paostatistics;
}

void CtiTblPAO::DumpData()
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << endl;
    dout << "PAO Id                                      : " << _paObjectID << endl;
    dout << "Name                                        : " << _name << endl;
    dout << "Description                                 : " << _description << endl;
    dout << "Category                                    : " << _category << endl;
    dout << "Class                                       : " << _class << endl;
    dout << "Type                                        : " << _type << endl;
    dout << "Disabled                                    : " << (_disableFlag ? "Yes" : "No") << endl;
    dout << "Statistics                                  : " << getStatisticsStr() << endl;
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


