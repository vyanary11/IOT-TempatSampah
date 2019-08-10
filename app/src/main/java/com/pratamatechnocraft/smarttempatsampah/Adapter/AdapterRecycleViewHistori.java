package com.pratamatechnocraft.smarttempatsampah.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.pratamatechnocraft.smarttempatsampah.Database.DBDataSource;
import com.pratamatechnocraft.smarttempatsampah.Model.Histori;
import com.pratamatechnocraft.smarttempatsampah.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdapterRecycleViewHistori extends RecyclerView.Adapter<AdapterRecycleViewHistori.ViewHolder>  {

    private ArrayList<Histori> historis;
    private Context context;
    private DBDataSource dbDataSource;

    public AdapterRecycleViewHistori(ArrayList<Histori> historis, Context context) {
        this.historis= historis;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.list_item_histori,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Histori histori = historis.get(position);
        final DecimalFormat formatter = new DecimalFormat("#,###,###");

    }

    @Override
    public int getItemCount() {
        return historis.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
