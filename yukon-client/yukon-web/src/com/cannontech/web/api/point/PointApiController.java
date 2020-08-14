
package com.cannontech.web.api.point;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.api.token.ApiRequestContext;
import com.cannontech.common.device.dao.DevicePointDao;
import com.cannontech.common.device.dao.DevicePointDao.SortBy;
import com.cannontech.common.device.model.DevicePointsFilter;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointTypeEditor;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.pao.service.YukonPointHelper;
import com.cannontech.web.spring.parameters.exceptions.InvalidSortingParametersException;
import com.cannontech.web.tools.points.model.LitePointModel;
import com.cannontech.web.tools.points.model.PointBaseModel;
import com.cannontech.web.tools.points.model.PointCopy;
import com.cannontech.web.tools.points.service.PointEditorService;
import com.cannontech.web.tools.points.service.PointEditorService.AttachedException;
import com.cannontech.web.util.YukonUserContextResolver;

@RestController
public class PointApiController <T extends PointBaseModel<?>> {

    @Autowired private PointEditorService pointEditorService;
    @Autowired private PointCreateApiValidator<T> pointCreateApiValidator;
    @Autowired private List<PointApiValidator<T>> pointApiValidators;
    @Autowired private PointCopyApiValidator pointCopyApiValidator;
    @Autowired private YukonUserContextResolver contextResolver;
    @Autowired private YukonPointHelper pointHelper;

    @PostMapping("/points")
    public ResponseEntity<Object> create(@Valid @RequestBody PointBaseModel<?> pointBase, HttpServletRequest request) {
        pointHelper.verifyRoles(getYukonUserContext(request).getYukonUser(), HierarchyPermissionLevel.CREATE);
        return new ResponseEntity<>(pointEditorService.create(pointBase, getYukonUserContext(request)), HttpStatus.OK);
    }

    @GetMapping(value = "/points/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> retrieve(@PathVariable int id , HttpServletRequest request) {
        pointHelper.verifyRoles(getYukonUserContext(request).getYukonUser(), HierarchyPermissionLevel.VIEW);
        return new ResponseEntity<>(pointEditorService.retrieve(id), HttpStatus.OK);
    }

    @PatchMapping("/points/{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody PointBaseModel<?> pointBase, @PathVariable("id") int id, HttpServletRequest request) {
        pointHelper.verifyRoles(getYukonUserContext(request).getYukonUser(), HierarchyPermissionLevel.UPDATE);
        return new ResponseEntity<>(pointEditorService.update(id, pointBase, getYukonUserContext(request)), HttpStatus.OK);
    }

    @DeleteMapping("/points/{id}")
    public ResponseEntity<Object> delete(@PathVariable int id, HttpServletRequest request) throws AttachedException {
        pointHelper.verifyRoles(getYukonUserContext(request).getYukonUser(), HierarchyPermissionLevel.OWNER);
        int pointId = pointEditorService.delete(id, getYukonUserContext(request));
        HashMap<String, Integer> pointIdMap = new HashMap<>();
        pointIdMap.put("id", pointId);
        return new ResponseEntity<>(pointIdMap, HttpStatus.OK);
    }

    @PostMapping("/points/{id}/copy")
    public ResponseEntity<Object> copy(@Valid @RequestBody PointCopy pointCopy, @PathVariable("id") int id, HttpServletRequest request) {
        pointHelper.verifyRoles(getYukonUserContext(request).getYukonUser(), HierarchyPermissionLevel.CREATE);
        return new ResponseEntity<>(pointEditorService.copy(id, pointCopy), HttpStatus.OK);
      
    }
    
    @GetMapping("/devices/{paoId}/points")
   public ResponseEntity<Object> getPoints(@PathVariable int paoId , 
                                           @RequestParam(value = "types", required = false) List<PointType> types,
                                           @RequestParam(value = "pointNames", required = false) List<String> pointNames,
                                           @DefaultSort(dir = Direction.asc, sort = "pointName") SortingParameters sorting,
                                           @DefaultItemsPerPage(value = 250) PagingParameters paging,
                                           HttpServletRequest request) {

       pointHelper.verifyRoles(getYukonUserContext(request).getYukonUser(), HierarchyPermissionLevel.VIEW);

       // Fetch valid sort by
       SortBy sortBy = getValidSortBy(sorting.getSort());

       DevicePointsFilter filter = new DevicePointsFilter(types, pointNames);
       Direction direction = sorting.getDirection();
       return new ResponseEntity<>(pointEditorService.getDevicePointDetail(paoId, filter, direction, sortBy, paging), HttpStatus.OK);
   }
   
    @InitBinder("pointBaseModel")
    public void setupBinder(WebDataBinder binder) {
        pointApiValidators.stream().forEach(e -> {
            if (e.supports(binder.getTarget().getClass())) {
                if (((LitePointModel) binder.getTarget()).getPointType() == PointType.CalcStatus) {
                    if (!(e.getClass().equals(StatusPointApiValidator.class))) {
                        binder.addValidators(e);
                    }
                } else {
                    binder.addValidators(e);
                }
            }
        });

        String pointId = ServletUtils.getPathVariable("id");
        if (pointId == null) {
            binder.addValidators(pointCreateApiValidator);
        }
    }

    /**
     * Get YukonUserContext from request
     */
    private YukonUserContext getYukonUserContext(HttpServletRequest request) {
        LiteYukonUser user = ApiRequestContext.getContext().getLiteYukonUser();
        YukonUserContext userContext = contextResolver.resolveContext(user, request);
        return userContext;
    }

    @Autowired
    void setValidators(List<PointApiValidator<T>> validators) {
        this.pointApiValidators = validators;
    }
    
    @InitBinder("pointCopy")
    public void setupBinderCopy(WebDataBinder binder) {
        binder.addValidators(pointCopyApiValidator);
    }

    @InitBinder("type")
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(PointType.class, new PointTypeEditor());
    }

    /**
     * Get valid Sort By from request parameters
     * 
     * @throws InvalidSortingParametersException when sorting parameters is in valid
     */
    private SortBy getValidSortBy(String sortByString) {
        try {
            return DevicePointDao.SortBy.valueOf(sortByString);
        } catch (IllegalArgumentException e) {
            throw new InvalidSortingParametersException(sortByString + " could not be interpreted as sorting by parameter");
        }
    }

}
