/*-----------------------------------------------------------------------------*
*
* File:   pt_numeric
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/pt_numeric.h-arc  $
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2008/06/30 15:24:29 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PT_NUMERIC_H__
#define __PT_NUMERIC_H__
#pragma warning( disable : 4786)

#include "boost/shared_ptr.hpp"
using boost::shared_ptr;

#include <rw/db/reader.h>
#include <rw/db/nullind.h>

#include <limits.h>
#include "yukon.h"
#include "dlldefs.h"
#include "pt_base.h"
#include "resolvers.h"
#include "tbl_pt_unit.h"

class IM_EX_PNTDB CtiPointNumeric : public CtiPointBase
{
protected:
   CtiTablePointUnit    _pointUnits;

   INT                  _rateOfChange;

protected:

public:

   typedef CtiPointBase       Inherited;

   CtiPointNumeric();
   CtiPointNumeric(const CtiPointNumeric& aRef);
   virtual ~CtiPointNumeric();

   CtiPointNumeric& operator=(const CtiPointNumeric& aRef);
   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   static void getLimitSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   INT getRateOfChange() const;
   CtiPointNumeric setRateOfChange(INT rate);

   CtiTablePointUnit   getPointUnits() const;
   CtiTablePointUnit&  getPointUnits();

   virtual DOUBLE      getMultiplier() const;
   virtual DOUBLE      getDataOffset() const;

   virtual void        setMultiplier(DOUBLE d);
   virtual void        setDataOffset(DOUBLE d);

   virtual UINT getStaticTags();
   virtual UINT adjustStaticTags(UINT &tag) const;
   virtual double getDefaultValue( ) const;
   virtual double getInitialValue( ) const;

   virtual void DumpData();

   virtual DOUBLE computeValueForUOM(DOUBLE Value) const;

};

typedef CtiPointNumeric    CtiPointPseudoAnalog;
typedef CtiPointNumeric    CtiPointCalculated;

typedef shared_ptr< CtiPointNumeric > CtiPointNumericSPtr;

#endif // #ifndef __PT_NUMERIC_H__

