
/*-----------------------------------------------------------------------------*
*
* File:   tbl_rawpthistory
*
* Date:   4/27/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DBSIGNAL/INCLUDE/tbl_rawpthistory.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_RAWPTHISTORY_H__
#define __TBL_RAWPTHISTORY_H__

#include <windows.h>
#include <rw\cstring.h>
#include <rw\rwtime.h>
#include <rw/db/datetime.h>

#include "dlldefs.h"
#include "pointdefs.h"
#include "utility.h"
#include "yukon.h"

class IM_EX_SIGNAL CtiTableRawPointHistory
{
protected:

   LONG        _changeID;
   LONG        _pointID;
   RWTime      _time;
   INT         _quality;
   DOUBLE      _value;

private:

public:

   CtiTableRawPointHistory(LONG     pid            = 0L,
                           INT      qual           = NormalQuality,
                           DOUBLE   val            = 0.0,
                           const RWTime    &tme    = RWTime(),
                           LONG     cid            = ChangeIdGen()) :
      _changeID(cid),
      _pointID(pid),
      _quality(qual),
      _value(val),
      _time(tme)
   {
   }

   CtiTableRawPointHistory(const CtiTableRawPointHistory& aRef)
   {
      *this = aRef;
   }

   virtual ~CtiTableRawPointHistory() {}

   CtiTableRawPointHistory& operator=(const CtiTableRawPointHistory& aRef);
   virtual RWBoolean operator<(const CtiTableRawPointHistory& aRef) const;

   virtual void Insert();
   virtual void Insert(RWDBConnection &conn);
   virtual void Restore();
   void RestoreMax();
   virtual RWCString getTableName() const;

   virtual void DecodeDatabaseReader( RWDBReader& rdr );


   LONG                       getChangeID() const;
   LONG                       getPointID() const;
   RWTime                     getTime() const;
   INT                        getQuality() const;
   DOUBLE                     getValue() const;

   CtiTableRawPointHistory&   setChangeID(LONG id);
   CtiTableRawPointHistory&   setPointID(LONG id);
   CtiTableRawPointHistory&   setTime(const RWTime &rwt);
   CtiTableRawPointHistory&   setQuality(const INT &qual);
   CtiTableRawPointHistory&   setValue(const DOUBLE &val);
   CtiTableRawPointHistory&   setBookmark(const RWCString &mark);


};
#endif // #ifndef __TBL_RAWPTHISTORY_H__
