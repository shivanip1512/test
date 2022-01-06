package com.cannontech.web.picker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class GatewayNMIPAddressPortPicker extends BasePicker<GatewayPickerModel> {

	@Autowired private RfnGatewayService rfnGatewayService;

    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        columns.add(new OutputColumn("gatewayName", "yukon.web.picker.pao.name"));
        columns.add(new OutputColumn("nmIpAddress", "yukon.web.modules.operator.gateways.nmipaddress"));
        columns.add(new OutputColumn("nmPort", "yukon.web.modules.operator.gateways.nmport"));

        outputColumns = Collections.unmodifiableList(columns);
    }
    
    @Override
    public final SearchResults<GatewayPickerModel> search(final String ss, int start, int count, String extraArgs,
            YukonUserContext userContext) {
        final String lcSS = ss.toLowerCase();
        Predicate<GatewayPickerModel> searchFilterPredicate = new Predicate<GatewayPickerModel>() {
            @Override
            public boolean apply(GatewayPickerModel gateway) {
                return gateway.getGatewayName().toLowerCase().contains(lcSS) ||
                		gateway.getNmIpAddress().contains(lcSS) ||
                		gateway.getNmPort().toString().contains(lcSS);
            }
        };
        List<GatewayPickerModel> notificationGroups =
            Lists.newArrayList(Iterables.filter(getAllGateways(), searchFilterPredicate));
        return SearchResults.indexBasedForWholeList(start, count, notificationGroups);
    }
    
    private List<GatewayPickerModel> getAllGateways() {
        Set<RfnGateway> allGateways = rfnGatewayService.getAllGateways();
        List<GatewayPickerModel> gateways = new ArrayList<>(allGateways.size());
        for (RfnGateway gateway : allGateways) {
        	gateways.add(new GatewayPickerModel(gateway.getId(), gateway.getName(), gateway.getData().getNmIpAddress(), gateway.getData().getNmPort()));
        }
        return gateways;
    }
    
    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }
    
    @Override
    public String getIdFieldName() {
        return "gatewayId";
    }

	@Override
	public SearchResults<GatewayPickerModel> search(Collection<Integer> initialIds, String extraArgs,
			YukonUserContext userContext) {
		return null;
	}

}
