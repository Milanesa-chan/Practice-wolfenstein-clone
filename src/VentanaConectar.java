import javax.swing.*;
import java.awt.*;

public class VentanaConectar extends JFrame {
    private static VentanaConectar instance;
    JTextField campoNombre;
    JTextField campoIP;
    JLabel labelEstado;

    public static VentanaConectar getInstance(){
        if(instance == null)
            instance = new VentanaConectar();
        return instance;
    }

    public void inicializar(Motor m){
        this.setSize(400, 200);
        this.setLayout(new GridLayout(4, 1));
        JPanel tempPanel = new JPanel(new FlowLayout());
        tempPanel.add(new JLabel("Nombre: "));
        campoNombre = new JTextField(20);
        tempPanel.add(campoNombre);
        this.add(tempPanel);
        tempPanel = new JPanel(new FlowLayout());
        tempPanel.add(new JLabel("IP: "));
        campoIP = new JTextField(20);
        campoIP.addActionListener(m);
        campoIP.setActionCommand("ACEPTAR_CONEXION");
        tempPanel.add(campoIP);
        this.add(tempPanel);
        JButton botonAceptar = new JButton("Aceptar");
        botonAceptar.setActionCommand("ACEPTAR_CONEXION");
        botonAceptar.addActionListener(m);
        this.add(new JPanel(new FlowLayout()).add(botonAceptar).getParent());
        labelEstado = new JLabel("Acordate que la entrada no esta chequeada, no la cagues.");
        this.add(new JPanel().add(labelEstado).getParent());
        this.setLocationRelativeTo(null);
        this.pack();
        this.setResizable(false);
    }
}
