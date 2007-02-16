#ifndef _RWISMTPAGENTIMPL_
#define _RWISMTPAGENTIMPL_
/***************************************************************************
 *
 * smtpai.h
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
#include <rw/toolpro/smtp.h>
#include <rw/toolpro/agenti.h>

#include <rw/thr/iouescro.h>

// Keys used by the smtp implementation
#define SMTPAI_SMTPHOST_KEY    "Host"
#define SMTPAI_LOCALHOST_KEY   "LocalHost"
#define SMTPAI_TO_KEY          "To"
#define SMTPAI_FROM_KEY        "From"
#define SMTPAI_HEADER_KEY      "Headers"

// Key arguments used by the smtp implementation
#define SMTPAI_ARG_LOCALHOST   "localhost"
#define SMTPAI_ARG_DEFHEADERS  "defaultHeaders"
#define SMTPAI_ARG_NOHEADERS   "noHeaders"

/*
 * RWISmtpAgentImpl
 *
 * RWISmtpAgentImpl is an implementation class of its interface counterpart
 * RWISmtpAgent. The main functionality of RWISmtpAgentImpl is to handle
 * all SMTP agent commands dispatched from RWISmtpAgent.
 *
 */

class RWINETExport RWISmtpAgentImpl : public RWIAgentImpl {

  public:
    
    RWISmtpAgentImpl(void);
    // Default constructor
    
    virtual ~RWISmtpAgentImpl(void);
    // Destructor
    
    void
    init(void);
    // provides an initialization method used to signal
    // initial setup of property dictionary.
    
    void
    send(RWIOUEscrow<RWSocketPortal> iouEscrow);
    // performs an agent send transaction.  This involves
    // login negotiation with the SMTP server and
    // opens a data connection to a new mail message.
    // A call to dataClose must follow this method.
    
    void
    dataClose(RWIOUEscrow<RWBoolean> iouEscrow);
    // closes the mail message opened with the send
    // method.  This method must be called after a
    // call to send.  Once dataClose has been called
    // a new transaction may be made by calling send.

    virtual void setProperty(const RWCString& p, const RWCString& v);
    // Sets property p to value v.

  private: // member functions

    void
    connect(void);
    
  private: // data members

    enum { INIT_ACTION, SEND_ACTION, CLOSE_ACTION };
    // internal simple state machine states.
    
    RWISmtpClient smtpClient_;
    // smtp client that performs the work.
  
    int nextAction_;
    // next action that should be performed.

    RWBoolean connected_;
    // flag that contains control connection status.
    
    RWMutex threadMutex_;
    // exclusive thread access control. 
};

#endif
