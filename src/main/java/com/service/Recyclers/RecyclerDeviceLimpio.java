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
    import com.service.PreferencesDevicesManager;
    import com.service.R;

    import java.util.ArrayList;
    import java.util.List;

    public class RecyclerDeviceLimpio extends RecyclerView.Adapter<RecyclerDeviceLimpio.ViewHolder> {
            private int selectedPos = RecyclerView.NO_POSITION;
            private List<classDevice> listaDispositivos;
            private final LayoutInflater mInflater;
            private ItemClickListener mClickListener;
            private int lastPosition = -1;
            private final AppCompatActivity mainActivity;
            Boolean banderaVistaPrevia =false;
            int Device=0;
            String Salida="";
            String Salidastr="",Modelo="";
            String Devicestr;

            public RecyclerDeviceLimpio(Context context, List<classDevice> data, AppCompatActivity mainActivity, ItemClickListener clickListener, String Salida, int Device) {
                this.mInflater = LayoutInflater.from(context);
                this.listaDispositivos = data;
                this.mainActivity = mainActivity;
                mClickListener = clickListener;
                this.Salida=Salida;
                this.Device = Device;

            }

            // inflates the row layout from xml when needed
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view=null;
                view = mInflater.inflate(R.layout.adapterbza, parent, false);

                return new ViewHolder(view);
            }
            // binds the data to the TextView in each row
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                Devicestr="";
                ArrayList<String> Direccion = listaDispositivos.get(position).getDireccion();
                if(Device==-1){
                    banderaVistaPrevia = true;
                    Device = PreferencesDevicesManager.obtenerIndiceTipo(listaDispositivos.get(position).getTipo());
                    Devicestr=listaDispositivos.get(position).getTipo();
                }
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
                    }
                if(banderaVistaPrevia){
                    Salidastr= listaDispositivos.get(position).getSalida();
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
                            if(Direccion!=null) {
                                int lenght = Direccion.size();
                                if (lenght > 0) {
                                    String IP = Direccion.get(0);
                                    holder.Slave.setText(IP);
                                }
                            }
                            holder.tvnombreipmac.setText("Red");
                            holder.Slave.setClickable(false);

                            break;
                        }
                        case "Bluetooth": {
                            Salidastr = "Bluetooth";
                            if(Direccion!=null) {
                                int lenght = Direccion.size();
                                holder.tvnombreipmac.setText("MAC");
                                holder.Ltvid.setVisibility(VISIBLE);

                                if (lenght > 0) {
                                    String mac = Direccion.get(0);
                                    holder.Slave.setText(mac);
                                }
                            }
                            holder.Slave.setClickable(false);
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
//
//                        break;
//                    }
//                }
                Modelo = listaDispositivos.get(position).getModelo();
                   holder.Ltvid.setVisibility(GONE);
                    if(position<1||((Salidastr.equals("PuertoSerie 3")&& tieneid)|| Salidastr.equals("Red"))|| banderaVistaPrevia) { //POR AHORA ESTE IF/ELSE SE QUEDA HASTA QUE HAGAMOS PROTOCOLOS CON ID  ||Salidastr.equals("PuertoSerie 3")|| Salidastr.equals("Red")
                        if(banderaVistaPrevia){
                            holder.numMOD.setText(("Nº " + Devicestr + " " + (listaDispositivos.get(position).getNDL()))+"        "+Salidastr);
                        }else{
                        holder.numMOD.setText(("Nº "+Devicestr+" " + (listaDispositivos.get(position).getNDL())));
                        }
                        if (listaDispositivos.get(position).getID()!=-1|| banderaVistaPrevia) {// (mData.get(position).getSalida().equals(Salida) && // (mData.get(position).getTipo()!=-1 && (mData.get(position).getModelo()==Modelo)){
                       //     System.out.println("CONSOLE DEBUG MODELO "+Modelo+" DEVICE "+Device+"     DEVICEDEFAULT"+PreferencesDevicesManager.obtenerIndiceTipo(listaDispositivos.get(position).getTipo())+" DIRECCION"+ listaDispositivos.get(position).getDireccion().isEmpty());
                            System.out.println("DEVICE NUM "+ Device);
                            switch (Device){
                               case 0:{
                                   holder.constraintLayout1.setVisibility(VISIBLE);
                                   holder.constraintLayout2.setVisibility(GONE);
                                   if(tieneid){
                                       holder.Ltvid.setVisibility(VISIBLE);
                                   }
                                   if(Direccion!=null) {
                                       int lenght = Direccion.size();
                                       if (lenght > 0) {
                                           String Address = Direccion.get(0);
                                           holder.ipMac.setText(Address);
                                       }
                                   }
                                   holder.ipMac.setClickable(false);
                                   listaDispositivos.get(position).setID(0);//POR AHORA VA A SER 0 POR DEFAULT HASTA HACER LO DEL ID
                                   String Slavestr = Integer.toString(listaDispositivos.get(position).getID());
                                   holder.Slave.setText(Slavestr);
                                   holder.Slave.setClickable(false);
                                   break;
                               }
                               case 1:{
                                   holder.constraintLayout1.setVisibility(VISIBLE);
                                   holder.constraintLayout2.setVisibility(GONE);
                                   if (Direccion != null) {
                                       holder.Slave.setVisibility(VISIBLE);
                                       try {
                                           holder.Slave.setText(Direccion.get(0).toString());
                                       } catch (Exception e) {
                                       }
                                   }
                                   if(Salidastr.contains("Puerto Serie")||Salidastr.contains("PuertoSerie")){
                                       holder.LtvModelo.setVisibility(VISIBLE);
                                       holder.tv_Modelo.setText(Modelo);
                                       holder.Ltvid.setVisibility(GONE);

                                   }else{
                                       if(Salidastr.contains("Red")||Salidastr.equals("Bluetooth")){
                                           holder.Ltvid.setVisibility(VISIBLE);
                                           holder.tvnombreipmac.setText(Salidastr);
                                       }
                                       holder.tv_Modelo.setVisibility(GONE);
                                       holder.LtvModelo.setVisibility(GONE);
                                       holder.tvnombreipmac.setVisibility(VISIBLE);
                                       holder.ipMac.setVisibility(VISIBLE);
                                   }
                                   break;
                               }
                                case 2 : {

                                }
                               case 3:{
                                   holder.Ltvid.setVisibility(GONE);
                                   holder.LtvBaud.setVisibility(VISIBLE);
                                   holder.LtvData.setVisibility(VISIBLE);
                                   holder.LtvStop.setVisibility(VISIBLE);
                                   holder.constraintLayout12.setVisibility(VISIBLE);
                                   holder.Ltvparity.setVisibility(VISIBLE);
                                   if(Direccion!=null) {
                                       int lenght = Direccion.size();
                                       if (lenght > 0) {
                                           String Baud = Direccion.get(0);
                                           holder.tvBaud.setText(Baud);
                                       }

                                   holder.tvBaud.setClickable(false);
                                   holder.tvData.setClickable(false);
                                   if(lenght>1){
                                       String DATA = Direccion.get(1);
                                       holder.tvData.setText(DATA);
                                   }
                                   if (lenght > 2) {
                                       String Stop = Direccion.get(2);
                                       holder.tvStop.setText(Stop);
                                   }
                                   holder.tvStop.setClickable(false);

                                   if (lenght > 3) {
                                       String Parity = Direccion.get(3);
                                       holder.tvparity.setText(Parity);
                                   }
                                   }
                                   holder.tvparity.setClickable(false);
                                   break;
                               }
                               case 4:{
                                   holder.tv_Modelo.setVisibility(VISIBLE);
                                   holder.LtvModelo.setVisibility(VISIBLE);
                                   holder.tv_Modelo.setText(Modelo);
                                   holder.constraintLayout1.setVisibility(VISIBLE);
                                   if(Salidastr.contains("Puerto Serie")||Salidastr.contains("PuertoSerie")) {
                                       holder.constraintLayout12.setVisibility(VISIBLE);

                                   holder.LtvBaud.setVisibility(VISIBLE);
                                   holder.LtvData.setVisibility(VISIBLE);
                                   holder.LtvStop.setVisibility(VISIBLE);
                                   holder.Ltvparity.setVisibility(VISIBLE);
                                   if(Direccion!=null) {
                                       int lenght = Direccion.size();
                                       if (lenght > 0) {
                                           String Baud = Direccion.get(0);
                                           holder.tvBaud.setText(Baud);
                                       }
                                       holder.tvBaud.setClickable(false);
                                       holder.tvData.setClickable(false);
                                       if (lenght > 1) {
                                           String DATA = Direccion.get(1);
                                           holder.tvData.setText(DATA);
                                       }
                                       if (lenght > 2) {
                                           String Stop = Direccion.get(2);
                                           holder.tvStop.setText(Stop);
                                       }
                                       holder.tvStop.setClickable(false);

                                       if (lenght > 3) {
                                           String Parity = Direccion.get(3);
                                           holder.tvparity.setText(Parity);
                                       }
                                   }else{
                                       if(Direccion!=null) {
                                           int lenght = Direccion.size();
                                           if (lenght > 0) {
                                               String IP = Direccion.get(0);
                                               holder.Slave.setText(IP);
                                           }
                                       }
                                       holder.Ltvid.setVisibility(VISIBLE);
                                       holder.tvnombreipmac.setText("Red");
                                       holder.Slave.setClickable(false);
                                   }
                                   }
                                   holder.tvparity.setClickable(false);
                                   break;
                               }
                           }
                    } else if (listaDispositivos.get(position).getID() == -1) {
                        holder.constraintLayout1.setVisibility(GONE);
                        holder.constraintLayout2.setVisibility(VISIBLE);
                            holder.constraintLayout12.setVisibility(GONE);
                    } else {
                        holder.constraintLayout1.setVisibility(GONE);
                        holder.constraintLayout2.setVisibility(GONE);
                        holder.constraintLayout12.setVisibility(GONE);
                        holder.numMOD.setVisibility(GONE);
                        holder.ultimatelayout.setMaxHeight(0);
                    }
            }else{
                holder.ultimatelayout.setVisibility(GONE);
                    }
                holder.itemView.setOnClickListener(v -> {
                    if (mClickListener != null) {
                        mClickListener.onItemClick(v, position, listaDispositivos.get(position), Salida, Device);
                    }
                });
                if(banderaVistaPrevia){
                    Device=-1;
                }
                holder.itemView.setSelected(selectedPos == position);
                holder.tv_Modelo.setText(Modelo);
            }


            // total number of rows

            @Override
            public int getItemCount() {
                return listaDispositivos.size();
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
                TextView ipMac,Slave,numMOD,tvnombreipmac,tvBaud,tvStop,tvData,tvparity, tv_Modelo;

                LinearLayout constraintLayout1,constraintLayout12;
                ConstraintLayout constraintLayout2,ultimatelayout;
                LinearLayout Ltvid,LtvBaud,LtvStop,LtvData,Ltvparity,LtvModelo;


                ViewHolder(View itemView) {
                    super(itemView);
                    ultimatelayout=itemView.findViewById(R.id.ultimatelayout);
                    constraintLayout1=itemView.findViewById(R.id.constraintLayout1);
                    constraintLayout12 = itemView.findViewById(R.id.constraintLayout12);
                    constraintLayout2=itemView.findViewById(R.id.constraintLayout2);
                    numMOD=itemView.findViewById(R.id.numMOD);
                    ipMac = itemView.findViewById(R.id.tv_ipmac);
                    Slave = itemView.findViewById(R.id.tv_Slave);
                    tvnombreipmac= itemView.findViewById(R.id.textView1);
                    tv_Modelo = itemView.findViewById(R.id.sp_Modelo);
                    LtvModelo=itemView.findViewById(R.id.LtvModelo);
                    tv_Modelo = itemView.findViewById(R.id.sp_Modelo);
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
                    if (mClickListener != null) mClickListener.onItemClick(view, getBindingAdapterPosition(), listaDispositivos.get(selectedPos),Salida,Device);
                    notifyItemChanged(selectedPos);
                    selectedPos = getLayoutPosition();
                    notifyItemChanged(selectedPos);
                }
            }

            // convenience method for getting data at click position
            public classDevice getItem(int id) {
                return listaDispositivos.get(id);
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
                listaDispositivos.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, listaDispositivos.size());
            }
            public void filterList(ArrayList<classDevice> filteredList) {
                listaDispositivos = filteredList;
                notifyDataSetChanged();
            }

        }
