package tuberpraka.gpoin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.ViewHolder>{

    private Context contexts;
    private ArrayList<Topup> data;
    private LayoutInflater inflater;
    LogConfig lg;

    ListChatAdapter(ArrayList<Topup> data, Context context) {
        this.data = data;
        this.contexts = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ListChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListChatAdapter.ViewHolder holder, final int position) {
        lg = new LogConfig();
        holder.ket.setText(data.get(position).getKet());
        holder.alasan.setText(data.get(position).getAlasan());
        holder.tanggal.setText(data.get(position).getTanggal());
        holder.harga.setText(data.get(position).getHarga());
        holder.idpel.setText(data.get(position).getIdpel());

        final SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        String format = s.format(new Date());
        String []tgl_awal = data.get(position).getTanggal().split(" ");
        String tgl = tgl_awal[0];
        Boolean bisa = false;
if(data.get(position).getKet().toUpperCase().contains("CEK") && data.get(position).getStatus().toUpperCase().contains("4")
        && format.equals(tgl)&&  data.get(position).getStat_byr().toUpperCase().contains("1")) {
    holder.jelas.setText("Click Here to Pay");
    holder.ly.setBackgroundColor(Color.RED);
    bisa = true;
}
        final Boolean finalBisa = bisa;
        holder.jelas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalBisa){
                    if(lg.session(contexts)) {
                        Intent ep = new Intent(contexts, ProfilActivity.class);
                        ep.putExtra("id", data.get(position).getId());
                        contexts.startActivity(ep);
                    }
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView status, alasan, ket, tanggal, harga, idpel, jelas;
        LinearLayout ly ;

        ViewHolder(View itemView) {
            super(itemView);
            ly = (LinearLayout)itemView.findViewById(R.id.ly);

            ket  = (TextView) itemView.findViewById(R.id.ketchat);
            tanggal  = (TextView) itemView.findViewById(R.id.tanggalchat);
            jelas  = (TextView) itemView.findViewById(R.id.txtjelas);
            harga = (TextView) itemView.findViewById(R.id.hargachat);
            alasan  = (TextView) itemView.findViewById(R.id.alasan);
            idpel = (TextView) itemView.findViewById(R.id.idpel);
        }
    }
}
