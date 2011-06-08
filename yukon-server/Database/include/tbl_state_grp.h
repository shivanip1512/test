
#pragma warning( disable : 4786)
#ifndef __TBL_STATE_GRP_H__
#define __TBL_STATE_GRP_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_state_grp
*
* Class:  CtiTableStateGroup
* Date:   12/29/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_state_grp.h-arc  $
* REVISION     :  $Revision: 1.4.24.1 $
* DATE         :  $Date: 2008/11/18 20:11:29 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <set>
#include <rw/thr/recursiv.h>
#include <rw\thr\mutex.h>

#include "rwutil.h"


#include "dlldefs.h"
#include "mutex.h"
#include "tbl_state.h"


class IM_EX_CTIYUKONDB CtiTableStateGroup
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

   void dump() const;

};
#endif // #ifndef __TBL_STATE_GRP_H__
