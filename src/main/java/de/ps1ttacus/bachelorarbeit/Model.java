package de.ps1ttacus.bachelorarbeit;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private ArrayList<String> terminale;
    private ArrayList<String> nichtterminale;
    private String startsymbol;
    private String testwort;
    private List<String> produktionen;


    public List<String> getTerminale() {
        return terminale;
    }

    public void setTerminale(ArrayList<String> terminale) {
        this.terminale = terminale;
    }

    public List<String> getNichtterminale() {
        return nichtterminale;
    }

    public void setNichtterminale(ArrayList<String> nichtTerminale) {
        this.nichtterminale = nichtTerminale;
    }

    public String getStartsymbol() {
        return startsymbol;
    }

    public void setStartsymbol(String startsymbol) {
        this.startsymbol = startsymbol;
    }

    public List<String> getProduktionen() {
        return produktionen;
    }

    public void setProduktionen(List<String> produktionen) {
        this.produktionen = produktionen;
    }

    public String getTestwort() {
        return testwort;
    }

    public void setTestwort(String testwort) {
        this.testwort = testwort;
    }
}
