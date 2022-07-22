package cd.clavatar.wani.utilities;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cd.clavatar.wani.data.model.CompactCarnet;
import cd.clavatar.wani.data.model.CompactUser;

public class FetchCarnetsTask extends AsyncTask<CompactUser, Void, Integer> {
    private final MutableLiveData<List<CompactCarnet>> rawCarnets;

    public FetchCarnetsTask(MutableLiveData<List<CompactCarnet>> rawCarnets) {
        this.rawCarnets=rawCarnets;
    }

    /* access modifiers changed from: protected */
    public Integer doInBackground(CompactUser... user) {
        Log.i("zaza", String.format("liu %s", user[0].getUsername()));
        Class<CompactCarnet> cls = CompactCarnet.class;
        List<CompactCarnet> fcc = (CompactCarnet.findWithQuery(cls, String.format("SELECT DISTINCT * FROM COMPACT_CARNET  LEFT JOIN  COMPACT_STATUS ON COMPACT_CARNET.CARNET_STATUS = COMPACT_STATUS.ID WHERE COMPACT_CARNET.STORY IN (SELECT LOCALE_STORY_ID FROM CARNET_CHECK WHERE ID_PAIEMENT IN (SELECT ID FROM COMPACT_PAIEMENT WHERE ID_RECEIVER = %d)) OR COMPACT_CARNET.ID IN (SELECT LOCALE_CARNET_ID FROM VACCIN_OCCURENCE WHERE ID_PAIEMENT IN (SELECT ID FROM COMPACT_PAIEMENT WHERE ID_RECEIVER = %d)) OR COMPACT_CARNET.CARNET_STATUS IN (SELECT ID FROM COMPACT_STATUS WHERE AUTHOR = %d) ORDER BY COMPACT_STATUS.CREATED DESC", user[0].getId(), user[0].getId(), user[0].getId()), (String[]) null));

        rawCarnets.postValue(fcc);

        Common.backgroundWork.postValue(false);
        return new Integer(fcc.size());
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(Integer count) {
        Common.backgroundWork.postValue(false);
        Log.i("zaza", String.format("fetched %d carnets", count));
    }
}
