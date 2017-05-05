package tuberpraka.gpoin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

class ListCsAdapter extends RecyclerView.Adapter<ListCsAdapter.ViewHolder>{

//    private Context contexts;
    private ArrayList<csClass> data;
    private LayoutInflater inflater;

    ListCsAdapter(ArrayList<csClass> data, Context context) {
        this.data = data;
//        this.contexts = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ListCsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_cs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListCsAdapter.ViewHolder holder, int position) {

        holder.jenis.setText(data.get(position).getJenis());
        holder.nomor.setText(data.get(position).getNomor());
        holder.tgl.setText(data.get(position).getTgl());


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView jenis, nomor,tgl;

        ViewHolder(View itemView) {
            super(itemView);
            jenis  = (TextView) itemView.findViewById(R.id.jenis);
            nomor  = (TextView) itemView.findViewById(R.id.nomor);
            tgl  = (TextView) itemView.findViewById(R.id.tgl);

        }
    }
}
