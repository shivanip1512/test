
#pragma once

#include "ZoneManager.h"


namespace Cti           {
namespace CapControl    {

class ZoneLoader
{

public:

    ZoneLoader()    {  }

    ~ZoneLoader()   {  }

    virtual ZoneManager::ZoneMap load(const long Id) = 0;

private:

};



class ZoneDBLoader : public ZoneLoader
{

public:

    ZoneDBLoader() : ZoneLoader()   {  }

    ~ZoneDBLoader()   {  }

    virtual ZoneManager::ZoneMap load(const long Id);

private:

    void loadCore(const long Id, ZoneManager::ZoneMap &zones);

    void loadBankParameters(const long Id, ZoneManager::ZoneMap &zones);
    void loadPointParameters(const long Id, ZoneManager::ZoneMap &zones);
};

}   // namespace Cti
}   // namespace CapControl

