#include "precompiled.h"

#include "CppUnitTest.h"

#include <iostream>
#pragma message("Ignore deprecated warnings.  https://svn.boost.org/trac/boost/ticket/11954")
#include <boost/iostreams/device/file_descriptor.hpp>
#include <boost/iostreams/stream.hpp>

using namespace Microsoft::VisualStudio::CppUnitTestFramework;
using namespace std;
using namespace boost::iostreams;

/*

	This module runs the legacy Boost Unit Tests.  It runs them each as a seperate processes. 

*/

// Convenience wrapper
#define TEST_WRAPPER(name) \
TEST_METHOD(Test_##name) \
{ \
    runBoostTest(#name); \
} 

// Boost unit tests
namespace test_base
{
    TEST_CLASS(TestBoostWrapper)
    {
        TEST_WRAPPER(calc);
        TEST_WRAPPER(capcontrol);
        TEST_WRAPPER(common);
        TEST_WRAPPER(database);
        TEST_WRAPPER(deviceConfig);
        TEST_WRAPPER(dispatch);
        TEST_WRAPPER(loadManagement);
        TEST_WRAPPER(macs);
        TEST_WRAPPER(message);
        TEST_WRAPPER(pil);
        TEST_WRAPPER(porter);
        TEST_WRAPPER(protocol);
        TEST_WRAPPER(RTDB);
        TEST_WRAPPER(simulator);

		/* Creates a pipe so the child can return stdout & stderr. */
        void createStdOut(HANDLE &stdoutRead, HANDLE &stdoutWrite)
        {
            // Create a pipe to return output.
            SECURITY_ATTRIBUTES saAttr{};

            saAttr.nLength = sizeof(SECURITY_ATTRIBUTES);
            saAttr.bInheritHandle = TRUE;
            saAttr.lpSecurityDescriptor = NULL;

            // Create a pipe for the child process's STDOUT. 

            if (!CreatePipe(&stdoutRead, &stdoutWrite, &saAttr, 0))
                Assert::Fail(L"Stdout CreatePipe");

            // Ensure the read handle to the pipe for STDOUT is not inherited.
            if (!SetHandleInformation(stdoutRead, HANDLE_FLAG_INHERIT, 0))
                Assert::Fail(L"Stdout SetHandleInformation");
        }

		/* Run the boost unit test executable as a seperate process. */
        void runBoostTest(std::string name)
        {
            Logger::WriteMessage(string("Running test_" + name + "\r").c_str());

            // We run the test executable in another process, 
            //   create a pipe to handle output.
            HANDLE stdoutRead = NULL;
            HANDLE stdoutWrite = NULL;
            createStdOut(stdoutRead, stdoutWrite);

            STARTUPINFO StartupInfo{};
            PROCESS_INFORMATION ProcessInfo{};
            unsigned long rc;

			// create the command
			char cmd[1024];
			sprintf_s(cmd, sizeof cmd, "test_%s.exe", name.c_str());

			StartupInfo.cb = sizeof(STARTUPINFO);
            StartupInfo.dwFlags = STARTF_USESHOWWINDOW | STARTF_USESTDHANDLES;
            StartupInfo.wShowWindow = SW_HIDE;
            StartupInfo.hStdError = stdoutWrite;
            StartupInfo.hStdOutput = stdoutWrite;

			// Run our test
            if (!CreateProcess(NULL, cmd, NULL, NULL, TRUE,
                CREATE_NEW_CONSOLE,
                NULL,
                NULL,
                &StartupInfo,
                &ProcessInfo))
            {
                rc = GetLastError();
            }

            // Make sure we close our copy of stdout or the pipe won't close.
            CloseHandle(stdoutWrite);

            // Convert Windows HANDLE to a stream to make life easier.
            stream_buffer<file_descriptor_source> fpstream(stdoutRead, never_close_handle);
            std::istream in(&fpstream);

            // Read in stdout from the child and deliver it as test information.
            while (!in.eof())
            {
                string line;
                getline(in, line);
                Logger::WriteMessage(line.c_str());
            }

            // Clean up the last of the pipe.
            CloseHandle(stdoutRead);

            // Now we wait for the program to officially end.
            WaitForSingleObject(ProcessInfo.hProcess, INFINITE);
            if (!GetExitCodeProcess(ProcessInfo.hProcess, &rc))
            {
                rc = 0;
            }

            // Clean up out process
            CloseHandle(ProcessInfo.hThread);
            CloseHandle(ProcessInfo.hProcess);

			const std::wstring wname(name.begin(), name.end());
			Assert::AreEqual(0, (int)rc, (wstring(L"test ") + wname + wstring(L" failed")).c_str());
        }
    };
}