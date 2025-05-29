package servicios;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import entidades.Documento;

public class ServicioDocumento {

    private static List<Documento> documentos = new ArrayList<>();

    
    private static boolean listaOrdenada = false;
    private static int criterioOrdenActual = -1;

    public static void ordenarSiEsNecesario(int criterio) {
        if (!listaOrdenada || criterioOrdenActual != criterio) {
            ordenarInsercion(criterio); // o Rapido o Burbuja
            listaOrdenada = true;
            criterioOrdenActual = criterio;
        }
    }


    public static void cargar(String nombreArchivo) {
        var br = Archivo.abrirArchivo(nombreArchivo);
        if (br != null) {
            try {
                var linea = br.readLine();
                linea = br.readLine();
                while (linea != null) {
                    var textos = linea.split(";");
                    var documento = new Documento(textos[0], textos[1], textos[2], textos[3]);
                    documentos.add(documento);
                    linea = br.readLine();
                }
            } catch (Exception ex) {

            }
        }
    }

    public static void mostrar(JTable tbl) {
        String[] encabezados = new String[] { "#", "Primer Apellido", "Segundo Apellido", "Nombres", "Documento" };
        String[][] datos = new String[documentos.size()][encabezados.length];

        int fila = 0;
        for (var documento : documentos) {
            datos[fila][0] = String.valueOf(fila + 1);
            datos[fila][1] = documento.getApellido1();
            datos[fila][2] = documento.getApellido2();
            datos[fila][3] = documento.getNombre();
            datos[fila][4] = documento.getDocumento();
            fila++;
        }

        var dtm = new DefaultTableModel(datos, encabezados);
        tbl.setModel(dtm);
    }

    
    private static boolean esMayor(Documento d1, Documento d2, int criterio) {
        switch (criterio) {
            case 0: // Nombre Completo, Tipo de Documento
                return d1.getNombreCompleto().compareTo(d2.getNombreCompleto()) > 0 ||
                        (d1.getNombreCompleto().equals(d2.getNombreCompleto()) &&
                        d1.getDocumento().compareTo(d2.getDocumento()) > 0);

            case 1: // Tipo de Documento, Nombre Completo
                return d1.getDocumento().compareTo(d2.getDocumento()) > 0 ||
                        (d1.getDocumento().equals(d2.getDocumento()) &&
                        d1.getNombreCompleto().compareTo(d2.getNombreCompleto()) > 0);

            case 2: // Primer Apellido
                return d1.getApellido1().compareTo(d2.getApellido1()) > 0;

            case 3: // Segundo Apellido
                return d1.getApellido2().compareTo(d2.getApellido2()) > 0;

            case 4: // Nombre
                return d1.getNombre().compareTo(d2.getNombre()) > 0;

            case 5: // Documento
                return d1.getDocumento().compareTo(d2.getDocumento()) > 0;

            default:
                return false;
        }
    }


    private static void intercambiar(int origen, int destino) {
        if (0 <= origen && origen < documentos.size() &&
                0 <= destino && destino < documentos.size()) {
            var temporal = documentos.get(origen);
            documentos.set(origen, documentos.get(destino));
            documentos.set(destino, temporal);
        }
    }

    public static void ordenarBurbuja(int criterio) {
        for (int i = 0; i < documentos.size() - 1; i++) {
            for (int j = i + 1; j < documentos.size(); j++) {
                System.out.println(
                        "d[i]=" + documentos.get(i).getNombreCompleto() + " " + documentos.get(i).getDocumento());
                System.out.println(
                        "d[j]=" + documentos.get(j).getNombreCompleto() + " " + documentos.get(j).getDocumento());

                if (esMayor(documentos.get(i), documentos.get(j), criterio)) {
                    intercambiar(i, j);
                }
            }
        }
    }

    private static int getPivote(int inicio, int fin, int criterio) {
        var pivote = inicio;
        var documentoPivote = documentos.get(pivote);

        for (int i = inicio + 1; i <= fin; i++) {
            if (esMayor(documentoPivote, documentos.get(i), criterio)) {
                pivote++;
                if (i != pivote) {
                    intercambiar(i, pivote);
                }
            }
        }
        if (inicio != pivote) {
            intercambiar(inicio, pivote);
        }

        return pivote;
    }

    private static void ordenarRapido(int inicio, int fin, int criterio) {
        if (inicio < fin) {
            var pivote = getPivote(inicio, fin, criterio);
            ordenarRapido(inicio, pivote - 1, criterio);
            ordenarRapido(pivote + 1, fin, criterio);
        }
    }

    public static void ordenarRapido(int criterio) {
        ordenarRapido(0, documentos.size() - 1, criterio);
    }

    public static void ordenarInsercion(int criterio) {
        for (int i = 1; i < documentos.size(); i++) {
            var documentoActual = documentos.get(i);
            // mover los documentos mayores que el actual
            int j = i - 1;
            while (j >= 0 && esMayor(documentos.get(j), documentoActual, criterio)) {
                documentos.set(j + 1, documentos.get(j));
                j--;
            }
            // insertar el documento
            documentos.set(j + 1, documentoActual);
        }
    }

    private static void ordenarInsercionRecursivo(int posicion, int criterio) {
        if (posicion == 0) {
            return;
        }
        ordenarInsercionRecursivo(posicion - 1, criterio);

        var documentoActual = documentos.get(posicion);
        //System.out.println(documentoActual.getNombreCompleto());
        // mover los documentos mayores que el actual
        int j = posicion - 1;
        while (j >= 0 && esMayor(documentos.get(j), documentoActual, criterio)) {
            //System.out.println(documentos.get(j));
            documentos.set(j + 1, documentos.get(j));
            j--;
        }
        // insertar el documento
        documentos.set(j + 1, documentoActual);
    }

    public static void ordenarInsercionRecursivo(int criterio) {
        ordenarInsercionRecursivo(documentos.size()-1, criterio);
    }

    // Nuevo método: búsqueda binaria recursiva
    
    public static int buscarBinarioRecursivo(String texto, int inicio, int fin, int criterio) {
        if (inicio > fin) return -1;

        int medio = (inicio + fin) / 2;
        Documento doc = documentos.get(medio);

        String valorComparar;

        switch (criterio) {
            case 0: // Nombre Completo, Documento
                valorComparar = doc.getNombreCompleto();
                break;
            case 1: // Documento, N. Completo
                valorComparar = doc.getDocumento();
                break;
            case 2: // Primer Apellido
                valorComparar = doc.getApellido1();
                break;
            case 3: // Segundo Apellido
                valorComparar = doc.getApellido2();
                break;
            case 4: // Nombre
                valorComparar = doc.getNombre();
                break;
            case 5: // Documento
                valorComparar = doc.getDocumento();
                break;
            default:
                return -1;
        }

        int cmp = valorComparar.compareToIgnoreCase(texto);

        if (valorComparar.toUpperCase().contains(texto.toUpperCase())) return medio;

        if (cmp > 0) {
            return buscarBinarioRecursivo(texto, inicio, medio - 1, criterio);
        } else {
            return buscarBinarioRecursivo(texto, medio + 1, fin, criterio);
        }
    }



    // Nuevo método: obtener Documento en una posición
    public static Documento getDocumentoEn(int pos) {
        if (pos >= 0 && pos < documentos.size()) {
            return documentos.get(pos);
        }
        return null;
    }

    // Nuevo método: obtener el tamaño de la lista
    public static int getCantidad() {
        return documentos.size();
    }
}
