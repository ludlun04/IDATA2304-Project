package no.ntnu.gui.greenhouse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * The main GUI window for greenhouse simulator.
 */
public class MainGreenhouseGuiWindow extends Scene {
  public static final int WIDTH = 300;
  public static final int HEIGHT = 300;

  /**
   * Create the main window for the greenhouse simulator.
   */
  public MainGreenhouseGuiWindow() {
    super(createMainContent(), WIDTH, HEIGHT);
  }

  /**
   * Create the main content of the window.
   *
   * @return The main content of the window
   */
  private static Parent createMainContent() {
    VBox container = new VBox(createInfoLabel(), createMasterImage(), createCopyrightNotice());
    container.setPadding(new Insets(20));
    container.setAlignment(Pos.CENTER);
    container.setSpacing(5);
    return container;
  }

  /**
   * Create an information label.
   *
   * @return The information label
   */
  private static Label createInfoLabel() {
    Label l = new Label("Close this window to stop the whole simulation");
    l.setWrapText(true);
    l.setPadding(new Insets(0, 0, 10, 0));
    return l;
  }

  /**
   * Create a label with the copyright notice.
   *
   * @return The label with the copyright notice
   */
  private static Node createCopyrightNotice() {
    Label l = new Label("Source NTNU");
    l.setFont(Font.font(10));
    return l;
  }

  private static Node createMasterImage() {
    Node node;
    try {
      InputStream fileContent = new FileInputStream("images/business_arne.png");
      ImageView imageView = new ImageView();
      imageView.setImage(new Image(fileContent));
      imageView.setFitWidth(100);
      imageView.setPreserveRatio(true);
      node = imageView;
    } catch (FileNotFoundException e) {
      node = new Label("Could not find image file: " + e.getMessage());
    }
    return node;
  }

}
