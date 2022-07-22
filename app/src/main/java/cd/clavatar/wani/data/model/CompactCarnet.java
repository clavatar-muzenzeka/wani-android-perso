package cd.clavatar.wani.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Unique;
import com.orm.query.Condition;
import com.orm.query.Select;
import java.util.Date;
import java.util.List;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.utilities.Common;

/* renamed from: cd.clavatar.wani.data.model.CompactCarnet */
public class CompactCarnet extends SugarRecord implements Parcelable {
    public static final Creator<CompactCarnet> CREATOR = new Creator<CompactCarnet>() {
        public CompactCarnet createFromParcel(Parcel source) {
            return new CompactCarnet(source);
        }

        public CompactCarnet[] newArray(int size) {
            return new CompactCarnet[size];
        }
    };
    @Unique
    protected String _id;
    private int carnetPurpose;
    CompactStatus carnetStatus;
    protected Date created = WaniApp.getCurrentDate();
    protected Date edited = WaniApp.getCurrentDate();
    People idPeople;
    String idRawCarnet;
    String idiCarnet;
    private Long localePictureRawCarnet;
    String pictureRawCarnet;
    protected Boolean status = true;
    Story story;
    protected Date synced;
    @Ignore
    List<VaccinOccurence> vaccins;

    public String get_id() {
        return this._id;
    }

    public void set_id(String _id2) {
        this._id = _id2;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public int getCarnetPurpose() {
        return this.carnetPurpose;
    }

    public void setCarnetPurpose(int carnetPurpose2) {
        this.carnetPurpose = carnetPurpose2;
    }

    public void setStatus(Boolean status2) {
        this.status = status2;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created2) {
        this.created = created2;
    }

    public Date getEdited() {
        return this.edited;
    }

    public void setEdited(Date edited2) {
        this.edited = edited2;
    }

    public Date getSynced() {
        return this.synced;
    }

    public void setSynced(Date synced2) {
        if (synced2 != null) {
            this.edited = Common.generatedEdidtedFromSynced(synced2);
        }
        this.synced = synced2;
    }

    public String getIdiCarnet() {
        return this.idiCarnet;
    }

    public void setIdiCarnet(String idiCarnet2) {
        this.idiCarnet = idiCarnet2;
    }

    public String getIdRawCarnet() {
        return this.idRawCarnet;
    }

    public void setIdRawCarnet(String idRawCarnet2) {
        this.idRawCarnet = idRawCarnet2;
    }

    public String getPictureRawCarnet() {
        return this.pictureRawCarnet;
    }

    public void setPictureRawCarnet(String pictureRawCarnet2) {
        this.pictureRawCarnet = pictureRawCarnet2;
    }

    public Long getLocalePictureRawCarnet() {
        return this.localePictureRawCarnet;
    }

    public void setLocalePictureRawCarnet(Long localePictureRawCarnet2) {
        this.localePictureRawCarnet = localePictureRawCarnet2;
    }

    public People getIdPeople() {
        return this.idPeople;
    }

    public void setIdPeople(People idPeople2) {

        this.idPeople = idPeople2;
    }

    public Story getStory() {
        return this.story;
    }

    public void setStory(Story story2) {
        this.story = story2;
    }

    public CompactStatus getCarnetStatus() {
        return this.carnetStatus;
    }

    public void setCarnetStatus(CompactStatus carnetStatus2) {
        this.carnetStatus = carnetStatus2;
    }

    public List<VaccinOccurence> getVaccins() {
        List<VaccinOccurence> list;
        if (getId() != null && ((list = this.vaccins) == null || (list != null && list.size() == 0))) {
            this.vaccins = Select.from(VaccinOccurence.class).where(Condition.prop("LOCALE_CARNET_ID").eq(getId())).list();
        }
        return this.vaccins;
    }

    public void setVaccins(List<VaccinOccurence> vaccins2) {
        this.vaccins = vaccins2;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeValue(this.status);
        dest.writeInt(this.carnetPurpose);
        Date date = this.created;
        long j = -1;
        dest.writeLong(date != null ? date.getTime() : -1);
        Date date2 = this.edited;
        dest.writeLong(date2 != null ? date2.getTime() : -1);
        Date date3 = this.synced;
        if (date3 != null) {
            j = date3.getTime();
        }
        dest.writeLong(j);
        dest.writeString(this.idiCarnet);
        dest.writeString(this.idRawCarnet);
        dest.writeString(this.pictureRawCarnet);
        dest.writeValue(this.localePictureRawCarnet);
        dest.writeParcelable(this.idPeople, flags);
        dest.writeParcelable(this.story, flags);
        dest.writeParcelable(this.carnetStatus, flags);
        dest.writeTypedList(getVaccins());
    }

    public CompactCarnet() {
    }

    protected CompactCarnet(Parcel in) {
        this._id = in.readString();
        this.status = (Boolean) in.readValue(Boolean.class.getClassLoader());
        long tmpCreated = in.readLong();
        Date date = null;
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        long tmpEdited = in.readLong();
        this.edited = tmpEdited == -1 ? null : new Date(tmpEdited);
        long tmpSynced = in.readLong();
        this.synced = tmpSynced != -1 ? new Date(tmpSynced) : date;
        this.idiCarnet = in.readString();
        this.idRawCarnet = in.readString();
        this.carnetPurpose = in.readInt();
        this.pictureRawCarnet = in.readString();
        this.localePictureRawCarnet = (Long) in.readValue(Long.class.getClassLoader());
        this.idPeople = (People) in.readParcelable(People.class.getClassLoader());
        this.story = (Story) in.readParcelable(Story.class.getClassLoader());
        this.carnetStatus = (CompactStatus) in.readParcelable(CompactStatus.class.getClassLoader());
        this.vaccins = in.createTypedArrayList(VaccinOccurence.CREATOR);
    }

    public CompactCarnet(String _id2, Boolean status2, Date created2, Date edited2, Date synced2, String idiCarnet2, int carnetPurpose2, String idRawCarnet2, String pictureRawCarnet2, Long localePictureRawCarnet2, People idPeople2, Story story2, CompactStatus carnetStatus2, List<VaccinOccurence> vaccins2) {
        this._id = _id2;
        this.status = status2;
        this.created = created2;
        this.edited = edited2;
        this.synced = synced2;
        this.idiCarnet = idiCarnet2;
        this.idRawCarnet = idRawCarnet2;
        this.pictureRawCarnet = pictureRawCarnet2;
        this.localePictureRawCarnet = localePictureRawCarnet2;
        this.idPeople = idPeople2;
        this.story = story2;
        this.carnetStatus = carnetStatus2;
        this.vaccins = vaccins2;
        this.carnetPurpose = carnetPurpose2;
    }
}
