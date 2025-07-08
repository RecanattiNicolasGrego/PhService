
### Inicializacion BalanzaService

        BalanzaService.init(AppCompatActivity,OnFragmentChangeListener) ( Devuelve Service ) Llamar 1 Sola vez luego
        BalanzaService.getInstance() Devuelve Service

### Dispositivos

    dispositivoBase dispositivoBase = BalanzaService.getInstance().Dispositivos.getDispositivo(1);
    ArrayList<Integer> lista = new ArrayList<>();
    for (int j = 0; j < 30; j++) lista.add(j);
        BasicProcessImageSlave img = new BasicProcessImageSlave.BPIBuilder(mSlave.getImageBasic())
            .InitDefaultHoldingRegisters(lista)
            .InitDefaultCoils(lista)
            .build();
        for (int i = 0; i <BalanzaService.ModelosClasesDispositivos.values().length ; i++) { // Ejecutar codigo segun modelo
            if(BalanzaService.ModelosClasesDispositivos.values()[i].compararInstancia(1)) {
                BalanzaService.ModelosClasesDispositivos modelo = BalanzaService.ModelosClasesDispositivos.values()[i];
                switch (modelo) {
                case Slave:{
                    dispositivoBase.Modbus.Slave mSlave = (dispositivoBase.Modbus.Slave) dispositivoBase;
                    mSlave.init(new dispositivoBase.Modbus.Slave.DeviceMessageListenerM_Slave() {
                    @Override
                    public void CoilChange(int Num, int nRegistro, boolean oldVal, boolean newVal) {
                        System.out.println("dispositivo Nº: "+Num+ " Coil "+nRegistro+ "cambio"); 
                    }   
                    @Override
                    public void RegisterChange(int Num, int nRegistro, Short oldVal, Short newVal) {
                        System.out.println("dispositivo Nº: "+Num+ " HoldingRegister "+nRegistro+ "cambio");
                    }
                },img);
            }
            case Master: {
                dispositivoBase.Modbus.Master mMaster = (dispositivoBase.Modbus.Master) dispositivoBase;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mMaster.WriteHoldingRegister(1,11);
                        mMaster.WriteMultiplesCoils(10,new Boolean[]);
                        mMaster.leerHoldingRegister(2, com.service.Interfaz.dispositivoBase.Modbus.ClasesModbus.Long, new dispositivoBase.Modbus.RegisterCallback() {
                        @Override
                        public void finish(String result) {}
                    });
                    mMaster.leerMultiplesHoldingRegisters(1, 10, new dispositivoBase.Modbus.RegistersCallback() {
                        @Override
                        public void finish(short[] result) {}
                    });
                    }
                });
            }
            case ASCII:{
                dispositivoBase.ASCII x = (dispositivoBase.ASCII) dispositivoBase;  
                x.init(new dispositivoBase.ASCII.DeviceMessageListenerRs232() {
                @Override
                public void DeviceListener(int Num, String data) {
                    System.out.println("dispositivo Nº: "+Num+ " data:"+data);
                    x.Write("OK");
                }
                });
            }
        }}
    }

### Inicializacion Expansiones,Escaneres

            BalanzaService.getInstance().Escaneres.init(new EscannerManager.ScannerMessageListener() {
                @Override
                public void EscannerListener(int num, String data) {
                    
                }
            });

            BalanzaService.getInstance().Expansiones.init(new ExpansionManager.ExpansionesMessageListener() {
                @Override
                public void ExpansionListener(int num, ArrayList<Integer> data) {
                    BalanzaService.getInstance().Expansiones.CambiarSalida(1,false);
                    Boolean x  = BalanzaService.getInstance().Expansiones.LeerEntrada(1);
                    
                }
            });

### Balanzas

        Balanza bzaEstandar = BalanzaService.getInstance().Balanzas; // Casteo a una balanza especifica
        Balanza bzaEspecifica = bzaEstandar.getBalanza(1);
        if(BalanzaService.ModelosClasesBzas.ITW410.compararInstancia(1)){
            Balanza.ITW410 bza = (Balanza.ITW410)bzaEspecifica;
            bza.Itw410FrmGetUltimoIndice(0);
        };
        for (int i = 0; i <BalanzaService.ModelosClasesBzas.values().length ; i++) { // Ejecutar codigo segun modelo
            if(BalanzaService.ModelosClasesBzas.values()[i].compararInstancia(1)) {
                BalanzaService.ModelosClasesBzas modelo = BalanzaService.ModelosClasesBzas.values()[i];
                switch (modelo) {
                    case Optima: {
                       
                    }
                    case ITW410: {

                    }
                    
                }
            }
        }
        
### Otros
       
    BalanzaService.getInstance().Impresoras.ImprimirEstandar(1,""); //imprime en la impresora 1
        
        Tipo a utilizar para la clase BalanzaService.Balanza es la interfaz "**Balanza**"
        ej: BalanzaService Service = BalanzaService.getInstance()
        ej: Balanza Balanzas = Service.Balanzas

### Desarrollo
    Para Realizar una nueva Balanza Es necesario modificar ModelosClasesBzas, crear un nuevo enum con Nombre(NombreClase.Class).
    El Nombre es el nombre visible del protocolo, la interfaz visual se actualiza con este. mientras que el nombreclase es 
    el protocolo de dicha balanza.
    Este protocolo va a tener que extender de BalanzaBase como asi tener valores estaticos con el nombre exacto
    tal cual aparece en EnumManager. debe tener todos los de Configuracion_Puertos y Balanzas. En caso de no tenerlo tomara un valor default escrito en Balanza Base
    Ademas debera tener el constructor igual a balanzabase y otros protocolos, en caso de necesitar un cambio hay que realizarlo en todos los protocolos
    y en initializateBalanzas ( donde se define el contructor de inicializacion ). Hay un ejemplo en NuevaBalanza().