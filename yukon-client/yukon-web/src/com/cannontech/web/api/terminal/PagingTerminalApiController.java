package com.cannontech.web.api.terminal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.api.terminal.service.PagingTerminalService;

@RestController
@RequestMapping("/terminals")
public class PagingTerminalApiController {
    @Autowired private PagingTerminalService terminalService;
    @Autowired private PagingTerminalApiCreateValidator<? extends TerminalBase<?>> createApiValidator;
    @Autowired private PagingTerminalApiValidator<? extends TerminalBase<?>> apiValidator;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody TerminalBase terminalBase) {
        return new ResponseEntity<>(terminalService.create(terminalBase), HttpStatus.CREATED);
    }

    @InitBinder("terminalBase")
    public void setupBinder(WebDataBinder binder) {
        binder.addValidators(apiValidator);

        String portId = ServletUtils.getPathVariable("id");
        if (portId == null) {
            binder.addValidators(createApiValidator);
        }
    }
}