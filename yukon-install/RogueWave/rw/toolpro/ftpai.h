#ifndef _RWIFTPAGENTIMPL_
#define _RWIFTPAGENTIMPL_
/***************************************************************************
 *
 * ftpai.h
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
#include <rw/toolpro/ftp.h>
#include <rw/toolpro/agenti.h>

#include <rw/thr/iouescro.h>

// Keys used by the ftp implementation
#define FTPAI_HOST_KEY                  "Host"
#define FTPAI_USER_KEY                  "User"
#define FTPAI_PASSWORD_KEY              "Password"
#define FTPAI_FILEPATH_KEY              "FilePath"
#define FTPAI_XFERMODE_KEY              "Mode"
#define FTPAI_DATA_ACTIVE_KEY           "DataActive"
#define FTPAI_FILE_RENAME_FROM_KEY      "RenameFrom"
#define FTPAI_FILE_RENAME_TO_KEY        "RenameTo"

// Values used by the ftp implementation
#define FTPAI_VALUE_ACTIVE              "Active"
#define FTPAI_VALUE_PASSIVE             "Passive"
#define FTPAI_VALUE_LAST_XFER_MODE      "LastXferMode"
#define FTPAI_VALUE_ASCII               "A"
#define FTPAI_VALUE_BINARY              "I"

/*
 * RWIFtpAgentImpl
 *
 * RWIFtpAgentImpl is an implementation class of its interface counterpart
 * RWIFtpAgent. The main functionality of RWIFtpAgentImpl is to handle
 * all FTP agent commands dispatched from RWIFtpAgent.
 *
 */

class RWINETExport RWIFtpAgentImpl : public RWIAgentImpl {

  public:
    
    RWIFtpAgentImpl(void);
    // Default Constructor
    
    virtual ~RWIFtpAgentImpl(void);
    // Destructor
    
    void
    init(void);
    // provides the initialization method, informs the simple
    // state machine that basic agent information has been set
    // in the property dictionary.
    
    void
    put(RWIOUEscrow<RWSocketPortal> iouEscrow);
    // performs a put transaction, and opens a data connection
    // to the file to be put.
    // NOTE: a call to the dataClose method must follow.
    
    void
    get(RWIOUEscrow<RWSocketPortal> iouEscrow);
    // performs a get transaction, and opens a data connection
    // to the requested file.
    // NOTE: a call to the dataClose method must follow.

    void
    dir(RWIOUEscrow<RWSocketPortal> iouEscrow);
    // performs a dir transaction, and opens a data connection
    // to the requested directory data.
    // NOTE: a call to the dataClose method must follow.

    void
    dataClose(RWIOUEscrow<RWBoolean> iouEscrow);    
    // closes the data connection to the server.  This
    // method must be called after a call to get, put or
    // dir.  A new get, put, dir, or any other transaction can
    // be performed after the dataClose call has been made.

    void
    mkdir(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs a mkdir transaction.

    void
    rmdir(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs a rmdir transaction.

    void
    cd(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs a cd transaction.
    
    void
    pwd(RWIOUEscrow<RWIFtpPwdReply> iouEscrow);
    // performs a pwd transaction.

    void
    del(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs a delete transaction.

    void
    rename(RWIOUEscrow<RWIFtpReply> iouEscrow);
    // performs a rename transaction.

    virtual void
    setProperty(const RWCString& p, const RWCString& v);
    // Sets property p with value v
    
  private: // member functions

    void
    connect(void);
    // performs the client connect actions.
    
    void
    dataTransferMode(void);
    // sets up the data transfer mode on the server.
       
    int
    port(void);
    // returns the internal port information.

  private: // data member
 
    enum { INIT_ACTION, TRANSFER_ACTION, CLOSE_ACTION };
    // simple internal state machine states.
    
    RWIFtpClient ftpClient_;
    // ftp client class.

    int nextAction_;
    // next action that should be performed.

    RWBoolean connected_;
    // flag that contains control connection status;

    RWMutex threadMutex_;
    // exclusive thread access control.
};

#endif
