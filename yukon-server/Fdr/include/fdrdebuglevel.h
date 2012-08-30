#pragma once

#define STARTUP_FDR_DEBUGLEVEL           0x00000001
#define TRANSLATION_LOADING_DEBUGLEVEL   0x00000002

#define MIN_DETAIL_FDR_DEBUGLEVEL        0x00000010  // minimum details
#define DETAIL_FDR_DEBUGLEVEL            0x00000020  // show everything in minimum plus more
#define MAJOR_DETAIL_FDR_DEBUGLEVEL      0x00000040  // show everything in detail plus more
#define ERROR_FDR_DEBUGLEVEL             0x00000100
#define EXPECTED_ERR_FDR_DEBUGLEVEL      0x00000200
#define DATABASE_FDR_DEBUGLEVEL          0x00001000
#define CONNECTION_FDR_DEBUGLEVEL        0x00002000  // FDR - Yukon Related Logging
#define STARTUP_VERBOSE_FDR_DEBUGLEVEL   0x00004000

#define CONNECTION_HEALTH_DEBUGLEVEL       0x00010000 // Foreign system related logging
#define CONNECTION_INFORMATION_DEBUGLEVEL  0x00020000 // Foreign system related logging
#define DATA_SEND_DEBUGLEVEL               0x00100000
#define DATA_SEND_ERR_DEBUGLEVEL           0x00200000
#define DATA_RECV_DEBUGLEVEL               0x00400000
#define DATA_RECV_ERR_DEBUGLEVEL           0x00800000
