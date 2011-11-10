#include "precompiled.h"

#include "MsgVoltageRegulator.h"
#include "ccid.h"

RWDEFINE_COLLECTABLE( VoltageRegulatorMessage, CTIVOLTAGEREGULATOR_MSG_ID )

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

void VoltageRegulatorMessage::saveGuts(RWvostream & ostrm) const
{
    Inherited::saveGuts(ostrm);

    ostrm << regulators;
}

void VoltageRegulatorMessage::insert(Cti::CapControl::VoltageRegulator * regulator)
{
    regulators.push_back( regulator->replicate() );
}

void VoltageRegulatorMessage::cleanup()
{
    if (regulators.size() > 0)
    {
        delete_container(regulators);
        regulators.clear();
    }
}

