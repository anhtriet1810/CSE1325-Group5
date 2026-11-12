package supplydistribution;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Room Supply Distribution System - map file reading and writing.
 */
public final class MapFileIO {
    public static final String FOLDER = "Saved Maps";
    public static final String AUTOSAVE_FILE = "autosave.txt";

    private static final String HEADER_V1 = "ROOM_MAP_V1";
    private static final String HEADER_V2 = "ROOM_MAP_V2";

    private MapFileIO() { }

    /** Writes the map to a temporary file first, so a failed save never damages an existing file. */
    public static void save(Path file, List<RoomView> rooms) throws IOException {
        Path absolute = file.toAbsolutePath();
        if (absolute.getParent() != null) Files.createDirectories(absolute.getParent());
        Path temp = absolute.resolveSibling(absolute.getFileName() + ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                writer.write(HEADER_V2);
                writer.newLine();
                for (RoomView room : rooms) {
                    writer.write("ROOM\t" + encode(room.getName()) + "\t" + room.getNumber() + "\t"
                            + room.getItemCapacity() + "\t" + room.getPersonCapacity() + "\t"
                            + room.getX() + "\t" + room.getY());
                    writer.newLine();
                    for (Item item : room.getItems()) {
                        writer.write("ITEM\t" + encode(item.getName()) + "\t" + item.getQuantity()
                                + "\t" + encode(item.getDescription()));
                        writer.newLine();
                    }
                }
            }
            Files.move(temp, absolute, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | RuntimeException ex) {
            Files.deleteIfExists(temp);
            throw ex;
        }
    }

    public static List<RoomView> load(Path file) throws IOException {
        List<RoomView> rooms = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            boolean v2 = HEADER_V2.equals(header);
            if (!v2 && !HEADER_V1.equals(header)) {
                throw new IOException("Missing or unknown map header.");
            }
            RoomView current = null;
            int lineNumber = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\t", -1);
                try {
                    if (parts[0].equals("ROOM")) {
                        current = parseRoom(parts, v2, rooms);
                        rooms.add(current);
                    } else if (parts[0].equals("ITEM") && current != null) {
                        current.addItem(parseItem(parts, v2));
                    } else {
                        throw new IllegalArgumentException("Unexpected entry.");
                    }
                } catch (NumberFormatException ex) {
                    throw new IOException("Line " + lineNumber + ": invalid number.");
                } catch (IllegalArgumentException ex) {
                    throw new IOException("Line " + lineNumber + ": " + ex.getMessage());
                }
            }
        }
        return rooms;
    }

    private static RoomView parseRoom(String[] p, boolean v2, List<RoomView> loadedSoFar) {
        if (p.length != (v2 ? 7 : 5)) throw new IllegalArgumentException("Wrong number of fields for a room.");
        if (loadedSoFar.size() >= RoomView.MAX_ROOMS) {
            throw new IllegalArgumentException("Too many rooms (maximum " + RoomView.MAX_ROOMS + ").");
        }
        int number = Integer.parseInt(p[2]);
        for (RoomView existing : loadedSoFar) {
            if (existing.getNumber() == number) throw new IllegalArgumentException("Duplicate room number " + number + ".");
        }
        int itemCapacity = Integer.parseInt(p[3]);
        int personCapacity = Integer.parseInt(p[4]);
        Point position = RoomView.gridSlot(loadedSoFar.size()); // V1 files have no positions
        int x = v2 ? Integer.parseInt(p[5]) : position.x;
        int y = v2 ? Integer.parseInt(p[6]) : position.y;
        return new RoomView(decode(p[1]), number, itemCapacity, personCapacity, x, y);
    }

    private static Item parseItem(String[] p, boolean v2) {
        if (p.length != (v2 ? 4 : 2)) throw new IllegalArgumentException("Wrong number of fields for an item.");
        int quantity = v2 ? Integer.parseInt(p[2]) : 1;
        String description = v2 ? decode(p[3]) : "";
        return new Item(decode(p[1]), quantity, description);
    }

    private static String encode(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String s) {
        return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8);
    }
}
