package com.cannontech.web.tools.points.model;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PointInfos {

    private PaoIdentifier paoidentifier;
    private List<PointInfoModel> points;

    public PaoIdentifier getPaoidentifier() {
        return paoidentifier;
    }

    public void setPaoidentifier(PaoIdentifier paoidentifier) {
        this.paoidentifier = paoidentifier;
    }

    public List<PointInfoModel> getPoints() {
        return points;
    }

    public void setPoints(List<PointInfoModel> points) {
        this.points = points;
    }
    
    public static PointInfos of(PaoIdentifier paoIdentifier, List<PointInfoModel> listOfPointInfoModel) {
        PointInfos pointInfos = new PointInfos();
        pointInfos.setPaoidentifier(paoIdentifier);
        pointInfos.setPoints(listOfPointInfoModel);
        return pointInfos;
    }
}
