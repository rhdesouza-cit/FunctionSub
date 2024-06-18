package com.poc.function.businessclient.dto;


public class BusinessClientDTO {
    public Long id;
    public String name;


    public Long getId() {
        return id;
    }

    public BusinessClientDTO setId(final Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public BusinessClientDTO setName(final String name) {
        this.name = name;
        return this;
    }

}
