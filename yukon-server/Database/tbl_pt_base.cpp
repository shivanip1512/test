#include "precompiled.h"

#include "logger.h"
#include "resolvers.h"
#include "tbl_pt_base.h"

using std::string;
using std::endl;


string CtiTablePointBase::getSQLCoreStatement()
{
    static const string sql =  "SELECT PT.pointid, PT.pointname, PT.pointtype, PT.paobjectid, PT.stategroupid, "
                                   "PT.pointoffset, PT.serviceflag, PT.alarminhibit, PT.pseudoflag, PT.archivetype, "
                                   "PT.archiveinterval "
                               "FROM Point PT";

    return sql;
}

void CtiTablePointBase::DecodeDatabaseReader(Cti::RowReader &rdr)
{
   static const string pointid = "pointid";
   string   tmpStr;

   if(getDebugLevel() & DEBUGLEVEL_DATABASE)
   {
       CTILOG_DEBUG(dout, "Decoding DB read from Point");
   }

   rdr[pointid]         >> _pointID;
   rdr                  >> _name;

   rdr                  >> tmpStr;
   _type = (CtiPointType_t) resolvePointType(tmpStr);

   rdr                  >> _paObjectID;
   rdr                  >> _stateGroupID;
   rdr                  >> _pointOffset;

   rdr                  >> tmpStr;
   _outOfService = ciStringEqual(tmpStr, "y");

   rdr                  >> tmpStr;
   _alarmDisable = ciStringEqual(tmpStr, "y");

   rdr                  >> tmpStr;
   _pseudoPoint = !_pointOffset || ciStringEqual(tmpStr, "p")
                     || _type == CalculatedPointType
                     || _type == CalculatedStatusPointType;

   rdr                 >> tmpStr;
   _archiveType = resolvePointArchiveType(tmpStr);

   rdr                 >> _archiveInterval;

   setUpdatedFlag();
}

LONG CtiTablePointBase::getPointID() const
{
   return _pointID;
}

LONG CtiTablePointBase::getID() const
{
   return _pointID;
}

string CtiTablePointBase::getName() const
{
   return _name;
}

CtiPointType_t CtiTablePointBase::getType() const
{
   return _type;
}

LONG CtiTablePointBase::getPAObjectID() const
{
   return _paObjectID;
}

INT CtiTablePointBase::getPointOffset() const
{
   return _pointOffset;
}

LONG CtiTablePointBase::getStateGroupID() const
{
   return _stateGroupID;
}

BOOL CtiTablePointBase::isOutOfService() const
{
   return _outOfService;
}

BOOL CtiTablePointBase::isAlarmDisabled() const
{
   return _alarmDisable;
}

BOOL CtiTablePointBase::isPseudoPoint() const
{
   return _pseudoPoint;
}

INT CtiTablePointBase::getArchiveInterval() const
{
   return _archiveInterval;
}

INT CtiTablePointBase::getArchiveType() const
{
   return _archiveType;
}

void CtiTablePointBase::setType(CtiPointType_t t)
{
   _type = t;
}

CtiTablePointBase::CtiTablePointBase(LONG pid) :
    _pointID( pid ),
    _paObjectID(-1),
    _pointOffset(-1),
    _stateGroupID(-1),
    _flag(0),
    _archiveType(0),
    _archiveInterval(0)
{}

