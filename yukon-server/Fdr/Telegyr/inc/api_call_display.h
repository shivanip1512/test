/******************************************************************************
        Filename: $RCSfile: api_call_display.h,v $
        Revision: $Revision: 1.1 $
        Author:   $Author: eschmit $
        Date:     $Date: 2002/07/15 14:04:34 $
*******************************************************************************
$Log: api_call_display.h,v $
Revision 1.1  2002/07/15 14:04:34  eschmit
Telegyr stuff added

Revision 1.1  2000/12/19 14:43:52  imhoff
Initial revision

*******************************************************************************/
/*
 * 	Structure for New EMS display requests from API Client to API Server.
*/

#ifndef API_CALL_DISPLAY_H
#define API_CALL_DISPLAY_H

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

#endif /* define API_CALL_DISPLAY_H */

