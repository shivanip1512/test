import React from 'react';

import { Grid, List, ListItem, ListItemText, ListItemSecondaryAction } from '@material-ui/core';
import { Avatar, Card, CardHeader, CardContent, CardActions } from '@material-ui/core';

import IconButton from '@material-ui/core/IconButton';
import { useTheme, makeStyles } from '@material-ui/core/styles';

import ExpandLessIcon from '@material-ui/icons/ExpandLess';
import HelpIcon from '@material-ui/icons/Help';
import EmailIcon from '@material-ui/icons/Email';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import SyncIcon from '@material-ui/icons/Sync';
import EditIcon from '@material-ui/icons/Edit';
import ConfigureIcon from '@brightlayer-ui/icons-mui/Configuration';
import WarningIcon from '@material-ui/icons/Warning';
import CheckCircleIcon from '@material-ui/icons/CheckCircle';
import SearchIcon from '@material-ui/icons/Search';

import { ScoreCard } from '@brightlayer-ui/react-components';
import { InfoListItem } from '@brightlayer-ui/react-components';

import PageHeader from '../../PageContents/PageHeader';
import PageContents from '../../PageContents/PageContents';

import Input from '../../controls/Input';
import Button from '../../controls/Button';

const useStyles = makeStyles(theme => ({
    availableCircle: {
        padding: theme.spacing(3),
        backgroundColor: theme.palette.success.main,
        color: theme.palette.common.white
    },
    expectedCircle: {
        padding: theme.spacing(3),
        backgroundColor: theme.palette.primary.main,
        color: theme.palette.common.white
    },
    outdatedCircle: {
        padding: theme.spacing(3),
        backgroundColor: theme.palette.warning.dark,
        color: theme.palette.common.white
    },
    unavailableCircle: {
        padding: theme.spacing(3),
        backgroundColor: theme.palette.grey[500],
        color: theme.palette.common.white
    },
}));

const Dashboard = () => {

    const breadcrumbs = [
        { link: '/', title: 'Home' }
    ];

    const actionButtons = [
        { label: 'Edit', icon: <EditIcon/> },
        { label: 'Manage Dashboards', icon: <ConfigureIcon/> }
    ];

    const theme = useTheme();
    const classes = useStyles();

    return (
        <div>
            <PageHeader breadcrumbs={breadcrumbs} pageTitle="Dashboard" actionButtons={actionButtons}/>
            <PageContents>
                <Grid container spacing={4}>
                    <Grid item xs>
                        <Card variant="outlined" color="primary">
                            <CardHeader title="Meter Search" titleTypographyProps={{variant: 'h6', color: 'primary'}} 
                                style={{borderBottom: '1px solid #0000001f'}}
                                action={[
                                    <IconButton key="helpIcon"><HelpIcon/></IconButton>, <IconButton key="expandButton"><ExpandLessIcon/></IconButton>
                                ]}/>
                            <CardContent>
                                <Input label="Quick Search" name="quickSearch" style={{width: '90%'}}/>
                                <Input label="Meter Number" name="meterNumber" style={{width: '90%'}}/>
                                <Input label="Device Name" name="deviceName" style={{width: '90%'}}/>
                                <Input label="Device Type" name="deviceType" style={{width: '90%'}}/>
                                <Input label="Address/Serial" name="addressSerial" style={{width: '90%'}}/>
                                <Input label="Route" name="route" style={{width: '90%'}}/>
                            </CardContent>
                            <CardActions style={{float:'right', paddingRight: theme.spacing(4), paddingBottom: theme.spacing(2)}}>
                                <Button variant="outlined" color="primary" icon={<SearchIcon/>} label="Search"/>
                            </CardActions>
                        </Card>
                    </Grid>
                    <Grid item xs>
                        <ScoreCard headerTitle="Data Collection" headerSubtitle="All RFN Meters"
                            actionItems={[
                                <HelpIcon/>, <ExpandLessIcon/>
                            ]}
                            actionRow={
                                <List>
                                    <ListItem button dense={true}>
                                        <ListItemText primary="View Details" primaryTypographyProps={{color: 'primary'}}/>
                                        <ListItemSecondaryAction>
                                            <IconButton><ChevronRightIcon/></IconButton>
                                        </ListItemSecondaryAction>
                                    </ListItem>
                                    <ListItem dense={true}>
                                        <ListItemText primary="Last Refresh: 09/09/2021 10:22:34 PM"/>
                                        <ListItemSecondaryAction>
                                            <IconButton><SyncIcon/></IconButton>
                                        </ListItemSecondaryAction>
                                    </ListItem>
                                </List>
                            }>
                            <div style={{margin: theme.spacing(4)}}>
                                <Grid container spacing={2} align="center">
                                    <Grid item xs>
                                        <Avatar className={classes.availableCircle}>60%</Avatar>
                                        Available
                                    </Grid>
                                    <Grid item xs>
                                        <Avatar className={classes.expectedCircle}>15%</Avatar>
                                        Expected
                                    </Grid>
                                    <Grid item xs>
                                        <Avatar className={classes.outdatedCircle}>20%</Avatar>
                                        Outdated
                                    </Grid>
                                    <Grid item xs>
                                        <Avatar className={classes.unavailableCircle}>15%</Avatar>
                                        Unavailable
                                    </Grid>
                                </Grid>
                            </div>
                            <div style={{margin: theme.spacing(4), display: 'flex'}}>
                                <div style={{height: theme.spacing(2), width: '60%', backgroundColor: theme.palette.success.main}}/>
                                <div style={{height: theme.spacing(2), width: '15%', backgroundColor: theme.palette.primary.main}}/>
                                <div style={{height: theme.spacing(2), width: '20%', backgroundColor: theme.palette.warning.dark}}/>
                                <div style={{height: theme.spacing(2), width: '5%', backgroundColor: theme.palette.grey[500]}}/>
                            </div>
                        </ScoreCard>
                    </Grid>
                    <Grid item xs>
                        <ScoreCard headerTitle="Infrastructure Warnings"
                            actionItems={[
                                <EmailIcon/>, <HelpIcon/>, <ExpandLessIcon/>
                            ]}
                            actionRow={
                                <List>
                                    <ListItem button dense={true}>
                                        <ListItemText primary="View Details" primaryTypographyProps={{color: 'primary'}}/>
                                        <ListItemSecondaryAction>
                                            <IconButton><ChevronRightIcon/></IconButton>
                                        </ListItemSecondaryAction>
                                    </ListItem>
                                    <ListItem dense={true}>
                                        <ListItemText primary="Last Refresh: 09/09/2021 10:22:34 PM"/>
                                        <ListItemSecondaryAction>
                                            <IconButton><SyncIcon/></IconButton>
                                        </ListItemSecondaryAction>
                                    </ListItem>
                                </List>
                            }>
                            <div style={{margin: theme.spacing(4)}}>
                                <Grid container spacing={4} align="center" alignItems="stretch" direction="row">
                                    <Grid item xs style={{height: '100%'}}>
                                        <Grid container spacing={2} alignItems="center" direction="row"
                                            style={{backgroundColor: theme.palette.warning.dark, borderRadius: '4px'}}>
                                            <Grid item xs>
                                                <WarningIcon style={{color: theme.palette.warning.light}}/>
                                            </Grid>
                                            <Grid item xs style={{color: theme.palette.common.white}}>
                                                <div>Gateways</div>
                                                <div><span style={{fontSize: '20px'}}>2</span>/5</div>
                                                In Alert
                                            </Grid>
                                        </Grid>
                                    </Grid>
                                    <Grid item xs style={{height: '100%'}}>
                                        <Grid container spacing={2} alignItems="center" direction="row"
                                            style={{backgroundColor: theme.palette.success.light, height: '100%', borderRadius: '4px'}}>
                                            <Grid item xs>
                                                <CheckCircleIcon style={{color: theme.palette.success.main}}/>
                                            </Grid>
                                            <Grid item xs>
                                                <div>Relays</div>
                                                <div style={{fontSize: '20px'}}>OK</div>
                                            </Grid>
                                        </Grid>
                                    </Grid>
                                    <Grid item xs style={{height: '100%'}}>
                                        <Grid container spacing={2} alignItems="center" direction="row"
                                            style={{backgroundColor: theme.palette.warning.dark, borderRadius: '4px'}}>
                                            <Grid item xs>
                                                <WarningIcon style={{color: theme.palette.warning.light}}/>
                                            </Grid>
                                            <Grid item xs style={{color: theme.palette.common.white}}>
                                                <div>CCUs</div>
                                                <div><span style={{fontSize: '20px'}}>1</span>/5</div>
                                                <div style={{whiteSpace: 'nowrap'}}>In Alert</div>
                                            </Grid>
                                        </Grid>
                                    </Grid>
                                    <Grid item xs style={{height: '100%'}}>
                                        <Grid container spacing={2} alignItems="center" direction="row"
                                            style={{backgroundColor: theme.palette.success.light, height: '100%', borderRadius: '4px'}}>
                                            <Grid item xs>
                                                <CheckCircleIcon style={{color: theme.palette.success.main}}/>
                                            </Grid>
                                            <Grid item xs>
                                                <div>Repeaters</div>
                                                <div style={{fontSize: '20px'}}>OK</div>
                                            </Grid>
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </div>
                            <InfoListItem title="Most Recent Active Items" fontColor={theme.palette.primary.main} hidePadding={true} dense={true} divider="full"/>
                            <InfoListItem title="Gateway 100128" leftComponent={<WarningIcon style={{color: theme.palette.error.dark}}/>} hidePadding={true} divider="full" dense={true}
                                subtitle="Data streaming capacity at 95%." wrapSubtitle={true}
                                rightComponent="1 week"/>
                            <InfoListItem title="CCU 117" leftComponent={<WarningIcon style={{color: theme.palette.warning.dark}}/>} hidePadding={true} divider="full" dense={true}
                                subtitle="Communication status is disconnected." wrapSubtitle={true}
                                rightComponent="1 week"/>
                            <InfoListItem title="Gateway 100123" leftComponent={<WarningIcon style={{color: theme.palette.warning.dark}}/>} hidePadding={true} divider="full" dense={true}
                                subtitle="Gateway's total ready node count (20) is lower than the warning threshold (25)." wrapSubtitle={true}
                                rightComponent="2 weeks"/>
                        </ScoreCard>
                    </Grid>
                </Grid>
            </PageContents>
        </div>
    )
}

export default Dashboard;