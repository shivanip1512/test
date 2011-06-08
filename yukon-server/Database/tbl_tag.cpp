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

using std::string;

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

string CtiTableTag::getSQLCoreStatement()
{
    static const string sql = "SELECT TGS.tagid, TGS.tagname, TGS.taglevel, TGS.inhibit, TGS.colorid, TGS.imageid "
                              "FROM Tags TGS";

    return sql;
}

void CtiTableTag::DecodeDatabaseReader(Cti::RowReader& rdr)
{
    string rwsTemp;

    rdr["tagid"]        >> _tagId;
    rdr["tagname"]      >> _tagName;
    rdr["taglevel"]     >> _tagLevel;
    rdr["inhibit"]      >> rwsTemp;
    rdr["colorid"]      >> _colorId;
    rdr["imageid"]      >> _imageId;

    _inhibit = ciStringEqual(rwsTemp,"Y");

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


