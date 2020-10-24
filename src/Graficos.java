import javax.swing.*;
import java.awt.*;

public class Graficos extends JPanel {
    private FPSCounter fpsCounter;
    private double[] columnas;
    private final int offsetVentana = 20;
    private final double PASO_RAYO = 0.002;
    private final double FOV_VERTICAL = 100, FOV_HORIZONTAL = 100, MAX_PASOS = 10000;

    private final int altoMinimapa = 200, anchoMinimapa = 200, xMinimapa = 20, yMinimapa = 340;
    public Graficos(int cantCols){
        super();
        columnas = new double[cantCols];
        fpsCounter = new FPSCounter();
        iniciarThread();
    }

    private void iniciarThread(){
        new Thread(() -> {
            while(true) {
                this.repaint();
            }
        }).start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        pintarFondo(g);
        pintarColumnas(g);
        pintarMinimapa(g);
        pintarFPS(g);
        fpsCounter.framesHastaAhora++;
    }

    private void pintarFPS(Graphics g){
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("FPS: "+fpsCounter.fps, 5, 20);
    }

    private void pintarMinimapa(Graphics g){
        Motor mot = Motor.getInstance();
        double angJug = mot.getAngJugador();
        double xJug = mot.getxJugador();
        double yJug = mot.getyJugador();
        int[][] matMapa = mot.getMatMapa();

        g.setColor(Color.BLUE);
        g.fillRect(xMinimapa-10, yMinimapa-10, anchoMinimapa+20, altoMinimapa+20);
        int anchoCuad = anchoMinimapa/matMapa[0].length;
        int altoCuad = altoMinimapa/matMapa.length;
        for(int i=0; i<matMapa.length; i++){
            for(int j=0; j<matMapa[0].length; j++){
                if(matMapa[i][j]==0) g.setColor(Color.BLACK);
                else g.setColor(Color.RED);
                g.fillRect(xMinimapa+j*anchoCuad, yMinimapa+i*altoCuad, anchoCuad, altoCuad);
            }
        }

        g.setColor(Color.WHITE);
        int anchoMapa = matMapa[0].length;
        int altoMapa = matMapa.length;
        g.fillOval((int) ((xMinimapa+(xJug/anchoMapa)*anchoMinimapa)) - 3, (int) ((yMinimapa+(yJug/altoMapa)*altoMinimapa)) - 3, 6, 6);

        int xJugMinimapa = (int) (xMinimapa+(xJug/anchoMapa)*anchoMinimapa);
        int yJugMinimapa = (int) (yMinimapa+(yJug/altoMapa)*altoMinimapa);
        double largoLinea = 0.3;
        g.drawLine(xJugMinimapa, yJugMinimapa, (int)(xJugMinimapa+(largoLinea*Math.sin(-angJug-Math.toRadians(90))*anchoMinimapa)), (int)(yJugMinimapa+(largoLinea*Math.cos(-angJug-Math.toRadians(90))*altoMinimapa)));
    }

    private void pintarColumnas(Graphics g){
        g.setColor(Color.RED);
        actualizarColumnas();
        int anchoColumna = this.getWidth()/columnas.length;
        for(int c=0; c<columnas.length; c++){
            if(columnas[c]!=0.0) {
                int altoColumna = (int) (this.getHeight()*columnas[c]);
                int inicioCol = (this.getHeight()/2)-(altoColumna/2);
                g.fillRect(c * anchoColumna, inicioCol-offsetVentana, anchoColumna, altoColumna);
            }
        }
    }

    private void actualizarColumnas(){
        Motor mot = Motor.getInstance();
        double angJug = mot.getAngJugador();
        double xJug = mot.getxJugador();
        double yJug = mot.getyJugador();
        int[][] matMapa = mot.getMatMapa();

        double pasoAng = Math.toRadians(FOV_HORIZONTAL)/columnas.length;
        double angInicial = angJug-(pasoAng*columnas.length)/2;

        for(int c=0; c<columnas.length; c++){
            columnas[c] = calcularColumna(xJug, yJug, angInicial+pasoAng*c, matMapa);
        }
    }

    private double calcularColumna(double xInicial, double yInicial, double angulo, int[][] matMundo){
        int cantPasos = 0;
        double incX = Math.sin((-angulo)-Math.toRadians(90))*PASO_RAYO;
        double incY = Math.cos(-angulo-Math.toRadians(90))*PASO_RAYO;
        double xAct = xInicial;
        double yAct = yInicial;
        while(cantPasos<MAX_PASOS){
            if(xAct>matMundo[0].length || yAct>matMundo.length ||
            yAct<0 || xAct<0) return 0.0;
            if(matMundo[(int) yAct][(int) xAct] != 0) break;
            cantPasos++;
            xAct += incX;
            yAct += incY;
        }
        if(cantPasos == 0) return 1.0;
        else if(cantPasos==MAX_PASOS) return 0.0;
        else return (double)1/(cantPasos/FOV_VERTICAL);
    }

    private void pintarFondo(Graphics g){
        int iteraciones = 10;
        int paso = (this.getHeight()/2-30)/iteraciones;
        int color = 0xff03ff;
        int pasoAlpha = 255/iteraciones;
        for(int i=0; i<iteraciones; i++){
            g.setColor(new Color((color+((255-pasoAlpha*i)<<24)), true));
            g.fillRect(0, i*paso, this.getWidth(), paso);
        }
        g.setColor(new Color(0xb5b2a8));
        g.fillRect(0, this.getHeight()/2-offsetVentana, this.getWidth(), this.getHeight()/2+offsetVentana);
    }
}
