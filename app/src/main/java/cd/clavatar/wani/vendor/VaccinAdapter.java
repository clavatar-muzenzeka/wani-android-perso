package cd.clavatar.wani.vendor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cd.clavatar.wani.R;
import cd.clavatar.wani.data.model.Vaccin;

/**
 * Created by Cl@v@t@r on 2019-12-25
 */
public class VaccinAdapter extends BaseAdapter {

    private final Context contex;
    private final List<Vaccin> assertions;

    public VaccinAdapter(Context context, List<Vaccin> assertions) {
        this.contex= context;
        this.assertions=assertions;
        //this.assertions.add(0, kaminskiAssertionDefault);

    }

    @Override
    public int getCount() {
        return this.assertions!=null?this.assertions.size():0;
    }

    @Override
    public Object getItem(int position) {
        //if(position==0) position++;

        Vaccin requested=this.assertions!=null?this.assertions.get(position):null;
        return requested;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView content= (TextView) LayoutInflater.from(this.contex).inflate(R.layout.layout_kaminsky_view_spinner_item, null);
        Vaccin requested;
        //if(this.assertions.size()==position) requested=this.assertions.get(position-1);
        //else requested=this.assertions.get(position);
        requested=this.assertions.get(position);
        content.setText(requested.getDesignation());
        return content;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        super.getDropDownView(position, convertView, parent);
        TextView content= (TextView) LayoutInflater.from(this.contex).inflate(R.layout.layout_kaminsky_view_spinner_dropdown_item, null);
        Vaccin requested=this.assertions.get(position);
        content.setText(requested.getDesignation());
        return content;
    }


    public int getPosition(Vaccin newSelectedValue) {
        return assertions.indexOf(newSelectedValue);
    }
}
