#include "yukon.h"



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
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dbaccess.h"
#include "logger.h"
#include "resolvers.h"
#include "tbl_pao.h"

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
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

RWCString CtiTblPAO::getCategory() const
{

    return _category;
}

RWCString& CtiTblPAO::getCategory()
{

    return _category;
}

CtiTblPAO& CtiTblPAO::setCategory(const RWCString &catStr)
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

const RWCString& CtiTblPAO::getClassStr() const
{

    return _classStr;
}

CtiTblPAO& CtiTblPAO::setClassStr(const RWCString& classStr)
{

    _class = resolvePAOClass(classStr);
    _classStr = classStr;
    return *this;
}

RWCString CtiTblPAO::getName() const
{

    return _name;
}

RWCString& CtiTblPAO::getName()
{

    return _name;
}

CtiTblPAO& CtiTblPAO::setName(const RWCString &nmStr)
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

const RWCString& CtiTblPAO::getTypeStr() const
{

    return _typeStr;
}

CtiTblPAO& CtiTblPAO::setTypeStr(const RWCString& typeStr)
{

    _type = resolvePAOType(getCategory(), typeStr);
    _typeStr = typeStr;
    return *this;
}

RWCString CtiTblPAO::getDisableFlagStr() const
{


    return( _disableFlag ? "Y" : "N" );
}

CtiTblPAO& CtiTblPAO::setDisableFlagStr(const RWCString& flag)
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

RWCString CtiTblPAO::getDescription() const
{

    return _category;
}

RWCString& CtiTblPAO::getDescription()
{

    return _category;
}

CtiTblPAO& CtiTblPAO::setDescription(const RWCString &desStr)
{


    _category = desStr;
    return *this;
}

RWCString CtiTblPAO::getTableName()
{
    return "YukonPAObject";
}

void CtiTblPAO::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(getTableName() );

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

    RWDBTable table = getDatabase().table( getTableName() );
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

    RWDBTable table = getDatabase().table( getTableName() );
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

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
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

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["paobjectid"] == getID() );

    updater <<
    table["category"].assign(getCategory()) <<
    table["paoclass"].assign(getClassStr()) <<
    table["paoname"].assign(getName()) <<
    table["type"].assign(getTypeStr()) <<
    table["description"].assign(getDescription()) <<
    table["disableflag"].assign(getDisableFlagStr()) <<
    table["paostatistics"].assign(getStatisticsStr());

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTblPAO::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["paobjectid"] == getID() );
    deleter.execute( conn );
    return deleter.status();
}


void CtiTblPAO::DecodeDatabaseReader(RWDBReader &rdr)
{
    INT iTemp;
    RWCString rwsTemp;

    if(getDebugLevel() & 0x0800)
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
    rwsTemp.toLower();
    _disableFlag = ((rwsTemp == 'y') ? true : false);

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


RWCString CtiTblPAO::getStatisticsStr() const
{
    return _paostatistics;
}

CtiTblPAO& CtiTblPAO::setStatisticsStr(const RWCString &Str)
{
    _paostatistics = Str;
    return *this;
}


