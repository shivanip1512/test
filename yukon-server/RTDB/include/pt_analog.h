#pragma once

#include <limits.h>
#include <float.h>

#include "dlldefs.h"
#include "pt_numeric.h"
#include "tbl_pt_analog.h"
#include "tbl_pt_control.h"
#include "logger.h"
#include "string_utility.h"

#include "boost/optional.hpp"

class IM_EX_PNTDB CtiPointAnalog : public CtiPointNumeric
{
private:

    CtiTablePointAnalog _pointAnalog;

    boost::optional<CtiTablePointControl> _pointControl;

    friend class Test_CtiPointAnalog;

public:

    typedef CtiPointNumeric Inherited;

    static std::string getSQLCoreStatement()
    {
        static const std::string sql =
            "SELECT"
                " PT.pointid, PT.pointname, PT.pointtype, PT.paobjectid, PT.stategroupid, PT.pointoffset,"
                " PT.serviceflag, PT.alarminhibit, PT.pseudoflag, PT.archivetype, PT.archiveinterval,"
                " UNT.uomid, UNT.decimalplaces, UNT.decimaldigits,"
                " UM.calctype,"
                " ALG.multiplier, ALG.dataoffset, ALG.deadband,"
				" PC.controloffset, PC.controlinhibit"
            " FROM"
                " Point PT "
                " JOIN PointUnit UNT on PT.pointid = UNT.pointid"
                " JOIN UnitMeasure UM on UNT.uomid = UM.uomid"
                " JOIN PointAnalog ALG on PT.pointid = ALG.pointid"
                " LEFT OUTER JOIN PointControl PC on ALG.pointid = PC.pointid";

        return sql;
    }

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr)
    {
        if( getDebugLevel() & DEBUGLEVEL_DATABASE )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Decoding " << FO(__FILE__) << " (" << __LINE__ << ")" << std::endl;
        }

        Inherited::DecodeDatabaseReader(rdr);
        _pointAnalog.DecodeDatabaseReader(rdr);

        if( ! rdr["controloffset"].isNull() )
        {
            _pointControl = CtiTablePointControl();

            _pointControl->DecodeDatabaseReader(rdr);
        }
        else
        {
            _pointControl.reset();
        }
    }

    UINT adjustStaticTags(UINT &tag) const
    {
        if( getType() == AnalogOutputPointType )
        {
            tag |= TAG_ATTRIB_CONTROL_AVAILABLE;
        }
        return Inherited::adjustStaticTags(tag);
    }

    const CtiTablePointControl *getControl() const
    {
        if( _pointControl )
        {
            return &(*_pointControl);
        }

        return 0;
    }

    double getDeadband() const  {  return _pointAnalog.getDeadband();  }

    virtual double getMultiplier() const  {  return _pointAnalog.getMultiplier();  }
    virtual double getDataOffset() const  {  return _pointAnalog.getDataOffset();  }
};


struct IM_EX_PNTDB Test_CtiPointAnalog : public CtiPointAnalog
{
    void setPointOffset( int  offset   )  {  _pointBase.setPointOffset(offset);   }
    void setID         ( long id       )  {  _pointBase.setID(id);                }
    void setDeviceID   ( long deviceid )  {  _pointBase.setPAObjectID(deviceid);  }
};

typedef shared_ptr< CtiPointAnalog > CtiPointAnalogSPtr;


