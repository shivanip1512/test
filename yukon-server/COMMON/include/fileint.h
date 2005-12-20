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
using std::string;


#include <rw/thr/mutex.h>
#include <rw/thr/thread.h>
#include <rw/thr/thrfunc.h>

#include "dlldefs.h"

class IM_EX_CTIBASE CtiFileInterface
{
public:
    CtiFileInterface(const string& dirtowatch, const string& extension);
    virtual ~CtiFileInterface();

    const string& getDirectory() const;
    const string& getExtension() const;
    bool getDeleteOnStart() const;

    CtiFileInterface& setDirectory(const string& dir);
    CtiFileInterface& setExtension(const string& ext);
    CtiFileInterface& setDeleteOnStart(bool del);

    //start and stop don't necessarily need to be implemented in sub-classes
    virtual void start();
    virtual void stop();

    bool isValid() const;

protected:
    virtual void handleFile(const string& filename) = 0;

private:
    string _dir;
    string _extension;
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
