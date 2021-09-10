import React, { useState, useEffect } from 'react';
import { useDispatch } from 'react-redux';

import { useTheme } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';

import PageHeader from '../../PageContents/PageHeader';
import PageContents from '../../PageContents/PageContents';
import Input from '../../controls/Input';
import ToggleButtons from '../../controls/ToggleButtons';
import Dropdown from '../../controls/Dropdown';
import PageButtons from '../../PageContents/PageButtons';

import axios from '../../../axiosConfig';

import * as actions from '../../../redux/actions/index';

import yup from '../../../validationConfig';

import { i18n, getPagei18nValues } from '../../../utils/Helpers';

const CommChannelCreate = () => {

    const dispatch = useDispatch();
    const theme = useTheme();
    const [pageKeysReceived, setPageKeysReceived] = useState(false);

    useEffect(() => {
        const pageKeys = [
           { nameKey: 'yukon.web.menu.home' },
           { nameKey: 'yukon.web.menu.assets' },
           { nameKey: 'yukon.web.modules.operator.commChannel.pageName' },
           { nameKey: 'yukon.common.disabled' },
           { nameKey: 'yukon.common.enabled' },
           { nameKey: 'yukon.common.name' },
           { nameKey: 'yukon.common.type' },
           { nameKey: 'yukon.common.pao.UDPPORT' },
           { nameKey: 'yukon.web.modules.operator.commChannelInfoWidget.portNumber' },
           { nameKey: 'yukon.web.modules.operator.commChannelInfoWidget.baudRate' },
           { nameKey: 'yukon.web.modules.operator.commChannel.create' },
           { nameKey: 'yukon.common.save' },
           { nameKey: 'yukon.common.cancel' }
        ];
        getPagei18nValues(pageKeys, setPageKeysReceived);
    }, []);

    const commChannelListUrl = window.configs.YUKON_API_URL + '/stars/device/commChannel/list';

    const breadcrumbs = [
        { link: '/', title: i18n('yukon.web.menu.home') },
        { link: '/stars/operator/inventory/home', title: i18n('yukon.web.menu.assets') },
        { link: '/stars/device/commChannel/list', title: i18n('yukon.web.modules.operator.commChannel.pageName') }
    ];

    const statusButtons = [
        { label: i18n('yukon.common.disabled'), value: false },
        { label: i18n('yukon.common.enabled'), value: true }
    ];

    const types = [
        { label: i18n('yukon.common.pao.UDPPORT'), value: 'UDPPORT' }
    ];

    const handleSaveClicked = () => {
        axios.post('/api/devices/commChannels', {
            name: name,
            type: type,
            portNumber: port,
            baudRate: baudRate,
            enabled: enabled
        }).then(response => {
            const successMsg = i18n('yukon.common.save.success', name);
            dispatch(actions.setFlashSuccess(successMsg));
            window.location.href = commChannelListUrl;
        });
    };

    const pageButtons = [
        { label: i18n('yukon.common.save'), onClick: handleSaveClicked },
        { label: i18n('yukon.common.cancel'), href: commChannelListUrl }
    ]

    const baudRates = [
        { label: '300', value: 'BAUD_300' },
        { label: '1200', value: 'BAUD_1200' },
        { label: '2400', value: 'BAUD_2400' },
        { label: '4800', value: 'BAUD_4800' },
        { label: '9600', value: 'BAUD_9600' },
        { label: '14400', value: 'BAUD_14400' },
        { label: '38400', value: 'BAUD_38400' },
        { label: '57600', value: 'BAUD_57600' },
        { label: '115200', value: 'BAUD_115200' }
    ]

    const [name, setName] = useState("");
    const [type, setType] = useState("UDPPORT");
    const [port, setPort] = useState("");
    const [baudRate, setBaudRate] = useState("BAUD_1200");
    const [enabled, setEnabled] = useState(true);

    const portValidationErrorMessage = i18n('yukon.web.error.invalidPort');

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
        pageKeysReceived ?
            <div>
                <PageHeader breadcrumbs={breadcrumbs} pageTitle={i18n('yukon.web.modules.operator.commChannel.create')}/>
                <PageContents>
                    <Paper style={{padding: theme.spacing(4)}}>
                        <Input label={i18n("yukon.common.name")} name="name" value={name} maxLength={60} style={{width: '30%'}}
                            onChange={handleNameChanged} validationSchema={validationSchema.name}/>
                        <Dropdown value={type} name="type" label={i18n('yukon.common.type')} onChange={handleTypeChanged} items={types}/>
                        <Input label={i18n("yukon.web.modules.operator.commChannelInfoWidget.portNumber")} name="portNumber" value={port} maxLength={5} 
                            onChange={handlePortChanged} validationSchema={validationSchema.port}/>
                        <Dropdown value={baudRate} name="baudRate" label={i18n('yukon.web.modules.operator.commChannelInfoWidget.baudRate')} onChange={handleBaudRateChanged} items={baudRates}/>
                        <ToggleButtons value={enabled} name="enabled" label={i18n('yukon.common.status')} onChange={handleStatusChanged} buttons={statusButtons}/>
                        <PageButtons buttons={pageButtons}/>
                    </Paper>
                </PageContents>
            </div>
        : null
    )
}

export default CommChannelCreate;
