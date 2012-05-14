#pragma once

#include "yukon.h"
#include "row_reader.h"
#include "boostutil.h"
#include "boost/weak_ptr.hpp"
#include "dbmemobject.h"
#include "dlldefs.h"
#include "resolvers.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "pointtypes.h"
#include "tbl_pt_base.h"
#include "tbl_pt_property.h"
#include "tbl_pt_trigger.h"

#include <boost/shared_ptr.hpp>

class CtiTablePointAlarming;

class IM_EX_PNTDB CtiPointBase : public CtiMemDBObject, boost::noncopyable
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

   std::string     getName() const;
   LONG            getDeviceID() const;

   LONG            getStateGroupID() const;

   BOOL            isAlarmDisabled() const;

   BOOL            isPseudoPoint() const;

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

typedef boost::shared_ptr< CtiPointBase > CtiPointSPtr;
typedef boost::weak_ptr< CtiPointBase > CtiPointWPtr;

