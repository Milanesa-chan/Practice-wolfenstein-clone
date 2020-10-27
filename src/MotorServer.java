import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

public class MotorServer {
    private static MotorServer instance;
    private final int puertoServer = 42069;
    private final long timeoutJugador = 2500;
    private int[][] matMapa;
    private final ArrayList<Jugador> listaJug;
    private DatagramSocket socketSalida;
    private DatagramSocket socketEntrada;

    public static void main(String[] args){
        MotorServer.getInstance();
    }

    private MotorServer(){
        listaJug = new ArrayList<>();
        try {
            socketEntrada = new DatagramSocket(puertoServer);
            socketSalida = new DatagramSocket(42070);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        matMapa = new MatMapaLoader().getMatMapa("mapa_server.png");
        iniciarServer();
    }

    public static MotorServer getInstance(){
        if(instance == null)
            instance = new MotorServer();
        return instance;
    }

    private void iniciarServer(){
        /*
        new Thread(() -> {
            while(true){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                actualizarJugadores();
            }
        }).start();
        */
        new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(timeoutJugador);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                limpiarJugadores();
            }
        }).start();

        new Thread(() -> {
            while(true){
                escucharConexion();
            }
        }).start();
        System.out.println("Conexion iniciada.");
    }

    private void limpiarJugadores(){
        long tiempoAct = System.currentTimeMillis();
        //listaJug.removeIf(j -> tiempoAct - j.ultimoPulsoMillis > timeoutJugador);
        synchronized (listaJug) {
            Iterator<Jugador> it = listaJug.iterator();
            while(it.hasNext()){
                Jugador j = it.next();
                if (tiempoAct - j.ultimoPulsoMillis > timeoutJugador) {
                    System.out.println("Se desconecto el jugador: " + j.nombre);
                    it.remove();
                }
            }
        }
    }

    private void enviarListaAJugador(Jugador j){
        String datos = "";
        datos = datos.concat((listaJug.size()-1)+" ");

        for(Jugador jug : listaJug){
            if(!jug.equals(j)) {
                datos = datos.concat(jug.posX + " " + jug.posY + " " + jug.nombre + " ");
            }
        }
        //System.out.println("Se envio: "+datos+"\nCantidad de jug: "+listaJug.size());

        DatagramPacket packet = new DatagramPacket(datos.getBytes(), datos.length(), j.IP, j.puerto);
        try {
            socketSalida.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enviarMapaAJugador(Jugador j){
        String datos = "";
        datos = datos.concat(matMapa[1].length+" "+matMapa.length+" ");
        for(int[] col : matMapa){
            for(int i : col){
                datos = datos.concat(i+" ");
            }
        }
        DatagramPacket packet = new DatagramPacket(datos.getBytes(), datos.length(), j.getIP(), j.getPuerto());
        try {
            socketSalida.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void escucharConexion(){
        try {
            byte[] data = new byte[63000];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socketEntrada.receive(packet);

            String s = new String(packet.getData());
            String[] datos = s.split(" ");
            double posX = Double.parseDouble(datos[0]);
            double posY = Double.parseDouble(datos[1]);
            String nombre = datos[2];
            Jugador j = estaConectado(nombre);
            if(j==null){
                System.out.println("Se conecto el jugador: "+nombre+" ("+packet.getAddress().getHostName()+":"+packet.getPort()+")");
                Jugador nuevoJ = new Jugador(posX, posY, nombre, packet.getAddress(), packet.getPort());
                synchronized (listaJug) {
                    listaJug.add(nuevoJ);
                }
                nuevoJ.ultimoPulsoMillis = System.currentTimeMillis();
                enviarMapaAJugador(nuevoJ);
                //enviarListaAJugador(nuevoJ);
            }else{
                j.setPosX(posX);
                j.setPosY(posY);
                j.ultimoPulsoMillis = System.currentTimeMillis();
                j.setIP(packet.getAddress());
                j.setPuerto(packet.getPort());
                enviarListaAJugador(j);
            }
            //System.out.println("Recibido: "+s.replace("\0", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Jugador estaConectado(String nombre){
        synchronized (listaJug) {
            for (Jugador j : listaJug) {
                if (j.getNombre().equals(nombre)) return j;
            }
        }
        return null;
    }
}
