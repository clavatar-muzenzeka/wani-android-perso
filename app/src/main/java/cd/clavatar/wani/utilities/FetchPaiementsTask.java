package cd.clavatar.wani.utilities;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;
import cd.clavatar.wani.data.model.CompactPaiement;
import cd.clavatar.wani.data.model.CompactUser;

public class FetchPaiementsTask extends AsyncTask<CompactUser, Void, List<CompactPaiement>> {
    private final MutableLiveData<List<CompactPaiement>> rawPaiements;

    public FetchPaiementsTask(MutableLiveData<List<CompactPaiement>> rawPaiements) {
        this.rawPaiements=rawPaiements;
    }

    /* access modifiers changed from: protected */
    public List<CompactPaiement> doInBackground(CompactUser... user) {
        Class<CompactPaiement> cls = CompactPaiement.class;
        List<CompactPaiement> fcp = (Select.from(CompactPaiement.class).where(Condition.prop("ID_RECEIVER").eq(user[0].getId())).orderBy("EDITED DESC").list());


        Common.backgroundWork.postValue(false);
        this.rawPaiements.postValue(fcp);
        return fcp;
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(List<CompactPaiement> fetchedData) {
    }
}
