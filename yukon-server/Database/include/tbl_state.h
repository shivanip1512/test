#pragma once

#include "row_reader.h"
#include "loggable.h"
#include "dlldefs.h"
#include "mutex.h"

class IM_EX_CTIYUKONDB CtiTableState : public Cti::Loggable
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

   virtual std::string toString() const override;
};
