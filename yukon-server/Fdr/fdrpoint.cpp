/*****************************************************************************
*
*    FILE NAME: fdrpoint.cpp
*
*    DATE: 10/25/2000
*
*    AUTHOR: Matt Fisher
*
*    PURPOSE: class header CtiFDRPoint
*
*    DESCRIPTION: represents a FDR translation record
*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrpoint.cpp-arc  $
*    REVISION     :  $Revision: 1.5 $
*    DATE         :  $Date: 2005/02/17 19:02:58 $
*    History:
      $Log: fdrpoint.cpp,v $
      Revision 1.5  2005/02/17 19:02:58  mfisher
      Removed space before CVS comment header, moved #include "yukon.h" after CVS header

      Revision 1.4  2005/02/10 23:23:51  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.3  2002/04/16 15:58:34  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:18:56  cplender

      This is an update due to the freezing of PVCS on 4/13/2002


*****************************************************************************/
#include "yukon.h"

#include <rw/rwtime.h>
using namespace std;
#include "fdrpoint.h"
#include "fdrdestination.h"
#include "pointdefs.h"

#include "logger.h"


/** local definitions **/


CtiFDRPoint::CtiFDRPoint( long pointID)
:   iPointID(pointID),
    iControllable(false),
    iMultiplier(1.0),
    iOffset(0.0),
    iPointType (InvalidPointType),
    iValue (0.0),
    iQuality (UnintializedQuality),
    iLastTimeStamp(rwEpoch + (86400 * 10))
{
}

CtiFDRPoint::~CtiFDRPoint()
{
    iDestinationList.erase (iDestinationList.begin(),iDestinationList.end());
}

/*
BOOL CtiFDRPoint::operator==( const CtiFDRPoint &other ) const
{
    return( (
             getPointID() ==        other.getPointID()          &&
             getTranslateName() ==  other.getTranslateName()    &&
             getDestinationName() ==other.getDestinationName()  &&
             getMultiplier() ==     other.getMultiplier()       &&
             getLastTimeStamp() ==  other.getLastTimeStamp()    &&
             getPointType() ==    other.getPointType()    &&
             isControllable() ==    other.isControllable()    &&
             getOffset() ==         other.getOffset()
             )
            );
}
*/
/*************************************************
* Function Name: operator=
*
* Description: Assignment
*
**************************************************
*/
CtiFDRPoint& CtiFDRPoint::operator=( const CtiFDRPoint &other )
{
    if(this != &other)
    {
        iPointID = other.getPointID();
        iMultiplier = other.getMultiplier();
        iOffset = other.getOffset();
        iLastTimeStamp = other.getLastTimeStamp();
        iPointType = other.getPointType();
        iControllable = other.isControllable();
        iDestinationList = other.getDestinationList();
        iValue = other.getValue();
        iQuality = other.getQuality();
    }
    return *this;
}

vector< CtiFDRDestination > CtiFDRPoint::getDestinationList () const
{
    return iDestinationList;
}

vector< CtiFDRDestination > & CtiFDRPoint::getDestinationList ()
{
    return iDestinationList;
}


CtiFDRPoint& CtiFDRPoint::setDestinationList (CtiFDRPoint &aList)
{
    iDestinationList = aList.getDestinationList();
    return *this;
}

CtiFDRPoint& CtiFDRPoint::setDestinationList (vector< CtiFDRDestination > &aList)
{
    iDestinationList = aList;
    return *this;
}

long CtiFDRPoint::getPointID( void ) const
{
    return iPointID;
}

CtiFDRPoint &  CtiFDRPoint::setPointID(const long aPointID)
{
    iPointID = aPointID;
    return *this;
}

double CtiFDRPoint::getMultiplier( void ) const
{
    return iMultiplier;
}


CtiFDRPoint &  CtiFDRPoint::setMultiplier( const double aMultiplier )
{
    iMultiplier = aMultiplier;
    return *this;
}

double CtiFDRPoint::getValue( void ) const
{
    return iValue;
}


CtiFDRPoint &  CtiFDRPoint::setValue( const double aValue )
{
    iValue = aValue;
    return *this;
}



double CtiFDRPoint::getOffset( void ) const
{
    return iOffset;
}

CtiFDRPoint &  CtiFDRPoint::setOffset( const double aOffset )
{
    iOffset = aOffset;
    return *this;
}

unsigned CtiFDRPoint::getQuality( void ) const
{
    return iQuality;
}

CtiFDRPoint &  CtiFDRPoint::setQuality( const unsigned aQuality )
{
    iQuality = aQuality;
    return *this;
}

RWTime CtiFDRPoint::getLastTimeStamp ( void ) const
{
    return iLastTimeStamp;
}


CtiFDRPoint &  CtiFDRPoint::setLastTimeStamp( const RWTime & aTimeStamp )
{
    iLastTimeStamp = aTimeStamp;
    return *this;
}

bool CtiFDRPoint::isControllable( void ) const
{
    return iControllable;
}

CtiFDRPoint &  CtiFDRPoint::setControllable(const bool aFlag)
{
    iControllable = aFlag;
    return *this;
}

CtiPointType_t CtiFDRPoint::getPointType() const
{
    return iPointType;
}
CtiFDRPoint & CtiFDRPoint::setPointType(CtiPointType_t aType)
{
    iPointType = aType;
    return *this;
}

RWCString CtiFDRPoint::getTranslateName( RWCString &aDestinationName )
{
    RWCString retVal;
    int entries = iDestinationList.size();

    for (int x=0; x < entries; x++)
    {
        if(!(iDestinationList[x].getDestination().compareTo (aDestinationName,RWCString::ignoreCase)))
            retVal = iDestinationList[x].getTranslation();
    }

    return retVal;
}

RWCString CtiFDRPoint::getTranslateName( int aIndex )
{
    RWCString retVal;

    if (iDestinationList.size() >= aIndex+1)
        retVal = iDestinationList[aIndex].getTranslation();

    return retVal;
}


