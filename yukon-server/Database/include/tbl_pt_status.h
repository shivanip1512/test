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

class IM_EX_CTIYUKONDB CtiTablePointStatus : public CtiMemDBObject
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

private:

public:

   CtiTablePointStatus();
   CtiTablePointStatus(const CtiTablePointStatus& aRef);
   virtual ~CtiTablePointStatus();

   CtiTablePointStatus& operator=(const CtiTablePointStatus& aRef);

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

   CtiTablePointStatus& setPointID( const LONG ptid );
   CtiTablePointStatus& setInitialState(INT i);
   CtiTablePointStatus& setControlInhibit(const BOOL b = TRUE);
   CtiTablePointStatus& setControlType(CtiControlType_t t);
   CtiTablePointStatus& setControlOffset(INT i);
   CtiTablePointStatus& setCloseTime1(INT i);
   CtiTablePointStatus& setCloseTime2(INT i);
   CtiTablePointStatus& setStateZeroControl(const std::string& zero);
   CtiTablePointStatus& setStateOneControl(const std::string& one);
   CtiTablePointStatus& setCommandTimeout(INT i);

   static std::string getTableName();

   void DecodeDatabaseReader(Cti::RowReader &rdr);
   void dump() const;

   UINT getStaticTags() const;
   UINT adjustStaticTags(UINT &tags) const;
};
