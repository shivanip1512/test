#pragma once

/******************************************************************************
!
!	defines
!
******************************************************************************/

/* string length without terminating '\0' */
#define API_MAX_NOTIFYER_TYPE		10
#define API_MAX_CONTEXT			10
#define API_TRENNAME_SIZE_ELC		34
#define API_MAX_DATETIME		24
#define API_AOR_SIZE			4
#define API_LONGNAME_SIZE_ELC		132
#define API_MAX_ALARMEVENT_MSG		20
#define API_MAX_VALUE			8
#define API_MAX_CONSOLE_NAME		12
#define API_MAX_COMMENT			150
#define API_MAX_JOB_NAME		30

#define API_ALMEVT_MAX_MSGS		20   /* max. API_MAX_REQ_SIZE / sizeof(APICLI_GET_LIST) */
#define API_ALARMS_MAX_MSGS             API_ALMEVT_MAX_MSGS

#define API_CATEGORY_SIZE               8

#define API_LIST_SIZELEM		sizeof(APICLI_GET_LIST)
#define API_LIST_SIZELEM_older_V4_1	sizeof(APICLI_GET_LIST_older_V4_1)     

#define API_MAX_LIST_IN_QUEUE		ALMEVT_MAX_MSGS /* (10200) depends on ini file */

#define API_LIST_MJR 			101
#define API_ALARMS_LIST_INIT_MJR 	102
#define API_ALARMS_LIST_UPDATE_MJR 	103
#define API_ALARMS_LIST_RE_INIT_MJR 	104
#define API_ACK_DELETE_ERROR_MJR 	105

#define API_LIST_MQ_DQS_PACKET		{ API_LIST_MJR, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }


/******************************************************************************
!
!	typedefs
!
******************************************************************************/

/* place for terminating '\0' added, so that each field length is a multiple of 4 */
typedef struct APICLI_GET_LIST_older_V4_1
{
	int ptr;             /* only used from UPDATE_LIST to API_SERVER */
	char notifyer_type[API_MAX_NOTIFYER_TYPE + 2];
	char context[API_MAX_CONTEXT + 2];
	char tren[API_TRENNAME_SIZE_ELC + 2];
	int major;
	int minor;
	char event_datetime[API_MAX_DATETIME + 4];
	char aor[API_AOR_SIZE + 4];
	int alarm_priority;
	int dst_flag;
	char longname[API_LONGNAME_SIZE_ELC + 4];
	char alarm_message[API_MAX_ALARMEVENT_MSG + 4];
	char event_message[API_MAX_ALARMEVENT_MSG + 4];
	char limit[API_MAX_VALUE + 4];
	char actual_value[API_MAX_VALUE + 4];
	char console_name[API_MAX_CONSOLE_NAME + 4];
	int line_of;
	int max_lines;
	char log_datetime[API_MAX_DATETIME + 4];
	char comment_datetime[API_MAX_DATETIME + 4];
	char comment[API_MAX_COMMENT + 2];
	char job_name[API_MAX_JOB_NAME + 2];
} APICLI_GET_LIST_older_V4_1;

typedef struct APICLI_GET_LIST_
{
	int ptr;             /* only used from UPDATE_LIST to API_SERVER */
	char notifyer_type[API_MAX_NOTIFYER_TYPE + 2];
	char context[API_MAX_CONTEXT + 2];
	char tren[API_TRENNAME_SIZE_ELC + 2];
	int major;
	int minor;
	char event_datetime[API_MAX_DATETIME + 4];
	char aor[API_AOR_SIZE + 4];
	int alarm_priority;
	int dst_flag;
	char longname[API_LONGNAME_SIZE_ELC + 4];
	char alarm_message[API_MAX_ALARMEVENT_MSG + 4];
	char event_message[API_MAX_ALARMEVENT_MSG + 4];
	char limit[API_MAX_VALUE + 4];
	char actual_value[API_MAX_VALUE + 4];
	char console_name[API_MAX_CONSOLE_NAME + 4];
	int line_of;
	int max_lines;
	char log_datetime[API_MAX_DATETIME + 4];
	char comment_datetime[API_MAX_DATETIME + 4];
	char comment[API_MAX_COMMENT + 2];
	char job_name[API_MAX_JOB_NAME + 2];
		/* Items below only applicable for alarms list entries */
	short            alarm_seq_num;
	unsigned short   alarm_index;	/* Index 1 to MAX_ALARMS */
	unsigned short   alarm_index_oldest;
	unsigned short   alarm_index_newest;
	unsigned short   alarm_index_next;
	unsigned short   alarm_index_previous;
	int              alarm_unacknowledged;
} APICLI_GET_LIST;

