/*
 * INTERGRANTES:
 * - Mayra Angélica Escobedo García
 * - Claudia Irene Hernández Flores
 * - Luis Felipe Renovato Ávila
 */

// Importar librerías
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {

    // Crear un conjunto de ámbitos
    static HashSet<String> ambitos = new HashSet<>();
    // HashMap para almacenar las variables declaradas en cada ámbito
    static HashMap<String, HashSet<String>> variablesPorAmbito = new HashMap<>();

    public static void main(String[] args) {
        try {
            // Usar JFileChooser para seleccionar el archivo a leer
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT files", "txt");
            fileChooser.setFileFilter(filter);
            fileChooser.setDialogTitle("Seleccione el archivo a leer");
            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(null, "Debe seleccionar un archivo", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String archivoLeer = fileChooser.getSelectedFile().getPath();

            // Usar JFileChooser para seleccionar la carpeta donde guardar las tablas generadas
            JFileChooser directoryChooser = new JFileChooser();
            directoryChooser.setDialogTitle("Seleccione la carpeta donde guardar las tablas");
            directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int directoryVal = directoryChooser.showOpenDialog(null);
            if (directoryVal != JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(null, "Debe seleccionar una carpeta", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String rutaGuardar = directoryChooser.getSelectedFile().getPath();

            // Leer archivo de tabla de tokens
            BufferedReader leer = new BufferedReader(new FileReader(archivoLeer));
            // Escribir archivo de tabla de direcciones
            BufferedWriter escribir_dir = new BufferedWriter(new FileWriter(rutaGuardar + "/tabla_direcciones.txt"));
            // Escribir archivo de tabla de spimbolos
            BufferedWriter escribir_sim = new BufferedWriter(new FileWriter(rutaGuardar + "/tabla_simbolos.txt"));
            // Escribir archivo de tabla de tokens modificada
            BufferedWriter escribir_tokens_mod = new BufferedWriter(new FileWriter(rutaGuardar + "/tabla_tokens_modificada.txt"));

            // Cada línea del archivo
            String linea;
            // Divide en partes la línea (4 partes)
            String[] parts;
            // Almacenar cada parte de una línea en su elemento correspondiente, ya sea si es lexema, token, posición en tabla o núnero de línea
            StringBuilder lexemas = new StringBuilder();
            StringBuilder tokens = new StringBuilder();
            StringBuilder posiciones = new StringBuilder();
            StringBuilder num_linea = new StringBuilder();

            // Tabla de direcciones
            StringBuilder id = new StringBuilder(); // para los lexemas que cumplen con la condición
            StringBuilder tokenValues = new StringBuilder();
            StringBuilder lineNumbers = new StringBuilder();
            StringBuilder vciValues = new StringBuilder();

            // Se va almacenando el ámbito al que corresponden las línea
            StringBuilder ambito = new StringBuilder();

            // Agregar una variable para verificar si ya se encontró un ámbito
            boolean ambitoEncontrado = false;

            // Variable que indica si el lexema actual es un vector o matriz.
            boolean isVectorOrMatrix = false;

            // Almacena en orden los valores que corresponden a la tabla de direcciones
            //ArrayList<String[]> tabla_dir = new ArrayList<String[]>();
            StringBuilder tabla_dir = new StringBuilder();
            // Almacena en orden los valores que corresponden a la tabla de símbolos
            StringBuilder tabla_sim = new StringBuilder();

            // Inicializa el contador de posición en tabla de símbolos
            int positionCounter_sim = -1;
            // Inicializa el contador de posición en tabla de direcciones
            int positionCounter_dir = -1;

            int estado = 0;
            String dimension1 = "0";
            String dimension2 = "0";
            String idetifierSaved = "";

            // Almacena en orden los valores que corresponden a la tabla de tokens modificada
            StringBuilder tabla_tokens_mod = new StringBuilder();

            // Variable para almacenar el ámbito actual
            String currentAmbito = "";

            // Variable para almacenar el token anterior
            String previousToken = "";

            // DIMENSIONES
            // Variables de control para identificadores duplicados
            boolean bandera_dir = false;
            boolean bandera_simb = false;
            // Variables para identificar corchetes 
            boolean bandera_corch = false;
            boolean bandera_corch2 = false;

            // Variables para manejo de errores de dimensiones
            boolean debe_tener_1_dim = false;
            boolean debe_tener_2_dim = false;
            boolean no_tiene_dim = false;

            // Cuando no viene un identificador, ni un [] ni un ;
            //boolean viene_otra_cosa = false;

            boolean viene_otra_dimension = false;

            String prevIdentifier = "";

            // Es para que lea la línea siguiente, y saber qué viene después
            BufferedReader leerSiguiente = new BufferedReader(new FileReader(archivoLeer));
            // Cada línea siguiente del archivo
            String lineaSiguiente = leerSiguiente.readLine();

            // Lee línea hasta que acaba el archivo
            while ((linea = leer.readLine()) != null) {
                lineaSiguiente = leerSiguiente.readLine();
                // Si la línea inicia con , este elemento es un lexema
                if (linea.startsWith(",")) {
                    // Por lo que la línea se divide en 5 partes
                    parts = linea.split(",", 5);
                    // Se almacena cada elemento según corresponda
                    // El primer elemento es un lexema
                    lexemas.append(parts[0]).append(",").append(parts[1]).append("\n");
                    // El segundo elemento es el token
                    tokens.append(parts[2]).append("\n");
                    // El tercer elemento es la posición en tabla
                    posiciones.append(parts[3]).append("\n");
                    // El cuarto y último elemento es el número de línea
                    num_linea.append(parts[4]).append("\n");

                    // Si no es un lexema que requiere modificación, simplemente copia la línea original
                    tabla_tokens_mod.append(linea).append("\n");

                    if (bandera_corch == true) {
                        bandera_corch = false;
                        bandera_corch2 = true;
                    }
                } else {
                    // En caso que la línea no inicie con , se divide solo en 4 partes, diviendo cuando haya una ,
                    parts = linea.split(",", 4);
                    // El primer elemento es un lexema
                    lexemas.append(parts[0]).append("\n");
                    // El segundo elemento es el token
                    tokens.append(parts[1]).append("\n");
                    // El tercer elemento es la posición en tabla
                    posiciones.append(parts[2]).append("\n");
                    // El cuarto y último elemento es el número de línea
                    num_linea.append(parts[3]).append("\n");

                    // Verificación de lexemas para la tabla de direcciones
                    String lexema = parts[0];
                    // Si un lexema cumple con el patrón de iniciar con letra, y tener alguna otra letra o número o no,
                    // pero termina con @ (Identificadores tipo programa/rutina), va a la tabla de direcciones
                    if (lexema.matches("[a-zA-Z][a-zA-Z0-9]*@")) {
                        // Verificar si el lexema ya existe en la tabla_dir
                        int index = tabla_dir.indexOf(lexema);
                        // -1 significa que ya existe
                        if (index != -1) {
                            // Si ya existe busca el positionCounter_dir que le corresponde
                            // El lexema ya existe, buscar el valor de positionCounter_dir en la tabla_tokens_mod
                            int positionCounter_anterior = -1;
                            String[] lines = tabla_tokens_mod.toString().split("\\r?\\n");
                            for (String line : lines) {
                                String[] tokens1 = line.split(",");
                                // Si el lexema es igual lo almacena
                                if (tokens1[0].equals(lexema)) {
                                    // Guardamos el correspondiente valor de positionCounter_tok, que sería el que está en el lexama anteriormente declarado
                                    positionCounter_anterior = Integer.parseInt(tokens1[2]);
                                    break;
                                }
                            }

                            // Asignar el valor correspondiente a positionCounter_dir
                            if (positionCounter_anterior != -1) {
                                positionCounter_dir = positionCounter_anterior;
                            } else {
                                positionCounter_dir++;
                            }
                        } // Si no existe, lo agrega a la tabla de direcciones
                        else {
                            //if (previousToken.equals("-1") || previousToken.equals("-14"))
                            // En la tabla de direcciones se va a almacenar el ID (lexema), token, número de línea (num_linea) y VCI (0)
                            tabla_dir.append(parts[0]).append("\t").append(parts[1]).append("\t").append(parts[3]).append("\t0\n");

                            // Y aumenta el contador de posiciones
                            if (parts[2].equals("-2")) {
                                positionCounter_dir++;
                            }
                        }

                        // Verifica si el ámbito ya ha sido agregado al conjunto
                        if (!ambitos.contains(lexema)) {
                            // Agrega el ámbito al conjunto y actualiza el ámbito actual
                            ambitos.add(lexema);
                            ambito = new StringBuilder(lexema);
                        }
                        // Actualiza el ámbito actual
                        currentAmbito = lexema;

                        // Agregar la línea modificada a la tabla de tokens modificada
                        tabla_tokens_mod.append(parts[0]).append(",").append(parts[1]).append(",").append(positionCounter_dir).append(",").append(parts[3]).append("\n");

                    } // Si el lexema que viene es "var", significa que se van a declarar variables
                    // Por lo tanto la bandera de símbolos bandera_simb se vuelve true
                    else if (parts[0].equals("var")) {
                        bandera_simb = true;

                        // Si no es un lexema que requiere modificación, simplemente copia la línea original
                        tabla_tokens_mod.append(linea).append("\n");
                        // Si inicia con [] se involucran las banderas de dimensiones
                    } else if (parts[0].equals("[") || parts[0].equals("]")) {
                        //System.out.println("Identifier Guardado" + idetifierSaved);
                        if (parts[0].equals("[")) {
                            bandera_corch = true;
                            //System.out.println("Abre corchete");
                            // Si no_tiene_dim es true, significa que el ID no tiene dimensiones, por lo que no debe de venir un [
                            if (no_tiene_dim == true){
                                no_tiene_dim = false;
                                JOptionPane.showMessageDialog(null, "Error: La variable «" + idetifierSaved + "» fue declarada sin dimensiones", "Error", JOptionPane.ERROR_MESSAGE);
                                System.exit(1);
                            }

                        } else if (parts[0].equals("]")) {
                            //System.out.println("Cierra corchete");
                            bandera_corch2 = false;
                            bandera_corch = false;
                        }

                        // Si no es un lexema que requiere modificación, simplemente copia la línea original
                        tabla_tokens_mod.append(linea).append("\n");

                    }

                    // Si un lexema cumple con el patrón iniciar con letra, y tener alguna otra letra o número o no,
                    // pero termina con $ (String), % (Reales), & (Enteros), va a la tabla de símbolos, ya que es un IDENTIFICADOR
                    else if ((lexema.matches("[a-zA-Z][a-zA-Z0-9]*&") || lexema.matches("[a-zA-Z][a-zA-Z0-9]*%") || lexema.matches("[a-zA-Z][a-zA-Z0-9]*\\$"))) {
                        idetifierSaved = lexema;

                        // Verifica si el token anterior es "var"
                        if (previousToken.equals("-15")) {
                            // Verifica si el ámbito actual tiene un conjunto de variables
                            if (!variablesPorAmbito.containsKey(currentAmbito)) {
                                variablesPorAmbito.put(currentAmbito, new HashSet<>());
                            }
                            HashSet<String> variables = variablesPorAmbito.get(currentAmbito);
                            // Verifica si la variable ya existe en el ámbito actual
                            if (variables.contains(lexema)) {
                                //JOptionPane.showMessageDialog(null, "Error: La variable «" + lexema + "» ya fue declarada en el ámbito «" + currentAmbito + "».", "Error", JOptionPane.ERROR_MESSAGE);
                                //System.exit(1);
                            } else {
                                // Agrega la variable al conjunto de variables del ámbito actual
                                variables.add(lexema);
                            }
                        }

                        // Determinar el valor según el tipo de lexema
                        String valor = "0";
                        if (lexema.matches("[a-zA-Z][a-zA-Z0-9]*\\$")) {
                            valor = "null";
                        }

                        // Repetido de arriba, pero si no se pone, no jala :D
                        // Verifica si el ámbito ya ha sido agregado al conjunto
                        if (!ambitos.contains(lexema)) {
                            // Agrega el ámbito al conjunto y actualiza el ámbito actual
                            ambitos.add(lexema);
                            ambito = new StringBuilder(lexema);
                        }

                        // Verificar si el lexema ya existe en la tabla_sim
                        int indexs = tabla_sim.indexOf(lexema);

                        // -1 significa que ya existe
                        // Verifica si el ámbito actual coincide con alguno otro ya declarado en el StringBuilder, para verificar que no exista ese lexema en un mismo ámbito
                        // Si viene bandera_simb en false significa que no se están declarando variables, solo hay que buscar que exista 
                        // AQUÍ VAN LOS ERRORES DE DIMENSIONES, VARIABLES YA DECLARADAS 
                        if (indexs != -1 && !ambito.toString().equals(currentAmbito) && bandera_simb == false) {
                            // Si ya existe y pertenece al mismo ámbito, busca el positionCounter_sim que le corresponde
                            int positionCounter_anterior2 = -1;
                            String[] lines2 = tabla_tokens_mod.toString().split("\\r?\\n");
                            for (String line2 : lines2) {
                                String[] tokens2 = line2.split(",");
                                // Si el lexema es igual lo almacena
                                if (tokens2[0].equals(lexema)) {
                                    // Guardamos el correspondiente valor de positionCounter_anterior, que sería el que está en el lexama anteriormente declarado
                                    positionCounter_anterior2 = Integer.parseInt(tokens2[2]);
                                    break;
                                }
                            }

                            // Asignar el valor correspondiente a positionCounter_dir
                            if (positionCounter_anterior2 != -1) {
                                positionCounter_sim = positionCounter_anterior2;
                            } else {
                                positionCounter_sim++;
                            }

                            // Buscar el identificador en la tabla_sim y obtener sus dimensiones
                            String[] lines_sim = tabla_sim.toString().split("\\r?\\n");
                            // Se inicializan las dimensiones en 0
                            dimension1 = "0";
                            dimension2 = "0";

                            for (String line_sim : lines_sim) {
                                String[] tokens_sim = line_sim.split("\t");

                                // Si existe
                                if (tokens_sim[0].equals(lexema)) {
                                    // Asigna las partes 3 y 4 de la tabla_sim como las dimensiones, ya que así está en la tabla asignadas
                                    dimension1 = tokens_sim[3];
                                    dimension2 = tokens_sim[4];
                                    break;
                                }
                            }

                            // Busca si tiene dimensiones
                            // Dependiendo de si tiene dimensiones y cuántas, se activa una bandera
                            if (!dimension1.equals("0") && !dimension2.equals("0")) {
                                //System.out.println(lexema + " DIMENSIÓN 1:" + dimension1);
                                //System.out.println(lexema + " DIMENSIÓN 2:" + dimension2);
                                no_tiene_dim = false;
                                debe_tener_1_dim = false;
                                debe_tener_2_dim = true;
                            }
                            else if (!dimension1.equals("0")) {
                                //System.out.println(lexema + " DIMENSIÓN 1:" + dimension1);
                                no_tiene_dim = false;
                                debe_tener_2_dim = false;
                                debe_tener_1_dim = true;
                            }
                            else {
                                //System.out.println(lexema + " NO TIENE DIMENSIONES");
                                debe_tener_1_dim = false;
                                debe_tener_2_dim = false;
                                no_tiene_dim = true;
                            }

                        } // Si la bandera_simb es false significa que no se están declarando variables, entonces debemos buscar en las que ya están en 
                        // tabla_sim, pero si no entra en la primera condición significa que no está declarada y manda ERROR
                        else if (bandera_simb == false) {
                            JOptionPane.showMessageDialog(null, "Error: La variable «" + lexema + "» no ha sido declarada en ningún ámbito.", "Error", JOptionPane.ERROR_MESSAGE);
                            System.exit(1);
                        }
                        // SE DECLARA NUEVA VARIABLE
                        else if (bandera_simb == true) {
                            // Verificar si el lexema ya existe en la tabla_sim
                            int indexs2 = tabla_sim.indexOf(lexema);

                            dimension1 = "0";
                            dimension2 = "0";
                            // Si la bandera bandera_simb es true, significa que se están declarando variables, hay que buscar que no exista
                            // No existe (-1 significa que NO existe) en el mismo ámbito o el ámbito no es igual a otro ya declarado
                            if (indexs2 == -1 || ambito.toString().equals(currentAmbito)) {
                                // Si no tiene dimensiones simplemente lo agrega
                                // En la tabla de símbolos se va a almacenar el ID (lexema), token, valor (0 o null), dirección 1 (0), dirección 2 (0), PTR (null) y el ámbito
                                tabla_sim.append(parts[0]).append("\t").append(parts[1]).append("\t")
                                        .append(valor).append("\t").append(dimension1).append("\t").append(dimension2).append("\t").append("null\t").append(currentAmbito).append("\n");

                                // Incrementa el contador de posición en la tabla de símbolos
                                if (parts[2].equals("-2")) {
                                    positionCounter_sim++;
                                }
                            } // Variable ya declarada anteriormente en el mismo ámbito
                            else {
                                JOptionPane.showMessageDialog(null, "Error: La variable «" + lexema + "» ya fue declarada anteriormente en el mismo ámbito «" + currentAmbito + "».", "Error", JOptionPane.ERROR_MESSAGE);
                                System.exit(1);
                            }
                        }

                        // Agregar la línea modificada a la tabla de tokens modificada
                        tabla_tokens_mod.append(parts[0]).append(",").append(parts[1]).append(",").append(positionCounter_sim).append(",").append(parts[3]).append("\n");

                    }

                    // **************************************************************
                    // MANEJO DE ERRORES DE LAS DIMENSIONES
                    // Verifica que viene un dígito y no se está declarando variable
                    else if ((lexema.matches("[a-zA-Z][a-zA-Z0-9]*&") || lexema.matches("[a-zA-Z][a-zA-Z0-9]*%") || lexema.matches("[a-zA-Z][a-zA-Z0-9]*\\$") || lexema.matches("\\d+")) && bandera_simb == false) {
                        // SE BUSCA VARIABLE CON 1 DIMENSIÓN
                        //System.out.println("LÍNEA SIGUIENTE: " + lineaSiguiente); 
                        // Obtener el siguiente lexema de la siguiente línea

                        // En caso que debe_tener_1_dim es true, significa que ese ID en la tabla de símbolos tiene 1 dimensión
                        if (debe_tener_1_dim == true){
                            //System.out.println("LÍNEA SIGUIENTE: " + lineaSiguiente); 
                            int startIndex = 0; // Índice de inicio del siguiente lexema
                            int endIndex = 0; // Índice de fin del siguiente lexema
                            StringBuilder siguienteLexema = new StringBuilder();

                            if (lineaSiguiente.length() > 0) {
                                // Verificar si el primer carácter es una coma
                                if (lineaSiguiente.charAt(0) == ',') {
                                    siguienteLexema.append(",");
                                    // Si el siguiente lexema es coma, significa que viene más de 1 dimensión, lo cuál sería un error
                                    JOptionPane.showMessageDialog(null, "Error: La variable «" + idetifierSaved + "» fue declarada con 1 dimensión", "Error", JOptionPane.ERROR_MESSAGE);
                                    System.exit(1);
                                } else {
                                    // Si el primer carácter no es una coma, se busca el primer lexema
                                    startIndex = 0;
                                    endIndex = lineaSiguiente.indexOf(","); // Buscar el índice de la primera coma
                                    if (endIndex == -1) {
                                        // Si no se encuentra ninguna coma, se asume que el siguiente lexema es la línea completa
                                        endIndex = lineaSiguiente.length();
                                    }
                                    siguienteLexema.append(lineaSiguiente.substring(startIndex, endIndex));
                                }
                            }
                            //System.out.println("LEXEMA SIGUIENTE: " + siguienteLexema); 
                            // Si bandera_corch es true significa que viene [ y debe verificar que coincida la cantidad de dimensiones
                            if (bandera_corch == true){
                                //System.out.println("LEXEMA ACTUAL: " + lexema);                                
                                // Si no es un lexema que requiere modificación, simplemente copia la línea original
                                //tabla_tokens_mod.append(linea).append("\n");
                            }
                            else{
                                //System.out.println("SI ES ERROR");  
                                JOptionPane.showMessageDialog(null, "Error: La variable «" + idetifierSaved + "» fue declarada con 1 dimensión", "Error", JOptionPane.ERROR_MESSAGE);
                                System.exit(1);
                            }

                            // Se regresa la bandera a false
                            debe_tener_1_dim = false;
                        }
                        // SE BUSCA VARIABLE CON 2 DIMENSIONES
                        else if (debe_tener_2_dim == true){
                            //System.out.println("LÍNEA SIGUIENTE: " + lineaSiguiente); 
                            int startIndex = 0; // Índice de inicio del siguiente lexema
                            int endIndex = 0; // Índice de fin del siguiente lexema
                            StringBuilder siguienteLexema = new StringBuilder();

                            if (lineaSiguiente.length() > 0) {
                                // Verificar si el primer carácter es una coma
                                if (lineaSiguiente.charAt(0) == ',') {
                                    siguienteLexema.append(",");
                                } else {
                                    // Si el primer carácter no es una coma, se busca el primer lexema
                                    startIndex = 0;
                                    endIndex = lineaSiguiente.indexOf(","); // Buscar el índice de la primera coma
                                    if (endIndex == -1) {
                                        // Si no se encuentra ninguna coma, se asume que el siguiente lexema es la línea completa
                                        endIndex = lineaSiguiente.length();
                                    }
                                    siguienteLexema.append(lineaSiguiente.substring(startIndex, endIndex));
                                    // Si el siguiente lexema no es coma, significa que solo viene declarada 1 dimensión, sería un error
                                    JOptionPane.showMessageDialog(null, "Error: La variable «" + idetifierSaved + "» fue declarada con 2 dimensiones", "Error", JOptionPane.ERROR_MESSAGE);
                                    System.exit(1);
                                }
                            }
                            //System.out.println("LEXEMA SIGUIENTE: " + siguienteLexema); 
                            // Si bandera_corch es true significa que viene [ y debe verificar que coincida la cantidad de dimensiones
                            if (bandera_corch == true){
                                //System.out.println("LEXEMA ACTUAL: " + lexema);                                
                                // Si no es un lexema que requiere modificación, simplemente copia la línea original
                                //tabla_tokens_mod.append(linea).append("\n");
                            }
                            else{
                                //System.out.println("SI ES ERROR");  
                                JOptionPane.showMessageDialog(null, "Error: La variable «" + idetifierSaved + "» fue declarada con 2 dimensiones", "Error", JOptionPane.ERROR_MESSAGE);
                                System.exit(1);
                            }
                            // Se regresa la bandera a false
                            debe_tener_2_dim = false;
                        }
                        // Si no es un lexema que requiere modificación, simplemente copia la línea original
                        tabla_tokens_mod.append(linea).append("\n");
                    }

                    // **************************************************************


                    // Cuando la bandera del corchete este abierta indica que siguen las direcciones en ser introducidas
                    // SE DECLARA VARIABLE CON 1 DIMENSIÓN
                    else if ((lexema.matches("[a-zA-Z][a-zA-Z0-9]*&") || lexema.matches("[a-zA-Z][a-zA-Z0-9]*%") || lexema.matches("[a-zA-Z][a-zA-Z0-9]*\\$") || lexema.matches("\\d+")) && bandera_corch == true && bandera_simb == true) {
                        dimension1 = lexema;
                        bandera_corch = true;

                        // Condicional para validar que el token anterior no sea una coma, al no ser coma el appen se realizara en el siguiente else if,
                        // en este if hacemos el append para dimensiones de 1, por ejemplo j&[1].
                        if (!prevIdentifier.equals(",")) {
                            // Separa la tabla en filas
                            String[] filas = tabla_sim.toString().split("\n");

                            // Busca la fila que contiene el identificador guardado
                            for (int i = 0; i < filas.length; i++) {
                                if (filas[i].contains(idetifierSaved)) {
                                    // Separa la fila en columnas
                                    String[] columnas = filas[i].split("\t");

                                    // Actualiza el valor de dimension1 en la columna correspondiente
                                    columnas[3] = dimension1;

                                    // Une las columnas en una sola cadena separada por tabuladores
                                    String fila_actualizada = String.join("\t", columnas);

                                    // Reemplaza la fila original por la fila actualizada
                                    filas[i] = fila_actualizada;

                                    break; // Deja de buscar una vez que encuentre la fila correspondiente
                                }
                            }

                            // Convertir la lista de filas en una cadena con saltos de línea
                            String tabla_sim_actualizada = String.join("\n", filas);

                            // Borrar el contenido actual de tabla_sim
                            tabla_sim.setLength(0);

                            // Agregar el contenido actualizado a tabla_sim y un salto de línea para que no amontone todo en la misma línea
                            tabla_sim.append(tabla_sim_actualizada + "\n");

                            // Imprime el resultado
                            //System.out.println(tabla_sim_actualizada);
                        }

                        // Si no es un lexema que requiere modificación, simplemente copia la línea original
                        tabla_tokens_mod.append(linea).append("\n");
                    }

                    // SE DECLARA VARIABLE CON 2 DIMENSIONES
                    else if ((lexema.matches("[a-zA-Z][a-zA-Z0-9]*&") || lexema.matches("[a-zA-Z][a-zA-Z0-9]*%") || lexema.matches("[a-zA-Z][a-zA-Z0-9]*\\$") || lexema.matches("\\d+")) && bandera_corch == false && bandera_corch2 == true && bandera_simb == true) {
                        bandera_corch2 = false;
                        dimension2 = lexema;
                        // Convierte el StringBuilder a una cadena
                        String tabla_sim_str = tabla_sim.toString();
                        int m_position = tabla_sim_str.indexOf(idetifierSaved);
                        if (m_position != -1) {
                            // Separa la tabla en filas, para acceder más facilmente a la sposiciones
                            String[] filas = tabla_sim.toString().split("\n");

                            // Busca la fila que contiene el identificador guardado
                            for (int i = 0; i < filas.length; i++) {
                                if (filas[i].contains(idetifierSaved)) {
                                    // Separa la fila en columnas
                                    String[] columnas = filas[i].split("\t");

                                    // Actualiza el valor de dimension1 en la columna correspondiente
                                    columnas[3] = dimension1;
                                    columnas[4] = dimension2;

                                    // Une las columnas en una sola cadena separada por tabuladores
                                    String fila_actualizada = String.join("\t", columnas);

                                    // Reemplaza la fila original por la fila actualizada
                                    filas[i] = fila_actualizada;

                                    break; // Deja de buscar una vez que encuentre la fila correspondiente
                                }
                            }

                            // Une las filas actualizadas en una sola cadena
                            // Convertir la lista de filas en una cadena con saltos de línea
                            String tabla_sim_actualizada = String.join("\n", filas);

                            // Borrar el contenido actual de tabla_sim
                            tabla_sim.setLength(0);

                            // Agregar el contenido actualizado a tabla_sim y un salto de línea para que no amontone todo en la misma línea
                            tabla_sim.append(tabla_sim_actualizada+ "\n");
                            // Imprime el resultado
                            //System.out.println(tabla_sim);
                        }

                        // Si no es un lexema que requiere modificación, simplemente copia la línea original
                        tabla_tokens_mod.append(linea).append("\n");
                        //tabla_sim.append(idetifierSaved).append("\t").append(parts[1]).append("\t")
                        //      .append(0).append("\t").append(dimension1).append("\t").append(dimension2).append("\t").append("null\t").append(currentAmbito).append("\n");//.append(positionCounter).append("\n");

                    }
                    // Si el lexema que viene es un ; y bandera_simb está como true (significa que se empezaron a declarar variables)
                    // entonces bandera_simb se hace false, ya que el ; indica que se terminó de declarar variables
                    else if (parts[0].equals(";") && bandera_simb == true) {
                        bandera_simb = false;

                        // Si no es un lexema que requiere modificación, simplemente copia la línea original
                        tabla_tokens_mod.append(linea).append("\n");
                    } else {
                        // Si no es un lexema que requiere modificación, simplemente copia la línea original
                        tabla_tokens_mod.append(linea).append("\n");
                    }

                }
                // Actualiza el token anterior
                previousToken = parts[1];

                //Actualiza identificador anterior
                prevIdentifier = parts[0];
                //System.out.println("ID Anterior" + prevIdentifier);
            }
            // Escribir la tabla de direcciones
            escribir_dir.write("Tabla de Direcciones: \nID\tToken\tLínea\tVCI\n");
            escribir_dir.write(tabla_dir.toString().trim());

            // Escribir la tabla de símbolos
            escribir_sim.write("Tabla de Símbolos: \nID\tToken\tValor\tD1\tD2\tPTR\tÁmbito\n");
            escribir_sim.write(tabla_sim.toString().trim());

            // Escribir la tabla de tokens modificada
            //escribir_tokens_mod.write("Tabla de Tokens Modificada: \n");
            escribir_tokens_mod.write(tabla_tokens_mod.toString().trim());

            // Guardar la ruta del archivo de tabla de tokens modificada
            // Leer archivo de tabla de tokens
            BufferedReader leerTablaTokensModificada = new BufferedReader(new FileReader(rutaGuardar + "/tabla_tokens_modificada.txt"));

            // Acabar lectura y escritura
            leer.close();
            escribir_dir.close();
            escribir_sim.close();
            escribir_tokens_mod.close();
// ****************************************************************************************************************************************************
// ****************************************************************************************************************************************************
            // SEGUNDA PARTE DEL PROYECTO
            // Leer el archivo de tabla de tokens modificada que se acaba de generar
            try {
                // Escribir archivo de VCI
                BufferedWriter escribir_VCI = new BufferedWriter(new FileWriter(rutaGuardar + "/VCI.txt"));
                // Almacena en orden los valores que corresponden a la tabla de tokens modificada
                StringBuilder VCI = new StringBuilder();
                String linea2;

                // Es para que lea la línea siguiente, y saber qué viene después
                BufferedReader leerSiguiente2 = new BufferedReader(new FileReader(archivoLeer));
                // Cada línea siguiente del archivo
                String lineaSiguiente2 = leerSiguiente2.readLine();
                // Divide en partes la línea (4 partes)
                String[] partes;

                // VARIABLES 
                // Contador de direcciones, inicia en 1 porque en esa posición empieza a escribirse el VCI
                int apuntador = 1;
                // Variable para almacenar el token anterior
                String tokenAnterior = "";
                String identificadorAnterior = "";
                // Guarda la línea cuando viene el lexema leer
                String lineaLeer = null;
                // Guarda la línea cuando viene el lexema escribir
                String lineaEscribir = null;
                // Para las prioridades de los operadores
                int prioridadOperador = 0;

                // BANDERAS
                // Bandera que indica cuando inicia el programa
                boolean iniciaVCI = false;
                // Bandera que indica comienzo de estatuto
                boolean iniciaDo = false;
                // Bandera que indica que se están leyendo variables
                boolean leyendo_var = false;
                // Bandera que indica que se están escribiendo variables
                boolean escribiendo_var = false;
                // Bandera que indica el inicio de la condición del do while
                boolean inicia_condicion = false;

                // PILAS
                // Pila de operadores
                Stack<StringBuilder> pilaOperadores = new Stack<>();
                // Pila de direcciones  
                Stack<Integer> pilaDirecciones = new Stack<>();

                // Lee línea por línea hasta que acaba el archivo
                while ((linea2 = leerTablaTokensModificada.readLine()) != null) {

                    lineaSiguiente2 = leerSiguiente2.readLine();
                    // Si la línea inicia con , este elemento es un lexema
                    if (linea2.startsWith(",")) {
                        // Por lo que la línea se divide en 5 partes
                        partes = linea2.split(",", 5);
                        // Se almacena cada elemento según corresponda
                        // El primer elemento es un lexema
                        lexemas.append(partes[0]).append(",").append(partes[1]).append("\n");
                        // El segundo elemento es el token
                        tokens.append(partes[2]).append("\n");
                        // El tercer elemento es la posición en tabla
                        posiciones.append(partes[3]).append("\n");
                        // El cuarto y último elemento es el número de línea
                        num_linea.append(partes[4]).append("\n");
                    } else {
                        // En caso que la línea no inicie con , se divide solo en 4 partes, diviendo cuando haya una ,
                        partes = linea2.split(",", 4);
                        // El primer elemento es un lexema
                        lexemas.append(partes[0]).append("\n");
                        // El segundo elemento es el token
                        tokens.append(partes[1]).append("\n");
                        // El tercer elemento es la posición en tabla
                        posiciones.append(partes[2]).append("\n");
                        // El cuarto y último elemento es el número de línea
                        num_linea.append(partes[3]).append("\n");

                        // Verificación de lexemas para la tabla de direcciones
                        String lexema = partes[0];
                        String token = partes[1];
                        String posicion = partes[2];
                        String num_line = partes[3];

                        // PROBABLEMENTE HAYA QUE CAMBIAR ESTO A PILAS PARA LOS DO ANIDADOS
                        // Comienza a leer el archivo de texto, si viene un inicio se activa una bandera
                        if (lexema.matches("inicio")) {
                            // Si el token anterior es un -9, significa que viene un estatuto repite (do)
                            if (tokenAnterior.equals("-9")){
                                iniciaDo = true;

                                // Guardo la dirección actual en la pila de direcciones
                                pilaDirecciones.push(apuntador);
                            }
                            // Si no, significa que inicia el programa
                            else {
                                iniciaVCI = true;
                            }
                        }
                        // Si viene un fin e iniciaDo es true, se cierra
                        else if (lexema.matches("fin") && iniciaDo == true) {
                            iniciaDo = false;
                            // Cuando viene fin del do, se activa una bandera que indica que inicia la condición while
                            inicia_condicion =  true;
                            //System.out.println("Es el fin del estatuto repite"); 
                        }
                        // Si viene un fin e iniciaVCI es true, se cierra y acaba el programa
                        else if (lexema.matches("fin") && iniciaVCI == true) {
                            iniciaVCI = false;
                            //System.out.println("FIN DEL PROGRAMA"); 
                        }
                        //// CAMBIAR A PILAS LO DE ARRIBA, ASÍ NO VA A FUNCIONAR

                        // Si iniciaVCI es true, comienza a guardar elementos del VCI
                        if (iniciaVCI == true){
                            // Si el lexema coinside con alguno de estos elementos, se ignora para el VCI
                            // LAS DIMENSIONES TAMBIÉN LAS IGNORA O CÓMO LAS MANEJA??????????????????????
                            if (lexema.equals("inicio") || lexema.equals("(") || lexema.equals(")") || lexema.equals(",") || lexema.equals("fin") || lexema.equals("repite")) {
                                // No hace nada, ya que en el VCI estos elementos se ignoran
                            }
                            // Si no, si se escriben en el VCI
                            // En este else vamos a empezar a escribir el VCI
                            else {
                                ////////////////////////// LEER //////////////////////////
                                // Si el lexema es leer, en el VCI escribe primero cada variable y luego la línea de leer
                                if (lexema.equals("leer")){
                                    // Se guarda la linea que dice leer
                                    lineaLeer = linea2;
                                    // La bandera de leyendo_var es true, pues significa que va a leer variables que demos por teclado
                                    leyendo_var = true;
                                }
                                // Si leyendo_var es true, significa que se están leyendo variables
                                // Además debe verificar que lo que viene si es un identificador
                                else if (leyendo_var == true && (lexema.matches("[a-zA-Z][a-zA-Z0-9]*&") || lexema.matches("[a-zA-Z][a-zA-Z0-9]*%") || lexema.matches("[a-zA-Z][a-zA-Z0-9]*\\$"))){
                                    // Se copia la línea en el VCI tal cual venía en la tabla de tokens modificada
                                    VCI.append(linea2).append("\n");
                                    // Aumenta el contador de apuntador
                                    apuntador++;

                                    // Y escribe la línea de leer que se había guardado, y esto para todas las variables
                                    VCI.append(lineaLeer).append("\n");
                                    // Aumenta el contador de apuntador
                                    apuntador++;

                                    // Pido por pantalla los valores de las variables, las guardo dependiendo del tipo que sean (entero, real, string)
                                    // Enteros
                                    if (lexema.matches("[a-zA-Z][a-zA-Z0-9]*&")){

                                    }
                                    // Reales
                                    else if (lexema.matches("[a-zA-Z][a-zA-Z0-9]*%")){

                                    }
                                    // String
                                    else if (lexema.matches("[a-zA-Z][a-zA-Z0-9]*\\$")){

                                    }
                                }
                                // Si leyendo_var es true y viene un ; indica el fin de leer
                                else if (lexema.equals(";") && leyendo_var == true){
                                    // La bandera de leyendo_var se hace false, ya que el ; indica que se dejan de leer más variables
                                    leyendo_var = false;
                                }

                                ////////////////////////// ESCRIBIR //////////////////////////
                                // Si el lexema es escribir, en el VCI escribe primero cada variable y luego la línea de escribir
                                if (lexema.equals("escribir")){
                                    // Se guarda la linea que dice escribir
                                    lineaEscribir = linea2;
                                    // La bandera de escribiendo_var es true, pues significa que va a escribir variables e imprimirlas al final
                                    escribiendo_var = true;
                                }
                                // Si escribiendo_var es true, significa que se están escribiendo variables
                                // Además debe verificar que lo que viene si es un identificador o un string
                                else if (escribiendo_var == true && (lexema.matches("[a-zA-Z][a-zA-Z0-9]*&") || lexema.matches("[a-zA-Z][a-zA-Z0-9]*%") || lexema.matches("[a-zA-Z][a-zA-Z0-9]*\\$") || lexema.matches("[a-zA-Z][a-zA-Z0-9]*"))){
                                    // Se copia la línea en el VCI tal cual venía en la tabla de tokens modificada
                                    VCI.append(linea2).append("\n");
                                    // Aumenta el contador de apuntador
                                    apuntador++;

                                    // Y escribe la línea de leer que se había guardado, y esto para todas las variables
                                    VCI.append(lineaEscribir).append("\n");
                                    // Aumenta el contador de apuntador
                                    apuntador++;

                                    // Muestro por pantalla el valor de las variables 

                                }
                                // Si escribiendo_var es true y viene un ; indica el fin de escribir
                                else if (lexema.equals(";") && escribiendo_var == true){
                                    // La bandera de escribiendo_var se hace false, ya que el ; indica que se dejan de escribir más variables
                                    escribiendo_var = false;
                                }

                                ////////////////////////// ASIGNAR //////////////////////////
                                // Si viene un identificador, número entero o real o string cualquiera se dirá que es asignación, ya que no entró en los demás
                                else if ((lexema.matches("[a-zA-Z][a-zA-Z0-9]*&") || lexema.matches("[a-zA-Z][a-zA-Z0-9]*%") || lexema.matches("[a-zA-Z][a-zA-Z0-9]*\\$") || lexema.matches("\\d+") || lexema.matches("\\d+(\\.\\d+)?") || lexema.matches("[a-zA-Z][a-zA-Z0-9]*")) && leyendo_var == false && escribiendo_var == false){
                                    // Se copia la línea en el VCI tal cual venía en la tabla de tokens modificada
                                    VCI.append(linea2).append("\n");

                                    // Aumenta el contador de apuntador
                                    apuntador++;
                                }
                                // Si viene un operador entra en la pila de operadores
                                else if (lexema.equals("*") || lexema.equals("/") || lexema.equals("%") || lexema.equals("+") || lexema.equals("-") || lexema.equals("<") || lexema.equals(">") || lexema.equals("<=") || lexema.equals(">=") || lexema.equals("==") || lexema.equals("!=") || lexema.equals("!") || lexema.equals("&&") || lexema.equals("||") || lexema.equals("=")){
                                    // Si el operador tiene mayor o igual prioridad que el que está de último en la pila, lo o los saca y los escribe en el VCI
                                    prioridadOperador = obtenerPrioridad(lexema);
                                    // Busca la prioridad del operador, extrae de la pila de operadores aquellos que tienen mayor o igual prioridad que el operador actual
                                    // Se usa el substring para que solo saque la prioridad de la primera parte de la línea, pues ese sería el lexema
                                    while (!pilaOperadores.isEmpty() && obtenerPrioridad(pilaOperadores.peek().substring(0, pilaOperadores.peek().indexOf(","))) >= prioridadOperador) {
                                        // Lo saca de la pila
                                        StringBuilder operador = pilaOperadores.pop();
                                        // Y lo guarda en el VCI
                                        VCI.append(operador).append("\n");

                                        // Aumenta el contador de apuntador
                                        apuntador++;
                                    }
                                    pilaOperadores.push(new StringBuilder(linea2)); // Agrega la línea2 actual a la pila
                                    //System.out.println("prioridad de: " + lexema + " es de: " + prioridadOperador); 
                                }
                                // Si inicia_condicion es true y viene un ; indica el fin de la condición del do while
                                else if (lexema.equals(";") && inicia_condicion == true){
                                    // Se vacía la pila de operadores también
                                    while (!pilaOperadores.isEmpty()) {
                                        StringBuilder operador = pilaOperadores.pop();
                                        VCI.append(operador).append("\n");

                                        // Aumenta el contador de apuntador
                                        apuntador++;
                                    }

                                    // La bandera de inicia_condicion se hace false, ya que el ; indica que se terminó la condición del do while
                                    inicia_condicion = false;
                                    // Se saca la última dirección de la pila
                                    int ultimaDireccion = pilaDirecciones.pop();
                                    // Y se escribe en el VCI
                                    VCI.append(ultimaDireccion).append("\n");
                                    // Aumenta el contador de apuntador
                                    apuntador++;

                                    // Se escribe la línea de fin de do while
                                    VCI.append("FIN DO WHILE").append("\n");
                                    // Aumenta el contador de apuntador
                                    apuntador++;
                                    //System.out.println("FIN CONDICIÓN"); 
                                }
                                // Cuando venga un ; se vacía la pila de operadores y los escribe en el VCI
                                else if (lexema.equals(";")){
                                    while (!pilaOperadores.isEmpty()) {
                                        StringBuilder operador = pilaOperadores.pop();
                                        VCI.append(operador).append("\n");

                                        // Aumenta el contador de apuntador
                                        apuntador++;
                                    }
                                }

                                // Else provicional para pruebas, hay que quitarlo CREO
                                else {

                                }
                            }
                        }
                        // Si no es true, ignora las líneas, de manera que ignora el principio del programa y la declaración de variables
                        else {
                            // No hace nada
                        }
                    }
                    // Actualiza el token anterior
                    tokenAnterior = partes[1];

                    //Actualiza identificador anterior
                    identificadorAnterior = partes[0];
                    //System.out.println("ID Anterior" + prevIdentifier);
                }
                System.out.println("Apuntador en posición: " + apuntador);

                // Escribir VCI
                escribir_VCI.write("VCI: \n");
                escribir_VCI.write(VCI.toString().trim());
                // Acabar lectura y escritura
                leerTablaTokensModificada.close();
                escribir_VCI.close();

            } catch (IOException e) {
                // Manejar la excepción en caso de error de lectura
                System.err.println("Error: " + e.getMessage());
            }
            // Agregar un cuadro de diálogo que muestre un mensaje de éxito
            JOptionPane.showMessageDialog(null, "Proceso completado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            // Mostrar error
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Obtener las prioridades de los operadores
    private static int obtenerPrioridad(String operador) {
        switch (operador) {
            case "*":
            case "/":
            case "%":
                return 60;
            case "+":
            case "-":
                return 50;
            case "<":
            case ">":
            case "<=":
            case ">=":
            case "==":
            case "!=":
                return 40;
            case "!":
                return 30;
            case "&&":
                return 20;
            case "||":
                return 10;
            case "=":
                return 0;
            default:
                return -1; // Valor por defecto para operadores no reconocidos
        }
    }
}