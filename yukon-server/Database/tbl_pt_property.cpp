
/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_property
*
* Date:   2/13/08
*
* PVCS KEYWORDS:
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2008/02/25 21:12:41 $
*
* Copyright (c) 2008 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_pt_property.h"
#include "logger.h"
#include "rwutil.h"
using namespace std;

CtiTablePointProperty::CtiTablePointProperty() :
_pointID(0)
{}

CtiTablePointProperty::CtiTablePointProperty(const CtiTablePointProperty& aRef) :
_pointID(0)
{
    *this = aRef;
}

CtiTablePointProperty& CtiTablePointProperty::operator=(const CtiTablePointProperty& aRef)
{
    if(this != &aRef)
    {
        _pointID = aRef._pointID;
        PropertyMapIter iter;

        unsigned int propertyID;
        float floatAttribValue;
        for( iter = aRef._propertyMap.begin(); iter != aRef._propertyMap.end(); iter++ )
        {
            propertyID = iter->first;
            floatAttribValue = iter->second;

            _propertyMap.insert(PropertyMap::value_type(propertyID, floatAttribValue));
        }
    }
    return *this;
}

CtiTablePointProperty::~CtiTablePointProperty()
{}

CtiTablePointProperty& CtiTablePointProperty::operator=(const CtiTablePointProperty& aRef);

void CtiTablePointProperty::DecodeDatabaseReader(RWDBReader &rdr)
{
    unsigned int propertyID;
    float floatAttribValue;
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["pointid"]  >> _pointID;
    rdr   >> propertyID;
    rdr   >> floatAttribValue;

    _propertyMap.insert((const PropertyMap::value_type) PropertyMap::value_type(propertyID, floatAttribValue));
}

void CtiTablePointProperty::dump() const
{
    PropertyMapIter iter;
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << " PointID                                  : " << _pointID << endl;
    for( iter = _propertyMap.begin(); iter != _propertyMap.end(); iter++ )
    {
        dout << " Property ID                         : " << iter->first << endl;
        dout << " Value                                : " << iter->second << endl;
    }
}

void CtiTablePointProperty::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(getTableName().c_str() );

    selector <<
    keyTable["pointid"] <<
    keyTable["pointpropertycode"] <<
    keyTable["floatvalue"];

    selector.from(keyTable);
}

string CtiTablePointProperty::getTableName()
{
    return "PointPropertyValue";
}

bool CtiTablePointProperty::hasProperty(unsigned int propertyID)
{
    bool retVal = false;
    if( _propertyMap.find(propertyID) != _propertyMap.end() )
    {
        retVal = true;
    }
    return retVal;
}

//Returns the property float value or std::numeric_limits<float>::min()
float CtiTablePointProperty::getFloatProperty(unsigned int propertyID)
{
    PropertyMap::iterator iter;
    float retVal = std::numeric_limits<float>::min();
    if( (iter = _propertyMap.find(propertyID)) != _propertyMap.end() )
    {
        retVal = (*iter).second;
    }
    return retVal;
}

//Returns the property int value or std::numeric_limits<int>::min()
int CtiTablePointProperty::getIntProperty(unsigned int propertyID)
{
    int retVal = std::numeric_limits<int>::min();
    float floatVal = getFloatProperty(propertyID);
    if( floatVal != std::numeric_limits<float>::min() )
    {
        retVal = floatVal;
    }
    return retVal;
}

void CtiTablePointProperty::resetTable()
{
    _propertyMap.clear();
}
