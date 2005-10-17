/*****************************************************************************
*
*    FILE NAME: fdrdestination.cpp
*
*    DATE: 05/24/2001
*
*    AUTHOR: David Sutton
*
*    PURPOSE: class header CtiFDRDestination
*
*    DESCRIPTION: represents a FDR destination record
*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************/
#include "yukon.h"

#include <rw/cstring.h>
#include <rw/ctoken.h>


#include "fdrpoint.h"
#include "fdrdestination.h"


/** local definitions **/

CtiFDRDestination::CtiFDRDestination(CtiFDRPoint* parentPoint, RWCString &translation, RWCString &destination)
:   iTranslation(translation),
    iDestination(destination),
    iParentPoint(parentPoint)
{
}

CtiFDRDestination::~CtiFDRDestination()
{
}

CtiFDRDestination& CtiFDRDestination::operator=( const CtiFDRDestination &other )
{
    if(this != &other)
    {
        iDestination = other.getDestination();
        iTranslation = other.getTranslation();
        iParentPoint = other.getParentPoint();
    }
    return *this;
}

bool CtiFDRDestination::operator<(const CtiFDRDestination& other) const
{
    if (iParentPoint->getPointID() != other.getParentPoint()->getPointID())
    {
        return iParentPoint->getPointID() < other.getParentPoint()->getPointID();
    }
    else if (iDestination != other.iDestination)
    {
        return iDestination < other.iDestination;
    }
    else
    {
        return iTranslation < other.iTranslation;
    }
}


RWCString & CtiFDRDestination::getTranslation(void)
{
    return iTranslation;
}

RWCString  CtiFDRDestination::getTranslation(void) const
{
    return iTranslation;
}

CtiFDRDestination& CtiFDRDestination::setTranslation (RWCString aTranslation)
{
  iTranslation = aTranslation;
  return *this;
}

RWCString & CtiFDRDestination::getDestination(void)
{
    return iDestination;
}

RWCString  CtiFDRDestination::getDestination(void) const
{
    return iDestination;
}

CtiFDRDestination& CtiFDRDestination::setDestination (RWCString aDestination)
{
  iDestination = aDestination;
  return *this;
}

CtiFDRPoint*  CtiFDRDestination::getParentPoint(void) const
{
    return iParentPoint;
}

CtiFDRDestination& CtiFDRDestination::setParentPoint (CtiFDRPoint* aParentPoint)
{
  iParentPoint = aParentPoint;
  return *this;
}

RWCString CtiFDRDestination::getTranslationValue(RWCString propertyName) const {
  RWCString result("");
  RWCTokenizer pairTokenizer(getTranslation());

  RWCString nameValuePair;
  while (!(nameValuePair = pairTokenizer(";")).isNull()) {
    RWCTokenizer valueTokenizer(nameValuePair);

    RWCString thisPropertyName = valueTokenizer(":");
    if (thisPropertyName == propertyName) {
      // right now the string looks like this: ":thisvalue;nextproperty:nextvalue"
      // we're going to return up to the ';', and then trim the first character
      RWCString valueWithColon = valueTokenizer(";");
      result = valueWithColon(1, valueWithColon.length() - 1);
      break;
    }
  }
  return result;
}

std::ostream& operator<< (std::ostream& os, const CtiFDRDestination& dest)
{
    return os << "[destination " << dest.getDestination() 
        << " for " << *dest.getParentPoint() << "]";
}
