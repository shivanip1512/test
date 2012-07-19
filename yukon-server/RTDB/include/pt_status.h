#pragma once

#include <boost/shared_ptr.hpp>
#include "boostutil.h"
#include "dlldefs.h"
#include "pt_base.h"
#include "tbl_pt_status.h"
#include "tbl_pt_status_control.h"

#include <boost/optional.hpp>

class IM_EX_PNTDB CtiPointStatus : public CtiPointBase
{
protected:

   CtiTablePointStatus _pointStatus;
   boost::optional<CtiTablePointStatusControl> _pointStatusControl;

   typedef CtiPointBase Inherited;

public:

   const boost::optional<CtiTablePointStatusControl> getControlParameters() const;

   static std::string getSQLCoreStatement();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual UINT adjustStaticTags(UINT &tag) const;
   virtual double getDefaultValue( ) const;
};


struct IM_EX_PNTDB Test_CtiPointStatus : public CtiPointStatus
{
    void setPointOffset  ( int  offset   )  {  _pointBase.setPointOffset(offset);   }
    void setControlOffset( int offset    )  {  getPointStatusControl()->setControlOffset(offset);   }
    void setID           ( long id       )  {  _pointBase.setID(id);                }
    void setDeviceID     ( long deviceid )  {  _pointBase.setPAObjectID(deviceid);  }

    void setControlType  ( CtiControlType_t type )  {  getPointStatusControl()->setControlType(type);  }

    CtiTablePointStatusControl *getPointStatusControl()
    {
        if( ! _pointStatusControl )
        {
            _pointStatusControl = CtiTablePointStatusControl();
        }

        return &(*_pointStatusControl);
    }
};

typedef boost::shared_ptr< CtiPointStatus > CtiPointStatusSPtr;
