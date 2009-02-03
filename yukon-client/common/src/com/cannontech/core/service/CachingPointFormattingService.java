package com.cannontech.core.service;

import com.cannontech.database.data.lite.LitePoint;

public interface CachingPointFormattingService extends PointFormattingService {
	public void addLitePointsToCache(Iterable<LitePoint> litePoints);
}
