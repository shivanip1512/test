
#pragma warning( disable : 4786)
#ifndef __TBL_LM_CONTROLHIST_H__
#define __TBL_LM_CONTROLHIST_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_lm_controlhist
*
* Class:  CtiTableLMControlHistory
* Date:   9/24/2001
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_lm_controlhist.h-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2004/02/16 20:57:28 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/db/reader.h>
#include <rw\cstring.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dbmemobject.h"
#include "dbaccess.h"
#include "yukon.h"
#include "dbmemobject.h"
#include "utility.h"

class IM_EX_CTIYUKONDB CtiTableLMControlHistory: public CtiMemDBObject
{
protected:

   LONG        _lmControlHistID;
   LONG        _paoID;
   RWTime      _startDateTime;          // represents the time at which control was begun
   RWTime      _controlCompleteTime;    // represents the time at which control was last sent
   RWTime      _stopDateTime;           // represents the time at which the current log interval completed.
   LONG        _soeTag;
   INT         _controlDuration;
   RWCString   _controlType;
   LONG        _currentDailyTime;
   LONG        _currentMonthlyTime;
   LONG        _currentSeasonalTime;
   LONG        _currentAnnualTime;
   mutable RWCString   _activeRestore;
   DOUBLE      _reductionValue;

   // Values below are note stored in the DB.
   RWCString   _defaultActiveRestore;
   RWTime      _prevLogTime;            // Not stored, but used to determine relative positions of controls
   RWTime      _prevStopReportTime;
   int         _reductionRatio;         // Needed to compute the contribution of cycles


private:

    bool _isNewControl;
    static CtiMutex    _soeMux;

public:

    typedef CtiMemDBObject Inherited;

   //CtiTableLMControlHistory();

   CtiTableLMControlHistory(LONG             paoid   = 0,
                            const RWTime&    start   = RWTime(),
                            LONG             soe     = 0,
                            INT              dur     = 0,
                            const RWCString& type    = RWCString("Unavailable"),
                            LONG             daily   = 0,
                            LONG             month   = 0,
                            LONG             season  = 0,
                            LONG             annual  = 0,
                            const RWCString& restore = RWCString(LMAR_NEWCONTROL),
                            DOUBLE           reduce  = 0.0,
                            LONG             lmchid  = 0L);

   CtiTableLMControlHistory(const CtiTableLMControlHistory& aRef);

   virtual ~CtiTableLMControlHistory();

   CtiTableLMControlHistory& operator=(const CtiTableLMControlHistory& aRef);
   bool operator<(const CtiTableLMControlHistory& aRef) const;

   LONG getLMControlHistoryID() const;
   CtiTableLMControlHistory& setLMControlHistoryID( const LONG lmHID );

   LONG getPAOID() const;
   CtiTableLMControlHistory& setPAOID( const LONG paoID );

   const RWTime& getStartTime() const;
   CtiTableLMControlHistory& setStartTime( const RWTime& st );

   const RWTime& getControlCompleteTime() const;
   CtiTableLMControlHistory& setControlCompleteTime( const RWTime& cst );

   const RWTime& getStopTime() const;
   CtiTableLMControlHistory& setStopTime( const RWTime& st );

   const RWTime& getPreviousLogTime() const;
   CtiTableLMControlHistory& setPreviousLogTime( const RWTime& st );

   const RWTime& getPreviousStopReportTime() const;
   CtiTableLMControlHistory& setPreviousStopReportTime( const RWTime& st );

   LONG getSoeTag() const;
   CtiTableLMControlHistory& setSoeTag( const LONG soe );

   INT getControlDuration() const;
   CtiTableLMControlHistory& setControlDuration( const INT cd );

   const RWCString& getControlType() const;
   CtiTableLMControlHistory& setControlType( const RWCString& ct );

   LONG getCurrentDailyTime() const;
   CtiTableLMControlHistory& setCurrentDailyTime( const LONG dt );

   LONG getCurrentMonthlyTime() const;
   CtiTableLMControlHistory& setCurrentMonthlyTime( const LONG mt );

   LONG getCurrentSeasonalTime() const;
   CtiTableLMControlHistory& setCurrentSeasonalTime( const LONG seat );

   LONG getCurrentAnnualTime() const;
   CtiTableLMControlHistory& setCurrentAnnualTime( const LONG at );

   const RWCString& getActiveRestore() const;
   CtiTableLMControlHistory& setActiveRestore( const RWCString& ar );

   const RWCString& getDefaultActiveRestore() const;
   CtiTableLMControlHistory& setDefaultActiveRestore( const RWCString& ar );

   DOUBLE getReductionValue() const;
   CtiTableLMControlHistory& setReductionValue( const DOUBLE rv );

   int getReductionRatio() const;
   CtiTableLMControlHistory& setReductionRatio( int redrat );

   CtiTableLMControlHistory& incrementTimes( const RWTime &now, const LONG increment, bool season_reset = false );

   bool isNewControl() const;
   CtiTableLMControlHistory& setNotNewControl( );

   static LONG getNextSOE();
   static RWCString getTableName();

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   RWDBStatus RestoreControlTimes();
   void DecodeControlTimes(RWDBReader &rdr);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual RWDBStatus Restore();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Insert(RWDBConnection &conn);
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_LM_CONTROLHIST_H__
