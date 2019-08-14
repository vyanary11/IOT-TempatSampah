package com.pratamatechnocraft.smarttempatsampah.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.pratamatechnocraft.smarttempatsampah.Fragment.HistoriFragment;
import com.pratamatechnocraft.smarttempatsampah.Fragment.HomeFragment;
import com.pratamatechnocraft.smarttempatsampah.Model.Histori;
import com.pratamatechnocraft.smarttempatsampah.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterRecycleViewDataHistori extends RecyclerView.Adapter<AdapterRecycleViewDataHistori.ViewHolder> implements Filterable {

    private List<Histori> listItemHistoris;
    private List<Histori> listItemHistoriFull;
    private Context context;

    public AdapterRecycleViewDataHistori(List<Histori> listItemHistoris, Context context) {
        this.listItemHistoris = listItemHistoris;
        listItemHistoriFull = new ArrayList<>( listItemHistoris );
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.list_item_histori,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Histori listItemHistori = listItemHistoris.get(position);

        holder.txtHariHistori.setText(listItemHistori.getTanggal());
        holder.txtTanggalHistori.setText(listItemHistori.getTanggal());
        holder.txtJamHistori.setText(listItemHistori.getTanggal());

        holder.cardViewDataHistori.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new HomeFragment(0,listItemHistori.getIdHistori());
                FragmentManager fragmentManager =new HistoriFragment().getChildFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.screen_area, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItemHistoris.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtHariHistori, txtTanggalHistori, txtJamHistori;
        public ImageButton imageButtonHapusHistori;
        public CardView cardViewDataHistori;

        public ViewHolder(View itemView) {
            super(itemView);
            txtHariHistori = (TextView) itemView.findViewById(R.id.txtHariHistori);
            txtTanggalHistori = (TextView) itemView.findViewById(R.id.txtTanggalHistori);
            txtJamHistori = (TextView) itemView.findViewById(R.id.txtJamHistori);
            imageButtonHapusHistori = (ImageButton) itemView.findViewById(R.id.imageButtonHapusHistori);
            cardViewDataHistori = (CardView) itemView.findViewById(R.id.cardViewDataHistori);
        }
    }

    @Override
    public Filter getFilter() {
        return listItemFilter;
    }

    private Filter listItemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Histori> filteredList = new ArrayList<>(  );

            if (charSequence == null || charSequence.length()==0){
                filteredList.addAll( listItemHistoriFull );
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Histori itemHistori : listItemHistoriFull){
                    if (itemHistori.getTanggal().toLowerCase().contains( filterPattern )){
                        filteredList.add( itemHistori );
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values=filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listItemHistoris.clear();
            listItemHistoris.addAll((List) filterResults.values );
            notifyDataSetChanged();
        }
    };

}
