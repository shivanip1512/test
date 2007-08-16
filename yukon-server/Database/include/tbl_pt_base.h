
#pragma warning( disable : 4786)
#ifndef __TBL_PT_BASE_H__
#define __TBL_PT_BASE_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_base
*
* Class:  CtiTablePointBase
* Date:   8/14/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_pt_base.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2007/08/16 20:52:13 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/datetime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "desolvers.h"
#include "yukon.h"
#include "pointdefs.h"
#include "pointtypes.h"


class IM_EX_CTIYUKONDB CtiTablePointBase : public CtiMemDBObject
{

protected:

   LONG              _pointID;          // ID of the point, system unique
   string         _name;             // _name of the point
   CtiPointType_t    _type;             // Point _type
   LONG              _paObjectID;         // 9/14/2001
   string         _logicalGroup;     // Used primarily for reporting.
   LONG              _stateGroupID;      // ID of the State _name entry which applies to this point
   INT               _pointOffset;
   INT               _archiveType;      // What type of archival do I use?
   INT               _archiveInterval;  // How many seconds between archive entries?

   union
   {
      UINT     _flag;
      struct
      {
         UINT _archivePending  : 1;       // If set, the next point read causes an archive.
         UINT _pseudoPoint     : 1;       // point is a pseudo point in the DB
         UINT _outOfService    : 1;       // Point is out of service in the DB
         UINT _alarmDisable    : 1;       // Point has alarm Disable set in the DB
      };
   };


private:

public:

   CtiTablePointBase(LONG pid = -1);
   CtiTablePointBase(const CtiTablePointBase& aRef);
   virtual ~CtiTablePointBase();

   CtiTablePointBase& operator=(const CtiTablePointBase& aRef);

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   void DecodeDatabaseReader(RWDBReader &rdr);
   void dump();

   INT  getArchiveInterval() const;
   CtiTablePointBase&  setArchiveInterval(const INT aI);

   LONG getPointID() const;
   CtiTablePointBase& setPointID(LONG id);

   LONG getID() const;
   CtiTablePointBase& setID(LONG id);

   LONG getPAObjectID() const;
   CtiTablePointBase& setPAObjectID(LONG id);

   LONG getStateGroupID() const;
   CtiTablePointBase& setStateGroupID(LONG id);

   BOOL getDisableTag() const;
   CtiTablePointBase& setDisableTag(BOOL b = 1);
   CtiTablePointBase& resetDisableTag(BOOL b = 0);

   BOOL isInService() const;
   BOOL isOutOfService() const;

   BOOL getAlarmDisableTag() const;
   CtiTablePointBase& setAlarmDisableTag(BOOL b = 1);
   CtiTablePointBase& resetAlarmDisableTag(BOOL b = 0);
   BOOL isAlarmDisabled() const;

   BOOL getPseudoTag() const;
   CtiTablePointBase& setPseudoTag(BOOL b = 1);
   CtiTablePointBase& resetPseudoTag(BOOL b = 0);

   BOOL getArchivePending() const;
   CtiTablePointBase& setArchivePending(BOOL b = 1);
   CtiTablePointBase& resetArchivePending(BOOL b = 0);
   BOOL isArchivePending() const;

   string getName() const;
   CtiTablePointBase& setName(string str);

   string getLogicalGroup() const;
   CtiTablePointBase& setLogicalGroup(string str);

   CtiPointType_t getType() const;
   CtiTablePointBase& setType(CtiPointType_t t);

   INT  getArchiveType() const;
   CtiTablePointBase&  setArchiveType(const INT aT);

   INT  getPointOffset() const;
   CtiTablePointBase setPointOffset(INT i);


   BOOL isPseudoPoint() const;

   CtiPointType_t isA() const;

   UINT getStaticTags() const;
   UINT adjustStaticTags(UINT &tags) const;

   static const int MASK_POINT_BASE_TAGS;

};
#endif // #ifndef __TBL_PT_BASE_H__
