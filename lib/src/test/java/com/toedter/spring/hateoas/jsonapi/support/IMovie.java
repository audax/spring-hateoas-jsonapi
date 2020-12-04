package com.toedter.spring.hateoas.jsonapi.support;

import lombok.Data;

public interface IMovie {
    String getId();
    void setId(String id);
    String getTitle();
    void setTitle(String title);
}
