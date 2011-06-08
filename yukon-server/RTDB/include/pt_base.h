/*-----------------------------------------------------------------------------*
*
* File:   pt_base
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/pt_base.h-arc  $
* REVISION     :  $Revision: 1.26 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PT_BASE_H__
#define __PT_BASE_H__
#pragma warning( disable : 4786)


#include "row_reader.h"
#include <boost/shared_ptr.hpp>
#include "boostutil.h"
#include "boost/weak_ptr.hpp"
#include "dbmemobject.h"
#include "dlldefs.h"
#include "resolvers.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "pointtypes.h"
#include "pt_dyn_base.h"
#include "tbl_pt_base.h"
#include "tbl_pt_property.h"
#include "yukon.h"
#include "tbl_pt_trigger.h"

class CtiTablePointAlarming;

/*----------------------------------------------------------------------------------------*
 * CtiPointBase is the base class for all point objects in the YUKON system.
 *----------------------------------------------------------------------------------------*/
class IM_EX_PNTDB CtiPointBase : public CtiMemDBObject
{
public:

   typedef void (*DynamicFactory)(const CtiPointBase& pt);

protected:

   CtiTablePointBase       _pointBase;

public:

   typedef CtiMemDBObject Inherited;

   CtiPointBase(LONG pid = -1);
   CtiPointBase(const CtiPointBase& aRef);

   virtual ~CtiPointBase();


   CtiPointBase& operator=(const CtiPointBase& aRef);

   static std::string getSQLCoreStatement();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   void DecodeAttributeDatabaseReader(Cti::RowReader &rdr);

   virtual void DumpData();

   INT             getArchiveInterval() const;
   INT             getArchiveType() const;
   INT             getPointOffset() const;

   LONG            getPointID() const;
   LONG            getID() const;

   std::string          getName() const;
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

   void            setType(CtiPointType_t type);
   CtiPointType_t  getType() const;
   CtiPointType_t  isA() const;


   void setArchivePending(BOOL b = 1);
   void resetArchivePending(BOOL b = 0);

   virtual UINT getStaticTags();
   virtual UINT adjustStaticTags(UINT &tag) const;

   virtual bool isAbnormal( double value );

   virtual double getDefaultValue( ) const;

   virtual double getInitialValue( ) const;
   virtual int getControlExpirationTime() const;

   bool isNumeric() const;
   bool isStatus() const;
   bool isA(Cti::RowReader &rdr) const;

   virtual int getControlOffset() const { return 0; }
};

void IM_EX_PNTDB DefDynamicFactory(const CtiPointBase& pt);

typedef CtiPointBase CtiPoint;

typedef boost::shared_ptr< CtiPointBase > CtiPointSPtr;
typedef boost::weak_ptr< CtiPointBase > CtiPointWPtr;


#endif // #ifndef __PT_BASE_H__
