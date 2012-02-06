#include "precompiled.h"

#include "dev_cbc7020.h"

#include "numstr.h"

using namespace std;

namespace Cti {
namespace Devices {

const char *Cbc7020Device::PutConfigPart_all             = "all";
const char *Cbc7020Device::PutConfigPart_comms_lost      = "commslost";
const char *Cbc7020Device::PutConfigPart_control_times   = "controltimes";
const char *Cbc7020Device::PutConfigPart_data_logging    = "logging";
const char *Cbc7020Device::PutConfigPart_dnp             = "dnp";
const char *Cbc7020Device::PutConfigPart_fault_detection = "faultdetection";
const char *Cbc7020Device::PutConfigPart_neutral_current = "neutralcurrent";
const char *Cbc7020Device::PutConfigPart_time_temp_1     = "timeandtemp1";
const char *Cbc7020Device::PutConfigPart_time_temp_2     = "timeandtemp2";
const char *Cbc7020Device::PutConfigPart_udp             = "udp";
const char *Cbc7020Device::PutConfigPart_voltage         = "voltage";

const Cbc7020Device::ConfigPartsList Cbc7020Device::_config_parts = Cbc7020Device::initConfigParts();

Cbc7020Device::ConfigPartsList Cbc7020Device::initConfigParts()
{
    Cbc7020Device::ConfigPartsList tempList;
    tempList.push_back(Cbc7020Device::PutConfigPart_all);
    tempList.push_back(Cbc7020Device::PutConfigPart_comms_lost);
    tempList.push_back(Cbc7020Device::PutConfigPart_control_times);
    tempList.push_back(Cbc7020Device::PutConfigPart_data_logging);
    tempList.push_back(Cbc7020Device::PutConfigPart_dnp);
    tempList.push_back(Cbc7020Device::PutConfigPart_fault_detection);
    tempList.push_back(Cbc7020Device::PutConfigPart_neutral_current);
    tempList.push_back(Cbc7020Device::PutConfigPart_time_temp_1);
    tempList.push_back(Cbc7020Device::PutConfigPart_time_temp_2);
    tempList.push_back(Cbc7020Device::PutConfigPart_udp);
    tempList.push_back(Cbc7020Device::PutConfigPart_voltage);

    return tempList;
}

INT Cbc7020Device::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT nRet = NoMethod;
    bool didExecute = false;

    //  if it's a control open/close request without an offset
    if( (parse.getCommand() == ControlRequest) && !(parse.getFlags() & CMD_FLAG_OFFSET) &&
        (parse.getFlags() & CMD_FLAG_CTL_OPEN || parse.getFlags() & CMD_FLAG_CTL_CLOSE) )
    {
        if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
        {
            pReq->setCommandString("control open offset 1");
        }
        else // if( parse.getFlags() & CMD_FLAG_CTL_CLOSE ) - implied because of the above if condition
        {
            pReq->setCommandString("control close offset 1");
        }

        CtiCommandParser new_parse(pReq->CommandString());

        //  NOTE the new parser I'm passing in - i've already touched the pReq string, so
        //    i need to seal the deal with a new parse
        nRet = Inherited::ExecuteRequest(pReq, new_parse, OutMessage, vgList, retList, outList);
    }
    /*else if( parse.getCommand() == PutConfigRequest && parse.isKeyValid("install") )
    {
        nRet = executePutConfig( pReq, parse, OutMessage, vgList, retList, outList );
    }*/
    else
    {
        nRet = Inherited::ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;
}


//  This overrides the processPoints function in dev_dnp, but calls it afterward to do the real processing
void Cbc7020Device::processPoints( Protocol::Interface::pointlist_t &points )
{
    Protocol::Interface::pointlist_t::iterator pt_itr, last_pos;

    last_pos    = points.end();

    //  we need to find any status values for offsets 1 and 2
    for( pt_itr = points.begin(); pt_itr != last_pos; pt_itr++ )
    {
        CtiPointDataMsg *pt_msg = *pt_itr;

        if( pt_msg &&
            pt_msg->getType() == AnalogPointType &&
            pt_msg->getId()   == PointOffset_FirmwareRevision )
        {
            unsigned int value = pt_msg->getValue();

            //  convert 0x[hibyte][lobyte] into lobyte.hibyte
            //  i.e. 0x0208 = 8.02
            double firmware = static_cast<double>(value & 0xff) +
                              static_cast<double>((value >> 8) & 0xff) / 100;

            pt_msg->setValue(firmware);
        }
    }

    //  do the final processing
    Inherited::processPoints(points);
}

/*INT Cbc7020Device::executePutConfig(CtiRequestMsg                  *pReq,
                                   CtiCommandParser               &parse,
                                   OUTMESS                        *&OutMessage,
                                   list< CtiMessage* >      &vgList,
                                   list< CtiMessage* >      &retList,
                                   list< OUTMESS* >         &outList)
{
    bool  found = false;
    INT   nRet = NoError, sRet;

    if( parse.isKeyValid("install") )
    {
        if( parse.getsValue("installvalue") == PutConfigPart_all )
        {
            if(!_config_parts.empty())
            {
                CtiRequestMsg tempReq = CtiRequestMsg(*pReq);
                tempReq.setConnectionHandle(pReq->getConnectionHandle());

                // Load all the other stuff that is needed
                OutMessage->DeviceID  = getID();
                OutMessage->TargetID  = getID();
                OutMessage->Port      = getPortID();
                OutMessage->Remote    = getAddress();
                OutMessage->Priority  = MAXPRIORITY-4;//standard seen in rest of devices.
                OutMessage->TimeOut   = 2;
                OutMessage->Retry     = 2;
                OutMessage->Request.RouteID   = getRouteID();

                for(Cbc7020Device::ConfigPartsList::const_iterator tempItr = _config_parts.begin(); tempItr != _config_parts.end(); tempItr++)
                {
                    if( *tempItr != PutConfigPart_all)//_all == infinite loop == unhappy program == very unhappy jess
                    {
                        string tempString = pReq->CommandString();
                        string replaceString = " ";
                        replaceString += *tempItr; //FIX_ME Consider not keeping the old string but just creating a new, internal string.
                        replaceString += " ";

                        CtiToLower(tempString);

                        CtiString ts_tempString = tempString;
                        boost::regex re (" all($| )");
                        ts_tempString.replace( re,replaceString );
                        tempString = ts_tempString;

                        tempReq.setCommandString(tempString);

                        CtiCommandParser parseSingle(tempReq.CommandString());

                        sRet = executePutConfigSingle(&tempReq, parseSingle, OutMessage, vgList, retList, outList);
                    }
                }
            }

        }
        else
        {
            strncpy(OutMessage->Request.CommandStr, (pReq->CommandString()).c_str(), COMMAND_STR_SIZE);
            sRet = executePutConfigSingle(pReq, parse, OutMessage, vgList, retList, outList);
        }
        incrementGroupMessageCount(pReq->UserMessageId(), (long)pReq->getConnectionHandle(), outList.size());

        if(OutMessage!=NULL)
        {
            delete OutMessage;
            OutMessage = NULL;
        }

        if( outList.size() )
        {
            CtiString resultString;

            resultString = CtiNumStr(outList.size()) + " configuration messages being sent ";
            retList.push_back( CTIDBG_new CtiReturnMsg(getID( ),
                                                    string(pReq->CommandString()),
                                                    resultString,
                                                    NoError,
                                                    pReq->RouteId(),
                                                    pReq->MacroOffset(),
                                                    pReq->AttemptNum(),
                                                    pReq->TransmissionId(),
                                                    pReq->UserMessageId(),
                                                    pReq->getSOE(),
                                                    CtiMultiMsg_vec( )) );
        }
    }
    else
    {
        nRet = Inherited::Inherited::Inherited::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;

}

int Cbc7020Device::executePutConfigSingle(CtiRequestMsg         *pReq,
                                   CtiCommandParser               &parse,
                                   OUTMESS                        *&OutMessage,
                                   list< CtiMessage* >      &vgList,
                                   list< CtiMessage* >      &retList,
                                   list< OUTMESS* >         &outList)
{
    // Load all the other stuff that is needed
    OutMessage->DeviceID  = getID();
    OutMessage->TargetID  = getID();
    OutMessage->Port      = getPortID();
    OutMessage->Remote    = getAddress();
    OutMessage->Priority  = MAXPRIORITY-4;//standard seen in rest of devices.
    OutMessage->TimeOut   = 2;
    OutMessage->Retry     = 2;
    OutMessage->Request.RouteID   = getRouteID();

    string installValue = parse.getsValue("installvalue");

    int nRet = NORMAL;
    if( installValue == PutConfigPart_comms_lost )
    {
        nRet = executePutConfigCommsLost(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_control_times )
    {
        nRet = executePutConfigControlTimes(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_data_logging )
    {
        nRet = executePutConfigDataLogging(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_dnp )
    {
        nRet = executePutConfigDNP(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_fault_detection )
    {
        nRet = executePutConfigFaultDetection(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_neutral_current )
    {
        nRet = executePutConfigNeutralCurrent(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_time_temp_1 )
    {
        nRet = executePutConfigTimeAndTemp1(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_time_temp_2 )
    {
        nRet = executePutConfigTimeAndTemp2(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_udp )
    {
        nRet = executePutConfigUDP(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else if( installValue == PutConfigPart_voltage )
    {
        nRet = executePutConfigVoltage(pReq,parse,OutMessage,vgList,retList,outList);
    }
    else
    {   //Not sure if this is correct, this could just return NoMethod. This is here
        //just in case anyone wants to use a putconfig install  for anything but configs.
        //nRet = Inherited::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
        nRet = NoMethod;
    }

    if( nRet != NORMAL )
    {
        CtiString resultString;

        if( nRet == NoConfigData )
        {
            resultString = "ERROR: Invalid config data. Config name:" + installValue;
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime( ) << " Device " << getName( ) << " had no configuration for config: " << installValue << endl;
        }
        else
        {
            resultString = "ERROR: NoMethod or invalid config. Config name:" + installValue;
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime( ) << " Device " << getName( ) << " had a configuration error using config " << installValue << endl;
        }

        retList.push_back( CTIDBG_new CtiReturnMsg(getID( ),
                                                string(OutMessage->Request.CommandStr),
                                                resultString,
                                                nRet,
                                                OutMessage->Request.RouteID,
                                                OutMessage->Request.MacroOffset,
                                                OutMessage->Request.Attempt,
                                                OutMessage->Request.TrxID,
                                                OutMessage->Request.UserID,
                                                OutMessage->Request.SOE,
                                                CtiMultiMsg_vec( )) );
    }
    return nRet;
}

// ExecutePutConfigVoltage
// Put the voltage configuration on the device. Simple.
/*int Cbc7020Device::executePutConfigVoltage(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    OUTMESS *tempOutMess;

    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeCBCVoltage);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeCBCVoltage )
        {
            long uvClosePt, ovTripPt, triggerTime, activeSettings, voltageHysteresis, adaptiveVoltageFlag;
            long emergencyUVPoint, emergencyOVPoint, emergencyVoltageTime;

            CBCVoltageSPtr config = boost::static_pointer_cast< ConfigurationPart<CBCVoltage> >(tempBasePtr);
            uvClosePt            = config->getLongValueFromKey(UVClosePoint);
            ovTripPt             = config->getLongValueFromKey(OVTripPoint);
            triggerTime          = config->getLongValueFromKey(OVUVControlTriggerTime);
            activeSettings       = config->getLongValueFromKey(ActiveSettings);
            voltageHysteresis    = config->getLongValueFromKey(AdaptiveVoltageHysteresis);
            adaptiveVoltageFlag  = config->getLongValueFromKey(AdaptiveVoltageFlag);
            emergencyUVPoint     = config->getLongValueFromKey(EmergencyUVPoint);
            emergencyOVPoint     = config->getLongValueFromKey(EmergencyOVPoint);
            emergencyVoltageTime = config->getLongValueFromKey(EmergencyVoltageTime);

            //UV/UV
            if( uvClosePt == numeric_limits<long>::min()
                || ovTripPt == numeric_limits<long>::min()
                || triggerTime == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                sendPutValueAnalog(UVClosePointOffset, uvClosePt, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(OVTripPointOffset, ovTripPt, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(OVUVControlTriggerTimeOffset, triggerTime, pReq, OutMessage, vgList, retList, outList);
            }

            //Adaptive Voltage
            if( voltageHysteresis == numeric_limits<long>::min()
                || adaptiveVoltageFlag == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                sendPutValueAnalog(AdaptiveVoltageHysteresisOffset, voltageHysteresis, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(AdaptiveVoltageFlagOffset, adaptiveVoltageFlag, pReq, OutMessage, vgList, retList, outList);
            }

            //Emergency Voltage
            if( emergencyUVPoint == numeric_limits<long>::min()
                || emergencyOVPoint == numeric_limits<long>::min()
                || emergencyVoltageTime == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                sendPutValueAnalog(EmergencyUVPointOffset, emergencyUVPoint, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(EmergencyOVPointOffset, emergencyOVPoint, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(EmergencyVoltageTimeOffset, emergencyVoltageTime, pReq, OutMessage, vgList, retList, outList);
            }

            BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeCBCFaultDetection);

            if( tempBasePtr && tempBasePtr->getType() == ConfigTypeCBCFaultDetection )
            {
                CBCFaultDetectionSPtr faultConfig = boost::static_pointer_cast< ConfigurationPart<CBCFaultDetection> >(tempBasePtr);

                long faultDetectionActive = faultConfig->getLongValueFromKey(FaultDetectionActive);
                if( faultDetectionActive == numeric_limits<long>::min()
                    || activeSettings == numeric_limits<long>::min() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }
                else
                {
                    sendPutValueAnalog(ActiveSettingsOffset, (activeSettings + (faultDetectionActive << 2)), pReq, OutMessage, vgList, retList, outList);
                }
            }
            else
            {
                //assume it is off.
                if( activeSettings == numeric_limits<long>::min() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }
                else
                {
                    sendPutValueAnalog(ActiveSettingsOffset, activeSettings, pReq, OutMessage, vgList, retList, outList);
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            nRet = NoConfigData;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        nRet = NoConfigData;
    }

    return nRet;

}

int Cbc7020Device::executePutConfigCommsLost(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    OUTMESS *tempOutMess;
    CtiCommandParser *parseSingle;
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeCBCCommsLost);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeCBCCommsLost )
        {
            long commsLostUVClosePt, commsLostOVClosePt, commsLostTime, commsLostAction;

            CBCCommsLostSPtr config = boost::static_pointer_cast< ConfigurationPart<CBCCommsLost> >(tempBasePtr);
            commsLostUVClosePt   = config->getLongValueFromKey(CommsLostUVClosePoint);
            commsLostOVClosePt   = config->getLongValueFromKey(CommsLostOVTripPoint);
            commsLostTime        = config->getLongValueFromKey(CommsLostTime);
            commsLostAction      = config->getLongValueFromKey(CommsLostAction);

            if( commsLostUVClosePt == numeric_limits<long>::min()
             || commsLostOVClosePt == numeric_limits<long>::min()
             || commsLostTime      == numeric_limits<long>::min()
             || commsLostAction    == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                sendPutValueAnalog(CommsLostUVClosePointOffset, commsLostUVClosePt, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(CommsLostOVTripPointOffset, commsLostOVClosePt, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(CommsLostTimeOffset, commsLostTime, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(CommsLostActionOffset, commsLostAction, pReq, OutMessage, vgList, retList, outList);
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            nRet = NoConfigData;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        nRet = NoConfigData;
    }


    return nRet;
}

int Cbc7020Device::executePutConfigNeutralCurrent(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    OUTMESS *tempOutMess;
    CtiCommandParser *parseSingle;
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeCBCNeutralCurrent);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeCBCNeutralCurrent )
        {
            long faultCurrentSetPt, stateChangeSetPt, neutralCurrentRetryCount;

            CBCNeutralCurrentSPtr config = boost::static_pointer_cast< ConfigurationPart<CBCNeutralCurrent> >(tempBasePtr);
            faultCurrentSetPt        = config->getLongValueFromKey(FaultCurrentSetPoint);
            stateChangeSetPt         = config->getLongValueFromKey(StateChangeSetPoint);
            //neutralCurrentRetryCount = config->getLongValueFromKey(NeutralCurrentRetryCount);

            if( faultCurrentSetPt        == numeric_limits<long>::min()
             || stateChangeSetPt         == numeric_limits<long>::min()
             /*|| neutralCurrentRetryCount == numeric_limits<long>::min()*/ /* )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                sendPutValueAnalog(FaultCurrentSetPointOffset, faultCurrentSetPt, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(StateChangeSetPointOffset, stateChangeSetPt, pReq, OutMessage, vgList, retList, outList);
                //sendPutValueAnalog(NeutralCurrentRetryCountOffset, neutralCurrentRetryCount, pReq, OutMessage, vgList, retList, outList);
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            nRet = NoConfigData;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        nRet = NoConfigData;
    }


    return nRet;
}

int Cbc7020Device::executePutConfigFaultDetection(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    OUTMESS *tempOutMess;
    CtiCommandParser *parseSingle;
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeCBCFaultDetection);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeCBCFaultDetection )
        {
            long faultDetectionActive, ai1AverageTime, ai1PeakSamples, ai1RatioThreshold;
            long ai2AverageTime, ai2PeakSamples, ai2RatioThreshold, batteryOnTime;
            long ai3AverageTime, ai3PeakSamples, ai3RatioThreshold;

            CBCFaultDetectionSPtr config = boost::static_pointer_cast< ConfigurationPart<CBCFaultDetection> >(tempBasePtr);
            faultDetectionActive = config->getLongValueFromKey(FaultDetectionActive);
            ai1AverageTime       = config->getLongValueFromKey(AI1AverageTime);
            ai2AverageTime       = config->getLongValueFromKey(AI2AverageTime);
            ai3AverageTime       = config->getLongValueFromKey(AI3AverageTime);
            ai1PeakSamples       = config->getLongValueFromKey(AI1PeakSamples);
            ai2PeakSamples       = config->getLongValueFromKey(AI2PeakSamples);
            ai3PeakSamples       = config->getLongValueFromKey(AI3PeakSamples);
            ai1RatioThreshold    = config->getLongValueFromKey(AI1RatioThreshold);
            ai2RatioThreshold    = config->getLongValueFromKey(AI2RatioThreshold);
            ai3RatioThreshold    = config->getLongValueFromKey(AI3RatioThreshold);
            batteryOnTime        = config->getLongValueFromKey(BatteryOnTime);

            if( ai1AverageTime    == numeric_limits<long>::min()
             || ai1PeakSamples    == numeric_limits<long>::min()
             || ai1RatioThreshold == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                sendPutValueAnalog(AI1AverageTimeOffset, ai1AverageTime, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(AI1PeakSamplesOffset, ai1PeakSamples, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(AI1RatioThresholdOffset, ai1RatioThreshold, pReq, OutMessage, vgList, retList, outList);
            }

            if( ai2AverageTime    == numeric_limits<long>::min()
             || ai2PeakSamples    == numeric_limits<long>::min()
             || ai2RatioThreshold == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                sendPutValueAnalog(AI2AverageTimeOffset, ai2AverageTime, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(AI2PeakSamplesOffset, ai2PeakSamples, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(AI2RatioThresholdOffset, ai2RatioThreshold, pReq, OutMessage, vgList, retList, outList);
            }

            if( ai3AverageTime    == numeric_limits<long>::min()
             || ai3PeakSamples    == numeric_limits<long>::min()
             || ai3RatioThreshold == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                sendPutValueAnalog(AI3AverageTimeOffset, ai3AverageTime, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(AI3PeakSamplesOffset, ai3PeakSamples, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(AI3RatioThresholdOffset, ai3RatioThreshold, pReq, OutMessage, vgList, retList, outList);
            }

            if( batteryOnTime == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                sendPutValueAnalog(BatteryOnTimeOffset, batteryOnTime, pReq, OutMessage, vgList, retList, outList);
            }

            BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeCBCVoltage);

            if( tempBasePtr && tempBasePtr->getType() == ConfigTypeCBCVoltage )
            {
                CBCVoltageSPtr voltConfig = boost::static_pointer_cast< ConfigurationPart<CBCVoltage> >(tempBasePtr);

                long voltageDetectionActive = voltConfig->getLongValueFromKey(ActiveSettings);
                if( voltageDetectionActive == numeric_limits<long>::min()
                    || faultDetectionActive == numeric_limits<long>::min() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }
                else
                {
                    sendPutValueAnalog(ActiveSettingsOffset, (voltageDetectionActive + (faultDetectionActive << 2)), pReq, OutMessage, vgList, retList, outList);
                }
            }
            else
            {
                //assume it is off.
                if( faultDetectionActive == numeric_limits<long>::min() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }
                else
                {
                    sendPutValueAnalog(ActiveSettingsOffset, (faultDetectionActive << 2), pReq, OutMessage, vgList, retList, outList);
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            nRet = NoConfigData;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        nRet = NoConfigData;
    }

    return nRet;
}

int Cbc7020Device::executePutConfigTimeAndTemp1(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    OUTMESS *tempOutMess;
    CtiCommandParser *parseSingle;
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeCBCSeason1TimeAndTemp);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeCBCSeason1TimeAndTemp )
        {
            long season1Start, weekdayTimedControlClose1, weekendTimedControlClose1, weekdayTimedControlTrip1,
            weekendTimedControlTrip1, offTimeState1, tempMinThreshold1, tempMinThresholdAction1, tempMaxThresholdTrigTime1,
            tempMinHysterisis1, tempMinThresholdTrigTime1, tempMaxThreshold1, tempMaxThresholdAction1, tempMaxHysterisis1;

            CBCSeason1TimeAndTempSPtr config = boost::static_pointer_cast< ConfigurationPart<CBCSeason1TimeAndTemp> >(tempBasePtr);
            season1Start              = config->getLongValueFromKey(Season1Start);
            weekdayTimedControlClose1 = config->getLongValueFromKey(WeekdayTimedControlClose1);
            weekendTimedControlClose1 = config->getLongValueFromKey(WeekendTimedControlClose1);
            weekdayTimedControlTrip1  = config->getLongValueFromKey(WeekdayTimedControlTrip1);
            weekendTimedControlTrip1  = config->getLongValueFromKey(WeekendTimedControlTrip1);
            offTimeState1             = config->getLongValueFromKey(OffTimeState1);
            tempMinThreshold1         = config->getLongValueFromKey(TempMinThreshold1);
            tempMinThresholdAction1   = config->getLongValueFromKey(TempMinThresholdAction1);
            tempMinHysterisis1        = config->getLongValueFromKey(TempMinHysterisis1);
            tempMinThresholdTrigTime1 = config->getLongValueFromKey(TempMinThresholdTrigTime1);
            tempMaxThreshold1         = config->getLongValueFromKey(TempMaxThreshold1);
            tempMaxThresholdAction1   = config->getLongValueFromKey(TempMaxThresholdAction1);
            tempMaxHysterisis1        = config->getLongValueFromKey(TempMaxHysterisis1);
            tempMaxThresholdTrigTime1 = config->getLongValueFromKey(TempMaxThresholdTrigTime1);

            if( season1Start    == numeric_limits<long>::min()
             || weekdayTimedControlClose1 == numeric_limits<long>::min()
             || weekendTimedControlClose1 == numeric_limits<long>::min()
             || weekendTimedControlTrip1  == numeric_limits<long>::min()
             || weekdayTimedControlTrip1  == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                sendPutValueAnalog(Season1StartOffset+1, ((season1Start-season1Start%100)/100), pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(Season1StartOffset, (season1Start%100), pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(WeekdayTimedControlClose1Offset, weekdayTimedControlClose1, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(WeekendTimedControlClose1Offset, weekendTimedControlClose1, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(WeekendTimedControlTrip1Offset, weekendTimedControlTrip1, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(WeekdayTimedControlTrip1Offset, weekdayTimedControlTrip1, pReq, OutMessage, vgList, retList, outList);
            }

            if( offTimeState1             == numeric_limits<long>::min()
             || tempMinThreshold1         == numeric_limits<long>::min()
             || tempMinThresholdAction1   == numeric_limits<long>::min()
             || tempMinHysterisis1        == numeric_limits<long>::min()
             || tempMinThresholdTrigTime1 == numeric_limits<long>::min()
             || tempMaxThreshold1         == numeric_limits<long>::min()
             || tempMaxThresholdAction1   == numeric_limits<long>::min()
             || tempMaxHysterisis1        == numeric_limits<long>::min()
             || tempMaxThresholdTrigTime1 == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                sendPutValueAnalog(OffTimeState1Offset, offTimeState1, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(TempMinThreshold1Offset, tempMinThreshold1, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(TempMinThresholdAction1Offset, tempMinThresholdAction1, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(TempMinHysterisis1Offset, tempMinHysterisis1, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(TempMinThresholdTrigTime1Offset, tempMinThresholdTrigTime1, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(TempMaxThreshold1Offset, tempMaxThreshold1, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(TempMaxThresholdAction1Offset, tempMaxThresholdAction1, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(TempMaxHysterisis1Offset, tempMaxHysterisis1, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(TempMaxThresholdTrigTime1Offset, tempMaxThresholdTrigTime1, pReq, OutMessage, vgList, retList, outList);
            }

        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            nRet = NoConfigData;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        nRet = NoConfigData;
    }

    return nRet;
}

int Cbc7020Device::executePutConfigTimeAndTemp2(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    OUTMESS *tempOutMess;
    CtiCommandParser *parseSingle;
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeCBCSeason2TimeAndTemp);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeCBCSeason2TimeAndTemp )
        {
            long season2Start, weekdayTimedControlClose2, weekendTimedControlClose2, weekdayTimedControlTrip2,
            weekendTimedControlTrip2, offTimeState2, tempMinThreshold2, tempMinThresholdAction2, tempMaxThresholdTrigTime2,
            tempMinHysterisis2, tempMinThresholdTrigTime2, tempMaxThreshold2, tempMaxThresholdAction2, tempMaxHysterisis2;

            CBCSeason2TimeAndTempSPtr config = boost::static_pointer_cast< ConfigurationPart<CBCSeason2TimeAndTemp> >(tempBasePtr);

            season2Start              = config->getLongValueFromKey(Season2Start);
            weekdayTimedControlClose2 = config->getLongValueFromKey(WeekdayTimedControlClose2);
            weekendTimedControlClose2 = config->getLongValueFromKey(WeekendTimedControlClose2);
            weekdayTimedControlTrip2  = config->getLongValueFromKey(WeekdayTimedControlTrip2);
            weekendTimedControlTrip2  = config->getLongValueFromKey(WeekendTimedControlTrip2);
            offTimeState2             = config->getLongValueFromKey(OffTimeState2);
            tempMinThreshold2         = config->getLongValueFromKey(TempMinThreshold2);
            tempMinThresholdAction2   = config->getLongValueFromKey(TempMinThresholdAction2);
            tempMinHysterisis2        = config->getLongValueFromKey(TempMinHysterisis2);
            tempMinThresholdTrigTime2 = config->getLongValueFromKey(TempMinThresholdTrigTime2);
            tempMaxThreshold2         = config->getLongValueFromKey(TempMaxThreshold2);
            tempMaxThresholdAction2   = config->getLongValueFromKey(TempMaxThresholdAction2);
            tempMaxHysterisis2        = config->getLongValueFromKey(TempMaxHysterisis2);
            tempMaxThresholdTrigTime2 = config->getLongValueFromKey(TempMaxThresholdTrigTime2);

            if( season2Start    == numeric_limits<long>::min()
             || weekdayTimedControlClose2 == numeric_limits<long>::min()
             || weekendTimedControlClose2 == numeric_limits<long>::min()
             || weekendTimedControlTrip2  == numeric_limits<long>::min()
             || weekdayTimedControlTrip2  == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                sendPutValueAnalog(Season2StartOffset+1, ((season2Start-season2Start%100)/100), pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(Season2StartOffset, (season2Start%100), pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(WeekdayTimedControlClose2Offset, weekdayTimedControlClose2, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(WeekendTimedControlClose2Offset, weekendTimedControlClose2, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(WeekendTimedControlTrip2Offset, weekendTimedControlTrip2, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(WeekdayTimedControlTrip2Offset, weekdayTimedControlTrip2, pReq, OutMessage, vgList, retList, outList);
            }

            if( offTimeState2             == numeric_limits<long>::min()
             || tempMinThreshold2         == numeric_limits<long>::min()
             || tempMinThresholdAction2   == numeric_limits<long>::min()
             || tempMinHysterisis2        == numeric_limits<long>::min()
             || tempMinThresholdTrigTime2 == numeric_limits<long>::min()
             || tempMaxThreshold2         == numeric_limits<long>::min()
             || tempMaxThresholdAction2   == numeric_limits<long>::min()
             || tempMaxHysterisis2        == numeric_limits<long>::min()
             || tempMaxThresholdTrigTime2 == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                sendPutValueAnalog(OffTimeState2Offset, offTimeState2, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(TempMinThreshold2Offset, tempMinThreshold2, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(TempMinThresholdAction2Offset, tempMinThresholdAction2, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(TempMinHysterisis2Offset, tempMinHysterisis2, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(TempMinThresholdTrigTime2Offset, tempMinThresholdTrigTime2, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(TempMaxThreshold2Offset, tempMaxThreshold2, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(TempMaxThresholdAction2Offset, tempMaxThresholdAction2, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(TempMaxHysterisis2Offset, tempMaxHysterisis2, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(TempMaxThresholdTrigTime2Offset, tempMaxThresholdTrigTime2, pReq, OutMessage, vgList, retList, outList);
            }

        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            nRet = NoConfigData;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        nRet = NoConfigData;
    }

    return nRet;
}

int Cbc7020Device::executePutConfigControlTimes(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    OUTMESS *tempOutMess;
    CtiCommandParser *parseSingle;
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeCBCControlTimes);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeCBCControlTimes )
        {
            long contactClosureTime, manualControlDelayTrip, manualControlDelayClose, recloseDelayTime;

            CBCControlTimesSPtr config = boost::static_pointer_cast< ConfigurationPart<CBCControlTimes> >(tempBasePtr);
            contactClosureTime       = config->getLongValueFromKey(ContactClosureTime);
            manualControlDelayTrip   = config->getLongValueFromKey(ManualControlDelayTrip);
            manualControlDelayClose  = config->getLongValueFromKey(ManualControlDelayClose);
            recloseDelayTime         = config->getLongValueFromKey(RecloseDelayTime);

            if( contactClosureTime        == numeric_limits<long>::min()
             || manualControlDelayTrip    == numeric_limits<long>::min()
             || manualControlDelayClose   == numeric_limits<long>::min()
             || recloseDelayTime          == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                sendPutValueAnalog(ContactClosureTimeOffset, contactClosureTime, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(ManualControlDelayTripOffset, manualControlDelayTrip, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(ManualControlDelayCloseOffset, manualControlDelayClose, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(RecloseDelayTimeOffset, recloseDelayTime, pReq, OutMessage, vgList, retList, outList);
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            nRet = NoConfigData;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        nRet = NoConfigData;
    }

    return nRet;
}

int Cbc7020Device::executePutConfigDataLogging(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    OUTMESS *tempOutMess;
    CtiCommandParser *parseSingle;
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeCBCDataLogging);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeCBCDataLogging )
        {
            long dataLogFlags, logTimeInterval;

            CBCDataLoggingSPtr config = boost::static_pointer_cast< ConfigurationPart<CBCDataLogging> >(tempBasePtr);
            dataLogFlags      = config->getLongValueFromKey(DataLogFlags);
            logTimeInterval   = config->getLongValueFromKey(LogTimeInterval);

            if( dataLogFlags        == numeric_limits<long>::min()
             || logTimeInterval     == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                sendPutValueAnalog(DataLogFlagsOffset, dataLogFlags, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(LogTimeIntervalOffset, logTimeInterval, pReq, OutMessage, vgList, retList, outList);
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            nRet = NoConfigData;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        nRet = NoConfigData;
    }

    return nRet;
}

int Cbc7020Device::executePutConfigDNP(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    OUTMESS *tempOutMess;
    CtiCommandParser *parseSingle;
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeCBC_DNP);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeCBC_DNP )
        {
            long lineVoltageDeadBand, deltaVoltageDeadBand, analogDeadBand;

            CBC_DNPSPtr config = boost::static_pointer_cast< ConfigurationPart<CBC_DNP> >(tempBasePtr);
            lineVoltageDeadBand  = config->getLongValueFromKey(LineVoltageDeadBand);
            deltaVoltageDeadBand = config->getLongValueFromKey(DeltaVoltageDeadBand);
            analogDeadBand       = config->getLongValueFromKey(AnalogDeadBand);

            if( lineVoltageDeadBand    == numeric_limits<long>::min()
             || deltaVoltageDeadBand   == numeric_limits<long>::min()
             || AnalogDeadBand         == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                sendPutValueAnalog(LineVoltageDeadBandOffset, lineVoltageDeadBand, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(DeltaVoltageDeadBandOffset, deltaVoltageDeadBand, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(AnalogDeadBandOffset, analogDeadBand, pReq, OutMessage, vgList, retList, outList);
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            nRet = NoConfigData;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        nRet = NoConfigData;
    }

    return nRet;
}

int Cbc7020Device::executePutConfigUDP(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    OUTMESS *tempOutMess;
    CtiCommandParser *parseSingle;
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeCBC_UDP);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeCBC_UDP )
        {
            long retryDelay, pollTimeout;

            CBC_UDPSPtr config = boost::static_pointer_cast< ConfigurationPart<CBC_UDP> >(tempBasePtr);
            retryDelay  = config->getLongValueFromKey(RetryDelay);
            pollTimeout = config->getLongValueFromKey(PollTimeout);

            if( retryDelay    == numeric_limits<long>::min()
             || pollTimeout   == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                sendPutValueAnalog(RetryDelayOffset, retryDelay, pReq, OutMessage, vgList, retList, outList);
                sendPutValueAnalog(PollTimeoutOffset, pollTimeout, pReq, OutMessage, vgList, retList, outList);
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            nRet = NoConfigData;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        nRet = NoConfigData;
    }

    return nRet;
}*/

int Cbc7020Device::sendPutValueAnalog(int outputPt, double value, CtiRequestMsg *pReq, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS* >   &outList)
{
    string tempStr = "putvalue analog " + CtiNumStr(outputPt) + " " + CtiNumStr(value);
    OUTMESS *tempOutMess = CTIDBG_new OUTMESS(*OutMessage);
    strncpy(tempOutMess->Request.CommandStr, tempStr.c_str(), COMMAND_STR_SIZE );
    CtiCommandParser parseSingle(tempOutMess->Request.CommandStr);
    return Inherited::ExecuteRequest(pReq, parseSingle, tempOutMess, vgList, retList, outList);
}

void Cbc7020Device::initOffsetAttributeMaps( std::map <int, PointAttribute> &analogOffsetAttribute,
                                             std::map <int, PointAttribute> &statusOffsetAttribute,
                                             std::map <int, PointAttribute> &accumulatorOffsetAttribute)
{
    analogOffsetAttribute.insert( std::make_pair( 5,  PointAttribute::CbcVoltage) );
    analogOffsetAttribute.insert( std::make_pair( 6,  PointAttribute::HighVoltage) );
    analogOffsetAttribute.insert( std::make_pair( 7,  PointAttribute::LowVoltage) );
    analogOffsetAttribute.insert( std::make_pair( 8,  PointAttribute::DeltaVoltage) );
    analogOffsetAttribute.insert( std::make_pair( 9,  PointAttribute::AnalogInput1) );
    analogOffsetAttribute.insert( std::make_pair( 10, PointAttribute::Temperature) );
    analogOffsetAttribute.insert( std::make_pair( 13, PointAttribute::RSSI) );
    analogOffsetAttribute.insert( std::make_pair( 14, PointAttribute::IgnoredReason) );

    //dnp analog output points have offsets starting with 10000
    analogOffsetAttribute.insert( std::make_pair( 10001, PointAttribute::VoltageControl) );
    analogOffsetAttribute.insert( std::make_pair( 10002, PointAttribute::UvThreshold) );
    analogOffsetAttribute.insert( std::make_pair( 10003, PointAttribute::OvThreshold) );
    analogOffsetAttribute.insert( std::make_pair( 10004, PointAttribute::OVUVTrackTime) );
    analogOffsetAttribute.insert( std::make_pair( 10010, PointAttribute::NeutralCurrentSensor) );
    analogOffsetAttribute.insert( std::make_pair( 10011, PointAttribute::NeutralCurrentAlarmThreshold) );
    analogOffsetAttribute.insert( std::make_pair( 10026, PointAttribute::TimeTempSeasonOne) );
    analogOffsetAttribute.insert( std::make_pair( 10042, PointAttribute::TimeTempSeasonTwo) );
    analogOffsetAttribute.insert( std::make_pair( 10068, PointAttribute::VarControl) );
    analogOffsetAttribute.insert( std::make_pair( 20001, PointAttribute::UDPIpAddress) );
    analogOffsetAttribute.insert( std::make_pair( 20002, PointAttribute::UDPPortNumber) );

    statusOffsetAttribute.insert( std::make_pair( 1, PointAttribute::CapacitorBankState) );
    statusOffsetAttribute.insert( std::make_pair( 2, PointAttribute::ReCloseBlocked) );
    statusOffsetAttribute.insert( std::make_pair( 3, PointAttribute::ControlMode) );
    statusOffsetAttribute.insert( std::make_pair( 4, PointAttribute::AutoVoltControl) );
    statusOffsetAttribute.insert( std::make_pair( 5, PointAttribute::LastControlLocal) );
    statusOffsetAttribute.insert( std::make_pair( 6, PointAttribute::LastControlRemote) );
    statusOffsetAttribute.insert( std::make_pair( 7, PointAttribute::LastControlOvUv) );
    statusOffsetAttribute.insert( std::make_pair( 8, PointAttribute::LastControlNeutralFault) );
    statusOffsetAttribute.insert( std::make_pair( 9, PointAttribute::LastControlScheduled) );
    statusOffsetAttribute.insert( std::make_pair( 10, PointAttribute::LastControlDigital) );
    statusOffsetAttribute.insert( std::make_pair( 11, PointAttribute::LastControlAnalog) );
    statusOffsetAttribute.insert( std::make_pair( 12, PointAttribute::LastControlTemperature) );
    statusOffsetAttribute.insert( std::make_pair( 13, PointAttribute::OvCondition) );
    statusOffsetAttribute.insert( std::make_pair( 14, PointAttribute::UvCondition) );
    statusOffsetAttribute.insert( std::make_pair( 15, PointAttribute::OpFailedNeutralCurrent) );
    statusOffsetAttribute.insert( std::make_pair( 16, PointAttribute::NeutralCurrentFault) );
    statusOffsetAttribute.insert( std::make_pair( 24, PointAttribute::BadRelay) );
    statusOffsetAttribute.insert( std::make_pair( 25, PointAttribute::DailyMaxOps) );
    statusOffsetAttribute.insert( std::make_pair( 26, PointAttribute::VoltageDeltaAbnormal) );
    statusOffsetAttribute.insert( std::make_pair( 27, PointAttribute::TempAlarm) );
    statusOffsetAttribute.insert( std::make_pair( 28, PointAttribute::DSTActive) );
    statusOffsetAttribute.insert( std::make_pair( 29, PointAttribute::NeutralLockout) );
    statusOffsetAttribute.insert( std::make_pair( 34, PointAttribute::IgnoredIndicator) );

    accumulatorOffsetAttribute.insert( std::make_pair( 1, PointAttribute::TotalOpCount) );
    accumulatorOffsetAttribute.insert( std::make_pair( 2, PointAttribute::UvCount) );
    accumulatorOffsetAttribute.insert( std::make_pair( 3, PointAttribute::OvCount) );
    return;
}


}
}

