#pragma once

#include "MsgCapControlMessage.h"

#include "VoltageRegulator.h"

#include <boost/ptr_container/ptr_vector.hpp>

class VoltageRegulatorMessage : public CapControlMessage
{
    public:
        DECLARE_COLLECTABLE( VoltageRegulatorMessage );

    private:
        typedef CapControlMessage Inherited;

    public:
        VoltageRegulatorMessage();
        VoltageRegulatorMessage(const VoltageRegulatorMessage & toCopy);
        VoltageRegulatorMessage & operator=(const VoltageRegulatorMessage & rhs);

        virtual ~VoltageRegulatorMessage();

        virtual CtiMessage * replicateMessage() const;

        void insert(Cti::CapControl::VoltageRegulator * regulator);

        const boost::ptr_vector<Cti::CapControl::VoltageRegulator> getVoltageRegulators() const;

    private:
        void cleanup();

        std::vector<Cti::CapControl::VoltageRegulator *> regulators;
};

