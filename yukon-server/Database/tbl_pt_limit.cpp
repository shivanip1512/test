#include "precompiled.h"

#include "tbl_pt_limit.h"
#include "resolvers.h"
#include "logger.h"

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


void CtiTablePointLimit::getSQL(string &sql, LONG pointID, LONG paoID, const std::set<long> &pointIds)
{
    ostringstream sql_stream;

    sql_stream << "select pointid, limitnumber, highlimit, lowlimit, limitduration from pointlimits";

    if( pointID )
    {
        sql_stream << " where pointid = " << pointID;
    }
    else if( paoID )
    {
        sql_stream << " where pointid in (select pointid from point where paobjectid = " << paoID << ")";
    }
    else if( !pointIds.empty() )
    {
        sql_stream << " where pointid in (";
        sql_stream << Cti::join(pointIds, ",");
        sql_stream << ")";
    }

    sql = sql_stream.str();
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

