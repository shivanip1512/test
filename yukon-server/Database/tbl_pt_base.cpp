/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_base
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_base.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/06/15 23:56:34 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"
#include <windows.h>

#include "logger.h"
#include "resolvers.h"
#include "tbl_pt_base.h"

CtiTablePointBase& CtiTablePointBase::operator=(const CtiTablePointBase& aRef)
{
   if(this != &aRef)
   {
      _pointID           = aRef.getID();
      _name              = aRef.getName();
      _type              = aRef.getType();
      _pointOffset       = aRef.getPointOffset();
      _paObjectID        = aRef.getPAObjectID();
      _logicalGroup      = aRef.getLogicalGroup();
      _stateGroupID      = aRef.getStateGroupID();
      _archiveInterval   = aRef.getArchiveInterval();
      _archiveType       = aRef.getArchiveType();
      _archivePending    = aRef.isArchivePending();
      _pseudoPoint       = aRef.isPseudoPoint();
      _outOfService      = aRef.isOutOfService();
      _alarmDisable      = aRef.isAlarmDisabled();
   }
   return *this;
}

void CtiTablePointBase::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   keyTable = db.table("Point");

   selector <<
      keyTable["pointid"] <<
      keyTable["pointname"] <<
      keyTable["pointtype"] <<
      keyTable["paobjectid"] <<
      keyTable["logicalgroup"] <<
      keyTable["stategroupid"] <<
      keyTable["pointoffset"] <<
      keyTable["serviceflag"] <<
      keyTable["alarminhibit"] <<
      keyTable["pseudoflag"] <<
      keyTable["archivetype"] <<
      keyTable["archiveinterval"];

   selector.from(keyTable);
}

void CtiTablePointBase::DecodeDatabaseReader(RWDBReader &rdr)
{
   RWCString   rwsTemp;

   if(getDebugLevel() & DEBUGLEVEL_DATABASE) 
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   rdr["pointid"]       >> _pointID;
   rdr["pointname"]     >> _name;

   rdr["pointtype"]     >> rwsTemp;
   _type = (CtiPointType_t) resolvePointType(rwsTemp);

   rdr["paobjectid"]    >> _paObjectID;
   rdr["logicalgroup"]  >> _logicalGroup;
   rdr["stategroupid"]  >> _stateGroupID;
   rdr["pointoffset"]   >> _pointOffset;

   rdr["serviceflag"]   >> rwsTemp;
   rwsTemp.toLower();
   //ServiceFlag = ((rwsTemp == "y") ? TRUE : FALSE);
   setDisableTag(((rwsTemp == "y") ? TRUE : FALSE));

   rdr["alarminhibit"]  >> rwsTemp;
   rwsTemp.toLower();
   // AlarmInhibit = ((rwsTemp == "y") ? TRUE : FALSE);
   setAlarmDisableTag(((rwsTemp == "y") ? TRUE : FALSE));


   rdr["pseudoflag"]    >> rwsTemp;
   rwsTemp.toLower();
   // PseudoFlag = ((rwsTemp == "y") ? TRUE : FALSE);
   setPseudoTag(((rwsTemp == "p") ? TRUE : FALSE));

   if(!_pointOffset)
   {
       setPseudoTag(TRUE);
   }

   rdr["archivetype"]   >> rwsTemp;
   _archiveType = resolvePointArchiveType(rwsTemp);

   rdr["archiveinterval"] >> _archiveInterval;

   setUpdatedFlag();
}

void CtiTablePointBase::dump()
{
   RWCString   rwsTemp;

   CtiLockGuard<CtiLogger> doubt_guard(dout);
   dout << " PointID                                  : " << _pointID << endl;
   dout << " PointName                                : " << _name << endl;
   dout << " PointType                                : " << _type << endl;
   dout << " Point Offset                             : " << _pointOffset << endl;
   dout << " PAObjectID                               : " << _paObjectID << endl;
   dout << " Logical Group ID                         : " << _logicalGroup << endl;
   dout << " State Name ID                            : " << _stateGroupID << endl;
   dout << " Out of Service                           : " << getDisableTag() << endl;
   dout << " Alarm Inhibit                            : " << getAlarmDisableTag() << endl;
   dout << " Pseudo Point                             : " << getPseudoTag() << endl;
}

LONG CtiTablePointBase::getPointID() const
{
   return _pointID;
}

LONG CtiTablePointBase::getID() const
{
   return _pointID;
}

RWCString CtiTablePointBase::getName() const
{
   return _name;
}

CtiPointType_t CtiTablePointBase::getType() const
{
   return _type;
}

CtiPointType_t CtiTablePointBase::isA() const
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

RWCString CtiTablePointBase::getLogicalGroup() const
{
   return _logicalGroup;
}

LONG CtiTablePointBase::getStateGroupID() const
{
   return _stateGroupID;
}

BOOL CtiTablePointBase::getDisableTag() const
{
   return _outOfService;
}

BOOL CtiTablePointBase::isInService() const
{
   return !(_outOfService);
}

BOOL CtiTablePointBase::isOutOfService() const
{
   return _outOfService;
}

BOOL CtiTablePointBase::getAlarmDisableTag() const
{
   return _alarmDisable;
}

BOOL CtiTablePointBase::isAlarmDisabled() const
{
   return _alarmDisable;
}

BOOL CtiTablePointBase::getPseudoTag() const
{
   return _pseudoPoint;
}

BOOL CtiTablePointBase::isPseudoPoint() const
{
   return _pseudoPoint;
}

BOOL CtiTablePointBase::getArchivePending() const
{
   return _archivePending;
}

BOOL CtiTablePointBase::isArchivePending() const
{
   return _archivePending;
}

INT CtiTablePointBase::getArchiveInterval() const
{
   return _archiveInterval;
}

INT CtiTablePointBase::getArchiveType() const
{
   return _archiveType;
}

/* setters */
CtiTablePointBase& CtiTablePointBase::setPointID(LONG id)
{
   _pointID = id;
   return *this;
}

CtiTablePointBase& CtiTablePointBase::setID(LONG id)
{
   _pointID = id;
   return *this;
}

CtiTablePointBase& CtiTablePointBase::setName(RWCString str)
{
   _name = str;
   return *this;
}

CtiTablePointBase& CtiTablePointBase::setType(CtiPointType_t t)
{
   _type = t;
   return *this;
}

CtiTablePointBase& CtiTablePointBase::setPAObjectID(LONG id)
{
   _paObjectID = id;
   return *this;
}

CtiTablePointBase& CtiTablePointBase::setLogicalGroup(RWCString str)
{
   _logicalGroup = str;
   return *this;
}

CtiTablePointBase& CtiTablePointBase::setStateGroupID(LONG id)
{
   _stateGroupID = id;
   return *this;
}

CtiTablePointBase& CtiTablePointBase::setDisableTag(BOOL b)
{
   _outOfService = b;
   return *this;
}

CtiTablePointBase& CtiTablePointBase::resetDisableTag(BOOL b)
{
   return setDisableTag(b);
}

CtiTablePointBase& CtiTablePointBase::setAlarmDisableTag(BOOL b)
{
   _alarmDisable = b;
   return *this;
}

CtiTablePointBase& CtiTablePointBase::resetAlarmDisableTag(BOOL b)
{
   setAlarmDisableTag(b);
   return *this;
}

CtiTablePointBase& CtiTablePointBase::setPseudoTag(BOOL b)
{
   _pseudoPoint = b;
   return *this;
}

CtiTablePointBase& CtiTablePointBase::resetPseudoTag(BOOL b)
{
   return setPseudoTag(b);
}

CtiTablePointBase& CtiTablePointBase::setArchivePending(BOOL b)
{
   _archivePending = b;
   return *this;
}

CtiTablePointBase& CtiTablePointBase::resetArchivePending(BOOL b)
{
   return setArchivePending(b);
}

CtiTablePointBase&  CtiTablePointBase::setArchiveInterval(const INT aI)
{
   _archiveInterval = aI;
   return *this;
}

CtiTablePointBase&  CtiTablePointBase::setArchiveType(const INT aT)
{
   _archiveType = aT;
   return *this;
}

CtiTablePointBase CtiTablePointBase::setPointOffset(INT i)
{
   _pointOffset = i;
   return *this;
}

UINT CtiTablePointBase::adjustStaticTags(UINT &tag) const
{
    if(isPseudoPoint())
        tag |= TAG_ATTRIB_PSEUDO;
    else
        tag &= ~TAG_ATTRIB_PSEUDO;

    if(isOutOfService())
        tag |= TAG_DISABLE_POINT_BY_POINT;
    else
        tag &= ~TAG_DISABLE_POINT_BY_POINT;

    if(isAlarmDisabled())
        tag |= TAG_DISABLE_ALARM_BY_POINT;
    else
        tag &= ~TAG_DISABLE_ALARM_BY_POINT;

    return tag;
}

UINT CtiTablePointBase::getStaticTags() const
{
   UINT tag = 0;

   if(isPseudoPoint()) tag |= TAG_ATTRIB_PSEUDO;
   if(isOutOfService()) tag |= TAG_DISABLE_POINT_BY_POINT;
   if(isAlarmDisabled()) tag |= TAG_DISABLE_ALARM_BY_POINT;

   return tag;
}

CtiTablePointBase::CtiTablePointBase(LONG pid) :
   _pointID( pid ),
   _paObjectID(-1),
   _pointOffset(-1),
   _stateGroupID(-1),
   _flag(0)
{}

CtiTablePointBase::CtiTablePointBase(const CtiTablePointBase& aRef)
{
   *this = aRef;
}

CtiTablePointBase::~CtiTablePointBase() {}


