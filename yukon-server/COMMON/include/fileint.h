/*-----------------------------------------------------------------------------
    Filename:  fileint.h

    Programmer:  Aaron Lauinger

    Description:    Header file for CtiFileInterface.
                    An abstract class that watches a directory for new files.
                    It calls the pure virtual function handleFile when
                    a new file is found.  Subclass should implement handleFile
                    to take the appropriate action when a file is found.
                    CtiFileInterface is 'thread-hot' in that it creates a thread
                    when the start member function is called.  This thread is
                    stopped when either the stop member function or the
                    destructor is called.


    Initial Date:  6/29/99

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#ifndef CTIFILEINTERFACE_H
#define CTIFILEINTREFACE_H
#pragma warning( disable : 4786 )


#include <iostream>

#include <rw/thr/mutex.h>
#include <rw/thr/thread.h>
#include <rw/thr/thrfunc.h>

#include "dlldefs.h"

class IM_EX_CTIBASE CtiFileInterface
{
public:
    CtiFileInterface(const std::string& dirtowatch, const std::string& extension);
    virtual ~CtiFileInterface();

    const std::string& getDirectory() const;
    const std::string& getExtension() const;
    bool getDeleteOnStart() const;

    CtiFileInterface& setDirectory(const std::string& dir);
    CtiFileInterface& setExtension(const std::string& ext);
    CtiFileInterface& setDeleteOnStart(bool del);

    //start and stop don't necessarily need to be implemented in sub-classes
    virtual void start();
    virtual void stop();

    bool isValid() const;

protected:
    virtual void handleFile(const std::string& filename) = 0;

private:
    std::string _dir;
    std::string _extension;
    bool      _delete_on_start;

    RWThread _watchthr;
    RWMutexLock _mutex;

    bool _valid;

    void _watch();

    //Don't allow these things
    CtiFileInterface(const CtiFileInterface& other) { };
    CtiFileInterface& operator=(const CtiFileInterface& right) { return *this; };
};

#endif
