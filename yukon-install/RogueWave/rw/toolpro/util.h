#ifndef _RWINUTIL_H_
#define _RWINUTIL_H_
/***************************************************************************
 *
 * util.h
 *
 * $Id$
 *
 * Copyright (c) 1998-1999 Rogue Wave Software, Inc.  All Rights Reserved.
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
 **************************************************************************/

#include <rw/cstring.h>
#include <rw/regexp.h>

#include <rw/toolpro/inetaddr.h>

#include <rw/toolpro/inetdefs.h>

/*
 * Utility functions for Tools.h++ Professional
 *
 */

/*
 * rwAddPeriods(RWCString)
 *
 * SMTP requires that <newline><period> be sent as 
 * <newline><period><period>. This function doubles any leading 
 *  period in the input string, and any period immediately
 * following a newline (any type).
 */

RWINETExportFunc(RWCString)   rwAddPeriods(RWCString text);

/*
 * rwRemovePeriods(RWCString)
 *
 * POP3 ships data with a period doubled if it immediately follows a 
 * newline character sequence. This function looks for double periods 
 * at the front of the string, or after any newline and removes one
 * period from any such sequence
 * 
 */

RWINETExportFunc(RWCString)   rwRemovePeriods(RWCString text);

/*
 * rwNormalizeLine(RWCString text)
 *
 * rwNormalizeLine function removes CR and LF characters embedded in 
 * an RWCString, and returns the normalized RWCString
 *
 */

RWINETExportFunc(RWCString)   rwNormalizeLine(RWCString text);

/*
 * rwFtpStringToAddr(RWCString addrString)
 *
 * rwFtpStringToAddr function converts an internet address in RWCString
 * format to RWInetAddr format.
 *
 */

RWINETExportFunc(RWInetAddr)  rwFtpStringToAddr(RWCString addrString);

/*
 * rwFtpAddrToString(const RWInetAddr& addr)
 *
 * rwFtpAddrToString function converts an RWInetAddr to RWCString format.
 *
 */

RWINETExportFunc(RWCString)   rwFtpAddrToString(const RWInetAddr& addr);

/*
 * rwDecodeString(RWCString s)
 *
 * rwDecodeString function returns a decoded RWCString.
 */

RWINETExportFunc(RWCString)   rwDecodeString(RWCString s);

#endif
