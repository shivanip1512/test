#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   fdrdebuglevel
*
*    DATE: 10/25/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2004/06/15 19:33:59 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __FDRDEBUGLEVEL_H__
#define __FDRDEBUGLEVEL_H__

#define STARTUP_FDR_DEBUGLEVEL      0x00000001
#define MIN_DETAIL_FDR_DEBUGLEVEL   0x00000010  // minimum details
#define DETAIL_FDR_DEBUGLEVEL       0x00000020  // show everything in minimum plus more
#define MAJOR_DETAIL_FDR_DEBUGLEVEL 0x00000040  // show everything in detail plus more
#define ERROR_FDR_DEBUGLEVEL        0x00000100
#define EXPECTED_ERR_FDR_DEBUGLEVEL 0x00000200
#define DATABASE_FDR_DEBUGLEVEL     0x00001000

#endif
