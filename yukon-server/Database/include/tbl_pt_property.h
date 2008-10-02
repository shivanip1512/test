/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_property
*
* Date:   2/12/2008
*
* PVCS KEYWORDS:
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2008/10/02 18:27:30 $
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

private:
    long         _pointID;
    unsigned int _propertyID;
    float        _floatAttributeValue;

    CtiTablePointProperty(const CtiTablePointProperty& aRef);

    CtiTablePointProperty& operator=(const CtiTablePointProperty& aRef);

public:

    CtiTablePointProperty(RWDBReader &rdr);
    virtual ~CtiTablePointProperty();

    bool operator<(const CtiTablePointProperty &rhs) const;

    long         getPointID()    const;
    unsigned int getPropertyID() const;

    float  getFloatProperty() const;
    int    getIntProperty  () const;

    static void   getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    void          dump() const;
    static string getTableName();

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
