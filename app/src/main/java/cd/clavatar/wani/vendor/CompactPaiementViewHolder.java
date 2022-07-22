package cd.clavatar.wani.vendor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cd.clavatar.wani.R;
import cd.clavatar.wani.data.model.CompactCarnet;
import cd.clavatar.wani.data.model.CompactPaiement;
import cd.clavatar.wani.data.model.WaniNetworkAccess;
import cd.clavatar.wani.databinding.LayoutCarnetItemBinding;
import cd.clavatar.wani.databinding.LayoutCompactPaiementItemBinding;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Cl@v@t@r on 2020-01-30
 */
public class CompactPaiementViewHolder extends RecyclerView.ViewHolder {
    private final LayoutCompactPaiementItemBinding itemBing;
    private CompactPaiement paiement=null;

    public CompactPaiementViewHolder(@NonNull LayoutCompactPaiementItemBinding itemBinding) {
        super(itemBinding.getRoot());
        View mV=itemBinding.getRoot();
        LinearLayout.LayoutParams mLp= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mV.setLayoutParams(mLp);
        this.itemBing=itemBinding;
    }

    public void bind(CompactPaiement cc){
        CompactPaiementViewModel ccVm=new CompactPaiementViewModel(cc);
        this.paiement=cc;
        this.itemBing.setModel(ccVm);

    }
}
