/*-----------------------------------------------------------------------------*
*
* File:   tbl_pthist
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_pthist.h-arc  $
* REVISION     :  $Revision: 1.4.24.1 $
* DATE         :  $Date: 2008/11/18 20:11:29 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __PTHIST_H__
#define __PTHIST_H__

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....


#include "yukon.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "ctibase.h"
#include "ctitime.h"
#include "row_reader.h"

class CtiTablePointHistory : public CtiMemDBObject
{

protected:
        LONG PointID;
        CtiTime TimeStamp;
        INT Quality;
        FLOAT Value;

public:

    typedef CtiMemDBObject Inherited;

    CtiTablePointHistory();
    CtiTablePointHistory( LONG pointid, const CtiTime& timestamp,
                          INT quality, FLOAT value );
    CtiTablePointHistory(const CtiTablePointHistory& );

    virtual ~CtiTablePointHistory();

    virtual CtiTablePointHistory& operator=(const CtiTablePointHistory&);
    virtual bool operator==(const CtiTablePointHistory&) const;

    virtual std::string getTableName() const;

    virtual void DecodeDatabaseReader(Cti::RowReader& rdr);

    LONG getPointID() const;
    CtiTablePointHistory& setPointID(LONG pointID);

    const CtiTime& getTimeStamp() const;
    CtiTablePointHistory& setTimeStamp(const CtiTime& timestamp);

    INT getQuality() const;
    CtiTablePointHistory& setQuality(INT quality);

    FLOAT getValue() const;
    CtiTablePointHistory& setValue(FLOAT value);

};

#endif
