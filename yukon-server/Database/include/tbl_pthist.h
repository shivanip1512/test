/*-----------------------------------------------------------------------------*
*
* File:   tbl_pthist
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_pthist.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:18 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __PTHIST_H__
#define __PTHIST_H__

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <rw/db/db.h>

#include "yukon.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "resolvers.h"
#include "ctibase.h"

class IM_EX_CTIYUKONDB CtiTablePointHistory : public CtiMemDBObject
{

protected:
        LONG PointID;
        RWDBDateTime TimeStamp;
        INT Quality;
        FLOAT Value;

public:

    typedef CtiMemDBObject Inherited;

    CtiTablePointHistory();
    CtiTablePointHistory( LONG pointid, const RWDBDateTime& timestamp,
                          INT quality, FLOAT value );
    CtiTablePointHistory(const CtiTablePointHistory& );

    virtual ~CtiTablePointHistory();

    virtual operator=(const CtiTablePointHistory&);
    virtual operator==(const CtiTablePointHistory&) const;

    virtual RWCString getTableName() const;

    virtual void Insert();
    virtual void Update();
    virtual void Restore();
    virtual void Delete();

    virtual void DecodeDatabaseReader(RWDBReader& rdr);

    LONG getPointID() const;
    CtiTablePointHistory& setPointID(LONG pointID);

    const RWDBDateTime& getTimeStamp() const;
    CtiTablePointHistory& setTimeStamp(const RWDBDateTime& timestamp);

    INT getQuality() const;
    CtiTablePointHistory& setQuality(INT quality);

    FLOAT getValue() const;
    CtiTablePointHistory& setValue(FLOAT value);

};

#endif
