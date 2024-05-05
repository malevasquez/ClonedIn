package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Size;

public class ContactForm {
    @Size(max=100)
    private String message;

    private long category;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(long category) {
        this.category = category;
    }
}
