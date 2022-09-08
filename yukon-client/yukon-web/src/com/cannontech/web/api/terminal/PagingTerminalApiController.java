package com.cannontech.web.api.terminal;

import java.util.HashMap;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.device.terminal.dao.PagingTerminalDao.SortBy;
import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.common.device.terminal.model.TerminalCopy;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.api.terminal.service.PagingTerminalService;

@RestController
@RequestMapping("/terminals")
public class PagingTerminalApiController {
    @Autowired private PagingTerminalService terminalService;
    @Autowired private PagingTerminalApiCreateValidator<? extends TerminalBase<?>> createApiValidator;
    @Autowired private PagingTerminalApiValidator<? extends TerminalBase<?>> apiValidator;
    @Autowired private PagingTerminalApiCopyValidator copyApiValidator;

    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id) {
        return new ResponseEntity<>(terminalService.retrieve(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody TerminalBase terminalBase) {
        return new ResponseEntity<>(terminalService.create(terminalBase), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HashMap<String, Integer>> delete(@PathVariable int id) {
        int paoId = terminalService.delete(id);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("id", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(String name,
            @DefaultSort(dir = Direction.asc, sort = "NAME") SortingParameters sorting,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(name = "itemsPerPage", defaultValue = "250") int itemsPerPage) {
        SortBy sortBy = SortBy.valueOf(sorting.getSort());
        Direction direction = sorting.getDirection();
        return new ResponseEntity<>(terminalService.retrieveAll(sortBy, direction, pageNumber, itemsPerPage, name),
                HttpStatus.OK);
    }

    @PostMapping("/{id}/copy")
    public ResponseEntity<Object> copy(@PathVariable int id, @Valid @RequestBody TerminalCopy terminalCopy) {
        return new ResponseEntity<>(terminalService.copy(id, terminalCopy), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable int id, @Valid @RequestBody TerminalBase terminalBase) {
        return new ResponseEntity<>(terminalService.update(id, terminalBase), HttpStatus.OK);
    }

    @InitBinder("terminalBase")
    public void setupBinder(WebDataBinder binder) {
        binder.addValidators(apiValidator);

        String terminalId = ServletUtils.getPathVariable("id");
        if (terminalId == null) {
            binder.addValidators(createApiValidator);
        }
    }

    @InitBinder("terminalCopy")
    public void setupBinderCopy(WebDataBinder binder) {
        binder.addValidators(copyApiValidator);
    }
}