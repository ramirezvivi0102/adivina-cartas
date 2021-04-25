package com.uniminuto.viviana;

import java.net.*;
import java.io.*;
import java.net.SocketException;

public class ServidorCartasUDP extends Thread {
    private DatagramSocket socketCliente;
    private boolean running;
    private byte[] bufer = new byte[256];

   private void inicializarSocketServidor() throws SocketException {
       System.out.println("Servidor CartasUDP Iniciado y Esperando cliente.....");
       //Creacion del socket
       socketCliente = new DatagramSocket(9107);
   }

    public void ejecutarServidorUdp(){
        boolean ejecutandose = true;
        try {
            this.inicializarSocketServidor();

            while (ejecutandose) {
                DatagramPacket packet
                        = new DatagramPacket(bufer, bufer.length);
                socketCliente.receive(packet);

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(bufer, bufer.length, address, port);
                String received
                        = new String(packet.getData(), 0, packet.getLength());

                if (received.equals("end")) {
                    ejecutandose = false;
                    continue;
                }
                socketCliente.send(packet);

            }
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private String obtenerCartaAleatoria(){
        return "60";
    }

}
