package com.laresencanto.laresencantorestapi.utils.enums;

public enum AddressCategory {
    DELIVERY("Entrega"),
    BILLING("Cobrança");

    private final String category;

    AddressCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public static AddressCategory fromString(String category) {
        for (AddressCategory addressCategory : AddressCategory.values()) {
            if (addressCategory.category.equalsIgnoreCase(category)) {
                return addressCategory;
            }
        }
        return null;
    }
}
