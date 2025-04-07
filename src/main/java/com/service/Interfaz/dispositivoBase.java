package com.service.Interfaz;

import com.service.Comunicacion.Modbus.Req.BasicProcessImage;

public interface dispositivoBase{
    void stop();
    interface rs232 extends dispositivoBase{
        public interface DeviceMessageListenerRs232 {
            void DeviceListener(int Num, String data);
        }

        void init(dispositivoBase.rs232.DeviceMessageListenerRs232 Listenerrs232);
        /**
         * Envía un mensaje a un dispositivo escáner específico.
         * @param Msj el mensaje que se enviará al dispositivo escáner.
         */
        void Write( String Msj);
    }


}

