/*-----------------------------------------------------------------------------*
*
* File:   pt_numeric
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/pt_numeric.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2003/08/22 21:43:31 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PT_NUMERIC_H__
#define __PT_NUMERIC_H__
#pragma warning( disable : 4786)


#include <rw/db/reader.h>
#include <rw/db/nullind.h>

#include <limits.h>
#include "yukon.h"
#include "dlldefs.h"
#include "pt_base.h"
#include "resolvers.h"
#include "tbl_pt_unit.h"
#include "tbl_pt_limit.h"

#define  LIMIT_IN_RANGE    0
#define  LIMIT_EXCEEDS_LO  1
#define  LIMIT_EXCEEDS_HI  2
#define  LIMIT_SETUP_ERROR 3


class IM_EX_PNTDB CtiPointNumeric : public CtiPointBase
{
protected:


   CtiTablePointUnit    _pointUnits;

   INT                  _rateOfChange;

   // CtiTablePointLimits  _pointLimits[MAX_POINTLIMITS];

   /* Data Elements from Table PointLimits */
   BOOL                 _limitValid[MAX_POINTLIMITS];  // Is this limit defined?
   CtiTablePointLimit   _limit[MAX_POINTLIMITS];

   /* Data Elements from Table PointHISsettings */
   // History settings may be added here later

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
   void DecodeLimitsDatabaseReader(RWDBReader &rdr);
   BOOL  inLimitRange(int i) const;
   BOOL is_limitValid(int i) const;
   DOUBLE getHighLimit(int i)  const;
   DOUBLE getLowLimit(int i)  const;
   /*nukepao RWCString getLimitName(int i)  const;*/
   /*nukepao CtiFilter_t   getFilterType(int i)  const;*/
   INT getRateOfChange() const;
   CtiPointNumeric setRateOfChange(INT rate);

   CtiTablePointUnit   getPointUnits() const;
   CtiTablePointUnit&  getPointUnits();

   CtiTablePointLimit   getLimit(INT i) const;
   CtiTablePointLimit&  getLimit(INT i);

   // setters
   void set_limitValid(int i, BOOL b = TRUE);

   virtual DOUBLE      getMultiplier() const;
   virtual DOUBLE      getDataOffset() const;

   virtual void        setMultiplier(DOUBLE d);
   virtual void        setDataOffset(DOUBLE d);

   virtual bool limitStateCheck( const int limitOrState, double val, int &direction);
   virtual UINT getStaticTags();
   virtual UINT adjustStaticTags(UINT &tag) const;
   virtual double getDefaultValue( ) const;
   virtual double getInitialValue( ) const;

   virtual void DumpData();

   virtual DOUBLE computeValueForUOM(DOUBLE Value) const;

};

typedef CtiPointNumeric    CtiPointPseudoAnalog;
typedef CtiPointNumeric    CtiPointCalculated;


#endif // #ifndef __PT_NUMERIC_H__

