package tuberpraka.gpoin;

import android.content.Context;
import android.content.DialogInterface;
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

class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder>{

    private Context contexts;
    private ArrayList<Menu_voucher> data;
    private LayoutInflater inflater;

    VoucherAdapter(ArrayList<Menu_voucher> data, Context context) {
        this.data = data;
        this.contexts = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public VoucherAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VoucherAdapter.ViewHolder holder, final int position) {
String status ="";
        holder.ket.setText(data.get(position).getPaket());

        holder.tanggal.setText(data.get(position).getTgl());
        holder.harga.setText(data.get(position).getHarga());
        if(data.get(position).getId_kode_voucher().equals("null")||data.get(position).getId_kode_voucher().equals("")||
                data.get(position).getId_kode_voucher().equals("0")) {
            switch (data.get(position).getStatus()) {
                case "1":
                    status = "Your request on process";
                    break;

                case "2":
                    status = "Your voucher has been sent, please check your email";
                    break;
                case "3":
                    status = "Sorry, your request cancel";
                    break;
            }
        }else{
            status = "<"+data.get(position).getKode()+">"+data.get(position).getId_kode_voucher();
        }
        holder.idpel.setVisibility(View.GONE);
        holder.alasan.setText(status);

//        final String finalStatus = status;
//        holder.ly.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(contexts);
//                builder.setMessage("Detail   :\nVoucher :"+data.get(position).getPaket()+
//"\nStatus :"+ finalStatus +""
//                )
//                        .setCancelable(false)
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//
//                            }
//                        })
//                ;
//                final android.app.AlertDialog alert = builder.create();
//                alert.show();
//            }
//        });





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
