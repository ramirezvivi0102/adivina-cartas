package com.uniminuto.viviana;

import java.io.IOException;
import java.net.*;

public class ClienteCartas {
    private int puertoServidoUDP;
    private String direccionIpServidorUDP;

    private DatagramSocket socket;
    private InetAddress address;
    public double puntaje = 0;
    private int numeroIntento = 0;
    private int posicionEncontrada = -1;
    private byte[] buf;

    public ClienteCartas(int puertoServidorUDP, String direccionIpServidorUDP) {
        this.puertoServidoUDP = puertoServidorUDP;
        this.direccionIpServidorUDP = direccionIpServidorUDP;
    }

    private void inicializarServidor() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName(direccionIpServidorUDP);
    }

    public String adivinarCartasDesdeServidor(String msg) {
        try {
            this.inicializarServidor();
            buf = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, this.puertoServidoUDP);
            socket.send(packet);

            String tiraDeCartasAdivinadas = recibirMensageServidor();
            return tiraDeCartasAdivinadas;
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

    private boolean validarSiFueAdivinoElNumero(int numeroSeleccionadoUsuario, String tiraCartasAdivinadas){
        this.aumentarIntento();
        String[] listaCartasAdivinadas = tiraCartasAdivinadas.split(";");
        int posicion = 0;
        for(String cartaAdivinada: listaCartasAdivinadas){
            int numeroCarta = Integer.parseInt(cartaAdivinada);
            if(numeroCarta == numeroSeleccionadoUsuario){
                this.posicionEncontrada = posicion;
                return true;
            }
        }
        return false;
    }

    private void mostrarTiraCartasAdivinadas(String tiraCartasAdivinadas){
        String[] listaCartasAdivinadas = tiraCartasAdivinadas.split(";");
        int limiteSaltoLinea = 8;
        int posicionParaSaltoLinea = 1;
        System.out.println("");
        System.out.println("Carta del intento N° " + (this.numeroIntento + 1) );
        System.out.println("---------------------------");
        for(String cartaAdivinada: listaCartasAdivinadas) {
            Integer numeroCartaAdivinada = Integer.parseInt(cartaAdivinada);
            if(posicionParaSaltoLinea % limiteSaltoLinea != 0){
                System.out.print(cartaAdivinada + "\t");
            }else{
                System.out.println(cartaAdivinada);
            }
            posicionParaSaltoLinea ++;
        }
    }

    private void aumentarIntento() {
        this.numeroIntento++;
    }

    private void calcularPuntaje() {
        int intentos = 1;
        int puntaje = 0;
        int incremento1 = 0;
        int anterior = 0;
        int incrementoExcepcion = 0;
        do  {
            switch (intentos){
                case 1:
                    puntaje = 1;
                    anterior = 1;
                    incremento1 = 1;
                    break;
                case 5:
                    // Caso especial en la quinta carta. sigue con 15 (anterior)
                    anterior = puntaje;
                    incremento1 = anterior + 1;
                    puntaje = anterior;
                    incrementoExcepcion = anterior + 1;
                    break;
                default:
                    anterior = puntaje;
                    incremento1 = anterior +1;
                    puntaje = incremento1 + anterior + incrementoExcepcion;
                    incrementoExcepcion = 0;
                    break;
            }
            intentos++;
        }while ( intentos <= this.numeroIntento);

        this.puntaje = puntaje;
    }

    private String recibirMensageServidor() throws IOException {
        byte[] buf = new byte[256];
        DatagramPacket packetResult = new DatagramPacket(buf, buf.length);
        socket.receive(packetResult);
        String received = new String(packetResult.getData(), 0, packetResult.getLength());
        return received;
    }

    public void cerrarServidor() {
        socket.close();
    }

    public static void main(String[] args) {
        // Datos configuracion del servidor UDP
        String direccionIpServidorUDP = "localhost";
        int puertoServidorUDP = 9107;

        // Obtener carta
        int numeroCartaSeleccionadoUsuario = 22;
        boolean fueAdivinadoElNumero = false;

        // Inicializan y ejectuar servidores
        ClienteCartas clienteUdp = new ClienteCartas( puertoServidorUDP,  direccionIpServidorUDP);

        while (fueAdivinadoElNumero == false) {
            String tiraCartasServidor = clienteUdp.adivinarCartasDesdeServidor("obtenerCarta");
            clienteUdp.mostrarTiraCartasAdivinadas(tiraCartasServidor);
            fueAdivinadoElNumero = clienteUdp.validarSiFueAdivinoElNumero(numeroCartaSeleccionadoUsuario, tiraCartasServidor);
            if( clienteUdp.numeroIntento == 6){
                // Obliga a salir del bucle. No continua la consulta de cartas al servidor
                break;
            }
        }

        System.out.println("");
        System.out.println("RESULTADOS ");
        System.out.println("==============================================");
        System.out.println("El cliente habia seleccinado el numero: " + numeroCartaSeleccionadoUsuario);

        if(fueAdivinadoElNumero){
            clienteUdp.calcularPuntaje();
            System.out.println("El servidor adivino la carta en el acierto N° " +
                    clienteUdp.numeroIntento + " y con un Puntaje = " + clienteUdp.puntaje);
        }else {
            System.out.println("El servidor NO adivino la carta en sus "  +
                    clienteUdp.numeroIntento + " intentos");
        }

        clienteUdp.cerrarServidor();
    }
}