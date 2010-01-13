#include "yukon.h"

#include "prot_gpuff.h"

#include "pt_base.h"
#include "cparms.h"

#include "logger.h"

using namespace std;

namespace Cti       {
namespace Protocols {

unsigned GpuffProtocol::decode( const unsigned char *p_data, unsigned last_seq, const string device_name, pointlist_t &point_list )
{
    unsigned len, cid, seq, devt, devr, ser, crc;
    bool crc_included, ack_required;

    crc_included = p_data[2] & 0x80;
    ack_required = p_data[2] & 0x40;
    len  = ((p_data[2] & 0x03) << 8) | p_data[3];
    cid  = (p_data[4] << 8) | p_data[5];
    seq  = (p_data[6] << 8) | p_data[7];
    devt = (p_data[8] << 8) | p_data[9];
    devt = (p_data[8] << 8) | p_data[9];
    devr = p_data[10];


    if( last_seq != seq )   // Is this a new message for this device?
    {
        int pos = 15, usedbytes = 0, fcn;

        CtiPointSPtr     point;
        CtiPointDataMsg *pdm;

        switch( devt )
        {
        case 1:  //  Faulted Circuit Indicator
        case 3:
            {
                while( pos < (len - (crc_included * 2)) )
                {
                    fcn = p_data[pos++];
                    switch( fcn )
                    {
                    case 0x00:
                        {
                            bool request_ack    =  p_data[pos] & 0x80;
                            int  udp_repeats    = (p_data[pos] & 0x70) >> 4;
                            int  phase          = (p_data[pos] & 0x0c) >> 2;
                            bool current_survey =  p_data[pos] & 0x02;

                            pos++;

                            float latitude, longitude;

                            latitude = convertBytes( p_data, pos, 4);
                            latitude  /= 10000.0;

                            longitude = convertBytes( p_data, pos, 4);
                            longitude /= 10000.0;

                            point_list.push_back(CTIDBG_new CtiPointDataMsg(FCI_Analog_Latitude, latitude, NormalQuality, AnalogPointType));
                            point_list.push_back(CTIDBG_new CtiPointDataMsg(FCI_Analog_Longitude, longitude, NormalQuality, AnalogPointType));

                            string device_name;

                            device_name.assign((char *)p_data + pos, 128);
                            if( device_name.find('\0') != string::npos )
                            {
                                device_name.erase(device_name.find_first_of('\0'));
                            }

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - FCI device has reported in **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << CtiTime() << " name: \"" << device_name << "\"" << endl;
                                dout << CtiTime() << " latitude:  " << string(CtiNumStr(latitude))  << endl;
                                dout << CtiTime() << " longitude: " << string(CtiNumStr(longitude)) << endl;
                            }

                            break;
                        }
                    case 0x01:
                        {
                            unsigned char msg_flags = p_data[pos++];
                            bool fault = p_data[pos] & 0x80;
                            bool noPower = p_data[pos] & 0x20;

                            pos++;

                            unsigned long time = 0;
                            float battery_voltage, temperature, amps_nominal, amps_peak;

                            if( msg_flags & 0x20 )
                            {
                                time = convertBytes( p_data, pos, 4);

                                if( msg_flags & 0x80 )
                                {
                                    time = CtiTime::now().seconds() - time;
                                }

                            }

                            pdm = CTIDBG_new CtiPointDataMsg(FCI_Status_Fault, fault, NormalQuality, StatusPointType);

                            if( msg_flags & 0x20 )
                            {
                                pdm->setTime(time);
                            }

                            point_list.push_back(pdm);
                            /*
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint: No Fault status point **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            */

                            pdm = CTIDBG_new CtiPointDataMsg(FCI_Status_NoPower, noPower, NormalQuality, StatusPointType);

                            if( msg_flags & 0x20 )
                            {
                                pdm->setTime(time);
                            }

                            point_list.push_back(pdm);

                            if( msg_flags & 0x10 )
                            {
                                battery_voltage = convertBytes( p_data, pos, 2);
                                battery_voltage /= 1000.0;

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(FCI_Analog_BatteryVoltage, battery_voltage, NormalQuality, AnalogPointType));
                            }
                            if( msg_flags & 0x08 )
                            {
                                temperature = convertSignedBytes( p_data, pos, 2);
                                temperature /= 100.0;

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(FCI_Analog_Temperature, temperature, NormalQuality, AnalogPointType));
                            }
                            if( msg_flags & 0x04 )
                            {
                                amps_nominal = convertBytes( p_data, pos, 2);

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(FCI_Analog_NominalAmps, amps_nominal, NormalQuality, AnalogPointType));
                            }
                            if( msg_flags & 0x02 )
                            {
                                amps_peak = convertBytes( p_data, pos, 2);

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(FCI_Analog_PeakAmps, amps_peak, NormalQuality, AnalogPointType));
                            }

                            break;
                        }
                    case 0x02:
                        {
                            unsigned char flags = p_data[pos++];

                            unsigned long time = 0;
                            unsigned long duration = 0;

                            bool current_fault_status = flags & 0x40;
                            bool event_report_status = false;

                            time = convertBytes( p_data, pos, 4);

                            if( flags & 0x80 )
                            {
                                time = CtiTime::now().seconds() - time;
                            }

                            if( flags & 0x20 )
                            {
                                //  it's an event report
                                event_report_status = flags & 0x01;
                            }
                            else
                            {
                                //  if it's not an event report, it has duration
                                duration = convertBytes( p_data, pos, 4);
                            }

                            break;
                        }
                    case 0x03:
                        {
                            unsigned char flags = p_data[pos++];

                            unsigned long time = 0;
                            int   rate = 0,
                            count;
                            float reading;

                            time = convertBytes( p_data, pos, 4);

                            if( flags & 0x80 )
                            {
                                time = CtiTime::now().seconds() - time;
                            }

                            switch( (p_data[pos  ] & 0xf8) >> 3 )
                            {
                            case 0:  rate =  60 * 60;  break;
                            case 1:  rate =  30 * 60;  break;
                            case 2:  rate =  15 * 60;  break;
                            case 3:  rate =   5 * 60;  break;
                            case 4:  rate = 120 * 60;  break;
                            case 5:  rate = 240 * 60;  break;
                            }

                            count = (int)(p_data[pos++] & 0x07) << 8 | (int)(p_data[pos++]);

                            for( int i = 0; i < count; i++ )
                            {
                                reading = convertBytes( p_data, pos, 2);

                                reading /= 1000.0;

                                time -= rate;
                            }

                            break;
                        }
                    case 0x04:
                        {
                            int index, port, hostname_len;

                            string hostname;

                            index = p_data[pos++];

                            port = convertBytes( p_data, pos, 2);

                            hostname_len = convertBytes( p_data, pos, 2);

                            if( (pos + hostname_len) < (len - (crc_included * 2)) )
                            {
                                hostname.assign((char *)p_data + pos, hostname_len);
                            }

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - device \"" << device_name << "\" host config report - index " << index << " : " << hostname << ":" << port << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

                            break;
                        }
                    case 0x05:
                        {
                            unsigned char msg_flags = p_data[pos++];

                            unsigned long time = 0;
                            float battery_voltage, temperature, amps_nominal, amps_peak;

                            if( msg_flags & 0x20 )
                            {
                                time = convertBytes( p_data, pos, 4);

                                if( msg_flags & 0x80 )
                                {
                                    time = CtiTime::now().seconds() - time;
                                }

                            }

                            unsigned short momCount = 0;
                            momCount = convertBytes( p_data, pos, 2);


                            pdm = CTIDBG_new CtiPointDataMsg(FCI_Accum_MomentaryCount, momCount, NormalQuality, PulseAccumulatorPointType);

                            if( msg_flags & 0x20 )
                            {
                                pdm->setTime(time);
                            }

                            point_list.push_back(pdm);

                            // Toggle the momentary point true then false to allow an alarm to be recorded.
                            if( momCount > 0 )
                            {
                                pdm = CTIDBG_new CtiPointDataMsg(FCI_Status_Momentary, true, NormalQuality, StatusPointType);

                                if( msg_flags & 0x20 )
                                {
                                    pdm->setTime(time);
                                }

                                point_list.push_back((CtiPointDataMsg *)pdm->replicateMessage());
                                pdm->setMillis(10); // We pulse the false value "at time now + 1ms ".
                                pdm->setValue(false);
                                point_list.push_back(pdm);
                            }

                            if( msg_flags & 0x04 )
                            {
                                amps_nominal = convertBytes( p_data, pos, 2);

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(FCI_Analog_NominalAmps, amps_nominal, NormalQuality, AnalogPointType));
                            }
                            if( msg_flags & 0x02 )
                            {
                                amps_peak = convertBytes( p_data, pos, 2);

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(FCI_Analog_PeakAmps, amps_peak, NormalQuality, AnalogPointType));
                            }

                            // TODO: Add duration here (msg_flags & 0x01)
                            if( msg_flags & 0x01 )
                            {
                                unsigned short duration = 0;
                                duration = convertBytes( p_data, pos, 2);
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " Duration decode unimplemented. Duration is: " << duration << ". " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                            }

                            break;
                        }
                    case 0x06:     // FCI - Device Values with Current Profile.
                        {
                            unsigned char msg_flags = convertBytes(p_data, pos, 1);
                            unsigned char msg_status = convertBytes(p_data, pos, 1);
                            unsigned char rec_num = convertBytes( p_data, pos, 1);

                            bool fault = msg_status & 0x80;
                            bool event = msg_status & 0x40;
                            bool noPower = msg_status & 0x20;
                            bool calibrated = msg_status & 0x10;
                            bool charge_enabled = msg_status & 0x08;
                            bool high_current = msg_status & 0x04;

                            unsigned long time = CtiTime::now().seconds();
                            unsigned int momentary_cnt = 0, th_high, rpt_max, rpt_min, amps;
                            float battery_voltage, temperature, amps_nominal, amps_peak;

                            if( msg_flags & 0x20 )
                            {
                                time = convertBytes( p_data, pos, 4);

                                if( msg_flags & 0x80 )
                                {
                                    time = CtiTime::now().seconds() - time;
                                }
                            }

                            pdm = CTIDBG_new CtiPointDataMsg(FCI_Status_Fault, fault, NormalQuality, StatusPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(FCI_Status_NoPower, noPower, NormalQuality, StatusPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            if( msg_flags & 0x10 )
                            {
                                battery_voltage = convertBytes( p_data, pos, 2);
                                battery_voltage /= 1000.0;

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(FCI_Analog_BatteryVoltage, battery_voltage, NormalQuality, AnalogPointType));
                            }

                            if( msg_flags & 0x08 )
                            {
                                temperature = convertSignedBytes( p_data, pos, 2);
                                temperature /= 100.0;

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(FCI_Analog_Temperature, temperature, NormalQuality, AnalogPointType));
                            }

                            if( msg_flags & 0x04 )
                            {
                                amps_nominal = convertBytes( p_data, pos, 2);

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(FCI_Analog_NominalAmps, amps_nominal, NormalQuality, AnalogPointType));
                            }

                            if( msg_flags & 0x02 )
                            {
                                amps_peak = convertBytes( p_data, pos, 2);

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(FCI_Analog_PeakAmps, amps_peak, NormalQuality, AnalogPointType));
                            }

                            momentary_cnt = convertBytes( p_data, pos, 2);
                            pdm = CTIDBG_new CtiPointDataMsg(FCI_Accum_MomentaryCount, momentary_cnt, NormalQuality, PulseAccumulatorPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);


                            th_high = convertBytes( p_data, pos, 2);
                            rpt_max = convertBytes( p_data, pos, 2);
                            rpt_min = convertBytes( p_data, pos, 2);


                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint: ADD CODE FOR 0x06 DECODE  **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

                            time -= rec_num * 3600;

                            for(int i = 0; i < rec_num; i++)
                            {
                                time += 3600;
                                amps = convertBytes( p_data, pos, 2);

                                // ACH
                            }

                            if(msg_flags & 0x01)
                            {
                                // REC[E] included
                                amps = convertBytes( p_data, pos, 2);

                                // ACH for DECODE
                            }

                            break;
                        }
                    case 0x08:
                        {
                            // This is a configuration packet!
                            unsigned char total_len = p_data[pos++];   // This is the total length of the Information elements contained herein.

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** ACH: Process the configuration packet. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

                            pos += total_len;   // hop past the config.
                            break;
                        }
                    default:
                        {
                            // We just consumed a GPUFF FCN we do not recognize.  All beds are off for the remainder of this message.
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Unknown GPUFF FCN " << fcn << " - message processing aborted " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            pos = len;
                            break;
                        }
                    }
                }

                break;
            }
        case 2:  //  Neutral Current Sensor
            {
                while( pos < (len - (crc_included * 2)) )
                {
                    fcn = p_data[pos++];
                    switch( fcn )
                    {
                    case 0x00:
                        {
                            bool request_ack    =  p_data[pos] & 0x80;
                            int  udp_repeats    = (p_data[pos] & 0x70) >> 4;
                            bool current_survey =  p_data[pos] & 0x02;

                            pos++;

                            float latitude, longitude, amp_threshold;

                            latitude = convertBytes( p_data, pos, 4);
                            latitude  /= 10000.0;

                            longitude = convertBytes( p_data, pos, 4);
                            longitude /= 10000.0;

                            amp_threshold = convertBytes( p_data, pos, 2);
                            amp_threshold /= 1000.0;

                            string device_name;

                            device_name.assign((char *)p_data + pos, 128);
                            if( device_name.find('\0') != string::npos )
                            {
                                device_name.erase(device_name.find_first_of('\0'));
                            }

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - Neutral Monitor device has reported in **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << CtiTime() << " name: \"" << device_name << "\"" << endl;
                                dout << CtiTime() << " latitude:      " << string(CtiNumStr(latitude))      << endl;
                                dout << CtiTime() << " longitude:     " << string(CtiNumStr(longitude))     << endl;
                                dout << CtiTime() << " amp threshold: " << string(CtiNumStr(amp_threshold)) << endl;
                            }

                            break;
                        }
                    case 0x01:
                        {
                            unsigned char msg_flags = p_data[pos++];

                            unsigned long time = 0;
                            float battery_voltage, temperature, amps;

                            if( msg_flags & 0x20 )
                            {
                                time = convertBytes( p_data, pos, 4);

                                if( msg_flags & 0x80 )
                                {
                                    time = CtiTime::now().seconds() - time;
                                }
                            }
                            if( msg_flags & 0x10 )
                            {
                                battery_voltage = convertBytes( p_data, pos, 2);
                                battery_voltage /= 1000.0;
                            }
                            if( msg_flags & 0x08 )
                            {
                                temperature = convertSignedBytes( p_data, pos, 2);
                                temperature /= 100.0;
                            }
                            if( msg_flags & 0x08 )
                            {
                                amps = convertBytes( p_data, pos, 2);
                            }

                            break;
                        }
                    case 0x02:
                        {
                            unsigned char flags = p_data[pos++];
                            unsigned long time = 0;
                            int rate, count, reading;
                            float battery_voltage;

                            time = convertBytes( p_data, pos, 4);

                            if( flags & 0x80 )
                            {
                                time = CtiTime::now().seconds() - time;
                            }

                            battery_voltage = convertBytes( p_data, pos, 2);
                            battery_voltage /= 1000.0;

                            if( !(rate = gConfigParms.getValueAsULong("CBNM_CURRENT_SURVEY_RATE", 0)) )
                            {
                                switch( (p_data[pos  ] & 0xc0) >> 6 )
                                {
                                case 0:  rate = 60 * 60;  break;
                                case 1:  rate = 30 * 60;  break;
                                case 2:  rate = 15 * 60;  break;
                                case 3:  rate =  5 * 60;  break;
                                }
                            }

                            count  = (p_data[pos++] & 0x3f) << 8;
                            count |=  p_data[pos++];

                            CtiPointSPtr point;

                            CtiPointDataMsg *pdm;

                            pdm = CTIDBG_new CtiPointDataMsg(CBNM_Analog_BatteryVoltage, battery_voltage, NormalQuality, AnalogPointType, "", TAG_POINT_MUST_ARCHIVE);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(CBNM_Analog_CurrentSurvey, 0.0, NormalQuality, AnalogPointType, "", TAG_POINT_MUST_ARCHIVE);

                            //  get the timestamp ready
                            time -= rate * count;

                            for( int i = 0; i < count; i++ )
                            {
                                time += rate;

                                reading = p_data[pos + (i * 2)] << 8 |
                                          p_data[pos + (i * 2) + 1];

                                pdm->setValue(reading);
                                pdm->setTime(time);

                                point_list.push_back((CtiPointDataMsg *)pdm->replicateMessage());
                            }

                            pos += count * 2;

                            delete pdm;

                            break;
                        }
                    case 0x03:
                        {
                            bool hasTime, over = false, reset = false, calibrated, reed_triggered;
                            unsigned char flags = p_data[pos++];

                            unsigned long emaxtime = 0, emintime = 0, time = CtiTime::now().seconds();
                            int interval_cnt = 0, sample_rate = 0, rate, ts_max, ts_min;
                            float battery_voltage = 0.0, temperature = 0.0, min_reading, max_reading;

                            hasTime = flags & 0x10 ? true : false;
                            calibrated = flags & 0x02 ? true : false;
                            reed_triggered = flags & 0x01 ? true : false;

                            if( hasTime )
                            {
                                time = convertBytes( p_data, pos, 4);

                                if( flags & 0x80 )
                                {
                                    time = CtiTime::now().seconds() - time;
                                }
                            }

                            if( flags & 0x08 )
                            {
                                battery_voltage = convertBytes( p_data, pos, 2);
                                battery_voltage /= 1000.0;
                            }

                            if( flags & 0x04 )
                            {
                                temperature = convertSignedBytes( p_data, pos, 2);
                                temperature /= 100.0;
                            }

                            if( !(rate = gConfigParms.getValueAsULong("CBNM_CURRENT_SURVEY_RATE", 0)) )
                            {
                                switch( (flags & 0x60) >> 5 )
                                {
                                case 0:
                                    interval_cnt = 24;
                                    sample_rate = 60 * 60;
                                    rate = interval_cnt * sample_rate;
                                    break;  // Default hourly rate.
                                case 1:
                                    interval_cnt = 3;
                                    sample_rate = 5 * 60;
                                    rate = interval_cnt * sample_rate;
                                    break;  // Firmware test rate of one per 5 minutes
                                }
                            }

                            float threshold_hi = convertBytes( p_data, pos, 2) / 10.0;
                            float threshold_lo = convertBytes( p_data, pos, 2) / 10.0;
                            float report_max = convertBytes( p_data, pos, 2) / 10.0;
                            float report_min = convertBytes( p_data, pos, 2) / 10.0;
                            int rec_num = convertBytes( p_data, pos, 1);

                            int report_flgs = convertBytes( p_data, pos, 1);
                            int report_period = report_flgs & 0x0000003f;
                            over = (report_flgs & 0x80) == 0x80;        // Over Threshold
                            reset = (report_flgs & 0x40) == 0x40;       // Reset Threshold

/**************************************************************
GVAR1A_Analog_MaxCurrentReport  =   1,
GVAR1A_Analog_MinCurrentReport  =   2,
GVAR1A_Analog_Battery           =   3,
GVAR1A_Analog_Temperature       =   4,
GVAR1A_Analog_MaxCurrentPeriod  =   5,
GVAR1A_Analog_MinCurrentPeriod  =   6,
GVAR1A_Analog_Period            =   10,
GVAR1A_Analog_HighAlertSetpoint =   11,
GVAR1A_Analog_ResetAlertSetpoint =  12,
                            *
**************************************************************/

                            pdm = CTIDBG_new CtiPointDataMsg(GVAR1A_Analog_MaxCurrentReport, report_max, NormalQuality, AnalogPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GVAR1A_Analog_MinCurrentReport, report_min, NormalQuality, AnalogPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GVAR1A_Analog_Battery, battery_voltage, NormalQuality, AnalogPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GVAR1A_Analog_Temperature, temperature, NormalQuality, AnalogPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GVAR1A_Analog_Period, rate, NormalQuality, AnalogPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GVAR1A_Analog_HighAlertSetpoint, threshold_hi, NormalQuality, AnalogPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GVAR1A_Analog_ResetAlertSetpoint, threshold_lo, NormalQuality, AnalogPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);


/*
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " time:          " << CtiTime(time) << endl;
                                dout << CtiTime() << " battery:       " << battery_voltage << endl;
                                dout << CtiTime() << " temperature    " << temperature << endl;
                                dout << CtiTime() << " record rate    " << string(CtiNumStr(rate))     << endl;
                                dout << CtiTime() << " calibrated     " << calibrated << endl;
                                dout << CtiTime() << " reed trigger   " << reed_triggered << endl;
                                dout << CtiTime() << " high threshold " << (float)threshold_hi / 10.0 << endl;
                                dout << CtiTime() << " low  threshold " << (float)threshold_lo / 10.0 << endl;
                                dout << CtiTime() << " report max     " << report_max << endl;
                                dout << CtiTime() << " report min     " << report_min << endl;
                                dout << CtiTime() << " record count   " << rec_num << endl;
                                dout << CtiTime() << " report period  " << report_period << endl;
                                dout << CtiTime() << " over alarm     " << over << endl;
                                dout << CtiTime() << " reset alarm    " << reset << endl;
                            }
*/

                            // get the timestamp ready
                            time -= rate * rec_num;

                            for( int i = 0; i < rec_num; i++ )
                            {
                                time += rate;
                                max_reading = convertBytes( p_data, pos, 2) / 10.0;
                                min_reading = convertBytes( p_data, pos, 2) / 10.0;
                                ts_max = convertBytes( p_data, pos, 1) & 0x3f;
                                ts_min = convertBytes( p_data, pos, 1) & 0x3f;

/*
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " interval time: " << CtiTime(time) <<
                                        " min time " << CtiTime(time - ((interval_cnt - ts_min - 1) * sample_rate)) << " min amps: " << string(CtiNumStr(min_reading)) <<
                                        " max time " << CtiTime(time - ((interval_cnt - ts_max - 1) * sample_rate)) << " max amps: " << string(CtiNumStr(max_reading)) <<
                                        " ts_min/max: " << string(CtiNumStr(ts_min)) << "/" << string(CtiNumStr(ts_max)) << endl;
                                }
*/
                                pdm = CTIDBG_new CtiPointDataMsg(GVAR1A_Analog_MaxCurrentPeriod, max_reading, NormalQuality, AnalogPointType);
                                pdm->setTime(CtiTime(time - ((interval_cnt - ts_max - 1) * sample_rate)));
                                point_list.push_back(pdm);

                                pdm = CTIDBG_new CtiPointDataMsg(GVAR1A_Analog_MinCurrentPeriod, min_reading, NormalQuality, AnalogPointType);
                                pdm->setTime(CtiTime(time - ((interval_cnt - ts_min - 1) * sample_rate)));
                                point_list.push_back(pdm);
                            }

                            // Process Rec[E]
                            max_reading = convertBytes( p_data, pos, 2) / 10.0;
                            min_reading = convertBytes( p_data, pos, 2) / 10.0;
                            ts_max = convertBytes( p_data, pos, 1);
                            bool rec_e_valid = ts_max & 0x40;
                            ts_max = ts_max & 0x3f;
                            ts_min = convertBytes( p_data, pos, 1) & 0x3f;

                            if(rec_e_valid)
                            {
                                if(reed_triggered)
                                {
                                    emaxtime = emintime = CtiTime::now().seconds(); // Time of arrival!
                                }
                                else if(reset)
                                {
                                    emintime = time + (ts_min + 1) * sample_rate;
                                    if(hasTime)
                                        emaxtime = time + (ts_max + 1) * sample_rate;
                                    else
                                        emaxtime = 0;    // Invalid
                                }
                                else if(over)
                                {
                                    emaxtime = time + (ts_max + 1) * sample_rate;
                                    if(hasTime)
                                        emintime = time + (ts_min + 1) * sample_rate;
                                    else
                                        emintime = 0;   // Invalid
                                }

                                if( emaxtime)
                                {
                                    pdm = CTIDBG_new CtiPointDataMsg(GVAR1A_Analog_MaxCurrentPeriod, max_reading, NormalQuality, AnalogPointType);
                                    pdm->setTime(CtiTime(emaxtime));
                                    point_list.push_back(pdm);
                                }

                                if( emintime )
                                {
                                    pdm = CTIDBG_new CtiPointDataMsg(GVAR1A_Analog_MinCurrentPeriod, min_reading, NormalQuality, AnalogPointType);
                                    pdm->setTime(CtiTime(emintime));
                                    point_list.push_back(pdm);
                                }
                            }
/*
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " event triggered record reed/over/reset: " << reed_triggered << "/" << over << "/" << reset << endl;
                                dout << CtiTime() <<
                                    " min time " << CtiTime(emintime) <<
                                    " min amps: " << string(CtiNumStr(min_reading)) <<
                                    " max time " << CtiTime(emaxtime) <<
                                    " max amps: " << string(CtiNumStr(max_reading)) <<
                                    " ts_min / max: " << string(CtiNumStr(ts_min)) << " / " << string(CtiNumStr(ts_max)) << endl;
                            }
*/

                            break;
                        }
                    case 0x08:
                        {
                            // This is a configuration packet!
                            int total_len = p_data[pos++];    // This is the total length of the Information elements contained herein.
                            const unsigned char *pConfig = &p_data[pos];    // This is a pointer to the config
                            int cfg_pos = 0;

                            int url_cnt = 0;
                            string stg;
                            string url_name;
                            string port_name;

                            while( cfg_pos < total_len )
                            {
                                unsigned char rsvd;    // Reserved byte
                                unsigned char ie_type = pConfig[cfg_pos++];    // Information Element Type
                                unsigned char ie_len = pConfig[cfg_pos++];    // Information Element Length
                                int next_ie_pos = cfg_pos + ie_len;

                                switch( ie_type )
                                {
                                case 0x01:
                                    {
                                        // APN Device to Server Information Element
                                        int periodic = pConfig[cfg_pos++];    // Nonzero for periodic configuraiton reports.
                                        rsvd = convertBytes(pConfig, cfg_pos, 1);

                                        string apn = convertBytesToString(pConfig, cfg_pos, ie_len-2 );

                                        /*
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << "APN                            : " << apn << endl;
                                            dout << "periodic cfg reports           : " << periodic << endl;
                                        }
                                        */

                                        break;
                                    }
                                case 0x02:
                                    {
                                        // IP Target Information Device to Server
                                        rsvd = convertBytes(pConfig, cfg_pos, 1);
                                        rsvd = convertBytes(pConfig, cfg_pos, 1);

                                        string port = convertBytesToString(pConfig, cfg_pos, 6);
                                        string url  = convertBytesToString(pConfig, cfg_pos, ie_len - 8);

                                        url_name = "url" + CtiNumStr(url_cnt);
                                        port_name = "ip port" + CtiNumStr(url_cnt++);

                                        /*
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << "URL                            : " << url << endl;
                                            dout << "IP Port                        : " << port << endl;
                                        }
                                        */

                                        break;
                                    }
                                case 0x03:
                                    {
                                        // Serial Number Information Device to Server
                                        unsigned int cooper_sn_h = convertBytes(pConfig, cfg_pos, 4);
                                        unsigned int cooper_sn_l = convertBytes(pConfig, cfg_pos, 4);

                                        unsigned int radio_sn_3 = convertBytes(pConfig, cfg_pos, 4);
                                        unsigned int radio_sn_2 = convertBytes(pConfig, cfg_pos, 4);
                                        unsigned int radio_sn_1 = convertBytes(pConfig, cfg_pos, 4);
                                        unsigned int radio_sn_0 = convertBytes(pConfig, cfg_pos, 4);

                                        /*
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << "cooper serial number h/l       : " << cooper_sn_h << " / " << cooper_sn_l << endl;
                                            dout << "radio serial number            : " << hex << showbase << uppercase << setfill('0')
                                                                                        << setw(2) << radio_sn_3 << " / "
                                                                                        << setw(2) << radio_sn_2 << " / "
                                                                                        << setw(2) << radio_sn_1 << " / "
                                                                                        << setw(2) << radio_sn_0 << endl;
                                        }
                                        */

                                        break;
                                    }
                                case 0x11:
                                    {
                                        // GVAR(x) configuration parameters.
                                        int report_freq_days = convertBytes(pConfig, cfg_pos, 1);
                                        rsvd = convertBytes(pConfig, cfg_pos, 1);
                                        int over_current_threshold = convertBytes(pConfig, cfg_pos, 2);
                                        int reset_threshold = convertBytes(pConfig, cfg_pos, 2);

                                        /*
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << "GVAR report frequency          : " << report_freq_days << endl;
                                            dout << "over current threshold         : " << over_current_threshold << endl;
                                            dout << "reset current threshold        : " << reset_threshold << endl;
                                        }
                                        */

                                        break;
                                    }
                                case 0x05:
                                case 0x85:
                                    {
                                        // Diagnostic.

                                        for( int i = 0; i < 32; i++ )  // Pop off the TraceQ.
                                        {
                                            rsvd = convertBytes(pConfig, cfg_pos, 1);
                                            rsvd = convertBytes(pConfig, cfg_pos, 1);
                                        }


                                        int guardbyte0       =  convertBytes(pConfig, cfg_pos, 1);
                                        int guardbyte1       =  convertBytes(pConfig, cfg_pos, 1);
                                        int firmware_major   =  convertBytes(pConfig, cfg_pos, 1);
                                        int firmware_minor   =  convertBytes(pConfig, cfg_pos, 1);
                                        int reset_count      =  convertBytes(pConfig, cfg_pos, 2);
                                        int SPI_errors       =  convertBytes(pConfig, cfg_pos, 2);
                                        int fw_major         =  convertBytes(pConfig, cfg_pos, 1);
                                        int fw_minor         =  convertBytes(pConfig, cfg_pos, 1);
                                        int rssi             =  convertBytes(pConfig, cfg_pos, 1);
                                        int ber              =  convertBytes(pConfig, cfg_pos, 1);
                                        int reset_momentary  =  convertBytes(pConfig, cfg_pos, 1);
                                        int revert_cnt       =  convertBytes(pConfig, cfg_pos, 1);

                                        /*
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << "firmware major                 : " << firmware_major  << endl;
                                            dout << "firmware minor                 : " << firmware_minor  << endl;
                                            dout << "reset_count                    : " << reset_count     << endl;
                                            dout << "spi_errors                     : " << SPI_errors      << endl;
                                            dout << "analog firmware major          : " << fw_major << endl;
                                            dout << "analog firmware minor          : " << fw_minor     << endl;
                                            dout << "rssi                           : " << rssi << endl;
                                            dout << "ber                            : " << ber<< endl;
                                            dout << "reset_momentary                : " << reset_momentary << endl;
                                            dout << "revert count                   : " << revert_cnt << endl << endl;
                                        }
                                        */

                                        pdm = CTIDBG_new CtiPointDataMsg(GVAR1A_Analog_RSSI, rssi, NormalQuality, AnalogPointType);
                                        point_list.push_back(pdm);

                                        pdm = CTIDBG_new CtiPointDataMsg(GVAR1A_Analog_BER, ber, NormalQuality, AnalogPointType);
                                        point_list.push_back(pdm);


                                        break;
                                    }
                                case 0x81:
                                    {
                                        // APN Server to Device Information Element
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                        }

                                        break;
                                    }
                                default:
                                    {
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << "  new/unknown ie_type  " << CtiNumStr((int)ie_type).hex().zpad(2) << "  total config len " << (int)total_len << " IE Position " << (int)(cfg_pos-2) << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                        }

                                        if(cfg_pos + ie_len < total_len)
                                        {
                                            // Process through the ie in the for loop - HOP OVER IT.
                                            string rawBytes = "";
                                            for( int xx = 0; xx < ie_len; xx++ )
                                            {
                                                if( xx == 0 ) rawBytes += CtiNumStr(convertBytes(pConfig, cfg_pos, 1)).hex().zpad(2).toString();
                                                else rawBytes += " " + CtiNumStr(convertBytes(pConfig, cfg_pos, 1)).hex().zpad(2).toString();
                                            }

                                            {
                                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                dout << CtiTime() << " ie_type = " << CtiNumStr((int)ie_type).hex().zpad(2) << " ie_len = " << (int)ie_len << ": " << rawBytes << endl;
                                            }
                                        }
                                        else
                                        {
                                            cfg_pos = total_len;
                                        }
                                        break;
                                    }
                                }

                                if(cfg_pos < next_ie_pos)
                                {
                                    string rawBytes = "";
                                    while(cfg_pos < next_ie_pos)
                                    {
                                        rawBytes += CtiNumStr(convertBytes(pConfig, cfg_pos, 1)).hex().zpad(2).toString() + " ";
                                    }
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " ie_type = " << CtiNumStr((int)ie_type).hex().zpad(2) << " ie_len = " << (int)ie_len << ": had extra bytes: " << rawBytes << endl;
                                    }
                                }
                            }

                            pos += total_len;   // hop past the config.


                            break;
                        }
                    default:
                        {
                            // We just consumed a GPUFF FCN we do not recognize.  All beds are off for the remainder of this message.
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Unknown GPUFF FCN " << fcn << " - message processing aborted " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            pos = len;
                            break;
                        }
                    }
                }

                break;
            }

        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - unknown GPUFF device type " << devt << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }

    return seq;
}


void GpuffProtocol::describeFrame(unsigned char *p_data, int p_len, int len, bool crc_included, bool ack_required, int devt, int ser)
{
    string keys, values;

    unsigned cid, seq, devr, crc;

    int pos = 15, usedbytes = 0, fcn;

    add_to_csv_summary(keys, values, "CRC included", crc_included);
    add_to_csv_summary(keys, values, "ACK required", ack_required);

    add_to_csv_summary(keys, values, "length", len);

    cid  = (p_data[4] << 8) | p_data[5];
    seq  = (p_data[6] << 8) | p_data[7];
    devr = p_data[10];


    add_to_csv_summary(keys, values, "CID",  cid);
    add_to_csv_summary(keys, values, "SEQ",  seq);
    add_to_csv_summary(keys, values, "DEVT", devt);
    add_to_csv_summary(keys, values, "DEVR", devr);
    add_to_csv_summary(keys, values, "SER",  ser);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << "LEN  : " << len << endl;
        dout << "CRC? : " << crc_included << endl;
        dout << "ACK? : " << ack_required << endl;
        dout << "CID  : " << cid << endl;
        dout << "SEQ  : " << seq << endl;
        dout << "DEVT : " << devt << endl;
        dout << "DEVR : " << devr << endl;
        dout << "SER  : " << ser << endl;

        dout << endl;
    }

    switch( devt )
    {
    case 3:  //  Faulted Circuit Indicator
    case 1:  //  Faulted Circuit Indicator
        {
            while( pos < (len - (crc_included * 2)) )
            {
                fcn = p_data[pos++];
                switch( fcn )
                {
                case 0x00:
                    {
                        bool request_ack    =  p_data[pos] & 0x80;
                        int  udp_repeats    = (p_data[pos] & 0x70) >> 4;
                        int  phase          = (p_data[pos] & 0x0c) >> 2;
                        bool current_survey =  p_data[pos] & 0x02;

                        add_to_csv_summary(keys, values, "ACK requested",  request_ack);
                        add_to_csv_summary(keys, values, "UDP repeats",    udp_repeats);
                        add_to_csv_summary(keys, values, "phase",          phase);
                        add_to_csv_summary(keys, values, "current survey", current_survey);

                        pos++;

                        float latitude, longitude;

                        latitude = convertBytes( p_data, pos, 4);
                        latitude  /= 10000.0;

                        longitude = convertBytes( p_data, pos, 4);
                        longitude /= 10000.0;

                        add_to_csv_summary(keys, values, "latitude",  latitude);
                        add_to_csv_summary(keys, values, "longitude", longitude);

                        string device_name;

                        device_name.assign((char *)p_data + pos, 128);
                        if( device_name.find('\0') != string::npos )
                        {
                            device_name.erase(device_name.find_first_of('\0'));
                        }

                        add_to_csv_summary(keys, values, "device name", device_name);

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - FCI device has reported in **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << CtiTime() << " name: \"" << device_name << "\"" << endl;
                            dout << CtiTime() << " latitude:    " << string(CtiNumStr(latitude))  << endl;
                            dout << CtiTime() << " longitude:   " << string(CtiNumStr(longitude)) << endl;
                            dout << CtiTime() << " request ack: " << request_ack << endl;
                            dout << CtiTime() << " udp repeats: " << udp_repeats << endl;
                            dout << CtiTime() << " phase:       " << phase << endl;
                            dout << CtiTime() << " current survey: " << current_survey << endl;
                        }

                        break;
                    }
                case 0x01:
                    {
                        unsigned char items_included = p_data[pos++];
                        bool fault = p_data[pos] & 0x80;
                        bool event = p_data[pos] & 0x40;
                        bool noPower = p_data[pos] & 0x20;

                        add_to_csv_summary(keys, values, "fault", fault);
                        add_to_csv_summary(keys, values, "event", event);
                        add_to_csv_summary(keys, values, "no power", noPower);

                        pos++;

                        unsigned long time = 0;
                        float battery_voltage, temperature, amps_nominal, amps_peak;

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - FCI device values **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << CtiTime() << " fault: " << fault << endl;
                            dout << CtiTime() << " event: " << event << endl;
                            dout << CtiTime() << " no power: " << noPower << endl;
                        }

                        add_to_csv_summary(keys, values, "includes time", items_included & 0x20);
                        add_to_csv_summary(keys, values, "TSO",           items_included & 0x80);
                        add_to_csv_summary(keys, values, "GMT",         !(items_included & 0x80));

                        if( items_included & 0x20 )
                        {
                            time = convertBytes( p_data, pos, 4);

                            if( items_included & 0x80 )
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " tso value: " << time << endl;
                                }
                                time = CtiTime::now().seconds() - time;
                            }

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " time: " << CtiTime(time) << endl;
                            }

                            add_to_csv_summary(keys, values, "time", CtiTime(time).asString());
                        }

                        add_to_csv_summary(keys, values, "includes battery voltage", items_included & 0x10);

                        if( items_included & 0x10 )
                        {
                            battery_voltage = convertBytes( p_data, pos, 2);

                            battery_voltage /= 1000.0;

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " battery voltage: " << battery_voltage << endl;
                            }
                        }

                        add_to_csv_summary(keys, values, "includes temperature", items_included & 0x08);

                        if( items_included & 0x08 )
                        {
                            temperature = convertSignedBytes( p_data, pos, 2);

                            temperature /= 100.0;

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " temperature: " << temperature << endl;
                            }

                            add_to_csv_summary(keys, values, "temperature", temperature);
                        }

                        add_to_csv_summary(keys, values, "includes nominal amps", items_included & 0x04);

                        if( items_included & 0x04 )
                        {
                            amps_nominal = convertBytes( p_data, pos, 2);

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " amps nominal: " << amps_nominal << endl;
                            }

                            add_to_csv_summary(keys, values, "nominal amps", amps_nominal);
                        }

                        add_to_csv_summary(keys, values, "includes peak amps", items_included & 0x02);

                        if( items_included & 0x02 )
                        {
                            amps_peak = convertBytes( p_data, pos, 2);

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " amps peak: " << amps_peak << endl;
                            }

                            add_to_csv_summary(keys, values, "peak amps", amps_peak);
                        }

                        break;
                    }
                case 0x02:
                    {
                        unsigned char flags = p_data[pos++];

                        unsigned long time = 0;
                        unsigned long duration = 0;

                        bool current_fault_status = flags & 0x40;
                        bool event_report_status = false;

                        time = convertBytes( p_data, pos, 4);

                        if( flags & 0x80 )
                        {
                            time = CtiTime::now().seconds() - time;
                        }

                        if( flags & 0x20 )
                        {
                            //  it's an event report
                            event_report_status = flags & 0x01;
                        }
                        else
                        {
                            //  if it's not an event report, it has duration
                            duration = convertBytes( p_data, pos, 4);
                        }

                        break;
                    }
                case 0x03:
                    {
                        unsigned char flags = p_data[pos++];

                        unsigned long time = 0;
                        int   rate = 0, count;
                        float reading;

                        time = convertBytes( p_data, pos, 4);

                        if( flags & 0x80 )
                        {
                            time = CtiTime::now().seconds() - time;
                        }

                        switch( (p_data[pos  ] & 0xf8) >> 3 )
                        {
                        case 0:  rate =  60 * 60;  break;
                        case 1:  rate =  30 * 60;  break;
                        case 2:  rate =  15 * 60;  break;
                        case 3:  rate =   5 * 60;  break;
                        case 4:  rate = 120 * 60;  break;
                        case 5:  rate = 240 * 60;  break;
                        }

                        count = (((int)(p_data[pos++]) & 0x07) << 8) | (int)(p_data[pos++]);

                        for( int i = 0; i < count; i++ )
                        {
                            reading = convertBytes( p_data, pos, 2);

                            reading /= 1000.0;

                            time -= rate;
                        }

                        break;
                    }
                case 0x04:
                    {
                        int index, port, len;

                        string hostname;

                        index = p_data[pos++];
                        port = convertBytes( p_data, pos, 2);
                        len = convertBytes( p_data, pos, 2);

                        hostname.assign((char *)p_data + pos, len);

                        break;
                    }
                case 0x05:
                    {
                        unsigned char items_included = p_data[pos++];

                        unsigned long time = 0;
                        float amps_nominal, amps_peak;

                        add_to_csv_summary(keys, values, "includes time", items_included & 0x20);
                        add_to_csv_summary(keys, values, "TSO",           items_included & 0x80);
                        add_to_csv_summary(keys, values, "GMT",         !(items_included & 0x80));

                        if( items_included & 0x20 )
                        {
                            time = convertBytes( p_data, pos, 4);

                            if( items_included & 0x80 )
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " tso value: " << time << endl;
                                }
                                time = CtiTime::now().seconds() - time;
                            }

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " time: " << CtiTime(time) << endl;
                            }

                            add_to_csv_summary(keys, values, "time", CtiTime(time).asString());
                        }

                        unsigned short momCount = 0;

                        momCount  = convertBytes( p_data, pos, 2);

                        add_to_csv_summary(keys, values, "momentary count", momCount);

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " momentary count: " << momCount << endl;
                        }

                        add_to_csv_summary(keys, values, "includes nominal amps", items_included & 0x04);

                        if( items_included & 0x04 )
                        {
                            amps_nominal = convertBytes( p_data, pos, 2);

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " amps nominal: " << amps_nominal << endl;
                            }

                            add_to_csv_summary(keys, values, "nominal amps", amps_nominal);
                        }

                        add_to_csv_summary(keys, values, "includes peak amps", items_included & 0x02);

                        if( items_included & 0x02 )
                        {
                            amps_peak = convertBytes( p_data, pos, 2);

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " amps peak: " << amps_peak << endl;
                            }

                            add_to_csv_summary(keys, values, "nominal amps", amps_peak);
                        }

                        // TODO: Add duration here (items_included & 0x01)
                        if( items_included & 0x01 )
                        {
                            unsigned short duration = 0;
                            duration = convertBytes( p_data, pos, 2);
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Duration decode unimplemented. Duration is: " << duration << ". " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }

                        break;
                    }
                case 0x06:     // FCI - Device Values with Current Profile.
                    {
                        unsigned char msg_flags = convertBytes(p_data, pos, 1);
                        unsigned char msg_status = convertBytes(p_data, pos, 1);
                        unsigned char rec_num = convertBytes( p_data, pos, 1);

                        bool fault = msg_status & 0x80;
                        bool event = msg_status & 0x40;
                        bool noPower = msg_status & 0x20;
                        bool calibrated = msg_status & 0x10;
                        bool charge_enabled = msg_status & 0x08;
                        bool high_current = msg_status & 0x04;

                        add_to_csv_summary(keys, values, "fault", fault);
                        add_to_csv_summary(keys, values, "event", event);
                        add_to_csv_summary(keys, values, "no power", noPower);
                        add_to_csv_summary(keys, values, "calibrated", calibrated);
                        add_to_csv_summary(keys, values, "charge ckt enabled", charge_enabled);
                        add_to_csv_summary(keys, values, "high current detect", high_current);

                        unsigned long time = CtiTime::now().seconds();
                        unsigned int momentary_cnt = 0, th_high, rpt_max, rpt_min, amps;
                        float battery_voltage, temperature, amps_nominal, amps_peak;

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - FCI device status/values with current profile **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << CtiTime() << " fault: " << fault << endl;
                            dout << CtiTime() << " event: " << event << endl;
                            dout << CtiTime() << " no power: " << noPower << endl;
                            dout << CtiTime() << " calibrated: " << calibrated << endl;
                            dout << CtiTime() << " charge ckt enabled: " << charge_enabled << endl;
                            dout << CtiTime() << " high current detect: " << high_current << endl;
                        }

                        add_to_csv_summary(keys, values, "includes time", msg_flags & 0x20);
                        add_to_csv_summary(keys, values, "TSO",           msg_flags & 0x80);
                        add_to_csv_summary(keys, values, "GMT",         !(msg_flags & 0x80));

                        if( msg_flags & 0x20 )
                        {
                            time = convertBytes( p_data, pos, 4);

                            if( msg_flags & 0x80 )
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " tso value: " << time << endl;
                                }
                                time = CtiTime::now().seconds() - time;
                            }

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " time: " << CtiTime(time) << endl;
                            }

                            add_to_csv_summary(keys, values, "time", CtiTime(time).asString());
                        }

                        add_to_csv_summary(keys, values, "includes battery voltage", msg_flags & 0x10);

                        if( msg_flags & 0x10 )
                        {
                            battery_voltage = convertBytes( p_data, pos, 2);

                            battery_voltage /= 1000.0;

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " battery voltage: " << battery_voltage << endl;
                            }
                        }

                        add_to_csv_summary(keys, values, "includes temperature", msg_flags & 0x08);

                        if( msg_flags & 0x08 )
                        {
                            temperature = convertSignedBytes( p_data, pos, 2);

                            temperature /= 100.0;

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " temperature: " << temperature << endl;
                            }

                            add_to_csv_summary(keys, values, "temperature", temperature);
                        }

                        add_to_csv_summary(keys, values, "includes nominal amps", msg_flags & 0x04);

                        if( msg_flags & 0x04 )
                        {
                            amps_nominal = convertBytes( p_data, pos, 2);

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " amps nominal: " << amps_nominal << endl;
                            }

                            add_to_csv_summary(keys, values, "nominal amps", amps_nominal);
                        }

                        add_to_csv_summary(keys, values, "includes peak amps", msg_flags & 0x02);

                        if( msg_flags & 0x02 )
                        {
                            amps_peak = convertBytes( p_data, pos, 2);

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " amps peak: " << amps_peak << endl;
                            }

                            add_to_csv_summary(keys, values, "peak amps", amps_peak);
                        }

                        momentary_cnt = convertBytes( p_data, pos, 2);
                        add_to_csv_summary(keys, values, "momentary count", momentary_cnt);
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " momentary count: " << momentary_cnt << endl;
                        }


                        th_high = convertBytes( p_data, pos, 2);
                        rpt_max = convertBytes( p_data, pos, 2);
                        rpt_min = convertBytes( p_data, pos, 2);

                        add_to_csv_summary(keys, values, "threshold high", th_high);
                        add_to_csv_summary(keys, values, "report max amps", rpt_max);
                        add_to_csv_summary(keys, values, "report min amps", rpt_min);
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " threshold high: " << th_high << endl;
                            dout << CtiTime() << " report max amps: " << rpt_max << endl;
                            dout << CtiTime() << " report min amps: " << rpt_min << endl;
                        }

                        time -= rec_num * 3600;

                        for(int i = 0; i < rec_num; i++)
                        {
                            time += 3600;
                            amps = convertBytes( p_data, pos, 2);
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime(time) << " record[" << i << "]: " << amps << endl;
                            }
                        }

                        if(msg_flags & 0x01)
                        {
                            // REC[E] included
                            amps = convertBytes( p_data, pos, 2);
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " record[E]: " << amps << endl;
                            }
                        }

                        break;
                    }
                case 0x08:
                    {
                        // This is a configuration packet!
                        int total_len = p_data[pos++];    // This is the total length of the Information elements contained herein.
                        unsigned char *pConfig = &p_data[pos];    // This is a pointer to the config
                        int cfg_pos = 0;

                        int url_cnt = 0;
                        string stg;
                        string url_name;
                        string port_name;

                        add_to_csv_summary(keys, values, "config length", total_len);

                        while( cfg_pos < total_len )
                        {
                            unsigned char rsvd;    // Reserved byte
                            unsigned char ie_type = pConfig[cfg_pos++];    // Information Element Type
                            unsigned char ie_len = pConfig[cfg_pos++];    // Information Element Length
                            int next_ie_pos = cfg_pos + ie_len;           //

                            switch( ie_type )
                            {
                            case 0x01:
                                {
                                    // APN Device to Server Information Element
                                    int periodic = pConfig[cfg_pos++];    // Nonzero for periodic configuraiton reports.
                                    rsvd = convertBytes(pConfig, cfg_pos, 1);

                                    string apn = convertBytesToString(pConfig, cfg_pos, ie_len-2 );


                                    add_to_csv_summary(keys, values, "periodic cfg report", periodic);
                                    add_to_csv_summary(keys, values, "apn", apn);

                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << "APN                            : " << apn << endl;
                                        dout << "periodic cfg reports           : " << periodic << endl;
                                    }

                                    break;
                                }
                            case 0x02:
                                {
                                    // IP Target Information Device to Server
                                    rsvd = convertBytes(pConfig, cfg_pos, 1);
                                    rsvd = convertBytes(pConfig, cfg_pos, 1);

                                    string port = convertBytesToString(pConfig, cfg_pos, 6);
                                    string url  = convertBytesToString(pConfig, cfg_pos, ie_len - 8);

                                    url_name = "url" + CtiNumStr(url_cnt);
                                    port_name = "ip port" + CtiNumStr(url_cnt++);

                                    add_to_csv_summary(keys, values, url_name, url);
                                    add_to_csv_summary(keys, values, port_name, port);

                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << "URL                            : " << url << endl;
                                        dout << "IP Port                        : " << port << endl;
                                    }


                                    break;
                                }
                            case 0x03:
                                {
                                    // Serial Number Information Device to Server
                                    unsigned int cooper_sn_h = convertBytes(pConfig, cfg_pos, 4);
                                    unsigned int cooper_sn_l = convertBytes(pConfig, cfg_pos, 4);

                                    unsigned int radio_sn_3 = convertBytes(pConfig, cfg_pos, 4);
                                    unsigned int radio_sn_2 = convertBytes(pConfig, cfg_pos, 4);
                                    unsigned int radio_sn_1 = convertBytes(pConfig, cfg_pos, 4);
                                    unsigned int radio_sn_0 = convertBytes(pConfig, cfg_pos, 4);

                                    add_to_csv_summary(keys, values, "cooper serial high", cooper_sn_h);
                                    add_to_csv_summary(keys, values, "cooper serial low ", cooper_sn_l);

                                    add_to_csv_summary(keys, values, "radio serial[3]", radio_sn_3);
                                    add_to_csv_summary(keys, values, "radio serial[2]", radio_sn_2);
                                    add_to_csv_summary(keys, values, "radio serial[1]", radio_sn_1);
                                    add_to_csv_summary(keys, values, "radio serial[0]", radio_sn_0);

                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << "cooper serial number h/l       : " << cooper_sn_h << " / " << cooper_sn_l << endl;
                                        dout << "radio serial number            : " << hex << showbase << uppercase << setfill('0')
                                                                                    << setw(2) << radio_sn_3 << " / "
                                                                                    << setw(2) << radio_sn_2 << " / "
                                                                                    << setw(2) << radio_sn_1 << " / "
                                                                                    << setw(2) << radio_sn_0 << endl;
                                    }

                                    break;
                                }
                            case 0x05:
                            case 0x85:
                                {
                                    // Diagnostic GCVT.

                                    for( int i = 0; i < 32; i++ )  // Pop off the TraceQ.
                                    {
                                        rsvd = convertBytes(pConfig, cfg_pos, 1);
                                        stg = "eDbg " + CtiNumStr(rsvd);
                                        rsvd = convertBytes(pConfig, cfg_pos, 1);
                                        // add_to_csv_summary(keys, values, stg, rsvd);
                                    }


                                    int guardbyte0       =  convertBytes(pConfig, cfg_pos, 1);
                                    int guardbyte1       =  convertBytes(pConfig, cfg_pos, 1);
                                    int firmware_major   =  convertBytes(pConfig, cfg_pos, 1);
                                    int firmware_minor   =  convertBytes(pConfig, cfg_pos, 1);
                                    int reset_count      =  convertBytes(pConfig, cfg_pos, 2);
                                    int SPI_errors       =  convertBytes(pConfig, cfg_pos, 2);
                                    int momentary_count  =  convertBytes(pConfig, cfg_pos, 1);
                                    int fault_count      =  convertBytes(pConfig, cfg_pos, 1);
                                    int all_clear_count  =  convertBytes(pConfig, cfg_pos, 1);
                                    int power_loss_count =  convertBytes(pConfig, cfg_pos, 1);
                                    int reset_momentary  =  convertBytes(pConfig, cfg_pos, 1);
                                    int revert_cnt       =  convertBytes(pConfig, cfg_pos, 1);

                                    // the following values pertain to devr >= 4
                                    int abfw_major;
                                    int abfw_minor;
                                    int rssi;
                                    int ber;
                                    int rs_cnt;

                                    add_to_csv_summary(keys, values, "guardbyte0",              guardbyte0      );
                                    add_to_csv_summary(keys, values, "guardbyte1",              guardbyte1      );
                                    add_to_csv_summary(keys, values, "firmware major",          firmware_major  );
                                    add_to_csv_summary(keys, values, "firmware minor",          firmware_minor  );
                                    add_to_csv_summary(keys, values, "reset count",             reset_count     );
                                    add_to_csv_summary(keys, values, "SPI errors",              SPI_errors      );
                                    add_to_csv_summary(keys, values, "momentary count",         momentary_count );
                                    add_to_csv_summary(keys, values, "fault count",             fault_count     );
                                    add_to_csv_summary(keys, values, "all clear count",         all_clear_count );
                                    add_to_csv_summary(keys, values, "power loss count",        power_loss_count);
                                    add_to_csv_summary(keys, values, "reset momentary count",   reset_momentary );
                                    add_to_csv_summary(keys, values, "revert count", revert_cnt );

                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << "firmware major                 : " << firmware_major  << endl;
                                        dout << "firmware minor                 : " << firmware_minor  << endl;
                                        dout << "reset_count                    : " << reset_count     << endl;
                                        dout << "spi_errors                     : " << SPI_errors      << endl;
                                        dout << "momentary_count                : " << momentary_count << endl;
                                        dout << "fault_count                    : " << fault_count     << endl;
                                        dout << "all_clear_count                : " << all_clear_count << endl;
                                        dout << "power_loss_count               : " << power_loss_count<< endl;
                                        dout << "reset_momentary                : " << reset_momentary << endl;
                                        dout << "revert count                   : " << revert_cnt << endl << endl;
                                    }

                                    if(devr >= 4 && cfg_pos < next_ie_pos)
                                    {
                                        abfw_major = convertBytes(pConfig, cfg_pos, 1);
                                        abfw_minor = convertBytes(pConfig, cfg_pos, 1);
                                        rssi       = convertBytes(pConfig, cfg_pos, 1);
                                        ber        = convertBytes(pConfig, cfg_pos, 1);
                                        rs_cnt     = convertBytes(pConfig, cfg_pos, 1);

                                        add_to_csv_summary(keys, values, "firmware major a-bd",     abfw_major );
                                        add_to_csv_summary(keys, values, "firmware minor a-bd",     abfw_minor     );
                                        add_to_csv_summary(keys, values, "rssi",                    rssi );
                                        add_to_csv_summary(keys, values, "ber",                     ber);
                                        add_to_csv_summary(keys, values, "rs count",                rs_cnt );

                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << "analog firmware major          : " << abfw_major << endl;
                                            dout << "analog firmware minor          : " << abfw_minor << endl;
                                            dout << "rssi                           : " << rssi << endl;
                                            dout << "ber                            : " << ber << endl;
                                            dout << "reed switch count              : " << rs_cnt << endl;
                                        }
                                    }

                                    break;
                                }
                            case 0x12:
                                {
                                    // GCVT(x) configuration parameters.
                                    int report_period_hours = convertBytes(pConfig, cfg_pos, 1);
                                    rsvd = convertBytes(pConfig, cfg_pos, 1);
                                    int high_current_threshold = convertBytes(pConfig, cfg_pos, 2);
                                    rsvd = convertBytes(pConfig, cfg_pos, 1);
                                    rsvd = convertBytes(pConfig, cfg_pos, 1);

                                    add_to_csv_summary(keys, values, "report period (hours)", report_period_hours);
                                    add_to_csv_summary(keys, values, "high current thresh.", high_current_threshold);

                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << "GCVT report period             : " << report_period_hours << endl;
                                        dout << "high current threshold         : " << high_current_threshold << endl;
                                    }

                                    break;
                                }
                            default:
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << "  new/unknown ie_type  " << CtiNumStr((int)ie_type).hex().zpad(2) << "  total config len " << (int)total_len << " IE Position " << (int)(cfg_pos-2) << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    }

                                    if(cfg_pos + ie_len < total_len)
                                    {
                                        // Process through the ie in the for loop - HOP OVER IT.
                                        string rawBytes = "";
                                        for( int xx = 0; xx < ie_len; xx++ )
                                        {
                                            if( xx == 0 ) rawBytes += CtiNumStr(convertBytes(pConfig, cfg_pos, 1)).hex().zpad(2).toString();
                                            else rawBytes += " " + CtiNumStr(convertBytes(pConfig, cfg_pos, 1)).hex().zpad(2).toString();
                                        }

                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << " ie_type = " << CtiNumStr((int)ie_type).hex().zpad(2) << " ie_len = " << (int)ie_len << ": " << rawBytes << endl;
                                        }
                                    }
                                    else
                                    {
                                        cfg_pos = total_len;
                                    }
                                    break;
                                }
                            }

                            if(cfg_pos < next_ie_pos)
                            {
                                string rawBytes = "";
                                while(cfg_pos < next_ie_pos)
                                {
                                    rawBytes += CtiNumStr(convertBytes(pConfig, cfg_pos, 1)).hex().zpad(2).toString() + " ";
                                }
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " ie_type = " << CtiNumStr((int)ie_type).hex().zpad(2) << " ie_len = " << (int)ie_len << ": had extra bytes: " << rawBytes << endl;
                                }
                            }
                        }

                        pos += total_len;   // hop past the config.


                        break;
                    }
                default:
                    {
                        // We just consumed a GPUFF FCN we do not recognize.  All beds are off for the remainder of this message.
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Unknown GPUFF FCN " << fcn << " - message processing aborted " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        pos = len;
                        break;
                    }
                }
            }

            if( gConfigParms.getValueAsInt("FCI_CSV_SUMMARY") )
            {
                string rawBytes = "\"";

                for( int xx = 0; xx < p_len; xx++ )
                {
                    if( xx == 0 ) rawBytes += CtiNumStr(p_data[xx]).hex().zpad(2).toString();
                    else rawBytes += " " + CtiNumStr(p_data[xx]).hex().zpad(2).toString();
                }
                rawBytes += "\"";
                add_to_csv_summary(keys, values, "raw data", rawBytes);

                #if 0
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** FCI CSV summary **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << keys   << endl;
                    dout << values << endl;
                }
                #endif
            }


            break;
        }
    case 2:                                 //  GVAR(x) - Neutral Current Sensor
        {
            while( pos < (len - (crc_included * 2)) )
            {
                fcn = p_data[pos++];
                switch( fcn )
                {
                case 0x00:
                    {
                        bool request_ack    =  p_data[pos] & 0x80;
                        int  udp_repeats    = (p_data[pos] & 0x70) >> 4;
                        bool current_survey =  p_data[pos] & 0x02;

                        pos++;

                        float latitude, longitude, amp_threshold;

                        latitude = convertBytes( p_data, pos, 4);
                        latitude  /= 10000.0;

                        longitude = convertBytes( p_data, pos, 4);
                        longitude /= 10000.0;

                        amp_threshold = convertBytes( p_data, pos, 2);

                        amp_threshold /= 10000.0;

                        string device_name;

                        device_name.assign((char *)p_data + pos, 128);
                        if( device_name.find('\0') != string::npos )
                        {
                            device_name.erase(device_name.find_first_of('\0'));
                        }

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - Neutral Monitor device has reported in **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << CtiTime() << " name: \"" << device_name << "\"" << endl;
                            dout << CtiTime() << " latitude:      " << string(CtiNumStr(latitude))      << endl;
                            dout << CtiTime() << " longitude:     " << string(CtiNumStr(longitude))     << endl;
                            dout << CtiTime() << " amp threshold: " << string(CtiNumStr(amp_threshold)) << endl;
                        }

                        break;
                    }
                case 0x01:
                    {
                        unsigned char msg_flags = p_data[pos++];

                        unsigned long time = 0;
                        float battery_voltage, temperature, amps;

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - GVAR(X): Device Values 0x01 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        if( msg_flags & 0x20 )
                        {
                            time = convertBytes( p_data, pos, 4);

                            if( msg_flags & 0x80 )
                            {
                                time = CtiTime::now().seconds() - time;
                            }

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " time:          " << CtiTime(time) << endl;
                            }
                        }
                        if( msg_flags & 0x10 )
                        {
                            battery_voltage = convertBytes( p_data, pos, 2);
                            battery_voltage /= 1000.0;
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " battery:       " << string(CtiNumStr(battery_voltage))     << endl;
                            }
                        }
                        if( msg_flags & 0x08 )
                        {
                            temperature = convertSignedBytes( p_data, pos, 2);
                            temperature /= 100.0;
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " temperature:   " << string(CtiNumStr(temperature))     << endl;
                            }
                        }
                        if( msg_flags & 0x08 )
                        {
                            amps = convertBytes( p_data, pos, 2);
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " amps:          " << string(CtiNumStr(amps))     << endl;
                            }
                        }


                        break;
                    }
                case 0x02:
                    {
                        unsigned char flags = p_data[pos++];

                        unsigned long time = 0;
                        int rate, count, reading;
                        float battery_voltage;

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - GVAR(X): Neutral Current History Report 0x02 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        time = convertBytes( p_data, pos, 4);
                        if( flags & 0x80 )
                        {
                            time = CtiTime::now().seconds() - time;
                        }

                        battery_voltage = convertBytes( p_data, pos, 2);
                        battery_voltage /= 1000.0;

                        if( !(rate = gConfigParms.getValueAsULong("CBNM_CURRENT_SURVEY_RATE", 0)) )
                        {
                            switch( (p_data[pos  ] & 0xc0) >> 6 )
                            {
                            case 0:  rate = 60 * 60;  break;
                            case 1:  rate = 30 * 60;  break;
                            case 2:  rate = 15 * 60;  break;
                            case 3:  rate =  5 * 60;  break;
                            }
                        }

                        count  = (p_data[pos++] & 0x3f) << 8;
                        count |=  p_data[pos++];

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " time:          " << CtiTime(time) << endl;
                            dout << CtiTime() << " battery:       " << battery_voltage << endl;
                            dout << CtiTime() << " rate:          " << string(CtiNumStr(rate))     << endl;
                            dout << CtiTime() << " count:         " << string(CtiNumStr(count))     << endl;
                        }
                        //  get the timestamp ready
                        time -= rate * count;

                        for( int i = 0; i < count; i++ )
                        {
                            time += rate;
                            reading = p_data[pos + (i * 2)] << 8 | p_data[pos + (i * 2) + 1];

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " time:          " << CtiTime(time) << " amps: " << string(CtiNumStr(reading)) << endl;
                            }
                        }

                        pos += count * 2;


                        break;
                    }
                case 0x03:
                    {
                        bool hasTime, over = false, reset = false, calibrated, reed_triggered;
                        unsigned char flags = p_data[pos++];

                        unsigned long emaxtime = 0, emintime = 0, time = CtiTime::now().seconds();
                        int interval_cnt = 0, sample_rate = 0, rate, ts_max, ts_min;
                        float battery_voltage = 0.0, temperature = 0.0, min_reading, max_reading;

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - GVAR(X): Neutral Current History Report 0x03 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        hasTime = flags & 0x10 ? true : false;
                        calibrated = flags & 0x02 ? true : false;
                        reed_triggered = flags & 0x01 ? true : false;

                        if( hasTime )
                        {
                            time = convertBytes( p_data, pos, 4);

                            if( flags & 0x80 )
                            {
                                time = CtiTime::now().seconds() - time;
                            }
                        }

                        if( flags & 0x08 )
                        {
                            battery_voltage = convertBytes( p_data, pos, 2);
                            battery_voltage /= 1000.0;
                        }

                        if( flags & 0x04 )
                        {
                            temperature = convertSignedBytes( p_data, pos, 2);
                            temperature /= 100.0;
                        }

                        if( !(rate = gConfigParms.getValueAsULong("CBNM_CURRENT_SURVEY_RATE", 0)) )
                        {
                            switch( (flags & 0x60) >> 5 )
                            {
                            case 0:
                                interval_cnt = 24;
                                sample_rate = 60 * 60;
                                rate = interval_cnt * sample_rate;
                                break;  // Default hourly rate.
                            case 1:
                                interval_cnt = 3;
                                sample_rate = 5 * 60;
                                rate = interval_cnt * sample_rate;
                                break;  // Firmware test rate of one per 5 minutes
                            }
                        }

                        float threshold_hi = convertBytes( p_data, pos, 2) / 10.0;
                        float threshold_lo = convertBytes( p_data, pos, 2) / 10.0;
                        float report_max = convertBytes( p_data, pos, 2) / 10.0;
                        float report_min = convertBytes( p_data, pos, 2) / 10.0;
                        int rec_num = convertBytes( p_data, pos, 1);

                        int report_flgs = convertBytes( p_data, pos, 1);
                        int report_period = report_flgs & 0x0000003f;
                        over = (report_flgs & 0x80) == 0x80;        // Over Threshold
                        reset = (report_flgs & 0x40) == 0x40;       // Reset Threshold

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " time:          " << CtiTime(time) << endl;
                            dout << CtiTime() << " battery:       " << battery_voltage << endl;
                            dout << CtiTime() << " temperature    " << temperature << endl;
                            dout << CtiTime() << " record rate    " << string(CtiNumStr(rate))     << endl;
                            dout << CtiTime() << " calibrated     " << calibrated << endl;
                            dout << CtiTime() << " reed trigger   " << reed_triggered << endl;
                            dout << CtiTime() << " high threshold " << threshold_hi << endl;
                            dout << CtiTime() << " low  threshold " << threshold_lo << endl;
                            dout << CtiTime() << " report max     " << report_max << endl;
                            dout << CtiTime() << " report min     " << report_min << endl;
                            dout << CtiTime() << " record count   " << rec_num << endl;
                            dout << CtiTime() << " report period  " << report_period << endl;
                            dout << CtiTime() << " over alarm     " << over << endl;
                            dout << CtiTime() << " reset alarm    " << reset << endl;
                        }

                        // get the timestamp ready
                        time -= rate * rec_num;

                        for( int i = 0; i < rec_num; i++ )
                        {
                            time += rate;
                            max_reading = convertBytes( p_data, pos, 2) / 10.0;
                            min_reading = convertBytes( p_data, pos, 2) / 10.0;
                            ts_max = convertBytes( p_data, pos, 1) & 0x3f;
                            ts_min = convertBytes( p_data, pos, 1) & 0x3f;

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " interval time: " << CtiTime(time) <<
                                " min time " << CtiTime(time - ((interval_cnt - ts_min - 1) * sample_rate)) << " min amps: " << string(CtiNumStr(min_reading)) <<
                                " max time " << CtiTime(time - ((interval_cnt - ts_max - 1) * sample_rate)) << " max amps: " << string(CtiNumStr(max_reading)) <<
                                " ts_min/max: " << string(CtiNumStr(ts_min)) << "/" << string(CtiNumStr(ts_max)) << endl;
                            }
                        }

                        // Process Rec[E]
                        max_reading = convertBytes( p_data, pos, 2) / 10.0;
                        min_reading = convertBytes( p_data, pos, 2) / 10.0;
                        ts_max = convertBytes( p_data, pos, 1);
                        bool rec_e_valid = ts_max & 0x40;
                        ts_max = ts_max & 0x3f;
                        ts_min = convertBytes( p_data, pos, 1) & 0x3f;

                        if(rec_e_valid)
                        {
                            if(reed_triggered)
                            {
                                emaxtime = emintime = CtiTime::now().seconds(); // Time of arrival!
                            }
                            else if(reset)
                            {
                                emintime = time + (ts_min + 1) * sample_rate;
                                if(hasTime)
                                    emaxtime = time + (ts_max + 1) * sample_rate;
                                else
                                    emaxtime = 0;    // Invalid
                            }
                            else if(over)
                            {
                                emaxtime = time + (ts_max + 1) * sample_rate;
                                if(hasTime)
                                    emintime = time + (ts_min + 1) * sample_rate;
                                else
                                    emintime = 0;   // Invalid
                            }

                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " event triggered record reed/over/reset: " << reed_triggered << "/" << over << "/" << reset << endl;
                            dout << CtiTime() <<
                            " min time " << CtiTime(emintime) <<
                            " min amps: " << string(CtiNumStr(min_reading)) <<
                            " max time " << CtiTime(emaxtime) <<
                            " max amps: " << string(CtiNumStr(max_reading)) <<
                            " ts_min / max: " << string(CtiNumStr(ts_min)) << " / " << string(CtiNumStr(ts_max)) << endl;
                        }

                        break;
                    }
                case 0x08:
                    {
                        // This is a configuration packet!
                        int total_len = p_data[pos++];    // This is the total length of the Information elements contained herein.
                        unsigned char *pConfig = &p_data[pos];    // This is a pointer to the config
                        int cfg_pos = 0;

                        int url_cnt = 0;
                        string stg;
                        string url_name;
                        string port_name;

                        add_to_csv_summary(keys, values, "config length", total_len);

                        while( cfg_pos < total_len )
                        {
                            unsigned char rsvd;    // Reserved byte
                            unsigned char ie_type = pConfig[cfg_pos++];    // Information Element Type
                            unsigned char ie_len = pConfig[cfg_pos++];    // Information Element Length
                            int next_ie_pos = cfg_pos + ie_len;

                            switch( ie_type )
                            {
                            case 0x01:
                                {
                                    // APN Device to Server Information Element
                                    int periodic = pConfig[cfg_pos++];    // Nonzero for periodic configuraiton reports.
                                    rsvd = convertBytes(pConfig, cfg_pos, 1);

                                    string apn = convertBytesToString(pConfig, cfg_pos, ie_len-2 );


                                    add_to_csv_summary(keys, values, "periodic cfg report", periodic);
                                    add_to_csv_summary(keys, values, "apn", apn);

                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << "APN                            : " << apn << endl;
                                        dout << "periodic cfg reports           : " << periodic << endl;
                                    }

                                    break;
                                }
                            case 0x02:
                                {
                                    // IP Target Information Device to Server
                                    rsvd = convertBytes(pConfig, cfg_pos, 1);
                                    rsvd = convertBytes(pConfig, cfg_pos, 1);

                                    string port = convertBytesToString(pConfig, cfg_pos, 6);
                                    string url  = convertBytesToString(pConfig, cfg_pos, ie_len - 8);

                                    url_name = "url" + CtiNumStr(url_cnt);
                                    port_name = "ip port" + CtiNumStr(url_cnt++);

                                    add_to_csv_summary(keys, values, url_name, url);
                                    add_to_csv_summary(keys, values, port_name, port);

                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << "URL                            : " << url << endl;
                                        dout << "IP Port                        : " << port << endl;
                                    }


                                    break;
                                }
                            case 0x03:
                                {
                                    // Serial Number Information Device to Server
                                    unsigned int cooper_sn_h = convertBytes(pConfig, cfg_pos, 4);
                                    unsigned int cooper_sn_l = convertBytes(pConfig, cfg_pos, 4);

                                    unsigned int radio_sn_3 = convertBytes(pConfig, cfg_pos, 4);
                                    unsigned int radio_sn_2 = convertBytes(pConfig, cfg_pos, 4);
                                    unsigned int radio_sn_1 = convertBytes(pConfig, cfg_pos, 4);
                                    unsigned int radio_sn_0 = convertBytes(pConfig, cfg_pos, 4);

                                    add_to_csv_summary(keys, values, "cooper serial high", cooper_sn_h);
                                    add_to_csv_summary(keys, values, "cooper serial low ", cooper_sn_l);

                                    add_to_csv_summary(keys, values, "radio serial[3]", radio_sn_3);
                                    add_to_csv_summary(keys, values, "radio serial[2]", radio_sn_2);
                                    add_to_csv_summary(keys, values, "radio serial[1]", radio_sn_1);
                                    add_to_csv_summary(keys, values, "radio serial[0]", radio_sn_0);

                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << "cooper serial number h/l       : " << cooper_sn_h << " / " << cooper_sn_l << endl;
                                        dout << "radio serial number            : " << hex << showbase << uppercase << setfill('0')
                                                                                    << setw(2) << radio_sn_3 << " / "
                                                                                    << setw(2) << radio_sn_2 << " / "
                                                                                    << setw(2) << radio_sn_1 << " / "
                                                                                    << setw(2) << radio_sn_0 << endl;
                                    }

                                    break;
                                }
                            case 0x11:
                                {
                                    // GVAR(x) configuration parameters.
                                    int report_freq_days = convertBytes(pConfig, cfg_pos, 1);
                                    rsvd = convertBytes(pConfig, cfg_pos, 1);
                                    float over_current_threshold = convertBytes(pConfig, cfg_pos, 2) / 10.0;
                                    float reset_threshold = convertBytes(pConfig, cfg_pos, 2) / 10.0;

                                    add_to_csv_summary(keys, values, "report freq", report_freq_days);
                                    add_to_csv_summary(keys, values, "over current thresh.", over_current_threshold);
                                    add_to_csv_summary(keys, values, "reset thresh.", reset_threshold);

                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << "GVAR report frequency          : " << report_freq_days << endl;
                                        dout << "over current threshold         : " << over_current_threshold << endl;
                                        dout << "reset current threshold        : " << reset_threshold << endl;
                                    }

                                    break;
                                }
                            case 0x05:
                            case 0x85:
                                {
                                    // Diagnostic GVAR.

                                    for( int i = 0; i < 32; i++ )  // Pop off the TraceQ.
                                    {
                                        rsvd = convertBytes(pConfig, cfg_pos, 1);
                                        stg = "eDbg " + CtiNumStr(rsvd);
                                        rsvd = convertBytes(pConfig, cfg_pos, 1);
                                        // add_to_csv_summary(keys, values, stg, rsvd);
                                    }


                                    int guardbyte0       =  convertBytes(pConfig, cfg_pos, 1);
                                    int guardbyte1       =  convertBytes(pConfig, cfg_pos, 1);
                                    int firmware_major   =  convertBytes(pConfig, cfg_pos, 1);
                                    int firmware_minor   =  convertBytes(pConfig, cfg_pos, 1);
                                    int reset_count      =  convertBytes(pConfig, cfg_pos, 2);
                                    int SPI_errors       =  convertBytes(pConfig, cfg_pos, 2);
                                    int abfw_major       =  convertBytes(pConfig, cfg_pos, 1);
                                    int abfw_minor       =  convertBytes(pConfig, cfg_pos, 1);
                                    int rssi             =  convertBytes(pConfig, cfg_pos, 1);
                                    int ber              =  convertBytes(pConfig, cfg_pos, 1);
                                    rsvd                 =  convertBytes(pConfig, cfg_pos, 1);
                                    int revert_cnt       =  convertBytes(pConfig, cfg_pos, 1);

                                    add_to_csv_summary(keys, values, "guardbyte0",              guardbyte0      );
                                    add_to_csv_summary(keys, values, "guardbyte1",              guardbyte1      );
                                    add_to_csv_summary(keys, values, "firmware major",          firmware_major  );
                                    add_to_csv_summary(keys, values, "firmware minor",          firmware_minor  );
                                    add_to_csv_summary(keys, values, "reset count",             reset_count     );
                                    add_to_csv_summary(keys, values, "SPI errors",              SPI_errors      );
                                    add_to_csv_summary(keys, values, "firmware major a-bd",     abfw_major );
                                    add_to_csv_summary(keys, values, "firmware minor a-bd",     abfw_minor     );
                                    add_to_csv_summary(keys, values, "rssi",                    rssi );
                                    add_to_csv_summary(keys, values, "ber",                     ber);
                                    add_to_csv_summary(keys, values, "revert count", revert_cnt );

                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << "firmware major                 : " << firmware_major  << endl;
                                        dout << "firmware minor                 : " << firmware_minor  << endl;
                                        dout << "reset_count                    : " << reset_count     << endl;
                                        dout << "spi_errors                     : " << SPI_errors      << endl;
                                        dout << "analog firmware major          : " << abfw_major << endl;
                                        dout << "analog firmware minor          : " << abfw_minor     << endl;
                                        dout << "rssi                           : " << rssi << endl;
                                        dout << "ber                            : " << ber<< endl;
                                        dout << "revert count                   : " << revert_cnt << endl << endl;
                                    }

                                    break;
                                }
                            case 0x81:
                                {
                                    // APN Server to Device Information Element
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    }

                                    break;
                                }
                            default:
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << "  new/unknown ie_type  " << CtiNumStr((int)ie_type).hex().zpad(2) << "  total config len " << (int)total_len << " IE Position " << (int)(cfg_pos-2) << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    }

                                    if(cfg_pos + ie_len < total_len)
                                    {
                                        // Process through the ie in the for loop - HOP OVER IT.
                                        string rawBytes = "";
                                        for( int xx = 0; xx < ie_len; xx++ )
                                        {
                                            if( xx == 0 ) rawBytes += CtiNumStr(convertBytes(pConfig, cfg_pos, 1)).hex().zpad(2).toString();
                                            else rawBytes += " " + CtiNumStr(convertBytes(pConfig, cfg_pos, 1)).hex().zpad(2).toString();
                                        }

                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << " ie_type = " << CtiNumStr((int)ie_type).hex().zpad(2) << " ie_len = " << (int)ie_len << ": " << rawBytes << endl;
                                        }
                                    }
                                    else
                                    {
                                        cfg_pos = total_len;
                                    }
                                    break;
                                }
                            }

                            if(cfg_pos < next_ie_pos)
                            {
                                string rawBytes = "";
                                while(cfg_pos < next_ie_pos)
                                {
                                    rawBytes += CtiNumStr(convertBytes(pConfig, cfg_pos, 1)).hex().zpad(2).toString() + " ";
                                }
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " ie_type = " << CtiNumStr((int)ie_type).hex().zpad(2) << " ie_len = " << (int)ie_len << ": had extra bytes: " << rawBytes << endl;
                                }
                            }
                        }

                        pos += total_len;   // hop past the config.


                        break;
                    }
                default:
                    {
                        // We just consumed a GPUFF FCN we do not recognize.  All beds are off for the remainder of this message.
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Unknown GPUFF FCN " << fcn << " - message processing aborted " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        pos = len;
                        break;
                    }
                }
            }

            break;
        }

    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - unknown GPUFF device type " << devt << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
}


string GpuffProtocol::convertBytesToString( const unsigned char *buf, int &position, int bytes_to_combine )
{
    string str;
    str.assign((char *)buf + position, bytes_to_combine);
    if( str.find('\0') != string::npos ) str.erase(str.find_first_of('\0'));
    position += bytes_to_combine;

    return str;
}

/* Notice that the passed by reference variable "position" is incremented. */
unsigned int GpuffProtocol::convertBytes( const unsigned char *buf, int &position, int bytes_to_combine )
{
    unsigned int temp = 0;

    for( int i = 0; i < bytes_to_combine; i++ )
    {
        temp = temp << 8;
        temp |= buf[position++];
    }

    return temp;
}

/* Notice that the passed by reference variable "position" is incremented. */
int GpuffProtocol::convertSignedBytes( const unsigned char *buf, int &position, int bytes_to_combine )
{
    unsigned int temp = 0;
    int retval;

    for(int i = 0; i < bytes_to_combine; i++)
    {
        temp = temp << 8;
        temp |= buf[position++];
    }

    switch(bytes_to_combine)
    {
    case 1:
        {
            unsigned char usv = temp;
            char sv = (char)usv;
            retval =  sv;

            break;
        }
    case 2:
        {
            unsigned short usv = temp;
            short sv = (short)usv;
            retval =  sv;

            break;
        }
    case 4:
    default:                        // Just being explicit here.
        {
            retval = (int) temp;
            break;
        }
    }

    return retval;
}


void GpuffProtocol::add_to_csv_summary( string &keys, string &values, string key, bool value )
{
    keys += key + ",";
    values += value?"Y,":"N,";
}


void GpuffProtocol::add_to_csv_summary( string &keys, string &values, string key, int value )
{
    keys += key + ",";
    values += CtiNumStr(value) + ",";
}


void GpuffProtocol::add_to_csv_summary( string &keys, string &values, string key, unsigned value )
{
    keys += key + ",";
    values += CtiNumStr(value) + ",";
}


void GpuffProtocol::add_to_csv_summary( string &keys, string &values, string key, float value )
{
    keys += key + ",";
    values += CtiNumStr(value) + ",";
}


void GpuffProtocol::add_to_csv_summary( string &keys, string &values, string key, string value )
{
    keys += key + ",";
    values += value + ",";  //  to make this robust, it should escape commas
}


bool GpuffProtocol::isPacketValid(const unsigned char *buf, const size_t len)
{
    if( len < HeaderLength )
    {
        return false;
    }

    if( buf[0] != 0xa5 ||
        buf[1] != 0x96 )
    {
        return false;
    }

    unsigned packet_length = ((buf[2] & 0x03) << 8) | buf[3];

    if( len < packet_length + 4 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - Cti::Protocols::GpuffProtocol::isPacketValid() - packet too small (" << len << " < " << packet_length << " + 4) **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

        return false;
    }

    bool crc_included = buf[2] & 0x80;

    std::vector<unsigned char> mutable_copy(buf, buf + len);

    if( crc_included && CheckCCITT16CRC(-1, &mutable_copy.front(), packet_length + 4) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - Cti::Protocols::GpuffProtocol::isPacketValid() - packet failed CRC check **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

        return false;
    }

    return true;
}


}
}
