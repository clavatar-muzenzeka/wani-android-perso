package cd.clavatar.wani.vendor;

import cd.clavatar.wani.data.model.VaccinOccurence;

/**
 * Created by Cl@v@t@r on 2020-02-23
 */
public class VaccinDialogResult {
    VaccinOccurence vaccinOccurence;
    int tag;
    int result;


    public VaccinDialogResult(VaccinOccurence vaccinOccurence, int tag, int result) {
        this.vaccinOccurence = vaccinOccurence;
        this.tag = tag;
        this.result = result;
    }

    public VaccinOccurence getVaccinOccurence() {
        return vaccinOccurence;
    }

    public void setVaccinOccurence(VaccinOccurence vaccinOccurence) {
        this.vaccinOccurence = vaccinOccurence;
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
