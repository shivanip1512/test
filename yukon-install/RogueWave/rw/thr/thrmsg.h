#if !defined(__RWTHRTHRMSG_H__)
#  define __RWTHRTHRMSG_H__
/*****************************************************************************
 *
 * thrmsg.h
 *
 * $Id$
 *
 * Copyright (c) 1996-1999 Rogue Wave Software, Inc.  All Rights Reserved.
 *
 * This computer software is owned by Rogue Wave Software, Inc. and is
 * protected by U.S. copyright laws and other laws and by international
 * treaties.  This computer software is furnished by Rogue Wave Software,
 * Inc. pursuant to a written license agreement and may be used, copied,
 * transmitted, and stored only in accordance with the terms of such
 * license and with the inclusion of the above copyright notice.  This
 * computer software or any other copies thereof may not be provided or
 * otherwise made available to any other person.
 *
 * U.S. Government Restricted Rights.  This computer software is provided
 * with Restricted Rights.  Use, duplication, or disclosure by the
 * Government is subject to restrictions as set forth in subparagraph (c)
 * (1) (ii) of The Rights in Technical Data and Computer Software clause
 * at DFARS 252.227-7013 or subparagraphs (c) (1) and (2) of the
 * Commercial Computer Software – Restricted Rights at 48 CFR 52.227-19,
 * as applicable.  Manufacturer is Rogue Wave Software, Inc., 5500
 * Flatiron Parkway, Boulder, Colorado 80301 USA.
 *
 ****************************************************************************/

/*****************************************************************************

thrmsg.h - Exception message definition macros.

*****************************************************************************/

#define RW_THR_Unexpected_Error              "Unexpected Error"
#define RW_THR_Unrecognized_Error            "Unrecognized Error"
#define RW_THR_Unexpected_Exception          "Unexpected exception"
#define RW_THR_Unrecognized_Exception        "Unrecognized exception"

#define RW_THR_Unsupported_Value             "Unsupported value"
#define RW_THR_Not_Supported                 "Operation not supported"
#define RW_THR_Not_Available                 "Operation not available"
#define RW_THR_Not_Implemented               "Operation not implemented"
#define RW_THR_Aborted                       "Operation aborted"
#define RW_THR_Terminated                    "Operation terminated"
#define RW_THR_Invalid_Usage                 "Invalid usage"
#define RW_THR_InternalError                 "Internal error"
#define RW_THR_ExternalError                 "External error"
#define RW_THR_Invalid_Pointer               "Invalid pointer"

#define RW_THR_Insufficient_Privileges       "Insufficient privileges"
#define RW_THR_Not_Owner                     "Not owner"


// Bounds

#define RW_THR_Bounds_Error                  "Bounds error"
#define RW_THR_Range                         "Value out of current allowable range"
#define RW_THR_Invalid                       "Value is invalid or unrecognized"
#define RW_THR_Unrecognized_Priority_Class   "Unrecognized scheduling or priority class"

// Resource Limits

#define RW_THR_Resource_Limit                "Resource limit"
#define RW_THR_No_Memory                     "Insufficient memory"
#define RW_THR_No_Resources                  "Insufficient system resources"
#define RW_THR_Too_Many_Threads              "Too many threads"
#define RW_THR_Too_Many_Handles              "Too many handles"
#define RW_THR_Too_Many_Requests             "Too many requests"

// Runnable 

#define RW_THR_Already_Started               "The runnable is already started"
#define RW_THR_Thread_Active                 "A thread is already active within the runnable"
#define RW_THR_Thread_Not_Active             "No thread is active within the runnable"
#define RW_THR_Not_Interrupted               "The runnable is not currently interrupted"
#define RW_THR_Illegal_Access                "Illegal access"
#define RW_THR_Illegal_Internal_Access       "External thread illegally attempted to call this internal function"
#define RW_THR_Illegal_External_Access       "Internal thread illegally attempted to call this external function"
#define RW_THR_Canceled                      "Runnable cancelled"
#define RW_THR_Invalid_Runnable_Functor      "Runnable started with invalid functor"

// Thread Stuff

#define RW_THR_Invalid_Thread_Id             "Invalid thread id"
#define RW_THR_Thread_Not_Suspended          "Thread not suspended"
#define RW_THR_Illegal_Threads_Access        "Illegal call from Threads.h++ thread"

#endif // __RWTHRTHRMSG_H__

