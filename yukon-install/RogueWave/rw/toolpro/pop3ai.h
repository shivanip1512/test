#ifndef _RWIPOP3AGENTIMPL_
#define _RWIPOP3AGENTIMPL_
/***************************************************************************
 *
 * pop3ai.h
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

#include <rw/toolpro/inetdefs.h>
#include <rw/toolpro/pop3.h>
#include <rw/toolpro/agenti.h>

#include <rw/thr/iouescro.h>

// Keys used for pop3 property dictionary
#define POP3AI_POPHOST_KEY     "Host"
#define POP3AI_USER_KEY        "User"
#define POP3AI_PASS_KEY        "Pass"
#define POP3AI_INDEX_KEY       "Index"

/*
 * RWIPop3AgentImpl
 *
 * RWIPop3AgentImpl is an implementation class of its interface counterpart
 * RWIPop3Agent. The main functionality of RWIPop3AgentImpl is to handle
 * all POP3 agent commands dispatched from RWIPop3Agent.
 *
 */

class RWINETExport RWIPop3AgentImpl : public RWIAgentImpl {

  public:
    
    RWIPop3AgentImpl(void);
    // Default constructor
    
    virtual ~RWIPop3AgentImpl(void);
    // Destructor
    
    void
    init(void);
    // informs that stat machine that initial data
    // has been set in the propery dictionary.
    
    void
    messages(RWIOUEscrow<int> iouEscrow);
    // connects to the pop3 server (if not already
    // connected) and askes for the number of messages
    // being held for the user.
    
    void
    get(RWIOUEscrow<RWSocketPortal> iouEscrow);
    // opens a data connection to the specified mail
    // message.
    
    void
    remove(RWIOUEscrow<RWIPop3Reply> iouEscrow);
    // removes a message from the mail drop
    
    virtual void
    setProperty(const RWCString& p, const RWCString& v);
    // sets properties
    
  private:

    enum { INIT_ACTION, DATA_ACTION };
    // states used by the simple state machine
    
    void
    connect(void);
    // negotiates a connection with
    // the pop3 server.

  private:
    
    RWIPop3Client pop3Client_;
    // The pop3Client that does all the work for us.
    
    int nextAction_;
    // The next valid action to be performed.
    
    RWBoolean connected_;
    // Flag that contains connected status.
    
    RWMutex threadMutex_;
    // controls thread access to the implementation.
};

#endif
