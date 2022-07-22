package cd.clavatar.wani.data.model;
import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.util.Date;

import androidx.annotation.Nullable;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.utilities.Common;

public class Location extends SugarRecord implements Parcelable {

    @Unique
    protected @Nullable String _id;
    protected Boolean status=true;
    protected Date created= WaniApp.getCurrentDate();

    //@Convert(converter = WaniDateConverter.class, columnType = Date.class)
    protected Date edited= WaniApp.getCurrentDate();;

    //@Convert(converter = WaniDateConverter.class, columnType = Date.class)
    protected @Nullable Date  synced;

    public Location(@Nullable String _id, Boolean status, Date created, Date edited, @Nullable Date synced, String adresse, String designation, String locationType, float lat, float lon) {
        super();
        this._id = _id;
        this.status = status;
        this.created = created;
        this.edited = edited;
        this.synced = synced;
        this.adresse = adresse;
        this.designation = designation;
        this.locationType = locationType;
        this.lat = lat;
        this.lon = lon;
    }



    String adresse, designation, locationType;
    float lat, lon;


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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public static Creator<Location> getCREATOR() {
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
        dest.writeString(this.adresse);
        dest.writeString(this.designation);
        dest.writeString(this.locationType);
        dest.writeFloat(this.lat);
        dest.writeFloat(this.lon);
    }

    public Location() {
    }

    protected Location(Parcel in) {
        this._id = in.readString();
        this.status = (Boolean) in.readValue(Boolean.class.getClassLoader());
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        long tmpEdited = in.readLong();
        this.edited = tmpEdited == -1 ? null : new Date(tmpEdited);
        long tmpSynced = in.readLong();
        this.synced = tmpSynced == -1 ? null : new Date(tmpSynced);
        this.adresse = in.readString();
        this.designation = in.readString();
        this.locationType = in.readString();
        this.lat = in.readFloat();
        this.lon = in.readFloat();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}
