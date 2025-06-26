package Screens;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


import Components.TableStyler;
import Components.RoundedComboBox; // Assuming this class is available
import javax.swing.plaf.basic.BasicScrollBarUI; // Import for scrollbar styling
import javax.swing.plaf.basic.ComboPopup; // Import for ComboPopup
import javax.swing.border.EmptyBorder; // Import for EmptyBorder

import Module.E201File.E201File;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays; // Needed for Arrays.copyOfRange in filterTable

public class Attendance extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainContainer;

    private static DefaultTableModel employeeTableModel;
    private static String[] tableViewHeaders = { "Name", "ID", "Department", "Employment Status" };
    private static Object[][] tableViewData;
    private static Object[][] detailsViewData;
    private JTable table;
    private TableRowSorter<DefaultTableModel> rowSorter;
    // Declare these at the class level for access within listeners
    private JComboBox<String> departmentDropdown; // This will now be the "sortCombo"
    private JTextField searchField;
    // Store original raw data for filtering (as in PayrollScreen's approach)
    private Object[][] rawEmployeeData;

    private Map<Integer, Integer> filteredToOriginalIndex = new HashMap<>();


    /**
     * Loads employee data from E201File and populates the table model.
     * This method is static so it can be called from outside the Attendance class
     * to refresh the employee table data globally.
     */
    public static void loadEmployeeTabledata() {
        // Query raw data from the external source
        Object[][] rawData = E201File.getEmployeeTableData();

        // Map data for table view
        tableViewData = new Object[rawData.length][tableViewHeaders.length];
        for (int i = 0; i < rawData.length; i++) {
            tableViewData[i][0] = rawData[i][1] + ", " + rawData[i][2]; // Combine last name and first name
            tableViewData[i][1] = rawData[i][0]; // ID
            tableViewData[i][2] = rawData[i][4]; // Department
            tableViewData[i][3] = rawData[i][5]; // Employment Status

        }
        // This is crucial: Set the model *before* any filtering is attempted on it.
        if (employeeTableModel != null) {
            employeeTableModel.setDataVector(tableViewData, tableViewHeaders);
        }
        detailsViewData = rawData; // Store raw data directly for details view (if needed later)
    }

    private void initializeFilteredIndices() {
        filteredToOriginalIndex.clear();
        // Map all original indices when table is first loaded
        for (int i = 0; i < tableViewData.length; i++) {
            filteredToOriginalIndex.put(i, i);
        }
    }

    /**
     * Constructor for the Attendance panel.
     * Sets up the UI components, layout, and event listeners.
     */
    public Attendance() {
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // --- Table Panel (Main content panel with green background) ---
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(34, 139, 34)); // Original dark green background

        // --- Header Area (Container for top and bottom bars) ---
        JPanel headerArea = new JPanel();
        headerArea.setLayout(new BoxLayout(headerArea, BoxLayout.Y_AXIS)); // Stack panels vertically
        headerArea.setBackground(new Color(34, 139, 34)); // Ensure consistent green background

        // --- Top Bar: Search, Sort By Department ---
        JPanel topBar = new JPanel(new GridBagLayout()); // Using GridBagLayout for precise control
        topBar.setBackground(new Color(34, 139, 34)); // Original green
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding for the bar itself

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; // Allow components to fill their display area
        gbc.insets = new Insets(0, 0, 0, 0); // Default no insets between GridBag cells

        // --- Search Bar Section ---
        JPanel searchMainPanel = new JPanel(new BorderLayout(10, 0)); // No horizontal gap between search button and field
        searchMainPanel.setOpaque(false);

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
        };// Assign to class member
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5)); // Standard internal padding for text
        searchField.setBackground(Color.WHITE); // White background for the input area

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 16));
        searchButton.setPreferredSize(new Dimension(150, 10)); // Adjusted width and height for consistency
        searchButton.setBackground(Color.WHITE);
        searchButton.setForeground(Color.BLACK); // Ensure text is black
        searchButton.setBorder(null); // No border for the button
        searchButton.setFocusable(false);

        //searchMainPanel.add(searchButton, BorderLayout.WEST); // Search button on the left
        searchMainPanel.add(searchField, BorderLayout.CENTER); // Text field takes center

        // Set preferred size for the entire search block to match image height
        searchMainPanel.setPreferredSize(new Dimension(searchMainPanel.getPreferredSize().width, 45));
        searchMainPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        // Add the integrated searchMainPanel to the topBar's GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Allow it to expand horizontally, taking most of the space
        gbc.insets = new Insets(0, 0, 0, 10); // Padding to the right of the search block
        topBar.add(searchMainPanel, gbc);


        // --- Sort By Department Section ---
        // Create a JPanel to hold both the label and the dropdown, side-by-side
        // Changed horizontal gap to 0 to remove spacing between components
        JPanel sortFilterContainerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        sortFilterContainerPanel.setOpaque(false); // Make it transparent

        // Label for "Sort by Department:"
        JLabel sortLabel = new JLabel("Sort by Department: ");
        sortLabel.setFont(new Font("Arial", Font.BOLD, 16));
        sortLabel.setForeground(Color.BLACK); // Changed to BLACK to be visible on white background
        sortLabel.setBackground(Color.WHITE); // Set background to white
        sortLabel.setOpaque(true); // Make the label opaque so background is painted
        // Removed right padding from the border to eliminate gap
        // Adjusted top/bottom padding slightly to help with vertical alignment for a 45px height
        sortLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 16, 15));
        sortFilterContainerPanel.add(sortLabel); // Add the label to the container

        // Dropdown (as specified by user, now named departmentDropdown)
        departmentDropdown = new RoundedComboBox<>(new String[] {
                "All Departments", // First item as requested by user
                "Human Resource", "Administration", "Accounting", "Sales",
                "Production", "Production (Pre-Press)", "Production (Press)", "Production (Post-Press)", "Production (Quality Control)"
        }) {
            @Override
            protected void paintBorder(Graphics g) {
                // Do nothing: no border for this instance
            }
        };
        departmentDropdown.setFont(new Font("Arial", Font.BOLD, 16)); // Keep bold font for consistency
        departmentDropdown.setPreferredSize(new Dimension(220, 50)); // Adjust width, maintain height
        departmentDropdown.setBackground(Color.WHITE);
        departmentDropdown.setFocusable(false);
        departmentDropdown.setMaximumRowCount(12);
        ((JLabel)departmentDropdown.getRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        // Custom renderer for hover effect in dropdown list
        departmentDropdown.setRenderer(new DefaultListCellRenderer() {
            private int hoveredIndex = -1;
            {
                departmentDropdown.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
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
                        ComboPopup popup = (ComboPopup) departmentDropdown.getUI().getAccessibleChild(departmentDropdown, 0);
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

        // Add hover effect
        Color comboDefaultBg = Color.WHITE;
        Color comboHoverBg = new Color(230, 255, 230); // light greenish
        departmentDropdown.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (departmentDropdown.isEnabled()) {
                    departmentDropdown.setBackground(comboHoverBg);
                }
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                departmentDropdown.setBackground(comboDefaultBg);
            }
        });
        sortFilterContainerPanel.add(departmentDropdown); // Add the dropdown to the container

        // Add the combined panel to the topBar's GridBagLayout
        gbc.gridx = 1; // This block is next to the search bar
        gbc.gridy = 0;
        gbc.weightx = 0.0; // Don't expand, just take required space
        gbc.insets = new Insets(0, 0, 0, 0); // No extra padding for this cell
        topBar.add(sortFilterContainerPanel, gbc); // Add the new container panel

        headerArea.add(topBar); // Add the top bar to the header area


        // --- Bottom Bar: Logo and Attendance Backup Button ---
        JPanel bottomBar = new JPanel(new BorderLayout()); // Use BorderLayout for the logo and backup button
        bottomBar.setBackground(new Color(34, 139, 34)); // Original green
        bottomBar.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Padding around elements

        // Logo Wrapper (Original styling, on the left)
        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Use FlowLayout for precise left alignment
        logoArea.setOpaque(false); // Make it transparent

        int targetHeight = 30; // Original height from PayrollScreen snippet
        // Load and scale the logo image
        ImageIcon logoIcon = new ImageIcon(getClass().getClassLoader().getResource("whole_logo.png"));
        int origWidth = logoIcon.getIconWidth();
        int origHeight = logoIcon.getIconHeight();
        int targetWidth = (int) ((double) origWidth / origHeight * targetHeight);
        Image scaledLogo = logoIcon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);

        // Create a JLabel for the logo and text "SynergyGrafixCorp." as shown in the image
        JLabel logoAndTextLabel = new JLabel(new ImageIcon(scaledLogo));


        JPanel logoWrapper = new JPanel(new BorderLayout());
        logoWrapper.setBackground(Color.WHITE); // Original white background
        logoWrapper.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Original padding
        logoWrapper.add(logoAndTextLabel, BorderLayout.CENTER); // Add the combined label
        logoArea.add(logoWrapper);
        bottomBar.add(logoArea, BorderLayout.WEST); // Add to the left of the bottom bar


        // Attendance Backup Button (Black background, white text, on the right)
        JPanel backupButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // FlowLayout for precise right alignment
        backupButtonPanel.setOpaque(false); // Transparent background

        JButton attendanceBackupButton = new JButton("Attendance Backup"); // Text as in image
        attendanceBackupButton.setPreferredSize(new Dimension(225, 50)); // Adjusted size to match image
        attendanceBackupButton.setBackground(Color.WHITE); // Black background as in image
        attendanceBackupButton.setForeground(Color.BLACK); // White text
        attendanceBackupButton.setFont(new Font("Arial", Font.BOLD, 16));
        attendanceBackupButton.setFocusPainted(false);
        attendanceBackupButton.setBorder(BorderFactory.createEmptyBorder()); // No border
        attendanceBackupButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Indicate it's clickable

        attendanceBackupButton.addActionListener(e -> {
            mainContainer.add(new AttendanceBackup(cardLayout, mainContainer), "backup");
            cardLayout.show(mainContainer, "backup");
        });
        backupButtonPanel.add(attendanceBackupButton);
        bottomBar.add(backupButtonPanel, BorderLayout.EAST); // Add to the right of the bottom bar

        headerArea.add(bottomBar); // Add the bottom bar to the header area

        // Add the complete header area to the top of the tablePanel
        tablePanel.add(headerArea, BorderLayout.NORTH);

        // --- Employee Table Setup ---
        employeeTableModel = new DefaultTableModel(tableViewData, tableViewHeaders) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        table = new JTable(employeeTableModel);
        loadEmployeeTabledata();
// Then initialize filtered indices
        initializeFilteredIndices();
        table.getTableHeader().setReorderingAllowed(false); // Prevent column reordering
        TableStyler.styleTable(table); // Apply custom styling

        // Initialize row sorter for filtering and sorting
        rowSorter = new TableRowSorter<>(employeeTableModel);
        table.setRowSorter(rowSorter); // Apply the sorter to the table

        // --- Event Listeners ---

        // Listener for the search field to filter table rows
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchUsingBinarySearch(); }
            public void removeUpdate(DocumentEvent e) { searchUsingBinarySearch(); }
            public void changedUpdate(DocumentEvent e) { searchUsingBinarySearch();}

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

        // Listener for the department dropdown to filter table rows by department
        departmentDropdown.addActionListener(e -> {
            String selected = (String) departmentDropdown.getSelectedItem();
            // Now, "All Departments" is the default item that clears the filter
            if (selected != null && selected.equals("All Departments")) {
                rowSorter.setRowFilter(null); // Clear the filter
            } else if (selected != null) {
                // Apply filter to the "Department" column (index 2)
                rowSorter.setRowFilter(RowFilter.regexFilter("^" + Pattern.quote(selected) + "$", 2));
            }
        });


        // Mouse motion listener for row highlighting on hover
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != -1) {
                    table.setRowSelectionInterval(row, row); // Select the hovered row
                } else {
                    table.clearSelection(); // Clear selection if not hovering over a row
                }
            }
        });

        // Mouse listener for row clicks (to view employee details)
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int viewRow = table.getSelectedRow();
                if (viewRow != -1) {
                    // Get the original index from our mapping
                    int modelRow = filteredToOriginalIndex.get(viewRow);

                    // Get the row data using the original index
                    Object[] rowData = new Object[employeeTableModel.getColumnCount()];
                    for (int i = 0; i < employeeTableModel.getColumnCount(); i++) {
                        rowData[i] = tableViewData[modelRow][i];
                    }

                    searchField.setText(""); // Clear search field
                    EmployeeAttendanceDetail detailPanel = new EmployeeAttendanceDetail(cardLayout, mainContainer, rowData);
                    mainContainer.add(detailPanel, "detail");
                    cardLayout.show(mainContainer, "detail");
                }
            }
        });

        // Wrap the table in a JScrollPane for scrollability
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Original padding

        // --- Apply Green Scroll Bar Styling ---
        // Apply custom scrollbar UI for the vertical scroll bar
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(34, 139, 34); // Green thumb
                this.trackColor = new Color(100, 180, 100); // Lighter green track
            }
            // Optional: Override paintThumb and paintTrack for custom shapes/rounding
            @Override
            protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                button.setVisible(false);
                return button;
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
                g2.dispose();
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(trackColor);
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                g2.dispose();
            }
        });

        // Apply custom scrollbar UI for the horizontal scroll bar (if applicable)
        scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(34, 139, 34); // Green thumb
                this.trackColor = new Color(100, 180, 100); // Lighter green track
            }
            @Override
            protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                button.setVisible(false);
                return button;
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
                g2.dispose();
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(trackColor);
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                g2.dispose();
            }
        });

        // Add the scroll pane (containing the table) to the center of the tablePanel
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add the main tablePanel to the CardLayout container
        mainContainer.add(tablePanel, "table");
        // Finally, add the mainContainer to this Attendance panel
        add(mainContainer, BorderLayout.CENTER);

        // Load data into the table after all components are set up
        loadEmployeeTabledata();
    }
}