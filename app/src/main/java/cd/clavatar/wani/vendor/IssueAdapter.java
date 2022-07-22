package cd.clavatar.wani.vendor;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cd.clavatar.wani.databinding.LayoutIssueItemBinding;
import cd.clavatar.wani.ui.carnet.CarnetActivity;

/**
 * Created by Cl@v@t@r on 2020-01-30
 */
public class IssueAdapter extends RecyclerView.Adapter<IssueViewHolder> {

    private List<String> issues=new ArrayList<>();

    public IssueAdapter(List<String> paiements) {
        if(paiements!=null)this.issues =paiements;
    }

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf=LayoutInflater.from(parent.getContext());
        LayoutIssueItemBinding b=LayoutIssueItemBinding.inflate(inf);
        b.setLifecycleOwner((CarnetActivity)parent.getContext());
        return new IssueViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueViewHolder holder, int position) {
        holder.bind(issues.get(position));
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }
}
