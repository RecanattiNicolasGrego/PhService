package com.service.Recyclers;

import static android.view.View.GONE;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.service.Devices.Impresora.PrinterDiscovery;
import com.service.estructuras.ZebraStruct;
import com.service.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecyclerSearcher extends RecyclerView.Adapter<RecyclerSearcher.ViewHolder> {


    private static int selectedPos = RecyclerView.NO_POSITION;
    private List<ZebraStruct> ListElementsArrayList2;
    private List<ZebraStruct>  ListScanner;
    private final LayoutInflater mInflater;
    private static ItemClickListener mClickListener;
    private Context context;

    public RecyclerSearcher(Context context, List<ZebraStruct> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.ListElementsArrayList2 = data;
        this.ListScanner=data;

    }

    // Método para iniciar el escaneo de dispositivos Bluetooth clásicos

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.impresorasrecycler2, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mac.setText(ListScanner.get(position).getDireccionx());
        if(ListScanner.get(position).getClase().equals(String.valueOf(BluetoothClass.Device.Major.IMAGING))){
            holder.tv_f_Campo1.setText("Impresora: ");
        }else{
            holder.tv_f_Campo1.setText("Nombre: ");
        }
        String tipo = ListScanner.get(position).getTipo();
    //    System.out.println(tipo+" "+ PrinterDiscovery.btle+" a "+ String.valueOf(PrinterDiscovery.btc));
        if (!tipo.isEmpty()) {

            if(tipo.equals(PrinterDiscovery.btc) ) {
                holder.nombretv1.setText(ListScanner.get(position).getname());
                holder.linear.setBackgroundResource(R.drawable.borde_c);
                holder.Tipo_I.setImageResource(R.drawable.bluetooth_b);
                holder.btle_tv.setVisibility(GONE);
                holder.nombretv2.setText("mac:");
            }else if(tipo.equals(PrinterDiscovery.btle)) {
                holder.nombretv1.setText(ListScanner.get(position).getname());
                holder.linear.setBackgroundResource(R.drawable.borde_le);
                holder.Tipo_I.setImageResource(R.drawable.bluetooth_b);
                holder.btle_tv.setVisibility(View.VISIBLE);
                holder.nombretv2.setText("mac:");
            }else if (tipo.equals("WFO")) {
                holder.nombretv1.setVisibility(GONE);
                holder.tv_f_Campo1.setVisibility(GONE);
                holder.linear.setBackgroundResource(R.drawable.borde_wo);
                holder.Tipo_I.setImageResource(R.drawable.icono_wifi_white);
                holder.btle_tv.setVisibility(GONE);
                holder.nombretv2.setText("SSID:");
            }else if (tipo.equals("WF")) {
                holder.nombretv1.setVisibility(GONE);
                holder.tv_f_Campo1.setVisibility(GONE);
                holder.linear.setBackgroundResource(R.drawable.borde_w);
                holder.Tipo_I.setImageResource(R.drawable.icono_wifi_white);
                holder.btle_tv.setVisibility(GONE);
                holder.nombretv2.setText("IP:");
            }else{

            }

        holder.itemView.setSelected(selectedPos == position);
    }
    }

    @Override
    public int getItemCount() {
        return ListScanner.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nombretv1, btle_tv,mac,tv_f_Campo1, nombretv2;
        ImageView Tipo_I;
        LinearLayout linear;

        ViewHolder(View itemView) {
            super(itemView);
            nombretv1 = itemView.findViewById(R.id.post);
            Tipo_I = itemView.findViewById(R.id.tipo);
            btle_tv = itemView.findViewById(R.id.btle_tv);
            mac = itemView.findViewById(R.id.post2);
            linear = itemView.findViewById(R.id.linear);
            tv_f_Campo1= itemView.findViewById(R.id.tv_f_Campo1);
            nombretv2 = itemView.findViewById(R.id.tv_f_Campo2);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void removeAt(int position) {
        ListElementsArrayList2.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, ListElementsArrayList2.size());
    }

    public void filterList(List<ZebraStruct> filteredList) {
        ListScanner = filteredList;
        notifyDataSetChanged();
    }
    public String getAddress(int position){
        return ListElementsArrayList2.get(position).getDireccionx();
    }

    public List<ZebraStruct> getlist() {
        return ListElementsArrayList2;
    }
    public void removeall(){
        ListElementsArrayList2.clear();
    }
    public String getTipo(int position){
        return ListElementsArrayList2.get(position).getname();
    }
    public void add(String A, String B,String C, String D) {
        ListElementsArrayList2.add(new ZebraStruct(A, B,C,D));
        notifyItemInserted(ListElementsArrayList2.size() - 1);
        notifyDataSetChanged();
    }
    public void filtrar(String texto) {
        List<ZebraStruct> listaFiltrada = new ArrayList<>();
        for (ZebraStruct imp  : ListElementsArrayList2) {
            if(!Objects.equals(imp.getname(), "") && !texto.equalsIgnoreCase("")) {
                if (imp.getname().toLowerCase().contains(texto.toLowerCase())) {
                    listaFiltrada.add(imp);
                }
            }else{
                if(texto.toLowerCase().equals("")){
                    listaFiltrada.add(imp);
                }
            }
            ListScanner.clear();
        }
        notifyDataSetChanged();
    }
}