package com.uniminuto.viviana;

import java.io.IOException;
import java.net.*;

public class ClienteCartas {
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    private void inicializarServidor() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
    }

    public String sendMensajeServidor(String msg) {
        try {
            this.inicializarServidor();
            buf = msg.getBytes();
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, address, 9107);
            socket.send(packet);
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(
                    packet.getData(), 0, packet.getLength());
            return received;
        }
        // region:Excepciones
        catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // endregion
        return "fin";
    }

    public void close() {
        socket.close();
    }

    public static void main(String[] args) {

        // Inicializan servidores
        ClienteCartas clienteUdp = new ClienteCartas();

        // Ejecutan Servidores
        String resultado = clienteUdp.sendMensajeServidor("hola");

        System.out.println("fin. Resultado:" + resultado);

//        try {
//            cliente.inicializarServidor();
//            servidorCartasUDP.in
//        } catch (SocketException e) {
//            System.out.println("Error inicializando cliente servidor con detalles: "+e.getMessage() + ", causado por:" + e.getCause());
//            e.printStackTrace();
//        } catch (UnknownHostException e) {
//             System.out.println("Error inicializando cliente servidor con detalles: "+e.getMessage() + ", causado por:" + e.getCause());
//            e.printStackTrace();
//        }



    }
}