#pragma once

#include "api_call_display.h"

extern int api_init (
		char *path_name,
		char *prog_name,
		char *api_version,
		int  priority_nbr
		);

extern int api_add_control_center (
		const char *system_name,
		const char *system_type,
		const char *hostA_name,
		const char *hostB_name
		);

extern int api_get_event (
		long *api_event
		);

extern int api_connect (
		int 	channel_id,
		char 	*operator_name,
		char 	*password,
		char 	*system_name,
		int 	access,
		int 	*groups_valid,
		int 	*system_type
		);

extern int api_get_emsproc_nbr (
		int 	channel_id,
		short 	ems_proc_count,
		char	*ems_proc_names[],
		int 	*ems_proc_nbr
		);

extern int api_disconnect (   
       		int 	channel_id, 
       		int 	calculated_valid 
               	); 

extern int api_create_group (
                int  	channel_id,
                int  	group_type,
                int  	group_number,
                int  	persistence,
		int 	priority,
                int  	object_count,
                char 	*name_list[]
	    	);

extern int api_read_def_groups (
		int 	channel_id
		);

extern int api_read_group (
		int 	channel_id,
		int 	group_type,
		int 	group_number
		);

extern int api_delete_group (
                int  	channel_id,
                int  	group_type,
                int  	group_number
	    	);

extern int api_delete_all_groups (
                int  channel_id
	    	);

extern int api_request_rts_data (
                int  channel_id,
                int  group_type,
                int  group_number,
                int  first_index,
                int  last_index
	    	);

extern int api_request_his_data (
               	int  channel_id,
               	int  group_type,
               	int  group_number,
		int  first_index,
		int  last_index,
		APICLI_TIME 	  first_time,
		int  		  delta_time,
		APICLI_TIME_UNITS delta_unit,
		int  		  slices
            	);

extern int api_request_list (
                int  channel_id,
                int  group_type,
                int  group_number,
		APICLI_TIME 	  start_time,
		APICLI_TIME 	  end_time
	    	);

extern int api_enable_cyclic (
		int	channel_id,
                int  	group_type,
                int  	group_number,
		int 			cycle_time,
		APICLI_TIME_UNITS	cycle_unit,
		int			alignment
		);

extern int api_enable_spontaneous (
		int	channel_id,
                int  	group_type,
                int  	group_number
		);

extern int api_enable_dqs (
		int	channel_id
		);

extern int api_enable_list (
				int		channel_id,
                int  	group_type,
                int  	group_number,
		APICLI_TIME 	start_time
		);

extern int api_disable_cyclic (
		int	channel_id,
                int  	group_type,
                int  	group_number
		);

extern int api_disable_spontaneous (
		int	channel_id,
                int  	group_type,
                int  	group_number
		);

extern int api_disable_all_cyclic (
		int	channel_id
		);

extern int api_disable_all_spontaneous (
		int	channel_id
		);

extern int api_disable_dqs (
		int	channel_id
		);

extern int api_disable_list (
		int	channel_id,
                int  	group_type,
                int  	group_number
		);

extern int api_get_queued_data (
                int  read_mode,
                int  block_time,
                int  *reason,
                int  *func_status,
                int  *channel_id,
                int  *group_type,
                int  *group_number,
		APICLI_TIME *group_time,
                int  *first_index,
		int  *last_index,
		int  *more,
		int  *result
		);

extern int api_unpack_indications (
		APICLI_GET_IND 	*info_ind
	    	);

extern int api_unpack_measurands (
		APICLI_GET_MEA 	*info_mea
	    	);

extern int api_unpack_counter_values (
		APICLI_GET_CNT 	*info_cnt
	    	);

extern int api_unpack_equipments (
		APICLI_GET_EQUIP  *info_equip
	    	);

extern int api_unpack_text (
		APICLI_TXT 	*info_txt
	    	);

extern int api_unpack_command (
		APICLI_GET_COM 	*info_com
	    	);

extern int api_unpack_generic (
		char		*info_generic,
		int		info_generic_size
	    	);

extern int api_unpack_groups_def (
		int 	*no_of_groups,
		int 	*group_types,
		int	*group_numbers,
		int	*group_persist,
		int	*group_priority 
	    	);

extern int api_unpack_group (
		int 	*no_of_objects,
		char 	*obj_in_group[]
	    	);

extern int api_unpack_dqs (
		int 		*sender_proc,
		int 		*dqs_length,
		APICLI_DQS_DATA *dqs_buffer
	    	);

extern int api_unpack_list (
		APICLI_GET_LIST 	*info_list
	    	);


extern int api_send_dqs (
		int	channel_id,
		int	dest_proc,
		int 	dqs_length,
		APICLI_DQS_DATA *dqs_buffer
	    	);

extern int api_write_rts_indications (
		int	channel_id,
                int  	group_type,
                int  	group_number,
                int  	first_index,
                int  	last_index,
                int  	*info_value,
                int  	*info_valid,
                API_PADQ_FLAGS  	*info_padq
	    	);

extern int api_write_rts_measurands (
		int	channel_id,
                int  	group_type,
                int  	group_number,
                int  	first_index,
                int  	last_index,
                float	*info_value,
                int  	*info_valid,
                API_PADQ_FLAGS  	*info_padq
	    	);

extern int api_write_rts_counters (
		int	channel_id,
                int  	group_type,
                int  	group_number,
                int  	first_index,
                int  	last_index,
                float	*info_value,
                int  	*info_valid,
                API_PADQ_FLAGS  	*info_padq
	    	);

extern int api_write_rts_mix (
		int	channel_id,
                int  	num_points,
                API_MIXGRP *mixobjs
	    	);

extern int api_write_rts_text (
		int	channel_id,
                int  	group_type,
                int  	group_number,
                int  	first_index,
                int  	last_index,
                APICLI_TXT	*info_txt
	    	);

extern int api_write_list (
                int  channel_id,
                int  group_type,
                int  group_number,
                int  priority,
                char *category,
                char *msg_text,
                int  acknowlegement_id
                );

extern int api_write_his_indications (
                int  channel_id,
                int  group_type,
                int  group_number,
                int  first_index,
                int  last_index,
                APICLI_TIME       first_time,
                int  		  delta_time,
                APICLI_TIME_UNITS delta_unit,
                int  		  slices,
                int  		  *info_value,
                int  		  *info_valid
           	);

extern int api_write_his_measurands (
                int  channel_id,
                int  group_type,
                int  group_number,
                int  first_index,
                int  last_index,
                APICLI_TIME       first_time,
                int  		  delta_time,
                APICLI_TIME_UNITS delta_unit,
                int  		  slices,
                float  		  *info_value,
                int    		  *info_valid
           	);

extern int api_write_his_counters (
                int  	channel_id,
                int  	group_type,
                int  	group_number,
                int  	first_index,
                int  	last_index,
                APICLI_TIME       first_time,
                int  		  delta_time,
                APICLI_TIME_UNITS delta_unit,
                int  		  slices,
                float  		  *info_value,
                int    		  *info_valid
           	);

extern int api_write_generic (
                int  	channel_id,
                int  	group_type,
                int  	group_number,
                int  	first_index,
                int  	last_index,
		int  	data_length,
		char 	*info_value
		);

extern int api_give_pulse_command (
		int	channel_id,
                int  	group_type,
                int  	group_number,
                int  	index,
                int  	direction
	    	);

extern int api_give_setpoint_command (
		int	channel_id,
                int  	group_type,
                int  	group_number,
                int  	index,
                int  	direction,
		int 	repeat_count,
		float	setpoint_value
	    	);

extern int api_check_any_quality_bit_set (
                SYS_DEP_INFO sys_dependent_info
                );

extern int api_check_quality_bit_set (
                SYS_DEP_INFO sys_dependent_info,
                int check_bit
                );

extern int api_end (void);

extern int api_filereadnames (
            	char  	*names_file_name,
		char    *names_list[],
		int     *objects_number
            	); 

extern int api_set_padq_measurands (
		int channel_id,
		int group_type,
		int group_number,
		int first_index,
		int last_index,
		float *info_value,
		int *code);

extern int api_set_padq_indications (
		int channel_id,
		int group_type,
		int group_number,
		int first_index,
		int last_index,
		int *info_value,
		int *code);

extern int api_set_padq_counters (
		int channel_id,
		int group_type,
		int group_number,
		int first_index,
		int last_index,
		float *info_value,
		int *code);

extern int api_unpack_tags (
                int *num_tags,
                char *names_list[],
                APICLI_GET_TAG *info_tag);

extern int api_write_tag(
                int channel_id,
                int group_type,
                int group_number,
                int index,
                APICLI_GET_TAG *info_tag);

extern int api_delete_tag(
                int channel_id,
                int group_type,
                int group_number,
                int index,
                APICLI_GET_TAG *info_tag);

extern int api_acknowledge_alarm (
		int channel_id,
		int group_type,
		int group_number,
		int alarm_index,
		int alarm_seq_num );

extern int api_delete_alarm (
		int channel_id,
		int group_type,
		int group_number,
		int alarm_index,
		int alarm_seq_num );

extern int api_simple_call_display (
		int channel_id,
		int system_crt_num,  /* 1, 2, ..., Max */
		int window_num,      /* 1, 2, ..., 16 */
		int honor_destination,
		char  * display_filename );

extern int api_call_display (
		int channel_id,
		int system_crt_num,  /* 1, 2, ..., Max */
		int window_num,      /* 1, 2, ..., 16 */
		API_NEW_DSP  * display_info );

