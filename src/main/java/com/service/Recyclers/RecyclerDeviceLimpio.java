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

    import com.service.BalanzaService;
    import com.service.estructuras.classDevice;
    import com.service.PreferencesDevicesManager;
    import com.service.R;

    import java.util.ArrayList;
    import java.util.List;

    public class RecyclerDeviceLimpio extends RecyclerView.Adapter<RecyclerDeviceLimpio.ViewHolder> {
            private int selectedPos = RecyclerView.NO_POSITION;
            private List<classDevice> listaDispositivos;
            private Boolean band485=false;
            private Boolean onlyonce=false;
            private final LayoutInflater mInflater;
            private ItemClickListener mClickListener;
            private int lastPosition = -1;
            private final AppCompatActivity mainActivity;
            Boolean banderaVistaPrevia =false;
            int Device=0;
            String Salida="";
            Integer nuevomargin =0;
            String Salidastr="",Modelo="";
            String Devicestr;

        public void setPaddingchild(Integer padding){
            this.nuevomargin =padding;
            notifyDataSetChanged();
        }
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
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                Devicestr="";

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.ultimatelayout.getLayoutParams();
                params.setMargins(params.leftMargin, params.topMargin, nuevomargin, params.bottomMargin);
                holder.ultimatelayout.setLayoutParams(params);

                ArrayList<String> Direccion = listaDispositivos.get(position).getDireccion();
                if(Device==-1){
                    banderaVistaPrevia = true;
                    Device = PreferencesDevicesManager.obtenerIndiceTipo(listaDispositivos.get(position).getTipo());
                    Devicestr=listaDispositivos.get(position).getTipo();
                }
                Devicestr = PreferencesDevicesManager.listaKeyDeviceMap.get(Device);
                 if(banderaVistaPrevia){
                    Salidastr= listaDispositivos.get(position).getSalida();
                }else {
                    Salidastr = PreferencesDevicesManager.salidaMap.get(Salida);
                    switch (PreferencesDevicesManager.listaKeySalidaMap.indexOf(Salida)) {
                        case 3: {
                            if(Direccion!=null) {
                                int lenght = Direccion.size();
                                if (lenght > 0) {
                                    String IP = Direccion.get(0);
                                    holder.Slave.setText(IP);
                                }
                            }
                            holder.tvnombreipmac.setText("Red:");
                            holder.Slave.setClickable(false);

                            break;
                        }
                        case 4: {
                            if(Direccion!=null) {
                                int lenght = Direccion.size();
                                holder.Ltvid.setVisibility(VISIBLE);
                                holder.tvnombreipmac.setText("Mac:");
                                if (lenght > 0) {
                                    String mac = Direccion.get(0);
                                    holder.Slave.setText(mac);
                                }
                            }
                            holder.Slave.setClickable(false);
                            break;
                        }


                    }
                }
                Modelo = listaDispositivos.get(position).getModelo();
                 //if(listaDispositivos.size()>1 && !banderaVistaPrevia){
                  //   Modelo = listaDispositivos.get(0).getModelo();
                 //}
                 Boolean multipledevices=true;
                 switch (Device){
                     case 0:{
                         for (BalanzaService.ModelosClasesBzas modelo : BalanzaService.ModelosClasesBzas.values()) {
                             System.out.println(Modelo+" "+modelo.name());
                             if(Modelo.equals(modelo.name())){
                                 multipledevices = modelo.getTienePorDemanda();
                                 break;
                             }

                         }
                         break;
                     }
                     case 4:{
                         for (BalanzaService.ModelosClasesDispositivos modelo : BalanzaService.ModelosClasesDispositivos.values()) {
                             if(Modelo.equals(modelo.name())){
                                 multipledevices = modelo.getTiene485();
                                 break;
                             }

                         }
                     }
                 }
                 /*if(onlyonce){
                    band485=false;
                    multipledevices=false;
                }*/
                if(listaDispositivos.get(position).getID()>0 && ((PreferencesDevicesManager.EsPuertoSerie(Salidastr))||(PreferencesDevicesManager.aparentaDispositivo(PreferencesDevicesManager.listaKeyDeviceMap.get(4),Devicestr)))) {
                   if(multipledevices){ //&& !onlyonce) {
                       band485 = true;
                   }
                       String Slavestr = Integer.toString(listaDispositivos.get(position).getID());
                       holder.Slave.setText(Slavestr);
                       holder.Slave.setVisibility(VISIBLE);
                       holder.Ltvid.setVisibility(VISIBLE);

                }else{
                    holder.Slave.setVisibility(GONE);
                }

                // exceptionbool = !Modelo.equals(BalanzaService.ModelosClasesDispositivos.Slave.name());
                    System.out.println("SALIDA: "+Salidastr);
                    if((position<1||(band485||  multipledevices && Salidastr.equals(PreferencesDevicesManager.salidaMap.get(PreferencesDevicesManager.listaKeySalidaMap.get(5)))||Salidastr.equals(PreferencesDevicesManager.salidaMap.get(PreferencesDevicesManager.listaKeySalidaMap.get(3)))||Salidastr.equals(PreferencesDevicesManager.salidaMap.get(PreferencesDevicesManager.listaKeySalidaMap.get(4)))&& !(Salidastr.equals(PreferencesDevicesManager.salidaMap.get(3))))|| banderaVistaPrevia )){//&& !onlyonce) { //POR AHORA ESTE IF/ELSE SE QUEDA HASTA QUE HAGAMOS PROTOCOLOS CON ID  ||Salidastr.equals("PuertoSerie 3")|| Salidastr.equals("Red")
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
                                   if(Direccion!=null) {
                                       int lenght = Direccion.size();
                                       if (lenght > 0) {
                                           String Address = Direccion.get(0);
                                           holder.ipMac.setText(Address);
                                       }
                                   }
                                   holder.ipMac.setClickable(false);
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
                                   if(PreferencesDevicesManager.EsPuertoSerie(Salidastr)){
                                       holder.LtvModelo.setVisibility(VISIBLE);
                                       holder.tv_Modelo.setText(Modelo.replace("_"," "));


                                   }else{
                                       if(Salidastr.contains(PreferencesDevicesManager.salidaMap.get(PreferencesDevicesManager.listaKeySalidaMap.get(3)))||Salidastr.equals(PreferencesDevicesManager.salidaMap.get(PreferencesDevicesManager.listaKeySalidaMap.get(4)))){
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
                                   if(PreferencesDevicesManager.EsPuertoSerie(Salidastr)) {
                                       holder.LtvBaud.setVisibility(VISIBLE);
                                       holder.LtvData.setVisibility(VISIBLE);
                                       holder.LtvStop.setVisibility(VISIBLE);
                                       holder.constraintLayout12.setVisibility(VISIBLE);
                                       holder.Ltvparity.setVisibility(VISIBLE);
                                       if (Direccion != null) {
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
                                       }
                                       holder.tvparity.setClickable(false);
                                   }
                                   break;
                               }
                               case 4:{
                                   holder.tv_Modelo.setVisibility(VISIBLE);
                                   holder.LtvModelo.setVisibility(VISIBLE);
                                   holder.tv_Modelo.setText(Modelo.replace("_"," "));

                                   if(listaDispositivos.get(position).getID()>0){
                                       holder.Slave.setVisibility(VISIBLE);
                                       holder.Ltvid.setVisibility(VISIBLE);
                                       try {
                                           holder.Slave.setText(String.valueOf(listaDispositivos.get(position).getID()));
                                       } catch (Exception e) {
                                       }
                                   }
                                   holder.constraintLayout1.setVisibility(VISIBLE);
                                   if(PreferencesDevicesManager.EsPuertoSerie(Salidastr)) {
                                       holder.constraintLayout12.setVisibility(VISIBLE);

                                       holder.LtvBaud.setVisibility(VISIBLE);
                                       holder.LtvData.setVisibility(VISIBLE);
                                       holder.LtvStop.setVisibility(VISIBLE);
                                       holder.Ltvparity.setVisibility(VISIBLE);
                                       if (Direccion != null) {
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
                                       }
                                   }else if (Salidastr.contains(PreferencesDevicesManager.salidaMap.get(PreferencesDevicesManager.listaKeySalidaMap.get(3)))) {
                                           holder.LtvExceptionIP.setVisibility(VISIBLE);
                                           if(Direccion!=null){
                                               int lenght = Direccion.size();
                                               if (lenght > 0) {
                                                   String IP = Direccion.get(0);
                                                   holder.ipMac.setText(IP);
                                               }
                                           }
                                       }
                                   holder.tvnombreipmac.setText("Id");
                                   if(Modelo.replace(" ","_").equals(BalanzaService.ModelosClasesDispositivos.Master.name())){
                                       holder.tvnombreipmac.setText("Slave id");
                                   }
                                   holder.Slave.setClickable(false);
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
                holder.tv_Modelo.setText(Modelo.replace("_"," "));
               // if((Modelo.equals(BalanzaService.ModelosClasesDispositivos.Slave.name()) && Salidastr.equals(PreferencesDevicesManager.salidaMap.get(PreferencesDevicesManager.listaKeySalidaMap.get(3)))) || Device==3 && Salidastr.equals(PreferencesDevicesManager.salidaMap.get(PreferencesDevicesManager.listaKeySalidaMap.get(5)))){ // EXCEPCIONES
                   // onlyonce=true;
                //}
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
                TextView ipMac,Slave,numMOD,tvnombreipmac,tvBaud,tvStop,tvData,tvparity, tv_Modelo,tv_ipmac;

                LinearLayout constraintLayout1,constraintLayout12;
                ConstraintLayout constraintLayout2,ultimatelayout;
                LinearLayout Ltvid,LtvBaud,LtvStop,LtvData,Ltvparity,LtvModelo,LtvExceptionIP;


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
                    LtvExceptionIP = itemView.findViewById(R.id.LtvExceptionIP);

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
