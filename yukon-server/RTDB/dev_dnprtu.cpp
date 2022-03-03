#include "precompiled.h"

#include "dev_dnprtu.h"
#include "mgr_point.h"

using Cti::Logging::Set::operator<<;

namespace Cti {
namespace Devices {

auto DnpRtuDevice::getDevicePointsByType() const -> PointsByType
{
    PointsByType devicePointsByType;

    std::vector<CtiPointSPtr> hierarchyPoints;

    getDevicePoints(hierarchyPoints);

    for( auto childDeviceId : _childDevices )
    {
        _pointMgr->getEqualByPAO(childDeviceId, hierarchyPoints);
    }

    for( auto& pt : hierarchyPoints )
    {
        if( ! pt->isPseudoPoint() )
        {
            auto result = devicePointsByType[pt->getType()].emplace(pt->getPointOffset(), pt);

            if( !result.second )
            {
                auto& existingPoint = *result.first->second;

                CTILOG_WARN(dout, "Hierarchy point collision, keeping first encountered point:" + FormattedList::of(
                    "Device ID", getID(),
                    "Device name", getName(),
                    "Point type", pt->getType(),
                    "Point offset", pt->getPointOffset(),
                    "Retained point ID", existingPoint.getPointID(),
                    "Retained device ID", existingPoint.getDeviceID(),
                    "Rejected point ID", pt->getPointID(),
                    "Rejected device ID", pt->getDeviceID()));
            }
        }
    }

    return devicePointsByType;
}

CtiPointSPtr DnpRtuDevice::getDeviceControlPointOffsetEqual(int offset)
{
    if( _executeId && _pointMgr )
    {
        return _pointMgr->getControlOffsetEqual(_executeId, offset);
    }

    return Inherited::getDeviceControlPointOffsetEqual(offset);
}

CtiPointSPtr DnpRtuDevice::getDevicePointByID(int pointid)
{
    if( _executeId && _pointMgr )
    {
        return _pointMgr->getPoint(pointid, _executeId);
    }

    return Inherited::getDevicePointByID(pointid);
}

YukonError_t DnpRtuDevice::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    ScopeExit resetExecuteId;

    //  Does the message specify a child device ID?
    if( pReq->OptionsField() )
    {
        const auto childDeviceId = pReq->OptionsField();

        if( ! _childDevices.count(childDeviceId) )
        {
            CTILOG_ERROR(dout, "Received a request message that is not from a child device:" << FormattedList::of(
                "Device ID", getID(),
                "Device name", getName(),
                "Request device ID", childDeviceId,
                "Child devices", _childDevices));

            return insertReturnMsg(ClientErrors::ChildDeviceUnknown, OutMessage, retList, 
                        "Unknown child device ID " + std::to_string(pReq->OptionsField()));
        }

        _executeId = childDeviceId;

        resetExecuteId.reset( [this] { 
            _executeId = 0; 
        } );
    }

    if( parse.getCommand() == ScanRequest &&
        parse.getiValue("scantype") == ScanRateIntegrity )
    {
        if( _executeId )
        {
            const auto Now = std::chrono::steady_clock::now();

            if( _lastIntegrityScan + _childScanQuietPeriod > Now )
            {
                const auto lastScan = std::chrono::duration_cast<std::chrono::milliseconds>(Now - _lastIntegrityScan);

                return insertReturnMsg(ClientErrors::None, OutMessage, retList,
                            std::string("Parent RTU already scanned ") + std::to_string(lastScan.count()) + " millis ago");
            }
            else
            {
                _childScanQuietPeriod = 
                    std::chrono::duration_cast<std::chrono::seconds>(
                        gConfigParms.getValueAsDuration("DNP_RTU_CHILD_SCAN_QUIET_PERIOD", _childScanQuietPeriod));
            }
        }

        _lastIntegrityScan = std::chrono::steady_clock::now();
    }

    return Inherited::ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList);
    
}

void DnpRtuDevice::addChildDevice(const long childDeviceId)
{
    _childDevices.insert(childDeviceId);
}

void DnpRtuDevice::removeChildDevice(const long childDeviceId)
{
    _childDevices.erase(childDeviceId);
}

}
}
