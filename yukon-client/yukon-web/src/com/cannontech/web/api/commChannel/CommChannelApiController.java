
package com.cannontech.web.api.commChannel;

import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.service.PortService;
import com.cannontech.common.device.port.service.impl.PortServiceImpl.CommChannelSortBy;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.spring.parameters.exceptions.InvalidSortingParametersException;

@RestController
@CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/devices/commChannels")
public class CommChannelApiController {

    @Autowired private PortService portService;
    @Autowired private PortCreateApiValidator<? extends PortBase<?>> portApiCreationValidator;
    @Autowired private PortApiValidator<? extends PortBase<?>> portApiValidator;

    //Create port
    @PostMapping
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> create(@Valid @RequestBody PortBase<?> port, YukonUserContext userContext) {
        return new ResponseEntity<>(portService.create(port, userContext.getYukonUser()), HttpStatus.CREATED);
    }

    //Get port
    @GetMapping("/{portId}")
    public ResponseEntity<Object> retrieve(@PathVariable int portId) {
        return new ResponseEntity<>(portService.retrieve(portId), HttpStatus.OK);
    }

    //Update port
    @PatchMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> update(@PathVariable("id") int portId, @Valid @RequestBody PortBase<?> port,
            YukonUserContext userContext) {
        return new ResponseEntity<>(portService.update(portId, port, userContext.getYukonUser()), HttpStatus.OK);
    }

    //Delete port
    @DeleteMapping("/{portId}")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.OWNER)
    public ResponseEntity<Object> delete(@PathVariable int portId, YukonUserContext userContext) {
        int id = portService.delete(portId, userContext.getYukonUser());
        HashMap<String, Integer> portIdMap = new HashMap<>();
        portIdMap.put("id", id);
        return new ResponseEntity<>(portIdMap, HttpStatus.OK);
    }

    //Get all ports
    @GetMapping("/")
    public ResponseEntity<Object> retrieveAllPorts() {
        List<PortBase> listOfPorts = portService.getAllPorts();
        return new ResponseEntity<>(listOfPorts, HttpStatus.OK);
    }

    //Get devices assigned to port along with sorting and paging parameters
    @GetMapping("/{portId}/devicesAssigned")
    public ResponseEntity<Object> retrieveAllDevicesForPort(@PathVariable int portId,
                                                            @DefaultSort(dir = Direction.asc, sort = "name") SortingParameters sorting,
                                                            @DefaultItemsPerPage(value = 25) PagingParameters paging) {
        
        // Fetch valid sort by
        CommChannelSortBy sortBy = getValidSortBy(sorting.getSort());
        Direction direction = sorting.getDirection();
        SearchResults<DeviceBaseModel> searchResults = portService.getDevicesAssignedPort(portId, sortBy, paging, direction);
        return new ResponseEntity<>(searchResults, HttpStatus.OK);
    }

    @InitBinder("portBase")
    public void setupBinder(WebDataBinder binder) {
        binder.addValidators(portApiValidator);

        String portId = ServletUtils.getPathVariable("id");
        if (portId == null) {
            binder.addValidators(portApiCreationValidator);
        }
    }
    
    /**
     * Get valid Sort By from request parameters
     * 
     * @throws InvalidSortingParametersException when sorting parameters is in valid
     */
    private CommChannelSortBy getValidSortBy(String sortByString) {
        try {
            return CommChannelSortBy.valueOf(sortByString);
        } catch (IllegalArgumentException e) {
            throw new InvalidSortingParametersException(sortByString + " could not be interpreted as sorting by parameter");
        }
    }
   
}
