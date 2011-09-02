#pragma once

#include "yukon.h"

#include <boost/shared_ptr.hpp>
#include "boostutil.h"
using boost::shared_ptr;

#include "logger.h"
#include "dllbase.h"
#include <string>

class CtiConfigManager;

namespace Cti       {
namespace Config    {

class IM_EX_CONFIG Base
{
    friend class CtiConfigManager;
protected:
    virtual int getProtectedResolvedKey(std::string key);
    virtual bool setProtectedValueWithKey(const std::string &value, const int key);

private:

public:
    Base();
    virtual ~Base();

//    void setID(string ID);//String Identifier stored in Database.
//    string getID();
    virtual CtiConfig_type getType();

    virtual std::string getOutputStrings();
/*    virtual int getResolvedKey(Rstring key);
    virtual string getValueFromKey(const int key);
    virtual long getLongValueFromKey(const int key);
    virtual bool setValueWithKey(const string &value,const int key);*/
};



typedef shared_ptr< Base > BaseSPtr;

}
}
