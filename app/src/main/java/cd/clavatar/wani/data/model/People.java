package cd.clavatar.wani.data.model;

import
        android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.util.Date;

import androidx.annotation.Nullable;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.utilities.Common;

public class People extends SugarRecord implements Parcelable {
    @Unique
    protected @Nullable String _id;
    protected Boolean status=true;
    protected Date created= WaniApp.getCurrentDate(), edited= WaniApp.getCurrentDate();;
    protected @Nullable Date  synced;

    String name
            ,
            lastName
            ,
            firstName
            ,
            sex	,
            adress
            ,
            tel,
            email
            ,
    passportNumber,
            nationality,
            idProfile
                    ;

    private Long localeIdProfile;

    Date birthDate;

    public People(@Nullable String _id, Boolean status, Date created, Date edited, @Nullable Date synced, String name, String lastName, String firstName, String sex, String adress, String tel, String email, String passportNumber, String nationality, String idProfile, Long localeIdProfile, Date birthDate) {
        this._id = _id;
        this.status = status;
        this.created = created;
        this.edited = edited;
        this.synced = synced;
        this.name = name;
        this.lastName = lastName;
        this.firstName = firstName;
        this.sex = sex;
        this.adress = adress;
        this.tel = tel;
        this.email = email;
        this.passportNumber = passportNumber;
        this.nationality = nationality;
        this.idProfile = idProfile;
        this.localeIdProfile = localeIdProfile;
        this.birthDate = birthDate;
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
        dest.writeString(this.name);
        dest.writeString(this.lastName);
        dest.writeString(this.firstName);
        dest.writeString(this.sex);
        dest.writeString(this.adress);
        dest.writeString(this.tel);
        dest.writeString(this.email);
        dest.writeString(this.passportNumber);
        dest.writeString(this.nationality);
        dest.writeString(this.idProfile);
        dest.writeValue(this.localeIdProfile);
        dest.writeLong(this.birthDate != null ? this.birthDate.getTime() : -1);
    }

    public People() {
    }

    protected People(Parcel in) {
        this._id = in.readString();
        this.status = (Boolean) in.readValue(Boolean.class.getClassLoader());
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        long tmpEdited = in.readLong();
        this.edited = tmpEdited == -1 ? null : new Date(tmpEdited);
        long tmpSynced = in.readLong();
        this.synced = tmpSynced == -1 ? null : new Date(tmpSynced);
        this.name = in.readString();
        this.lastName = in.readString();
        this.firstName = in.readString();
        this.sex = in.readString();
        this.adress = in.readString();
        this.tel = in.readString();
        this.email = in.readString();
        this.passportNumber = in.readString();
        this.nationality = in.readString();
        this.idProfile = in.readString();
        this.localeIdProfile = (Long) in.readValue(Long.class.getClassLoader());
        long tmpBirthDate = in.readLong();
        this.birthDate = tmpBirthDate == -1 ? null : new Date(tmpBirthDate);
    }

    public static final Creator<People> CREATOR = new Creator<People>() {
        @Override
        public People createFromParcel(Parcel source) {
            return new People(source);
        }

        @Override
        public People[] newArray(int size) {
            return new People[size];
        }
    };

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getIdProfile() {
        return idProfile;
    }

    public void setIdProfile(String idProfile) {
        this.idProfile = idProfile;
    }

    public Long getLocaleIdProfile() {
        return localeIdProfile;
    }

    public void setLocaleIdProfile(Long localeIdProfile) {
        this.localeIdProfile = localeIdProfile;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public static Creator<People> getCREATOR() {
        return CREATOR;
    }
}
