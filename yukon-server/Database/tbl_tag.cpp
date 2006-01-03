#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_tag
*
* Date:   12/23/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2006/01/03 20:23:37 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "dbaccess.h"
#include "logger.h"
#include "numstr.h"
#include "tbl_tag.h"
#include "utility.h"
#include "rwutil.h"

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

string CtiTableTag::getTableName()
{
    return string("Tags");
}


RWDBStatus CtiTableTag::Restore()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
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
    keyTable = db.table(CtiTableTag::getTableName().c_str());

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
    string rwsTemp;

    rdr["tagid"]        >> _tagId;
    rdr["tagname"]      >> _tagName;
    rdr["taglevel"]     >> _tagLevel;
    rdr["inhibit"]      >> rwsTemp;
    rdr["colorid"]      >> _colorId;
    rdr["imageid"]      >> _imageId;

    _inhibit = ( stringCompareIgnoreCase(rwsTemp,"Y") == 0 ? true : false );

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

string CtiTableTag::getTagName() const
{
    return _tagName;
}


