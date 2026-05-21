package com.vvss.FlavorFiesta.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RankedItem<T> {
    private T entity;
    private long rank;
}