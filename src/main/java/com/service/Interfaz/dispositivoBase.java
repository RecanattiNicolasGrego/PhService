package com.service.Interfaz;

import com.service.Comunicacion.Modbus.Req.BasicProcessImageSlave;

public interface dispositivoBase{
    void stop();
    interface ASCII extends dispositivoBase{
        public interface DeviceMessageListenerRs232 {
            void DeviceListener(int Num, String data);
        }

        void init(ASCII.DeviceMessageListenerRs232 Listenerrs232);
        /**
         * Envía un mensaje a un dispositivo escáner específico.
         * @param Msj el mensaje que se enviará al dispositivo escáner.
         */
        void Write( String Msj);
    }

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
            public BasicProcessImageSlave getImageBasic();

            @Override void stop();

            public interface DeviceMessageListenerM_Slave {
                void CoilChange(int Num, int nRegistro, boolean oldVal, boolean newVal);

                void RegisterChange(int Num, int nRegistro, Short oldVal, Short newVal);
            }
            void init(DeviceMessageListenerM_Slave ListenerM_Slave, BasicProcessImageSlave image);
            Boolean publicarHoldingRegister(Integer registro,ClasesModbus clase, String valor);
            Boolean publicarCoil(Integer registro,Boolean valor);
            String leerHoldingRegister(Integer registro,ClasesModbus clase);
            Boolean leerCoil(Integer registro);
        }

        public interface Master extends Modbus{

            void init();
            void leerHoldingRegister(Integer registro, ClasesModbus clase, RegisterCallback callback);
            void leerCoil(Integer registro, CoilCallback callback);
            void leerMultiplesHoldingRegisters(Integer registro,Integer Alcance,RegistersCallback callbackCrudo);
            void leerMultiplesCoils(Integer registro,Integer Alcance,CoilsCallback callbackCrudo);
            Boolean WriteMultiplesHoldingRegisters(Integer registro, short[] valor);
            Boolean WriteMultiplesCoils(Integer registro, boolean[] valor);
            Boolean WriteHoldingRegister(Integer registro, ClasesModbus clase, Integer valor);
            Boolean WriteCoil(Integer registro, Boolean valor);

        }
        public interface CoilCallback {
            void finish(Boolean result);
        }
        public interface CoilsCallback{
            void finish(boolean[] result);
        }
        public interface RegisterCallback {
            void finish(String result);
        }
        public interface RegistersCallback {
            void finish(short[] result);
        }
    }

}

