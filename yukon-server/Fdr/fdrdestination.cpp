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
  string nameValuePair;
  int n, pos;
  char *buf;

  boost::char_separator<char> sep(";");
  Boost_char_tokenizer pairTokenizer(getTranslation(), sep);

  //string nameValuePair = *pairTokenizer.begin();
  for ( Boost_char_tokenizer::iterator itr = pairTokenizer.begin() ;
        itr != pairTokenizer.end() ; 
        ++itr) 
  {
      nameValuePair = *itr;
      n = nameValuePair.size();
      buf = new char [n];
      //grab the name so we can tell if this is the token we want.

      //Find the  ':' and copy everything before it into a string. This is the name.
      pos = nameValuePair.find(":",0);
      pos = nameValuePair.copy(buf,pos);
      buf[pos] = '\0';

    if (buf == propertyName) {
      //matched. Everything after pos is the value we want to return.
      pos = nameValuePair.copy(buf,n,++pos);
      buf[pos] = '\0';
      
      result = buf;
      delete buf;
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
