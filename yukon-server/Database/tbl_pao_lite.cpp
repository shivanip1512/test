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
#include "precompiled.h"

#include "dbaccess.h"
#include "logger.h"
#include "resolvers.h"
#include "tbl_pao_lite.h"
#include "database_connection.h"
#include "database_reader.h"

using std::transform;
using std::string;
using std::endl;

CtiTblPAOLite::CtiTblPAOLite() :
    _paObjectID(-1),
    _class(0),
    _type(0),
    _disableFlag(false)
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

void CtiTblPAOLite::DecodeDatabaseReader(Cti::RowReader &rdr)
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
