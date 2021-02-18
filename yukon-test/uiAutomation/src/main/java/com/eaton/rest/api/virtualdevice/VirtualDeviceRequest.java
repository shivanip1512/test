package com.eaton.rest.api.virtualdevice;

import static org.assertj.core.api.Assertions.assertThat;

import com.eaton.framework.APIs;
import com.eaton.rest.api.common.ApiCallHelper;
import io.restassured.response.ExtractableResponse;

public class VirtualDeviceRequest {
	public static ExtractableResponse<?> createVirtualDevice(Object body) {

        String pathParam = APIs.VirtualDevice.CREATE_VIRTUALDEVICE;
        ExtractableResponse<?> response = ApiCallHelper.post(pathParam, body);
        assertThat(response.statusCode()).isEqualTo(201);
        return response;
    }

    public static ExtractableResponse<?> getVirtualDevice(String virtualDeviceId) {

        String pathParam = APIs.VirtualDevice.GET_VIRTUALDEVICE + virtualDeviceId;
        ExtractableResponse<?> response = ApiCallHelper.get(pathParam);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }

    public static ExtractableResponse<?> updateVirtualDevice(String virtualDeviceId, Object body) {

        String pathParam = APIs.VirtualDevice.UPDATE_VIRTUALDEVICE + virtualDeviceId;
        ExtractableResponse<?> response = ApiCallHelper.patch(pathParam, body);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }

    public static ExtractableResponse<?> deleteVirtualDevice(String virtualDeviceId) {

        String pathParam = APIs.VirtualDevice.DELETE_VIRTUALDEVICE + virtualDeviceId;
        ExtractableResponse<?> response = ApiCallHelper.delete(pathParam);
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }
}
