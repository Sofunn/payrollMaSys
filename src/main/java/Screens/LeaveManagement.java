package Screens;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import Components.RoundedButton;
import Components.RoundedComboBox;
import Components.RoundedTextField;
import Components.TableStyler;
import Module.E201File.E201File;
import org.payroll.MainWindow;

public class LeaveManagement extends JPanel {
    private JTextField searchField;

    private static DefaultTableModel employeeTableModel;
    private static String[] columnHeaders = { "Name", "ID", "Department", "Employment Status" };
    private static String[] tableViewHeaders = { "Name", "ID", "Department", "Employment Status" };
    private JTable table;
    private static Object[][] tableViewData;
    private static Object[][] detailsViewData;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private Map<Integer, Integer> filteredToOriginalIndex = new HashMap<>();
    public static void loadEmployeeTabledata() {
        Object[][] rawData = E201File.getEmployeeTableData(); // Query your source

        // Map data for table view
        tableViewData = new Object[rawData.length][tableViewHeaders.length];
        for (int i = 0; i < rawData.length; i++) {
            tableViewData[i][0] = rawData[i][1] + ", " + rawData[i][2]; // Combine last name and first name
            tableViewData[i][1] = rawData[i][0]; // ID
            tableViewData[i][2] = rawData[i][4]; // Department
            tableViewData[i][3] = rawData[i][5]; // Employment Status
        }

        // Map data for details view
        detailsViewData = rawData; // Use rawData directly for details view
        employeeTableModel.setDataVector(tableViewData, tableViewHeaders);
    }

    private void initializeFilteredIndices() {
        filteredToOriginalIndex.clear();
        // Map all original indices when table is first loaded
        for (int i = 0; i < tableViewData.length; i++) {
            filteredToOriginalIndex.put(i, i);
        }
    }
    public void clearSearchField() {
        searchField.setText("");
    }

    public LeaveManagement() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        getCursor();

        // CardLayout for switching views
        CardLayout cardLayout = new CardLayout();
        JPanel contentPanel = new JPanel(cardLayout);

        // --- Table View ---
        // Search bar
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout(10, 0));
        searchPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        searchPanel.setOpaque(false);
        searchPanel.setPreferredSize(new Dimension(0, 70));
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        Font font = new Font("Arial", Font.PLAIN, 16);
        searchField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().length() == 0) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(Color.GRAY);
                    g2.setFont(getFont().deriveFont(Font.PLAIN));
                    FontMetrics fm = g2.getFontMetrics();
                    int padding = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString("Search", 5, padding);
                    g2.dispose();
                }
            }
        };
        searchField.setFont(font);
        searchField.setBorder(null);
        searchField.setPreferredSize(null);

        JButton searchButton = new JButton("Search");
        searchButton.setFont(font);
        searchButton.setPreferredSize(new Dimension(150, 70));
        searchButton.setBackground(Color.WHITE);
        searchButton.setBorder(null);
        searchButton.setFocusable(false);

        searchPanel.add(searchField, BorderLayout.CENTER);
        //searchPanel.add(searchButton, BorderLayout.WEST);

        RoundedComboBox<String> sortCombo = new RoundedComboBox<>(new String[] {
                "All Departments", "Human Resource", "Administration", "Accounting", "Sales",  "Production", "Production (Pre-Press)", "Production (Press)", "Production (Post-Press)", "Production (Quality Control)"
        }) {
            @Override
            protected void paintBorder(Graphics g) {
                // Do nothing: no border for this instance
            }
        };
        sortCombo.setFont(new Font("Arial", Font.PLAIN, 18));
        sortCombo.setPreferredSize(new Dimension(250, 36));
        sortCombo.setBackground(Color.WHITE);
        sortCombo.setFocusable(false);
        sortCombo.setMaximumRowCount(12);
        ((JLabel)sortCombo.getRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        // Custom renderer for hover effect in dropdown list
        sortCombo.setRenderer(new DefaultListCellRenderer() {
            private int hoveredIndex = -1;
            {
                // Add mouse motion listener to popup list for hover effect
                sortCombo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
                    @Override
                    public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                        JList<?> list = getPopupList();
                        if (list != null) {
                            list.addMouseMotionListener(new MouseMotionAdapter() {
                                @Override
                                public void mouseMoved(MouseEvent e) {
                                    hoveredIndex = list.locationToIndex(e.getPoint());
                                    list.repaint();
                                }
                            });
                            list.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseExited(MouseEvent e) {
                                    hoveredIndex = -1;
                                    list.repaint();
                                }
                            });
                        }
                    }
                    @Override public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
                    @Override public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
                    private JList<?> getPopupList() {
                        ComboPopup popup = (ComboPopup) sortCombo.getUI().getAccessibleChild(sortCombo, 0);
                        return popup != null ? popup.getList() : null;
                    }
                });
            }
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (index >= 0 && index == hoveredIndex && !isSelected) {
                    c.setBackground(new Color(230, 255, 230)); // light greenish
                }
                return c;
            }
        });

        sortCombo.addActionListener(e -> {
            String selected = (String) sortCombo.getSelectedItem();
            TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(table.getModel());
            table.setRowSorter(rowSorter);
            if (selected != null && selected.equals("All Departments")) {
                rowSorter.setRowFilter(null);
            } else if (selected != null) {
                rowSorter.setRowFilter(RowFilter.regexFilter("^" + Pattern.quote(selected) + "$", 2));
            }
        });

        // Add hover effect (like LeaveManagement)
        Color comboDefaultBg = Color.WHITE;
        Color comboHoverBg = new Color(230, 255, 230); // light greenish
        sortCombo.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (sortCombo.isEnabled()) {
                    sortCombo.setBackground(comboHoverBg);
                }
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                sortCombo.setBackground(comboDefaultBg);
            }
        });

        // Use GridBagLayout for vertical centering
        JPanel comboPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcCombo = new GridBagConstraints();
        gbcCombo.gridx = 0;
        gbcCombo.gridy = 0;
        gbcCombo.anchor = GridBagConstraints.CENTER;
        JLabel sortLabel = new JLabel("Sort by Department: ");
        sortLabel.setFont(new Font("Arial", Font.BOLD, 16));
        sortLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // Add left padding
        comboPanel.add(sortLabel, gbcCombo);

        gbcCombo.gridx = 1;
        comboPanel.add(sortCombo, gbcCombo);
        comboPanel.setOpaque(true);
        comboPanel.setBackground(Color.WHITE);

        searchPanel.setOpaque(false);
        searchPanel.add(comboPanel, BorderLayout.EAST);

        Object[][] data = E201File.getEmployeeTableData();
        employeeTableModel = new DefaultTableModel(tableViewData, tableViewHeaders) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(employeeTableModel);
        loadEmployeeTabledata();
// Then initialize filtered indices
        initializeFilteredIndices();

        table.getTableHeader().setReorderingAllowed(false);
        TableStyler.styleTable(table);
        JScrollPane tableScrollPane = new JScrollPane(table);


        table.setFont(font);
        table.getTableHeader().setFont(font);

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

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // --- Details View ---
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(new Color(34, 177, 76)); // green background

        // Container panel to group and center the fields
        JPanel groupPanel = new JPanel(null); // We'll use absolute layout for the group, but center the group itself
        groupPanel.setOpaque(false);

        Font detailsFont = new Font("Arial", Font.PLAIN, 18);

        RoundedTextField nameField = new RoundedTextField(10);
        nameField.setMargin(new java.awt.Insets(0, 5, 0, 0));
        RoundedTextField idField = new RoundedTextField(10);
        idField.setMargin(new java.awt.Insets(0, 5, 0, 0));
        RoundedTextField departmentField = new RoundedTextField(10);
        departmentField.setMargin(new java.awt.Insets(0, 5, 0, 0));
        RoundedTextField employmentStatusField = new RoundedTextField(10);
        employmentStatusField.setMargin(new java.awt.Insets(0, 5, 0, 0));
        RoundedTextField leavesUsedField = new RoundedTextField(10);
        leavesUsedField.setHorizontalAlignment(JTextField.CENTER);
        RoundedTextField remainingSILField = new RoundedTextField(10);
        remainingSILField.setHorizontalAlignment(JTextField.CENTER);
        RoundedButton dateField1 = new RoundedButton ("Input Date", 20);
        RoundedButton dateField2 = new RoundedButton ("Input Date", 20);
        RoundedButton dateField3 = new RoundedButton ("Input Date", 20);
        RoundedButton dateField4 = new RoundedButton ("Input Date", 20);
        RoundedButton dateField5 = new RoundedButton ("Input Date", 20);

        ImageIcon calendarIcon = new ImageIcon(
                new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("Black Calendar.png")))
                        .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)
        );

        dateField1.setIcon(calendarIcon);
        dateField1.setHorizontalTextPosition(SwingConstants.LEFT); // Text left, icon right
        dateField1.setIconTextGap(12);

        dateField2.setIcon(calendarIcon);
        dateField2.setHorizontalTextPosition(SwingConstants.LEFT); // Text left, icon right
        dateField2.setIconTextGap(12);

        dateField3.setIcon(calendarIcon);
        dateField3.setHorizontalTextPosition(SwingConstants.LEFT); // Text left, icon right
        dateField3.setIconTextGap(12);

        dateField4.setIcon(calendarIcon);
        dateField4.setHorizontalTextPosition(SwingConstants.LEFT); // Text left, icon right
        dateField4.setIconTextGap(12);

        dateField5.setIcon(calendarIcon);
        dateField5.setHorizontalTextPosition(SwingConstants.LEFT); // Text left, icon right
        dateField5.setIconTextGap(12);

        final java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy");
        final java.util.Date[] field1Date = {null};
        final java.util.Date[] field2Date = {null};
        final java.util.Date[] field3Date = {null};
        final java.util.Date[] field4Date = {null};
        final java.util.Date[] field5Date = {null};

        dateField1.addActionListener(e -> {
            org.jdatepicker.impl.UtilDateModel model = new org.jdatepicker.impl.UtilDateModel();
            java.util.Properties p = new java.util.Properties();
            org.jdatepicker.impl.JDatePanelImpl datePanel = new org.jdatepicker.impl.JDatePanelImpl(model, p);
            org.jdatepicker.impl.JDatePickerImpl picker = new org.jdatepicker.impl.JDatePickerImpl(datePanel, new org.jdatepicker.impl.DateComponentFormatter());

            // Set max selectable date to today using Calendar
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int year = cal.get(java.util.Calendar.YEAR);
            int month = cal.get(java.util.Calendar.MONTH);
            int day = cal.get(java.util.Calendar.DAY_OF_MONTH);
            datePanel.getModel().setSelected(true);
            datePanel.getModel().setDate(year, month, day);
            datePanel.getModel().setSelected(false);

            java.util.Date today = cal.getTime();

            int result = JOptionPane.showConfirmDialog(null, picker, "Select Date", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                java.util.Date selectedDate = (java.util.Date) picker.getModel().getValue();
                if (selectedDate != null) {
                    if (selectedDate.after(today)) {
                        JOptionPane.showMessageDialog(null, "Start date cannot be in the future.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    field1Date[0] = selectedDate;
                    dateField1.setText(sdf.format(selectedDate));
                }
            }
        });
        dateField2.addActionListener(e -> {
            org.jdatepicker.impl.UtilDateModel model = new org.jdatepicker.impl.UtilDateModel();
            java.util.Properties p = new java.util.Properties();
            org.jdatepicker.impl.JDatePanelImpl datePanel = new org.jdatepicker.impl.JDatePanelImpl(model, p);
            org.jdatepicker.impl.JDatePickerImpl picker = new org.jdatepicker.impl.JDatePickerImpl(datePanel, new org.jdatepicker.impl.DateComponentFormatter());

            // Set max selectable date to today using Calendar
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int year = cal.get(java.util.Calendar.YEAR);
            int month = cal.get(java.util.Calendar.MONTH);
            int day = cal.get(java.util.Calendar.DAY_OF_MONTH);
            datePanel.getModel().setSelected(true);
            datePanel.getModel().setDate(year, month, day);
            datePanel.getModel().setSelected(false);

            java.util.Date today = cal.getTime();

            int result = JOptionPane.showConfirmDialog(null, picker, "Select Date", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                java.util.Date selectedDate = (java.util.Date) picker.getModel().getValue();
                if (selectedDate != null) {
                    if (selectedDate.after(today)) {
                        JOptionPane.showMessageDialog(null, "Start date cannot be in the future.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    field2Date[0] = selectedDate;
                    dateField2.setText(sdf.format(selectedDate));
                }
            }
        });
        dateField3.addActionListener(e -> {
            org.jdatepicker.impl.UtilDateModel model = new org.jdatepicker.impl.UtilDateModel();
            java.util.Properties p = new java.util.Properties();
            org.jdatepicker.impl.JDatePanelImpl datePanel = new org.jdatepicker.impl.JDatePanelImpl(model, p);
            org.jdatepicker.impl.JDatePickerImpl picker = new org.jdatepicker.impl.JDatePickerImpl(datePanel, new org.jdatepicker.impl.DateComponentFormatter());

            // Set max selectable date to today using Calendar
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int year = cal.get(java.util.Calendar.YEAR);
            int month = cal.get(java.util.Calendar.MONTH);
            int day = cal.get(java.util.Calendar.DAY_OF_MONTH);
            datePanel.getModel().setSelected(true);
            datePanel.getModel().setDate(year, month, day);
            datePanel.getModel().setSelected(false);

            java.util.Date today = cal.getTime();

            int result = JOptionPane.showConfirmDialog(null, picker, "Select Date", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                java.util.Date selectedDate = (java.util.Date) picker.getModel().getValue();
                if (selectedDate != null) {
                    if (selectedDate.after(today)) {
                        JOptionPane.showMessageDialog(null, "Start date cannot be in the future.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    field3Date[0] = selectedDate;
                    dateField3.setText(sdf.format(selectedDate));
                }
            }
        });
        dateField4.addActionListener(e -> {
            org.jdatepicker.impl.UtilDateModel model = new org.jdatepicker.impl.UtilDateModel();
            java.util.Properties p = new java.util.Properties();
            org.jdatepicker.impl.JDatePanelImpl datePanel = new org.jdatepicker.impl.JDatePanelImpl(model, p);
            org.jdatepicker.impl.JDatePickerImpl picker = new org.jdatepicker.impl.JDatePickerImpl(datePanel, new org.jdatepicker.impl.DateComponentFormatter());

            // Set max selectable date to today using Calendar
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int year = cal.get(java.util.Calendar.YEAR);
            int month = cal.get(java.util.Calendar.MONTH);
            int day = cal.get(java.util.Calendar.DAY_OF_MONTH);
            datePanel.getModel().setSelected(true);
            datePanel.getModel().setDate(year, month, day);
            datePanel.getModel().setSelected(false);

            java.util.Date today = cal.getTime();

            int result = JOptionPane.showConfirmDialog(null, picker, "Select Date", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                java.util.Date selectedDate = (java.util.Date) picker.getModel().getValue();
                if (selectedDate != null) {
                    if (selectedDate.after(today)) {
                        JOptionPane.showMessageDialog(null, "Start date cannot be in the future.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    field4Date[0] = selectedDate;
                    dateField4.setText(sdf.format(selectedDate));
                }
            }
        });
        dateField5.addActionListener(e -> {
            org.jdatepicker.impl.UtilDateModel model = new org.jdatepicker.impl.UtilDateModel();
            java.util.Properties p = new java.util.Properties();
            org.jdatepicker.impl.JDatePanelImpl datePanel = new org.jdatepicker.impl.JDatePanelImpl(model, p);
            org.jdatepicker.impl.JDatePickerImpl picker = new org.jdatepicker.impl.JDatePickerImpl(datePanel, new org.jdatepicker.impl.DateComponentFormatter());

            // Set max selectable date to today using Calendar
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int year = cal.get(java.util.Calendar.YEAR);
            int month = cal.get(java.util.Calendar.MONTH);
            int day = cal.get(java.util.Calendar.DAY_OF_MONTH);
            datePanel.getModel().setSelected(true);
            datePanel.getModel().setDate(year, month, day);
            datePanel.getModel().setSelected(false);

            java.util.Date today = cal.getTime();

            int result = JOptionPane.showConfirmDialog(null, picker, "Select Date", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                java.util.Date selectedDate = (java.util.Date) picker.getModel().getValue();
                if (selectedDate != null) {
                    if (selectedDate.after(today)) {
                        JOptionPane.showMessageDialog(null, "Start date cannot be in the future.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    field5Date[0] = selectedDate;
                    dateField5.setText(sdf.format(selectedDate));
                }
            }
        });

        RoundedComboBox<String> typeOfLeave1 = new RoundedComboBox<>(new String[]{"", "Sick Leave", "Maternity Leave", "Paternity Leave", "Bereavement Leave"});
        RoundedComboBox<String> typeOfLeave2 = new RoundedComboBox<>(new String[]{"", "Sick Leave", "Maternity Leave", "Paternity Leave", "Bereavement Leave"});
        RoundedComboBox<String> typeOfLeave3 = new RoundedComboBox<>(new String[]{"", "Sick Leave", "Maternity Leave", "Paternity Leave", "Bereavement Leave"});
        RoundedComboBox<String> typeOfLeave4 = new RoundedComboBox<>(new String[]{"", "Sick Leave", "Maternity Leave", "Paternity Leave", "Bereavement Leave"});
        RoundedComboBox<String> typeOfLeave5 = new RoundedComboBox<>(new String[]{"", "Sick Leave", "Maternity Leave", "Paternity Leave", "Bereavement Leave"});

        int x = 20, y = 20, w = 220, h = 35, gap = 50;
        JLabel nameLabel = new JLabel("Name");
        int nameW = w + 80;
        nameLabel.setBounds(x, y, nameW, 25);
        nameField.setBounds(x, y + 25, nameW, h);
        nameField.setFont(detailsFont);
        nameLabel.setForeground(Color.WHITE);
        nameField.setEditable(false);
        groupPanel.add(nameLabel); groupPanel.add(nameField);

        JLabel idLabel = new JLabel("ID");
        int idX = x + nameW + 20;
        int idW = 140;
        idLabel.setBounds(idX, y, idW, 25);
        idField.setBounds(idX, y + 25, idW, h);
        idField.setFont(detailsFont);
        idLabel.setForeground(Color.WHITE);
        idField.setEditable(false);
        groupPanel.add(idLabel); groupPanel.add(idField);

        JLabel departmentLabel = new JLabel("Department");
        int deptX = idX + idW + 20;
        departmentLabel.setBounds(deptX, y, w, 25);
        departmentField.setBounds(deptX, y + 25, w, h);
        departmentField.setFont(detailsFont);
        departmentLabel.setForeground(Color.WHITE);
        departmentField.setEditable(false);
        groupPanel.add(departmentLabel); groupPanel.add(departmentField);

        JLabel employmentStatusLabel = new JLabel("Employment Status");
        int empStatX = deptX + w + 20;
        employmentStatusLabel.setBounds(empStatX, y, w, 25);
        employmentStatusField.setBounds(empStatX, y + 25, w, h);
        employmentStatusField.setFont(detailsFont);
        employmentStatusLabel.setForeground(Color.WHITE);
        employmentStatusField.setEditable(false);
        groupPanel.add(employmentStatusLabel); groupPanel.add(employmentStatusField);

        // Year, Leaves Used, Remaining SIL (below Name+ID)
        int row2Y = y + 25 + h + 20;
        int belowNameIdX = x;
        JLabel yearLabel = new JLabel("Year");
        yearLabel.setBounds(belowNameIdX, row2Y, 80, 25);
        yearLabel.setForeground(Color.WHITE);
        RoundedComboBox<String> yearCombo = new RoundedComboBox<>(new String[]{"2024", "2023", "2022"});
        yearCombo.setBounds(belowNameIdX, row2Y + 25, 140, h);
        yearCombo.setFont(detailsFont);
        yearCombo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ((javax.swing.JLabel)yearCombo.getRenderer()).setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        groupPanel.add(yearLabel); groupPanel.add(yearCombo);

        JLabel leavesUsedLabel = new JLabel("Leaves Used");
        leavesUsedLabel.setBounds(belowNameIdX + 160, row2Y, 120, 25);
        leavesUsedField.setBounds(belowNameIdX + 160, row2Y + 25, 140, h);
        leavesUsedField.setFont(detailsFont);
        leavesUsedLabel.setForeground(Color.WHITE);
        leavesUsedField.setEditable(false);
        groupPanel.add(leavesUsedLabel); groupPanel.add(leavesUsedField);

        JLabel remainingSILLabel = new JLabel("Remaining SIL");
        remainingSILLabel.setBounds(belowNameIdX + 320, row2Y, 120, 25);
        remainingSILField.setBounds(belowNameIdX + 320, row2Y + 25, 140, h);
        remainingSILField.setFont(detailsFont);
        remainingSILLabel.setForeground(Color.WHITE);
        remainingSILField.setEditable(false);
        groupPanel.add(remainingSILLabel); groupPanel.add(remainingSILField);

        // Type of Leave comboboxes (below Department)
        int typeOfLeaveY = y + 25 + h + 20;
        JLabel typeOfLeaveLabel = new JLabel("Type of Leave");
        typeOfLeaveLabel.setForeground(Color.WHITE);
        typeOfLeaveLabel.setBounds(deptX, typeOfLeaveY, w, 25);
        groupPanel.add(typeOfLeaveLabel);

        typeOfLeave1.setBounds(deptX, typeOfLeaveY + 25, w, h);
        typeOfLeave2.setBounds(deptX, typeOfLeaveY + 25 + gap, w, h);
        typeOfLeave3.setBounds(deptX, typeOfLeaveY + 25 + gap * 2, w, h);
        typeOfLeave4.setBounds(deptX, typeOfLeaveY + 25 + gap * 3, w, h);
        typeOfLeave5.setBounds(deptX, typeOfLeaveY + 25 + gap * 4, w, h);

        typeOfLeave1.setFont(detailsFont);
        typeOfLeave2.setFont(detailsFont);
        typeOfLeave3.setFont(detailsFont);
        typeOfLeave4.setFont(detailsFont);
        typeOfLeave5.setFont(detailsFont);

        typeOfLeave1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        typeOfLeave2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        typeOfLeave3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        typeOfLeave4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        typeOfLeave5.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Set initial enabled/disabled state for typeOfLeave comboboxes and date fields
        typeOfLeave1.setEnabled(true);
        typeOfLeave2.setEnabled(false);
        typeOfLeave3.setEnabled(false);
        typeOfLeave4.setEnabled(false);
        typeOfLeave5.setEnabled(false);

        dateField1.setEnabled(false);
        dateField2.setEnabled(false);
        dateField3.setEnabled(false);
        dateField4.setEnabled(false);
        dateField5.setEnabled(false);

        // Add listeners to enable the next combobox and date field only if the current one is filled
        typeOfLeave1.addActionListener(e -> {
            boolean filled = typeOfLeave1.getSelectedIndex() > 0;
            typeOfLeave2.setEnabled(filled);
            dateField1.setEnabled(filled);
            if (!filled) {
                typeOfLeave1.setSelectedIndex(0);
                typeOfLeave2.setEnabled(false); typeOfLeave2.setSelectedIndex(0);
                typeOfLeave3.setEnabled(false); typeOfLeave3.setSelectedIndex(0);
                typeOfLeave4.setEnabled(false); typeOfLeave4.setSelectedIndex(0);
                typeOfLeave5.setEnabled(false); typeOfLeave5.setSelectedIndex(0);

                dateField1.setEnabled(false);
                dateField2.setEnabled(false);
                dateField3.setEnabled(false);
                dateField4.setEnabled(false);
                dateField5.setEnabled(false);
            }
        });
        typeOfLeave2.addActionListener(e -> {
            boolean filled = typeOfLeave2.getSelectedIndex() > 0;
            typeOfLeave3.setEnabled(filled);
            dateField2.setEnabled(filled);
            if (!filled) {
                typeOfLeave2.setSelectedIndex(0);
                typeOfLeave3.setEnabled(false); typeOfLeave3.setSelectedIndex(0);
                typeOfLeave4.setEnabled(false); typeOfLeave4.setSelectedIndex(0);
                typeOfLeave5.setEnabled(false); typeOfLeave5.setSelectedIndex(0);

                dateField2.setEnabled(false);
                dateField3.setEnabled(false);
                dateField4.setEnabled(false);
                dateField5.setEnabled(false);
            }
        });
        typeOfLeave3.addActionListener(e -> {
            boolean filled = typeOfLeave3.getSelectedIndex() > 0;
            typeOfLeave4.setEnabled(filled);
            dateField3.setEnabled(filled);
            if (!filled) {
                typeOfLeave3.setSelectedIndex(0);
                typeOfLeave4.setEnabled(false); typeOfLeave4.setSelectedIndex(0);
                typeOfLeave5.setEnabled(false); typeOfLeave5.setSelectedIndex(0);

                dateField3.setEnabled(false);
                dateField4.setEnabled(false);
                dateField5.setEnabled(false);
            }
        });
        typeOfLeave4.addActionListener(e -> {
            boolean filled = typeOfLeave4.getSelectedIndex() > 0;
            typeOfLeave5.setEnabled(filled);
            dateField4.setEnabled(filled);
            if (!filled) {
                typeOfLeave4.setSelectedIndex(0);
                dateField4.setEnabled(false);
                dateField5.setEnabled(false);
            }
        });
        typeOfLeave5.addActionListener(e -> {
            boolean filled = typeOfLeave4.getSelectedIndex() > 0;
            typeOfLeave5.setEnabled(filled);
            dateField5.setEnabled(filled);
            if (!filled) {
                typeOfLeave5.setSelectedIndex(0);
                dateField5.setEnabled(false);
            }
        });
        groupPanel.add(typeOfLeave1); groupPanel.add(typeOfLeave2);
        groupPanel.add(typeOfLeave3); groupPanel.add(typeOfLeave4); groupPanel.add(typeOfLeave5);

        JComboBox<?>[] comboBoxes = {typeOfLeave1, typeOfLeave2, typeOfLeave3, typeOfLeave4, typeOfLeave5, yearCombo};
        for (JComboBox<?> combo : comboBoxes) {
            combo.setBackground(comboDefaultBg);
            combo.setOpaque(false);
            combo.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (combo.isEnabled()) {
                        combo.setBackground(comboHoverBg);
                    }
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    combo.setBackground(comboDefaultBg);
                }
            });
        }

        // Date textfields (below Employment Status)
        int dateY = y + 25 + h + 20;
        JLabel dateLabel = new JLabel("Date");
        dateLabel.setBounds(empStatX, dateY, w, 25);
        dateLabel.setForeground(Color.WHITE);
        groupPanel.add(dateLabel);

        dateField1.setBounds(empStatX, dateY + 25, w, h);
        dateField2.setBounds(empStatX, dateY + 25 + gap, w, h);
        dateField3.setBounds(empStatX, dateY + 25 + gap * 2, w, h);
        dateField4.setBounds(empStatX, dateY + 25 + gap * 3, w, h);
        dateField5.setBounds(empStatX, dateY + 25 + gap * 4, w, h);
        dateField1.setFont(detailsFont); dateField2.setFont(detailsFont);
        dateField3.setFont(detailsFont); dateField4.setFont(detailsFont); dateField5.setFont(detailsFont);
        groupPanel.add(dateField1); groupPanel.add(dateField2);
        groupPanel.add(dateField3); groupPanel.add(dateField4); groupPanel.add(dateField5);

        dateField1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dateField2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dateField3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dateField4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dateField5.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        dateField1.setPreferredSize(new Dimension(120, 35));
        dateField2.setPreferredSize(new Dimension(120, 35));
        dateField3.setPreferredSize(new Dimension(120, 35));
        dateField4.setPreferredSize(new Dimension(120, 35));
        dateField5.setPreferredSize(new Dimension(120, 35));

        // Set preferred size for the group panel to match its content, not too wide
        groupPanel.setPreferredSize(new java.awt.Dimension(990, 400)); // Adjust width as needed

        // --- Place components in detailsPanel using GridBagLayout ---

        GridBagConstraints gbc = new GridBagConstraints();

        int buttonWidth = 160; // wider if you want
        int buttonHeight = h;  // same as your text fields
        int arc = 20; 

        // Back button (upper left)
        RoundedButton backButton = new RoundedButton("Back", arc);
        backButton.setFont(detailsFont);
        backButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight)); // width increased, height same as text fields
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder());
        backButton.setContentAreaFilled(false);
        backButton.setOpaque(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover and click effect for back button
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                backButton.setBackground(new Color(30, 30, 30)); // Slightly lighter black
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                backButton.setBackground(Color.BLACK);
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                backButton.setBackground(new Color(60, 60, 60)); // Even lighter on click
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                backButton.setBackground(backButton.getBounds().contains(e.getPoint()) ? new Color(30, 30, 30) : Color.BLACK);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(25, 30, 0, 0); // Top, Left, Bottom, Right
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        detailsPanel.add(backButton, gbc);

        // Centered groupPanel
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 10, 0, 0);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        detailsPanel.add(groupPanel, gbc);

        // Save button (bottom right)
        RoundedButton saveButton = new RoundedButton("Save", arc);
        saveButton.setFont(detailsFont);
        saveButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight)); // width increased, height same as text fields
        saveButton.setBackground(Color.WHITE);
        saveButton.setForeground(new Color(0, 153, 0));
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder());
        saveButton.setContentAreaFilled(false);
        saveButton.setOpaque(false);
        saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover and click effect for save button
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                saveButton.setBackground(new Color(140, 153, 140)); // Light greenish white
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                saveButton.setBackground(Color.WHITE);
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                saveButton.setBackground(new Color(200, 240, 200)); // Slightly darker greenish white
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                saveButton.setBackground(saveButton.getBounds().contains(e.getPoint()) ? new Color(230, 255, 230) : Color.WHITE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.insets = new Insets(0, 0, 30, 35); // Top, Left, Bottom, Right
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        detailsPanel.add(saveButton, gbc);

        // --- Add panels to CardLayout ---
        contentPanel.setBackground(MainWindow.activeColor);
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        contentPanel.add(tablePanel, "TableView");
        contentPanel.add(detailsPanel, "DetailsView");
        add(contentPanel, BorderLayout.CENTER);

        // --- Table row click event ---
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int viewRow = table.getSelectedRow();
                if (viewRow != -1) {
                    // Get the original index from our mapping
                    int modelRow = filteredToOriginalIndex.get(viewRow);

                    nameField.setText(tableViewData[modelRow][0].toString());
                    idField.setText(tableViewData[modelRow][1].toString());
                    departmentField.setText(tableViewData[modelRow][2].toString());
                    employmentStatusField.setText(tableViewData[modelRow][3].toString());
                    // Set default values when opening details
                    leavesUsedField.setText("0");
                    remainingSILField.setText("5 / 5");
                    typeOfLeave1.setSelectedIndex(0);
                    typeOfLeave2.setSelectedIndex(0);
                    typeOfLeave3.setSelectedIndex(0);
                    typeOfLeave4.setSelectedIndex(0);
                    typeOfLeave5.setSelectedIndex(0);

                    searchField.setText(""); // Clear search field
                    cardLayout.show(contentPanel, "DetailsView");
                    nameField.setFocusable(false);
                    idField.setFocusable(false);
                    departmentField.setFocusable(false);
                    employmentStatusField.setFocusable(false);
                    leavesUsedField.setFocusable(false);
                    remainingSILField.setFocusable(false);
                }
            }
        });

        // --- Save button logic ---
        saveButton.addActionListener(e -> {
            int used = 0;
            JComboBox<?>[] leaveBoxes = {typeOfLeave1, typeOfLeave2, typeOfLeave3, typeOfLeave4, typeOfLeave5};
            JButton[] dateButtons = {dateField1, dateField2, dateField3, dateField4, dateField5};
            boolean valid = true;

            for (int i = 0; i < 5; i++) {
                if (leaveBoxes[i].getSelectedIndex() > 0) {
                    String dateText = dateButtons[i].getText().trim();

                    if (dateText.equals("Input Date")) {
                        JOptionPane.showMessageDialog(null,
                                "Please input date for leave #" + (i + 1),
                                "Missing Date",
                                JOptionPane.WARNING_MESSAGE);
                        valid = false;
                        break;  // stop checking further, since saving shouldn't proceed
                    } else {
                        used++;
                    }
                }
            }

            if (valid) {
                leavesUsedField.setText(String.valueOf(used));
                remainingSILField.setText((5 - used) + " / 5");
            }
        });

        // --- Back button event ---
        backButton.addActionListener(e -> cardLayout.show(contentPanel, "TableView"));

        // --- Search filter logic ---

        rowSorter = new TableRowSorter<>(employeeTableModel);
        table.setRowSorter(rowSorter);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { searchUsingBinarySearch(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { searchUsingBinarySearch(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {searchUsingBinarySearch(); }
            private void searchUsingBinarySearch() {
                String searchText = searchField.getText().toLowerCase();
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);
                filteredToOriginalIndex.clear();

                if (searchText.isEmpty()) {
                    // Display all rows if search field is empty
                    for (int i = 0; i < tableViewData.length; i++) {
                        model.addRow(tableViewData[i]);
                        filteredToOriginalIndex.put(i, i);
                    }
                } else {
                    // Linear search for all matching entries
                    int filteredIndex = 0;
                    for (int i = 0; i < tableViewData.length; i++) {
                        Object[] row = tableViewData[i];
                        String name = row[0].toString().toLowerCase();
                        if (name.contains(searchText)) {
                            model.addRow(row);
                            filteredToOriginalIndex.put(filteredIndex, i);
                            filteredIndex++;
                        }
                    }
                }
            }
        });
    }
}