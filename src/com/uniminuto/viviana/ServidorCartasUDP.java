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

                String tipoSolicitudCliente  = obtenerTextoSinEspaciosDeBitsEnBlanco(packet);
                switch (tipoSolicitudCliente){
                    case "obtenerCarta":
                        String mensajeSalida= obtenerCartaAleatoria();
                        enviarMensajeCliente(address, port, mensajeSalida);
                        break;
                    case "finalizar":
                        ejecutandose = false;
                        continue;
                }

            }
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private String obtenerTextoSinEspaciosDeBitsEnBlanco(DatagramPacket packet){
        String result = new String(packet.getData());
        char[] annoyingchar = new char[1];
        char[] charresult = result.toCharArray();
        result = "";
        for(int i=0;i<charresult.length;i++){
            if(charresult[i]==annoyingchar[0]){
                break;
            }
            result+=charresult[i];
        }
        return result;
    }

    private void enviarMensajeCliente(InetAddress address, int port, String mensajeSalida) throws IOException {
        DatagramPacket packetResult = new DatagramPacket(mensajeSalida.getBytes(), mensajeSalida.length(), address, port);
        socketCliente.send(packetResult);
    }

    private String obtenerCartaAleatoria(){
        return "60";
    }

    public static void main(String[] args) {
        // Iniciar y ejecutar servidor
        ServidorCartasUDP servidorCartasUDP = new ServidorCartasUDP();
        servidorCartasUDP.ejecutarServidorUdp();

    }
}
