#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   Dcl
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:00 $
*
* Copyright 1997 Visual Systems, Inc.
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __DCL__
#define __DCL__

#ifndef _DOS
#ifndef OS2
    // disable the level 4 warnings in windows.h
    #pragma warning ( disable: 4201 4514 )
    #include <windows.h>
#endif
#endif

#include <ars.h>

#ifdef OS2
    #define DCLMEMTYPE
    #define WORD unsigned short
    #define DWORD unsigned long
    #define far
#endif

#ifdef __cplusplus
    extern "C" {
#endif

#ifndef DCLMEMTYPE

#ifndef WIN32
    #define DCLMEMTYPE far
#else
    #define DCLMEMTYPE
#endif // DCLMEMTYPE

#endif // DCLMEMTYPE

#ifndef DCLFUNTYPE

    // Define _WINDLL when working with DCLWD.DLL or DCLND.DLL
    #ifdef _WINDLL
        #define DCLFUNTYPE WINAPI
    #else
        #define DCLFUNTYPE
    #endif // _WINDLL

#endif // DCLFUNTYPE

/********************************************************************/

/* GLOBALLY VISIBLE DATA */

extern unsigned short DnaClientWellKnownPort;
extern int            DnaClientWKPHops;

/********************************************************************/

/* DCL function return codes */
/* NOTE: error codes >0 are network errors */

#define DNACLIENT_OK         0  /* OK return code */
#define DNACLIENT_UNINITED  -1  /* DnaClientInit hasn't been called */
#define DNACLIENT_MEMORY    -2  /* Memory allocation error */
#define DNACLIENT_NLOGIN    -3  /* Workstation not logged in */
#define DNACLIENT_NONAME    -5  /* Service Name not found in the ARS */
//#define DNACLIENT_NAMES     -6  /* OBSOLETE */
#define DNACLIENT_FULL      -7  /* Cannot add Service name to DCL tables */
#define DNACLIENT_VERROR    -8  /* Response(s) received but validation failed */
#define DNACLIENT_OOS       -9  /* Service is shut down */
#define DNACLIENT_FAILED    -10 /* Service is failed */
#define DNACLIENT_NDIR      -11 /* Service Name not it ARS */
#define DNACLIENT_DIRF      -12 /* Can't contact ARS */
//#define DNACLIENT_BUSY      -13 /* OBSOLETE */
#define DNACLIENT_NOANSWER  -14 /* Message sent no answer received */
#define DNACLIENT_FILEERR   -15 /* File open error */
#define DNACLIENT_MUTEX_ERR -16 /* Failed to create mutex */
#define DCL_ILLEGAL_CMPRS_TYPE -17 // Invalid compression type set by DclSetCmprsParms
#define DCL_ILLEGAL_ENCRYPT_TYPE -18 // Invalid encryption type set by DclSetCmprsParms

// CygNet timeout and retry defaults
#define DCL_DEFAULT_TIMEOUT 10 // seconds
#define DCL_DEFAULT_RETRIES  5 // receive retries after a verify error

/********************************************************************/
/*  DCL Startup and Shutdown functions                              */
/********************************************************************/

// This function retrieves ARS Well Known Port info
unsigned short DCLFUNTYPE GetVsiPort(void);

/*  This function must be called before you perform any Connect or
    Call operations.

    The 'svcdir_wkp' parameter is used by the client routines to get
    an address for an ARS to then get service addresses.

    Return Codes: DNACLIENT_OK
                  DNACLIENT_MEMORY
                  DNACLIENT_NLOGIN

*/
int DCLFUNTYPE DnaClientInit(unsigned short svcdir_wkp);

/*  This function closes any open sockets and cleans up in
    preperation for program termination.

    Return Codes: Always returns DNACLIENT_OK.
*/
int DCLFUNTYPE DnaClientStop(void);

/*  This function returns the current logged in user id.  The
    area pointed to by 'user_id' must be at least 65 bytes long.

    Return Codes: DNACLIENT_OK     if got name ok.
                  DNACLIENT_NLOGIN if workstation not logged in.
*/
int DCLFUNTYPE DnaClientGetUserid(char  *user_id);

// This function allows the APP programmer to modify message timeout and
// retry parameters
int DCLFUNTYPE DclSetTimeoutParms( short timeout, short retries );

// This function allows the APP programmer to modify the default network compression and encryption options
// 32 bit only. The types are defined in CMPRS.H
int DCLFUNTYPE DclSetCmprsParms( WORD wCmprsType, WORD wEncprytType );

// This function gets the a string suitable for setting the TZ environment
// variable.
int DCLFUNTYPE VSIGetTZ(char DCLMEMTYPE *szTimeZone);

/*  These functions create entries in a table internal to the client
    code that will be used for communicating with and reconnecting
    to the service in case of communications errors with the service.

    These functions do not actually perform any connect with the
    specified service. They simply attempt to get the network address
    of the service.  The return code from these routines can only
    tell you if getting that address was successful or not.  The
    first time you will know if that address is valid and the
    service is responding correctly is on your first Call to that
    service.

    The 'service_name' parameter is used for both the obvious
    purpose while obtaining the address of the service from
    the specified directory service and for reference in other
    calls to these library routines.  For unknown possible
    future inhancements, this name will be treated by the
    client routines as case sensitive.

    The 'service_type' parameter in the DnaClientNativeDSConnect
    function is for TCP/IP network use.  TCP/IP domain name services
    allow only machine name lookup.  The port within that machine
    that is supporting the desired service is not available through
    the the domain name service.  This parameter is used in TCP/IP
    network services to get a port number for a server.  For other
    networks, like Banyan Vines that have directory services that
    support machine and port addressing, this parameter is ignored.

    The 'DnaClientSvcDirName' function allows you ask the CygNet name
    of the ARS that you are currently using. 'name' must point
    to a character array at least 18 chars long.

    Note: It is not an error to make connect calls for the same
    service more than once as long the same exact parmeters are used
    each time the connect is done.

    Return Codes: DNACLIENT_OK
                  DNACLIENT_UNINITED  DnaClientInit hasn't been called
                  DNACLIENT_NLOGIN    Workstation not logged in
                  DNACLIENT_MAMES     Service name duplication
                  DNACLIENT_FULL      Service name table is full
                  DNACLIENT_OOS       Service is shut down
                  DNACLIENT_FAILED    Service is failed
                  DNACLIENT_NDIR      Name not it directory service
                  DNACLIENT_DIRF      Can't contact directory service

    The DnaClientDisconnect routine allows you to remove an entry
    from the internal table.

    Return Codes: DNACLIENT_OK
                  DNACLIENT_UNINITED  DnaClientInit hasn't been called
                  DNACLIENT_NOMAME    Service name not found
*/
int DCLFUNTYPE DnaClientSvcDirConnect( const char  *service_name );
int DCLFUNTYPE DnaClientDisconnect( const char  *service_name );
int DCLFUNTYPE DnaClientSvcDirName( char  *name );
int DCLFUNTYPE DnaClientNativeDSConnect( const char *service_name,
    const char *service_type );

// The SetARSAddr accepts a TCPIP address and attempts to communicate with an ARS at that
// address. If successful, that ARS becomes the "connected" ARS and it's name is returned
// in the szARSName parameter. Return of 0 => success.
int DCLFUNTYPE SetARSAddr( const char *szAddress, char *szARSName );

/********************************************************************/

/* SERVER CALL REQUEST BLOCK STRUCTURE */

typedef struct {

    /* STATUS FLAGS AND INFO FILLED IN BY CLIENT ROUTINES */

    short error_code;           /* operation error code (0 if ok) */
    short error_operation;      /* what was going on when error was detected */
    short rcv_len;              /* how many bytes were actually received */

    /* RESPONSE VERIFICATION INFO */

    short verify_flags;         /* bit mask of verify types */
    unsigned short req_type;    /* used when verifying first 2 bytes of resp */
    unsigned long req_id;       /* used when verifying bytes 8..11 of resp */

    /* SERVICE NAME AND TIMEOUT */

    char service_name[64];      /* name of service to direct request to */
    short timeout;              /* operation timeout in seconds */

    /* DATA BUFFER INFO (send_buffer and rcv_buffer may point to same memory) */

    void  *send_buffer;  /* pointer to data to send */
    short send_len;         /* how many bytes to send (max 4096) */

    void  *rcv_buffer;   /* pointer to receive buffer */
    short rcv_buffer_len;   /* sizeof rcv_buffer (max 4096) */

    /* CLIENT INTERNAL DATA */

    char reserved[32];

} DNACRB;

/* RESPONSE VERIFICATION FLAG INFO */

#define DNACLIENT_VERIFY_ADDR     1 /* verify rcv address is correct */
#define DNACLIENT_VERIFY_REQ_TYPE 2 /* verify bytes 0 and 1 of response match
                                       the value you placed in the 'req_type'
                                       parameter */
#define DNACLIENT_VERIFY_REQ_ID   4 /* verify bytes 8 thru 11 of response
                                       match the value you placed in the
                                       'req_id' parameter */
// DclCall is the "work horse" message sender. It is not as detailed
// as the following DnaClientCall but is simpler to use.
int DCLFUNTYPE DclCall( const char *service_name, void *send_buffer,
    unsigned short send_buffer_len, void *rcv_buffer,
    unsigned short rcv_buffer_len, unsigned short  *actual_rcv_len );

/* DclICall is same as DclCall except it uses DNACLIENT_VERFIY_REQ_ID.
   only a handful of CygNet messages are designed for this call. Care
   must be taken to insure this is used with a message with the req_id
   as the 9th and 10th bytes of the REQ and RESP structures
*/
int DCLFUNTYPE DclICall( const char      *service_name,
             void            *send_buffer,
             unsigned short      send_buffer_len,
             void            *rcv_buffer,
             unsigned short      rcv_buffer_len,
             unsigned short  *actual_rcv_len
               );

/*  This function provides the actual request/response call to a
    service.  The code that implements this function will attempt
    to insure that the client socket is cleared of pending receives
    before sending the request.

    If verify flags are set in the request structure, the response
    will be checked and if validation fails, new receives will be issued.
    This validation capability is provided to allow developers to
    design to minimize the probability that the client/server
    request/response cycle could get out of sequence in case of major
    network distrubances.  An error code (DNACLIENT_OP_VERIFY) is
    is provided to indicate to the calling routine that something was
    was received after the request was sent but that validation failed.
    'DNACLIENT_OP_VERIFY' will be returned only if there was a timeout
    (Vines error 163) on receive and at least one transmission was
    received from somewhere.

    The 'error_code' and 'error_operation' fields will be set to
    indicate the type of error incountered.  'error_operation' is
    included to allow determination of where in the fairly complex
    request/response code the error was detected (see defines below
    for 'error_operation' values).

    Return Codes:

        DNACLIENT_OK            OK return code
        DNACLIENT_UNINITED      DnaClientInit hasn't been called
        DNACLIENT_NLOGIN        Workstation not logged in
        DNACLIENT_NONAME        Name not found in table
        DNACLIENT_VERROR        Response(s) received but validation failed
        DNACLIENT_OOS           Service is shut down
        DNACLIENT_FAILED        Service is failed
        DNACLIENT_NDIR          Name not it directory service
        DNACLIENT_DIRF          Can't contact directory service
        or network error code
*/
int DCLFUNTYPE DnaClientCall(volatile DNACRB  *rb);


#define DNACLIENT_OP_ENTRY  0 /* checking request block fields */
#define DNACLIENT_OP_DIRCON 1 /* getting directory service address */
#define DNACLIENT_OP_DIRCOM 2 /* communicating with directory service */
#define DNACLIENT_OP_FLUSH  3 /* flushing pending receives on socket */
#define DNACLIENT_OP_SEND   4 /* sending request */
#define DNACLIENT_OP_RCV    5 /* receiving response */
#define DNACLIENT_OP_VERIFY 6 /* receiving response after a verify error */

/*  This function allows you to trigger a new connect to service
    sequence for a service on your next call to this service.  This
    is not normally needed but is provided in case you get back some
    sort of bogus response and you suspect you may no longer be
    communicating with the service you thought you were.

    Return Codes:
        DNACLIENT_OK           OK return code
        DNACLIENT_UNINITED     DnaClientInit hasn't been called
        DNACLIENT_NONAME       Name not found in table
*/
int DCLFUNTYPE DnaClientMarkBad(const char  *service_name);

/*  This function allows you to check the status of a service in the
    client code's local table.

    Return Codes:
        DNACLIENT_OK          OK return code - sb data is valid.
        DNACLIENT_UNINITED    DnaClientInit hasn't been called.
        DNACLIENT_NONAME      Name not found in table.
*/
typedef struct {
    short status;           /* status values shown below */
    char  actual_name[64];  /* resolved name */
    char  service_addr[16]; /* resolved name */
} DNACSB;

int DCLFUNTYPE DnaClientGetStatus(const char  *service_name, DNACSB  *sb);

#define DNACLIENT_S_INIT      0 /* not yet accessed */
#define DNACLIENT_S_OK        1 /* successful communications has occured */
#define DNACLIENT_S_CONNECTED 2 /* in table but no communications attempted */
#define DNACLIENT_S_OOS       3 /* service is shut down */
#define DNACLIENT_S_FAILED    4 /* service is failed */
#define DNACLIENT_S_DIRF      5 /* directory service unavailable */
#define DNACLIENT_S_UNDEF     6 /* service name not in directory service */

/* CygNet ARS SERVICE LIST INFO */

typedef struct {
    char  site[8];          /* site name (left justified, null filled) */
    char  name[8];          /* service name (left justified, null filled) */
    char  type[8];          /* service type (left justified, null filled) */
    char  desc[32];         /* service desc (left justified, null filled) */
    short status;           /* service status as for 'DnaClientGetStatus' */
    unsigned char addr[16]; /* Network address */
} DNACSSS;

#define SERVICE_LIST_PAGE_SIZE 20

typedef struct {
    short n_items;          /* how many item were found */
    DNACSSS e[SERVICE_LIST_PAGE_SIZE];          /* array of items */
} DNACSSS_LIST;


/*  These functions allow you to get a list of services from
    a services directory.  This operation eases the process of
    providing a list of services to your user.

    DnaClientGetSList:

        This function calls a services directory at the well known
        port specified in the DnaClientInit call and gets a list of
        services of the type that match the service_type string you
        supplied in the 'service_type' string.  If that string is
        nulled out, all services in the services dirctory table will
        be included.

        If the 'backwards_flag' parameter is set to zero, the list
        will begin with the specified name (if it exists) and up to
        'how_many_names' entries.  The list will be in alphabetic
        order.

        The 'backwards_flag' parameter, if non-zero, will get a list
        of services starting with the 'starting_service_name' going
        towards the front of the SvcDir service list.  The list will
        be in alphabetic order.  If less than 'how_many_names' are
        found of 'service_type' before the front of the list is
        encountered, only that many entries will be returned.

        The 'how_many_names' paramter limits are 1 thru 20.

        The 'starting_service_name' parameter must be supplied in
        packed, null terminated displayable format.
        (i.e. "SITE.SERVICE")
        The function 'DnaClientSDNameString' allows you to convert
        the 'site' and 'name' fields of a DNACSSS structure into
        this form.

        The normal way to use this function is to start by passing
        a blank 'starting_service_name' (i.e. "." - just a period)
        on your first call.  This will cause the Svcdir to begin its
        search at the front of its list.   If you wish to begin at
        the end of the list and work towards the front, supply a
        name "ZZZZZZZZ.ZZZZZZZZ" and set 'backward_flag' non-zero.
    DnaClientSDNameString:

        This function allows simple conversion of DNACSSS name formats
        to null terminated displayable string format.

    DnaClientStringToSDName:

        This function is provided for convenience and converts a
        null terminiated string in the 'SITE.SERVICE' format into
        SERVICE_NAME structure format.
*/
int DCLFUNTYPE DnaClientGetSList( const char     *starting_service_name,
                              const char         *service_type,
                              short         how_many_names,
                              short         backwards_flag,
                              DNACSSS_LIST *list);

void DCLFUNTYPE DnaClientSDNameString( const char  *service_site,
                                    const char  *service_name,
                                    char  *string);

void DCLFUNTYPE DnaClientStringToSDName( const char          *string,
                                      SERVICE_NAME  *service_name);

/******************************/
/* DCL LIBRARY CVS CALLS      */
/******************************/

#ifdef DCL_RTSERVER

#include "cvs.h"

int DCLFUNTYPE DclRtGetRec( const char  *service_name,
                RT_GET_REC_REQ   *req,
                RT_GET_REC_RESP  *resp
              );

int DCLFUNTYPE DclRtGetShortRec( const char  *service_name,
                 RT_GET_SHORT_REC_REQ   *req,
                 RT_GET_SHORT_REC_RESP  *resp
                   );

int DCLFUNTYPE DclRtGetNamedRec( const char  *service_name,
                 RT_GET_NAMED_REC_REQ   *req,
                 RT_GET_NAMED_REC_RESP  *resp
                   );

int DCLFUNTYPE DclRtGetShortNamedRec( const char  *service_name,
                      RT_GET_SHORT_NAMED_REC_REQ   *req,
                      RT_GET_SHORT_NAMED_REC_RESP  *resp
                    );

int DCLFUNTYPE DclRtSetRec( const char  *service_name,
                RT_SET_REC_REQ   *req,
                RT_SET_REC_RESP  *resp
              );

int DCLFUNTYPE DclRtGetRefs( const char  *service_name,
                 RT_GET_REFS_REQ   *req,
                 RT_GET_REFS_RESP  *resp
               );

int DCLFUNTYPE DclRtMatchName( const char  *service_name,
                   RT_MATCH_NAME_REQ   *req,
                   RT_MATCH_NAME_RESP  *resp
                 );

#endif /* DCL_RTSERVER */


/******************************************/
/* DCL LIBRARY History CALLS */
/******************************************/

#ifdef DCL_HSERV

#include "vhs.h"

int DCLFUNTYPE DclStartHistoryRead( const char  *service_name,
                    START_HISTORY_READ_REQ  *req,
                    HISTORY_READ_RESP       *resp
                  );

int DCLFUNTYPE DclContinueHistoryRead( const char  *service_name,
                       CONTINUE_HISTORY_READ_REQ  *req,
                       HISTORY_READ_RESP          *resp
                     );

int DCLFUNTYPE DclStorePointHistory( const char  *service_name,
                     STORE_POINT_HISTORY_REQ   *req,
                     STORE_POINT_HISTORY_RESP  *resp
                   );

int DCLFUNTYPE DclStoreHistoryList( const char  *service_name,
                    STORE_HISTORY_LIST_REQ   *req,
                    STORE_HISTORY_LIST_RESP  *resp
                  );

#endif /* DCL_HSERV */

/******************************************/
/* DCL LIBRARY Boss CALLS         */
/******************************************/

#ifdef DCL_BOSS

#include "rsm.h"
//#include "rsmcmd.h"

int DCLFUNTYPE DclBossGetInfo( const char  *service_name,
                   BOSS_GET_INFO_REQ  *req,
                   BOSS_GET_INFO_RESP  *resp
                 );

int DCLFUNTYPE DclBossUpdateInfo( const char  *service_name,
                  BOSS_UPDATE_INFO_REQ  *req,
                  BOSS_UPDATE_INFO_RESP  *resp
                );

int DCLFUNTYPE DclBossGetSvcInfo( const char  *service_name,
                  BOSS_GET_SVC_INFO_REQ  *req,
                  BOSS_GET_SVC_INFO_RESP  *resp
                );

int DCLFUNTYPE DclBossGetSvcList( const char  *service_name,
                  BOSS_GET_SVC_LIST_REQ  *req,
                  BOSS_GET_SVC_LIST_RESP  *resp
                );

int DCLFUNTYPE DclBossGetSvcListShort( const char  *service_name,
                       BOSS_GET_SVC_LIST_SHORT_REQ  *req,
                       BOSS_GET_SVC_LIST_SHORT_RESP  *resp
                     );

int DCLFUNTYPE DclBossGetSvcDef( const char  *service_name,
                  BOSS_GET_SVC_DEF_REQ  *req,
                  BOSS_GET_SVC_DEF_RESP  *resp
                );

int DCLFUNTYPE DclBossAddSvcDef( const char  *service_name,
                 BOSS_ADD_SVC_DEF_REQ  *req,
                 BOSS_ADD_SVC_DEF_RESP  *resp
                   );

int DCLFUNTYPE DclBossUpdateSvcDef( const char  *service_name,
                    BOSS_UPDATE_SVC_DEF_REQ  *req,
                    BOSS_UPDATE_SVC_DEF_RESP  *resp
                  );

int DCLFUNTYPE DclBossDeleteSvcDef( const char  *service_name,
                    BOSS_DELETE_SVC_DEF_REQ  *req,
                    BOSS_DELETE_SVC_DEF_RESP  *resp
                  );

int DCLFUNTYPE DclBossStartSvc( const char  *service_name,
                BOSS_CONTROL_SVC_REQ  *req,
                BOSS_CONTROL_SVC_RESP  *resp
                  );

int DCLFUNTYPE DclBossStopSvc( const char  *service_name,
                   BOSS_CONTROL_SVC_REQ  *req,
                   BOSS_CONTROL_SVC_RESP  *resp
                 );

int DCLFUNTYPE DclBossKillSvc( const char  *service_name,
                   BOSS_CONTROL_SVC_REQ  *req,
                   BOSS_CONTROL_SVC_RESP  *resp
                 );

int DCLFUNTYPE DclBossDiskInfo( const char  *service_name,
                BOSS_DISK_INFO_REQ  *req,
                BOSS_DISK_INFO_RESP  *resp
                  );

int DCLFUNTYPE DclBossDirFirst( const char  *service_name,
                BOSS_DIR_FIRST_REQ  *req,
                BOSS_DIR_RESP  *resp
                  );

int DCLFUNTYPE DclBossDirNext( const char  *service_name,
                   BOSS_DIR_NEXT_REQ  *req,
                   BOSS_DIR_RESP  *resp
                 );

int DCLFUNTYPE DclBossDirEnd( const char  *service_name,
                  BOSS_DIR_END_REQ  *req,
                  BOSS_DIR_END_RESP  *resp
                );

int DCLFUNTYPE DclBossFileLength( const char  *service_name,
                  BOSS_FILE_LENGTH_REQ  *req,
                  BOSS_FILE_LENGTH_RESP  *resp
                );

int DCLFUNTYPE DclBossFileRead( const char  *service_name,
                BOSS_FILE_READ_REQ  *req,
                BOSS_FILE_READ_RESP  *resp
                  );

int DCLFUNTYPE DclBossStartFileWrite( const char  *service_name,
                      BOSS_START_FILE_WRITE_REQ  *req,
                      BOSS_START_FILE_WRITE_RESP  *resp
                    );

int DCLFUNTYPE DclBossFileDataWrite( const char  *service_name,
                     BOSS_FILE_DATA_WRITE_REQ  *req,
                     BOSS_FILE_DATA_WRITE_RESP  *resp
                   );

int DCLFUNTYPE DclBossEndFileWrite( const char  *service_name,
                    BOSS_END_FILE_WRITE_REQ  *req,
                    BOSS_END_FILE_WRITE_RESP  *resp
                  );

int DCLFUNTYPE DclBossFileSysOp( const char  *service_name,
                 BOSS_FILE_SYS_OP_REQ  *req,
                 BOSS_FILE_SYS_OP_RESP  *resp
                   );

int DCLFUNTYPE DclBossSystem( const char  *service_name,
                  BOSS_SYSTEM_REQ  *req,
                  BOSS_SYSTEM_RESP  *resp
                );

#endif /* DCL_BOSS */
/******************************************/
/* DCL LIBRARY DBS CALLS                  */
/******************************************/

/* These calls document the DataBase Service (DBS) protocol as implemented */
/* in the DCL library. All calls will return an error code. The routine    */
/* will check the error returned. If the network interface indicates that  */
/* no error occurred, the error code from the response header will be      */
/* checked. If that error code is non-zero, it will be returned. If the    */
/* response header error code is either DB_EOF or DB_EOQ, a zero will be   */
/* returned from the function and the end_flag parameter will be set to    */
/* the appropriate value (DB_EOF or DB_EOQ).                               */


#ifdef DCL_DBS

#define DBS_UPDATE_BUFFER_FULL 1

#ifndef FALSE
#define FALSE 0
#endif /* FALSE */
#ifndef TRUE
#define TRUE !FALSE
#endif /* TRUE */

#include "dbs.h"

/* This function gets the current DBS version.                      */
int DCLFUNTYPE DnaDBSGetVersion( const char DCLMEMTYPE *dbs_service,
         int  DCLMEMTYPE *version
           );

/* This function gets the current DBS table and security info       */
int DCLFUNTYPE DnaDBSGetInfo( const char DCLMEMTYPE *dbs_service,
          char DCLMEMTYPE *trs_service,
          char DCLMEMTYPE *acs_service,
          char DCLMEMTYPE *acs_main_event,
          char DCLMEMTYPE *acs_secondary_events
        );

/* This function reads the index  records starting at "key" for up  */
/* to count records. Dir indicates direction (FORWARD or BACKWARD   */
/* defined in dbs.h).                                               */
int DCLFUNTYPE DnaDBSReadIndex( const char DCLMEMTYPE *dbs_service,
        const char DCLMEMTYPE *key,
        int  dir,
        int DCLMEMTYPE *count,
        int DCLMEMTYPE *index_len,
        char DCLMEMTYPE *data,
        int DCLMEMTYPE *end_flag
          );

/* This function "scans" the index records starting at "key" for up  */
/* to count records. Dir indicates direction (FORWARD or BACKWARD    */
/* defined in dbs.h). An index scan will return index  records that  */
/* have different strings when comparing the first key_len chars but */
/* will always have matching chars for the first prefix_len chars.   */
int DCLFUNTYPE DnaDBSScanIndex( const char DCLMEMTYPE *dbs_service,
        const char DCLMEMTYPE *key,
        int  dir,
        int DCLMEMTYPE *count,
        int key_len,
        int prefix_len,
        int DCLMEMTYPE *index_len,
        char DCLMEMTYPE *data,
        int DCLMEMTYPE *end_flag
          );

/* This function reads the data records starting at "key" for up  */
/* to count records. Dir indicates direction (FORWARD or BACKWARD */
/* defined in dbs.h).                                             */
int DCLFUNTYPE DnaDBSReadData(  const char DCLMEMTYPE *dbs_service,
        const char DCLMEMTYPE *key,
        int  dir,
        int DCLMEMTYPE *count,
        char DCLMEMTYPE *data,
        int DCLMEMTYPE *end_flag
         );


/* This function reads the data records either from the DBS data */
/* file or the temporary storage file (from a previous create or */
/* update call)                                                  */
int DCLFUNTYPE DnaDBSReadSegmentData(  const char DCLMEMTYPE *dbs_service,
             char DCLMEMTYPE *key,
             unsigned short record_type,
             char           *parent_key,
             int            ts_switch,
             int        dir,
             int        *count,
             char DCLMEMTYPE *data,
             int        *end_flag
           );


/* This function reads the data records for updating using "key"  */
/* to identify the header key for the queue. Up to count records  */
/* will be read. Dir indicates direction (FORWARD or BACKWARD     */
/* defined in dbs.h). The userid will be used by DBS to mark the  */
/* owner of the lock. The lock_time will be needed for future     */
/* updates and commits.                                           */
int DCLFUNTYPE DnaDBSOpenOldQueue( const char DCLMEMTYPE *dbs_service,
           const char DCLMEMTYPE *key,
           int  dir,
           int  DCLMEMTYPE *count,
           const char DCLMEMTYPE *userid,
           int  force,
           long DCLMEMTYPE *lock_time,
           char DCLMEMTYPE *data,
           int  DCLMEMTYPE *end_flag
         );

/* This function get the next header key from the DBS and place it in   */
/* new_key. If copy_key is not blank the new queue will be loaded with  */
/* the data from the copied queue. Lock_time will be needed for updates */
/* and commits.                                                         */
int DCLFUNTYPE DnaDBSOpenNewQueue( const char DCLMEMTYPE *dbs_service,
           const char DCLMEMTYPE *copy_key,
           char DCLMEMTYPE *new_key,
           const char DCLMEMTYPE *userid,
           long DCLMEMTYPE *lock_time
         );

/* This function reads the data records from the queue starting at */
/* "key" Up to count records will be read. Dir indicates direction */
/* (FORWARD or BACKWARD defined in dbs.h).                             */
int DCLFUNTYPE DnaDBSReadQueue( const char DCLMEMTYPE *dbs_service,
        const char DCLMEMTYPE *key,
        int  dir,
        int  DCLMEMTYPE *count,
        char DCLMEMTYPE *data,
        int  DCLMEMTYPE *end_flag
          );

/* This function updates the data records in the queue.             */
int DCLFUNTYPE DnaDBSUpdateQueue( const char DCLMEMTYPE *dbs_service,
          char DCLMEMTYPE *data
        );

/* This function releases the data records in the queue.             */
int DCLFUNTYPE DnaDBSReleaseQueue( const char DCLMEMTYPE *dbs_service,
          const char DCLMEMTYPE *key,
          const char DCLMEMTYPE *userid,
          long lock_time
        );

/* This function will obtain a new lock for the queue.               */
int DCLFUNTYPE DnaDBSRelockQueue( const char DCLMEMTYPE *dbs_service,
          const char DCLMEMTYPE *key,
          const char DCLMEMTYPE *userid,
          long DCLMEMTYPE *lock_time
        );

/* This function will read a single database record and lock its header */
/* No Queue manipulation functions are valid on the retrieved data      */
int DCLFUNTYPE DnaDBSLockDBRecords( const char DCLMEMTYPE *dbs_service,
          const char DCLMEMTYPE *key,
          unsigned short force_flag,
          char DCLMEMTYPE *userid,
          long DCLMEMTYPE *lock_time
        );

/* This function will get a new lock for the indicated record's header */
int DCLFUNTYPE DnaDBSRelockDBRecords( const char DCLMEMTYPE *dbs_service,
          const char DCLMEMTYPE *key,
          char DCLMEMTYPE *userid,
          long DCLMEMTYPE *lock_time
        );

/* This function will unlock the indicated record's header             */
int DCLFUNTYPE DnaDBSReleaseDBRecords( const char DCLMEMTYPE *dbs_service,
          const char DCLMEMTYPE *key,
          char DCLMEMTYPE *userid,
          long DCLMEMTYPE *lock_time
        );

/* This function is used to write, update and delete a record and optionally */
/* the associated header record. */
int DCLFUNTYPE DnaDBSUpdateDBRecords( const char DCLMEMTYPE *dbs_service,
        int  DCLMEMTYPE *count,
        unsigned short force_flag,
        const char DCLMEMTYPE *userid,
        char DCLMEMTYPE *lock_userid,
        long DCLMEMTYPE *lock_time,
        char DCLMEMTYPE *records
        );

/* This function will prepare the buffer by initializing it with    */
/* nulls and placing the DBS request header at the beginning.       */
int DCLFUNTYPE DnaDBSUpdateBufferInit( const char DCLMEMTYPE *userid,
               long lock_time,
               char DCLMEMTYPE *buffer
             );

/* This function will place a data record in the update buffer for   */
/* create.                                                           */
int DCLFUNTYPE DnaDBSUpdateBufferAdd( const char DCLMEMTYPE *data,
              char DCLMEMTYPE *buffer
            );

/* This function will place a data record in the update buffer for   */
/* update.                                                           */
int DCLFUNTYPE DnaDBSUpdateBufferUpdate( const char DCLMEMTYPE *data,
             char DCLMEMTYPE *buffer
               );

/* This function will place a data record in the update buffer for   */
/* delete.                                                           */
int DCLFUNTYPE DnaDBSUpdateBufferDelete( const char DCLMEMTYPE *data,
             char DCLMEMTYPE *buffer
               );

/* This function commits the data records in the queue for update.   */
int DCLFUNTYPE DnaDBSCommitOldQueue( const char DCLMEMTYPE *dbs_service,
             const char DCLMEMTYPE *key,
             const char DCLMEMTYPE *userid,
             long lock_time
           );

/* This function commits the data records in the queue for create.  */
int DCLFUNTYPE DnaDBSCommitNewQueue( const char DCLMEMTYPE *dbs_service,
             const char DCLMEMTYPE *key,
             const char DCLMEMTYPE *userid,
             long lock_time
           );

/* This function commits the data records in the queue for delete.  */
int DCLFUNTYPE DnaDBSCommitDeleteQueue( const char DCLMEMTYPE *dbs_service,
            const char DCLMEMTYPE *key,
            const char DCLMEMTYPE *userid,
            long lock_time
              );

/* This function will increment a DBS key for the level indicate    */
void DCLFUNTYPE DnaDBSKeyInc( char DCLMEMTYPE *key,
          int  level
        );

/* This function will decrement a DBS key for the level indicate    */
void DCLFUNTYPE DnaDBSKeyDec( char DCLMEMTYPE *key,
          int  level
        );

/* This function will set a DBS segment key to a value at the level */
/* indicated. The segment_char will be used as the segment prefix   */
/* character                                                        */
void DCLFUNTYPE DnaDBSKeySet( char DCLMEMTYPE *key,
          int  level,
          char segment_char,
          long  value
        );

/* This function will copy a file from the blob service to the standard */
/* file.                                                                */
int DCLFUNTYPE CopyBlobToFile( const char DCLMEMTYPE *bss_service,
                   const char DCLMEMTYPE *application,
                   const char DCLMEMTYPE *filename );

/* This function will copy a file from the standard file to the blob     */
/* service.                                                              */
int DCLFUNTYPE CopyFileToBlob( const char DCLMEMTYPE *bss_service,
                   const char DCLMEMTYPE *application,
                   const char DCLMEMTYPE *filename,
                   const char DCLMEMTYPE *userid );

#endif /* DCL_DBS */


/******************************************/
/* DCL LIBRARY LOGINX CALLS               */
/******************************************/

// These calls document the LoginX interface. They are intended to be used
// when an external application(s) requires login/logout functions.
// This is accomplished by using a DLL name LOGINXWD.DLL (16 bit) or
// LOGINXND.DLL (32 bit). A sample LOGINX DLL exists in \CYGNET\MSTRSRCE\LOGINX.
// Acual implementations require a customer specific LOGINX DLL to be supplied.


#ifndef _DOS
#ifdef DCL_LOGINX

// DCL login error codes - 0 thru 19 used by loginx
#define DCL_LOGINX_TABLE_FULL     20 // Dcl Application table full
#define DCL_LOGINX_MISSING_USERID 21 // the userid is required, but missing
#define DCL_LOGINX_OTHER_USER     22 // a different userid is logged onto that application
#define DCL_LOGINX_NOT_LOGGED_IN  23 // the DCL table does not have a user logged into the Appl

#define DCL_LOGINX_YES 0    // yes, the user is logged in
#define DCL_LOGINX_NO  1    // no, the user is not logged in

/********************************************************************
*
*   This function attempts a login to an external application.
*   Since we don't know the required agruments of the external
*   login, some parameters may not always be used.
*
*   Returns: (defined in loginx.h)
*       LOGINX_SUCCESS      - good login
*       LOGINX_ALREADY_IN   - user was already logged in
*       LOGINX_FAIL         - login failed (reason unknown)
*       LOGINX_BAD_PASSWORD - login password invalid
*       LOGINX_BAD_ID       - login userid invalid or expired
*       plus defined in DCL.H:
*       DCL_LOGINX_TABLE_FULL -  the internal DCL table is full - can't login
*       DCL_LOGINX_MISSING_USERID - the userid is required, but missing
*       DCL_LOGINX_OTHER_USER -  a different userid is logged onto that application
*
*********************************************************************/
int DCLFUNTYPE CygNetLogin(const char *szUserId, const char *szPassword,
    const char *szApplication,  const char *szService, long *lCode, char *szArg6 );

/********************************************************************
*
*   This function attempts a logout an external application.
*   Since we don't know the required agruments of the external
*   logout, some parameters may not always be used.
*
*   Returns: (defined in loginx.h)
*       LOGOUTX_SUCCESS         - good logout
*       LOGOUTX_NOT_LOGGED_IN   - user was not logged in
*       LOGOUTX_FAIL            - logout failed (reason unknown)
*       plus defined in DCL.H:
*       DCL_LOGINX_NOT_LOGGED_IN -  the DCL table does not have a user logged into the Appl
*
*********************************************************************/
int DCLFUNTYPE CygNetLogout( const char *szApplication, const char *szService, long *lCode  );

/********************************************************************
*
*   This function gets the login status and userid for an application.
*
*   Returns: defined in DCL.H:
*       DCL_LOGINX_YES    - user is logged in to application
*       DCL_LOGINX_NO     - user is not logged in to application
*
*********************************************************************/
int DCLFUNTYPE GetCygNetLoginStatus( const char *szApplication, char *szUserId );

#endif /* DCL_LOGINX */
#endif /* not _DOS */

#ifdef __cplusplus
}
#endif

#endif /* __DCL__ */
