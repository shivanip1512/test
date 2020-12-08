package com.eaton.api.tests.v1.commchannel;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.eaton.builders.assets.commchannel.CommChannelCreateService;
import com.eaton.framework.APIs;
import com.eaton.framework.TestConstants;
import com.eaton.rest.api.common.ApiCallHelper;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class GetByIdCommChannelV1ApiTests {
    private Faker faker = new Faker();

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS, TestConstants.Features.COMM_CHANNELS })
    public void getByIdCommChannelApi_TCPPortAllFields_200Success() {
        SoftAssertions softly = new SoftAssertions();
        
        Pair<JSONObject, JSONObject> pair = CommChannelCreateService.createTCPPortAllFields();
        JSONObject createResponse = pair.getValue1();
        Integer id = createResponse.getInt("id");
        JSONObject createResponseTiming = createResponse.getJSONObject("timing");
        
        ExtractableResponse<?> response = ApiCallHelper.get(APIs.CommChannel.GET_COMM_CHANNEL + id);
        String res = response.asString();
        JSONObject json = new JSONObject(res);
        JSONObject jsonTiming = json.getJSONObject("timing");

        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertThat(json.getString("type")).isEqualTo(createResponse.getString("type"));
        softly.assertThat(json.getString("name")).isEqualTo(createResponse.getString("name"));
        softly.assertThat(json.getBoolean("enable")).isEqualTo(createResponse.getBoolean("enable"));
        softly.assertThat(json.getString("baudRate")).isEqualTo(createResponse.getString("baudRate"));
        softly.assertThat(jsonTiming.getInt("preTxWait")).isEqualTo(createResponseTiming.getInt("preTxWait"));
        softly.assertThat(jsonTiming.getInt("rtsToTxWait")).isEqualTo(createResponseTiming.getInt("rtsToTxWait"));
        softly.assertThat(jsonTiming.getInt("postTxWait")).isEqualTo(createResponseTiming.getInt("postTxWait"));
        softly.assertThat(jsonTiming.getInt("receiveDataWait")).isEqualTo(createResponseTiming.getInt("receiveDataWait"));
        softly.assertThat(jsonTiming.getInt("extraTimeOut")).isEqualTo(createResponseTiming.getInt("extraTimeOut"));
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS,
            TestConstants.Features.COMM_CHANNELS })
    public void getByIdCommChannelApi_LocalSharedPort_200Success() {
        SoftAssertions softly = new SoftAssertions();
        
        Pair<JSONObject, JSONObject> pair = CommChannelCreateService.createLocalSharedPortAllFields();
        JSONObject createResponse = pair.getValue1();
        Integer id = createResponse.getInt("id");
        JSONObject createResponseTiming = createResponse.getJSONObject("timing");
        JSONObject createResponseSharing = createResponse.getJSONObject("sharing");
        
        ExtractableResponse<?> response = ApiCallHelper.get(APIs.CommChannel.GET_COMM_CHANNEL + id);
        String res = response.asString();
        JSONObject json = new JSONObject(res);
        JSONObject jsonTiming = json.getJSONObject("timing");
        JSONObject jsonSharing = json.getJSONObject("sharing");

        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertThat(json.getString("type")).isEqualTo(createResponse.getString("type"));
        softly.assertThat(json.getString("name")).isEqualTo(createResponse.getString("name"));
        softly.assertThat(json.getBoolean("enable")).isEqualTo(createResponse.getBoolean("enable"));
        softly.assertThat(json.getString("baudRate")).isEqualTo(createResponse.getString("baudRate"));
        softly.assertThat(jsonTiming.getInt("preTxWait")).isEqualTo(createResponseTiming.getInt("preTxWait"));
        softly.assertThat(jsonTiming.getInt("rtsToTxWait")).isEqualTo(createResponseTiming.getInt("rtsToTxWait"));
        softly.assertThat(jsonTiming.getInt("postTxWait")).isEqualTo(createResponseTiming.getInt("postTxWait"));
        softly.assertThat(jsonTiming.getInt("receiveDataWait")).isEqualTo(createResponseTiming.getInt("receiveDataWait"));
        softly.assertThat(jsonTiming.getInt("extraTimeOut")).isEqualTo(createResponseTiming.getInt("extraTimeOut"));
        softly.assertThat(jsonSharing.getString("sharedPortType")).isEqualTo(createResponseSharing.getString("sharedPortType"));
        softly.assertThat(jsonSharing.getInt("sharedSocketNumber")).isEqualTo(createResponseSharing.getInt("sharedSocketNumber"));
        softly.assertThat(json.getInt("carrierDetectWaitInMilliseconds")).isEqualTo(createResponse.getInt("carrierDetectWaitInMilliseconds"));
        softly.assertThat(json.getString("protocolWrap")).isEqualTo(createResponse.getString("protocolWrap"));
        softly.assertThat(json.getString("physicalPort")).isEqualTo(createResponse.getString("physicalPort"));
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS,
            TestConstants.Features.COMM_CHANNELS })
    public void getByIdCommChannelApi_TerminalServer_200Success() {
        SoftAssertions softly = new SoftAssertions();
        
        Pair<JSONObject, JSONObject> pair = CommChannelCreateService.createTerminalServerAllFields();
        JSONObject createResponse = pair.getValue1();
        Integer id = createResponse.getInt("id");
        JSONObject createResponseTiming = createResponse.getJSONObject("timing");
        JSONObject createResponseSharing = createResponse.getJSONObject("sharing");
        
        ExtractableResponse<?> response = ApiCallHelper.get(APIs.CommChannel.GET_COMM_CHANNEL + id);
        String res = response.asString();
        JSONObject json = new JSONObject(res);

        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertThat(json).isNotNull();
        softly.assertThat(json.getInt("id")).isEqualTo(id);
        softly.assertThat(json.getString("type")).isEqualTo(createResponse.getString("type"));
        softly.assertThat(json.getString("name")).isEqualTo(createResponse.getString("name"));
        softly.assertThat(json.getBoolean("enable")).isEqualTo(createResponse.getBoolean("enable"));
        softly.assertThat(json.getString("baudRate")).isEqualTo(createResponse.getString("baudRate"));
        JSONObject jsonTiming = json.getJSONObject("timing");
        softly.assertThat(jsonTiming.getInt("preTxWait")).isEqualTo(createResponseTiming.getInt("preTxWait"));
        softly.assertThat(jsonTiming.getInt("rtsToTxWait")).isEqualTo(createResponseTiming.getInt("rtsToTxWait"));
        softly.assertThat(jsonTiming.getInt("postTxWait")).isEqualTo(createResponseTiming.getInt("postTxWait"));
        softly.assertThat(jsonTiming.getInt("receiveDataWait")).isEqualTo(createResponseTiming.getInt("receiveDataWait"));
        softly.assertThat(jsonTiming.getInt("extraTimeOut")).isEqualTo(createResponseTiming.getInt("extraTimeOut"));
        JSONObject jsonSharing = json.getJSONObject("sharing");
        softly.assertThat(jsonSharing.getString("sharedPortType")).isEqualTo(createResponseSharing.getString("sharedPortType"));
        softly.assertThat(jsonSharing.getInt("sharedSocketNumber")).isEqualTo(createResponseSharing.getInt("sharedSocketNumber"));
        softly.assertThat(json.getInt("carrierDetectWaitInMilliseconds")).isEqualTo(createResponse.getInt("carrierDetectWaitInMilliseconds"));
        softly.assertThat(json.getString("protocolWrap")).isEqualTo(createResponse.getString("protocolWrap"));
        softly.assertThat(json.getInt("portNumber")).isEqualTo(createResponse.getInt("portNumber"));
        softly.assertThat(json.getString("ipAddress")).isEqualTo(createResponse.getString("ipAddress"));
        softly.assertThat(json.length()).isEqualTo(11);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS,
            TestConstants.Features.COMM_CHANNELS })
    public void getByIdCommChannelApi_UdpTerminalServer_200Success() {
        SoftAssertions softly = new SoftAssertions();
        
        Pair<JSONObject, JSONObject> pair = CommChannelCreateService.createUdpTerminalServerAllFields();
        JSONObject createResponse = pair.getValue1();
        Integer id = createResponse.getInt("id");
        JSONObject createResponseTiming = createResponse.getJSONObject("timing");
        JSONObject createResponseSharing = createResponse.getJSONObject("sharing");
        
        ExtractableResponse<?> response = ApiCallHelper.get(APIs.CommChannel.GET_COMM_CHANNEL + id);
        String res = response.asString();
        JSONObject json = new JSONObject(res);

        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertThat(json).isNotNull();
        softly.assertThat(json.getInt("id")).isEqualTo(id);
        softly.assertThat(json.getString("type")).isEqualTo(createResponse.getString("type"));
        softly.assertThat(json.getString("name")).isEqualTo(createResponse.getString("name"));
        softly.assertThat(json.getBoolean("enable")).isEqualTo(createResponse.getBoolean("enable"));
        softly.assertThat(json.getString("baudRate")).isEqualTo(createResponse.getString("baudRate"));
        JSONObject jsonTiming = json.getJSONObject("timing");
        softly.assertThat(jsonTiming.getInt("preTxWait")).isEqualTo(createResponseTiming.getInt("preTxWait"));
        softly.assertThat(jsonTiming.getInt("rtsToTxWait")).isEqualTo(createResponseTiming.getInt("rtsToTxWait"));
        softly.assertThat(jsonTiming.getInt("postTxWait")).isEqualTo(createResponseTiming.getInt("postTxWait"));
        softly.assertThat(jsonTiming.getInt("receiveDataWait")).isEqualTo(createResponseTiming.getInt("receiveDataWait"));
        softly.assertThat(jsonTiming.getInt("extraTimeOut")).isEqualTo(createResponseTiming.getInt("extraTimeOut"));
        JSONObject jsonSharing = json.getJSONObject("sharing");
        softly.assertThat(jsonSharing.getString("sharedPortType")).isEqualTo(createResponseSharing.getString("sharedPortType"));
        softly.assertThat(jsonSharing.getInt("sharedSocketNumber")).isEqualTo(createResponseSharing.getInt("sharedSocketNumber"));
        softly.assertThat(json.getInt("carrierDetectWaitInMilliseconds")).isEqualTo(createResponse.getInt("carrierDetectWaitInMilliseconds"));
        softly.assertThat(json.getString("protocolWrap")).isEqualTo(createResponse.getString("protocolWrap"));
        softly.assertThat(json.getInt("portNumber")).isEqualTo(createResponse.getInt("portNumber"));
        softly.assertThat(json.getString("keyInHex")).isEqualTo(createResponse.getString("keyInHex"));
        softly.assertThat(json.getString("ipAddress")).isEqualTo("UDP");
        softly.assertThat(json.length()).isEqualTo(12);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ASSETS, TestConstants.Features.COMM_CHANNELS })
    public void getByIdCommChannelApi_NotFoundId_400BadRequest() {
        String nonExistingId = faker.number().digits(9);

        ExtractableResponse<?> response = ApiCallHelper.get(APIs.CommChannel.GET_COMM_CHANNEL + nonExistingId);
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.ASSETS,
            TestConstants.Features.COMM_CHANNELS })
    public void getByIdCommChannelApi_InvalidId_400BadRequest() {
        String invalidId = faker.number().digits(12);

        ExtractableResponse<?> response = ApiCallHelper.get(APIs.CommChannel.GET_COMM_CHANNEL + invalidId);

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.API, TestConstants.Features.ASSETS,
            TestConstants.Features.COMM_CHANNELS })
    public void getByIdCommChannelApi_EmptyId_404NotFound() {
    	throw new SkipException("YUK-23336");
    	
    	//ExtractableResponse<?> response = ApiCallHelper.get(APIs.CommChannel.GET_COMM_CHANNEL + "");

        //assertThat(response.statusCode()).isEqualTo(404);
    }
}
