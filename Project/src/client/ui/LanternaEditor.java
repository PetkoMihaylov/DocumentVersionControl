package client.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.*;
//import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;

public class LanternaEditor {

    private String oldContent;

    public LanternaEditor(String oldContent) {
        this.oldContent = oldContent;
    }

    public void start() throws IOException {

        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();

        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

        BasicWindow window = new BasicWindow("Document Editor");

        Panel mainPanel = new Panel(new GridLayout(2));

        // the left panel (Old version)
        TextBox leftBox = new TextBox(new TerminalSize(40, 20));
        leftBox.setReadOnly(true);
        leftBox.setText(oldContent);

        // the right panel (New version where you can edit)
        TextBox rightBox = new TextBox(new TerminalSize(40, 20));

        // buttons
        Button saveButton = new Button("Save (Ctrl+S)", () -> {
            String newContent = rightBox.getText();
            System.out.println("Saving new version...");
            System.out.println(newContent);

            // add DocumentService here for saving?
        });

        Button diffButton = new Button("Show Diff (Ctrl+D)", () -> {
            showDiff(oldContent, rightBox.getText());
        });

        Panel buttonPanel = new Panel();
        buttonPanel.addComponent(saveButton);
        buttonPanel.addComponent(diffButton);

        mainPanel.addComponent(leftBox);
        mainPanel.addComponent(rightBox);

        Panel root = new Panel();
        root.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        root.addComponent(mainPanel);
        root.addComponent(buttonPanel);

        window.setComponent(root);

        gui.addWindowAndWait(window);
    }

    // method for showing differences from content/text with -> (~ + -)
    private void showDiff(String oldText, String newText) {

        System.out.println("\n--- DIFFERENCES ---");

        String[] oldLines = oldText.split("\n");
        String[] newLines = newText.split("\n");

        int max = Math.max(oldLines.length, newLines.length);

        for (int i = 0; i < max; i++) {

            String oldLine = i < oldLines.length ? oldLines[i] : null;
            String newLine = i < newLines.length ? newLines[i] : null;

            if (oldLine == null) {
                System.out.println("+ " + newLine);
            } else if (newLine == null) {
                System.out.println("- " + oldLine);
            } else if (!oldLine.equals(newLine)) {
                System.out.println("~ " + newLine);
            } else {
                System.out.println("  " + newLine);
            }
        }

        System.out.println("-------------\n");
    }

}
