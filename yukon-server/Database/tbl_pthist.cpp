#include "precompiled.h"
#include "tbl_pthist.h"

/*-----------------------------------------------------------------------------*
*
* File:   tbl_pthist
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pthist.cpp-arc  $
* REVISION     :  $Revision: 1.7.24.1 $
* DATE         :  $Date: 2008/11/18 20:11:28 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dbaccess.h"

#include "resolvers.h"

#include "database_connection.h"
#include "database_reader.h"

using std::string;

CtiTablePointHistory::CtiTablePointHistory() :
    PointID(0),
    Quality(0),
    Value(0.0)
{
}

CtiTablePointHistory::CtiTablePointHistory(LONG pointid, const CtiTime& timestamp, INT quality, FLOAT value ):
PointID(pointid),
TimeStamp(timestamp),
Quality(quality),
Value(value)
{
}

CtiTablePointHistory::CtiTablePointHistory(const CtiTablePointHistory& ref)
{
    *this = ref;
}

CtiTablePointHistory::~CtiTablePointHistory()
{
}

CtiTablePointHistory& CtiTablePointHistory::operator=(const CtiTablePointHistory& right)
{
    setPointID( right.getPointID() );
    setTimeStamp( right.getTimeStamp() );
    setQuality( right.getQuality() );
    setValue( right.getValue() );
    return *this;
}

bool CtiTablePointHistory::operator==(const CtiTablePointHistory& right) const
{
    return( getPointID() == right.getPointID() &&
            getTimeStamp() == right.getTimeStamp() );
}

string CtiTablePointHistory::getTableName() const
{
    return "PointHistory";
}

void CtiTablePointHistory::DecodeDatabaseReader(Cti::RowReader& rdr )
{
    rdr >> PointID >> TimeStamp >> Quality >> Value;
}

LONG CtiTablePointHistory::getPointID() const
{


    return PointID;
}

CtiTablePointHistory& CtiTablePointHistory::setPointID(LONG pointid)
{


    PointID = pointid;
    return *this;
}

const CtiTime& CtiTablePointHistory::getTimeStamp() const
{


    return TimeStamp;
}

CtiTablePointHistory& CtiTablePointHistory::setTimeStamp(const CtiTime& timestamp)
{


    TimeStamp = timestamp;
    return *this;
}

INT CtiTablePointHistory::getQuality() const
{


    return Quality;
}

CtiTablePointHistory& CtiTablePointHistory::setQuality(INT quality)
{


    Quality = quality;
    return *this;
}

FLOAT CtiTablePointHistory::getValue() const
{


    return Value;
}

CtiTablePointHistory& CtiTablePointHistory::setValue(FLOAT value)
{


    Value = value;
    return *this;
}


