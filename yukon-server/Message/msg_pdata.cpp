/*-----------------------------------------------------------------------------*
*
* File:   msg_pdata
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/msg_pdata.cpp-arc  $
* REVISION     :  $Revision: 1.15 $
* DATE         :  $Date: 2007/11/08 22:47:24 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include <iostream>
#include <iomanip>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore


#include "message.h"
#include "logger.h"
#include "pointtypes.h"
#include "msg_pdata.h"
#include "collectable.h"


RWDEFINE_COLLECTABLE( CtiPointDataMsg, MSG_POINTDATA );

unsigned int CtiPointDataMsg::_instanceCount = 0;

CtiPointDataMsg::CtiPointDataMsg(long id,
                                 double    value,
                                 unsigned  quality,
                                 CtiPointType_t type,
                                 string    valReport,
                                 unsigned  tags,
                                 unsigned  attrib,
                                 unsigned  limit,
                                 int       pri,
                                 unsigned  millis) :
   _id(id),
   _value(value),
   _quality(quality),
   _type(type),
   _str(valReport),
   _time(CtiTime()),
   _tags(tags),
   _attrib(attrib),
   _limit(limit),
   //_millis(millis)  //  set below so the time can be rounded
   CtiMessage(pri)
{
    _instanceCount++;
    setMillis(millis);

    if(_type == StatusPointType)
    {
        setExemptionStatus(TRUE); // Status data must always default to exemptable!
    }

    if(_isnan(_value) || !_finite(_value))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") pdata is NaN or INF!" << endl;
        }
        _value = 0.0;
    }
}

CtiPointDataMsg::CtiPointDataMsg(const CtiPointDataMsg &aRef)
{
    _instanceCount++;
   *this = aRef;
}

CtiPointDataMsg::~CtiPointDataMsg()
{
    _instanceCount--;
}

CtiPointDataMsg& CtiPointDataMsg::operator=(const CtiPointDataMsg& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);

      _id               = aRef.getId();
      _type             = aRef.getType();
      _quality          = aRef.getQuality();
      _tags             = aRef.getTags();
      _attrib           = aRef.getAttributes();
      _limit            = aRef.getLimit();
      _value            = aRef.getValue();
      _str              = aRef.getString();
      _time             = aRef.getTime();
      _millis           = aRef.getMillis();
   }

   return *this;
}

void CtiPointDataMsg::saveGuts(RWvostream &aStream) const
{
    Inherited::saveGuts(aStream);
    aStream << getId() << getType() << getQuality() << getTags() << getAttributes() << getLimit() << getValue() << isExemptable() << getString() << getTime() << getMillis();
}

void CtiPointDataMsg::restoreGuts(RWvistream& aStream)
{
    Inherited::restoreGuts(aStream);

    long        id;
    int         type;
    unsigned    quality;            // Point quality
    unsigned    tags;               // Point Tags
    unsigned    limit;              // set iff ExceedsHigh or Low.
    unsigned    force;              // set iff ExceedsHigh or Low.
    double      value;
    string   str;
    CtiTime      intime;
    unsigned    millis;

    aStream >> id >> type >> quality >> tags >> _attrib >> limit >> value >> force >> str >> intime >> millis;

    setId(id);
    setType(resolveType(type));
    setQuality(quality);

    resetTags();
    setTags(tags);
    setLimit(limit);
    setValue(value);

    // Forced status is no longer used on the C++.
    setExemptionStatus(force);

    setString(str);
    setTime(intime);
    setMillis(millis);

    //dump();
}

long  CtiPointDataMsg::getId() const
{
   return _id;
}
CtiPointDataMsg& CtiPointDataMsg::setId( const long a_id )
{
   _id = a_id;
   return *this;
}

const string& CtiPointDataMsg::getString() const
{
    return _str;
}

CtiPointDataMsg& CtiPointDataMsg::setString(const string& string_value)
{
    _str = string_value;
    return *this;
}


CtiPointType_t CtiPointDataMsg::getType() const
{
    return _type;
}

CtiPointDataMsg& CtiPointDataMsg::setType(CtiPointType_t type)
{
    _type = type;
    return *this;
}

CtiPointType_t CtiPointDataMsg::resolveType(int type)
{
    CtiPointType_t retval = InvalidPointType;

    switch( type )
    {
        case StatusPointType:            retval = StatusPointType; break;
        case AnalogPointType:            retval = AnalogPointType; break;
        case PulseAccumulatorPointType:  retval = PulseAccumulatorPointType; break;
        case DemandAccumulatorPointType: retval = DemandAccumulatorPointType; break;
        case CalculatedPointType:        retval = CalculatedPointType; break;
        case StatusOutputPointType:      retval = StatusOutputPointType; break;
        case AnalogOutputPointType:      retval = AnalogOutputPointType; break;
        case SystemPointType:            retval = SystemPointType; break;
        case CalculatedStatusPointType:  retval = CalculatedStatusPointType; break;
    }

    return retval;
}

double CtiPointDataMsg::getValue() const
{
    return _value;
}

CtiPointDataMsg& CtiPointDataMsg::setValue(double value)
{
    _value = value;
    if(_isnan(_value) || !_finite(_value))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") pdata is NaN or INF!" << endl;
        }
        _value = 0.0;
    }
    return *this;
}

unsigned  CtiPointDataMsg::getQuality() const
{
   return _quality;
}
CtiPointDataMsg& CtiPointDataMsg::setQuality( const unsigned a_quality )
{
   _quality = a_quality;
   return *this;
}

unsigned  CtiPointDataMsg::getTags() const
{
   return _tags;
}
CtiPointDataMsg& CtiPointDataMsg::setTags( const unsigned a_tags )
{
   _tags |= a_tags;
   return *this;
}
CtiPointDataMsg& CtiPointDataMsg::resetTags( const unsigned a_tags )
{
   _tags &= ~(a_tags);
   return *this;
}

unsigned  CtiPointDataMsg::getAttributes() const
{
   return _attrib;
}
CtiPointDataMsg& CtiPointDataMsg::setAttributes( const unsigned at )
{
   _attrib = at;
   return *this;
}

unsigned  CtiPointDataMsg::getLimit() const
{
   return _limit;
}
CtiPointDataMsg& CtiPointDataMsg::setLimit( const unsigned a_limit )
{
   _limit = a_limit;
   return *this;
}

const CtiTime& CtiPointDataMsg::getTime() const
{
   return _time;
}

CtiTime& CtiPointDataMsg::getTime()
{
   return _time;
}

CtiPointDataMsg& CtiPointDataMsg::setTime(const CtiTime& aTime)
{
   _time = aTime;
   return *this;
}

unsigned CtiPointDataMsg::getMillis() const
{
   return _millis;
}

CtiPointDataMsg& CtiPointDataMsg::setMillis(unsigned millis)
{
    _millis = millis % 1000;

    if( millis > 999 )
    {
       _millis = 0;
       //CtiLockGuard<CtiLogger> doubt_guard(dout);
       //dout << CtiTime() << " **** Checkpoint - setMillis(), millis = " << millis << " > 999 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return *this;
}



void CtiPointDataMsg::dump() const
{
   Inherited::dump();

   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);

      dout << " Id                            " << getId() << endl;
      dout << " Type                          " << getType() << endl;
      dout << " Quality                       " << getQuality() << endl;
      CHAR  oldFill = dout.fill();
      dout.fill('0');
      dout << " Tags                          0x" << hex << setw(8) <<  getTags() << dec << endl;
      dout.fill(oldFill);
      dout << " Limit                         " << getLimit() << endl;
      dout << " Value                         " << getValue() << endl;
      dout << " Change Time                   " << getTime() << ", " << getMillis() << "ms" << endl;
      dout << " Change Report                 " << getString() << endl;
      dout << " Is this data exemptable       " << isExemptable() << endl;
      // dout << " Exception Exempt              " << (_exceptionExempt ? "TRUE" : "FALSE") << endl;
   }

}

// Return a new'ed copy of this message!
CtiMessage* CtiPointDataMsg::replicateMessage() const
{
   CtiPointDataMsg *ret = CTIDBG_new CtiPointDataMsg(*this);

   return( (CtiMessage*)ret );
}

unsigned  CtiPointDataMsg::isExemptable() const
{
   return (_tags & TAG_POINT_MAY_BE_EXEMPTED);
}
CtiPointDataMsg& CtiPointDataMsg::setExemptionStatus( const unsigned a_ex )
{
   //_tags = ( (a_ex == 0) ? (_tags & ~TAG_POINT_MUST_NOTIFY_CLIENTS) : ( _tags | TAG_POINT_MUST_NOTIFY_CLIENTS ) );
   _tags = ( (a_ex == 0) ? (_tags & ~TAG_POINT_MAY_BE_EXEMPTED) : ( _tags | TAG_POINT_MAY_BE_EXEMPTED ) );
   return *this;
}

bool CtiPointDataMsg::isValid()
{
    return _time.isValid();
}

