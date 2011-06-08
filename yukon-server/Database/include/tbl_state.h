
#pragma warning( disable : 4786)
#pragma once

#include <rw/thr/recursiv.h>
#include <rw\thr\mutex.h>

#include "row_reader.h"

#include "dlldefs.h"

class IM_EX_CTIYUKONDB CtiTableState
{

protected:

   LONG           _pointID;
   LONG           _stateGroupID;
   LONG           _rawState;
   std::string    _text;

private:
    mutable CtiMutex _classMutex;
public:

   CtiTableState(LONG id = -1, LONG raw = 0);
   CtiTableState(const CtiTableState& aRef);
   virtual ~CtiTableState();

   CtiTableState& operator=(const CtiTableState& aRef);
   LONG getStateGroupID() const;
   CtiTableState& setStateGroupID( const LONG id );

   LONG getRawState() const;
   CtiTableState& setRawState( const LONG id );

   const std::string& getText() const;
   CtiTableState& setText( const std::string &str );

   LONG getPointID();
   CtiTableState& setPointID( const LONG ptid );

   static std::string getTableName();
   virtual bool Restore();

   virtual void DecodeDatabaseReader(Cti::RowReader& rdr);

   bool operator<( const CtiTableState &rhs ) const;
   bool operator==( const CtiTableState &rhs ) const;
   bool operator()(const CtiTableState& aRef) const;

   void dump() const;
};
