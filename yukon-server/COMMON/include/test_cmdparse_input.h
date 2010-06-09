#pragma once

#include <string>

std::string inputString[] = {
"getvalue kwh",
"getvalue kwh select name \"Test device\"",
"getvalue kwh select name \" Test  multispace  device     \"",
"getvalue peak offset 3",
"control emetcon shed 1hr relay 1,2",
"control shed ?'NumMins'm",
"putconfig led yyy",
"putconfig service in",
"putconfig service out",
"control cycle terminate relay 3",
"control cycle terminate relay 2",
"control cycle terminate relay 1",
"control cycle 50 period 30 relay 1",
"control restore relay 3",
"control restore relay 2",
"control restore relay 1",
"control shed 5m relay 3",
"control shed 5m relay 2",
"control shed 5m relay 1",
"getvalue peak",
"putconfig emetcon timesync",
"getvalue voltage",
"getvalue demand",
"putconfig precanned table 11",
"putconfig precanned table 11 read interval 800",
"putconfig cold_load r1 10",
"putconfig template '?LoadGroup'",
"putconfig cycle r1 50",
"putconfig xcom service in",
"putconfig xcom service out temp offhours 24",
"putconfig xcom main 0x01 0x80",
"putconfig xcom main 0x01 0x40",
"putconfig xcom raw 0x05 0x00",
"putstatus xcom prop inc",
"putconfig xcom raw 0x30 0x00 0x02 0x58",
"putconfig emetcon ied class 0 1",
"putconfig emetcon ied class 0 0",
"getvalue ied demand",
"putconfig emetcon ied class 72 2",
"putconfig emetcon ied class 72 1",
"getstatus eventlog",
"scan integrity",
"scan general",
"loop locate",
"putconfig emetcon install",
"getconfig role 1",
"putconfig led yyy",
"putstatus reset r1 r2 r3 cl",
"putconfig service in",
"putconfig service out",
"control cycle terminate",
"control cycle 50 period 30 count 4",
"control restore",
"control shed 60m",
"control shed 30m",
"control shed 15m",
"control shed 7.m",
"putstatus reset",
"loop 5",
"loop",
"ping",
"putstatus ovuv enable",
"putstatus ovuv disable",
"control close",
"control open",
"scan integrity",
"control restore",
"control shed 5m",
"putvalue ied reset",
"getconfig ied scan",
"getconfig ied time",
"getvalue ied kwh",
"getvalue ied demand",
"getconfig time sync",
"getconfig time",
"getstatus LP",
"getstatus external",
"control connect",
"control disconnect",
"getstatus disconnect",
"putconfig emetcon interval ?LP/LI",
"getconfig interval ?LP/LI",
"putconfig emetcon multiplier kyz1 ?Multiplier(x.xxx)",
"getconfig mult kyz 1",
"getstatus internal",
"getvalue powerfail",
"putvalue kyz 1 reset",
"getconfig model",
"getvalue demand",
"getvalue kWh",
"getconfig intervals lp",
"getconfig model",
"putvalue analog 1 3.14159",
"putvalue kyz 1 1.234",
"putstatus reset",
"putstatus reset alarms",
"putstatus powerfail reset",
"putvalue powerfail reset",
"getconfig raw start=0x93",
"getconfig function raw start=0x90832 2",
"putconfig emetcon raw start=0x9823 0x83 0x39 0x32",
"getvalue lp peak interval channel 2 12/31/2003 31",
"putstatus xcom prop display",
"putstatus xcom prop inc",
"putstatus xcom prop term",
"putstatus xcom prop rssi",
"putstatus xcom prop test",
"putstatus xcom prop ping",
"putconfig xcom extended tier 2 rate 254 cmd 5 display 3 timeout 600 delay 5432",
"putconfig xcom display setup LCD",
"putconfig xcom display setup segment",
"putconfig xcom display 2 'ThisisMessage2'",
"putconfig xcom thermo config 34",
"putconfig xcom utility usage 1:past usage:34, 2:present usage:35, 3:past cost:36, 4:present cost:37.3",
"putconfig xcom utility info chan 1 usage cost cents 'Channel 1'",
"control xcom tcycle 12 period 2 count 35 ctrl temp 45 limit 34 afallback 4 max rate 23 bfallback 76 delay time 4",
"control xcom setpoint min 2 max 50 tr 25 ta 26 tb 27 dsb 4 tc 30 td 29 dsd -1 te 32 tf 35 dsf 11 stage 15 hold bump",
"putconfig xcom setstate run hold timeout 40 cooltemp 33 fan on system auto",
"putconfig xcom setstate run timeout 40 cooltemp 33 heattemp 43 fan auto system off",
"putconfig xcom setstate run timeout 40 heattemp 43 fan circulate system heat",
"putconfig xcom setstate run timeout 40 heattemp 43 fan circulate system cool",
"putconfig xcom setstate run timeout 40 heattemp 43 cooltemp 33 fan circulate system emheat",
"control xcom backlight cycles 23 duty 34 bperiod 45",
"putconfig xcom data 'Julie rocks!' port 2 deletable msgpriority 7 timeout 30 hour clear",
"putconfig xcom data 0x01 0x02 03 0x04 0x05 0xF1 0xFF 0xF3 configbyte 0x21",
"putconfig xcom data 0x01 0x02 0x03 0x04 0x05 0xF1 0xFF 0xF3 configbyte 21",
"putconfig xcom coldl r=10",
"putconfig xcom coldl r1=10",
"putconfig xcom coldl r1=10 r2=2h r14=10m",
"putconfig xcom lcrmode ExEmVG",
"putconfig xcom lcrmode ExVG",
"putconfig xcom gold 1",
"putconfig xcom silver 1",
"putconfig xcom gold 4",
"putconfig xcom silver 60",
"putconfig vcom lcrmode e",
"putconfig vcom lcrmode v",
"putconfig vcom gold 1",
"putconfig vcom silver 1",
"putconfig vcom gold 4",
"putconfig vcom silver 60",
"putconfig emetcon raw 0x30 34 77 serial 200148000",
"putconfig xcom command initiator 2 serial 200148000",
"putconfig xcom price tier 3 serial 200148000",
"putconfig xcom compare rssi serial 200148000",
"control xcom cpp 3600 min minheat 71 delta wake=+2 sleep =+4 return=-1 leave -2 serial 200148000",
"control xcom cpp 3600 minutes maxcool 71 wake=78 sleep =72 return=76 serial 200148000",
"control xcom cpp 3600 mins celsius maxcool 29 delta wake=+1 sleep =-2 leave +3 serial 200148000",
"putconfig emetcon phasedetect clear",
"putconfig emetcon phasedetect phase A delta=3 interval=30 num 4",
"getconfig phasedetect read",
"putconfig xcom target serial 1112345 assign geo 2",
"putconfig xcom target spid 1 geo 2 sub 3 feeder 4 zip 5 uda 6 program 7 splinter 8 assign spid 10 geo 12 sub 13 feeder 14 zip 15 uda 16 program 17 splinter 18 relay 3",
"putconfig emetcon alarm_mask tamper",
"putconfig emetcon alarm_mask1=0x03 alarm_mask2=0x04 alarm_mask_meter1=0x33 alarm_mask_meter2=0x44",
"putconfig emetcon alarm_mask power_fail under_voltage over_voltage pf_carryover rtc_adjusted holiday dst_change disconnect read_corrupted",
"putconfig emetcon alarm_mask tamper alarm_mask_meter1=0x11",
"putconfig emetcon alarm_mask configbyte=0x07 tamper alarm_mask_meter1=0x11",
"getvalue daily read detail channel 2 02/02/2000",
"getvalue daily read detail channel 3",
"getconfig centron",
"getconfig centron ratio",
"getconfig centron parameters",
"putconfig emetcon centron display 5x1 test 0s errors disable",
"putconfig emetcon centron ratio 40 display 5x1 test 0s errors disable",
"putconfig emetcon centron display 4x1 test 1s errors enable",
"putconfig emetcon centron ratio 60 display 4x1 test 1s errors enable",
"putconfig emetcon centron display 4x10 test 7s errors enable",
"putconfig emetcon centron ratio 200 display 4x10 test 7s errors enable",
"putconfig emetcon centron display 5x3 test 0s errors disable",
"putconfig emetcon centron ratio 40 display 5x3 test 0s errors disable",
"putconfig emetcon centron display 5x1 test 3s errors disable",
"putconfig emetcon centron ratio 40 display 5x1 test 3s errors disable",
"putconfig emetcon centron ratio 400 display 5x1 test 0s errors disable",
"putconfig emetcon centron display 5x1 test 0s",
"putconfig emetcon centron display 5x1 errors enable",
"putconfig emetcon centron test 0 errors enable",
"putconfig emetcon centron ratio 40",
"putconfig emetcon centron ratio 10 display 4x5 test 5 errors disable",
"putconfig emetcon centron ratio 290 display 5x1 test 7 errors disable",
"putconfig emetcon centron ratio 25 display 5x1 test 10 errors disable",
//  duplicates of the above Centron command strings until we remove "Centron" as an option
"getconfig meter",
"getconfig meter ratio",
"getconfig meter parameters",
"putconfig emetcon parameters display 5x1 test 0s errors disable",
"putconfig emetcon parameters ratio 40 display 5x1 test 0s errors disable",
"putconfig emetcon parameters display 4x1 test 1s errors enable",
"putconfig emetcon parameters ratio 60 display 4x1 test 1s errors enable",
"putconfig emetcon parameters display 4x10 test 7s errors enable",
"putconfig emetcon parameters ratio 200 display 4x10 test 7s errors enable",
"putconfig emetcon parameters display 5x3 test 0s errors disable",
"putconfig emetcon parameters ratio 40 display 5x3 test 0s errors disable",
"putconfig emetcon parameters display 5x1 test 3s errors disable",
"putconfig emetcon parameters ratio 40 display 5x1 test 3s errors disable",
"putconfig emetcon parameters ratio 400 display 5x1 test 0s errors disable",
"putconfig emetcon parameters display 5x1 test 0s",
"putconfig emetcon parameters display 5x1 errors enable",
"putconfig emetcon parameters test 0 errors enable",
"putconfig emetcon parameters ratio 40",
"putconfig emetcon parameters ratio 10 display 4x5 test 5 errors disable",
"putconfig emetcon parameters ratio 290 display 5x1 test 7 errors disable",
"putconfig emetcon parameters ratio 25 display 5x1 test 10 errors disable",
"putconfig emetcon parameters ratio 25 display 6x1 test 10 errors disable",
};

