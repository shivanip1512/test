#include "yukon.h"

#include <vector>
using namespace std;

#include "thread.h"
#include "guard.h"
#include "logger.h"

// CtiLogger dout;  // Globally defined

class TestLogThread : public CtiThread
{
public:
    TestLogThread(int id, int n, long sleep) : _id(id), _n(n), _sleep(sleep) {}

    void run()
    {
        for ( int i = 0; i < _n && !isSet(SHUTDOWN); i++ )
        {
            {
                CtiLockGuard<CtiLogger> guard(dout);
                dout << "Thread# " << _id << " entry# " << i << endl;
            }
            sleep(_sleep);
         }

        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << "Thread# " << _id << " done# " << _n << endl;
        }
    }

private:
    int _id;
    int _n;
    long _sleep;
};
void main(int argc, char **argv)
{
    if ( argc != 7 )
    {
        cout << "Usage: " << endl <<
        "logtest path file loggerwriteinterval #threads #logentries millisbetweenentries" << endl <<
        "example: " << endl <<
        "logtest c:\\yukon\\log testlog 3000 2 500 20" << endl;
        return;
    }

    string path(argv[1]);
    string file(argv[2]);
    long write_interval = atol(argv[3]);
    long threads = atol(argv[4]);
    long entries = atol(argv[5]);
    long entry_interval = atol(argv[6]);

    dout.start();

    dout.setOutputPath(path);
    dout.setOutputFile(file);
    dout.setToStdOut(true);
    dout.setWriteInterval(write_interval);

    TestLogThread** v = (TestLogThread**) malloc(sizeof(TestLogThread*) * threads);

    for( int i = 0; i < threads; i++ )
    {
        TestLogThread* t = new TestLogThread(i, entries, entry_interval);
        t->start();
        v[i] = t;
    }

    for( int j = 0; j < threads; j++ )
    {
        v[j]->join();
        delete v[j];
    }

    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();


}
