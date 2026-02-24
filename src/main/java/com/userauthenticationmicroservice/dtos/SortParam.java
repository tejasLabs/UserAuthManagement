package com.userauthenticationmicroservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SortParam {
    String paramName;
    SortType sortType;
}
