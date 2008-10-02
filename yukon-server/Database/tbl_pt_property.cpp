
/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_property
*
* Date:   2/13/08
*
* PVCS KEYWORDS:
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2008/10/02 18:27:30 $
*
* Copyright (c) 2008 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_pt_property.h"
#include "logger.h"
#include "rwutil.h"
using namespace std;

CtiTablePointProperty::~CtiTablePointProperty()
{}

CtiTablePointProperty::CtiTablePointProperty(RWDBReader &rdr)
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

void CtiTablePointProperty::dump() const
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << " PointID      : " << _pointID << endl;
    dout << " Property ID  : " << _propertyID << endl;
    dout << " Value        : " << _floatAttributeValue << endl;
}

void CtiTablePointProperty::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(getTableName().c_str() );

    selector <<
    keyTable["pointid"] <<
    keyTable["pointpropertycode"] <<
    keyTable["fltvalue"];

    selector.from(keyTable);
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

