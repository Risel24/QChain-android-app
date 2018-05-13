package io.rsl.pragma.screens.contracts.create;

public class ContractDescription {

    private String title;
    private String description;
    private String fragmentName;

    public ContractDescription(String fragmentName, String title, String description) {
        this.fragmentName = fragmentName;
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFragmentName() {
        return fragmentName;
    }
}
