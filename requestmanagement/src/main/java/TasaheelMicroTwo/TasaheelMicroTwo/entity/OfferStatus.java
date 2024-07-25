package TasaheelMicroTwo.TasaheelMicroTwo.entity;

public enum OfferStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected");

    private final String status;

    OfferStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.status;
    }

    public static OfferStatus fromString(String status) {
        for (OfferStatus os : OfferStatus.values()) {
            if (os.status.equalsIgnoreCase(status)) {
                return os;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}
