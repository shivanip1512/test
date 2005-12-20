/*-----------------------------------------------------------------------------*
*
* File:   tbl_pao
*
* Date:   9/12/2001
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pao.cpp-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2005/12/20 17:16:06 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "logger.h"
#include "resolvers.h"
#include "tbl_pao.h"

#include "rwutil.h"

using std::transform;

CtiTblPAO::CtiTblPAO() :
_paObjectID(-1)
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

void CtiTblPAO::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(getTableName().c_str());

    selector <<
    keyTable["paobjectid"] <<
    keyTable["category"] <<
    keyTable["paoclass"] <<
    keyTable["paoname"] <<
    keyTable["type"] <<
    keyTable["description"] <<
    keyTable["disableflag"] <<
    keyTable["paostatistics"];

    selector.from(keyTable);

    // No where clause here! selector.where( selector.where() == devTbl["paobjectid"] );
}

RWDBStatus CtiTblPAO::Restore()
{
    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["paobjectid"] <<
    table["category"] <<
    table["paoclass"] <<
    table["paoname"] <<
    table["type"] <<
    table["description"] <<
    table["disableflag"] <<
    table["paostatistics"];

    selector.where( table["paobjectid"] == getID() );

    RWDBReader reader = selector.reader( conn );

    if( reader() )
    {
        DecodeDatabaseReader( reader );
        setDirty( false );
    }
    else
    {
        setDirty( true );
    }
    return reader.status();
}

RWDBStatus CtiTblPAO::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getID() <<
    getCategory() <<
    getClassStr() <<
    getName() <<
    getTypeStr() <<
    getDescription() <<
    getDisableFlagStr() <<
    getStatisticsStr();

    if( ExecuteInserter(conn,inserter,__FILE__,__LINE__).errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTblPAO::Update()
{
    char temp[32];


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBUpdater updater = table.updater();

    updater.where( table["paobjectid"] == getID() );

    updater <<
    table["category"].assign(getCategory().c_str()) <<
    table["paoclass"].assign(getClassStr().c_str()) <<
    table["paoname"].assign(getName().c_str()) <<
    table["type"].assign(getTypeStr().c_str()) <<
    table["description"].assign(getDescription().c_str()) <<
    table["disableflag"].assign(getDisableFlagStr().c_str()) <<
    table["paostatistics"].assign(getStatisticsStr().c_str());

    if( ExecuteUpdater(conn,updater,__FILE__,__LINE__) == RWDBStatus::ok )
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTblPAO::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["paobjectid"] == getID() );
    deleter.execute( conn );
    return deleter.status();
}


void CtiTblPAO::DecodeDatabaseReader(RWDBReader &rdr)
{
    INT iTemp;
    string rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


