#include "precompiled.h"

#include "ctitime.h"
using namespace std;
#include "fdrpoint.h"
#include "fdrdestination.h"
#include "pointdefs.h"

#include "logger.h"
#include "utility.h"

/** local definitions **/


CtiFDRPoint::CtiFDRPoint( long pointID)
:   iPointID(pointID),
    iPointOffset(-1),
    iPaoID(-1),
    iControllable(false),
    iUnsolicited(false),
    iMultiplier(1.0),
    iOffset(0.0),
    iPointType (InvalidPointType),
    iValue (0.0),
    iQuality (UnintializedQuality),
    iLastTimeStamp(PASTDATE + (86400 * 10))
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
        iUnsolicited = other.isUnsolicited();
        iDestinationList = other.getDestinationList();
        iValue = other.getValue();
        iQuality = other.getQuality();
        iPaoID = other.getPaoID();
        iPointOffset = other.getPointOffset();
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

bool CtiFDRPoint::isUnsolicited( void ) const
{
    return iUnsolicited;
}

CtiFDRPoint &  CtiFDRPoint::setUnsolicited( const bool unsolicited )
{
    iUnsolicited = unsolicited;
    return *this;
}

CtiTime CtiFDRPoint::getLastTimeStamp ( void ) const
{
    return iLastTimeStamp;
}


CtiFDRPoint &  CtiFDRPoint::setLastTimeStamp( const CtiTime & aTimeStamp )
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

long CtiFDRPoint::getPaoID() const
{
    return iPaoID;
}
void CtiFDRPoint::setPaoID(long aPaoID)
{
    iPaoID = aPaoID;
    return;
}

long CtiFDRPoint::getPointOffset() const
{
    return iPointOffset;
}
void CtiFDRPoint::setPointOffset(long aPointOffset)
{
    iPointOffset = aPointOffset;
    return;
}


string CtiFDRPoint::getTranslateName( string &aDestinationName )
{
    string retVal;
    int entries = iDestinationList.size();

    for (int x=0; x < entries; x++)
    {
        if(ciStringEqual(iDestinationList[x].getDestination(), aDestinationName))
            retVal = iDestinationList[x].getTranslation();
    }

    return retVal;
}

string CtiFDRPoint::getTranslateName( int aIndex )
{
    string retVal;

    if (iDestinationList.size() >= aIndex+1)
        retVal = iDestinationList[aIndex].getTranslation();

    return retVal;
}

std::ostream& operator<< (std::ostream& os, const CtiFDRPoint& point)
{
    return os << "[point id " << point.getPointID() << "]";
}

std::string CtiFDRPoint::toString() const
{
    std::ostringstream oss;
    oss << *this;
    return oss.str();
}

bool CtiFDRPoint::isCommStatus()
{
    return (iPointOffset == 2000 ? true : false);
}

