/*-----------------------------------------------------------------------------*
*
* File:   pt_base
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/pt_base.h-arc  $
* REVISION     :  $Revision: 1.17 $
* DATE         :  $Date: 2007/09/28 15:43:05 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PT_BASE_H__
#define __PT_BASE_H__
#pragma warning( disable : 4786)


#include <rw/db/reader.h>
#include "boost/shared_ptr.hpp"
using boost::shared_ptr;
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
#include "tbl_pt_trigger.h"
class CtiPointBase;     // Forward declaration...
class CtiTablePointAlarming;

void IM_EX_PNTDB DefDynamicFactory(const CtiPointBase& pt);

/*----------------------------------------------------------------------------------------*
 * CtiPointBase is the base class for all point objects in the YUKON system.
 *----------------------------------------------------------------------------------------*/
class IM_EX_PNTDB CtiPointBase : public CtiMemDBObject
{
public:

   typedef void (*DynamicFactory)(const CtiPointBase& pt);

protected:

   CtiTablePointBase       _pointBase;
   DynamicFactory          _fpDynFactory;

   CtiDynamicPointBase     *_dynamic;
   CtiTablePointAlarming   *_alarming;

   bool                    _triggerPoint;
   bool                    _verificationPoint;

public:

   typedef CtiMemDBObject Inherited;

   CtiPointBase(LONG pid = -1);
   CtiPointBase(const CtiPointBase& aRef);

   virtual ~CtiPointBase();


   CtiPointBase& operator=(const CtiPointBase& aRef);

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   void DecodeAlarmingDatabaseReader(RWDBReader &rdr);

   virtual void DumpData();

   INT             getArchiveInterval() const;
   INT             getArchiveType() const;
   INT             getPointOffset() const;

   LONG            getPointID() const;
   LONG            getID() const;

   string          getName() const;
   LONG            getDeviceID() const;

   //string          getLogicalGroup() const;
   LONG            getStateGroupID() const;

   BOOL            getDisableTag() const;
   BOOL            isInService() const;
   BOOL            isOutOfService() const;

   BOOL            getAlarmDisableTag() const;
   BOOL            isAlarmDisabled() const;

   BOOL            getPseudoTag() const;
   BOOL            isPseudoPoint() const;

   BOOL            getArchivePending() const;
   BOOL            isArchivePending() const;

   void            setIsTriggerPoint(bool isTrigPoint = true);
   void            setIsVerificationPoint(bool isTrigPoint = true);
   bool            isATriggerPoint(); //Only in Dispatch!!
   bool            isAVerificationPoint(); //Only in Dispatch!!

   void            setType(CtiPointType_t type);
   CtiPointType_t  getType() const;
   CtiPointType_t  isA() const;


   void setArchivePending(BOOL b = 1);
   void resetArchivePending(BOOL b = 0);

   CtiTablePointAlarming& getAlarming(bool refresh = false);

   CtiDynamicPointBase* getDynamic();

   CtiPointBase& setDynamic(CtiDynamicPointBase *pDyn);

   void           primeDynamicData();
   CtiDynamicPointBase* replicateDynamicData() const;


   DynamicFactory getDynamicFactory();
   DynamicFactory setDynamicFactory(DynamicFactory fpNew);


   virtual bool limitStateCheck( const int limitOrState, double val, int &direction);

   virtual UINT getStaticTags();
   virtual UINT adjustStaticTags(UINT &tag) const;

   virtual bool isAbnormal( double value );

   virtual double getDefaultValue( ) const;

   virtual double getInitialValue( ) const;
   virtual int getControlExpirationTime() const;

   bool isNumeric() const;
   bool isStatus() const;
   bool hasAlarming() const;
   bool isA(RWDBReader &rdr) const;

   virtual int getControlOffset() const { return 0; }
};

typedef CtiPointBase CtiPoint;

typedef shared_ptr< CtiPointBase > CtiPointSPtr;


#endif // #ifndef __PT_BASE_H__
