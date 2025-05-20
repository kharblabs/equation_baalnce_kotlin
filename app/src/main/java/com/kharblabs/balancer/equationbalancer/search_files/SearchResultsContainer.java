package com.kharblabs.balancer.equationbalancer.search_files;

import java.util.ArrayList;

public class SearchResultsContainer {
    ArrayList<String> Names;
    ArrayList<String> Formulas;
    ArrayList<String> Masses;

    public SearchResultsContainer(ArrayList<String> names, ArrayList<String> formulas, ArrayList<String> masses) {
        Names = names;
        Formulas = formulas;
        Masses = masses;
    }

    public ArrayList<String> getNames() {
        return Names;
    }

    public ArrayList<String> getFormulas() {
        return Formulas;
    }

    public ArrayList<String> getMasses() {
        return Masses;
    }
}
