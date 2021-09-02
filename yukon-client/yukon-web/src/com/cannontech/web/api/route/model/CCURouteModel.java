package com.cannontech.web.api.route.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.db.route.RepeaterRoute;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
@JsonDeserialize(using = JsonDeserializer.None.class)
public class CCURouteModel<T extends CCURoute> extends RouteBaseModel<T> {

    private CarrierRouteModel carrierRoute;
    private List<RepeaterRouteModel> repeaters;

    public CarrierRouteModel getCarrierRoute() {
        if (carrierRoute == null) {
            carrierRoute = new CarrierRouteModel();
        }
        return carrierRoute;

    }

    public void setCarrierRoute(CarrierRouteModel carrierRoute) {
        this.carrierRoute = carrierRoute;
    }

    public List<RepeaterRouteModel> getRepeaters() {
        if (repeaters == null) {
            repeaters = new ArrayList<RepeaterRouteModel>();
        }
        return repeaters;
    }

    public void setRepeaters(List<RepeaterRouteModel> repeaters) {
        this.repeaters = repeaters;
    }

    @Override
    public void buildDBPersistent(T route) {

        List<RepeaterRoute> repeaterRoutesList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(getRepeaters())) {
            int i=1;
            for (RepeaterRouteModel repeaterRouteModel : getRepeaters()) {
                RepeaterRoute repeaterRoute = new RepeaterRoute();
                repeaterRouteModel.buildDBPersistent(repeaterRoute);
                if(route.getPAObjectID() != null) {
                    repeaterRoute.setRouteID(route.getPAObjectID());
                }
                repeaterRoute.setRepeaterOrder(i++);
                repeaterRoutesList.add(repeaterRoute);
            }
            route.setRepeaters(repeaterRoutesList);
            
            getCarrierRoute().buildDBPersistent(route.getCarrierRoute());
            route.setCarrierRoute(route.getCarrierRoute());

        }

        super.buildDBPersistent(route);

    }

    @Override
    public void buildModel(T route) {

        if (route.getRepeaters().size() > 0) {
            getRepeaters().clear();
            for (RepeaterRoute repeaterRoute : route.getRepeaters()) {
                RepeaterRouteModel repeaterRouteModel = new RepeaterRouteModel();
                repeaterRouteModel.buildModel(repeaterRoute);
                getRepeaters().add(repeaterRouteModel);
            }
            getCarrierRoute().buildModel(route.getCarrierRoute());
        }
        super.buildModel(route);

    }
}
