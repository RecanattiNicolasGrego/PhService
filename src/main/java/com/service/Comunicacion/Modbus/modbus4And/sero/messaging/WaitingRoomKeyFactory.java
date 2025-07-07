package com.service.Comunicacion.Modbus.modbus4And.sero.messaging;


public interface WaitingRoomKeyFactory {
    WaitingRoomKey createWaitingRoomKey(OutgoingRequestMessage request);

    WaitingRoomKey createWaitingRoomKey(IncomingResponseMessage response);
}
