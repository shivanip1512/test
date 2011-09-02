#pragma once

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
