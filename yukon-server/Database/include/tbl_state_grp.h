#pragma once

#include "dlldefs.h"
#include "mutex.h"
#include "tbl_state.h"
#include "loggable.h"

#include <map>


class IM_EX_CTIYUKONDB CtiTableStateGroup : public Cti::Loggable
{
public:

   typedef std::map< long, CtiTableState > CtiStateMap_t;

protected:

   LONG           _stateGroupID;
   std::string    _name;

   CtiStateMap_t  _stateMap;

public:

   CtiTableStateGroup(LONG id = -1);

   LONG getStateGroupID() const;
   const std::string& getName() const;

   std::string getRawState(LONG rawValue);

   bool Restore();

   void DecodeDatabaseReader(Cti::RowReader& rdr);

   std::string toString() const override;
};
