#pragma once
#pragma warning( disable : 4786)

#include "devicetypes.h"
#include "msg_pcrequest.h"
#include "pointattribute.h"

namespace Cti           {
namespace CapControl    {
enum CapControlType
{
    Undefined = 0,
    CapBank,
    Feeder,
    SubBus,
    Substation,
    Area,
    Strategy,
    Schedule,
    SpecialArea,
    ZoneType,                   // added Type suffix to remove warnings from compiler
    VoltageRegulatorType
};


enum Phase
{
    Unknown,
    A,
    B,
    C,
    Poly
};


Phase       resolvePhase( const std::string & p );
std::string desolvePhase( const Phase & p );


CtiRequestMsg* createPorterRequestMsg(long controllerId,const std::string& commandString);
CtiRequestMsg* createPorterRequestMsg(long controllerId,const std::string& commandString, const std::string& user);
bool isQualityOk(unsigned quality);

class MissingPointAttribute : public std::exception
{

public:

    MissingPointAttribute(const long ID, const PointAttribute & attribute, std::string paoType, bool complainFlag = true);

    virtual const char * what( ) const;
    const bool complain( ) const;

private:

    std::string _description;
    bool    _complain;
};
}}
