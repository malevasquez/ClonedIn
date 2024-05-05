package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Size;

public class SearchForm {
    @Size(max = 50)
    private String term;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
