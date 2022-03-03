package com.cannontech.common.device.port;

import java.io.IOException;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.model.PaoModelFactory;
import com.cannontech.common.exception.TypeNotSupportedException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.yukon.IDatabaseCache;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class JsonDeserializePaoTypeLookup extends StdDeserializer<YukonPao> {

    protected JsonDeserializePaoTypeLookup() {
        super(YukonPao.class);
    }

    private IDatabaseCache serverDatabaseCache = YukonSpringHook.getBean(IDatabaseCache.class);

    @Override
    public YukonPao deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException, TypeNotSupportedException {
        TreeNode node = parser.readValueAsTree();
        if (node == null) {
            throw new NotFoundException("request is not found in correct format");
        }
        String idStr = ServletUtils.getPathVariable("id");
        if (idStr == null) {
            TreeNode id = node.get("deviceId");
            if (id != null) {
                idStr = id.toString();
            }
        }
        Integer id = null;
        TreeNode type = node.get("deviceType");
        PaoType paoType;

        if (idStr == null) {
            paoType = getPaoTypeFromJson(type);
        } else {
            // Update case
            // if type field is present in request, Validate type.
            if (type != null) {
                getPaoTypeFromJson(type);
            }
            id = Integer.valueOf(idStr);
            paoType = getPaoTypeFromCache(idStr);
        }

        YukonPao yukonPao = (YukonPao) parser.getCodec().treeToValue(node, getYukonPaoFromModelFactory(paoType, id).getClass());
        if (yukonPao != null) {
            ((DeviceBaseModel) yukonPao).setDeviceId(id);
        }
        return yukonPao;
    }

    /**
     * Retrieves PaoType from type field provided in JSON.
     * @throws TypeNotSupportedExcpetion when invalid PaoType is provided in JSON,
     * this exception is handled by ApiExceptionHandler which will convert it into a global error.
     */
    private PaoType getPaoTypeFromJson(TreeNode type) throws TypeNotSupportedException {
        String paoTypeString = null;
        if (type != null) {
            try {
                paoTypeString = type.toString().replace("\"", "");
                return PaoType.valueOf(paoTypeString);
            } catch (IllegalArgumentException e) {
                // throw exception for invalid paoType
                throw new TypeNotSupportedException(paoTypeString + " type is not valid.");
            }
        } else {
            throw new NotFoundException("type is not found in the request.");
        }

    }

    /**
     * Retrieves PaoType from Cache based on id provided.
     */
    private PaoType getPaoTypeFromCache(String id) {
        LiteYukonPAObject pao = serverDatabaseCache.getAllPaosMap().get(Integer.valueOf(id));
        if (pao == null) {
            throw new NotFoundException("paoid "+id+" not found.") ;
        }
        return pao.getPaoType();

    }

    /**
     * Retrieves valid Yukon PAO from model factory.
     * @throws TypeNotSupportedExcpetion when a valid PaoType is provided in JSON but
     * that PaoType is not present in PaoModelFactory, this exception is handled by
     * ApiExceptionHandler which will convert it a into global error.
     */
    private YukonPao getYukonPaoFromModelFactory(PaoType paoType, Integer id) {
        YukonPao pao = PaoModelFactory.getModel(paoType);
        if (pao != null) {
            ((DeviceBaseModel) pao).setDeviceId(id);
            return pao;
        } else {
            // throw exception for not supported paoType
            throw new TypeNotSupportedException(paoType.name() + " type is not supported.");

        }
    }
}