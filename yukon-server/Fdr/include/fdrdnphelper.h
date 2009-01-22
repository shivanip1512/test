
/*
 *    FILE NAME: fdrscadahelper.h
 *
 *    Copyright (C) 2005 Cannon Technologies, Inc.  All rights reserved.
 *
 */

#ifndef __FDRDNPHELPER_H__
#define __FDRDNPHELPER_H__

#include <windows.h>

#include <map>

#include "dlldefs.h"
#include "pointtypes.h"
#include "fdrdestination.h"

class CtiFDRSocketServer;

/** Scada Helper Class
 * This class is parameterized over an ID type. This ID should be
 * thought of as a translation of the translation string. For example,
 * DNPconverts the translation string into an DnpID struct that
 * contains the four things which identify a point in DNP. 
 */
template<typename T>
class IM_EX_FDRBASE CtiFDRDNPHelper
{
    public:
        CtiFDRDNPHelper(CtiFDRSocketServer* parent);
        virtual ~CtiFDRDNPHelper();

        void addSendMapping(const T& id, const CtiFDRDestination& destination);
        void removeSendMapping(const T& id, const CtiFDRDestination& destination);
        bool getIdForDestination(const CtiFDRDestination& destination, T& result) const;
        void clearMappings();

        std::map<CtiFDRDestination, T> getSendMappings(){return sendMap;};

    private:

        typedef std::map<CtiFDRDestination, T> SendMap;
        SendMap sendMap;


        CtiFDRSocketServer* _parent;
};


#endif  //  #ifndef __FDRSCADAHELPER_H__

