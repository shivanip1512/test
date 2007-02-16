#ifndef _RWINUMREPLY_H_
#define _RWINUMREPLY_H_
/***************************************************************************
 *
 * nreply.h
 *
 * $Id: nreply.h@#/main/14  02/11/98 09:33:38  jc (TPR0100_WIN32_19980305)
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
#include <rw/tpslist.h>

#include <rw/toolpro/sockport.h>
#include <rw/toolpro/portstrm.h>
#include <rw/toolpro/inetaddr.h>

#include <rw/toolpro/inetdefs.h>
#include <rw/toolpro/reply.h>
#include <rw/toolpro/nreplyln.h>

class RWIFtpFSM;
class RWISmtpFSM;
class RWIHttpFSM;

/*
 * RWINumReply
 *
 * RWINumReply encapsulates a numerical protocol reply, typical
 * of many common internet protocols. A reply is first parsed
 * into RWINumReplyLine objects and then collected in an
 * RWINumReply object. RWINumReply is a composite of multiple
 * RWINumReplyLines.
 *
 */

class RWINETExport RWINumReply : public RWIReply {
  
  public:
    
    RWINumReply(void);
    // Default constructor

    RWINumReply(const RWINumReply& r);
    // Copy constructor

    RWINumReply&
    operator=(const RWINumReply& r);
    // Assignment operator
    
    virtual ~RWINumReply(void);
    // Destructor
    
    void clearAndDestroy(void);
    // clears all RWINumReplyLine objects from self
    
    RWBoolean is1XX(void) const;
    RWBoolean is2XX(void) const;
    RWBoolean is3XX(void) const;
    RWBoolean is4XX(void) const;
    RWBoolean is5XX(void) const;
    // returns true if reply is in the
    // correct family

    RWCString data(void) const;
    // returns the raw data from the first reply line.

    unsigned int code(void) const;
    // returns the numeric code of the reply lines in
    // the reply (all will be the same).
    
    int entries(void) const;
    // returns the number of RWINumReplyLine objects

    const RWINumReplyLine& operator[](size_t) const;
    // gets the ith item

  protected:

    void readFromPortal(const RWSocketPortal& portal);
    // builds from socket data

    void append(RWINumReplyLine& r);
    // appends an RWINumReplyLine

    void copy(const RWINumReply& r);
    // makes a deep copy of r to self
  
    virtual void parse(void);
    // parses out additional set meaning

  private:

    friend RWINETExportFunc(RW_SL_IO_STD(ostream)&) operator<<
	(RW_SL_IO_STD(ostream)& str, const RWINumReply& r);
    // outputs an RWINumReply to an ostream
        
  private:

    RWTPtrSlist<RWINumReplyLine> replylist_;
};

/*
 * RWIFtpReply
 *
 * RWIFtpReply is the base class for all FTP protocol replys. It
 * provides no more functionality than its direct superclass
 * RWINumReply.
 *
 */

class RWINETExport RWIFtpReply : public RWINumReply {

  friend class RWIFtpFSM;

  public:

    RWIFtpReply(void)
    {}
    // default constructor

    RWIFtpReply(const RWIFtpReply& r) : RWINumReply(r)
    {}
    // copy constructor
};

/*
 * RWISmtpReply
 *
 * RWISmtpReply is the base class for all SMTP protocol replys. It
 * provides no more functionality than its direct superclass
 * RWINumReply.
 *
 */

class RWINETExport RWISmtpReply : public RWINumReply {

  friend class RWISmtpFSM;

  public:

    RWISmtpReply(void)
    {}
    // default constructor
};

/*
 * RWIFtpPwdReply
 *
 * RWIFtpPwdReply attempts to parse the FTP protocol reply for the
 * current directory information. For instance, a common server
 * reply from the PWD command is:
 *
 *   257 "/pub" is the current directory
 *
 * The 257 numerical reply is a standardized reply as defined by the
 * RFC 765. However, the RFC doesn't state a standardized syntax for
 * returning the directory information in the text part of the
 * message. RWIFtpPwdReply examines RWINumReplyLine objects, which
 * is contained within RWIFtpPwdReply, in a last to first order
 * searching for a quoted string. As soon as a match is found, a
 * member variable is set to the path. This path can be obtained
 * with the directory member function.
 *
 * This technique is not guaranteed however because this information
 * is returned in the reply text part of the protocol reply. In
 * practice, all the ftp servers we have seen comply with this
 * practice. However for this reason, failure to parse the
 * directory from the reply does not constitute an error. In the
 * case that the directory name can not be parsed, the directory
 * method simply returns an empty string.
 *
 */

class RWINETExport RWIFtpPwdReply : public RWIFtpReply {

  friend class RWIFtpFSM;

  public:

    RWIFtpPwdReply(void);
    // Default constructor
    
    RWIFtpPwdReply(const RWIFtpPwdReply& r);
    // Copy constructor
    
    RWIFtpPwdReply&
    operator=(const RWIFtpPwdReply& r);
    // Assignment operator
        
    RWCString directory(void) const;
    // returns the directory name that was contained
    // in the reply text
    
  protected:
  
    virtual void parse(void);
    // provides an extended parse method that knows how to
    // pull the directory path out of the reply text.
  
  private:
  
    friend RWINETExportFunc(RW_SL_IO_STD(ostream)&) operator<<
	     (RW_SL_IO_STD(ostream)& str, const RWIFtpPwdReply& r);
    // outputs an RWIFtpPwdReply to an ostream
    
  private:
  
    RWCString path_;
};

/*
 * RWFtpDataReply
 *
 * RWFtpDataReply is a specialization class of RWIFtpReply.
 * It contains an RWSocketPortal for data to be read/written
 * from/to the underlying socket.
 *
 */

class RWINETExport RWIFtpDataReply : public RWIFtpReply {

  friend class RWIFtpFSM;

  public:
  
    RWIFtpDataReply(void);
    // Default constructor
    
    RWIFtpDataReply(const RWIFtpDataReply& r);
    // Copy constructor
    
    RWIFtpDataReply&
    operator=(const RWIFtpDataReply& r);
    // Assignment operator
        
    RWSocketPortal portal(void) const;
    // returns the connected data portal.

  private:

    friend RWINETExportFunc(RW_SL_IO_STD(ostream)&) operator<<
	     (RW_SL_IO_STD(ostream)& str, const RWIFtpDataReply& r);
    // outputs an RWIFtpDataReply to an ostream
  
  private:

    void setPortal(const RWSocketPortal& portal);
    // sets the data portal that will be contained within the
    // reply set.
 
    RWSocketPortal  portal_;
};

/*
 * RWIFtpPasvReply
 *
 * RWIFtpPasvReply is a specialization class of RWIFtpReply.
 * It contains the server's internet address which is used
 * by the FTP client to establish an TCP connection to
 * the FTP server.
 *
 */

class RWINETExport RWIFtpPasvReply : public RWIFtpReply {

  friend class RWIFtpFSM;

  public:
  
    RWIFtpPasvReply(void);
    // Default constructor
    
    RWInetAddr
    address(void) const;
    // Returns the address of the data port on the server.
    
  protected:
  
    virtual void parse(void);
    // provides an extended parse method that knows how to
    // pull the server data address out of the text part of
    // the reply.
  
  private:
  
    friend RWINETExportFunc(RW_SL_IO_STD(ostream)&) operator<<
     (RW_SL_IO_STD(ostream)& str, const RWIFtpPasvReply& r);
    // outputs an RWIFtpPasvReply to an ostream

  private:
  
    RWInetAddr addr_;
};

/*
 * RWISmtpDataReply
 *
 * RWISmtpDataReply is a specialization class of RWISmtpReply.
 * It contains an RWSocketPortal to write the body of the
 * mail message.
 *
 */

class RWINETExport RWISmtpDataReply : public RWISmtpReply {

  friend class RWISmtpFSM;

  public:
  
    RWISmtpDataReply(void);
    // Default constructor
    
    RWISmtpDataReply(const RWISmtpDataReply& r);
    // Copy constructor
    
    RWISmtpDataReply&
    operator=(const RWISmtpDataReply& r);
    // Assignment operator
        
    RWSocketPortal portal(void) const;
    // returns the connected data portal.
    
  private:

    void setPortal(const RWSocketPortal& portal);
    // sets the data portal that will be contained within the
    // reply set.

    RWSocketPortal portal_;
};

#endif
