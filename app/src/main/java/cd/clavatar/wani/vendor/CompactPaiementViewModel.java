package cd.clavatar.wani.vendor;

import androidx.lifecycle.MutableLiveData;
import cd.clavatar.wani.data.model.CompactPaiement;

/**
 * Created by Cl@v@t@r on 2020-02-17
 */
public class CompactPaiementViewModel {

    public MutableLiveData<CompactPaiement> paiement=new MutableLiveData<>(null);

    public CompactPaiementViewModel(CompactPaiement cc){
        this.paiement.setValue(cc);
    }

    public MutableLiveData<CompactPaiement> getPaiement() {
        return paiement;
    }
}
