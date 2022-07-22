package cd.clavatar.wani.data.model;

import android.os.Parcel;
import android.os.Parcelable;


import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.util.Date;

import androidx.annotation.Nullable;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.utilities.Common;


/**
 * Created by Cl@v@t@r on 2020-01-30
 */
public class CompactStatus extends SugarRecord implements Parcelable {
    @Unique
    protected @Nullable
    String _id;
    protected Boolean status=true;
    protected Date created= WaniApp.getCurrentDate(), edited= WaniApp.getCurrentDate();;
    protected @Nullable Date  synced;

    private Long localeStoryId;





    String designation;
    int distributionStatus, typeStatus;


    Location idLocation;

    CompactUser author;

    CompactPaiement idPaiement;

    public CompactStatus() {
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

    public void setSynced(@Nullable Date synced) { if(synced!=null) this.edited= Common.generatedEdidtedFromSynced(synced);
        this.synced = synced;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public int getDistributionStatus() {
        return distributionStatus;
    }

    public void setDistributionStatus(int distributionStatus) {
        this.distributionStatus = distributionStatus;
    }

    public int getTypeStatus() {
        return typeStatus;
    }

    public void setTypeStatus(int typeStatus) {
        this.typeStatus = typeStatus;
    }

    public Location getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(Location idLocation) {
        this.idLocation = idLocation;
    }

    public CompactUser getAuthor() {
        return author;
    }

    public void setAuthor(CompactUser author) {
        this.author = author;
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

    public CompactStatus(@Nullable String _id, Boolean status, Date created, Date edited, @Nullable Date synced, Long localeStoryId, String designation, int distributionStatus, int typeStatus, Location idLocation, CompactUser author, CompactPaiement idPaiement) {
        this._id = _id;
        this.status = status;
        this.created = created;
        this.edited = edited;
        this.synced = synced;
        this.localeStoryId = localeStoryId;
        this.designation = designation;
        this.distributionStatus = distributionStatus;
        this.typeStatus = typeStatus;
        this.idLocation = idLocation;
        this.author = author;
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
        dest.writeString(this.designation);
        dest.writeInt(this.distributionStatus);
        dest.writeInt(this.typeStatus);
        dest.writeParcelable(this.idLocation, flags);
        dest.writeParcelable(this.author, flags);
        dest.writeParcelable(this.idPaiement, flags);
    }

    protected CompactStatus(Parcel in) {
        this._id = in.readString();
        this.status = (Boolean) in.readValue(Boolean.class.getClassLoader());
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        long tmpEdited = in.readLong();
        this.edited = tmpEdited == -1 ? null : new Date(tmpEdited);
        long tmpSynced = in.readLong();
        this.synced = tmpSynced == -1 ? null : new Date(tmpSynced);
        this.localeStoryId = (Long) in.readValue(Long.class.getClassLoader());
        this.designation = in.readString();
        this.distributionStatus = in.readInt();
        this.typeStatus = in.readInt();
        this.idLocation = in.readParcelable(Location.class.getClassLoader());
        this.author = in.readParcelable(CompactUser.class.getClassLoader());
        this.idPaiement = in.readParcelable(CompactPaiement.class.getClassLoader());
    }

    public static final Creator<CompactStatus> CREATOR = new Creator<CompactStatus>() {
        @Override
        public CompactStatus createFromParcel(Parcel source) {
            return new CompactStatus(source);
        }

        @Override
        public CompactStatus[] newArray(int size) {
            return new CompactStatus[size];
        }
    };
}