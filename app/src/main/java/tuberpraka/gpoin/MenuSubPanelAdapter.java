package tuberpraka.gpoin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mimin on 12/8/16.
 */

public class MenuSubPanelAdapter extends RecyclerView.Adapter<MenuSubPanelAdapter.MenuPanelViewHolder>{

    private Context context;
    private ArrayList<Menu> data;
    private LayoutInflater inflater;
    MainActivity m;

    MenuSubPanelAdapter(Context context, ArrayList<Menu> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public MenuSubPanelAdapter.MenuPanelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_menu, parent, false);
        return new MenuSubPanelAdapter.MenuPanelViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final MenuSubPanelAdapter.MenuPanelViewHolder holder, int position) {
        m = new MainActivity();
        if (m.cek_status(context)) {
        holder.textView.setText(data.get(position).menu);

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainSubPanel.class);
                intent.putExtra("menu", data.get(holder.getAdapterPosition()).menu);
                context.startActivity(intent);
//                Toast.makeText(context, "a",Toast.LENGTH_SHORT).show();
            }
        });
        }else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("You are not connected internet. Please, connect first")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();



                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MenuPanelViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        MenuPanelViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.txv_row);
        }
    }
}
