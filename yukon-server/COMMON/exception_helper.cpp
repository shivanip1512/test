#include "precompiled.h"

#include <SQLAPI.h>
#include <xercesc/util/XMLException.hpp>
#include <xercesc/sax/SAXException.hpp>
#include <boost/thread.hpp>

#include "streamBuffer.h"
#include "exception_helper.h"

namespace Cti {
namespace Logging {


/**
 * Retrieve exception cause if the object is derived from std::exception
 */
std::string getExceptionCause(const std::exception& e)
{
    return e.what();
}

/**
 * Retrieve exception cause if the object is (or is derived from) SAException (SQLAPI)
 */
std::string getExceptionCause(const SAException& e)
{
    return StreamBuffer() << (std::string)e.ErrText() <<" Class "<< e.ErrClass() <<" Pos "<< e.ErrPos() <<" nativeCode "<< e.ErrNativeCode();
}

/**
 * XML exceptions cause defined in COMMON\xml.cpp
 * Retrieve exception cause if the objects are (or are derived from) XMLException / SAXException
 */
extern std::string getExceptionCause(const xercesc::XMLException&);
extern std::string getExceptionCause(const xercesc::SAXException&);

/**
 * Called from inside a catch(...), This methods re-throws and tries to identify the object
 * from known exceptions.
 */
std::string getUnknownExceptionCause()
{
    StreamBuffer cause;

    try
    {
       throw;
    }
    catch(const std::exception &e)
    {
        cause <<"exception "<< typeid(e).name() <<" - "<< getExceptionCause(e);
    }
    catch(const SAException& e)
    {
        cause <<"exception "<< typeid(e).name() <<" - "<< getExceptionCause(e);
    }
    catch(const xercesc::XMLException& e)
    {
        cause <<"exception "<< typeid(e).name() <<" - "<< getExceptionCause(e);
    }
    catch(const xercesc::SAXException& e)
    {
        cause <<"exception "<< typeid(e).name() <<" - "<< getExceptionCause(e);
    }
    catch(const boost::thread_interrupted& e)
    {
        cause <<"exception "<< typeid(e).name();  e;  //  to avoid C4101: 'e': unreferenced local variable
    }
    catch(...)
    {
        cause <<"unknown exception";
    }

    return cause;
}


}
} // namespace Cti::Log

