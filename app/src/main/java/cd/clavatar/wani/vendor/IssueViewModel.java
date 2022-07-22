package cd.clavatar.wani.vendor;

import androidx.lifecycle.MutableLiveData;

/**
 * Created by Cl@v@t@r on 2020-02-17
 */
public class IssueViewModel {

    public MutableLiveData<String> issue=new MutableLiveData<>(null);

    public MutableLiveData<String> getIssue() {
        return issue;
    }

    public void setIssue(MutableLiveData<String> issue) {
        this.issue = issue;
    }
}
