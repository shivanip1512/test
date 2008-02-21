/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_attribute
*
* Date:   2/12/2008
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_pt_unit.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2008/02/21 18:56:08 $
*
* Copyright (c) 2008 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)
#ifndef __TBL_PT_ATTRIBUTE_H__
#define __TBL_PT_ATTRIBUTE_H__

#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>

#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/datetime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include <map>
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "yukon.h"

class IM_EX_CTIYUKONDB CtiTablePointAttribute : public CtiMemDBObject
{
protected:

    // If memory becomes an issue, this string should be modified (pointer perhaps)
    typedef std::map<unsigned int, float> AttributeMap;
    typedef AttributeMap::iterator AttributeMapIter;

private:
    AttributeMap _attributeMap;
    long _pointID;

public:

    CtiTablePointAttribute();
    CtiTablePointAttribute(const CtiTablePointAttribute& aRef);
    virtual ~CtiTablePointAttribute();
    
    CtiTablePointAttribute&   operator=(const CtiTablePointAttribute& aRef);

    bool   hasAttribute(unsigned int attributeID);
    float  getFloatAttribute(unsigned int attributeID);
    int    getIntAttribute(unsigned int attributeID);

    static void          getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    void                 DecodeDatabaseReader(RWDBReader &rdr);
    void                 dump() const;
    static string     getTableName();

    enum
    {
        STALE_ALARM_TIME = 1,
        STALE_UPDATE_TYPE,
    };

    enum
    {
        UPDATE_ALWAYS = 1,
        UPDATE_ON_CHANGE,
    };
};

#endif // #ifndef __TBL_PT_ATTRIBUTE_H__
