package cd.clavatar.wani.vendor;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Cl@v@t@r on 2020-01-17
 */
public class ArrayListCaster<S, D> {
    public ArrayList<D> cast(ArrayList<S> source, Class<S> originClass, Class<D> destinationClass){
        ArrayList<D> destArray=null;
        for (S currentItem:
             source) {
            destArray.add(destinationClass.cast(currentItem));
        };
        return destArray;
    }
}
