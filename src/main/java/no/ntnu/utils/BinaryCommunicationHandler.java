package no.ntnu.utils;

import no.ntnu.tools.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class BinaryCommunicationHandler {

  private DataInputStream dataInputStream;
  private final static String OUTPUT_IMAGE_PATH = "output.img";

  public BinaryCommunicationHandler(Socket socket) throws IOException {
    InputStream inputStream = socket.getInputStream();
    this.dataInputStream = new DataInputStream(inputStream);
  }

  public void readImageFromInputStream() throws IOException {
    //we need to know the length of the image so we know when to stop listening
    int imageLength = this.dataInputStream.readInt();

    byte[] imageAsBytes = new byte[imageLength];
    this.dataInputStream.readFully(imageAsBytes);

    try (FileOutputStream fileOutputStream = new FileOutputStream(OUTPUT_IMAGE_PATH)){
      fileOutputStream.write(imageAsBytes);
    } catch (FileNotFoundException e) {
      Logger.error("Could not write file, " + e.getMessage());
    }

  }
}
