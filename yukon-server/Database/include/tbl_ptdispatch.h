
#pragma warning( disable : 4786)
#ifndef __TBL_PTPERSISTENCE_H__
#define __TBL_PTPERSISTENCE_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_ptpersistence
*
* Class:  CtiTablePointDispatch
* Date:   6/16/2000
*
* Author: Corey G. Plender
*
* This class is used to store the state of a point across runs of the dispatch
* application.  It allows dispatch to maintain the state of the system's points
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <limits.h>

#include <rw/db/db.h>
#include <rw/rwtime.h>

#include "ctibase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "pointdefs.h"
#include "yukon.h"


class IM_EX_CTIYUKONDB CtiTablePointDispatch : public CtiMemDBObject
{
protected:

    LONG           iPointID;

    RWDBDateTime   iTimeStamp;
    DOUBLE         iValue;
    UINT           iQuality;

    RWDBDateTime   iNextArchiveTime;
    UINT           iTags;
    UINT           iStaleCount;

private:

    public:

    typedef CtiMemDBObject Inherited;

    CtiTablePointDispatch();

    CtiTablePointDispatch(LONG pointid,
                          DOUBLE value = 0,
                          UINT quality = UnintializedQuality,
                          const RWDBDateTime& timestamp = RWDBDateTime( (UINT)1990, (UINT)1, (UINT)1 ) );

    CtiTablePointDispatch(const CtiTablePointDispatch& aRef);

    virtual ~CtiTablePointDispatch();

    virtual CtiTablePointDispatch& operator=(const CtiTablePointDispatch&);
    virtual operator==(const CtiTablePointDispatch&) const;

    static RWCString getTableName();

    RWDBStatus Insert(RWDBConnection &conn);
    RWDBStatus Update(RWDBConnection &conn);

    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Restore();
    virtual RWDBStatus Delete();

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    void DecodeDatabaseReader(RWDBReader& rdr);

    LONG getPointID() const;
    CtiTablePointDispatch& setPointID(LONG pointID);

    const RWDBDateTime& getTimeStamp() const;
    CtiTablePointDispatch& setTimeStamp(const RWDBDateTime& timestamp);

    UINT getQuality() const;
    CtiTablePointDispatch& setQuality(UINT quality);

    DOUBLE getValue() const;
    CtiTablePointDispatch& setValue(DOUBLE value);


    const RWDBDateTime& getNextArchiveTime() const;
    CtiTablePointDispatch& setNextArchiveTime(const RWDBDateTime& timestamp);

    UINT getTags() const;
    UINT setTags(UINT tags);
    UINT resetTags(UINT mask = 0xffffffff);

    UINT getStaleCount() const;
    CtiTablePointDispatch& setStaleCount(UINT tags);

    virtual void dump();

    CtiTablePointDispatch&  applyNewReading(const RWDBDateTime& timestamp,
                                            UINT quality,
                                            DOUBLE value,
                                            UINT tags,
                                            const RWDBDateTime& archivetime,
                                            UINT count );
};
#endif // #ifndef __TBL_PTPERSISTENCE_H__
