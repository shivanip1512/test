/*-----------------------------------------------------------------------------*
*
* File:   pt_numeric
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/pt_numeric.cpp-arc  $
* REVISION     :  $Revision: 1.19 $
* DATE         :  $Date: 2007/09/28 15:38:00 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>

#include "device.h"
#include "pt_numeric.h"
#include "tbl_pt_alarm.h"
#include "tbl_pt_unit.h"
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
    //if(isA(rdr))
    {
       Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
       _pointUnits.DecodeDatabaseReader(rdr);
    }
    /*else
    {
         {
             CtiLockGuard<CtiLogger> doubt_guard(dout);
             dout << CtiTime() << " " << getName() << " cannot decode this rdr " << __FILE__ << " (" << __LINE__ << ")" << endl;
         }
    }*/
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
         dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
         dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }
   }
   return Ret;
}

/*nukepao string CtiPointNumeric::getLimitName(int i)  const
{
   string Temp;

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
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - Limit is out of range (" << i << ") in point \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
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

// Takes a numeric alarm and the id of the limit and checks for exceeding the limits
// This currently assumes that you know the limitID, if not it could be changed to call
// GetNumericStateLimitFromHighLow
bool CtiPointNumeric::limitStateCheck( const int alarm, const int limitID, double val, int &direction)
{
   direction = LIMIT_IN_RANGE;          // This indicates that

   if(getLowLimit(limitID) >= getHighLimit(limitID))
   {
       direction = LIMIT_SETUP_ERROR;
   }
   else if( alarm >= CtiTablePointAlarming::limitLow0 && alarm <= CtiTablePointAlarming::limitHigh1 )
   {
       switch(alarm)
       {
           case CtiTablePointAlarming::limitLow0:
           case CtiTablePointAlarming::limitLow1:
           {
               if( val < getLowLimit(limitID) )
               {
                   direction = LIMIT_EXCEEDS_LO;
               }
               break;
           }
           case CtiTablePointAlarming::limitHigh0:
           case CtiTablePointAlarming::limitHigh1:
           {
               if( val > getHighLimit(limitID) )
               {
                   direction = LIMIT_EXCEEDS_HI;
               }
               break;
           }
       }
   }
   else
   {
       if( val < getLowLimit(limitID) )
       {
          // Lo limit has been breached!
          direction = LIMIT_EXCEEDS_LO;
       }
       else if( getHighLimit(limitID) < val )
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
    int digits   = _pointUnits.getDecimalDigits();

    switch( calcType )
    {
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

    if( digits > 0 )
    {
        Value = fmod(Value, pow(10,digits));
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


