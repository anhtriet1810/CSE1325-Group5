package supplydistribution;
import javax.swing.*;
//import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/// frontend file

/**
 * Room Supply Distribution System - Map screen.
 */
public class MapGUI extends JPanel {
    private static final long serialVersionUID = 1L;
    //unused due to AccessLevel.java
    //private static final String ROLE_STUDENT = "Student";
    private static final Color SELECTED_FILL = new Color(168, 215, 178);
    private static final Color SELECTED_BORDER = new Color(40, 110, 60);

    /** Called by the Add Room / Edit Room dialog; throws IllegalArgumentException if input is invalid. */
    private interface RoomAction {
        void apply(String name, int number, int itemCapacity, int personCapacity);
    }

    /** Called by the New Item / Edit Item dialog; throws IllegalArgumentException if input is invalid. */
    private interface ItemAction {
        void apply(Item item);
    }

    private final AccessLevel role;
    private final boolean canEdit;
    private final Runnable onClose;

    private final List<RoomView> rooms = new ArrayList<>();
    private RoomView selected;
    private RoomView listedRoom;   // room whose items are currently in the list
    private boolean dirty;         // unsaved changes?

    private final MapCanvas canvas = new MapCanvas();
    private final JTextArea details = new JTextArea();
    private final DefaultListModel<Item> itemListModel = new DefaultListModel<>();
    private final JList<Item> itemList = new JList<>(itemListModel);
    private final JLabel status = new JLabel();

    private JButton addRoomButton;
    private JButton editRoomButton;
    private JButton removeRoomButton;
    private JButton newItemButton;
    private JButton editItemButton;
    private JButton deleteItemButton;
    private JButton moveItemButton;

    /**
     * @param role         "Student", "Faculty" or "Admin"
     * @param loadExisting true = ask for a map file right away; false = start a new map
     * @param onClose      run when the user closes the map (or cancels the initial load)
     */
    public MapGUI(AccessLevel role, boolean loadExisting, Runnable onClose) {
        super(new BorderLayout(8, 8));
        this.role = role;
        this.canEdit = !AccessLevel.STUDENT.equals(role);
        this.onClose = onClose != null ? onClose : () -> { };
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(buildToolbar(), BorderLayout.NORTH);
        add(new JScrollPane(canvas), BorderLayout.CENTER);
        add(buildSidePanel(), BorderLayout.EAST);
        status.setBorder(BorderFactory.createEmptyBorder(2, 4, 0, 4));
        add(status, BorderLayout.SOUTH);

        if (loadExisting) {
            // Later, so the panel is already on screen when the file chooser opens.
            SwingUtilities.invokeLater(() -> {
                if (!loadMapFromChooser()) this.onClose.run();
            });
        } else {
            Point slot = RoomView.gridSlot(0);
            rooms.add(new RoomView("Storage", 100, 500, 0, slot.x, slot.y));
        }
        refreshAll();
    }

    // ======================================================================
    // Building the screen
    // ======================================================================

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        addRoomButton = new JButton("Add Room");
        editRoomButton = new JButton("Edit Room");
        removeRoomButton = new JButton("Remove Room");
        JButton exportButton = new JButton("Export Map");
        JButton openButton = new JButton("Open Different Map");
        JButton closeButton = new JButton("Close Map");
        bar.add(addRoomButton);
        bar.add(editRoomButton);
        bar.add(removeRoomButton);
        bar.add(Box.createHorizontalStrut(16));
        bar.add(exportButton);
        bar.add(openButton);
        bar.add(closeButton);

        addRoomButton.addActionListener(e -> addRoom());
        editRoomButton.addActionListener(e -> editRoom());
        removeRoomButton.addActionListener(e -> removeRoom());
        exportButton.addActionListener(e -> exportMap());
        openButton.addActionListener(e -> openDifferentMap());
        closeButton.addActionListener(e -> closeMap());
        return bar;
    }

    private JPanel buildSidePanel() {
        JPanel side = new JPanel(new BorderLayout(6, 6));
        side.setPreferredSize(new Dimension(250, 0));

        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        details.setRows(7);
        details.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        JScrollPane detailsPane = new JScrollPane(details);
        detailsPane.setBorder(BorderFactory.createTitledBorder("Room Details"));

        newItemButton = new JButton("New Item");
        editItemButton = new JButton("Edit Item");
        deleteItemButton = new JButton("Delete Item");
        moveItemButton = new JButton("Move Item");
        newItemButton.addActionListener(e -> newItem());
        editItemButton.addActionListener(e -> editItem());
        deleteItemButton.addActionListener(e -> deleteItem());
        moveItemButton.addActionListener(e -> moveItem());

        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemList.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean hasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
                if (value instanceof Item) {
                    Item item = (Item) value;
                    label.setText(item.getName() + " - " + item.getQuantity());
                    label.setToolTipText(item.getDescription().isEmpty() ? null : item.getDescription());
                }
                return label;
            }
        });
        itemList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtons();
        });
        JScrollPane itemPane = new JScrollPane(itemList);
        itemPane.setBorder(BorderFactory.createTitledBorder("Inventory (hover for description)"));

        JPanel buttons = new JPanel(new GridLayout(2, 2, 6, 6));
        buttons.add(newItemButton);
        buttons.add(editItemButton);
        buttons.add(deleteItemButton);
        buttons.add(moveItemButton);

        side.add(detailsPane, BorderLayout.NORTH);
        side.add(itemPane, BorderLayout.CENTER);
        side.add(buttons, BorderLayout.SOUTH);
        return side;
    }

    // ======================================================================
    // Refreshing the screen
    // ======================================================================

    private void select(RoomView room) {
        selected = room;
        refreshAll();
    }

    /** Call after any change to the map data. */
    private void changed() {
        dirty = true;
        refreshAll();
    }

    private void refreshAll() {
        refreshDetails();
        updateButtons();
        updateStatus();
        canvas.repaint();
    }

    private void refreshDetails() {
        int keep = (listedRoom == selected) ? itemList.getSelectedIndex() : -1;
        itemListModel.clear();
        listedRoom = selected;
        if (selected == null) {
            details.setText("No room selected.\n\nClick a room on the map to see its details.");
            return;
        }
        details.setText("Room: " + selected.getName()
                + "\nNumber: " + selected.getNumber()
                + "\nItem Capacity: " + selected.totalQuantity() + " / " + selected.getItemCapacity()
                + "\nPerson Capacity: " + selected.getPersonCapacity()
                + "\nDifferent items: " + selected.getItems().size());
        for (Item item : selected.getItems()) itemListModel.addElement(item);
        if (keep >= 0 && keep < itemListModel.size()) itemList.setSelectedIndex(keep);
    }

    private void updateButtons() {
        boolean hasRoom = selected != null;
        boolean hasItem = hasRoom && itemList.getSelectedIndex() >= 0;
        addRoomButton.setEnabled(canEdit);
        editRoomButton.setEnabled(canEdit && hasRoom);
        removeRoomButton.setEnabled(canEdit && hasRoom);
        newItemButton.setEnabled(canEdit && hasRoom);
        editItemButton.setEnabled(canEdit && hasItem);
        deleteItemButton.setEnabled(canEdit && hasItem);
        moveItemButton.setEnabled(canEdit && hasItem && rooms.size() > 1);
    }

    private void updateStatus() {
        status.setText("Access level: " + role + (canEdit ? "" : " (view only)")
                + "   |   Rooms: " + rooms.size() + "/" + RoomView.MAX_ROOMS
                + (dirty ? "   |   Unsaved changes" : ""));
    }

    // ======================================================================
    // Rooms
    // ======================================================================

    private void addRoom() {
        if (rooms.size() >= RoomView.MAX_ROOMS) {
            error("The map is full (" + RoomView.MAX_ROOMS + " rooms maximum). Remove a room first.");
            return;
        }
        Point slot = findFreeSlot();
        if (slot == null) {
            error("There is no free space left on the map. Move or remove a room first.");
            return;
        }
        promptRoom("Add Room", "Confirm Room Creation", null, (name, number, itemCap, personCap) -> {
            requireUniqueNumber(number, null);
            RoomView room = new RoomView(name, number, itemCap, personCap, slot.x, slot.y);
            rooms.add(room);
            selected = room;
            changed();
        });
    }

    private void editRoom() {
        if (selected == null) return;
        promptRoom("Edit Room", "Save Changes", selected, (name, number, itemCap, personCap) -> {
            requireUniqueNumber(number, selected);
            selected.update(name, number, itemCap, personCap);
            changed();
        });
    }

    private void removeRoom() {
        if (selected == null) return;
        int count = selected.getItems().size();
        String message = "Remove room " + selected + "?"
                + (count > 0 ? "\nIts " + count + " item type(s) will be deleted too." : "");
        if (JOptionPane.showConfirmDialog(this, message, "Confirm Remove Room",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        rooms.remove(selected);
        selected = null;
        changed();
    }

    /** Shows the room form until the input is accepted by the action (true) or the user cancels (false). */
    private boolean promptRoom(String title, String confirmLabel, RoomView existing, RoomAction action) {
        JTextField name = new JTextField(existing == null ? "" : existing.getName());
        JSpinner number = new JSpinner(new SpinnerNumberModel(
                existing == null ? nextRoomNumber() : existing.getNumber(), 0, RoomView.MAX_NUMBER, 1));
        JSpinner itemCap = new JSpinner(new SpinnerNumberModel(
                existing == null ? 100 : existing.getItemCapacity(), 0, RoomView.MAX_CAPACITY, 1));
        JSpinner personCap = new JSpinner(new SpinnerNumberModel(
                existing == null ? 10 : existing.getPersonCapacity(), 0, RoomView.MAX_CAPACITY, 1));
        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.add(new JLabel("Room Name:"));
        form.add(name);
        form.add(new JLabel("Room Number:"));
        form.add(number);
        form.add(new JLabel("Item Capacity (units):"));
        form.add(itemCap);
        form.add(new JLabel("Person Capacity:"));
        form.add(personCap);

        Object[] options = {confirmLabel, "Cancel"};
        while (true) {
            int choice = JOptionPane.showOptionDialog(this, form, title, JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (choice != 0) return false;
            try {
                action.apply(name.getText(), spinnerValue(number), spinnerValue(itemCap), spinnerValue(personCap));
                return true;
            } catch (IllegalArgumentException ex) {
                error(ex.getMessage());
            }
        }
    }

    private int nextRoomNumber() {
        int next = 101;
        for (RoomView room : rooms) next = Math.max(next, room.getNumber() + 1);
        return Math.min(next, RoomView.MAX_NUMBER);
    }

    private void requireUniqueNumber(int number, RoomView ignore) {
        for (RoomView room : rooms) {
            if (room != ignore && room.getNumber() == number) {
                throw new IllegalArgumentException("Room number " + number + " is already used by \""
                        + room.getName() + "\".");
            }
        }
    }

    /** First default grid slot not covered by an existing room, or null if the map is crowded. */
    private Point findFreeSlot() {
        for (int i = 0; i < RoomView.MAX_ROOMS; i++) {
            Point p = RoomView.gridSlot(i);
            Rectangle slot = new Rectangle(p.x, p.y, RoomView.WIDTH, RoomView.HEIGHT);
            boolean free = true;
            for (RoomView room : rooms) {
                if (room.getBounds().intersects(slot)) { free = false; break; }
            }
            if (free) return p;
        }
        return null;
    }

    private RoomView roomAt(int px, int py) {
        for (int i = rooms.size() - 1; i >= 0; i--) { // last drawn = on top
            if (rooms.get(i).getBounds().contains(px, py)) return rooms.get(i);
        }
        return null;
    }

    // ======================================================================
    // Items
    // ======================================================================

    private void newItem() {
        if (selected == null) return;
        promptItem("New Item (free capacity: " + selected.remainingCapacity() + ")", "Confirm", null, item -> {
            selected.addItem(item);
            changed();
        });
    }

    private void editItem() {
        int index = itemList.getSelectedIndex();
        if (selected == null || index < 0) return;
        Item existing = selected.getItems().get(index);
        promptItem("Edit Item", "Save Changes", existing, item -> {
            selected.replaceItem(index, item);
            changed();
        });
    }

    private void deleteItem() {
        int index = itemList.getSelectedIndex();
        if (selected == null || index < 0) return;
        if (JOptionPane.showConfirmDialog(this, "Delete selected item?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        selected.removeItem(index);
        changed();
    }

    /** Moves some (or all) units of the selected item to another room. */
    private void moveItem() {
        RoomView source = selected;
        int index = itemList.getSelectedIndex();
        if (source == null || index < 0) return;
        Item item = source.getItems().get(index);

        List<RoomView> targets = new ArrayList<>(rooms);
        targets.remove(source);
        if (targets.isEmpty()) {
            error("Add another room first.");
            return;
        }
        String[] labels = new String[targets.size()];
        for (int i = 0; i < labels.length; i++) {
            RoomView t = targets.get(i);
            labels[i] = t + "  (free: " + t.remainingCapacity() + ")";
        }
        JComboBox<String> destination = new JComboBox<>(labels);
        JSpinner amount = new JSpinner(new SpinnerNumberModel(1, 1, item.getQuantity(), 1));
        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.add(new JLabel("Move \"" + item.getName() + "\" to:"));
        form.add(destination);
        form.add(new JLabel("Amount (1 to " + item.getQuantity() + "):"));
        form.add(amount);

        while (true) {
            int result = JOptionPane.showConfirmDialog(this, form, "Move Item",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return;
            RoomView target = targets.get(destination.getSelectedIndex());
            int n = spinnerValue(amount);
            try {
                // Add first: if the destination is full this throws and nothing is changed.
                target.addItem(new Item(item.getName(), n, item.getDescription()));
                source.reduceItem(index, n);
                changed();
                return;
            } catch (IllegalArgumentException ex) {
                error(ex.getMessage());
            }
        }
    }

    /** Shows the item form until the action accepts it (true) or the user cancels (false). */
    private boolean promptItem(String title, String confirmLabel, Item existing, ItemAction action) {
        JTextField name = new JTextField(existing == null ? "" : existing.getName());
        JSpinner quantity = new JSpinner(new SpinnerNumberModel(
                existing == null ? 1 : existing.getQuantity(), 1, Item.MAX_QUANTITY, 1));
        JTextField description = new JTextField(existing == null ? "" : existing.getDescription());
        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.add(new JLabel("Item Name:"));
        form.add(name);
        form.add(new JLabel("Quantity:"));
        form.add(quantity);
        form.add(new JLabel("Description:"));
        form.add(description);

        Object[] options = {confirmLabel, "Cancel"};
        while (true) {
            int choice = JOptionPane.showOptionDialog(this, form, title, JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (choice != 0) return false;
            try {
                action.apply(new Item(name.getText(), spinnerValue(quantity), description.getText()));
                return true;
            } catch (IllegalArgumentException ex) {
                error(ex.getMessage());
            }
        }
    }

    // ======================================================================
    // Export / load / close
    // ======================================================================

    /** @return true if the map was saved */
    private boolean exportMap() {
        File directory = new File(MapFileIO.FOLDER).getAbsoluteFile();
        if (!directory.exists() && !directory.mkdirs()) {
            error("Could not create the " + MapFileIO.FOLDER + " folder.");
            return false;
        }
        JFileChooser chooser = new JFileChooser(directory);
        chooser.setDialogTitle("Export Map");
        chooser.setFileFilter(new FileNameExtensionFilter("Map files (*.txt)", "txt"));
        chooser.setSelectedFile(new File(directory, "building-map.txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return false;

        File chosen = chooser.getSelectedFile().getAbsoluteFile();
        if (!chosen.getName().toLowerCase().endsWith(".txt")) {
            chosen = new File(chosen.getParentFile(), chosen.getName() + ".txt");
        }
        Path file = chosen.toPath();
        if (Files.exists(file) && JOptionPane.showConfirmDialog(this, "Overwrite existing file?",
                "Confirm Export", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return false;
        try {
            MapFileIO.save(file, rooms);
        } catch (IOException ex) {
            error("Export failed: " + ex.getMessage());
            return false;
        }
        dirty = false;
        refreshAll();
        JOptionPane.showMessageDialog(this, "Map exported successfully.");
        return true;
    }

    /** @return true if a map was loaded; false if cancelled or the file was not a valid map */
    private boolean loadMapFromChooser() {
        JFileChooser chooser = new JFileChooser(new File(MapFileIO.FOLDER).getAbsoluteFile());
        chooser.setDialogTitle("Load Map");
        chooser.setFileFilter(new FileNameExtensionFilter("Map files (*.txt)", "txt"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return false;
        try {
            List<RoomView> loaded = MapFileIO.load(chooser.getSelectedFile().toPath());
            rooms.clear();
            rooms.addAll(loaded);
            selected = null;
            dirty = false;
            refreshAll();
            JOptionPane.showMessageDialog(this, "Map loaded successfully.");
            return true;
        } catch (IOException ex) {
            error("The selected file is not a valid map.\n" + ex.getMessage());
            return false;
        }
    }

    private void openDifferentMap() {
        if (!resolveUnsavedChanges()) return;
        loadMapFromChooser();
    }

    private void closeMap() {
        if (resolveUnsavedChanges()) onClose.run();
    }

    /**
     * If there are unsaved changes, lets the user export, or continue (which writes an
     * autosave file), or cancel.
     * @return true if it is safe to leave the current map
     */
    private boolean resolveUnsavedChanges() {
        if (!dirty) return true;
        Object[] options = {"Export Map", "Don't Save", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
                "This map has unsaved changes.\n\"Don't Save\" keeps a copy in "
                        + MapFileIO.FOLDER + "/" + MapFileIO.AUTOSAVE_FILE + ".",
                "Unsaved Changes", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
        if (choice == 0) return exportMap();
        if (choice == 1) return autosave();
        return false;
    }

    private boolean autosave() {
        try {
            MapFileIO.save(Paths.get(MapFileIO.FOLDER, MapFileIO.AUTOSAVE_FILE), rooms);
        } catch (IOException ex) {
            error("Autosave failed: " + ex.getMessage());
            return false;
        }
        dirty = false;
        JOptionPane.showMessageDialog(this, "Your changes were saved to "
                + MapFileIO.FOLDER + "/" + MapFileIO.AUTOSAVE_FILE + ".");
        return true;
    }

    // ======================================================================
    // Small helpers
    // ======================================================================

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Reads a spinner, committing any text the user typed but has not confirmed yet. */
    private static int spinnerValue(JSpinner spinner) {
        try {
            spinner.commitEdit();
        } catch (ParseException ignored) {
            // keep the last valid value
        }
        return ((Number) spinner.getValue()).intValue();
    }

    private static String fit(String text, FontMetrics fm, int maxWidth) {
        if (fm.stringWidth(text) <= maxWidth) return text;
        String shortened = text;
        while (shortened.length() > 1 && fm.stringWidth(shortened + "...") > maxWidth) {
            shortened = shortened.substring(0, shortened.length() - 1);
        }
        return shortened + "...";
    }

    // ======================================================================
    // The map itself
    // ======================================================================

    /** Draws the rooms as rectangles; click to select, drag to move (when editing is allowed). */
    private class MapCanvas extends JPanel {
        private static final long serialVersionUID = 1L;
        private RoomView dragging;
        private int offsetX;
        private int offsetY;
        private boolean moved;

        MapCanvas() {
            setPreferredSize(new Dimension(RoomView.MAP_WIDTH, RoomView.MAP_HEIGHT));
            setBackground(new Color(245, 245, 240));
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
            MouseAdapter mouse = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    RoomView hit = roomAt(e.getX(), e.getY());
                    select(hit); // clicking empty space deselects
                    if (hit != null && canEdit) {
                        dragging = hit;
                        offsetX = e.getX() - hit.getX();
                        offsetY = e.getY() - hit.getY();
                        moved = false;
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (dragging == null) return;
                    dragging.moveTo(e.getX() - offsetX, e.getY() - offsetY);
                    moved = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (dragging != null && moved) changed();
                    dragging = null;
                    moved = false;
                }
            };
            addMouseListener(mouse);
            addMouseMotionListener(mouse);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Font normal = getFont();
            Font bold = normal.deriveFont(Font.BOLD);

            if (rooms.isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.drawString("No rooms on this map yet.", 20, 30);
            }
            for (RoomView room : rooms) {
                boolean isSelected = room == selected;
                int x = room.getX();
                int y = room.getY();
                g2.setColor(isSelected ? SELECTED_FILL : Color.WHITE);
                g2.fillRoundRect(x, y, RoomView.WIDTH, RoomView.HEIGHT, 12, 12);
                g2.setColor(isSelected ? SELECTED_BORDER : Color.DARK_GRAY);
                g2.setStroke(new BasicStroke(isSelected ? 3f : 1.5f));
                g2.drawRoundRect(x, y, RoomView.WIDTH, RoomView.HEIGHT, 12, 12);

                g2.setColor(Color.BLACK);
                g2.setFont(bold);
                drawCentered(g2, room.getName(), x, y + 30);
                g2.setFont(normal);
                drawCentered(g2, "Room " + room.getNumber(), x, y + 52);
                drawCentered(g2, "Items: " + room.totalQuantity() + "/" + room.getItemCapacity(), x, y + 72);
            }
            g2.dispose();
        }

        private void drawCentered(Graphics2D g2, String text, int roomX, int baselineY) {
            FontMetrics fm = g2.getFontMetrics();
            String shown = fit(text, fm, RoomView.WIDTH - 12);
            g2.drawString(shown, roomX + (RoomView.WIDTH - fm.stringWidth(shown)) / 2, baselineY);
        }
    }
}
