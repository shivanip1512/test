
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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/12/20 17:16:09 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_TAG_H__
#define __TBL_TAG_H__



#include "ctibase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableTag : public CtiMemDBObject
{
protected:

    int             _tagId;
    std::string       _tagName;
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

    static std::string getTableName();

    static std::string getSQLCoreStatement();

    void DecodeDatabaseReader(Cti::RowReader& rdr);

    int getTagId() const;
    bool getInhibit() const;
    std::string getTagName() const;
};
#endif // #ifndef __TBL_TAG_H__
