**Oct 29, 2026**
    -changed the logic for the equals() method in Item.java to now take another Item instead of a generic Object.
    
**Nov 12, 2026**
    -added comments to many of the class files
    -changed a lot of Exception throwing in Item.java and ItemManager.java to instead print to System.err and set default values since there are no try/catch blocks in the rest of the back end.
    -commented out the import: javax.swing.border.TitledBorder; in MapGUI.java since it is not used
    -merged the Item.java file from GUI code into the back end
    -reorganized a lot of the file structure and turned most of the code into a package
    -in many of the frontend files, changed the role related String into an AccessLevel from AccessLevel.java

**Nov 17, 2026** (Relish Karki — Software Integration, America/Chicago)
    -Option B: keep GUI as source of truth (`RoomView`, `MapGUI`, `MapFileIO`, `UserInterface`, `User`); backend helpers stay for UML / debug.
    -`Main.java`: default path only launches `UserInterface.UserInterfaceAppears()`; optional `--demo` flag runs the old console RoomManager/ItemManager smoke demo.
    -Fixed `ItemManager.addItem`: on name match (case-insensitive) quantities are merged; previously `editItem` was called with the same values and never added qty.
    -`Room.removeItem(String, int)` now matches item names case-insensitively (aligned with `RoomView` / MapGUI).
    -Added `ModelBridge` with `RoomView`↔`Room` conversion helpers and a thin `moveItem` wrapper over `RoomManager` (MapGUI unchanged).
    -Item name validation: backend `Item.setName` still defaults empty/null/too-long names to `"Unnamed"` (Angel Nov 12). MapGUI validates before constructing `Item`, so GUI paths are unaffected. Documented; no throw change that would break MapGUI.
