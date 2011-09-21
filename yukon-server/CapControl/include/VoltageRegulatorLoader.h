

#pragma once

#include "VoltageRegulatorManager.h"


namespace Cti           {
namespace CapControl    {

class VoltageRegulatorLoader
{

public:

    VoltageRegulatorLoader()    {  }

    ~VoltageRegulatorLoader()   {  }

    virtual VoltageRegulatorManager::VoltageRegulatorMap load(const long Id) = 0;

protected:

};



class VoltageRegulatorDBLoader : public VoltageRegulatorLoader
{

public:

    VoltageRegulatorDBLoader() : VoltageRegulatorLoader()   {  }

    ~VoltageRegulatorDBLoader()   {  }

    virtual VoltageRegulatorManager::VoltageRegulatorMap load(const long Id);

private:

    void loadCore(const long Id, VoltageRegulatorManager::VoltageRegulatorMap &voltageRegulators);
    void loadPoints(const long Id, VoltageRegulatorManager::VoltageRegulatorMap &voltageRegulators);

};

}
}

