package cd.clavatar.wani.vendor;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/* renamed from: cd.clavatar.wani.vendor.MainToCarnetActivityRoutingBundle */
public class MainToCarnetActivityRoutingBundle implements Parcelable {
    public static final int CHECK = 5;
    public static final int CONSIGNATION = 4;
    public static final int CREATION = 1;
    public static final Creator<MainToCarnetActivityRoutingBundle> CREATOR = new Creator<MainToCarnetActivityRoutingBundle>() {
        public MainToCarnetActivityRoutingBundle createFromParcel(Parcel source) {
            return new MainToCarnetActivityRoutingBundle(source);
        }

        public MainToCarnetActivityRoutingBundle[] newArray(int size) {
            return new MainToCarnetActivityRoutingBundle[size];
        }
    };
    public static final int DUPLICATION = 3;
    public static final int NO_ARGS = -1;
    public static final int NUMERISATION = 0;
    public static final int SWITCH_RAW_TO_NUMERIC = 2;
    private String carnetId;
    private Long carnetLocaleId;
    private int creationMode;
    private Parcelable extras;
    private int mode;
    private String rawCarnetId;

    /* renamed from: cd.clavatar.wani.vendor.MainToCarnetActivityRoutingBundle$IRoutingExtraBundle */
    public interface IRoutingExtraBundle extends Parcelable {
        public static final int ROUTING_BUNDLE_TYPE_CREATION_MODE = 0;

        int getExtraBundleType();
    }

    public MainToCarnetActivityRoutingBundle() {
        this.creationMode = 0;
        this.mode = -1;
    }

    public String getCarnetId() {
        return this.carnetId;
    }

    public void setCarnetId(String carnetId2) {
        this.carnetId = carnetId2;
    }

    public String getRawCarnetId() {
        return this.rawCarnetId;
    }

    public void setRawCarnetId(String rawCarnetId2) {
        this.rawCarnetId = rawCarnetId2;
    }

    public Long getCarnetLocaleId() {
        return this.carnetLocaleId;
    }

    public void setCarnetLocaleId(Long carnetLocaleId2) {
        this.carnetLocaleId = carnetLocaleId2;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode2) {
        this.mode = mode2;
    }

    public int getCreationMode() {
        return this.creationMode;
    }

    public void setCreationMode(int creationMode2) {
        this.creationMode = creationMode2;
    }

    public void switchToMode(int newMode, String mCarnetId, String mRawCarnetId, Long mCompactCarnet) throws Exception {
        if (newMode != 0) {
            if (newMode != 1) {
                if (newMode != 2) {
                    if (newMode != 3) {
                        if (newMode != 4) {
                            if (newMode == 5) {
                                if (mCompactCarnet != null) {
                                    this.mode = newMode;
                                    this.carnetLocaleId = mCompactCarnet;
                                    this.carnetId = null;
                                    this.rawCarnetId = null;
                                    return;
                                }
                                throw new Exception("carnet needed for check mode");
                            }
                        } else if (mRawCarnetId != null) {
                            this.mode = newMode;
                            this.rawCarnetId = mRawCarnetId;
                            this.carnetLocaleId = null;
                            this.carnetId = null;
                        } else {
                            throw new Exception("rawCarnetId needed for consignation mode");
                        }
                    } else if (mCarnetId == null) {
                        throw new Exception("carnetId needed for dupliaction mode");
                    } else if (mCompactCarnet != null) {
                        this.mode = newMode;
                        this.carnetId = mCarnetId;
                        this.carnetLocaleId = mCompactCarnet;
                        this.rawCarnetId = null;
                    } else {
                        throw new Exception("carnet needed for dupliaction mode");
                    }
                } else if (mCompactCarnet == null) {
                    throw new Exception("carnet needed for raw to numeric switch mode");
                } else if (mCarnetId != null) {
                    this.mode = newMode;
                    this.carnetId = mCarnetId;
                    this.carnetLocaleId = mCompactCarnet;
                    this.rawCarnetId = null;
                } else {
                    throw new Exception("carnetId needed for raw to numeric switch mode");
                }
            } else if (mCarnetId == null || this.creationMode == -1) {
                throw new Exception("carnetId needed for creation mode");
            } else {
                this.mode = newMode;
                this.carnetId = mCarnetId;
                this.rawCarnetId = null;
                this.carnetLocaleId = null;
            }
        } else if (mRawCarnetId != null) {
            this.mode = newMode;
            this.rawCarnetId = mRawCarnetId;
            this.carnetId = null;
            this.carnetLocaleId = null;
        } else {
            throw new Exception("rawCarnetId needed for numerisation mode");
        }
    }

    /* renamed from: cd.clavatar.wani.vendor.MainToCarnetActivityRoutingBundle$CreationBundleExtras */
    public static class CreationBundleExtras implements IRoutingExtraBundle {
        public static int CREATION_MODE_DELIVERY = 0;
        public static int CREATION_MODE_NUMERISATION = 1;
        public static final Creator<CreationBundleExtras> CREATOR = new Creator<CreationBundleExtras>() {
            public CreationBundleExtras createFromParcel(Parcel source) {
                return new CreationBundleExtras(source);
            }

            public CreationBundleExtras[] newArray(int size) {
                return new CreationBundleExtras[size];
            }
        };
        private final int bundleType = 0;
        private int creationMode;

        public int getExtraBundleType() {
            return 0;
        }

        public int getCreationMode() {
            return this.creationMode;
        }

        public void setCreationMode(int creationMode2) {
            this.creationMode = creationMode2;
        }

        public CreationBundleExtras(int crerationMode) {
            Log.d("zaza", String.format("provided mode %d", new Object[]{Integer.valueOf(crerationMode)}));
            this.creationMode = crerationMode;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            getClass();
            dest.writeInt(0);
            dest.writeInt(this.creationMode);
        }

        protected CreationBundleExtras(Parcel in) {
            in.setDataPosition(4);
            setCreationMode(in.readInt());
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.carnetId);
        dest.writeString(this.rawCarnetId);
        dest.writeValue(this.carnetLocaleId);
        dest.writeInt(this.mode);
        dest.writeInt(this.creationMode);
        dest.writeParcelable(this.extras, flags);
    }

    protected MainToCarnetActivityRoutingBundle(Parcel in) {
        this.creationMode = 0;
        this.carnetId = in.readString();
        this.rawCarnetId = in.readString();
        this.carnetLocaleId = (Long) in.readValue(Long.class.getClassLoader());
        this.mode = in.readInt();
        this.creationMode = in.readInt();
        this.extras = in.readParcelable(Parcelable.class.getClassLoader());
    }
}
