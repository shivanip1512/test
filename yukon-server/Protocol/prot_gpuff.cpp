#include "precompiled.h"

#include "prot_gpuff.h"

#include "pt_base.h"
#include "cparms.h"

#include "logger.h"

#include <boost/cstdint.hpp>

using namespace std;
using boost::uint8_t;
using boost::uint16_t;
using boost::uint32_t;

namespace Cti       {
namespace Protocols {

GpuffProtocol::decoded_packet GpuffProtocol::decode( const unsigned char *p_data, const unsigned last_seq, const string device_name )
{
    decoded_packet result;

    pointlist_t &point_list   = result.points;

    unsigned  len;
    uint16_t &cid      = result.cid;
    uint16_t &seq      = result.seq;
    uint16_t &devt     = result.devt;
    uint8_t  &devr     = result.devr;
    uint32_t &ser      = result.ser;
    uint16_t  crc;

    bool  crc_included;
    bool &ack_required = result.ack_required;

    crc_included = p_data[2] & 0x80;
    ack_required = p_data[2] & 0x40;
    len  = ((p_data[2] & 0x03) << 8) | p_data[3];
    int pos = 4, usedbytes = 0, fcn;
    cid  = convertBytes(p_data, pos, 2); // (p_data[4] << 8) | p_data[5];
    seq  = convertBytes(p_data, pos, 2); // (p_data[6] << 8) | p_data[7];
    devt = convertBytes(p_data, pos, 2); // (p_data[8] << 8) | p_data[9];
    devr = convertBytes(p_data, pos, 1); // p_data[10];
    ser  = convertBytes(p_data, pos, 4); // (p_data[11] << 24) | (p_data[12] << 16) | (p_data[13] <<  8) | p_data[14];


    if( last_seq != seq )   // Is this a new message for this device?
    {
        int usedbytes = 0, fcn;
        pos = 15;

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

                            point_list.push_back(CTIDBG_new CtiPointDataMsg(GCVTx_Analog_Latitude, latitude, NormalQuality, AnalogPointType));
                            point_list.push_back(CTIDBG_new CtiPointDataMsg(GCVTx_Analog_Longitude, longitude, NormalQuality, AnalogPointType));

                            string device_name;

                            device_name.assign((const char *)p_data + pos, 128);
                            if( device_name.find('\0') != string::npos )
                            {
                                device_name.erase(device_name.find_first_of('\0'));
                            }

                            CTILOG_INFO(dout, "FCI device has reported in"<<
                                    endl <<" name: \""<< device_name <<"\""<<
                                    endl <<" latitude:  "<< latitude <<
                                    endl <<" longitude: "<< longitude
                                    );

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

                            pdm = CTIDBG_new CtiPointDataMsg(GCVTx_Status_Fault, fault, NormalQuality, StatusPointType);

                            if( msg_flags & 0x20 )
                            {
                                pdm->setTime(time);
                            }

                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GCVTx_Status_NoPower, noPower, NormalQuality, StatusPointType);

                            if( msg_flags & 0x20 )
                            {
                                pdm->setTime(time);
                            }

                            point_list.push_back(pdm);

                            if( msg_flags & 0x10 )
                            {
                                battery_voltage = convertBytes( p_data, pos, 2);
                                battery_voltage /= 1000.0;

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(GCVTx_Analog_BatteryVoltage, battery_voltage, NormalQuality, AnalogPointType));
                            }
                            if( msg_flags & 0x08 )
                            {
                                temperature = convertSignedBytes( p_data, pos, 2);
                                temperature /= 100.0;

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(GCVTx_Analog_Temperature, temperature, NormalQuality, AnalogPointType));
                            }
                            if( msg_flags & 0x04 )
                            {
                                amps_nominal = convertBytes( p_data, pos, 2);

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(GCVTx_Analog_NominalAmps, amps_nominal, NormalQuality, AnalogPointType));
                            }
                            if( msg_flags & 0x02 )
                            {
                                amps_peak = convertBytes( p_data, pos, 2);

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(GCVTx_Analog_PeakAmps, amps_peak, NormalQuality, AnalogPointType));
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
                                hostname.assign((const char *)p_data + pos, hostname_len);
                            }

                            CTILOG_INFO(dout, "device \""<< device_name <<"\" host config report - index"<< index <<" : "<< hostname <<":"<< port);

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


                            pdm = CTIDBG_new CtiPointDataMsg(GCVTx_Accum_MomentaryCount, momCount, NormalQuality, PulseAccumulatorPointType);

                            if( msg_flags & 0x20 )
                            {
                                pdm->setTime(time);
                            }

                            point_list.push_back(pdm);

                            // Toggle the momentary point true then false to allow an alarm to be recorded.
                            if( momCount > 0 )
                            {
                                pdm = CTIDBG_new CtiPointDataMsg(GCVTx_Status_Momentary, true, NormalQuality, StatusPointType);

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

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(GCVTx_Analog_NominalAmps, amps_nominal, NormalQuality, AnalogPointType));
                            }
                            if( msg_flags & 0x02 )
                            {
                                amps_peak = convertBytes( p_data, pos, 2);

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(GCVTx_Analog_PeakAmps, amps_peak, NormalQuality, AnalogPointType));
                            }

                            // TODO: Add duration here (msg_flags & 0x01)
                            if( msg_flags & 0x01 )
                            {
                                unsigned short duration = 0;
                                duration = convertBytes( p_data, pos, 2);

                                CTILOG_WARN(dout, "Duration decode unimplemented. Duration is: "<< duration);
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
                            bool reed_triggered = msg_status & 0x02;
                            bool momentary_triggered = msg_status & 0x01;

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

                            pdm = CTIDBG_new CtiPointDataMsg(GCVTx_Status_Fault, fault, NormalQuality, StatusPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GCVTx_Status_NoPower, noPower, NormalQuality, StatusPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GCVTx_Status_ReedSwitch, reed_triggered, NormalQuality, StatusPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            if( msg_flags & 0x10 )
                            {
                                battery_voltage = convertBytes( p_data, pos, 2);
                                battery_voltage /= 1000.0;

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(GCVTx_Analog_BatteryVoltage, battery_voltage, NormalQuality, AnalogPointType));
                            }

                            if( msg_flags & 0x08 )
                            {
                                temperature = convertSignedBytes( p_data, pos, 2);
                                temperature /= 100.0;

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(GCVTx_Analog_Temperature, temperature, NormalQuality, AnalogPointType));
                            }

                            if( msg_flags & 0x04 )
                            {
                                amps_nominal = convertBytes( p_data, pos, 2);

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(GCVTx_Analog_NominalAmps, amps_nominal, NormalQuality, AnalogPointType));
                            }

                            if( msg_flags & 0x02 )
                            {
                                amps_peak = convertBytes( p_data, pos, 2);

                                point_list.push_back(CTIDBG_new CtiPointDataMsg(GCVTx_Analog_PeakAmps, amps_peak, NormalQuality, AnalogPointType));
                            }

                            momentary_cnt = convertBytes( p_data, pos, 2);
                            pdm = CTIDBG_new CtiPointDataMsg(GCVTx_Accum_MomentaryCount, momentary_cnt, NormalQuality, PulseAccumulatorPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            // Toggle the momentary point true then false to allow an alarm to be recorded.
                            if( momentary_triggered && momentary_cnt > 0 )
                            {
                                pdm = CTIDBG_new CtiPointDataMsg(GCVTx_Status_Momentary, true, NormalQuality, StatusPointType);

                                if( msg_flags & 0x20 )
                                {
                                    pdm->setTime(time);
                                }

                                point_list.push_back((CtiPointDataMsg *)pdm->replicateMessage());
                                pdm->setMillis(10); // We pulse the false value "at time now + 1ms ".
                                pdm->setValue(false);
                                point_list.push_back(pdm);
                            }


                            th_high = convertBytes( p_data, pos, 2);
                            rpt_max = convertBytes( p_data, pos, 2);
                            rpt_min = convertBytes( p_data, pos, 2);


                            int time_offset = 3600; // Standard offset is 3600 seconds per record
                            double dmult = 1.0, dvalue;

                            if(ser == 60000006 ||
                               ser == 60000007 ||
                               ser == 11661166 ||
                               ser == 22772277 ||
                               ser == 33883388 ||
                               ser == 60000050 ||
                               (60000020 <= ser && ser <= 60000045))
                            {    // dTechs devices - this is horrible - 5 minute offsets.

                                // FIXME: why is this a log print and not a comment?
                                CTILOG_WARN(dout, "REMOVE dTechs code someday - dirty");

                                time_offset = 300;
                                dmult = 0.01;
                            }

                            time -= rec_num * time_offset;

                            for(int i = 0; i < rec_num; i++)
                            {
                                time += time_offset;
                                amps = convertBytes( p_data, pos, 2);

                                pdm = CTIDBG_new CtiPointDataMsg(GCVTx_Analog_CurrentSurvey, amps, NormalQuality, AnalogPointType);
                                pdm->setTime(time);
                                point_list.push_back(pdm);
                            }

                            if(msg_flags & 0x01)
                            {
                                // REC[E] included
                                amps = convertBytes( p_data, pos, 2);
                                pdm = CTIDBG_new CtiPointDataMsg(GCVTx_Analog_CurrentSurvey, amps, NormalQuality, AnalogPointType);
                                pdm->setTime(time);
                                point_list.push_back(pdm);
                            }

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
                                unsigned char ie_type = pConfig[cfg_pos++];   // Information Element Type
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

                                        break;
                                    }
                                case 0x04:
                                    {
                                        // Serial Number Information Device to Server
                                        unsigned int cooper_sn_h = convertBytes(pConfig, cfg_pos, 4);
                                        unsigned int cooper_sn_l = convertBytes(pConfig, cfg_pos, 4);

                                        string rsn = convertBytesToString(pConfig, cfg_pos, next_ie_pos - cfg_pos);
                                        break;
                                    }
                                case 0x05:
                                case 0x85:
                                case 0x06:
                                    {
                                        // Diagnostic GCVT.
                                        if(ie_type != 0x06)
                                        {
                                            for( int i = 0; i < 32; i++ )  // Pop off the TraceQ.
                                            {
                                                rsvd = convertBytes(pConfig, cfg_pos, 1);
                                                stg = "eDbg " + CtiNumStr(rsvd);
                                                rsvd = convertBytes(pConfig, cfg_pos, 1);
                                                // add_to_csv_summary(keys, values, stg, rsvd);
                                            }

                                            int guardbyte0       =  convertBytes(pConfig, cfg_pos, 1);
                                            int guardbyte1       =  convertBytes(pConfig, cfg_pos, 1);
                                        }

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

                                        if(devr >= 4 && cfg_pos < next_ie_pos)
                                        {
                                            abfw_major = convertBytes(pConfig, cfg_pos, 1);
                                            abfw_minor = convertBytes(pConfig, cfg_pos, 1);
                                            rssi       = convertBytes(pConfig, cfg_pos, 1);
                                            ber        = convertBytes(pConfig, cfg_pos, 1);
                                            rs_cnt     = convertBytes(pConfig, cfg_pos, 1);

                                            int bars = 0;

                                            if(rssi > 14) bars = 4;
                                            else if(9 < rssi && rssi <= 14) bars = 3;
                                            else if(5 < rssi && rssi <= 9) bars = 2;
                                            else if(0 < rssi && rssi <= 5) bars = 1;

                                            point_list.push_back(CTIDBG_new CtiPointDataMsg(GCVTx_Status_RSSI, bars, NormalQuality, StatusPointType));
                                            point_list.push_back(CTIDBG_new CtiPointDataMsg(GCVTx_Analog_RSSI, rssi, NormalQuality, AnalogPointType));
                                            point_list.push_back(CTIDBG_new CtiPointDataMsg(GCVTx_Analog_BER, ber, NormalQuality, AnalogPointType));
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

                                        break;
                                    }
                                default:
                                    {
                                        CTILOG_WARN(dout, "new/unknown ie_type "<< CtiNumStr((int)ie_type).hex().zpad(2) <<", total config len "<< (int)total_len <<", IE Position "<< (int)(cfg_pos-2));

                                        if(cfg_pos + ie_len < total_len)
                                        {
                                            // Process through the ie in the for loop - HOP OVER IT.
                                            std::ostringstream rawBytes;
                                            rawBytes << hex << setfill('0');

                                            for(int nbr = 0; nbr < ie_len; ++nbr)
                                            {
                                                rawBytes << setw(2) << convertBytes(pConfig, cfg_pos, 1) <<" ";
                                            }

                                            CTILOG_INFO(dout, "ie_type = "<< CtiNumStr((int)ie_type).hex().zpad(2) <<", ie_len = "<< (int)ie_len <<": "<< rawBytes);
                                        }
                                        else
                                        {
                                            cfg_pos = total_len;
                                        }
                                    }
                                }

                                if(cfg_pos < next_ie_pos)
                                {
                                    std::ostringstream rawBytes;
                                    rawBytes << hex << setfill('0');

                                    while(cfg_pos < next_ie_pos)
                                    {
                                        rawBytes << setw(2) << convertBytes(pConfig, cfg_pos, 1) <<" ";
                                    }

                                    CTILOG_INFO(dout, "ie_type = "<< CtiNumStr((int)ie_type).hex().zpad(2) <<", ie_len = "<< (int)ie_len <<": had extra bytes: "<< rawBytes);
                                }
                            }

                            pos += total_len;   // hop past the config.


                            break;
                        }
                    default:
                        {
                            // We just consumed a GPUFF FCN we do not recognize.  All beds are off for the remainder of this message.
                            CTILOG_ERROR(dout, "Unknown GPUFF FCN "<< fcn <<" - message processing aborted");

                            pos = len;
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

                            device_name.assign((const char *)p_data + pos, 128);
                            if( device_name.find('\0') != string::npos )
                            {
                                device_name.erase(device_name.find_first_of('\0'));
                            }

                            CTILOG_INFO(dout, "Neutral Monitor device has reported in"<<
                                    endl <<" name:          \""<< device_name <<"\""<<
                                    endl <<" latitude:      "<< latitude <<
                                    endl <<" longitude:     "<< longitude <<
                                    endl <<" amp threshold: "<< amp_threshold
                                    );

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

                            pdm = CTIDBG_new CtiPointDataMsg(GVARx_Analog_BatteryVoltage, battery_voltage, NormalQuality, AnalogPointType, "", TAG_POINT_MUST_ARCHIVE);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GVARx_Analog_CurrentSurvey, 0.0, NormalQuality, AnalogPointType, "", TAG_POINT_MUST_ARCHIVE);

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

                            unsigned long emaxtime, emintime;
                            CtiTime time;
                            int interval_cnt = 0, sample_rate = 0, rate, ts_max, ts_min;
                            float battery_voltage = 0.0, temperature = 0.0, min_reading, max_reading;

                            hasTime = flags & 0x10 ? true : false;
                            calibrated = flags & 0x02 ? true : false;
                            reed_triggered = flags & 0x01 ? true : false;

                            if( hasTime )
                            {
                                unsigned long tmpTime = convertBytes( p_data, pos, 4);

                                if( flags & 0x80 )
                                {
                                    time -= tmpTime;
                                }
                                else
                                {
                                    time  = tmpTime;
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

                            pdm = CTIDBG_new CtiPointDataMsg(GVARx_Status_Calibrated, calibrated, NormalQuality, StatusPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GVARx_Status_OverCurrent, over, NormalQuality, StatusPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GVARx_Status_ReedSwitch, reed_triggered, NormalQuality, StatusPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GVARx_Status_ResetOverCurrent, reset, NormalQuality, StatusPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);



                            pdm = CTIDBG_new CtiPointDataMsg(GVARx_Analog_MaxCurrentReport, report_max, NormalQuality, AnalogPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GVARx_Analog_MinCurrentReport, report_min, NormalQuality, AnalogPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GVARx_Analog_BatteryVoltage, battery_voltage, NormalQuality, AnalogPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GVARx_Analog_Temperature, temperature, NormalQuality, AnalogPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GVARx_Analog_Period, rate, NormalQuality, AnalogPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GVARx_Analog_HighAlertSetpoint, threshold_hi, NormalQuality, AnalogPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            pdm = CTIDBG_new CtiPointDataMsg(GVARx_Analog_ResetAlertSetpoint, threshold_lo, NormalQuality, AnalogPointType);
                            pdm->setTime(time);
                            point_list.push_back(pdm);

                            // get the timestamp ready
                            CtiTime measurement_time = time - rate * rec_num;

                            for( int i = 0; i < rec_num; i++ )
                            {
                                measurement_time += rate;
                                max_reading = convertBytes( p_data, pos, 2) / 10.0;
                                min_reading = convertBytes( p_data, pos, 2) / 10.0;
                                ts_max = convertBytes( p_data, pos, 1) & 0x3f;
                                ts_min = convertBytes( p_data, pos, 1) & 0x3f;

                                pdm = CTIDBG_new CtiPointDataMsg(GVARx_Analog_MaxCurrentPeriod, max_reading, NormalQuality, AnalogPointType);
                                pdm->setTime(measurement_time - ((interval_cnt - ts_max - 1) * sample_rate));
                                point_list.push_back(pdm);

                                pdm = CTIDBG_new CtiPointDataMsg(GVARx_Analog_MinCurrentPeriod, min_reading, NormalQuality, AnalogPointType);
                                pdm->setTime(measurement_time - ((interval_cnt - ts_min - 1) * sample_rate));
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
                                unsigned long emaxtime = 0, emintime = 0;

                                if(reed_triggered)
                                {
                                    emaxtime = emintime = CtiTime::now().seconds(); // Time of arrival!
                                }
                                else if(reset)
                                {
                                    emintime = measurement_time.seconds() + (ts_min + 1) * sample_rate;
                                    if(hasTime)
                                        emaxtime = measurement_time.seconds() + (ts_max + 1) * sample_rate;
                                    else
                                        emaxtime = 0;    // Invalid
                                }
                                else if(over)
                                {
                                    emaxtime = measurement_time.seconds() + (ts_max + 1) * sample_rate;
                                    if(hasTime)
                                        emintime = measurement_time.seconds() + (ts_min + 1) * sample_rate;
                                    else
                                        emintime = 0;   // Invalid
                                }

                                if( emaxtime )
                                {
                                    pdm = CTIDBG_new CtiPointDataMsg(GVARx_Analog_MaxCurrentPeriod, max_reading, NormalQuality, AnalogPointType);
                                    pdm->setTime(CtiTime(emaxtime));
                                    point_list.push_back(pdm);
                                }

                                if( emintime )
                                {
                                    pdm = CTIDBG_new CtiPointDataMsg(GVARx_Analog_MinCurrentPeriod, min_reading, NormalQuality, AnalogPointType);
                                    pdm->setTime(CtiTime(emintime));
                                    point_list.push_back(pdm);
                                }
                            }

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

                                        break;
                                    }
                                case 0x03:
                                    {
                                        // Serial Number Information Device to Server
                                        unsigned int cooper_sn_h = convertBytes(pConfig, cfg_pos, 4);
                                        unsigned int cooper_sn_l = convertBytes(pConfig, cfg_pos, 4);
                                        string radio_sn  = convertBytesToString(pConfig, cfg_pos, 16);

                                        break;
                                    }
                                case 0x04:
                                    {
                                        // Serial Number Information Device to Server
                                        unsigned int cooper_sn_h = convertBytes(pConfig, cfg_pos, 4);
                                        unsigned int cooper_sn_l = convertBytes(pConfig, cfg_pos, 4);
                                        string radio_sn  = convertBytesToString(pConfig, cfg_pos, 24);

                                        break;
                                    }
                                case 0x11:
                                    {
                                        // GVAR(x) configuration parameters.
                                        int report_freq_days = convertBytes(pConfig, cfg_pos, 1);
                                        rsvd = convertBytes(pConfig, cfg_pos, 1);
                                        int over_current_threshold = convertBytes(pConfig, cfg_pos, 2);
                                        int reset_threshold = convertBytes(pConfig, cfg_pos, 2);

                                        break;
                                    }
                                case 0x05:
                                case 0x85:
                                case 0x06:
                                    {
                                        // Diagnostic.
                                        if(ie_type != 0x06)
                                        {
                                            for( int i = 0; i < 32; i++ )  // Pop off the TraceQ.
                                            {
                                                rsvd = convertBytes(pConfig, cfg_pos, 1);
                                                rsvd = convertBytes(pConfig, cfg_pos, 1);
                                            }

                                            int guardbyte0       =  convertBytes(pConfig, cfg_pos, 1);
                                            int guardbyte1       =  convertBytes(pConfig, cfg_pos, 1);
                                        }

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

                                        pdm = CTIDBG_new CtiPointDataMsg(GVARx_Analog_RSSI, rssi, NormalQuality, AnalogPointType);
                                        point_list.push_back(pdm);

                                        int bars = 0;

                                        if(rssi > 14) bars = 4;
                                        else if(9 < rssi && rssi <= 14) bars = 3;
                                        else if(5 < rssi && rssi <= 9) bars = 2;
                                        else if(0 < rssi && rssi <= 5) bars = 1;

                                        pdm = CTIDBG_new CtiPointDataMsg(GVARx_Status_RSSI, bars, NormalQuality, StatusPointType);
                                        point_list.push_back(pdm);

                                        pdm = CTIDBG_new CtiPointDataMsg(GVARx_Analog_BER, ber, NormalQuality, AnalogPointType);
                                        point_list.push_back(pdm);


                                        break;
                                    }
                                case 0x81:
                                    {
                                        // APN Server to Device Information Element
                                        CTILOG_INFO(dout, "APN Server to Device Information Element");

                                        break;
                                    }
                                default:
                                    {
                                        CTILOG_WARN(dout, "new/unknown ie_type "<< CtiNumStr((int)ie_type).hex().zpad(2) <<", total config len "<< (int)total_len <<", IE Position "<< (int)(cfg_pos-2));

                                        if(cfg_pos + ie_len < total_len)
                                        {
                                            std::ostringstream rawBytes;
                                            rawBytes << hex << setfill('0');

                                            for(int nbr = 0; nbr < ie_len; ++nbr)
                                            {
                                                rawBytes << setw(2) << convertBytes(pConfig, cfg_pos, 1) <<" ";
                                            }

                                            CTILOG_INFO(dout, "ie_type = "<< CtiNumStr((int)ie_type).hex().zpad(2) <<", ie_len = "<< (int)ie_len <<": "<< rawBytes);
                                        }
                                        else
                                        {
                                            cfg_pos = total_len;
                                        }
                                    }
                                }

                                if(cfg_pos < next_ie_pos)
                                {
                                    std::ostringstream rawBytes;
                                    rawBytes << hex << setfill('0');

                                    while(cfg_pos < next_ie_pos)
                                    {
                                        rawBytes << setw(2) << convertBytes(pConfig, cfg_pos, 1) <<" ";
                                    }

                                    CTILOG_INFO(dout, "ie_type = "<< CtiNumStr((int)ie_type).hex().zpad(2) <<", ie_len = "<< (int)ie_len <<": had extra bytes: "<< rawBytes);
                                }
                            }

                            pos += total_len;   // hop past the config.


                            break;
                        }
                    default:
                        {
                            // We just consumed a GPUFF FCN we do not recognize.  All beds are off for the remainder of this message.
                            CTILOG_ERROR(dout, "Unknown GPUFF FCN "<< fcn <<" - message processing aborted");

                            pos = len;
                        }
                    }
                }

                break;
            }

        default:
            {
                CTILOG_ERROR(dout, "Unknown GPUFF device type "<< devt);
            }
        }
    }

    return result;
}


void GpuffProtocol::describeFrame(unsigned char *p_data, int p_len, int len, bool crc_included, bool ack_required, int devt, int ser)
{
    string keys, values;

    unsigned cid, seq, devr, crc;

    int pos = 4, usedbytes = 0, fcn;

    add_to_csv_summary(keys, values, "CRC included", crc_included);
    add_to_csv_summary(keys, values, "ACK required", ack_required);

    add_to_csv_summary(keys, values, "length", len);

    cid  = convertBytes(p_data, pos, 2);
    seq  = convertBytes(p_data, pos, 2);
    devt = convertBytes(p_data, pos, 2);
    devr = convertBytes(p_data, pos, 1);
    ser  = convertBytes(p_data, pos, 4);

    add_to_csv_summary(keys, values, "CID",  cid);
    add_to_csv_summary(keys, values, "SEQ",  seq);
    add_to_csv_summary(keys, values, "DEVT", devt);
    add_to_csv_summary(keys, values, "DEVR", devr);
    add_to_csv_summary(keys, values, "SER",  ser);


    CTILOG_INFO(dout,
            endl <<" LEN:  "<< len <<
            endl <<" CRC?: "<< crc_included <<
            endl <<" ACK?: "<< ack_required <<
            endl <<" CID:  "<< cid <<
            endl <<" SEQ:  "<< seq <<
            endl <<" DEVT: "<< devt <<
            endl <<" DEVR: "<< devr <<
            endl <<" SER:  "<< ser
            );

    pos = 15;

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

                        CTILOG_INFO(dout, "FCI device has reported in"<<
                                endl <<" name:           \""<< device_name <<"\""<<
                                endl <<" latitude:       "<< latitude <<
                                endl <<" longitude:      "<< longitude <<
                                endl <<" request ack:    "<< request_ack <<
                                endl <<" udp repeats:    "<< udp_repeats <<
                                endl <<" phase:          "<< phase <<
                                endl <<" current survey: "<< current_survey
                                );

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

                        CTILOG_INFO(dout, "FCI device values"<<
                                endl <<" fault:    "<< fault <<
                                endl <<" event:    "<< event <<
                                endl <<" no power: "<< noPower
                                );

                        add_to_csv_summary(keys, values, "includes time", items_included & 0x20);
                        add_to_csv_summary(keys, values, "TSO",           items_included & 0x80);
                        add_to_csv_summary(keys, values, "GMT",         !(items_included & 0x80));

                        if( items_included & 0x20 )
                        {
                            time = convertBytes( p_data, pos, 4);

                            if( items_included & 0x80 )
                            {
                                CTILOG_INFO(dout, "tso value: "<< time);

                                time = CtiTime::now().seconds() - time;
                            }

                            CTILOG_INFO(dout, "time: "<< CtiTime(time));

                            add_to_csv_summary(keys, values, "time", CtiTime(time).asString());
                        }

                        add_to_csv_summary(keys, values, "includes battery voltage", items_included & 0x10);

                        if( items_included & 0x10 )
                        {
                            battery_voltage = convertBytes( p_data, pos, 2);

                            battery_voltage /= 1000.0;

                            CTILOG_INFO(dout, "battery voltage: "<< battery_voltage);
                        }

                        add_to_csv_summary(keys, values, "includes temperature", items_included & 0x08);

                        if( items_included & 0x08 )
                        {
                            temperature = convertSignedBytes( p_data, pos, 2);

                            temperature /= 100.0;

                            CTILOG_INFO(dout, "temperature: "<< temperature);

                            add_to_csv_summary(keys, values, "temperature", temperature);
                        }

                        add_to_csv_summary(keys, values, "includes nominal amps", items_included & 0x04);

                        if( items_included & 0x04 )
                        {
                            amps_nominal = convertBytes( p_data, pos, 2);

                            CTILOG_INFO(dout, "amps nominal: "<< amps_nominal);

                            add_to_csv_summary(keys, values, "nominal amps", amps_nominal);
                        }

                        add_to_csv_summary(keys, values, "includes peak amps", items_included & 0x02);

                        if( items_included & 0x02 )
                        {
                            amps_peak = convertBytes( p_data, pos, 2);

                            CTILOG_INFO(dout, "amps peak: "<< amps_peak);

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
                                CTILOG_INFO(dout, "tso value: "<< time);

                                time = CtiTime::now().seconds() - time;
                            }

                            CTILOG_INFO(dout, "time: "<< CtiTime(time));

                            add_to_csv_summary(keys, values, "time", CtiTime(time).asString());
                        }

                        unsigned short momCount = 0;

                        momCount  = convertBytes( p_data, pos, 2);

                        add_to_csv_summary(keys, values, "momentary count", momCount);

                        CTILOG_INFO(dout,  "momentary count: "<< momCount);

                        add_to_csv_summary(keys, values, "includes nominal amps", items_included & 0x04);

                        if( items_included & 0x04 )
                        {
                            amps_nominal = convertBytes( p_data, pos, 2);

                            CTILOG_INFO(dout, "amps nominal: "<< amps_nominal);

                            add_to_csv_summary(keys, values, "nominal amps", amps_nominal);
                        }

                        add_to_csv_summary(keys, values, "includes peak amps", items_included & 0x02);

                        if( items_included & 0x02 )
                        {
                            amps_peak = convertBytes( p_data, pos, 2);

                            CTILOG_INFO(dout, "amps peak: "<< amps_peak);

                            add_to_csv_summary(keys, values, "nominal amps", amps_peak);
                        }

                        // TODO: Add duration here (items_included & 0x01)
                        if( items_included & 0x01 )
                        {
                            unsigned short duration = 0;
                            duration = convertBytes( p_data, pos, 2);

                            CTILOG_WARN(dout, "Duration decode unimplemented. Duration is: "<< duration);
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
                        bool reed_triggered = msg_status & 0x02;
                        bool momentary_triggered = msg_status & 0x01;

                        add_to_csv_summary(keys, values, "fault", fault);
                        add_to_csv_summary(keys, values, "event", event);
                        add_to_csv_summary(keys, values, "no power", noPower);
                        add_to_csv_summary(keys, values, "calibrated", calibrated);
                        add_to_csv_summary(keys, values, "charge ckt enabled", charge_enabled);
                        add_to_csv_summary(keys, values, "high current detect", high_current);
                        add_to_csv_summary(keys, values, "reed triggered", reed_triggered);
                        add_to_csv_summary(keys, values, "momentary triggered", momentary_triggered);

                        unsigned long time = CtiTime::now().seconds();
                        unsigned int momentary_cnt = 0, th_high, rpt_max, rpt_min, amps;
                        float battery_voltage, temperature, amps_nominal, amps_peak;

                        CTILOG_INFO(dout, "FCI device status/values with current profile"<<
                                endl <<" fault:               "<< fault <<
                                endl <<" event:               "<< event <<
                                endl <<" no power:            "<< noPower <<
                                endl <<" calibrated:          "<< calibrated <<
                                endl <<" charge ckt enabled:  "<< charge_enabled <<
                                endl <<" high current detect: "<< high_current <<
                                endl <<" reed triggered:      "<< reed_triggered <<
                                endl <<" momentary triggered: "<< momentary_triggered
                                );

                        add_to_csv_summary(keys, values, "includes time", msg_flags & 0x20);
                        add_to_csv_summary(keys, values, "TSO",           msg_flags & 0x80);
                        add_to_csv_summary(keys, values, "GMT",         !(msg_flags & 0x80));

                        if( msg_flags & 0x20 )
                        {
                            time = convertBytes( p_data, pos, 4);

                            if( msg_flags & 0x80 )
                            {
                                CTILOG_INFO(dout, "tso value: "<< time);

                                time = CtiTime::now().seconds() - time;
                            }

                            CTILOG_INFO(dout, "time: "<< CtiTime(time));

                            add_to_csv_summary(keys, values, "time", CtiTime(time).asString());
                        }

                        add_to_csv_summary(keys, values, "includes battery voltage", msg_flags & 0x10);

                        if( msg_flags & 0x10 )
                        {
                            battery_voltage = convertBytes( p_data, pos, 2);

                            battery_voltage /= 1000.0;

                            CTILOG_INFO(dout, "battery voltage: "<< battery_voltage);
                        }

                        add_to_csv_summary(keys, values, "includes temperature", msg_flags & 0x08);

                        if( msg_flags & 0x08 )
                        {
                            temperature = convertSignedBytes( p_data, pos, 2);

                            temperature /= 100.0;

                            CTILOG_INFO(dout, "temperature: "<< temperature);

                            add_to_csv_summary(keys, values, "temperature", temperature);
                        }

                        add_to_csv_summary(keys, values, "includes nominal amps", msg_flags & 0x04);

                        if( msg_flags & 0x04 )
                        {
                            amps_nominal = convertBytes( p_data, pos, 2);

                            CTILOG_INFO(dout, "amps nominal: "<< amps_nominal);

                            add_to_csv_summary(keys, values, "nominal amps", amps_nominal);
                        }

                        add_to_csv_summary(keys, values, "includes peak amps", msg_flags & 0x02);

                        if( msg_flags & 0x02 )
                        {
                            amps_peak = convertBytes( p_data, pos, 2);

                            CTILOG_INFO(dout, "amps peak: "<< amps_peak);

                            add_to_csv_summary(keys, values, "peak amps", amps_peak);
                        }

                        momentary_cnt = convertBytes( p_data, pos, 2);
                        add_to_csv_summary(keys, values, "momentary count", momentary_cnt);

                        CTILOG_INFO(dout, "momentary count: "<< momentary_cnt);

                        th_high = convertBytes( p_data, pos, 2);
                        rpt_max = convertBytes( p_data, pos, 2);
                        rpt_min = convertBytes( p_data, pos, 2);

                        add_to_csv_summary(keys, values, "threshold high", th_high);
                        add_to_csv_summary(keys, values, "report max amps", rpt_max);
                        add_to_csv_summary(keys, values, "report min amps", rpt_min);

                        CTILOG_INFO(dout,
                                endl <<" threshold high:  "<< th_high <<
                                endl <<" report min amps: "<< rpt_min <<
                                endl <<" report avg amps: "<< ((msg_flags & 0x04) ? boost::lexical_cast<string>(amps_nominal) : string("N/A")) <<
                                endl <<" report max amps: "<< rpt_max
                                );


                        int time_offset = 3600; // Standard offset is 3600 seconds per record
                        double dmult = 1.0, dvalue;

                        if(ser == 60000006 || ser == 60000007 || ser == 11661166 || ser == 22772277 || ser == 33883388 || ser == 60000050 || (60000020 <= ser && ser <= 60000045))
                        {   // dTechs devices - this is horrible - 5 minute offsets.
                            CTILOG_WARN(dout, "REMOVE dTechs code someday - dirty");

                            time_offset = 300;
                            dmult = 0.01;
                        }

                        time -= rec_num * time_offset;

                        for(int i = 0; i < rec_num; i++)
                        {
                            time += time_offset;
                            amps = convertBytes( p_data, pos, 2);
                            dvalue = amps * dmult;

                            CTILOG_INFO(dout, "time: "<< CtiTime(time) <<", record["<< i <<"]: "<< dvalue);
                        }

                        if(msg_flags & 0x01)
                        {
                            // REC[E] included
                            amps = convertBytes( p_data, pos, 2);
                            dvalue = amps * dmult;

                            CTILOG_INFO(dout, "record[event]: "<< dvalue)
                        }

                        if(event)
                        {
                            if(reed_triggered)
                            {
                                CTILOG_INFO(dout, "REED SWITCH TRIGGERED MESSAGE");
                            }
                            else if(fault)
                            {
                                CTILOG_INFO(dout, "PERMANENT FAULT MESSAGE");
                            }
                            else if(high_current)
                            {
                                CTILOG_INFO(dout, "HIGH CURRENT THRESHOLD EXCEEDED MESSAGE");
                            }
                            else if(momentary_triggered)
                            {
                                CTILOG_INFO(dout, "MOMENTARY TRIGGERED EVENT");
                            }
                            else if(noPower)
                            {
                                CTILOG_INFO(dout, "NO POWER(CURRENT BELOW DETECT) MESSAGE");
                            }
                            else
                            {
                                CTILOG_INFO(dout, "POWER RESTORE (CURRENT ABOVE DETECT) MESSAGE");
                            }
                        }
                        else
                        {
                            CTILOG_INFO(dout, "SUPERVISORY MESSAGE");
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

                                    CTILOG_INFO(dout,
                                            endl <<" APN:                  "<< apn <<
                                            endl <<" periodic cfg reports: "<< periodic
                                            );

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

                                    CTILOG_INFO(dout,
                                            endl <<" URL:     "<< url <<
                                            endl <<" IP Port: "<< port
                                            );

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

                                    CTILOG_INFO(dout,
                                            endl <<" cooper serial number h/l: "<< cooper_sn_h <<" / "<< cooper_sn_l <<
                                            endl <<" radio serial number:      "<< hex << showbase << uppercase << setfill('0') <<
                                            setw(2) << radio_sn_3 <<" / "<<
                                            setw(2) << radio_sn_2 <<" / "<<
                                            setw(2) << radio_sn_1 <<" / "<<
                                            setw(2) << radio_sn_0
                                            );

                                    break;
                                }
                            case 0x04:
                                {
                                    // Serial Number Information Device to Server
                                    unsigned int cooper_sn_h = convertBytes(pConfig, cfg_pos, 4);
                                    unsigned int cooper_sn_l = convertBytes(pConfig, cfg_pos, 4);

                                    string rsn = convertBytesToString(pConfig, cfg_pos, next_ie_pos - cfg_pos);

                                    add_to_csv_summary(keys, values, "cooper serial high", cooper_sn_h);
                                    add_to_csv_summary(keys, values, "cooper serial low ", cooper_sn_l);

                                    add_to_csv_summary(keys, values, "radio serial", rsn);

                                    CTILOG_INFO(dout,
                                            endl <<" cooper serial number h/l: "<< cooper_sn_h <<" / "<< cooper_sn_l <<
                                            endl <<" radio serial number:      "<< rsn
                                            );

                                    break;
                                }
                            case 0x05:
                            case 0x85:
                            case 0x06:
                                {
                                    // Diagnostic GCVT.

                                    if(ie_type != 0x06)
                                    {
                                        for( int i = 0; i < 32; i++ )  // Pop off the TraceQ.
                                        {
                                            rsvd = convertBytes(pConfig, cfg_pos, 1);
                                            stg = "eDbg " + CtiNumStr(rsvd);
                                            rsvd = convertBytes(pConfig, cfg_pos, 1);
                                            // add_to_csv_summary(keys, values, stg, rsvd);
                                        }


                                        int guardbyte0       =  convertBytes(pConfig, cfg_pos, 1);
                                        int guardbyte1       =  convertBytes(pConfig, cfg_pos, 1);
                                    }
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

                                    CTILOG_INFO(dout,
                                            endl <<" firmware major:   "<< firmware_major <<
                                            endl <<" firmware minor:   "<< firmware_minor <<
                                            endl <<" reset_count:      "<< reset_count <<
                                            endl <<" spi_errors:       "<< SPI_errors <<
                                            endl <<" momentary_count:  "<< momentary_count <<
                                            endl <<" fault_count:      "<< fault_count <<
                                            endl <<" all_clear_count:  "<< all_clear_count <<
                                            endl <<" power_loss_count: "<< power_loss_count <<
                                            endl <<" reset_momentary:  "<< reset_momentary <<
                                            endl <<" revert count:     "<< revert_cnt
                                            );

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

                                        CTILOG_INFO(dout,
                                                endl <<" analog firmware major: "<< abfw_major <<
                                                endl <<" analog firmware minor: "<< abfw_minor <<
                                                endl <<" rssi:                  "<< rssi <<
                                                endl <<" ber:                   "<< ber <<
                                                endl <<" reed switch count:     "<< rs_cnt
                                                );
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

                                    CTILOG_INFO(dout,
                                            endl <<" GCVT report period:     "<< report_period_hours <<
                                            endl <<" high current threshold: "<< high_current_threshold
                                            );

                                    break;
                                }
                            default:
                                {
                                    CTILOG_WARN(dout, "new/unknown ie_type "<< CtiNumStr((int)ie_type).hex().zpad(2) <<", total config len "<< (int)total_len <<", IE Position "<< (int)(cfg_pos-2));

                                    if(cfg_pos + ie_len < total_len)
                                    {
                                        std::ostringstream rawBytes;
                                        rawBytes << hex << setfill('0');

                                        for(int nbr = 0; nbr < ie_len; ++nbr)
                                        {
                                            rawBytes << setw(2) << convertBytes(pConfig, cfg_pos, 1) <<" ";
                                        }

                                        CTILOG_INFO(dout, "ie_type = "<< CtiNumStr((int)ie_type).hex().zpad(2) <<", ie_len = "<< (int)ie_len <<": "<< rawBytes);
                                    }
                                    else
                                    {
                                        cfg_pos = total_len;
                                    }
                                }
                            }

                            if(cfg_pos < next_ie_pos)
                            {
                                std::ostringstream rawBytes;
                                rawBytes << hex << setfill('0');

                                while(cfg_pos < next_ie_pos)
                                {
                                    rawBytes << setw(2) << convertBytes(pConfig, cfg_pos, 1) <<" ";
                                }

                                CTILOG_INFO(dout, "ie_type = "<< CtiNumStr((int)ie_type).hex().zpad(2) <<", ie_len = "<< (int)ie_len <<": had extra bytes: "<< rawBytes);
                            }
                        }

                        pos += total_len;   // hop past the config.


                        break;
                    }
                default:
                    {
                        // We just consumed a GPUFF FCN we do not recognize.  All beds are off for the remainder of this message.
                        CTILOG_ERROR(dout, "Unknown GPUFF FCN "<< fcn <<" - message processing aborted");

                        pos = len;
                        break;
                    }
                }
            }

            if( gConfigParms.getValueAsInt("GCVTx_CSV_SUMMARY") )
            {
                string rawBytes = "\"";

                for( int xx = 0; xx < p_len; xx++ )
                {
                    if( xx == 0 ) rawBytes += CtiNumStr(p_data[xx]).hex().zpad(2).toString();
                    else rawBytes += " " + CtiNumStr(p_data[xx]).hex().zpad(2).toString();
                }
                rawBytes += "\"";
                add_to_csv_summary(keys, values, "raw data", rawBytes);
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

                        CTILOG_INFO(dout, "Neutral Monitor device has reported in"<<
                                endl <<" name: \""<< device_name <<"\""<<
                                endl <<" latitude:      "<< latitude <<
                                endl <<" longitude:     "<< longitude <<
                                endl <<" amp threshold: "<< amp_threshold
                                );

                        break;
                    }
                case 0x01:
                    {
                        unsigned char msg_flags = p_data[pos++];

                        unsigned long time = 0;
                        float battery_voltage, temperature, amps;

                        CTILOG_INFO(dout, "GVAR(X): Device Values 0x01");

                        if( msg_flags & 0x20 )
                        {
                            time = convertBytes( p_data, pos, 4);

                            if( msg_flags & 0x80 )
                            {
                                time = CtiTime::now().seconds() - time;
                            }

                            CTILOG_INFO(dout, "time: "<< CtiTime(time));
                        }
                        if( msg_flags & 0x10 )
                        {
                            battery_voltage = convertBytes( p_data, pos, 2);
                            battery_voltage /= 1000.0;

                            CTILOG_INFO(dout, "battery: "<< battery_voltage);
                        }
                        if( msg_flags & 0x08 )
                        {
                            temperature = convertSignedBytes( p_data, pos, 2);
                            temperature /= 100.0;

                            CTILOG_INFO(dout, "temperature: "<< temperature);
                        }
                        if( msg_flags & 0x08 )
                        {
                            amps = convertBytes( p_data, pos, 2);

                            CTILOG_INFO(dout," amps: "<< amps);
                        }

                        break;
                    }
                case 0x02:
                    {
                        unsigned char flags = p_data[pos++];

                        unsigned long time = 0;
                        int rate, count, reading;
                        float battery_voltage;

                        CTILOG_INFO(dout, "GVAR(X): Neutral Current History Report 0x02");

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

                        CTILOG_INFO(dout,
                                endl <<" time:    "<< CtiTime(time) <<
                                endl <<" battery: "<< battery_voltage <<
                                endl <<" rate:    "<< rate <<
                                endl <<" count:   "<< count
                                );

                        //  get the timestamp ready
                        time -= rate * count;

                        for( int i = 0; i < count; i++ )
                        {
                            time += rate;
                            reading = p_data[pos + (i * 2)] << 8 | p_data[pos + (i * 2) + 1];

                            CTILOG_INFO(dout, "time: "<< CtiTime(time) <<", amps: "<< reading);
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

                        CTILOG_INFO(dout, "GVAR(X): Neutral Current History Report 0x03");

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

                        CTILOG_INFO(dout,
                                endl <<" time:           "<< CtiTime(time) <<
                                endl <<" battery:        "<< battery_voltage <<
                                endl <<" temperature:    "<< temperature <<
                                endl <<" record rate:    "<< rate <<
                                endl <<" calibrated:     "<< calibrated <<
                                endl <<" reed trigger:   "<< reed_triggered <<
                                endl <<" high threshold: "<< threshold_hi <<
                                endl <<" low  threshold: "<< threshold_lo <<
                                endl <<" report max:     "<< report_max <<
                                endl <<" report min:     "<< report_min <<
                                endl <<" record count:   "<< rec_num <<
                                endl <<" report period:  "<< report_period <<
                                endl <<" over alarm:     "<< over <<
                                endl <<" reset alarm:    "<< reset
                                );

                        // get the timestamp ready
                        time -= rate * rec_num;

                        for( int i = 0; i < rec_num; i++ )
                        {
                            time += rate;
                            max_reading = convertBytes( p_data, pos, 2) / 10.0;
                            min_reading = convertBytes( p_data, pos, 2) / 10.0;
                            ts_max = convertBytes( p_data, pos, 1) & 0x3f;
                            ts_min = convertBytes( p_data, pos, 1) & 0x3f;

                            CTILOG_INFO(dout, "interval time: "<< CtiTime(time) <<
                                    ", min time: "<< CtiTime(time - ((interval_cnt - ts_min - 1) * sample_rate)) <<", min amps: "<< min_reading <<
                                    ", max time: "<< CtiTime(time - ((interval_cnt - ts_max - 1) * sample_rate)) <<", max amps: "<< max_reading <<
                                    ", ts_min/max: "<< ts_min <<"/"<< ts_max
                                    );
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

                            CTILOG_INFO(dout, "event triggered record reed/over/reset: "<< reed_triggered <<"/"<< over <<"/"<< reset <<
                                    endl <<" min time:      "<< CtiTime(emintime) <<
                                    endl <<", min amps:     "<< min_reading <<
                                    endl <<", max time:     "<< CtiTime(emaxtime) <<
                                    endl <<", max amps:     "<< max_reading <<
                                    endl <<", ts_min / max: "<< ts_min <<" / "<< ts_max
                                    );
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

                                    CTILOG_INFO(dout,
                                            endl <<" APN:                  "<< apn <<
                                            endl <<" periodic cfg reports: "<< periodic
                                            );

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

                                    CTILOG_INFO(dout,
                                            endl <<" URL:     "<< url <<
                                            endl <<" IP Port: "<< port
                                            );

                                    break;
                                }
                            case 0x03:
                                {
                                    // Serial Number Information Device to Server
                                    unsigned int cooper_sn_h = convertBytes(pConfig, cfg_pos, 4);
                                    unsigned int cooper_sn_l = convertBytes(pConfig, cfg_pos, 4);
                                    string radio_sn  = convertBytesToString(pConfig, cfg_pos, 16);

                                    add_to_csv_summary(keys, values, "cooper serial high", cooper_sn_h);
                                    add_to_csv_summary(keys, values, "cooper serial low ", cooper_sn_l);
                                    add_to_csv_summary(keys, values, "radio serial", radio_sn);

                                    CTILOG_INFO(dout,
                                            endl <<" cooper serial number h/l: "<< cooper_sn_h <<" / "<< cooper_sn_l <<
                                            endl <<" radio serial number:      "<< radio_sn
                                            );

                                    break;
                                }
                            case 0x04:
                                {
                                    // Serial Number Information Device to Server
                                    unsigned int cooper_sn_h = convertBytes(pConfig, cfg_pos, 4);
                                    unsigned int cooper_sn_l = convertBytes(pConfig, cfg_pos, 4);

                                    string rsn = convertBytesToString(pConfig, cfg_pos, next_ie_pos - cfg_pos);

                                    add_to_csv_summary(keys, values, "cooper serial high", cooper_sn_h);
                                    add_to_csv_summary(keys, values, "cooper serial low ", cooper_sn_l);

                                    add_to_csv_summary(keys, values, "radio serial", rsn);

                                    CTILOG_INFO(dout,
                                            endl <<" cooper serial number h/l: "<< cooper_sn_h <<" / "<< cooper_sn_l <<
                                            endl <<" radio serial number:      "<< rsn
                                            );

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

                                    CTILOG_INFO(dout,
                                            endl <<" GVAR report frequency:   "<< report_freq_days <<
                                            endl <<" over current threshold:  "<< over_current_threshold <<
                                            endl <<" reset current threshold: "<< reset_threshold
                                            );

                                    break;
                                }
                            case 0x05:
                            case 0x85:
                            case 0x06:
                                {
                                    // Diagnostic GVAR.

                                    if(ie_type != 0x06)
                                    {
                                        for( int i = 0; i < 32; i++ )  // Pop off the TraceQ.
                                        {
                                            rsvd = convertBytes(pConfig, cfg_pos, 1);
                                            stg = "eDbg " + CtiNumStr(rsvd);
                                            rsvd = convertBytes(pConfig, cfg_pos, 1);
                                            // add_to_csv_summary(keys, values, stg, rsvd);
                                        }


                                        int guardbyte0       =  convertBytes(pConfig, cfg_pos, 1);
                                        int guardbyte1       =  convertBytes(pConfig, cfg_pos, 1);
                                    }

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

                                    add_to_csv_summary(keys, values, "firmware major",          firmware_major  );
                                    add_to_csv_summary(keys, values, "firmware minor",          firmware_minor  );
                                    add_to_csv_summary(keys, values, "reset count",             reset_count     );
                                    add_to_csv_summary(keys, values, "SPI errors",              SPI_errors      );
                                    add_to_csv_summary(keys, values, "firmware major a-bd",     abfw_major );
                                    add_to_csv_summary(keys, values, "firmware minor a-bd",     abfw_minor     );
                                    add_to_csv_summary(keys, values, "rssi",                    rssi );
                                    add_to_csv_summary(keys, values, "ber",                     ber);
                                    add_to_csv_summary(keys, values, "revert count", revert_cnt );

                                    CTILOG_INFO(dout,
                                            endl <<" firmware major:        "<< firmware_major <<
                                            endl <<" firmware minor:        "<< firmware_minor <<
                                            endl <<" reset_count:           "<< reset_count <<
                                            endl <<" spi_errors:            "<< SPI_errors <<
                                            endl <<" analog firmware major: "<< abfw_major <<
                                            endl <<" analog firmware minor: "<< abfw_minor <<
                                            endl <<" rssi:                  "<< rssi <<
                                            endl <<" ber:                   "<< ber <<
                                            endl <<" revert count:          "<< revert_cnt
                                            );

                                    break;
                                }
                            case 0x81:
                                {
                                    // APN Server to Device Information Element
                                    CTILOG_INFO(dout, "APN Server to Device Information Element");

                                    break;
                                }
                            default:
                                {
                                    CTILOG_WARN(dout, "new/unknown ie_type "<< CtiNumStr((int)ie_type).hex().zpad(2) <<", total config len "<< (int)total_len <<", IE Position "<< (int)(cfg_pos-2));

                                    if(cfg_pos + ie_len < total_len)
                                    {
                                        std::ostringstream rawBytes;
                                        rawBytes << hex << setfill('0');

                                        for(int nbr = 0; nbr < ie_len; ++nbr)
                                        {
                                            rawBytes << setw(2) << convertBytes(pConfig, cfg_pos, 1) <<" ";
                                        }

                                        CTILOG_INFO(dout, "ie_type = "<< CtiNumStr((int)ie_type).hex().zpad(2) <<", ie_len = "<< (int)ie_len <<": "<< rawBytes);
                                    }
                                    else
                                    {
                                        cfg_pos = total_len;
                                    }
                                }
                            }

                            if(cfg_pos < next_ie_pos)
                            {
                                std::ostringstream rawBytes;
                                rawBytes << hex << setfill('0');

                                while(cfg_pos < next_ie_pos)
                                {
                                    rawBytes << setw(2) << convertBytes(pConfig, cfg_pos, 1) <<" ";
                                }

                                CTILOG_INFO(dout, "ie_type = "<< CtiNumStr((int)ie_type).hex().zpad(2) <<", ie_len = "<< (int)ie_len <<": had extra bytes: "<< rawBytes);
                            }
                        }

                        pos += total_len;   // hop past the config.


                        break;
                    }
                default:
                    {
                        // We just consumed a GPUFF FCN we do not recognize.  All beds are off for the remainder of this message.
                        CTILOG_ERROR(dout, "Unknown GPUFF FCN "<< fcn <<" - message processing aborted");

                        pos = len;
                        break;
                    }
                }
            }

            break;
        }

    default:
        {
            CTILOG_ERROR(dout, "Unknown GPUFF device type "<< devt);
        }
    }
}


template<typename T>
void GpuffProtocol::serialize(vector<unsigned char> &dst, const T src )
{
    size_t pos = sizeof(T);

    const unsigned char *src_ptr = reinterpret_cast<const unsigned char *>(&src);

    while( pos )
    {
        // MSB conversion
        dst.push_back(src_ptr[--pos]);
    }
}


vector<unsigned char> GpuffProtocol::generateAck(decoded_packet p)
{
    vector<unsigned char> result;

    const uint16_t strt = 0xa596;

    const uint16_t flg_len = 0x840d;  //  CRC included, ACK bit set, 13 byte message (17 minus STRT and FLG/LEN)

    serialize(result, strt);

    serialize(result, flg_len);

    serialize(result, p.cid);

    serialize(result, p.seq);

    serialize(result, p.devt);

    serialize(result, p.devr);

    serialize(result, p.ser);

    const uint16_t crc = CCITT16CRC(0, &result.front(), result.size(), false);

    serialize(result, crc);

    return result;
}


string GpuffProtocol::convertBytesToString( const unsigned char *buf, int &position, int bytes_to_combine )
{
    string str;
    str.assign((const char *)buf + position, bytes_to_combine);
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
        CTILOG_ERROR(dout, "packet too small ("<< len <<" < "<< packet_length <<" + 4)");

        return false;
    }

    bool crc_included = buf[2] & 0x80;

    std::vector<unsigned char> mutable_copy(buf, buf + len);

    if( crc_included && CheckCCITT16CRC(-1, &mutable_copy.front(), packet_length + 4) )
    {
        CTILOG_ERROR(dout, "packet failed CRC check");

        return false;
    }

    return true;
}


}
}
