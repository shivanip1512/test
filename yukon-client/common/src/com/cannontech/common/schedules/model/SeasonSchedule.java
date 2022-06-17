package com.cannontech.common.schedules.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.season.DateOfSeason;

public class SeasonSchedule implements DBPersistentConverter<com.cannontech.database.data.season.SeasonSchedule> {
    private Integer id;
    private String name;
    private List<Season> seasons;

    public SeasonSchedule() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void buildModel(com.cannontech.database.data.season.SeasonSchedule seasonSchedule) {
        setId(seasonSchedule.getScheduleID());
        setName(seasonSchedule.getScheduleName());
        if (CollectionUtils.isNotEmpty(seasonSchedule.getSeasonDatesVector())) {
            List<Season> seasons = new ArrayList<Season>();
            seasonSchedule.getSeasonDatesVector().stream().forEach(dateOfSeason -> {
                Season season = new Season();
                season.buildModel((DateOfSeason) dateOfSeason);
                seasons.add(season);
            });
            setSeasons(seasons);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void buildDBPersistent(com.cannontech.database.data.season.SeasonSchedule seasonSchedule) {
        seasonSchedule.setScheduleName(getName());
        if (CollectionUtils.isNotEmpty(getSeasons())) {
            getSeasons().stream().forEach(season -> {
                DateOfSeason dateOfSeason = new DateOfSeason();
                season.buildDBPersistent(dateOfSeason);
                seasonSchedule.getSeasonDatesVector().add(dateOfSeason);
            });
        }
    }
}
