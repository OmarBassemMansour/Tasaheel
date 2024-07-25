package TasaheelMicroTwo.TasaheelMicroTwo.entity;

public enum RequestStatus {
    PENDING("PENDING"),
    TAKEN_BY_USER("TAKEN BY USER"),
    COMPLETED("COMPLETED");

    private String status;

    RequestStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.status;
    }

    // Optional: Method to convert a string to a RequestStatus enum
    public static RequestStatus fromString(String status) {
        for (RequestStatus r : RequestStatus.values()) {
            if (r.status.equalsIgnoreCase(status)) {
                return r;
            }
        }
        throw new IllegalArgumentException("No constant with text " + status + " found");
    }
}
