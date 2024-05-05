package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Size;

public class FilterForm {
    private String category="";
    @Size(max = 50)
    private String location="";
    private String educationLevel="";

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }
}
