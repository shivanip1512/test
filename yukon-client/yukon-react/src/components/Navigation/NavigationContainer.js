import React, { Suspense } from "react";

import NavigationMenu from "./NavigationMenu";
import NavigationDrawer from "./NavigationDrawer";
import { ThemeProvider, createTheme } from "@material-ui/core/styles";
import * as PXBThemes from "@brightlayer-ui/react-themes";
import { DrawerLayout } from "@brightlayer-ui/react-components";
import { Provider } from "react-redux";

import { I18nextProvider } from "react-i18next";
import { SecurityContextProvider, AuthNavigationContainer } from "@brightlayer-ui/react-auth-workflow";
import AuthUIConfiguration from "../security/AuthUIConfiguration";
import { routes } from "../../constants/routing";
import yukoni18n from "../I18n/i18nConfig";

import { makeStyles, useTheme } from "@material-ui/core/styles";

import { store } from "../../redux/store";

import i18n from "i18next";

const useStyles = makeStyles((theme) => ({
    drawer: {
        top: 0,
    },
}));

/**
 * By-pass the authentication process for rendering the menu if the application is running in development mode.
 */
function renderNavigationMenu(props) {
    //TODO: Remove this later.
    console.log("Mode: " + process.env.NODE_ENV);
    const classes = useStyles();

    if (process.env.NODE_ENV == "development") {
        //TODO: Remove this later.
        console.warn("Application is running in development. Skipped the authentication for Navigation menu");
        return (
            <I18nextProvider i18n={yukoni18n}>
                <DrawerLayout drawer={<NavigationDrawer yukonPath={props.path} reactPath={props.reactPath} />} classes={{ drawer: classes.drawer }}>
                    <NavigationMenu yukonPath={props.path} reactPath={props.reactPath} />
                    <div id="page-contents"></div>
                </DrawerLayout>
            </I18nextProvider>
        );
    } else {
        //TODO: Remove this later.
        console.log("Application is not running in development mode. Current mode is " + process.env.NODE_ENV);
        return (
            <AuthUIConfiguration>
                <AuthNavigationContainer routeConfig={routes}>
                    <I18nextProvider i18n={yukoni18n}>
                        <DrawerLayout
                            drawer={<NavigationDrawer yukonPath={props.path} reactPath={props.reactPath} />}
                            classes={{ drawer: classes.drawer }}
                        >
                            <NavigationMenu yukonPath={props.path} reactPath={props.reactPath} />
                            <div id="page-contents"></div>
                        </DrawerLayout>
                    </I18nextProvider>
                </AuthNavigationContainer>
            </AuthUIConfiguration>
        );
    }
}

const NavigationContainer = (props) => {
    return (
        <Provider store={store}>
            <I18nextProvider i18n={i18n}>
                <ThemeProvider theme={createTheme(PXBThemes.blue)}>
                    <Suspense fallback={<div></div>}>
                        <SecurityContextProvider>{renderNavigationMenu(props)}</SecurityContextProvider>
                    </Suspense>
                </ThemeProvider>
            </I18nextProvider>
        </Provider>
    );
};

$(document).ready(function () {
    //move contents from yukon page to inside drawer
    const header = document.getElementsByClassName("yukon-header")[0];
    const pageContent = document.getElementsByClassName("yukon-content")[0];
    const drawerLayout = document.getElementById("page-contents");
    drawerLayout.appendChild(header);
    drawerLayout.appendChild(pageContent);
});

import(/* webpackChunkName: "react-dom" */ "react-dom").then((ReactDom) => {
    const navigationElement = document.getElementById("navigation");
    const path = navigationElement.getAttribute("data-path");
    const reactPath = navigationElement.getAttribute("data-react-path");
    ReactDom.render(<NavigationContainer path={path} reactPath={reactPath} />, navigationElement);
});
