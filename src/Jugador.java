import java.net.InetAddress;

public class Jugador {
    double posX, posY;
    String nombre;
    InetAddress IP;
    int puerto;
    long ultimoPulsoMillis;

    public Jugador(double posX, double posY, String nombre) {
        this.posX = posX;
        this.posY = posY;
        this.nombre = nombre;
    }

    public Jugador(double posX, double posY, String nombre, InetAddress IP, int puerto) {
        this.posX = posX;
        this.posY = posY;
        this.nombre = nombre;
        this.IP = IP;
        this.puerto = puerto;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public InetAddress getIP() {
        return IP;
    }

    public void setIP(InetAddress IP) {
        this.IP = IP;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }
}
