import React, { useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import PropTypes from 'prop-types';

import { TextField } from '@material-ui/core';
import { useTheme } from '@material-ui/core/styles';

import * as actions from '../../redux/actions/index';
import { checkForApiValidationError, displayApiValidationError } from '../../utils/Helpers';

const Input = (props) => {

    const { label, name, value, width, maxLength, style, onChange, validationSchema } = props;

    Input.propTypes = {
        label: PropTypes.string,         //i18n key for the label to give the Input
        name: PropTypes.string,             //name for Input, should match field name for validation errors from API
        value: PropTypes.string,            //default value for Input
        width: PropTypes.number,            //width for Input
        maxLength: PropTypes.number,        //max length to allow for Input
        style: PropTypes.object,            //additional styling for Input
        onChange: PropTypes.func,           //function to call on change of value
        validationSchema: PropTypes.object  //Yup Validation Schema for input
    }

    const theme = useTheme();
    const dispatch = useDispatch();

    const apiValidationErrors = useSelector(store => store.app.validationErrors);

    const [validationSchemaErrors, setValidationSchemaErrors] = useState(null);
    const [chars, setChars] = useState(value);

    const onBlurHandler = () => {
        if (validationSchema != null) {
            dispatch(actions.clearValidationErrors());
            validationSchema.validate(value, { abortEarly: false }).then(function() {
                setValidationSchemaErrors(null);
            }).catch(function(err) {
                err.inner.forEach(e => {
                    setValidationSchemaErrors(e.message);
                });
            });
        }
    }

    const checkForFieldError = () => {
        return checkForApiValidationError(name, apiValidationErrors) || validationSchemaErrors != null;
    }

    const getHelperText = () => {
        if (checkForFieldError()) {
            return displayApiValidationError(name, apiValidationErrors) || validationSchemaErrors;
        } else if (maxLength > 0) {
            const helperText = (
                <>
                <span>Max {maxLength} characters</span>
                <span style={{ float: 'right' }}>{chars.length}/{maxLength}</span>
                </>
            );
            return helperText;
        }
    };

    const onTextChangedHandler = (e) => {
        dispatch(actions.clearValidationErrors());
        setValidationSchemaErrors(null);
        onChange(e);
        setChars(e.target.value);
    }

    return (
        <TextField
            label={label}
            name={name}
            inputProps={{maxLength: maxLength}}
            variant="filled"
            size="small"
            value={value}
            style={{ width: width + 'px', marginBottom: theme.spacing(2), ...style }}
            onChange={onTextChangedHandler}
            error={checkForFieldError()}
            helperText={getHelperText()}
            onBlur={onBlurHandler}
        />
    )
}

export default Input;