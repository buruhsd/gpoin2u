package tuberpraka.gpoin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

class ListTopupAdapter extends RecyclerView.Adapter<ListTopupAdapter.ViewHolder>{

//    private Context contexts;
    private ArrayList<Topup> data;
    private LayoutInflater inflater;
    Dialog dialog;
    //variable for upload data into http
    LogConfig lg;
    private Context context;

    ListTopupAdapter(ArrayList<Topup> data, Context context) {
        this.data = data;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ListTopupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListTopupAdapter.ViewHolder holder, final int position) {
        lg = new LogConfig();
        holder.ket.setText(data.get(position).getKet());
        holder.alasan.setText(data.get(position).getAlasan());
        holder.tanggal.setText(data.get(position).getTanggal());
        holder.harga.setText(data.get(position).getHarga());
if(data.get(position).getAlasan().equals("click here for confirmation")){
    if(lg.session(context)) {
    holder.alasan.setTextColor(Color.RED);
    }
}
            holder.idpel.setText(" ");
        holder.alasan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(lg.session(context)) {
                    if (data.get(position).getAlasan().equals("click here for confirmation")) {
                        Intent ep = new Intent(context, UploadGambarActivity.class);
                        ep.putExtra("id", data.get(position).getIdpel());
                        ep.putExtra("poin", data.get(position).getHarga());
                        context.startActivity(ep);
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

        TextView status, alasan, ket, tanggal, harga, idpel;

        ViewHolder(View itemView) {
            super(itemView);
            ket  = (TextView) itemView.findViewById(R.id.ketchat);
            tanggal  = (TextView) itemView.findViewById(R.id.tanggalchat);
            harga = (TextView) itemView.findViewById(R.id.hargachat);
            alasan  = (TextView) itemView.findViewById(R.id.alasan);
            idpel = (TextView) itemView.findViewById(R.id.idpel);
        }
    }

    public void Pass(){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.upload);
        final EditText trxpass, txtnorek, txtatasnama, jml_tf, txttgl, txtket;
        Spinner bank, metode;
        Button btnkirim;

        trxpass = (EditText) dialog.findViewById(R.id.trxpass);
        txtnorek = (EditText) dialog.findViewById(R.id.txtnorek);
        txtatasnama = (EditText) dialog.findViewById(R.id.txtatasnama);
        jml_tf = (EditText) dialog.findViewById(R.id.jml_tf);
        txttgl = (EditText) dialog.findViewById(R.id.txttgl);
        txtket = (EditText) dialog.findViewById(R.id.txtket);
        bank = (Spinner) dialog.findViewById(R.id.bank_member);
        metode = (Spinner) dialog.findViewById(R.id.metode_member);
        btnkirim = (Button) dialog.findViewById(R.id.btnkirim);


        btnkirim = (Button) dialog.findViewById(R.id.btnepass);

        btnkirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(trxpass.getText().toString().trim().equals("") || txtnorek.getText().toString().trim().equals("")){
                    Toast.makeText(context, "Data must be filled",Toast.LENGTH_SHORT).show();
                }else{
//                    editpass(passlama.getText().toString(), passbaru.getText().toString());
                }
            }
        });
        dialog.setTitle("Edit Password");
        dialog.setCancelable(true);
        dialog.show();
    }
}
