#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   Ars
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:40 $
 $Workfile: Ars.h $

  Notes:

 --------------------------------------------------------------------------
 Copyright Visual Systems Inc., All Rights Reserved.
 --------------------------------------------------------------------------

 See bottom of file for revision history.

*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __ARS_H
#define __ARS_H

#include <time.h>

#ifdef _MSC_VER
#pragma pack(push, __ARS__PACKING__)
#pragma pack(1)
#endif // _MSC_VER

#include "CygNetDefs.h"


#define ARS_VERSION  1

#define SVCDIR_TYPE  "ars     "

struct SERVICE_DEFINITION
{
    char           service_site[8];     /* name of site                    */
    char           service_name[8];     /* name of service                 */
    long           record_ver;          /* record version #                */
    char           service_type[8];     /* service type                    */
    unsigned short record_type;         /* 16 bit record type              */
    unsigned short service_status;      /* status of server                */
    unsigned char  service_addr[16];    /* server network address          */
    char           service_desc[32];    /* description of service          */
    char           service_stname[64];  /* StreetTalk name of service      */
    char           backup_site[8];      /* name of backup server site      */
    char           backup_name[8];      /* name of backup server service   */
    char           owner_site[8];       /* name of owner, site             */
    char           owner_name[8];       /* name of owner, service          */
    long           def_ver;             /* version of definition           */
};

struct SD_UUID
{                               // universally unique identifier for seat
    unsigned char uuid[16];     //  identifiers and license change
};                              //  transction identifiers.

/*****************/
/* RECORD TYPES */
/*****************/

#define SSERVER_RECORD 1


/***********************/
/* SERVER STATUS CODES */
/***********************/

#define SERVER_UNUSED  0
#define SERVER_DEFINED 1
#define SERVER_OOS     2
#define SERVER_FAILED  3
#define SERVER_OK      4


/****************************************\
   NETWORK REQUEST TYPE CODES (base 512)
\****************************************/

#define SD_ADDR_GET         ARS_MESSAGE_BASE + 0x00  /* get addresses */
#define SD_ALPHA_F_GET      ARS_MESSAGE_BASE + 0x02  /* get records alphabetically (fwd) */
#define SD_ALPHA_R_GET      ARS_MESSAGE_BASE + 0x03  /* get records alphabetically (rev) */
#define SD_LIST_GET         ARS_MESSAGE_BASE + 0x07  /* get status indexed */
#define SD_NAME_GET         ARS_MESSAGE_BASE + 0x08  /* get name of this svcdir */

#define SD_REGISTER_CLIENT  ARS_MESSAGE_BASE + 0x09  /* register client with ARS for metering */

#define SD_ADDR_SET         ARS_MESSAGE_BASE + 0x10  /* set service address */

#define SD_LICENSE_CHANGE   ARS_MESSAGE_BASE + 0x17  // process a license change transaction
#define SD_LICENSE_STATUS   ARS_MESSAGE_BASE + 0x1a  // get current license status


/*************************/
/* ERROR CODES (base 512)*/
/*************************/

#define SD_INVALID_REQUEST 1
#define SD_SECURITY_ERROR  ARS_ERROR_BASE + 0x01
#define SD_NAME_EXISTS     ARS_ERROR_BASE + 0x02
#define SD_NAME_NOT_EXIST  ARS_ERROR_BASE + 0x03
#define SD_INVALID_REC_VER ARS_ERROR_BASE + 0x04
#define SD_TOO_MANY_SEATS  ARS_ERROR_BASE + 0x05
#define SD_EXPIRE_WARNING  ARS_ERROR_BASE + 0x06

struct SD_ADDR_INFO
{
    char           service_site[8];     /* name of site                    */
    char           service_name[8];     /* name of service                 */
    long           record_ver;          /* record version #                */
    char           service_type[8];     /* service type                    */
    unsigned short record_type;         /* 16 bit record type              */
    unsigned short service_status;      /* status of server                */
    unsigned char  service_addr[16];    /* server network address          */
};

struct SERVICE_NAME
{
    char           service_site[8];     /* name of site                    */
    char           service_name[8];     /* name of service                 */
};

struct SD_SERVICE_STATUS
{
    char site[8];           /* site name (left justified, null filled) */
    char name[8];           /* service name (left justified, null filled) */
    char type[8];           /* service type (left justified, null filled) */
    char desc[32];          /* service desc (left justified, null filled) */
    unsigned short status;  /* service status as for 'DnaClientGetStatus' */
    unsigned char addr[16]; /* Banyan network address */
};

#define SD_CHANGE_SEAT_COUNT    0x0001      // update the authorized seat count
#define SD_CHANGE_EXPIRATION    0x0002      // update the license expiration date

#define SD_LICENSE_INFINITE     0xffffffff  // license never expires

struct SD_LICENSE_INFO                         // license change information
{
    char            user_id[64];        // user that invoked the change
    unsigned char   change_type;        // type of change (bit masked)
    int             seat_count;         // seat count increase(+) or decrease(-)
    unsigned long   expiration_days;    // expiration in days from the current time
};

struct SD_SEAT_INFO
{
    SD_UUID         seat_uuid;          // uuid of the seat
    char            seat_name[16];      // seat name
};

/*******************************/
/* NETWORK REQUEST DEFINITIONS */
/*******************************/

struct SD_ADDR_GET_REQ
{
    enum { eMAX_COUNT = 48 };
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    SERVICE_NAME   name[eMAX_COUNT];
};

struct SD_ADDR_GET_RESP
{
    enum { eMAX_COUNT = SD_ADDR_GET_REQ::eMAX_COUNT };
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    SD_ADDR_INFO   info[eMAX_COUNT];
};

/*---------------------------------------------------------------------------*/

struct SD_ALPHA_F_GET_REQ
{
    enum { eMAX_COUNT = 20 };
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    SERVICE_NAME   name;
    char           service_type[8];     /* service type                    */
};

struct SD_ALPHA_F_GET_RESP
{
    enum { eMAX_COUNT = SD_ALPHA_F_GET_REQ::eMAX_COUNT };
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    SERVICE_DEFINITION info[eMAX_COUNT];
};

/*---------------------------------------------------------------------------*/

struct SD_ALPHA_R_GET_REQ
{
    enum { eMAX_COUNT = 20 };
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    SERVICE_NAME   name;
    char           service_type[8];     /* service type                    */
};

struct SD_ALPHA_R_GET_RESP
{
    enum { eMAX_COUNT = SD_ALPHA_R_GET_REQ::eMAX_COUNT };
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    SERVICE_DEFINITION info[eMAX_COUNT];
};

/*---------------------------------------------------------------------------*/

struct SD_LIST_GET_REQ
{
    enum { eMAX_COUNT = 40 };
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    unsigned short index;
    char           service_type[8];     /* service type                    */
};

struct SD_LIST_GET_RESP
{
    enum { eMAX_COUNT = SD_LIST_GET_REQ::eMAX_COUNT };
    unsigned short    type;
    unsigned short    count;
    unsigned short    error;
    unsigned short    version;
    unsigned short    index;
    char              service_type[8];  /* service type                    */
    SD_SERVICE_STATUS info[eMAX_COUNT];
};

/*---------------------------------------------------------------------------*/

struct SD_NAME_GET_REQ
{
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
};

struct SD_NAME_GET_RESP
{
    unsigned short    type;
    unsigned short    count;
    unsigned short    error;
    unsigned short    version;
    SD_SERVICE_STATUS info;
};

/*---------------------------------------------------------------------------*/

struct SD_SET_ADDR_REQ
{
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    SERVICE_NAME   name;
    unsigned char  service_addr[16];
};

struct SD_SET_ADDR_RESP
{
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
};

/*---------------------------------------------------------------------------*/

struct SD_LICENSE_CHANGE_REQ
{
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    SD_LICENSE_INFO license_info;
};

struct SD_LICENSE_CHANGE_RESP
{
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
};

/*---------------------------------------------------------------------------*/

struct SD_REGISTER_CLIENT_REQ
{
    unsigned short usType;
    unsigned short usCount;
    unsigned short usError;
    unsigned short usVersion;           // = 1
    union {
        SD_SEAT_INFO    seat_info;      // message version 1
        unsigned char   ucId[64];       // message version 0 (obsolete)
    };
};

struct SD_REGISTER_CLIENT_RESP
{
    unsigned short usType;
    unsigned short usCount;
    unsigned short usError;
    unsigned short usVersion;
    unsigned short usSeatsOK;
    unsigned long  ulSeatsInUse;
    unsigned long  ulSeatsAuthorized;
    unsigned short usExpireWarning;
    char           cExpireDate[8];      // YYYYMMDD
};

/*---------------------------------------------------------------------------*/

struct SD_LICENSE_STATUS_REQ
{
    unsigned short usType;
    unsigned short usCount;
    unsigned short usError;
    unsigned short usVersion;
};

struct SD_LICENSE_STATUS_RESP
{
    unsigned short usType;
    unsigned short usCount;
    unsigned short usError;
    unsigned short usVersion;
    unsigned long  ulSeatsInUse;
    unsigned long  ulSeatsAuthorized;
    long           tARS;                // UTC current time
    long           tExpiration;         // UTC license expiration time
};

/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/* Formerly in arscfg.h
/*---------------------------------------------------------------------------*/


#define WKP_HOPS             9
#define N_SERVICE_ENTRIES 1536
#define REGPATH "SOFTWARE\\Visual Systems, Inc.\\CygNet\\Directory Services"


/****************************************\
  NETWORK REQUEST TYPE CODES (base 512)
\****************************************/

#define SD_RECORD_GET       ARS_MESSAGE_BASE + 0x01  /* get records */
#define SD_VERSION_GET      ARS_MESSAGE_BASE + 0x04  /* get version info alphabetically */
#define SD_WHO_ARE_YOU      ARS_MESSAGE_BASE + 0x05  /* get server's own definition */
#define SD_ARE_YOU_THERE    ARS_MESSAGE_BASE + 0x06  /* check for duplicate services */

#define SD_REC_UPDATE       ARS_MESSAGE_BASE + 0x12  /* update record */
#define SD_CHANGE_NOTICE    ARS_MESSAGE_BASE + 0x13  /* notification of change of record */
#define SD_AM_HERE_NOTICE   ARS_MESSAGE_BASE + 0x14  /* notification of existance */
#define SD_LIST             ARS_MESSAGE_BASE + 0x15  /* get block of entries */
#define SD_CONNECT_TO       ARS_MESSAGE_BASE + 0x16  // causes ARS to send I AM HERE to tcpip address

#define SD_LICENSE_GET      ARS_MESSAGE_BASE + 0x18  // synchronize the license  database
#define SD_SEAT_GET         ARS_MESSAGE_BASE + 0x19  // synchronize the metering database

/**********************************/
/* LICENSE TRANSACTION STRUCTURES */
/**********************************/

// license change transaction structure
//
struct SD_LICENSE_TRANSACTION
{
    char            user_id[64];        // user that invoked the transaction
    time_t          timestamp;          // transaction timestamp (UTC)
    unsigned char   change_type;        // type of change (bit masked)
    int             seat_count;         // seat count increase(+) or decrease(-)
    time_t          expiration;         // expiration time (UTC)
};

// search bit masks
//
#define SD_DIRECTION            0x0001  // direction mask
#define SD_FORWARD              0x0001  // forward  search direction
#define SD_BACKWARD             0x0000  // backward search direction

#define SD_INEQUALITY           0x0002  // inequality type mask
#define SD_STRICT               0x0002  // strict inequality
#define SD_NONSTRICT            0x0000  // non strict inequality

/********************/
/* OTHER STRUCTURES */
/********************/

struct SD_VERSION_INFO
{
    char           service_site[8];     /* name of site                    */
    char           service_name[8];     /* name of service                 */
    long           record_ver;          /* record version #                */
    long           def_ver;             /* definition version #            */
};

/***********************/
/* CHANGE NOTICE TYPES */
/***********************/

#define ADDR_UPDATE_NOTICE    1
#define ADD_RECORD_NOTICE     2
#define UPDATE_RECORD_NOTICE  3
#define DELETE_RECORD_NOTICE  4

#define UPDATE_SEAT_ID_NOTICE 5         // propagate seat id changes
#define UPDATE_LICENSE_NOTICE 6         // propagate license change transactions

/*******************************/
/* NETWORK REQUEST DEFINITIONS */
/*******************************/

struct SD_RECORD_GET_REQ
{
    enum { eMAX_COUNT = 20 };
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    unsigned long  req_id;
    SERVICE_NAME   name[eMAX_COUNT];
};

struct SD_RECORD_GET_RESP
{
    enum { eMAX_COUNT = SD_RECORD_GET_REQ::eMAX_COUNT };
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    unsigned long  req_id;
    SERVICE_DEFINITION info[eMAX_COUNT];
};

/*---------------------------------------------------------------------------*/

struct SD_LIST_REQ
{
    enum { eMAX_COUNT = 20 };
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    unsigned long  req_id;
    unsigned short index;
};

struct SD_LIST_RESP
{
    enum { eMAX_COUNT = SD_LIST_REQ::eMAX_COUNT };
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    unsigned long  req_id;
    SERVICE_DEFINITION info[eMAX_COUNT];
};

/*---------------------------------------------------------------------------*/

struct SD_VERSION_GET_REQ
{
    enum { eMAX_COUNT = 40 };
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    unsigned long  req_id;
    SERVICE_NAME   name;
};

struct SD_VERSION_GET_RESP
{
    enum { eMAX_COUNT = SD_VERSION_GET_REQ::eMAX_COUNT };
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    unsigned long  req_id;
    SD_VERSION_INFO info[eMAX_COUNT];
};

/*---------------------------------------------------------------------------*/

struct SD_WHO_ARE_YOU_REQ
{
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    unsigned long  req_id;
};

struct SD_WHO_ARE_YOU_RESP
{
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    unsigned long  req_id;
    SERVICE_DEFINITION info;
};

/*---------------------------------------------------------------------------*/

struct SD_ARE_YOU_THERE_REQ
{
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    unsigned long  req_id;
    SERVICE_NAME   name;
};

struct SD_ARE_YOU_THERE_RESP
{
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    unsigned long  req_id;
    SERVICE_DEFINITION info;
};

/*---------------------------------------------------------------------------*/

struct SD_REC_UPDATE_REQ
{
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    unsigned long  req_id;
    unsigned short notice_type;
    SERVICE_DEFINITION rec;
    char     user_id[64];
};

struct SD_REC_UPDATE_RESP
{
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    unsigned long  req_id;
};

/*---------------------------------------------------------------------------*/

#ifndef WIN32
    #define WORD unsigned short
    typedef struct _SYSTEMTIME
    {
        WORD wYear;
        WORD wMonth;
        WORD wDayOfWeek;
        WORD wDay;
        WORD wHour;
        WORD wMinute;
        WORD wSecond;
        WORD wMilliseconds;
    } SYSTEMTIME;
#endif


struct SD_CHANGE_NOTICE_REQ
{
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;                 // = 1
    unsigned short notice_type;             // change notice type
    SERVICE_NAME   name;                    // service definition that as been changed
    SERVICE_NAME   owner;                   // service definition owner ARS
    long           record_ver;              // service definition version timestamp
    unsigned char  addr[16];                // network address of the service

    union {
        SD_SEAT_INFO   seat_info;           // seat information
        unsigned char  seat_id[64];         // metered seat identifier (obsolete)
    };

    time_t         seat_last_update;        // metered seat last updated timestamp

    SYSTEMTIME     SystemTime;              // time master time update

    SD_UUID transaction_id;                 // license change transaction identifier
    SD_LICENSE_TRANSACTION transaction;     // license change transaction (encrypted)

};

struct SD_CHANGE_NOTICE_RESP
{
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
};

/*---------------------------------------------------------------------------*/

struct SD_CONNECT_TO_REQ
{
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    char           szAddress[16]; // 255.255.255.255
};

struct SD_CONNECT_TO_RESP
{
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
};

/*---------------------------------------------------------------------------*/

struct SD_LICENSE_GET_REQ
{
    enum { eMAX_COUNT = 40 };
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    unsigned char  match_type;              // SD_STRICT for strict inequality
    SD_UUID        transaction_id;          // license change transaction identifier
};

struct SD_LICENSE_GET_RESP
{
    enum { eMAX_COUNT = SD_LICENSE_GET_REQ::eMAX_COUNT };
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;

    struct
    {
        SD_UUID transaction_id;             // license change transaction identifier
        SD_LICENSE_TRANSACTION transaction; // license change transaction (encrypted)
    } transactions[eMAX_COUNT];

};

/*---------------------------------------------------------------------------*/

struct SD_SEAT_GET_REQ
{
    enum { eMAX_COUNT = 200 };
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;
    unsigned char  match_type;              // SD_STRICT for strict inequality
    SD_UUID        seat_id;                 // metered seat identifier
};

struct SD_SEAT_GET_RESP
{
    enum { eMAX_COUNT = SD_SEAT_GET_REQ::eMAX_COUNT };
    unsigned short type;
    unsigned short count;
    unsigned short error;
    unsigned short version;

    struct
    {
        SD_SEAT_INFO seat_info;             // metered seat identifier
        time_t       seat_last_update;      // metered seat last updated timestamp
    } seats[eMAX_COUNT];

};

/*---------------------------------------------------------------------------*/


#ifdef _MSC_VER
#pragma pack(pop, __ARS__PACKING__)
#endif  // _MSC_VER

/*************************************************************************\
 $History: Ars.h $
 *
 * *****************  Version 3  *****************
 * User: Rps1         Date: 10/06/99   Time: 10:12p
 * Updated in $/CygNet/Source/Support/CygNetCore
 * Modified message ids to use common service id base value.
 *
 * *****************  Version 2  *****************
 * User: Rps1         Date: 9/20/99    Time: 8:51p
 * Updated in $/CygNet/Source/Support/CygNetCore
 * Added include of time.h for time_t def.
 *
 * *****************  Version 1  *****************
 * User: Rps1         Date: 9/17/99    Time: 3:56p
 * Created in $/CygNet/Source/Support/CygNetCore
 * New core struct and message files.

\*************************************************************************/

#endif /* __ARS_H */
