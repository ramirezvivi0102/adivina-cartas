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
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 9107);
            socket.send(packet);

            String received = recibirMensageServidor();
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

    private String recibirMensageServidor() throws IOException {
        byte[] buf = new byte[256];
        DatagramPacket packetResult = new DatagramPacket(buf, buf.length);
        socket.receive(packetResult);
        String received = new String(packetResult.getData(), 0, packetResult.getLength());
        return received;
    }

    public void close() {
        socket.close();
    }

    public static void main(String[] args) {
        // Inicializan y ejectuar servidores
        ClienteCartas clienteUdp = new ClienteCartas();
        String resultado = clienteUdp.sendMensajeServidor("obtenerCarta");
        System.out.println("fin. Resultado:" + resultado);
        resultado = clienteUdp.sendMensajeServidor("obtenerCarta");
        System.out.println("fin. Resultado:" + resultado);
    }
}