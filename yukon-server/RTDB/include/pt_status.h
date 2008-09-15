/*
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/pt_status.h-arc  $
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2008/09/15 17:59:18 $
*
*/
#ifndef __PT_STATUS_H__
#define __PT_STATUS_H__

#include <boost/shared_ptr.hpp>
#include "boostutil.h"
using boost::shared_ptr;

#include "dlldefs.h"
#include "pt_base.h"
#include "tbl_pt_status.h"
#include "yukon.h"

class IM_EX_PNTDB CtiPointStatus : public CtiPointBase
{
private:

   CtiTablePointStatus  _pointStatus;

   friend class Test_CtiPointStatus;

public:

   typedef     CtiPointBase    Inherited;

   CtiPointStatus();

   CtiPointStatus(const CtiPointStatus& aRef);
   CtiPointStatus& operator=(const CtiPointStatus& aRef);

   CtiTablePointStatus  getPointStatus() const;
   CtiTablePointStatus& getPointStatus();

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual void DumpData();
   virtual bool limitStateCheck( const int limitOrState, double val, int &direction);
   virtual UINT adjustStaticTags(UINT &tag) const;
   virtual UINT getStaticTags();
   virtual double getDefaultValue( ) const;
   virtual double getInitialValue( ) const;
   virtual int getControlExpirationTime() const;
   virtual int getControlOffset() const;
};


class IM_EX_PNTDB Test_CtiPointStatus : public CtiPointStatus
{
public:
    void setPointOffset  ( int  offset   )  {  _pointBase.setPointOffset(offset);   }
    void setControlOffset( int offset    )  {  _pointStatus.setControlOffset(offset);   }
    void setID           ( long id       )  {  _pointBase.setID(id);                }
    void setDeviceID     ( long deviceid )  {  _pointBase.setPAObjectID(deviceid);  }
};


typedef CtiPointStatus CtiPointCalculatedStatus;

typedef shared_ptr< CtiPointStatus > CtiPointStatusSPtr;


#endif // #ifndef __PT_STATUS_H__

