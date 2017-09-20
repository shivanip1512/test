#pragma once

#include "dbmemobject.h"
#include "tbl_pt_base.h"

#include <boost/shared_ptr.hpp>
#include <boost/weak_ptr.hpp>

class IM_EX_PNTDB CtiPointBase : public CtiMemDBObject, boost::noncopyable
{
   CtiTablePointBase _pointBase;

public:

   typedef CtiMemDBObject Inherited;

   CtiPointBase(LONG pid = -1);

   static std::string getSQLCoreStatement();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   INT             getArchiveInterval() const;
   INT             getArchiveType() const;
   INT             getPointOffset() const;

   LONG            getPointID() const;
   LONG            getID() const;

   std::string     getName() const;
   LONG            getDeviceID() const;

   LONG            getStateGroupID() const;

   bool            isAlarmDisabled() const;
   bool            isOutOfService() const;
   bool            isPseudoPoint() const;

   void            setType(CtiPointType_t type);
   CtiPointType_t  getType() const;

   static unsigned makeStaticTags(bool isPseudoPoint, bool isOutOfService, bool isAlarmDisabled);
   virtual unsigned adjustStaticTags(unsigned& tag) const;

   virtual double getDefaultValue( ) const;

   bool isNumeric() const;
   bool isStatus() const;

   static bool isNumeric(CtiPointType_t type);
   static bool isStatus (CtiPointType_t type);
};

typedef boost::shared_ptr< CtiPointBase > CtiPointSPtr;
typedef boost::weak_ptr< CtiPointBase > CtiPointWPtr;

