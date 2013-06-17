#pragma once

#include "MsgCapControlMessage.h"

#include "VoltageRegulator.h"

class VoltageRegulatorMessage : public CapControlMessage
{
    RWDECLARE_COLLECTABLE( VoltageRegulatorMessage )

    private:
        typedef CapControlMessage Inherited;

    public:
        VoltageRegulatorMessage();
        VoltageRegulatorMessage(const VoltageRegulatorMessage & toCopy);
        VoltageRegulatorMessage & operator=(const VoltageRegulatorMessage & rhs);

        virtual ~VoltageRegulatorMessage();

        virtual CtiMessage * replicateMessage() const;

        void saveGuts(RWvostream & ostrm) const;

        void insert(Cti::CapControl::VoltageRegulator * regulator);

    private:
        void cleanup();

        std::vector<Cti::CapControl::VoltageRegulator *> regulators;
};

