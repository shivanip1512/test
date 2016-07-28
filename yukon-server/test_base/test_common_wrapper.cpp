#include "precompiled.h"

#include "CppUnitTest.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;
using namespace std;

#define TEST_WRAPPER(name) \
TEST_METHOD(Test##name) \
{ \
    Logger::WriteMessage("Running test_" #name ); \
    int rc = system("test_" #name ".exe"); \
    Assert::AreEqual(0, rc, L"test " #name " failed"); \
} 

namespace test_base
{
    TEST_CLASS(TestBoostWrapper)
    {
        TEST_WRAPPER(calc);
        TEST_WRAPPER(Capcontrol);
        TEST_WRAPPER(Common);
        TEST_WRAPPER(Database);
        TEST_WRAPPER(DeviceConfig);
        TEST_WRAPPER(Dispatch);
        TEST_WRAPPER(LoadManagement);
        TEST_WRAPPER(Macs);
        TEST_WRAPPER(Message);
        TEST_WRAPPER(Pil);
        TEST_WRAPPER(Porter);
        TEST_WRAPPER(Protocol);
        TEST_WRAPPER(RTDB);
        TEST_WRAPPER(Simulator);
    };
}