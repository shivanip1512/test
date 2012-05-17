#pragma once

#include "row_reader.h"
#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "desolvers.h"
#include "yukon.h"

#include "pointtypes.h"

class IM_EX_CTIYUKONDB CtiTablePointStatus : public CtiMemDBObject, boost::noncopyable
{
protected:
   /* Data Elements from Table PointStatus */

   LONG              _pointID;
   INT               _initialState;
   BOOL              _controlInhibit;
   CtiControlType_t  _controlType;
   INT               _controlOffset;
   INT               _closeTime1;
   INT               _closeTime2;
   std::string       *_stateZeroControl;
   std::string       *_stateOneControl;
   INT               _commandTimeout;

public:

   CtiTablePointStatus();
   virtual ~CtiTablePointStatus();

   LONG getPointID();
   INT getInitialState() const;
   BOOL getControlInhibit() const;
   CtiControlType_t getControlType()  const;
   INT getControlOffset() const;
   INT getCloseTime1() const;
   INT getCloseTime2() const;
   const std::string& getStateZeroControl() const;
   const std::string& getStateOneControl() const;
   INT getCommandTimeout() const;

   BOOL isControlInhibited() const;

   void setControlType(CtiControlType_t t);
   void setControlOffset(INT i);
   void setStateZeroControl(const std::string& zero);
   void setStateOneControl(const std::string& one);

   static std::string getTableName();

   void DecodeDatabaseReader(Cti::RowReader &rdr);
   void dump() const;

   UINT getStaticTags() const;
   UINT adjustStaticTags(UINT &tags) const;
};
