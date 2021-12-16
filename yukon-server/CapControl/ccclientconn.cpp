#include "precompiled.h"

#include "capcontroller.h"
#include "logger.h"
#include "ccclientconn.h"

using namespace std;

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCClientConnection::CtiCCClientConnection(const std::string& replyDestination, const std::string& serverQueueName) :
_valid(true),
_connection( replyDestination, serverQueueName, & CtiCapController::getInClientMsgQueueHandle() )
{
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCClientConnection::~CtiCCClientConnection()
{
    CTILOG_INFO(dout, "Client Connection closing.");

    try
    {
        close();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    CTILOG_INFO(dout, "Client Connection closed.");
}

/*---------------------------------------------------------------------------
    isValid

    Returns true is the connection is valid, false otherwise
---------------------------------------------------------------------------*/
bool CtiCCClientConnection::isValid()
{
    if( ! _connection.isConnectionUsable() )
    {
        _valid = false;
    }

    return _valid;
}

/*---------------------------------------------------------------------------
    close

    Closes the connection
---------------------------------------------------------------------------*/
void CtiCCClientConnection::close()
{
    _valid = false;

    _connection.close();
}

/*---------------------------------------------------------------------------
    write

    Writes a message into the queue which will be sent to the client.
---------------------------------------------------------------------------*/
void CtiCCClientConnection::write( CtiMessage* msg )
{
    _connection.WriteConnQue(msg, CALLSITE);
}

/*---------------------------------------------------------------------------
    start

    start the connection
---------------------------------------------------------------------------*/
void CtiCCClientConnection::start()
{
    try
    {
        _connection.start();
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        _valid = FALSE;
    }
}
