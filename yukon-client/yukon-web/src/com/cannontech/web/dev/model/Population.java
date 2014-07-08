package com.cannontech.web.dev.model;

public class Population {
    
    private String city;
    private long population;
    
    public Population(String city, long population) {
        this.city = city;
        this.population = population;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public long getPopulation() {
        return population;
    }
    
    public void setPopulation(long population) {
        this.population = population;
    }
    
}