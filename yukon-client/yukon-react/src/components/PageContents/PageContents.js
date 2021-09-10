import React from 'react';

import { useTheme } from '@material-ui/core/styles';

const PageContents = (props) => {

    const theme = useTheme();

    return (
        <div style={{...props.style, margin: theme.spacing(3)}}>
            {props.children}
        </div>
    )
}

export default PageContents;