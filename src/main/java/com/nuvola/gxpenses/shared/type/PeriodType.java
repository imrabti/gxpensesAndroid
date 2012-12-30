package com.nuvola.gxpenses.shared.type;

public enum PeriodType {
    THIS_MONTH("This month", 0),
    LAST_MONTH("Last month", 1),
    THIS_QUARTER("This quarter", 2),
    LAST_QUARTER("Last quarter", 3),
    THIS_SEMESTER("This semester", 4),
    LAST_SEMESTER("Last semester", 5),
    THIS_YEAR("This year", 6),
    LAST_YEAR("Last year", 7);

    private final String label;
    private final Integer value;

    private PeriodType(String label, Integer value) {
        this.label = label;
        this.value = value;
    }

    public static PeriodType periodeForValue(Integer value) {
        switch (value) {
            case 0:
                return THIS_MONTH;
            case 1:
                return LAST_MONTH;
            case 2:
                return THIS_QUARTER;
            case 3:
                return LAST_QUARTER;
            case 4:
                return THIS_SEMESTER;
            case 5:
                return LAST_SEMESTER;
            case 6:
                return THIS_YEAR;
            case 7:
                return LAST_YEAR;
            default:
                return THIS_MONTH;
        }
    }

    public String getLabel() {
        return label;
    }

    public Integer getValue() {
        return value;
    }
}
