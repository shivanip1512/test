import React, { useState } from "react";
import { useDispatch } from "react-redux";

import { useTheme } from "@material-ui/core/styles";
import Paper from "@material-ui/core/Paper";

import PageHeader from "../../PageContents/PageHeader";
import PageContents from "../../PageContents/PageContents";
import Input from "../../controls/Input";
import ToggleButtons from "../../controls/ToggleButtons";
import Dropdown from "../../controls/Dropdown";
import PageButtons from "../../PageContents/PageButtons";
import { urlHelper } from "../../../helpers/urlHelper";

import axios from "../../../axiosConfig";

import * as actions from "../../../redux/actions/index";

import yup from "../../../validationConfig";

const CommChannelCreate = () => {
    const dispatch = useDispatch();
    const theme = useTheme();

    const commChannelListUrl = urlHelper.getYukonApiUrl() + "/stars/device/commChannel/list";

    const breadcrumbs = [
        { link: "/", title: "Home" },
        { link: "/stars/operator/inventory/home", title: "Assets" },
        { link: "/stars/device/commChannel/list", title: "Comm Channels" },
    ];

    const statusButtons = [
        { label: "Disabled", value: false },
        { label: "Enabled", value: true },
    ];

    const types = [{ label: "UDP", value: "UDPPORT" }];

    const handleSaveClicked = () => {
        axios
            .post("/api/devices/commChannels", {
                deviceName: name,
                deviceType: type,
                portNumber: port,
                baudRate: baudRate,
                enabled: enabled,
            })
            .then((response) => {
                dispatch(actions.setFlashSuccess(name + " saved successfully."));
                window.location.href = commChannelListUrl;
            });
    };

    const pageButtons = [
        { label: "Save", onClick: handleSaveClicked },
        { label: "Cancel", href: commChannelListUrl },
    ];

    const baudRates = [
        { label: "300", value: "BAUD_300" },
        { label: "1200", value: "BAUD_1200" },
        { label: "2400", value: "BAUD_2400" },
        { label: "4800", value: "BAUD_4800" },
        { label: "9600", value: "BAUD_9600" },
        { label: "14400", value: "BAUD_14400" },
        { label: "38400", value: "BAUD_38400" },
        { label: "57600", value: "BAUD_57600" },
        { label: "115200", value: "BAUD_115200" },
    ];

    const [name, setName] = useState("");
    const [type, setType] = useState("UDPPORT");
    const [port, setPort] = useState("");
    const [baudRate, setBaudRate] = useState("BAUD_1200");
    const [enabled, setEnabled] = useState(true);

    const portValidationErrorMessage = "Port must be between 5 and 65535";

    const validationSchema = {
        name: yup.string().trim().required(),
        port: yup.number().typeError(portValidationErrorMessage).min(5).max(65535, portValidationErrorMessage),
    };

    const handleNameChanged = (e) => {
        setName(e.target.value);
    };

    const handleTypeChanged = (e) => {
        setType(e.target.value);
    };

    const handlePortChanged = (e) => {
        setPort(e.target.value);
    };

    const handleBaudRateChanged = (e) => {
        setBaudRate(e.target.value);
    };

    const handleStatusChanged = (event, selectedValue) => {
        setEnabled(selectedValue);
    };

    return (
        <div>
            <PageHeader breadcrumbs={breadcrumbs} pageTitle="Create Comm Channel" />
            <PageContents>
                <Paper style={{ padding: theme.spacing(4) }}>
                    <Input
                        label="Name"
                        name="name"
                        value={name}
                        maxLength={60}
                        style={{ width: "30%" }}
                        onChange={handleNameChanged}
                        validationSchema={validationSchema.name}
                    />
                    <Dropdown value={type} name="type" label="Type" onChange={handleTypeChanged} items={types} />
                    <Input
                        label="Port Number"
                        name="portNumber"
                        value={port}
                        maxLength={5}
                        onChange={handlePortChanged}
                        validationSchema={validationSchema.port}
                    />
                    <Dropdown value={baudRate} name="baudRate" label="Baud Rate" onChange={handleBaudRateChanged} items={baudRates} />
                    <ToggleButtons value={enabled} name="enabled" label="Status" onChange={handleStatusChanged} buttons={statusButtons} />
                    <PageButtons buttons={pageButtons} />
                </Paper>
            </PageContents>
        </div>
    );
};

export default CommChannelCreate;
