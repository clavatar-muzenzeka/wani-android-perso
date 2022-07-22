package cd.clavatar.wani.vendor;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import cd.clavatar.wani.R;
import cd.clavatar.wani.data.model.CompactCarnet;
import cd.clavatar.wani.data.model.Picture;
import cd.clavatar.wani.data.model.WaniNetworkAccess;
import cd.clavatar.wani.databinding.LayoutCarnetItemBinding;
import cd.clavatar.wani.utilities.Common;
import de.hdodenhof.circleimageview.CircleImageView;

/* renamed from: cd.clavatar.wani.vendor.CompactCardViewHolder */
public class CompactCardViewHolder extends RecyclerView.ViewHolder {
    private CompactCarnet carnet = null;
    private final LayoutCarnetItemBinding itemBing;

    public CompactCardViewHolder(LayoutCarnetItemBinding itemBinding) {
        super(itemBinding.getRoot());
        itemBinding.getRoot().setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        this.itemBing = itemBinding;
    }

    public void bind(final CompactCarnet cc) {
        CompactCarnetViewModel ccVm = new CompactCarnetViewModel(cc);
        this.carnet = cc;
        this.itemBing.setModel(ccVm);
        this.itemBing.getRoot().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CompactCarnetViewModel.select(cc.getId());
            }
        });
        loadImage();
        this.itemBing.getRoot().findViewById(R.id.picture).post(new Runnable() {
            public void run() {
                CompactCardViewHolder.this.loadImage();
            }
        });
    }

    public void loadImage() {
        CircleImageView iv = (CircleImageView) this.itemBing.getRoot().findViewById(R.id.picture);
        Long feature = null;
        if (this.carnet.getCarnetStatus().getTypeStatus() == 2) {
            feature = this.carnet.getLocalePictureRawCarnet();
        } else if (this.carnet.getIdPeople() != null) {
            feature = this.carnet.getIdPeople().getLocaleIdProfile();
        }
        if (feature == null || Picture.findById(Picture.class, feature) == null) {
            String stringFeature = null;
            if (!(this.carnet.getCarnetStatus().getTypeStatus() == 2 || this.carnet.getIdPeople() == null)) {
                stringFeature = this.carnet.getCarnetStatus().getTypeStatus() == 2 ? this.carnet.getPictureRawCarnet() : this.carnet.getIdPeople().getIdProfile();
            }
            if (stringFeature != null) {
                Picasso.get().cancelRequest((ImageView) iv);
                Picasso.get().load(WaniNetworkAccess.BASE_URL + "/picture/load/" + stringFeature + "?static=yes").placeholder((int) R.drawable.ic_user).into(iv, new Callback() {
                    public void onSuccess() {
                    }

                    public void onError(Exception e) {
                    }
                });
                return;
            }
            return;
        }
        iv.setImageBitmap(Common.loadPictureByLocaleId(this.itemBing.getRoot().getContext(), feature));
    }
}
