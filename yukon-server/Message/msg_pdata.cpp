#include "precompiled.h"

#include "logger.h"
#include "pointtypes.h"
#include "msg_pdata.h"
#include "collectable.h"
#include "numstr.h"

using namespace std;

DEFINE_COLLECTABLE( CtiPointDataMsg, MSG_POINTDATA );

unsigned int CtiPointDataMsg::_instanceCount = 0;

CtiPointDataMsg::CtiPointDataMsg(long id,
                                 double    value,
                                 unsigned  quality,
                                 CtiPointType_t type,
                                 string    valReport,
                                 unsigned  tags) :
   _id(id),
   _value(value),
   _quality(quality),
   _type(type),
   _str(valReport),
   _time(CtiTime()),
   _tags(tags),
   _millis(0),
   CtiMessage(7)
{
    _instanceCount++;

    if(_isnan(_value) || !_finite(_value))
    {
        CTILOG_WARN(dout, "pdata is NaN or INF!");
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
      _value            = aRef.getValue();
      _str              = aRef.getString();
      _time             = aRef.getTime();
      _millis           = aRef.getMillis();
   }

   return *this;
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
        CTILOG_WARN(dout, "pdata is NaN or INF!");
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
bool CtiPointDataMsg::isRegistrationUpload() const
{
    return _tags & TAG_POINT_REG_UPLOAD;
}
bool CtiPointDataMsg::isOldTimestamp() const
{
    return _tags & TAG_POINT_OLD_TIMESTAMP;
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
    }

    return *this;
}

CtiPointDataMsg& CtiPointDataMsg::setTimeWithMillis(const CtiTime& aTime, const unsigned millis)
{
    setTime(aTime);
    setMillis(millis);

    return *this;
}


std::string CtiPointDataMsg::getTrackingId() const
{
    //  Delimiter used to indicate that there is a tracking ID
    static const std::string TrackingDelimiter = " trkid ";

    const auto& source = getSource();

    if( auto pos = source.find(TrackingDelimiter); pos != std::string::npos )
    {
        return source.substr(pos + TrackingDelimiter.size());
    }

    return "";
}

std::string CtiPointDataMsg::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiPointDataMsg";
    itemList.add("Id")                      << getId();
    itemList.add("Type")                    << getType();
    itemList.add("Quality")                 << getQuality();
    itemList.add("Tags")                    << CtiNumStr(getTags()).xhex().zpad(8);
    itemList.add("Value")                   << getValue();
    itemList.add("Change Time")             << getTime() <<", "<< getMillis() <<"ms";
    itemList.add("Change Report")           << getString();

    return (Inherited::toString() += itemList.toString());
}

// Return a new'ed copy of this message!
CtiMessage* CtiPointDataMsg::replicateMessage() const
{
   CtiPointDataMsg *ret = CTIDBG_new CtiPointDataMsg(*this);

   return( (CtiMessage*)ret );
}

bool CtiPointDataMsg::isValid()
{
    return _time.isValid();
}

