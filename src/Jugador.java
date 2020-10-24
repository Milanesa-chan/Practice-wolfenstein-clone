public class Jugador {
    double posX, posY;
    String nombre;

    public Jugador(double posX, double posY, String nombre) {
        this.posX = posX;
        this.posY = posY;
        this.nombre = nombre;
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
}
