/******************************************************************************
        Filename: $RCSfile: apicliinc.h,v $
        Revision: $Revision: 1.1 $
        Author:   $Author: eschmit $
        Date:     $Date: 2002/07/15 14:04:34 $
*******************************************************************************
$Log: apicliinc.h,v $
Revision 1.1  2002/07/15 14:04:34  eschmit
Telegyr stuff added

Revision 1.3  1999/11/03 15:52:50  imhoff
include apicli_get_list.h

Revision 1.2  1999/10/14 11:45:14  imhoff
Changes for LME ....

*******************************************************************************/
/*
 *	This file includes all the files that the api_client program 
 *      needs to include for using the API library.
 *
 *	It is the only exception to the rule that an include file
 *      should never include any other include file. 
*/
 
#ifndef API_CLIENT_INC_H
#define API_CLIENT_INC_H

#ifdef __cplusplus
extern "C" {
#endif

#include "apiclierr.h"     
#include "apiclipadq.h"     
#include "apicliconst.h"
#include "apiclidata.h"     
#include "apicli_calls.h"
#include "apicli_get_list.h"

#ifdef __cplusplus
}
#endif

#endif  /* APIINC_H */
