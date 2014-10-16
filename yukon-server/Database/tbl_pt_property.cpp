#include "precompiled.h"

#include "tbl_pt_property.h"
#include "logger.h"

using namespace std;

CtiTablePointProperty::~CtiTablePointProperty()
{}

CtiTablePointProperty::CtiTablePointProperty(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["pointid"]  >> _pointID;
    rdr   >> _propertyID;
    rdr   >> _floatAttributeValue;
}

CtiTablePointProperty::CtiTablePointProperty(long pointID, unsigned int propertyID, float attributeValue) :
_pointID(pointID),
_propertyID(propertyID),
_floatAttributeValue(attributeValue)
{
}

std::string CtiTablePointProperty::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTablePointProperty";
    itemList.add("PointID")    << _pointID;
    itemList.add("PropertyID") << _propertyID;
    itemList.add("Value")      << _floatAttributeValue;

    return itemList.toString();
}

string CtiTablePointProperty::getSQLCoreStatement()
{
    static const string sql = "SELECT PPV.pointid, PPV.pointpropertycode, PPV.fltvalue "
                              "FROM PointPropertyValue PPV";

    return sql;
}

bool CtiTablePointProperty::operator<(const CtiTablePointProperty &rhs) const
{
    if( _pointID < rhs._pointID )  return true;
    if( _pointID > rhs._pointID )  return false;

    return _propertyID < rhs._propertyID;
}

string       CtiTablePointProperty::getTableName()            {  return "PointPropertyValue";  }
long         CtiTablePointProperty::getPointID()       const  {  return _pointID;              }
unsigned int CtiTablePointProperty::getPropertyID()    const  {  return _propertyID;           }
float        CtiTablePointProperty::getFloatProperty() const  {  return _floatAttributeValue;  }
int          CtiTablePointProperty::getIntProperty  () const  {  return static_cast<int>(_floatAttributeValue);  }

