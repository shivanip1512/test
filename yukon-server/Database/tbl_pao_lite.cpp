/*-----------------------------------------------------------------------------*
*
* File:   tbl_pao_lite
*
* Date:   8/15/2008
*
* Author: Jess Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pao.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2008/10/28 19:21:40 $
*
* Copyright (c) 2008 Cooper Industries. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "logger.h"
#include "resolvers.h"
#include "tbl_pao_lite.h"

#include "rwutil.h"

using std::transform;

CtiTblPAOLite::CtiTblPAOLite() :
_paObjectID(-1)
{}

CtiTblPAOLite::CtiTblPAOLite(const CtiTblPAOLite& aRef)
{
    *this = aRef;
}

CtiTblPAOLite::~CtiTblPAOLite() {}

CtiTblPAOLite& CtiTblPAOLite::operator=(const CtiTblPAOLite& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _paObjectID = aRef._paObjectID;
        _class = aRef._class;
        _name = aRef._name;
        _type = aRef._type;
        _disableFlag = aRef._disableFlag;

        /* WHAT WAS THIS CHECKPOINT FOR?
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        } */
    }
    return *this;
}

LONG CtiTblPAOLite::getID() const
{
    return _paObjectID;
}

CtiTblPAOLite& CtiTblPAOLite::setID( LONG paObjectID )
{
    _paObjectID = paObjectID;
    return *this;
}

INT CtiTblPAOLite::getClass() const
{
    return _class;
}

string CtiTblPAOLite::getName() const
{
    return _name;
}

INT CtiTblPAOLite::getType() const
{
    return _type;
}

CtiTblPAOLite& CtiTblPAOLite::setType( const INT &type )
{
    _type = type;
    return *this;
}

string CtiTblPAOLite::getTableName()
{
    return "YukonPAObject";
}

void CtiTblPAOLite::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(getTableName().c_str());

    selector <<
    keyTable["paobjectid"] <<
    keyTable["category"] <<
    keyTable["paoclass"] <<
    keyTable["paoname"] <<
    keyTable["type"] <<
    keyTable["disableflag"];

    selector.from(keyTable);

    // No where clause here! selector.where( selector.where() == devTbl["paobjectid"] );
}

RWDBStatus CtiTblPAOLite::Restore()
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
    table["disableflag"];

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

void CtiTblPAOLite::DecodeDatabaseReader(RWDBReader &rdr)
{
    INT iTemp;
    string rwsTemp, category;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["paobjectid"] >> _paObjectID;
    rdr >> category;
    rdr >> rwsTemp;

    _class = resolvePAOClass(rwsTemp);

    rdr >> _name;
    rdr >> rwsTemp; // type

    _type = resolvePAOType(category, rwsTemp);

    rdr >> rwsTemp;
    transform(rwsTemp.begin(), rwsTemp.end(), rwsTemp.begin(), tolower);
    _disableFlag = ((rwsTemp == "y") ? true : false);
}

void CtiTblPAOLite::DumpData()
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << endl;
    dout << "PAO Id                                      : " << _paObjectID << endl;
    dout << "Name                                        : " << _name << endl;
    dout << "Class                                       : " << _class << endl;
    dout << "Type                                        : " << _type << endl;
}
