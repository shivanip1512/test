#pragma once

#include "pointattribute.h"
#include "devicetypes.h"
#include "msg_pcrequest.h"
#include "mgr_paosched.h"
#include "amq_connection.h"
#include "CapControlOperationMessage.h"

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
    VoltageRegulatorType,
    MonitorPoint
};


enum ReservedPointOffsets
{
    Offset_EstimatedVarLoad                     = 1,
    Offset_CapbankControlStatus                 = 1,
    Offset_CapbankOperationAnalog               = 1,
    Offset_DailyOperations                      = 2,
    Offset_PowerFactor                          = 3,
    Offset_EstimatedPowerFactor                 = 4,
    Offset_CommsState                           = 300,
    Offset_PaoIsDisabled                        = 500,
    Offset_OperationSuccessPercentRangeMin      = 10000,
    Offset_OperationSuccessPercentRangeMax      = 10003,
    Offset_ConfirmationSuccessPercentRangeMin   = 10010,
    Offset_ConfirmationSuccessPercentRangeMax   = 10013
};


enum Phase
{
    Phase_Unknown,
    Phase_A,
    Phase_B,
    Phase_C,
    Phase_Poly
};

Phase       resolvePhase( const std::string & p );
std::string desolvePhase( const Phase & p );

std::set<int> initClosedStates();
std::set<int> initOpenStates();
CtiRequestMsg* createPorterRequestMsg(long controllerId,const std::string& commandString);
CtiRequestMsg* createPorterRequestMsg(long controllerId,const std::string& commandString, const std::string& user);
bool isQualityOk(unsigned quality);

static const std::set<int> ClosedStates = initClosedStates();
static const std::set<int> OpenStates = initOpenStates();

template<class T>
bool setVariableIfDifferent(T &original, const T &updated)
{
    if (original != updated)
    {
        original = updated;
        return true;
    }
    return false;
}

CtiPAOScheduleManager::VerificationStrategy ConvertIntToVerificationStrategy(int verifyId);

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

void static sendCapControlOperationMessage( Cti::Messaging::CapControl::CapControlOperationMessage * message )
{
    using namespace Cti::Messaging;

    std::auto_ptr<StreamableMessage> msg( message );
    Cti::Messaging::gActiveMQConnection.enqueueMessage( ActiveMQConnectionManager::Queue_CapControlOperationMessage, msg );
}


double calculatePowerFactor( const double kvar, const double kwatt );



}}
