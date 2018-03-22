package com.cannontech.web.bulk;

import java.text.DecimalFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.alert.model.Alert;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.BaseAlert;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.util.ServletUtil;

public class CollectionActionAlertHelper {

    public static SimpleCallback<CollectionActionResult> createAlert(AlertType type, AlertService alertService,
            MessageSourceAccessor accessor, HttpServletRequest request) {
        return  createAlert(type, alertService, accessor, null, request, null);
    }
    
    public static SimpleCallback<CollectionActionResult> createAlert(AlertType type, AlertService alertService,
            MessageSourceAccessor accessor, HttpServletRequest request, String detailText) {
        return  createAlert(type, alertService, accessor, null, request, detailText);
    }
    
    public static SimpleCallback<CollectionActionResult> createAlert(AlertType type, AlertService alertService,
            MessageSourceAccessor accessor, SimpleCallback<CollectionActionResult> callback, HttpServletRequest request) {
        return  createAlert(type, alertService, accessor, callback, request, null);
    }
    
    public static SimpleCallback<CollectionActionResult> createAlert(AlertType type, AlertService alertService,
            MessageSourceAccessor accessor, SimpleCallback<CollectionActionResult> callback, HttpServletRequest request, String detailText) {

        return new SimpleCallback<CollectionActionResult>() {
            String partialUrl = ServletUtil.createSafeUrl(request, "/bulk/progressReport/detail");

            @Override
            public void handle(CollectionActionResult result) throws Exception {
                ResolvableTemplate template;
                if (result.isFailed()) {
                    template = new ResolvableTemplate("yukon.common.alerts.collectionActionCompletion.failed");
                    String exceptionReason = result.getExecutionExceptionText();
                    template.addData("exceptionReason", exceptionReason);
                } else if (result.isCanceled()) {
                    template = new ResolvableTemplate("yukon.common.alerts.collectionActionCompletion.canceled");
                } else {
                    template = new ResolvableTemplate("yukon.common.alerts.collectionActionCompletion");
                }
                String url = partialUrl + "?key=" + result.getCacheKey();
                template.addData("url", url);
                template.addData("detail", detailText);
                template.addData("command", accessor.getMessage(result.getAction().getFormatKey()));
                
                DecimalFormat format = new DecimalFormat("0.#");
                StringBuilder builder = new StringBuilder();
                for (CollectionActionDetail detail : result.getAction().getDetails()) {
                    int count = result.getDeviceCollection(detail).getDeviceCount();
                    if (count > 0) {
                        builder.append(accessor.getMessage(detail) + ":" + count + " ("
                            + format.format(result.getCounts().getPercentages().get(detail)) + "%)" + "  ");
                    }
                }
                template.addData("statistics", builder.toString());

                Alert alert = new BaseAlert(new Date(), template) {
                    @Override
                    public AlertType getType() {
                        return type;
                    };
                };
                alertService.add(alert);
                if(callback != null) {
                    callback.handle(result);
                }
            }
        };
    }
}
