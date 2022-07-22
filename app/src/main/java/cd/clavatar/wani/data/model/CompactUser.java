package cd.clavatar.wani.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.util.Date;

import androidx.annotation.Nullable;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.utilities.Common;

public class CompactUser extends SugarRecord implements Parcelable {

    @Unique
    protected @Nullable
    String _id;
    protected Boolean status=true;
    protected Date created= WaniApp.getCurrentDate(), edited= WaniApp.getCurrentDate();;
    protected @Nullable Date  synced;

private
    int trys=3;
    private @Nullable String username, role;

    private People idPeople;

    private Location idLocation;

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

    public int getTrys() {
        return trys;
    }

    public void setTrys(int trys) {
        this.trys = trys;
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    @Nullable
    public String getRole() {
        return role;
    }

    public void setRole(@Nullable String role) {
        this.role = role;
    }

    public People getIdPeople() {
        return idPeople;
    }

    public void setIdPeople(People idPeople) {
        this.idPeople = idPeople;
    }

    public Location getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(Location idLocation) {
        this.idLocation = idLocation;
    }

    public static Creator<CompactUser> getCREATOR() {
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
        dest.writeInt(this.trys);
        dest.writeString(this.username);
        dest.writeString(this.role);
        dest.writeParcelable(this.idPeople, flags);
        dest.writeParcelable(this.idLocation, flags);
    }

    public CompactUser() {
    }

    protected CompactUser(Parcel in) {
        this._id = in.readString();
        this.status = (Boolean) in.readValue(Boolean.class.getClassLoader());
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        long tmpEdited = in.readLong();
        this.edited = tmpEdited == -1 ? null : new Date(tmpEdited);
        long tmpSynced = in.readLong();
        this.synced = tmpSynced == -1 ? null : new Date(tmpSynced);
        this.trys = in.readInt();
        this.username = in.readString();
        this.role = in.readString();
        this.idPeople = in.readParcelable(People.class.getClassLoader());
        this.idLocation = in.readParcelable(Location.class.getClassLoader());
    }

    public static final Creator<CompactUser> CREATOR = new Creator<CompactUser>() {
        @Override
        public CompactUser createFromParcel(Parcel source) {
            return new CompactUser(source);
        }

        @Override
        public CompactUser[] newArray(int size) {
            return new CompactUser[size];
        }
    };
}
