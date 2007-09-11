package com.cannontech.web.input.validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.DefaultBindingErrorProcessor;

import com.cannontech.web.input.Input;
import com.cannontech.web.input.InputGroup;
import com.cannontech.web.input.InputSource;

/**
 * Extension of DefaultBindingErrorProcessor which is used to get the bind error
 * arguments for an input.
 */
public class InputBindingErrorProcessor extends DefaultBindingErrorProcessor {

    private Map<String, Input> inputMap = new HashMap<String, Input>();

    public InputBindingErrorProcessor(List<Input> inputList) {

        for (Input input : inputList) {
            inputMap.put(input.getField(), input);
        }

    }

    @Override
    protected Object[] getArgumentsForBindError(String objectName, String field) {

        Input input = null;
        if (field.contains(".")) {

            String group = field.substring(0, field.indexOf("."));
            InputGroup groupInput = (InputGroup) inputMap.get(group);

            String path = field.substring(field.indexOf(".") + 1, field.length());

            input = getInput(path, groupInput);
        } else {
            input = inputMap.get(field);
        }

        return new Object[] { ((InputSource) input).getDisplayName() };
    }

    /**
     * Helper method to traverse a path and find the input at the end of the
     * path
     * @param path - Path to input
     * @param input - Input that contains the path to the input we're looking
     *            for
     * @return - The input at the end of the path
     */
    private Input getInput(String path, Input input) {

        Input returnInput = null;
        if (path.contains(".")) {

            String group = path.substring(0, path.indexOf("."));
            Input currentInput = ((InputGroup) input).getInputMap().get(group);

            String newPath = path.substring(path.indexOf(".") + 1, path.length());

            returnInput = getInput(newPath, currentInput);
        } else {
            returnInput = ((InputGroup) input).getInputMap().get(path);
        }

        return returnInput;

    }
}
