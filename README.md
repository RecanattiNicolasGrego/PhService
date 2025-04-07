**********************************************************Inicializacion BalanzaService**********************************************************

        BalanzaService.init(AppCompatActivity,OnFragmentChangeListener) ( Devuelve Service ) Llamar 1 Sola vez luego
        BalanzaService.getInstance() Devuelve Service

************************************************Inicializacion Dispositivos,Expansiones,Escaneres************************************************

        BalanzaService.getInstance().Devices.init(new DeviceManager.DeviceMessageListener() {
            @Override
            public void DeviceListener(int Num, String data) {
                
            }
        });( Devuelve DeviceManager ) Llamar 1 Sola vez luego
        BalanzaService.getInstance().getInstanceDevices() Devuelve DeviceManager

         BalanzaService.getInstance().Escaneres.init(new EscannerManager.ScannerMessageListener() {
            @Override
            public void EscannerListener(int num, String data) {
                
            }
        });( Devuelve EscaneresManager ) Llamar 1 Sola vez luego
        BalanzaService.getInstance().getInstanceEscaneres()  Devuelve EscaneresManager

        BalanzaService.getInstance().Expansiones.init(new ExpansionManager.ExpansionesMessageListener() {
            @Override
            public void ExpansionListener(int num, ArrayList<Integer> data) {
                
            }
        }); ( Devuelve ExpansionesManager ) Llamar 1 Sola vez luego 
        
        BalanzaService.getInstance().getInstanceExpansiones()  Devuelve ExpansionesManager

****************************************************************Poliformismo****************************************************************

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
        
****************************************************************otros****************************************************************
        BalanzaService.getInstance().Impresoras.ImprimirEstandar(1,""); //imprime en la impresora 1



        Tipo a utilizar para la clase BalanzaService.Balanza es la interfaz "**Balanza**"
        ej: BalanzaService Service = BalanzaService.getInstance()
        ej: Balanza Balanzas = Service.Balanzas


        
