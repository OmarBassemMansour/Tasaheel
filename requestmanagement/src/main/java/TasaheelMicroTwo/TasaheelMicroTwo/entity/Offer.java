package TasaheelMicroTwo.TasaheelMicroTwo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.cloud.Timestamp;

public class Offer {
    private int offerId;
    private OfferStatus offerStatus;
    private float offerValue;
    private int providerId;
    private int requestId;
    private String formattedDate;

    @JsonIgnore
    private Timestamp timestamp;


    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public OfferStatus getOfferStatus() {
        return offerStatus;
    }

    public void setOfferStatus(OfferStatus offerStatus) {
        this.offerStatus = offerStatus;
    }

    public float getOfferValue() {
        return offerValue;
    }

    public void setOfferValue(float offerValue) {
        this.offerValue = offerValue;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }


    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
