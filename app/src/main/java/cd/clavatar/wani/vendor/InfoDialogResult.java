package cd.clavatar.wani.vendor;

/**
 * Created by Cl@v@t@r on 2020-02-23
 */
public class InfoDialogResult {
    int tag;
    int result;

    public InfoDialogResult() {
    }

    public InfoDialogResult(int tag, int result) {
        this.tag = tag;
        this.result = result;
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
