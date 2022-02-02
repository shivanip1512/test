#pragma once

#include <string>
#include <chrono>

namespace Cti {

class YukonMetricTracker
{
public:
	YukonMetricTracker(){};

	void sendYukonMetricMessage(const std::string pointInfo, int64_t value, std::chrono::system_clock::time_point timestamp);
	static const std::string RPH_INSERTS;
	static const std::string RPH_QUEUE_SIZE;

};
}