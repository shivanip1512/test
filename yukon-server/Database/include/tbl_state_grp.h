#pragma once

#include "rwutil.h"
#include "dlldefs.h"
#include "mutex.h"
#include "tbl_state.h"
#include "loggable.h"

#include <set>
#include <rw/thr/recursiv.h>
#include <rw\thr\mutex.h>


class IM_EX_CTIYUKONDB CtiTableStateGroup : public Cti::Loggable
{
public:

   typedef std::set< CtiTableState > CtiStateSet_t;

protected:

   LONG           _stateGroupID;
   std::string      _name;

   CtiMutex       _stateMux;
   CtiStateSet_t  _stateSet;

private:
    mutable CtiMutex _classMutex;
public:

   CtiTableStateGroup(LONG id = -1);
   CtiTableStateGroup(const CtiTableStateGroup& aRef);
   virtual ~CtiTableStateGroup();

   CtiTableStateGroup& operator=(const CtiTableStateGroup& aRef);
   LONG getStateGroupID() const;
   const std::string& getName() const;
   const CtiStateSet_t& getStateSet() const;

   CtiTableStateGroup& setStateGroupID( const LONG id );
   CtiTableStateGroup& setName( const std::string &str );
   CtiTableStateGroup& setStateSet( const CtiStateSet_t& aSet );

   std::string getRawState(LONG rawValue);

   static std::string getTableName();
   virtual bool Restore();

   virtual void DecodeDatabaseReader(Cti::RowReader& rdr);

   bool operator<( const CtiTableStateGroup &rhs ) const;
   bool operator==( const CtiTableStateGroup &rhs ) const;
   bool operator()(const CtiTableStateGroup& aRef) const;

   virtual std::string toString() const override;

};
