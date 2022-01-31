#pragma once
#include <boost/container/string.hpp>

namespace Cti {

class YukonMetricTracker
{
public:
	YukonMetricTracker();
	YukonMetricTracker(const std::string pointInfo, int64_t value, long long timeStamp);

	void sendYukonMetricMessage(const std::string pointInfo, int64_t value, long long timeStamp);
	static const std::string RPH_INSERTS;
	static const std::string RPH_QUEUE_SIZE;

};
}