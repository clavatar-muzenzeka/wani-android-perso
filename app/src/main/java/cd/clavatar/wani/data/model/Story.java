package cd.clavatar.wani.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Unique;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;
import java.util.Date;

import androidx.annotation.Nullable;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.utilities.Common;

/**
 * Created by Cl@v@t@r on 2020-01-30
 */
public class Story extends SugarRecord implements Parcelable {
    @Unique
    protected @Nullable
    String _id;
    protected Boolean status=true;
    protected Date created= WaniApp.getCurrentDate(), edited= WaniApp.getCurrentDate();;
    protected @Nullable Date  synced;

    @Ignore
    List<CompactStatus> statusStory;

    @Ignore
    List<Check> checkStory;


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



    public List<CompactStatus> getStatusStory() {
        if(this.getId() != null && (statusStory==null || (statusStory !=null && statusStory.size()==0)) )statusStory= Select.from(CompactStatus.class).where(Condition.prop("LOCALE_STORY_ID").eq(this.getId())).list();
        return statusStory;
    }

    public void setStatusStory(List<CompactStatus> statusStory) {
        this.statusStory = statusStory;
    }

    public List<Check> getCheckStory() {
        if(this.getId() != null && (checkStory==null || (checkStory !=null && checkStory.size()==0)) )checkStory= Select.from(Check.class).where(Condition.prop("LOCALE_STORY_ID").eq(this.getId())).list();
        return checkStory;
    }

    public void setCheckStory(List<Check> checkStory) {
        this.checkStory = checkStory;
    }

    public static Creator<Story> getCREATOR() {
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
        dest.writeTypedList(this.getStatusStory());
        dest.writeTypedList(this.getCheckStory());
    }

    public Story() {
    }

    protected Story(Parcel in) {
        this._id = in.readString();
        this.status = (Boolean) in.readValue(Boolean.class.getClassLoader());
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        long tmpEdited = in.readLong();
        this.edited = tmpEdited == -1 ? null : new Date(tmpEdited);
        long tmpSynced = in.readLong();
        this.synced = tmpSynced == -1 ? null : new Date(tmpSynced);
        this.statusStory = in.createTypedArrayList(CompactStatus.CREATOR);
        this.checkStory = in.createTypedArrayList(Check.CREATOR);
    }

    public static final Creator<Story> CREATOR = new Creator<Story>() {
        @Override
        public Story createFromParcel(Parcel source) {
            return new Story(source);
        }

        @Override
        public Story[] newArray(int size) {
            return new Story[size];
        }
    };
}
