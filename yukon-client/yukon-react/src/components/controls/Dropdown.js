import React from 'react';
import { useSelector } from 'react-redux';
import PropTypes from 'prop-types';

import { Select, MenuItem, InputLabel, FormHelperText, FormControl } from '@material-ui/core';
import { useTheme } from '@material-ui/core/styles';

import { checkForApiValidationError, displayApiValidationError } from '../../utils/Helpers';

const Dropdown = (props) => {

    const { label, name, value, items, onChange, width, style, wrapperStyle } = props;

    Dropdown.propTypes = {
        label: PropTypes.string,                //label to give the dropdown
        name: PropTypes.string,                 //name for the dropdown, should match field name for validation errors from API
        value: PropTypes.string,                //initial value for dropdown
        items: PropTypes.array.isRequired,      //array of dropdown options, should have label and value defined for each
        onChange: PropTypes.func,               //function to call on change of dropdown value
        width: PropTypes.number,                //width for dropdown
        style: PropTypes.object,                //additional styling to give dropdown
        wrapperStyle: PropTypes.object          //additional styling to give wrapper around label and dropdown
    }
    
    const theme = useTheme();

    const apiValidationErrors = useSelector(store => store.app.validationErrors);

    const checkForFieldError = () => {
        return checkForApiValidationError(name, apiValidationErrors);
    }

    const getHelperText = () => {
        return displayApiValidationError(name, apiValidationErrors);
    };

    return (
        <div style={{marginBottom: theme.spacing(2), ...wrapperStyle}}>
            <InputLabel shrink id={name}>{label}</InputLabel>
            <FormControl size="small">
                <Select 
                    value={value} 
                    name={name}
                    labelId={name}
                    variant="filled"
                    onChange={onChange}
                    style={{width: width + 'px', ...style}} 
                    error={checkForFieldError()}>
                    {items.map(item => {
                        return <MenuItem key={item.value} value={item.value}>{item.label}</MenuItem>
                    })}
                </Select>
            </FormControl>
            <FormHelperText>{getHelperText()}</FormHelperText> 
        </div>
    )
}

export default Dropdown;