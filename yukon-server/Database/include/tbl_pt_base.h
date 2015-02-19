#pragma once

#include "row_reader.h"
#include <limits.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "pointdefs.h"
#include "pointtypes.h"


class IM_EX_CTIYUKONDB CtiTablePointBase : public CtiMemDBObject, private boost::noncopyable
{
   LONG              _pointID;          // ID of the point, system unique
   std::string       _name;             // _name of the point
   CtiPointType_t    _type;             // Point _type
   LONG              _paObjectID;         // 9/14/2001
   LONG              _stateGroupID;      // ID of the State _name entry which applies to this point
   INT               _pointOffset;
   INT               _archiveType;      // What type of archival do I use?
   INT               _archiveInterval;  // How many seconds between archive entries?

   union
   {
      UINT     _flag;
      struct
      {
         UINT _pseudoPoint     : 1;       // point is a pseudo point in the DB
         UINT _outOfService    : 1;       // Point is out of service in the DB
         UINT _alarmDisable    : 1;       // Point has alarm Disable set in the DB
      };
   };

public:

   CtiTablePointBase(LONG pid = -1);

   static std::string getSQLCoreStatement();

   void DecodeDatabaseReader(Cti::RowReader &rdr);

   INT  getArchiveInterval() const;
   LONG getPointID() const;
   LONG getID() const;
   LONG getPAObjectID() const;
   LONG getStateGroupID() const;

   BOOL isOutOfService() const;
   BOOL isAlarmDisabled() const;

   std::string    getName() const;
   CtiPointType_t getType() const;
   void setType(CtiPointType_t t);

   INT  getArchiveType() const;
   INT  getPointOffset() const;

   BOOL isPseudoPoint() const;

   UINT adjustStaticTags(UINT &tags) const;

   static const int MASK_POINT_BASE_TAGS;

};
