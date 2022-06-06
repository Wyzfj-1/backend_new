package com.wsn.powerstrip.common.util;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Counter {
    String collection;
    Integer seq;
}
