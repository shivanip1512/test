
/*-----------------------------------------------------------------------------*
*
* File:   tbl_tag
*
* Class:  CtiTableTag
* Date:   12/23/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/12/30 21:57:24 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_TAG_H__
#define __TBL_TAG_H__


#include <rw/db/db.h>
#include <rw/rwtime.h>

#include "ctibase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "yukon.h"

class IM_EX_CTIYUKONDB CtiTableTag : public CtiMemDBObject
{
protected:

    int             _tagId;
    RWCString       _tagName;
    int             _tagLevel;
    bool            _inhibit;
    int             _colorId;
    int             _imageId;

private:

public:

    typedef CtiMemDBObject Inherited;


    CtiTableTag();
    CtiTableTag(const CtiTableTag& aRef);
    virtual ~CtiTableTag();

    virtual int operator==(const CtiTableTag& aRef) const;

    static RWCString getTableName();
    virtual RWDBStatus Restore();

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    void DecodeDatabaseReader(RWDBReader& rdr);

    int getTagId() const;
    bool getInhibit() const;
    RWCString getTagName() const;
};
#endif // #ifndef __TBL_TAG_H__
