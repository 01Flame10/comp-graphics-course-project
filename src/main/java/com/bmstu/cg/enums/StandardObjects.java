package com.bmstu.cg.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StandardObjects {
    CUBE("Куб", "cube.obj"),
    SPHERE("Сфера", "sphere.obj"),
    CONE("Конус", "conus2.obj"),
    CYLINDER("Цилиндр", "cylinder2.obj"),
    PYRAMID("Пирамида", "pyramyd.obj"),
    TORUS("Тор", "tor2.obj");

    private final String displayName;
    private final String objectFileName;

}
