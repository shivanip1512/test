import React from 'react';

import NavigationMenu from './NavigationMenu';
import NavigationDrawer from './NavigationDrawer';
import { ThemeProvider, createTheme } from '@material-ui/core/styles';
import * as PXBThemes from '@pxblue/react-themes';
import { DrawerLayout} from '@pxblue/react-components';
import { Provider } from 'react-redux';

import { makeStyles, useTheme } from '@material-ui/core/styles';

import { store } from '../../redux/store';

const useStyles = makeStyles(theme => ({
    drawer: {
        top: 0
    }
}));

const NavigationContainer = (props) => {
    const theme = useTheme();
    const classes = useStyles();

    return (
        <Provider store={store}>
            <ThemeProvider theme={createTheme(PXBThemes.blue)}>
                <DrawerLayout drawer={<NavigationDrawer yukonPath={props.path} reactPath={props.reactPath}/>} classes={{drawer: classes.drawer}}>
                    <NavigationMenu yukonPath={props.path} reactPath={props.reactPath}/>
                    <div id="page-contents"></div>
                </DrawerLayout>
            </ThemeProvider>
        </Provider>
    );
}

import(/* webpackChunkName: "react-dom" */'react-dom').then((ReactDom) => {
    const navigationElement = document.getElementById('navigation');
    const path = navigationElement.getAttribute('data-path');
    const reactPath = navigationElement.getAttribute('data-react-path');
    ReactDom.render(<NavigationContainer path={path} reactPath={reactPath}/>, navigationElement);
    //move contents from yukon page to inside drawer
    const header = document.getElementsByClassName('yukon-header')[0];
    const pageContent = document.getElementsByClassName('yukon-content')[0];
    const drawerLayout = document.getElementById('page-contents');
    drawerLayout.appendChild(header);
    drawerLayout.appendChild(pageContent);
    //remove temporary navigation that is used to make menu render more smoothly
    document.getElementById('navigation-temp').remove();
});