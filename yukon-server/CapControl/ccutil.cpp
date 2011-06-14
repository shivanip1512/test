/*---------------------------------------------------------------------------
        Filename:  ccstatsobject.cpp
        
        Programmer:  Julie Richter
                
        Description:    CCStatsObject
                        

        Initial Date:  2/24/2009
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2009
---------------------------------------------------------------------------*/

#include "yukon.h"
#include "ccutil.h"
#include "pointdefs.h"
 
using std::string;

extern ULONG _MSG_PRIORITY;
namespace Cti           {
namespace CapControl    {

CtiRequestMsg* createPorterRequestMsg(long controllerId,const string& commandString) 
{
    CtiRequestMsg* reqMsg = new CtiRequestMsg(controllerId, commandString);
    reqMsg->setMessagePriority(_MSG_PRIORITY);
    return reqMsg;
};

CtiRequestMsg* createPorterRequestMsg(long controllerId,const string& commandString, const string& user) 
{
    CtiRequestMsg* reqMsg = createPorterRequestMsg(controllerId, commandString);
    reqMsg->setUser(user);
    return reqMsg;
};

bool isQualityOk(unsigned quality)
{
    if (quality == NormalQuality || quality == ManualQuality)
    {
        return true;
    }
    return false;
}

MissingPointAttribute::MissingPointAttribute(const long ID, const PointAttribute & attribute, string paoType, bool complainFlag)
    : std::exception(),
      _description("Missing Point Attribute: '")
{
    _complain = complainFlag;
    _description += attribute.name() + "' on "+ paoType +" with ID: " + CtiNumStr(ID);
}


const char * MissingPointAttribute::what( ) const
{
    return _description.c_str();
}
const bool MissingPointAttribute::complain( ) const
{
    return _complain;
}


Phase resolvePhase( const std::string & p )
{
    if ( p == "A" )
    {
        return A;
    }
    else if ( p == "B" )
    {
        return B;
    }
    else if ( p == "C" )
    {
        return C;
    }
    else if ( p == "*" )
    {
        return Poly;
    }
//    else if ( p == "?" )

    return Unknown;
}


std::string desolvePhase( const Phase & p )
{
    switch ( p )
    {
        case A:
        {
            return "A";
        }
        case B:
        {
            return "B";
        }
        case C:
        {
            return "C";
        }
    
        case Poly:
        {
            return "*";
        }
//        default:
//        case Unknown:
    }

    return "?";
}


}
}
