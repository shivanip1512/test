
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   mc_fileint
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/mc_fileint.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2007/12/10 23:02:57 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/*-----------------------------------------------------------------------------
    Filename:  fileint.h

    Programmer:  Aaron Lauinger

    Description:    Header file for CtiMCFileInterface.
                    CtiMCFileInterface adds MACS specific functionality
                    to CtiFileInterface

    Initial Date:  6/29/99

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTIMCFILEINTERFACE_H
#define CTIMCFILEINTERFACE_H

#define MC_FILE_BUF_SIZE 100

#include <direct.h>
#include <string>

#include <rw/cstring.h>

#include "fileint.h"
#include "queue.h"

#include "mgr_mcsched.h"



class CtiMCFileInterface : public CtiFileInterface
{
public:

    CtiMCFileInterface(CtiMCScheduleManager& mgr,
                       const std::string& dir = "..\\macsftp",
                       const std::string& ext = ".txt")
    :   _schedule_manager(mgr),
        _consumed_dir("..\\macsftp\\consumed"),
        CtiFileInterface(dir, ext)
    { };

    const std::string getConsumedDirectory() const;
    CtiMCFileInterface& setConsumedDirectory(const std::string& dir);

    virtual void start();
    virtual void handleFile(const std::string& filename );

    // Messages generated from the file interface will be put in here
    void setQueue(CtiQueue< CtiMessage, std::greater<CtiMessage> >* queue );

private:

    CtiMCScheduleManager& _schedule_manager;
    CtiQueue< CtiMessage, std::greater<CtiMessage> >* _message_queue;
    std::string _consumed_dir;

    void execute(const std::string& function, const std::string& name);
};
#endif
