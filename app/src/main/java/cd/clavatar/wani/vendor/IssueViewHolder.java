package cd.clavatar.wani.vendor;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cd.clavatar.wani.databinding.LayoutIssueItemBinding;

/**
 * Created by Cl@v@t@r on 2020-01-30
 */
public class IssueViewHolder extends RecyclerView.ViewHolder {
    private final LayoutIssueItemBinding itemBing;
    private String paiement=null;

    public IssueViewHolder(@NonNull LayoutIssueItemBinding itemBinding) {
        super(itemBinding.getRoot());
        View mV=itemBinding.getRoot();
        this.itemBing=itemBinding;
    }

    public void bind(String cc){
        IssueViewModel ccVm=new IssueViewModel();
        ccVm.getIssue().setValue(cc);
        this.itemBing.setModel(ccVm);

    }
}
