import React, { useEffect, useState} from "react";
import { useSelector } from "react-redux";

import {
  AuthUIContextProvider,
  useSecurityActions,
} from "@brightlayer-ui/react-auth-workflow";

import { ProjectAuthUIActions } from "../../actions/AuthUIActions";
import { ProjectRegistrationUIActions } from "../../actions/RegistrationUIActions";

import productLogo from "../../assets/images/eaton_yukon_logo.png";

const AuthUIConfiguration = (props) => {
  const [backgroundImage, setBackgroundImage] = useState("");
  const securityContextActions = useSecurityActions();
  const yukonTheme = useSelector((store) => store.app.theme);
  
  //console.log("inside Auth ui confi"+yukonTheme.properties.LOGO);
  //console.log("built url  ----"+props.yukonPath+"/api/common/images/"+yukonTheme.properties.LOGO);

  useEffect(() => {
    if (yukonTheme?.properties?.LOGO) {
      setBackgroundImage(`url(${props.yukonPath}/api/common/images/${yukonTheme.properties.LOGIN_BACKGROUND})`);
      console.log("run when yukon theme changes", yukonTheme);
    }
  }, [yukonTheme]);

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
