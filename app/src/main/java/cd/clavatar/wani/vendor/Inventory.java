package cd.clavatar.wani.vendor;

import java.util.ArrayList;
import java.util.List;

import cd.clavatar.wani.data.model.Check;

public class Inventory {
    boolean valid;
    List<String> issues=new ArrayList<>();;
    Check lastCheck;

    public Inventory() {
        issues=new ArrayList<>();
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getIssues() {
        return issues;
    }

    public void setIssues(List<String> issues) {
        this.issues = issues;
    }

    public void addIssue(String issue){
        this.issues.add(issue);
    }

    public Check getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(Check lastCheck) {
        this.lastCheck = lastCheck;
    }
}