package com.cannontech.web.input;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.input.type.InputType;
import com.cannontech.web.input.validate.InputBindingErrorProcessor;
import com.cannontech.web.input.validate.InputValidator;

/**
 * Form controller for inputs. Handles the registration of property editors from
 * a list of inputs. Also manages validation calls to all inputs in the list.
 */
public abstract class InputFormController extends SimpleFormController {

    /**
     * Method to get the InputRoot to be used on a form that this controller
     * controls.
     * @return The input root for the form
     */
    public abstract InputRoot getInputRoot();

    @Override
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
            throws Exception {

        Map<String, ? extends InputSource> inputMap = getInputRoot().getInputMap();

        LiteYukonUser user = ServletUtil.getYukonUser(request);
        List<String> notEditableFields = new ArrayList<String>();

        // Initialize the binder for each input

        InputSource input = null;
        InputType inputType = null;
        for (String fieldPath : inputMap.keySet()) {

            input = inputMap.get(fieldPath);
            inputType = input.getType();

            // Register property editor for this input
            binder.registerCustomEditor(inputType.getTypeClass(),
                                        fieldPath,
                                        inputType.getPropertyEditor());

            // See if the input is editable
            if (!input.getSecurity().isEditable(user)) {
                notEditableFields.add(fieldPath);
            }

        }

        binder.setBindingErrorProcessor(new InputBindingErrorProcessor(inputMap));

        // Ignore binding for fields that are not editable
        binder.setDisallowedFields(notEditableFields.toArray(new String[] {}));

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onBindAndValidate(HttpServletRequest request, Object command,
            BindException errors) throws Exception {

        Map<String, ? extends InputSource> inputMap = getInputRoot().getInputMap();
        BeanWrapper beanWrapper = new BeanWrapperImpl(command);

        // Validate each of the input values

        InputSource input = null;
        InputType inputType = null;
        Object value = null;
        for (String fieldPath : inputMap.keySet()) {

            input = inputMap.get(fieldPath);
            inputType = input.getType();

            // Get the submitted value
            value = beanWrapper.getPropertyValue(fieldPath);

            // Validate the value
            InputValidator validator = inputType.getValidator();
            validator.validate(fieldPath, input, value, errors);

        }

    }

}
