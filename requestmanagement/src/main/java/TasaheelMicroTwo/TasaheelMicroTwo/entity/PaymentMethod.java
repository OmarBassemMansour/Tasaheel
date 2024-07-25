package TasaheelMicroTwo.TasaheelMicroTwo.entity;

public enum PaymentMethod {
    wallet,
    cash;

    public static PaymentMethod fromString(String method) {
        for (PaymentMethod m : PaymentMethod.values()) {
            if (m.name().equalsIgnoreCase(method)) {
                return m;
            }
        }
        throw new IllegalArgumentException("No constant with text " + method + " found");
    }
}
