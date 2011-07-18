/*
 *
 *    Copyright (C) 2005 Cannon Technologies, Inc.  All rights reserved.
 *
 */
#include "precompiled.h"

#include <windows.h>
#include <iostream>
#include <sstream>

#include "msg_cmd.h"
#include "fdrdebuglevel.h"

// this class header
#include "fdrdnphelper.h"

using std::endl;

template<typename T>
CtiFDRDNPHelper<T>::CtiFDRDNPHelper(CtiFDRSocketServer* parent)
{
    _parent = parent;
    if (_parent->getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        _parent->logNow() << "Helper created" << endl;;
    }
}

template<typename T>
CtiFDRDNPHelper<T>::~CtiFDRDNPHelper()
{
    if (_parent->getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        _parent->logNow() << "Helper destroyed" << endl;;
    }
}

template<typename T>
void CtiFDRDNPHelper<T>::addSendMapping(const T& id, const CtiFDRDestination& pointDestination)
{
    sendMap[pointDestination] = id;
    if (_parent->getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        _parent->logNow() << "Added send mapping " << pointDestination << " to " << id << endl;
    }
}


template<typename T>
void CtiFDRDNPHelper<T>::removeSendMapping(const T& id, const CtiFDRDestination& pointDestination)
{
    //just call erase, there can only be one
    sendMap.erase(pointDestination);
    if (_parent->getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        _parent->logNow() << "Removing send mapping " << pointDestination << " to " << id << endl;
    }
}


template<typename T>
bool CtiFDRDNPHelper<T>::getIdForDestination(const CtiFDRDestination& pointDestination, T& result) const
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
void CtiFDRDNPHelper<T>::clearMappings()
{
    sendMap.clear();
}


#include "fdrdnpslave.h"
template class IM_EX_FDRBASE CtiFDRDNPHelper<CtiDnpId>;


