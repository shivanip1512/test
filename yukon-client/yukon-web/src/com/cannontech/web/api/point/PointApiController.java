
package com.cannontech.web.api.point;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.api.token.ApiRequestContext;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointInfo;
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

    @PostMapping("/point/create")
    public ResponseEntity<Object> create(@Valid @RequestBody PointBaseModel<?> pointBase, HttpServletRequest request) {
        return new ResponseEntity<>(pointEditorService.create(pointBase, getYukonUserContext(request)), HttpStatus.OK);
    }

    @GetMapping("/point/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id) {
        return new ResponseEntity<>(pointEditorService.retrieve(id), HttpStatus.OK);
    }

    @PostMapping("/point/update/{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody PointBaseModel<?> pointBase, @PathVariable("id") int id, HttpServletRequest request) {
        return new ResponseEntity<>(pointEditorService.update(id, pointBase, getYukonUserContext(request)), HttpStatus.OK);
    }

    @DeleteMapping("/point/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable int id, HttpServletRequest request) throws AttachedException {
        YukonUserContext userContext = getYukonUserContext(request);
        //TODO pointEditorService.delete(id, userContext)
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/device/{paoId}/points")
    public ResponseEntity<Object> getPoints(@PathVariable int paoId) {
        List<PointInfo> pointInfos = new ArrayList<>();
        //TODO
        return new ResponseEntity<>(pointInfos, HttpStatus.OK);
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

}
