#pragma once

#include "row_reader.h"

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "desolvers.h"
#include "yukon.h"

#include "pointtypes.h"

#include <boost/optional.hpp>

class IM_EX_CTIYUKONDB CtiTablePointStatus : public CtiMemDBObject, boost::noncopyable
{
protected:

   LONG              _pointID;
   INT               _initialState;
   BOOL              _controlInhibit;
   CtiControlType_t  _controlType;
   INT               _controlOffset;
   INT               _closeTime1;
   INT               _closeTime2;
   boost::optional<std::string> _stateZeroControl;
   boost::optional<std::string> _stateOneControl;
   INT               _commandTimeout;

   void setStateZeroControl(const std::string& zero);
   void setStateOneControl(const std::string& one);

public:

   CtiTablePointStatus();

   LONG getPointID()        const  {  return _pointID;  }
   INT getInitialState()    const  {  return _initialState;  }
   BOOL getControlInhibit() const  {  return _controlInhibit;  }
   CtiControlType_t getControlType() const  {  return _controlType;  }
   INT getControlOffset()   const  {  return _controlOffset;  }
   INT getCloseTime1()      const  {  return _closeTime1;  }
   INT getCloseTime2()      const  {  return _closeTime2;  }
   const std::string& getStateZeroControl() const;
   const std::string& getStateOneControl() const;
   INT getCommandTimeout()  const  {  return _commandTimeout;  }

   BOOL isControlInhibited() const;

   void setControlType(CtiControlType_t t);
   void setControlOffset(INT i);

   void DecodeDatabaseReader(Cti::RowReader &rdr);

   UINT getStaticTags() const;
   UINT adjustStaticTags(UINT &tags) const;
};
