package cd.clavatar.wani.data.model;

import android.os.Parcel;

import com.orm.dsl.Unique;

import java.util.Date;

import androidx.annotation.Nullable;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.utilities.Common;

/**
 * Created by Cl@v@t@r on 2020-02-19
 */
public class CompactVaccinOccurence extends VaccinOccurence {
    @Unique
    protected @Nullable
    String _id;
    protected Boolean status=true;
    protected Date created= WaniApp.getCurrentDate(), edited= WaniApp.getCurrentDate();;
    protected @Nullable Date  synced;

    private Date received;

    private Vaccin idVaccin;


    @Override
    @Nullable
    public String get_id() {
        return _id;
    }

    @Override
    public void set_id(@Nullable String _id) {
        this._id = _id;
    }

    @Override
    public Boolean getStatus() {
        return status;
    }

    @Override
    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public Date getEdited() {
        return edited;
    }

    @Override
    public void setEdited(Date edited) {
        this.edited = edited;
    }

    @Override
    @Nullable
    public Date getSynced() {
        return synced;
    }

    @Override
    public void setSynced(@Nullable Date synced) { if(synced!=null) this.edited= Common.generatedEdidtedFromSynced(synced);
        this.synced = synced;
    }

    @Override
    public Date getReceived() {
        return received;
    }

    @Override
    public void setReceived(Date received) {
        this.received = received;
    }

    @Override
    public Vaccin getIdVaccin() {
        return idVaccin;
    }

    @Override
    public void setIdVaccin(Vaccin idVaccin) {
        this.idVaccin = idVaccin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this._id);
        dest.writeValue(this.status);
        dest.writeLong(this.created != null ? this.created.getTime() : -1);
        dest.writeLong(this.edited != null ? this.edited.getTime() : -1);
        dest.writeLong(this.synced != null ? this.synced.getTime() : -1);
        dest.writeLong(this.received != null ? this.received.getTime() : -1);
        dest.writeParcelable(this.idVaccin, flags);
    }

    public CompactVaccinOccurence() {
    }

    protected CompactVaccinOccurence(Parcel in) {
        super(in);
        this._id = in.readString();
        this.status = (Boolean) in.readValue(Boolean.class.getClassLoader());
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        long tmpEdited = in.readLong();
        this.edited = tmpEdited == -1 ? null : new Date(tmpEdited);
        long tmpSynced = in.readLong();
        this.synced = tmpSynced == -1 ? null : new Date(tmpSynced);
        long tmpReceived = in.readLong();
        this.received = tmpReceived == -1 ? null : new Date(tmpReceived);
        this.idVaccin = in.readParcelable(Vaccin.class.getClassLoader());
    }

    public static final Creator<CompactVaccinOccurence> CREATOR = new Creator<CompactVaccinOccurence>() {
        @Override
        public CompactVaccinOccurence createFromParcel(Parcel source) {
            return new CompactVaccinOccurence(source);
        }

        @Override
        public CompactVaccinOccurence[] newArray(int size) {
            return new CompactVaccinOccurence[size];
        }
    };
}
