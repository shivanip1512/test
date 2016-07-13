#include "precompiled.h"

#include "tbl_pt_limit.h"
#include "resolvers.h"
#include "logger.h"
#include "database_util.h"

using namespace std;

CtiTablePointLimit&   CtiTablePointLimit::operator=(const CtiTablePointLimit& aRef)
{
   if(this != &aRef)
   {
      _pointID       = aRef.getPointID();
      _limitNumber   = aRef.getLimitNumber();
      _highLimit     = aRef.getHighLimit();
      _lowLimit      = aRef.getLowLimit();
      _limitDuration = aRef.getLimitDuration();
   }

   return *this;
}

INT    CtiTablePointLimit::getLimitNumber()   const  {  return _limitNumber;    }
LONG   CtiTablePointLimit::getPointID()       const  {  return _pointID;        }
DOUBLE CtiTablePointLimit::getHighLimit()     const  {  return _highLimit;      }
DOUBLE CtiTablePointLimit::getLowLimit()      const  {  return _lowLimit;       }
INT    CtiTablePointLimit::getLimitDuration() const  {  return _limitDuration;  }


std::string CtiTablePointLimit::getSqlForFullLoad()
{
    return
        "SELECT "
            "PL.POINTID, "
            "PL.LIMITNUMBER, "
            "PL.HIGHLIMIT, "
            "PL.LOWLIMIT, "
            "PL.LIMITDURATION "
        "FROM "
            "POINTLIMITS PL";
}

std::string CtiTablePointLimit::getSqlForPointId()
{
    return
        getSqlForFullLoad()
            + " WHERE "
            + Cti::Database::createIdEqualClause( "PL", "POINTID" );
}

std::string CtiTablePointLimit::getSqlForPaoId()
{
    return
        getSqlForFullLoad()
            + " JOIN POINT P ON PL.POINTID = P.POINTID"
            + " WHERE "
            + Cti::Database::createIdEqualClause( "P", "PAObjectID" );
}

std::string CtiTablePointLimit::getSqlForPointIds(const size_t count)
{
    return
        getSqlForFullLoad()
            + " WHERE "
            + Cti::Database::createIdInClause( "PL", "POINTID", count );
}

std::string CtiTablePointLimit::toString() const
{
    Cti::FormattedList itemList;

    itemList.add("PointID")            << _pointID;
    itemList.add("Limit Number Value") << _limitNumber;
    itemList.add("Hi Limit Value")     << _highLimit;
    itemList.add("Lo Limt Value")      << _lowLimit;
    itemList.add("Limit Duration")     << _limitDuration;

    return itemList.toString();
}


CtiTablePointLimit::CtiTablePointLimit(long pointid, int limitnumber) :
   _pointID(pointid),
   _limitNumber(limitnumber),
   _limitDuration(-1),
   _highLimit(DBL_MAX),
   _lowLimit(DBL_MIN)
{}

CtiTablePointLimit::CtiTablePointLimit(Cti::RowReader &rdr)
{
   rdr["pointid"] >> _pointID;
   rdr >> _limitNumber;
   rdr >> _highLimit;
   rdr >> _lowLimit;
   rdr >> _limitDuration;
}

CtiTablePointLimit::CtiTablePointLimit(const CtiTablePointLimit& aRef)
{
   *this = aRef;
}

CtiTablePointLimit::~CtiTablePointLimit() {}

string CtiTablePointLimit::getTableName()  {  return "PointLimits";  }

bool CtiTablePointLimit::operator<(const CtiTablePointLimit &rhs) const
{
    return (_pointID < rhs.getPointID() || (_pointID == rhs.getPointID() && _limitNumber < rhs.getLimitNumber()));
}

bool CtiTablePointLimit::operator==(const CtiTablePointLimit &rhs) const
{
    return _pointID == rhs._pointID && _limitNumber == rhs._limitNumber;
}

