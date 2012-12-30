package com.nuvola.gxpenses.shared.type;

public enum FrequencyType {
    MONTH("Every Month", 0),
    QUARTER("Every Quarter", 1),
    SEMESTER("Every Semester", 2),
    YEAR("Every Year", 3);

    private final String label;
    private final Integer value;

    private FrequencyType(String label, Integer value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
