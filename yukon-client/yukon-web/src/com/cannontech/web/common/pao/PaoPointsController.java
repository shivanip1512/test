package com.cannontech.web.common.pao;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.meter.model.PointSortField;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.pao.service.LiteYukonPoint;
import com.cannontech.web.common.pao.service.YukonPointHelper;
import com.cannontech.web.common.sort.SortableColumn;

@Controller
@RequestMapping("/pao/*")
public class PaoPointsController {
    
    @Autowired private ServerDatabaseCache cache;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private YukonPointHelper yukonPointHelper;
    
    @GetMapping("{paoId}")
    public @ResponseBody LiteYukonPAObject pao(HttpServletResponse resp, @PathVariable int paoId) {
        
        LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);
        if (pao == null) resp.setStatus(HttpStatus.NOT_FOUND.value());
        
        return pao;
    }
   
    @GetMapping("{paoId}/points")
    public String points(ModelMap model, @PathVariable int paoId) {
        model.addAttribute("deviceId", paoId);
        LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);
        model.addAttribute("deviceName", pao.getPaoName());
        return "pao/points.jsp";
    }
    
    @GetMapping("{paoId}/points-simple")
    public String pointsSimple(ModelMap model, @PathVariable int paoId, YukonUserContext userContext,
            @DefaultSort(dir = Direction.asc, sort = "POINTNAME") SortingParameters sorting) {
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);
        List<LiteYukonPoint> liteYukonPoints = yukonPointHelper.getYukonPoints(pao, sorting, accessor);
        
        model.addAttribute("points", liteYukonPoints);
        model.addAttribute("pao", pao);
        
        buildColumn(model, accessor, PointSortField.POINTNAME, sorting);
        
        return "pao/points-simple.jsp";
    }
    
    private void buildColumn(ModelMap model, MessageSourceAccessor accessor, PointSortField field, 
            SortingParameters sorting) {
        
        Direction dir = sorting.getDirection();
        PointSortField sort = PointSortField.valueOf(sorting.getSort());
        
        String text = accessor.getMessage(field);
        boolean active = sort == field;
        SortableColumn col = SortableColumn.of(dir, active, text, field.name());
        model.addAttribute(field.name(), col);
    }
    
}