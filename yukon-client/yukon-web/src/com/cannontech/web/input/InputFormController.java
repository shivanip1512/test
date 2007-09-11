package com.cannontech.web.input;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.cannontech.web.input.validate.InputBindingErrorProcessor;

/**
 * Form controller for inputs. Handles the registration of property editors from
 * a list of inputs. Also manages validation calls to all inputs in the list.
 */
public abstract class InputFormController extends SimpleFormController {

    /**
     * Method to get a list of inputs to be used on a form that this controller
     * controls.
     * @return - List of inputs for the form
     */
    public abstract List<Input> getInputList();

    @Override
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
            throws Exception {

        for (Input input : getInputList()) {
            input.doRegisterEditor(binder);
        }

        binder.setBindingErrorProcessor(new InputBindingErrorProcessor(getInputList()));

    }

    @Override
    protected void onBindAndValidate(HttpServletRequest request, Object command,
            BindException errors) throws Exception {

        List<Input> inputList = getInputList();
        for (Input input : inputList) {
            input.doValidate("", command, errors);
        }
    }

}
