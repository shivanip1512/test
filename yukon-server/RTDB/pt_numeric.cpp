/*-----------------------------------------------------------------------------*
*
* File:   pt_numeric
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/pt_numeric.cpp-arc  $
* REVISION     :  $Revision: 1.23.2.2 $
* DATE         :  $Date: 2008/11/18 20:11:28 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "pt_numeric.h"
#include "tbl_pt_alarm.h"
#include "tbl_pt_unit.h"
#include "logger.h"

using std::string;
using std::endl;

CtiPointNumeric::CtiPointNumeric() :
_rateOfChange(-1)
{
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

      _pointUnits       = aRef.getPointUnits();
      _rateOfChange     = aRef.getRateOfChange();
   }

   return *this;
}

string CtiPointNumeric::getSQLCoreStatement()
{
    static const string sql =  "SELECT PT.pointid, PT.pointname, PT.pointtype, PT.paobjectid, PT.stategroupid, "
                                   "PT.pointoffset, PT.serviceflag, PT.alarminhibit, PT.pseudoflag, PT.archivetype, "
                                   "PT.archiveinterval, PNU.uomid, PNU.decimalplaces, PNU.decimaldigits, UTM.calctype "
                               "FROM Point PT, PointUnit PNU, UnitMeasure UTM "
                               "WHERE PT.pointid = PNU.pointid AND PNU.uomid = UTM.uomid";

    return sql;
}

void CtiPointNumeric::DecodeDatabaseReader(Cti::RowReader &rdr)
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

/*CtiTablePointLimit   CtiPointNumeric::getLimit(INT i) const
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
}*/

void CtiPointNumeric::DumpData()
{
   Inherited::DumpData();       // get the base class handled

   _pointUnits.dump();

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

UINT CtiPointNumeric::adjustStaticTags(UINT &tag) const
{
    return Inherited::adjustStaticTags(tag);
}

UINT CtiPointNumeric::getStaticTags()
{
   return Inherited::getStaticTags();
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
        Value = fmod(Value, pow(10.0,digits));
    }

    return Value;
}

