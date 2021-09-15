import React, { useState } from 'react';

import { useTheme } from '@material-ui/core/styles';

import { Paper } from '@material-ui/core';

import PageHeader from '../../PageContents/PageHeader';
import PageContents from '../../PageContents/PageContents';
import PageButtons from '../../PageContents/PageButtons';
import Dialog from '../../controls/Dialog';

const DRTestPage = () => {

    const theme = useTheme();

    const [dialogOpen, setDialogOpen] = useState(false);

    const breadcrumbs = [
        { link: '/', title: 'Home' },
        { link: '/dr/home', title: 'Demand Response' }
    ];

    const handleCloseDialog = () => {
        setDialogOpen(false);
    }

    const handleClickDelete = () => {
        setDialogOpen(true);
    }

    const pageButtons = [
        { label: 'Save' },
        { label: 'Delete', onClick: handleClickDelete, style: {color: theme.palette.error.main, borderColor: theme.palette.error.main} },
        { label: 'Cancel' }
    ]

    const dialogButtons = [
        { label: 'Cancel', onClick: handleCloseDialog },
        { label: 'Delete', onClick: handleCloseDialog, style: {color: theme.palette.error.main} }
    ]

    return (
        <div>
            <Dialog open={dialogOpen} closeEvent={handleCloseDialog} buttons={dialogButtons}
                title="Delete test?" content="Are you sure you want to delete test? This cannot be undone"/>
            <PageHeader breadcrumbs={breadcrumbs} pageTitle="Test Page"/>
            <PageContents>
                <Paper style={{padding: theme.spacing(4)}}>
                    This is just a test page using React.
                    <PageButtons buttons={pageButtons}/>
                </Paper>
            </PageContents>
        </div>
    )
};

export default DRTestPage;