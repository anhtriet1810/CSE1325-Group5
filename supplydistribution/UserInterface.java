package supplydistribution;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/// frontend file

/**
 * Room Supply Distribution System - Login and Main Menu window.
 */
public class UserInterface extends JFrame {

    // ---- constants -------------------------------------------------------

    //replaces with AccessLevel.java
    // private static final String ROLE_STUDENT = "Student";
    // private static final String ROLE_FACULTY = "Faculty";
    // private static final String ROLE_ADMIN = "Admin";
    // private static final String[] ROLES = {ROLE_STUDENT, ROLE_FACULTY, ROLE_ADMIN};

    private static final String CARD_LOGIN = "login";
    private static final String CARD_MENU = "menu";
    private static final String CARD_MAP = "map";

    private static final int MIN_PASSWORD_LENGTH = 4;
    private static final Dimension SMALL_SIZE = new Dimension(480, 380);
    private static final Dimension MAP_SIZE = new Dimension(1060, 720);

    // ---- state -----------------------------------------------------------
    private final CardLayout cards = new CardLayout();
    private final JPanel screens = new JPanel(cards);
    // Fully qualified because the project has its own Map class (see Software Design).
    private final java.util.Map<String, User> accounts = new java.util.HashMap<>(); //hash map format: (String Name, User user)
    private User currentUser;   // null when nobody is logged in
    private JPanel activeMap;   // null when no map is open

    // ---- login screen widgets ---------------------------------------------
    private JComboBox<AccessLevel> accountType;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    // ---- main menu widgets -------------------------------------------------
    private JLabel welcomeLabel;
    private JLabel roleLabel;
    private JButton createMapButton;

    public UserInterface() {
        super("Room Supply Distribution System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        screens.add(buildLoginScreen(), CARD_LOGIN);
        screens.add(buildMainMenu(), CARD_MENU);
        setContentPane(screens);
        showScreen(CARD_LOGIN, SMALL_SIZE);
    }

    // ======================================================================
    // Screen building
    // ======================================================================

    private JPanel buildLoginScreen() {
        JPanel outer = new JPanel(new BorderLayout(12, 12));
        outer.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel heading = new JLabel("Room Supply Distribution System", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 19));
        outer.add(heading, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(4, 2, 10, 12));
        accountType = new JComboBox<>(AccessLevel.values());
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        JButton createAccountButton = new JButton("Create Account");
        fields.add(new JLabel("Account Type:"));
        fields.add(accountType);
        fields.add(new JLabel("Username:"));
        fields.add(usernameField);
        fields.add(new JLabel("Password:"));
        fields.add(passwordField);
        fields.add(loginButton);
        fields.add(createAccountButton);
        outer.add(fields, BorderLayout.CENTER);

        JLabel info = new JLabel("<html><center>Log in, or choose Create Account if you are new.<br>"
                + "Prototype: accounts only last until the program is closed.</center></html>",
                SwingConstants.CENTER);
        info.setBorder(BorderFactory.createTitledBorder("Information"));
        outer.add(info, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> login());
        createAccountButton.addActionListener(e -> createAccount());
        return outer;
    }

    private JPanel buildMainMenu() {
        JPanel outer = new JPanel(new BorderLayout(12, 12));
        outer.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        welcomeLabel = new JLabel("Welcome!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        roleLabel = new JLabel("Access level:", SwingConstants.CENTER);
        header.add(welcomeLabel);
        header.add(roleLabel);
        outer.add(header, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(3, 1, 10, 12));
        createMapButton = new JButton("Create Map");
        JButton loadMapButton = new JButton("Load Map");
        JButton logoutButton = new JButton("Logout");
        buttons.add(createMapButton);
        buttons.add(loadMapButton);
        buttons.add(logoutButton);
        outer.add(buttons, BorderLayout.CENTER);

        JLabel footer = new JLabel("Group 5 | Room Supply Distribution System | GUI Prototype v1.1",
                SwingConstants.CENTER);
        footer.setBorder(BorderFactory.createTitledBorder("Information"));
        outer.add(footer, BorderLayout.SOUTH);

        createMapButton.addActionListener(e -> openMap(false));
        loadMapButton.addActionListener(e -> openMap(true));
        logoutButton.addActionListener(e -> logout());
        return outer;
    }

    // ======================================================================
    // Navigation helpers
    // ======================================================================

    /** Shows a card, resizes the window for it, and sets the Enter-key default button. */
    private void showScreen(String card, Dimension size) {
        cards.show(screens, card);
        setSize(size);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(CARD_LOGIN.equals(card) ? loginButton : null);
        if (CARD_LOGIN.equals(card)) {
            usernameField.requestFocusInWindow();
        }
    }

    private void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    /** Which roles may start a brand-new map. Change this rule if the team decides differently. */
    private boolean canCreateMap(AccessLevel role) {
        return !AccessLevel.STUDENT.equals(role);
    }

    // ======================================================================
    // Accounts
    // ======================================================================

    private void createAccount() {
        JTextField name = new JTextField();
        JPasswordField pass = new JPasswordField();
        JPasswordField confirm = new JPasswordField();
        JComboBox<AccessLevel> role = new JComboBox<>(AccessLevel.values());
        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.add(new JLabel("Username:"));
        form.add(name);
        form.add(new JLabel("Password:"));
        form.add(pass);
        form.add(new JLabel("Confirm Password:"));
        form.add(confirm);
        form.add(new JLabel("Account Type:"));
        form.add(role);

        try {
            // Keep re-showing the form (with the username kept) until the input is valid or cancelled.
            while (true) {
                int result = JOptionPane.showConfirmDialog(this, form, "Create Account",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result != JOptionPane.OK_OPTION) return;

                String newName = name.getText().trim();
                AccessLevel newRole = (AccessLevel) role.getSelectedItem();
                char[] newPassword = pass.getPassword();
                char[] newConfirm = confirm.getPassword();
                String problem = validateNewAccount(newName, newPassword, newConfirm);

                if (problem == null) {
                    accounts.put(newName, new User(newName, newRole, newPassword));
                    Arrays.fill(newPassword, '\0');
                    Arrays.fill(newConfirm, '\0');
                    usernameField.setText(newName);
                    accountType.setSelectedItem(newRole);
                    passwordField.setText("");
                    showMessage("Account created. Please log in.", "Account Created",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                Arrays.fill(newPassword, '\0');
                Arrays.fill(newConfirm, '\0');
                pass.setText("");
                confirm.setText("");
                showMessage(problem, "Cannot Create Account", JOptionPane.WARNING_MESSAGE);
            }
        } finally {
            pass.setText("");
            confirm.setText("");
        }
    }

    /** Returns a message describing the first problem found, or null if the input is valid. */
    private String validateNewAccount(String username, char[] password, char[] confirm) {
        if (username.isEmpty()) return "Enter a username.";
        if (accounts.containsKey(username)) return "That username is already taken.";
        if (password.length < MIN_PASSWORD_LENGTH) {
            return "Password must be at least " + MIN_PASSWORD_LENGTH + " characters.";
        }
        if (!Arrays.equals(password, confirm)) return "The passwords do not match.";
        return null;
    }

    private void login() {
        String username = usernameField.getText().trim();
        AccessLevel role = (AccessLevel) accountType.getSelectedItem();
        char[] entered = passwordField.getPassword();

        User user = accounts.get(username);
        boolean valid = user != null && user.getRole().equals(role) && user.passwordMatches(entered);
        Arrays.fill(entered, '\0');
        passwordField.setText("");

        if (!valid) {
            // Same message for every failure so it does not reveal which usernames exist.
            showMessage("Invalid username, password, or account type.",
                    "Login Unsuccessful", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getUsername() + "!");
        roleLabel.setText("Access level: " + user.getRole());
        boolean allowed = canCreateMap(user.getRole());
        createMapButton.setEnabled(allowed);
        createMapButton.setToolTipText(allowed ? null : "Your access level cannot create new maps.");
        showScreen(CARD_MENU, SMALL_SIZE);
    }

    private void logout() {
        removeActiveMap();
        currentUser = null;
        usernameField.setText("");
        passwordField.setText("");
        showScreen(CARD_LOGIN, SMALL_SIZE);
    }

    // ======================================================================
    // Maps
    // ======================================================================

    private void openMap(boolean loadExisting) {
        if (currentUser == null) return;
        removeActiveMap(); // never leave an old map behind
        // invokeLater: MapGUI may call onClose from its constructor (e.g. the user
        // cancels the file chooser), which must not run before activeMap is set.
        activeMap = new MapGUI(currentUser.getRole(), loadExisting,
                () -> SwingUtilities.invokeLater(this::closeMap));
        screens.add(activeMap, CARD_MAP);
        showScreen(CARD_MAP, MAP_SIZE);
    }

    /** Called by MapGUI's "Close Map": discards the map screen and returns to the menu. */
    private void closeMap() {
        removeActiveMap();
        showScreen(currentUser != null ? CARD_MENU : CARD_LOGIN, SMALL_SIZE);
    }

    private void removeActiveMap() {
        if (activeMap != null) {
            screens.remove(activeMap);
            activeMap = null;
            screens.revalidate();
            screens.repaint();
        }
    }

    //this makes the GUI appear, use it in Main.java
    public static void UserInterfaceAppears() {
        SwingUtilities.invokeLater(() -> new UserInterface().setVisible(true));
    }
}
