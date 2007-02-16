#ifndef _RWINUMREPLYLINE_H_
#define _RWINUMREPLYLINE_H_
/***************************************************************************
 *
 * nreplyln.h
 *
 * $Id: nreplyln.h@#/main/11  02/11/98 09:33:39  jc (TPR0100_WIN32_19980305)
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

#include <rw/toolpro/inetdefs.h>

/*
 * RWINumReplyLine
 *
 * RWINumReplyLine encapsulates a single numerical protocol reply as
 * defined by the FTP and SMTP protocols.  An example reply is
 * "200 Command Ok". If the fourth character is '-', instead of ' '
 * (space character), it means that the reply consists of multiple
 * lines with the '-' as the continuation marker. The continuation
 * attribute is therefore used by the RWINumReply class to build
 * complete replies that encapsulate an entire server response.
 *
 */

class RWINETExport RWINumReplyLine
{
  public:

    enum Family { INVALID,   VALID_1XX, VALID_2XX,
                  VALID_3XX, VALID_4XX, VALID_5XX };
    // enumerates all possible reply categories
    
    RWINumReplyLine(void);
    // Default Constructor, use the set method to initialize
    
    RWINumReplyLine(const RWCString& s);
    // Constructor taking RWCString

    RWBoolean
    operator==(const RWINumReplyLine& other) const;
    // comparison operator

	// STL fix : added < operator
	RWBoolean
	operator<(const RWINumReplyLine& other) const;

    void set(const RWCString& data);
    // Parses and set member data

    void reset(void);
    // resets internal data

    unsigned int
    code(void) const;
    // returns the code part

    RWCString codeAsString(void) const;
    // returns the reply code as 3 character ascii string
    
    RWBoolean is1XX(void) const;
    RWBoolean is2XX(void) const;
    RWBoolean is3XX(void) const;
    RWBoolean is4XX(void) const;
    RWBoolean is5XX(void) const;
    // returns true if replyline is in the
    // family

    RWCString text(void) const;
    // returns the text portion of the message

    RWCString data(void) const;
    // returns the raw text line that was parsed.
    // Note: this is the only valid method to call after
    // an exception has been thrown.
    
    RWBoolean isContinued(void) const;
    // returns the reply continuation attribute.  A return
    // value of TRUE states that additional replies
    // follow this one.
    
    RWBoolean isValid(void) const;
    // returns the valid status.

  private:

    friend RWINETExportFunc(RW_SL_IO_STD(ostream)&) operator<<
	(RW_SL_IO_STD(ostream)& str, const RWINumReplyLine& rl);
    // outputs an RWINumReplyLine to an ostream 
    
  private:

    unsigned int code_;
    RWCString text_;
    RWCString num_;
    RWCString data_;
    RWBoolean continued_;
    Family    attribute_;
};

#endif
