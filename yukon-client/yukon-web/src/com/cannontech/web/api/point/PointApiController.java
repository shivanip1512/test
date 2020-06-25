
package com.cannontech.web.api.point;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.api.token.ApiRequestContext;
import com.cannontech.common.device.dao.DevicePointDao.SortBy;
import com.cannontech.common.device.model.DevicePointsFilter;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.points.model.PointBaseModel;
import com.cannontech.web.tools.points.service.PointEditorService;
import com.cannontech.web.tools.points.service.PointEditorService.AttachedException;
import com.cannontech.web.util.YukonUserContextResolver;

@RestController
public class PointApiController {

    @Autowired private PointEditorService pointEditorService;
    @Autowired private PointApiCreationValidator<? extends PointBaseModel<?>> pointApiCreationValidator;
    @Autowired private PointApiValidator<? extends PointBaseModel<?>> pointApiValidator;
    @Autowired private YukonUserContextResolver contextResolver;

    @PostMapping("/points")
    public ResponseEntity<Object> create(@Valid @RequestBody PointBaseModel<?> pointBase, HttpServletRequest request) {
        return new ResponseEntity<>(pointEditorService.create(pointBase, getYukonUserContext(request)), HttpStatus.OK);
    }

    @GetMapping(value = "/points/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> retrieve(@PathVariable int id) {
        return new ResponseEntity<>(pointEditorService.retrieve(id), HttpStatus.OK);
    }

    @PatchMapping("/points/{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody PointBaseModel<?> pointBase, @PathVariable("id") int id, HttpServletRequest request) {
        return new ResponseEntity<>(pointEditorService.update(id, pointBase, getYukonUserContext(request)), HttpStatus.OK);
    }

    @DeleteMapping("/points/{id}")
    public ResponseEntity<Object> delete(@PathVariable int id, HttpServletRequest request) throws AttachedException {
        return new ResponseEntity<>(pointEditorService.delete(id, getYukonUserContext(request)), HttpStatus.OK);
    }

    @GetMapping("/devices/{paoId}/points")
    public ResponseEntity<Object> getPoints(@PathVariable int paoId, @ModelAttribute("filter") DevicePointsFilter filter,
            @DefaultSort(dir = Direction.asc, sort = "pointName") SortingParameters sorting,
            @DefaultItemsPerPage(value = 250) PagingParameters paging) {

        DevicePointsSortBy sortBy = DevicePointsSortBy.valueOf(sorting.getSort());
        Direction direction = sorting.getDirection();
        return new ResponseEntity<>(pointEditorService.getDevicePointDetail(paoId, filter, direction, sortBy.getValue(), paging), HttpStatus.OK);
    }

    @InitBinder("pointBaseModel")
    public void setupBinder(WebDataBinder binder) {
        binder.addValidators(pointApiValidator);

        String pointId = ServletUtils.getPathVariable("id");
        if (pointId == null) {
            binder.addValidators(pointApiCreationValidator);
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

    public enum DevicePointsSortBy {

        pointName(SortBy.POINT_NAME),
        offset(SortBy.POINT_OFFSET),
        deviceName(SortBy.DEVICE_NAME),
        pointType(SortBy.POINT_TYPE);

        private DevicePointsSortBy(SortBy value) {
            this.value = value;
        }

        private final SortBy value;

        public SortBy getValue() {
            return value;
        }
    }

}
