#include "yukon.h"

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
****************************************************************************
*/


/** include files **/
#include "fdrdestination.h"


/** local definitions **/

CtiFDRDestination::CtiFDRDestination(RWCString &translation, RWCString &destination)
:   iTranslation(translation),
    iDestination(destination)

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
    }
    return *this;
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

