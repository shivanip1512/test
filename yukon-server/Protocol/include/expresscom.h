/*-----------------------------------------------------------------------------*
*
* File:   expresscom
*
* Class:  CtiProtocolExpresscom
* Date:   8/13/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2003/03/13 19:35:46 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __EXPRESSCOM_H__
#define __EXPRESSCOM_H__
#pragma warning( disable : 4786)


#include <windows.h>
#include <vector>
using namespace std;


#include "cmdparse.h"
#include "dllbase.h"

class IM_EX_PROT CtiProtocolExpresscom
{
public:

    typedef enum
    {
        atSpid          = 0x80,            // Service Provider ID
        atGeo           = 0x40,
        atSubstation    = 0x20,
        atFeeder        = 0x10,
        atZip           = 0x08,
        atUser          = 0x04,
        atProgram       = 0x02,
        atSplinter      = 0x01,
        atIndividual    = 0x00

    } CtiExpresscomAddress_t;

    typedef enum
    {
        mtReserved                  = 0x00,
        mtSync                      = 0x01,
        mtTimeSync                  = 0x02,
        mtSignalTest                = 0x05,
        mtTimedLoadControl          = 0x08,
        mtRestoreLoadControl        = 0x09,
        mtCycleLoadControl          = 0x0a,
        mtThermostatSetpointControl = 0x0b,
        mtThermostatLoadControl     = 0x0c,
        mtThermostatSetState        = 0x0d,
        mtConfiguration             = 0x10,
        mtMaintenance               = 0x14,
        mtService                   = 0x15,
        mtTemporaryService          = 0x16,
        mtData                      = 0x1d,
        mtCapcontrol                = 0x20

    } CtiExpresscomMessageType_t;

protected:

    bool    _useProtocolCRC;                        // 'S'-'T' bounded message (false)  or 'U'-'V' bounded message (true).

    // Addressing Levels
    BYTE    _addressLevel;                  // Bit field indicating addressing TO BE USED.

    USHORT  _spidAddress;                   // 1-65534
    USHORT  _geoAddress;                    // 1-65534
    USHORT  _substationAddress;             // 1-65534
    USHORT  _feederAddress;                 // Bit field 16 feeders 1 is LSB 16 is MSB.
    UINT    _zipAddress;                    // 1-16777214 3 byte field.
    USHORT  _udaAddress;                    // 1-65534 User defined address.
    BYTE    _programAddress;                // 1-254 program load (like relay)
    BYTE    _splinterAddress;               // 1-254 subset of program.
    UINT    _uniqueAddress;                 // 1-4294967295 UID.

    // Parameters which affect the construction of the messages.

    bool    _celsiusMode;                   // Default to false/no. (Implies Fahrenheit).
    bool    _heatingMode;                   // Default to true/yes;
    bool    _coolingMode;                   // Default to true/yes;
    bool    _absoluteTemps;                 // Default to false/no. (Implies delta)

private:

    vector< BYTE > _message;                // This is the baby...
    int _messageCount;

    void addressMessage();
    void terminateMessage();
    void resolveAddressLevel();
    INT assembleControl(CtiCommandParser &parse, CtiOutMessage &OutMessage);
    INT assemblePutConfig(CtiCommandParser &parse, CtiOutMessage &OutMessage);
    INT assemblePutStatus(CtiCommandParser &parse, CtiOutMessage &OutMessage);

    INT parseSchedule(CtiCommandParser &parse);
    INT schedulePoint(vector< BYTE > &schedule);

    INT sync();
    INT timeSync(RWTime &gmt, bool fullsync = true);

    INT signalTest(BYTE test);
    INT timedLoadControl(UINT loadMask, UINT shedtime_seconds, BYTE randin = 0, BYTE randout = 0, USHORT delay = 0 );                 // This is a shed!
    INT restoreLoadControl(UINT loadMask, BYTE rand = 0, USHORT delay = 0 );
    INT cycleLoadControl(UINT loadMask, BYTE cyclepercent, BYTE period_minutes, BYTE cyclecount, USHORT delay = 0, bool preventrampin = false, bool allowTrueCycle = false);

    /*
     *
     *
     *  BYTE cyclepercent,
     *  BYTE periodminutes,
     *  BYTE cyclecount,
     *  USHORT delay = 0,
     *  INT controltemperature = 0,
     *  BYTE limittemperature = 0,            // Max or min temperature at which we switch to the limitfallbackpercent control rate.
     *  BYTE limitfallbackpercent = 0,        // If limittemperature is defined this is the rate to fall back to iff it is exceeded.
     *  CHAR maxdelatperhour = 0,             // Signed value. maximum temperature delat allowed before we goto deltafallback rate.
     *  BYTE deltafallbackpercent = 0);       // If the temperature is changing too quickly we switch to this rate.
     *
     */
    INT thermostatLoadControl(UINT loadMask, BYTE cyclepercent, BYTE periodminutes, BYTE cyclecount, USHORT delay = 0, INT controltemperature = 0, BYTE limittemperature = 0, BYTE limitfallbackpercent = 0, CHAR maxdeltaperhour = 0, BYTE deltafallbackpercent = 0, bool noramp = false);

    /*  Ok, this really requires the document, but here's some ASCII art too.
     *
     *                                                      /---------------\     <- Controlled setpoint
     *                                                     /                 \
     *                                                    /                   \
     *                                                   /                     \
     *                                                  /                       \
     *                                                 /                         \
     *     R     A        B          C              D /             E           F \
     *                                               /                             \
     *  t0---tr------ta--------tb----------tc-------/--------td-------------te------tf---------- Uncontrolled Setpoint
     *                \                            /
     *                 \                          /
     *                  \                        /
     *                   \                      /
     *                    \                    /
     *                     \                  /
     *                      \                /
     *                       \              /
     *                        \------------/
     *
     *  USHORT flags,           // bitwise control flags.
     *  BYTE minTemp,           // absolute minimum temp at any point of cycle.
     *  BYTE maxTemp,           // absolute maximum temp at any point of control
     *  USHORT T_r,             // random minutes 0 - T_r chosen by switch and added to T_a.
     *  USHORT T_a,             // minutes of delay before control start.
     *  USHORT T_b,             // minutes of precontrol ramp time. Setpoint is transitioned linearly over this time period.
     *  BYTE delta_S_b,         // setpoint delta or absolute temperature for precontrol period.
     *  USHORT T_c,             // minutes duration of precontrol (precool/preheat) stage.
     *  USHORT T_d,             // minutes of precontrol to control ramp time. Setpoint is transitioned linearly over this time period.
     *  BYTE delta_S_d,         // setpoint delta or absolute temperature for control period.
     *  USHORT T_e,             // minutes duration of control stage.
     *  USHORT T_f,             // minutes of postcontrol ramp time. Setpoint is transitioned linearly over this time period back to the uncontrolled setpoint.
     *  BYTE delta_S_f);
     *
     */
    INT thermostatSetpointControl(BYTE minTemp = 0, BYTE maxTemp = 0, USHORT T_r = 0, USHORT T_a = 0, USHORT T_b = 0, BYTE delta_S_b = 0, USHORT T_c = 0, USHORT T_d = 0, BYTE delta_S_d = 0, USHORT T_e = 0, USHORT T_f = 0, BYTE delta_S_f = 0, bool hold = false);
    INT thermostatSetState(UINT loadmask = 0x01, bool temporary = true, bool restore = false, int timeout_min = -1, int setpoint = -1, BYTE fanstate = 0x00, BYTE sysstate = 0x00, USHORT delay = 0);
    INT configuration(BYTE configNumber, BYTE length, PBYTE data);
    INT rawconfiguration(RWCString str);
    INT rawmaintenance(RWCString str);
    INT maintenance(BYTE function, BYTE opt1, BYTE opt2, BYTE opt3, BYTE opt4);
    INT service(BYTE action);
    INT service(UINT loadMask, bool activate = true);
    INT temporaryService(USHORT hoursout, bool cancel = false, bool deactiveColdLoad = false, bool deactiveLights = false);
    INT data(RWCString str);
    INT data(PBYTE data, BYTE length, BYTE dataTransmitType = 0, BYTE targetPort = 0);
    INT capControl(BYTE action, BYTE subAction, BYTE data1 = 0x00, BYTE data2 = 0x00);

    INT configureGeoAddressing(CtiCommandParser &parse);
    INT configureLoadAddressing(CtiCommandParser &parse);

public:

    CtiProtocolExpresscom();
    CtiProtocolExpresscom(const CtiProtocolExpresscom& aRef);
    virtual ~CtiProtocolExpresscom();
    CtiProtocolExpresscom& operator=(const CtiProtocolExpresscom& aRef);

    /*
     * This method incorporates all the assigned addressing into the parse object.
     */
    void addAddressing( UINT serial = 0, USHORT spid = 0, USHORT geo = 0, USHORT substation = 0, USHORT feeder = 0, UINT zip = 0, USHORT uda = 0, BYTE program = 0, BYTE splinter = 0);
    bool parseAddressing(CtiCommandParser &parse);

    enum
    {
        stCancelProp                    = 0x00,
        stIncrementProp                 = 0x01,
        stIncrementPropAndTurnOnLight   = 0x02,
        stFlashRSSI                     = 0x03,
        stPing                          = 0x80

    } CtiExpresscomSignalTest;


    typedef enum
    {
        tspAbsoluteTemps    = 0x4000,
        tspCelsius          = 0x2000,
        tspTwoByteTimes     = 0x1000,
        tspIncMinTemp       = 0x0800,
        tspIncMaxTemp       = 0x0400,
        tspHeatMode         = 0x0200,
        tspCoolMode         = 0x0100,
        tspMaintainControl  = 0x0080,
        tspIncRandomTime    = 0x0040,
        tspIncTimeA         = 0x0020,
        tspIncTimeB         = 0x0010,
        tspIncTimeC         = 0x0008,
        tspIncTimeD         = 0x0004,
        tspIncTimeE         = 0x0002,
        tspIncTimeF         = 0x0001
    } CtiExpresscomTSPControlFlags;

    typedef enum
    {
        ccControl                       = 0x01,
        ccOneByteConfig                 = 0x02,
        ccTwoByteConfig                 = 0x03

    } CtiExpresscomCapControlActions;

    typedef enum
    {
        ccControlOpen                   = 0x01,     // No data
        ccControlClose                  = 0x02,     // No data
        ccControlDisableOVUV            = 0x03,     // No data
        ccControlEnableOVUV             = 0x04,     // No data
        ccOneByteConfigControlRelayTime = 0x01,     // One byte data in seconds
        ccTwoByteConfigOVUVCalcTime     = 0x01      // Two bytes data in seconds.

    } CtiExpresscomCapControlSubActions;


    INT parseRequest(CtiCommandParser &parse, CtiOutMessage &OutMessage);

    inline int messageSize() const { return _message.size(); }
    inline BYTE getByte(int pos) const
    {
        return _message[pos];
    }
    inline INT entries() const
    {
       return _messageCount;
    }
    void dump() const;
    BYTE getStartByte() const;
    BYTE getStopByte() const;
};
#endif // #ifndef __EXPRESSCOM_H__
