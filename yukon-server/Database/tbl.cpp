/*-----------------------------------------------------------------------------*
*
* File:   tbl.cpp
*
* Initial Date:  5/12/99
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/12/20 17:16:05 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
/*-----------------------------------------------------------------------------
    Filename:  tbl.cpp

    Programmer:  Aaron Lauinger

    Description: Source file for CtiDBTable

    Initial Date:  5/12/99

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl.h"
#include "dbaccess.h"

/*-----------------------------------------------------------------------------
    Restore

    Restores the state of the table object from the database.
-----------------------------------------------------------------------------*/
void CtiDBTable::Restore()
{
    RWDBSelector selector = getDatabase().selector();
    selector << RWDBTable()["*"];
    selector.from( TableName() );

    RWDBCriterion crit;
    EncodeDatabaseCriterion( crit );

    selector.where(crit);

    RWDBReader rdr = selector.reader(getConnection());
    DecodeDatabaseReader(rdr);
}

/*----------------------- ------------------------------------------------------
    Update

    Updates the table in the database that corresponds to this object.
-----------------------------------------------------------------------------*/
void CtiDBTable::Update()
{
    RWDBUpdater updater = RWDBTable().updater();
    RWDBCriterion crit;

    EncodeDatabaseCriterion( crit );
    updater.where(crit);

    EncodeDatabaseUpdater( updater );

    updater.execute( getConnection() );
}

/*-----------------------------------------------------------------------------
    Insert

    Inserts into the table in the database that corresponds to this object.
-----------------------------------------------------------------------------*/
void CtiDBTable::Insert()
{
    RWDBInserter inserter = RWDBTable().inserter();

    EncodeDatabaseInserter( inserter );

    inserter.execute( getConnection() );
}

/*-----------------------------------------------------------------------------
    Delete

    Remove from the table in the database that corresponds to this object.
-----------------------------------------------------------------------------*/
void CtiDBTable::Delete()
{
    RWDBDeleter deleter = RWDBTable().deleter();
    RWDBCriterion crit;

    EncodeDatabaseCriterion( crit );
    deleter.where( crit );

    EncodeDatabaseDeleter( deleter );

    deleter.execute( getConnection() );
}

/*-----------------------------------------------------------------------------
    RWDBTable

    Returns the RWDBTable that contains this objects state in the database.
-----------------------------------------------------------------------------*/
RWDBTable CtiDBTable::RWDBTable()
{
    return getDatabase().table( TableName() );
}

/*-----------------------------------------------------------------------------
    TableName

    Returns the name of the corresponding databas table
-----------------------------------------------------------------------------*/
const string& CtiDBTable::TableName() const
{
    return _tablename;
}
