import React from 'react';
import { useSelector } from 'react-redux';
import PropTypes from 'prop-types';

import { ToggleButtonGroup, ToggleButton } from '@material-ui/lab';
import { FormHelperText, InputLabel } from '@material-ui/core';
import { useTheme } from '@material-ui/core/styles';

import { displayApiValidationError } from '../../utils/Helpers';

const ToggleButtons = (props) => {

    const { label, name, value, buttons, onChange } = props;

    ToggleButtons.propTypes = {
        label: PropTypes.string,                                                        //label to give Toggle Button
        name: PropTypes.string,                                                         //name for Toggle Button, should match field name for validation errors from API
        value: PropTypes.oneOfType([PropTypes.bool, PropTypes.string]),                 //default value for Toggle Button
        buttons: PropTypes.array.isRequired,                                            //array of buttons, should have label and value defined for each
        onChange: PropTypes.func                                                        //function to call when Toggle Button is changed
    }

    const theme = useTheme();

    const apiValidationErrors = useSelector(store => store.app.validationErrors);
    
    const getHelperText = () => {
        return displayApiValidationError(name, apiValidationErrors);
    };

    return (
        <div style={{marginBottom: theme.spacing(2)}}>
            <InputLabel shrink id={label}>{label}</InputLabel>
            <ToggleButtonGroup 
                size="small" 
                exclusive={true} 
                name={name}
                value={value} 
                onChange={onChange}>
                {buttons.map(button => {
                    return <ToggleButton key={button.value} value={button.value}>{button.label}</ToggleButton>
                })};
            </ToggleButtonGroup>
            <FormHelperText>{getHelperText()}</FormHelperText>
        </div>
    )
}

export default ToggleButtons;