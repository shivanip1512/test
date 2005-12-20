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

#include <rw/ctoken.h>
#include <boost/tokenizer.hpp>


#include "fdrpoint.h"
#include "fdrdestination.h"
#include "rwutil.h"


/** local definitions **/

CtiFDRDestination::CtiFDRDestination(CtiFDRPoint* parentPoint, string &translation, string &destination)
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


string & CtiFDRDestination::getTranslation(void)
{
    return iTranslation;
}

string  CtiFDRDestination::getTranslation(void) const
{
    return iTranslation;
}

CtiFDRDestination& CtiFDRDestination::setTranslation (string aTranslation)
{
  iTranslation = aTranslation;
  return *this;
}

string & CtiFDRDestination::getDestination(void)
{
    return iDestination;
}

string  CtiFDRDestination::getDestination(void) const
{
    return iDestination;
}

CtiFDRDestination& CtiFDRDestination::setDestination (string aDestination)
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

string CtiFDRDestination::getTranslationValue(string propertyName) const {
  string result("");

  boost::char_separator<char> sep(";");
  Boost_char_tokenizer pairTokenizer(getTranslation(), sep);

  string nameValuePair = *pairTokenizer.begin();
  while (!nameValuePair.empty()) {
      boost::char_separator<char> sep1(":");
      Boost_char_tokenizer valueTokenizer(nameValuePair, sep1);
      Boost_char_tokenizer::iterator ite = valueTokenizer.begin();

    string thisPropertyName = *ite;
    if (thisPropertyName == propertyName) {
      // right now the string looks like this: ":thisvalue;nextproperty:nextvalue"
      // we're going to return up to the ';', and then trim the first character
      ite++;
      Boost_char_tokenizer valueTokenizer_(ite.base(), ite.end(), sep);


      string valueWithColon = *valueTokenizer_.begin();

      result = valueWithColon[1, valueWithColon.length() - 1];
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
