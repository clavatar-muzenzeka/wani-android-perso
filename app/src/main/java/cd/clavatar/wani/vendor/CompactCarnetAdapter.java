package cd.clavatar.wani.vendor;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import cd.clavatar.wani.data.model.CompactCarnet;
import cd.clavatar.wani.databinding.LayoutCarnetItemBinding;
import cd.clavatar.wani.ui.main.MainActivity;

/* renamed from: cd.clavatar.wani.vendor.CompactCarnetAdapter */
public class CompactCarnetAdapter extends RecyclerView.Adapter<CompactCardViewHolder> {
    private List<CompactCarnet> carnets;

    public CompactCarnetAdapter(List<CompactCarnet> carnets2) {
        this.carnets = carnets2;
    }

    public CompactCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutCarnetItemBinding b = LayoutCarnetItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        b.setLifecycleOwner((MainActivity) parent.getContext());
        return new CompactCardViewHolder(b);
    }

    public void onBindViewHolder(CompactCardViewHolder holder, int position) {
        holder.bind(this.carnets.get(position));
    }

    public void onViewAttachedToWindow(CompactCardViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.loadImage();
    }

    public int getItemCount() {
        return this.carnets.size();
    }
}
