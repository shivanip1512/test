
/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_attribute
*
* Date:   2/13/08
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_unit.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2008/02/21 18:56:08 $
*
* Copyright (c) 2008 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_pt_attribute.h"
#include "logger.h"
#include "rwutil.h"
using namespace std;

CtiTablePointAttribute::CtiTablePointAttribute() :
_pointID(0)
{}

CtiTablePointAttribute::CtiTablePointAttribute(const CtiTablePointAttribute& aRef) :
_pointID(0)
{
    *this = aRef;
}

CtiTablePointAttribute& CtiTablePointAttribute::operator=(const CtiTablePointAttribute& aRef)
{
    if(this != &aRef)
    {
        _pointID = aRef._pointID;
        AttributeMapIter iter;

        unsigned int attributeID;
        float floatAttribValue;
        for( iter = aRef._attributeMap.begin(); iter != aRef._attributeMap.end(); iter++ )
        {
            attributeID = iter->first;
            floatAttribValue = iter->second;

            _attributeMap.insert(AttributeMap::value_type(attributeID, floatAttribValue));
        }
    }
    return *this;
}

CtiTablePointAttribute::~CtiTablePointAttribute()
{}

CtiTablePointAttribute& CtiTablePointAttribute::operator=(const CtiTablePointAttribute& aRef);

void CtiTablePointAttribute::DecodeDatabaseReader(RWDBReader &rdr)
{
    unsigned int attributeID;
    float floatAttribValue;
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["pointid"]  >> _pointID;
    rdr   >> attributeID;
    rdr   >> floatAttribValue;

    _attributeMap.insert((const AttributeMap::value_type) AttributeMap::value_type(attributeID, floatAttribValue));
}

void CtiTablePointAttribute::dump() const
{
    AttributeMapIter iter;
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << " PointID                                  : " << _pointID << endl;
    for( iter = _attributeMap.begin(); iter != _attributeMap.end(); iter++ )
    {
        dout << " Attribute ID                         : " << iter->first << endl;
        dout << " Value                                : " << iter->second << endl;
    }
}

void CtiTablePointAttribute::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(getTableName().c_str() );

    selector <<
    keyTable["pointid"] <<
    keyTable["attributeid"] <<
    keyTable["floatvalue"];

    selector.from(keyTable);
}

string CtiTablePointAttribute::getTableName()
{
    return "PointAttribute";
}

bool CtiTablePointAttribute::hasAttribute(unsigned int attributeID)
{
    bool retVal = false;
    if( _attributeMap.find(attributeID) != _attributeMap.end() )
    {
        retVal = true;
    }
    return retVal;
}

//Returns the attribute float value or std::numeric_limits<float>::min()
float CtiTablePointAttribute::getFloatAttribute(unsigned int attributeID)
{
    AttributeMap::iterator iter;
    float retVal = std::numeric_limits<float>::min();
    if( (iter = _attributeMap.find(attributeID)) != _attributeMap.end() )
    {
        retVal = (*iter).second;
    }
    return retVal;
}

//Returns the attribute int value or std::numeric_limits<int>::min()
int CtiTablePointAttribute::getIntAttribute(unsigned int attributeID)
{
    int retVal = std::numeric_limits<int>::min();
    float floatVal = getFloatAttribute(attributeID);
    if( floatVal != std::numeric_limits<float>::min() )
    {
        retVal = floatVal;
    }
    return retVal;
}
