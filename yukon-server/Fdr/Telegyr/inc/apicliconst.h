/******************************************************************************
	Filename: $RCSfile: apicliconst.h,v $
	Revision: $Revision: 1.1 $
	Author:   $Author: eschmit $
	Date:     $Date: 2002/07/15 14:04:34 $
*******************************************************************************
$Log: apicliconst.h,v $
Revision 1.1  2002/07/15 14:04:34  eschmit
Telegyr stuff added

Revision 1.7  2000/12/19 14:46:57  imhoff
merged changes for new API-UEA functions

 * DDF: API Extensions for Tags
 * Added tag parameters and group types.
 *
 * Revision 1.24  2000/06/28  23:59:37  bscott
 * DDF: API Extension for Alarms List
 * DDF: API Extension for Display request
 * Added Alarms List entry and Display Request items

Revision 1.6  2000/07/04 08:49:03  imhoff
changes for new API functions (TG8000_SMR_FI_00038.doc)

Revision 1.5  2000/04/07 09:10:14  imhoff
intr. definitions of API_NODE_NAME_SIZE and API_APINET_CNF

Revision 1.4  1999/05/12 14:36:44  imhoff
murged with 7.2 system revision 1.22

Revision 1.3  1999/01/13 00:29:01  drossi
Updated based on V7.2 1.20 : Merged by bscott

Revision 1.20  1998/12/01 09:48:59  drossi
QAPR3469. Define new reason code: 26, API_INOUT_SCAN.
Sent to applications for written points changing in-out scan flag value.

Revision 1.19  1998/11/12  16:13:34  danieler
New alm&evt lists functionality.

Revision 1.17  1998/09/17  12:57:32  heinzerb
Modification for Common SCADA/DMS list server

Revision 1.16  1998/09/16  15:09:35  heinzerb
Modification for Common SCADA/DMS list server

******************************************************************************/
/*
	This file includes global macros and enum 
	for use of API library.
	It has to be included by API application programs.
*/


#ifndef APICLICONST_H
#define APICLICONST_H


#include "apicli_get_list.h"


/*
 *  TRANSMISSION REASON VALUES
 *  (output parameter of api_get_queued_data routine)
*/
#define API_REQ_DATA		1
#define API_CYC_DATA		2
#define API_SPO_DATA		3
#define API_CRE_RES		4
#define API_DEL_RES		5
#define API_DELALL_RES		6
#define API_ENCYC_RES		7
#define API_ENSPO_RES		8
#define API_ENDQS_RES		9
#define API_DISCYC_RES		10
#define API_DISSPO_RES		11
#define API_DISDQS_RES		12
#define API_DISCYCALL_RES	13
#define API_DISSPOALL_RES	14
#define API_WRI_RES	  	    15
#define API_COM_RES		    16
#define API_GROUPS		    17
#define API_READ_GRP		18
#define API_DISC_NOTIFY		19
#define API_FULL_NOTIFY		20
#define API_DQS_DATA		21
#define API_DQS_RES		    22
#define API_LIST_ALM_EVT	23
#define API_ENLIST_RES		24
#define API_DISLIST_RES		25
#define API_INOUT_SCAN		26
#define API_ALARMS_LIST		27
#define API_ALARM_ACK_DELETE_RES    28
#define API_SETPADQMEA_RES      29
#define API_SETPADQIND_RES      30 
#define API_SETPADQCNT_RES      31
#define API_DISPLAY_REQUEST_RES     32
#define API_SPO_TAG_ADD         33
#define API_SPO_TAG_DELETE      34
#define API_SPO_TAG_MODIFY      35
#define API_WRT_RES             36
#define API_TAG_RES             37

#define API_TRUE 		  1
#define API_FALSE		  0
/*
 * DEQUEUING METHOD
*/
#define API_DEQUEUE_FIRST_DATA    0
#define API_DEQUEUE_PRIO_DATA     1

/*
 * INFORMATION VALIDITY
*/
#define API_INVALID               1
#define API_VALID                 0

/*
 * INFORMATION SOURCE 
*/
#define API_OUT_OF_SERVICE        0
#define API_TRANSMIT              1
#define API_CALCULATED            2
#define API_MANUAL                3

/*
 * INFORMATION STATUS
*/
#define API_NO_ALARM              1
#define API_ENDANGERED            2
#define API_DISTURBED             3
#define API_ALARM		  4
#define API_ABNORMAL		  5

/* INFORMATION TEST STATUS */
#define API_TEST_NORMAL           0
#define API_TEST_MODE             1

/*
 * INFORMATION CHECK BACK
*/
#define API_CHECK_BACK_OK         1
#define API_CHECK_BACK_NOK        0

/*
 * INFORMATION ZONE OF DATA WITH RESPECT OF ALARM LIMITS
 * (measurands and counters)
*/  
#define	API_ZONE_0  		0
#define	API_ZONE_1		1
#define	API_ZONE_2		2
#define	API_ZONE_3		3
#define	API_ZONE_4		4
#define	API_ZONE_5		5

/*
 * CODE OF INDICATION VALUES
*/
#define API_ERROR                 0
#define API_ON                    1
#define API_OFF                   2
#define API_ZERO                  3

/*
 * VALUES for ALIGNMENT Parameter to
 * delay the first sending of data in 
 * api_enable_cyclic routine
*/
#define API_ALIG_MIDNIGHT	-7
#define API_ALIG_FULLHOUR	-6
#define API_ALIG_HALFHOUR	-5
#define API_ALIG_QUARTERH	-4
#define API_ALIG_FULLMIN	-3
#define API_ALIG_HALFMIN	-2
#define API_ALIG_QUARTERMIN	-1
#define API_ALIG_NOALIG		 0
/* Values greater then 0 are allowed
   with the meaning of a delay to 
   seconds or minutes or hours 
   depending on cycle_unit */

/*
 * COMMANDS DIRECTIONS
*/
#define API_CMDDIR_LEN		  9

#define API_COMMAND_OPEN          1 /* "Open" 		*/
#define API_COMMAND_CLOSE         2 /* "Close"  	*/
#define API_COMMAND_RAISE         3 /* "Raise" 		*/
#define API_COMMAND_LOWER         4 /* "Lower"  	*/
#define API_COMMAND_SETANALOG     5 /* "SetAnalog"  	*/

/*
 * Code parameter values to routines api_set_padq_xxx
*/
#define API_MANUAL_SET               1
#define API_SET_IN_SCAN              2
#define API_SET_OUT_SCAN             3
#define API_SET_ALARM_INHIBIT        4
#define API_CLEAR_ALARM_INHIBIT      5
#define API_SET_WARN_ALARM_INHIBIT   6
#define API_CLEAR_WARN_ALARM_INHIBIT 7

/* General CONSTANTS */
#define API_CLIENT_NAME_SIZE      6
#define API_SYSTEM_NAME_SIZE      5
#define API_PROG_NAME_SIZE        31
#define API_VERSION_SIZE          6
#define API_OPERATOR_SIZE         12
#define API_PASSWORD_SIZE         8
#define API_MAX_CHAR_SYS_DEP      32
#define API_MAX_TEXT              1024
#define API_MAX_GEN               10240 
#define API_MAX_DQS               512 
#define API_MAX_EMSPROC           10
#define API_ALMEVT_MSG_LEN	  132
#define API_TAG_PERMIT_LEN        8
#define API_TAG_TYPE_LEN          15
#define API_TAG_COMMENT_LEN       78
#define API_MAX_TAGS              500
#define API_MAX_BUFFER_TAGS       50

/*
 * CONSTANTS for Groups Management
*/
#define API_OBJECTS_IN_GROUP      128   /* Max nbr of objects per Grp */
#define API_MAX_GROUPS_PER_CONN   256   /* Max nbr of groups per conn */

#define API_GRP_NO_PERSISTENCE    0	/* No persistence group */
#define API_GRP_PERSISTENCE       1	/* Persistence group 	*/

#define API_PRIORITY_LOW    	  1	/* Normal Priority Group */
#define API_PRIORITY_HIGH    	  8     /* High   Priority Group */

#define API_ALARM_PRIORITY_LOW 	  1	/* Low alarm entry priority  */
#define API_ALARM_PRIORITY_HIGH	 16     /* High alarm entry priority */


/* GROUP TYPES */
typedef enum {

API_GRP_TYPE_START = 150, /* WARNING: Always let this item as the      */
                          /*          first one of the enumerated type */

/* READ DATA REQUESTED FROM REAL-TIME DATABASE */
API_GET_RTS_MEA = 	   151,
API_GET_RTS_IND,
API_GET_RTS_CNT,
API_GET_RTS_TEXT,

/* READ DATA REQUESTED FROM HISTORICAL DATABASE */
API_GET_HIS_MEA,	/* 155 */
API_GET_HIS_IND,
API_GET_HIS_CNT,

/* READ DATA SPONTANEOUS  FROM  REALTIME  DATABASE */
API_GET_SPO_MEA,	/* 158 */
API_GET_SPO_IND,
API_GET_SPO_CNT,
API_GET_SPO_TEXT,

/* READ DATA CYCLIC  FROM  REALTIME  DATABASE */
API_GET_CYC_MEA,	/* 162 */
API_GET_CYC_IND,
API_GET_CYC_CNT,
API_GET_CYC_TEXT,

API_GRP_PUT_START, 	/* 166 */

/* WRITE DATA INTO REALTIME  DATABASE */
API_PUT_RTS_MEA,	/* 167 */
API_PUT_RTS_IND,
API_PUT_RTS_CNT,
API_PUT_RTS_TEXT,

/* WRITE DATA INTO HISTORICAL DATABASE */
API_PUT_HIS_MEA, 	/* 171 */
API_PUT_HIS_CNT,
API_PUT_HIS_IND,

API_GRP_PUT_END, 	/* 174 */

/* WRITE COMMANDS AS INPUT TO  REALTIME  PROCESSING */
API_COM_RTS_PLS,	/* 175 */
API_COM_RTS_SPT,
API_COM_RTS_SEQ,

/* GENERIC GROUPS */
API_IND,		/* 178 */
API_MEA,
API_CNT,
API_MIX,
API_TEXT,
API_GEN_TABLE,
API_GEN_RECORD,
API_GEN_ENTRY,

/* OUTPUT COMMANDS */
API_GET_SPO_PLS,	/* 186 */
API_GET_SPO_SPT,

/* ALARMS AND EVENTS LIST */

API_GET_RTS_LIST,	/* 188 */
API_GET_SPO_LIST,       /* 189 */
API_PUT_RTS_LIST,	/* 190 */
API_LIST,               /* 191 */

/* GROUPS FOR EQUIPMENTS */

API_GET_RTS_EQUIP,      /* 192 */
API_GET_SPO_EQUIP,      /* 193 */
API_GET_CYC_EQUIP,      /* 194 */
API_PUT_RTS_EQUIP,      /* 195 */
API_EQUIP,              /* 196 */

API_GET_SPO_ALARMS_LIST, /* 197 */

/* WRITE POINT ATTRIBUTE/DATA QUALITY BITS/VALUES */

API_PUT_PADQ_MEA,       /* 198 */ 
API_PUT_PADQ_IND,       /* 199 */
API_PUT_PADQ_CNT,       /* 200 */

/* TAG GROUPS */
 
API_GET_RTS_TAG,        /* 201 */
API_GET_SPO_TAG,        /* 202 */
API_PUT_RTS_TAG,        /* 203 */
API_DEL_RTS_TAG,        /* 204 */

API_GRP_TYPE_END /* WARNING: always let this item as the last one */
                 /*          of the enumerated type               */


} API_GRP_TYPES;

/*
* get queued time parameter 
*/

#define API_NO_TIMEOUT 	  	((long)0)        /* immediate return */
#define API_WAIT_FOREVER   	((long)-1)       /* wait forever */
#define API_NO_WAIT_ANY_PL      ((long)1)        /* no call wait_any_pl*/

/*
 * TYPE OF CLIENT ACCESS TO CONTROL CENTRE
*/
typedef enum {
	API_READ = 1,
	API_WRITE,
	API_CONTROL,
	API_SUPER
	} APICLI_CLI_ACCESS_MODES;

typedef enum {		/* TYPES OF CONTROL CENTRES */
	API_LS2030 = 1,
	API_LS3200,
	API_TG8020,
	API_TELEGYR,
	API_ELCOM_CC,
	API_XXXXX_CC
	} APICLI_CEN_CON_TYP;

#define API_NODE_NAME_SIZE      15
#define API_APINET_CNF          "APINET.CNF"
#define API_ALL_TAG             "API_ALL_TAG"

#endif	/* APICLICONST_H */
