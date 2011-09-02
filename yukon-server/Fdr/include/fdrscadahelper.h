#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include <map>

#include "dlldefs.h"
#include "pointtypes.h"
#include "fdrdestination.h"

class CtiFDRScadaServer;

/** Scada Helper Class
 * This class is parameterized over an ID type. This ID should be
 * thought of as a translation of the translation string. For example,
 * ACS converts the translation string into an AcsID struct that contains
 * the three things which identify a point in ACS.
 */
template<typename T>
class IM_EX_FDRBASE CtiFDRScadaHelper
{
    public:
        CtiFDRScadaHelper(CtiFDRScadaServer* parent);
        virtual ~CtiFDRScadaHelper();

        bool handleValueUpdate(const T& id, double rawValue, int quality, CtiTime timestamp) const;
        bool handleStatusUpdate(const T& id, int value, int quality, CtiTime timestamp) const;
        bool handleControl(const T& id, int controlState) const;
        void addSendMapping(const T& id, const CtiFDRDestination& destination);
        void removeSendMapping(const T& id, const CtiFDRDestination& destination);
        void addReceiveMapping(const T& id, const CtiFDRDestination& destination);
        void removeReceiveMapping(const T& id, const CtiFDRDestination& destination);
        bool getIdForDestination(const CtiFDRDestination& destination, T& result) const;
        void clearMappings();

    private:
        typedef bool(*CheckStatusFunc)(CtiPointType_t);

        bool handleUpdate(const T& id, double value, int quality,
                          CtiTime timestamp, CheckStatusFunc checkFunc) const;

        typedef std::map<CtiFDRDestination, T> SendMap;
        SendMap sendMap;
        typedef std::multimap<T, CtiFDRDestination> ReceiveMap;
        ReceiveMap receiveMap;


        CtiFDRScadaServer* _parent;
};
