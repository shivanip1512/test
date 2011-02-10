#include "yukon.h"

#include <boost/tokenizer.hpp>


#include "fdrpoint.h"
#include "fdrdestination.h"
#include "logger.h"
#include "guard.h"
/** local definitions **/

CtiFDRDestination::CtiFDRDestination(CtiFDRPoint* parentPoint, const string &translation, const string &destination)
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

bool CtiFDRDestination::operator==(const CtiFDRDestination& other) const
{
    bool eq = false;

    if (iParentPoint->getPointID() == other.getParentPoint()->getPointID())
    {
        if (iDestination == other.iDestination)
        {
            if (iTranslation == other.iTranslation)
            {
                return true;
            }
        }
    }

    return eq;
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
    int pos;
    const string translation = getTranslation();

    boost::char_separator<char> sep(";");
    Boost_char_tokenizer pairTokenizer(translation, sep);
    for ( Boost_char_tokenizer::iterator itr = pairTokenizer.begin() ;
        itr != pairTokenizer.end() ;
        ++itr)
    {
        nameValuePair = *itr;
        //grab the name so we can tell if this is the token we want.
        //Find the  ':' and copy everything before it into a string. This is the name.
        pos = nameValuePair.find(":",0);
        if( pos != -1 )
        {
            string name(nameValuePair,0,pos);
            if (name == propertyName)
            {
              //matched. Everything after pos is the value we want to return.
              result = string (nameValuePair,pos+1,nameValuePair.size());
              break;
            }
        }
    }
    return result;
}

std::ostream& operator<< (std::ostream& os, const CtiFDRDestination& dest)
{
    return os << "[destination " << dest.getDestination()
        << " for " << *dest.getParentPoint() << "]";
}
