package cd.clavatar.wani.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.util.Date;

import androidx.annotation.Nullable;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.utilities.Common;

public class Vaccin extends SugarRecord implements Parcelable {

    @Unique
    protected @Nullable
    String _id;
    protected Boolean status=true;
    protected Date created= WaniApp.getCurrentDate(), edited= WaniApp.getCurrentDate();;
    protected @Nullable Date  synced;


    int expiration;
    Boolean required;
    private String designation;


    PaimentType idPaiementType;


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

    public void setSynced(@Nullable Date synced) { if(synced!=null) this.edited= Common.generatedEdidtedFromSynced(synced);
        this.synced = synced;
    }

    public int getExpiration() {
        return expiration;
    }

    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public PaimentType getIdPaiementType() {
        return idPaiementType;
    }

    public void setIdPaiementType(PaimentType idPaiementType) {
        this.idPaiementType = idPaiementType;
    }

    public static Creator<Vaccin> getCREATOR() {
        return CREATOR;
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
        dest.writeInt(this.expiration);
        dest.writeValue(this.required);
        dest.writeString(this.designation);
        dest.writeParcelable(this.idPaiementType, flags);
    }

    public Vaccin() {
    }

    protected Vaccin(Parcel in) {
        this._id = in.readString();
        this.status = (Boolean) in.readValue(Boolean.class.getClassLoader());
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        long tmpEdited = in.readLong();
        this.edited = tmpEdited == -1 ? null : new Date(tmpEdited);
        long tmpSynced = in.readLong();
        this.synced = tmpSynced == -1 ? null : new Date(tmpSynced);
        this.expiration = in.readInt();
        this.required = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.designation = in.readString();
        this.idPaiementType = in.readParcelable(PaimentType.class.getClassLoader());
    }

    public static final Creator<Vaccin> CREATOR = new Creator<Vaccin>() {
        @Override
        public Vaccin createFromParcel(Parcel source) {
            return new Vaccin(source);
        }

        @Override
        public Vaccin[] newArray(int size) {
            return new Vaccin[size];
        }
    };
}
