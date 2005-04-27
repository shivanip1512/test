#pragma warning( disable : 4786)
#ifndef __POINTDEFS_H__
#define __POINTDEFS_H__

/*-----------------------------------------------------------------------------*
*
* File:   pointdefs
*
* Class:
* Date:   6/7/2000
*
* Author: Corey G. Plender
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define COMM_FAIL_OFFSET            2000
#define PAGECOUNTOFFSET             2001
#define PAGECHARCOUNTOFFSET         2002


#define DAILYCONTROLHISTOFFSET      2500
#define MONTHLYCONTROLHISTOFFSET    2501
#define SEASONALCONTROLHISTOFFSET   2502
#define ANNUALCONTROLHISTOFFSET     2503

#define CONTROLSTOPCOUNTDOWNOFFSET  2505

/* Status Point State definitions */
// 100900 CGP States changed to 0 and 1
#define INVALID             -1
#define OPENED              0
#define CLOSED              1
#define INDETERMINATE       2

#define STATEZERO           0
#define STATEONE            1
#define STATETWO            2
#define STATETHREE          3
#define STATEFOUR           4
#define STATEFIVE           5
#define STATESIX            6
#define STATESEVEN          7
#define STATEEIGHT          8
#define STATENINE           9
#define STATETEN            10

#define UNCONTROLLED        OPENED      // This is now LAW.
#define CONTROLLED          CLOSED


/*
 * Tags - may be applied to any entity in the system.  Define the running characteristics
 *  of the entity and may not be available offline, but can be rebuilt from stored state
 */

#define TAG_DISABLE_POINT_BY_POINT           0x00000001        // point/device out of service.
#define TAG_DISABLE_ALARM_BY_POINT           0x00000002        // point/device will not cause alarms.
#define TAG_DISABLE_CONTROL_BY_POINT         0x00000004        // point/device cannot be controled.

#define TAG_DISABLE_DEVICE_BY_DEVICE         0x00000010        // point/device out of service.
#define TAG_DISABLE_ALARM_BY_DEVICE          0x00000020        // point/device will not cause alarms.
#define TAG_DISABLE_CONTROL_BY_DEVICE        0x00000040        // point/device cannot be controled.
#define TAG_INVALID_LODESTAR_READING         0x00000080        // Lodestar Reading Invalid **000009**

#define TAG_POINT_DO_NOT_ROUTE               0x00000200        // This point data message will not be routed to clinets of dispatch
#define TAG_POINT_MOA_REPORT                 0x00000400        // This point data message is the result of a registration
#define TAG_POINT_DELAYED_UPDATE             0x00000800        // Dispatch delay this point data until the time specified in the message!

#define TAG_POINT_FORCE_UPDATE               0x00001000        // Dispatch will no matter what copy this into his RT memory
#define TAG_POINT_MUST_ARCHIVE               0x00002000        // This data will archive no matter how the point is set up
#define TAG_POINT_MAY_BE_EXEMPTED            0x00004000        // This data may be exempted from propagation if the value element has not changed
#define TAG_POINT_LOAD_PROFILE_DATA          0x00008000        // This data will archive to raw point history

#define TAG_MANUAL                           0x00010000        // Point was set manually by a client.. this affects quality.
#define TAG_EXTERNALVALUE                    0x00020000        // setByExternalApp           = 0x08, Another application set this value!
#define TAG_CONTROL_SELECTED                 0x00040000        // This control point is selected by a client for control
#define TAG_CONTROL_PENDING                  0x00080000        // This control has been executed and a change is pending

#define TAG_REPORT_MSG_TO_ALARM_CLIENTS      0x01000000        // This Message should be reported to any alarm clients in the world
#define TAG_REPORT_MSG_BLOCK_EXTRA_EMAIL     0x02000000        // This SIGNAL message should not be sent as an email!

#define TAG_ATTRIB_CONTROL_AVAILABLE         0x10000000        // This status point can also be controlled
#define TAG_ATTRIB_PSEUDO                    0x20000000        // Device/point is not real.
#define TAG_UNACKNOWLEDGED_ALARM             0x40000000        // Alarm State has not been acknowledged.
#define TAG_ACTIVE_ALARM                     0x80000000        // Alarm State is active now.

// Masks
#define MASK_RESETTABLE_TAGS                 0x0003FE00        // Tags which are reset upon any setPoint operation

#define MASK_ANY_ALARM                       0xC0000000        // Get any alarm
#define MASK_ANY_CONTROL                     0x000C0000

#define MASK_ANY_DISABLE                     0x00000077
#define MASK_ANY_POINT_DISABLE               0x00000007
#define MASK_ANY_DEVICE_DISABLE              0x00000070
#define MASK_ANY_SERVICE_DISABLE             0x00000011
#define MASK_ANY_ALARM_DISABLE               0x00000022
#define MASK_ANY_CONTROL_DISABLE             0x00000044

/***************************
* point offsets for electronic metering
****************************
*/

#define OFFSET_TOTAL_KWH                             1
#define OFFSET_PEAK_KW_OR_RATE_A_KW                  2
#define OFFSET_RATE_A_KWH                            3
#define OFFSET_RATE_B_KW                             4
#define OFFSET_RATE_B_KWH                            5
#define OFFSET_RATE_C_KW                             6
#define OFFSET_RATE_C_KWH                            7
#define OFFSET_RATE_D_KW                             8
#define OFFSET_RATE_D_KWH                            9
#define OFFSET_RATE_E_KW                            10
#define OFFSET_RATE_E_KWH                           11
#define OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW    14
#define OFFSET_LOADPROFILE_KW                       15

#define OFFSET_TOTAL_KVARH                          21
#define OFFSET_PEAK_KVAR_OR_RATE_A_KVAR             22
#define OFFSET_RATE_A_KVARH                         23
#define OFFSET_RATE_B_KVAR                          24
#define OFFSET_RATE_B_KVARH                         25
#define OFFSET_RATE_C_KVAR                          26
#define OFFSET_RATE_C_KVARH                         27
#define OFFSET_RATE_D_KVAR                          28
#define OFFSET_RATE_D_KVARH                         29
#define OFFSET_RATE_E_KVAR                          30
#define OFFSET_RATE_E_KVARH                         31
#define OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR  34
#define OFFSET_LOADPROFILE_KVAR                     35
#define OFFSET_LOADPROFILE_QUADRANT1_KVAR           36
#define OFFSET_LOADPROFILE_QUADRANT2_KVAR           37
#define OFFSET_LOADPROFILE_QUADRANT3_KVAR           38
#define OFFSET_LOADPROFILE_QUADRANT4_KVAR           39


#define OFFSET_TOTAL_KVAH                           41
#define OFFSET_PEAK_KVA_OR_RATE_A_KVA               42
#define OFFSET_RATE_A_KVAH                          43
#define OFFSET_RATE_B_KVA                           44
#define OFFSET_RATE_B_KVAH                          45
#define OFFSET_RATE_C_KVA                           46
#define OFFSET_RATE_C_KVAH                          47
#define OFFSET_RATE_D_KVA                           48
#define OFFSET_RATE_D_KVAH                          49
#define OFFSET_RATE_E_KVA                           50
#define OFFSET_RATE_E_KVAH                          51
#define OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA   54
#define OFFSET_LOADPROFILE_KVA                      55
#define OFFSET_LOADPROFILE_QUADRANT1_KVA            56
#define OFFSET_LOADPROFILE_QUADRANT2_KVA            57
#define OFFSET_LOADPROFILE_QUADRANT3_KVA            58
#define OFFSET_LOADPROFILE_QUADRANT4_KVA            59


#define OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE        61
#define OFFSET_LOADPROFILE_PHASE_A_VOLTAGE          62
#define OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE        63
#define OFFSET_LOADPROFILE_PHASE_B_VOLTAGE          64
#define OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE        65
#define OFFSET_LOADPROFILE_PHASE_C_VOLTAGE          66

#define OFFSET_INSTANTANEOUS_PHASE_A_CURRENT        71
#define OFFSET_LOADPROFILE_PHASE_A_CURRENT          72
#define OFFSET_INSTANTANEOUS_PHASE_B_CURRENT        73
#define OFFSET_LOADPROFILE_PHASE_B_CURRENT          74
#define OFFSET_INSTANTANEOUS_PHASE_C_CURRENT        75
#define OFFSET_LOADPROFILE_PHASE_C_CURRENT          76
#define OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT        77
#define OFFSET_LOADPROFILE_NEUTRAL_CURRENT          78

#define OFFSET_TOTAL_CHANNEL_1                      90
#define OFFSET_LOADPROFILE_CHANNEL_1                91
#define OFFSET_TOTAL_CHANNEL_2                      92
#define OFFSET_LOADPROFILE_CHANNEL_2                93
#define OFFSET_TOTAL_CHANNEL_3                      94
#define OFFSET_LOADPROFILE_CHANNEL_3                95
#define OFFSET_TOTAL_CHANNEL_4                      96
#define OFFSET_LOADPROFILE_CHANNEL_4                97

#define OFFSET_QUADRANT1_TOTAL_KVARH                110
#define OFFSET_QUADRANT1_LAST_INTERVAL_KVAR         111

#define OFFSET_QUADRANT2_TOTAL_KVARH                125
#define OFFSET_QUADRANT2_LAST_INTERVAL_KVAR         126

#define OFFSET_QUADRANT3_TOTAL_KVARH                140
#define OFFSET_QUADRANT3_LAST_INTERVAL_KVAR         141

#define OFFSET_QUADRANT4_TOTAL_KVARH                155
#define OFFSET_QUADRANT4_LAST_INTERVAL_KVAR         156


#define OFFSET_HIGHEST_CURRENT_OFFSET              160




// Points 180 to 200 will be reserved for odds and ends information
// Sentinel to report battery life
#define OFFSET_BATTERY_LIFE                        180
#define OFFSET_DAYS_ON_BATTERY                     181
#define OFFSET_POWER_FACTOR                        182
#define OFFSET_METER_TIME_STATUS                   183

/*
 * Qualities are singular (non-bit-masked) and based upon point values in conjunction with the tags
 * They are priority based with higher enumerative values able to overwrite lower ones.  For example
 * should dispatch have a manual entry on a point, it cannot become non-updated
 */

// Qualities
typedef enum
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
   ConstantQuality

} PointQuality_t;

// Relatively arbitrary, but should be ok.
#define MAX_HIGH_REASONABILITY          (1e30)
#define MIN_LOW_REASONABILITY           (-1e30)

#endif // #ifndef __POINTDEFS_H__
