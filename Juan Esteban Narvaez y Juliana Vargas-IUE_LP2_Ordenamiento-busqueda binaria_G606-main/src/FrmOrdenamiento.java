import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;
import javax.swing.JOptionPane;

import servicios.ServicioDocumento;
import servicios.Util;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import entidades.Documento;

public class FrmOrdenamiento extends JFrame {

    private JButton btnOrdenarBurbuja;
    private JButton btnOrdenarRapido;
    private JButton btnOrdenarInsercion;
    private JToolBar tbOrdenamiento;
    private JComboBox cmbCriterio;
    private JTextField txtTiempo;
    private JButton btnBuscar;
    private JTextField txtBuscar;

    private JTable tblDocumentos;

    public FrmOrdenamiento() {

        tbOrdenamiento = new JToolBar();
        btnOrdenarBurbuja = new JButton();
        btnOrdenarInsercion = new JButton();
        btnOrdenarRapido = new JButton();
        cmbCriterio = new JComboBox();
        txtTiempo = new JTextField();

        btnBuscar = new JButton();
        txtBuscar = new JTextField();

        tblDocumentos = new JTable();

        setSize(600, 400);
        setTitle("Ordenamiento Documentos");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        btnOrdenarBurbuja.setIcon(new ImageIcon(getClass().getResource("/iconos/Ordenar.png")));
        btnOrdenarBurbuja.setToolTipText("Ordenar Burbuja");
        btnOrdenarBurbuja.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnOrdenarBurbujaClick(evt);
            }
        });
        tbOrdenamiento.add(btnOrdenarBurbuja);

        btnOrdenarRapido.setIcon(new ImageIcon(getClass().getResource("/iconos/OrdenarRapido.png")));
        btnOrdenarRapido.setToolTipText("Ordenar Rapido");
        btnOrdenarRapido.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnOrdenarRapidoClick(evt);
            }
        });
        tbOrdenamiento.add(btnOrdenarRapido);

        btnOrdenarInsercion.setIcon(new ImageIcon(getClass().getResource("/iconos/OrdenarInsercion.png")));
        btnOrdenarInsercion.setToolTipText("Ordenar Inserción");
        btnOrdenarInsercion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnOrdenarInsercionClick(evt);
            }
        });
        tbOrdenamiento.add(btnOrdenarInsercion);

        
        cmbCriterio.setModel(new DefaultComboBoxModel<String>(
            new String[] {
                "Nombre Completo, Tipo de Documento",     // índice 0
                "Tipo de Documento, Nombre Completo",     // índice 1
                "Primer Apellido",                        // índice 2
                "Segundo Apellido",                       // índice 3
                "Nombre"                                 // índice 4
            }));
        tbOrdenamiento.add(cmbCriterio);
        tbOrdenamiento.add(txtTiempo);

        btnBuscar.setIcon(new ImageIcon(getClass().getResource("/iconos/Buscar.png")));
        btnBuscar.setToolTipText("Buscar");
        btnBuscar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnBuscar(evt);
            }
        });
        tbOrdenamiento.add(btnBuscar);
        tbOrdenamiento.add(txtBuscar);

        var spDocumentos = new JScrollPane(tblDocumentos);

        getContentPane().add(tbOrdenamiento, BorderLayout.NORTH);
        getContentPane().add(spDocumentos, BorderLayout.CENTER);

        String nombreArchivo = System.getProperty("user.dir")
                + "/src/datos/Datos.csv";

        ServicioDocumento.cargar(nombreArchivo);
        ServicioDocumento.mostrar(tblDocumentos);
    }

    private void btnOrdenarBurbujaClick(ActionEvent evt) {
        if (cmbCriterio.getSelectedIndex() >= 0) {
            Util.iniciarCronometro();
            ServicioDocumento.ordenarBurbuja(cmbCriterio.getSelectedIndex());
            txtTiempo.setText(Util.getTextoTiempoCronometro());
            ServicioDocumento.mostrar(tblDocumentos);
        }
    }

    private void btnOrdenarRapidoClick(ActionEvent evt) {
        if (cmbCriterio.getSelectedIndex() >= 0) {
            Util.iniciarCronometro();
            ServicioDocumento.ordenarRapido(cmbCriterio.getSelectedIndex());
            txtTiempo.setText(Util.getTextoTiempoCronometro());
            ServicioDocumento.mostrar(tblDocumentos);
        }
    }

    private void btnOrdenarInsercionClick(ActionEvent evt) {
        if (cmbCriterio.getSelectedIndex() >= 0) {
            Util.iniciarCronometro();
            ServicioDocumento.ordenarInsercionRecursivo(cmbCriterio.getSelectedIndex());
            txtTiempo.setText(Util.getTextoTiempoCronometro());
            ServicioDocumento.mostrar(tblDocumentos);
        }
    }

    
    private void btnBuscar(ActionEvent evt) {
        if (cmbCriterio.getSelectedIndex() >= 0) {
            String textoBusqueda = txtBuscar.getText().trim();
            if (!textoBusqueda.isEmpty()) {
                Util.iniciarCronometro();

                int criterio = cmbCriterio.getSelectedIndex();

                // Ordenar antes de buscar, para asegurar consistencia (puedes omitir si ya se ordenó)
                ServicioDocumento.ordenarSiEsNecesario(criterio);


                int pos = ServicioDocumento.buscarBinarioRecursivo(
                    textoBusqueda, 0, ServicioDocumento.getCantidad() - 1, criterio
                );

                txtTiempo.setText(Util.getTextoTiempoCronometro());

                if (pos != -1) {
                    Documento encontrado = ServicioDocumento.getDocumentoEn(pos);

                    String mensaje = "Encontrado:\n" +
                            "Nombre completo: " + encontrado.getNombreCompleto() +
                            "\nDocumento: " + encontrado.getDocumento();

                    if (pos > 0) {
                        Documento anterior = ServicioDocumento.getDocumentoEn(pos - 1);
                        mensaje += "\n\nAnterior:\n" +
                                  anterior.getNombreCompleto() + " - " + anterior.getDocumento();
                    }

                    if (pos < ServicioDocumento.getCantidad() - 1) {
                        Documento siguiente = ServicioDocumento.getDocumentoEn(pos + 1);
                        mensaje += "\n\nSiguiente:\n" +
                                  siguiente.getNombreCompleto() + " - " + siguiente.getDocumento();
                    }

                    JOptionPane.showMessageDialog(this, mensaje);

                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el documento.");
                }
            }
        }
    }


}
