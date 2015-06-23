#include "precompiled.h"

#include "fdrscadahelper.h"

#include "msg_cmd.h"
#include "fdrdebuglevel.h"
#include "logger.h"

using std::string;
using std::endl;

static bool checkStatusType(CtiPointType_t type);
static bool checkValueType(CtiPointType_t type);


template<typename T>
CtiFDRScadaHelper<T>::CtiFDRScadaHelper(CtiFDRScadaServer* parent)
{
    _parent = parent;
    if (_parent->getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, _parent->logNow() << "Helper created");
    }
}

template<typename T>
CtiFDRScadaHelper<T>::~CtiFDRScadaHelper()
{
    if (_parent->getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, _parent->logNow() << "Helper destroyed");
    }
}

template<typename T>
bool CtiFDRScadaHelper<T>::handleValueUpdate(const T& id, double rawValue,
                                             int quality, CtiTime timestamp) const
{
    if (_parent->getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, _parent->logNow() << "Handling value update message for "<< id <<" with value=" << rawValue);
    }

    // call generic update function with pointer to the
    // "value" Point Type checker function
    return handleUpdate(id, rawValue, quality, timestamp, &checkValueType);
}

template<typename T>
bool CtiFDRScadaHelper<T>::handleStatusUpdate(const T& id, int value,
                                              int quality, CtiTime timestamp) const
{
    if (value == STATE_INVALID)
    {
        CTILOG_ERROR(dout, _parent->logNow() <<"Invalid status for "<< id);
        return false;
    }

    if (_parent->getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, _parent->logNow() <<"Handling status update message for "<< id <<" with state=" << value);
    }

    // call generic update function with pointer to the
    // "status" Point Type checker function
    return handleUpdate(id, value, quality, timestamp, &checkStatusType);
}

template<typename T>
bool CtiFDRScadaHelper<T>::handleUpdate(const T& id, double rawValue, int quality,
                                        CtiTime timestamp, CheckStatusFunc checkFunc) const
{
    bool sentAPoint = false;
    ReceiveMap::const_iterator destIter, destEnd;
    destIter = receiveMap.lower_bound(id);
    destEnd = receiveMap.upper_bound(id);
    for (; destIter != destEnd; ++destIter)
    {
        const CtiFDRDestination& dest = (*destIter).second;
        CtiFDRPoint& point = *dest.getParentPoint();

        if (!(*checkFunc)(point.getPointType()))
        {
            if (_parent->getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, _parent->logNow() <<"Point type mismatch for "<< dest <<" and "<< id);
            }
            continue;
        }

        double value = rawValue;

        if (checkValueType(point.getPointType())) // is this correct???
        {
            value *= point.getMultiplier();
            value += point.getOffset();
        }

        if (timestamp == PASTDATE)
        {
            if (_parent->getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, _parent->logNow() <<"Value received with an invalid timestamp ("<< timestamp <<")");
            }
            continue;
        }
        CtiPointDataMsg* pData;
        pData = new CtiPointDataMsg(point.getPointID(),
                                    value,
                                    quality,
                                    point.getPointType());

        pData->setTime(timestamp);

        // consumes memory
        _parent->queueMessageToDispatch(pData);

        if (_parent->getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, _parent->logNow() <<"New value of "<< value <<" updated for "<< point);
        }

        sentAPoint = true;
    }

    if (!sentAPoint)
    {
        if (_parent->getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, _parent->logNow() <<"No matching point/destination found for "<< id);
        }
    }
    return sentAPoint;
}

template<typename T>
bool CtiFDRScadaHelper<T>::handleControl(const T& id, int controlState) const
{
    if (_parent->getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, _parent->logNow() <<"Handling control message for "<< id <<" with controlstate="<< controlState);
    }

    if (controlState == STATE_INVALID)
    {
        CTILOG_ERROR(dout, _parent->logNow() <<"Invalid control state for "<< id);

        _parent->logEvent(
                Cti::StreamBuffer() << _parent->getInterfaceName() << ": Invalid control state received from " << id, "", true);

        return false;
    }

    bool sentAControl = false;
    ReceiveMap::const_iterator destIter, destEnd;
    destIter = receiveMap.lower_bound(id);
    destEnd = receiveMap.upper_bound(id);
    for (; destIter != destEnd; ++destIter)
    {
        const CtiFDRDestination& dest = (*destIter).second;
        CtiFDRPoint& point = *dest.getParentPoint();

        if (!checkStatusType(point.getPointType()))
        {
            CTILOG_ERROR(dout, _parent->logNow() <<"Foreign control point "<< id <<
                    " was mapped to non-control point "<< dest);

            _parent->logEvent(
                    Cti::StreamBuffer() << _parent->getInterfaceName() << ": Foreign control point " << id << " was mapped to non-control point " << dest, "");

            continue;
        }

        if (!point.isControllable())
        {
            CTILOG_ERROR(dout, _parent->logNow() <<"Foreign control point "<< id
                    <<" was mapped to "<< dest <<", which is not configured for control");

            _parent->logEvent(
                    Cti::StreamBuffer() << _parent->getInterfaceName() << ": Control point " << id << " was mapped to " << dest << ", which is not configured for control", "");

            continue;
        }

        // make sure the value is valid
        // build the command message and send the control
        CtiCommandMsg *cmdMsg;
        cmdMsg = new CtiCommandMsg(CtiCommandMsg::ControlRequest);

        cmdMsg->insert( -1 );                // This is the dispatch token and is unimplemented at this time
        cmdMsg->insert(0);                   // device id, unknown at this point, dispatch will find it
        cmdMsg->insert(point.getPointID());  // point for control
        cmdMsg->insert(controlState);
        _parent->sendMessageToDispatch(cmdMsg);

        if (_parent->getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, _parent->logNow() << "Control message of "<< (controlState == STATE_OPENED ? "OPENED" : "CLOSED") <<
                    " sent to " << dest << " for " << id);
        }
        sentAControl = true;
    }

    if (!sentAControl)
    {
        if (_parent->getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, _parent->logNow() <<"No matching point/destination found for "<< id);
        }
    }
    return sentAControl;
}

template<typename T>
void CtiFDRScadaHelper<T>::addSendMapping(const T& id, const CtiFDRDestination& pointDestination)
{
    sendMap[pointDestination] = id;
    if (_parent->getDebugLevel () & TRANSLATION_LOADING_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, _parent->logNow() <<"Added send mapping "<< pointDestination <<" to "<< id);
    }
}

template<typename T>
void CtiFDRScadaHelper<T>::addReceiveMapping(const T& id, const CtiFDRDestination& pointDestination)
{
    receiveMap.insert(ReceiveMap::value_type(id, pointDestination));
    if (_parent->getDebugLevel () & TRANSLATION_LOADING_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, _parent->logNow() << "Added receive mapping "<< id <<" to "<< pointDestination);
    }
}

template<typename T>
void CtiFDRScadaHelper<T>::removeSendMapping(const T& id, const CtiFDRDestination& pointDestination)
{
    //just call erase, there can only be one
    sendMap.erase(pointDestination);
    if (_parent->getDebugLevel () & TRANSLATION_LOADING_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, _parent->logNow() <<"Removing send mapping "<< pointDestination << " to " << id);
    }
}

template<typename T>
void CtiFDRScadaHelper<T>::removeReceiveMapping(const T& id, const CtiFDRDestination& pointDestination)
{
    //Remove on the iterator that matches id and pointdestination
    ReceiveMap::iterator itr;
    for (itr = receiveMap.equal_range(id).first; itr != receiveMap.equal_range(id).second; itr++ )
    {
        if ((*itr).second == pointDestination)
        {
            receiveMap.erase(itr);
            break;
        }
    }

    if (_parent->getDebugLevel () & TRANSLATION_LOADING_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, _parent->logNow() <<"Removing receive mapping "<< id <<" to "<< pointDestination);
    }
}

template<typename T>
bool CtiFDRScadaHelper<T>::getIdForDestination(const CtiFDRDestination& pointDestination, T& result) const
{
    SendMap::const_iterator iter = sendMap.find(pointDestination);
    if (iter == sendMap.end())
    {
        return false;
    }
    result = (*iter).second;
    return true;
}

template<typename T>
void CtiFDRScadaHelper<T>::clearMappings()
{
    sendMap.clear();
    receiveMap.clear();
}

static bool checkStatusType(CtiPointType_t type)
{
    return type == StatusPointType;
}
static bool checkValueType(CtiPointType_t type)
{
    return type == AnalogPointType ||
            type == PulseAccumulatorPointType ||
            type == DemandAccumulatorPointType ||
            type == CalculatedPointType;
}

// The following must be declared in the base DLL. Probably because
// that is where the template itself is declared, but I really
// don't know. Also, the class that it is declared on (CtkAcsId in the
// first case) must be declared in the base DLL.
#include "fdracsmulti.h"
template class IM_EX_FDRBASE CtiFDRScadaHelper<CtiAcsId>;

#include "fdrvalmetmulti.h"
template class IM_EX_FDRBASE CtiFDRScadaHelper<CtiValmetPortId>;

