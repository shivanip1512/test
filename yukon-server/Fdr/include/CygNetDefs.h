#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   CygNetDefs
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:40 $
* $Workfile: CygNetDefs.h $

  Notes:

 --------------------------------------------------------------------------
 Copyright Visual Systems Inc., All Rights Reserved.
 --------------------------------------------------------------------------

 See bottom of file for revision history.

* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma once

/*************************************************************************\
    Service Message ID Ranges
\*************************************************************************/

#define CVS_MESSAGE_BASE    0
#define RSM_MESSAGE_BASE    275
#define ACS_MESSAGE_BASE    300
#define ARS_MESSAGE_BASE    512     // 0x200
#define VHS_MESSAGE_BASE    600
#define SRS_MESSAGE_BASE    700
#define CAS_MESSAGE_BASE    800
#define ALC_MESSAGE_BASE    900
#define DBS_MESSAGE_BASE    1000
#define GNS_MESSAGE_BASE    1200
#define ELS_MESSAGE_BASE    1300
#define MSS_MESSAGE_BASE    1400
#define UIS_MESSAGE_BASE    1500
#define DDS_MESSAGE_BASE    1600
#define PCS_MESSAGE_BASE    1700
#define TRS_MESSAGE_BASE    1800
#define BSS_MESSAGE_BASE    1900
#define GENSRV_MESSAGE_BASE 4200

//define VCS_MESSAGE_BASE


/*************************************************************************\
    Service Error Code Ranges
\*************************************************************************/

#define ACS_ERROR_BASE         0
#define ARS_ERROR_BASE       512    // 0x200
#define ELS_ERROR_BASE       600
#define SRS_ERROR_BASE       700
#define MSS_ERROR_BASE       800
#define ALC_ERROR_BASE       900
#define UIS_ERROR_BASE      1000
#define DDS_ERROR_BASE      2000
#define PCS_ERROR_BASE      3000
#define TRS_ERROR_BASE      4000
#define BSS_ERROR_BASE      5000
#define CAS_ERROR_BASE      8000
#define CVS_ERROR_BASE     11000

/* UNIMPLEMENTED - currently base zero
#define DBS_ERROR_BASE
#define GNS_ERROR_BASE
#define RSM_ERROR_BASE
#define VCS_ERROR_BASE
#define VHS_ERROR_BASE
*/

/*************************************************************************\
    Service Types
\*************************************************************************/

#define SERVICE_TYPE_ACS    "ACS"
#define SERVICE_TYPE_ARS    "ars"
#define SERVICE_TYPE_BSS    "BSS"
#define SERVICE_TYPE_CAS    "CAS"
#define SERVICE_TYPE_CMS    "CMS"
#define SERVICE_TYPE_CVS    "CVS"
#define SERVICE_TYPE_DBS    "DBS"
#define SERVICE_TYPE_DDS    "DDS"
#define SERVICE_TYPE_ELS    "ELS"
#define SERVICE_TYPE_GNS    "GNS"
#define SERVICE_TYPE_GNSDB  "GNSDB"
#define SERVICE_TYPE_MSS    "MSS"
#define SERVICE_TYPE_PCS    "PCS"
#define SERVICE_TYPE_RSM    "RSM"
#define SERVICE_TYPE_SRS    "SRS"
#define SERVICE_TYPE_TRS    "TRS"
#define SERVICE_TYPE_UIS    "UIS"
#define SERVICE_TYPE_VHS    "VHS"
#define SERVICE_TYPE_VCS    "VCS"
#define SERVICE_TYPE_VCCMS  "VCCMS"


/*************************************************************************\
    Common Constants
\*************************************************************************/
#define SERVICE_NAME_STRING_LENGTH (17)
#define MAX_NET_MSG_SIZE           (4096)


/*************************************************************************\
 $History: CygNetDefs.h $
 *
 * *****************  Version 9  *****************
 * User: Dwo1         Date: 8/01/00    Time: 7:50p
 * Updated in $/CygNet/Source/Support/CygNetCore
 * Added  #define for SERVICE_NAME_STRING_LENGTH.
 *
 * *****************  Version 8  *****************
 * User: Dwo1         Date: 6/06/00    Time: 8:31p
 * Updated in $/CygNet/Source/Support/CygNetCore
 * Removed .inl files per our new strategy of eliminating implementation
 * in header files.  Moved the IMPLEMENT_XXX macros into VsiClasses.h.
 *
 * *****************  Version 7  *****************
 * User: Sek1         Date: 5/01/00    Time: 2:51p
 * Updated in $/CygNet/Source/Support/CygNetCore
 * added CMS service_type
 *
 * *****************  Version 6  *****************
 * User: Sek1         Date: 4/26/00    Time: 6:11a
 * Updated in $/CygNet/Source/Support/CygNetCore
 * Added SERVICE_TYPE constants
 *
 * *****************  Version 5  *****************
 * User: Dwo1         Date: 3/23/00    Time: 4:59p
 * Updated in $/CygNet/Source/Support/CygNetCore
 * Added support for new GET_TABLE_ENTRIES message on the TRS service.
 *
 * *****************  Version 4  *****************
 * User: Dwo1         Date: 1/19/00    Time: 7:26p
 * Updated in $/CygNet/Source/Support/CygNetCore
 * Replaced DDS_GET_CONTROL_INFO msg with GET_SERVICE_CONFIG_INFO msg.
 *
 * *****************  Version 3  *****************
 * User: Dwo1         Date: 1/17/00    Time: 6:44p
 * Updated in $/CygNet/Source/Support/CygNetCore
 * Added PCS defines.
 *
 * *****************  Version 2  *****************
 * User: Jgt1         Date: 11/30/99   Time: 1:49p
 * Updated in $/CygNet/Source/Support/CygNetCore
 * Added DDS error base and message base #defines
 *
 * *****************  Version 1  *****************
 * User: Rps1         Date: 10/06/99   Time: 5:52p
 * Created in $/CygNet/Source/Support/CygNetCore
\*************************************************************************/