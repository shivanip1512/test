#ifndef __PT_STATUS_H__
#define __PT_STATUS_H__
/*
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/pt_status.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2002/06/10 20:46:55 $
*
*/

#include <rw\cstring.h>
#include "dlldefs.h"
#include "pt_base.h"
#include "tbl_pt_status.h"
#include "yukon.h"

class IM_EX_PNTDB CtiPointStatus : public CtiPointBase
{
private:

   CtiTablePointStatus  _pointStatus;

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

};
#endif // #ifndef __PT_STATUS_H__

