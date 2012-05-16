#pragma once

#include "dbmemobject.h"
#include "tbl_pt_base.h"

#include <boost/shared_ptr.hpp>
#include <boost/weak_ptr.hpp>

class IM_EX_PNTDB CtiPointBase : public CtiMemDBObject, boost::noncopyable
{
protected:

   CtiTablePointBase       _pointBase;

public:

   typedef CtiMemDBObject Inherited;

   CtiPointBase(LONG pid = -1);

   static std::string getSQLCoreStatement();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   void DecodeAttributeDatabaseReader(Cti::RowReader &rdr);

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

   void            setType(CtiPointType_t type);
   CtiPointType_t  getType() const;

   virtual UINT getStaticTags();
   virtual UINT adjustStaticTags(UINT &tag) const;

   virtual double getDefaultValue( ) const;

   virtual int getControlExpirationTime() const;

   bool isNumeric() const;
   bool isStatus() const;

   virtual int getControlOffset() const { return 0; }
};

typedef boost::shared_ptr< CtiPointBase > CtiPointSPtr;
typedef boost::weak_ptr< CtiPointBase > CtiPointWPtr;

