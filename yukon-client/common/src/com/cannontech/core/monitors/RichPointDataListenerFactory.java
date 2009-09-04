package com.cannontech.core.monitors;

import java.util.List;

import com.cannontech.core.dynamic.RichPointDataListener;

public interface RichPointDataListenerFactory {

    List<RichPointDataListener> createListeners();

}
