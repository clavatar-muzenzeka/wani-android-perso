package cd.clavatar.wani.data.model;

import android.os.Parcel;
import android.os.Parcelable;


import com.orm.dsl.Unique;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
public class User implements Parcelable {
private
    int trys=3;
    private @Nullable String username, role, idPeople, idLocation;

    @Unique
    private String _id;


    public User() {
        super();
    }

    public User(int trys, @Nullable String username, @Nullable String role, @Nullable String idPeople, @Nullable String idLocation, @Nullable String _id) {
        this.trys = trys;
        this.username = username;
        this.role = role;
        this.idPeople = idPeople;
        this.idLocation = idLocation;
        this._id = _id;
    }

    @Nullable
    public String get_id() {
        return _id;
    }

    public void set_id(@Nullable String _id) {
        this._id = _id;
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

    @Nullable
    public String getIdPeople() {
        return idPeople;
    }

    public void setIdPeople(@Nullable String idPeople) {
        this.idPeople = idPeople;
    }

    @Nullable
    public String getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(@Nullable String idLocation) {
        this.idLocation = idLocation;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.trys);
        dest.writeString(this.username);
        dest.writeString(this.role);
        dest.writeString(this.idPeople);
        dest.writeString(this.idLocation);
        dest.writeString(this._id);
    }

    protected User(Parcel in) {
        this.trys = in.readInt();
        this.username = in.readString();
        this.role = in.readString();
        this.idPeople = in.readString();
        this.idLocation = in.readString();
        this._id = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
