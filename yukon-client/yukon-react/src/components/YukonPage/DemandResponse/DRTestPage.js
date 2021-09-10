import React from 'react';

import { useTheme } from '@material-ui/core/styles';

import Paper from '@material-ui/core/Paper';

import PageHeader from '../../PageContents/PageHeader';
import PageContents from '../../PageContents/PageContents';
import PageButtons from '../../PageContents/PageButtons';

const DRTestPage = () => {

    const theme = useTheme();

    const breadcrumbs = [
        { link: '/', title: 'Home' },
        { link: '/dr/home', title: 'Demand Response' }
    ];

    const pageButtons = [
        { label: 'Save' },
        { label: 'Cancel' }
    ]

    return (
        <div>
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