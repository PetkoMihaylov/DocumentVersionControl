package client.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.*;
//import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LanternaEditor {

    private final String oldContent;
    private String newContent;

    public LanternaEditor(String oldContent) {
        this.oldContent = oldContent;
        this.newContent = oldContent;
    }

    public int startView() throws IOException {
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();

        BasicWindow window = new BasicWindow("Document Viewer");
        Panel mainPanel = new Panel(new GridLayout(1));


        TextBox textBox = new TextBox(new TerminalSize(70, 20));
        textBox.setReadOnly(true);
        textBox.setText(oldContent);
        mainPanel.addComponent(textBox);

        Button exitButton = new Button("Exit (CTRL+W)", () -> {
            //showSideBySideDifference(oldContent, rightBox.getText(), leftDiff, rightDiff);
            //return;
        });

        Panel buttonPanel = new Panel();
        buttonPanel.addComponent(exitButton);

        window.addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onInput(Window basePane, KeyStroke keyStroke, AtomicBoolean deliverEvent) {

                if (keyStroke.isCtrlDown()) {

                    switch (keyStroke.getKeyType()) {
                        case Character:
                            char c = keyStroke.getCharacter();

                            if (c == 'w' || c == 'w') {
                                deliverEvent.set(false);
                                window.close();
                                try {
                                    screen.stopScreen();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                return;
                            }
                            break;
                    }
                }
            }
        });

        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);



        Panel root = new Panel();

        mainPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        mainPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        //window.setHints(List.of(Window.Hint.EXPANDED)); //uses Array coordinates to resize Lanterna window
        //buttonPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning));


        root.addComponent(mainPanel);
        //root.addComponent(diffButtonLanterna);
        root.addComponent(buttonPanel);

        window.setComponent(root);

        gui.addWindowAndWait(window);
        return 0;
    }

    public int startEdit() throws IOException {

        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();

        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

        BasicWindow window = new BasicWindow("Document Editor");

        Panel mainPanel = new Panel(new GridLayout(5));


        // the left panel (Old version)
        TextBox leftBox = new TextBox(new TerminalSize(40, 20));
        leftBox.setReadOnly(true);
        leftBox.setText(oldContent);

        // the right panel (New version where you can edit)
        TextBox rightBox = new TextBox(new TerminalSize(40, 20));
        rightBox.setText(oldContent);
        //window.setFocusedInteractable(rightBox);

        //left difference
        TextBox leftDiff = new TextBox(new TerminalSize(3, 20));
        leftDiff.setReadOnly(true);
        //right difference
        TextBox rightDiff = new TextBox(new TerminalSize(3, 20));
        rightDiff.setReadOnly(true);


        EmptySpace emptySpace = new EmptySpace(new TerminalSize(2, 20));

//        TextBox diffBox = new TextBox(new TerminalSize(1, 20));
//        diffBox.setReadOnly(true);


        // buttons
        Button saveButton = new Button("Save (Ctrl+S)", () -> {
            newContent = rightBox.getText();
            System.out.println("Saving new version...");
            System.out.println(newContent);
            // add DocumentService here for saving?
        });

//        Button diffButton = new Button("Show Diff (Ctrl+D)", () -> {
//            showDiff(oldContent, rightBox.getText());
//        });
        Button diffButton = new Button("Show Diff (Ctrl+D)", () -> {
            showSideBySideDifference(oldContent, rightBox.getText(), leftDiff, rightDiff);
        });

        Button exitButton = new Button("Exit (CTRL+W)", () -> {
            //showSideBySideDifference(oldContent, rightBox.getText(), leftDiff, rightDiff);
            //return;
        });

        //String differences = getDifference(oldContent, rightBox.getText());
//        Button diffButtonLanterna = new Button("Show Difference in Lanterna (Ctrl+E)", () -> {
//            String diff = getDifference(oldContent, rightBox.getText());
//            diffBox.setText(diff);
//        });

        //panel
        Panel buttonPanel = new Panel();
        buttonPanel.addComponent(saveButton);
        buttonPanel.addComponent(diffButton);
        buttonPanel.addComponent(exitButton);

        mainPanel.addComponent(leftDiff);
        mainPanel.addComponent(leftBox);
        mainPanel.addComponent(emptySpace);
        mainPanel.addComponent(rightDiff);
        mainPanel.addComponent(rightBox);

        StringBuilder leftDiffText = new StringBuilder();
        StringBuilder rightDiffText = new StringBuilder();

        //mainPanel.addComponent(leftBox);
        //mainPanel.addComponent(diffBox);
        //mainPanel.addComponent(rightBox);

        Panel root = new Panel();

        mainPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        buttonPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));


        //mainPanel.withBorder(Borders.singleLine("Editor"));
        //buttonPanel.withBorder(Borders.singleLine("Actions"));

        mainPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        window.setHints(List.of(Window.Hint.EXPANDED)); //uses Array coordinates to resize Lanterna window
        //buttonPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning));

        root.addComponent(mainPanel);
        //root.addComponent(diffButtonLanterna);
        root.addComponent(buttonPanel);

        window.setComponent(root);

        //window.setHints(Arrays.asList(Window.Hint.EXPANDED));
        //window.setFixedSize(new TerminalSize(460, 100)); // don't know what it does as it says it should resize the window, but it doesn't do so.

        window.addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onInput(Window basePane, KeyStroke keyStroke, AtomicBoolean deliverEvent) {
                if (keyStroke.getKeyType() == KeyType.Backspace ||  keyStroke.getKeyType() == KeyType.Enter || keyStroke.getKeyType() == KeyType.ArrowDown) {
                    showSideBySideDifference(oldContent, rightBox.getText(), leftDiff, rightDiff);
                }
            }
        });

        window.addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onInput(Window basePane, KeyStroke keyStroke, AtomicBoolean deliverEvent) {

                if (keyStroke.isCtrlDown()) {

                    switch (keyStroke.getKeyType()) {
                        case Character:
                            char c = keyStroke.getCharacter();

                            if (c == 's' || c == 'S') {
                                newContent = rightBox.getText();
                                System.out.println("Saving...");
                                deliverEvent.set(false); // this ignores input when using shortcut
                                //sendCommand("SAVE_VERSION", newContent);

                            }
                            else if (c == 'd' || c == 'D') {
                                showSideBySideDifference(oldContent, rightBox.getText(), leftDiff, rightDiff);
                                deliverEvent.set(false);
                            }
                            else if (c == 'w' || c == 'w') {
                                deliverEvent.set(false);
                                window.close();
                                try {
                                    screen.stopScreen();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                return;
                            }
                            break;
                    }
                }
                return;
            }
        });





        rightBox.takeFocus();
        gui.addWindowAndWait(window);


        return 0;
    }

    // method for showing differences from content/text with -> (~ + -)
//    private void showDiff(String oldText, String newText) {
//
//        System.out.println("\n--- DIFFERENCES ---");
//
//        String[] oldLines = oldText.split("\n");
//        String[] newLines = newText.split("\n");
//
//        int max = Math.max(oldLines.length, newLines.length);
//
//        for (int i = 0; i < max; i++) {
//
//            String oldLine = i < oldLines.length ? oldLines[i] : null;
//            String newLine = i < newLines.length ? newLines[i] : null;
//
//            if (oldLine == null) {
//                System.out.println("+ " + newLine);
//            } else if (newLine == null) {
//                System.out.println("- " + oldLine);
//            } else if (!oldLine.equals(newLine)) {
//                System.out.println("~ " + newLine);
//            } else {
//                System.out.println("  " + newLine);
//            }
//        }
//
//        System.out.println("-------------\n");
//    }
//
//    private String getDifference(String oldText, String newText) {
//        StringBuilder result = new StringBuilder();
//
//        String[] oldLines = oldText.split("\n");
//        String[] newLines = newText.split("\n");
//
//        int max = Math.max(oldLines.length, newLines.length);
//
//        for (int i = 0; i < max; i++) {
//            String oldLine = i < oldLines.length ? oldLines[i] : null;
//            String newLine = i < newLines.length ? newLines[i] : null;
//
//            if (oldLine == null) {
//                result.append("+ ").append(newLine).append("\n");
//            } else if (newLine == null) {
//                result.append("- ").append(oldLine).append("\n");
//            } else if (!oldLine.equals(newLine)) {
//                result.append("~ ").append(newLine).append("\n");
//            } else {
//                result.append("  ").append(newLine).append("\n");
//            }
//        }
//
//        return result.toString();
//    }


    public String getEditedText() {
        return newContent;
    }

    private void showSideBySideDifference(String oldText, String newText, TextBox leftDiff, TextBox rightDiff) {

        String[] oldLines = oldText.split("\n");
        String[] newLines = newText.split("\n");

        int max = Math.max(oldLines.length, newLines.length);

        StringBuilder leftDiffText = new StringBuilder();
        StringBuilder rightDiffText = new StringBuilder();

        for (int i = 0; i < max; i++) {

            String oldLine = i < oldLines.length ? oldLines[i] : null;
            String newLine = i < newLines.length ? newLines[i] : null;

            if (oldLine == null) {
                // line added in new the new version
                leftDiffText.append(" ").append("\n");
                rightDiffText.append("+").append("\n");

            } else if (newLine == null) {
                // line removed from the old version
                leftDiffText.append("-").append("\n");
                rightDiffText.append(" ").append("\n");

            } else if (!oldLine.equals(newLine)) {
                // edited
                leftDiffText.append("~").append("\n");
                rightDiffText.append("~").append("\n");

            } else {
                // no edit
                leftDiffText.append(" ").append("\n");
                rightDiffText.append(" ").append("\n");
            }
        }

        leftDiff.setText(leftDiffText.toString());
        rightDiff.setText(rightDiffText.toString());
    }

}
