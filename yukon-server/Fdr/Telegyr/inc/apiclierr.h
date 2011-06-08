#pragma once

#define API_NORMAL                  	1
#define APIERR_NOTOK			-1

/* NEW MESSAGES   - SYSTEM  100+ */   
#define APIERR_GENERIC     	 	100
#define APIERR_END_PROGRAM     	 	101

#define APIERR_FATAL     	 	199
#define APIERR_FILE_OPEN     	 	102
#define APIERR_FILE_CLOSE     	 	103
#define APIERR_FILE_EXIST     	 	104
#define APIERR_FILE_NOT_EXIST     	105
#define APIERR_FILE_READ      	 	106
#define APIERR_NO_MEMORY      	 	107
#define APIERR_NUL_PTR_PAR     	 	108
#define APIERR_PROC_CRE_FAIL   	 	109
#define APIERR_FILE_WRITE   	 	110

/* NEW MESSAGES SIZE ERROR  200+ */   
#define APIERR_CLIENT_TOO_L       	200 
#define APIERR_OPERATOR_TOO_L     	201 
#define APIERR_PASSWORD_TOO_L     	202 
#define APIERR_SYSTEM_TOO_L       	203 

#define APIERR_NOTIME               	9031
#define APIERR_CONNECT              	9050
#define APIERR_DISCONNECT           	9051
#define APIERR_LAN_FAIL           	9052
#define APIERR_QUEUE_CREATE_FAIL       	9053
#define APIERR_TIM_CRE_FAIL       	9054
#define APIERR_QUEUE_FAIL       	9055
#define APIERR_QUEUE_CON_FAIL       	9056
#define APIERR_LOCAL_READ_FAIL       	9057
#define APIERR_NET_READ_FAIL       	9058
#define APIERR_LOCAL_WRT_FAIL       	9059
#define APIERR_NET_WRT_FAIL       	9060
#define APIERR_UNK_EVENT       		9061
#define APIERR_MISS_DATA       		9062
#define APIERR_MISS_DATA_LOCAL       	9065
#define APIERR_PRIMARY_DISCONNECT      	9063
#define APIERR_HSB_DISCONNECT          	9064
#define APIERR_INV_HOST_DISCONNECT      9066

#define APIERR_SYS_DBA_REA       	9100
#define APIERR_SYS_DBA_WRT       	9101
#define APIERR_SYS_DQS_SND		9102
#define APIERR_SYS_DQS_RCV		9103
	
/* CLIENT LAYER ERROR CODES 3000+ */
#define APIERR_OUT_OF_OPER    		3000
#define APIERR_UNK_SYS_NAM    		3001
#define APIERR_SYS_ALR_CON    		3002
#define APIERR_NO_CNF_FILE    		3003
#define APIERR_NO_ASC_FILE    		3004
#define APIERR_NO_HOST_NAM    		3005
#define APIERR_DSP_ENV_UNDEF            3006
#define APIERR_SRV_NOT_READY            3007

#define APIERR_TOO_MNY_CON    3030      /* Too many connect (>20)   */

#define APIERR_INV_CHAN_ID    		3050
#define APIERR_INV_CLI_ID    		3051
#define APIERR_INV_CLI_REQ    		3052
#define APIERR_INV_CLI_VER    		3054
#define APIERR_INV_GRP_TYP    		3055
#define APIERR_INV_GRP_NBR    		3056
#define APIERR_INV_OBJ_COU    		3057
#define APIERR_INV_EVENT   		3058
#define APIERR_INVALID_FUN    		3059
#define APIERR_CLI_NO_COMP    		3060

#define APIERR_INV_PAT_NAM    		3100 /* Invalid Path Name Parameter */
#define APIERR_INV_ACC_TYP    		3101 /* Invalid ACCESS Mode parameter */
#define APIERR_INV_CLI_NAM    		3102  /* Invalid CLIENT parameter */
#define APIERR_INV_OPE_NAM    		3103  /* Invalid OPERATOR parameter */
#define APIERR_INV_PASSW      		3104  /* Invalid PASSWD parameter */
#define APIERR_INV_CEN_NAM    		3105  /* Invalid CENTRE parameter */
#define APIERR_INV_COMMAND    		3106  /* Invalid Command  */
#define APIERR_INV_PRG_NAM    		3107 /* Invalid Name USER Program  */

#define APIERR_INV_TIM_ALIG    		3153
#define APIERR_INV_TIM_UNI    		3154
#define APIERR_INV_TIM_SIZ    		3155
#define APIERR_INV_VAL_COD    		3156
#define APIERR_INV_IND_VAL    		3157
#define APIERR_INV_OPE_COD    		3158
#define APIERR_INV_POL_COD    		3159
#define APIERR_INV_IDX_RAN    		3160
#define APIERR_INV_IDX_REQ    		3161
#define APIERR_INV_CYC_MOD    		3162
#define APIERR_INV_VALUE      		3163
#define APIERR_INV_DATE      		3164
#define APIERR_DATE_NOT_ARCH		3165
#define APIERR_INV_OBJ      		3166
#define APIERR_INV_CHECKSUM    		3167
#define APIERR_INV_AOR    		3168
#define APIERR_INV_CODE                 3169
#define APIERR_INV_PARAM                3170
#define APIERR_INV_TREN                 3171
#define APIERR_NO_UPDATE                3172
#define APIERR_INV_TAG_TYPE             3173
#define APIERR_BAD_TAG                  3174
#define APIERR_TAG_LIMIT                3175
#define APIERR_BAD_VALIDATE             3176
#define APIERR_TAG_LOCK                 3177
#define APIERR_SET_LOCK                 3178
#define APIERR_TAG_LINK                 3179
#define APIERR_TAG_NOTFOUND             3180
#define APIERR_BAD_TAGFIND              3181

#define APIERR_GRP_DEF_SUB    		3200
#define APIERR_GRP_DEF_MSG    		3201
#define APIERR_GRP_CEL_MSG    		3202
#define APIERR_GRP_TOO_LAR    		3203
#define APIERR_GRP_TOO_MNY    		3204
#define APIERR_GRP_NOT_FOU    		3205
#define APIERR_GRP_INV_IDX    		3206
#define APIERR_GRP_NOT_ACC    		3207
#define APIERR_GRP_HIS_WS               3208
#define APIERR_GRP_NOT_SAV              3209
#define APIERR_GRP_IS_ENA               3210
#define APIERR_GRP_NOT_ENA              3211

#define APIERR_TOO_MNY_INC    		3250
#define APIERR_BUF_TOO_SMA    		3251
#define APIERR_INV_BUF_SIZ    		3252

#define APIERR_NOT_REQ_DAT    		3300
#define APIERR_NOT_UNP_DAT    		3301
#define APIERR_NO_SUCH_INC    		3302

#define APIERR_NOT_UNP_MEA    		3350
#define APIERR_NOT_UNP_CNT    		3351
#define APIERR_NOT_UNP_IND    		3352
#define APIERR_NOT_UNP_GEN    		3353
#define APIERR_NOT_UNP_DQS    		3354
#define APIERR_NOT_UNP_LIM    		3355
#define APIERR_NOT_UNP_LST    		3356    
#define APIERR_NOT_UNP_LIST    		3357    
#define APIERR_NOT_UNP_EQUIP   		3358
#define APIERR_NOT_UNP_TAG   		3359

#define APIERR_DQS_IS_ENA               3400
#define APIERR_DQS_NOT_ENA              3401
#define APIERR_NO_SUCH_PRC              3402
#define APIERR_DQS_PRC_OFF              3403
#define APIERR_DQS_GEN_FAIL             3404
#define APIERR_APPL_NOT_CONN            3405
#define APIERR_DQSPKT_TOO_LAR           3406

#define APIERR_FAIL_PRI_WRT		3540 
#define APIERR_FAIL_HSB_WRT		3541 
#define APIERR_WRT_NOT_ALW		3542 
#define APIERR_IS_OUT_SCAN		3543 

#define APIERR_CTL_NOT_ALW		3550 
#define APIERR_CTL_IN_PRG		3551 
#define APIERR_CTL_IS_TAG		3552 
#define APIERR_CTL_PRE_OP		3553 

#define APIERR_NO_DATA			3560
#define APIERR_READ_DEF_MSG		3561 
#define APIERR_WRI_DEF_MSG		3562 


#define APIERR_NO_READ_LIST_GRP		3563
#define APIERR_FUNC_NOT_SUPPORTED	3564
#define APIERR_LIST_NOT_CMPLT		3565 

#define APIERR_INV_ALMLIST_INDEX	3600
#define APIERR_INV_ALMLIST_SEQ_NUM	3601
#define APIERR_ALARM_NOT_ACKD		3602
#define APIERR_ALARM_ALREADY_ACKD	3603
#define APIERR_ALARM_ALREADY_DELETED	3604

#define APIERR_INVALID_CRT_NUM		3700
#define APIERR_CRT_NOT_CONNECTED	3701
#define APIERR_INVALID_WINDOW_NUM	3702
#define APIERR_WINDOW_NOT_ACTIVE	3703
#define APIERR_NO_SUCH_DISPLAY_FILE	3704

#define APIERR_VALUE_OUT_OF_RANGE	3800

