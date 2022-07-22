package cd.clavatar.wani.vendor;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cd.clavatar.wani.data.model.CompactPaiement;
import cd.clavatar.wani.databinding.LayoutCompactPaiementItemBinding;
import cd.clavatar.wani.ui.main.MainActivity;

/**
 * Created by Cl@v@t@r on 2020-01-30
 */
public class CompactPaiementAdapter extends RecyclerView.Adapter<CompactPaiementViewHolder> {

    private List<CompactPaiement> paiements;

    public CompactPaiementAdapter(List<CompactPaiement> Paiements) {
        this.paiements=Paiements;
    }

    @NonNull
    @Override
    public CompactPaiementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf=LayoutInflater.from(parent.getContext());
        LayoutCompactPaiementItemBinding b=LayoutCompactPaiementItemBinding.inflate(inf);
        b.setLifecycleOwner((MainActivity)parent.getContext());
        return new CompactPaiementViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull CompactPaiementViewHolder holder, int position) {
        holder.bind(paiements.get(position));
    }

    @Override
    public int getItemCount() {
        return paiements.size();
    }
}
