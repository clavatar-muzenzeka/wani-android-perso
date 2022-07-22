package cd.clavatar.wani.vendor;

import androidx.lifecycle.ViewModel;

/**
 * Created by Cl@v@t@r on 2019-12-27
 */
public class WaniDialogViewModelWrapper<T> extends ViewModel {
    private IDialogResultHandler<T> resultHandler;

    public IDialogResultHandler<T> getResultHandler() {
        return resultHandler;
    }

    public void setResultHandler(IDialogResultHandler<T> resultHandler) {
        this.resultHandler = resultHandler;
    }
}
