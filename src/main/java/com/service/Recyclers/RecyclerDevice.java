    package com.service.Recyclers;

    import static android.view.View.GONE;
    import static android.view.View.VISIBLE;

    import android.content.Context;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.view.animation.Animation;
    import android.view.animation.AnimationUtils;
    import android.widget.LinearLayout;
    import android.widget.TextView;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.constraintlayout.widget.ConstraintLayout;
    import androidx.recyclerview.widget.RecyclerView;

    import com.service.estructuras.classDevice;
    import com.service.R;

    import java.util.ArrayList;
    import java.util.List;

    public class RecyclerDevice extends RecyclerView.Adapter<RecyclerDevice.ViewHolder> {


            private int selectedPos = RecyclerView.NO_POSITION;
            private List<classDevice> mData;
            private final LayoutInflater mInflater;
            private ItemClickListener mClickListener;
            private int lastPosition = -1;
            private final AppCompatActivity mainActivity;
            Boolean todos=false;
            TextView ipMac,Slave,numMOD,tvnombreipmac,tvBaud,tvStop,tvData,tvparity,nombremodelo;
            private LinearLayout Ltvid,LtvBaud,LtvStop,LtvData,Ltvparity,LtvModelo;
            TextView tvModelo;
            int Device=0;
            String Salida="";
            String Salidastr="",Modelo="";
            String Devicestr;

            public RecyclerDevice(Context context, List<classDevice> data, AppCompatActivity mainActivity, ItemClickListener clickListener, String Salida, int Device) {
                this.mInflater = LayoutInflater.from(context);
                this.mData = data;
                this.mainActivity = mainActivity;
                mClickListener = clickListener;
                this.Salida=Salida;
                this.Device = Device;

            }

            // inflates the row layout from xml when needed
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view=null;
                ArrayList<String> datos = new ArrayList<String>();
                switch (Device){
                    case -1: {
                        view = mInflater.inflate(R.layout.adapterbza, parent, false);
                        ipMac = view.findViewById(R.id.tv_Slave);
                        Ltvid=view.findViewById(R.id.Ltvid);
                        LtvModelo=view.findViewById(R.id.LtvModelo);
                        tvModelo = view.findViewById(R.id.sp_Modelo);
                        tvnombreipmac=view.findViewById(R.id.textView1);
                        nombremodelo= view.findViewById(R.id.textView3);
                        Slave = view.findViewById(R.id.tv_Slave);
                        tvBaud = view.findViewById(R.id.tv_Baud);
                        tvStop = view.findViewById(R.id.tv_Stopbit);
                        tvData = view.findViewById(R.id.tv_Databit);
                        tvparity = view.findViewById(R.id.tv_Parity);
                        LtvBaud = view.findViewById(R.id.Lbaud);
                        LtvStop = view.findViewById(R.id.Lstop);
                        LtvData = view.findViewById(R.id.Ldata);
                        Ltvparity = view.findViewById(R.id.Lparity);

                        break;
                    }
                    case 0: {
                        view = mInflater.inflate(R.layout.adapterbza, parent, false);
                        tvModelo = view.findViewById(R.id.sp_Modelo);
                        numMOD = view.findViewById(R.id.numMOD);
                        ipMac = view.findViewById(R.id.tv_ipmac);
                        Slave = view.findViewById(R.id.tv_Slave);
                        Ltvid=view.findViewById(R.id.Ltvid);


                        break;
                    }
                    case 1:{
                        view = mInflater.inflate(R.layout.adapterbza, parent, false);
                        ipMac = view.findViewById(R.id.tv_Slave);
                        Ltvid=view.findViewById(R.id.Ltvid);
                        LtvModelo=view.findViewById(R.id.LtvModelo);
                        tvModelo = view.findViewById(R.id.sp_Modelo);
                        tvnombreipmac=view.findViewById(R.id.textView1);
                        nombremodelo= view.findViewById(R.id.textView3);


                        break;
                    }
                    case 2:{
                        view = mInflater.inflate(R.layout.adapterbza, parent, false);
                        ipMac = view.findViewById(R.id.tv_Slave);
                        Ltvid=view.findViewById(R.id.Ltvid);
                        LtvModelo=view.findViewById(R.id.LtvModelo);
                        tvModelo = view.findViewById(R.id.sp_Modelo);
                        tvnombreipmac=view.findViewById(R.id.textView1);
                        nombremodelo= view.findViewById(R.id.textView3);


                        break;
                    }
                    case 3:{
                        view = mInflater.inflate(R.layout.adapterbza, parent, false);
                        tvModelo = view.findViewById(R.id.sp_Modelo);
                        numMOD = view.findViewById(R.id.numMOD);
                        ipMac = view.findViewById(R.id.tv_ipmac);
                        Slave = view.findViewById(R.id.tv_Slave);
                        tvBaud = view.findViewById(R.id.tv_Baud);
                        tvStop = view.findViewById(R.id.tv_Stopbit);
                        tvData = view.findViewById(R.id.tv_Databit);
                        tvparity = view.findViewById(R.id.tv_Parity);
                        LtvBaud = view.findViewById(R.id.Lbaud);
                        LtvStop = view.findViewById(R.id.Lstop);
                        LtvData = view.findViewById(R.id.Ldata);
                        Ltvparity = view.findViewById(R.id.Lparity);
                        Ltvid=view.findViewById(R.id.Ltvid);

                        break;
                    }
                    case 4:{
                        view = mInflater.inflate(R.layout.adapterbza, parent, false);
                        tvModelo = view.findViewById(R.id.sp_Modelo);
                        numMOD = view.findViewById(R.id.numMOD);
                        ipMac = view.findViewById(R.id.tv_ipmac);
                        Slave = view.findViewById(R.id.tv_Slave);
                        tvBaud = view.findViewById(R.id.tv_Baud);
                        tvStop = view.findViewById(R.id.tv_Stopbit);
                        tvData = view.findViewById(R.id.tv_Databit);
                        tvparity = view.findViewById(R.id.tv_Parity);
                        LtvBaud = view.findViewById(R.id.Lbaud);
                        LtvStop = view.findViewById(R.id.Lstop);
                        LtvData = view.findViewById(R.id.Ldata);
                        Ltvparity = view.findViewById(R.id.Lparity);
                        Ltvid=view.findViewById(R.id.Ltvid);

                        break;
                    }

                }

                return new ViewHolder(view);
            }
            // binds the data to the TextView in each row
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                //ExampleItem currentItem = mExampleList.get(position);
                Devicestr=mData.get(position).getTipo();
             //   System.out.println("ADAPTER CONSOLE "+mData.get(position).getTipo());
            /*    if(Device==-1) {
                    todos = true;
                    Device = mData.get(position).getTipo();
                }
//                if(mData.size()>position && mData.size()>=1){
                    switch (Device) {
                        case 0: {
                            Devicestr = "Balanza";
                            break;
                        }
                        case 1:{
                            Devicestr = "Impresora";
                            break;
                        }
                        case 2:{
                            Devicestr="Expansion";
                            break;
                        }
                        case 3:{
                            Devicestr="Escaner";
                            break;
                        }
                        case 4:{
                            Devicestr="Dispositivo";
                            break;
                        }
                    }*/
                if(todos){
                    Salidastr= mData.get(position).getSalida();

                }else {
                    switch (Salida) {
                        case "Puerto Serie 1": {
                            Salidastr = "PuertoSerie 1";
                            break;
                        }
                        case "Puerto Serie 2": {
                            Salidastr = "PuertoSerie 2";
                            break;
                        }
                        case "Puerto Serie 3": {
                            Salidastr = "PuertoSerie 3";
                            break;
                        }
                        case "Red": {
                            Salidastr = "Red";

                            ArrayList<String> List = mData.get(position).getDireccion();
                            int lenght = List.size();
                            if (lenght > 0) {
                                String Baud = List.get(0);
                                ipMac.setText(Baud);
                            }
                            tvnombreipmac.setText("Red");
                            ipMac.setClickable(false);

                            break;
                        }
                        case "Bluetooth": {
                            Salidastr = "Bluetooth";
                            ArrayList<String> List = mData.get(position).getDireccion();
                            int lenght = List.size();
                            tvnombreipmac.setText("MAC");
                            holder.Ltvid.setVisibility(VISIBLE);

                            if (lenght > 0) {
                                String Baud = List.get(0);
                                ipMac.setText(Baud);
                            }
                            ipMac.setClickable(false);
                            break;
                        }
                        case "USB": {
                            Salidastr = "USB";
                            break;
                        }

                    }
                }
                Boolean tieneid=false;
//                switch (mData.get(0).getModelo()){
//                    case 0:{
//                        tieneid=OPTIMA_I.tieneid;
//
//                        break;
//                    }
//                    case 1:{
//                        tieneid= MINIMA_I.tieneid;
//
//                        break;
//                    }
//                    case 2:{
//                        tieneid= R31P30_I.tieneid;
//
//                       break;
//                    }
//                    case 3:{
//                        tieneid= ITW410_FORM.tieneid;
//                        break;
//                    }
//                    case 10:{
//                        tieneid=false;
//                        break;
//                    }
//                    case 20:{
//                        tieneid=false;
//                         break;
//                    }
//                    case 21:{
//                        tieneid=false;
//                         break;
//                    }
//                    case 22:{
//                        tieneid=false;
//                        break;
//                    }
//                    case 23:{
//                        tieneid=false;
//                        break;
//                    }
//                    case 30:{
//                        tieneid=false;
//                        break;
//                    }
//                    case 40:{
//                        tieneid=false;
//                        break;
//                    }
//
//                    default:{
//                        tieneid=false;
//                        break;
//                    }
//                }
               /* switch (PreferencesDevicesManager.obtenerIndiceModeloPorTipo(PreferencesDevicesManager.obtenerIndiceTipo(mData.get(position).getTipo()), mData.get(position).getModelo())){
                    case 0:{
                        Modelo= "Optima";
                        break;
                    }
                    case 1:{
                        Modelo= "Minima";
                        break;
                    }
                    case 2:{
                        Modelo="R31P30";
                        break;
                    }
                    case 3:{
                        Modelo="ITW410";
                        break;
                    }
                    case 4:{
                       Modelo="SPIDER3";
                        break;
                    }
                    case 5:{
                        Modelo="ANDGF3000";
                        break;
                    }
                    case 10:{
                        Modelo="ZEBRA";
                        break;
                    }
                    case 20:{
                        Modelo="Salidas";
                        break;
                    }
                    case 21:{
                         Modelo= "Entradas";
                        break;
                    }
                    case 22:{
                        Modelo="Mixtos";
                        break;
                    }
                    case 23:{
                        Modelo="Salidas Analogicas";
                        break;
                    }
                    case 30:{
                        Modelo="Escaner";
                        break;
                    }
                    case 40:{
                        Modelo="Dispositivo";
                        break;
                    }
                    default:{
                         break;
                    }
                }*/
                    holder.Ltvid.setVisibility(GONE);
                    if(position<1||((Salidastr.equals("PuertoSerie 3")&& tieneid)|| Salidastr.equals("Red"))||todos) { //POR AHORA ESTE IF/ELSE SE QUEDA HASTA QUE HAGAMOS PROTOCOLOS CON ID  ||Salidastr.equals("PuertoSerie 3")|| Salidastr.equals("Red")
                        if(todos){
                            holder.numMOD.setText(("Nº " + Devicestr + " " + (mData.get(position).getNDL()))+"        "+Salidastr);
                        }else{
                        holder.numMOD.setText(("Nº "+Devicestr+" " + (mData.get(position).getNDL())));
                        }
                        if (mData.get(position).getID()!=-1|| todos ) {// (mData.get(position).getSalida().equals(Salida) && // (mData.get(position).getTipo()!=-1 && (mData.get(position).getModelo()==Modelo)){
                            if(Device==0){
                                holder.constraintLayout1.setVisibility(VISIBLE);
                                holder.constraintLayout2.setVisibility(GONE);
                                ArrayList<String> List = mData.get(position).getDireccion();
                                if(tieneid){
                                    holder.Ltvid.setVisibility(VISIBLE);
                                }
                                int lenght = List.size();
                                if (lenght > 0) {
                                    String Baud = List.get(0);
                                    holder.ipMac.setText(Baud);
                                }
                                holder.ipMac.setClickable(false);
                                mData.get(position).setID(0);//POR AHORA VA A SER 0 POR DEFAULT HASTA HACER LO DEL ID
                                String Slave = Integer.toString(mData.get(position).getID());
                                holder.Slave.setText(Slave);
                                holder.Slave.setClickable(false);
                                    }else if ( Device==1) {
                                holder.constraintLayout1.setVisibility(VISIBLE);
                                holder.constraintLayout2.setVisibility(GONE);
                                ArrayList<String> List = mData.get(position).getDireccion();
                                if (List.size()>0 ) {
                                    ipMac.setVisibility(VISIBLE);
                                    ipMac.setText(List.get(0).toString());
                                }
                                if(Salidastr.contains("Puerto Serie")||Salidastr.contains("PuertoSerie")){
                                    holder.nombremodelo.setVisibility(VISIBLE);
                                    holder.LtvModelo.setVisibility(VISIBLE);
                                    holder.tv_Modelo.setText(mData.get(position).getModelo());
                                     holder.Ltvid.setVisibility(GONE);

                                }else{
                                    if(Salidastr.contains("Red")||Salidastr.equals("Bluetooth")){
                                        holder.Ltvid.setVisibility(VISIBLE);
                                    }
                                    holder.nombremodelo.setVisibility(GONE);
                                    holder.tv_Modelo.setVisibility(GONE);
                                    holder.LtvModelo.setVisibility(GONE);
                                    holder.tvnombreipmac.setVisibility(VISIBLE);
                                    ipMac.setVisibility(VISIBLE);
                                }


                            }else if(Device==2){
                                holder.Ltvid.setVisibility(GONE);

                            }
                            else if (Device==3){


                                holder.LtvBaud.setVisibility(VISIBLE);
                                holder.LtvData.setVisibility(VISIBLE);
                                holder.LtvStop.setVisibility(VISIBLE);
                                holder.Ltvparity.setVisibility(VISIBLE);

                                    ArrayList<String> List = mData.get(position).getDireccion();
                                    int lenght = List.size();
                                    if (lenght > 0) {
                                        String Baud = List.get(0);
                                        holder.tvBaud.setText(Baud);
                                    }
                                    holder.tvBaud.setClickable(false);
                                    holder.tvData.setClickable(false);
                                    if(lenght>1){
                                        String DATA = List.get(1);
                                        holder.tvData.setText(DATA);
                                    }
                                    if (lenght > 2) {
                                        String Stop = List.get(2);
                                        holder.tvStop.setText(Stop);
                                    }
                                    holder.tvStop.setClickable(false);

                                    if (lenght > 3) {
                                        String Parity = List.get(3);
                                        holder.tvparity.setText(Parity);
                                    }
                                    holder.tvparity.setClickable(false);

                            }else if (Device==4){
                                holder.LtvBaud.setVisibility(VISIBLE);
                                holder.LtvData.setVisibility(VISIBLE);
                                holder.LtvStop.setVisibility(VISIBLE);
                                holder.Ltvparity.setVisibility(VISIBLE);
                                    ArrayList<String> List = mData.get(position).getDireccion();
                                    int lenght = List.size();
                                    if (lenght > 0) {
                                        String Baud = List.get(0);
                                        holder.tvBaud.setText(Baud);
                                    }
                                    holder.tvBaud.setClickable(false);
                                    holder.tvData.setClickable(false);
                                    if(lenght>1){
                                        String DATA = List.get(1);
                                        holder.tvData.setText(DATA);
                                    }
                                    if (lenght > 2) {
                                        String Stop = List.get(2);
                                        holder.tvStop.setText(Stop);
                                    }
                                    holder.tvStop.setClickable(false);

                                    if (lenght > 3) {
                                        String Parity = List.get(3);
                                        holder.tvparity.setText(Parity);
                                    }
                                    holder.tvparity.setClickable(false);
                            }
                        } else if (mData.get(position).getID() == -1) {
                            holder.constraintLayout1.setVisibility(GONE);
                            holder.constraintLayout2.setVisibility(VISIBLE);
                        } else {
                            holder.constraintLayout1.setVisibility(GONE);
                            holder.constraintLayout2.setVisibility(GONE);
                            holder.numMOD.setVisibility(GONE);
                            holder.ultimatelayout.setMaxHeight(0);
                        }

                    }else{
                        // POR AHORA
                        holder.ultimatelayout.setVisibility(GONE);
                    }
                    holder.itemView.setOnClickListener(v -> {
                        if (mClickListener != null) {
                            mClickListener.onItemClick(v, position, mData.get(position), Salida, Device);
                        }
                    });

                    //   setAnimation(holder.itemView, psition);
                    if(todos){
                        Device=-1;
                    }
                    holder.itemView.setSelected(selectedPos == position);
               tvModelo.setText(Modelo);
            }


            // total number of rows

            @Override
            public int getItemCount() {
                return mData.size();
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }

            private void setAnimation(View viewToAnimate, int position) {
                // If the bound view wasn't previously displayed on screen, it's animated
                if (position > lastPosition) {
                    Animation animation = AnimationUtils.loadAnimation(mainActivity.getApplicationContext(), android.R.anim.slide_in_left);
                    viewToAnimate.startAnimation(animation);
                    lastPosition = position;
                }

            }
            // stores and recycles views as they are scrolled off screen
            public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
                TextView ipMac,Slave,numMOD,tvnombreipmac,tvBaud,tvStop,tvData,tvparity,nombremodelo, tv_Modelo;

                ConstraintLayout constraintLayout1,constraintLayout2,ultimatelayout;
                LinearLayout Ltvid,LtvBaud,LtvStop,LtvData,Ltvparity,LtvModelo;


                ViewHolder(View itemView) {
                    super(itemView);
                    ultimatelayout=itemView.findViewById(R.id.ultimatelayout);
                    constraintLayout1=itemView.findViewById(R.id.constraintLayout1);
                    constraintLayout2=itemView.findViewById(R.id.constraintLayout2);
                    numMOD=itemView.findViewById(R.id.numMOD);
                    ipMac = itemView.findViewById(R.id.tv_ipmac);
                    Slave = itemView.findViewById(R.id.tv_Slave);
                    tvnombreipmac= itemView.findViewById(R.id.textView1);
                    nombremodelo=itemView.findViewById(R.id.textView3);
                    tv_Modelo = itemView.findViewById(R.id.sp_Modelo);
                    LtvModelo=itemView.findViewById(R.id.LtvModelo);
                    Ltvid=itemView.findViewById(R.id.Ltvid);
                     tvBaud = itemView.findViewById(R.id.tv_Baud);
                     tvStop = itemView.findViewById(R.id.tv_Stopbit);
                     tvData = itemView.findViewById(R.id.tv_Databit);
                     tvparity = itemView.findViewById(R.id.tv_Parity);
                     LtvBaud = itemView.findViewById(R.id.Lbaud);
                     LtvStop = itemView.findViewById(R.id.Lstop);
                     LtvData = itemView.findViewById(R.id.Ldata);
                     Ltvparity = itemView.findViewById(R.id.Lparity);

                    itemView.setOnClickListener(this);

                }

                @Override
                public void onClick(View view) {
                    if (mClickListener != null) mClickListener.onItemClick(view, getBindingAdapterPosition(),mData.get(selectedPos),Salida,Device);
                    notifyItemChanged(selectedPos);
                    selectedPos = getLayoutPosition();
                    notifyItemChanged(selectedPos);
                }
            }

            // convenience method for getting data at click position
            public classDevice getItem(int id) {
                return mData.get(id);
            }

            // allows clicks events to be caught
            public void setClickListener(ItemClickListener itemClickListener) {
                this.mClickListener = itemClickListener;
            }

            // parent activity will implement this method to respond to click events
            public interface ItemClickListener {
                void onItemClick(View view, int position, classDevice Device, String Salida, int Tipodevice);
            }
            public void removeAt(int position) {
                mData.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mData.size());
            }
            public void filterList(ArrayList<classDevice> filteredList) {
                mData = filteredList;
                notifyDataSetChanged();
            }

        }
