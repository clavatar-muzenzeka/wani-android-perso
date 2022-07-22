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
public class CompactPaiement extends SugarRecord implements Parcelable {
    @Unique
    protected @Nullable
    String _id;
    protected Boolean status=true;
    protected Date created= WaniApp.getCurrentDate(), edited= WaniApp.getCurrentDate();;
    protected @Nullable Date  synced;


    String paiementMethod;

    private double paiementPrice;

    CompactEncaissement encaissed;

    CompactUser idReceiver;


    People idPayer;

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

    public String getPaiementMethod() {
        return paiementMethod;
    }

    public void setPaiementMethod(String paiementMethod) {
        this.paiementMethod = paiementMethod;
    }

    public double getPaiementPrice() {
        return paiementPrice;
    }

    public void setPaiementPrice(double paiementPrice) {
        this.paiementPrice = paiementPrice;
    }

    public CompactEncaissement getEncaissed() {
        return encaissed;
    }

    public void setEncaissed(CompactEncaissement encaissed) {
        this.encaissed = encaissed;
    }

    public CompactUser getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(CompactUser idReceiver) {
        this.idReceiver = idReceiver;
    }

    public People getIdPayer() {
        return idPayer;
    }

    public void setIdPayer(People idPayer) {
        this.idPayer = idPayer;
    }

    public PaimentType getIdPaiementType() {
        return idPaiementType;
    }

    public void setIdPaiementType(PaimentType idPaiementType) {
        this.idPaiementType = idPaiementType;
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
        dest.writeString(this.paiementMethod);
        dest.writeDouble(this.paiementPrice);
        dest.writeParcelable(this.encaissed, flags);
        dest.writeParcelable(this.idReceiver, flags);
        dest.writeParcelable(this.idPayer, flags);
        dest.writeParcelable(this.idPaiementType, flags);
    }

    public CompactPaiement() {
    }

    protected CompactPaiement(Parcel in) {
        this._id = in.readString();
        this.status = (Boolean) in.readValue(Boolean.class.getClassLoader());
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        long tmpEdited = in.readLong();
        this.edited = tmpEdited == -1 ? null : new Date(tmpEdited);
        long tmpSynced = in.readLong();
        this.synced = tmpSynced == -1 ? null : new Date(tmpSynced);
        this.paiementMethod = in.readString();
        this.paiementPrice = in.readDouble();
        this.encaissed = in.readParcelable(CompactEncaissement.class.getClassLoader());
        this.idReceiver = in.readParcelable(CompactUser.class.getClassLoader());
        this.idPayer = in.readParcelable(People.class.getClassLoader());
        this.idPaiementType = in.readParcelable(PaimentType.class.getClassLoader());
    }

    public static final Creator<CompactPaiement> CREATOR = new Creator<CompactPaiement>() {
        @Override
        public CompactPaiement createFromParcel(Parcel source) {
            return new CompactPaiement(source);
        }

        @Override
        public CompactPaiement[] newArray(int size) {
            return new CompactPaiement[size];
        }
    };
}