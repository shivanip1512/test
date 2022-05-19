import React, { useEffect, useState } from "react";
import { useDispatch } from "react-redux";

import { AuthUIContextProvider, useSecurityActions } from "@brightlayer-ui/react-auth-workflow";

import { ProjectAuthUIActions } from "../../actions/AuthUIActions";
import { ProjectRegistrationUIActions } from "../../actions/RegistrationUIActions";
import { fetchTheme } from "../../apiHelpers/themeApiHelper";
import * as actions from "../../redux/actions/index";
import axios from "../../axiosConfig";

import productLogo from "../../assets/images/eaton_yukon_logo.png";

const AuthUIConfiguration = (props) => {
    const [backgroundImage, setBackgroundImage] = useState("url(/yukon-ui/yukon_background.png)");
    const dispatch = useDispatch();
    const securityContextActions = useSecurityActions();

    if (process.env.NODE_ENV !== "development") {
        axios.defaults.baseURL = props.yukonPath;
    }

    useEffect(() => {
        fetchTheme().then((themeResp) => {
            if (themeResp && themeResp.themeId > 0) {
                dispatch(actions.setTheme(themeResp));
                setBackgroundImage(`url(${props.yukonPath}/api/common/images/${themeResp.properties.LOGIN_BACKGROUND})`);
            }
        });
    }, [dispatch, props.yukonPath]);

    const backgroundProps = {
        backgroundImage: backgroundImage,
        backgroundRepeat: "no-repeat",
        backgroundSize: "cover",
    };

    return (
        <AuthUIContextProvider
            authActions={ProjectAuthUIActions(securityContextActions)} //add dispatch as parameter
            registrationActions={ProjectRegistrationUIActions}
            showSelfRegistration={false}
            showContactSupport={false}
            htmlEula={false}
            loginType="username"
            background={backgroundProps}
            contactEmail={"something@email.com"}
            contactPhone={"1-800-123-4567"}
            projectImage={productLogo}
            loginErrorDisplayConfig={{ mode: "message-box" }}
        >
            {props.children}
        </AuthUIContextProvider>
    );
};

export default AuthUIConfiguration;
