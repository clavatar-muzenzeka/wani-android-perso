package cd.clavatar.wani.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.orm.SugarRecord;
import com.orm.dsl.Table;
import com.orm.dsl.Unique;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.utilities.Common;

/**
 * Created by Cl@v@t@r on 2020-01-30
 */
@Table(name = "CARNET_CHECK")
public class Check extends SugarRecord implements Parcelable {

    @Unique
    protected @Nullable
    String _id;
    protected Boolean status=true;
    protected Date created= WaniApp.getCurrentDate(), edited= WaniApp.getCurrentDate();;
    protected @Nullable Date  synced;

    private Long localeStoryId;

    CompactPaiement idPaiement;

    public Check() {
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
        if(synced!=null) this.edited= Common.generatedEdidtedFromSynced(synced);
        this.synced = synced;
    }

    public CompactPaiement getIdPaiement() {
        return idPaiement;
    }

    public void setIdPaiement(CompactPaiement idPaiement) {
        this.idPaiement = idPaiement;
    }


    public Long getLocaleStoryId() {
        return localeStoryId;
    }

    public void setLocaleStoryId(Long localeStoryId) {
        this.localeStoryId = localeStoryId;
    }

    public Check(@Nullable String _id, Boolean status, Date created, Date edited, @Nullable Date synced, Long localeStoryId, CompactPaiement idPaiement) {
        this._id = _id;
        this.status = status;
        this.created = created;
        this.edited = edited;
        this.synced = synced;
        this.localeStoryId = localeStoryId;
        this.idPaiement = idPaiement;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeValue(this.status);
        dest.writeLong(this.created != null ? this.created.getTime() : -1);
        dest.writeLong(this.edited != null ? this.edited.getTime() : -1);
        dest.writeLong(this.synced != null ? this.synced.getTime() : -1);
        dest.writeValue(this.localeStoryId);
        dest.writeParcelable(this.idPaiement, flags);
    }

    protected Check(Parcel in) {
        this._id = in.readString();
        this.status = (Boolean) in.readValue(Boolean.class.getClassLoader());
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        long tmpEdited = in.readLong();
        this.edited = tmpEdited == -1 ? null : new Date(tmpEdited);
        long tmpSynced = in.readLong();
        this.synced = tmpSynced == -1 ? null : new Date(tmpSynced);
        this.localeStoryId = (Long) in.readValue(Long.class.getClassLoader());
        this.idPaiement = in.readParcelable(CompactPaiement.class.getClassLoader());
    }

    public static final Creator<Check> CREATOR = new Creator<Check>() {
        @Override
        public Check createFromParcel(Parcel source) {
            return new Check(source);
        }

        @Override
        public Check[] newArray(int size) {
            return new Check[size];
        }
    };
}