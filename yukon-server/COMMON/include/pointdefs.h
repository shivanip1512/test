#pragma once

enum PointOffsets
{
    COMM_FAIL_OFFSET           = 2000,
    PAGECOUNTOFFSET            = 2001,
    PAGECHARCOUNTOFFSET        = 2002,

    DAILYCONTROLHISTOFFSET     = 2500,
    MONTHLYCONTROLHISTOFFSET   = 2501,
    SEASONALCONTROLHISTOFFSET  = 2502,
    ANNUALCONTROLHISTOFFSET    = 2503,

    CONTROLSTOPCOUNTDOWNOFFSET = 2505,
};

enum StatusPointStates
{
    STATE_INVALID       = -1,
    STATE_OPENED        =  0,
    STATE_CLOSED        =  1,
    STATE_INDETERMINATE =  2,
};

enum NamedStates
{
    STATEZERO  =  0,
    STATEONE   =  1,
    STATETWO   =  2,
    STATETHREE =  3,
    STATEFOUR  =  4,
    STATEFIVE  =  5,
    STATESIX   =  6,
    STATESEVEN =  7,
    STATEEIGHT =  8,
    STATENINE  =  9,
    STATETEN   = 10,
};

enum ControlledStates
{
    UNCONTROLLED = STATE_OPENED,
    CONTROLLED   = STATE_CLOSED,
};

/*
 * Tags - may be applied to any entity in the system.  Define the running characteristics
 *  of the entity and may not be available offline, but can be rebuilt from stored state
 */

const unsigned TAG_DISABLE_POINT_BY_POINT      = 0x00000001;  // point/device out of service.
const unsigned TAG_DISABLE_ALARM_BY_POINT      = 0x00000002;  // point/device will not cause alarms.
const unsigned TAG_DISABLE_CONTROL_BY_POINT    = 0x00000004;  // point/device cannot be controled.
const unsigned TAG_DISABLE_BY_PORT_ROUTE       = 0x00000008;  // point/device cannot be scanned OR controlled.

const unsigned TAG_DISABLE_DEVICE_BY_DEVICE    = 0x00000010;  // point/device out of service.
const unsigned TAG_DISABLE_ALARM_BY_DEVICE     = 0x00000020;  // point/device will not cause alarms.
const unsigned TAG_DISABLE_CONTROL_BY_DEVICE   = 0x00000040;  // point/device cannot be controled.
const unsigned TAG_INVALID_LODESTAR_READING    = 0x00000080;  // Lodestar Reading Invalid **000009**

const unsigned TAG_POINT_DATA_TIMESTAMP_VALID  = 0x00000100;  // This point data message's timestamp comes from the device! (SOE data)
const unsigned TAG_POINT_DATA_UNSOLICITED      = 0x00000200;  // This point data message was an unsolicited report from a device
const unsigned TAG_POINT_MOA_REPORT            = 0x00000400;  // This point data message is the result of a registration
const unsigned TAG_POINT_DELAYED_UPDATE        = 0x00000800;  // Dispatch delay this point data until the time specified in the message!

const unsigned TAG_POINT_FORCE_UPDATE          = 0x00001000;  // Dispatch will no matter what copy this into his RT memory
const unsigned TAG_POINT_MUST_ARCHIVE          = 0x00002000;  // This data will archive no matter how the point is set up
const unsigned TAG_POINT_RESERVED              = 0x00004000;  // Formerly TAG_POINT_MAY_BE_EXEMPTED, available for reuse
const unsigned TAG_POINT_LOAD_PROFILE_DATA     = 0x00008000;  // This data will archive to raw point history

const unsigned TAG_MANUAL                      = 0x00010000;  // Point was set manually by a client.. this affects quality.
const unsigned TAG_EXTERNALVALUE               = 0x00020000;  // setByExternalApp           = 0x08, Another application set this value!
const unsigned TAG_CONTROL_SELECTED            = 0x00040000;  // This control point is selected by a client for control
const unsigned TAG_CONTROL_PENDING             = 0x00080000;  // This control has been executed and a change is pending

const unsigned TAG_POINT_OLD_TIMESTAMP         = 0x00100000;  // The timestamp on this point is older than the most recent timestamp.
                                                              // This is being added for the clients sake.

const unsigned TAG_REPORT_MSG_TO_ALARM_CLIENTS = 0x01000000;  // This Message should be reported to any alarm clients in the world
const unsigned TAG_DO_NOT_SEND_SIGNAL_AS_EMAIL = 0x02000000;  // This SIGNAL message should not be sent as an email!
const unsigned TAG_ACTIVE_CONDITION            = 0x04000000;  // This SIGNAL message has violated a constraint (alarm MAY be set)

const unsigned TAG_ATTRIB_CONTROL_AVAILABLE    = 0x10000000;  // This status point can also be controlled
const unsigned TAG_ATTRIB_PSEUDO               = 0x20000000;  // Device/point is not real.
const unsigned TAG_UNACKNOWLEDGED_ALARM        = 0x40000000;  // Alarm State has not been acknowledged.
const unsigned TAG_ACTIVE_ALARM                = 0x80000000;  // Alarm State is active now.

// Masks
const unsigned MASK_RESETTABLE_TAGS            = 0x0013FF00;  // Tags which are reset upon any setPoint operation

const unsigned MASK_INCOMING_VALUE_TAGS        = TAG_POINT_OLD_TIMESTAMP | 
                                                 TAG_POINT_DATA_TIMESTAMP_VALID | 
                                                 TAG_POINT_DATA_UNSOLICITED | 
                                                 TAG_POINT_LOAD_PROFILE_DATA;

const unsigned MASK_ANY_ALARM                  = 0xC0000000;  // Get any alarm
const unsigned MASK_ANY_CONTROL                = 0x000C0000;

const unsigned MASK_ANY_DISABLE                = 0x0000007f;
const unsigned MASK_ANY_POINT_DISABLE          = 0x0000000f;
const unsigned MASK_ANY_DEVICE_DISABLE         = 0x00000078;
const unsigned MASK_ANY_SERVICE_DISABLE        = 0x00000019;
const unsigned MASK_ANY_ALARM_DISABLE          = 0x00000022;
const unsigned MASK_ANY_CONTROL_DISABLE        = 0x0000004c;

/***************************
* point offsets for electronic metering
****************************
*/
enum ElectronicMeterPointOffsets
{
    OFFSET_TOTAL_KWH                           =  1,
    OFFSET_PEAK_KW_OR_RATE_A_KW                =  2,
    OFFSET_RATE_A_KWH                          =  3,
    OFFSET_RATE_B_KW                           =  4,
    OFFSET_RATE_B_KWH                          =  5,
    OFFSET_RATE_C_KW                           =  6,
    OFFSET_RATE_C_KWH                          =  7,
    OFFSET_RATE_D_KW                           =  8,
    OFFSET_RATE_D_KWH                          =  9,
    OFFSET_RATE_E_KW                           = 10,
    OFFSET_RATE_E_KWH                          = 11,
    OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW   = 14,
    OFFSET_LOADPROFILE_KW                      = 15,

    OFFSET_TOTAL_KVARH                         = 21,
    OFFSET_PEAK_KVAR_OR_RATE_A_KVAR            = 22,
    OFFSET_RATE_A_KVARH                        = 23,
    OFFSET_RATE_B_KVAR                         = 24,
    OFFSET_RATE_B_KVARH                        = 25,
    OFFSET_RATE_C_KVAR                         = 26,
    OFFSET_RATE_C_KVARH                        = 27,
    OFFSET_RATE_D_KVAR                         = 28,
    OFFSET_RATE_D_KVARH                        = 29,
    OFFSET_RATE_E_KVAR                         = 30,
    OFFSET_RATE_E_KVARH                        = 31,
    OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR = 34,
    OFFSET_LOADPROFILE_KVAR                    = 35,
    OFFSET_LOADPROFILE_QUADRANT1_KVAR          = 36,
    OFFSET_LOADPROFILE_QUADRANT2_KVAR          = 37,
    OFFSET_LOADPROFILE_QUADRANT3_KVAR          = 38,
    OFFSET_LOADPROFILE_QUADRANT4_KVAR          = 39,


    OFFSET_TOTAL_KVAH                          = 41,
    OFFSET_PEAK_KVA_OR_RATE_A_KVA              = 42,
    OFFSET_RATE_A_KVAH                         = 43,
    OFFSET_RATE_B_KVA                          = 44,
    OFFSET_RATE_B_KVAH                         = 45,
    OFFSET_RATE_C_KVA                          = 46,
    OFFSET_RATE_C_KVAH                         = 47,
    OFFSET_RATE_D_KVA                          = 48,
    OFFSET_RATE_D_KVAH                         = 49,
    OFFSET_RATE_E_KVA                          = 50,
    OFFSET_RATE_E_KVAH                         = 51,
    OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA  = 54,
    OFFSET_LOADPROFILE_KVA                     = 55,
    OFFSET_LOADPROFILE_QUADRANT1_KVA           = 56,
    OFFSET_LOADPROFILE_QUADRANT2_KVA           = 57,
    OFFSET_LOADPROFILE_QUADRANT3_KVA           = 58,
    OFFSET_LOADPROFILE_QUADRANT4_KVA           = 59,


    OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE       = 61,
    OFFSET_LOADPROFILE_PHASE_A_VOLTAGE         = 62,
    OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE       = 63,
    OFFSET_LOADPROFILE_PHASE_B_VOLTAGE         = 64,
    OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE       = 65,
    OFFSET_LOADPROFILE_PHASE_C_VOLTAGE         = 66,

    OFFSET_INSTANTANEOUS_PHASE_A_CURRENT       = 71,
    OFFSET_LOADPROFILE_PHASE_A_CURRENT         = 72,
    OFFSET_INSTANTANEOUS_PHASE_B_CURRENT       = 73,
    OFFSET_LOADPROFILE_PHASE_B_CURRENT         = 74,
    OFFSET_INSTANTANEOUS_PHASE_C_CURRENT       = 75,
    OFFSET_LOADPROFILE_PHASE_C_CURRENT         = 76,
    OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT       = 77,
    OFFSET_LOADPROFILE_NEUTRAL_CURRENT         = 78,

    OFFSET_TOTAL_CHANNEL_1                     = 90,
    OFFSET_LOADPROFILE_CHANNEL_1               = 91,
    OFFSET_TOTAL_CHANNEL_2                     = 92,
    OFFSET_LOADPROFILE_CHANNEL_2               = 93,
    OFFSET_TOTAL_CHANNEL_3                     = 94,
    OFFSET_LOADPROFILE_CHANNEL_3               = 95,
    OFFSET_TOTAL_CHANNEL_4                     = 96,
    OFFSET_LOADPROFILE_CHANNEL_4               = 97,

    OFFSET_QUADRANT1_TOTAL_KVARH               = 110,
    OFFSET_QUADRANT1_LAST_INTERVAL_KVAR        = 111,

    OFFSET_QUADRANT2_TOTAL_KVARH               = 125,
    OFFSET_QUADRANT2_LAST_INTERVAL_KVAR        = 126,

    OFFSET_QUADRANT3_TOTAL_KVARH               = 140,
    OFFSET_QUADRANT3_LAST_INTERVAL_KVAR        = 141,

    OFFSET_QUADRANT4_TOTAL_KVARH               = 155,
    OFFSET_QUADRANT4_LAST_INTERVAL_KVAR        = 156,


    OFFSET_HIGHEST_CURRENT_OFFSET              = 160,

    // Points 180 to 200 will be reserved for odds and ends information
    // Sentinel to report battery life
    OFFSET_BATTERY_LIFE                        = 180,
    OFFSET_DAYS_ON_BATTERY                     = 181,
    OFFSET_POWER_FACTOR                        = 182,
    OFFSET_METER_TIME_STATUS                   = 183,
};

/*
 * Qualities are singular (non-bit-masked) and based upon point values in conjunction with the tags
 * They are priority based with higher enumerative values able to overwrite lower ones.  For example
 * should dispatch have a manual entry on a point, it cannot become non-updated
 */

// Qualities
enum PointQuality_t
{
   UnintializedQuality = 0,
   InitDefaultQuality,
   InitLastKnownQuality,
   NonUpdatedQuality,
   ManualQuality,
   NormalQuality,
   ExceedsLowQuality,
   ExceedsHighQuality,
   AbnormalQuality,
   UnknownQuality,
   InvalidQuality,
   PartialIntervalQuality,
   DeviceFillerQuality,
   QuestionableQuality,
   OverflowQuality,
   PowerfailQuality,
   UnreasonableQuality,
   ConstantQuality,
   EstimatedQuality,
};
