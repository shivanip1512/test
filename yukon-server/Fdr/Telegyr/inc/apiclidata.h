/******************************************************************************
	Filename: $RCSfile: apiclidata.h,v $
	Revision: $Revision: 1.1 $
	Author:   $Author: eschmit $
	Date:     $Date: 2002/07/15 14:04:34 $
*******************************************************************************
$Log: apiclidata.h,v $
Revision 1.1  2002/07/15 14:04:34  eschmit
Telegyr stuff added

Revision 1.8  2000/12/19 14:50:02  imhoff
merged changes for new API-UEA functions

 * DDF: API Extensions for Tags
 * Added structure APICLI_GET_TAG.

Revision 1.7  2000/07/04 08:49:43  imhoff
changes for new API functions (TG8000_SMR_FI_00038.doc)

Revision 1.6  2000/04/07 09:07:42  imhoff
intr. definition of RECSYSTEM

Revision 1.5  1999/09/23 12:35:18  imhoff
Added a spare alignment field in APICLI_TIME structure.

 * Revision 1.4  1999/05/12  14:40:09  imhoff
 * murged with 7.2 system revision 1.23
 *
Revision 1.3  1999/01/13 00:30:20  danieler
Updated based on V7.2 1.21 : Merged by bscott

Revision 1.21  1998/11/12 16:14:33  danieler
New alm&evt lists functionality.

Revision 1.18  1998/09/17  12:57:52  heinzerb
Modification for Common SCADA/DMS list server

Revision 1.17  1998/09/16  15:12:32  heinzerb
Modification for Common SCADA/DMS list server

******************************************************************************/
/*
     This file defines the structures for all API data groups 
     (derived from database related tables) to be used by and
     returned to the Client.
     It also contains others data types and structures used in
     the client interface.
*/


#ifndef APICLIDATA_H
#define APICLIDATA_H


#include "apicli_get_list.h"


/* 
 * GENERAL PURPOSE STRUCTURES
*/
typedef struct apicli_time {         /* Time parameters for Clients */

    unsigned int       year;
    unsigned int       month;
    unsigned int       day;
    unsigned int       hour;
    unsigned int       min;
    unsigned int       sec;
    unsigned int       dst;	     /* Daylight Savings Time flag */

    unsigned int       filler;	     /* alignment spare field */

   } APICLI_TIME;

typedef enum {			     /* Delta Unit Parameter */
	API_HOUR = 1,
	API_MINUTE,
	API_DAY,
	API_WEEK,
	API_MONTH,
	API_YEAR,
	API_SECOND
	} APICLI_TIME_UNITS;

typedef struct api_padq_flags {
	unsigned int padq_mask;
	unsigned int padq_set;
	} API_PADQ_FLAGS;

typedef struct api_mixgrp {
	int group_type;
	int group_nbr;
	int index;
	union {
	    int i_value;
	    float f_value;
	    } 	info_value;
	int 	info_valid;
	API_PADQ_FLAGS 	info_padq;
	} API_MIXGRP;

/*
 *
 *	The next structure is returned to the client program as last
 *	field of the output parameter of the api_unpack_xxx routines;
 *	It is always filled with NULLs for data coming from a TG8000
 *	system.
 *
*/
typedef union {

	long 	long_value;
	int 	int_value;
	char	string_value[32];
	float	float_value;

	} SYS_DEP_INFO;

/* 
 * GET INDICATIONS
 *
 * GROUPS: 
 * 	API_GET_RTS_IND, API_GET_SPO_IND, API_GET_CYC_IND
 *
*/
typedef struct apicli_get_ind {

	int             ind_value;
	int 		ind_valid;
	APICLI_TIME     ind_time;
	int 		ind_source;
	int 		ind_status;
	int 		ind_events;
	int 		ind_check_back;
	int 		ind_test_mode;
	int 		ind_intermediate_change;
	SYS_DEP_INFO    sys_dependent_info;

	} APICLI_GET_IND;


/* 
 * GET MEASURANDS
 *
 * GROUPS: 
 * 	API_GET_RTS_MEA, API_GET_SPO_MEA, API_GET_CYC_MEA
 *
*/
typedef struct apicli_get_mea {

	union {
	float   	mea4_value;
	double		mea8_value;
	      } mea_value;

	int 		mea_valid;
	APICLI_TIME     mea_time;
	int 		mea_source;
	int 		mea_status;
	int 		mea_zone;
	int 		mea_check_back;
	int 		mea_test_mode;

	SYS_DEP_INFO    sys_dependent_info;

	} APICLI_GET_MEA;

/* 
 * GET COUNTER VALUES
 *
 * GROUPS: 
 * 	API_GET_RTS_CNT, API_GET_SPO_CNT, API_GET_CYC_CNT
 *
*/
typedef struct apicli_get_cnt {

	union {
	float   	cnt4_value;
	double		cnt8_value;
	      } cnt_value;

	float		cnt_deltvalue;
	float		cnt_dhrvalue;
	float		cnt_lhrvalue;

	int 		cnt_valid;
	APICLI_TIME     cnt_time;
	int 		cnt_source;
	int 		cnt_status;
	int 		cnt_zone;
	int 		cnt_test_mode;

	SYS_DEP_INFO    sys_dependent_info;

	} APICLI_GET_CNT;

/* 
 * GET EQUIPMENTS
 *
 * GROUPS: 
 * 	API_GET_RTS_EQUIP, API_GET_SPO_EQUIP, API_GET_CYC_EQUIP
 *
*/
typedef struct apicli_get_equip {

	int             equip_value;
	int 		equip_valid;
	APICLI_TIME     equip_time;

	SYS_DEP_INFO    sys_dependent_info;

	} APICLI_GET_EQUIP;

/* 
 * GET TAG INFORMATION 
 *
 * GROUPS: 
 * 	API_GET_SPO_TAG, API_GET_RTS_TAG
 *
*/
typedef struct apicli_get_tag {
        APICLI_TIME     tag_time;
        char            permit_no[API_TAG_PERMIT_LEN + 1];
        char            tag_type[API_TAG_TYPE_LEN + 1];
        char            tag_comment_1[API_TAG_COMMENT_LEN + 1];
        char            tag_comment_2[API_TAG_COMMENT_LEN + 1]; 
        } APICLI_GET_TAG;


/* 
 * GET DQS 
 *
 * GROUPS: 
 * 	none 
 *
*/
typedef struct apicli_dqs_data {

	char dqs_data[API_MAX_DQS];

	} APICLI_DQS_DATA;

/* 
 * GET TEXT 
 *
 * GROUPS: 
 * 	API_GET_RTS_TEXT, API_GET_SPO_TEXT, API_GET_CYC_TEXT
 *
*/
typedef struct apicli_txt {

	char text[API_MAX_TEXT];

	} APICLI_TXT;

/* 
 * GET GENERIC 
 *
 * GROUPS: 
 * 	API_GEN_TABLE, API_GEN_RECORD, API_GEN_ENTRY
 *
*/
typedef struct apicli_gen {

	char generic[API_MAX_GEN];

	} APICLI_GENERIC;


/* 
 * GET Outgoing COMMANDs
 *
 * GROUPS: 
 * 	API_GET_SPO_PLS, API_GET_SPO_SPT
 *
*/
typedef struct apicli_get_com {

	int 	direction;
	float 	value;				/* SPT only */
	int 	repeat_count;			/* SPT only */
	int 	com_sbo;			/* PLS only */
	int	com_timeout;			/* PLS only */

	} APICLI_GET_COM;

#define API_SIZEOF_CLIDATA sizeof(APICLI_GET_CNT)

/*
 * Data structure to store the system definitions from APINET.CNF
*/
typedef struct recsystem {

        char centrename[API_SYSTEM_NAME_SIZE+1];
        char centretype[7];
        char numberm[3];
        char namem1[API_NODE_NAME_SIZE+1];
        char namem2[API_NODE_NAME_SIZE+1];
        int status;

        } RECSYSTEM;

#endif /* APICLIDATA_H */
