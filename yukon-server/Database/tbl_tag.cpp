

/*-----------------------------------------------------------------------------*
*
* File:   tbl_tag
*
* Date:   12/23/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/12/30 21:57:23 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include "dbaccess.h"
#include "logger.h"
#include "numstr.h"
#include "tbl_tag.h"

CtiTableTag::CtiTableTag() :
_tagId(0),
_tagName("(none)"),
_tagLevel(0),
_inhibit(false),
_colorId(0),
_imageId(0)
{
}

CtiTableTag::CtiTableTag(const CtiTableTag& aRef) :
_tagId(0),
_tagName("(none)"),
_tagLevel(0),
_inhibit(false),
_colorId(0),
_imageId(0)
{
    *this = aRef;
}

CtiTableTag::~CtiTableTag()
{
}


int CtiTableTag::operator==(const CtiTableTag &right) const
{
    return( getTagId() == right.getTagId() );
}

RWCString CtiTableTag::getTableName()
{
    return RWCString("Tags");
}


RWDBStatus CtiTableTag::Restore()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector<<
    table["tagid"] <<
    table["tagname"] <<
    table["taglevel"] <<
    table["inhibit"] <<
    table["colorid"] <<
    table["imageid"];

    selector.where( table["tagid"] == getTagId() );

    RWDBReader reader = selector.reader( conn );

    /*
     *  If we are in the database, we reload and ARE NOT dirty... otherwise, we are sirty and need to be
     *  written into the database
     */
    if( reader() )
    {
        DecodeDatabaseReader( reader );
    }
    else
    {
        setDirty( TRUE );
    }

    return reader.status();
}

void CtiTableTag::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(CtiTableTag::getTableName());

    selector <<
    keyTable["tagid"] <<
    keyTable["tagname"] <<
    keyTable["taglevel"] <<
    keyTable["inhibit"] <<
    keyTable["colorid"] <<
    keyTable["imageid"];

    selector.from(keyTable);
}

void CtiTableTag::DecodeDatabaseReader(RWDBReader& rdr)
{
    RWCString rwsTemp;

    rdr["tagid"]        >> _tagId;
    rdr["tagname"]      >> _tagName;
    rdr["taglevel"]     >> _tagLevel;
    rdr["inhibit"]      >> rwsTemp;
    rdr["colorid"]      >> _colorId;
    rdr["imageid"]      >> _imageId;

    _inhibit = ( rwsTemp.compareTo("Y", RWCString::ignoreCase) == 0 ? true : false );

    resetDirty(FALSE);
}

int CtiTableTag::getTagId() const
{
    return _tagId;
}

bool CtiTableTag::getInhibit() const
{
    return _inhibit;
}

RWCString CtiTableTag::getTagName() const
{
    return _tagName;
}


