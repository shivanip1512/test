#ifndef _RWIAGENT_
#define _RWIAGENT_
/***************************************************************************
 *
 * agent.h
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

#include <rw/toolpro/agenti.h>
#include <rw/toolpro/handle.h>

/*
 * RWIAgent
 *
 * RWIAgent is a class that all protocol agents are built from.
 * It is the interface part of the interface-implementation tandem,
 * with the implementation class as the RWIAgentImpl class.
 *
 * RWIAgent itself cannot be instantiated, since no public
 * constructor is provided. It is intended to be constructed
 * as the super-class portion of a specific protocol-agent
 * object.
 *
 */

class RWINETExport RWIAgent {

  protected:

    RWIAgent(void);
    // Default constructor
    
    RWIAgent(RWIAgentImpl* i);
    // Constructor, takes an implementation pointer.

  protected:

    RWNetHandle<RWIAgentImpl> impl_;
    // Implementation pointer
};

#endif
