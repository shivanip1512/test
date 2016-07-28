#include "precompiled.h"

#include "CppUnitTest.h"
#include "counter.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace test_base
{
	TEST_CLASS(CtiCounterTest)
	{
	public:
		TEST_METHOD(TestCounter)
		{
			CtiCounter counter;

			counter.set(0, 0);
			Assert::AreEqual(0, counter.get(0));

			counter.inc(0);
			Assert::AreEqual(1, counter.get(0));

			counter.reset(0);
			Assert::AreEqual(0, counter.get(0));

			counter.dec(0);
			Assert::AreEqual(-1, counter.get(0));

			counter.inc(1);
			Assert::AreEqual(1, counter.get(1));

			counter.resetAll();
			Assert::AreEqual(0, counter.get(1));
		}
	};
}