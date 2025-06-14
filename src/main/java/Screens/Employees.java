package Screens;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Components.TableStyler;

public class Employees extends JPanel {
    private JTextField searchField;

    public void clearSearchField() {
        searchField.setText("");
    }

    public Employees(JFrame parentFrame) {
        setLayout(new BorderLayout());

        // Search bar
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());
        searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchField.setPreferredSize(null);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Table data
        String[] columnNames = { "Name", "ID", "Department", "Employment Status" };
        Object[][] data = {
                { "Aela Cruz, Juan C.", 1, "Sales", "Regular" },
                { "Bela Cruz, Juan C.", 23, "Sales", "Regular" },
                { "Cela Cruz, Juan C.", 31, "Production(Pre-Press)", "Regular" },
                { "Dela Cruz, Juan C.", 14, "Production(Pre-Press)", "Regular" },
                { "Eela Cruz, Juan C.", 25, "Production(Pre-Press)", "Regular" },
                { "Fela Cruz, Juan C.", 36, "Production (Press)", "Regular" },
                { "Gela Cruz, Juan C.", 15, "Production(Pre-Press)", "Regular" },
                { "Hela Cruz, Juan C.", 4, "Production(Pre-Press)", "Regular" },
                { "Hela Cruz, Juan C.", 8, "Production (Post-Press)", "Regular" },
                { "Iela Cruz, Juan C.", 10, "Production (Post-Press)", "Regular" },
                { "Jela Cruz, Juan C.", 17, "Production (Quality Control)", "Regular" },
                { "Jela Cruz, Juan C.", 22, "Production (Quality Control)", "Regular" },
                { "Kela Cruz, Juan C.", 11, "Sales", "Regular" },
                { "Lela Cruz, Juan C.", 12, "Sales", "Regular" },
                { "Mela Cruz, Juan C.", 13, "Sales", "Regular" },
                { "Nela Cruz, Juan C.", 16, "Production (Press)", "Regular" },
                { "Oela Cruz, Juan C.", 18, "Production (Press)", "Regular" },
                { "Pela Cruz, Juan C.", 19, "Production (Post-Press)", "Regular" },
                { "Qela Cruz, Juan C.", 20, "Production (Post-Press)", "Regular" },
                { "Rela Cruz, Juan C.", 21, "Production (Quality Control)", "Regular" },
                { "Sela Cruz, Juan C.", 24, "Production (Quality Control)", "Regular" },
                { "Tela Cruz, Juan C.", 26, "Sales", "Regular" },
                { "Uela Cruz, Juan C.", 27, "Sales", "Regular" },
                { "Vela Cruz, Juan C.", 28, "Sales", "Regular" },
                { "Wela Cruz, Juan C.", 29, "Production (Press)", "Regular" },
                { "Xela Cruz, Juan C.", 30, "Production (Press)", "Regular" },
                { "Yela Cruz, Juan C.", 32, "Production (Post-Press)", "Regular" },
                { "Zela Cruz, Juan C.", 33, "Production (Post-Press)", "Regular" },
                { "Aela Cruz, Juan C.", 34, "Production (Quality Control)", "Regular" },
                { "Bela Cruz, Juan C.", 35, "Production (Quality Control)", "Regular" },
                { "Cela Cruz, Juan C.", 37, "Sales", "Regular" },
                { "Dela Cruz, Juan C.", 38, "Sales", "Regular" },
                { "Eela Cruz, Juan C.", 39, "Sales", "Regular" },
                { "Fela Cruz, Juan C.", 40, "Production (Press)", "Regular" },
                { "Gela Cruz, Juan C.", 41, "Production (Press)", "Regular" },
                { "Hela Cruz, Juan C.", 42, "Production (Post-Press)", "Regular" },
                { "Iela Cruz, Juan C.", 43, "Production (Post-Press)", "Regular" },
                { "Jela Cruz, Juan C.", 44, "Production (Quality Control)", "Regular" },
                { "Kela Cruz, Juan C.", 45, "Production (Quality Control)", "Regular" },
                { "Lela Cruz, Juan C.", 46, "Sales", "Regular" },
                { "Mela Cruz, Juan C.", 47, "Sales", "Regular" },
                { "Nela Cruz, Juan C.", 48, "Sales", "Regular" },
                { "Oela Cruz, Juan C.", 49, "Production (Press)", "Regular" },
                { "Pela Cruz, Juan C.", 50, "Production (Press)", "Regular" },
                { "Qela Cruz, Juan C.", 51, "Production (Post-Press)", "Regular" },
                { "Rela Cruz, Juan C.", 52, "Production (Post-Press)", "Regular" },
                { "Sela Cruz, Juan C.", 53, "Production (Quality Control)", "Regular" },
                { "Tela Cruz, Juan C.", 54, "Production (Quality Control)", "Regular" },
                { "Uela Cruz, Juan C.", 55, "Sales", "Regular" },
                { "Vela Cruz, Juan C.", 56, "Sales", "Regular" },
                { "Wela Cruz, Juan C.", 57, "Sales", "Regular" },
                { "Xela Cruz, Juan C.", 58, "Production (Press)", "Regular" },
                { "Yela Cruz, Juan C.", 59, "Production (Press)", "Regular" },
                { "Zela Cruz, Juan C.", 60, "Production (Post-Press)", "Regular" },
                { "Aela Cruz, Juan C.", 61, "Production (Post-Press)", "Regular" },
                { "Bela Cruz, Juan C.", 62, "Production (Quality Control)", "Regular" },
                { "Cela Cruz, Juan C.", 63, "Production (Quality Control)", "Regular" },
                { "Dela Cruz, Juan C.", 64, "Sales", "Regular" },
                { "Eela Cruz, Juan C.", 65, "Sales", "Regular" },
                { "Fela Cruz, Juan C.", 66, "Sales", "Regular" },
                { "Gela Cruz, Juan C.", 67, "Production (Press)", "Regular" },
                { "Hela Cruz, Juan C.", 68, "Production (Press)", "Regular" },
                { "Iela Cruz, Juan C.", 69, "Production (Post-Press)", "Regular" },
                { "Jela Cruz, Juan C.", 70, "Production (Post-Press)", "Regular" }
        };
        JTable table = new JTable(new DefaultTableModel(data, columnNames));
        TableStyler.styleTable(table);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Set table header background to green and foreground to white
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0, 128, 0)); // green
        header.setForeground(Color.WHITE);

        // Remove the up/down arrow buttons from the vertical scrollbar
        tableScrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                button.setVisible(false);
                return button;
            }

            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(34, 177, 76); // green
                this.trackColor = new Color(220, 255, 220); // light green track
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(34, 177, 76)); // green
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
                g2.dispose();
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(220, 255, 220)); // light green
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                g2.dispose();
            }
        });

        // Font
        Font font = new Font("Arial", Font.PLAIN, 16);
        table.setFont(font);
        table.setRowHeight(20);
        table.getTableHeader().setFont(font);
        searchField.setFont(font);
        searchButton.setFont(font);

        // CardLayout for switching views
        JPanel contentPanel = new JPanel(new CardLayout());

        // Table view panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Employee details view panel
        JPanel detailsPanel = new JPanel(new BorderLayout());

        JPanel topButtonPanel = new JPanel(new BorderLayout());
        JPanel saveButtonPanel = new JPanel(new FlowLayout());

        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 30));
        JButton editButton = new JButton("Edit");
        editButton.setPreferredSize(new Dimension(100, 30));
        JButton saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(100, 30));

        topButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topButtonPanel.add(backButton, BorderLayout.WEST);
        topButtonPanel.add(editButton, BorderLayout.EAST);

        saveButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        saveButtonPanel.add(saveButton);
        saveButtonPanel.setPreferredSize(new Dimension(0, 50));
        saveButton.setVisible(false);

        detailsPanel.add(topButtonPanel, BorderLayout.NORTH);
        detailsPanel.add(saveButtonPanel, BorderLayout.SOUTH);

        JPanel combinedDetailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Details fields
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        nameField.setEditable(false);

        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField();
        idField.setEditable(false);

        JLabel departmentLabel = new JLabel("Department:");
        JTextField departmentField = new JTextField();
        departmentField.setEditable(false);

        JLabel employmentStatusLabel = new JLabel("Employment Status:");
        JTextField employmentStatusField = new JTextField();
        employmentStatusField.setEditable(false);

        JLabel rateLabel = new JLabel("Rate / Hour:");
        JTextField rateField = new JTextField("₱ 610.00");
        rateField.setEditable(false);

        JLabel tinLabel = new JLabel("TIN No.:");
        JTextField tinField = new JTextField("000 – 123 – 456 – 001");
        tinField.setEditable(false);

        JLabel pagibigLabel = new JLabel("Pag-Ibig No.:");
        JTextField pagibigField = new JTextField("1234 – 5678 – 9101");
        pagibigField.setEditable(false);

        JLabel sssLabel = new JLabel("SSS No.:");
        JTextField sssField = new JTextField("02 – 1234567 – 9");
        sssField.setEditable(false);

        JLabel philhealthLabel = new JLabel("PhilHealth No.:");
        JTextField philhealthField = new JTextField("02 – 385929672 – 8");
        philhealthField.setEditable(false);

        // Add fields to panel
        gbc.gridx = 0; gbc.gridy = 0;
        combinedDetailsPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        combinedDetailsPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        combinedDetailsPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        combinedDetailsPanel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        combinedDetailsPanel.add(departmentLabel, gbc);
        gbc.gridx = 1;
        combinedDetailsPanel.add(departmentField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        combinedDetailsPanel.add(employmentStatusLabel, gbc);
        gbc.gridx = 1;
        combinedDetailsPanel.add(employmentStatusField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        combinedDetailsPanel.add(rateLabel, gbc);
        gbc.gridx = 1;
        combinedDetailsPanel.add(rateField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        combinedDetailsPanel.add(tinLabel, gbc);
        gbc.gridx = 1;
        combinedDetailsPanel.add(tinField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        combinedDetailsPanel.add(pagibigLabel, gbc);
        gbc.gridx = 1;
        combinedDetailsPanel.add(pagibigField, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        combinedDetailsPanel.add(sssLabel, gbc);
        gbc.gridx = 1;
        combinedDetailsPanel.add(sssField, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        combinedDetailsPanel.add(philhealthLabel, gbc);
        gbc.gridx = 1;
        combinedDetailsPanel.add(philhealthField, gbc);

        detailsPanel.add(combinedDetailsPanel, BorderLayout.CENTER);

        contentPanel.add(tablePanel, "TableView");
        contentPanel.add(detailsPanel, "DetailsView");

        setLayout(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);

        // Store default border and insets for restoration and spacing
        Border defaultBorder = nameField.getBorder();
        Insets defaultInsets = defaultBorder.getBorderInsets(nameField);

        // Helper to set all fields to "plain text" look but keep spacing
        Runnable setPlainTextLook = () -> {
            Border emptyBorder = BorderFactory.createEmptyBorder(
                defaultInsets.top, defaultInsets.left, defaultInsets.bottom, defaultInsets.right
            );
            nameField.setBorder(emptyBorder);
            idField.setBorder(emptyBorder);
            departmentField.setBorder(emptyBorder);
            employmentStatusField.setBorder(emptyBorder);
            rateField.setBorder(emptyBorder);
            tinField.setBorder(emptyBorder);
            pagibigField.setBorder(emptyBorder);
            sssField.setBorder(emptyBorder);
            philhealthField.setBorder(emptyBorder);
            Color bg = combinedDetailsPanel.getBackground();
            nameField.setBackground(bg);
            idField.setBackground(bg);
            departmentField.setBackground(bg);
            employmentStatusField.setBackground(bg);
            rateField.setBackground(bg);
            tinField.setBackground(bg);
            pagibigField.setBackground(bg);
            sssField.setBackground(bg);
            philhealthField.setBackground(bg);
        };

        // Helper to restore default borders
        Runnable setEditableLook = () -> {
            nameField.setBorder(defaultBorder);
            idField.setBorder(defaultBorder);
            departmentField.setBorder(defaultBorder);
            employmentStatusField.setBorder(defaultBorder);
            rateField.setBorder(defaultBorder);
            tinField.setBorder(defaultBorder);
            pagibigField.setBorder(defaultBorder);
            sssField.setBorder(defaultBorder);
            philhealthField.setBorder(defaultBorder);
            Color bg = Color.WHITE;
            nameField.setBackground(bg);
            idField.setBackground(bg);
            departmentField.setBackground(bg);
            employmentStatusField.setBackground(bg);
            rateField.setBackground(bg);
            tinField.setBackground(bg);
            pagibigField.setBackground(bg);
            sssField.setBackground(bg);
            philhealthField.setBackground(bg);
        };

        // Set plain text look initially
        setPlainTextLook.run();

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    searchField.setText("");

                    nameField.setText(table.getValueAt(row, 0).toString());
                    idField.setText(table.getValueAt(row, 1).toString());
                    departmentField.setText(table.getValueAt(row, 2).toString());
                    employmentStatusField.setText(table.getValueAt(row, 3).toString());

                    nameField.setEditable(false);
                    idField.setEditable(false);
                    departmentField.setEditable(false);
                    employmentStatusField.setEditable(false);
                    rateField.setEditable(false);
                    tinField.setEditable(false);
                    pagibigField.setEditable(false);
                    sssField.setEditable(false);
                    philhealthField.setEditable(false);

                    setPlainTextLook.run();

                    rateField.setText("₱ 610.00");
                    tinField.setText("000 – 123 – 456 – 001");
                    pagibigField.setText("1234 – 5678 – 9101");
                    sssField.setText("02 – 1234567 – 9");
                    philhealthField.setText("02 – 385929672 – 8");

                    searchPanel.setVisible(false);
                    CardLayout cl = (CardLayout) (contentPanel.getLayout());
                    cl.show(contentPanel, "DetailsView");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) (contentPanel.getLayout());
                cl.show(contentPanel, "TableView");
                searchPanel.setVisible(true);
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameField.setEditable(true);
                idField.setEditable(true);
                departmentField.setEditable(true);
                employmentStatusField.setEditable(true);
                rateField.setEditable(true);
                tinField.setEditable(true);
                pagibigField.setEditable(true);
                sssField.setEditable(true);
                philhealthField.setEditable(true);

                setEditableLook.run();

                saveButton.setVisible(true);
                editButton.setVisible(false);
                topButtonPanel.revalidate();
                topButtonPanel.repaint();
                saveButtonPanel.revalidate();
                saveButtonPanel.repaint();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameField.setEditable(false);
                idField.setEditable(false);
                departmentField.setEditable(false);
                employmentStatusField.setEditable(false);
                rateField.setEditable(false);
                tinField.setEditable(false);
                pagibigField.setEditable(false);
                sssField.setEditable(false);
                philhealthField.setEditable(false);

                setPlainTextLook.run();

                saveButton.setVisible(false);
                editButton.setVisible(true);
                topButtonPanel.revalidate();
                topButtonPanel.repaint();
                saveButtonPanel.revalidate();
                saveButtonPanel.repaint();
            }
        });

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(model);

        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != -1) {
                    table.setRowSelectionInterval(row, row);
                } else {
                    table.clearSelection();
                }
            }
        });

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            private void filterTable() {
                String searchText = searchField.getText().toLowerCase();
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);
                if (searchText.isEmpty()) {
                    for (Object[] row : data) {
                        model.addRow(row);
                    }
                } else {
                    for (Object[] row : data) {
                        boolean matchFound = false;
                        for (Object cell : row) {
                            if (cell != null && cell.toString().toLowerCase().contains(searchText)) {
                                matchFound = true;
                                break;
                            }
                        }
                        if (matchFound) {
                            model.addRow(row);
                        }
                    }
                }
            }
        });

        Font detailsFont = new Font("Arial", Font.PLAIN, 16);
        nameLabel.setFont(detailsFont);
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        nameField.setFont(detailsFont);

        idLabel.setFont(detailsFont);
        idLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        idField.setFont(detailsFont);

        departmentLabel.setFont(detailsFont);
        departmentLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        departmentField.setFont(detailsFont);

        employmentStatusLabel.setFont(detailsFont);
        employmentStatusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        employmentStatusField.setFont(detailsFont);

        rateLabel.setFont(detailsFont);
        rateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        rateField.setFont(detailsFont);

        tinLabel.setFont(detailsFont);
        tinLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        tinField.setFont(detailsFont);

        pagibigLabel.setFont(detailsFont);
        pagibigLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        pagibigField.setFont(detailsFont);

        sssLabel.setFont(detailsFont);
        sssLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        sssField.setFont(detailsFont);

        philhealthLabel.setFont(detailsFont);
        philhealthLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        philhealthField.setFont(detailsFont);
    }
}