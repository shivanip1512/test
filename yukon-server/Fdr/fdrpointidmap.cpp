#include "yukon.h"
 
/***************************************************************************** 
*
*    FILE NAME: fdrpointidmap.cpp
*
*    DATE: 10/23/2000
*
*    AUTHOR: Ben Wallace
*
*    PURPOSE: member functions for class: CtiFDRPointIdMap
*
*
*    DESCRIPTION: This class models a generic FDR Translation record.
*                 It uses a single String for an Id on the other system
*                 and include the points multiplier and offset.
*
******************************************************************************
*/


/** include files **/
#include <rw/db/db.h>

#include "dbaccess.h"
#include "fdrpointidmap.h"
#include "resolvers.h"
#include "logger.h"
#include "guard.h"


/** local definitions **/

/**************************************************
* Function Name: CtiFDRPointIdMap
*
* Description: constructor
*
*
***************************************************
*/
CtiFDRPointIdMap::CtiFDRPointIdMap( long pointID, RWCString & translateName, RWCString & destinationName, FDRDbReloadReason aReason)
:   iPointID(pointID),
    iTranslateName(translateName),
    iDestinationName(destinationName),
    iReasonForReload(aReason),
    iControllable(false),
    iMultiplier(1.0),
    iOffset(0.0),
    iPointType (InvalidPointType),
    iLastTimeStamp(rwEpoch + (86400 * 10))
{
}

CtiFDRPointIdMap::~CtiFDRPointIdMap()
{
}


/*************************************************
* Function Name: operator==
*
* Description:
*
*
**************************************************
*/
BOOL CtiFDRPointIdMap::operator==( const CtiFDRPointIdMap &other ) const
{   
    return( (
             getPointID() ==        other.getPointID()          &&
             getTranslateName() ==  other.getTranslateName()    &&
             getDestinationName() ==other.getDestinationName()  &&
             getMultiplier() ==     other.getMultiplier()       &&
             getLastTimeStamp() ==  other.getLastTimeStamp()    &&
             getReasonForReload() ==    other.getReasonForReload()    &&
             getPointType() ==    other.getPointType()    &&
             isControllable() ==    other.isControllable()    &&
             getOffset() ==         other.getOffset()           
             ) 
            );
}

/*************************************************
* Function Name: operator=
*
* Description: Assignment
*
**************************************************
*/
CtiFDRPointIdMap& CtiFDRPointIdMap::operator=( const CtiFDRPointIdMap &other )
{   
    
    iPointID = other.getPointID();
    iTranslateName = other.getTranslateName();
    iDestinationName = other.getDestinationName();
    iMultiplier = other.getMultiplier();
    iOffset = other.getOffset();           
    iLastTimeStamp = other.getLastTimeStamp();
    iPointType = other.getPointType();
    iReasonForReload = other.getReasonForReload();
    iControllable = other.isControllable();

    return *this;
}

/*****************  Begin **************************
*
* Description: getters and setters for private data
*
****************************************************
*/
long CtiFDRPointIdMap::getPointID( void ) const
{   
    return iPointID;
}
        
CtiFDRPointIdMap &  CtiFDRPointIdMap::setPointID(const long aPointID)
{   
    iPointID = aPointID;
    return *this;
}
        
RWCString CtiFDRPointIdMap::getTranslateName() const
{
    return iTranslateName;
}
        

CtiFDRPointIdMap &  CtiFDRPointIdMap::setTranslateName(const RWCString & aName)
{                                   
    iTranslateName = aName;
    return *this;
}

RWCString CtiFDRPointIdMap::getDestinationName() const
{
    return iDestinationName;
}
        

CtiFDRPointIdMap &  CtiFDRPointIdMap::setDestinationName(const RWCString & aName)
{                                   
    iDestinationName = aName;
    return *this;
}


double CtiFDRPointIdMap::getMultiplier( void ) const
{
    return iMultiplier;
}
        

CtiFDRPointIdMap &  CtiFDRPointIdMap::setMultiplier( const double aMultiplier )
{
    iMultiplier = aMultiplier;
    return *this;
}

        
double CtiFDRPointIdMap::getOffset( void ) const
{
    return iOffset;
}
        
CtiFDRPointIdMap &  CtiFDRPointIdMap::setOffset( const double aOffset )
{
    iOffset = aOffset;
    return *this;
}

RWTime CtiFDRPointIdMap::getLastTimeStamp ( void ) const
{
    return iLastTimeStamp;
}
        

CtiFDRPointIdMap &  CtiFDRPointIdMap::setLastTimeStamp( const RWTime & aTimeStamp )
{
    iLastTimeStamp = aTimeStamp;
    return *this;
}


FDRDbReloadReason CtiFDRPointIdMap::getReasonForReload () const
{
    return iReasonForReload;
}

CtiFDRPointIdMap & CtiFDRPointIdMap::setReasonForReload (FDRDbReloadReason  aFlag)
{
    iReasonForReload = aFlag;
    return *this;
}
bool CtiFDRPointIdMap::isControllable( void ) const
{   
    return iControllable;
}
        
CtiFDRPointIdMap &  CtiFDRPointIdMap::setControllable(const bool aFlag)
{   
    iControllable = aFlag;
    return *this;
}

CtiPointType_t CtiFDRPointIdMap::getPointType() const        
{ 
    return iPointType;
}
CtiFDRPointIdMap & CtiFDRPointIdMap::setPointType(CtiPointType_t aType)
{ 
    iPointType = aType;
    return *this;
}


/**************** FINISH  Getters - Setters*************************
********************************************************************
*/




