#pragma once

#define  API_DSP_FILE_MAXC  31

typedef struct api_new_dsp
{
	unsigned short    honor_destination;
	unsigned short    context;
	unsigned char     mode_sel;
	unsigned char     sdy_mnem_sel;
	unsigned char     sdy_data_type;
	unsigned char     sdy_case_no;
	unsigned short    list_page;
	char              display_fname[API_DSP_FILE_MAXC+1];
	char              sdyc_fname[API_DSP_FILE_MAXC+1];
	char              dreq_tren[API_TRENNAME_SIZE_ELC+1];
	short             dreq_p1;
	short             dreq_p2;
	short             dreq_p3;
	unsigned char     dreq_p4;
	unsigned char     dreq_p5;
} API_NEW_DSP;

