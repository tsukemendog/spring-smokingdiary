package io.github.tsukemendog.nondesire.enums;

public enum Level {
    LV1(1),
    LV2(2),
    LV3(3),
    LV4(4),
    LV5(5),
    LV6(6),
    LV7(7),
    LV8(8),
    LV9(9),
    LV10(10);

    private final int level;

    Level(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
