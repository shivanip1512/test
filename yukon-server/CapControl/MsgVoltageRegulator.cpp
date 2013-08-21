#include "precompiled.h"

#include "MsgVoltageRegulator.h"
#include "ccid.h"

DEFINE_COLLECTABLE( VoltageRegulatorMessage, CTIVOLTAGEREGULATOR_MSG_ID )

VoltageRegulatorMessage::VoltageRegulatorMessage()
    : Inherited()
{

}

VoltageRegulatorMessage::VoltageRegulatorMessage(const VoltageRegulatorMessage & toCopy)
    : Inherited()
{
    operator=(toCopy);
}

VoltageRegulatorMessage & VoltageRegulatorMessage::operator=(const VoltageRegulatorMessage & rhs)
{
    if( this != &rhs )
    {
        Inherited::operator=(rhs);

        cleanup();
        for each( Cti::CapControl::VoltageRegulator * regulator in rhs.regulators )
        {
            insert(regulator);
        }
    }

    return *this;
}

VoltageRegulatorMessage::~VoltageRegulatorMessage()
{
    cleanup();
}

CtiMessage * VoltageRegulatorMessage::replicateMessage() const
{
    return new VoltageRegulatorMessage(*this);
}

void VoltageRegulatorMessage::insert(Cti::CapControl::VoltageRegulator * regulator)
{
    regulators.push_back( regulator->replicate() );
}

const boost::ptr_vector<Cti::CapControl::VoltageRegulator> VoltageRegulatorMessage::getVoltageRegulators() const
{
    boost::ptr_vector<Cti::CapControl::VoltageRegulator> vec;

    vec.reserve( regulators.size() );

    for each( const Cti::CapControl::VoltageRegulator* regulator in regulators )
    {
        vec.push_back( regulator->replicate() );
    }

    return vec;
}

void VoltageRegulatorMessage::cleanup()
{
    if (regulators.size() > 0)
    {
        delete_container(regulators);
        regulators.clear();
    }
}

