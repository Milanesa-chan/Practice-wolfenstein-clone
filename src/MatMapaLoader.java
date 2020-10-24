import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MatMapaLoader {
    private String nombreMapa;
    private BufferedImage imagenMapa;

    public MatMapaLoader(String nombreMapa) {
        this.nombreMapa = nombreMapa;
    }

    public int[][] getMatMapa(){
        try {
            if(this.getClass().getClassLoader().getResource(nombreMapa)==null) throw new IOException();
            imagenMapa = ImageIO.read(this.getClass().getClassLoader().getResource(nombreMapa));
            int anImg = imagenMapa.getWidth();
            int alImg = imagenMapa.getHeight();
            int[][] matMapa = new int[alImg][anImg];
            int sigNumero = 0;
            for(int i=0; i<alImg; i++){
                for(int j=0; j<anImg; j++){
                    if((imagenMapa.getRGB(j, i) & 0x00FFFFFF)!= 0) sigNumero = 1;
                    else sigNumero = 0;
                    matMapa[i][j] = sigNumero;
                }
            }
            System.out.println("Mapa cargado:");
            for(int[] fila : matMapa){
                for(int p : fila){
                    System.out.print(p+" ");
                }
                System.out.println();
            }
            return matMapa;
        } catch (IOException e) {
            System.err.println("No se encontro la imagen-mapa: '"+nombreMapa+"'");
            return new int[][]{{1}};
        }
    }
}
