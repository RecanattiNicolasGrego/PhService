package com.service.Interfaz;

import com.service.Comunicacion.Modbus.Req.BasicProcessImage;

public interface Modbus extends dispositivoBase{
    static enum  ClasesModbus {
        Float("float"), Unsigned_Int("Unsigned_Int"),Signed_Int("Signed_Int"), Long("long");

        private final String tipo;

        ClasesModbus(String tipo) {
            this.tipo = tipo;
        }

        public String getTipo() {
            return tipo;
        }
    }
    public interface Slave extends Modbus{
        public BasicProcessImage getImageBasic();

        @Override void stop();

        public interface DeviceMessageListenerM_Slave {
            void CoilChange(int Num, int nRegistro, boolean oldVal, boolean newVal);

            void RegisterChange(int Num, int nRegistro, Short oldVal, Short newVal);
        }
        void init(DeviceMessageListenerM_Slave ListenerM_Slave, BasicProcessImage image);
        Boolean publicarHoldingRegister(Integer registro,ClasesModbus clase, String valor);
        Boolean publicarCoil(Integer registro,Boolean valor);
        String leerHoldingRegister(Integer registro,ClasesModbus clase);
        Boolean leerCoil(Integer registro);
    }

    public interface Master extends Modbus{

        void init();
        void leerHoldingRegister(Integer registro, ClasesModbus clase, RegisterCallback callback);
        void leerCoil(Integer registro, CoilCallback callback);

    }
    public interface CoilCallback {
        void finish(Boolean result);
    }
    public interface RegisterCallback {
        void finish(String result);
    }
}
