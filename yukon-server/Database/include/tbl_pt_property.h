/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_property
*
* Date:   2/12/2008
*
* PVCS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2008/02/22 23:47:08 $
*
* Copyright (c) 2008 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)
#ifndef __TBL_PT_PROPERTY_H__
#define __TBL_PT_PROPERTY_H__

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

class IM_EX_CTIYUKONDB CtiTablePointProperty : public CtiMemDBObject
{
protected:

    // If memory becomes an issue, this string should be modified (pointer perhaps)
    typedef std::map<unsigned int, float> PropertyMap;
    typedef PropertyMap::iterator PropertyMapIter;

private:
    PropertyMap _propertyMap;
    long _pointID;

public:

    CtiTablePointProperty();
    CtiTablePointProperty(const CtiTablePointProperty& aRef);
    virtual ~CtiTablePointProperty();
    
    CtiTablePointProperty&   operator=(const CtiTablePointProperty& aRef);

    bool   hasProperty(unsigned int propertyID);
    float  getFloatProperty(unsigned int propertyID);
    int    getIntProperty(unsigned int propertyID);

    static void          getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    void                 DecodeDatabaseReader(RWDBReader &rdr);
    void                 dump() const;
    static string        getTableName();
    void                 resetTable();

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

#endif // #ifndef __TBL_PT_PROPERTY_H__
