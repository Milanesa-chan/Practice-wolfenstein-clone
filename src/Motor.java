import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Motor implements KeyListener, ActionListener {
    private static Motor instance;
    private Graficos instGraficos;
    private final int cantCols = 98;
    private final double VEL_ROTACION = 0.02, VEL_MOVIMIENTO = 0.02;
    private final String NOMBRE_IMG_MAPA = "mapa.png";

    private int[][] matMapa;
    private boolean conectadoAServer;

    private double xJugador = 2, yJugador = 2;
    private double angJugador = 0;
    private ArrayList<Jugador> listaJugadores;

    private boolean tW, tA, tS, tD, tIzq, tDer;

    public static void main(String[] args) {
        Motor.getInstance();
    }

    public static Motor getInstance() {
        if (instance == null)
            instance = new Motor();
        return instance;
    }

    private Motor() {
        VentanaConectar.getInstance().inicializar(this);
        listaJugadores = new ArrayList<>();
        matMapa = new MatMapaLoader().getMatMapa(NOMBRE_IMG_MAPA);
        JFrame ventana = new JFrame("A");
        ventana.setSize(800, 600);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        SwingUtilities.invokeLater(() -> ventana.setVisible(true));
        ventana.setLayout(new BorderLayout());
        inicializarMenu(ventana);
        instGraficos = new Graficos(cantCols);
        ventana.add(instGraficos, BorderLayout.CENTER);
        ventana.addKeyListener(this);
        iniciarThreadAct();
        listaJugadores.add(new Jugador(9, 17, "Test"));
    }

    private void inicializarMenu(JFrame ventana) {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuJuego = new JMenu("Juego");
        JMenuItem itemConectar = new JMenuItem("Conectar...");
        itemConectar.setActionCommand("ITEM_CONECTAR");
        itemConectar.addActionListener(this);
        menuJuego.add(itemConectar);
        menuBar.add(menuJuego);
        ventana.add(menuBar, BorderLayout.NORTH);
    }

    private void iniciarThreadAct() {
        new Thread(() -> {
            while (true) {
                Actualizar();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void Actualizar() {
        if (tIzq) angJugador -= VEL_ROTACION;
        if (tDer) angJugador += VEL_ROTACION;
        if (tW) {
            //xJugador -= Math.cos(angJugador)*VEL_MOVIMIENTO;
            moverJugX(-Math.cos(angJugador) * VEL_MOVIMIENTO);
            //yJugador -= Math.sin(angJugador)*VEL_MOVIMIENTO;
            moverJugY(-Math.sin(angJugador) * VEL_MOVIMIENTO);
        }
        if (tS) {
            //xJugador += Math.cos(angJugador)*VEL_MOVIMIENTO;
            moverJugX(Math.cos(angJugador) * VEL_MOVIMIENTO);
            //yJugador += Math.sin(angJugador)*VEL_MOVIMIENTO;
            moverJugY(Math.sin(angJugador) * VEL_MOVIMIENTO);
        }
        if (tA) {
            //xJugador -= Math.sin(angJugador)*VEL_MOVIMIENTO;
            moverJugX(-Math.sin(angJugador) * VEL_MOVIMIENTO);
            //yJugador += Math.cos(angJugador)*VEL_MOVIMIENTO;
            moverJugY(Math.cos(angJugador) * VEL_MOVIMIENTO);
        }
        if (tD) {
            //xJugador += Math.sin(angJugador)*VEL_MOVIMIENTO;
            moverJugX(Math.sin(angJugador) * VEL_MOVIMIENTO);
            //yJugador -= Math.cos(angJugador)*VEL_MOVIMIENTO;
            moverJugY(-Math.cos(angJugador) * VEL_MOVIMIENTO);
        }
    }

    private void moverJugX(double cantidad) {
        double xJugNuevo = xJugador + cantidad;
        if (matMapa[(int) yJugador][(int) xJugNuevo] == 0 || matMapa[(int) yJugador][(int) xJugador] != 0) {
            xJugador = xJugNuevo;
        }
    }

    private void moverJugY(double cantidad) {
        double yJugNuevo = yJugador + cantidad;
        if (matMapa[(int) yJugNuevo][(int) xJugador] == 0 || matMapa[(int) yJugador][(int) xJugador] != 0) {
            yJugador = yJugNuevo;
        }
    }

    public int[][] getMatMapa() {
        return matMapa;
    }

    public double getxJugador() {
        return xJugador;
    }

    public double getyJugador() {
        return yJugador;
    }

    public double getAngJugador() {
        return angJugador;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println(e.getKeyCode());
        switch (e.getKeyCode()) {
            case 87:
                tW = true;
                break;
            case 65:
                tA = true;
                break;
            case 83:
                tS = true;
                break;
            case 68:
                tD = true;
                break;
            case 37:
                tIzq = true;
                break;
            case 39:
                tDer = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case 87:
                tW = false;
                break;
            case 65:
                tA = false;
                break;
            case 83:
                tS = false;
                break;
            case 68:
                tD = false;
                break;
            case 37:
                tIzq = false;
                break;
            case 39:
                tDer = false;
                break;
        }
    }

    DatagramSocket socket;
    InetAddress serverIP;
    String nombre;

    public ArrayList<Jugador> getListaJugadores() {
        return listaJugadores;
    }

    private void conectarAServer() {
        try {
            System.out.println("Conectando...");
            conectadoAServer = false;
            VentanaConectar.getInstance().labelEstado.setText("Conectando...");
            VentanaConectar.getInstance().pack();
            socket = new DatagramSocket();
            System.out.println("Socket creado en puerto: "+socket.getPort());
            if (VentanaConectar.getInstance().campoNombre.getText().isEmpty())
                nombre = "JUGADOR_" + (int) (Math.random() * 3000) + " ";
            else
                nombre = VentanaConectar.getInstance().campoNombre.getText().replace(" ", "_") + " ";
            System.out.println("Nombre: "+nombre);
            serverIP = InetAddress.getByName(VentanaConectar.getInstance().campoIP.getText());
            System.out.println("IP Servidor: "+serverIP);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            try {
                enviarDatos();
                recibirMapa();
                VentanaConectar.getInstance().labelEstado.setText("Acordate que la entrada no esta chequeada, no la cagues.");
                VentanaConectar.getInstance().setVisible(false);
                conectadoAServer = true;
                while (conectadoAServer) {
                    //Thread.sleep(10);
                    long tiempoInicial = System.currentTimeMillis();
                    enviarDatos();
                    recibirJugadores();
                    FPSCounter.getInstance().agregarPing((int) (System.currentTimeMillis() - tiempoInicial));
                }
                System.out.println("Desconectado del servidor.");
                matMapa = new MatMapaLoader().getMatMapa(NOMBRE_IMG_MAPA);
                xJugador = 2.0;
                yJugador = 2.0;
            } catch (SocketTimeoutException e) {
                System.out.println("No se pudo conectar al servidor.");
                VentanaConectar.getInstance().labelEstado.setText("No se pudo conectar. (Error: 420)");
                VentanaConectar.getInstance().pack();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void recibirMapa() throws IOException {
        byte[] buffer = new byte[63000];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.setSoTimeout(30000);
        socket.receive(packet);
        System.out.println("Cargando mapa.");
        VentanaConectar.getInstance().labelEstado.setText("Cargando mapa...");
        VentanaConectar.getInstance().pack();
        String datos = new String(buffer);
        //System.out.println("Mapa recibido: "+datos.replace("\0", ""));
        String[] datosArray = datos.replace("\0", "").split(" ");
        int[] datosNumArray = new int[datosArray.length];
        for (int i = 0; i < datosArray.length; i++) {
            datosNumArray[i] = Integer.parseInt(datosArray[i]);
        }
        this.matMapa = new MatMapaLoader().getMatMapa(datosNumArray);
        xJugador = 2.0;
        yJugador = 2.0;
        System.out.println("Mapa cargado.");
    }

    private void recibirJugadores() {
        byte[] data = new byte[64000];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            socket.receive(packet);
            ArrayList<Jugador> nuevaLista = new ArrayList<>();
            String s = new String(packet.getData());
            String[] datos = s.split(" ");
            System.out.println("Mensaje del server: " + s.replace("\0", ""));
            int cantJug = Integer.parseInt(datos[0]);
            for (int i = 0; i < cantJug; i++) {
                Jugador nuevoJug = new Jugador(Double.parseDouble(datos[(i * 3) + 1]), Double.parseDouble(datos[(i * 3) + 2]), datos[(i * 3) + 3]);
                nuevaLista.add(nuevoJug);
            }
            listaJugadores = nuevaLista;
        } catch (SocketTimeoutException e) {
            conectadoAServer = false;
            System.out.println("Tiempo de espera de lista de jugadores superado.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void enviarDatos() {
        String datos = "";
        datos = datos.concat(xJugador + " " + yJugador + " " + nombre);
        try {
            DatagramPacket packet = new DatagramPacket(datos.getBytes(), datos.length(), serverIP, 42069);
            socket.send(packet);
            System.out.println("Se envio: " + datos.replace("\0", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("ITEM_CONECTAR")) {
            VentanaConectar.getInstance().setVisible(true);
        } else if (e.getActionCommand().equalsIgnoreCase("ACEPTAR_CONEXION")) {
            conectarAServer();
        }
    }
}
