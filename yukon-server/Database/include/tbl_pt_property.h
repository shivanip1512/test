/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_property
*
* Date:   2/12/2008
*
* PVCS KEYWORDS:
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2008/11/11 21:51:43 $
*
* Copyright (c) 2008 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)
#ifndef __TBL_PT_PROPERTY_H__
#define __TBL_PT_PROPERTY_H__

#include "row_reader.h"

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include <boost/shared_ptr.hpp>

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

    CtiTablePointProperty(Cti::RowReader &rdr);
    CtiTablePointProperty(long pointID, unsigned int propertyID, float attributeValue);
    virtual ~CtiTablePointProperty();

    bool operator<(const CtiTablePointProperty &rhs) const;

    long         getPointID()    const;
    unsigned int getPropertyID() const;

    float  getFloatProperty() const;
    int    getIntProperty  () const;

    static std::string getSQLCoreStatement();

    void          dump() const;
    static std::string getTableName();

    enum
    {
        STALE_ALARM_TIME = 1,
        STALE_UPDATE_TYPE,

        ARCHIVE_ON_TIMER, //The number of this does not matter as this is not stored in the database
    };

    enum
    {
        UPDATE_ALWAYS = 1,
        UPDATE_ON_CHANGE,
    };
};

typedef boost::shared_ptr< CtiTablePointProperty > CtiTablePointPropertySPtr;

#endif // #ifndef __TBL_PT_PROPERTY_H__
