import React, { useState, useEffect } from 'react';
import { Divider, InputLabel } from '@material-ui/core';
import AddIcon from '@material-ui/icons/Add';
import { useTheme } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';

import PageHeader from '../../PageContents/PageHeader';
import PageContents from '../../PageContents/PageContents';
import Table from '../../controls/Table';
import Input from '../../controls/Input';
import Dropdown from '../../controls/Dropdown';

import axios from '../../../axiosConfig';

import { i18n, getPagei18nValues } from '../../../utils/Helpers';

import yup from '../../../validationConfig';

const DRSetupFilter = () => {

    const theme = useTheme();

    const [pageKeysReceived, setPageKeysReceived] = useState(false);

    useEffect(() => {
        const pageKeys = [
           { nameKey: 'yukon.web.menu.home' },
           { nameKey: 'yukon.web.menu.dr' },
           { nameKey: 'yukon.common.name' },
           { nameKey: 'yukon.common.type' },
           { nameKey: 'yukon.web.menu.dr.setup' }
        ];
        getPagei18nValues(pageKeys, setPageKeysReceived);
    }, []);

    const [list, setList] = useState([]);
    const [drType, setDrType] = useState('CONTROL_AREA');
    const [name, setName] = useState('');
    const [rowsPerPage, setRowsPerPage] = useState(25);
    const [page, setPage] = useState(0);
    const [totalCount, setTotalCount] = useState(0);
    const [sortBy, setSortBy] = useState('PAONAME');
    const [direction, setDirection] = useState('asc');

    const validationSchema = {
        name: yup.number().typeError().min(1)
    };

    const breadcrumbs = [
        { link: '/', title: i18n('yukon.web.menu.home') },
        { link: '/dr/home', title: i18n('yukon.web.menu.dr') }
    ];

    const actionButtons = [
        { icon: <AddIcon/>, label: "Create" }
    ];

    const drTypes = [
        { label: 'Control Area', value: 'CONTROL_AREA' },
        { label: 'Control Scenario', value: 'CONTROL_SCENARIO' },
        { label: 'Load Program', value: 'LOAD_PROGRAM' },
        { label: 'Load Group', value: 'LOAD_GROUP' },
        { label: 'Macro Load Group', value: 'MACRO_LOAD_GROUP' },
        { label: 'Program Constraint', value: 'PROGRAM_CONSTRAINT'},
        { label: 'Gear', value: 'GEAR' }
    ];

    const handleChangeType = (e) => {
        setDrType(e.target.value);
        setName('');
        setPage(0);
        setSortBy('PAONAME');
        setDirection("asc");
    };

    const handleChangeName = (e) => {
        setName(e.target.value);
        setPage(0);
    };

    const handlePageChange = (event, newPage) => {
        setPage(newPage);
    };

    const handleRowChange = (event) => {
        setRowsPerPage(event.target.value);
        setPage(0);
    };

    const renderName = (object) => {
        if (object.name) return object.name;
        if (object.program) return object.program.name;
        if (object.scenario) return object.scenario.name;
        if (object.controlAreaName) return object.controlAreaName;
        if (object.gearName) return object.gearName;
    };

    const renderId = (object) => {
        if (object.id) return object.id;
        if (object.controlAreaId) return object.controlAreaId;
        if (object.scenario) return object.scenario.id;
        if (object.program) return object.program.id;
        if (object.gearId) return object.gearId;
    };

    const renderType = (object) => {
        if (object.type) return object.type;
        if (object.controlMethod) return object.controlMethod;
    };

    const handleSortClick = (sort) => {
        if (sort === sortBy) {
            setDirection(direction === 'asc' ? 'desc' : 'asc');
        } else {
            setSortBy(sort);
            setDirection('asc');
        }
    };

    useEffect(() => {
        let sorting = sortBy;
        if (drType === 'PROGRAM_CONSTRAINT' && sortBy === 'PAONAME') {
            sorting = 'CONSTRAINTNAME';
        }
        axios.post('/api/dr/setup/filter', {
            filteringParameters: {
                filterByType: drType,
                name: name
            },
            sortingParameters: {
                sort: sorting,
                direction: direction
            },
            pagingParameters: {
                page: page + 1,
                itemsPerPage: rowsPerPage
            }
        }).then(listResponse => {
            if (listResponse) {
                setList(listResponse.data.resultList);
                setTotalCount(listResponse.data.hitCount);
            }
        });    
    }, [drType, name, page, rowsPerPage, sortBy, direction]);

    let drFilterTable = <div></div>;

    const tableHeaders = [
        { label: i18n('yukon.common.name'), active: sortBy === 'PAONAME', direction: direction, onClick: () => handleSortClick('PAONAME') },
        { label: i18n('yukon.common.type'), active: sortBy === 'TYPE', direction: direction, onClick: () => handleSortClick('TYPE') }
    ]

    const tableRows = [];
    list.forEach((row) => {
        tableRows.push({ id: renderId(row), columns: [renderName(row), renderType(row)] })
    });


    if (tableRows.length > 0) {
        drFilterTable = 
            <Table 
                headers={tableHeaders} 
                rows={tableRows} 
                totalCount={totalCount} 
                rowsPerPage={rowsPerPage} 
                page={page} 
                onPageChange={handlePageChange} 
                onRowChange={handleRowChange}/>
    }

    return (
        pageKeysReceived ?
            <div>
                <PageHeader breadcrumbs={breadcrumbs} pageTitle={i18n('yukon.web.menu.dr.setup')} actionButtons={actionButtons}/>
                <PageContents>
                    <Paper style={{padding: theme.spacing(4)}}>
                        <Divider/>
                        <InputLabel style={{display: 'inline-block', paddingTop: theme.spacing(1)}}>Filter By:</InputLabel>
                        <Dropdown value={drType} style={{marginLeft: theme.spacing(2), marginBottom: theme.spacing(1)}} 
                            wrapperStyle={{display: 'inline-block', marginBottom: theme.spacing(0)}} 
                            onChange={handleChangeType} items={drTypes}/>
                        <Input label={i18n('yukon.common.name')} value={name} style={{margin: theme.spacing(1), width: '20%'}} 
                            onChange={handleChangeName} validationSchema={validationSchema.name}/>
                        <Divider/>

                        {drFilterTable}
                    </Paper>
                </PageContents>
            </div>
        : null
        
    )

}

export default DRSetupFilter;