import React from 'react';
import PropTypes from 'prop-types';

import { useSelector } from 'react-redux';

import { Button as MuiButton } from '@material-ui/core';

const Button = (props) => {

    const { label, color, onClick, href, style } = props;

    Button.propTypes = {
        label: PropTypes.string.isRequired,                                         //label for the Button
        color: PropTypes.oneOf(['default', 'inherit', 'primary', 'secondary']),     //color for the Button
        onClick: PropTypes.func,                                                    //function to call on click of the button
        href: PropTypes.string,                                                     //URL to link button to
        style: PropTypes.object                                                     //additional styling to give button
    }

    const yukonTheme = useSelector(store => store.app.theme);

    var themeColor = "";
    if (color === 'primary' && yukonTheme != null) {
        themeColor = yukonTheme.properties.BUTTON_COLOR;
    }

    return (
        <MuiButton 
            color={color}
            onClick={onClick}
            href={href}
            variant="contained" 
            style={{...style, backgroundColor: themeColor}}>{label}</MuiButton>
    )
}

export default Button;