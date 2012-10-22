#pragma once

/* Definitions for Device types */
#define TYPELMT2                             2
#define TYPEDCT501                           4
#define TYPE_REPEATER900                     5
#define TYPE_REPEATER800                     6
#define TYPE_REPEATER850                     7
#define TYPEPSEUDODEVICE                     8
#define TYPEMCT210                           10
#define TYPEMCT212                           11
#define TYPEMCT213                           12
#define TYPEMCT224                           20
#define TYPEMCT226                           21
#define TYPEMCT240                           30
#define TYPEMCT242                           31
#define TYPEMCT248                           32
#define TYPEMCT250                           40
#define TYPEMCT260                           50
#define TYPEMCT310                           60
#define TYPEMCT310ID                         64
#define TYPEMCT318                           68
#define TYPEMCT310IL                         69
#define TYPEMCT318L                          70
#define TYPEMCT310IDL                        71
#define TYPEMCT360                           75
#define TYPEMCT370                           80
#define TYPEMCT410CL                         82
#define TYPEMCT410FL                         83
#define TYPEMCT410GL                         84
#define TYPEMCT410IL                         85
#define TYPEMCT420CL                         86
#define TYPEMCT420CD                         87
#define TYPEMCT420FL                         88
#define TYPEMCT420FD                         89
#define TYPEMCT470                           90
#define TYPEMCT430A                          91
#define TYPEMCT430A3                         92
#define TYPEMCT430S4                         93
#define TYPEMCT430SL                         94
#define TYPEMCT440_2131B                     95
#define TYPEMCT440_2133B                     96

#define TYPELCR3102                          120

#define TYPELTC                              150

#define TYPECAPBANK                          220
#define TYPEVERSACOMCBC                      221
#define TYPEEXPRESSCOMCBC                    222
#define TYPEFISHERPCBC                       230
#define TYPECBC6510                          240
#define TYPECBC7010                          250
#define TYPECBC7020                          260
#define TYPECBCDNP                           261
#define TYPECBC8020                          270

#define TYPERFN430A3                         300
#define TYPERFN430ELO                        301
#define TYPERFN430KV                         302
#define TYPERFN410FL                         310
#define TYPERFN410FX                         311
#define TYPERFN410FD                         312
#define TYPERFN420ELO                        320

   /* Substation device identifiers */
#define TYPE_CCU700                          1000
#define TYPE_CCU710                          1001
#define TYPE_CCU711                          1003
#define TYPE_CCU721                          1010
#define TYPE_ILEXRTU                         1100
#define TYPE_WELCORTU                        1101
#define TYPE_SES92RTU                        1102
#define TYPE_DNPRTU                          1103
#define TYPE_DARTRTU                         1110
#define TYPE_ION7330                         1150
#define TYPE_ION7700                         1151
#define TYPE_ION8300                         1152
#define TYPE_LCU415                          1200
#define TYPE_LCU415LG                        1205
#define TYPE_LCU415ER                        1210
#define TYPE_LCUT3026                        1215
#define TYPE_TCU5000                         1250
#define TYPE_TCU5500                         1255
#define TYPE_TDMARKV                         1500
#define TYPE_DAVIS                           1600
#define TYPE_VTU                             1700
#define TYPE_ALPHA_PPLUS                     1800
#define TYPE_FULCRUM                         1805  // Schlumberger Fulcrum....
#define TYPE_LGS4                            1810  // Landis and Gyr S4....
#define TYPE_VECTRON                         1815  // Schlumberger Vectron...
#define TYPE_ALPHA_A1                        1820
#define TYPE_DR87                            1825
#define TYPE_QUANTUM                         1830  // Schlumberger Quantum
#define TYPE_KV2                             1835  // GE KV2
#define TYPE_SENTINEL                        1840  // Schlumberger Sentinel
#define TYPE_FOCUS                           1841  // Focus
#define TYPE_ALPHA_A3                        1845  // ABB Alpha A3

#define TYPE_SIXNET                          1850  // Sixnet VersaTrak/SiteTrak firmware > 7/1/01

#define TYPE_IPC_410FL                       1860
#define TYPE_IPC_420FD                       1861
#define TYPE_IPC_430S4E                      1862
#define TYPE_IPC_430SL                       1863

#define TYPE_TAPTERM                         1900
#define TYPE_WCTP                            1905
#define TYPE_RDS                             1906
#define TYPE_SNPP                            1907
#define TYPE_PAGING_RECEIVER                 1908
#define TYPE_TNPP                            1909

#define TYPE_RTC                             1910
#define TYPE_RTM                             1911
#define TYPE_FMU                             1912
#define TYPE_SERIESVRTU                      1915
#define TYPE_SERIESVLMIRTU                   1920
#define TYPE_MODBUS                          1930

#define TYPE_FCI                             1940
#define TYPE_NEUTRAL_MONITOR                 1941

#define TYPE_LMGROUP_EMETCON                 2000  // Group type devices...
#define TYPE_LMGROUP_VERSACOM                2001
#define TYPE_LMGROUP_RIPPLE                  2005
#define TYPE_LMGROUP_POINT                   2010
#define TYPE_LMGROUP_EXPRESSCOM              2015
#define TYPE_LMGROUP_RFN_EXPRESSCOM          2016
#define TYPE_LMGROUP_DIGI_SEP                2017
#define TYPE_LMGROUP_MCT                     2020

#define TYPE_LMGROUP_GOLAY                   2030
#define TYPE_LMGROUP_SADIGITAL               2031
#define TYPE_LMGROUP_SA105                   2032
#define TYPE_LMGROUP_SA205                   2033
#define TYPE_LMGROUP_SA305                   2034

#define TYPEMCTBCAST                         2050

#define TYPE_LMPROGRAM_DIRECT                2100  // LM category objects...
#define TYPE_LMPROGRAM_CURTAILMENT           2101
#define TYPE_LM_CONTROL_AREA                 2102
#define TYPE_LMPROGRAM_ENERGYEXCHANGE        2103

#define TYPE_CI_CUSTOMER                     2200  // Customer devices...

#define TYPE_CC_SUBSTATION_BUS               2300  // CC category objects...
#define TYPE_CC_FEEDER                       2301
#define TYPE_CC_AREA                         2302
#define TYPE_CC_SUBSTATION                   2303
#define TYPE_CC_SPECIALAREA                  2304
#define TYPE_CC_VOLTAGEREGULATOR             2305

// A macro device that can contain other devices - basically a generalized group of devices
#define TYPE_MACRO                           2900
// A system defined pseudo device. CGP 022300
#define TYPE_SYSTEM                          3000
#define TYPE_VIRTUAL_SYSTEM                  3001

// Port types occupy the range 6000 to 6100.  !!!!!! DO NOT USE !!!!!!


#define SYS_DID_SYSTEM        0     // A catchall for any op which needs an ID

