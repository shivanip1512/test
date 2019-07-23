package com.cannontech.common.dr.setup;

/* This POJO is mainly used to pass Id and Name of different LM Objects in case of retrieval */
public class LMDto {

    private Integer id;
    private String name;

    public LMDto() {

    }

    public LMDto(Integer id, String name) {
        this.id = id;
        this.name = name;
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

}
