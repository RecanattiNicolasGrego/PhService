package com.service.Interfaz;

import com.service.Comunicacion.Modbus.Req.MatrizSlave;

public interface dispositivoBase{
    void stop();
    interface ASCII extends dispositivoBase{
        public interface DispositivoAsciiListener {
            void MensajeAscii(int Num, String data);
        }

        void Iniciar(DispositivoAsciiListener Listenerrs232);
        /**
         * Envía un mensaje a un dispositivo escáner específico.
         * @param Msj el mensaje que se enviará al dispositivo escáner.
         */
        void Escribir(String Msj);
    }

    public interface Modbus extends dispositivoBase{
        static enum  ClasesModbus {
            Float("float"), enteroSinSigno("Unsigned_Int"), enteroConSigno("Signed_Int"), Long("long");

            private final String tipo;

            ClasesModbus(String tipo) {
                this.tipo = tipo;
            }

            public String getTipo() {
                return tipo;
            }
        }
        public interface Slave extends Modbus{
            public MatrizSlave getMatrizSlaveBasica();

            @Override void stop();

            public interface DispositivoSlaveListener {
                void CoilCambiado(int Num, int nRegistro, boolean oldVal, boolean newVal);

                void RegistroCambiado(int Num, int nRegistro, Short oldVal, Short newVal);
            }
            void init(DispositivoSlaveListener ListenerM_Slave, MatrizSlave image);
            Boolean PublicarHoldingRegister(Integer registro, ClasesModbus clase, String valor);
            Boolean PublicarCoil(Integer registro, Boolean valor);
            String LeerHoldingRegister(Integer registro, ClasesModbus clase);
            Boolean LeerCoil(Integer registro);
        }

        public interface Master extends Modbus{

            void init();
            void LeerHoldingRegister(Integer registro, ClasesModbus clase, RegisterCallback callback);
            void LeerCoil(Integer registro, CoilCallback callback);
            void LeerMultiplesHoldingRegisters(Integer registro, Integer Alcance, RegistersCallback callbackCrudo);
            void LeerMultiplesCoils(Integer registro, Integer Alcance, CoilsCallback callbackCrudo);
            Boolean EscribirMultiplesHoldingRegister(Integer registro, short[] valor);
            Boolean EscribirMultiplesCoils(Integer registro, boolean[] valor);
            Boolean EscribirHoldingRegister(Integer registro, Integer valor);
            Boolean EscribirCoil(Integer registro, Boolean valor);

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

