package com.uniminuto.viviana;

import java.net.*;
import java.io.*;
import java.net.SocketException;
import java.util.*;

public class ServidorCartasUDP extends Thread {
    private DatagramSocket socketCliente;
    private boolean running;
    private byte[] bufer = new byte[256];
    private int numeroMaximoAGenerar = 100;
    private int cantidadNumerosAGenerar = 64;

   private void inicializarSocketServidor(int puertoEscuchaServidorUDP) throws SocketException {
       System.out.println("Servidor CartasUDP Iniciado y Esperando cliente.....");
       //Creacion del socket
       socketCliente = new DatagramSocket(puertoEscuchaServidorUDP);
   }

    public void ejecutarServidorUdp(int puertoEscuchaServidorUDP){
        boolean ejecutandose = true;
        try {
            this.inicializarSocketServidor(puertoEscuchaServidorUDP);

            while (ejecutandose) {

                // Detecta paquete del cliente , tambien detecta el puerto y su dirección ip
                DatagramPacket packet = new DatagramPacket(bufer, bufer.length);
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
                        // Obliga a salir del bucle. Detiene el servidor
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
        Random r = new Random();

        List<Integer> alreadyUsedNumbers = new ArrayList<Integer>();
        int i = 1;
        // Vamos a generar 10 números aleatorios sin repetición
        while (alreadyUsedNumbers.size()< cantidadNumerosAGenerar) {
            // Número aleatorio entre 0 y numeroMaximoAGenerar, excluido el numeroMaximoAGenerar.
            int randomNumber = r.nextInt(numeroMaximoAGenerar);

            // Si el nuevo numero aleatorio generado (randomNumber) no ha sido generado previamente, entonces se agrega
            if (!alreadyUsedNumbers.contains(randomNumber)){
                alreadyUsedNumbers.add(randomNumber);
            }
        }

        // Desordena la lista de numeros ordenados
        Collections.shuffle(alreadyUsedNumbers);

        // Genera una cadena de string (se le ha llamado tira) separados por ; ejemplo: "13;25;2;33;91...ext"
        StringBuilder tiraNumeros = new StringBuilder();
        for(Integer numero: alreadyUsedNumbers){
            tiraNumeros.append(numero + ";");
        }
       return tiraNumeros.toString();
    }

    public static void main(String[] args) {
        // Iniciar y ejecutar servidor
        ServidorCartasUDP servidorCartasUDP = new ServidorCartasUDP();
        int puertoEscuchaServidorUDP = 9107;
        servidorCartasUDP.ejecutarServidorUdp(puertoEscuchaServidorUDP);

    }
}
