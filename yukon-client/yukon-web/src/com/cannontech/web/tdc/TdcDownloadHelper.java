package com.cannontech.web.tdc;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.tdc.model.Column;
import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayData;
import com.cannontech.common.tdc.service.TdcService;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateValue;
import com.cannontech.web.updater.point.PointDataRegistrationService;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class TdcDownloadHelper {

    private MessageSourceAccessor accessor;
    private PointDataRegistrationService registrationService;
    private DateFormattingService dateFormattingService;
    private TdcService tdcService;
    private Display display;
    private List<DisplayData> displayData;
    private YukonUserContext context;



    public TdcDownloadHelper(MessageSourceAccessor accessor,
                             PointDataRegistrationService registrationService,
                             DateFormattingService dateFormattingService, TdcService tdcService,
                             Display display, List<DisplayData> displayData,
                             YukonUserContext context) {
        super();
        this.accessor = accessor;
        this.registrationService = registrationService;
        this.dateFormattingService = dateFormattingService;
        this.tdcService = tdcService;
        this.display = display;
        this.displayData = displayData;
        this.context = context;
    }

    public List<String> getColumnNames() {
        List<String> columnNames = Lists.transform(display.getColumns(),
                                                   new Function<Column, String>() {
                                                       @Override
                                                       public String apply(Column column) {
                                                           return column.getTitle();
                                                       }
                                                   }

            );
        return columnNames;
    }

    public List<List<String>> getDataGrid() {
        List<List<String>> dataGrid = new ArrayList<>();
        UpdateValue value;
        for (DisplayData data : displayData) {
            List<String> row = new ArrayList<>();
            for (Column column : display.getColumns()) {
                switch (column.getType()) {
                case POINT_ID:
                    row.add(String.valueOf(data.getPointId()));
                    break;
                case POINT_NAME:
                    row.add(data.getPointName());
                    break;
                case POINT_TYPE:
                    row.add(accessor.getMessage(data.getPointType()));
                    break;
                case POINT_STATE:
                    YukonMessageSourceResolvable msg =
                        new YukonMessageSourceResolvable("yukon.web.modules.tools.tdc.point.enabled."
                                                         + data.isPointEnabled());
                    row.add(accessor.getMessage(msg));
                    break;
                case DEVICE_NAME:
                    row.add(data.getDeviceName());
                    break;
                case DEVICE_TYPE:
                    row.add(data.getDevice().getDeviceType().getPaoTypeName());
                    break;
                case DEVICE_CURRENT_STATE:
                    row.add(data.getDeviceCurrentState());
                    break;
                case DEVICE_ID:
                    row.add(String.valueOf(data.getDevice().getDeviceId()));
                    break;
                case POINT_VALUE:
                    value =
                        registrationService.getLatestValue(data.getPointId(),
                                                           Format.VALUE_UNIT.name(),
                                                           context);
                    row.add(value.getValue());
                    break;
                case POINT_QUALITY:
                    value =
                        registrationService.getLatestValue(data.getPointId(),
                                                           Format.QUALITY.name(),
                                                           context);
                    row.add(value.getValue());
                    break;
                case POINT_TIME_STAMP:
                    value =
                        registrationService.getLatestValue(data.getPointId(),
                                                           Format.DATE.name(),
                                                           context);
                    row.add(value.getValue());
                    break;
                case TIME_STAMP:
                    String formattedDate =
                        dateFormattingService.format(data.getDate(), DateFormatEnum.BOTH, context);
                    row.add(formattedDate);
                    break;
                case TEXT_MESSAGE:
                    row.add(data.getTextMessage());
                    break;
                case ADDITIONAL_INFO:
                    row.add(data.getAdditionalInfo());
                    break;
                case DESCRIPTION:
                    row.add(data.getDescription());
                    break;
                case USERNAME:
                    row.add(data.getUserName());
                    break;
                case STATE:
                    row.add(tdcService.getPointState(data.getPointId()));
                    break;
                case TAG:
                    row.add(data.getTagName());
                    break;
                case U_OF_M:
                    value =
                        registrationService.getLatestValue(data.getPointId(),
                                                           Format.UNIT.name(),
                                                           context);
                    row.add(value.getValue());
                    break;
                default:
                    break;
                }
            }
            dataGrid.add(row);
        }
        return dataGrid;
    }
}
