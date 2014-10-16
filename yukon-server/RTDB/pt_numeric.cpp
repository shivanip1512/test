#include "precompiled.h"

#include "pt_numeric.h"
#include "tbl_pt_alarm.h"
#include "tbl_pt_unit.h"
#include "logger.h"

using std::string;
using std::endl;

CtiPointNumeric::CtiPointNumeric()
{
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
   Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
   _pointUnits.DecodeDatabaseReader(rdr);
}


const CtiTablePointUnit &CtiPointNumeric::getPointUnits() const
{
   return _pointUnits;
}

DOUBLE      CtiPointNumeric::getMultiplier() const
{
   return 1.0;
}    // Some nice defaults.
DOUBLE      CtiPointNumeric::getDataOffset() const
{
   return 0.0;
}    // Some nice defaults.

UINT CtiPointNumeric::adjustStaticTags(UINT &tag) const
{
    return Inherited::adjustStaticTags(tag);
}

DOUBLE CtiPointNumeric::computeValueForUOM(DOUBLE Value) const
{
    int calcType = getPointUnits().getUnitMeasure().getCalcType();
    int digits   = _pointUnits.getDecimalDigits();

    switch( calcType )
    {
        default:
        {
            CTILOG_WARN(dout, "Don't know about CalcType "<< calcType <<". Defaulting to CalcTypeNormal");

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

