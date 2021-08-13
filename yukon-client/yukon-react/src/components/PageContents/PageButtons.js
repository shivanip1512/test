import React from 'react';
import PropTypes from 'prop-types';

import { useTheme } from '@material-ui/core/styles';

import Button from '../controls/Button';

const PageButtons = (props) => {

    const { buttons } = props;

    PageButtons.propType = {
        buttons: PropTypes.array.isRequired     //array of buttons, each button should have label and either onClick or href defined
    }

    const theme = useTheme();

    return (
        <div style={{paddingTop: theme.spacing(3)}}>
            {buttons.map(function (button, index) {
                return <Button 
                            key={index}
                            label={button.label}
                            color={index === 0 ? 'primary' : 'default'}
                            onClick={button.onClick}
                            href={button.href}
                            style={{marginRight: theme.spacing(2)}}/>
            })}
        </div>
    )
}

export default PageButtons;