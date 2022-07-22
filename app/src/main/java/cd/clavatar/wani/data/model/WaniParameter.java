package cd.clavatar.wani.data.model;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.util.Date;

import androidx.annotation.Nullable;
import cd.clavatar.wani.WaniApp;

public class WaniParameter extends SugarRecord {
    @Unique
    protected @Nullable
    String _id;
    protected Boolean status=true;
    protected Date created= WaniApp.getCurrentDate(), edited= WaniApp.getCurrentDate();;
    protected @Nullable Date  synced;

    private
    int maxRawCarnetChecks;
    double maxAccount;

    public WaniParameter(int maxRawCarnetChecks, double maxAccount) {
        this.maxRawCarnetChecks = maxRawCarnetChecks;
        this.maxAccount = maxAccount;
    }

    public WaniParameter() {
    }

    public int getMaxRawCarnetChecks() {
        return maxRawCarnetChecks;
    }

    public double getMaxAccount() {
        return maxAccount;
    }

    @Nullable
    public String get_id() {
        return _id;
    }

    public void set_id(@Nullable String _id) {
        this._id = _id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getEdited() {
        return edited;
    }

    public void setEdited(Date edited) {
        this.edited = edited;
    }

    @Nullable
    public Date getSynced() {
        return synced;
    }

    public void setSynced(@Nullable Date synced) {
        this.synced = synced;
    }

    public void setMaxRawCarnetChecks(int maxRawCarnetChecks) {
        this.maxRawCarnetChecks = maxRawCarnetChecks;
    }

    public void setMaxAccount(double maxAccount) {
        this.maxAccount = maxAccount;
    }
}
