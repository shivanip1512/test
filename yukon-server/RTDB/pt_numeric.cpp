/*-----------------------------------------------------------------------------*
*
* File:   pt_numeric
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/pt_numeric.cpp-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2004/10/12 20:14:18 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include <windows.h>

#include "device.h"
#include "pt_numeric.h"
#include "tbl_pt_alarm.h"
#include "logger.h"

CtiPointNumeric::CtiPointNumeric() :
_rateOfChange(-1)
{
   for(int i = 0; i < MAX_POINTLIMITS; i++)
   {
      _limitValid[i] = FALSE;
   }
}

CtiPointNumeric::CtiPointNumeric(const CtiPointNumeric& aRef)
{
   *this = aRef;
}

CtiPointNumeric::~CtiPointNumeric()
{
}

CtiPointNumeric& CtiPointNumeric::operator=(const CtiPointNumeric& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);

      for(int i = 0; i < MAX_POINTLIMITS; i++)
      {
         if(aRef.is_limitValid(i))
         {
            _limitValid[i] = TRUE;
            _limit[i]      = aRef.getLimit(i);
         }
         else
         {
            _limitValid[i]  = FALSE;
         }
      }

      _pointUnits       = aRef.getPointUnits();
      _rateOfChange     = aRef.getRateOfChange();
   }

   return *this;
}

void CtiPointNumeric::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   Inherited::getSQL(db, keyTable, selector);       // get the base class handled
   CtiTablePointUnit::getSQL(db, keyTable, selector);
}

void CtiPointNumeric::getLimitSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   CtiTablePointLimit::getSQL(db, keyTable, selector);
}

void CtiPointNumeric::DecodeDatabaseReader(RWDBReader &rdr)
{
   Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
   _pointUnits.DecodeDatabaseReader(rdr);
}

void CtiPointNumeric::DecodeLimitsDatabaseReader(RWDBReader &rdr)
{
   INT iTemp;
   RWDBNullIndicator isNull;

   rdr["limitnumber"] >> isNull; // Used for verification of existance.
   rdr["limitnumber"] >> iTemp;

   if(!isNull)
   {
      if( inLimitRange(iTemp - 1))
      {
         _limitValid[iTemp - 1] = TRUE;
         _limit[iTemp - 1].DecodeDatabaseReader(rdr);
      }
   }
}

BOOL  CtiPointNumeric::inLimitRange(int i) const
{
   BOOL bRet = FALSE;

   if( 0 <= i && i < MAX_POINTLIMITS)
   {
      bRet = TRUE;
   }
   return bRet;
}

BOOL CtiPointNumeric::is_limitValid(int i) const
{
   BOOL bRet = FALSE;

   if(inLimitRange(i))
   {
      bRet = _limitValid[i];
   }
   return bRet;
}

DOUBLE CtiPointNumeric::getHighLimit(int i)  const
{
   DOUBLE Ret = DBL_MAX;

   try {
      if(is_limitValid(i))
      {
         Ret = _limit[i].getHighLimit();
      }
   }
   catch(...)
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }
   }
   return Ret;
}

DOUBLE CtiPointNumeric::getLowLimit(int i)  const
{
   DOUBLE Ret = -DBL_MAX;

   try {
      if(is_limitValid(i))
      {
         Ret = _limit[i].getLowLimit();
      }
   }
   catch(...)
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }
   }
   return Ret;
}

/*nukepao RWCString CtiPointNumeric::getLimitName(int i)  const
{
   RWCString Temp;

   if(is_limitValid(i))
   {
      Temp = _limit[i].getName();
   }
   return Temp;
}*/

/*nukepao CtiFilter_t   CtiPointNumeric::getFilterType(int i)  const
{
   CtiFilter_t Ret = InvalidFilter;

   if(is_limitValid(i))
   {
      Ret = _limit[i].getFilterType();
   }
   return Ret;
}*/

INT CtiPointNumeric::getRateOfChange() const
{
   return _rateOfChange;
}
CtiPointNumeric CtiPointNumeric::setRateOfChange(INT rate)
{
   if(0 <= rate && rate <= 100)
   {
      _rateOfChange = rate;
   }
   else
   {
      _rateOfChange = -1;
   }

   return *this;
}

CtiTablePointUnit   CtiPointNumeric::getPointUnits() const
{
   return _pointUnits;
}
CtiTablePointUnit&  CtiPointNumeric::getPointUnits()
{
   return _pointUnits;
}

CtiTablePointLimit   CtiPointNumeric::getLimit(INT i) const
{
   return _limit[i];
}
CtiTablePointLimit&  CtiPointNumeric::getLimit(INT i)
{
   return _limit[i];
}

// setters
void CtiPointNumeric::set_limitValid(int i, BOOL b)
{
   if(inLimitRange(i))
   {
      _limitValid[i] = b;
   }
   else
   {
      cout << "Limit is out of range " << __LINE__ << endl;
   }
}

void CtiPointNumeric::DumpData()
{
   Inherited::DumpData();       // get the base class handled

   _pointUnits.dump();

   for(int i = 0; i <  MAX_POINTLIMITS; i++)
   {
      if(_limitValid[i])
      {
         _limit[i].dump();
      }
   }
}

DOUBLE      CtiPointNumeric::getMultiplier() const
{
   return 1.0;
}    // Some nice defaults.
DOUBLE      CtiPointNumeric::getDataOffset() const
{
   return 0.0;
}    // Some nice defaults.

void        CtiPointNumeric::setMultiplier(DOUBLE d)
{
   return;
}
void        CtiPointNumeric::setDataOffset(DOUBLE d)
{
   return;
}

bool CtiPointNumeric::limitStateCheck( const int limitOrState, double val, int &direction)
{
   direction = LIMIT_IN_RANGE;          // This indicates that

   if(getLowLimit(limitOrState) >= getHighLimit(limitOrState))
   {
       direction = LIMIT_SETUP_ERROR;
   }
   else
   {
       if( val < getLowLimit(limitOrState) )
       {
          // Lo limit has been breached!
          direction = LIMIT_EXCEEDS_LO;
       }
       else if( getHighLimit(limitOrState) < val )
       {
          // Hi limit has been breached!
           direction = LIMIT_EXCEEDS_HI;
       }
   }


   return (direction != LIMIT_IN_RANGE);
}

UINT CtiPointNumeric::adjustStaticTags(UINT &tag) const
{
    return Inherited::adjustStaticTags(tag);
}

UINT CtiPointNumeric::getStaticTags()
{
   return Inherited::getStaticTags();
}

double CtiPointNumeric::getInitialValue( ) const
{
   return _pointUnits.getDefaultValue();
}

double CtiPointNumeric::getDefaultValue( ) const
{
   return _pointUnits.getDefaultValue();
}

DOUBLE CtiPointNumeric::computeValueForUOM(DOUBLE Value) const
{
    int calcType = getPointUnits().getUnitMeasure().getCalcType();

    switch( calcType )
    {
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Don't know about CalcType " << calcType << ".  Defaulting to CalcTypeNormal" << endl;
            }
            //  fall through
        }
        case CalcTypeNormal:
        {
            Value = (Value * getMultiplier()) + getDataOffset();
            break;
        }
        case CalcTypeVoltsFromV2H:
        {
            /* the base value is from V2H */
            Value = (DOUBLE) sqrt (fabs ((DOUBLE) (Value * getMultiplier())));
            break;
        }
    }

    return Value;
}

void CtiPointNumeric::invalidateLimits()
{
   for(int i = 0; i <  MAX_POINTLIMITS; i++)
   {
       set_limitValid(i,FALSE);
   }
}


