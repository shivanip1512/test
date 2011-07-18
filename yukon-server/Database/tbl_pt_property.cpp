
/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_property
*
* Date:   2/13/08
*
* PVCS KEYWORDS:
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2008/10/30 19:54:26 $
*
* Copyright (c) 2008 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

void CtiTablePointProperty::dump() const
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << " PointID      : " << _pointID << endl;
    dout << " Property ID  : " << _propertyID << endl;
    dout << " Value        : " << _floatAttributeValue << endl;
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

