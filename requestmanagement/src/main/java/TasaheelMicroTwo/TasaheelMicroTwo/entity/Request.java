package TasaheelMicroTwo.TasaheelMicroTwo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.cloud.Timestamp;

import java.time.Instant;
import java.util.List;

public class Request {
    private int requestId;
    private int userId;
    private String title;
    private String description;
    private String category;
    private int suggestedAmount;
    private RequestStatus status; // Changed from int to String
    private String address;
    private PaymentMethod PaymentMethod ;
    private String formattedDate;
    private List<String> images;
    @JsonIgnore
    private Timestamp timestamp;

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getSuggestedAmount() {
        return suggestedAmount;
    }

    public void setSuggestedAmount(int suggestedAmount) {
        this.suggestedAmount = suggestedAmount;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public PaymentMethod getPaymentMethod() {
        return PaymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.PaymentMethod = paymentMethod;
    }

    public void setTimestampNow() {
        this.timestamp = Timestamp.now();  // Set the current timestamp
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

}
