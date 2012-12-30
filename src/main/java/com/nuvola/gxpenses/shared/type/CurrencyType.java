package com.nuvola.gxpenses.shared.type;

public enum CurrencyType {
    US_DOLLAR("US Dollar - $ (USD)", "USD"),
    YEN("Japanese Yen - ¥ (JPY)", "JPY"),
    LEV("Bulgarian Lev - (BGN)", "BGN"),
    CZECH_KORUNA("Czech Republic Koruna - Kč (CZK)", "CZK"),
    DANISH_KORUNA("Danish Krone - Dkr (DKK)", "DKK"),
    POUND("British Pound Sterling - £ (GBP)", "GBP"),
    MAD("Moroccan Dirham - (MAD)", "MAD"),
    FORINT("Hungarian Forint - Ft (HUF)", "HUF"),
    EURO("European Euo - € (EURO)", "EURO");

    private final String label;
    private final String value;

    private CurrencyType(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }
}
