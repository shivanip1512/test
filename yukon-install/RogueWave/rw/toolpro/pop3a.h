#ifndef _RWIPOP3AGENT_
#define _RWIPOP3AGENT_
/***************************************************************************
 *
 * pop3a.h
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
#include <rw/toolpro/pop3ai.h>

#include <rw/thr/ioureslt.h>

/*
 * RWIPop3Agent
 *
 * RWIPop3Agent is an easy-to-use Inter.Net.h++ class that does
 * basic POP3 mail access. It handles more of the details of the
 * POP3 protocol than the RWIPop3Client class. However, it
 * doesn't provide the flexibility of the RWIPop3Client class.
 *
 * RWIPop3Agent performs actions in a transaction-based model
 * rather than the connection-based model of the RWIPop3Client.
 * The constructor stores connection information. The methods
 * perform interaction with the server by connecting, perform-
 * ing the requested action, and disconnecting with the server.
 * Multiple transactions may be performed before an RWIPop3Agent
 * object gets destroyed. Finally, the destructor cleans up
 * resources.
 *
 * An RWIPop3Agent object is lightweight. It is implemented
 * using interface-implementation pattern. The RWIPop3Agent
 * itself is really a handle to an implementation that performs
 * the protocol interaction.
 *
 */

class RWINETExport RWIPop3Agent : public RWIAgent {

  public:

    RWIPop3Agent(void);
    // Default constructor.  Constructs an invalid object.  Use the
    // assignment operator to initialize.

    RWIPop3Agent(const RWCString& host, const RWCString& user, const RWCString& password);
    // Constructor.  'host' is a host name or ip address of the pop3 server
    // machine.  'user' is the user name (mailbox) name we are interested in
    // and 'password' is the password to use to gain access to the mailbox.

    RWIOUResult<int>
    messages(void);
    // Returns the number of messages waiting on the pop server.
    
    RWIOUResult<RWSocketPortal>
    get(int messageIndex);
    // Opens a data connection to requested mail message.  If the message does not
    // exist an exception is thrown.
    // The dataClose method must be called after the get method.
    
    RWIOUResult<RWIPop3Reply>
    remove(int messageIndex);
    // Deletes a message on the POP3 server. The message number must not be
    // referred to again.
    
  private:
  
    RWIPop3AgentImpl*
    pop3Impl(void) const;
    // returns native pointer to the smtp agent implementation.
};

#endif
