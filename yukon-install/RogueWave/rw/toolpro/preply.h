#ifndef _RWIPOP3REPLY_H_
#define _RWIPOP3REPLY_H_
/***************************************************************************
 *
 * preply.h
 *
 * $Id: preply.h@#/main/8  02/11/98 09:33:55  jc (TPR0100_WIN32_19980305)
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

class RWIPop3FSM;

/*
 * RWIPop3Reply
 *
 * RWIPop3Reply encapsulates common information of POP3
 * protocol replies. It is the base class for more specific
 * POP3 replies. 
 *
 */

class RWINETExport RWIPop3Reply : public RWIReply
{
  friend class RWIPop3FSM;
  
  public:
    
    RWIPop3Reply(void);
    // Default constructor
    
    RWBoolean isOk(void)  const;
    // returns TRUE if the reply parsed is positive (i.e. +OK)

    RWBoolean isErr(void) const;
    // returns TRUE if the reply parsed is negative (i.e. -ERR)

    RWCString data(void) const;
    // returns the raw data from the first reply line.
    
  protected:

    enum resultCode { CODE_UNKNOWN, CODE_OK, CODE_ERR };
    // enumerates possible reply categories
  
    void readFromPortal(const RWSocketPortal& portal);
    // builds from socket data

    virtual void parse(void);
    // parses out additional set meaning

    RWCString data_;
    // contains the raw data line.
  
  private:
  
    friend RWINETExportFunc(RW_SL_IO_STD(ostream)&) operator<<
	(RW_SL_IO_STD(ostream)& str, const RWIPop3Reply& r);
    // outputs an RWIPop3Reply to an ostream
    
  private:

    int       code_;
};

/*
 * RWIPop3ConnReply
 *
 * RWIPop3ConnReply attempts to parse the POP3 protocol reply
 * for the timestamp passed by the server. Some servers don't
 * support this option in which case the timestamp method
 * returns an empty string.
 *
 */
 
class RWINETExport RWIPop3ConnReply : public RWIPop3Reply {

  public:

    RWIPop3ConnReply(void);
    // Default constructor
            
    RWCString timestamp(void) const;
    // returns the server timestamp used for
    // authentication
    
  protected:
  
    virtual void parse(void);
    // provides an extended parse method that attempts to obtain
    // the server timestamp
   
  private:
 
    friend RWINETExportFunc(RW_SL_IO_STD(ostream)&) operator<<
     (RW_SL_IO_STD(ostream)& str, const RWIPop3ConnReply& r);
    // outputs an RWIPop3ConnReply to an ostream
    
  private:
  
    RWCString timestamp_;
};

/*
 * RWIPop3StatReply
 *
 * RWIPop3StatReply parses out the arguments returned in response
 * to the POP3 STAT command. It contains the number of messages
 * waiting in the main drop, and the number of octets consumed
 * by those messages.
 *
 */

class RWINETExport RWIPop3StatReply : public RWIPop3Reply {

  public:

    RWIPop3StatReply(void);
    // Default constructor
            
    int messages(void) const;
    // returns the number of messages waiting in the mail drop

    int octets(void) const;
    // returns the length of all waiting messages in octet
    
  private:
  
    void parse(void);
    // provides an extended parse method to obtain the number of
    // waiting messages and their total size
   
  private:
 
   friend RWINETExportFunc(RW_SL_IO_STD(ostream)&) operator<<
     (RW_SL_IO_STD(ostream)& str, const RWIPop3StatReply& r);
    // outputs an RWIPop3StatReply
    
  private:
  
    int octets_;
    int messages_;
};

/*
 * RWIPop3DataReply
 *
 * RWIPop3DataReply is a specialization class of RWIPop3Reply.
 * In addition to functionality provided by RWIPop3Reply, it
 * contains an RWSocketPortal used to obtain the data portion
 * of a message.
 *
 */

class RWINETExport RWIPop3DataReply : public RWIPop3Reply {

  friend class RWIPop3FSM;
    
  public:
  
    RWIPop3DataReply(void);
    // Default constructor
            
    RWSocketPortal portal(void) const;
    // returns the connected data portal.

    RWCString additionalData(void) const;
    // returns additional info available on a POP3 reply
    // line. If no additional data is available following
    // either +OK or -ERR, then the method returns a null
    // string.

  private:

    friend RWINETExportFunc(RW_SL_IO_STD(ostream)&) operator<<
     (RW_SL_IO_STD(ostream)& str, const RWIPop3DataReply& r);
    // outputs an RWIPop3DataReply

  private:

    void parse(void);
    // provides an extended parse method to obtain
    // additional info that follows either +OK or -ERR
    // on the reply line.

    void setPortal(const RWSocketPortal& portal);
    // sets the data portal that will be contained within the
    // reply.

    RWSocketPortal  portal_;
    RWCString additionalData_;
};
    
#endif
