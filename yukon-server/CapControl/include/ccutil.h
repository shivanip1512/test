#pragma once

#include "Attribute.h"
#include "devicetypes.h"
#include "msg_pcrequest.h"
#include "mgr_paosched.h"
#include "amq_connection.h"
#include "amq_queues.h"
#include "CapControlOperationMessage.h"
#include "cctwowaycbcpoints.h"

extern bool _LOG_MAPID_INFO;

class CtiCCCapBank;
class CapControlPao;

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

bool isQualityOk(unsigned quality);

extern const std::set<int> ClosedStates;
extern const std::set<int> OpenStates;

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

class MissingAttribute : public std::exception
{
public:

    MissingAttribute( const long ID, const Attribute & attribute, const std::string & paoType, bool complainFlag = true );

    const char * what() const override;
    const bool complain() const;

private:

    std::string _description;
    bool    _complain;
};

class NoAttributeValue : public std::exception
{

public:

    NoAttributeValue(const long ID, const Attribute & attribute, std::string paoType);

    const char * what() const override;

private:

    std::string _description;
};

void static sendCapControlOperationMessage( Cti::Messaging::CapControl::CapControlOperationMessage * message )
{
    using namespace Cti::Messaging;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    ActiveMQConnectionManager::enqueueMessage( 
            OutboundQueue::CapControlOperationMessage, 
            std::unique_ptr<StreamableMessage>(message));
}


double calculatePowerFactor( const double kvar, const double kwatt );

char populateFlag( const bool flag );
std::string serializeFlag( const bool flag );
bool deserializeFlag( const std::string & flags, const unsigned index = 0 );

extern const std::string SystemUser;

double calculateKVARSolution( const std::string & controlUnits, double setPoint, double varValue, double wattValue,
                              const CapControlPao & pao );

/*
    This function formats the extended map information in a string.  The map info is only supported on
        busses, feeders and capbanks.
*/
template <typename T>
std::string formatMapInfo( const T & object )
{
    return _LOG_MAPID_INFO
        ? " MapID: " + object->getMapLocationId() + " (" + object->getPaoDescription() + ")"
        : "";
}

bool eligibleForVoltageControl( CtiCCCapBank & bank );

}}
