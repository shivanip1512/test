
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   pt_base
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/pt_base.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __PT_BASE_H__
#define __PT_BASE_H__

/*
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/pt_base.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
*/


#include <rw/db/reader.h>

#include <rw\rwtime.h>
#include <rw\cstring.h>

#include "dbmemobject.h"
#include "dlldefs.h"
#include "resolvers.h"
// #include "rtdb.h"

#include "pointdefs.h"
#include "pointtypes.h"
#include "pointtypes.h"
#include "pt_dyn_base.h"
#include "tbl_pt_base.h"
// #include "tbl_pt_alarm.h"
#include "yukon.h"

class CtiPointBase;     // Forward declaration...
class CtiTablePointAlarming;

void IM_EX_PNTDB DefDynamicFactory(const CtiPointBase& pt);

/*----------------------------------------------------------------------------------------*
 * CtiPointBase is the base class for all point objects in the YUKON system.
 *----------------------------------------------------------------------------------------*/
class IM_EX_PNTDB CtiPointBase : public CtiMemDBObject, public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
public:

   typedef void (*DynamicFactory)(const CtiPointBase& pt);

protected:

   CtiTablePointBase       _pointBase;
   DynamicFactory          _fpDynFactory;

   CtiDynamicPointBase     *_dynamic;
   CtiTablePointAlarming   *_alarming;

public:

   typedef CtiMemDBObject Inherited;

   CtiPointBase(LONG pid = -1);
   CtiPointBase(const CtiPointBase& aRef);

   virtual ~CtiPointBase();


   CtiPointBase& operator=(const CtiPointBase& aRef);

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   void DecodeAlarmingDatabaseReader(RWDBReader &rdr);

   virtual void DumpData();

   CtiTablePointBase  getPointBase() const;
   CtiTablePointBase& getPointBase();


   RWTime computeNextArchiveTime(const RWTime &Now) const;

   INT               getArchiveInterval() const;
   INT               getArchiveType() const;
   INT               getPointOffset() const;

   LONG              getPointID() const;
   LONG              getID() const;

   RWCString         getName() const;
   LONG              getDeviceID() const;


   RWCString         getLogicalGroup() const;
   LONG              getStateGroupID() const;

   BOOL              getDisableTag() const;
   BOOL              isInService() const;
   BOOL              isOutOfService() const;

   BOOL              getAlarmDisableTag() const;
   BOOL              isAlarmDisabled() const;

   BOOL              getPseudoTag() const;
   BOOL              isPseudoPoint() const;

   BOOL              getArchivePending() const;
   BOOL              isArchivePending() const;

   CtiPointType_t    getType() const;
   CtiPointType_t    isA() const;


   void setArchivePending(BOOL b = 1);
   void resetArchivePending(BOOL b = 0);

   CtiTablePointAlarming& getAlarming(bool refresh = false);

   CtiDynamicPointBase* getDynamic();

   CtiPointBase& setDynamic(CtiDynamicPointBase *pDyn);

   void           primeDynamicData();
   CtiDynamicPointBase* replicateDynamicData() const;


   DynamicFactory getDynamicFactory();
   DynamicFactory setDynamicFactory(DynamicFactory fpNew);


   virtual bool limitStateCheck( const int limitOrState, double &val, int &direction);

   virtual UINT getStaticTags();
   virtual UINT adjustStaticTags(UINT &tag) const;

   virtual bool isAbnormal( double value );

   virtual double getDefaultValue( ) const;

   virtual double getInitialValue( ) const;
   virtual int getControlExpirationTime() const;

   bool isNumeric() const;
   bool isStatus() const;
   bool hasAlarming() const;

   MutexType& getMux()  { return mutex();}
};

typedef CtiPointBase CtiPoint;

#endif // #ifndef __PT_BASE_H__
