#pragma once

#include <boost/shared_ptr.hpp>
#include "boostutil.h"
#include "dlldefs.h"
#include "pt_base.h"
#include "tbl_pt_status.h"

class IM_EX_PNTDB CtiPointStatus : public CtiPointBase
{
private:

   CtiTablePointStatus  _pointStatus;

   friend class Test_CtiPointStatus;

   typedef CtiPointBase Inherited;

public:

   const CtiTablePointStatus &getPointStatus() const;

   static std::string getSQLCoreStatement();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual UINT adjustStaticTags(UINT &tag) const;
   virtual UINT getStaticTags();
   virtual double getDefaultValue( ) const;
   virtual int getControlExpirationTime() const;
   virtual int getControlOffset() const;
};


struct IM_EX_PNTDB Test_CtiPointStatus : public CtiPointStatus
{
    void setPointOffset  ( int  offset   )  {  _pointBase.setPointOffset(offset);   }
    void setControlOffset( int offset    )  {  _pointStatus.setControlOffset(offset);   }
    void setID           ( long id       )  {  _pointBase.setID(id);                }
    void setDeviceID     ( long deviceid )  {  _pointBase.setPAObjectID(deviceid);  }

    void setControlType  ( CtiControlType_t type )  {  _pointStatus.setControlType(type);  }
};

typedef CtiPointStatus CtiPointCalculatedStatus;

typedef boost::shared_ptr< CtiPointStatus > CtiPointStatusSPtr;
