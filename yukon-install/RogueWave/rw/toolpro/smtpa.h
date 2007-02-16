#ifndef _RWISMTPAGENT_
#define _RWISMTPAGENT_
/***************************************************************************
 *
 * smtpa.h
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

#include <rw/toolpro/sockport.h>

#include <rw/toolpro/inetdefs.h>
#include <rw/toolpro/agent.h>
#include <rw/toolpro/smtpai.h>

#include <rw/thr/ioureslt.h>

/*
 * RWISmtpAgent
 *
 * RWISmtpAgent is an easy-to-use Inter.Net.h++ class that does
 * data sending via the SMTP protocol. It handles more of the
 * details of the SMTP protocol than the RWISmtpClient class. 
 * However, it doesn't provide the flexibility of the RWISmtpClient
 * class.
 *
 * RWISmtpAgent performs actions in a transaction-based model
 * rather than the connection-based model of the RWISmtpClient.
 * The constructor stores connection information. The methods
 * perform interaction with the server by connecting, perform-
 * ing the requested action, and disconnecting with the server.
 * Multiple transactions may be performed before an RWISmtpAgent
 * object gets destroyed. Finally, the destructor cleans up
 * resources.
 *
 * An RWISmtpAgent object is lightweight. It is implemented 
 * using interface-implementation pattern. The RWISmtpAgent
 * itself is really a handle to an implementation that performs
 * the protocol interaction.
 *
 */

class RWINETExport RWISmtpAgent : public RWIAgent {

  public:

    enum headerMode { DEFAULT_HEADERS, NO_HEADERS };
    // Enums to control default headers on the mail message.
    // DEFAULT_HEADERS includes the To: and From: headers
    // in the headers section of the mail message.
    // NO_HEADERS will not include this information.  The application
    // is free to include the information however.
    
    RWISmtpAgent(void);
    // Default constructor.  Constructs an invalid object.  Use the
    // assignment operator to initialize.

    RWISmtpAgent(const RWCString& smtpHost, const RWCString& localHost = "localhost");
    // Constructor.  smtpHost is a host name or ip address of the smtp server
    // machine.  localHost is the client host.  

    RWIOUResult<RWSocketPortal>
    send(const RWCString& from, const RWCString& to, headerMode h=RWISmtpAgent::DEFAULT_HEADERS);
    // Open a data connection for a new mail message. to indicates the
    // email address of recipient and from is the email address of
    // the sender.  The header mode argument controls the addition of
    // default To: From: headers to the message.  If headers are excluded the
    // application is free to add them.  The close method must be called after
    // the send method.
    
    RWIOUResult<RWBoolean>
    dataClose(void);
    // close the body of the message
    
  private:

    RWISmtpAgentImpl*
    smtpImpl(void) const;
    // native pointer to the smtp agent implementation.

    static RWCString headerModeStr[2];
};

#endif
