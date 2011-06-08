#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   mc_script
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/mc_script.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:59:12 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------
*
 *
 *    FILE NAME: mc_script.h
 *
 *    DATE: 3/10/2001
 *
 *    PVCS KEYWORDS:
 *    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/mc_script.h-arc  $
 *    REVISION     :  $Revision: 1.3 $
 *    DATE         :  $Date: 2002/04/16 15:59:12 $
 *
 *
 *    AUTHOR:  Aaron Lauinger
 *
 *    DESCRIPTION: Represents a MACS Script.
 *                 Supports persistence to disk and
 *                 to RogueWave streams.
 *
 *
 *    Copyright (C) 2001 Cannon Technologies, Inc.  All rights reserved.
 *---------------------------------------------------------------------------*/


#ifndef __MC_SCRIPT_H__
#define __MC_SCRIPT_H__

// Uses ANSI c file io
#include <stdio.h>

#include "mc.h"
#include "message.h"

class CtiMCScript : public CtiMessage
{
public:

    RWDECLARE_COLLECTABLE( CtiMCScript )

    CtiMCScript();
    virtual ~CtiMCScript();

    const std::string& getScriptName() const;
    CtiMCScript& setScriptName(const std::string& name);

    const std::string& getContents() const;
    CtiMCScript& setContents(const std::string& contents);

    // Reads and writes the contents respectively
    // read brings the contents into memory
    // write, writes the contents to disk
    bool readContents();
    bool writeContents();

    virtual CtiMessage* replicateMessage() const;

    virtual void restoreGuts(RWvistream &);
    virtual void saveGuts(RWvostream &) const;

    static const std::string& getScriptPath();
    static void setScriptPath(const std::string& path);

private:

    static std::string _path;
    static const unsigned _io_buf_size;

    std::string _name;
    std::string _contents;
};
#endif
