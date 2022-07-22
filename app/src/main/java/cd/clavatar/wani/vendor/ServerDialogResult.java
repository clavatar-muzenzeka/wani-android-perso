package cd.clavatar.wani.vendor;

import cd.clavatar.wani.data.model.VaccinOccurence;

/**
 * Created by Cl@v@t@r on 2020-02-23
 */
public class ServerDialogResult {
    boolean backup;
    int tag;
    int result;


    public ServerDialogResult(Boolean backup, int tag, int result) {
        this.backup = backup;
        this.tag = tag;
        this.result = result;
    }

    public boolean isBackup() {
        return backup;
    }

    public void setBackup(boolean backup) {
        this.backup = backup;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
