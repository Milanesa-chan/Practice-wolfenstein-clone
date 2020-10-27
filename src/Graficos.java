import javax.swing.*;
import javax.swing.text.AttributeSet;
import java.awt.*;
import java.util.ArrayList;

public class Graficos extends JPanel {
    private double[] columnas;
    private final int offsetVentana = 20;
    private final double PASO_RAYO = 0.002;
    private final double FOV_VERTICAL = 150, FOV_HORIZONTAL = 100, MAX_PASOS = 10000;

    private int tamMinimapa = 256, xMinimapa = 20, yMinimapa = 0;

    public Graficos(int cantCols) {
        super();
        columnas = new double[cantCols];
        iniciarThread();
    }

    private void iniciarThread() {
        new Thread(() -> {
            while (true) {
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
        pintarJugadores(g);
        pintarMinimapa(g);
        pintarFPS(g);
        FPSCounter.getInstance().framesHastaAhora++;
    }

    private void pintarFPS(Graphics g) {
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("FPS: " + FPSCounter.getInstance().fps, 5, 20);
        g.drawString("Ping: " + FPSCounter.getInstance().ping + " ms", 5, 25 + g.getFontMetrics().getHeight());
    }

    private void pintarMinimapa(Graphics g) {
        yMinimapa = this.getHeight() - tamMinimapa - offsetVentana;

        Motor mot = Motor.getInstance();
        double angJug = mot.getAngJugador();
        double xJug = mot.getxJugador();
        double yJug = mot.getyJugador();
        int[][] matMapa = mot.getMatMapa();

        g.setColor(Color.BLUE);
        g.fillRect(xMinimapa - 10, yMinimapa - 10, tamMinimapa + 20, tamMinimapa + 20);
        int anchoCuad = tamMinimapa / matMapa[0].length;
        int altoCuad = tamMinimapa / matMapa.length;
        for (int i = 0; i < matMapa.length; i++) {
            for (int j = 0; j < matMapa[0].length; j++) {
                if (matMapa[i][j] == 0) g.setColor(Color.BLACK);
                else g.setColor(Color.RED);
                g.fillRect(xMinimapa + j * anchoCuad, yMinimapa + i * altoCuad, anchoCuad, altoCuad);
            }
        }

        g.setColor(Color.WHITE);
        int anchoMapa = matMapa[0].length;
        int altoMapa = matMapa.length;
        g.fillOval((int) ((xMinimapa + (xJug / anchoMapa) * tamMinimapa)) - 3, (int) ((yMinimapa + (yJug / altoMapa) * tamMinimapa)) - 3, 6, 6);

        int xJugMinimapa = (int) (xMinimapa + (xJug / anchoMapa) * tamMinimapa);
        int yJugMinimapa = (int) (yMinimapa + (yJug / altoMapa) * tamMinimapa);
        double largoLinea = 0.3;
        g.drawLine(xJugMinimapa, yJugMinimapa, (int) (xJugMinimapa + (largoLinea * Math.sin(-angJug - Math.toRadians(90)) * tamMinimapa)), (int) (yJugMinimapa + (largoLinea * Math.cos(-angJug - Math.toRadians(90)) * tamMinimapa)));

        g.setColor(Color.GREEN);
        for(Jugador j : Motor.getInstance().getListaJugadores()){
            double XJ = j.getPosX();
            double YJ = j.getPosY();
            g.fillOval((int)(xMinimapa+(XJ/anchoMapa)*tamMinimapa) - 3, (int)(yMinimapa + (YJ/altoMapa)*tamMinimapa)-3, 6, 6);
        }
    }

    private void pintarColumnas(Graphics g) {
        g.setColor(Color.RED);
        actualizarColumnas();
        int anchoColumna = this.getWidth() / columnas.length;
        for (int c = 0; c < columnas.length; c++) {
            if (columnas[c] != 0.0) {
                int altoColumna = (int) (this.getHeight() * columnas[c]);
                int inicioCol = (this.getHeight() / 2) - (altoColumna / 2);
                g.fillRect(c * anchoColumna, inicioCol - offsetVentana, anchoColumna, altoColumna);
            }
        }
    }

    private void actualizarColumnas() {
        Motor mot = Motor.getInstance();
        double angJug = mot.getAngJugador();
        double xJug = mot.getxJugador();
        double yJug = mot.getyJugador();
        int[][] matMapa = mot.getMatMapa();

        double pasoAng = Math.toRadians(FOV_HORIZONTAL) / columnas.length;
        double angInicial = angJug - (pasoAng * columnas.length) / 2;

        for (int c = 0; c < columnas.length; c++) {
            columnas[c] = calcularColumna(xJug, yJug, angInicial + pasoAng * c, matMapa);
        }
    }

    private double calcularColumna(double xInicial, double yInicial, double angulo, int[][] matMundo) {
        int cantPasos = 0;
        double incX = Math.sin((-angulo) - Math.toRadians(90)) * PASO_RAYO;
        double incY = Math.cos(-angulo - Math.toRadians(90)) * PASO_RAYO;
        double xAct = xInicial;
        double yAct = yInicial;
        while (cantPasos < MAX_PASOS) {
            if (xAct > matMundo[0].length || yAct > matMundo.length ||
                    yAct < 0 || xAct < 0) return 0.0;
            if (matMundo[(int) yAct][(int) xAct] != 0) break;
            cantPasos++;
            xAct += incX;
            yAct += incY;
        }
        if (cantPasos == 0) return 1.0;
        else if (cantPasos == MAX_PASOS) return 0.0;
        else return (double) 1 / (cantPasos / FOV_VERTICAL);
    }

    private void pintarFondo(Graphics g) {
        int iteraciones = 10;
        int paso = (this.getHeight() / 2 - 30) / iteraciones;
        int color = 0xff03ff;
        int pasoAlpha = 255 / iteraciones;
        for (int i = 0; i < iteraciones; i++) {
            g.setColor(new Color((color + ((255 - pasoAlpha * i) << 24)), true));
            g.fillRect(0, i * paso, this.getWidth(), paso);
        }
        g.setColor(new Color(0xb5b2a8));
        g.fillRect(0, this.getHeight() / 2 - offsetVentana, this.getWidth(), this.getHeight() / 2 + offsetVentana);
    }

    private void pintarJugadores(Graphics g) {
        ArrayList<Jugador> listaJug = Motor.getInstance().getListaJugadores();
        g.setColor(Color.GREEN);
        for (Jugador j : listaJug) {
            pintarJugador(j, g);
        }
    }

    private void pintarJugador(Jugador jug, Graphics g) {
        Motor mot = Motor.getInstance();
        double xJug = mot.getxJugador();
        double yJug = mot.getyJugador();
        double angJug = mot.getAngJugador();
        int[][] matMundo = mot.getMatMapa();

        if (esJugadorVisible(xJug, yJug, angJug, matMundo, jug)) {
            double xOtro = jug.getPosX();
            double yOtro = jug.getPosY();
            double angJugReal = -angJug - Math.toRadians(180);
            double angJugNormalizado = normalizarAngulo(angJugReal);
            double distX = xOtro - xJug;
            double distY = yJug - yOtro;
            double angAOtro = normalizarAngulo(Math.atan(distY / distX));
            if (distX < 0) angAOtro = normalizarAngulo(angAOtro - Math.toRadians(180));
            double angMin = angJugNormalizado - Math.toRadians(FOV_HORIZONTAL) / 2;
            double angMax = angJugNormalizado + Math.toRadians(FOV_HORIZONTAL) / 2;
            double distJug = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
            angMin = normalizarAngulo(angMin);
            angMax = normalizarAngulo(angMax);
            if (angMax < angMin) {
                angMax = normalizarAngulo(angMax + Math.toRadians(180));
                angAOtro = normalizarAngulo(angAOtro + Math.toRadians(180));
            }

            double anchoJug = 0.3;
            int altoJug = (int) (this.getHeight() * (1 / (3 * distJug)));

            double porcionHorizontal = Math.abs(angMax - angAOtro) / Math.toRadians(FOV_HORIZONTAL);
            int xJugPantalla = (int) (this.getWidth() * porcionHorizontal);
            int yJugPantalla = (int) ((this.getHeight() / 2) - (altoJug / 2));

            g.setColor(Color.GREEN);
            g.fillOval(xJugPantalla, yJugPantalla, (int) (anchoJug * altoJug), altoJug);

            String nomJug = jug.nombre;
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.setColor(Color.WHITE);
            int xNombre = (int) ((xJugPantalla + (anchoJug * altoJug) / 2) - g.getFontMetrics().stringWidth(nomJug) / 2);
            int yNombre = yJugPantalla - 5;
            g.drawString(nomJug, xNombre, yNombre);
        }
    }

    private boolean esJugadorVisible(double xJug, double yJug, double angJug, int[][] matMundo, Jugador otro) {
        double xOtro = otro.getPosX();
        double yOtro = otro.getPosY();
        double angJugReal = -angJug - Math.toRadians(180);
        double angJugNormalizado = normalizarAngulo(angJugReal);
        double distX = xOtro - xJug;
        double distY = yJug - yOtro;
        double angAOtro = normalizarAngulo(Math.atan(distY / distX));
        if (distX < 0) angAOtro = normalizarAngulo(angAOtro - Math.toRadians(180));
        double angMin = angJugNormalizado - Math.toRadians(FOV_HORIZONTAL) / 2;
        double angMax = angJugNormalizado + Math.toRadians(FOV_HORIZONTAL) / 2;
        angMin = normalizarAngulo(angMin);
        angMax = normalizarAngulo(angMax);
        if (angMax < angMin) {
            angMax = normalizarAngulo(angMax + Math.toRadians(180));
            angMin = normalizarAngulo(angMin + Math.toRadians(180));
            angAOtro = normalizarAngulo(angAOtro + Math.toRadians(180));
        }

        //Si es visible por el angulo, vemos si es visible por las paredes.
        if(angAOtro > angMin && angAOtro < angMax){
            double distAOtro = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
            //Recalculo angAOtro porque ya lo destrui multiples veces
            angAOtro = normalizarAngulo(Math.atan(distY / distX));
            if (distX < 0) angAOtro = normalizarAngulo(angAOtro - Math.toRadians(180));
            return lanzarRayo(xJug, yJug, angAOtro, matMundo)>distAOtro;
        }else return false;
    }

    //Tira un rayo y devuelve la distancia a la primera pared que encuentres. Si no choca tira 0.0
    private double lanzarRayo(double xInicial, double yInicial, double angulo, int[][] matMundo) {
        int cantPasos = 0;
        double incX = Math.cos(angulo) * PASO_RAYO;
        double incY = -Math.sin(angulo) * PASO_RAYO;
        double xAct = xInicial;
        double yAct = yInicial;
        while (cantPasos < MAX_PASOS) {
            if (xAct > matMundo[0].length || yAct > matMundo.length ||
                    yAct < 0 || xAct < 0) return 0.0;
            if (matMundo[(int) yAct][(int) xAct] != 0) break;
            cantPasos++;
            xAct += incX;
            yAct += incY;
        }
        if (cantPasos == 0) return 0.0;
        else return cantPasos*PASO_RAYO;
    }
    private double normalizarAngulo(double angulo) {
        double normalizado = angulo;
        while (normalizado >= 2 * Math.PI)
            normalizado -= 2 * Math.PI;
        while (normalizado < 0)
            normalizado += 2 * Math.PI;
        return normalizado;
    }
}
