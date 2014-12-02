package kit.pef.studenti;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class StudentiAdapeter extends BaseAdapter {

    FragmentActivity activity;
    List<Student> data = new ArrayList<Student>();


    public StudentiAdapeter(FragmentActivity activity, List<Student> data) {
        this.activity = activity;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return fillView(i);
    }

    public View fillView(int i) {
        Context context = activity.getApplicationContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = null;

        view = inflater.inflate(R.layout.radek, null);
        TextView t0  = (TextView) view.findViewById(R.id.id);
        t0.setText(String.valueOf(data.get(i).getId()));
        TextView t1  = (TextView) view.findViewById(R.id.jmeno);
        t1.setText(data.get(i).getJmeno());

        TextView t2  = (TextView) view.findViewById(R.id.prijmeni);
        t2.setText(data.get(i).getPrijmeni());



        return view;
    }
}


