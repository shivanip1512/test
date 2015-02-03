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
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTablePointBase(const CtiTablePointBase&);
    CtiTablePointBase& operator=(const CtiTablePointBase&);

protected:

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
   void setPointID(LONG id);

   LONG getID() const;
   void setID(LONG id);

   LONG getPAObjectID() const;
   void setPAObjectID(LONG id);

   LONG getStateGroupID() const;

   BOOL getDisableTag() const;

   BOOL isOutOfService() const;

   BOOL getAlarmDisableTag() const;
   BOOL isAlarmDisabled() const;

   BOOL getPseudoTag() const;

   std::string getName() const;
   void setName( std::string name );

   CtiPointType_t getType() const;
   void setType(CtiPointType_t t);

   INT  getArchiveType() const;

   INT  getPointOffset() const;
   void setPointOffset(INT i);


   BOOL isPseudoPoint() const;

   UINT adjustStaticTags(UINT &tags) const;

   static const int MASK_POINT_BASE_TAGS;

};
