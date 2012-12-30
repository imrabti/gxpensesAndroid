package com.nuvola.gxpenses.shared.type;

public enum AccountType {
    CASH("Cash", "CASH"),
    SAVING("Saving", "SAV"),
    CHECKING("Checking", "CHEC");

    private final String label;
    private final String value;

    private AccountType(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
