import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Motor implements KeyListener {
    private static Motor instance;
    private Graficos instGraficos;
    private final int cantCols = 98;
    private final double VEL_ROTACION = 0.02, VEL_MOVIMIENTO = 0.02;
    private final String NOMBRE_IMG_MAPA = "mapa.png";

    private int[][] matMapa;

    private double xJugador = 2, yJugador = 2;
    private double angJugador = 0;

    private boolean tW, tA, tS, tD, tIzq, tDer;

    public static void main(String[] args) {
        Motor.getInstance();
    }

    public static Motor getInstance(){
        if(instance == null)
            instance = new Motor();
        return instance;
    }

    private Motor(){
        matMapa = new MatMapaLoader(NOMBRE_IMG_MAPA).getMatMapa();
        JFrame ventana = new JFrame("A");
        ventana.setSize(800, 600);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ventana.setVisible(true);
        instGraficos = new Graficos(cantCols);
        ventana.add(instGraficos);
        ventana.addKeyListener(this);
        iniciarThreadAct();
    }

    private void iniciarThreadAct(){
        new Thread(() -> {
            while(true){
                Actualizar();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void Actualizar(){
        if(tIzq) angJugador-=VEL_ROTACION;
        if(tDer) angJugador+=VEL_ROTACION;
        if(tW){
            //xJugador -= Math.cos(angJugador)*VEL_MOVIMIENTO;
            moverJugX(-Math.cos(angJugador)*VEL_MOVIMIENTO);
            //yJugador -= Math.sin(angJugador)*VEL_MOVIMIENTO;
            moverJugY(-Math.sin(angJugador)*VEL_MOVIMIENTO);
        }
        if(tS){
            //xJugador += Math.cos(angJugador)*VEL_MOVIMIENTO;
            moverJugX(Math.cos(angJugador)*VEL_MOVIMIENTO);
            //yJugador += Math.sin(angJugador)*VEL_MOVIMIENTO;
            moverJugY(Math.sin(angJugador)*VEL_MOVIMIENTO);
        }
        if(tA){
            //xJugador -= Math.sin(angJugador)*VEL_MOVIMIENTO;
            moverJugX(-Math.sin(angJugador)*VEL_MOVIMIENTO);
            //yJugador += Math.cos(angJugador)*VEL_MOVIMIENTO;
            moverJugY(Math.cos(angJugador)*VEL_MOVIMIENTO);
        }
        if(tD){
            //xJugador += Math.sin(angJugador)*VEL_MOVIMIENTO;
            moverJugX(Math.sin(angJugador)*VEL_MOVIMIENTO);
            //yJugador -= Math.cos(angJugador)*VEL_MOVIMIENTO;
            moverJugY(-Math.cos(angJugador)*VEL_MOVIMIENTO);
        }
    }

    private void moverJugX(double cantidad){
        double xJugNuevo = xJugador + cantidad;
        if(matMapa[(int) yJugador][(int) xJugNuevo] == 0 || matMapa[(int) yJugador][(int) xJugador] != 0){
            xJugador = xJugNuevo;
        }
    }

    private void moverJugY(double cantidad){
        double yJugNuevo = yJugador + cantidad;
        if(matMapa[(int) yJugNuevo][(int) xJugador] == 0 || matMapa[(int) yJugador][(int) xJugador] != 0){
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
        switch(e.getKeyCode()){
            case 87:
                tW=true;
                break;
            case 65:
                tA=true;
                break;
            case 83:
                tS=true;
                break;
            case 68:
                tD=true;
                break;
            case 37:
                tIzq=true;
                break;
            case 39:
                tDer=true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()){
            case 87:
                tW=false;
                break;
            case 65:
                tA=false;
                break;
            case 83:
                tS=false;
                break;
            case 68:
                tD=false;
                break;
            case 37:
                tIzq=false;
                break;
            case 39:
                tDer=false;
                break;
        }
    }
}
