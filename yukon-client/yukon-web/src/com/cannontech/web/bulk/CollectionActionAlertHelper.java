package com.cannontech.web.bulk;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.alert.model.Alert;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.BaseAlert;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.util.ServletUtil;

public class CollectionActionAlertHelper {

    public static SimpleCallback<CollectionActionResult> createAlert(AlertType type, AlertService alertService,
            MessageSourceAccessor accessor, HttpServletRequest request) {

        return new SimpleCallback<CollectionActionResult>() {
            String partialUrl = ServletUtil.createSafeUrl(request, "/bulk/progressReport/detail");
            @Override
            public void handle(CollectionActionResult result) throws Exception {
                ResolvableTemplate template;
                double percentSuccess = result.getCounts().getPercentSuccess();
                if (result.isFailed()) {
                    template = new ResolvableTemplate("yukon.common.alerts.collectionActionCompletion.failed");
                    String exceptionReason = result.getExecutionExceptionText();
                    template.addData("notCompletedCount", result.getCounts().getNotCompleted());
                    template.addData("exceptionReason", exceptionReason);
                } else {
                    template = new ResolvableTemplate("yukon.common.alerts.collectionActionCompletion");
                }
                String url = partialUrl + "?key=" + result.getCacheKey();
                template.addData("url", url);
                template.addData("command", accessor.getMessage(result.getAction().getFormatKey()));
                template.addData("percentSuccess", percentSuccess);
                template.addData("completedCount", result.getCounts().getCompleted());

                Alert alert = new BaseAlert(new Date(), template) {
                    @Override
                    public AlertType getType() {
                        return type;
                    };
                };
                alertService.add(alert);
            }
        };
    }
}
