#pragma once

#include "dlldefs.h"

#include "critical_section.h"

#include "worker_thread.h"

namespace Cti {

class IM_EX_CTIBASE FileInterface
{
public:
    FileInterface(const std::string& dirtowatch, const std::string& extension);

    void setDirectory(const std::string& dir);
    void setExtension(const std::string& ext);
    void setDeleteOnStart(bool del);

    //start and stop don't necessarily need to be implemented in sub-classes
    virtual void start();
    virtual void stop();

protected:
    virtual void handleFile(const std::string& filename) = 0;

private:
    std::string _dir;
    std::string _extension;
    bool _delete_on_start;

    Cti::WorkerThread _watchthr;
    CtiCriticalSection _mutex;

    void _watch();

    //Don't allow these things
    FileInterface(const FileInterface& other);
    FileInterface& operator=(const FileInterface& right);
};

}
