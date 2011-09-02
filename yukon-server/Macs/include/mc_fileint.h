#pragma once

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
