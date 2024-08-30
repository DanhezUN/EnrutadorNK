package com.unal;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class EnrutadorNK extends JFrame {
    private File file = null;
    private Graph routes = new Graph();


    private JButton selectfile_btn = new JButton("Seleccionar archivo");
    private JTextField origin_tf = new JTextField();
    private JTextField destiny_tf = new JTextField();
    private JButton search_btn = new JButton("Buscar Ruta");
    private JLabel currentfile_lb = new JLabel("Archivo:");
    private JLabel origin_lbl = new JLabel("Origen   :");
    private JLabel destiny_lbl = new JLabel("Destino :");
    private JPanel control_panel = new JPanel(new GridBagLayout());
    private JPanel route_panel = new JPanel();
    private JScrollPane scroll_panel = new JScrollPane(route_panel);

    public EnrutadorNK() {
        setTitle("EnrutadorNK");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1,2, 10, 1));

        route_panel.setLayout(new BoxLayout(route_panel, BoxLayout.Y_AXIS));

        add(scroll_panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.weightx = 1.0;
        control_panel.add(selectfile_btn, gbc);
        selectfile_btn.addActionListener(e -> {
            selectFile();
        });

        gbc.gridy = 1;
        gbc.insets = new Insets(1, 0, 70, 0);
        control_panel.add(currentfile_lb, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(0, 0, 0, 5);
        control_panel.add(origin_lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 10);
        control_panel.add(origin_tf, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(0, 0, 0, 5);
        control_panel.add(destiny_lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 10);
        control_panel.add(destiny_tf, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(80, 0, 0, 10);
        control_panel.add(search_btn, gbc);
        search_btn.addActionListener(e -> {
            searchRoute();
        });


        add(control_panel);


        setVisible(true);
    }

    public void selectFile(){
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            file = chooser.getSelectedFile();
            routes.readFile(file);
            currentfile_lb.setText("Archivo: " + file.getName());
            origin_tf.setText("");
            destiny_tf.setText("");
            route_panel.removeAll();
            route_panel.repaint();
        }
    }

    public void searchRoute(){
        route_panel.removeAll();
        route_panel.repaint();
        if (!routes.nodes.containsKey(origin_tf.getText()) && !origin_tf.getText().isEmpty()){
            route_panel.add(new NKLabel("El archivo no contiene la parada " + origin_tf.getText()));
            this.revalidate();
            return;
        } else if (!routes.nodes.containsKey(destiny_tf.getText()) && !destiny_tf.getText().isEmpty()){
            route_panel.add(new NKLabel("El archivo no contiene la parada " + destiny_tf.getText()));
            this.revalidate();
            return;
        }
        if (origin_tf.getText().isEmpty() || destiny_tf.getText().isEmpty()) {
            route_panel.add(new NKLabel("Ingrese el nombre de parada a buscar"));
            this.revalidate();
            return;
        }
        List<Graph.Route> solution = routes.shortestPath(origin_tf.getText(), destiny_tf.getText());

        if (!solution.isEmpty()) {
            for (int i = 0; i < solution.size(); i++){
                if (i == 0){
                    route_panel.add(new NKLabel(String.format("Comienza en %s", solution.get(i).start_node)));
                    route_panel.add(new NKLabel(String.format("Tome la Ruta %d", solution.get(i).route_id)));
                    continue;
                }else{
                    if (solution.get(i).route_id != solution.get(i-1).route_id){
                        route_panel.add(new NKLabel(String.format("Hasta %s", solution.get(i).start_node)));
                    }

                }
                if(solution.get(i).route_id != solution.get(i-1).route_id && i >= 1){
                    route_panel.add(new NKLabel(String.format("Tome la Ruta %d", solution.get(i).route_id)));
                }
            }
            route_panel.add(new NKLabel("Hasta su Destino: " + solution.getLast().end_node));
        } else {
            route_panel.add(new NKLabel(String.format("No se encontro ninguna ruta desde %S hasta %s", origin_tf.getText(), destiny_tf.getText())));
        }
        this.revalidate();
    }

    private class NKLabel extends JLabel{
        NKLabel(String label){
            setText(label);
            setFont(new Font("Serif", Font.PLAIN, 22));
        }
    }

}