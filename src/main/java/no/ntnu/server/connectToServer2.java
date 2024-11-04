package no.ntnu.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.Buffer;

public class connectToServer2 {
  public static void main(String[] args) throws IOException {
    Socket clientSocket = new Socket("localhost", 1234);
    BufferedReader inputReader =
        new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    PrintWriter outputWriter = new PrintWriter(clientSocket.getOutputStream(), true);
    BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));

    outputWriter.println("Greenhouse");
    outputWriter.flush();

    new Thread(() -> {
      try {
        String message;
        while ((message = inputReader.readLine()) != null) {
          System.out.println(message);
        }
      } catch (IOException e) {
        System.err.println("Error reading from server: " + e.getMessage());
      }
    }).start();

    String userInput;
    while ((userInput = userInputReader.readLine()) != null) {
      outputWriter.println(userInput);
      outputWriter.flush();
    }
  }
}

