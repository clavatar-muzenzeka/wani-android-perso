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
 * Created by Cl@v@t@r on 2020-02-19
 */
public class VaccinOccurence extends SugarRecord implements Parcelable {

@Unique
    protected @Nullable
    String _id;
    protected Boolean status=true;
    protected Date created= WaniApp.getCurrentDate(), edited= WaniApp.getCurrentDate();;
    protected @Nullable Date  synced;

    private Date received;

    private Vaccin idVaccin;

    private Long localeCarnetId;

    CompactPaiement idPaiement;
    private String idLotVaccin ;

    public VaccinOccurence(@Nullable String _id, Boolean status, Date created, Date edited, @Nullable Date synced, Date received, Vaccin idVaccin, Long localeCarnetId, CompactPaiement idPaiement, String idLotVaccin) {
        this._id = _id;
        this.status = status;
        this.created = created;
        this.edited = edited;
        this.synced = synced;
        this.received = received;
        this.idVaccin = idVaccin;
        this.localeCarnetId = localeCarnetId;
        this.idPaiement = idPaiement;
        this.idLotVaccin = idLotVaccin;
    }

    @Nullable
    public String get_id() {
        return _id;
    }

    public Long getLocaleCarnetId() {
        return localeCarnetId;
    }

    public void setLocaleCarnetId(Long localeCarnetId) {
        this.localeCarnetId = localeCarnetId;
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

    public Date getReceived() {
        return received;
    }

    public void setReceived(Date received) {
        this.received = received;
    }

    public Vaccin getIdVaccin() {
        return idVaccin;
    }

    public void setIdVaccin(Vaccin idVaccin) {
        this.idVaccin = idVaccin;
    }

    public CompactPaiement getIdPaiement() {
        return idPaiement;
    }

    public void setIdPaiement(CompactPaiement idPaiement) {
        this.idPaiement = idPaiement;
    }

    public String getIdLotVaccin() {
        return idLotVaccin;
    }

    public void setIdLotVaccin(String idLotVaccin) {
        this.idLotVaccin = idLotVaccin;
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
        dest.writeLong(this.received != null ? this.received.getTime() : -1);
        dest.writeParcelable(this.idVaccin, flags);
        dest.writeValue(this.localeCarnetId);
        dest.writeParcelable(this.idPaiement, flags);
        dest.writeString(this.idLotVaccin);
    }

    public VaccinOccurence() {
    }

    protected VaccinOccurence(Parcel in) {
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
        this.localeCarnetId = (Long) in.readValue(Long.class.getClassLoader());
        this.idPaiement = in.readParcelable(CompactPaiement.class.getClassLoader());
        this.idLotVaccin = in.readString();
    }

    public static final Creator<VaccinOccurence> CREATOR = new Creator<VaccinOccurence>() {
        @Override
        public VaccinOccurence createFromParcel(Parcel source) {
            return new VaccinOccurence(source);
        }

        @Override
        public VaccinOccurence[] newArray(int size) {
            return new VaccinOccurence[size];
        }
    };
}
