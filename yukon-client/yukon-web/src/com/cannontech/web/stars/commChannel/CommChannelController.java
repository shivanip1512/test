package com.cannontech.web.stars.commChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;

@Controller
@RequestMapping("/device/commChannel")
public class CommChannelController {

    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String baseKey = "yukon.web.modules.operator.commChannel.";
    private static final Logger log = YukonLogManager.getLogger(CommChannelController.class);
    @Autowired private ApiControllerHelper helper;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    @GetMapping("/list")
    public String list(ModelMap model, YukonUserContext userContext, HttpServletRequest request, FlashScope flash,
            @DefaultSort(dir = Direction.asc, sort = "type") SortingParameters sorting) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.commChannelListUrl);
            List<PortBase> commChannelList = new ArrayList<>();

            PortBase tcpPort1 = new PortBase();
            tcpPort1.setEnable(true);
            tcpPort1.setId(1);
            tcpPort1.setType(PaoType.TCPPORT);
            tcpPort1.setName("TCP Port Comm Channel1");
            commChannelList.add(tcpPort1);

            PortBase tcpPort2 = new PortBase();
            tcpPort2.setEnable(false);
            tcpPort2.setId(2);
            tcpPort2.setType(PaoType.TCPPORT);
            tcpPort2.setName("A TCP Port Comm Channel2");
            commChannelList.add(tcpPort2);

            PortBase tcpPort3 = new PortBase();
            tcpPort3.setEnable(false);
            tcpPort3.setId(3);
            tcpPort3.setType(PaoType.TCPPORT);
            tcpPort3.setName("TCP Port Comm Channel3");
            commChannelList.add(tcpPort3);

            // Sorting Logic
            CommChannelSortBy sortBy = CommChannelSortBy.valueOf(sorting.getSort());
            Direction dir = sorting.getDirection();
            Comparator<PortBase> comparator = (o1, o2) -> {
                return o1.getName().compareToIgnoreCase(o2.getName());
            };
            if (sortBy == CommChannelSortBy.type) {
                comparator = (o1, o2) -> o1.getType().compareTo(o2.getType());
            }
            if (sortBy == CommChannelSortBy.status) {
                comparator = (o1, o2) -> o1.getEnable().compareTo(o2.getEnable());
            }
            if (sorting.getDirection() == Direction.desc) {
                comparator = Collections.reverseOrder(comparator);
            }
            Collections.sort(commChannelList, comparator);

            model.addAttribute("commChannelList", commChannelList);

            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            for (CommChannelSortBy column : CommChannelSortBy.values()) {
                String text = accessor.getMessage(column);
                SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
                model.addAttribute(column.name(), col);
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
        }
        return "/commChannel/list.jsp";
    }

    public enum CommChannelSortBy implements DisplayableEnum {
        name,
        type,
        status;

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }
}
