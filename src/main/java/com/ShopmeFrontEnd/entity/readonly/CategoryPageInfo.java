package com.ShopmeFrontEnd.entity.readonly;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CategoryPageInfo implements Serializable {
    private int totalPage;
    private long totalElements;


}
