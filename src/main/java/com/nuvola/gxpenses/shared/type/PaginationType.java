package com.nuvola.gxpenses.shared.type;

public enum PaginationType {
    PAGE_10("10 by page", 10),
    PAGE_15("15 by page", 15),
    PAGE_20("20 by page", 20),
    PAGE_30("30 by page", 30),
    PAGE_40("40 by page", 40);

    private final String label;
    private final Integer value;

    private PaginationType(String label, Integer value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public Integer getValue() {
        return value;
    }
}
