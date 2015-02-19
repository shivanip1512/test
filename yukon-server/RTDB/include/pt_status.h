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


typedef boost::shared_ptr< CtiPointStatus > CtiPointStatusSPtr;
