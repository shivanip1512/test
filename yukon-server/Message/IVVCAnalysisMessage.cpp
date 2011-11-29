#include "precompiled.h"
#include "CtiTime.h"
#include "IVVCAnalysisMessage.h"

#include <cms/StreamMessage.h>


namespace Cti           {
namespace Messaging     {
namespace CapControl    {


IVVCAnalysisMessage::IVVCAnalysisMessage( const long      subbusId,
                                          const int       scenarioId,
                                          const CtiTime & timestamp )
    : _subbusId( subbusId ),
      _scenarioId( scenarioId ),
      _timestamp( timestamp.seconds() )
{
    // empty...!
}


IVVCAnalysisMessage * IVVCAnalysisMessage::createCommsRatioMessage( const int       subbusId,
                                                                    const int       scenarioId,
                                                                    const CtiTime & timestamp,
                                                                    const float     actualReceivedPercent,
                                                                    const float     minimumReceivedPercent )
{
    IVVCAnalysisMessage * message = new IVVCAnalysisMessage( subbusId, scenarioId, timestamp );

    message->_floatData.push_back( actualReceivedPercent );
    message->_floatData.push_back( minimumReceivedPercent );

    return message;
}


IVVCAnalysisMessage * IVVCAnalysisMessage::createTapOperationMessage( const int       subbusId,
                                                                      const int       scenarioId,
                                                                      const CtiTime & timestamp,
                                                                      const int       regulatorId )
{
    IVVCAnalysisMessage * message = new IVVCAnalysisMessage( subbusId, scenarioId, timestamp );

    message->_intData.push_back( regulatorId );

    return message;
}


IVVCAnalysisMessage * IVVCAnalysisMessage::createNoTapOpMinTapPeriodMessage( const int       subbusId,
                                                                             const CtiTime & timestamp,
                                                                             const int       minTapPeriodMinutes )
{
    IVVCAnalysisMessage * message = new IVVCAnalysisMessage( subbusId, Scenario_NoTapOperationMinTapPeriod, timestamp );

    message->_intData.push_back( minTapPeriodMinutes );

    return message;
}


IVVCAnalysisMessage * IVVCAnalysisMessage::createNoTapOpNeededMessage( const int       subbusId,
                                                                       const CtiTime & timestamp )
{
    return new IVVCAnalysisMessage( subbusId, Scenario_NoTapOperationNeeded, timestamp );
}


IVVCAnalysisMessage * IVVCAnalysisMessage::createCapbankOperationMessage( const int       subbusId,
                                                                          const int       scenarioId,
                                                                          const CtiTime & timestamp,
                                                                          const int       capbankId )
{
    IVVCAnalysisMessage * message = new IVVCAnalysisMessage( subbusId, scenarioId, timestamp );

    message->_intData.push_back( capbankId );

    return message;
}


IVVCAnalysisMessage * IVVCAnalysisMessage::createExceedMaxKVarMessage( const int       subbusId,
                                                                       const CtiTime & timestamp,
                                                                       const int       capbankId,
                                                                       const int       maxKVar )
{
    IVVCAnalysisMessage * message = new IVVCAnalysisMessage( subbusId, Scenario_ExceededMaxKVar, timestamp );

    message->_intData.push_back( capbankId );
    message->_intData.push_back( maxKVar );

    return message;
}


void IVVCAnalysisMessage::streamInto( cms::StreamMessage & message ) const
{
    message.writeLong( _subbusId );
    message.writeLong( _timestamp );
    message.writeInt( _scenarioId );
    message.writeInt( _intData.size() );
    message.writeInt( _floatData.size() );

    for each ( int value in  _intData )
    {
        message.writeInt( value );
    }

    for each ( float value in  _floatData )
    {
        message.writeFloat( value );
    }
}


}
}
}

